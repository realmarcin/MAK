setwd("~/Desktop")
data1 <- read.table("./MakeNull_faker_test_1.fullcrits",sep="\t",header=F)
data2 <- read.table("./MakeNull_faker_test_2.fullcrits",sep="\t",header=F)

plot(density(data1[,7]))
lines(density(data2[,7]),col="red")

hist(data1[,7])

hist(data2[,7])