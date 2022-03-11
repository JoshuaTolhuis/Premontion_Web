import sys

class StrNode:
    def __init__(self, n1, n2, prob):
        self.name = n1
        self.connections = {n2: int(prob)}
        self.is_base_node = 0

    def add_edge(self, n2, prob):
        self.connections[n2] = int(prob)

    def set_base_object(self, base_n_value):
        self.is_base_node = base_n_value

    def get_base_con_names(self):
        if self.is_base_node:
            return list(self.connections.keys())
        else:
            return list()


def read_ioi_list(items_file):
    """
    This function reads a list of proteins (or other items) that will be used as initial seeds for the network: Node1\n
    :param items_file: path the to file containing the proteins of interest
    :return: a list containing the names of the proteins of interest
    """
    b_nodes = list()
    with open(items_file) as file_handle:
        for line in file_handle:
            line = line.strip()
            b_nodes.append(line)

    return b_nodes


def read_string_file(str_file):
    """
    This function reads a file containing the String interactions: Node1\tNode2\tprobability
    :param str_file: path to the Sting evidence file
    :return: dictionary, of which the key is Node1_name and the value a StrNode object
    """
    edges = dict()
    with open(str_file) as file_handle:
        for line in file_handle:
            line = line.strip()
            [node1, node2, prob] = line.split('\t')
            if node1 in edges.keys():
                edges[node1].add_edge(node2, prob)
            else:
                edges[node1] = StrNode(node1, node2, prob)
    return edges


def make_base_network(s_nodes, b_nodes, base_n_value=1):
    """
    This function
    :param s_nodes: dictionary with protein name as key and StrNode objects as value
    :param b_nodes: list of base node names
    :param base_n_value: iteration value of the base nodes
    :return: list of the updated StrNodes
    """
    for base_name in b_nodes:
        if base_name in s_nodes:
            s_nodes[base_name].set_base_object(base_n_value)

    return s_nodes


def update_base_nodes(str_nodes):
    """
    For expanding the network, we need to update the base nodes list. Namely, all connecting nodes to the base node
    need to be considered as base nodes
    :param str_nodes: dictionary a node name as key and a StrNode as value
    :return new_base_nodes: a new list of bases node names
    """
    new_b_nodes = list()
    for node_name in str_nodes:
        node_obj = str_nodes[node_name]
        new_b_nodes.extend(node_obj.get_base_con_names())

    return new_b_nodes


def get_networked_nodes(s_nodes):
    """
    This functions filters and returns the nodes used in the generated network (from all String nodes)
    :param s_nodes: all String nodes
    :return nw_nodes: list of Node Objects only used in the network
    """
    nw_nodes = list()
    for s_name in s_nodes:
        if s_nodes[s_name].is_base_node != 0:
            nw_nodes.append(s_nodes[s_name])

    return nw_nodes


def remove_bi_directionality(n_nodes):
    check_dict = dict()
    new_n_nodes = list()
    for node_obj in n_nodes:
        elements_to_pop = list()
        for n2 in node_obj.connections.keys():
            name1 = node_obj.name+':'+n2
            name2 = n2+':'+node_obj.name

            if not(name1 in check_dict.keys() or name2 in check_dict.keys()):
                elements_to_pop.append(n2)
                check_dict[name1] = 1
        for ele in elements_to_pop:
            node_obj.connections.pop(ele, "None")

        new_n_nodes.append(node_obj)

    return new_n_nodes


def trim_end_nodes(n_nodes, iterations):
    """
    This function removes the most outer nodes of a branch
    :param n_nodes: list of Node Objects used in the generated network
    :param iterations: the number of times the outer nodes will be removed
    :return new_nw_nodes: a reduced list of he Node Objects used in the generated network
    """
    new_nw_nodes = dict()
    for i in range(0,iterations, 1):
        for node_obj in n_nodes:
            pass

    return new_nw_nodes


def flatten_network():
    pass


def save_network(nodes):
    for n1 in nodes:
        n1_name = n1.name
        for n2 in n1.connections:
            print(n1_name + "\t" + n2)

if __name__ == "__main__":
   # protein_list = '/Users/jasperbosman/Desktop/TEST_genes.txt'
   # evidenceFile = '/Users/jasperbosman/Desktop/TEST_String_yeast_protein.links.v9.0_filter>=0.7.txt'
   # network_level = 1
   # trim_nodes = True
   # flat_network = True

    print(len(sys.argv))
    
    protein_list = sys.argv[1]
    evidenceFile = sys.argv[2]
    network_level = 1 
    trim_nodes = True 
    flat_network = True
    
    string_nodes = read_string_file(evidenceFile)
    base_nodes = read_ioi_list(protein_list)

    string_nodes = make_base_network(string_nodes, base_nodes)

    for i in range(0, network_level, 1):
        new_base_nodes = update_base_nodes(string_nodes)
        string_nodes = make_base_network(string_nodes, new_base_nodes, i+2)

    networked_nodes = get_networked_nodes(string_nodes)
    networked_nodes = remove_bi_directionality(networked_nodes)

    if trim_nodes:
        networked_nodes = trim_end_nodes(networked_nodes, network_level)

    if flat_network:
        flatten_network()

    save_network(networked_nodes)

    print("premonition operation complete.")