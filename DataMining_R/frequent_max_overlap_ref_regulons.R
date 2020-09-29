setwd("/auto/sahara/namib/home/marcin/integr8functgenom/Miner/miner_results/yeast/")


splitlabels <- read.table("./REGULONS_ABS/regulon_clusters_yeastract_split_label.txt",sep="",header=F)

#regulon_clusters_yeastract_split_label.txt


#dataISAdist <- read.table("./ISA_vs_ref.txt_gene.txt",sep="\t",header=F)

dataISA <- read.table("./OUT_ISA/ISAyeastcmonkey_MSEC_KendallC_GEECE_inter_feat_0.25_1.0_c_reconstructed.txt_0.0_geneenrich_pvals_stats.txt",sep="\t",header=F)
colnames(dataISA) <- c("j","i","genesj","genesi","over")
#dataISA_agmax <- aggregate(dataISA[,5], by=list(dataISA$i), FUN=min, na.rm=TRUE)

dataISA_agmaxindex <- c()
uniqueISA <- unique(dataISA$i)
for(i in 1:length(uniqueISA)) {
index <- which(dataISA$i == uniqueISA[i])
max <- min(dataISA[index,5])
dataISA_agmaxindex <- c(dataISA_agmaxindex, which(dataISA[,5] == max & dataISA$i == uniqueISA[i]))
}

ISAlabels <- c()
for(i in 1:length(dataISA_agmaxindex)) {
ISAlabels <- c(ISAlabels, as.character(splitlabels[dataISA[dataISA_agmaxindex[i],1]+1,1]))
}
write.table(cbind(dataISA[dataISA_agmaxindex,],ISAlabels), file="ISA_minpval_regulon_hits.txt",sep="\t")

sort(table(dataISA[dataISA_agmaxindex$x,1]))
sort(table(dataISA[dataISA_agmaxindex$x,2]))


dataCOALESCE <- read.table("./OUT_COALESCE/motifs_summary.txt_MSEC_KendallC_GEECE_inter_feat_0.25_1.0_c_reconstructed.txt_0.0_geneenrich_pvals_stats.txt",sep="\t",header=F)
colnames(dataCOALESCE) <- c("j","i","genesj","genesi","over")
#dataCOALESCE_agmax <- aggregate(dataCOALESCE, by=list(dataCOALESCE$i), FUN=min, na.rm=TRUE)
#dataCOALESCE_agmaxindex <- aggregate(dataCOALESCE[,5], by=list(dataCOALESCE$i), FUN=which.max)

dataCOALESCE_agmaxindex <- c()
uniqueCOALESCE <- unique(dataCOALESCE$i)
for(i in 1:length(uniqueCOALESCE)) {
index <- which(dataCOALESCE$i == uniqueCOALESCE[i])
max <- min(dataCOALESCE[index,5])
dataCOALESCE_agmaxindex <- c(dataCOALESCE_agmaxindex, which(dataCOALESCE[,5] == max & dataCOALESCE$i == uniqueCOALESCE[i]))
}

COALESCElabels <- c()
for(i in 1:length(dataCOALESCE_agmaxindex)) {
COALESCElabels <- c(COALESCElabels, as.character(splitlabels[dataCOALESCE[dataCOALESCE_agmaxindex[i],1]+1,1]))
}
write.table(cbind(dataCOALESCE[dataCOALESCE_agmaxindex,],COALESCElabels), file="COALESCE_minpval_regulon_hits.txt",sep="\t")

sort(table(dataCOALESCE[dataCOALESCE_agmaxindex$x,1]))
sort(table(dataCOALESCE[dataCOALESCE_agmaxindex$x,2]))


datacmonkey <- read.table("./OUT_cmonkey/cMonkey_yeast_MSEC_KendallC_GEECE_inter_feat_0.25_1.0_c_liven_reconstructed.txt_0.0_geneenrich_pvals_stats.txt",sep="\t",header=F)
colnames(datacmonkey) <- c("j","i","genesj","genesi","over")
#datacmonkey_agmax <- aggregate(datacmonkey, by=list(datacmonkey$i), FUN=min, na.rm=TRUE)
#datacmonkey_agmaxindex <- aggregate(datacmonkey[,5], by=list(datacmonkey$i), FUN=which.max)

