load("synthdata090621_c")
Data <- Eall
InterF <- Iall
save(Data, InterF, file="synthdata090621_c")
write.table(Data,file="synthdata090621_c_expr.txt",sep="\t")
write.table(InterF,file="synthdata090621_c_inter.txt",sep="\t")


rm(list=ls())

setwd("~/Documents/integr8_genom/Miner/miner_results/yeast/cMonkey_yeast_results/")
Data <- read.table("cMonkey_yeast.txt", sep="\t", skip=1) #header=T, row.names=1

write.table(Data, file="cMonkey_yeast.txt",sep="\t")