###MIN
#library(e1071)
library(amap)
library(Hmisc)
#library(gplots)

#setwd("/usr2/people/marcin/integr8functgenom/Miner/miner_results/yeast")
source("/usr2/people/marcin/Genomics/html/applet/MicroArrayUp/MicroArrayUp/DataMining_R/Miner.R")

###for large files
#length(scan("results_yeast_cmonkey_1_expr_round1234_exclude0.5_top_cut_0.985_0.05.txt", nlines=1, sep="\t", what="character"))
#m<-matrix(nrow=1259340, ncol=4837)
#filecon<-file("members_MSEC_KendallC_GEECE_yeast_round123_top_cut_0.98_0.05__MSEC_KendallC_GEECE__geneexp_member.txt", open="r")
#pos<-seek(filecon, rw="r")

#for(i in 1:10) {
# if (i  % %  5 == 0) {     
# print(i)   
# }
 #   tt<-readLines(filecon, n=1)
 #   tt2<-as.numeric(unlist(strsplit(tt, "\t")))
 #   if(i!=1) {
 #     m[(i-1),]<-t(tt2)
 #   }
 #   pos<-seek(filecon, rw="r")
 #}
 
data <- read.table("members_MSEC_KendallC_GEECE_yeast_round1234_top_cut_85_0.05_top100__MSEC_KendallC_GEECE__geneexp_member.txt",sep="\t",header=T)
row.names(data) <- data[,1]
data <- as.matrix(data[,-1])
datadim <- dim(data)

##blocks
##hamming clustering block axis
hclust_block <- hcluster(t(data), method="binary")
save(hclust_block,data, file="hclust_hamming_block_MSEC_KendallC_GEEREC__mergedround1round2_top0.01")

pdf("hclust_hamming_block_results_yeast_cmonkey_1_expr_round1234_top_cut_85_0.05_top100__MSEC_KendallC_GEECE.pdf",width=8,height=11)
plot(hclust_block)
dev.off(2)

##cut tree and save
hcut <- 0.1
hclust_block_cut <- cutree(hclust_block,h=hcut)
length(hclust_block_cut)
write.table(hclust_block_cut, file="results_yeast_cmonkey_1_expr_round1234_top_cut_85_0.05_top100__MSEC_KendallC_GEECE__h0.1.txt", sep="\t")

#get list of unique heights
uheight <- unique(hclust_block$height)
cutdata <- matrix(0,nrow=length(uheight), ncol=length(hclust_block_cut))
for(i in 1:length(uheight)) {
   cutdata[i,] <- cutree(hclust_block,h=uheight[i])
}
colnames(cutdata) <- c("label",colnames(data))
colnames(cutdata) <- colnames(data)
write.table(cutdata, file=paste("hclust_block_cut_results_yeast_cmonkey_expr_TF_round1234_top_cut_85_0.05_top100__MSEC_KendallC_GEECE.txt",sep=""), sep="\t") 



###MIN TF
library(e1071)
library(amap)
library(Hmisc)
library(gplots)

#setwd("/usr2/people/marcin/integr8functgenom/Miner/miner_results/yeast")
source("/usr2/people/marcin/Genomics/html/applet/MicroArrayUp/MicroArrayUp/DataMining_R/Miner.R")
data <- read.table("members_MSEC_KendallC_GEECE_yeast_batchperc0.2_top0.01__MSEC_KendallC_GEECE___geneexp_member.txt",sep="\t",header=T)
row.names(data) <- data[,1]
data <- data[,-1]
datadim <- dim(data)

##blocks
##hamming clustering block axis
hclust_block <- hcluster(t(data), method="binary")
save(hclust_block,data, file="hclust_hamming_block_MSEC_KendallC_GEEREC__mergedround1round2_top0.01")

pdf("hclust_hamming_block_results_yeast_cmonkey_1_expr_mergedround1round2_top0.01__MSEC_KendallC_GEECE_TF.pdf",width=8,height=11)
plot(hclust_block)
dev.off(2)

##cut tree and save
hcut <- 0.1
hclust_block_cut <- cutree(hclust_block,h=hcut)
length(hclust_block_cut)
write.table(hclust_block_cut, file="results_yeast_cmonkey_1_expr_mergedround1round2_top0.01__MSEC_KendallC_GEECE_TF__h0.1.txt", sep="\t")

#get list of unique heights
uheight <- unique(hclust_block$height)
cutdata <- matrix(0,nrow=length(uheight), ncol=length(hclust_block_cut))
for(i in 1:length(uheight)) {
   cutdata[i,] <- cutree(hclust_block,h=uheight[i])
}
colnames(cutdata) <- colnames(data)
write.table(cutdata, file=paste("hclust_block_cut_results_yeast_cmonkey_1_expr_mergedround1round2_top0.01__MSEC_KendallC_GEECE_TF.txt",sep=""), sep="\t") 



####
####
####
####UNTESTED!

###FULL
library(e1071)
library(amap)
library(Hmisc)
library(gplots)