datacmonkey_agmaxindex <- c()
uniquecmonkey <- unique(datacmonkey$i)
for(i in 1:length(uniquecmonkey)) {
index <- which(datacmonkey$i == uniquecmonkey[i])
max <- min(datacmonkey[index,5])
datacmonkey_agmaxindex <- c(datacmonkey_agmaxindex, which(datacmonkey[,5] == max & datacmonkey$i == uniquecmonkey[i]))
}

cmonkeylabels <- c()
for(i in 1:length(datacmonkey_agmaxindex)) {
cmonkeylabels <- c(cmonkeylabels, as.character(splitlabels[datacmonkey[datacmonkey_agmaxindex[i],1]+1,1]))
}
write.table(cbind(datacmonkey[datacmonkey_agmaxindex,],cmonkeylabels), file="cmonkey_minpval_regulon_hits.txt",sep="\t")

sort(table(datacmonkey[datacmonkey_agmaxindex$x,1]))
sort(table(datacmonkey[datacmonkey_agmaxindex$x,2]))


datamak <- read.table("./EXPR_round12345_merge_refine/results_yeast_cmonkey_1_expr_MSEC_KendallC_GEECE_refine12345_top_0.25_1.0_c_liven_reconstructed.txt_0.0_geneenrich_pvals_stats.txt",sep="\t",header=F)
colnames(datamak) <- c("j","i","genesj","genesi","over")
#datamak_agmax <- aggregate(datamak, by=list(datamak$i), FUN=min, na.rm=TRUE)
#datamak_agmaxindex <- aggregate(datamak[,5], by=list(datamak$i), FUN=which.max)

datamak_agmaxindex <- c()
uniquemak <- unique(datamak$i)
for(i in 1:length(uniquemak)) {
index <- which(datamak$i == uniquemak[i])
max <- min(datamak[index,5])
datamak_agmaxindex <- c(datamak_agmaxindex, which(datamak[,5] == max & datamak$i == uniquemak[i]))
}

maklabels <- c()
for(i in 1:length(datamak_agmaxindex)) {
maklabels <- c(maklabels, as.character(splitlabels[datamak[datamak_agmaxindex[i],1]+1,1]))
}
write.table(cbind(datamak[datamak_agmaxindex,],maklabels), file="mak_minpval_regulon_hits.txt",sep="\t")

sort(table(datamak[datamak_agmaxindex$x,1]))
sort(table(datamak[datamak_agmaxindex$x,2]))


###compare ref regulon output
dataISAhits <- read.table("./ISA_minpval_regulon_hits.txt",sep="\t",header=T)
dataISAhitssig <-dataISAhits
test <- length(which(dataISAhitssig[,5] > 0.01))
if(test > 0) {
dataISAhitssig <- dataISAhitssig[-which(dataISAhitssig[,5] > 0.01),]
}

dataCOALESCEhits <- read.table("./COALESCE_minpval_regulon_hits.txt",sep="\t",header=T)
dataCOALESCEhitssig <-dataCOALESCEhits
test <- length(which(dataCOALESCEhitssig[,5] > 0.01))
if(test > 0) {
dataCOALESCEhitssig <- dataCOALESCEhitssig[-which(dataCOALESCEhitssig[,5] > 0.01),]
}

datacmonkeyhits <- read.table("./cmonkey_minpval_regulon_hits.txt",sep="\t",header=T)
datacmonkeyhitssig <-datacmonkeyhits
datacmonkeyhitssig <- datacmonkeyhitssig[-which(datacmonkeyhitssig[,5] > 0.01),]

datamakhits <- read.table("./mak_minpval_regulon_hits.txt",sep="\t",header=T)
datamakhitssig <-datamakhits
datamakhitssig <- datamakhitssig[-which(datamakhitssig[,5] > 0.01),]



17
201
219
334


