
Imax=100
Imin=10
Jmax=50
Jmin=10

#library(fastcluster)
library(amap)#
library(Hmisc)

#setwd("~/integr8functgenom/Miner/rda/common/yeast_data/")
source("/usr2/people/marcin/Genomics/html/applet/MicroArrayUp/MicroArrayUp/DataMining_R/Miner.R")
#load("zymonomas_mobilis_fit.Rdata")
load("yeast_cmonkey")

prefix <- "yeast_cmonkey"

expr_data_row=t(apply(expr_data,1,missfxn))
expr_data_col=apply(expr_data,2,missfxn)

nbs=allpossibleInitial(expr_data_row,Imin,Imax,Jmin,Jmax,"correlation",useAbs=0, isCol=0)
write.table(nbs, file=paste(prefix,"_Rimpute_STARTS_noabs_R.txt",sep=""), sep="\t")

nbs=allpossibleInitial(expr_data_col,Imin,Imax,Jmin,Jmax,"correlation",useAbs=0, isCol=0)
write.table(nbs, file=paste(prefix,"_Cimpute_STARTS_noabs_R.txt",sep=""), sep="\t")

nbs=allpossibleInitial(expr_data_row,Imin,Imax,Jmin,Jmax,"correlation",useAbs=1, isCol=0)
write.table(nbs, file=paste(prefix,"_Rimpute_STARTS_abs_R.txt",sep=""), sep="\t")

nbs=allpossibleInitial(expr_data_col,Imin,Imax,Jmin,Jmax,"correlation",useAbs=1, isCol=0)
write.table(nbs, file=paste(prefix,"_Cimpute_STARTS_abs_R.txt",sep=""), sep="\t")


nbs=allpossibleInitial(expr_data_row,Imin,Imax,Jmin,Jmax,"correlation",useAbs=0, isCol=1)
write.table(nbs, file=paste(prefix,"_Rimpute_STARTS_noabs_C.txt",sep=""), sep="\t")

nbs=allpossibleInitial(expr_data_col,Imin,Imax,Jmin,Jmax,"correlation",useAbs=0, isCol=1)
write.table(nbs, file=paste(prefix,"_Cimpute_STARTS_noabs_C.txt",sep=""), sep="\t")

nbs=allpossibleInitial(expr_data_row,Imin,Imax,Jmin,Jmax,"correlation",useAbs=1, isCol=1)
write.table(nbs, file=paste(prefix,"_Rimpute_STARTS_abs_C.txt",sep=""), sep="\t")

nbs=allpossibleInitial(expr_data_col,Imin,Imax,Jmin,Jmax,"correlation",useAbs=1, isCol=1)
write.table(nbs, file=paste(prefix,"_Cimpute_STARTS_abs_C.txt",sep=""), sep="\t")



nbs=allpossibleInitial(expr_data_row,Imin,Imax,Jmin,Jmax,"correlation",useAbs=0, isCol=0)
write.table(nbs, file=paste(prefix,"_STARTS_noabs_R.txt",sep=""), sep="\t")

nbs=allpossibleInitial(expr_data_col,Imin,Imax,Jmin,Jmax,"correlation",useAbs=0, isCol=0)
write.table(nbs, file=paste(prefix,"_Cimpute_STARTS_noabs_R.txt",sep=""), sep="\t")

nbs=allpossibleInitial(expr_data_row,Imin,Imax,Jmin,Jmax,"correlation",useAbs=1, isCol=0)
write.table(nbs, file=paste(prefix,"_STARTS_abs_R.txt",sep=""), sep="\t")

nbs=allpossibleInitial(expr_data_col,Imin,Imax,Jmin,Jmax,"correlation",useAbs=1, isCol=0)
write.table(nbs, file=paste(prefix,"_Cimpute_STARTS_abs_R.txt",sep=""), sep="\t")


###

Imax=100
Imin=10
Jmax=50
Jmin=10

#library(fastcluster)
library(amap)#
library(Hmisc)

#setwd("~/integr8functgenom/Miner/rda/common/yeast_data/")
source("/usr2/people/marcin/Genomics/html/applet/MicroArrayUp/MicroArrayUp/DataMining_R/Miner.R")
load("yeast_cmonkey_rscale.Rdata")

expr_data_row=t(apply(expr_data,1,missfxn))
expr_data_col=apply(expr_data,2,missfxn)

nbs=allpossibleInitial(expr_data_row,Imin,Imax,Jmin,Jmax,"correlation",useAbs=0, isCol=1)
write.table(nbs, file="yeast_cmonkey_rscale_STARTS_noabs_C.txt", sep="\t")

