library(pheatmap)
library(tiff)
library(Hmisc)
library(gplots)
library(RColorBrewer)
library(png)
source("~/Documents/java/MAK/src/DataMining_R/Miner.R")

#setwd("~/Documents/integr8_genom/Miner/miner_results/yeast/OVERLAPS")
setwd("~/Documents/integr8_genom/Miner/miner_results/yeast/final_paper_results/OVERLAPS")

mypalette <- rev(brewer.pal(6, "Blues"))
mypalette <- c(mypalette, brewer.pal(6, "YlOrBr"))
breaks <- seq(-3, 3,0.5 )

#load bicluster pairs
#vbl <- read.table("./overlap_select_mergedratio.txt",header=T,sep="\t",comment="@")
vbl <- read.table("./mergedscore_greater.txt",header=T,sep="\t",comment="@")
load("../yeast_cmonkey")


#load intersection

dim <- dim(vbl)

gintersects <- c()
gintersects_indexr <- c()
gintersects_indexl <- c()
gdiffright <- c()
gdiffleft <- c()
eintersects <- c()
eintersects_indexr <- c()
eintersects_indexl <- c()
ediffright <- c()
ediffleft <- c()


for(i in 1:(dim[1]/2)) {
print(i)

index1 <- as.character(vbl[2*i-1,3])
index2 <- as.character(vbl[2*i,3])
index1 <- gsub("\"","",index1)
index2 <- gsub("\"","",index2)

splitindex1 <- as.character(strsplit(index1, "/", fixed =T)[[1]])
splitindex2 <- as.character(strsplit(index2, "/", fixed =T)[[1]])

genes1 <- strsplit(splitindex1[1], ",")[[1]]
genes2 <- strsplit(splitindex2[1], ",")[[1]]

ginter <- intersect(genes1, genes2)
gdiffr <- setdiff(genes1, genes2)
gdiffl <- setdiff(genes2, genes1)

gintersects <- c(gintersects, list(ginter))
gdiffright <- c(gdiffright, list(gdiffr))
gdiffleft <- c(gdiffleft, list(gdiffl))

gintersects_indexr <- c(gintersects_indexr, list(match(ginter, genes1)))
gintersects_indexl <- c(gintersects_indexl, list(match(ginter, genes2)))


exps1 <- strsplit(splitindex1[2], ",")[[1]]
exps2 <- strsplit(splitindex2[2], ",")[[1]]

einter <- intersect(exps1, exps2)
ediffr <- setdiff(exps1, exps2)
ediffl <- setdiff(exps2, exps1)

#print(paste(length(exps1),length(exps2),length(einter),sep="\\t"))
print(paste(length(ginter),length(einter),sep="\\t"))

eintersects <- c(eintersects, list(einter))
ediffright <- c(ediffright, list(ediffr))
ediffleft <- c(ediffleft, list(ediffl))

eintersects_indexr <- c(eintersects_indexr, list(match(einter, exps1)))
eintersects_indexl <- c(eintersects_indexl, list(match(einter, exps2)))


#print(genes1)
}


