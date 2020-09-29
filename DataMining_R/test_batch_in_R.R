source("Miner.R")
load("synthdata090621_4r_incr_nono")
library("amap")

Iibatch <- c(21,29,34,35,36,37)
Jjbatch <- c(6,7,8,9,10,11,12,13,14,15)
currentbatch <- BatchCreate(Data,Iibatch,Jjbatch,0,1,pSize=0.2,dmethod="euclidean")


Iibatch <- c(11,38,39,39,42,50,53,66,68,74,77,92,114,121,146,147,163,164,166,166,167,167,168,169,170,172,173,173,174,175,178,179,180)
Jjbatch <- c(6,7,8,9,10,11,12,13,14,15,26,27,28,31,32,34,36,69,71,73,74)
currentbatch <- BatchCreate(Data,Iibatch,Jjbatch,0,0,pSize=0.2,dmethod="euclidean")



Iibatch <- c(21,22,24,29,30,32,33,34,35,36,37)
Jjbatch <- c(5,6,7,8,9,10,11,12,13,14)
currentbatch <- BatchCreate(Data,Iibatch,Jjbatch,1,1,pSize=.2,dmethod="euclidean")



Iibatch <- c(21,22,24,25,29,30,32,33,34,35,36,37,38)
Jjbatch <- c(7,11,12,13,14)
currentbatch <- BatchCreate(Data,Iibatch,Jjbatch,1,1,pSize=.2,dmethod="euclidean")


Iibatch <- c(21,22,24,29,30,32,33,34,35,36,37)
Jjbatch <- c(5,6,7,8,9,10,11,12,13,14)
currentbatch <- BatchCreate(Data,Iibatch,Jjbatch,1,1,pSize=.2,dmethod="euclidean")

Iibatch <- c(21,22,24,29,30,32,33,34,35,36,37,39,40)
Jjbatch <- c(5,6,7,8,9,10,11,12,13,14)
currentbatch <- BatchCreate(Data,Iibatch,Jjbatch,0,0,pSize=.2,dmethod="euclidean")


Iibatch <- c(21,22,24,29,30,32,33,34,35,36,37)
Jjbatch <- c(5,6,7,8,9,10,11,12,13,14)
currentbatch <- BatchCreate(Data,Iibatch,Jjbatch,1,1,pSize=.2,dmethod="euclidean")
