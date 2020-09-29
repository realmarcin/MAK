#Mccord et al
setwd("~/Documents/integr8_genom/data/Scerevisiae/Mccord_et_al")

data <- read.table("compiledgenescores_noavg2.txt", header=T, row.names=1)

dim <- dim(data)
ranks <- c()
for(i in 1:dim[2]) {
ranks <- cbind(ranks, rank(-as.numeric(data[,i]), na.last=NA, ties.method="min"))
}

max <- apply(ranks, 2, max)
#normalize by max rank
ranks <- ranks/max

colnames(ranks) <- colnames(data)
row.names(ranks) <- row.names(data)
write.table(ranks, "ranks.txt", sep="\t")




#YETFASCO
setwd("~/Documents/integr8_genom/data/Scerevisiae/yeTFaSCo")

data <- read.table("20120126_allMotifData1.01.rdat", sep="\t", header=T, row.names=1)

dim <- dim(data)
ranks <- c()
for(i in 1:dim[2]) {
ranks <- cbind(ranks, rank(-as.numeric(data[,i]), na.last=NA, ties.method="min"))
}

max <- apply(ranks, 2, max)
#normalize by max rank
ranksnorm <- 1 - sweep(ranks, 2, max, "/")

colnames(ranksnorm) <- colnames(data)
row.names(ranksnorm) <- row.names(data)
write.table(ranksnorm, "20120126_allMotifData1.01_rankscore.rdat", sep="\t")

