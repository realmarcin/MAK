library("RColorBrewer")
setwd("~/Documents/integr8_genom/Miner/miner_results/yeast/EXPR_round12345_merge_refine/")
dataTF <- read.table("./results_yeast_cmonkey_1_expr_MSEC_KendallC_GEECE_refine12345_top_0.25_1.0_c_liven_reconstructed.txt_TFover.txt", sep="\t",header=F)
head(dataTF)
datamotif <- read.table("./results_yeast_cmonkey_1_expr_MSEC_KendallC_GEECE_refine12345_top_0.25_1.0_c_liven_reconstructed.txt_motifover.txt", sep="\t",header=F)
head(datamotif)


maxall <- max(dataTF$V2, dataTF$V3, datamotif$V2, datamotif$V3)

pal <- brewer.pal(9,"YlOrRd")
palcol <- mat.or.vec(length(pal),1)
palcolnoa <- mat.or.vec(length(pal),1)
for(i in 1:length(palcol)) {
    curcol <- col2rgb(pal[i])
    palcol[i] <- rgb(curcol[1], curcol[2], curcol[3],  alpha=10, maxColorValue=255)
    palcolnoa[i] <- rgb(curcol[1], curcol[2], curcol[3], maxColorValue=255)
}

#TF
coldatae <- round(8*dataTF$V3/maxall+1)
colorse <-mat.or.vec(length(coldatae),1)
colorsnoae <-mat.or.vec(length(coldatae),1)
for(i in 1:length(coldatae)) {
#palcol<- col2rgb(pal[coldatae[i]])
colorse[i] <- palcol[coldatae[i]] #rgb(palcol[1], palcol[2], palcol[3],  alpha=10, maxColorValue=255)
colorsnoae[i] <- palcolnoa[coldatae[i]] #rgb(palcol[1], palcol[2], palcol[3], maxColorValue=255)
}

coldatag <- round(8*dataTF$V2/maxall+1)
colorsg <-mat.or.vec(length(coldatag),1)
colorsnoag <-mat.or.vec(length(coldatag),1)
for(i in 1:length(coldatag)) {
colorsg[i] <- palcol[coldatag[i]] #rgb(palcol[i,1], palcol[i,2], palcol[i,3],  alpha=10, maxColorValue=255)
colorsnoag[i] <- palcolnoa[coldatag[i]] #rgb(palcol[i,1], palcol[i,2], palcol[i,3], maxColorValue=255)
}

#shapeTF <-mat.or.vec(length(coldatae),1)
#for(i in 1:length(shapeTF)) {
#shapeTF[i] <- as.character(dataTF$V6)
#}
shapeTF <- as.character(dataTF$V6)

plot(dataTF$V2, dataTF$V5,col = colorsnoae, pch=shapeTF, xlab="Gene overlap",ylab="TF overlap", main="Bicluster gene vs. TF overlap")

png("geover_vs_TFover_0.25_v4.png", width = 1000, height = 1000)
par(mfrow=c(2,2))
plot(dataTF$V2, dataTF$V5,col = colorse, xlab="Gene overlap", pch=shapeTF,ylab="TF overlap", main="Bicluster gene vs. TF overlap")
plot(dataTF$V2, dataTF$V5,col = colorsnoae, xlab="Gene overlap", pch=shapeTF,ylab="TF overlap", main="Bicluster gene vs. TF overlap")
plot(dataTF$V3, dataTF$V5,col = colorsg, xlab="Experiment overlap", pch=shapeTF,ylab="TF overlap", main="Bicluster experiment vs. TF overlap")
plot(dataTF$V3, dataTF$V5,col = colorsnoag, xlab="Experiment overlap", pch=shapeTF,ylab="TF overlap", main="Bicluster experiment vs. TF overlap")
dev.off(2)


dataTFrem2 <- dataTF[-which(dataTF$V6 == 0),]
dataTFrem3 <- dataTF[-which(dataTF$V6 == 0),]
datamotifrem2 <- datamotif[-which(datamotif$V6 == 0),]
datamotifrem3 <- datamotif[-which(datamotif$V6 == 0),]


#TF
coldatae <- round(8*dataTFrem3$V3/maxall+1)
colorse <-mat.or.vec(length(coldatae),1)
colorsnoae <-mat.or.vec(length(coldatae),1)
for(i in 1:length(coldatae)) {
#palcol<- col2rgb(pal[coldatae[i]])
colorse[i] <- palcol[coldatae[i]] #rgb(palcol[1], palcol[2], palcol[3],  alpha=10, maxColorValue=255)
colorsnoae[i] <- palcolnoa[coldatae[i]] #rgb(palcol[1], palcol[2], palcol[3], maxColorValue=255)
}

coldatag <- round(8*dataTFrem2$V2/maxall+1)
colorsg <-mat.or.vec(length(coldatag),1)
colorsnoag <-mat.or.vec(length(coldatag),1)
for(i in 1:length(coldatag)) {
colorsg[i] <- palcol[coldatag[i]] #rgb(palcol[i,1], palcol[i,2], palcol[i,3],  alpha=10, maxColorValue=255)
colorsnoag[i] <- palcolnoa[coldatag[i]] #rgb(palcol[i,1], palcol[i,2], palcol[i,3], maxColorValue=255)
}

