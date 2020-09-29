setwd("~/Documents/integr8_genom/Miner/rda/common/yeast_data/")
library(RColorBrewer)
library("gplots")
source("~/Documents/java/MAK/src/DataMining_R/Miner.R")
data <- read.table("./yeast_cmonkey.txt",sep="\t",header=T,row.names=1)

#tfdata <- read.table("")

numdata <- t(apply(data,1,missfxn))
dim(numdata)
absnumdata <- abs(numdata)


heatmap.2(numdata, trace="none", Rowv=FALSE, Colv=FALSE, dendrogram="none")




mean <- mean(numdata)
#[1] -0.06121169
absmean <- mean(abs(numdata))
#[1] 0.5563058

hist(numdata)

gmeans <- array(rowMeans(numdata))
emeans <- array(colMeans(numdata))

absgmeans <- rowMeans(abs(numdata))
gvar <-apply(numdata, 1, var)
gsd <- apply(numdata, 1, sd)

absemeans <- colMeans(abs(numdata))
evar <-apply(numdata, 2, var)
esd <- apply(numdata, 2, sd)


hist(gsd)

hist(esd)


sum(gsd< 0.25)
sum(esd< 0.25)

which(gsd< 0.25)
which(esd< 0.25)

sum(gsd< 0.5)
sum(esd< 0.5)


plot(lines(density(gsd)))


meangmeans <- mean(gmeans)
centergmeans <- gmeans - meangmeans

meanabsgmeans <- mean(absgmeans)
meangvar <- mean(gvar)

zscoregmeans <- scale(gmeans)

quantile(gmeans,probs=seq(0, 1, 0.05))
#          0%           5%          10%          15%          20%          25%          30%          35%          40%          45% 
#-0.917947885 -0.423434800 -0.315753864 -0.254693574 -0.213417206 -0.182244346 -0.155644674 -0.130649443 -0.109936870 -0.088476119 
#         50%          55%          60%          65%          70%          75%          80%          85%          90%          95% 
#-0.067156081 -0.047016476 -0.025852763 -0.002556689  0.021734863  0.050111662  0.085839741  0.127008847  0.189889924  0.289089741 
#        100% 
# 1.317656716


quantile(gvar,probs=seq(0, 1, 0.05))
#         0%          5%         10%         15%         20%         25%         30%         35%         40%         45%         50% 
#0.001865989 0.316121695 0.345124004 0.370430072 0.389975064 0.410120411 0.430834902 0.452350163 0.474579697 0.495411032 0.521263807 
#        55%         60%         65%         70%         75%         80%         85%         90%         95%        100% 
#0.548061640 0.578775067 0.612283149 0.655779051 0.718522277 0.796807877 0.917527152 1.089532645 1.359572180 6.647128875


compareRegimes(gmeans, gvar)


###
###
###nucleolar analysis

nucleolar <- read.table("~/Documents/integr8_genom/data/Scerevisiae/GENE_SETS/nuc_501.txt",header=F)

nucindex <- match(nucleolar[,1], noquote(row.names(numdata)))
nucindex <- nucindex[!is.na(nucindex)]
nucleolar_data <- numdata[nucindex,]
absnucleolar_data <- abs(nucleolar_data)

notnucleolar_data <- numdata[-nucindex,]
absnotnucleolar_data <- abs(notnucleolar_data)

nucmean <- mean(nucleolar_data)
#[1] -0.3393983 #lower mean vs all
nucabsmean <- mean(abs(nucleolar_data))
#[1] 0.7026201 #higher differential expression mean vs all

plot(density(numdata))
lines(density(nucleolar_data), col="red")

plot(density(absnumdata))
lines(density(absnucleolar_data), col="red")



nucgmeans <- array(rowMeans(nucleolar_data))
nucabsgmeans <- rowMeans(abs(nucleolar_data))
nucgvar <-apply(nucleolar_data, 1, var)
nucgsd <- apply(nucleolar_data, 1, sd)

nucmeangmeans <- mean(nucgmeans)
nuccentergmeans <- nucgmeans - nucmeangmeans

nucmeanabsgmeans <- mean(nucabsgmeans)
nucmeangvar <- mean(nucgvar)

nuczscoregmeans <- scale(nucgmeans)


plot(density(gmeans))
lines(density(nucgmeans), col="red")

plot(density(gvar))
lines(density(nucgvar), col="red")

compareRegimes(nucgmeans, nucgvar)

nucemeans <- colMeans(nucleolar_data)


