setwd("~/Documents/integr8_genom/Miner/miner_results/yeast/EXPR_TF_round12_merge_refine/")
data <- read.table("./results_yeast_cmonkey_TF_2to200_absSTART_001_NERSC_refine_1_top_fl.vbl",header=T,comment="@")


datainit <- data[which(data$move_type == "initial" ),]
datafinal <- data[which(data$move_type != "initial" ),]


quants <- quantile(datainit$full_crit,probs=seq(0,1,0.01))
quants[66]

> length(which(datainit$full_crit >= quants[66]))
[1] 1455

