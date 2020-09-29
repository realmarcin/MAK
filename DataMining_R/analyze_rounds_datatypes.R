setwd("~/Documents/integr8_genom/Miner/miner_results/yeast/MASTER/")
dataraw <- read.table("./bicluster_set_summary_MAK_v7.txt",sep="\t",header=T)
data <- dataraw
head(data)
names <- colnames(data)
names <- names[-31]
data <- data[,-1]
colnames(data) <- names

linedata <- data[,c(1,2,8,10,20:23,29,30)]
linedata <- linedata[1:7,]
MAKnames <- dataraw[1:7,1]
row.names(linedata) <- MAKnames
colnames(linedata) <- colnames(data[,c(1,2,8,10,20:23,29,30)])
scalelinedata <- scale(linedata)

linecols <- c("black","black","black","black","red","red", "red", "red",  "black","black")
lwddata <- mat.or.vec(10,1)
lwddata[1] <- 0.8
plot(seq(1:7),scalelinedata[,1], type="l", xaxt="n",lwd=lwddata[1],ylab="Standardized feature",xlab="MAK round")
for(i in 2:10) {
lwddata[i] <- 0.8+(i-2)*0.4
lines(seq(1:7),scalelinedata[,i], type="l",lty=2,lwd=lwddata[i],col=linecols[i])
}

legend(3.5,1.7 , legend=colnames(linedata),col=linecols, lty=(1:10),cex=0.6,lwd=lwddata)
axis(1, at=1:7, labels = FALSE)
text(seq(1, 10, by=1), par("usr")[3] - 0.2, labels = MAKnames, srt = 45, pos = 1, xpd = TRUE,cex=0.4)