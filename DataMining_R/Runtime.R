setwd("~/Documents/integr8_genom/Miner/miner_results/yeast/")
data <- read.table("./results_yeast_cmonkey_1_expr_MSEC_KendallC_GEECE_all_runtimestats.txt",sep="\t",header=F)

datarefine <- read.table("./results_yeast_cmonkey_1_expr_MSEC_KendallC_GEECE_refine12345_all_runtimestats.txt",sep="\t",header=F)

maxA1 <-  max(data[,2])
maxB1 <-  max(datarefine[,2])
maxA2 <-  max(data[,2]/data[,3])
maxB2 <-  max(datarefine[,2]/datarefine[,3])
maxA3 <-  max(data[,3])
maxB3 <-  max(datarefine[,3])

maxx1 <- max(data[,2], datarefine[,2])
maxx2 <- max(data[,2]/data[,3], datarefine[,2]/datarefine[,3])
maxx3 <- max(data[,3], datarefine[,3])


redrgb <-col2rgb("red")
redalpha <- rgb(redrgb[1], redrgb[2], redrgb[3],20, maxColorValue = 255)


pdf("results_yeast_cmonkey_1_expr_MSEC_KendallC_GEECE_runtimestats.pdf",height=8.5, width=11)
par(mfrow=c(1,3))

par(mar=c(5, 8, 3, 1) + 0.1)
par(new=F)

densA1 <- density(data[,2], from=0, to=maxA1)
densB1 <- density(datarefine[,2], from=0, to=maxB1)
plot(densA1,main="Time per trajectory",xlab="Time (s)", xlim=c(0,maxx1),yaxt="n",ylab="")
lines(densB1, lwd=2)
axis(2, ylim=c(0,max(densA1$y, densB1$y)),lwd=2,line=0.2)
mtext(2,text="Density of trajectories",line=2)

par(new=T)
plot(data[,2],data[,4], type="p",col=redalpha,xaxt="n",yaxt="n",xlab="",ylab="")
axis(2, ylim=c(0,max(data[,4])),lwd=2,line=3.5, col="red")
mtext(2,text="Bicluster score",line=5)


par(mar=c(5, 8, 3, 1) + 0.1)
par(new=F)

densA2 <- density(data[,2]/data[,3], from=0, to=maxA2)
densB2 <- density(datarefine[,2]/datarefine[,3], from=0, to=maxB2)

plot(densA2,main="Time per move",xlab="Time (s)", xlim=c(0,maxx2),yaxt="n",ylab="")
lines(densB2 , lwd=2)
axis(2, ylim=c(0,max(densA2$y, densB2$y)),lwd=2,line=0.2)
mtext(2,text="Density of moves",line=2)

par(new=T)
plot(data[,2]/data[,3],data[,4], type="p",col=redalpha,xaxt="n",yaxt="n",xlab="",ylab="")
axis(2, ylim=c(0,max(data[,4])),lwd=2,line=3.5, col="red")
mtext(2,text="Bicluster score",line=5)

par(mar=c(5, 8, 3, 1) + 0.1)
par(new=F)

densA3 <- density(data[,3], from=0, to=maxA3)
densB3 <- density(datarefine[,3], from=0, to=maxB3)

plot(densA3,main="Number of moves",xlab="Moves", xlim=c(0,maxx3),yaxt="n",ylab="")
lines(densB3, lwd=2)
axis(2, ylim=c(0,max(densA3$y, densB3$y)),lwd=2,line=0.2)
mtext(2,text="Density of moves",line=2)

par(new=T)
plot(data[,3],data[,4], type="p",col=redalpha,xaxt="n",yaxt="n",xlab="",ylab="")
axis(2, ylim=c(0,max(data[,4])),lwd=2,line=3.5, col="red")
mtext(2,text="Bicluster score",line=5)


dev.off(2)