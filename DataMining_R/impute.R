rm(list=ls())
setwd("~/Documents/integr8_genom/Miner/rda/common/yeast_data/")

expr_dataorig <- as.matrix(read.table("yeast_cmonkey.txt", sep="\t", header=T, row.names=1))
interact_dataorig <- as.matrix(read.table("BIOGRID-ORGANISM-Saccharomyces_cerevisiae-2.0.58.tab_trim_matrix.txt", sep="\t", header=T, row.names=1))

expr_data <- expr_dataorig
interact_data <- interact_dataorig
write.table(row.names(expr_data), "yeast_cmonkey_geneids.txt", sep="\t")
write.table(colnames(expr_data), "yeast_cmonkey_expids.txt", sep="\t")

row.names(expr_data) <- c()
colnames(expr_data) <- c()
row.names(interact_data) <- c()
colnames(interact_data) <- c()
save(expr_data, interact_data, file="yeast_cmonkey")

rmeans <- rowMeans(expr_dataorig, na.rm=T)
expr_data_impute <- expr_data
for(i in 1 : dim(expr_data)[1]) {
    test <- is.na(expr_data_impute[i,])
    if(length(test) > 0) {
        expr_data_impute[i,test] <- rmeans[i]
    }
}

write.table(expr_data_impute, "yeast_cmonkey_impute.txt", sep="\t")

rvar <- apply(expr_dataorig, 1, var, na.rm=T)
rvarrank <- rank(rvar)
rankcut <- length(rvar)*0.25

expr_data_75 <- expr_dataorig[which(rvarrank > rankcut),]
write.table(expr_data_75, "yeast_cmonkey_75.txt", sep="\t")

interact_data_75 <- interact_data[which(rvarrank > rankcut),]
interact_data_75 <- interact_data_75[,which(rvarrank > rankcut)]
write.table(interact_data_75, "yeast_cmonkey_ppi_75.txt", sep="\t")

expr_data <- expr_data_75
interact_data <- interact_data_75
row.names(expr_data) <- c()
colnames(expr_data) <- c()
row.names(interact_data) <- c()
colnames(interact_data) <- c()
save(expr_data, interact_data, file="yeast_cmonkey_75")

expr_data_impute_75 <- expr_data_impute[which(rvarrank > rankcut),]
write.table(expr_data_impute_75, "yeast_cmonkey_impute_75.txt", sep="\t")

geneids_75 <- row.names(expr_data_75)[which(rvarrank > rankcut)]
write.table(geneids_75, "yeast_cmonkey_75_geneids.txt", sep="\t")