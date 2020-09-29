
###REAL
library(e1071)
library(amap)
library(Hmisc)
library(gplots)

setwd("/usr2/people/marcin/integr8functgenom/Miner/miner_results/eval_faker_fastGEE")
data <- read.table("members_faker_123__MSER_Kendall_GEERE___geneexp_member.txt",sep="\t",header=T)
hamdist <- read.table("members_faker_123__MSER_Kendall_GEERE__hammingblocks.txt",sep="\t",header=T)

row.names(data) <- data[,1]
data <- data[,-1]

datadim <- dim(data)

row.names(dist) <-hamdist[,1]
hamdist <- hamdist[,-1]

distdim <- dim(hamdist)

##blocks
##hamming clustering block axis
hclust_block <- hcluster(t(data), method="binary")
save(hclust_block, file="hclust_hamming_block_MSER_Kendall_GEERE")

pdf("hclust_hamming_block_MSER_Kendall_GEERE.pdf",width=8,height=11)
plot(hclust_block)
dev.off(2)

pdf("hclust_hamming_block_MSER_Kendall_GEERE_heat.pdf",width=8,height=11)
heatmap.2(as.matrix(hamdist), trace="none")
dev.off(2)

pdf("hclust_hamming_block_MSER_Kendall_GEERE_hist.pdf",width=8,height=11)
hist(c(as.matrix(hamdist)))
dev.off(2)

##cut tree and save
hcut <- 0.1
hclust_block_cut <- cutree(hclust_block,h=hcut)
length(hclust_block_cut)
write.table(hclust_block_cut, file="hclust_block_cut_MSER_Kendall_GEERE_h0.1.txt", sep="\t")

uheight <- unique(hclust_block$height)
cutdata <- matrix(0,nrow=length(uheight), ncol=length(hclust_block_cut))
for(i in 1:length(uheight)) {
   cutdata[i,] <- cutree(hclust_block,h=uheight[i])
}
colnames(cutdata) <- colnames(data)
write.table(cutdata, file=paste("hclust_block_cut_MSER_Kendall_GEERE_allcuts.txt",sep=""), sep="\t") 

out1=Dmerge(hclust_block$merge)



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