mak_vs_isa <- intersect(datamakhitssig[,6], dataISAhitssig[,6])
[1] "yeastract_KTR3_regulon_124_cluster_9_pos"   
[2] "yeastract_DUR1.2_regulon_127_cluster_26_pos"
[3] "yeastract_YAL064C.A_regulon_5_cluster_9_pos"
[4] "yeastract_ROT2_regulon_147_cluster_10_pos"

mak_vs_coalesce <- intersect(datamakhitssig[,6], dataCOALESCEhitssig[,6])
 [1] "yeastract_TAF5_regulon_117_cluster_7_neg"    
 [2] "yeastract_OM14_regulon_148_cluster_14_neg"   
 [3] "yeastract_YPT10_regulon_182_cluster_17_neg"  
 [4] "yeastract_RPS9B_regulon_108_cluster_10_neg"  
 [5] "yeastract_YAL064C.A_regulon_5_cluster_9_pos" 
 [6] "yeastract_ROT2_regulon_147_cluster_4_neg"    
 [7] "yeastract_AME1_regulon_130_cluster_21_pos"   
 [8] "yeastract_YBR235W_regulon_154_cluster_43_neg"
 [9] "yeastract_ABD1_regulon_155_cluster_13_pos"   
[10] "yeastract_PHO88_regulon_25_cluster_19_neg"   
[11] "yeastract_AIM4_regulon_113_cluster_28_pos"   
[12] "yeastract_YBR089W_regulon_8_cluster_17_pos"  
[13] "yeastract_YBP1_regulon_135_cluster_5_pos"    
[14] "yeastract_ARC40_regulon_153_cluster_46_neg"  
[15] "yeastract_TIM12_regulon_11_cluster_32_neg"   
[16] "yeastract_SLI15_regulon_75_cluster_39_pos"   
[17] "yeastract_NTC20_regulon_107_cluster_20_neg"  
[18] "yeastract_SEC66_regulon_90_cluster_30_neg"   
[19] "yeastract_GDT1_regulon_106_cluster_12_pos"   
[20] "yeastract_NPL4_regulon_89_cluster_16_neg"    
[21] "yeastract_YBR226C_regulon_144_cluster_33_pos"
[22] "yeastract_ATG12_regulon_136_cluster_10_pos"  
[23] "yeastract_ROT2_regulon_147_cluster_40_pos"   
[24] "yeastract_YAL064W_regulon_6_cluster_13_neg"  
[25] "yeastract_SEO1_regulon_2_cluster_6_pos"

mak_vs_cmonkey <- intersect(datamakhitssig[,6], datacmonkeyhitssig[,6])
 [1] "yeastract_RPL21A_regulon_110_cluster_45_pos" 
 [2] "yeastract_SLX1_regulon_146_cluster_39_pos"   
 [3] "yeastract_VMA2_regulon_46_cluster_13_neg"    
 [4] "yeastract_VMA2_regulon_46_cluster_5_neg"     
 [5] "yeastract_YPT10_regulon_182_cluster_17_neg"  
 [6] "yeastract_RPS9B_regulon_108_cluster_10_neg"  
 [7] "yeastract_ECM31_regulon_95_cluster_24_pos"   
 [8] "yeastract_YBR184W_regulon_103_cluster_42_neg"
 [9] "yeastract_TSC10_regulon_183_cluster_35_neg"  
[10] "yeastract_YBR220C_regulon_138_cluster_23_neg"
[11] "yeastract_TOS1_regulon_80_cluster_15_neg"    
[12] "yeastract_PRP5_regulon_156_cluster_18_neg"   
[13] "yeastract_ROT2_regulon_147_cluster_1_pos"    
[14] "yeastract_EHT1_regulon_96_cluster_47_neg"    
[15] "yeastract_SDS24_regulon_133_cluster_2_pos"   
[16] "yeastract_SMP1_regulon_101_cluster_31_neg"   
[17] "yeastract_SPO23_regulon_169_cluster_29_neg"  
[18] "yeastract_MET8_regulon_132_cluster_33_neg"   
[19] "yeastract_YBR226C_regulon_144_cluster_33_pos"
[20] "yeastract_SEO1_regulon_2_cluster_6_pos"      
[21] "yeastract_YAL064W_regulon_6_cluster_32_pos"

