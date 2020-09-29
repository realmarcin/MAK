#setwd("/Users/marcin/Documents/integr8_genom/Miner/miner_results/yeast/REF_REGULON_OVERLAP/signed/")
setwd("/Users/marcin/Documents/integr8_genom/Miner/miner_results/yeast/REF_REGULON_OVERLAP/")

dataISA <- read.table("./ISA_vs_ref.txt_gene.txt",sep="\t",header=T,row.names=1)
#dataISA <- read.table("./ISA_vs_ref.txt_total.txt",sep="\t",header=T,row.names=1)
#dataISA <- read.table("./ISA_vs_ref.txt_gene_pval.txt",sep="\t",header=T,row.names=1)
dataISAmax <- apply(dataISA, 1, max)
#dataISAmax <- apply(dataISA, 1, min)

dataCOALESCE <- read.table("./coalesce_vs_ref.txt_gene.txt",sep="\t",header=T,row.names=1)
#dataCOALESCE <- read.table("./coalesce_vs_ref.txt_exp.txt",sep="\t",header=T,row.names=1)
#dataCOALESCE <- read.table("./coalesce_vs_ref.txt_total.txt",sep="\t",header=T,row.names=1)
#dataCOALESCE <- read.table("./coalesce_vs_ref.txt_gene_pval.txt",sep="\t",header=T,row.names=1)
dataCOALESCEmax <- apply(dataCOALESCE, 1, max)
#dataCOALESCEmax <- apply(dataCOALESCE, 1, min)

#cmonkey_vs_ref.txt_gene.txt
datacmonkey <- read.table("./cmonkey_vs_ref.txt_gene.txt",sep="\t",header=T,row.names=1)
#datacmonkey <- read.table("./cmonkey_vs_ref.txt_exp.txt",sep="\t",header=T,row.names=1)
#datacmonkey <- read.table("./cmonkey_vs_ref.txt_total.txt",sep="\t",header=T,row.names=1)
#datacmonkey <- read.table("./cmonkey_vs_ref.txt_gene_pval.txt",sep="\t",header=T,row.names=1)
datacmonkeymax <- apply(datacmonkey, 1, max)
#datacmonkeymax <- apply(datacmonkey, 1, min)

data2dhcl <- read.table("./HCL_vs_ref.txt_gene.txt",sep="\t",header=T,row.names=1)
#data2dhcl <- read.table("./HCL_vs_ref.txt_total.txt",sep="\t",header=T,row.names=1)
#data2dhcl <- read.table("./HCL_vs_ref.txt_gene.txt",sep="\t",header=T,row.names=1)
data2dhclmax <- apply(data2dhcl, 1, max)
#data2dhclmax <- apply(data2dhcl, 1, min)

data_expr <- read.table("./MAK_expr_vs_ref.txt_gene.txt",sep="\t",header=T,row.names=1)
#data_expr <- read.table("./MAK_expr_vs_ref.txt_exp.txt",sep="\t",header=T,row.names=1)
#data_expr <- read.table("./MAK_expr_vs_ref.txt_total.txt",sep="\t",header=T,row.names=1)
#data_expr <- read.table("./MAK_expr_vs_ref.txt_gene_pval.txt",sep="\t",header=T,row.names=1)
data_expr_max <- apply(data_expr, 1, max)
#data_expr_max <- apply(data_expr, 1, min)

#data_expr_TF <- read.table("../MAK_exprTF_vs_ref.txt_gene.txt",sep="\t",header=T,row.names=1)
data_expr_TF <- read.table("./MAK_exprTF_vs_ref.txt_gene.txt",sep="\t",header=T,row.names=1)
data_expr_TF_max <- apply(data_expr_TF, 1, max)
#data_expr_TF_max <- apply(data_expr_TF, 1, min)

data_expr_TF_inter_feat <- read.table("./MAK_exprTFinterfeat_vs_ref.txt_gene.txt",sep="\t",header=T,row.names=1)
data_expr_TF_inter_feat_max <- apply(data_expr_TF_inter_feat, 1, max)
#data_expr_TF_inter_feat_max <- apply(data_expr_TF_inter_feat, 1, min)


