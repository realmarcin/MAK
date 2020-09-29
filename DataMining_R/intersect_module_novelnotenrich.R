rm(list=ls())
setwd("~/Documents/integr8_genom/Miner/miner_results/yeast/EXPR_round12345_merge_refine/")
modules <- read.table("./moduland3/EXPR_round12345_merge_refine/modules.csv",sep=",",header=T,skip=11)
novelnotenrich <- read.table("./EXPR_round12345_merge_refine_newbiclusters/novel_notenriched.txt")
summary <- read.table("results_yeast_cmonkey_1_expr_MSEC_KendallC_GEECE_refine12345_top_0.25_1.0_c_liven_reconstructed_pval0.001_cut_0.01_summary_forR.txt", sep="\t",comment="@",header=T)
colnames(summary) <- colnames(summary)[-1]
vbl <- read.table("results_yeast_cmonkey_1_expr_MSEC_KendallC_GEECE_refine12345_top_0.25_1.0_c_liven_reconstructed.txt", sep="\t",comment="@",header=T)



table(modules[intersect(as.vector(novelnotenrich[,1]), as.vector(modules[,1])),2])

intersect <- modules[modules[,1] %in% intersect(as.vector(novelnotenrich[,1]), as.vector(modules[,1])),]

notbigintersect <- intersect[intersect[,2] != 5 & intersect[,2] != 43 & intersect[,2] != 54,]

vbl$num_genes[as.numeric(notbigintersect[,1])]

expcrit <- (vbl$expr_mean_crit.1 + vbl$expr_reg_crit + vbl$expr_kend_crit)/3
plot(vbl$num_genes[as.numeric(notbigintersect[,1])], expcrit[as.numeric(notbigintersect[,1])])


max(expcrit[as.numeric(notbigintersect[,1])] )
[1] 0.9870055
notbigintersect[24,]

> notbigintersect
    node.ID the.ID.of.the.module.where.the.node.mostly.belongs
13      266                                                 74
16      269                                                 87
20      262                                                 79
22      238                                                227
27      231                                                231
34      249                                                 87
38      245                                                 87
43      190                                                 87
54      298                                                 87
60      296                                                296
63      294                                                 87
78      105                                                 87
87      275                                                 87
92      270                                                 87
97      282                                                 87
98      283                                                 79
101     280                                                 87
102     281                                                231
106     140                                                 74
170     300                                                 87
187     110                                                 87
212     324                                                 87
238     311                                                 79
254     179                                                 87
255     314                                                 87
258     214                                                 74
328     330                                                231
> table(notbigintersect[,2])

 74  79  87 227 231 296 
  3   3  16   1   3   1 
> ls()
[1] "intersect"       "modules"         "notbigintersect" "novelnotenrich" 
> table(modules[,2])

  5  30  32  43  44  54  74  79  80  87  92 110 158 168 202 227 231 237 296 
122  10   1  70   3  70   8   9   1  19   1   1   2   2   3   2   6   2   2 


notbigintersect_rows_87 <- notbigintersect[notbigintersect[,2]==87,1]




vblraw <- readLines("./results_yeast_cmonkey_1_expr_MSEC_KendallC_GEECE_refine12345_top_0.25_1.0_c_liven_reconstructed.txt")
summaryraw <- readLines("./results_yeast_cmonkey_1_expr_MSEC_KendallC_GEECE_refine12345_top_0.25_1.0_c_liven_reconstructed_pval0.001_cut_0.01_summary.txt")

vblsplit <- sapply(vblraw, function(x) strsplit(x,"\t"))
vbl <- matrix(unlist(vblsplit), ncol = 23, byrow = TRUE)
colnames(vbl) <- vbl[1,]
vbl <- vbl[-1,]

summarysplit <-  sapply(summaryraw, function(x) strsplit(x,"\t"))
summary <- matrix(unlist(summarysplit), ncol = 28, byrow = TRUE)
colnames(summary) <- summary[1,]
summary <- summary[-1,]


summary[notbigintersect_rows,]

#broken
summary$full_crit[notbigintersect_rows] == vbl$full_crit[notbigintersect_rows]


write.table(vbl[notbigintersect[notbigintersect[,2]==87,1],], file="netmod87.vbl",sep="\t",row.names=F)
