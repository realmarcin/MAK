###MIN
library(e1071)
library(amap)
library(Hmisc)
library(gplots)

#setwd("/usr2/people/marcin/integr8functgenom/Miner/miner_results/yeast")
source("/usr2/people/marcin/Genomics/html/applet/MicroArrayUp/MicroArrayUp/DataMining_R/Miner.R")
data <- read.table("memberdata.txt",sep="\t",header=T)
row.names(data) <- data[,1]
data <- data[,-1]
datadim <- dim(data)

##blocks
##hamming clustering block axis
hclust_block <- hcluster(t(data), method="binary")
save(hclust_block,data, file="hclust_hamming_block_memberdata")

pdf("hclust_hamming_block_memberdata.pdf",width=8,height=11)
plot(hclust_block)
dev.off(2)

##cut tree and save
hcut <- 0.1
hclust_block_cut <- cutree(hclust_block,h=hcut)
length(hclust_block_cut)
write.table(hclust_block_cut, file="memberdata__h0.1.txt", sep="\t")

#get list of unique heights
uheight <- unique(hclust_block$height)
cutdata <- matrix(0,nrow=length(uheight), ncol=length(hclust_block_cut))
for(i in 1:length(uheight)) {
   cutdata[i,] <- cutree(hclust_block,h=uheight[i])
}
colnames(cutdata) <- colnames(data)
write.table(cutdata, file=paste("hclust_block_cut_memberdata.txt",sep=""), sep="\t") 

