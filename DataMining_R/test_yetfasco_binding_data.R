setwd("~/Documents/integr8_genom/Miner/miner_results/yeast/")
data <- read.table("./20120126_allMotifData1.01.rdat",sep="\t")

> grep("YER109C",colnames(data))
[1] 1802



MSS11 <- c()
for(i in 1803:1811) {
MSS11 <- c(MSS11, row.names(data[which(data[,i] > 0.99),]))
}

apply(data, 1, funcion(x) length(which(x > 0.99)))