###motif

coldatae_m <- round(8*datamotifrem3$V3/maxall+1)
colorse_m <-mat.or.vec(length(coldatae_m),1)
colorsnoae_m <-mat.or.vec(length(coldatae_m),1)
for(i in 1:length(coldatae_m)) {
colorse_m[i] <- palcol[coldatae_m[i]] 
colorsnoae_m[i] <- palcolnoa[coldatae_m[i]]
}

coldatag_m <- round(8*datamotifrem2$V2/maxall+1)
colorsg_m <-mat.or.vec(length(coldatag_m),1)
colorsnoag_m <-mat.or.vec(length(coldatag_m),1)
for(i in 1:length(coldatag_m)) {
colorsg_m[i] <- palcol[coldatag_m[i]]
colorsnoag_m[i] <- palcolnoa[coldatag_m[i]]
}

shapemotif2 <- as.character(datamotifrem2$V6)
shapemotif3 <- as.character(datamotifrem3$V6)
shapeTF2 <- as.character(dataTFrem2$V6)
shapeTF3 <- as.character(dataTFrem3$V6)

png("geover_vs_motifover_0.25_v4.png", width = 1000, height = 1000)
par(mfrow=c(2,2))
plot(datamotifrem2$V2, datamotifrem2$V5,col = colorse_m, pch=shapemotif2, xlab="Gene overlap",ylab="GRE overlap", main="Bicluster gene vs. GRE overlap")
plot(datamotifrem2$V2, datamotifrem2$V5,col = colorsnoae_m, pch=shapemotif2, xlab="Gene overlap",ylab="GRE overlap", main="Bicluster gene vs. GRE overlap")
plot(datamotifrem3$V3, datamotifrem3$V5,col = colorsg_m, pch=shapeTF3, xlab="Experiment overlap",ylab="GRE overlap", main="Bicluster experiment vs. GRE overlap")
plot(datamotifrem3$V3, datamotifrem3$V5,col = colorsnoag_m, pch=shapeTF3, xlab="Experiment overlap",ylab="GRE overlap", main="Bicluster experiment vs. GRE overlap")
dev.off(2)

png("geover_vs_TFover-motifover_0.25_black_v4.png", width = 1000, height = 1000)
par(mfrow=c(2,2), bg='black',fg='white', col.axis='white', col.lab='white', col.main='white', col.sub='white') 
plot(dataTFrem2 $V2, dataTFrem2 $V5,col = colorsnoae, xlab="Gene overlap", pch=shapeTF2,ylab="TF overlap", main="Bicluster gene vs. TF overlap", xlim=c(0,1), ylim=c(0,1))
plot(dataTFrem3$V3, dataTFrem3$V5,col = colorsnoag, xlab="Experiment overlap", pch=shapeTF3,ylab="TF overlap", main="Bicluster experiment vs. TF overlap", xlim=c(0,1), ylim=c(0,1))
plot(datamotifrem2$V2,datamotifrem2$V5,col = colorsnoae_m, pch=shapemotif2, xlab="Gene overlap",ylab="GRE overlap", main="Bicluster gene vs. GRE overlap", xlim=c(0,1), ylim=c(0,1),)
plot(datamotifrem3$V3, datamotifrem3$V5,col = colorsnoag_m, pch=shapemotif3, xlab="Experiment overlap",ylab="GRE overlap", main="Bicluster experiment vs. GRE overlap", xlim=c(0,1), ylim=c(0,1))
dev.off(2)


#png("TF-motif_vs_geneexpover_0.25_black_v4.png", width = 1000, height = 1000)
pdf("TF-motif_vs_geneexpover_0.25_black_v4.pdf", width =11, height =8.5)
par(mfrow=c(2,2), bg='black',fg='white', col.axis='white', col.lab='white', col.main='white', col.sub='white') 
plot(dataTFrem2 $V2, dataTFrem2 $V5,col = colorsnoae, xlab="Gene overlap", pch=shapeTF2,ylab="TF overlap", main="Bicluster gene vs. TF overlap", xlim=c(0,1), ylim=c(0,1))
plot(dataTFrem3$V3, dataTFrem3$V5,col = colorsnoag, xlab="Experiment overlap", pch=shapeTF3,ylab="TF overlap", main="Bicluster experiment vs. TF overlap", xlim=c(0,1), ylim=c(0,1))
plot(datamotifrem2$V2, datamotifrem2$V5,col = colorsnoae_m, pch=shapemotif2, xlab="Gene overlap",ylab="GRE overlap", main="Bicluster gene vs. GRE overlap", xlim=c(0,1), ylim=c(0,1),)
plot(datamotifrem3$V3, datamotifrem3$V5,col = colorsnoag_m, pch=shapemotif3, xlab="Experiment overlap",ylab="GRE overlap", main="Bicluster experiment vs. GRE overlap", xlim=c(0,1), ylim=c(0,1))
dev.off(2)

