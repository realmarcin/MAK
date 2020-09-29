
library(gplots)
library(ggplot2)
library(RColorBrewer)
#library(Matching)
setwd("~/Documents/integr8_genom/Miner/miner_results/yeast/FINAL_PAPER_RESULTS/TOPLIST_SELECT/")


readData=function(file){
  
  setwd("~/Documents/integr8_genom/Miner/miner_results/yeast/FINAL_PAPER_RESULTS/TOPLIST_SELECT")
  data_expr <- read.table(file,sep="\t",header=T)
  #cnames <- colnames(data_expr)
  #data_expr <- cbind(row.names(data_expr), data_expr[,1:27])
  #colnames(data_expr) <- cnames
  #data_expr[is.na(data_expr)] <- 0
  #data_expr
}

###
###
###
#data_expr <- readData("./results_yeast_cmonkey_ws27_29_34_ROW_round12_cut_scoreperc66.0_exprNaN_0.0__nr_0.25_norm__nr_0.25_score.txt")
data_expr <- read.csv("./results_yeast_cmonkey_ws27_29_34_ROW_round12_cut_scoreperc66.0_exprNaN_0.0__nr_0.25_norm__nr_0.25_score.txt", sep="\t",header=T)
head(data_expr)
data_expr_TF <-  read.csv("./results_yeast_cmonkey_TF_check_round12_cut_scoreperc66.0_exprNaN_0.0__nr_0.25.txt", sep="\t",header=T)
data_expr_TF_inter_feat <-  read.csv("./results_yeast_cmonkey_ws43_woexclusion_round12_cut_scoreperc66.0_exprNaN_0.0__nr_0.25.txt", sep="\t",header=T)
data_expr_ROW<-  read.csv("./results_yeast_cmonkey_ROW_MSER_KendallR_GEERE_round12_cut_scoreperc66.0_exprNaN_0.0__nr_0.25.txt", sep="\t",header=T)

#!!! ISA BuildGraph needs updating
dataISA <-  read.csv("./ISAyeastcmonkey_MSEC_KendallC_GEECE_inter_feat.txt", sep="\t",header=T)
dataFABIA <-  read.csv("./yeast_cmonkey_expr_FABIA.txt_MSEC_KendallC_GEECE.txt", sep="\t",header=T)
dataCOALESCE <-  read.csv("./COALESCE.txt", sep="\t",header=T)

datacmonkey <-  read.csv("./cmonkey1_yeast_MSEC_KendallC_GEECE_inter_feat.txt", sep="\t",header=T)
datacmonkey2_meme <-  read.csv("./cmonkey1_yeast_MSEC_KendallC_GEECE_inter_feat.txt", sep="\t",header=T)
datacmonkey2_nomeme <-  read.csv("./cmonkey2_meme_yeast_cmonkey_MSEC_KendallC_GEECE_inter_feat.txt", sep="\t",header=T)


cols <- colnames(dataCOALESCE)
dataCOALESCE2 <- cbind(row.names(dataCOALESCE), dataCOALESCE)
dataCOALESCE2 <- dataCOALESCE2[,-29]
colnames(dataCOALESCE2) <- cols
dataCOALESCE <-dataCOALESCE2

data2DHCL <- read.csv("./2DHCL_abs_expr1.0.txt", sep="\t",header=T)


xlabelspart <- c("genes","exps","area","full_crit","expr_mean","TF","GO","TIGR","TIGRrole","Path","expr_crit","TF_crit","interaction_crit","feature_crit")#
ylabels <- c("expr","expr_TF","expr_TF_inter_feat","expr_ALL","ISA","FABIA","cMonkey","filtered 2D-HCL","COALESCE")

ylabels2 <- c("expr","expr_TF","expr_TF_inter_feat","expr_ALL","ISA","cMonkey","filtered 2D-HCL","COALESCE")
ylen <- length(ylabels)




###
make.bar <- function(label, custylim, data1, data2, data3, data4, data5, data6,data7,data8,data9,threshold=0) {
  medians <- mat.or.vec(1,0)
  
  #print(paste("threshold ",threshold,sep=""))
  
  if(threshold == 0.0) {
    r1 <- doBoot(data1,10000)
    #print(r1)
    r2 <- doBoot(data2,10000)
    #print(r2)
    r3 <- doBoot(data3,10000)
    #print(r3)
    if(!is.null(data4)) {
      r4 <- doBoot(data4,10000)
      #print(r4)
    }
    r5 <- doBoot(data5,10000)
    #print(r5)
    r6 <- doBoot(data6,10000)
    #print(r6)
    r7 <- doBoot(data7,10000)
    #print(r7)
    r8 <- doBoot(data8,10000)
    #print(r8)
    r9 <- doBoot(data9,10000)
    #print(r9)
  }    
  else {
    r1 <- doBootThreshold(data1,10000, threshold)
    #print(r1)
    r2 <- doBootThreshold(data2,10000, threshold)
    #print(r2)
    r3 <- doBootThreshold(data3,10000, threshold)
    #print(r3)
    if(!is.null(data4)) {
      r4 <- doBootThreshold(data4,10000, threshold)
      #print(r4)
    }
    r5 <- doBootThreshold(data5,10000, threshold)
    #print(r5)
    r6 <- doBootThreshold(data6,10000, threshold)
    #print(r6)
    r7 <- doBootThreshold(data7,10000, threshold)
    #print(r7)
    r8 <- doBootThreshold(data8,10000, threshold)
    #print(r8)
    r9 <- doBootThreshold(data9,10000, threshold)
    #print(r9)
  }
  
  medians <- c(medians, r1[[2]])
  medians <- c(medians, r2[[2]])
  medians <- c(medians, r3[[2]])
  if(!is.null(data4)) {
    medians <- c(medians, r4[[2]])
  }
  medians <- c(medians, r5[[2]])
  medians <- c(medians, r6[[2]])
  medians <- c(medians, r7[[2]])
  medians <- c(medians, r8[[2]])
  medians <- c(medians, r9[[2]])
  
  upper <- mat.or.vec(1,0)
  lower <- mat.or.vec(1,0)
  
  upper <- c(upper, r1[[3]])
  upper <- c(upper, r2[[3]])
  upper <- c(upper,  r3[[3]])
  if(!is.null(data4)) {
    upper <- c(upper,  r4[[3]])
  }
  upper <- c(upper, r5[[3]])
  upper <- c(upper,  r6[[3]])
  upper <- c(upper,  r7[[3]])
  upper <- c(upper,  r8[[3]])
  upper <- c(upper,  r9[[3]])
  
  lower <- c(lower,  r1[[3]])
  lower <- c(lower, r2[[3]])
  lower <- c(lower,  r3[[3]])
  if(!is.null(data4)) {
    lower <- c(lower,  r4[[3]])
  }
  lower <- c(lower, r5[[3]])
  lower <- c(lower,  r6[[3]])
  lower <- c(lower,  r7[[3]])
  lower <- c(lower,  r8[[3]])
  lower <- c(lower,  r9[[3]])
  
  #print(upper)
  #print(lower)
  
  curyl <- ylabels
  if(is.null(data4)) {
    curyl <- ylabels2
  }
  
  if(!is.null(ylim)) {
    barx <- barplot(medians,main=paste(label,sep=""), xlab="Method", ylab="Median value",ylim=custylim) #log="y", #, ylim=c(0, 3500)
  }
  else {
    barx <- barplot(medians,main=paste(label,sep=""), xlab="Method", ylab="Median value")   
  }
  axis(1, at=((1:length(curyl))*1.2 - 0.6), labels=curyl,cex.axis=0.8)
  barx <- error.bar(barx, medians, upper, lower)
  
  medians
}

