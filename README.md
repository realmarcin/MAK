# MAK
Massive Associative K-biclustering

Index
https://github.com/realmarcin/MAK/blob/master/README.md#installation
https://github.com/realmarcin/MAK/blob/master/README.md#running-a-single-bicluster-search-using-precomputed-null-distributions

## Installation
Requirements:
- Java (tested with 1.8)
- R (tested with 2.15.1)

Obtain these JAR files:
- Found in this repo
```
MAK_build/MAK.jar
```
or build from scratch by
```
cd MAK_build/
source antbuild_git.sh
```
- JRI.jar (the easiest way is by installing the rJava R package, e.g. 
```
install.packages("rJava")
```
Once rJava is installed you can find the location of the JRI.jar file by:
```
system.file("jri",package="rJava")
```
- Add the paths to these JAR files to your system CLASSPATH variable, e.g on linux bash:
```
export CLASSPATH=$CLASSPATH:path/to/JRI.jar:path/to/MAK.jar
```
- Add JRI shared library files to LD_LIBRARY PATH variable, e.g. on linux bash:
```
export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:path/to/R-2.15.1/library/rJava/jri
```
- Check/set your R_HOME variable, e.g. on linux bash:
```
export R_HOME=path/to/R/2.15.1/lib64/R/
```

## Running a single bicluster search using precomputed null distributions
1. Download the MAK yeast example tar ball:
https://github.com/realmarcin/MAK_results/raw/master/results/yeast/input/example/MAK_example.tar.gz
2. Place this tar ball in the top level MAK repo directory and extract it:
```
tar zxf MAK_example.tar.gz
```
3. Run a single MAK trajectory:
```
cd MAK_example
source run.sh
```

## Running the HPC MAK bicluster discovery pipeline
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
