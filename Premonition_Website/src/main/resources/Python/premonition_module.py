#!/usr/bin/python3

import numpy as np
import pandas as pd
import itertools
import collections
from datetime import datetime


class Premonition:

    def __init__(self, e, l, n, c, r=250, i=True, m=True):

        self.ev_file = e
        self.nodes_in_file = l
        self.nodes_out_file = n
        self.cytoscape_file = c

        self.limited = r
        self.incl_NRCs = i
        self.remove_edges = m
        self.log_file_name = ""

    def run(self):
        timestamp = datetime.timestamp(datetime.now())
        org = self.cytoscape_file.split("/")[-1].split("_")[0]
        log_dir = "/".join(self.cytoscape_file.split("/")[0:-2])
        self.log_file_name = "{}/premonition_log_{}_{}.log".format(log_dir, org, timestamp)

        self.log("processing: {}\n".format(self.nodes_in_file), self.log_file_name)
        goi_set = self.read_goi_file(self.nodes_in_file)
        [ev_con_dict, unique_node_set] = self.read_evidence_file(self.ev_file)

        self.log("Reading files\n", self.log_file_name)
        f_goi_set = self.filter_gois(goi_set, unique_node_set)
        interaction_matrix = self.create_int_matrix(ev_con_dict, unique_node_set)

        self.log("Generating network\n", self.log_file_name)
        interaction_df = pd.DataFrame(interaction_matrix, columns=unique_node_set, index=unique_node_set)
        limited_inter_df = self.generate_network(interaction_df, f_goi_set, self.incl_NRCs)

        self.log("Pruning network: round 1/2\n", self.log_file_name)
        pruned_inter_df = self.prune_network(limited_inter_df, f_goi_set)

        if self.remove_edges:
            self.log("Removing edges\n", self.log_file_name)
            sparse_network_df = self.make_sparse_network(pruned_inter_df, f_goi_set)
        else:
            sparse_network_df = pruned_inter_df

        if self.limited:
            self.log("Pruning network limited to {}: round 2/2\n".format(self.limited), self.log_file_name)
            sparse_network_df = self.prune_network_limited(sparse_network_df, f_goi_set, self.limited)
        else:
            self.log("Pruning network: round 2/2\n", self.log_file_name)
            sparse_network_df = self.prune_network(sparse_network_df, f_goi_set)

        self.log("Writing Cytoscape output\n", self.log_file_name)
        self.write_cytoscape_output(sparse_network_df, self.cytoscape_file)
        self.write_cytoscape_node_table(sparse_network_df.columns.values, goi_set, self.nodes_out_file)

        self.log("Done\n\n", self.log_file_name)

    def read_goi_file(self, file):
        """
        Reads the provided notes of interest
        :param file: path String of the Nodes of interest file
        :return: a Set of nodes
        """
        nodes = set()
        with open(file) as FILE:
            for line in FILE:
                line = line.strip()
                nodes.add(line)
        return nodes

    def read_evidence_file(self,file):
        """
        Reads the provided evidence
        :param file: path String of the evidence file
        :return: Dictionary containing the interaction nodes and a Set of all the nodes in de given evidence network
        file
        """
        con_dict = dict()
        node_set = set()
        with open(file) as FILE:
            for line in FILE:
                [node1, node2, prob] = line.strip().split("\t")
                if node1 not in con_dict.keys():
                    con_dict[node1] = dict()
                con_dict[node1][node2] = prob
                # make sure all nodes are accounted for
                node_set.add(node1)  # Sufficient when interactions are bidirectional provided in de evidence file
                node_set.add(node2)  # Needed when interactions are uni-directional provided
        return con_dict, node_set

    def filter_gois(self, gois, uniques):
        """
        Filters the provided nodes of interest. They must be represented in the evidence file
        :param gois: Set of nodes
        :param uniques: Set of nodes present in the evidence file
        :return: Set of only nodes present in the evidence file
        """
        return set(filter(lambda x: x in uniques, gois))

    def create_int_matrix(self, ev_dict, nodes):
        """
        :param ev_dict: Dictionary of the provided node connections from the evidence file
        :param nodes: Set of nodes provided via the goi
        :return: a Pandas Dataframe (connectivity matrix)
        Constructs a Pandas Dataframe from a Dictionary
        """
        index_dict = {n: i for i, n in enumerate(nodes)}
        matrix = np.zeros((len(nodes), len(nodes)), dtype=int)
        for node1 in nodes:
            node1_index = index_dict[node1]
            if node1 in ev_dict.keys():
                for node2 in ev_dict[node1].keys():
                    node2_index = index_dict[node2]
                    matrix[node1_index, node2_index] = ev_dict[node1][node2]
            else:
                self.log("ERROR - no evidence entry for node {}\n".format(node1), self.log_file_name)

        return matrix

    def generate_network(self, int_df, gois, add_nrc):
        """
        :param int_df: Pandas Dataframe row and columns are nodes, values the connectivity probability
        :param gois: specific Set of nodes
        :param add_nrc: Boolean, True if NRCs need to be included else False (gois only)
        :return: Pandas Dataframe containing a subset of the initial/ provided Dataframe
        Constructs a sub-network from the user submitted interaction network
        """
        network_nodes = list()
        for goi in gois:
            connecting_nodes = self.select_rows(int_df, goi)
            if add_nrc:
                network_nodes.extend(connecting_nodes)
                end_nodes = list(map(lambda x: self.select_rows(int_df, x), connecting_nodes))
                end_nodes = list(filter(lambda x: x in goi, list(itertools.chain(*end_nodes))))
                network_nodes.extend(end_nodes)
            else:
                end_nodes = list(set(connecting_nodes) & gois)
                network_nodes.extend(end_nodes)

        rc_inf_df = self.reconstruct_int_df(int_df, set(network_nodes), gois)

        return rc_inf_df

    def select_rows(self, int_df, node):
        """
        Select the column of a node and return the rows / nodes to which it connects
        :param int_df: Pandas Dataframe
        :param node: Node String from which the row needs to be selected
        :return: List of nodes
        """
        row_numbers = [i for i, e in enumerate(int_df[node]) if e > 0]
        row_nodes = int_df.index.values[row_numbers]
        return row_nodes.tolist()

    def select_columns(self, int_df, node):
        """
        Select the row of node and return the columns/ nodes to which it connects
        :param int_df: Pandas Dataframe
        :param node: Node String from which the row needs to be selected
        :return: List of nodes
        """
        column_numbers = [i for i, e in enumerate(int_df.loc[node, :]) if e > 0]
        column_nodes = int_df.columns.values[column_numbers]
        return column_nodes

    def reconstruct_int_df(self, int_df, n_nodes, gois):
        """
        :param int_df: Pandas Dataframe containing the initial/ provided Dataframe
        :param n_nodes: Set of networked nodes
        :param gois: Set of nodes of interest
        :return: Pandas Pandas Dataframe containing a subset of the initial/ provided Dataframe
        Based on a set of nodes, a new interaction matrix (Dataframe) is constructed
        """
        self.log("Reconstructing interacting matrix\n", self.log_file_name)
        index_dict = {n: i for i, n in enumerate(n_nodes)}
        matrix = np.zeros((len(n_nodes), len(n_nodes)), dtype=int)
        for node1 in n_nodes:
            node1_index = index_dict[node1]
            for node2 in n_nodes:
                node2_index = index_dict[node2]
                if not node1 == node2:
                    if node1 in gois or node2 in gois:
                        matrix[node1_index, node2_index] = int_df[node1][node2]

        return pd.DataFrame(matrix, columns=n_nodes, index=n_nodes)

    def prune_network(self, int_df, gois):
        """
        Removes all nodes with a single connection
        :param int_df: Pandas Dataframe containing an interaction matrix
        :param gois: Set of Nodes provided by the user
        :return: Pandas Dataframe with an reduced interaction matrix
        """
        self.log("Ingoing matrix dimensions are: {}\n".format(int_df.shape), self.log_file_name)
        binary_df = int_df.astype(bool).astype(int)
        diagonal_k = np.diag(np.dot(binary_df, binary_df))
        all_nodes = int_df.columns.values
        remove_nodes = all_nodes[np.where(diagonal_k == 0 | 1)[0]]

        to_remove = [x for x in remove_nodes if x not in gois]
        int_df = int_df.drop(to_remove, axis=0)
        int_df = int_df.drop(to_remove, axis=1)

        self.log("Resulting matrix dimensions are: {}\n".format(int_df.shape), self.log_file_name)
        return int_df

    def prune_network_limited(self, int_df, gois, max_size):
        binary_df = int_df.astype(bool).astype(int)
        diagonal_k = np.diag(np.dot(binary_df, binary_df))
        all_nodes = int_df.columns.values
        remove_nodes = all_nodes[np.where(diagonal_k == 0 | 1)[0]]
        maximum = int(int_df.stack().max()+1)
        int_df_nan = int_df.replace(0, np.nan)
        run = True
        counter = 0
        while run:
            node1, node2 = int_df_nan.stack().idxmin(axis=0)
            if node1 in remove_nodes and not (node1 in gois):
                if node1 in int_df:
                    int_df = int_df.drop(node1, axis=0)
                    int_df = int_df.drop(node1, axis=1)
            elif node2 in remove_nodes and not (node2 in gois):
                if node2 in int_df:
                    int_df = int_df.drop(node2, axis=0)
                    int_df = int_df.drop(node2, axis=1)
            int_df_nan[node1][node2] = maximum
            int_df_nan[node2][node1] = maximum
            if int_df.shape[1] <= max_size:
                run = False
                self.log("Minimal network size reached: \n" + str(int_df.shape[1]), self.log_file_name)
            if counter >= int_df_nan.shape[1]:
                run = False
            else:
                counter += 1
        return int_df

    def make_bfs_graph(self, df):
        """
        Transforms a Pandas Dataframe to a dictionary structure needed for BFS
        :param df: Dataframe
        :return: Dictionary of nodes having Lists as values
        """
        bfs_graph = dict()
        for node in df.columns.values:
            row_numbers = [i for i, e in enumerate(df[node]) if e > 0]
            bfs_graph[node] = list(df.index.values[row_numbers])
        return bfs_graph

    def is_connected(self, graph, root, leaf, number_nodes):
        """
        Breadth-first search (BFS) implementation, checks if a network it still intact/ no sub-networks
        :param graph: a Dictionary of nodes having Lists as values (created by make_bfs_graph)
        :param root: a node String as starting point
        :param leaf: a node String as end-point
        :param number_nodes: The number of nodes in the original generated network
        :return: Boolean (True when network is intact, False when sub-networks are found
        """
        seen, queue = set(root), collections.deque([root])
        while queue:
            vertex = queue.popleft()
            for node in graph[vertex]:
                if node not in seen:
                    if node == leaf:
                        return True
                    else:
                        seen.add(node)
                        queue.append(node)
        if len(seen) == number_nodes:
            return True
        else:
            return False

    def make_sparse_network(self, int_df, gois):
        """
        Removes as many edges as possible form a given network
        :param int_df: Pandas Dataframe containing a interaction network
        :param gois: Set of provided Nodes of interest
        :return: Pandas Dataframe containing a network with the least number of edges possible
        """
        maximum = int(int_df.stack().max()+1)
        nb_nodes = int_df.shape[1]
        int_df_bup = int_df.copy()
        int_df_nan = int_df.replace(0, np.nan)
        run = True

        while run:
            node1, node2 = int_df_nan.stack().idxmin()
            if int_df_nan[node1][node2] == maximum:
                run = False
            else:
                if (node1 in gois) and (node2 in gois):
                    # edges between input nodes are NOT removed
                    int_df_nan[node1][node2] = maximum
                    int_df_nan[node2][node1] = maximum
                else:
                    int_df[node1][node2] = 0
                    int_df[node2][node1] = 0
                    int_df_nan[node1][node2] = np.nan
                    int_df_nan[node2][node1] = np.nan

                    bfs_graph = self.make_bfs_graph(int_df)

                    if self.is_connected(bfs_graph, node1, node2, nb_nodes):
                        int_df_bup = int_df.copy()
                    else:
                        int_df = int_df_bup.copy()
                        int_df_nan[node1][node2] = maximum
                        int_df_nan[node2][node1] = maximum
        return int_df

    def write_cytoscape_output(self, df, out_f):
        """
        Transforms the final network (df) to Cytoscape compatible format and writes it to file
        :param df: Pandas Dataframe containing the network
        :param out_f: a path-String where the output is stored
        """
        super_string = "{}\t{}\t{}\n".format("Node_name", "Node_name_B", "Score")
        for node1 in df.columns.values:
            for node2 in df.index:
                if df[node1][node2] != 0:
                    super_string += "{}\t{}\t{}\n".format(node1, node2, df[node1][node2])
        self.write(super_string, out_f)

    def write_cytoscape_node_table(self, all_nodes, gois, out_f):
        super_string = "{}\t{}\n".format("Node_name", "Node_type")
        for node in all_nodes:
            if node in gois:
                super_string += "{}\t{}\n".format(node, "NOI")
            else:
                super_string += "{}\t{}\n".format(node, "IGN")
        self.write(super_string, out_f)

    def write(self, data, file):
        with open(file, 'w') as FILE_H:
            FILE_H.write(data)

    def log(self, data, file):
        with open(file, 'a') as FILE_H:
            FILE_H.write(data)