error.bar <- function(x, y, upper, lower=upper, length=0.1,...){
  if(length(x) != length(y) | length(y) !=length(lower) | length(lower) != length(upper))
    stop("vectors must be same length")
  x <- arrows(x,y+upper, x, y-lower, angle=90, code=3, length=length, ...)
  x
}

###
doBoot <- function(data, iter) {
  rs <- lapply(1:iter, function(i) sample(data, replace = T))
  m1s <- sapply(rs, median)
  m1 <- median(m1s)
  sd1 <- sqrt(var(m1s))
  
  ret <- list(m1s, m1, sd1)
}

doBootThreshold <- function(data, iter, threshold) {
  #print(paste("doBootThreshold ",threshold,sep=""))
  totallen <- length(data)
  rs <- lapply(1:iter, function(i) sample(data, replace = T))
  #print(dim(rs))
  #print(length(rs))
  #print(rs[1])
  #print(rs[10000])
  
  m1s <- mat.or.vec(1,0);
  for(i in 1:iter) {
    d <- rs[[i]]
    #cat("len ",length(d)," ","sublen ", length(d[d < threshold])," m1s ",  length(m1s)," ",dim(m1s),"\n")
    #print(d[1:10])
    #print(length(d[d < threshold]))
    m1s <- c(m1s, length(d[d < threshold]))
  }
  #m1s <-apply(rs, 2, function(x) length(x[[x < threshold]]))
  #print(totallen)
  #print(m1s)
  m1s <- m1s/totallen
  #print(m1s)
  m1 <- median(m1s)
  sd1 <- sqrt(var(m1s))
  
  ret <- list(m1s, m1, sd1)
}


###

featuredata <- mat.or.vec(1,1)
print("genes")
pdf("bar_genes_methods_0.25.pdf",width=8.5,height=11)
featuredata <- cbind(featuredata, make.bar("genes", c(0, 500), data_expr$num_genes,data_expr_TF$num_genes,data_expr_TF_inter_feat$num_genes,data_expr_ROW$num_genes,dataISA$num_genes,dataFABIA$num_genes,datacmonkey$num_genes,data2DHCL$num_genes,dataCOALESCE$num_genes))
dev.off(2)
print("experiments")
pdf("bar_experiments_methods_0.25.pdf",width=8.5,height=11)
featuredata <- cbind(featuredata, make.bar("experiments",c(0, 150),  data_expr$exps,data_expr_TF$exps,data_expr_TF_inter_feat$exps,data_expr_ROW$exps,dataISA$exps,dataFABIA$exps,datacmonkey$exps,data2DHCL$exps,dataCOALESCE$exps))
dev.off(2)
print("area")
pdf("bar_area_methods_0.25.pdf",width=8.5,height=11)
featuredata <- cbind(featuredata, make.bar("area", c(0, 4000), data_expr$area,data_expr_TF$area,data_expr_TF_inter_feat$area,data_expr_ROW$area,dataISA$area,dataFABIA$area,datacmonkey$area,data2DHCL$area,dataCOALESCE$area))
dev.off(2)
print("crit")
pdf("bar_fullcrit_methods_0.25.pdf",width=8.5,height=11)
featuredata <- cbind(featuredata, make.bar("full criterion", c(0, 3.1), data_expr$full_crit,data_expr_TF$full_crit,data_expr_TF_inter_feat$full_crit,data_expr_ROW$full_crit,dataISA$full_crit,dataFABIA$full_crit,datacmonkey$full_crit,data2DHCL$full_crit,dataCOALESCE$full_crit))
dev.off(2)
print("expression")
pdf("bar_exprmean_methods_0.25.pdf",width=8.5,height=11)
featuredata <- cbind(featuredata, make.bar("expression mean", c(0, 1.5), data_expr$exp_mean,data_expr_TF$exp_mean,data_expr_TF_inter_feat$exp_mean,data_expr_ROW$exp_mean,dataISA$exp_mean,dataFABIA$exp_mean,datacmonkey$exp_mean,data2DHCL$exp_mean,dataCOALESCE$exp_mean))
dev.off(2)

pdf("bar_TF_methods_0.25.pdf",width=8.5,height=11)
print("TF")
featuredata <- cbind(featuredata, make.bar("TF", c(0,1), data_expr$TFpval,data_expr_TF$TFpval,data_expr_TF_inter_feat$TFpval,data_expr_ROW$TFpval,dataISA$TFpval,dataFABIA$TFpval,datacmonkey$TFpval,data2DHCL$TFpval,dataCOALESCE$TFpval, 0.01))
dev.off(2)
pdf("bar_GO_methods_0.25.pdf",width=8.5,height=11)
featuredata <- cbind(featuredata, make.bar("GO", c(0,1), data_expr$GOpval,data_expr_TF$GOpval,data_expr_TF_inter_feat$GOpval,data_expr_ROW$GOpval,dataISA$GOpval,dataFABIA$GOpval,datacmonkey$GOpval,data2DHCL$GOpval,dataCOALESCE$GOpval, 0.01))
dev.off(2)
pdf("bar_TIGR_methods_0.25.pdf",width=8.5,height=11)
featuredata <- cbind(featuredata, make.bar("TIGR", c(0, 1.2), data_expr$TIGRpval,data_expr_TF$TIGRpval,data_expr_TF_inter_feat$TIGRpval,data_expr_ROW$TIGRpval,dataISA$TIGRpval,dataFABIA$TIGRpval,datacmonkey$TIGRpval,data2DHCL$TIGRpval,dataCOALESCE$TIGRpval))
dev.off(2)
pdf("bar_TIGRrole_methods_0.25.pdf",width=8.5,height=11)
featuredata <- cbind(featuredata, make.bar("TIGR role", c(0, 1.2), data_expr$TIGRrolepval,data_expr_TF$TIGRrolepval,data_expr_TF_inter_feat$TIGRrolepval,data_expr_ROW$TIGRrolepval,dataISA$TIGRrolepval,dataFABIA$TIGRrolepval,datacmonkey$TIGRrolepval,data2DHCL$TIGRrolepval,dataCOALESCE$TIGRrolepval))
dev.off(2)
pdf("bar_path_methods_0.25.pdf",width=8.5,height=11)
featuredata <- cbind(featuredata, make.bar("pathway", c(0,1), data_expr$Pathpval,data_expr_TF$Pathpval,data_expr_TF_inter_feat$Pathpval,data_expr_ROW$Pathpval,dataISA$Pathpval,dataFABIA$Pathpval,datacmonkey$Pathpval,data2DHCL$Pathpval,dataCOALESCE$Pathpval, 0.01))
dev.off(2)

