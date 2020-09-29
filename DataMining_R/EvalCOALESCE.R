
setwd("~/Documents/integr8_genom/Miner/miner_results/COALESCE_eval/")
data <- read.table("./eval_COALESCE_out_MSEC_KendallC_GEECE_top_cut_scoreperc95.0_exprperc95.0_0.0__nr.txt",sep="\t",header=T)


pdf("MAK_vs_COALESC_eval_COALESCE.pdf", height=8.5, width=11)
par(mfrow=c(1,3))
plot(density(data[intersect(which(data[,1] == "MAK"), which(!is.nan(data[,31]))),31]), main="F1 precision and recall of genes and experiments", xlab="F1(precision, recall)", ylim=c(0,250))
lines(density(data[intersect(which(data[,1] == "COALESCE"), which(!is.nan(data[,31]))),31]), col="red")
#dev.off(2)

#pdf("MAK_vs_COALESC_eval_COALESCE_precisionge.pdf")
plot(density(data[intersect(which(data[,1] == "MAK"), which(!is.nan(data[,29]))),29]), main="Precision of genes and experiments", xlab="precision genes+experiments", ylim=c(0,800))
lines(density(data[intersect(which(data[,1] == "COALESCE"), which(!is.nan(data[,29]))),29]), col="red")
#dev.off(2)

#pdf("MAK_vs_COALESC_eval_COALESCE_recallge.pdf")
plot(density(data[intersect(which(data[,1] == "MAK"), which(!is.nan(data[,30]))),30]), main="Recall of genes and experiments", xlab="recall genes+experiments", ylim=c(0,5000))
lines(density(data[intersect(which(data[,1] == "COALESCE"), which(!is.nan(data[,30]))),30]), col="red")
dev.off(2)


#ROC
ablack <- rgb(0,0,0,120,maxColorValue=255)
ared <- rgb(255,0,0,120,maxColorValue=255)


pdf("MAK_vs_COALESC_eval_COALESCE_ROC.pdf", height=8.5, width=11)
par(mfrow=(c(1,3)))

plot(1 - data[which(data[,1] == "MAK"),29] , data[which(data[,1] == "MAK"),30], xlim=c(0,0.001),ylim=c(0,1), main="ROC for COALESCE and MAK on COALESCE yeast-like evaluation (gene-exp. pairs)", ylab="True positive rate", xlab="False positive rate", col=ablack, pch=(data[which(data[,1] == "MAK"),2]),cex=((data[which(data[,1] == "COALESCE"),3]+4)/7))
points(1 - data[which(data[,1] == "COALESCE"),29] , data[which(data[,1] == "COALESCE"),30], col=ared, pch=(data[which(data[,1] == "COALESCE"),2]),cex=((data[which(data[,1] == "COALESCE"),3]+4)/7))


unique(data[which(data[,1] == "MAK" & data[,2] == 0),3])
1 0
unique(data[which(data[,1] == "MAK" & data[,2] == 1),3])
9 3
unique(data[which(data[,1] == "MAK" & data[,2] == 2),3])
3 9 0 1
unique(data[which(data[,1] == "MAK" & data[,2] == 3),3])
7 5 3 6
unique(data[which(data[,1] == "MAK" & data[,2] == 4),3])
4 5 1 8

> ( 2 + 2 + 4 + 4 +4)/5
[1] 3.2

unique(data[which(data[,1] == "COALESCE" & data[,2] == 0),3])
1 2 0 4 5 3
unique(data[which(data[,1] == "COALESCE" & data[,2] == 1),3])
1 3 4 7 6 8 9 0
unique(data[which(data[,1] == "COALESCE" & data[,2] == 2),3])
9 1 0 3
unique(data[which(data[,1] == "COALESCE" & data[,2] == 3),3])
6 5 3 7 2 9 1
unique(data[which(data[,1] == "COALESCE" & data[,2] == 4),3])
2 7 1 4 6 3 8 5 9

> (6 + 8 + 4 + 5 + 8)/5
[1] 6.2

#specificityge
mean(1 - data[which(data[,1] == "MAK"),29])
0.0008206757
#sensitivityge
mean(data[which(data[,1] == "MAK"),30])
0.6187783
#specificityg
mean(1 - data[which(data[,1] == "MAK"),24])
0.001190432
#sensitivityg
mean(data[which(data[,1] == "MAK"),25])
0.632817

sd(1 - data[which(data[,1] == "MAK"),29])
0.0008517399
sd(data[which(data[,1] == "MAK"),30])
0.3708345

#specificityge
mean(1 - data[which(data[,1] == "COALESCE"),29])
6.124236e-06
#sensitivityge
mean(data[which(data[,1] == "COALESCE"),30])
0.9435852
#specificityg
mean(1 - data[which(data[,1] == "COALESCE"),24])
5.938172e-06
#sensitivityg
mean(data[which(data[,1] == "COALESCE"),25])
0.9435852

sd(1 - data[which(data[,1] == "COALESCE"),29])
2.229017e-05
sd(data[which(data[,1] == "COALESCE"),30])
0.08002775

plot(1 - data[which(data[,1] == "MAK"),24] , data[which(data[,1] == "MAK"),25], xlim=c(0,0.15), main="Genes", ylab="True positive rate", xlab="False positive rate", col=ablack, pch=(data[which(data[,1] == "MAK"),2]),cex=((data[which(data[,1] == "COALESCE"),3]+4)/7))
points(1 - data[which(data[,1] == "COALESCE"),24] , data[which(data[,1] == "COALESCE"),25], col=ared, pch=(data[which(data[,1] == "COALESCE"),2]),cex=((data[which(data[,1] == "COALESCE"),3]+4)/7))

plot(1 - data[which(data[,1] == "MAK"),27] , data[which(data[,1] == "MAK"),28], xlim=c(0,0.15), main="Experiments", ylab="True positive rate", xlab="False positive rate", col=ablack, pch=(data[which(data[,1] == "MAK"),2]),cex=((data[which(data[,1] == "COALESCE"),3]+4)/7))
points(1 - data[which(data[,1] == "COALESCE"),27] , data[which(data[,1] == "COALESCE"),28], col=ared, pch=(data[which(data[,1] == "COALESCE"),2]),cex=((data[which(data[,1] == "COALESCE"),3]+4)/7))

dev.off(2)




set.seed(42)
p1 <- hist(data[which(data[,1] == "COALESCE"),2],  
p2 <- hist(data[which(data[,1] == "MAK"),2])                   
plot( p1, col=rgb(0,0,1,0.5),ylim=c(0,25) )  # first histogram
plot( p2, col=rgb(1,0,0,0.5),ylim=c(0,25), add=T)