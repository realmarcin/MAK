#setwd("/Users/marcin/Documents/integr8_genom/Miner/miner_results/yeast/REF_REGULON_OVERLAP/signed/")
setwd("/Users/marcin/Documents/integr8_genom/Miner/miner_results/yeast/REF_REGULON_OVERLAP/")

dataISA <- read.table("./ISA_vs_ref_notrim.txt_gene.txt",sep="\t",header=T,row.names=1)
#dataISA <- read.table("./ISA_vs_ref_notrim.txt_total.txt",sep="\t",header=T,row.names=1)
dataISAmax <- apply(dataISA, 1, max)
#dataISAmax <- apply(dataISA, 1, min)

dataCOALESCE <- read.table("./coalesce_vs_ref_notrim.txt_gene.txt",sep="\t",header=T,row.names=1)
#dataCOALESCE <- read.table("./coalesce_vs_ref_notrim.txt_exp.txt",sep="\t",header=T,row.names=1)
#dataCOALESCE <- read.table("./coalesce_vs_ref_notrim.txt_total.txt",sep="\t",header=T,row.names=1)
dataCOALESCEmax <- apply(dataCOALESCE, 1, max)
#dataCOALESCEmax <- apply(dataCOALESCE, 1, min)

#cmonkey_vs_ref_notrim.txt_gene.txt
datacmonkey <- read.table("./cmonkey_vs_ref_notrim.txt_gene.txt",sep="\t",header=T,row.names=1)
#datacmonkey <- read.table("./cmonkey_vs_ref_notrim.txt_exp.txt",sep="\t",header=T,row.names=1)
#datacmonkey <- read.table("./cmonkey_vs_ref_notrim.txt_total.txt",sep="\t",header=T,row.names=1)
datacmonkeymax <- apply(datacmonkey, 1, max)
#datacmonkeymax <- apply(datacmonkey, 1, min)

data2dhcl <- read.table("./HCLstart_vs_ref_notrim.txt_gene.txt",sep="\t",header=T,row.names=1)
#data2dhcl <- read.table("./HCLstart_vs_ref_notrim.txt_total.txt",sep="\t",header=T,row.names=1)
data2dhclmax <- apply(data2dhcl, 1, max)
#data2dhclmax <- apply(data2dhcl, 1, min)

data_expr <- read.table("./EXPR_round12345_merge_refine_vs_ref_notrim.txt_gene.txt",sep="\t",header=T,row.names=1)
#data_expr <- read.table("./MAK_expr_vs_ref_notrim.txt_exp.txt",sep="\t",header=T,row.names=1)
#data_expr <- read.table("./MAK_expr_vs_ref_notrim.txt_total.txt",sep="\t",header=T,row.names=1)
data_expr_max <- apply(data_expr, 1, max)
#data_expr_max <- apply(data_expr, 1, min)

data_expr_TF <- read.table("./EXPR_TF_round12_merge_refine_vs_ref_notrim.txt_gene.txt",sep="\t",header=T,row.names=1)
#data_expr_TF <- read.table("./MAK_exprTF_vs_ref_notrim.txt_gene.txt",sep="\t",header=T,row.names=1)
data_expr_TF_max <- apply(data_expr_TF, 1, max)
#data_expr_TF_max <- apply(data_expr_TF, 1, min)

data_expr_TF_inter_feat <- read.table("./EXPR_TF_inter_feat_round12_merge_refine_vs_ref_notrim.txt_gene.txt",sep="\t",header=T,row.names=1)
data_expr_TF_inter_feat_max <- apply(data_expr_TF_inter_feat, 1, max)
#data_expr_TF_inter_feat_max <- apply(data_expr_TF_inter_feat, 1, min)

data_all <- read.table("./EXPR_ALL_merge_refine_vs_ref_notrim.txt_gene.txt",sep="\t",header=T,row.names=1)
data_all_max <- apply(data_all, 1, max)
#data_all_max <- apply(data_all, 1, min)