data_expr__expr_crit <- rowMeans(cbind(data_expr$expr_mean_crit.1, data_expr$exp_kendall_crit,data_expr$exp_reg_crit))
data_expr_TF__expr_crit <- rowMeans(cbind(data_expr_TF$expr_mean_crit.1, data_expr_TF$exp_kendall_crit,data_expr_TF$exp_reg_crit))
data_expr_TF_inter_feat__expr_crit <- rowMeans(cbind(data_expr_TF_inter_feat$expr_mean_crit.1, data_expr_TF_inter_feat$exp_kendall_crit,data_expr_TF_inter_feat$exp_reg_crit))
data_expr_ROW__expr_crit <- rowMeans(cbind(data_expr_ROW$expr_mean_crit.1, data_expr_ROW$exp_kendall_crit,data_expr_ROW$exp_reg_crit))
dataISA__expr_crit <- rowMeans(cbind(dataISA$expr_mean_crit.1, dataISA$exp_kendall_crit,dataISA$exp_reg_crit))
dataFABIA__expr_crit <- rowMeans(cbind(dataFABIA$expr_mean_crit.1, dataFABIA$exp_kendall_crit,dataFABIA$exp_reg_crit))
datacmonkey__expr_crit <- rowMeans(cbind(datacmonkey$expr_mean_crit.1, datacmonkey$exp_kendall_crit,datacmonkey$exp_reg_crit))
data2DHCL__expr_crit <- rowMeans(cbind(data2DHCL$expr_mean_crit.1, data2DHCL$exp_kendall_crit,data2DHCL$exp_reg_crit))
dataCOALESCE__expr_crit <- rowMeans(cbind(dataCOALESCE$expr_mean_crit.1, dataCOALESCE$exp_kendall_crit,dataCOALESCE$exp_reg_crit))

pdf("bar_exprcrit_methods_0.25.pdf",width=8.5,height=11)
featuredata <- cbind(featuredata, make.bar("expression criterion", c(0, 1.1), data_expr__expr_crit,data_expr_TF__expr_crit,data_expr_TF_inter_feat__expr_crit,data_expr_ROW__expr_crit,dataISA__expr_crit,dataFABIA__expr_crit,datacmonkey__expr_crit,data2DHCL__expr_crit,dataCOALESCE__expr_crit))
dev.off(2)
pdf("bar_TFcrit_methods_0.25.pdf",width=8.5,height=11)
featuredata <- cbind(featuredata, make.bar("TF criterion",c(0, 1.1),  data_expr$TF_crit,data_expr_TF$TF_crit,data_expr_TF_inter_feat$TF_crit,data_expr_ROW$TF_crit,dataISA$TF_crit,dataFABIA$TF_crit,datacmonkey$TF_crit,data2DHCL$TF_crit,dataCOALESCE$TF_crit))
dev.off(2)
pdf("bar_interactioncrit_methods_0.25.pdf",width=8.5,height=11)
featuredata <- cbind(featuredata, make.bar("interaction criterion", c(0, 1.1), data_expr$inter_crit,data_expr_TF$inter_crit,data_expr_TF_inter_feat$inter_crit,data_expr_ROW$inter_crit,dataISA$inter_crit,dataFABIA$inter_crit,datacmonkey$inter_crit,data2DHCL$inter_crit,dataCOALESCE$inter_crit))
dev.off(2)
pdf("bar_featurecrit_methods_0.25.pdf",width=8.5,height=11)
featuredata <- cbind(featuredata, make.bar("ortholog criterion", c(0, 1.1), data_expr$feat_crit,data_expr_TF$feat_crit,data_expr_TF_inter_feat$feat_crit,data_expr_ROW$feat_crit,dataISA$feat_crit,dataFABIA$feat_crit,datacmonkey$feat_crit,data2DHCL$feat_crit,dataCOALESCE$feat_crit))
dev.off(2)

head(featuredata)
featuredata <- featuredata[,-1]
colnames(featuredata) <- xlabelspart
row.names(featuredata) <- ylabels
write.table(featuredata,file="featuredata_0.25_v20.txt",sep="\t")


###EXCLUDE
featuredata_exclude <- mat.or.vec(1,1)
print("genes")
pdf("bar_genes_methods.pdf",width=8.5,height=11)
featuredata_exclude <- cbind(featuredata_exclude, make.bar("genes", c(0, 500), data_expr_exclude$genes,data_expr_TF_exclude$genes,data_expr_TF_inter_feat_exclude$genes,data_expr_ROW_exclude$genes,dataISA_exclude$genes,dataFABIA_exclude$genes,datacmonkey_exclude$genes,data2DHCL_exclude$genes,dataCOALESCE_exclude$genes))
dev.off(2)
print("experiments")
pdf("bar_experiments_methods.pdf",width=8.5,height=11)
featuredata_exclude <- cbind(featuredata_exclude, make.bar("experiments",c(0, 150),  data_expr_exclude$exps,data_expr_TF_exclude$exps,data_expr_TF_inter_feat_exclude$exps,data_expr_ROW_exclude$exps,dataISA_exclude$exps,dataFABIA_exclude$exps,datacmonkey_exclude$exps,data2DHCL_exclude$exps,dataCOALESCE_exclude$exps))
dev.off(2)
print("area")
pdf("bar_area_methods.pdf",width=8.5,height=11)
featuredata_exclude <- cbind(featuredata_exclude, make.bar("area", c(0, 4000), data_expr_exclude$area,data_expr_TF_exclude$area,data_expr_TF_inter_feat_exclude$area,data_expr_ROW_exclude$area,dataISA_exclude$area,dataFABIA_exclude$area,datacmonkey_exclude$area,data2DHCL_exclude$area,dataCOALESCE_exclude$area))
dev.off(2)
print("crit")
pdf("bar_fullcrit_methods.pdf",width=8.5,height=11)
featuredata_exclude <- cbind(featuredata_exclude, make.bar("full criterion", c(0, 3.1), data_expr_exclude$full_crit,data_expr_TF_exclude$full_crit,data_expr_TF_inter_feat_exclude$full_crit,data_expr_ROW_exclude$full_crit,dataISA_exclude$full_crit,dataFABIA_exclude$full_crit,datacmonkey_exclude$full_crit,data2DHCL_exclude$full_crit,dataCOALESCE_exclude$full_crit))
dev.off(2)
print("expression")
pdf("bar_exprmean_methods.pdf",width=8.5,height=11)
featuredata_exclude <- cbind(featuredata_exclude, make.bar("expression mean", c(0, 1.5), data_expr_exclude$exp_mean,data_expr_TF_exclude$exp_mean,data_expr_TF_inter_feat_exclude$exp_mean,data_expr_ROW_exclude$exp_mean,dataISA_exclude$exp_mean,dataFABIA_exclude$exp_mean,datacmonkey_exclude$exp_mean,data2DHCL_exclude$exp_mean,dataCOALESCE_exclude$exp_mean))
dev.off(2)

