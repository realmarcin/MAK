setwd("~/Documents/integr8_genom/Miner/miner_results/yeast/MASTER/")
#data <- read.table("../results_yeast_cmonkey_1_expr_round12_merged0.5_refine_Sn_top_nosum_0.5_0.25_c_reconstructed.txt_biclustermotifs_test100.txt",sep="\t",header=F)
#data2 <- read.table("../results_yeast_cmonkey_1_expr_round12_merged0.5_refine_Sn_top_nosum_0.5_0.25_c_reconstructed.txt_cors_test100.txt",sep="\t",header=F)

data <- read.table("../EXPR_round12345_merge_refine/results_yeast_cmonkey_1_expr_MSEC_KendallC_GEECE_refine12345_top_0.25_1.0_c_liven_reconstructed.txt_r0.8_c0.7_biclustermotifs.txt",sep="\t",header=F)
data_TF <- read.table("../EXPR_TF_round12_merge_refine/results_yeast_cmonkey_TF_2to200_absSTART_001_NERSC_refine_1_top_0.25_1.0_c_liven_reconstructed.txt_r0.8_c0.7_biclustermotifs.txt",sep="\t",header=F)
#UPDATE
data_TF_PPI_ortho <- read.table("../EXPR_TF_inter_feat_round12_merge_refine/results_yeast_cmonkey_1_expr_TF_inter_feat_refine_all_NEW_top_0.25_1.0_c_liven_reconstructed.txt_r0.8_c0.7_biclustermotifs.txt",sep="\t",header=F)
#UPDATE
data_ALL <- read.table("../EXPR_ALL_merge_refine/results_yeast_cmonkey_1_expr_MSEC_KendallC_GEECE_ALL_top_NEW_relabel_0.25_1.0_c_liven_reconstructed.txt_r0.8_c0.7_biclustermotifs.txt",sep="\t",header=F)

data_cmon <- read.table("../OUT_cmonkey/cMonkey_yeast_MSEC_KendallC_GEECE_inter_feat_0.25_1.0_c_liven_reconstructed.txt_r0.8_c0.7_biclustermotifs.txt",sep="\t",header=F)
data_coal <- read.table("../OUT_COALESCE/motifs_summary.txt_MSEC_KendallC_GEECE_inter_feat_0.25_1.0_c_reconstructed.txt_r0.8_c0.7_biclustermotifs.txt",sep="\t",header=F)



#####
pdf("TF_bicluster_vs_TF_regulon_avgrankhist_0.25.pdf")
hist(data[,4])
dev.off(2)

#
pdf("TF_bicluster_vs_TF_regulon_cor_0.25.pdf")
plot(density(data[,8], from=-1, to=1), main="Distribution of correlations for TF-MAK vs TF-regulon vs TF-cMonkey",col="blue", xlab="Pearson correlation", ylim=c(0,1.4))
lines(density(data[abs(data[,4]) > 0.8,8], from=-1, to=1), col="green")
lines(density(data[,9], from=-1, to=1), col="gray",lty=3,lwd=2)
lines(density(data[abs(data[,4]) > 0.8,9], from=-1, to=1), col="black",lty=3,lwd=2)
lines(density(data[,11], from=-1, to=1), col="blue",lty=2)
lines(density(data[abs(data[,4]) > 0.8,11], from=-1, to=1), col="green",lty=2)

lines(density(data_cmon[,8], from=-1, to=1), col="orange")
lines(density(data_cmon[abs(data_cmon[,4]) > 0.8,8], from=-1, to=1), col="red")
lines(density(data_cmon[,9], from=-1, to=1), col="gray",lty=3,lwd=1)
lines(density(data_cmon[abs(data_cmon[,4]) > 0.8,9], from=-1, to=1), col="black",lty=3,lwd=1)
lines(density(data_cmon[,11], from=-1, to=1), col="orange",lty=2)
lines(density(data_cmon[abs(data_cmon[,4]) > 0.8,11], from=-1, to=1), col="red",lty=2)
legend(-1.05,1.4,c("TF-MAK","TF-MAK, motif > 0.8","TF-regulon (MAK)","TF-regulon, motif > 0.8", "MAK-regulon","MAK-regulon, motif > 0.8", "TF-cMonkey", "TF-cMonkey, motif > 0.8", "TF-regulon (cMonkey)","TF-regulon, motif > 0.8 (cMonkey)","cMonkey-regulon","cMonkey-regulon, motif > 0.8"), col=c("blue","green","gray","black","blue","green","orange","red","gray","black","orange","red"),lw=c(2.5,2.5,3,3,2.5,2.5,2.5,2.5,1.5,1.5,2.5,2.5),lty=c(1,1,3,3,2,2,1,1,3,3,2,2))
dev.off(2)

#
pdf("TF_bicluster_vs_TF_regulon_cor_onlyall_0.25.pdf")
plot(density(data[,8], from=-1, to=1), main="Distribution of correlations for TF-MAK vs TF-regulon vs TF-cMonkey",col="blue", xlab="Pearson correlation", ylim=c(0,1.4))
lines(density(data[,9], from=-1, to=1), col="gray",lty=3,lwd=3)
lines(density(data[,11], from=-1, to=1), col="blue",lty=2)

lines(density(data_cmon[,8], from=-1, to=1), col="orange")
lines(density(data_cmon[,9], from=-1, to=1), col="gray",lty=3,lwd=2)
lines(density(data_cmon[,11], from=-1, to=1), col="orange",lty=2)

