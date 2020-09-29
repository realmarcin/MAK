
setwd("/Users/ctuglus/Documents/Berkeley/Arkin/Yeast")
load("/Users/ctuglus/Documents/Berkeley/Arkin/Yeast/rdataNN1_impute2_ortho1")

library(mvtnorm)

trueblock1 = "21,29,166,154,153,131,72,193,47,86,65,115,138,28,3,126,19,1,85,46/44,9,8,67,2,54,60,15,31,76";
trueblock2 = "140,178,108,33,172,6,195,123,27,24,4,163,121,50,169,26,117,16,49,114/24,25,72,29,42,41,17,39,68,33";
trueblock3 = "181,64,10,37,74,175,35,98,152,171,158,194,81,17,199,100,177,38,90,165/21,34,48,80,5,23,40,30,26,38";
trueblock4 = "39,87,167,106,104,157,13,48,68,185,147,32,187,136,70,196,42,60,18,58/73,74,77,66,70,16,71,43,46,47";
    
###########Sim-ed experiements
J=40
J1=15
J2=10

I=100
I1=30
I2=20

n=I*J
sb=1000
#cov1=cov(Data[,sample(1:173,J)])
#m1=mean(Data[,sample(1:173,J)])
#for(b in 1:(sb-1)){
#	d1=Data[,sample(1:173,J)]
#	cov1=cov1+cov(d1)
#	m1=m1+mean(d1)
#	} 
#mall=rmvnorm(I,m1/sb,cov1/sb)

cov1=cov(Data[,sample(1:173,J)])
m1=mean(Data[,sample(1:173,J)])
for(b in 1:(sb-1)){
	d1=Data[,sample(1:173,J)]
	cov1=cov1+cov(d1)
	m1=m1+mean(d1)
	} 
mall=rmvnorm(I,m1/sb,cov1/sb)

##createFblock=function(J,I,Ja,Ia,meanB=NULL,corB=NULL)

########Creating block 1
mbl1=sample(c(seq(-3,-1.5,by=0.25),seq(1.5,3,by=0.25)),I1, replace=TRUE)
bl1sd=rep(1,I1)
bl1cor=rnorm(I1+choose(I1,2),0.5,.001)

cormat1=matrix(1,ncol=I1,nrow=I1)
k=0
for(i in 1:I1){ for(j in 1:i){
		k=k+1
		 cormat1[i,j]=cormat1[j,i]=bl1cor[k]
		 }}
diag(cormat1)=1
cormat1=bl1sd*cormat1
bl1=t(rmvnorm(J1,mbl1,cormat1))

########Creating block 2
#mbl1=sample(c(-4:-2,2:4),J2, replace=TRUE)
mb21=c(mbl1[23:30],sample(c(seq(-4,-2,by=0.25),seq(2,4,by=0.25)),(12), replace=TRUE))
bl1sd=rep(1,I2)
bl1cor=rnorm(I2+choose(I2,2),0.8,.001)

cormat1=matrix(1,ncol=I2,nrow=I2)
k=0
for(i in 1:I2){ for(j in 1:i){
		k=k+1
		 cormat1[i,j]=cormat1[j,i]=bl1cor[k]
		 }}
diag(cormat1)=1
cormat1=bl1sd*cormat1
bl2=t(rmvnorm(J2,mb21,cormat1))


mall[1:30,1:15]=0
mall[23:42,10:19]=0

mall[1:30,1:15]=mall[1:30,1:15]+bl1
mall[23:42,10:19]=mall[23:42,10:19]+bl2
mall[23:30,10:15]=mall[23:30,10:15]/2
image(mall)
Eall=mall

#######Interaction Data
Iall=matrix(rbinom(I*I,1,mean(InterF)),ncol=I)
Ib1=matrix(rbinom(I1*I1,1,.07),ncol=I1)
Ib2=matrix(rbinom(I2*I2,1,.07),ncol=I2)
Ib12=matrix(rbinom((8)^2,1,.07),ncol=8)

Itrue1=1:30
Itrue2=23:42
Jtrue1=1:15
Jtrue2=10:19

Iall[1:30,1:30]=Ib1
Iall[23:42,23:42]=Ib2
Iall[23:30,23:30]=Ib12

Iall2=Iall
for(i in 1:I){ for(j in 1:i){
		 Iall[i,j]=Iall[j,i]
		 }}
diag(Iall)=diag(Iall2)
image(Iall)


save(I,J,Iall,Eall,Itrue1,Itrue2,Jtrue1,Jtrue2,file="fake621_1")



par(mfrow=c(2,2))
sI=sample(1:100)
sJ=sample(1:40)
image(1:40,1:100,t(Eall[sI,sJ]),xlab="Experiments",ylab="Genes")
image(1:40,1:100,t(Eall),xlab="Experiments",ylab="Genes")
image(1:100,1:100,t(Iall[sI,sI]),xlab="Experiments",ylab="Genes")
image(1:100,1:100,t(Iall),xlab="Experiments",ylab="Genes")
#image(Data[sI,sJ])
#image(Data)
#image(GInter[sI,sJ])
#image(GInter)