data_all <- read.table("./MAK_ALL_vs_ref.txt_gene.txt",sep="\t",header=T,row.names=1)
data_all_max <- apply(data_all, 1, max)
#data_all_max <- apply(data_all, 1, min)

data_raw0.66 <- read.table("./MAK_exprraw0.66score_vs_ref.txt_gene.txt",sep="\t",header=T,row.names=1)
data_raw0.66_max <- apply(data_raw0.66, 1, max)

summary_cmonkey <- read.table("../OUT_cmonkey/cMonkey_yeast_MSEC_KendallC_GEECE_inter_feat_0.25_1.0_c_liven_reconstructed_pval0.001_cut_0.01_summary.txt", sep="\t",header=T)
summary_coalesce <- read.table("../OUT_COALESCE/motifs_summary.txt_MSEC_KendallC_GEECE_inter_feat_0.25_1.0_c_liven_reconstructed_pval0.001_cut_0.01_summary.txt", sep="\t",header=T)
summary_expr <- read.table("../EXPR_round12345_merge_refine/results_yeast_cmonkey_1_expr_MSEC_KendallC_GEECE_refine12345_top_0.25_1.0_c_liven_reconstructed_pval0.001_cut_0.01_summary.txt", sep="\t",header=T)

mak_expr_novel <- read.table("../OVERLAPS/MAK_expr_less0.2_overlap_v2.vbl", sep="\t",header=T)

max(data_expr_max)
dataCOALESCE_greatmak <- which(dataCOALESCEmax > 0.06617647) 
datacmonkey_greatmak <-which(datacmonkeymax > 0.06617647)

datacmonkey_expcrit <- (summary_cmonkey[,21] + summary_cmonkey[,22] + summary_cmonkey[,23])/3
dataexpr_expcrit <- (summary_expr[,21] + summary_expr[,22] + summary_expr[,23])/3
dataCOALESCE_expcrit <- (summary_coalesce[,21] + summary_coalesce[,22] + summary_coalesce[,23])/3

#exp_mean
wilcox.test(summary_cmonkey[,7][datacmonkey_greatmak],summary_expr[,7],alternative="less")
#exp crit
wilcox.test(datacmonkey_expcrit[datacmonkey_greatmak],dataexpr_expcrit,alternative="less")
#genes
wilcox.test(summary_cmonkey[,3][datacmonkey_greatmak],summary_expr[,3],alternative="less")
#TIGRrole, GO, path, TF
wilcox.test(summary_cmonkey[,10][datacmonkey_greatmak],summary_expr[,10],alternative="greater")$p.value
wilcox.test(summary_cmonkey[,12][datacmonkey_greatmak],summary_expr[,12],alternative="greater")$p.value
wilcox.test(summary_cmonkey[,14][datacmonkey_greatmak],summary_expr[,14],alternative="greater")$p.value
wilcox.test(summary_cmonkey[,16][datacmonkey_greatmak],summary_expr[,16],alternative="greater")$p.value

#exp_mean
wilcox.test(summary_coalesce[,7][dataCOALESCE_greatmak],summary_expr[,7],alternative="less")
#exp crit
wilcox.test(dataCOALESCE_expcrit[dataCOALESCE_greatmak],dataexpr_expcrit,alternative="less")
#genes
wilcox.test(summary_coalesce[,3][dataCOALESCE_greatmak],summary_expr[,3],alternative="less")
wilcox.test(summary_coalesce[,10][dataCOALESCE_greatmak],summary_expr[,10],alternative="greater")$p.value
wilcox.test(summary_coalesce[,12][dataCOALESCE_greatmak],summary_expr[,12],alternative="greater")$p.value
wilcox.test(summary_coalesce[,14][dataCOALESCE_greatmak],summary_expr[,14],alternative="greater")$p.value
wilcox.test(summary_coalesce[,16][dataCOALESCE_greatmak],summary_expr[,16],alternative="greater")$p.value


dim(summary_cmonkey)

summary_cmonkey_part <- summary_cmonkey[datacmonkey_greatmak,] 
summary_coalesce_part <- summary_coalesce[dataCOALESCE_greatmak,]
#remove data for one bicluster with 1 experiment
summary_coalesce_part <- summary_coalesce_part[-62,]
?
write.table(summary_cmonkey_part, "cmonkey_greatmak_summary.txt", sep="\t")
write.table(summary_coalesce_part, "coalesce_greatmak_summary.txt", sep="\t")