coalesce_vs_cmonkey <- intersect(dataCOALESCEhitssig[,6], datacmonkeyhitssig[,6])
[1] "yeastract_SEO1_regulon_2_cluster_6_pos"      
[2] "yeastract_YPT10_regulon_182_cluster_17_neg"  
[3] "yeastract_PTC4_regulon_44_cluster_33_neg"    
[4] "yeastract_RPS9B_regulon_108_cluster_10_neg"  
[5] "yeastract_YBR226C_regulon_144_cluster_33_pos"
[6] "yeastract_YPT10_regulon_182_cluster_37_neg"  
[7] "yeastract_YBR141C_regulon_60_cluster_49_neg" 
[8] "yeastract_YBR224W_regulon_142_cluster_24_pos"

mak_vs_cmonkey__coalesce_vs_cmonkey <- intersect(mak_vs_cmonkey, coalesce_vs_cmonkey)
[1] "yeastract_YPT10_regulon_182_cluster_17_neg"  
[2] "yeastract_RPS9B_regulon_108_cluster_10_neg"  
[3] "yeastract_YBR226C_regulon_144_cluster_33_pos"
[4] "yeastract_SEO1_regulon_2_cluster_6_pos"

mak_vs_coalesce__coalesce_vs_cmonkey <- intersect(mak_vs_coalesce, coalesce_vs_cmonkey)
[1] "yeastract_YPT10_regulon_182_cluster_17_neg"  
[2] "yeastract_RPS9B_regulon_108_cluster_10_neg"  
[3] "yeastract_YBR226C_regulon_144_cluster_33_pos"
[4] "yeastract_SEO1_regulon_2_cluster_6_pos" 


###overlap

mak_vs_isa <- intersect(datamakhits[,6], dataISAhits[,6])
KTR3_regulon_124_cluster_9_pos

mak_vs_coalesce <- intersect(datamakhits[,7], dataCOALESCEhits[,7])
 [1] "AGP2_regulon_51_cluster_28_neg"     "FES1_regulon_20_cluster_27_pos"    
 [3] "GDT1_regulon_106_cluster_45_neg"    "MCM7_regulon_121_cluster_34_neg"   
 [5] "MUD1_regulon_38_cluster_24_neg"     "PCS60_regulon_140_cluster_2_neg"   
 [7] "PHO3_regulon_12_cluster_4_neg"      "PHO5_regulon_13_cluster_25_pos"    
 [9] "PTC4_regulon_44_cluster_26_pos"     "RAD16_regulon_33_cluster_13_neg"   
[11] "RXT2_regulon_15_cluster_47_pos"     "SEO1_regulon_2_cluster_16_pos"     
[13] "SEO1_regulon_2_cluster_25_pos"      "SEO1_regulon_2_cluster_5_neg"      
[15] "SEO1_regulon_2_cluster_6_pos"       "SLI15_regulon_75_cluster_5_pos"    
[17] "SPO23_regulon_169_cluster_10_pos"   "SUP45_regulon_62_cluster_2_neg"    
[19] "TBS1_regulon_69_cluster_20_neg"     "TIM12_regulon_11_cluster_32_neg"   
[21] "TYR1_regulon_85_cluster_31_neg"     "TYR1_regulon_85_cluster_32_pos"    
[23] "VPS15_regulon_17_cluster_24_pos"    "YAL064C.A_regulon_5_cluster_37_pos"
[25] "YAL064C.A_regulon_5_cluster_39_neg" "YAL064W_regulon_6_cluster_12_neg"  
[27] "YBR124W_regulon_43_cluster_6_pos"   "YBR134W_regulon_53_cluster_35_pos" 
[29] "YBR141C_regulon_60_cluster_49_neg"  "YBR197C_regulon_116_cluster_49_pos"

