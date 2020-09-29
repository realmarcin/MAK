setwd("~/Documents/integr8_genom/Miner/miner_results/yeast/OVERLAPS/")

method <- "MAK"#"cmonkey"#"MAK "COALESCE"

if(method == "MAK") {
data <- read.table("./results_yeast_cmonkey_1_expr_MSEC_KendallC_GEECE_refine12345_top_0.25_1.0_c_liven_reconstructed.txt_vs_gene_0.2_overlaps.txt",sep="\t",header=T)
#data <- read.table("./results_yeast_cmonkey_1_expr_MSEC_KendallC_GEECE_refine12345_top_0.25_1.0_c_liven_reconstructed.txt_vs_gene_0.2_overlaps.txt",sep="\t",header=T)  
} else if(method == "cmonkey") { data <- read.table("./cMonkey_yeast_MSEC_KendallC_GEECE_inter_feat_0.25_1.0_c_liven_reconstructed.txt_vs_gene_0.2_overlaps.txt",sep="\t",header=T)
} else if(method == "COALESCE") {data <- read.table("./motifs_summary.txt_MSEC_KendallC_GEECE_inter_feat_0.25_1.0_c_liven_reconstructed.txt_vs_gene_0.2_overlaps.txt",sep="\t",header=T) }

if(method == "MAK") {                       
vbl <- read.table("../EXPR_round12345_merge_refine/results_yeast_cmonkey_1_expr_MSEC_KendallC_GEECE_refine12345_top_0.25_1.0_c_liven_reconstructed.txt",sep="\t",header=T,comment="@")
colnames(vbl)[1] <- "#name" 
} else if(method == "cmonkey") { vbl <- read.table("../OUT_cmonkey/cMonkey_yeast_MSEC_KendallC_GEECE_inter_feat_0.25_1.0_c_liven_reconstructed.txt",sep="\t",header=T,comment="@")
colnames(vbl)[1] <- "#name" 
} else if(method == "COALESCE") { vbl <- read.table("../OUT_COALESCE/motifs_summary.txt_MSEC_KendallC_GEECE_inter_feat_0.25_1.0_c_liven_reconstructed.txt",sep="\t",header=T,comment="@")
colnames(vbl)[1] <- "#name" }

if(method == "MAK") {
summary  <- read.table("../EXPR_round12345_merge_refine/results_yeast_cmonkey_1_expr_MSEC_KendallC_GEECE_refine12345_top_0.25_1.0_c_liven_reconstructed_pval0.001_cut_0.01_summary.txt",sep="\t",header=T,comment="@")
colnames(summary)[1] <- "#name"
} else if(method == "cmonkey") { summary  <- read.table("../OUT_cmonkey/cMonkey_yeast_MSEC_KendallC_GEECE_inter_feat_0.25_1.0_c_liven_reconstructed_pval0.001_cut_0.01_summary.txt",sep="\t",header=T,comment="@")
colnames(summary)[1] <- "#name"
} else if(method == "COALESCE") { summary  <- read.table("../OUT_COALESCE/motifs_summary.txt_MSEC_KendallC_GEECE_inter_feat_0.25_1.0_c_liven_reconstructed_pval0.001_cut_0.01_summary.txt",sep="\t",header=T,comment="@")
colnames(summary)[1] <- "#name"}

if(method == "MAK") {
modules <- read.table("../EXPR_round12345_merge_refine/moduland3/modules.csv",sep=",",header=T,skip=11)
#table(modules[intersect(as.vector(novelnotenrich[,1]), as.vector(modules[,1])),2])
} else if(method == "cmonkey") {  modules <- read.table("../OUT_cmonkey/moduland/cMonkey_yeast_MSEC_KendallC_GEECE_inter_feat_0.25_1.0_c_liven_reconstructed_pval0.001_cut_0.01__member_graph_gene-0_modules.csv",sep=",",header=T,skip=11) 
} else if(method == "COALESCE") { modules <- NULL }#read.table("../OUT_COALESCE/moduland/modules.csv",sep=",",header=T,skip=11) 
                                  