legend(-1.05,1.4,c("TF-MAK","TF-regulon (MAK)", "MAK-regulon", "TF-cMonkey", "TF-regulon (cMonkey)", "cMonkey-regulon"), col=c("blue","gray","blue","orange","gray","orange"),lw=c(2.5,3,2.5,2.5,1.5,2.5),lty=c(1,3,2,1,3,2))
dev.off(2)

#
pdf("TF_bicluster_vs_TF_regulon_cor_onlystrong_0.25.pdf")
plot(density(data[data[,4] > 0.8,8], from=-1, to=1), main="Distribution of correlations for TF-MAK vs TF-regulon vs TF-cMonkey, motif > 0.8",col="blue", xlab="Pearson correlation", ylim=c(0,1.4))
lines(density(data[data[,4] > 0.8,9], from=-1, to=1), col="gray",lty=3,lwd=3)
lines(density(data[data[,4] > 0.8,11], from=-1, to=1), col="blue",lty=2)

lines(density(data_cmon[data_cmon[,4] > 0.8,8], from=-1, to=1), col="orange")
lines(density(data_cmon[data_cmon[,4] > 0.8,9], from=-1, to=1), col="gray",lty=3,lwd=2)
lines(density(data_cmon[data_cmon[,4] > 0.8,11], from=-1, to=1), col="orange",lty=2)

lines(density(data_coal[data_coal[,4] > 0.8,8], from=-1, to=1), col="green")
lines(density(data_coal[data_coal[,4] > 0.8,9], from=-1, to=1), col="gray",lty=3,lwd=2)
lines(density(data_coal[data_coal[,4] > 0.8,11], from=-1, to=1), col="green",lty=2)

legend(-1.05,1.4,c("TF-MAK","TF-regulon (MAK)", "MAK-regulon", "TF-cMonkey", "TF-regulon (cMonkey)","cMonkey-regulon"), col=c("blue","gray","blue","orange","gray","orange"),lw=c(2.5,3,2.5,2.5,1.5,2.5),lty=c(1,3,2,1,3,2))
dev.off(2)

pdf("TF_bicluster_all_0.25.pdf")
plot(density(data[,8], from=-1, to=1), main="Distribution of correlations for TF-MAK vs TF-regulon vs TF-cMonkey, motif > 0.8",col="blue", xlab="Pearson correlation", ylim=c(0,1.4))
lines(density(data_cmon[,8], from=-1, to=1), col="orange")
lines(density(data_coal[,8], from=-1, to=1), col="green")
legend(-1.05,1.4,c("TF-MAK","TF-cMonkey", "TF-COALESCE"), col=c("blue","orange","green"),lw=c(2.5,2.5,2.5),lty=c(1,1,1))
dev.off(2)

pdf("TF_bicluster_onlystrong_0.25.pdf")
plot(density(data[data[,4] > 0.8,8], from=-1, to=1), main="Distribution of correlations for TF-MAK vs TF-regulon vs TF-cMonkey, motif > 0.8",col="blue", xlab="Pearson correlation", ylim=c(0,1.4))
lines(density(data_cmon[data_cmon[,4] > 0.8,8], from=-1, to=1), col="orange")
lines(density(data_coal[data_coal[,4] > 0.8,8], from=-1, to=1), col="green")
legend(-1.05,1.4,c("TF-MAK","TF-cMonkey", "TF-COALESCE"), col=c("blue","orange","green"),lw=c(2.5,2.5,2.5),lty=c(1,1,1))
dev.off(2)



pdf("TF_bicluster_all_0.25.pdf")
par(mfrow=c(3,2))

plot(density(data[,8], from=-1, to=1), main="TF-bicluster",col="blue", xlab="Pearson correlation", ylim=c(0,1.2))
lines(density(data_cmon[,8], from=-1, to=1), col="orange")
lines(density(data_coal[,8], from=-1, to=1), col="green")
legend(-1.05,1.2,c("MAK","cMonkey", "COALESCE"), col=c("blue","orange","green"),lw=c(2.5,2.5,2.5),lty=c(1,1,1))

plot(density(data[data[,4] > 0.8,8], from=-1, to=1), main="TF-bicluster, motif > 0.8",col="blue", xlab="Pearson correlation", ylim=c(0,1.6))
lines(density(data_cmon[data_cmon[,4] > 0.8,8], from=-1, to=1), col="orange")
lines(density(data_coal[data_coal[,4] > 0.8,8], from=-1, to=1), col="green")


plot(density(data[,9], from=-1, to=1), main="TF-regulon",col="blue", xlab="Pearson correlation", ylim=c(0,2))
lines(density(data_cmon[,9], from=-1, to=1), col="orange")
lines(density(data_coal[,9], from=-1, to=1), col="green")

plot(density(data[data[,4] > 0.8,9], from=-1, to=1), main="TF-regulon, motif > 0.8",col="blue", xlab="Pearson correlation", ylim=c(0,2.5))
lines(density(data_cmon[data_cmon[,4] > 0.8,9], from=-1, to=1), col="orange")
lines(density(data_coal[data_coal[,4] > 0.8,9], from=-1, to=1), col="green")


plot(density(data[,11], from=-1, to=1), main="Bicluster-regulon, motif > 0.8",col="blue", xlab="Pearson correlation", ylim=c(0,2.2))
lines(density(data_cmon[,11], from=-1, to=1), col="orange")
lines(density(data_coal[,11], from=-1, to=1), col="green")