pdf("bar_TF_methods.pdf",width=8.5,height=11)
print("TF")
featuredata_exclude <- cbind(featuredata_exclude, make.bar("TF", c(0,1), data_expr_exclude$TFpval,data_expr_TF_exclude$TFpval,data_expr_TF_inter_feat_exclude$TFpval,data_expr_ROW_exclude$TFpval,dataISA_exclude$TFpval,dataFABIA_exclude$TFpval,datacmonkey_exclude$TFpval,data2DHCL_exclude$TFpval,dataCOALESCE_exclude$TFpval, 0.01))
dev.off(2)
pdf("bar_GO_methods.pdf",width=8.5,height=11)
featuredata_exclude <- cbind(featuredata_exclude, make.bar("GO", c(0,1), data_expr_exclude$GOpval,data_expr_TF_exclude$GOpval,data_expr_TF_inter_feat_exclude$GOpval,data_expr_ROW_exclude$GOpval,dataISA_exclude$GOpval,dataFABIA_exclude$GOpval,datacmonkey_exclude$GOpval,data2DHCL_exclude$GOpval,dataCOALESCE_exclude$GOpval, 0.01))
dev.off(2)
pdf("bar_TIGR_methods.pdf",width=8.5,height=11)
featuredata_exclude <- cbind(featuredata_exclude, make.bar("TIGR", c(0, 1.2), data_expr_exclude$TIGRpval,data_expr_TF_exclude$TIGRpval,data_expr_TF_inter_feat_exclude$TIGRpval,data_expr_ROW_exclude$TIGRpval,dataISA_exclude$TIGRpval,dataFABIA_exclude$TIGRpval,datacmonkey_exclude$TIGRpval,data2DHCL_exclude$TIGRpval,dataCOALESCE_exclude$TIGRpval))
dev.off(2)
pdf("bar_TIGRrole_methods.pdf",width=8.5,height=11)
featuredata_exclude <- cbind(featuredata_exclude, make.bar("TIGR role", c(0, 1.2), data_expr_exclude$TIGRrolepval,data_expr_TF_exclude$TIGRrolepval,data_expr_TF_inter_feat_exclude$TIGRrolepval,data_expr_ROW_exclude$TIGRrolepval,dataISA_exclude$TIGRrolepval,dataFABIA_exclude$TIGRrolepval,datacmonkey_exclude$TIGRrolepval,data2DHCL_exclude$TIGRrolepval,dataCOALESCE_exclude$TIGRrolepval))
dev.off(2)
pdf("bar_path_methods.pdf",width=8.5,height=11)
featuredata_exclude <- cbind(featuredata_exclude, make.bar("pathway", c(0,1), data_expr_exclude$Pathpval,data_expr_TF_exclude$Pathpval,data_expr_TF_inter_feat_exclude$Pathpval,data_expr_ROW_exclude$Pathpval,dataISA_exclude$Pathpval,dataFABIA_exclude$Pathpval,datacmonkey_exclude$Pathpval,data2DHCL_exclude$Pathpval,dataCOALESCE_exclude$Pathpval, 0.01))
dev.off(2)

data_expr__expr_crit <- rowMeans(cbind(data_expr_exclude$exp_mse_crit, data_expr_exclude$exp_kendall_crit,data_expr_exclude$exp_reg_crit))
data_expr_TF__expr_crit <- rowMeans(cbind(data_expr_TF_exclude$exp_mse_crit, data_expr_TF_exclude$exp_kendall_crit,data_expr_TF_exclude$exp_reg_crit))
data_expr_TF_inter_feat__expr_crit <- rowMeans(cbind(data_expr_TF_inter_feat_exclude$exp_mse_crit, data_expr_TF_inter_feat_exclude$exp_kendall_crit,data_expr_TF_inter_feat_exclude$exp_reg_crit))
data_expr_ROW__expr_crit <- rowMeans(cbind(data_expr_ROW_exclude$exp_mse_crit, data_expr_ROW_exclude$exp_kendall_crit,data_expr_ROW_exclude$exp_reg_crit))
dataISA_exclude__expr_crit <- rowMeans(cbind(dataISA_exclude$exp_mse_crit, dataISA_exclude$exp_kendall_crit,dataISA_exclude$exp_reg_crit))
dataFABIA_exclude__expr_crit <- rowMeans(cbind(dataFABIA_exclude$exp_mse_crit, dataFABIA_exclude$exp_kendall_crit,dataFABIA_exclude$exp_reg_crit))
datacmonkey_exclude__expr_crit <- rowMeans(cbind(datacmonkey_exclude$exp_mse_crit, datacmonkey_exclude$exp_kendall_crit,datacmonkey_exclude$exp_reg_crit))
data2DHCL_exclude__expr_crit <- rowMeans(cbind(data2DHCL_exclude$exp_mse_crit, data2DHCL_exclude$exp_kendall_crit,data2DHCL_exclude$exp_reg_crit))
dataCOALESCE_exclude__expr_crit <- rowMeans(cbind(dataCOALESCE_exclude$exp_mse_crit, dataCOALESCE_exclude$exp_kendall_crit,dataCOALESCE_exclude$exp_reg_crit))