if(method == "MAK") {
nosigTF <- read.table("../EXPR_round12345_merge_refine/bicluster_dash_all_new/nosigTF_labels.txt",sep="\t",header=F)
} else if(method == "cmonkey") { nosigTF <- read.table("../OUT_cmonkey/bicluster_dash/nosigTF_labels.txt",sep="\t",header=F)
} else if(method == "COALESCE") { nosigTF <- read.table("../OUT_COALESCE/bicluster_dash/nosigTF_labels.txt",sep="\t",header=F) }
                                  
                                  
indexnovel <- which(data[,6] < 0.2 & data[,9] < 0.2 & data[,12] < 0.2)
table(modules[indexnovel,2])
             
count <- table(modules[indexnovel,2])
sum(count) - sum(count[-c(1, 4, 6)])

which(summary[,11] == 1  & summary[,13] == 1 & summary[,15] == 1  & summary[,17] == 1 )

dim <- dim(summary)

sigTFdata <- seq(1,dim[1],1)
sigTFdata <- sigTFdata[-as.numeric(as.vector(unlist(nosigTF[,1]))[-1])]

sigTF <- rep(1,dim[1])
sigTF[sigTFdata] <- 0

indexnovelnotenrichTF <- which(data[,6] < 0.2 & data[,9] < 0.2 & data[,12] < 0.2 & summary[,16] == 1 )
indexnovelnotenrichTFboth <- which(data[,6] < 0.2 & data[,9] < 0.2 & data[,12] < 0.2 & summary[,16] == 1 & sigTF == 1 )
indexnovelenrichTF <- which(data[,6] < 0.2 & data[,9] < 0.2 & data[,12] < 0.2 & (summary[,16] != 1 | sigTF != 1))
indexnovelnotenrichfunc <- which(data[,6] < 0.2 & data[,9] < 0.2 & data[,12] < 0.2 & summary[,10] == 1  & summary[,12] == 1 & summary[,14] == 1)
indexnovelnotenrich <- which(data[,6] < 0.2 & data[,9] < 0.2 & data[,12] < 0.2 & summary[,10] == 1  & summary[,12] == 1 & summary[,14] == 1  & summary[,16] == 1 )
indexnovelnotenrichfuncandTF <- which(data[,6] < 0.2 & data[,9] < 0.2 & data[,12] < 0.2 & summary[,10] == 1  & summary[,12] == 1 & summary[,14] == 1  & summary[,16] == 1  & sigTF == 1)

indexnovelenrichfuncandTF <- which(data[,6] < 0.2 & data[,9] < 0.2 & data[,12] < 0.2 & (summary[,10] != 1  | summary[,12] != 1 | summary[,14] != 1)  & (summary[,16] != 1 | sigTF != 1))


enriched <- which(summary[,10] != 1 | summary[,12] != 1 | summary[,14] != 1 | summary[,16] != 1)#  & sigTF != 1)

table(modules[indexnovelnotenrich,2])

count2 <- table(modules[indexnovelnotenrich,2])
sum(count2) - sum(count2[-c(1, 4, 6)])

count2 <- table(modules[indexnovelnotenrichfuncandTF,2])
sum(count2) - sum(count2[-c(1, 4, 6)])

length(intersect(as.vector(modules[,1]), vbl[which(data[,6] < 0.2 & data[,9] < 0.2 & data[,12] < 0.2), 1]))
table(modules[,2])

#214 = novel not enriched
#230 and 125 = novel

write.table(vbl[indexnovel, ],file="MAK_expr_less0.2_overlap_v2.vbl",row.names=F,sep="\t")
write.table(summary[indexnovel, ],file="MAK_expr_less0.2_overlap_summary_v2.txt",row.names=F,sep="\t")


nosigTFindex <- as.numeric(as.matrix(nosigTF)[-1,1])

exprcrit <- (summary[,21] + summary[,22] + summary[,23]) / 3
sigTF <- summary[,1][-nosigTFindex]
notnovel <- summary[,1][-indexnovel]



