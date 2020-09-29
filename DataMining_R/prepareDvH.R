source("~/Documents/java/MAK/src/DataMining_R/Miner.R")
setwd("~/Documents/integr8_genom/Miner/rda/common/DvH_data/")
data <- read.table("./DvH_all.txt",sep="\t",header=T)
row.names(data) <- data[,1]
data <- data[,-c(1,2)]

sumna <- apply(t(apply(data, 1, is.na)), 1, sum)
sumnacol <- apply(apply(data, 2, is.na), 2, sum)

dim <- dim(data)
hist(sumna/dim[2])

hist(sumnacol/dim[1])

#remove genes > 0.9 missing
datafilterrow <- data[-which(sumna/dim[2] > 0.9), ]

dim_filterrow <- dim(datafilterrow)

sumna_filterrow <- apply(t(apply(datafilterrow, 1, is.na)), 1, sum)
sumnacol_filterrow <- apply(apply(datafilterrow, 2, is.na), 2, sum)

#remove experiments > 0.2 genes missing
data_filterrowcol <- datafilterrow[,-which(sumnacol_filterrow/dim_filterrow[1] > 0.2)]


data_filterrowcol_impute <- t(apply(data_filterrowcol, 1, missfxn))

heatmap(as.matrix(data_filterrowcol_impute))

hist(rowMeans(data_filterrowcol_impute))
hist(colMeans(data_filterrowcol_impute))


rmean <- apply(data_filterrowcol, 1, function(x) mean(x[!is.na(x)]))
data_filterrowcol_sweep <- sweep(data_filterrowcol, 1, rmean)
cmean <- apply(data_filterrowcol_sweep, 2, function(x) mean(x[!is.na(x)]))
data_filterrowcol_sweep <- sweep(data_filterrowcol_sweep, 2, cmean)

hist(rowMeans(t(apply(data_filterrowcol_sweep, 1, missfxn))))
hist(colMeans(apply(data_filterrowcol_sweep, 2, missfxn)))

write.table(data_filterrowcol_sweep, "Ecoli_select.txt", sep="\t")
write.table(row.names(data_filterrowcol_sweep), "Ecoli_select_geneids.txt", sep="\t", col.names=F)
expr_data <- as.matrix(data_filterrowcol_sweep)
save(expr_data, file="Ecoli_select.Rdata")


###rows

rmean <- apply(data_filterrowcol, 1, function(x) mean(x[!is.na(x)]))
data_filterrow_sweep <- sweep(data_filterrowcol, 1, rmean)

write.table(data_filterrow_sweep, "Ecoli_select.txt", sep="\t")
write.table(row.names(data_filterrow_sweep), "Ecoli_select_geneids.txt", sep="\t", col.names=F)
expr_data <- as.matrix(data_filterrow_sweep)
save(expr_data, file="Ecoli_select.Rdata")

