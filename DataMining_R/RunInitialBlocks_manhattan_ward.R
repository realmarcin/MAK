
Imax=100
Imin=10
Jmax=50
Jmin=10

#library(fastcluster)
library(amap)#
library(Hmisc)

setwd("~/Documents/integr8_genom/Miner/rda/common/yeast_data/")
source("/Users/marcin/Documents/java/MAK/src/DataMining_R/Miner.R")
#source("/usr2/people/marcin/Genomics/html/applet/MicroArrayUp/MicroArrayUp/DataMining_R/Miner.R")
#load("zymonomas_mobilis_fit.Rdata")
load("yeast_cmonkey")

prefix <- "yeast_cmonkey"

expr_data_row=t(apply(expr_data,1,missfxn))
expr_data_col=apply(expr_data,2,missfxn)

nbs=allpossibleInitial(expr_data_row,Imin,Imax,Jmin,Jmax,"manhattan",useAbs=0, isCol=0,linkmethod="ward")
write.table(nbs, file=paste(prefix,"_Rimpute_STARTS_noabs_R.txt",sep=""), sep="\t")

nbs=allpossibleInitial(expr_data_col,Imin,Imax,Jmin,Jmax,"manhattan",useAbs=0, isCol=1,linkmethod="ward")
write.table(nbs, file=paste(prefix,"_Cimpute_STARTS_noabs_C.txt",sep=""), sep="\t")