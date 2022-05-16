#!/usr/bin/python3

import json


##Test function to see how well JSON out works. 
def main():

    stack_1 = [1, 2, 3, 4, 5 ,6 ,7, 8, 9, 10]
    stack_2 = ["a", "b", "c", "d", "e", "f", "g", "h", "i", "j",]

    data_out = []
    i = 0
    for node1 in stack_1:
        for node2 in stack_2:
            id_key = "obj_{}".format(i)
            data_out.append({id_key:[node1, node2]})
            i+=1
    with open("../../DataOut/123456.json", 'w') as f:
        json.dump(data_out, f)
    exit(0)

if __name__ == "__main__":
    main()



