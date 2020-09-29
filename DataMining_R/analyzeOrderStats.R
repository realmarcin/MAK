library(ggplot2)

setwd("~/Documents/integr8_genom/Miner/miner_results/yeast/")

setwd("~/Documents/integr8_genom/Miner/miner_results/yeast/EXPR_round12/orderstats")
makdata <- read.table("results_yeast_cmonkey_1_expr_MSEC_KendallC_GEECE_top_round12_cut_expr1.0_0.2_0.5_0.25_s_reconstructed_orderstats.txt",sep="\t",header=T)
makdatavsmakdata <- read.table("results_yeast_cmonkey_1_expr_MSEC_KendallC_GEECE_top_round12_cut_expr1.0_0.2_0.5_0.25_reconstructed_orderstats_MAKset.txt",sep="\t",header=T)
setwd("~/Documents/integr8_genom/Miner/miner_results/yeast/EXPR_round12/")
makdataenrich <- read.table("results_yeast_cmonkey_1_expr_MSEC_KendallC_GEECE_top_round12_cut_expr1.0_0.2_0.5_0.25_reconstructed_pval0.001_cut_0.01_summary.txt",sep="\t",header=T)

setwd("~/Documents/integr8_genom/Miner/miner_results/yeast/EXPR_round12_merge_refine/orderstats")
makdatarefine <- read.table("results_yeast_cmonkey_1_expr_round12_merged0.5_refine_Sn_top_0.5_0.25_reconstructed_orderstats.txt",sep="\t",header=T)
makdatarefinevsmakdata <- read.table("results_yeast_cmonkey_1_expr_round12_merged0.5_refine_Sn_top_0.5_0.25_reconstructed_orderstats_MAKset.txt",sep="\t",header=T)
setwd("~/Documents/integr8_genom/Miner/miner_results/yeast/EXPR_round12_merge_refine/")
makdatarefineenrich <- read.table("results_yeast_cmonkey_1_expr_round12_merged0.5_refine_Sn_top_0.5_0.25_reconstructed_pval0.0010_cut_0.01_summary.txt",sep="\t",header=T)

setwd("~/Documents/integr8_genom/Miner/miner_results/yeast/OUT_cmonkey/orderstats")
cmondata <- read.table("cMonkey_yeast_MSEC_KendallC_GEECE_inter_feat_orderstats.txt",sep="\t",header=T)
cmondatavsmakdata <- read.table("cMonkey_yeast_MSEC_KendallC_GEECE_inter_feat_orderstats_MAKset.txt",sep="\t",header=T)
setwd("~/Documents/integr8_genom/Miner/miner_results/yeast/OUT_cmonkey/")
cmondataenrich <- read.table("cMonkey_yeast_MSEC_KendallC_GEECE_inter_feat_pval0.0010_cut_0.01_summary.txt",sep="\t",header=T)

setwd("~/Documents/integr8_genom/Miner/miner_results/yeast/OUT_ISA/orderstats")
isadata <- read.table("ISAyeastcmonkey_MSEC_KendallC_GEECE_inter_feat_orderstats.txt",sep="\t",header=T)
isadatavsmakdata <- read.table("ISAyeastcmonkey_MSEC_KendallC_GEECE_inter_feat.txt_orderstats_MAKset.txt",sep="\t",header=T)
setwd("~/Documents/integr8_genom/Miner/miner_results/yeast/OUT_ISA/")
isadataenrich <- read.table("ISAyeastcmonkey_MSEC_KendallC_GEECE_inter_feat_pval0.0010_cut_0.01_summary.txt",sep="\t",header=T)