mean(summary_expr$exp_mean)
1.410224
sd(summary_expr$exp_mean)
0.2828487
mean(summary_coalesce_part$exp_mean)
0.9071095
sd(summary_coalesce_part$exp_mean)
0.2928065
mean(summary_cmonkey_part$exp_mean)
0.7017757
sd(summary_cmonkey_part$exp_mean)
0.1633229
wilcox.test(summary_expr$exp_mean,summary_coalesce_part$exp_mean)$p.value
4.477701e-22
wilcox.test(summary_expr$exp_mean,summary_cmonkey_part$exp_mean)$p.value
7.370465e-09

mean(summary_expr$genes)
149.8114
sd(summary_expr$genes)
90.76606
mean(summary_coalesce_part$genes)
101.6066
sd(summary_coalesce_part$genes)
115.9759
mean(summary_cmonkey_part$genes)
14
sd(summary_cmonkey_part$genes)
14.23823
wilcox.test(summary_expr$genes,summary_coalesce_part$genes)$p.value
9.683352e-07
wilcox.test(summary_expr$genes,summary_cmonkey_part$genes)$p.value
1.135603e-08

#exp mean
plot(density(summary_expr$exp_mean, from=0, to=3))
lines(density(summary_coalesce_part$exp_mean, from=0, to=3), col="red")

plot(density(summary_expr$exp_mean, from=0, to=3))
lines(density(summary_cmonkey_part$exp_mean, from=0, to=3), col="red")

#genes
plot(density(summary_expr$genes, from=6, to=516))
lines(density(summary_coalesce_part$genes, from=6, to=516), col="red")

plot(density(summary_expr$genes, from=6, to=516))
lines(density(summary_cmonkey_part$genes, from=6, to=516), col="red")


summary_expr_crit <- (summary_expr$exp_mse_crit + summary_expr$exp_kendall_crit + summary_expr$exp_reg_crit) /3
summary_novel_expr_crit <- (mak_expr_novel$expr_mean_crit.1 + mak_expr_novel$expr_kend_crit + mak_expr_novel$expr_reg_crit) /3

summary_cmonkey_expr_crit <- (summary_cmonkey_part$exp_mse_crit + summary_cmonkey_part$exp_kendall_crit + summary_cmonkey_part$exp_reg_crit) /3
summary_coalesce_expr_crit <- (summary_coalesce_part$exp_mse_crit + summary_coalesce_part$exp_kendall_crit + summary_coalesce_part$exp_reg_crit) /3

mean(summary_expr_crit)
0.9867472
sd(summary_expr_crit)
0.004489958
mean(summary_cmonkey_expr_crit)
0.8734065
sd(summary_cmonkey_expr_crit)
0.07498062
mean(summary_coalesce_expr_crit)
0.8138474
sd(summary_coalesce_expr_crit)
0.1616364
wilcox.test(summary_expr_crit,summary_coalesce_expr_crit)$p.value
5.421453e-35
wilcox.test(summary_expr_crit,summary_cmonkey_expr_crit)$p.value
4.271607e-09


plot(density(log(summary_expr_crit, 2), from=-1.37382614, to=-0.01125451))
lines(density(log(summary_coalesce_expr_crit, 2), from=-1.37382614, to=0), col="red")

plot(density(log(summary_expr_crit, 2), from=-1.37382614, to=-0.01125451))
lines(density(log(summary_cmonkey_expr_crit, 2), from=-1.37382614, to=1), col="red")

#novel MAK
plot(density(log(summary_novel_expr_crit, 2), from=-1.37382614, to=-0.01125451))
lines(density(log(summary_coalesce_expr_crit, 2), from=-1.37382614, to=0), col="red")

plot(density(log(summary_novel_expr_crit, 2), from=-1.37382614, to=-0.01125451))
lines(density(log(summary_cmonkey_expr_crit, 2), from=-1.37382614, to=1), col="red")