rangeISA <- range(dataISAmax)
rangeCOALESCE <- range(dataCOALESCEmax)
rangecmonkey <- range(datacmonkeymax)
range2dhcl <- range(data2dhclmax)
rangemakexpr <- range(data_expr_max)
rangemakexprTF <- range(data_expr_TF_max)
rangemakexprTFinterfeat <- range(data_expr_TF_inter_feat_max)
rangemakall <- range(data_all_max)
#rangemakall <- range(data_raw0.66_max)


#pdf("overlap_distribs_gene.pdf",width=8.5,height=11)

plot(density(data_expr_max, from=rangemakexpr[1], to=rangemakexpr[2]), col="deepskyblue",xlim=c(0,max(c(rangeISA, rangeCOALESCE, rangecmonkey, rangemakexpr))),ylim=c(0,40), main="Distributions of overlaps with reference regulon patterns", xlab="Overlap")
lines(density(datacmonkeymax, from=rangecmonkey[1], to=rangecmonkey[2]),col="orange")
lines(density(dataCOALESCEmax, from=rangeCOALESCE[1], to=rangeCOALESCE[2]), col="green")
lines(density(dataISAmax, from=rangeISA[1], to=rangeISA[2]), col="black")
lines(density(data2dhclmax, from=range2dhcl[1], to=range2dhcl[2]), col="gray")

#legend(0.15,40,c("MAK(expr.)","cMonkey","COALESCE","ISA","2D-HCL"), col=c("deepskyblue","orange","green","black","gray"),lty=c(1,1,1,1,1))

lines(density(data_expr_TF_max, from=rangemakexprTF[1], to=rangemakexprTF[2]),col="deepskyblue",lty=2)
lines(density(data_expr_TF_inter_feat_max, from=rangeISA[1], to=rangeISA[2]),col="deepskyblue",lty=4)
lines(density(data_all_max, from=rangemakall[1], to=rangemakall[2]),col="deepskyblue",lty=3)
#lines(density(data_raw0.66_max, from=rangeISA[1], to=rangeISA[2]),col="deepskyblue",lty=5,lwd=3)

legend(0.12,40,c("MAK(expr.)","cMonkey","COALESCE","ISA","2D-HCL","MAK(expr.+TF)","MAK(expr.+TF+PPI+feat.)","MAK(All)"), col=c("deepskyblue","orange","green","black","gray","deepskyblue","deepskyblue","deepskyblue"),lty=c(1,1,1,1,1,2,4,3))

###for pvals

dataISA <- read.table("./ISA_vs_ref_notrim.txt_geneenrich_pval.txt",sep="\t",header=T,row.names=1)
dataISAmax <- apply(dataISA, 1, min)

dataCOALESCE <- read.table("./coalesce_vs_ref_notrim.txt_geneenrich_pval.txt",sep="\t",header=T,row.names=1)
dataCOALESCEmax <- apply(dataCOALESCE, 1, min)

#cmonkey_vs_ref_notrim.txt_geneenrich_pval.txt
datacmonkey <- read.table("./cmonkey_vs_ref_notrim.txt_geneenrich_pval.txt",sep="\t",header=T,row.names=1)
datacmonkeymax <- apply(datacmonkey, 1, min)

data2dhcl <- read.table("./HCL_vs_ref_notrim.txt_geneenrich_pval.txt",sep="\t",header=T,row.names=1)
data2dhclmax <- apply(data2dhcl, 1, min)

data_expr <- read.table("./MAK_expr_vs_ref_notrim.txt_geneenrich_pval.txt",sep="\t",header=T,row.names=1)
data_expr_max <- apply(data_expr, 1, min)

data_expr_TF <- read.table("./MAK_exprTF_vs_ref_notrim.txt_geneenrich_pval.txt",sep="\t",header=T,row.names=1)
data_expr_TF_max <- apply(data_expr_TF, 1, min)

data_expr_TF_inter_feat <- read.table("./MAK_exprTFinterfeat_vs_ref_notrim.txt_geneenrich_pval.txt",sep="\t",header=T,row.names=1)
data_expr_TF_inter_feat_max <- apply(data_expr_TF_inter_feat, 1, min)

data_all <- read.table("./MAK_ALL_vs_ref_notrim.txt_geneenrich.txt",sep="\t",header=T,row.names=1)
data_all_max <- apply(data_all, 1, min)