nbs=allpossibleInitial(expr_data_row,Imin,Imax,Jmin,Jmax,"correlation",useAbs=1, isCol=1)
write.table(nbs, file="yeast_cmonkey_rscale_STARTS_abs_C.txt", sep="\t")

nbs=allpossibleInitial(expr_data_row,Imin,Imax,Jmin,Jmax,"correlation",useAbs=0, isCol=0)
write.table(nbs, file="yeast_cmonkey_rscale_STARTS_noabs_R.txt", sep="\t")

nbs=allpossibleInitial(expr_data_row,Imin,Imax,Jmin,Jmax,"correlation",useAbs=1, isCol=0)
write.table(nbs, file="yeast_cmonkey_rscale_STARTS_abs_R.txt", sep="\t")


###

Imax=100
Imin=10
Jmax=50
Jmin=10

#library(fastcluster)
library(amap)#
library(Hmisc)

setwd("~/integr8functgenom/Miner/rda/common/dvh_data/")
source("/usr2/people/marcin/Genomics/html/applet/MicroArrayUp/MicroArrayUp/DataMining_R/Miner.R")
load("DvH_select.Rdata")

expr_data_row=t(apply(expr_data,1,missfxn))
expr_data_col=apply(expr_data,2,missfxn)

nbs=allpossibleInitial(expr_data_row,Imin,Imax,Jmin,Jmax,"correlation",useAbs=0, isCol=1)
write.table(nbs, file="DvH_select_STARTS_noabs_C.txt", sep="\t")

nbs=allpossibleInitial(expr_data_row,Imin,Imax,Jmin,Jmax,"correlation",useAbs=1, isCol=1)
write.table(nbs, file="DvH_select_STARTS_abs_C.txt", sep="\t")

nbs=allpossibleInitial(expr_data_row,Imin,Imax,Jmin,Jmax,"correlation",useAbs=0, isCol=0)
write.table(nbs, file="DvH_select_STARTS_noabs_R.txt", sep="\t")

nbs=allpossibleInitial(expr_data_row,Imin,Imax,Jmin,Jmax,"correlation",useAbs=1, isCol=0)
write.table(nbs, file="DvH_select_STARTS_abs_R.txt", sep="\t")


###


Imax=50
Imin=5
Jmax=25
Jmin=5

#library(fastcluster)
library(amap)#
library(Hmisc)


source("/usr2/people/marcin/Genomics/html/applet/MicroArrayUp/MicroArrayUp/DataMining_R/Miner.R")

setwd("/usr2/people/marcin/integr8functgenom/data/COALESCE/coalesce_ds4/y1/00")
load("with.pcl_00.Rdata")
nbs=allpossibleInitial(expr_data,Imin,Imax,Jmin,Jmax,"correlation",useAbs=0, isCol=1)
write.table(nbs, file="COALESCE_00_STARTS_C_noabs.txt", sep="\t")
nbs2=allpossibleInitial(expr_data,Imin,Imax,Jmin,Jmax,"correlation",useAbs=0, isCol=0)
write.table(nbs2, file="COALESCE_00_STARTS_R_noabs.txt", sep="\t")
nbsall=c(nbs, nbs2)
write.table(nbsall, file="COALESCE_00_STARTS_RC_noabs.txt", sep="\t")

setwd("/usr2/people/marcin/integr8functgenom/data/COALESCE/coalesce_ds4/y1/01")
load("with.pcl_01.Rdata")
nbs=allpossibleInitial(expr_data,Imin,Imax,Jmin,Jmax,"correlation",useAbs=0, isCol=1)
write.table(nbs, file="COALESCE_01_STARTS_C_noabs.txt", sep="\t")
nbs2=allpossibleInitial(expr_data,Imin,Imax,Jmin,Jmax,"correlation",useAbs=0, isCol=0)
write.table(nbs2, file="COALESCE_01_STARTS_R_noabs.txt", sep="\t")
nbsall=c(nbs, nbs2)
write.table(nbsall, file="COALESCE_01_STARTS_RC_noabs.txt", sep="\t")

setwd("/usr2/people/marcin/integr8functgenom/data/COALESCE/coalesce_ds4/y1/02")
load("with.pcl_02.Rdata")
nbs=allpossibleInitial(expr_data,Imin,Imax,Jmin,Jmax,"correlation",useAbs=0, isCol=1)
write.table(nbs, file="COALESCE_02_STARTS_C_noabs.txt", sep="\t")
nbs2=allpossibleInitial(expr_data,Imin,Imax,Jmin,Jmax,"correlation",useAbs=0, isCol=0)
write.table(nbs2, file="COALESCE_02_STARTS_R_noabs.txt", sep="\t")
nbsall=c(nbs, nbs2)
write.table(nbsall, file="COALESCE_02_STARTS_RC_noabs.txt", sep="\t")

