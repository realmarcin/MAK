###cluster YEASTRACT TF data
library(gplots)
setwd("~/Documents/integr8_genom/Miner/miner_results/yeast/")
data <- read.table("./RegulationMatrix_Documented_20101213.txt", header=T, sep="\t")

tiff("YEASTRACT_cluster.tiff",height=1200, width = 1000)
#heatmap(as.matrix(data))
heatmap.2(as.matrix(data),trace="none",col=bluered(10), key=F)
dev.off(2)

data <- as.matrix(data)
sums <- rowSums(data)
hist(sums)