data_raw0.66 <- read.table("./MAK_exprraw0.66score_vs_ref_notrim.txt_geneenrich.txt",sep="\t",header=T,row.names=1)
data_raw0.66_max <- apply(data_raw0.66, 1, min)


rangeISA <- range(-log(dataISAmax, 10))
rangeCOALESCE <- range(-log(dataCOALESCEmax, 10))
rangecmonkey <- range(-log(datacmonkeymax, 10))
range2dhcl <- range(-log(data2dhclmax, 10))
rangemakexpr <- range(-log(data_expr_max, 10))
rangemakexprTF <- range(-log(data_expr_TF_max, 10))
rangemakexprTFinterfeat <- range(-log(data_expr_TF_inter_feat_max, 10))
rangemakall <- range(-log(data_all_max, 10))
rangemakall <- range(-log(data_raw0.66_max, 10))

plot(density(-log(data_expr_max, 10)), col="dodgerblue3",xlim=c(0,48),ylim=c(0,1.1),main="Distributions of -log(p-value) for overlap with reference regulon patterns", xlab="-log(p-value)")
lines(density(-log(datacmonkeymax, 10)),col="orange")
lines(density(-log(dataCOALESCEmax, 10)), col="green")
lines(density(-log(dataISAmax, 10)), col="black")
lines(density(-log(data2dhclmax, 10)), col="gray")

legend(10,1.1,c("MAK(expr.)","cMonkey","COALESCE","ISA","2D-HCL"), col=c("deepskyblue","orange","green","black","gray"),lty=c(1,1,1,1,1))

lines(density(-log(data_expr_TF_max, 10)), col="dodgerblue3",lty=2)
lines(density(-log(data_expr_TF_inter_feat_max, 10)), col="dodgerblue3",lty=4)
lines(density(-log(data_all_max, 10)), col="dodgerblue3",lty=3)
lines(density(-log(data_raw0.66_max, 10)), col="dodgerblue3",lty=5,lwd=3)



###for trim1



dataISA <- read.table("./ISA_vs_ref_notrim.txt_gene.txt",sep="\t",header=T,row.names=1)
#dataISA <- read.table("./ISA_vs_ref_notrim.txt_total.txt",sep="\t",header=T,row.names=1)
dataISAmax <- apply(dataISA, 1, max)
#dataISAmax <- apply(dataISA, 1, min)

dataCOALESCE <- read.table("./coalesce_vs_ref_notrim.txt_gene.txt",sep="\t",header=T,row.names=1)
#dataCOALESCE <- read.table("./coalesce_vs_ref_notrim.txt_exp.txt",sep="\t",header=T,row.names=1)
#dataCOALESCE <- read.table("./coalesce_vs_ref_notrim.txt_total.txt",sep="\t",header=T,row.names=1)
dataCOALESCEmax <- apply(dataCOALESCE, 1, max)
#dataCOALESCEmax <- apply(dataCOALESCE, 1, min)

#cmonkey_vs_ref_notrim.txt_gene.txt
datacmonkey <- read.table("./cmonkey_vs_ref_notrim.txt_gene.txt",sep="\t",header=T,row.names=1)
#datacmonkey <- read.table("./cmonkey_vs_ref_notrim.txt_exp.txt",sep="\t",header=T,row.names=1)
#datacmonkey <- read.table("./cmonkey_vs_ref_notrim.txt_total.txt",sep="\t",header=T,row.names=1)
datacmonkeymax <- apply(datacmonkey, 1, max)
#datacmonkeymax <- apply(datacmonkey, 1, min)

data2dhcl <- read.table("./HCLstart_vs_ref_notrim.txt_gene.txt",sep="\t",header=T,row.names=1)
#data2dhcl <- read.table("./HCLstart_vs_ref_notrim.txt_total.txt",sep="\t",header=T,row.names=1)
data2dhclmax <- apply(data2dhcl, 1, max)
#data2dhclmax <- apply(data2dhcl, 1, min)

