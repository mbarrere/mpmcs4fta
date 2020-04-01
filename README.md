# MPMCS4FTA - Maximum Probability Minimal Cut Sets for Fault Tree Analysis
### Version 0.76.1

## Contents
- [Summary](#summary)
- [Requirements](#requirements)
- [Usage](#usage)
- [Execution example](#execution-example)
- [Configuration parameters](#configuration-parameters)
- [Licence](#licence)

## Summary
Fault Tree Analysis (FTA) constitutes a fundamental analytical tool aimed at modelling and evaluating how complex systems may fail.
Essentially, a fault tree (FT) involves a set of basic events that are combined using logic operators (e.g. AND and OR gates) in order to model how these events may lead to an undesired system state represented at the root of the tree (usually called the top event).
Basic events can be associated to hardware failures, human errors, and other cyber-physical conditions.

FTA comprises a broad family of methods and techniques used for qualitative and quantitative analysis, among them, *minimal cut sets (MCSs)*.
MCSs are minimal combinations of events that together may lead to the failure of the top level event.
MCSs are extremely useful to understand how failures might contribute to the failure of the system.
The problem, however, is that there might be hundreds of MCSs depending on the size and composition of the fault tree under analysis. 

Therefore, instead of computing and sorting all MCSs by their joint probability of occurrence, we focus directly on the *MCS with maximum probability (MPMSC)*.
We model the MPMCS problem as a Weighted Partial MaxSAT problem and solve it using a parallel SAT-solving architecture.
MPMCS4FTA is a Java-based tool, built on top of [LDA4CPS](https://github.com/mbarrere/lda4cps), that is able to efficiently identify the MPMCS on *fault trees* enriched with *independent failure probabilities*.

## Requirements
* Java 8
* Python 2/3
* Optional: Python 3, PuLP, and Gurobi to enable second MaxSAT solver

## Usage

1. ```java -jar mpmcs4fta.jar inputFile.json [-c configFile]```  
This command executes MPMCS4FTA with an input JSON file that describes the fault tree under analysis.

2. ```./web-viewer.py```  
This command launches the webviewer (Python-based HTTP server) that displays the fault tree as well as the maximum probability minimal cut set.
By default, the webviewer reads the file *view/sol.json* and displays it at [http://localhost:8000/viz.html](http://localhost:8000/viz.html)

## Execution example
### Fire Protection System
```
$> java -jar mpmcs4fta.jar examples/fps.json
```
```
== MPMCS4FTA v0.76.1 ==
== Started at 2020-04-01 01:32:55.508 ==

=> Loading problem specification...  done in 267 ms (0 seconds).
----------------------------------
Problem source: _s_
Problem target: tle
----------------------------------
=> Performing Tseitin transformation...  done in 138 ms (0 seconds).
|+| Solvers: [MaxSAT]

==================================
=> BEST solution found by MAX-SAT-SOLVER for:
Source: _s_
Target: tle
=== Security Metric ===
Joint probability of failure: 0.02
[+] Failed nodes: none
Total critical nodes: 2
[+] Maximum Probability Minimal Cut Set (MPMCS): (n1,0.2); (n2,0.1);
[*] Metric computation time: 77 ms (0 seconds).
==================================
Solution saved in: ./view/sol.json
== MPMCS4FTA ended at 2020-04-01 01:32:56.021 ==
```

##### Run the webviewer:
```
$> ./web-viewer.py
```
```
Running in Python 2...
('Started HTTP server on port ', 8000)
```
In the browser, go to [http://localhost:8000/viz.html](http://localhost:8000/viz.html)  
You should see the following fault tree along with the MPMCS (*Smoke sensor*, *Heat sensor*):

![Screenshot - simple example](https://github.com/mbarrere/mpmcs4fta/blob/master/screenshots/fire-protection-system.png)

---

## Configuration parameters

The configuration parameters are stored in the file `mpmcs4fta.conf`.
The tool also accepts a different configuration file as argument [-c configFile] to override the configuration in *mpmcs4fta.conf*. If the file `mpmcs4fta.conf` is not present, MPMCS4FTA uses the default configuration values (see below).

### Solvers
* ```solvers.sat4j = true``` Enables/disables default MaxSAT solver (*default=true*)
* ```solvers.optim = false``` Enables/disables second Gurobi-based MaxSAT solver (*default=false*)

### Python environment
* ```python.path = /usr/local/bin/python3``` Specifies the path to the Python 3 binary (only used with the second [optional] Gurobi-based solver).
* ```python.solver.path = python/optim.py``` Specifies the path to the Gurobi-based solver.

### Output flags
* ```output.sol = true``` Indicates MPMCS4FTA to output the JSON solution with the MPMCS nodes.
* ```output.wcnf = false``` Enables/disables the specification of the problem in WCNF (DIMACS-like) format (*default=false*). The WCNF file can be used to experiment with other MaxSAT solvers.
* ```output.txt = false``` Enables/disables the specification of the problem in a simple list-based representation file (*default=false*).


### Output folders
* ```folders.output = output``` Specifies the default output folder for *.wcnf* and *.txt* files.
* ```folders.view = view``` Specifies the default view folder where the solution (*sol.json*) is stored.

### Debug
* ```tool.debug = false``` Enables light debugging.
* ```tool.fulldebug = false``` Enables full (heavy) debugging.

## Licence
Apache License 2.0