plot(density(emeans))
lines(density(nucemeans), col="red")


heatmap.2(nucleolar_data, trace="none", Rowv=FALSE, Colv=FALSE, dendrogram="none")

heatmap.2(notnucleolar_data, trace="none", Rowv=FALSE, Colv=FALSE, dendrogram="none")


plot(density(notnucleolar_data))
lines(density(nucleolar_data), col="red")

plot(density(absnotnucleolar_data))
lines(density(absnucleolar_data), col="red")

notnucgmeans <- array(rowMeans(notnucleolar_data))
notnucgvar <- apply(notnucleolar_data, 1, var)

plot(density(notnucgmeans))
lines(density(nucgmeans), col="red")

plot(density(notnucgvar))
lines(density(nucgvar), col="red")


###genes changing in N conditions
cut <- 1
totaldim <- dim(numdata)
notnucdim <- dim(notnucleolar_data)
nucdim <- dim(nucleolar_data)
numchange <- mat.or.vec(notnucdim[1], 1)
nucnumchange <- mat.or.vec(nucdim[1], 1)
totalnumchange <- mat.or.vec(totaldim[1], 1)

#find number of experiments in which each gene changes
for(i in 1:notnucdim[1]) {
numchange[i] <- sum(!is.na(which(notnucleolar_data[i,] < -cut| notnucleolar_data[i,] > cut)))
i <- i+1;
}

for(i in 1:nucdim[1]) {
nucnumchange[i] <- sum(!is.na(which(nucleolar_data[i,] < -cut | nucleolar_data[i,] > cut)))
i <- i+1;
}

for(i in 1:totaldim[1]) {
totalnumchange[i] <- sum(!is.na(which(numdata[i,] < -cut | numdata[i,] > cut)))
i <- i+1;
}

plot(density(numchange))
lines(density(nucnumchange), col="red")
lines(density(totalnumchange), col="blue")

#identify lovar and low change count genes
quantgvar <- quantile(gvar, ,probs=seq(0, 1, 0.05))

#genes  hi/lo variance < 10 or > 90 % quantile
logvar <- gvar[gvar < quantgvar[2]]
logvarindex <- which(gvar < quantgvar[2])

quantotalnumchange <- quantile(totalnumchange,  probs=seq(0, 1, 0.05))

lonumchangeindex <- which(totalnumchange < quantotalnumchange[2])

lonumchange_and_logvarindex <- logvarindex[!is.na(match(lonumchangeindex, logvarindex))]

#intersection of few changes and lo variance
length(lonumchange_and_logvarindex)
[1] 215

###low absolute mean
quantgmeans <- quantile(absgmeans)
loabsmeanindex <- which(absgmeans < quantgmeans[2])

###
###
###
mypalette <- bluered(20)
#rem <- c(1,2,3,4,5)
#mypalette <- mypalette[-rem]


png("heatmap_nuc_ordernumchange.png", width=800, height=800)
nucorder <- order(nucnumchange)
nucorder_data <- nucleolar_data[nucorder,]
heatmap.2(nucorder_data, trace="none", Rowv=FALSE, dendrogram="column",col=mypalette, cexCol=0.6, breaks=seq(-10,10,1))
dev.off(2)

png("heatmap_notnuc_ordernumchange.png", width=800, height=800)
notnucorder <- order(numchange)
notnucorder_data <- notnucleolar_data[notnucorder,]
heatmap.2(notnucorder_data, trace="none", Rowv=FALSE, dendrogram="column",col=mypalette, cexCol=0.6, breaks=seq(-10,10,1))
dev.off(2)


###
###
###Functions


