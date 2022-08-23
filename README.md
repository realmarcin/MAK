# MAK
Massive Associative K-biclustering

## Installation

Obtain these JAR files:
- MAK/MAK_build/MAK.jar
- JRI.jar (the easiest way is by installing the rJava R package, e.g. 
```
install.packages("rJava")
```
- Add these JAR files to your system CLASSPATH variable, e.g on linux bash:
```
export CLASSPATH=$CLASSPATH:path/to/JRI.jar:path/to/MAK.jar
```

## Running
1. Input data:
- Simple TSV file(s) with row and column labels
(with shared identifier axes if multiple data layers)

2. Edit fields in MAK parameter file:
- Input TSV data path(s) (up to four)
- Bicluster coherence criterion (column = MSEC_KENDALLC_FEM)
- null size boundaries (y=(2,200), x=(2,100))
- starting point size range (y=(10,100), x=(10,50))
- HCL starting point metric and linkage (Pearson correlation and Complete)
- move mode sequence (BS)
- HPC parameters
- number of CPUs (600)
- maximum wall time (48 hours)

3. Run:

java DataMining.MAKflow ...

Syntax:
-parameters = input parameter file
-server = HPC cluster
-account = HPC user account
-qos = HPC queue

4. Output:
- A TSV file of nonredundant, highest scoring biclusters in the input data
- A set of directories with all intermediate data and results
- Includes all executed commands and optional logging