plot(density(data[data[,4] > 0.8,11], from=-1, to=1), main="Bicluster-regulon, motif > 0.8",col="blue", xlab="Pearson correlation", ylim=c(0,3))
lines(density(data_cmon[data_cmon[,4] > 0.8,11], from=-1, to=1), col="orange")
lines(density(data_coal[data_coal[,4] > 0.8,11], from=-1, to=1), col="green")
dev.off(2)


###
col <- rgb(red=0, green=0, blue=0, alpha=70, max=255)
pdf("TF_bicluster_vs_TF_regulon_scatter_0.25.pdf")
plot(data[,8],data[,9], main="TF-bicluster vs TF-regulon", xlab="TF-bicluster", ylab="TF-regulon", col=col)
dev.off(2)

#8 bicluster - TF
#9 TF - regulon
#11 bicluster - regulon


#mean vs mak1,2,3, cmon1,2,3, mak_strong1,2,3, cmon_strong1,2,3, max vs median

length(unique(data[abs(data[,8]) > 0.5,2]))
length(unique(data[abs(data[,8] > 0.8) > 0.5,2]))

###trim to highly correlated TFs
data2 <- data[abs(data[,8]) > 0.7,]

#data2 <- data[,list(max = max(V8)),by='V2']
#
#tapply(data$V8, data$V2, max)


data_max <- aggregate(as.numeric(data$V8), list(exptCount=data$V2),max)
data_min <- aggregate(as.numeric(data$V8), list(exptCount=data$V2),min)

###code to count cases of other biclusters correlated with TF
###density plot of correlations for all bicluster mappings, give percentile of current bicluster?


###original coexpression test plot

summaries <- mat.or.vec(12,3)

summaries[1,1] <- median(abs(data[data[,4] > 0.8,8]))
summaries[1,2] <- median(abs(data[data[,4] > 0.8,9]))
summaries[1,3] <- median(abs(data[data[,4] > 0.8,11]))

summaries[2,1] <- median(abs(data_TF[data_TF[,4] > 0.8,8]))
summaries[2,2] <- median(abs(data_TF[data_TF[,4] > 0.8,9]))
summaries[2,3] <- median(abs(data_TF[data_TF[,4] > 0.8,11]))

summaries[3,1] <- median(abs(data_TF_PPI_ortho[data_TF_PPI_ortho[,4] > 0.8,8]))
summaries[3,2] <- median(abs(data_TF_PPI_ortho[data_TF_PPI_ortho[,4] > 0.8,9]))
summaries[3,3] <- median(abs(data_TF_PPI_ortho[data_TF_PPI_ortho[,4] > 0.8,11]))

summaries[4,1] <- median(abs(data_ALL[data_ALL[,4] > 0.8,8]))
summaries[4,2] <- median(abs(data_ALL[data_ALL[,4] > 0.8,9]))
summaries[4,3] <- median(abs(data_ALL[data_ALL[,4] > 0.8,11]))

summaries[5,1] <- median(abs(data_cmon[data_cmon[,4] > 0.8,8]))
summaries[5,2] <- median(abs(data_cmon[data_cmon[,4] > 0.8,9]))
summaries[5,3] <- median(abs(data_cmon[data_cmon[,4] > 0.8,11]))

summaries[6,1] <- median(abs(data_coal[data_coal[,4] > 0.8,8]))
summaries[6,2] <- median(abs(data_coal[data_coal[,4] > 0.8,9]))
summaries[6,3] <- median(abs(data_coal[data_coal[,4] > 0.8,11]))



summaries[7,1] <- median(abs(data[data[,4] > 0.99,8]))
summaries[7,2] <- median(abs(data[data[,4] > 0.99,9]))
summaries[7,3] <- median(abs(data[data[,4] > 0.99,11]))

summaries[8,1] <- median(abs(data_TF[data_TF[,4] > 0.99,8]))
summaries[8,2] <- median(abs(data_TF[data_TF[,4] > 0.99,9]))
summaries[8,3] <- median(abs(data_TF[data_TF[,4] > 0.99,11]))

summaries[9,1] <- median(abs(data_TF_PPI_ortho[data_TF_PPI_ortho[,4] > 0.99,8]))
summaries[9,2] <- median(abs(data_TF_PPI_ortho[data_TF_PPI_ortho[,4] > 0.99,9]))
summaries[9,3] <- median(abs(data_TF_PPI_ortho[data_TF_PPI_ortho[,4] > 0.99,11]))

summaries[10,1] <- median(abs(data_ALL[data_ALL[,4] > 0.99,8]))
summaries[10,2] <- median(abs(data_ALL[data_ALL[,4] > 0.99,9]))
summaries[10,3] <- median(abs(data_ALL[data_ALL[,4] > 0.99,11]))

summaries[11,1] <- median(abs(data_cmon[data_cmon[,4] > 0.99,8]))
summaries[11,2] <- median(abs(data_cmon[data_cmon[,4] > 0.99,9]))
summaries[11,3] <- median(abs(data_cmon[data_cmon[,4] > 0.99,11]))

summaries[12,1] <- median(abs(data_coal[data_coal[,4] > 0.99,8]))
summaries[12,2] <- median(abs(data_coal[data_coal[,4] > 0.99,9]))
summaries[12,3] <- median(abs(data_coal[data_coal[,4] > 0.99,11]))


#cmonkey & coalesce
t10 <- wilcox.test(abs(data_cmon[data_cmon[,4] > 0.99,8]), abs(data_cmon[,8]),alternative="greater")
t11 <- wilcox.test(abs(data_cmon[data_cmon[,4] > 0.99,9]), abs(data_cmon[,9]),alternative="greater")
t12 <- wilcox.test(abs(data_cmon[data_cmon[,4] > 0.99,11]), abs(data_cmon[,11]),alternative="greater")

