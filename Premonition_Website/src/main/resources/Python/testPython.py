import sys

print("testPython called")


if __name__ == "__main__":
    protein_list = sys.argv[1]
    evidenceFile = sys.argv[2]
    network_level = 1 
    # if len(sys.argv) > 2:
    #     network_level = sys.argv[3]
    trim_nodes = True 
    # if len(sys.argv) > 3:
    #     trim_nodes = sys.argv[4]
    flat_network = True 
    # if len(sys.argv) > 4: 
    #     flat_network = sys.argv[5]

    
    print("protein_list = " + protein_list)
    print("evidenceFile = " + evidenceFile)
    print("network_level = " + str(network_level))
    print("trim_nodes = " + str(trim_nodes))
    print("flat_network " + str(flat_network))

