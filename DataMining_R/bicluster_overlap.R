
#setwd("/Users/marcin/Documents/integr8_genom/Miner/miner_results/yeast/EXPR_round12345_merge_refine")
setwd("/Users/marcin/Documents/integr8_genom/Miner/miner_results/yeast/final_paper_results/")

datag <- read.table("./MAK_0.25_vs_MAK_0.25.txt_gene.txt",sep="\t",header=T,row.names=1)
datae <- read.table("./MAK_0.25_vs_MAK_0.25.txt_exp.txt",sep="\t",header=T,row.names=1)

cmondatag <- read.table("./cmonkey_0.5_vs_cmonkey_0.5.txt_gene.txt",sep="\t",header=T,row.names=1)
cmondatae <- read.table("./cmonkey_0.5_vs_cmonkey_0.5.txt_exp.txt",sep="\t",header=T,row.names=1)

coaldatag <- read.table("./coalesce_0.5_vs_cmonkey_0.5.txt_gene.txt",sep="\t",header=T,row.names=1)
coaldatae <- read.table("./coalesce_0.5_vs_cmonkey_0.5.txt_exp.txt",sep="\t",header=T,row.names=1)



minmax(datag, datae)

minmax(cmondatag, cmondatae)

minmax(coaldatag, coaldatae)

mean(datag[lower.tri(datag,diag=FALSE)])
mean(datae[lower.tri(datae,diag=FALSE)])

mean(cmondatag[lower.tri(cmondatag,diag=FALSE)])
mean(cmondatae[lower.tri(cmondatae,diag=FALSE)])

mean(coaldatag[lower.tri(coaldatag,diag=FALSE)])
mean(coaldatae[lower.tri(coaldatae,diag=FALSE)])




minmax <- function(datag, datae){
maxg <- max(datag[lower.tri(datag,diag=FALSE)])
ming <- min(datag[lower.tri(datag,diag=FALSE)])

maxe <- max(datae[lower.tri(datae,diag=FALSE)])
mine <- min(datae[lower.tri(datae,diag=FALSE)])

maxindicesg <- which(datag == maxg, arr.in=TRUE)
minindicesg <- which(datag == ming, arr.in=TRUE)
datae[maxindicesg]
datae[minindicesg]

maxindicese <- which(datae == maxe, arr.in=TRUE)
minindicese <- which(datae == mine, arr.in=TRUE)
datag[maxindicese]
datag[minindicese]

c(maxg, ming, datae[maxindicesg][1], datae[minindicesg][1], maxe, mine, datag[maxindicese][1], datag[minindicese][1])

}


###
########OLD
###

[1] "cMonkey_yeast_MSEC_KendallC_GEECE_inter_feat_0.25_1.0_c_liven_reconstructed.txt_max"                     
[2] "ISAyeastcmonkey_MSEC_KendallC_GEECE_inter_feat_0.25_1.0_c_reconstructed.txt_max"                         
[3] "motifs_summary.txt_MSEC_KendallC_GEECE_inter_feat_0.25_1.0_c_reconstructed.txt_max"                      
[4] "yeast_cmonkey_expr_FABIA_reformat_MSEC_KendallC_GEECE_inter_feat.txt_max"                                
[5] "yeast_cmonkey_STARTS_abs_MSEC_KendallC_GEECE_inter_feat_cut_expr1.0_0.0_0.25_1.0_c_reconstructed.txt_max"


indices <- c(6,9,12,15,18)
maxAll <- max(data[,indices])
plot(density(data[,6]), col="black",xlim=c(0,maxAll), ylim=c(0,40))
lines(density(data[,9]), col="red")
lines(density(data[,12]), col="green")
lines(density(data[,15]), col="blue")
lines(density(data[,18]), col="gray")

max <- apply(data[,indices],1, max)
names(max) <- row.names(data)
max <- apply(data[,indices],1, max)
sort(max, decreasing=FALSE)

write.table(sort(max, decreasing=FALSE),"./MAK_expr_max_overlap_g.txt",sep="\t")


f <- "./EXPR_round12345_merge_refine/results_yeast_cmonkey_1_expr_MSEC_KendallC_GEECE_refine12345_top_0.25_1.0_c_liven_reconstructed_pval0.001_cut_0.01_summary.txt"
  x <- readLines(f)
  y <- gsub( "index", "name", x )
  cat(y, file=f, sep="\n")
annot <- read.table(f,sep="\t",header=T,row.names=1)
annot[which(max < 0.2),c(10,12,15,16)]
annot_unknown <- annot[which(max < 0.2),]
write.table(annot_unknown,"./MAK_expr_max_overlap_g.new_summary.txt",sep="\t")

f2 <- "./EXPR_round12345_merge_refine/results_yeast_cmonkey_1_expr_MSEC_KendallC_GEECE_refine12345_top_0.25_1.0_c_liven_reconstructed.txt"
  x2 <- readLines(f2)
  y2 <- gsub( "index", "name", x2 )
  cat(y2, file=f2, sep="\n")
vbl <- read.table(f2,sep="\t",header=T,comment="~")
vbl_unknown <- vbl[which(max < 0.2),]
write.table(vbl_unknown[,-1],"./MAK_expr_max_overlap_g_new.vbl",sep="\t")