pdf("bar_exprcrit_methods.pdf",width=8.5,height=11)
featuredata_exclude <- cbind(featuredata_exclude, make.bar("expression criterion", c(0, 1.1), data_expr__expr_crit,data_expr_TF__expr_crit,data_expr_TF_inter_feat__expr_crit,data_expr_ROW__expr_crit,dataISA_exclude__expr_crit,dataFABIA_exclude__expr_crit,datacmonkey_exclude__expr_crit,data2DHCL_exclude__expr_crit,dataCOALESCE_exclude__expr_crit))
dev.off(2)
pdf("bar_TFcrit_methods.pdf",width=8.5,height=11)
featuredata_exclude <- cbind(featuredata_exclude, make.bar("TF criterion",c(0, 1.1),  data_expr_exclude$TF_crit,data_expr_TF_exclude$TF_crit,data_expr_TF_inter_feat_exclude$TF_crit,data_expr_ROW_exclude$TF_crit,dataISA_exclude$TF_crit,dataFABIA_exclude$TF_crit,datacmonkey_exclude$TF_crit,data2DHCL_exclude$TF_crit,dataCOALESCE_exclude$TF_crit))
dev.off(2)
pdf("bar_interactioncrit_methods.pdf",width=8.5,height=11)
featuredata_exclude <- cbind(featuredata_exclude, make.bar("interaction criterion", c(0, 1.1), data_expr_exclude$inter_crit,data_expr_TF_exclude$inter_crit,data_expr_TF_inter_feat_exclude$inter_crit,data_expr_ROW_exclude$inter_crit,dataISA_exclude$inter_crit,dataFABIA_exclude$inter_crit,datacmonkey_exclude$inter_crit,data2DHCL_exclude$inter_crit,dataCOALESCE_exclude$inter_crit))
dev.off(2)
pdf("bar_featurecrit_methods.pdf",width=8.5,height=11)
featuredata_exclude <- cbind(featuredata_exclude, make.bar("ortholog criterion", c(0, 1.1), data_expr_exclude$feat_crit,data_expr_TF_exclude$feat_crit,data_expr_TF_inter_feat_exclude$feat_crit,data_expr_ROW_exclude$feat_crit,dataISA_exclude$feat_crit,dataFABIA_exclude$feat_crit,datacmonkey_exclude$feat_crit,data2DHCL_exclude$feat_crit,dataCOALESCE_exclude$feat_crit))
dev.off(2)

featuredata_exclude <- featuredata_exclude[,-1]
colnames(featuredata_exclude) <- xlabelspart
row.names(featuredata_exclude) <- ylabels
write.table(featuredata_exclude,file="featuredata_exclude_0.25_v19.txt",sep="\t")


featuredata <- read.table("featuredata_0.25_v19.txt",sep="\t",header=T,row.names=1)
featuredata_exclude <- read.table("featuredata_exclude_0.25_v19.txt",sep="\t",header=T,row.names=1)



###all methods
#MAK-clust expr pval 0.001 0.5 merge exclude 
#MAK-clust expr pval 0.001 0.5 merge refine merge exclude
#MAK-clust expr Sn pval 0.001 0.5 merge exclude
#MAK-clust expr TF  pval 0.001 0.5 merge exclude
#MAK-clust expr TF  pval 0.001 0.5 merge refine merge exclude
#MAK-clust expr TF inter feat pval 0.001 0.5 merge exclude
#MAK-clust expr TF inter feat pval 0.001 0.5 merge refine merge exclude
#ISA pval 0.001 0.5 merge exclude
#FABIA pval 0.001 exclude
#cMonkey pval 0.001 0.5 merged exclude
#2D HCL > 1.0expr pval 0.001 exclude

#indices for plot
#3,5,7,8,9,10,11

#from featuredata
xlabels <- c("Genes","Experiments","Area","Full crit.","Abs. Diff. Expr.","Expression crit.","TF crit.","PPI crit.","Ortholog crit.")

#ALL LABELS bicluster_set_summary_v8.txt
#Unmerged	Number	Coverage	Coverage fraction	Coverage >1	Coverage fraction >1	Coverage >2	Coverage fraction >2	Enrichment >1	Enrichment >2	GO biclusters	TF biclusters	Pathway biclusters	TIGRrole biclusters	GO unique	TF unique	Pathway unique	TIGRrole unique	GO coverage	TF coverage	Pathway coverage	TIGRrole coverage	GO p.b.	TF p.b.	Path p.b.	TIGRrole p.b.	Runtime	perbic sum	coverage sum	GOcover/totalcover	TFcover/totalcover	Pathcover/totalcover	Num genes	Num exps
xlabels <- c(xlabels, "Number","Coverage", "Coverage > 1","Coverage > 2","Enrich. > 1 Diff. Expr.","Enrich. > 2 Diff. Expr.", "GO biclusters", "TF biclusters", "Pathway biclusters", "TIGRrole biclusters", "GO coverage","TF coverage","Pathway coverage", "TIGRrole coverage","GO p.b.","TF p.b.","Path p.b.", "TIGRrole p.b.", "Runtime", "Max. genes", "Max. exps.")

ylabels <- c("MAK(expr.)","MAK(expr.+TF)","MAK(expr.+TF+PPI+ortho.)","MAK(ALL)","ISA","FABIA","cMonkey","2D-HCL","COALESCE")



###
###PCA analysis
datacover <- read.table("../MASTER/bicluster_set_summary_v18.txt",sep="\t",header=T,row.names=1)

methodind <- c(2,5,7,8,9:11,13,12)

datacoveredit <- datacover[methodind,]
cn <- colnames(datacoveredit)

featuredataedit <- featuredata[,-(6:10)]

#ylabels <- c("expr","expr_TF","ISA","FABIA","cMonkey","filtered 2D-HCL")
colind <- c(2,4,6,8,9,10,11:14,19:27,33:34)
featuredataALL <- cbind(featuredataedit, datacoveredit[,colind])
colnames(featuredataALL) <- xlabels
rownames(featuredataALL) <- ylabels
write.table(featuredataALL,file="featuredataALL.txt",sep="\t")

pca <- prcomp(featuredataALL, scale=T)
plot(pca)
summary(pca)
biplot(pca)

###no FABIA
cpfeaturedata <- featuredataALL[-5,]
pca <- prcomp(cpfeaturedata, scale=T)
summary(pca)
biplot(pca)



#EXCLUDE PCA ANALYSIS
datacover_exclude <- read.table("../MASTER/bicluster_set_summary_exclude_v18.txt",sep="\t",header=T,row.names=1)
datacover_excludeedit <- datacover_exclude[methodind,]
cn_exclude <- colnames(datacover_excludeedit)
featuredataedit_exclude <- featuredata_exclude[,-(6:10)]

featuredataALL_exclude <- cbind(featuredataedit_exclude, datacover_excludeedit[,colind])
colnames(featuredataALL_exclude ) <- xlabels
ylabels_exclude <- c("MAK(expr.)*","MAK(expr.,TF)*","MAK(expr.,TF,PPI,ortho.)*","MAK(ALL)*","ISA*","FABIA*","cMonkey*","2D-HCL*","COALESCE*")
rownames(featuredataALL_exclude ) <- ylabels_exclude
write.table(featuredataALL_exclude,file="featuredataALL_exclude.txt",sep="\t")


pca_exclude <- prcomp(featuredataALL_exclude, scale=T)
plot(pca_exclude)
summary(pca_exclude)
biplot(pca_exclude)


###no FABIA
cpfeaturedata_exclude <- featuredataALL_exclude[-5,]
pca_exclude <- prcomp(cpfeaturedata_exclude, scale=T)
summary(pca_exclude)
biplot(pca_exclude)



####
###
##

mypalette <- rev(brewer.pal(9, "Blues"))
mypalette <- c(mypalette, brewer.pal(9, "YlOrBr"))

