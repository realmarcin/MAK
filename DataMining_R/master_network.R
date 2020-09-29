setwd("~/Documents/integr8_genom/Miner/miner_results/yeast/MASTER")

data <- read.table("./e1234_et123_ei123.vbl",sep="\t",header=T)

pdf("mean_hist.pdf",height=11, width = 8.5)
hist(data$exp_mean)
dev.off(2)


data_2 <- data[data$exp_mean > 2,-1]
write.table(data_2, "e1234_et123_ei123_mean2.0.vbl",sep="\t")
data_1 <- data[data$exp_mean > 1,-1]
write.table(data_1, "e1234_et123_ei123_mean1.0.vbl",sep="\t")
data_0.5 <- data[data$exp_mean > 0.5,-1]
write.table(data_0.5, "e1234_et123_ei123_mean0.5.vbl",sep="\t")