mak_vs_cmonkey <- intersect(datamakhits[,7], datacmonkeyhits[,7])
 [1] "ATG12_regulon_136_cluster_12_neg"   "DTR1_regulon_99_cluster_29_neg"    
 [3] "FES1_regulon_20_cluster_27_pos"     "FES1_regulon_20_cluster_44_neg"    
 [5] "FLO9_regulon_7_cluster_39_pos"      "IRA1_regulon_59_cluster_17_neg"    
 [7] "MUD1_regulon_38_cluster_24_neg"     "NHP6B_regulon_9_cluster_5_neg"     
 [9] "PHO3_regulon_12_cluster_4_neg"      "PHO5_regulon_13_cluster_25_pos"    
[11] "PHO88_regulon_25_cluster_5_neg"     "PTC4_regulon_44_cluster_1_neg"     
[13] "RAD16_regulon_33_cluster_13_neg"    "RAD16_regulon_33_cluster_16_pos"   
[15] "RXT2_regulon_15_cluster_47_pos"     "SEO1_regulon_2_cluster_1_neg"      
[17] "SPO23_regulon_169_cluster_10_pos"   "TBS1_regulon_69_cluster_20_neg"    
[19] "TPS1_regulon_45_cluster_34_pos"     "TYR1_regulon_85_cluster_32_pos"    
[21] "VMA2_regulon_46_cluster_40_pos"     "YAL064C.A_regulon_5_cluster_37_pos"
[23] "YAL064W_regulon_6_cluster_12_neg"   "YAL064W_regulon_6_cluster_18_pos"  
[25] "YAL065C_regulon_4_cluster_2_pos"    "YAL065C_regulon_4_cluster_27_pos"  
[27] "YAL066W_regulon_3_cluster_45_pos"   "YAL066W_regulon_3_cluster_5_pos"   
[29] "YBR089W_regulon_8_cluster_21_pos"   "YBR124W_regulon_43_cluster_38_pos" 
[31] "YBR124W_regulon_43_cluster_6_pos"   "YBR232C_regulon_150_cluster_29_neg"
[33] "YPT10_regulon_182_cluster_15_pos"   "YPT10_regulon_182_cluster_37_neg" 

coalesce_vs_cmonkey <- intersect(dataCOALESCEhits[,7], datacmonkeyhits[,7])
 [1] "CYC8_regulon_31_cluster_48_pos"     "ERV15_regulon_129_cluster_47_neg"  
 [3] "FES1_regulon_20_cluster_27_pos"     "FLO9_regulon_7_cluster_17_pos"     
 [5] "IRA1_regulon_59_cluster_10_pos"     "MBA1_regulon_104_cluster_14_neg"   
 [7] "MUD1_regulon_38_cluster_24_neg"     "NHP6B_regulon_9_cluster_11_pos"    
 [9] "PBY1_regulon_14_cluster_5_neg"      "PHO3_regulon_12_cluster_4_neg"     
[11] "PHO5_regulon_13_cluster_1_neg"      "PHO5_regulon_13_cluster_20_neg"    
[13] "PHO5_regulon_13_cluster_25_pos"     "RAD16_regulon_33_cluster_13_neg"   
[15] "RAD16_regulon_33_cluster_24_pos"    "RIB5_regulon_175_cluster_16_pos"   
[17] "RXT2_regulon_15_cluster_47_pos"     "SEO1_regulon_2_cluster_35_pos"     
[19] "SPO23_regulon_169_cluster_10_pos"   "TBS1_regulon_69_cluster_20_neg"    
[21] "TYR1_regulon_85_cluster_32_pos"     "YAL064C.A_regulon_5_cluster_37_pos"
[23] "YAL064W_regulon_6_cluster_12_neg"   "YAL064W_regulon_6_cluster_14_pos"  
[25] "YAL064W_regulon_6_cluster_35_pos"   "YAL066W_regulon_3_cluster_44_pos"  
[27] "YBR096W_regulon_16_cluster_34_neg"  "YBR124W_regulon_43_cluster_6_pos"  
[29] "YBR209W_regulon_128_cluster_47_pos" "YSY6_regulon_81_cluster_8_pos"