###
#feature heatmap
mat <- as.matrix(featuredataALL)
mat <- scale(mat)
#symnum( cordata <- cor(mat) )
range<- range(mat)

rowv <- as.dendrogram(hclust(as.dist(1-cor(t(mat)))))
colv <- as.dendrogram(hclust(as.dist(1-cor(mat))))

heatmap.2(mat, Rowv=rowv, Colv=colv,trace="none",col=mypalette)#
#pheatmap(t(mat),clustering_distance_rows="correlation",clustering_distance_cols="correlation",color=mypalette,cellwidth=10,cellheight=10)
#,show_rownames=T,show_colnames=T,legend=Tbreaks=breaks,cclustering_distance_rows="correlation",clustering_distance_cols="correlation",#,fontsize_row=2,fontsize_col=2,


#exclude
mat <- as.matrix(featuredataALL_exclude)
mat <- scale(mat)
#symnum( cordata <- cor(mat) )
range<- range(mat)
rowv <- as.dendrogram(hclust(as.dist(1-cor(t(mat)))))
colv <- as.dendrogram(hclust(as.dist(1-cor(mat))))
heatmap.2(mat, Rowv=rowv, Colv=colv,trace="none",col=mypalette)#
#heatmap.2(mat, trace="none",col=mypalette)#


###All plus exclude

featuredataALL_plus_exclude <- rbind(featuredataALL, featuredataALL_exclude)
write.table(scale(featuredataALL_plus_exclude),file="featuredataALL_plus_exclude_scale.txt",sep="\t")
write.table(featuredataALL_plus_exclude,file="featuredataALL_plus_exclude.txt",sep="\t")
mat <- as.matrix(featuredataALL_plus_exclude)
mat <- scale(mat)

mat <- mat[,-c(12, 14)]

#symnum( cordata <- cor(mat) )
range<- range(mat)
rowv <- as.dendrogram(hclust(as.dist(1-cor(t(mat)))))
colv <- as.dendrogram(hclust(as.dist(1-cor(mat))))
heatmap.2(mat, Rowv=rowv, Colv=colv,trace="none",col=mypalette)#,scale="column"

library(clValid)
#valid <- clValid(mat, nClust=c(2:18),clMethods=c("hierarchical"),validation=c("stability"),metric=c("correlation"),method=c("complete"))
valid <- clValid(mat, nClust=c(2:17),clMethods=c("hierarchical"),validation=c("internal"),metric=c("correlation"),method=c("complete"))
validC <- clValid(t(mat), nClust=c(2:27),clMethods=c("hierarchical"),validation=c("internal"),metric=c("correlation"),method=c("complete"))

plot(valid)
#silhouette max is 9 clusters val 0.7

plot(validC)
#silhouette max is 7 clusters for > 2, val 0.5

km <- kmeans(mat, 9, iter.max = 1000, nstart=100)

kmC <- kmeans(t(mat), 7, iter.max = 1000, nstart=100)

Clustering vector:
  MAK(expr.)             MAK(expr.,TF)  MAK(expr.,TF,PPI,ortho.)                  MAK(ALL)                       ISA                     FABIA 
7                         1                         8                         7                         4                         6 
cMonkey                    2D-HCL                  COALESCE               MAK(expr.)*            MAK(expr.,TF)* MAK(expr.,TF,PPI,ortho.)* 
  3                         4                         3                         7                         1                         8 
MAK(ALL)*                      ISA*                    FABIA*                  cMonkey*                   2D-HCL*                 COALESCE* 
  7                         4                         2                         3                         5                         3


Clustering vector:
  Genes       Experiments              Area        Full crit.  Abs. Diff. Expr.  Expression crit.          TF crit.         PPI crit. 
4                 4                 4                 1                 1                 1                 1                 1 
Ortholog crit.            Number          Coverage      Coverage > 1      Coverage > 2 Enrich. > 1 Expr. Enrich. > 2 Expr.              GO # 
5                 6                 6                 6                 6                 3                 3                 6 
TF #         Pathway #        TIGRrole #       GO coverage       TF coverage  Pathway coverage TIGRrole coverage           GO p.b. 
2                 6                 6                 5                 5                 5                 1                 2 
TF p.b.         Path p.b.     TIGRrole p.b.           Runtime        Max. genes        Max. exps. 
2                 1                 3                 3                 4                 6

sortmat <- mat.or.vec(1,1)
rnames <- mat.or.vec(0,0)
for(i in 1:max(km$cluster)) {
  print(i)
  index <- which(km$cluster == i)
  print(index)
  index <- sort(index)
  print(index)           
  print(row.names(mat)[index])
  rnames <- c(rnames, row.names(mat)[index])
  print(mat[index,])
  sortmat <- rbind(sortmat, mat[index,])
}
sortmat <- sortmat[-1,]
row.names(sortmat) <- rnames


sortmat2 <- mat.or.vec(1,1)
cnames <- mat.or.vec(0,0)
for(i in 1:max(kmC$cluster)) {
  print(i)
  index <- which(kmC$cluster == i)
  print(index)
  index <- sort(index)
  print(index)           
  print(colnames(sortmat)[index])
  cnames <- c(cnames, colnames(sortmat)[index])
  print(sortmat[,index])
  sortmat2 <- cbind(sortmat2, sortmat[,index])
}
sortmat2 <- sortmat2[,-1]
colnames(sortmat2) <- cnames

heatmap.2(sortmat2, dendrogram="none",Rowv=F, ColV=F, trace="none",col=mypalette)#,scale="column"


###example.

densary <- cbind(densary,dens$y)
dens <- density(data_MASTER$genes,from=0,to=300)
densarx <- cbind(densarx,dens$x)
densary <- cbind(densary,dens$y)
dens <- density(data2DHCL$genes,from=0,to=300)
densarx <- cbind(densarx,dens$x)
densary <- cbind(densary,dens$y)

dimdens <- dim(densarx)
ssd <- mat.or.vec(length(ylabels),length(ylabels))
for(i in 1:ylen) {
  for(j in 1:ylen) {
    if(i != j) {
      ssd[i,j] <- wilcox.test(densary[,i], densary[,j],alternative="greater")$p.value#1 - ks.boot(densary[,i], densary[,j],1000)$ks.boot.pvalue#sum(abs(densary[,i] - densary[,j])^2)
    }
    else {
      ssd[i,j] <- 1
    }
  }
}

rownames(ssd) <- ylabels
colnames(ssd) <- ylabels

#pdf("sumofsquaredifferences_self_genes.pdf",width=8.5,height=11)
#heatmap.2(log(ssd+0.000001,2))
#dev.off(2)
pdf("wrs_pval_self_genes.pdf",width=8.5,height=11)
heatmap.2(ssd,dendrogram ="none",Rowv=F,Colv=F)
dev.off(2)