setwd("~/Documents/integr8_genom/Miner/miner_results/yeast/OUT_HCLstart/orderstats")
hcldata <- read.table("yeast_cmonkey_STARTS_abs_MSEC_KendallC_GEECE_cut_expr1.0_0.05_orderstats.txt",sep="\t",header=T)
hcldatavsmakdata <- read.table("yeast_cmonkey_STARTS_abs_MSEC_KendallC_GEECE_cut_expr1.0_0.05_orderstats_MAKset.txt",sep="\t",header=T)
setwd("~/Documents/integr8_genom/Miner/miner_results/yeast/OUT_HCLstart/")
hcldataenrich <- read.table("yeast_cmonkey_STARTS_abs_MSEC_KendallC_GEECE_cut_expr1.0_0.05_MSEC_KendallC_GEECE_inter_feat_pval0.0010_cut_0.01_summary.txt",sep="\t",header=T)

setwd("~/Documents/integr8_genom/Miner/miner_results/yeast/")

col1 <- col2rgb("black")
col1 <- rgb(col1[1], col1[2], col1[3], 75, maxColorValue =255)
col12 <- col2rgb("gray")
col12 <- rgb(col12[1], col12[2], col12[3], 75, maxColorValue =255)
col2 <- col2rgb("blue")
col2 <- rgb(col2[1], col2[2], col2[3], 75, maxColorValue =255)
col3 <- col2rgb("orange")
col3 <- rgb(col3[1], col3[2], col3[3], 75, maxColorValue =255)
col4 <- col2rgb("magenta")
col4 <- rgb(col4[1], col4[2], col4[3], 75, maxColorValue =255)

plot(makdatarefine$expr_mean_crit.1 + makdatarefine$expr_reg_crit+makdatarefine$expr_kend_crit, makdatarefine$gedist, xlim=c(1,3),ylim=c(5,1000), col=col1,pch=16,log="y",xlab="Expression criterion",ylab="HCL order distance",cex=1.5)
points(makdata$expr_mean_crit.1 + makdata$expr_reg_crit+makdata$expr_kend_crit, makdata$gedist, col=col12,pch=16,cex=1.5)
points(cmondata$expr_mean_crit.1 + cmondata$expr_reg_crit+cmondata$expr_kend_crit, cmondata$gedist, col=col2,pch=16,cex=1.5)
points(isadata$expr_mean_crit.1 + isadata$expr_reg_crit+isadata$expr_kend_crit, isadata$gedist, col=col3,pch=16,cex=1.5)
points(hcldata$expr_mean_crit.1 + hcldata$expr_reg_crit+hcldata$expr_kend_crit, hcldata$gedist, col=col4,pch=16,cex=1.5)

plot(makdatarefine$exp_mean, makdatarefine$gedist, xlim=c(0.1,3),ylim=c(5,1000), col=col1,pch=16,log="y",xlab="Expression mean",ylab="HCL order distance",cex=1.5)
points(makdata$exp_mean, makdata$gedist, col=col12,pch=16,cex=1.5)
points(cmondata$exp_mean, cmondata$gedist, col=col2,pch=16,cex=1.5)
points(isadata$exp_mean, isadata$gedist, col=col3,pch=16,cex=1.5)
points(hcldata$exp_mean, hcldata$gedist, col=col4,pch=16,cex=1.5)

plot(makdatarefine$block_area, makdatarefine$gedist, xlim=c(100,20000),ylim=c(5,1000), col=col1,pch=16,log="xy",xlab="Block area",ylab="HCL order distance",cex=1.5)
points(makdata$block_area, makdata$gedist, col=col12,pch=16,cex=1.5)
points(cmondata$block_area, cmondata$gedist, col=col2,pch=16,cex=1.5)
points(isadata$block_area, isadata$gedist, col=col3,pch=16,cex=1.5)
points(hcldata$block_area, hcldata$gedist, col=col4,pch=16,cex=1.5)

plot(makdatarefine$num_genes, makdatarefine$gedist, xlim=c(5,350),ylim=c(5,1000), col=col1,pch=16,log="xy",xlab="Number of genes",ylab="HCL order distance",cex=1.5)
points(makdata$num_genes, makdata$gedist, col=col12,pch=16,cex=1.5)
points(cmondata$num_genes, cmondata$gedist, col=col2,pch=16,cex=1.5)
points(isadata$num_genes, isadata$gedist, col=col3,pch=16,cex=1.5)
points(hcldata$num_genes, hcldata$gedist, col=col4,pch=16,cex=1.5)