for(i in 1:(dim[1]/2)) {
print(i)

#i <- 1
label <- paste(vbl[2*i-1,1], "_", vbl[2*i,1])

prefix <- "./expdata/results_yeast_cmonkey_1_expr_MSEC_KendallC_GEECE_refine12345_top_0.25_1.0_c_liven_reconstructed.txt__"
read1 <- paste(prefix , (vbl[2*i-1,1]-1),"_expdata",".txt",sep="")
print(paste("reading ", read1,sep=""))
data_matrix1 <- read.table(read1,sep="\t",row.names=1,header=T)     
print(dim(data_matrix1))
data_matrix_imp1 <- t(apply(data_matrix1,1,missfxn))
data_matrix_imp1_intersect1 <- data_matrix_imp1[gintersects_indexr[[i]],eintersects_indexr[[i]]]

rm1inter <- rowMeans(abs(data_matrix_imp1_intersect1))
cm1inter <- colMeans(data_matrix_imp1_intersect1)
data_matrix_imp1_intersect1 <- data_matrix_imp1_intersect1[order(rm1inter), order(cm1inter)]

###threshold
data_matrix_imp1_intersect1[data_matrix_imp1_intersect1 > 3] <- 3
data_matrix_imp1_intersect1[data_matrix_imp1_intersect1 < -3] <- -3
        
outpng <- paste(prefix ,"_",label,"_intersect.png",sep="")
pheatmap(data_matrix_imp1_intersect1, cluster_rows=F, cluster_cols=F, breaks=breaks,color=mypalette,cellwidth=1,cellheight=1,show_rownames=F,show_colnames=F, legend=F, filename = outpng)



data_matrix_imp1_part <- as.matrix(data_matrix_imp1[-gintersects_indexr[[i]], -eintersects_indexr[[i]]])
rm1part <- rowMeans(abs(data_matrix_imp1_part))
cm1part <- colMeans(data_matrix_imp1_part)
data_matrix_imp1_part <- data_matrix_imp1_part[order(rm1part), order(cm1part)]



read2 <- paste(prefix , (vbl[2*i,1]-1),"_expdata",".txt",sep="")
print(paste("reading ", read2,sep=""))
data_matrix2 <- read.table(read2,sep="\t",row.names=1,header=T)     
print(dim(data_matrix2))
data_matrix_imp2 <- t(apply(data_matrix2,1,missfxn))

data_matrix_imp2_part <- as.matrix(data_matrix_imp2[-gintersects_indexl[[i]], -eintersects_indexl[[i]]])
rm2part <- rowMeans(abs(data_matrix_imp2_part))
cm2part <- colMeans(data_matrix_imp2_part)
data_matrix_imp2_part <- data_matrix_imp2_part[order(rm2part), order(cm2part)]




data_matrix_imp1_part2 <- as.matrix(data_matrix_imp1[-gintersects_indexr[[i]], eintersects_indexr[[i]]])
data_matrix_imp1_part2 <- data_matrix_imp1_part2[order(rm1part), order(cm1inter)]

data_matrix_imp1_part_inter <- as.matrix(data_matrix_imp1[gintersects_indexr[[i]], -eintersects_indexr[[i]]])[order(rm1inter),]
data_matrix_imp1_part_inter <- as.matrix(data_matrix_imp1_part_inter)
data_matrix_imp1_part_inter <- data_matrix_imp1_part_inter[order(rm1inter), order(cm1part)]

rightone <- expr_data[as.numeric(gdiffright[[i]]), as.numeric(ediffleft[[i]])]
rightone <- rightone[order(rm1part),order(cm2part)]

toptop <- cbind(data_matrix_imp1_part, data_matrix_imp1_part2)
topbottom <- cbind(data_matrix_imp1_part_inter, mat.or.vec(length(gintersects_indexr[[i]]),length(eintersects_indexr[[i]])))
data_matrix_imp1_diffr <- rbind(toptop, topbottom)

###threshold
data_matrix_imp1_diffr[data_matrix_imp1_diffr > 3] <- 3
data_matrix_imp1_diffr[data_matrix_imp1_diffr < -3] <- -3
        
outpng <- paste(prefix ,"_",label,"_diffr.png",sep="")
pheatmap(data_matrix_imp1_diffr, cluster_rows=F, cluster_cols=F, breaks=breaks,color=mypalette,cellwidth=1,cellheight=1,show_rownames=F,show_colnames=F, legend=F, filename = outpng)

rightone[rightone > 3] <- 3
rightone[rightone < -3] <- -3

outpng <- paste(prefix ,"_",label,"_extrar.png",sep="")
pheatmap(rightone, cluster_rows=F, cluster_cols=F, breaks=breaks,color=mypalette,cellwidth=1,cellheight=1,show_rownames=F,show_colnames=F, legend=F, filename = outpng)






data_matrix_imp2_part2 <- as.matrix(data_matrix_imp2[-gintersects_indexl[[i]], eintersects_indexl[[i]]])
data_matrix_imp2_part2 <- data_matrix_imp2_part2[order(rm2part), order(cm1inter)]

data_matrix_imp2_part_inter <- data_matrix_imp2[gintersects_indexl[[i]], -eintersects_indexl[[i]]]
data_matrix_imp2_part_inter <- as.matrix(data_matrix_imp2_part_inter)
data_matrix_imp2_part_inter <- data_matrix_imp2_part_inter[order(rm1inter), order(cm2part)]


leftone <- as.matrix(expr_data[as.numeric(gdiffleft[[i]]), as.numeric(ediffright[[i]])])
leftone <- leftone[order(rm2part),order(cm1part)]

topbottom <- cbind(mat.or.vec(length(gintersects_indexl[[i]]),length(eintersects_indexl[[i]])), data_matrix_imp2_part_inter)
bottombottom <- cbind(data_matrix_imp2_part2, data_matrix_imp2_part)

data_matrix_imp2_diffl <- rbind(topbottom, bottombottom)

###threshold
data_matrix_imp2_diffl[data_matrix_imp2_diffl > 3] <- 3
data_matrix_imp2_diffl[data_matrix_imp2_diffl < -3] <- -3
        
outpng <- paste(prefix ,"_",label,"_diffl.png",sep="")
pheatmap(data_matrix_imp2_diffl, cluster_rows=F, cluster_cols=F, breaks=breaks,color=mypalette,cellwidth=1,cellheight=1,show_rownames=F,show_colnames=F, legend=F, filename = outpng)

leftone[leftone > 3] <- 3
leftone[leftone < -3] <- -3

if(length(leftone) > 0) {
outpng <- paste(prefix ,"_",label,"_extral.png",sep="")
pheatmap(leftone, cluster_rows=F, cluster_cols=F, breaks=breaks,color=mypalette,cellwidth=1,cellheight=1,show_rownames=F,show_colnames=F, legend=F, filename = outpng)
}


}
        
