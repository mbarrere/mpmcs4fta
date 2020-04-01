#!/usr/local/bin/python3.7

# Author: Pasquale Malacaria <p.malacaria@qmul.ac.uk>
# Modified by: Martin Barrere <m.barrere@imperial.ac.uk>

import sys
#sys.path.append("/Library/gurobi810/mac64/bin/")
#print (sys.path)
from pulp import *
import math
import time
import ast
import json

#nodes=[1,2,3,4,5]
#clauses=[[-5,-4,-1,-2],[-5,-4,-2,-3]] #CNF
#costs={1:2,2:3.2,3:2,4:10,5:100000}
#graph_literals=[1,2,3,4,5]

#filename = 'input1/out-0n--exec1-.txt'
#filename = 'simple.txt'
#filename = 'input2/out-5000n--exec10-.txt'
#filename = 'input1/out-10000n--exec1-.txt'
#filename = '../output/2018.12.10/set10000-20000/out-10000n--exec1-.txt'

def loadGraphData(filename):
    graph_dict = {}
    with open(filename, 'r') as fileobj:
      for line in fileobj:
          key, value = line.split("=")
          graph_dict[key] = value

    nodes=ast.literal_eval(graph_dict['nodes'])
    clauses=ast.literal_eval(graph_dict['clauses'])
    costs=eval(graph_dict['costs'])
    graph_literals=ast.literal_eval(graph_dict['graph_literals'])

    for key in costs:
        #print(key, ':', costs[key])
        if (costs[key]=='inf'):
            #costs[key]=955555555;
            costs[key]=99999999;
            #costs[key]=sys.float_info.max;

    return {'nodes':nodes, 'clauses':clauses, 'costs':costs, 'graph_literals':graph_literals}

#def maxSATSolve(nodes=nodes,clauses=clauses, costs=costs, graph_literals=graph_literals):
def maxSATSolve(nodes, clauses, costs, graph_literals):
    model = LpProblem("simple", pulp.LpMaximize)
    start_time = time.time()
    x = LpVariable.dicts("x", nodes, lowBound=0, upBound = 1, cat=pulp.LpInteger)
    model += lpSum(x[n]*costs[n] for n in nodes if n in graph_literals)
    for clause in clauses:
        model += lpSum(x[n] if n >0 else (1-x[-1*n]) for n in clause) >= 1
    #print("building constraints time = %s seconds ---" % (time.time() - start_time))
    start_time = time.time()
    model.solve(GUROBI_CMD(msg=False))
    #print("optimization solution time = %s seconds ---" % (time.time() - start_time))
    return ('Optimization Status= ',model.status,'\nOptimization Value = ',model.objective.value(),'\nSolution=',[(x[n].name,x[n].varValue,costs[n]) for n in nodes])


def main():
    if (len(sys.argv)<2):
        print ("Please specify an input file with the problem specifiacation (TXT for the moment): ")
        print ("Example: $> python optim.py simple.txt")
        sys.exit()

    input_file = sys.argv[1]
    if not os.path.exists(input_file):
        #print ("Input file not found: " + input_file, file=sys.stderr)
        sys.stderr.write("Input file not found: " + input_file + "\n")
        sys.exit()

    input = loadGraphData(input_file)
    nodes = input["nodes"]
    clauses = input["clauses"]
    costs = input["costs"]
    graph_literals = input["graph_literals"]

    overall_resolution_time = time.time()
    sol=maxSATSolve(nodes, clauses, costs, graph_literals)
    sol_nodes=[x for x in sol[5] if x[1]==0 and x[2]>0]
    sol_total_cost=sum(x[2] for x in sol_nodes)

    #print("Overall time = %s seconds ---" % (time.time() - overall_resolution_time))
    #print(sol[0],'OK ' if sol[1]==1 else 'NOT FEASIBLE', '\nSolution size =',len(sol_nodes), '\nTotal cost = ', sol_total_cost, sol[4], sol_nodes)

    result = {}
    if sol[1]==1:
        result.update({ 'status' : 'ok'})
        result.update({ 'time_ms' : (time.time() - overall_resolution_time) * 1000})
        result.update({ 'cost' : sol_total_cost})
        #result.update({ 'solution' : [{x[0].replace("'","\""):str(x[2])} for x in sol_nodes]})
        #result.update({ 'solution' : [{x[0]:str(x[2])} for x in sol_nodes]})
        result.update({ 'solution' : {x[0]:x[2] for x in sol_nodes}})
        result.update({ 'size' : len(sol_nodes)})
    else:
        result.update({ "status" : "infeasible"})

    #print (result)
    print (json.dumps(result, indent=4, sort_keys=True))

if __name__ == '__main__':
    main()
