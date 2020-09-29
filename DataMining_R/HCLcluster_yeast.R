setwd("~/Documents/integr8_genom/Miner/rda/common/yeast_data/")
source("/java/DataMining_R/Miner.R")
load("yeast_cmonkey")

expr_data_row=t(apply(expr_data,1,missfxn))

heatmap.2(expr_data_row)