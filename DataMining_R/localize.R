library(gplots)
setwd("~/Documents/integr8_genom/Miner/miner_results/yeast/EXPR_round12_Sn/localization/")
data <- read.table("pair_localize_foR.txt",sep="\t",header=T,row.names=1)

heatmap.2(log(as.matrix(data)+0.001), trace="none",Colv=F,Rowv=F)
heatmap.2(log(as.matrix(data)+0.001), trace="none", symm=T)

#scatterhist = function(x, y, xlab="", ylab=""){
  zones=matrix(c(2,0,1,3), ncol=2, byrow=TRUE)
  layout(zones, widths=c(4/5,1/5), heights=c(1/5,4/5))
  xhist = hist(log(rowSums(data)+0.001), plot=FALSE)
  #yhist = hist(y, plot=FALSE)
  top = max(c(xhist$counts))#, yhist$counts))
  par(mar=c(3,3,1,1))
  #plot(x,y)
  heatmap.2(log(as.matrix(data)+0.001), trace="none",Colv=F,Rowv=F)
  par(mar=c(0,3,1,1))
  barplot(xhist$counts, axes=FALSE, ylim=c(0, top), space=0)
  par(mar=c(3,0,1,1))
  #barplot(yhist$counts, axes=FALSE, xlim=c(0, top), space=0, horiz=TRUE)
  #par(oma=c(3,3,0,0))
  #mtext(xlab, side=1, line=1, outer=TRUE, adj=0, 
  #  at=.8 * (mean(x) - min(x))/(max(x)-min(x)))
  #mtext(ylab, side=2, line=1, outer=TRUE, adj=0, 
# at=(.8 * (mean(y) - min(y))/(max(y) - min(y))))
#}