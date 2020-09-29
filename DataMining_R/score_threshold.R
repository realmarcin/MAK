setwd("~/Documents/integr8_genom/Miner/miner_results/yeast/EXPR_round12345/")
data <- read.table("./results_yeast_cmonkey_1_expr_MSEC_KendallC_GEECE_round12345_top_plusbypass.vbl",sep="\t",header=T,comment.char="@")

full_crit <- as.numeric(as.character(data$full_crit))

sortfull <- sort(full_crit[!is.na(full_crit)])

sortfull[length(sortfull)*0.05]

max score for bottom 5%
0.9523725


sortfull[length(sortfull)*0.66]

[1] 0.9813114