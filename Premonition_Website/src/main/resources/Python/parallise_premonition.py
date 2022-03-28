from multiprocessing import Pool
import glob
import os
import premonition_module as pm

string_evidence_files_dir = '/data/storix2/alife/Jasper/Stringfiles/'
input_gene_list_dir = '/data/storix2/alife/Jasper/all_circ_genes/Missing_networks/Retry/'
out_file_dir = input_gene_list_dir + 'Premonition_networks/'

tax_2_org_file = "/data/storix2/alife/Jasper/All_organisms.organism_2_taxonID.tsv"

nb_cores = 20


def construct_premonition_object(s_e_f, node_dir, out_dir, tax_file):
    premonition_objects = list()

    if not os.path.isdir(out_dir):
        os.mkdir(out_dir)

    org_2_tax_dict = read_org_2_taxid_file(tax_file)

    id_txt_files = glob.glob(node_dir+"*.tsv")
    for id_file in id_txt_files:
        print(id_file)
        id_file_r = id_file.replace(node_dir,"")
        org = id_file_r.split("_")[0]
        tax = org_2_tax_dict[org]

        string_p_file = s_e_f + tax + "protein.links.v11.0.tsv"
        node_file = id_file
        net_out_file = out_dir + id_file_r + "_PREM_network.txt"
        node_out_file = out_dir + id_file_r + "_PREM_nodes.txt"
        min_network_size = 250
        incl_nrcs = True
        remove_edges = True

        premonition_objects.append(pm.Premonition(string_p_file, node_file, node_out_file, net_out_file, min_network_size, incl_nrcs, remove_edges))

    return premonition_objects


def read_org_2_taxid_file(tax_file):
    trans_dict = dict()

    with open(tax_file, "r") as FILE:
        for line in FILE:
            line = line.strip()
            elements = line.split("\t")
            trans_dict[elements[0]] = elements[1]
    return trans_dict


def run_prem(prem_object):
    prem_object.run()


if __name__ == '__main__':

    print("Starting Running Premonition on {} cores".format(nb_cores))
    prem_objects = construct_premonition_object(string_evidence_files_dir, input_gene_list_dir, out_file_dir, tax_2_org_file)

    with Pool(processes=nb_cores) as pool:
#        pool.map(run_prem, prem_objects)
         pool.apply_async(run_prem, prem_objects)
    print("Done")
