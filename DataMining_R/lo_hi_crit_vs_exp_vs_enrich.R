
setwd("~/Documents/integr8_genom/Miner/miner_results/yeast/EXPR_round12345/")
data <- read.table("./results_yeast_cmonkey_1_expr_MSEC_KendallC_GEECE_round12345_top_plusbypass.vbl",sep="\t",header=T,comment.char="@")

badstartindex <- unique(c(which(as.vector(data$percent_orig_genes) < 0.2), which(as.vector(data$percent_orig_exp) < 0.2)))
plotdata <- as.numeric(as.character(data$full_crit[c(unique(badstartindex, which(as.vector(data$exp_mean) < 1.0)))]))

plotdatafinal <- as.numeric(as.character(data$full_crit[c(which(as.vector(data$percent_orig_genes) >= 0.2 & as.vector(data$percent_orig_exp) >= 0.2 & as.vector(data$exp_mean) >= 1.0))]))

full_crit <- as.numeric(as.character(data$full_crit))
xrange <- range(full_crit[!is.na(full_crit)])
exp_mean <- as.numeric(as.character(data$exp_mean))


plot(density(full_crit[!is.na(full_crit)]), col="red")
lines(density(full_crit[badstartindex]), col="red", lty=2)
lines(density(full_crit[which(as.vector(data$exp_mean)< 1.0 ) ]), col="red", lty=3)



setwd("~/Documents/integr8_genom/Miner/miner_results/yeast/EXPR_TF/")
data <- read.table("./results_yeast_cmonkey_1_expr_TF_MSEC_KendallC_GEECE_round12_top.vbl",sep="\t",header=T,comment.char="@")

badstartindex <- unique(c(which(as.vector(data$percent_orig_genes) < 0.2), which(as.vector(data$percent_orig_exp) < 0.2)))
plotdata <- as.numeric(as.character(data$full_crit[c(unique(badstartindex, which(as.vector(data$exp_mean) < 1.0)))]))

plotdatafinal <- as.numeric(as.character(data$full_crit[c(which(as.vector(data$percent_orig_genes) >= 0.2 & as.vector(data$percent_orig_exp) >= 0.2 & as.vector(data$exp_mean) >= 1.0))]))

full_crit <- as.numeric(as.character(data$full_crit))
xrange <- range(full_crit[!is.na(full_crit)])
exp_mean <- as.numeric(as.character(data$exp_mean))

plot(density(full_crit[!is.na(full_crit)]), col="red")
lines(density(full_crit[badstartindex]), col="red", lty=2)
lines(density(full_crit[which(as.vector(data$exp_mean)< 1.0 ) ]), col="red", lty=3)



setwd("~/Documents/integr8_genom/Miner/miner_results/yeast/EXPR_TF_inter_feat/")
data <- read.table("./results_yeast_cmonkey_1_expr_TF_inter_feat_round12.vbl",sep="\t",header=T,comment.char="@")

badstartindex <- unique(c(which(as.vector(data$percent_orig_genes) < 0.2), which(as.vector(data$percent_orig_exp) < 0.2)))
plotdata <- as.numeric(as.character(data$full_crit[c(unique(badstartindex, which(as.vector(data$exp_mean) < 1.0)))]))

plotdatafinal <- as.numeric(as.character(data$full_crit[c(which(as.vector(data$percent_orig_genes) >= 0.2 & as.vector(data$percent_orig_exp) >= 0.2 & as.vector(data$exp_mean) >= 1.0))]))

full_crit <- as.numeric(as.character(data$full_crit))
xrange <- range(full_crit[!is.na(full_crit)])
exp_mean <- as.numeric(as.character(data$exp_mean))


plot(density(full_crit[!is.na(full_crit)]), col="red")
lines(density(full_crit[badstartindex]), col="red", lty=2)
lines(density(full_crit[which(as.vector(data$exp_mean)< 1.0 ) ]), col="red", lty=3)



###all start and final for MAK(expr) round 1-5 + bypass
setwd("~/Documents/integr8_genom/Miner/miner_results/yeast/EXPR_round12345/INITIAL_FINAL/")
data <- read.table("./results_yeast_cmonkey_1_expr_MSEC_KendallC_GEECE_round12345_plusbypass_fl.vbl",header=T,sep="\t")

datainit <- data[which(data$move_type == "initial" ),]
datafinal <- data[which(data$move_type != "initial" ),]

startlowexpr <- which(datainit$exp_mean < 1.0)
startdrift <- unique(c(which(datafinal$percent_orig_genes < 0.2), which(datafinal$percent_orig_exps < 0.2)))
startdriftlowexpr <- unique(c(which(datafinal$exp_mean < 1.0), which(datafinal$percent_orig_genes < 0.2), which(datafinal$percent_orig_exps < 0.2)))
finalhighexpr <- which(datafinal$exp_mean >= 1.0)
finalloexpr <- which(datafinal$exp_mean < 1.0)
finalhighexprnodrift <- which(datafinal$exp_mean >= 1.0 & datafinal$percent_orig_genes >= 0.2 & datafinal$percent_orig_exp >= 0.2)
finalnodrift <- which(datafinal$percent_orig_genes >= 0.2 & datafinal$percent_orig_exp >= 0.2)
finalhighexprdrift <- unique(c(which(datafinal$exp_mean >= 1.0 & datafinal$percent_orig_genes < 0.2),which(datafinal$exp_mean >= 1.0 & datafinal$percent_orig_exp < 0.2)))

