#!/usr/bin/python3

import numpy as np
import pandas as pd
import itertools
import collections
import argparse
import sys
import os


def main(p_args):
    ev_file = p_args.ev_file
    nodes_in_file = p_args.nodes_in_file
    nodes_out_file = p_args.nodes_out_file
    cytoscape_file = p_args.cytoscape_file

    limited = p_args.limited
    incl_NRCs = p_args.incl_NRCs
    remove_edges = p_args.remove_edges

    goi_set = read_goi_file(nodes_in_file)
    [ev_con_dict, unique_node_set] = read_evidence_file(ev_file)

    print("Reading files\n")
    f_goi_set = filter_gois(goi_set, unique_node_set)
    interaction_matrix = create_int_matrix(ev_con_dict, unique_node_set)

    print("Generating network\n")
    interaction_df = pd.DataFrame(interaction_matrix, columns=unique_node_set, index=unique_node_set)
    limited_inter_df = generate_network(interaction_df, f_goi_set, incl_NRCs)

    print("Pruning network: round 1/2\n")
    pruned_inter_df = prune_network(limited_inter_df, f_goi_set)

    if remove_edges:
        print("Removing edges\n")
        sparse_network_df = make_sparse_network(pruned_inter_df, f_goi_set)
    else:
        sparse_network_df = pruned_inter_df

    if limited:
        print("Pruning network limited to {}: round 2/2\n".format(limited))
        sparse_network_df = prune_network_limited(sparse_network_df, f_goi_set, limited)
    else:
        print("Pruning network: round 2/2")
        sparse_network_df = prune_network(sparse_network_df, f_goi_set)

    print("Writing Cytoscape output\n")
    write_cytoscape_output(sparse_network_df, cytoscape_file)
    write_cytoscape_node_table(sparse_network_df.columns.values, goi_set, nodes_out_file)

    print("Done")


def parse_arguments():
    parser = argparse.ArgumentParser(description='Premonition: An algorithm for predicting the circadian '
                                                 'clock-regulated molecular network')
    parser.add_argument('-e', action="store",
                        dest="ev_file",
                        required=True,
                        help="File path, to network evidence file.txt (node1\tnode2\tinteraction probability)")
    parser.add_argument('-l', action="store",
                        dest="nodes_in_file",
                        required=True,
                        help="File path, to nodes of interest file.txt, used as seeds for network reconstruction"
                        "(node1\nnode2\netc)")
    parser.add_argument('-n', action="store",
                        dest="nodes_out_file", default="my_node_list.txt",
                        help="File path, where to store the constructed network "
                             "(node1\tnode2\tinteraction probability)")
    parser.add_argument('-c', action="store",
                        dest="cytoscape_file", default="my_network_file.txt",
                        help="File path, where to store a list of all nodes in the constructed network. "
                             "Convenient for Network  styling and GO-analysis.")

    parser.add_argument('-r', action="store",
                        dest="limited", default=False,
                        help="Boolean (False) or integer for minimal network-size, when NRCs are removed.")

    parser.add_argument('-i', action="store",
                        dest="incl_NRCs", default=True,
                        help="Boolean (True/False) for including NRCs in the network.")

    parser.add_argument('-m', action="store",
                        dest="remove_edges", default=True,
                        help="Boolean (True/False) for removing edges within the network while keeping the network"
                             "connected.")

    return parser.parse_args()


def read_goi_file(file):
    """
    Reads the provided notes of interest
    :param file: path String of the Nodes of interest file
    :return: a Set of nodes
    """
    nodes = set()
    if os.path.isfile(file):
        with open(file) as FILE:
            for line in FILE:
                line = line.strip()
                nodes.add(line)
        return nodes
    else:
        print("Your provided file " + file + "does not exists")
        sys.exit(0)


def read_evidence_file(file):
    """
    Reads the provided evidence
    :param file: path String of the evidence file
    :return: Dictionary containing the interaction nodes and a Set of all the nodes in de given evidence network
    file
    """
    con_dict = dict()
    node_set = set()
    if os.path.isfile(file):
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
    else:
        print("Your provided file " + file + "does not exists")
        sys.exit(0)


def filter_gois(gois, uniques):
    """
    Filters the provided nodes of interest. They must be represented in the evidence file
    :param gois: Set of nodes
    :param uniques: Set of nodes present in the evidence file
    :return: Set of only nodes present in the evidence file
    """
    return set(filter(lambda x: x in uniques, gois))


def create_int_matrix(ev_dict, nodes):
    """
    :param ev_dict: Dictionary of the provided node connections from the evidence file
    :param nodes: Set if nodes provided via the goi
    :return: a Pandas Dataframe (connectivity matrix)
    Constructs a Pandas Dataframe from a Dictionary
    """
    index_dict = dict(zip(nodes, list(range(0, len(nodes)))))

    matrix = np.zeros((len(nodes), len(nodes)), dtype=int)
    for node1 in nodes:
        node1_index = index_dict[node1]
        for node2 in ev_dict[node1].keys():
            node2_index = index_dict[node2]
            matrix[node1_index, node2_index] = ev_dict[node1][node2]

    return matrix


