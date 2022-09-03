# Massive Associative K-biclustering -- MAK


[Installation](https://github.com/realmarcin/MAK/blob/master/README.md#installation)

[Yeast column bicluster single CPU example (precomputed nulls)](https://github.com/realmarcin/MAK/blob/master/README.md#running-a-single-bicluster-search-using-precomputed-null-distributions)

[MAK HPC bicluster discovery pipeline](https://github.com/realmarcin/MAK/blob/master/README.md#running-the-hpc-mak-bicluster-discovery-pipeline)


## Installation
### Requirements:
1. Java (tested with version 11.0.16)
2. R (tested with 4.2.1)
3. Java-to-R interface JRI (instructions below)

The installation has been tested in the bash shell on Linux and Mac OS.

### Installation steps:
- Use the MAK.jar file found in this repo
```
MAK_build/MAK.jar
```
or build it from scratch
```
cd MAK_build/
source antbuild_git.sh
```
- Install R package dependencies with these R commands on the R command line:
```
> install.packages("fields")
> install.packages("amap")
> install.packages("irr")
> install.packages("rJava")
```
- Once rJava is installed you can find the location of the JRI.jar file using this R command on the R command line:
```
> system.file("jri",package="rJava")
```
- Add the JRI and MAK JAR paths to your system CLASSPATH variable, e.g in linux bash:
```
export CLASSPATH=$CLASSPATH:path/to/JRI/JRI.jar:path/to/MAK.jar
```
- Check/set your R_HOME variable, e.g. in linux bash:
To find you R home dir use this R command on the R command line:
```
> R.home()
```
And use this path to set the $R_HOME environment variable:
```
export R_HOME=path/to/R
```
- Add JRI shared library files and R shared library files to the LD_LIBRARY PATH variable:
```
export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:path/to/JRI/:$R_HOME/lib
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
If you get a java library path error, you can specify this path in the java command:
```
java -Djava.library.path=path/to/JRI/ -Xmx2G DataMining.RunMiner -param MAK_parameters.txt -debug 0
```
4. You can perform searches for any starting point in this dataset by changing the value of the INIT_BLOCKS field in the  MAK_parameters.txt parameter input file. The indices are 1-offset and based on the row and column labels in the yeast_cmonkey.txt input file. The format of the value is:
```
{a,b,c/x,y,z}
```
where a,b,c,x,y,z are integers in the range of the dimensions of the input matrix.

## Running the MAK HPC bicluster discovery pipeline
1. Input data:
- Simple TSV file(s) with row and column labels
(with shared identifier axes if multiple data layers)

2. Edit fields in MAKflow input parameter file:

E.g:
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