compareRegimes <- function(gmeans, gvar ) {

quantgmean <- quantile(gmeans, probs=seq(0, 1, 0.05))
quantgvar <- quantile(gvar, probs=seq(0, 1, 0.05))

#genes  hi/lo variance < 10 or > 90 % quantile
logvar <- gvar[gvar < quantgvar[2]]
logvarindex <- which(gvar < quantgvar[2])
length(logvar)
#[1] 616

midgvar <- gvar[which(gvar > quantgvar[10] & gvar < quantgvar[12])]
midgvarindex <- which(gvar > quantgvar[10] & gvar < quantgvar[12])
length(midgvar)
#[1] 1232

higvar <- gvar[gvar > quantgvar[20]]
higvarindex <- which(gvar > quantgvar[20])
length(higvar)
#[1] 616

#genes hi-lo differential expression
higmean <- gmeans[gmeans > quantgmean[20]]
logmean <- gmeans[gmeans < quantgmean[2]]
hilogmean <- c(higmean, logmean)

higmeanindex <- which(gmeans > quantgmean[20])
logmeanindex <- which(gmeans < quantgmean[2])
hilogmeanindex <- c(higmeanindex, logmeanindex)

length(higmean)
length(logmean)

#no change
nochange <- gmeans[which(gmeans > quantgmean[13] & gmeans < quantgmean[16])]
nochangeindex <- which(gmeans > quantgmean[13] & gmeans < quantgmean[16])
length(nochange)
#1540


png("heatmap_logvar.png", width=800, height=800)
logvarorder <- order(logvar)
logvarorder_data <- numdata[logvarindex,][logvarorder,]
heatmap.2(logvarorder_data, trace="none", Rowv=FALSE, dendrogram="column",col=mypalette, cexCol=0.6, breaks=seq(-10,10,1))
dev.off(2)

png("heatmap_higvar.png", width=800, height=800)
higvarorder <- order(higvar)
higvarorder_data <- numdata[higvarindex,][higvarorder,]
heatmap.2(higvarorder_data, trace="none", Rowv=FALSE, dendrogram="column",col=mypalette, cexCol=0.6, breaks=seq(-10,10,1))
dev.off(2)

png("heatmap_logmean.png", width=800, height=800)
logmeanorder <- order(logmean)
logmeanorder_data <- numdata[logmeanindex,][logmeanorder,]
heatmap.2(logmeanorder_data, trace="none", Rowv=FALSE, dendrogram="column",col=mypalette, cexCol=0.6, breaks=seq(-10,10,1))
dev.off(2)

png("heatmap_higmean.png", width=800, height=800)
higmeanorder <- order(higmean)
higmeanorder_data <- numdata[higmeanindex,][higmeanorder,]
heatmap.2(higmeanorder_data, trace="none", Rowv=FALSE, dendrogram="column",col=mypalette, cexCol=0.6, breaks=seq(-10,10,1))
dev.off(2)

png("heatmap_nochangegmean.png", width=800, height=800)
nochangeorder <- order(nochange)
nochangeorder_data <- numdata[nochangeindex,][nochangeorder,]
heatmap.2(nochangeorder_data, trace="none", Rowv=FALSE, dendrogram="column",col=mypalette, cexCol=0.6, breaks=seq(-10,10,1))
dev.off(2)

#genes hi/mid/lo variance hi/none/lo/hi-lo differential expression
counts <- mat.or.vec(4,3)

counts[1, 1] <- sum(!is.na(match(hilogmeanindex,logvarindex)))/min(length(hilogmeanindex),length(logvarindex))
counts[1, 2] <- sum(!is.na(match(hilogmeanindex,midgvarindex)))/min(length(hilogmeanindex),length(midgvarindex))
counts[1, 3] <- sum(!is.na(match(hilogmeanindex,higvarindex)))/min(length(hilogmeanindex),length(higvarindex))

counts[2, 1] <- sum(!is.na(match(higmeanindex,logvarindex)))/min(length(higmeanindex),length(logvarindex))
counts[2, 2] <- sum(!is.na(match(higmeanindex, midgvarindex)))/min(length(higmeanindex),length(midgvarindex))
counts[2, 3] <- sum(!is.na(match(higmeanindex, higvarindex)))/min(length(higmeanindex),length(higvarindex))

counts[3, 1] <- sum(!is.na(match(logmeanindex,logvarindex)))/min(length(logmeanindex),length(logvarindex))
counts[3, 2] <- sum(!is.na(match(logmeanindex,midgvarindex)))/min(length(logmeanindex),length(midgvarindex))
counts[3, 3] <- sum(!is.na(match(logmeanindex,higvarindex)))/min(length(logmeanindex),length(higvarindex))

counts[4, 1] <- sum(!is.na(match(nochangeindex,logvarindex)))/min(length(nochangeindex),length(logvarindex))
counts[4, 2] <- sum(!is.na(match(nochangeindex,midgvarindex)))/min(length(nochangeindex),length(midgvarindex))
counts[4, 3] <- sum(!is.na(match(nochangeindex,higvarindex)))/min(length(nochangeindex),length(higvarindex))

colnames(counts) <- c("lovar","midvar","hivar")
rownames(counts) <- c("hilomean","himean","lomean","nochange")

heatmap.2(counts,Rowv=FALSE, Colv=FALSE, dendrogram="none")

#return(value)
}