setwd("/usr2/people/marcin/integr8functgenom/data/COALESCE/coalesce_ds4/y1/03")
load("with.pcl_03.Rdata")
nbs=allpossibleInitial(expr_data,Imin,Imax,Jmin,Jmax,"correlation",useAbs=0, isCol=1)
write.table(nbs, file="COALESCE_03_STARTS_C_noabs.txt", sep="\t")
nbs2=allpossibleInitial(expr_data,Imin,Imax,Jmin,Jmax,"correlation",useAbs=0, isCol=0)
write.table(nbs2, file="COALESCE_03_STARTS_R_noabs.txt", sep="\t")
nbsall=c(nbs, nbs2)
write.table(nbsall, file="COALESCE_03_STARTS_RC_noabs.txt", sep="\t")

setwd("/usr2/people/marcin/integr8functgenom/data/COALESCE/coalesce_ds4/y1/04")
load("with.pcl_04.Rdata")
nbs=allpossibleInitial(expr_data,Imin,Imax,Jmin,Jmax,"correlation",useAbs=0, isCol=1)
write.table(nbs, file="COALESCE_04_STARTS_C_noabs.txt", sep="\t")
nbs2=allpossibleInitial(expr_data,Imin,Imax,Jmin,Jmax,"correlation",useAbs=0, isCol=0)
write.table(nbs2, file="COALESCE_04_STARTS_R_noabs.txt", sep="\t")
nbsall=c(nbs, nbs2)
write.table(nbsall, file="COALESCE_04_STARTS_RC_noabs.txt", sep="\t")


#PPI STARTS
Imax=100
Imin=10
Jmax=100
Jmin=10

library(fastcluster)#library(amap)#
library(Hmisc)

setwd("~/integr8functgenom/Miner/rda/common/yeast_data/")
source("/usr2/people/marcin/Genomics/html/applet/MicroArrayUp/MicroArrayUp/DataMining_R/Miner.R")
load("COALESCE_00")

nbs=allpossibleInitial(interact_data,Imin,Imax,Jmin,Jmax,"binary",useAbs=0)
write.table(nbs, file="COALESCE_00_PPI_STARTS.txt", sep="\t")


#SIMULATED DATA STARTS

library(fastcluster)#library(amap)#
library(Hmisc)

Imax=40
Imin=10
Jmax=40
Jmin=10

setwd("~/integr8functgenom/Miner/rda/common/yeast_data/fake_over_0.1noise_1var_4blocks/")
source("/usr2/people/marcin/Genomics/html/applet/MicroArrayUp/MicroArrayUp/DataMining_R/Miner.R")

expr_data <- read.table("fake110427_4r_incr_over_rand_expr_forR.txt",sep="\t", header=T)
expr_data <- as.matrix(expr_data)
row.names(expr_data) <- expr_data[,1]
expr_data <- expr_data[,-1]

#expr_data=t(apply(expr_data,1,missfxn))
nbs=allpossibleInitial(expr_data,Imin,Imax,Jmin,Jmax,"correlation",useAbs=1)#"binary","correlation","euclidean"
write.table(nbs, file="fake110427_4r_incr_over_4blocks_rand_expr_STARTS_abs.txt", sep="\t")
nbs=allpossibleInitial(expr_data,Imin,Imax,Jmin,Jmax,"correlation",useAbs=0)#"binary","correlation","euclidean"
write.table(nbs, file="fake110427_4r_incr_over_4blocks_rand_expr_STARTS_noabs.txt", sep="\t")

expr_data <- read.table("fake110427_4c_incr_over_rand_expr_forR.txt",sep="\t", header=T)
expr_data <- as.matrix(expr_data)
row.names(expr_data) <- expr_data[,1]
expr_data <- expr_data[,-1]

#expr_data=apply(expr_data,2,missfxn)
nbs=allpossibleInitial(expr_data,Imin,Imax,Jmin,Jmax,"correlation",useAbs=1)#"binary","correlation","euclidean"
write.table(nbs, file="fake110427_4c_incr_over_4blocks_rand_expr_STARTS_abs.txt", sep="\t")
nbs=allpossibleInitial(expr_data,Imin,Imax,Jmin,Jmax,"correlation",useAbs=0)#"binary","correlation","euclidean"
write.table(nbs, file="fake110427_4c_incr_over_4blocks_rand_expr_STARTS_noabs.txt", sep="\t")



