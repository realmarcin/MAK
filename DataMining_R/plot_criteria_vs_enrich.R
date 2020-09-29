
setwd("~/Documents/integr8_genom/Miner/miner_results/yeast/EXPR_round12345_merge_refine/")
data <- read.table("./results_yeast_cmonkey_1_expr_MSEC_KendallC_GEECE_refine12345_top_0.25_1.0_c_liven_reconstructed.txt",header=T,sep="\t",comment="@")

expcrit <- (data$exp_mse_crit+data$exp_kendall_crit+data$exp_reg_crit)/3.0

plot(log(data$Pathpval[data$Pathpval < 1], base = 10),(data$exp_mse_crit[data$Pathpval < 1]+data$exp_kendall_crit[data$Pathpval < 1]+data$exp_reg_crit[data$Pathpval < 1])/3.0)
plot(log(data$TIGRrolepval[data$TIGRrolepval < 1], base = 10),(data$exp_mse_crit[data$TIGRrolepval < 1]+data$exp_kendall_crit[data$TIGRrolepval < 1]+data$exp_reg_crit[data$TIGRrolepval < 1])/3.0)
plot(log(data$GOpval[data$GOpval < 1], base = 10),(data$exp_mse_crit[data$GOpval < 1]+data$exp_kendall_crit[data$GOpval < 1]+data$exp_reg_crit[data$GOpval < 1])/3.0)
plot(log(data$TFpval[data$TFpval < 1], base = 10),(data$exp_mse_crit[data$TFpval < 1]+data$exp_kendall_crit[data$TFpval < 1]+data$exp_reg_crit[data$TFpval < 1])/3.0)


length(which(data$Pathpval < 1 & expcrit < 0.98)) / length(which(data$Pathpval < 1))
length(which(data$TIGRrolepval < 1 & expcrit < 0.98)) / length(which(data$TIGRrolepval < 1))
length(which(data$GOpval < 1 & expcrit < 0.98)) / length(which(data$GOpval < 1))
length(which(data$TFpval < 1 & expcrit < 0.98)) / length(which(data$TFpval < 1))

length(which(expcrit < 0.98)) / length(expcrit)