t13 <- wilcox.test(abs(data_cmon[data_cmon[,4] > 0.99,8]), abs(data_cmon[,8]),alternative="less")
t14 <- wilcox.test(abs(data_cmon[data_cmon[,4] > 0.99,9]), abs(data_cmon[,9]),alternative="less")
t15 <- wilcox.test(abs(data_cmon[data_cmon[,4] > 0.99,11]), abs(data_cmon[,11]),alternative="less")

t10a <- wilcox.test(abs(data_coal[data_coal[,4] > 0.99,8]), abs(data_coal[,8]),alternative="greater")
t11a <- wilcox.test(abs(data_coal[data_coal[,4] > 0.99,9]), abs(data_coal[,9]),alternative="greater")
t12a <- wilcox.test(abs(data_coal[data_coal[,4] > 0.99,11]), abs(data_coal[,11]),alternative="greater")

t13a <- wilcox.test(abs(data_coal[data_coal[,4] > 0.99,8]), abs(data_coal[,8]),alternative="less")
t14a <- wilcox.test(abs(data_coal[data_coal[,4] > 0.99,9]), abs(data_coal[,9]),alternative="less")
t15a <- wilcox.test(abs(data_coal[data_coal[,4] > 0.99,11]), abs(data_coal[,11]),alternative="less")




t1 <- wilcox.test(abs(data[data[,4] > 0.8,8]), abs(data_cmon[data_cmon[,4] > 0.8,8]),alternative="greater")
t2 <- wilcox.test(abs(data[data[,4] > 0.8,9]), abs(data_cmon[data_cmon[,4] > 0.8,9]),alternative="greater")
t3 <- wilcox.test(abs(data[data[,4] > 0.8,11]), abs(data_cmon[data_cmon[,4] > 0.8,11]),alternative="greater")

t4 <- wilcox.test(abs(data[data[,4] > 0.99,8]), abs(data_cmon[data_cmon[,4] > 0.99,8]),alternative="greater")
t5 <- wilcox.test(abs(data[data[,4] > 0.99,9]), abs(data_cmon[data_cmon[,4] > 0.99,9]),alternative="greater")
t6 <- wilcox.test(abs(data[data[,4] > 0.99,11]), abs(data_cmon[data_cmon[,4] > 0.99,11]),alternative="greater")

t7 <- wilcox.test(abs(data[data[,4] > 0.99,8]), abs(data[,8]),alternative="greater")
t8 <- wilcox.test(abs(data[data[,4] > 0.99,9]), abs(data[,9]),alternative="greater")
t9 <- wilcox.test(abs(data[data[,4] > 0.99,11]), abs(data[,11]),alternative="greater")


t1a <- wilcox.test(abs(data[data[,4] > 0.8,8]), abs(data_coal[data_coal[,4] > 0.8,8]),alternative="greater")
t2a <- wilcox.test(abs(data[data[,4] > 0.8,9]), abs(data_coal[data_coal[,4] > 0.8,9]),alternative="greater")
t3a <- wilcox.test(abs(data[data[,4] > 0.8,11]), abs(data_coal[data_coal[,4] > 0.8,11]),alternative="greater")


t4a <- wilcox.test(abs(data[data[,4] > 0.99,8]), abs(data_coal[data_coal[,4] > 0.99,8]),alternative="greater")
t5a <- wilcox.test(abs(data[data[,4] > 0.99,9]), abs(data_coal[data_coal[,4] > 0.99,9]),alternative="greater")
t6a <- wilcox.test(abs(data[data[,4] > 0.99,11]), abs(data_coal[data_coal[,4] > 0.99,11]),alternative="greater")

t7a <- wilcox.test(abs(data[data[,4] > 0.99,8]), abs(data[,8]),alternative="greater")
t8a <- wilcox.test(abs(data[data[,4] > 0.99,9]), abs(data[,9]),alternative="greater")
t9a <- wilcox.test(abs(data[data[,4] > 0.99,11]), abs(data[,11]),alternative="greater")



t1_TF <- wilcox.test(abs(data_TF[data_TF[,4] > 0.8,8]), abs(data_cmon[data_cmon[,4] > 0.8,8]),alternative="greater")
t2_TF <- wilcox.test(abs(data_TF[data_TF[,4] > 0.8,9]), abs(data_cmon[data_cmon[,4] > 0.8,9]),alternative="greater")
t3_TF <- wilcox.test(abs(data_TF[data_TF[,4] > 0.8,11]), abs(data_cmon[data_cmon[,4] > 0.8,11]),alternative="greater")

t4_TF <- wilcox.test(abs(data_TF[data_TF[,4] > 0.99,8]), abs(data_cmon[data_cmon[,4] > 0.99,8]),alternative="greater")
t5_TF <- wilcox.test(abs(data_TF[data_TF[,4] > 0.99,9]), abs(data_cmon[data_cmon[,4] > 0.99,9]),alternative="greater")
t6_TF <- wilcox.test(abs(data_TF[data_TF[,4] > 0.99,11]), abs(data_cmon[data_cmon[,4] > 0.99,11]),alternative="greater")

t7_TF <- wilcox.test(abs(data_TF[data_TF[,4] > 0.99,8]), abs(data_TF[,8]),alternative="greater")
t8_TF <- wilcox.test(abs(data_TF[data_TF[,4] > 0.99,9]), abs(data_TF[,9]),alternative="greater")
t9_TF <- wilcox.test(abs(data_TF[data_TF[,4] > 0.99,11]), abs(data_TF[,11]),alternative="greater")


