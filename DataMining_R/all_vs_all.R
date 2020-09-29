
library(gplots)

setwd("~/Documents/integr8_genom/Miner/miner_results/yeast/EXPR_round12345_merge_refine/allvsall")
dataMC <- as.matrix(read.table("./MAK_expr_0.25_vs_cMonkey_0.25.txt",sep="\t",header=T))
dataCC <- as.matrix(read.table("./cmonkey_0.25_vs_ccmonkey_0.25.txt",sep="\t",header=T))
dataMM <- as.matrix(read.table("./MAK_0.25_vs_MAK_0.25.txt",sep="\t",header=T))
dataMCo <- as.matrix(read.table("./MAK_expr_0.25_vs_coalesce_0.25.txt",sep="\t",header=T))
dataCoCo <- as.matrix(read.table("./coalesce_0.25_vs_coalesce_0.25.txt",sep="\t",header=T))
dataCoC <- as.matrix(read.table("./coalesce_0.25_vs_cmonkey_0.25.txt",sep="\t",header=T))

row.names(dataMC) <- dataMC[,1]
dataMC <- dataMC[,-1]
row.names(dataCC) <- dataCC[,1]
dataCC <- dataCC[,-1]
row.names(dataMM) <- dataMM[,1]
dataMM <- dataMM[,-1]
row.names(dataMCo) <- dataMCo[,1]
dataMCo <- dataC[,-1]
row.names(dataCoCo) <- dataCoCo[,1]
dataCoCo <- dataCoCo[,-1]
row.names(dataCoC) <- dataCoC[,1]
dataCoC <- dataCoC[,-1]

diag(dataMM) <- 0
diag(dataCC) <- 0
diag(dataCoCo) <- 0


br <- seq(0,1,0.1)

png("MAK_expr_0.25_vs_cmonkey_0.25.png",height=800, width=800)
heatmap.2(dataMC,trace="none", col="topo.colors",breaks=br)
dev.off(2)

png("cmonkey_0.25_vs_cmonkey_0.25.png",height=800, width=800)
heatmap.2(dataCC,trace="none", col="topo.colors",breaks=br)
dev.off(2)

png("MAK_0.25_vs_MAK_0.25.png",height=800, width=800)
heatmap.2(dataMM,trace="none", col="topo.colors",breaks=br)
dev.off(2)

png("MAK_0.25_vs_coalesce_0.25.png",height=800, width=800)
heatmap.2(dataMCo,trace="none", col="topo.colors",breaks=br)
dev.off(2)

png("coalesce_0.25_vs_coalesce_0.25.png",height=800, width=800)
heatmap.2(dataCoCo,trace="none", col="topo.colors",breaks=br)
dev.off(2)

png("coalesce_0.25_vs_cmonkey_0.25.png",height=800, width=800)
heatmap.2(dataCoC,trace="none", col="topo.colors",breaks=br)
dev.off(2)




setwd("~/Documents/integr8_genom/Miner/miner_results/eval/COALESCE/")
dataCo <- as.matrix(read.table("./COALESCE_vs_y1_00.txt",sep="\t",header=T))
dataM <- as.matrix(read.table("./MAK_vs_y1_00.txt",sep="\t",header=T))
row.names(dataCo) <- dataCo[,1]
dataCo <- dataCo[,-1]
row.names(dataM ) <- dataM [,1]
dataM  <- dataM [,-1]

png("COALESCE_vs_y1_00.png",height=800, width=800)
heatmap.2(dataCo,trace="none", col="topo.colors",breaks=br)
dev.off(2)

png("MAK_vs_y1_00.png",height=800, width=800)
heatmap.2(dataM,trace="none", col="topo.colors",breaks=br)
dev.off(2)