plot(makdatarefine$num_exps, makdatarefine$gedist, xlim=c(5,200),ylim=c(5,1000), col=col1,pch=16,log="xy",xlab="Number of experiments",ylab="HCL order distance",cex=1.5)
points(makdata$num_exps, makdata$gedist, col=col12,pch=16,cex=1.5)
points(cmondata$num_exps, cmondata$gedist, col=col2,pch=16,cex=1.5)
points(isadata$num_exps, isadata$gedist, col=col3,pch=16,cex=1.5)
points(hcldata$num_exps, hcldata$gedist, col=col4,pch=16,cex=1.5)

plot(makdatarefine$TF_crit, makdatarefine$gedist, xlim=c(0.05,1),ylim=c(5,1000), col=col1,pch=16,log="xy",xlab="TF criterion",ylab="HCL order distance",cex=1.5)
points(makdata$TF_crit, makdata$gedist, col=col12,pch=16,cex=1.5)
points(cmondata$TF_crit, cmondata$gedist, col=col2,pch=16,cex=1.5)
points(isadata$TF_crit, isadata$gedist, col=col3,pch=16,cex=1.5)
points(hcldata$TF_crit, hcldata$gedist, col=col4,pch=16,cex=1.5)

plot(makdatarefine$PPI_crit, makdatarefine$gedist, xlim=c(0.15,1),ylim=c(5,1000), col=col1,pch=16,log="xy",xlab="PPI criterion",ylab="HCL order distance",cex=1.5)
points(makdata$PPI_crit, makdata$gedist, col=col12,pch=16,cex=1.5)
points(cmondata$PPI_crit, cmondata$gedist, col=col2,pch=16,cex=1.5)
points(isadata$PPI_crit, isadata$gedist, col=col3,pch=16,cex=1.5)
points(hcldata$PPI_crit, hcldata$gedist, col=col4,pch=16,cex=1.5)

plot(makdatarefine$feat_crit, makdatarefine$gedist, xlim=c(0.1,1),ylim=c(5,1000), col=col1,pch=16,log="xy",xlab="Ortholog criterion",ylab="HCL order distance",cex=1.5)
points(makdata$feat_crit, makdata$gedist, col=col12,pch=16,cex=1.5)
points(cmondata$feat_crit, cmondata$gedist, col=col2,pch=16,cex=1.5)
points(isadata$feat_crit, isadata$gedist, col=col3,pch=16,cex=1.5)
points(hcldata$feat_crit, hcldata$gedist, col=col4,pch=16,cex=1.5)


plot(makdatarefineenrich$TIGRrolepval, makdatarefine$gedist, xlim=c(0.00000000000000000001,1),ylim=c(5,1000), col=col1,pch=16,log="xy",xlab="TIGR role p-value",ylab="HCL order distance",cex=1.5)
points(makdataenrich$TIGRrolepval, makdata$gedist, col=col12,pch=16,cex=1.5)
points(cmondataenrich$TIGRrolepval, cmondata$gedist, col=col2,pch=16,cex=1.5)
points(isadataenrich$TIGRrolepval, isadata$gedist, col=col3,pch=16,cex=1.5)
points(hcldataenrich$TIGRrolepval, hcldata$gedist, col=col4,pch=16,cex=1.5)

plot(makdatarefineenrich$GOpval, makdatarefine$gedist, xlim=c(0.0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000001,1),ylim=c(5,1000), col=col1,pch=16,log="xy",xlab="GO p-value",ylab="HCL order distance",cex=1.5)
points(makdataenrich$GOpval, makdata$gedist, col=col12,pch=16,cex=1.5)
points(cmondataenrich$GOpval, cmondata$gedist, col=col2,pch=16,cex=1.5)
points(isadataenrich$GOpval, isadata$gedist, col=col3,pch=16,cex=1.5)
points(hcldataenrich$GOpval, hcldata$gedist, col=col4,pch=16,cex=1.5)