write.table(datafinal[startdrift,], file="results_yeast_cmonkey_1_expr_MSEC_KendallC_GEECE_round12345_plusbypass_drift.vbl",sep="\t")
write.table(datafinal[finalhighexprnodrift,], file="results_yeast_cmonkey_1_expr_MSEC_KendallC_GEECE_round12345_plusbypass_highexprnodrift.vbl",sep="\t")
write.table(datafinal[finalhighexprdrift,], file="results_yeast_cmonkey_1_expr_MSEC_KendallC_GEECE_round12345_plusbypass_highexprdrift.vbl",sep="\t")



redrgb <-col2rgb("red")
blackrgb <-col2rgb("black")

redalpha <- rgb(redrgb[1], redrgb[2], redrgb[3],20, maxColorValue = 255)
blackalpha <- rgb(blackrgb[1], blackrgb[2], blackrgb[3],20, maxColorValue = 255)

###full crit
colsnostart <- c(2,6,8,9,10,18,22,23)
cols <- c(2,6,8,9,10,16,17,18,19,22,23)
plot(datainit[,colsnostart], datafinal[,cols],col=blackalpha)
plot(cbind(datainit[,cols], datafinal[,cols]),col=blackalpha)

joindata <- cbind(datainit[,colsnostart], datafinal[,cols])
colnames(joindata) <- c(paste(colnames(datainit[,colsnostart]),"_initial",sep=""),colnames(datafinal[,cols]))
heatmap.2(cor(joindata), trace="none")


plot(data$exp_mean, data$full_crit,xlab="exp mean",ylab="final")

plot(data[which(data$move_type == "initial"),5], data[which(data$move_type != "initial"),5],xlab="initial",ylab="final", xlim=c(0.1,1),ylim=c(0.75,1))

plot(datainit[startlowexpr,5], datafinal[startlowexpr,5],xlab="initial",ylab="final", xlim=c(0.1,1),ylim=c(0.75,1))

plot(datainit[startdrift,5], datafinal[startdrift,5],xlab="initial",ylab="final", xlim=c(0.1,1),ylim=c(0.75,1))

plot(datainit[startdriftlowexpr,5], datafinal[startdriftlowexpr,5],xlab="initial",ylab="final", xlim=c(0.1,1),ylim=c(0.75,1))

plot(datainit[finalhighexpr,5], datafinal[finalhighexpr,5],xlab="initial",ylab="final", xlim=c(0.1,1),ylim=c(0.75,1))


redrgb <-col2rgb("red")
blackrgb <-col2rgb("black")

redalpha <- rgb(redrgb[1], redrgb[2], redrgb[3],20, maxColorValue = 255)
blackalpha <- rgb(blackrgb[1], blackrgb[2], blackrgb[3],20, maxColorValue = 255)

plot(datainit[finalhighexprnodrift,5], datafinal[finalhighexprnodrift,5],xlab="initial",ylab="final", xlim=c(0.1,1),ylim=c(0.75,1),col=blackalpha)
points(datainit[startdriftlowexpr,5], datafinal[startdriftlowexpr,5],col=redalpha)

mean(datafinal[finalhighexprnodrift,5])
mean(datafinal[startdriftlowexpr,5])
finalhighexprnodrift_pval <- t.test(datafinal[finalhighexprnodrift,5],datafinal[startdriftlowexpr,5],alternative="greater")
finalhighexprnodrift_pval$p.value
[1] 0

plot(datainit[finalhighexpr,5], datafinal[finalhighexpr,5],xlab="initial",ylab="final", xlim=c(0.1,1),ylim=c(0.75,1),col=blackalpha)
points(datainit[finalloexpr,5], datafinal[finalloexpr,5],col=redalpha)

mean(datafinal[finalhighexpr,5])
mean(datafinal[finalloexpr,5])
finalhighexpr_pval <- t.test(datafinal[finalhighexpr,5],datafinal[finalloexpr,5],alternative="greater")
finalhighexpr_pval$p.value
[1] 0


plot(datainit[finalnodrift,5], datafinal[finalnodrift,5],xlab="initial",ylab="final", xlim=c(0.1,1),ylim=c(0.75,1),col=blackalpha)
points(datainit[startdrift,5], datafinal[startdrift,5],col=redalpha)

mean(datafinal[finalnodrift,5])
mean(datafinal[startdrift,5])
t.test(datafinal[finalnodrift,5],datafinal[startdrift,5],alternative="greater")
 p-value =1



 
 
###
data$percent_orig_exp[which(as.vector(data$percent_orig_exp)< 0.2 ) ]

data$exp_mean[which(as.vector(data$percent_orig_exp)< 0.2 ) ]

plot(density(plotdata[!is.na(plotdata)]), lty=2, main="Final vs. rejected biclusters", xlab="Criterion")
lines(density(plotdatafinal[!is.na(plotdatafinal)]))

plot(full_crit, exp_mean, main="Criterion vs expression mean", xlab="Criterion", ylab="Expression mean", xlim=xrange, ylim=c(0,3))
lines(c(0,1), c(1,1), col="red")

plot(full_crit[-badstartindex], exp_mean[-badstartindex], main="Criterion vs expression mean", xlab="Criterion", ylab="Expression mean", xlim=xrange, ylim=c(0,3))
lines(c(0,1), c(1,1), col="red")

plot(full_crit[badstartindex], exp_mean[badstartindex], main="Criterion vs expression mean", xlab="Criterion", ylab="Expression mean", xlim=xrange, ylim=c(0,3))
lines(c(0,1), c(1,1), col="red")