Imax=40
Imin=10
Jmax=40
Jmin=10

ibrary(fastcluster)#library(amap)#l
library(Hmisc)

setwd("~/integr8functgenom/Miner/rda/common/yeast_data/fake_0.1noise_1var/")
source("/usr2/people/marcin/Genomics/html/applet/MicroArrayUp/MicroArrayUp/DataMining_R/Miner.R")

expr_data <- read.table("fake110427_4r_incr_rand_expr_forR.txt",sep="\t", header=T)
expr_data <- as.matrix(expr_data)
row.names(expr_data) <- expr_data[,1]
expr_data <- expr_data[,-1]

#expr_data=t(apply(expr_data,1,missfxn))
nbs=allpossibleInitial(expr_data,Imin,Imax,Jmin,Jmax,"correlation",useAbs=1)#"binary","correlation","euclidean"
write.table(nbs, file="fake110427_4r_incr_rand_expr_STARTS_abs.txt", sep="\t")
nbs=allpossibleInitial(expr_data,Imin,Imax,Jmin,Jmax,"correlation",useAbs=0)#"binary","correlation","euclidean"
write.table(nbs, file="fake110427_4r_incr_rand_expr_STARTS_noabs.txt", sep="\t")

expr_data <- read.table("fake110427_4c_incr_rand_expr_forR.txt",sep="\t", header=T)
expr_data <- as.matrix(expr_data)
row.names(expr_data) <- expr_data[,1]
expr_data <- expr_data[,-1]

#expr_data=apply(expr_data,2,missfxn)
nbs=allpossibleInitial(expr_data,Imin,Imax,Jmin,Jmax,"correlation",useAbs=1)#"binary","correlation","euclidean"
write.table(nbs, file="fake110427_4c_incr_rand_expr_STARTS_abs.txt", sep="\t")
nbs=allpossibleInitial(expr_data,Imin,Imax,Jmin,Jmax,"correlation",useAbs=0)#"binary","correlation","euclidean"
write.table(nbs, file="fake110427_4c_incr_rand_expr_STARTS_noabs.txt", sep="\t")



###CONST

Imax=40
Imin=10
Jmax=40
Jmin=10

library(amap)
library(Hmisc)

setwd("~/integr8functgenom/Miner/rda/common/yeast_data/fake_0.1noise_1var/")
source("/usr2/people/marcin/Genomics/html/applet/MicroArrayUp/MicroArrayUp/DataMining_R/Miner.R")

expr_data <- read.table("fake110427_4c_const_nono_expr_rand.txt",sep="\t", header=T)
expr_data <- as.matrix(expr_data)

nbs=allpossibleInitial(expr_data,Imin,Imax,Jmin,Jmax,"correlation",useAbs=1,isCol=0)
write.table(nbs, file="fake110427_4c_const_nono_rand_expr_STARTS_abs.txt", sep="\t")
nbs=allpossibleInitial(expr_data,Imin,Imax,Jmin,Jmax,"correlation",useAbs=0, isCol=0)
write.table(nbs, file="fake110427_4c_const_nono_rand_expr_STARTS_noabs.txt", sep="\t")


expr_data <- read.table("fake110427_4c_const_over_expr_rand.txt",sep="\t", header=T)
expr_data <- as.matrix(expr_data)

nbs=allpossibleInitial(expr_data,Imin,Imax,Jmin,Jmax,"correlation",useAbs=1,isCol=0)
write.table(nbs, file="fake110427_4c_const_over_rand_expr_STARTS_abs.txt", sep="\t")
nbs=allpossibleInitial(expr_data,Imin,Imax,Jmin,Jmax,"correlation",useAbs=0, isCol=0)
write.table(nbs, file="fake110427_4c_const_over_rand_expr_STARTS_noabs.txt", sep="\t")


expr_data <- read.table("fake110427_4r_const_nono_expr_rand.txt",sep="\t", header=T)
expr_data <- as.matrix(expr_data)

nbs=allpossibleInitial(expr_data,Imin,Imax,Jmin,Jmax,"correlation",useAbs=1,isCol=0)
write.table(nbs, file="fake110427_4r_const_nono_rand_expr_STARTS_abs.txt", sep="\t")
nbs=allpossibleInitial(expr_data,Imin,Imax,Jmin,Jmax,"correlation",useAbs=0, isCol=0)
write.table(nbs, file="fake110427_4r_const_nono_rand_expr_STARTS_noabs.txt", sep="\t")