t1a_TF <- wilcox.test(abs(data_TF[data_TF[,4] > 0.8,8]), abs(data_coal[data_coal[,4] > 0.8,8]),alternative="greater")
t2a_TF <- wilcox.test(abs(data_TF[data_TF[,4] > 0.8,9]), abs(data_coal[data_coal[,4] > 0.8,9]),alternative="greater")
t3a_TF <- wilcox.test(abs(data_TF[data_TF[,4] > 0.8,11]), abs(data_coal[data_coal[,4] > 0.8,11]),alternative="greater")

t4a_TF <- wilcox.test(abs(data_TF[data_TF[,4] > 0.99,8]), abs(data_coal[data_coal[,4] > 0.99,8]),alternative="greater")
t5a_TF <- wilcox.test(abs(data_TF[data_TF[,4] > 0.99,9]), abs(data_coal[data_coal[,4] > 0.99,9]),alternative="greater")
t6a_TF <- wilcox.test(abs(data_TF[data_TF[,4] > 0.99,11]), abs(data_coal[data_coal[,4] > 0.99,11]),alternative="greater")

t7a_TF <- wilcox.test(abs(data_TF[data_TF[,4] > 0.99,8]), abs(data_TF[,8]),alternative="greater")
t8a_TF <- wilcox.test(abs(data_TF[data_TF[,4] > 0.99,9]), abs(data_TF[,9]),alternative="greater")
t9a_TF <- wilcox.test(abs(data_TF[data_TF[,4] > 0.99,11]), abs(data_TF[,11]),alternative="greater")



t1_TF_PPI_ortho <- wilcox.test(abs(data_TF_PPI_ortho[data_TF_PPI_ortho[,4] > 0.8,8]), abs(data_cmon[data_cmon[,4] > 0.8,8]),alternative="greater")
t2_TF_PPI_ortho <- wilcox.test(abs(data_TF_PPI_ortho[data_TF_PPI_ortho[,4] > 0.8,9]), abs(data_cmon[data_cmon[,4] > 0.8,9]),alternative="greater")
t3_TF_PPI_ortho <- wilcox.test(abs(data_TF_PPI_ortho[data_TF_PPI_ortho[,4] > 0.8,11]), abs(data_cmon[data_cmon[,4] > 0.8,11]),alternative="greater")

t4_TF_PPI_ortho <- wilcox.test(abs(data_TF_PPI_ortho[data_TF_PPI_ortho[,4] > 0.99,8]), abs(data_cmon[data_cmon[,4] > 0.99,8]),alternative="greater")
t5_TF_PPI_ortho <- wilcox.test(abs(data_TF_PPI_ortho[data_TF_PPI_ortho[,4] > 0.99,9]), abs(data_cmon[data_cmon[,4] > 0.99,9]),alternative="greater")
t6_TF_PPI_ortho <- wilcox.test(abs(data_TF_PPI_ortho[data_TF_PPI_ortho[,4] > 0.99,11]), abs(data_cmon[data_cmon[,4] > 0.99,11]),alternative="greater")

t7_TF_PPI_ortho <- wilcox.test(abs(data_TF_PPI_ortho[data_TF_PPI_ortho[,4] > 0.99,8]), abs(data_TF_PPI_ortho[,8]),alternative="greater")
t8_TF_PPI_ortho <- wilcox.test(abs(data_TF_PPI_ortho[data_TF_PPI_ortho[,4] > 0.99,9]), abs(data_TF_PPI_ortho[,9]),alternative="greater")
t9_TF_PPI_ortho <- wilcox.test(abs(data_TF_PPI_ortho[data_TF_PPI_ortho[,4] > 0.99,11]), abs(data_TF_PPI_ortho[,11]),alternative="greater")



t1a_TF_PPI_ortho <- wilcox.test(abs(data_TF_PPI_ortho[data_TF_PPI_ortho[,4] > 0.8,8]), abs(data_coal[data_coal[,4] > 0.8,8]),alternative="greater")
t2a_TF_PPI_ortho <- wilcox.test(abs(data_TF_PPI_ortho[data_TF_PPI_ortho[,4] > 0.8,9]), abs(data_coal[data_coal[,4] > 0.8,9]),alternative="greater")
t3a_TF_PPI_ortho <- wilcox.test(abs(data_TF_PPI_ortho[data_TF_PPI_ortho[,4] > 0.8,11]), abs(data_coal[data_coal[,4] > 0.8,11]),alternative="greater")

t4a_TF_PPI_ortho <- wilcox.test(abs(data_TF_PPI_ortho[data_TF_PPI_ortho[,4] > 0.99,8]), abs(data_coal[data_coal[,4] > 0.99,8]),alternative="greater")
t5a_TF_PPI_ortho <- wilcox.test(abs(data_TF_PPI_ortho[data_TF_PPI_ortho[,4] > 0.99,9]), abs(data_coal[data_coal[,4] > 0.99,9]),alternative="greater")
t6a_TF_PPI_ortho <- wilcox.test(abs(data_TF_PPI_ortho[data_TF_PPI_ortho[,4] > 0.99,11]), abs(data_coal[data_coal[,4] > 0.99,11]),alternative="greater")