dens <- density(data_expr$exps,from=0,to=150)
densarx <- cbind(dens$x)
densary <- cbind(dens$y)
dens <- density(data_expr_TF$exps,from=0,to=150)
densarx <- cbind(densarx,dens$x)
densary <- cbind(densary,dens$y)
dens <- density(data_expr_feat$exps,from=0,to=150)
densarx <- cbind(densarx,dens$x)
densary <- cbind(densary,dens$y)
dens <- density(data_expr_inter$exps,from=0,to=150)
densarx <- cbind(densarx,dens$x)
densary <- cbind(densary,dens$y)
dens <- density(data_expr_TF_inter$exps,from=0,to=150)
densarx <- cbind(densarx,dens$x)
densary <- cbind(densary,dens$y)
dens <- density(data_expr_TF_inter_unmerged$exps,from=0,to=150)
densarx <- cbind(densarx,dens$x)
densary <- cbind(densary,dens$y)
dens <- density(data_expr_TF_inter_feat$exps,from=0,to=150)
densarx <- cbind(densarx,dens$x)
densary <- cbind(densary,dens$y)
dens <- density(data_MASTER$exps,from=0,to=150)
densarx <- cbind(densarx,dens$x)
densary <- cbind(densary,dens$y)
dens <- density(data2DHCL$exps,from=0,to=150)
densarx <- cbind(densarx,dens$x)
densary <- cbind(densary,dens$y)

dimdens <- dim(densarx)
ssd <- mat.or.vec(length(ylabels),length(ylabels))
for(i in 1:ylen) {
  for(j in 1:ylen) {
    if(i != j) {
      ssd[i,j] <- wilcox.test(densary[,i], densary[,j],alternative="greater")$p.value#1 - ks.boot(densary[,i], densary[,j],1000)$ks.boot.pvalue#sum(abs(densary[,i] - densary[,j])^2)
    }
    else {
      ssd[i,j] <- 1
    }
  }
}

rownames(ssd) <- ylabels
colnames(ssd) <- ylabels

#pdf("sumofsquaredifferences_self_exps.pdf",width=8.5,height=11)
#heatmap.2(log(ssd+0.000001,2))
pdf("wrs_pval_self_exps.pdf",width=8.5,height=11)
heatmap.2(ssd,dendrogram ="none",Rowv=F,Colv=F)
dev.off(2)


dens <- density(data_expr$TFfreq,from=0,to=1)
densarx <- cbind(dens$x)
densary <- cbind(dens$y)
dens <- density(data_expr_TF$TFfreq,from=0,to=1)
densarx <- cbind(densarx,dens$x)
densary <- cbind(densary,dens$y)
dens <- density(data_expr_feat$TFfreq,from=0,to=1)
densarx <- cbind(densarx,dens$x)
densary <- cbind(densary,dens$y)
dens <- density(data_expr_inter$TFfreq,from=0,to=1)
densarx <- cbind(densarx,dens$x)
densary <- cbind(densary,dens$y)
dens <- density(data_expr_TF_inter$TFfreq,from=0,to=1)
densarx <- cbind(densarx,dens$x)
densary <- cbind(densary,dens$y)
dens <- density(data_expr_TF_inter_unmerged$TFfreq,from=0,to=1)
densarx <- cbind(densarx,dens$x)
densary <- cbind(densary,dens$y)
dens <- density(data_expr_TF_inter_feat$TFfreq,from=0,to=1)
densarx <- cbind(densarx,dens$x)
densary <- cbind(densary,dens$y)
dens <- density(data_MASTER$TFfreq,from=0,to=1)
densarx <- cbind(densarx,dens$x)
densary <- cbind(densary,dens$y)
dens <- density(data2DHCL$TFfreq,from=0,to=1)
densarx <- cbind(densarx,dens$x)
densary <- cbind(densary,dens$y)

dimdens <- dim(densarx)
ssd <- mat.or.vec(length(ylabels),length(ylabels))
for(i in 1:ylen) {
  for(j in 1:ylen) {
    if(i != j) {
      ssd[i,j] <- wilcox.test(densary[,i], densary[,j],alternative="greater")$p.value#1 - ks.boot(densary[,i], densary[,j],1000)$ks.boot.pvalue#ssd[i,j] <- sum(abs(densary[,i] - densary[,j])^2)
    }
    else {
      ssd[i,j] <- 1
    }
  }
}

rownames(ssd) <- ylabels
colnames(ssd) <- ylabels

#pdf("sumofsquaredifferences_self_TFfreq.pdf",width=8.5,height=11)
#heatmap.2(log(ssd+0.000001,2))
#dev.off(2)
pdf("wrs_pval_self_TFfreq.pdf",width=8.5,height=11)
heatmap.2(ssd,dendrogram ="none",Rowv=F,Colv=F)
dev.off(2)


dens <- density(data_expr$full_crit,from=0,to=3)
densarx <- cbind(dens$x)
densary <- cbind(dens$y)
dens <- density(data_expr_TF$full_crit,from=0,to=3)
densarx <- cbind(densarx,dens$x)
densary <- cbind(densary,dens$y)
dens <- density(data_expr_feat$full_crit,from=0,to=3)
densarx <- cbind(densarx,dens$x)
densary <- cbind(densary,dens$y)
dens <- density(data_expr_inter$full_crit,from=0,to=3)
densarx <- cbind(densarx,dens$x)
densary <- cbind(densary,dens$y)
dens <- density(data_expr_TF_inter$full_crit,from=0,to=3)
densarx <- cbind(densarx,dens$x)
densary <- cbind(densary,dens$y)
dens <- density(data_expr_TF_inter_unmerged$full_crit,from=0,to=3)
densarx <- cbind(densarx,dens$x)
densary <- cbind(densary,dens$y)
dens <- density(data_expr_TF_inter_feat$full_crit,from=0,to=3)
densarx <- cbind(densarx,dens$x)
densary <- cbind(densary,dens$y)
dens <- density(data_MASTER$full_crit,from=0,to=3)
densarx <- cbind(densarx,dens$x)
densary <- cbind(densary,dens$y)
dens <- density(data2DHCL$full_crit,from=0,to=3)
densarx <- cbind(densarx,dens$x)
densary <- cbind(densary,dens$y)

dimdens <- dim(densarx)
ssd <- mat.or.vec(length(ylabels),length(ylabels))
for(i in 1:ylen) {
  for(j in 1:ylen) {
    if(i != j) {
      ssd[i,j] <- wilcox.test(densary[,i], densary[,j],alternative="greater")$p.value#1 - ks.boot(densary[,i], densary[,j],1000)$ks.boot.pvalue#sum(abs(densary[,i] - densary[,j])^2)
    }
    else {
      ssd[i,j] <- 1
    }
  }
}

rownames(ssd) <- ylabels
colnames(ssd) <- ylabels