plot(makdatarefineenrich$Pathpval, makdatarefine$gedist, xlim=c(0.0000000000000000000000000000000000000000000000001,1),ylim=c(5,1000), col=col1,pch=16,log="xy",xlab="KEGG p-value",ylab="HCL order distance",cex=1.5)
points(makdataenrich$Pathpval, makdata$gedist, col=col12,pch=16,cex=1.5)
points(cmondataenrich$Pathpval, cmondata$gedist, col=col2,pch=16,cex=1.5)
points(isadataenrich$Pathpval, isadata$gedist, col=col3,pch=16,cex=1.5)
points(hcldataenrich$Pathpval, hcldata$gedist, col=col4,pch=16,cex=1.5)

plot(makdatarefineenrich$TFpval, makdatarefine$gedist, xlim=c(0.00000000000000000001,1),ylim=c(5,1000), col=col1,pch=16,log="xy",xlab="TF p-value",ylab="HCL order distance",cex=1.5)
points(makdataenrich$TFpval, makdata$gedist, col=col12,pch=16,cex=1.5)
points(cmondataenrich$TFpval, cmondata$gedist, col=col2,pch=16,cex=1.5)
points(isadataenrich$TFpval, isadata$gedist, col=col3,pch=16,cex=1.5)
points(hcldataenrich$TFpval, hcldata$gedist, col=col4,pch=16,cex=1.5)


###vs MAK (merged 0.5, > 1.0 expr, > 0.2 start) reference
plot(makdatarefine$expr_mean_crit.1 + makdatarefine$expr_reg_crit+makdatarefine$expr_kend_crit, makdatarefine$gedist, xlim=c(1,3),ylim=c(5,1000), col=col1,pch=16,log="y",xlab="Expression criterion",ylab="MAK distance",cex=1.5)
points(makdatavsmakdata$expr_mean_crit.1 + makdatavsmakdata$expr_reg_crit+makdatavsmakdata$expr_kend_crit, makdatavsmakdata$gedist, col=col12,pch=16,cex=1.5)
points(cmondatavsmakdata$expr_mean_crit.1 + cmondatavsmakdata$expr_reg_crit+cmondatavsmakdata$expr_kend_crit, cmondatavsmakdata$gedist, col=col2,pch=16,cex=1.5)
points(isadatavsmakdata$expr_mean_crit.1 + isadatavsmakdata$expr_reg_crit+isadatavsmakdata$expr_kend_crit, isadatavsmakdata$gedist, col=col3,pch=16,cex=1.5)
points(hcldatavsmakdata$expr_mean_crit.1 + hcldatavsmakdata$expr_reg_crit+hcldatavsmakdata$expr_kend_crit, hcldatavsmakdata$gedist, col=col4,pch=16,cex=1.5)

plot(makdatarefine$exp_mean, makdatarefine$gedist, xlim=c(0.1,3),ylim=c(5,1000), col=col1,pch=16,log="y",xlab="Expression mean",ylab="MAK distance",cex=1.5)
points(makdatavsmakdata$exp_mean, makdatavsmakdata$gedist, col=col12,pch=16,cex=1.5)
points(cmondatavsmakdata$exp_mean, cmondatavsmakdata$gedist, col=col2,pch=16,cex=1.5)
points(isadatavsmakdata$exp_mean, isadatavsmakdata$gedist, col=col3,pch=16,cex=1.5)
points(hcldatavsmakdata$exp_mean, hcldatavsmakdata$gedist, col=col4,pch=16,cex=1.5)

plot(makdatarefine$block_area, makdatarefine$gedist, xlim=c(100,20000),ylim=c(5,1000), col=col1,pch=16,log="xy",xlab="Block area",ylab="MAK distance",cex=1.5)
points(makdatavsmakdata$block_area, makdatavsmakdata$gedist, col=col12,pch=16,cex=1.5)
points(cmondatavsmakdata$block_area, cmondatavsmakdata$gedist, col=col2,pch=16,cex=1.5)
points(isadatavsmakdata$block_area, isadatavsmakdata$gedist, col=col3,pch=16,cex=1.5)
points(hcldatavsmakdata$block_area, hcldatavsmakdata$gedist, col=col4,pch=16,cex=1.5)