####
rangeISA <- range(dataISAmax)
rangeCOALESCE <- range(dataCOALESCEmax)
rangecmonkey <- range(datacmonkeymax)
range2dhcl <- range(data2dhclmax)
rangemakexpr <- range(data_expr_max)
rangemakexprTF <- range(data_expr_TF_max)
rangemakexprTFinterfeat <- range(data_expr_TF_inter_feat_max)
rangemakall <- range(data_all_max)
rangemakall <- range(data_raw0.66_max)

#meanvect <- c(mean(dataISAmax), mean(dataCOALESCEmax),  mean(datacmonkeymax),  mean(data2dhclmax), mean(data_expr_max), mean(data_expr_TF_max), mean(data_expr_TF_inter_feat_max), mean(data_all_max))
#sdvect <- c(sd(dataISAmax), sd(dataCOALESCEmax),  sd(datacmonkeymax),  sd(data2dhclmax), sd(data_expr_max), sd(data_expr_TF_max), sd(data_expr_TF_inter_feat_max), sd(data_all_max))

#outvect <- cbind(meanvect, sdvect)
#write.table(outvect, file="methods_vs_yeastract_geneenrich_pval.txt", sep="\t")

#pdf("overlap_distribs_gene.pdf",width=8.5,height=11)

plot(density(data_expr_max, from=rangemakexpr[1], to=rangemakexpr[2]), col="deepskyblue",xlim=c(0,max(c(rangeISA, rangeCOALESCE, rangecmonkey, rangemakexpr))),ylim=c(0,100), main="Distributions of overlaps with reference regulon patterns", xlab="Overlap")
lines(density(datacmonkeymax, from=rangecmonkey[1], to=rangecmonkey[2]),col="orange")
lines(density(dataCOALESCEmax, from=rangeCOALESCE[1], to=rangeCOALESCE[2]), col="green")
lines(density(dataISAmax, from=rangeISA[1], to=rangeISA[2]), col="black")
lines(density(data2dhclmax, from=rangeISA[1], to=rangeISA[2]), col="gray")
lines(density(data_expr_TF_max, from=rangeISA[1], to=rangeISA[2]),col="deepskyblue",lty=2)
lines(density(data_expr_TF_inter_feat_max, from=rangeISA[1], to=rangeISA[2]),col="deepskyblue",lty=4)
lines(density(data_all_max, from=rangeISA[1], to=rangeISA[2]),col="deepskyblue",lty=3)
lines(density(data_raw0.66_max, from=rangeISA[1], to=rangeISA[2]),col="deepskyblue",lty=5,lwd=3)

dev.off(2)


###for pvals
plot(density(-log(data_expr_max, 10)), col="dodgerblue3",xlim=c(0,65),ylim=c(0,0.35),main="Distributions of -log(p-value) for overlap with reference regulon patterns", xlab="-log(p-value)")
lines(density(-log(datacmonkeymax, 10)),col="orange")
lines(density(-log(dataCOALESCEmax, 10)), col="green")
lines(density(-log(dataISAmax, 10)), col="black")
lines(density(-log(data2dhclmax, 10)), col="gray")
lines(density(-log(data_expr_TF_max, 10)), col="dodgerblue3",lty=2)
lines(density(-log(data_expr_TF_inter_feat_max, 10)), col="dodgerblue3",lty=4)
lines(density(-log(data_all_max, 10)), col="dodgerblue3",lty=3)
lines(density(-log(data_raw0.66_max, 10)), col="dodgerblue3",lty=5,lwd=3)

maxvect <- c(rangeISA[2],rangeCOALESCE[2], rangecmonkey[2],range2dhcl[2], rangemakexpr[2], rangemakexprTF[2], rangemakexprTFinterfeat[2], rangemakall[2])
counter <- 1
meanvectplot <- c()
maxvectplot <- c()
for(i in 1:length(meanvect)) {
  meanvectplot[2*i-1] <- meanvect[counter]
  meanvectplot[2*i] <- meanvect[counter]
  maxvectplot[2*i-1] <- maxvect[counter]
  maxvectplot[2*i] <- maxvect[counter]
  print(i)
  counter <- counter+1
}

maxX <- max(maxvect)

yvect <- rep(c(0,20), length(maxvect))

#par(mar = rep(2, 4))