mak_vs_cmonkey__coalesce_vs_cmonkey <- intersect(mak_vs_cmonkey, coalesce_vs_cmonkey)
 [1] "FES1_regulon_20_cluster_27_pos"     "MUD1_regulon_38_cluster_24_neg"    
 [3] "PHO3_regulon_12_cluster_4_neg"      "PHO5_regulon_13_cluster_25_pos"    
 [5] "RAD16_regulon_33_cluster_13_neg"    "RXT2_regulon_15_cluster_47_pos"    
 [7] "SPO23_regulon_169_cluster_10_pos"   "TBS1_regulon_69_cluster_20_neg"    
 [9] "TYR1_regulon_85_cluster_32_pos"     "YAL064C.A_regulon_5_cluster_37_pos"
[11] "YAL064W_regulon_6_cluster_12_neg"   "YBR124W_regulon_43_cluster_6_pos"

mak_vs_coalesce__coalesce_vs_cmonkey <- intersect(mak_vs_coalesce, coalesce_vs_cmonkey)
 [1] "FES1_regulon_20_cluster_27_pos"     "MUD1_regulon_38_cluster_24_neg"    
 [3] "PHO3_regulon_12_cluster_4_neg"      "PHO5_regulon_13_cluster_25_pos"    
 [5] "RAD16_regulon_33_cluster_13_neg"    "RXT2_regulon_15_cluster_47_pos"    
 [7] "SPO23_regulon_169_cluster_10_pos"   "TBS1_regulon_69_cluster_20_neg"    
 [9] "TYR1_regulon_85_cluster_32_pos"     "YAL064C.A_regulon_5_cluster_37_pos"
[11] "YAL064W_regulon_6_cluster_12_neg"   "YBR124W_regulon_43_cluster_6_pos"


mak_vs_coalesce__coalesce_vs_cmonkey <- intersect(mak_vs_coalesce, coalesce_vs_cmonkey)
mak_vs_cmonkey__coalesce_vs_cmonkey <- intersect(mak_vs_cmonkey, coalesce_vs_cmonkey)


###OLD
pdf("ISA_top_matching_yeastract_overlap.pdf)
hist(dataISA[dataISAdist_maxindex,5])
dev.off(2)

quantile(dataISA[,5],seq(0,1,0.1))
#size of regulon pattern
hist(dataISA[which(dataISA[,5] > 0.02),3])
#id of regulon pattern
sort(table(dataISA[which(dataISA[,5] > 0.02),1]))
sort(table(dataISA[which(dataISA[,5] > 0.02),2]))

dataCOALESCE <- read.table("./OUT_COALESCE/motifs_summary.txt_MSEC_KendallC_GEECE_inter_feat_0.25_1.0_c_reconstructed.txt_0.0_gene_stats.txt",sep="\t",header=F)
quantile(dataCOALESCE[,5],seq(0,1,0.1))
sort(table(dataCOALESCE[which(dataCOALESCE[,5] > 0.04),1]))
sort(table(dataCOALESCE[which(dataCOALESCE[,5] > 0.02),2]))

datacmonkey <- read.table("./OUT_cmonkey/cMonkey_yeast_MSEC_KendallC_GEECE_inter_feat_0.25_1.0_c_liven_reconstructed.txt_0.0_gene_stats.txt",sep="\t",header=F)
quantile(datacmonkey[,5],seq(0,1,0.1))
sort(table(datacmonkey[which(datacmonkey[,5] > 0.01),1]))
sort(table(datacmonkey[which(datacmonkey[,5] > 0.02),2]))

datamak <- read.table("./EXPR_round12345_merge_refine/results_yeast_cmonkey_1_expr_MSEC_KendallC_GEECE_refine12345_top_0.25_1.0_c_liven_reconstructed.txt_0.0_gene_stats.txt",sep="\t",header=F)
quantile(datamak[,5],seq(0,1,0.1))
sort(table(datamak[which(datamak[,5] > 0.02),1]))
sort(table(datamak[which(datamak[,5] > 0.02),2]))