plot(makdatarefine$num_genes, makdatarefine$gedist, xlim=c(5,350),ylim=c(5,1000), col=col1,pch=16,log="xy",xlab="Number of genes",ylab="MAK distance",cex=1.5)
points(makdatavsmakdata$num_genes, makdatavsmakdata$gedist, col=col12,pch=16,cex=1.5)
points(cmondatavsmakdata$num_genes, cmondatavsmakdata$gedist, col=col2,pch=16,cex=1.5)
points(isadatavsmakdata$num_genes, isadatavsmakdata$gedist, col=col3,pch=16,cex=1.5)
points(hcldatavsmakdata$num_genes, hcldatavsmakdata$gedist, col=col4,pch=16,cex=1.5)

plot(makdatarefine$num_exps, makdatarefine$gedist, xlim=c(5,200),ylim=c(5,1000), col=col1,pch=16,log="xy",xlab="Number of experiments",ylab="MAK distance",cex=1.5)
points(makdatavsmakdata$num_exps, makdatavsmakdata$gedist, col=col12,pch=16,cex=1.5)
points(cmondatavsmakdata$num_exps, cmondatavsmakdata$gedist, col=col2,pch=16,cex=1.5)
points(isadatavsmakdata$num_exps, isadatavsmakdata$gedist, col=col3,pch=16,cex=1.5)
points(hcldatavsmakdata$num_exps, hcldatavsmakdata$gedist, col=col4,pch=16,cex=1.5)

plot(makdatarefine$TF_crit, makdatarefine$gedist, xlim=c(0.05,1),ylim=c(5,1000), col=col1,pch=16,log="xy",xlab="TF criterion",ylab="MAK distance",cex=1.5)
points(makdatavsmakdata$TF_crit, makdatavsmakdata$gedist, col=col12,pch=16,cex=1.5)
points(cmondatavsmakdata$TF_crit, cmondatavsmakdata$gedist, col=col2,pch=16,cex=1.5)
points(isadatavsmakdata$TF_crit, isadatavsmakdata$gedist, col=col3,pch=16,cex=1.5)
points(hcldatavsmakdata$TF_crit, hcldatavsmakdata$gedist, col=col4,pch=16,cex=1.5)

plot(makdatarefine$PPI_crit, makdatarefine$gedist, xlim=c(0.15,1),ylim=c(5,1000), col=col1,pch=16,log="xy",xlab="PPI criterion",ylab="MAK distance",cex=1.5)
points(makdatavsmakdata$PPI_crit, makdatavsmakdata$gedist, col=col12,pch=16,cex=1.5)
points(cmondatavsmakdata$PPI_crit, cmondatavsmakdata$gedist, col=col2,pch=16,cex=1.5)
points(isadatavsmakdata$PPI_crit, isadatavsmakdata$gedist, col=col3,pch=16,cex=1.5)
points(hcldatavsmakdata$PPI_crit, hcldatavsmakdata$gedist, col=col4,pch=16,cex=1.5)

plot(makdatarefine$feat_crit, makdatarefine$gedist, xlim=c(0.1,1),ylim=c(5,1000), col=col1,pch=16,log="xy",xlab="Ortholog criterion",ylab="MAK distance",cex=1.5)
points(makdatavsmakdata$feat_crit, makdatavsmakdata$gedist, col=col12,pch=16,cex=1.5)
points(cmondatavsmakdata$feat_crit, cmondatavsmakdata$gedist, col=col2,pch=16,cex=1.5)
points(isadatavsmakdata$feat_crit, isadatavsmakdata$gedist, col=col3,pch=16,cex=1.5)
points(hcldatavsmakdata$feat_crit, hcldatavsmakdata$gedist, col=col4,pch=16,cex=1.5)