par(mfrow=c(2, 4))
grays <- rep("gray",8)
widths <- rep(0.5, 8)
plot(density(dataISAmax, from=0, to=rangeISA[2]), xlim=c(0, maxX), ylim=c(0, 100), lty=1, main="ISA")
thisgrays <- grays
thisgrays[1] <- "black"
thiswidths <- widths
thiswidths[1] <- 1
for(i in 1:length(maxvect)) {
lines(c(maxvect[i],maxvect[i]),c(0,100), col=thisgrays[i], lty=2,lwd=thiswidths[i])
}
for(i in 1:length(meanvect)) {
  lines(c(meanvect[i],meanvect[i]),c(0,100), col=thisgrays[i],lwd=thiswidths[i])
}

plot(density(dataCOALESCEmax, from=0, to=rangeCOALESCE[2]), xlim=c(0, maxX), ylim=c(0, 100), lty=1, main="COALESCE")
thisgrays <- grays
thisgrays[2] <- "black"
thiswidths <- widths
thiswidths[2] <- 1
for(i in 1:length(maxvect)) {
  lines(c(maxvect[i],maxvect[i]),c(0,100), col=thisgrays[i], lty=2,lwd=thiswidths[i])
}
for(i in 1:length(meanvect)) {
  lines(c(meanvect[i],meanvect[i]),c(0,100), col=thisgrays[i],lwd=thiswidths[i])
}

plot(density(datacmonkeymax, from=0, to=rangecmonkey[2]), xlim=c(0, maxX), ylim=c(0, 100), lty=1, main="cMonkey")
thisgrays <- grays
thisgrays[3] <- "black"
thiswidths <- widths
thiswidths[3] <- 1
for(i in 1:length(maxvect)) {
  lines(c(maxvect[i],maxvect[i]),c(0,100), col=thisgrays[i], lty=2,lwd=thiswidths[i])
}
for(i in 1:length(meanvect)) {
  lines(c(meanvect[i],meanvect[i]),c(0,100), col=thisgrays[i],lwd=thiswidths[i])
}

plot(density(data2dhclmax, from=0, to=range2dhcl[2]), xlim=c(0, maxX), ylim=c(0, 100), lty=1, main="2D-HCL")
thisgrays <- grays
thisgrays[4] <- "black"
thiswidths <- widths
thiswidths[4] <- 1
for(i in 1:length(maxvect)) {
  lines(c(maxvect[i],maxvect[i]),c(0,100), col=thisgrays[i], lty=2,lwd=thiswidths[i])
}
for(i in 1:length(meanvect)) {
  lines(c(meanvect[i],meanvect[i]),c(0,100), col=thisgrays[i],lwd=thiswidths[i])
}

plot(density(data_expr_max, from=0, to=rangemakexpr[2]), xlim=c(0, maxX), ylim=c(0, 100), lty=1, main="MAK(expr.)")
thisgrays <- grays
thisgrays[5] <- "black"
thiswidths <- widths
thiswidths[5] <- 1
for(i in 1:length(maxvect)) {
  lines(c(maxvect[i],maxvect[i]),c(0,100), col=thisgrays[i], lty=2,lwd=thiswidths[i])
}
for(i in 1:length(meanvect)) {
  lines(c(meanvect[i],meanvect[i]),c(0,100), col=thisgrays[i],lwd=thiswidths[i])
}

plot(density(data_expr_TF_max, from=0, to=rangemakexprTF[2]), xlim=c(0, maxX), ylim=c(0, 100), lty=1, main="MAK(expr.+TF)")
thisgrays <- grays
thisgrays[6] <- "black"
thiswidths <- widths
thiswidths[6] <- 1
for(i in 1:length(maxvect)) {
  lines(c(maxvect[i],maxvect[i]),c(0,100), col=thisgrays[i], lty=2,lwd=thiswidths[i])
}
for(i in 1:length(meanvect)) {
  lines(c(meanvect[i],meanvect[i]),c(0,100), col=thisgrays[i],lwd=thiswidths[i])
}