t7a_TF_PPI_ortho <- wilcox.test(abs(data_TF_PPI_ortho[data_TF_PPI_ortho[,4] > 0.99,8]), abs(data_TF_PPI_ortho[,8]),alternative="greater")
t8a_TF_PPI_ortho <- wilcox.test(abs(data_TF_PPI_ortho[data_TF_PPI_ortho[,4] > 0.99,9]), abs(data_TF_PPI_ortho[,9]),alternative="greater")
t9a_TF_PPI_ortho <- wilcox.test(abs(data_TF_PPI_ortho[data_TF_PPI_ortho[,4] > 0.99,11]), abs(data_TF_PPI_ortho[,11]),alternative="greater")


t1_ALL <- wilcox.test(abs(data_ALL[data_ALL[,4] > 0.8,8]), abs(data_cmon[data_cmon[,4] > 0.8,8]),alternative="greater")
t2_ALL <- wilcox.test(abs(data_ALL[data_ALL[,4] > 0.8,9]), abs(data_cmon[data_cmon[,4] > 0.8,9]),alternative="greater")
t3_ALL <- wilcox.test(abs(data_ALL[data_ALL[,4] > 0.8,11]), abs(data_cmon[data_cmon[,4] > 0.8,11]),alternative="greater")

t4_ALL <- wilcox.test(abs(data_ALL[data_ALL[,4] > 0.99,8]), abs(data_cmon[data_cmon[,4] > 0.99,8]),alternative="greater")
t5_ALL <- wilcox.test(abs(data_ALL[data_ALL[,4] > 0.99,9]), abs(data_cmon[data_cmon[,4] > 0.99,9]),alternative="greater")
t6_ALL <- wilcox.test(abs(data_ALL[data_ALL[,4] > 0.99,11]), abs(data_cmon[data_cmon[,4] > 0.99,11]),alternative="greater")

t7_ALL <- wilcox.test(abs(data_ALL[data_ALL[,4] > 0.99,8]), abs(data_ALL[,8]),alternative="greater")
t8_ALL <- wilcox.test(abs(data_ALL[data_ALL[,4] > 0.99,9]), abs(data_ALL[,9]),alternative="greater")
t9_ALL <- wilcox.test(abs(data_ALL[data_ALL[,4] > 0.99,11]), abs(data_ALL[,11]),alternative="greater")


t1a_ALL <- wilcox.test(abs(data_ALL[data_ALL[,4] > 0.8,8]), abs(data_coal[data_coal[,4] > 0.8,8]),alternative="greater")
t2a_ALL <- wilcox.test(abs(data_ALL[data_ALL[,4] > 0.8,9]), abs(data_coal[data_coal[,4] > 0.8,9]),alternative="greater")
t3a_ALL <- wilcox.test(abs(data_ALL[data_ALL[,4] > 0.8,11]), abs(data_coal[data_coal[,4] > 0.8,11]),alternative="greater")

t4a_ALL <- wilcox.test(abs(data_ALL[data_ALL[,4] > 0.99,8]), abs(data_coal[data_coal[,4] > 0.99,8]),alternative="greater")
t5a_ALL <- wilcox.test(abs(data_ALL[data_ALL[,4] > 0.99,9]), abs(data_coal[data_coal[,4] > 0.99,9]),alternative="greater")
t6a_ALL <- wilcox.test(abs(data_ALL[data_ALL[,4] > 0.99,11]), abs(data_coal[data_coal[,4] > 0.99,11]),alternative="greater")

t7a_ALL <- wilcox.test(abs(data_ALL[data_ALL[,4] > 0.99,8]), abs(data_ALL[,8]),alternative="greater")
t8a_ALL <- wilcox.test(abs(data_ALL[data_ALL[,4] > 0.99,9]), abs(data_ALL[,9]),alternative="greater")
t9a_ALL <- wilcox.test(abs(data_ALL[data_ALL[,4] > 0.99,11]), abs(data_ALL[,11]),alternative="greater")


mat <- rbind(
c(t10$p.value, t11$p.value, t12$p.value),
c(t13$p.value, t14$p.value, t15$p.value),

c(t10a$p.value, t11a$p.value, t12a$p.value),
c(t13a$p.value, t14a$p.value, t15a$p.value),

c(t1$p.value, t2$p.value, t3$p.value),
c(t4$p.value, t5$p.value, t6$p.value),
c(t7$p.value, t8$p.value, t9$p.value),

c(t1a$p.value, t2a$p.value, t3a$p.value),
c(t4a$p.value, t5a$p.value, t6a$p.value),
c(t7a$p.value, t8a$p.value, t9a$p.value),

c(t1_TF$p.value, t2_TF$p.value, t3_TF$p.value),
c(t4_TF$p.value, t5_TF$p.value, t6_TF$p.value),
c(t7_TF$p.value, t8_TF$p.value, t9_TF$p.value),
c(t1a_TF$p.value, t2a_TF$p.value, t3a_TF$p.value),
c(t4a_TF$p.value, t5a_TF$p.value, t6a_TF$p.value),
c(t7a_TF$p.value, t8a_TF$p.value, t9a_TF$p.value),

c(t1_TF_PPI_ortho$p.value, t2_TF_PPI_ortho$p.value, t3_TF_PPI_ortho$p.value),
c(t4_TF_PPI_ortho$p.value, t5_TF_PPI_ortho$p.value, t6_TF_PPI_ortho$p.value),
c(t7_TF_PPI_ortho$p.value, t8_TF_PPI_ortho$p.value, t9_TF_PPI_ortho$p.value),
c(t1a_TF_PPI_ortho$p.value, t2a_TF_PPI_ortho$p.value, t3a_TF_PPI_ortho$p.value),
c(t4a_TF_PPI_ortho$p.value, t5a_TF_PPI_ortho$p.value, t6a_TF_PPI_ortho$p.value),
c(t7a_TF_PPI_ortho$p.value, t8a_TF_PPI_ortho$p.value, t9a_TF_PPI_ortho$p.value),

c(t1_ALL$p.value, t2_ALL$p.value, t3_ALL$p.value),
c(t4_ALL$p.value, t5_ALL$p.value, t6_ALL$p.value),
c(t7_ALL$p.value, t8_ALL$p.value, t9_ALL$p.value),
c(t1a_ALL$p.value, t2a_ALL$p.value, t3a_ALL$p.value),
c(t4a_ALL$p.value, t5a_ALL$p.value, t6a_ALL$p.value),
c(t7a_ALL$p.value, t8a_ALL$p.value, t9a_ALL$p.value)
)

