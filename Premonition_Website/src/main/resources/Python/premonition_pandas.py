import numpy as np
import pandas as pd
import itertools

ev_file = '/Users/jasperbosman/Desktop/test_network.txt'
genes_file = '/Users/jasperbosman/Desktop/my_GOI.txt'
cytoscape_file = "/Users/jasperbosman/Desktop/my_cytoscape_file.txt"


def read_goi_file(file):
    """
    :param file: path String of the Nodes of interest file
    :return: a Set of nodes
    """
    nodes = set()
    with open(file) as FILE:
        for line in FILE:
            line = line.strip()
            nodes.add(line)
    return nodes


def read_evidence_file(file):
    """
    :param file: path String of the evidence file
    :return: Dictionary containing the interaction nodes and a Set of all the nodes in de given evidence network
    reads the provided evidence file
    """
    con_dict = dict()
    node_set = set()
    with open(file) as FILE:
        for line in FILE:
            [node1, node2, prob] = line.strip().split("\t")
            if node1 not in con_dict.keys():
                con_dict[node1] = dict()
            con_dict[node1][node2] = prob

            # safety, making sure all nodes are accounted for
            node_set.add(node1)  # sufficient when interactions are bidirectional given in de evidence file else:
            node_set.add(node2)  # when interactions are uni-directional provided
    return con_dict, node_set


def create_df(ev_dict, nodes):
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

    print(np.dot(matrix, matrix))
    pass

    return pd.DataFrame(matrix, columns=nodes, index=nodes)


def generate_network(int_df, gois):
    """
    :param int_df: Pandas Dataframe row and columns are nodes, values the connectivity probability
    :param gois: specific Set of nodes
    :return: Pandas Dataframe containing a subset of the initial/ provided Dataframe
    Constructs a sub-network from the user submitted interaction network
    """
    # configured for one NRC
    network_nodes = list()
    for goi in gois:
        connecting_nodes = select_rows(int_df, goi)
        network_nodes.extend(connecting_nodes)
        end_nodes = list(map(lambda x: select_rows(int_df, x), connecting_nodes))
        # end_nodes = list(itertools.chain(*end_nodes))
        end_nodes = list(filter(lambda x: x in goi, list(itertools.chain(*end_nodes))))

        network_nodes.extend(end_nodes)

    rc_inf_df = reconstruct_int_df(int_df, set(network_nodes))

    return rc_inf_df


def reconstruct_int_df(int_df, n_nodes):
    """
    :param int_df: Pandas Dataframe containing the initial/ provided Dataframe
    :param n_nodes: Set of networked nodes
    :return: Pandas Pandas Dataframe containing a subset of the initial/ provided Dataframe
    Based on a set of nodes, a new interaction matrix (Dataframe) is constructed
    """
    index_dict = dict(zip(n_nodes, list(range(0, len(n_nodes)))))
    matrix = np.zeros((len(n_nodes), len(n_nodes)), dtype=int)
    for node1 in n_nodes:
        node1_index = index_dict[node1]
        for node2 in n_nodes:
            node2_index = index_dict[node2]
            if not node1 == node2:
                matrix[node1_index, node2_index] = int_df[node1][node2]

    return pd.DataFrame(matrix, columns=n_nodes, index=n_nodes)


def select_rows(int_df, node):
    # select the column of node and return the rows/ nodes to which it connects
    row_numbers = [i for i, e in enumerate(int_df[node]) if e > 0]
    row_nodes = int_df.index.values[row_numbers]
    return row_nodes.tolist()


def select_columns(int_df, node):
    # select the row of node and return the columns/ nodes to which it connects
    column_numbers = [i for i, e in enumerate(int_df.loc[node, :]) if e > 0]
    column_nodes = int_df.columns.values[column_numbers]
    return column_nodes


def prune_network(int_df, gois):
    """
    :param int_df: Pandas Dataframe containing an interaction matrix
    :param gois: Set of Nodes provided by the user
    :return: Pandas Dataframe with an reduced interaction matrix
    Removes all nodes with a single connection
    """
    binary_df = int_df.astype(bool).astype(int)
    new_df = pd.DataFrame()
    for node in binary_df.columns.values:
        if node in gois or sum(binary_df[node]) > 1:
            new_df[node] = int_df[node].astype(int)

    # remove unused rows
    print("****"*10)
    print(binary_df)
    for node in binary_df.columns.values:
        if not (node in new_df.columns.values):
            new_df = new_df.drop(node, axis=0)
    print(new_df)
    print("****"*10)
    return new_df


def detect_sub_networks(df):

    pass


def make_sparse_network(int_df, gois):
    run = True
    maximum = int(int_df.stack().max()+1)

    int_df_bup = int_df.copy()
    int_df_nan = int_df.copy().replace(0, np.nan)
    int_df_nan_bup = int_df_nan.copy()

    while run:
        run = False
        for node in int_df.columns.values:

            node1, node2 = int_df_nan.stack().idxmin()

            int_df[node1][node2] = 0
            int_df_nan[node1][node2] = np.nan
            # and it's diagonal
            int_df[node2][node1] = 0
            int_df_nan[node2][node1] = np.nan

            new_network_df = generate_network(int_df, gois)

            print("try " + node1 + "\t" + node2)

            # check if there are any nodes disconnected
            new_network_df = new_network_df.loc[(new_network_df != 0).any(axis=1)]
            new_network_df = new_network_df.loc[:, (new_network_df != 0).any(axis=0)]
            if (int_df.shape[1] == new_network_df.shape[1]) and int_df.shape[0] == new_network_df.shape[0]:
                print("removed " + node1 + "\t" + node2)
                run = True
                int_df_bup = int_df.copy()
                int_df_nan_bup = int_df_nan.copy()
            else:
                int_df = int_df_bup.copy()
                int_df_nan = int_df_nan_bup.copy()
                int_df_nan[node1][node2] = maximum
                int_df_nan[node2][node1] = maximum

    return int_df


def write_cytoscape_output(df, out_f):
    super_string = ""
    for node1 in df.columns.values:
        for node2 in df.index:
            if df[node1][node2] != 0:
                super_string += "{}\t{}\t{}\n".format(node1, node2, df[node1][node2])
    with open(out_f, 'w') as FILE_H:
        FILE_H.write(super_string)
    print(super_string)

goi_set = read_goi_file(genes_file)
[ev_con_dict, unique_node_set] = read_evidence_file(ev_file)

interaction_df = create_df(ev_con_dict, unique_node_set)

limited_inter_df = generate_network(interaction_df, goi_set)
print("Generated network")
print(limited_inter_df)

pruned_inter_df = prune_network(limited_inter_df, goi_set)
print("\n Pruned network")
print(pruned_inter_df)

sparse_network_df = make_sparse_network(pruned_inter_df, goi_set)
print("\n Sparse network")
print(sparse_network_df)

print("\n Cytoscape output")
write_cytoscape_output(sparse_network_df, cytoscape_file)

print("Done")

#        if not (node in gois or (sum(binary_df[node]) == 1)):
#            binary_df = binary_df.drop(node, axis=0) # rows
#            binary_df = binary_df.drop(node, axis=1) # columns
