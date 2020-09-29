
setwd("~/Documents/integr8_genom/Miner/miner_results/SOMR1_fit/")
data <- read.table("./global_vs_bicluster.txt",header=F,sep="\t")
dim(data)

datarand <- data[,4] + runif(length(data[,4]), 0, 0.1)

alphacol <- rgb(0, 0, 0,  alpha=10, maxColorValue=255)

png("global_vs_bicluster_cor_num.png",width=800,height=600)
plot(data[,3],datarand,xlab="Pearson correlation",ylab="Number of biclusters", col=alphacol)
dev.off(2)

png("global_vs_bicluster_cor_crit.png",width=800,height=600)
plot(data[,3],data[,5],xlab="Pearson correlation",ylab="Bicluster criterion", col=alphacol)
dev.off(2)

png("global_vs_bicluster_cor.png",width=800,height=600)
plot(data[,3],data[,6],xlab="Pearson correlation",ylab="Mean Pearson correlation for bicluster conditions", col=alphacol)
dev.off(2)


plot(density(data[which(data[,4]>0),3]))
lines(density(data[which(data[,4]==0),3]), col="red")


plot(density(data[which(data[,4]>0),6]))
lines(density(data[which(data[,4]==0),3]), col="red")