def generate_network(int_df, gois, add_nrc):
    """
    :param int_df: Pandas Dataframe row and columns are nodes, values the connectivity probability
    :param gois: specific Set of nodes
    :param add_nrc: Boolean, True if NRCs need to be included else False (gois only)
    :return: Pandas Dataframe containing a subset of the initial/ provided Dataframe
    Constructs a sub-network from the user submitted interaction network
    """
    network_nodes = list()
    for goi in gois:
        connecting_nodes = select_rows(int_df, goi)
        if add_nrc:
            network_nodes.extend(connecting_nodes)
            end_nodes = list(map(lambda x: select_rows(int_df, x), connecting_nodes))
            end_nodes = list(filter(lambda x: x in goi, list(itertools.chain(*end_nodes))))
            network_nodes.extend(end_nodes)
        else:
            end_nodes = list(set(connecting_nodes) & gois)
            network_nodes.extend(end_nodes)

    rc_inf_df = reconstruct_int_df(int_df, set(network_nodes), gois)

    return rc_inf_df


def select_rows(int_df, node):
    """
    Select the column of a node and return the rows / nodes to which it connects
    :param int_df: Pandas Dataframe
    :param node: Node String from which the row needs to be selected
    :return: List of nodes
    """
    row_numbers = [i for i, e in enumerate(int_df[node]) if e > 0]
    row_nodes = int_df.index.values[row_numbers]
    return row_nodes.tolist()


def select_columns(int_df, node):
    """
    Select the row of node and return the columns/ nodes to which it connects
    :param int_df: Pandas Dataframe
    :param node: Node String from which the row needs to be selected
    :return: List of nodes
    """
    column_numbers = [i for i, e in enumerate(int_df.loc[node, :]) if e > 0]
    column_nodes = int_df.columns.values[column_numbers]
    return column_nodes


def reconstruct_int_df(int_df, n_nodes, gois):
    """
    :param int_df: Pandas Dataframe containing the initial/ provided Dataframe
    :param n_nodes: Set of networked nodes
    :param gois: Set of nodes of interest
    :return: Pandas Pandas Dataframe containing a subset of the initial/ provided Dataframe
    Based on a set of nodes, a new interaction matrix (Dataframe) is constructed
    """
    print("Reconstructing interacting matrix\n")
    index_dict = dict(zip(n_nodes, list(range(0, len(n_nodes)))))
    matrix = np.zeros((len(n_nodes), len(n_nodes)), dtype=int)
    for node1 in n_nodes:
        node1_index = index_dict[node1]
        for node2 in n_nodes:
            node2_index = index_dict[node2]
            if not node1 == node2:
                if node1 in gois or node2 in gois:
                    matrix[node1_index, node2_index] = int_df[node1][node2]

    return pd.DataFrame(matrix, columns=n_nodes, index=n_nodes)


def prune_network(int_df, gois):
    """
    Removes all nodes with a single connection
    :param int_df: Pandas Dataframe containing an interaction matrix
    :param gois: Set of Nodes provided by the user
    :return: Pandas Dataframe with an reduced interaction matrix
    """
    run = True
    while run:
        run = False
        binary_df = int_df.astype(bool).astype(int)
        diagonal_k = np.diag(np.dot(binary_df, binary_df))
        all_nodes = int_df.columns.values
        remove_nodes = all_nodes[np.where(diagonal_k == 0 | 1)[0]]
        for node in remove_nodes:
            if not (node in gois):
                int_df = int_df.drop(node, axis=0)
                int_df = int_df.drop(node, axis=1)
                run = True
    return int_df


def prune_network_limited(int_df, gois, max_size):
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
            print("Minimal network size reached: " + str(int_df.shape[1]))
        if counter >= int_df_nan.shape[1]:
            run = False
        else:
            counter += 1
    return int_df


def make_bfs_graph(df):
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


def is_connected(graph, root, leaf, number_nodes):
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


def make_sparse_network(int_df, gois):
    """
    Removes as many edges as possible form a given network
    :param int_df: Pandas Dataframe containing a interaction network
    :param gois: Set of provided Nodes of interest
    :return: Pandas Dataframe containing a network with the least number of edges possible
    """
    maximum = int(int_df.stack().max() + 1)
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

                bfs_graph = make_bfs_graph(int_df)

                if is_connected(bfs_graph, node1, node2, nb_nodes):
                    int_df_bup = int_df.copy()
                else:
                    int_df = int_df_bup.copy()
                    int_df_nan[node1][node2] = maximum
                    int_df_nan[node2][node1] = maximum
    return int_df


def write_cytoscape_output(df, out_f):
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
    write(super_string, out_f)


def write_cytoscape_node_table(all_nodes, gois, out_f):
    super_string = "{}\t{}\n".format("Node_name", "Node_type")
    for node in all_nodes:
        if node in gois:
            super_string += "{}\t{}\n".format(node, "NOI")
        else:
            super_string += "{}\t{}\n".format(node, "IGN")
    write(super_string, out_f)


def write(data, file):
    try:
        with open(file, 'w') as FILE_H:
            FILE_H.write(data)
    except IOError:
        print("The output file " + file + " could not be written")
        sys.exit(0)


if __name__ == "__main__":
    arguments = parse_arguments()
    main(arguments)
