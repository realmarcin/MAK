setwd("~/Documents/integr8_genom/Miner/miner_results/yeast/")



readData=function(file){

    #setwd("~/Documents/integr8_genom/Miner/miner_results/yeast/MASTER")
    data_expr <- read.table(file,sep="\t",header=T)
    cnames <- colnames(data_expr)
    data_expr <- cbind(row.names(data_expr), data_expr[,1:27])
    colnames(data_expr) <- cnames
    data_expr[is.na(data_expr)] <- 0
    data_expr
}

data_expr <- read.table("./EXPR_round12345_merge_refine/results_yeast_cmonkey_1_expr_MSEC_KendallC_GEECE_refine12345_top_0.25_1.0_c_liven_reconstructed_pval0.001_cut_0.01_summary.txt",sep="\t",header=T)
data_expr_TF <- read.table("./EXPR_TF_round12_merge_refine/results_yeast_cmonkey_TF_2to200_absSTART_001_NERSC_refine_1_top_0.25_1.0_c_liven_reconstructed_pval0.001_cut_0.01_summary.txt",sep="\t",header=T)
data_expr_TF_inter_feat <- read.table("./EXPR_TF_inter_feat_round12_merge_refine/results_yeast_cmonkey_1_expr_TF_inter_feat_refine_top_pluslong_0.25_1.0_c_liven_reconstructed_pval0.001_cut_0.01_summary.txt",sep="\t",header=T)
data_expr_ALL<- read.table("./EXPR_ALL_merge_refine/results_yeast_cmonkey_1_expr_MSEC_KendallC_GEECE_ALL_top_0.25_1.0_c_liven_reconstructed_pval0.001_cut_0.01_summary.txt",sep="\t",header=T)

