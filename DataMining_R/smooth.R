rm(list=ls())

library("fields")
#source ("/java/DataMining_R/Miner.R")
source("/usr2/people/marcin/Genomics/html/applet/MicroArrayUp/MicroArrayUp/DataMining_R/Miner.R")

#setwd("/Users/marcin/Documents/integr8_genom/Miner/rda/common/nulls_fake/nulls_101220")
#setwd("/Users/marcin/Documents/integr8_genom/Miner/rda/common/nulls_fake/nulls_101209")
#setwd("/Users/marcin/Documents/integr8_genom/Miner/rda/common/nulls_fake/nulls_101112")
#setwd("~/Documents/integr8_genom/Miner/rda/common/yeast_nulls/cmonkey/")

setwd("/usr2/people/marcin/integr8functgenom/Miner/rda/common/yeast_nulls/nulls_yeast_110214")

tpsSmooth("yeast_110214_")

tpsSmooth1D("110214_PPI")
tpsSmooth1D("110214_MAXTF")


tpsSmooth("yeast_110214_")

tpsSmooth1D("110214_PPI")
tpsSmooth1D("110214_MAXTF")

#OLD CODE
setwd("~/Documents/integr8_genom/Miner/rda/common/nulls_100618/")
ifiles=dir()
iIfiles1=ifiles[as.vector(sapply(ifiles,function(x)
grep("ppi",x)*grep("raw",x)))==1]
iIfiles1=iIfiles1[!is.na(iIfiles1)]

fi=1
tr1=matrix(scan(iIfiles1[fi]),nrow=2,byrow=TRUE)
xc=tr1[1,]
corsA=2:200
txtfile=tr1[-1,]
tps1=sreg(xc,as.vector(txtfile))
tps1p=matrix(predict(tps1,x=corsA),ncol=1)
Ffile=sub("raw","full",iIfiles1[fi])
write((tps1p),ncolumns=dim(tps1p)[1],file=Ffile,sep="\t")