expr_data <- read.table("fake110427_4r_const_over_expr_rand.txt",sep="\t", header=T)
dim(expr_data)
expr_data <- as.matrix(expr_data)

nbs=allpossibleInitial(expr_data,Imin,Imax,Jmin,Jmax,"correlation",useAbs=1,isCol=0)
write.table(nbs, file="fake110427_4r_const_over_rand_expr_STARTS_abs.txt", sep="\t")
nbs=allpossibleInitial(expr_data,Imin,Imax,Jmin,Jmax,"correlation",useAbs=0, isCol=0)
write.table(nbs, file="fake110427_4r_const_over_rand_expr_STARTS_noabs.txt", sep="\t")

library(amap)
library(Hmisc)

Imax=40
Imin=10
Jmax=40
Jmin=10

setwd("~/Documents/integr8_genom/Miner/rda/common/fakedata/")
source("~/Documents/java/MAK/src/DataMining_R/Miner.R")


nbs=allpossibleInitial(expr_data,Imin,Imax,Jmin,Jmax,"correlation",useAbs=1,isCol=0)
write.table(nbs, file="fake110427_4c_const_nono_rand_expr_STARTS_abs.txt", sep="\t")
nbs=allpossibleInitial(expr_data,Imin,Imax,Jmin,Jmax,"correlation",useAbs=0, isCol=0)
write.table(nbs, file="fake110427_4c_const_nono_rand_expr_STARTS_noabs.txt", sep="\t")



nbs=allpossibleInitial(expr_data,Imin,Imax,Jmin,Jmax,"correlation",useAbs=1,isCol=0)
write.table(nbs, file="fake110427_4c_const_over_rand_expr_STARTS_abs.txt", sep="\t")
nbs=allpossibleInitial(expr_data,Imin,Imax,Jmin,Jmax,"correlation",useAbs=0, isCol=0)
write.table(nbs, file="fake110427_4c_const_over_rand_expr_STARTS_noabs.txt", sep="\t")



nbs=allpossibleInitial(expr_data,Imin,Imax,Jmin,Jmax,"correlation",useAbs=1,isCol=0)
write.table(nbs, file="fake110427_4r_const_nono_rand_expr_STARTS_abs.txt", sep="\t")
nbs=allpossibleInitial(expr_data,Imin,Imax,Jmin,Jmax,"correlation",useAbs=0, isCol=0)
write.table(nbs, file="fake110427_4r_const_nono_rand_expr_STARTS_noabs.txt", sep="\t")



nbs=allpossibleInitial(expr_data,Imin,Imax,Jmin,Jmax,"correlation",useAbs=1,isCol=0)
write.table(nbs, file="fake110427_4r_const_over_rand_expr_STARTS_abs.txt", sep="\t")
nbs=allpossibleInitial(expr_data,Imin,Imax,Jmin,Jmax,"correlation",useAbs=0, isCol=0)
write.table(nbs, file="fake110427_4r_const_over_rand_expr_STARTS_noabs.txt", sep="\t")


load("fake110427_4r_const_nono_0.25ppi_rand")
#expr_data <- read.table("fake110427_4r_const_nono_expr_rand.txt",sep="\t", header=T)
dim(expr_data)
class(expr_data)
#expr_data <- as.matrix(expr_data)

nbs=allpossibleInitial(expr_data,Imin,Imax,Jmin,Jmax,"correlation",useAbs=1,isCol=0)
write.table(nbs, file="fake110427_4r_const_nono_rand_expr_STARTS_abs_v2.txt", sep="\t")
nbs=allpossibleInitial(expr_data,Imin,Imax,Jmin,Jmax,"correlation",useAbs=0, isCol=0)
write.table(nbs, file="fake110427_4r_const_nono_rand_expr_STARTS_noabs_v2.txt", sep="\t")


Imax=25
Imin=5
Jmax=25
Jmin=5

setwd("~/Documents/integr8_genom/Miner/rda/common/fakedata/")
source("~/Documents/java/MAK/src/DataMining_R/Miner.R")
load("fake110427_4r_const_nono_0.25ppi_rand")

nbs=allpossibleInitial(expr_data,Imin,Imax,Jmin,Jmax,"correlation",useAbs=1,isCol=0)
write.table(nbs, file="fake110427_4r_const_nono_rand_expr_STARTS_abs_5_25_v2.txt", sep="\t")
nbs=allpossibleInitial(expr_data,Imin,Imax,Jmin,Jmax,"correlation",useAbs=0, isCol=0)
write.table(nbs, file="fake110427_4r_const_nono_rand_expr_STARTS_noabs_5_25_v2.txt", sep="\t")
