library(ggplot2)
library(gplots)
setwd("~/Documents/integr8_genom/Miner/rda/common/yeast_data/")
data <- read.table("./yeast_cmonkey.txt",header=T,sep="\t")
datanoNA <- replace(data, is.na(data), 0)

#geneidsdata <- read.table("./yeast_cmonkey_geneids.txt",sep="\t")
#geneidsdata <- as.matrix(geneidsdata)
#geneids <-  geneidsdata[,1]

row.names(data) <- data[,1]
data <- data[,-1]

nucleolardata <- read.table("~/Documents/integr8_genom/data/Scerevisiae/Staub_etal/nucleolar.txt",header=T,sep="\t")

rplindex <- grep("RPL",nucleolardata[,1])
nucleolardata <- as.matrix(nucleolardata)[,2]

matched <- match(row.names(data),nucleolardata)

#RPL genes
datarpl <- datanoNA[rplindex,]
datanotrpl <- datanoNA[-rplindex,]

mean(datarpl != 0)
# 0.9719756
mean(apply(as.matrix(datarpl) != 0, 2, sd))
# 0.08983512

mean(datanotrpl != 0)
# 0.9772744
mean(apply(as.matrix(datanotrpl) != 0, 2, sd))
# 0.1215952

#423 genes from 439
datanucleolar <- datanoNA[matched[which(!is.na(matched))],]

dataNOTnucleolar <- datanoNA[which(is.na(matched)),]

mean(datanucleolar != 0)
# 0.9736975
mean(apply(as.matrix(datanucleolarnoNA) != 0, 2, sd))
# 0.1068396

mean(dataNOTnucleolar != 0)
# 0.9768122
mean(apply(as.matrix(dataNOTnucleolar) != 0, 2, sd))
# 0.1228479

heatmap(as.matrix(datanucleolar))

#colors <- rainbow(100, 0.1666667, 0.6666667)
colors <- gray(0:100/100)
heatmap.2(as.matrix(datanucleolar),trace="none",cexCol=0.6, col=colors)

heatmap.2(as.matrix(dataNOTnucleolar),trace="none",cexCol=0.6, col=colors)

heatmap.2(as.matrix(datarpl),trace="none",cexCol=0.6, col=colors)