###test novel vs rest etc.
wilcox.test(exprcrit[indexnovelenrichTF], exprcrit[indexnovelnotenrichTFboth], alternative="greater")
0.001173
wilcox.test(summary[,7][indexnovelenrichTF], summary[,7][indexnovelnotenrichTFboth], alternative="greater")
0.2815
wilcox.test(summary[,3][indexnovelenrichTF], summary[,3][indexnovelnotenrichTFboth], alternative="greater")
5.974e-09
wilcox.test(summary[,4][indexnovelenrichTF], summary[,4][indexnovelnotenrichTFboth], alternative="greater")
0.003847

wilcox.test(exprcrit[notnovel], exprcrit[indexnovel], alternative="greater")
p-value < 2.2e-16
wilcox.test(summary[,7][notnovel], summary[,7][indexnovel], alternative="greater")
4.466e-15
wilcox.test(summary[,3][notnovel], summary[,3][indexnovel], alternative="greater")
p-value < 2.2e-16
wilcox.test(summary[,4][notnovel], summary[,4][indexnovel], alternative="greater")
p-value < 2.2e-16

cyto <- grep("cytoplasmic translation", summary[,13])
mito <- grep("mitochondrial translation", summary[,13])



###combo reg
comboreg <- c(15,25,34,61)

max(c(data[comboreg,6], data[comboreg,9], data[comboreg,12]))


> length(unlist(genesind[15]))
[1] 281
> length(unlist(genesind[25]))
[1] 245
> length(unlist(genesind[34]))
[1] 178
> length(unlist(genesind[61]))
[1] 247

25_61  0.2017371469441068
15_34  0.3229254218072917
15_25  0.31674428170371177

blockids <- as.character(vbl[,3])
genes <- sapply(blockids, function(x) unlist(strsplit(x,"/"))[1])
exps <- sapply(blockids, function(x) unlist(strsplit(x,"/"))[2])

genesind <-  sapply(genes, function(x) unlist(strsplit(x,",")))

inter2561 <- intersect(unlist(genesind[25]),unlist(genesind[61]))
73
inter1534 <- intersect(unlist(genesind[15]),unlist(genesind[34]))
105
inter1525 <- intersect(unlist(genesind[15]),unlist(genesind[25]))
113
which2561 <- which(unlist(genesind[25]) %in% inter2561)
which1534 <- which(unlist(genesind[15]) %in% inter1534)
which1525 <- which(unlist(genesind[15]) %in% inter1525)

TF15 <- readLines("~/Documents/integr8_genom/Miner/miner_results/yeast/EXPR_round12345_merge_refine/bicluster_dash_all_new/results_yeast_cmonkey_1_expr_MSEC_KendallC_GEECE_refine12345_top_0.25_1.0_c_liven_reconstructed.txt__14_TFbind.txt")
TF25 <- readLines("~/Documents/integr8_genom/Miner/miner_results/yeast/EXPR_round12345_merge_refine/bicluster_dash_all_new/results_yeast_cmonkey_1_expr_MSEC_KendallC_GEECE_refine12345_top_0.25_1.0_c_liven_reconstructed.txt__25_TFbind.txt")
TF34 <- readLines("~/Documents/integr8_genom/Miner/miner_results/yeast/EXPR_round12345_merge_refine/bicluster_dash_all_new/results_yeast_cmonkey_1_expr_MSEC_KendallC_GEECE_refine12345_top_0.25_1.0_c_liven_reconstructed.txt__34_TFbind.txt")
TF61 <- readLines("~/Documents/integr8_genom/Miner/miner_results/yeast/EXPR_round12345_merge_refine/bicluster_dash_all_new/results_yeast_cmonkey_1_expr_MSEC_KendallC_GEECE_refine12345_top_0.25_1.0_c_liven_reconstructed.txt__61_TFbind.txt")

grep("MSN4.*HAP4", TF25[which2561])
24 34 46
grep("MSN4.*HAP4", TF15[which1534])
55
grep("MSN4.*HAP4", TF15[which1525])
54 56
