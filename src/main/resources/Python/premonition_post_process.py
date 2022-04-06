import os
import glob

nw_dir = "/Users/jasperbosman/Desktop/tmp/CGDB/Step 1 - Data-collection incl Network generation/All_circadian_genes/PREM_networks/Premonition_networks_SpecialCases_950/"
gois_dir = "/Users/jasperbosman/Desktop/tmp/CGDB/Step 1 - Data-collection incl Network generation/All_circadian_genes/PREM_String_inputs_SpecialCases_950/"
out_dir = "/Users/jasperbosman/Desktop/tmp/CGDB/Step 1 - Data-collection incl Network generation/All_circadian_genes/PREM_networks/Premonition_networks_SpecialCases_950_sparse/"


def main():
    if not os.path.isdir(out_dir):
        os.mkdir(out_dir)

    nw_txt_files = glob.glob(nw_dir + "*.txt")

    for nw_file in nw_txt_files:
        print(nw_file)
        tmp = nw_file.replace(nw_dir, "")
        tmp = tmp.split("_")[0]
        goi_file = gois_dir + tmp+"_uniprotIDs_StringIDs.tsv"
        gois = read_goi_file(goi_file)
        f_nodes = filter_nw_file(nw_file, gois)

        out_f = out_dir + tmp + "_PREM_sparse_network.tsv"
        write_cytoscape_output(f_nodes, out_f)

        out_f = out_dir + tmp + "_PREM_sparse_nodes.tsv"
        write_cytoscape_node_table(f_nodes, gois, out_f)


def read_goi_file(file):
    nodes = set()
    with open(file) as FILE:
        for line in FILE:
            line = line.strip()
            nodes.add(line)
    return nodes


def filter_nw_file(file, gois):
    """
    Reads the provided notes of interest
    :param file: path String of the Nodes of interest file
    :return: a Set of nodes
    """
    nodes_count = dict()
    nodes = dict()
    with open(file) as FILE:
        for line in FILE:
            line = line.strip()
            el = sorted(line.split("\t")[0:2])

            if el[0] in nodes.keys():
                if not el[1] in nodes[el[0]]:
                    nodes[el[0]][el[1]] = "PP"
                    if el[0] in nodes_count:
                        nodes_count[el[0]] += 1
                    else:
                        nodes_count[el[0]] = 1
                    if el[1] in nodes_count:
                        nodes_count[el[1]] += 1
                    else:
                        nodes_count[el[1]] = 1
                #else:
                # reverse link already exits
            else:
                nodes[el[0]] = {el[1]: "PP"}
                nodes_count[el[0]] = 1
                if el[1] in nodes_count:
                    nodes_count[el[1]] += 1
                else:
                    nodes_count[el[1]] = 1


    filtered_nodes = dict()
    for node in nodes.keys():
        if node in gois or nodes_count[node] > 1:
            for node2 in nodes[node].keys():
                if node2 in gois:
                    if node in filtered_nodes.keys():
                        filtered_nodes[node].append(node2)
                    else:
                        filtered_nodes[node] = [node2]
                else:
                    if nodes_count[node2] > 1:
                        if node in filtered_nodes.keys():
                            filtered_nodes[node].append(node2)
                        else:
                            filtered_nodes[node] = [node2]

    return filtered_nodes


def write_cytoscape_output(nodes, out_f):
    super_string = "{}\t{}\t{}\n".format("Node_name", "interaction", "Node_name_B")
    for node1 in nodes:
        for node2 in nodes[node1]:
            super_string += "{}\t{}\t{}\n".format(node1,'PP', node2)
    write(super_string, out_f)


def write_cytoscape_node_table(nodes, gois, out_f):
    super_string = "{}\t{}\n".format("Node_name", "Node_type")
    for node1 in nodes:
        if node1 in gois:
            super_string += "{}\t{}\n".format(node1, "NOI")
        else:
            super_string += "{}\t{}\n".format(node1, "IGN")

        for node2 in nodes[node1]:
            if node2 in gois:
                super_string += "{}\t{}\n".format(node2, "NOI")
            else:
                super_string += "{}\t{}\n".format(node2, "IGN")
    write(super_string, out_f)


def write(data, file):
    with open(file, 'w') as FILE_H:
        FILE_H.write(data)


main()