plot(density(data_expr_TF_inter_feat_max, from=0, to=rangemakexprTFinterfeat[2]), xlim=c(0, maxX), ylim=c(0, 100), lty=1, main="MAK(expr.+TF+PPI+ortho.)")
thisgrays <- grays
thisgrays[7] <- "black"
thiswidths <- widths
thiswidths[7] <- 1
for(i in 1:length(maxvect)) {
  lines(c(maxvect[i],maxvect[i]),c(0,100), col=thisgrays[i], lty=2,lwd=thiswidths[i])
}
for(i in 1:length(meanvect)) {
  lines(c(meanvect[i],meanvect[i]),c(0,100), col=thisgrays[i],lwd=thiswidths[i])
}

plot(density(data_all_max, from=0, to=rangemakall[2]), xlim=c(0, maxX), ylim=c(0, 100), lty=1, main="MAK(All)")
thisgrays <- grays
thisgrays[8] <- "black"
thiswidths <- widths
thiswidths[8] <- 1
for(i in 1:length(maxvect)) {
  lines(c(maxvect[i],maxvect[i]),c(0,100), col=thisgrays[i], lty=2,lwd=thiswidths[i])
}
for(i in 1:length(meanvect)) {
  lines(c(meanvect[i],meanvect[i]),c(0,100), col=thisgrays[i],lwd=thiswidths[i])
}


#legend(0.04, 100, c("ISA","COALESCE","cMonkey","2D-HCL", "MAK(expr.)"), lty=c(2,3,4,5,1))


####

dataall <- read.table("./methods_vs_yeastract.txt",sep="\t",header=T)
datatrim1 <- read.table("./methods_vs_yeastract_trim1.txt",sep="\t",header=T)
datatrim2 <- read.table("./methods_vs_yeastract_trim2.txt",sep="\t",header=T)
datatrim3 <- read.table("./methods_vs_yeastract_trim3.txt",sep="\t",header=T)
datatrim4 <- read.table("./methods_vs_yeastract_trim4.txt",sep="\t",header=T)

databind <- cbind(dataall, datatrim1, datatrim2, datatrim3, datatrim4)
dim <- dim(databind)

plot(databind[,which(seq(1:dim[2]) %% 2 == 1)], xlim=c(0, 0.07), ylim=c(0, 0.07))
meandata <- databind[,which(seq(1:dim[2]) %% 2 == 1)]
meandata <- meandata[-c(4,6,7,8),]
row.names(meandata) <- c("ISA","COALESCE","cMonkey","MAK(expr)")
colnames(meandata) <- c("all","1 TF","2 TFs","3 TFs","4 TFs")
plot(meandata, xlim=c(0, 0.07), ylim=c(0, 0.07))



###
dataall <- read.table("./regulon_clusters_yeastract_split_MSEC_KendallC_GEECE.txt",sep="\t",header=T, comment="@")
datatrim1 <- read.table("./regulon_clusters_yeastract_split_trim_MSEC_KendallC_GEECE_inter_feat.txt",sep="\t",header=T, comment="@")
datatrim2 <- read.table("./regulon_clusters_yeastract_split_trim2_MSEC_KendallC_GEECE_inter_feat.txt",sep="\t",header=T, comment="@")
datatrim3 <- read.table("./regulon_clusters_yeastract_split_trim3_MSEC_KendallC_GEECE_inter_feat.txt",sep="\t",header=T, comment="@")
datatrim4 <- read.table("./regulon_clusters_yeastract_split_trim4_MSEC_KendallC_GEECE_inter_feat.txt",sep="\t",header=T, comment="@")

dataallexp <- (dataall[,8] + dataall[,9] + dataall[,10])/3
datatrim1exp <- (datatrim1[,8] + datatrim1[,9] + datatrim1[,10])/3
datatrim2exp <- (datatrim2[,8] + datatrim2[,9] + datatrim2[,10])/3
datatrim3exp <- (datatrim3[,8] + datatrim3[,9] + datatrim3[,10])/3
datatrim4exp <- (datatrim4[,8] + datatrim4[,9] + datatrim4[,10])/3

plot(density(dataallexp), col="red", xlab="MAK(expr.) score", main="MAK(expr.) score distributions for reference regulon patterns")
lines(density(datatrim1exp), lty=2)
lines(density(datatrim2exp))
lines(density(datatrim3exp))
lines(density(datatrim4exp))
legend(0,6,c("All","TF=1","TF=2","TF=3","TF=4"), col=c("red","black","black","black","black"), lty=c(1,2,1,1,1))