#setwd("/usr2/people/marcin/integr8functgenom/Miner/miner_results/yeast")
source("/usr2/people/marcin/Genomics/html/applet/MicroArrayUp/MicroArrayUp/DataMining_R/Miner.R")
data <- read.table("members_MSEC_KendallC_GEECE_yeast_5percstart_top0.02__MSEC_KendallC_GEECE___geneexp_member.txt",sep="\t",header=T)
hamdist <- read.table("members_MSEC_KendallC_GEECE_yeast_5percstart_top0.02__MSEC_KendallC_GEECE__hammingblocks.txt",sep="\t",header=T)

row.names(data) <- data[,1]
data <- data[,-1]

datadim <- dim(data)

row.names(hamdist) <-hamdist[,1]
hamdist <- hamdist[,-1]

distdim <- dim(hamdist)

##blocks
##hamming clustering block axis
hclust_block <- hcluster(t(data), method="binary")
save(hclust_block,data, file="hclust_hamming_block_MSEC_KendallC_GEEREC__top0.02")

pdf("hclust_hamming_block_MSEC_KendallC_GEECE_top0.01.pdf",width=8,height=11)
plot(hclust_block)
dev.off(2)

pdf("hclust_hamming_block_MSEC_KendallC_GEECE_heat_top0.01.pdf",width=8,height=11)
heatmap.2(as.matrix(hamdist), trace="none")
dev.off(2)

pdf("hclust_hamming_block_MSEC_KendallC_GEECE_hist_top0.01.pdf",width=8,height=11)
hist(c(as.matrix(hamdist)))
dev.off(2)

##cut tree and save
hcut <- 0.1
hclust_block_cut <- cutree(hclust_block,h=hcut)
length(hclust_block_cut)
write.table(hclust_block_cut, file="hclust_block_cut_MSEC_KendallC_GEECE_top0.01_h0.1.txt", sep="\t")

#get list of unique heights
uheight <- unique(hclust_block$height)
cutdata <- matrix(0,nrow=length(uheight), ncol=length(hclust_block_cut))
for(i in 1:length(uheight)) {
   cutdata[i,] <- cutree(hclust_block,h=uheight[i])
}
colnames(cutdata) <- colnames(data)
write.table(cutdata, file=paste("hclust_block_cut_MSEC_KendallC_GEECE_allcuts_top0.01.txt",sep=""), sep="\t") 



###unfinished Dmerge code
   out1=Dmerge(hclust_block$merge)
   xJ=out1$sizeG
   
   uni_xJ=unique(out1$OrigInd[xJ])
      
   xJ1=xJ[(length(out1$OrigInd[xJ])-match(uni_xJ,rev((out1$OrigInd[xJ]))))+1] #index where unique and highest level lies
   #cluster each of xI clusters on exp scale
   for(jl in 1:length(xJ1)){
           Jc_jl=-out1$ListI[[xJ1[jl]]]
           
           Datamat_jl=Datamat[,Jc_jl]
           #genedist <- as.dist(CorDist(Datamat_jl,useAbs))
           #hc_jl=hclust(genedist)
           hc_jl=hcluster((Datamat_jl),method=distance)
           out1_jl=Dmerge(hc_jl$merge)
           xI=which(out1_jl$sizeG<=Imax & out1_jl$sizeG>=Imin)
           rmxI=match(unique(out1_jl$DisInd[xI]),unique(out1_jl$OrigInd[xI]))
           {if(length(rmxI[!is.na(rmxI)])==0) uni_xI=unique(out1_jl$OrigInd[xI])
                   else uni_xI=unique(out1_jl$OrigInd[xI])[-rmxI[!is.na(rmxI)]]}
           xI1=xI[(length(out1_jl$OrigInd[xI])-match(uni_xI,rev((out1_jl$OrigInd[xI]))))+1] #index where unique and highest level lies
           for(il in 1:length(xI1)){
                   Ic_il=-out1_jl$ListI[[xI1[il]]]
                   InitclustI=c(InitclustI,IcJctoijID(Ic_il,Jc_jl))
           }
   }




#gene_exps
##hamming clustering gene_exp axis
hclust_gene_exp <- hcluster(data, method="binary")
save(hclust_gene_exp, file="hclust_hamming_gene_exp")

pdf("hclust_hamming_gene_exp.pdf",width=8,height=11)
plot(hclust_gene_exp)
dev.off(2)

##cut tree and save
hcut <- 0.1
hclust_gene_exp_cut <- cutree(hclust_gene_exp,h=hcut)
length(hclust_gene_exp_cut)
write.table(hclust_gene_exp_cut, file="hclust_gene_exp_cut", sep="\t")

##Hamming distance output (input for network pairs etc.)
hamming_block_dist <- hamming.distance(t(data))

write.table(hamming_block_dist,file="hamming_block_distance.txt",sep="\t")

#hamming_gene_exp_dist <- hamming.distance(data)

#write.table(hamming_gene_exp_dist,file="hamming_gene_exp_distance.txt",sep="\t")