colnames(mat) <- c("TF-bicluster","TF-regulon","bicluster-regulon")
row.names(mat) <- c(
"cmon_great",
"cmon_less",
"coal_great",
"coal_less",

"MAK(expr)_cmon",
"MAK(expr)_cmon__strong",
"MAK(expr)_strong_MAK(expr)",
"MAK(expr)_coal",
"MAK(expr)_coal__strong",
"MAK(expr)_strong_MAK(expr)",

"MAK(expr+TF)_cmon",
"MAK(expr+TF)_cmon__strong",
"MAK(expr+TF)_strong_MAK(expr+TF)",
"MAK(expr+TF)_coal",
"MAK(expr+TF)_coal__strong",
"MAK(expr+TF)_strong_MAK(expr+TF)",

"MAK(expr+TF+PPI+ortho)_cmon",
"MAK(expr+TF+PPI+ortho)_cmon__strong",
"MAK(expr+TF+PPI+ortho)_strong_MAK(expr+TF+PPI+ortho)",
"MAK(expr+TF+PPI+ortho)_coal",
"MAK(expr+TF+PPI+ortho)_coal__strong",
"MAK(expr+TF+PPI+ortho)_strong_MAK(expr+TF+PPI+ortho)",

"MAK(All)_cmon",
"MAK(All)_cmon__strong",
"MAK(All)_strong_MAK(All)",
"MAK(All)_coal",
"MAK(All)_coal__strong",
"MAK(All)_strong_MAK(All)"
)


                                                      TF-bicluster    TF-regulon bicluster-regulon
cmon_great                                            5.805190e-01  9.999999e-01      1.000000e+00
cmon_less                                             4.194811e-01  1.097738e-07      0.000000e+00
coal_great                                            1.000000e+00  9.991105e-01      1.000000e+00
coal_less                                             1.118853e-56  8.895377e-04     5.342906e-106
MAK(expr)_cmon                                        0.000000e+00  0.000000e+00      0.000000e+00
MAK(expr)_cmon__strong                                6.546948e-55 9.282145e-121      5.099152e-37
MAK(expr)_strong_MAK(expr)                            3.464819e-16  2.671586e-37      1.000000e+00
MAK(expr)_coal                                        0.000000e+00  0.000000e+00      0.000000e+00
MAK(expr)_coal__strong                               1.731087e-149 6.391672e-169     7.608856e-118
MAK(expr)_strong_MAK(expr)                            3.464819e-16  2.671586e-37      1.000000e+00
MAK(expr+TF)_cmon                                     0.000000e+00  0.000000e+00      0.000000e+00
MAK(expr+TF)_cmon__strong                             1.942165e-88 1.115099e-118     3.758403e-300
MAK(expr+TF)_strong_MAK(expr+TF)                      6.477992e-16  9.510526e-08      9.971637e-01
MAK(expr+TF)_coal                                     0.000000e+00  0.000000e+00      0.000000e+00
MAK(expr+TF)_coal__strong                            3.944276e-197 8.902250e-167     3.370403e-307
MAK(expr+TF)_strong_MAK(expr+TF)                      6.477992e-16  9.510526e-08      9.971637e-01
MAK(expr+TF+PPI+ortho)_cmon                          1.765664e-135 6.492823e-104      1.000000e+00
MAK(expr+TF+PPI+ortho)_cmon__strong                   1.364772e-09  2.597060e-17      1.299108e-45
MAK(expr+TF+PPI+ortho)_strong_MAK(expr+TF+PPI+ortho)  3.955337e-05  5.573899e-08      9.426349e-07
MAK(expr+TF+PPI+ortho)_coal                           0.000000e+00  0.000000e+00      0.000000e+00
MAK(expr+TF+PPI+ortho)_coal__strong                   2.220855e-39  5.233240e-34      2.817641e-75
MAK(expr+TF+PPI+ortho)_strong_MAK(expr+TF+PPI+ortho)  3.955337e-05  5.573899e-08      9.426349e-07
MAK(All)_cmon                                         0.000000e+00  0.000000e+00      0.000000e+00
MAK(All)_cmon__strong                                1.146501e-114 2.912168e-206     4.498693e-261
MAK(All)_strong_MAK(All)                              1.217168e-48  2.007181e-72      1.418958e-04
MAK(All)_coal                                         0.000000e+00  0.000000e+00      0.000000e+00
MAK(All)_coal__strong                                8.757722e-255 3.977576e-253      0.000000e+00
MAK(All)_strong_MAK(All)                              1.217168e-48  2.007181e-72      1.418958e-04



#colnames(summaries) <- c("max TF-bicluster","max TF-regulon","max bicluster-regulon","median TF-bicluster","median TF-regulon","median bicluster-regulon")
colnames(summaries) <- c("TF-bicluster","TF-regulon","bicluster-regulon")