plot(makdatarefineenrich$TIGRrolepval, makdatarefine$gedist, xlim=c(0.00000000000000000001,1),ylim=c(5,1000), col=col1,pch=16,log="xy",xlab="TIGR role p-value",ylab="MAK distance",cex=1.5)
points(makdataenrich$TIGRrolepval, makdatavsmakdata$gedist, col=col12,pch=16,cex=1.5)
points(cmondataenrich$TIGRrolepval, cmondatavsmakdata$gedist, col=col2,pch=16,cex=1.5)
points(isadataenrich$TIGRrolepval, isadatavsmakdata$gedist, col=col3,pch=16,cex=1.5)
points(hcldataenrich$TIGRrolepval, hcldatavsmakdata$gedist, col=col4,pch=16,cex=1.5)

plot(makdatarefineenrich$GOpval, makdatarefine$gedist, xlim=c(0.0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000001,1),ylim=c(5,1000), col=col1,pch=16,log="xy",xlab="GO p-value",ylab="MAK distance",cex=1.5)
points(makdataenrich$GOpval, makdatavsmakdata$gedist, col=col12,pch=16,cex=1.5)
points(cmondataenrich$GOpval, cmondatavsmakdata$gedist, col=col2,pch=16,cex=1.5)
points(isadataenrich$GOpval, isadatavsmakdata$gedist, col=col3,pch=16,cex=1.5)
points(hcldataenrich$GOpval, hcldatavsmakdata$gedist, col=col4,pch=16,cex=1.5)

plot(makdatarefineenrich$Pathpval, makdatarefine$gedist, xlim=c(0.0000000000000000000000000000000000000000000000001,1),ylim=c(5,1000), col=col1,pch=16,log="xy",xlab="KEGG p-value",ylab="MAK distance",cex=1.5)
points(makdataenrich$Pathpval, makdatavsmakdata$gedist, col=col12,pch=16,cex=1.5)
points(cmondataenrich$Pathpval, cmondatavsmakdata$gedist, col=col2,pch=16,cex=1.5)
points(isadataenrich$Pathpval, isadatavsmakdata$gedist, col=col3,pch=16,cex=1.5)
points(hcldataenrich$Pathpval, hcldatavsmakdata$gedist, col=col4,pch=16,cex=1.5)

plot(makdatarefineenrich$TFpval, makdatarefine$gedist, xlim=c(0.00000000000000000001,1),ylim=c(5,1000), col=col1,pch=16,log="xy",xlab="TF p-value",ylab="MAK distance",cex=1.5)
points(makdataenrich$TFpval, makdatavsmakdata$gedist, col=col12,pch=16,cex=1.5)
points(cmondataenrich$TFpval, cmondatavsmakdata$gedist, col=col2,pch=16,cex=1.5)
points(isadataenrich$TFpval, isadatavsmakdata$gedist, col=col3,pch=16,cex=1.5)
points(hcldataenrich$TFpval, hcldatavsmakdata$gedist, col=col4,pch=16,cex=1.5)




###unrefined = x = 2,3 y = 0,1000
###refined
plot(data$full_crit, data$gedist)
plot(data$expr_mean_crit.1 + data$expr_reg_crit+data$expr_kend_crit, data$gedist, xlim=c(2,3),ylim=c(0,1000))
plot(data$expr_mean_crit.1 + data$expr_reg_crit+data$expr_kend_crit, data$gdist)
plot(data$expr_mean_crit.1 + data$expr_reg_crit+data$expr_kend_crit, data$edist)
plot(data$exp_mean, data$gedist)

plot(data$block_area, data$gedist)
plot(data$PPI_crit, data$gedist)
plot(data$TF_crit, data$gedist)
plot(data$feat_crit, data$gedist)
plot(data$expr_mean_crit, data$gedist)

plot(data$exp_mean[data$exp_mean > 1], data$gedist[data$exp_mean > 1])
