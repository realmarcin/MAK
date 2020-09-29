setwd("~/Documents/integr8_genom/Miner/miner_results/yeast/")

#labels <- c("1_C","TF_1_C","PPI_1_C","TF_PPI_1_C")

#for(i in 1:length(labels)) {
    #print(labels[i])
 
 labels <- c("TF_1_C")

    print(labels)
    
 infile <- paste("./",labels[i],"/results_yeast_cmonkey_",labels[i],".vbl",sep="")   
    exists <- file.info(infile)
    print(exists)
    if(!is.na(exists$size)) {
        print("doing")
data <- read.table(infile,sep="\t",header=T, comment.char="")


# [1] "block_area"          "full_crit"           "expr_mean_crit.1"    "expr_reg_crit"      
# [5] "expr_kend_crit"      "crit"             "percent_orig_genes"  "percent_orig_exp"   
# [9] "exp_mean"            "trajectory_position" "num_genes"           "num_exps"
write.table(data[order(data$full_crit,decreasing=F),],file=paste("results_yeast_cmonkey_",labels[i],".sort.full_crit.vbl",sep=""),sep="\t")
write.table(data[order(data$expr_mean_crit.1,decreasing=F),],file=paste("results_yeast_cmonkey_",labels[i],".sort.expr_mean_crit.1.vbl",sep=""),sep="\t")
write.table(data[order(data$expr_reg_crit,decreasing=F),],file=paste("results_yeast_cmonkey_",labels[i],".sort.expr_reg_crit.vbl",sep=""),sep="\t")
write.table(data[order(data$expr_kend_crit,decreasing=F),],file=paste("results_yeast_cmonkey_",labels[i],".sort.expr_kend_crit.vbl",sep=""),sep="\t")
write.table(data[order(data$TF_crit,decreasing=F),],file=paste("results_yeast_cmonkey_",labels[i],".sort.TF_crit.vbl",sep=""),sep="\t")
write.table(data[order(data$percent_orig_genes,decreasing=F),],file=paste("results_yeast_cmonkey_",labels[i],".sort.percent_orig_genes.vbl",sep=""),sep="\t")
write.table(data[order(data$percent_orig_exp,decreasing=),],file=paste("results_yeast_cmonkey_",labels[i],".sort.percent_orig_exp.vbl",sep=""),sep="\t")
write.table(data[order(data$exp_mean,decreasing=F),],file=paste("results_yeast_cmonkey_",labels[i],".sort.exp_mean.vbl",sep=""),sep="\t")
write.table(data[order(data$trajectory_position,decreasing=F),],file=paste("results_yeast_cmonkey_",labels[i],".sort.trajectory_position.vbl",sep=""),sep="\t")
write.table(data[order(data$num_genes,decreasing=F),],file=paste("results_yeast_cmonkey_",labels[i],".sort.num_genes.vbl",sep=""),sep="\t")
write.table(data[order(data$num_exps,decreasing=F),],file=paste("results_yeast_cmonkey_",labels[i],".sort.num_exps.vbl",sep=""),sep="\t")

###histograms
pdf(paste("results_yeast_cmonkey_",labels[i],".hist.pdf",sep=""),width=11,height=8.5)
par(mfrow=c(3,4))
hist(data$full_crit)
hist(data$expr_mean_crit.1)
hist(data$expr_reg_crit)
hist(data$expr_kend_crit)
hist(data$TF_crit)
hist(data$percent_orig_genes)
hist(data$percent_orig_exp)
hist(data$exp_mean)
hist(data$num_genes)
hist(data$num_exps)
hist(data$block_area)
hist(data$trajectory_position)
dev.off(2)

#> colnames(data)
# [1] "index"               "block_area"          "block_id"            "move_type"          
# [5] "pre_criterion"       "full_crit"           "expr_mean_crit"      "expr_mean_crit.1"   
# [9] "expr_reg_crit"       "expr_kend_crit"      "expr_cor_crit"       "expr_euc_crit"      
#[13] "PPI_crit"            "feat_crit"           "crit"             "percent_orig_genes" 
#[17] "percent_orig_exp"    "exp_mean"            "trajectory_position" "FEATURE_INDICES"    
#[21] "move_class"          "num_genes"           "num_exps"
 
 
library(lattice)
indices <- c(6,8,9,10,15,16,17,18,22,23,2,19)
ilabels <- colnames(data[,indices])
pdf(paste("results_yeast_cmonkey_",labels[i],".scattermatrix.pdf",sep=""),width=8.5,height=11)
splom(data[,indices], text=list(ilabels),col = rgb(0,0,0,alpha =0.05), axis.text.cex=.5,varname.cex=.5)
dev.off(2)

data_top <- data[data$exp_mean > 2 & data$full_crit > 0.95,]#data[data$exp_mean > 2 & data$full_crit > 0.95,]
#data_top <- data_top[,-1]
write.table(data_top, paste("results_yeast_cmonkey_",labels[i],"_top.vbl",sep=""),sep="\t")


datatop <- read.table(paste("results_yeast_cmonkey_",labels[i],"_top.vbl",sep=""),sep="\t",header=T)


# [1] "block_area"          "full_crit"           "expr_mean_crit.1"    "expr_reg_crit"      
# [5] "expr_kend_crit"      "crit"             "percent_orig_genes"  "percent_orig_exp"   
# [9] "exp_mean"            "trajectory_position" "num_genes"           "num_exps"
write.table(datatop[order(datatop$full_crit,decreasing=F),],file=paste("results_yeast_cmonkey_",labels[i],"_top.sort.full_crit.vbl",sep=""),sep="\t")
write.table(datatop[order(datatop$expr_mean_crit.1,decreasing=F),],file=paste("results_yeast_cmonkey_",labels[i],"_top.sort.expr_mean_crit.1.vbl",sep=""),sep="\t")
write.table(datatop[order(datatop$expr_reg_crit,decreasing=F),],file=paste("results_yeast_cmonkey_",labels[i],"_top.sort.expr_reg_crit.vbl",sep=""),sep="\t")
write.table(datatop[order(datatop$expr_kend_crit,decreasing=F),],file=paste("results_yeast_cmonkey_",labels[i],"_top.sort.expr_kend_crit.vbl",sep=""),sep="\t")
write.table(datatop[order(datatop$TF_crit,decreasing=F),],file=paste("results_yeast_cmonkey_",labels[i],"_top.sort.TF_crit.vbl",sep=""),sep="\t")
write.table(datatop[order(datatop$percent_orig_genes,decreasing=F),],file=paste("results_yeast_cmonkey_",labels[i],"_top.sort.percent_orig_genes.vbl",sep=""),sep="\t")
write.table(datatop[order(datatop$percent_orig_exp,decreasing=),],file=paste("results_yeast_cmonkey_",labels[i],"_top.sort.percent_orig_exp.vbl",sep=""),sep="\t")
write.table(datatop[order(datatop$exp_mean,decreasing=F),],file=paste("results_yeast_cmonkey_",labels[i],"_top.sort.exp_mean.vbl",sep=""),sep="\t")
write.table(datatop[order(datatop$trajectory_position,decreasing=F),],file=paste("results_yeast_cmonkey_",labels[i],"_top.sort.trajectory_position.vbl",sep=""),sep="\t")
write.table(datatop[order(datatop$num_genes,decreasing=F),],file=paste("results_yeast_cmonkey_",labels[i],"_top.sort.num_genes.vbl",sep=""),sep="\t")
write.table(datatop[order(datatop$num_exps,decreasing=F),],file=paste("results_yeast_cmonkey_",labels[i],"_top.sort.num_exps.vbl",sep=""),sep="\t")

###histograms
pdf(paste("results_yeast_cmonkey_",labels[i],"_top.hist.pdf",sep=""),width=11,height=8.5)
par(mfrow=c(3,4))
hist(datatop$full_crit)
hist(datatop$expr_mean_crit.1)
hist(datatop$expr_reg_crit)
hist(datatop$expr_kend_crit)
hist(datatop$TF_crit)
hist(datatop$percent_orig_genes)
hist(datatop$percent_orig_exp)
hist(datatop$exp_mean)
hist(datatop$num_genes)
hist(datatop$num_exps)
hist(datatop$block_area)
hist(datatop$trajectory_position)
dev.off(2)

#> colnames(datatop)
# [1] "index"               "block_area"          "block_id"            "move_type"          
# [5] "pre_criterion"       "full_crit"           "expr_mean_crit"      "expr_mean_crit.1"   
# [9] "expr_reg_crit"       "expr_kend_crit"      "expr_cor_crit"       "expr_euc_crit"      
#[13] "PPI_crit"            "feat_crit"           "crit"             "percent_orig_genes" 
#[17] "percent_orig_exp"    "exp_mean"            "trajectory_position" "FEATURE_INDICES"    
#[21] "move_class"          "num_genes"           "num_exps"
 
 
library(lattice)
indices <- c(6,8,9,10,15,16,17,18,22,23,2,19)
ilabels <- colnames(datatop[,indices])
pdf(paste("results_yeast_cmonkey_",labels[i],"_top.scattermatrix.pdf",sep=""),width=8.5,height=11)
splom(datatop[,indices], text=list(ilabels),col = rgb(0,0,0,alpha =0.05), axis.text.cex=.5,varname.cex=.5)
dev.off(2)

}
#}