#pdf("sumofsquaredifferences_self_full_crit.pdf",width=8.5,height=11)
#heatmap.2(log(ssd+0.000001,2))
#dev.off(2)
pdf("wrs_pval_self_fullcrit.pdf",width=8.5,height=11)
heatmap.2(ssd,dendrogram ="none",Rowv=F,Colv=F)
dev.off(2)


dens <- density(data_expr$exp_mean,from=0,to=3.5)
densarx <- cbind(dens$x)
densary <- cbind(dens$y)
dens <- density(data_expr_TF$exp_mean,from=0,to=3.5)
densarx <- cbind(densarx,dens$x)
densary <- cbind(densary,dens$y)
dens <- density(data_expr_feat$exp_mean,from=0,to=3.5)
densarx <- cbind(densarx,dens$x)
densary <- cbind(densary,dens$y)
dens <- density(data_expr_inter$exp_mean,from=0,to=3.5)
densarx <- cbind(densarx,dens$x)
densary <- cbind(densary,dens$y)
dens <- density(data_expr_TF_inter$exp_mean,from=0,to=3.5)
densarx <- cbind(densarx,dens$x)
densary <- cbind(densary,dens$y)
dens <- density(data_expr_TF_inter_unmerged$exp_mean,from=0,to=3.5)
densarx <- cbind(densarx,dens$x)
densary <- cbind(densary,dens$y)
dens <- density(data_expr_TF_inter_feat$exp_mean,from=0,to=3.5)
densarx <- cbind(densarx,dens$x)
densary <- cbind(densary,dens$y)
dens <- density(data_MASTER$exp_mean,from=0,to=3.5)
densarx <- cbind(densarx,dens$x)
densary <- cbind(densary,dens$y)
dens <- density(data2DHCL$exp_mean,from=0,to=3.5)
densarx <- cbind(densarx,dens$x)
densary <- cbind(densary,dens$y)

dimdens <- dim(densarx)
ssd <- mat.or.vec(length(ylabels),length(ylabels))
for(i in 1:ylen) {
  for(j in 1:ylen) {
    if(i != j) {
      ssd[i,j] <- wilcox.test(densary[,i], densary[,j],alternative="greater")$p.value#1 - ks.boot(densary[,i], densary[,j],1000)$ks.boot.pvalue#ssd[i,j] <- sum(abs(densary[,i] - densary[,j])^2)
    }
    else {
      ssd[i,j] <- 1
    }
  }
}

rownames(ssd) <- ylabels
colnames(ssd) <- ylabels

#pdf("sumofsquaredifferences_self_exp_mean.pdf",width=8.5,height=11)
#heatmap.2(log(ssd+0.000001,2))
#dev.off(2)
pdf("wrs_pval_self_exp_mean.pdf",width=8.5,height=11)
heatmap.2(ssd,dendrogram ="none",Rowv=F,Colv=F)
dev.off(2)



###
###
###pairwise validation label analysis

setwd("~/Documents/integr8_genom/Miner/miner_results/yeast/EXPR/1.0expr_0.75score/")

###all
data <- read.table("GO_vs_Path.txt",sep="\t",header=T)
rownames(data) <- data[,1]
data <- data[,-1]
pdf("GO_vs_Path_heatmap.pdf",width=8.5,height=11)
heatmap(as.matrix(log(data,10)),scale="none",margins=c(15,15),cexRow=0.6,cexCol=0.6)
dev.off(2)

data <- read.table("GO_vs_TF.txt",sep="\t",header=T)
rownames(data) <- data[,1]
data <- data[,-1]
pdf("GO_vs_TF_heatmap.pdf",width=8.5,height=11)
heatmap(as.matrix(log(data,10)),scale="none",margins=c(15,15),cexRow=0.6,cexCol=0.6)
dev.off(2)

data <- read.table("Path_vs_TF.txt",sep="\t",header=T)
rownames(data) <- data[,1]
data <- data[,-1]
pdf("Path_vs_TF_heatmap.pdf",width=8.5,height=11)
heatmap(as.matrix(log(data,10)),scale="none",margins=c(15,15),cexRow=0.6,cexCol=0.6)
dev.off(2)



###
###
###no translation related
remove <- c("none","translation","ribosome","ribosome biogenesis")

data <- read.table("GO_vs_Path.txt",sep="\t",header=T)
rownames(data) <- data[,1]
data <- data[,-1]
rowrem <- mat.or.vec(1,0)
for(i in 1:length(remove)) {
  ind <- which(rownames(data) == remove[i])
  print(ind)
  if(length(ind) > 0) {
    rowrem <- c(rowrem, ind)
  }
}
data <- data[-rowrem,]

colrem <- mat.or.vec(1,0)
for(i in 1:length(remove)) {
  ind <- which(colnames(data) == remove[i])
  print(ind)
  if(length(ind) > 0) {
    colrem <- c(colrem, ind)
  }
}
data <- data[,-colrem]

pdf("GO_vs_Path_heatmap_notranslation.pdf",width=8.5,height=11)
heatmap(as.matrix(log(data,10)),scale="none",margins=c(15,15),cexRow=0.6,cexCol=0.6)
dev.off(2)


data <- read.table("GO_vs_TF.txt",sep="\t",header=T)
rownames(data) <- data[,1]
data <- data[,-1]
rowrem <- mat.or.vec(1,0)
for(i in 1:length(remove)) {
  ind <- which(rownames(data) == remove[i])
  print(ind)
  if(length(ind) > 0) {
    rowrem <- c(rowrem, ind)
  }
}
data <- data[-rowrem,]

colrem <- mat.or.vec(1,0)
for(i in 1:length(remove)) {
  ind <- which(colnames(data) == remove[i])
  print(ind)
  if(length(ind) > 0) {
    colrem <- c(colrem, ind)
  }
}
data <- data[,-colrem]

pdf("GO_vs_TF_heatmap_notranslation.pdf",width=8.5,height=11)
heatmap(as.matrix(log(data,10)),scale="none",margins=c(15,15),cexRow=0.6,cexCol=0.6)
dev.off(2)



data <- read.table("Path_vs_TF.txt",sep="\t",header=T)
rownames(data) <- data[,1]
data <- data[,-1]
rowrem <- mat.or.vec(1,0)
for(i in 1:length(remove)) {
  ind <- which(rownames(data) == remove[i])
  print(ind)
  if(length(ind) > 0) {
    rowrem <- c(rowrem, ind)
  }
}
data <- data[-rowrem,]

colrem <- mat.or.vec(1,0)
for(i in 1:length(remove)) {
  ind <- which(colnames(data) == remove[i])
  print(ind)
  if(length(ind) > 0) {
    colrem <- c(colrem, ind)
  }
}
data <- data[,-colrem]

pdf("Path_vs_TF_heatmap_notranslation.pdf",width=8.5,height=11)
heatmap(as.matrix(log(data,10)),scale="none",margins=c(15,15),cexRow=0.6,cexCol=0.6)
dev.off(2)