data_expr <- read.table("./EXPR_round12345_merge_refine_vs_ref_notrim.txt_gene.txt",sep="\t",header=T,row.names=1)
#data_expr <- read.table("./MAK_expr_vs_ref_notrim.txt_exp.txt",sep="\t",header=T,row.names=1)
#data_expr <- read.table("./MAK_expr_vs_ref_notrim.txt_total.txt",sep="\t",header=T,row.names=1)
data_expr_max <- apply(data_expr, 1, max)
#data_expr_max <- apply(data_expr, 1, min)

data_expr_TF <- read.table("./EXPR_TF_round12_merge_refine_vs_ref_notrim.txt_gene.txt",sep="\t",header=T,row.names=1)
#data_expr_TF <- read.table("./MAK_exprTF_vs_ref_notrim.txt_gene.txt",sep="\t",header=T,row.names=1)
data_expr_TF_max <- apply(data_expr_TF, 1, max)
#data_expr_TF_max <- apply(data_expr_TF, 1, min)

data_expr_TF_inter_feat <- read.table("./EXPR_TF_inter_feat_round12_merge_refine_vs_ref_notrim.txt_gene.txt",sep="\t",header=T,row.names=1)
data_expr_TF_inter_feat_max <- apply(data_expr_TF_inter_feat, 1, max)
#data_expr_TF_inter_feat_max <- apply(data_expr_TF_inter_feat, 1, min)

data_all <- read.table("./EXPR_ALL_merge_refine_vs_ref_notrim.txt_gene.txt",sep="\t",header=T,row.names=1)
data_all_max <- apply(data_all, 1, max)
#data_all_max <- apply(data_all, 1, min)

data_raw0.66 <- read.table("./MAK_exprraw0.66score_vs_ref_notrim.txt_gene.txt",sep="\t",header=T,row.names=1)
data_raw0.66_max <- apply(data_raw0.66, 1, max)


rangeISA <- range(dataISAmax)
rangeCOALESCE <- range(dataCOALESCEmax)
rangecmonkey <- range(datacmonkeymax)
range2dhcl <- range(data2dhclmax)
rangemakexpr <- range(data_expr_max)
rangemakexprTF <- range(data_expr_TF_max)
rangemakexprTFinterfeat <- range(data_expr_TF_inter_feat_max)
rangemakall <- range(data_all_max)
rangemakall <- range(data_raw0.66_max)


#pdf("overlap_distribs_gene.pdf",width=8.5,height=11)

plot(density(data_expr_max, from=rangemakexpr[1], to=rangemakexpr[2]), col="deepskyblue",xlim=c(0,max(c(rangeISA, rangeCOALESCE, rangecmonkey, rangemakexpr))),ylim=c(0,40), main="Distributions of overlaps with reference regulon patterns", xlab="Overlap")
lines(density(datacmonkeymax, from=rangecmonkey[1], to=rangecmonkey[2]),col="orange")
lines(density(dataCOALESCEmax, from=rangeCOALESCE[1], to=rangeCOALESCE[2]), col="green")
lines(density(dataISAmax, from=rangeISA[1], to=rangeISA[2]), col="black")
lines(density(data2dhclmax, from=rangeISA[1], to=rangeISA[2]), col="gray")

#legend(0.15,40,c("MAK(expr.)","cMonkey","COALESCE","ISA","2D-HCL"), col=c("deepskyblue","orange","green","black","gray"),lty=c(1,1,1,1,1))

lines(density(data_expr_TF_max, from=rangeISA[1], to=rangeISA[2]),col="deepskyblue",lty=2)
lines(density(data_expr_TF_inter_feat_max, from=rangeISA[1], to=rangeISA[2]),col="deepskyblue",lty=4)
lines(density(data_all_max, from=rangeISA[1], to=rangeISA[2]),col="deepskyblue",lty=3)
#lines(density(data_raw0.66_max, from=rangeISA[1], to=rangeISA[2]),col="deepskyblue",lty=5,lwd=3)

legend(0.12,40,c("MAK(expr.)","cMonkey","COALESCE","ISA","2D-HCL","MAK(expr.+TF)","MAK(expr.+TF+PPI+feat.)","MAK(All)"), col=c("deepskyblue","orange","green","black","gray","deepskyblue","deepskyblue","deepskyblue"),lty=c(1,1,1,1,1,2,4,3))

