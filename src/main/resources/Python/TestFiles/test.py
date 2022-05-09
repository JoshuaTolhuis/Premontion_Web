#!/usr/bin/python3

from ast import arguments
import sys
import argparse

##arguments
args = None

def main():
    with open(args.evidence_file) as FILE:
        for line in FILE:
            print(line.strip().split("\t"))

    return 0

##Parses arguments 
def parseArgs():
    parser = argparse.ArgumentParser(description='To setup proteins and reference files.')
    parser.add_argument('-ef', '--evidence_file', type=str, required=False, help="A file containing proteins of interest.")
    parser.add_argument('-rf', '--reference_file',type=str, required=False, help="A file with reference genes for the query.")
    parser.add_argument('-no', '--node_output', type=str, required=False, help="File to save constructed nodes to.")
    parser.add_argument('-co', '--cyto_output',type=str, required=False, help="File to save cytoscape node graph data to.")
    parser.add_argument('-NRCs', '--include_NRCs', type=bool, required=False, help="Include NRCs, True or False.")
    parser.add_argument('-re', '--remove_edges', type=bool, required=False, help="Remove the lowest scoring edges, True or False.")
    parsed_arguments = parser.parse_args()
    return parsed_arguments

if __name__ == "__main__":
    args = parseArgs()
    exit(main())