row.names(summaries) <- c("MAK(expr.)","MAK(expr.+TF)","MAK(expr.+TF+PPI+ortho.)","MAK(All)","cMonkey", "COALESCE", "MAK(expr.) strong","MAK(expr.+TF) strong","MAK(expr.+TF+PPI+ortho.) strong","MAK(All) strong","cMonkey strong","COALESCE strong")


library(gridExtra)
pdf("coexpression_summary_high.pdf",width=11,height=8.5)
grid.table(round(summaries,2),row.just="left",col.just="left",
    gpar.coretext = gpar(col = "black", cex = 0.6),
    gpar.coltext = gpar(col = "black", cex = 0.7, fontface = "bold"),
    gpar.rowtext = gpar(col = "black", cex = 0.7, fontface = "italic"),
    gpar.corefill = gpar(fill = "white", col = "gray"),
    gpar.rowfill = gpar(fill = "white", col = "gray"),
    gpar.colfill = gpar(fill = "white", col = "gray"))
dev.off(2)

#setwd(""/auto/sahara/namib/home/marcin/integr8functgenom/Miner/miner_results/yeast/EXPR_round12_merge_refine")
data <- read.table("./results_yeast_cmonkey_1_expr_MSEC_KendallC_GEECE_refine12345_top_plusbypass_0.5_0.25_c_reconstructed.txt_r0.8_c0.7_TFedges_max.sif_0.1_TFedges_rename.txt",sep="\t",header=F)
datacmon <- read.table("../OUT_cmonkey/cMonkey_yeast_MSEC_KendallC_GEECE_inter_feat_0.5_0.25_c_reconstructed.txt_r0.8_c0.7_TFedges_max.sif_0.1_TFedges_rename.txt",sep="\t",header=F)

pdf("TFedges_all_MAK_cMon__hist.pdf")
plot(density(data[,3], from=0, to=2),main="",xlab="",ylab="")
lines(density(datacmon[,3], from=0, to=2),col="red")
#legend(1,60, c("MAK","cMonkey"),col=c("black","red"),lw=2.5)
dev.off(2)

pdf("TFedges_all_MAK_cMon__hist_0.25.pdf")
plot(density(data[,3], from=0, to=0.5),main="MAK vs cMonkey motif pair rank scores",xlab="Motif pair rank score")
lines(density(datacmon[,3], from=0, to=0.5),col="red")
legend(0.3,35, c("MAK","cMonkey"),col=c("black","red"),lw=2.5)
dev.off(2)



###p-values for MAK vs other correlation comparisons

labels <- c("data"        ,      "data_TF"   ,          "data_TF_PPI_ortho"  , "data_ALL"     ,     "data_cmon"    ,     "data_coal"   )


lists <- list()
lists[[1]] <- data
lists[[2]] <- data_TF
lists[[3]] <- data_TF_PPI_ortho
lists[[4]] <- data_ALL
lists[[5]] <- data_cmon
lists[[6]] <- data_coal


for(i in 1:4) {
for(j in 5:length(lists)) {
t1 <- wilcox.test(abs(lists[[i]][,8]), abs(lists[[j]][,8]),alternative="greater")
t2 <- wilcox.test(abs(lists[[i]][,9]), abs(lists[[j]][,9]),alternative="greater")
t3 <- wilcox.test(abs(lists[[i]][,11]), abs(lists[[j]][,11]),alternative="greater")
if(t1$p.value > 0.001) {
print(paste(labels[i], labels[j], i, j, t1$p.value, "TF-bicluster", sep="\\t"))
}
if(t2$p.value > 0.001) {
print(paste(labels[i], labels[j], i, j, t1$p.value, "TF-regulon",sep="\\t"))
}
if(t3$p.value > 0.001) {
print(paste(labels[i], labels[j], i, j, t1$p.value, "bicluster-regulon",sep="\\t"))
}

}
}



# strong
for(i in 1:4) {
for(j in 5:length(lists)) {
t1 <- wilcox.test(abs(lists[[i]][lists[[i]][,4] > 0.8,8]), abs(lists[[j]][lists[[j]][,4] > 0.8,8]),alternative="greater")
t2 <- wilcox.test(abs(lists[[i]][lists[[i]][,4] > 0.8,9]), abs(lists[[j]][lists[[j]][,4] > 0.8,9]),alternative="greater")
t3 <- wilcox.test(abs(lists[[i]][lists[[i]][,4] > 0.8,11]), abs(lists[[j]][lists[[j]][,4] > 0.8,11]),alternative="greater")

t1 <- wilcox.test(abs(data_ALL[data_ALL[,4] > 0.8,8]), abs(data_ALL[,8]),alternative="greater")
t2 <- wilcox.test(abs(data_ALL[data_ALL[,4] > 0.8,9]), abs(data_ALL[,9]),alternative="greater")
t3 <- wilcox.test(abs(data_ALL[data_ALL[,4] > 0.8,11]), abs(data_ALL[,11]),alternative="greater")

if(t1$p.value > 0.001) {
print(paste(labels[i], labels[j], i, j, t1$p.value, "strong TF-bicluster", sep="\\t"))
}
if(t2$p.value > 0.001) {
print(paste(labels[i], labels[j], i, j, t1$p.value, "strong TF-regulon",sep="\\t"))
}
if(t3$p.value > 0.001) {
print(paste(labels[i], labels[j], i, j, t1$p.value, "strong bicluster-regulon",sep="\\t"))
}

}
}