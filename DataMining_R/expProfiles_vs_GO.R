library(ggplot2)
setwd("~/Documents/integr8_genom/Miner/miner_results/yeast/EXPR_round12345_merge_refine/")
data <- read.table("./expprofiles.txt",sep="\t",header=T)
 
data <- data[,-1]
colors <- gray(0:15/15)
heatmap.2(log(as.matrix(data)+0.5,2), trace="none", col=colors)




data2 <- read.table("./exp_vs_GO.txt",sep="\t",header=T,row.names=1)
data2 <- data2[,-which(colSums(data2)==0)]

colors <- gray(0:7/7)
#heatmap.2(log(as.matrix(data2)+0.5,2), trace="none", col=colors)
#dim <- dim(data2)
#rand <- replicate(dim[2], runif(dim[1], 0, 0.001)) 
heatmap.2(as.matrix(data2) , trace="none", col=colors,breaks=c(-83,-60,-40,-20,-0.9,0.9,20,40,63),distfun=function(x) as.dist((1-cor(t(x)))/2))#+rand

#B/W
data2plot <- data2
data2plot[data2plot < 0] <- -1
data2plot[data2plot > 0] <- 1
colors <- gray(0:2/2)
heatmap.2(as.matrix(data2plot), trace="none", col=colors)