setwd("~/Documents/integr8_genom/Miner/miner_results/yeast/EXPR_round12/")

data <- read.table("./results_yeast_cmonkey_1_expr_MSEC_KendallC_GEECE_top_round12.vbl",sep="\t",header=T,comment="!")

pdf("./total_results_yeast_cmonkey_1_expr_MSEC_KendallC_GEECE_top_round12.pdf",width=8.5, height=11)
par(mfrow=c(4,1))
plot(density(as.numeric(data$full_crit)), main="criterion")
plot(density(as.numeric(data$exp_mean)), main="expr_mean")
plot(density(as.numeric(data$percent_orig_genes)), main="orig genes")
plot(density(as.numeric(data$percent_orig_exp)), main="orig exps")
dev.off(2)


newcol <- rgb(0,0,0,25, maxColorValue =255)

pdf("./results_yeast_cmonkey_1_expr_MSEC_KendallC_GEECE_top_round12_featurebyfeature.pdf",width=8.5, height=11)
par(mfrow=c(5,5))
plot(as.numeric(data$full_crit),as.numeric(data$full_crit),col=newcol)
plot(as.numeric(data$full_crit),as.numeric(data$exp_mean),col=newcol)
plot(as.numeric(data$full_crit),as.numeric(data$percent_orig_genes),col=newcol)
plot(as.numeric(data$full_crit),as.numeric(data$block_area),col=newcol)
plot(as.numeric(data$full_crit),as.numeric(data$trajectory_position),col=newcol)

plot(as.numeric(data$exp_mean),as.numeric(data$full_crit),col=newcol)
plot(as.numeric(data$exp_mean),as.numeric(data$exp_mean),col=newcol)
plot(as.numeric(data$exp_mean),as.numeric(data$percent_orig_genes),col=newcol)
plot(as.numeric(data$exp_mean),as.numeric(data$block_area),col=newcol)
plot(as.numeric(data$exp_mean),as.numeric(data$trajectory_position),col=newcol)

plot(as.numeric(data$percent_orig_genes),as.numeric(data$full_crit),col=newcol)
plot(as.numeric(data$percent_orig_genes),as.numeric(data$exp_mean),col=newcol)
plot(as.numeric(data$percent_orig_genes),as.numeric(data$percent_orig_genes),col=newcol)
plot(as.numeric(data$percent_orig_genes),as.numeric(data$block_area),col=newcol)
plot(as.numeric(data$percent_orig_genes),as.numeric(data$trajectory_position),col=newcol)

plot(as.numeric(data$block_area),as.numeric(data$full_crit),col=newcol)
plot(as.numeric(data$block_area),as.numeric(data$exp_mean),col=newcol)
plot(as.numeric(data$block_area),as.numeric(data$percent_orig_genes),col=newcol)
plot(as.numeric(data$block_area),as.numeric(data$block_area),col=newcol)
plot(as.numeric(data$block_area),as.numeric(data$trajectory_position),col=newcol)

plot(as.numeric(data$trajectory_position),as.numeric(data$full_crit),col=newcol)
plot(as.numeric(data$trajectory_position),as.numeric(data$exp_mean),col=newcol)
plot(as.numeric(data$trajectory_position),as.numeric(data$percent_orig_genes),col=newcol)
plot(as.numeric(data$trajectory_position),as.numeric(data$block_area),col=newcol)
plot(as.numeric(data$trajectory_position),as.numeric(data$trajectory_position),col=newcol)

dev.off(2)