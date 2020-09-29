setwd("/Users/marcin/Documents/integr8_genom/Miner/miner_results/yeast/REF_REGULON_OVERLAP/rand/")


for(i in 0:1000) {

dataISA <- read.table("../OUT_ISA/random/ISA_vs_ref.txt_gene.txt",sep="\t",header=T,row.names=1)
dataISAmax <- apply(dataISA, 1, max)

dataCOALESCE <- read.table("../OUT_COALESCE/random/coalesce_vs_ref.txt_gene.txt",sep="\t",header=T,row.names=1)
dataCOALESCEmax <- apply(dataCOALESCE, 1, max)

datacmonkey <- read.table("../OUT_cmonkey/random/cmonkey_vs_ref.txt_gene.txt",sep="\t",header=T,row.names=1)
datacmonkeymax <- apply(datacmonkey, 1, max)

data_expr <- read.table("../EXPR_round12345_merge_refine/random/MAK_expr_vs_ref.txt_gene.txt",sep="\t",header=T,row.names=1)
data_expr_max <- apply(data_expr, 1, max)



summary_cmonkey <- read.table("../../OUT_cmonkey/cMonkey_yeast_MSEC_KendallC_GEECE_inter_feat_0.25_1.0_c_liven_reconstructed_pval0.001_cut_0.01_summary.txt", sep="\t",header=T)
summary_coalesce <- read.table("../../OUT_COALESCE/motifs_summary.txt_MSEC_KendallC_GEECE_inter_feat_0.25_1.0_c_liven_reconstructed_pval0.001_cut_0.01_summary.txt", sep="\t",header=T)
summary_expr <- read.table("../../EXPR_round12345_merge_refine/results_yeast_cmonkey_1_expr_MSEC_KendallC_GEECE_refine12345_top_0.25_1.0_c_liven_reconstructed_pval0.001_cut_0.01_summary.txt", sep="\t",header=T)

mak_expr_novel <- read.table("../../OVERLAPS/MAK_expr_less0.2_overlap_v2.vbl", sep="\t",header=T)

max(data_expr_max)
dataCOALESCE_greatmak <- which(dataCOALESCEmax > 0.06617647) 
datacmonkey_greatmak <-which(datacmonkeymax > 0.06617647)

datacmonkey_expcrit <- (summary_cmonkey[,21] + summary_cmonkey[,22] + summary_cmonkey[,23])/3
dataexpr_expcrit <- (summary_expr[,21] + summary_expr[,22] + summary_expr[,23])/3
dataCOALESCE_expcrit <- (summary_coalesce[,21] + summary_coalesce[,22] + summary_coalesce[,23])/3


####
rangeISA <- range(dataISAmax)
rangeCOALESCE <- range(dataCOALESCEmax)
rangecmonkey <- range(datacmonkeymax)
rangemakexpr <- range(data_expr_max)

meanvect <- c(mean(dataISAmax), mean(dataCOALESCEmax),  mean(datacmonkeymax),  mean(data2dhclmax), mean(data_expr_max), mean(data_expr_TF_max), mean(data_expr_TF_inter_feat_max), mean(data_all_max))
sdvect <- c(sd(dataISAmax), sd(dataCOALESCEmax),  sd(datacmonkeymax),  sd(data2dhclmax), sd(data_expr_max), sd(data_expr_TF_max), sd(data_expr_TF_inter_feat_max), sd(data_all_max))

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






}