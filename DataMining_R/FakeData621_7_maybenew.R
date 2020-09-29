
###Created from FakeData621_5.R, this one is for rows, where rows are correlated, and each column is a draw of a set of genes with a set correlation structure

setwd("/Users/ctuglus/Documents/Berkeley/Arkin/Yeast")
load("/Users/ctuglus/Documents/Berkeley/Arkin/Yeast/rdataNN1_impute2_ortho1")

library(mvtnorm)
set.seed(18274)

###########Sim-ed experiements
J=40*2
J1=15
J2=10

I=100*2
I1=30
I2=20

n=I*J
sb=1000


#column
m1b=matrix(0,nrow=sb,ncol=J)
cov1=cov(Data[,sample(1:173,J)])
cor1=cor(Data[,sample(1:173,J)])
m1=m1b[1,]=mean(Data[,sample(1:173,J)])
for(b in 1:(sb-1)){
	d1=Data[,sample(1:173,J)]
	cov1=cov1+cov(d1)
	cor1=cor1+cor(d1)
	m1=m1+mean(d1)
	m1b[b+1,]=apply(d1,2,mean)
	} 

#row	
m2b=matrix(0,nrow=sb,ncol=I)
cov2=cov(t(Data[sample(1:5227,I),]))
cor2=cor(t(Data[sample(1:5227,I),]))
m2=m2b[1,]=apply(t(Data[sample(1:5227,I),]),2,mean)
for(b in 1:(sb-1)){
	d2=t(Data[sample(1:5227,I),])
	cov2=cov2+cov(d2)
	cor2=cor2+cor(d2)
	m2=m2+apply(d2,2,mean)
	m2b[b+1,]=apply(d2,2,mean)
	} 	

mallC=rmvnorm(I,m1/sb,cov1/sb)
	
mallR=t(rmvnorm(J,m2/sb,cov2/sb))#*t(rmvnorm(J,m2/sb,cov2/sb)) #unif(I*J,0.9,1.1),ncol=J)

AvgVarC=mean(diag(cov1/sb))
AvgVarR=mean(diag(cov2/sb))

#mall=t(rmvnorm(J,m2/sb,cov2/sb))


########Creating blocks for Row correlation
cors=c(0.3,.5,.7,.9) #
#cors=rep(0.5,4)
#rep(0.5,6)#c(0,.2,.4,.6,.8,1)

Ibl1to4=list(21:40, 61:80, 121:140, 161:180)
Jbl1to4=list(6:15,26:35,46:55,66:75)
# Ibl1to6=list(16:30, 46:60, 76:90, 16:30, 46:60, 76:90)
# Jbl1to6=list(6:15,26:35,6:15,26:35,6:15,26:35)

I1=20
J1=10

for(bl in 1:4){
	mbl1=sample(c(seq(-3,-1.5,by=0.25),seq(1.5,3,by=0.25)),I1, replace=TRUE)
	bl1cor=rep(cors[bl],I1+choose(I1,2))#cors[b1]+runif(I1+choose(I1,2),-.1,.1)#rnorm(I1+choose(I1,2),cors[bl],.0001) #adds a bit of randomness to the correlations
	bl1var=rep(AvgVarR,I1)#AvgVarR+runif(I1,0,.1)

	cormat1=matrix(1,ncol=I1,nrow=I1)
	k=0
	for(i in 1:I1){ for(j in 1:i){
		k=k+1
		 cormat1[i,j]=cormat1[j,i]=bl1cor[k]*bl1var[i]
		 }}
	diag(cormat1)=bl1var
	bl1=t(rmvnorm(J1,mbl1,cormat1))#*matrix(rnorm(I1*J1),ncol=J1)#*matrix(runif(I1*J1,0.9,1.1),ncol=J1)
	
	mallR[Ibl1to4[[bl]],Jbl1to4[[bl]]]=bl1
	
}
EallR=mallR

########Creating blocks for Column correlation
cors=c(0.3,.5,.7,.9) #
#cors=rep(0.5,4)
#rep(0.5,6)#c(0,.2,.4,.6,.8,1)

Ibl1to4=list(21:40, 61:80, 121:140, 161:180)
Jbl1to4=list(6:15,26:35,46:55,66:75)
# Ibl1to6=list(16:30, 46:60, 76:90, 16:30, 46:60, 76:90)
# Jbl1to6=list(6:15,26:35,6:15,26:35,6:15,26:35)

I1=20
J1=10

for(bl in 1:4){
	mbl1=sample(c(seq(-3,-1.5,by=0.25),seq(1.5,3,by=0.25)),J1, replace=TRUE)
	bl1cor=rep(cors[bl],J1+choose(J1,2))#cors[b1]+runif(I1+choose(I1,2),-.1,.1)#rnorm(I1+choose(I1,2),cors[bl],.0001) #adds a bit of randomness to the correlations
	bl1var=rep(AvgVarC,J1)#AvgVarR+runif(I1,0,.1)

	cormat1=matrix(1,ncol=J1,nrow=J1)
	k=0
	for(i in 1:J1){ for(j in 1:i){
		k=k+1
		 cormat1[i,j]=cormat1[j,i]=bl1cor[k]*bl1var[i]
		 }}
	diag(cormat1)=bl1var
	bl1=(rmvnorm(I1,mbl1,cormat1))#*matrix(rnorm(I1*J1),ncol=J1)#*matrix(runif(I1*J1,0.9,1.1),ncol=J1)
	
	mallC[Ibl1to4[[bl]],Jbl1to4[[bl]]]=bl1
	
}
EallC=mallC


#################Now create three interaction datasets

####Interaction Data with probsI=1 inside the block and background outside
Iall1=matrix(rbinom(I*I,1,mean(InterF)),ncol=I)

probsI1=rep(1,4)
Ibl1to4i=list(21:40, 61:80, 121:140, 161:180)
for(bl in 1:4){
	Ib1=matrix(rbinom(I1*I1,1,probsI1[bl]),ncol=10)
	Iall1[Ibl1to4[[bl]],Ibl1to4i[[bl]]]=Ib1
}


####Interaction Data with probsI=.25 inside the block and background outside
Iall25=matrix(rbinom(I*I,1,mean(InterF)),ncol=I)

probsI25=rep(.25,4)
Ibl1to4i=list(21:40, 61:80, 121:140, 161:180)
for(bl in 1:4){
	Ib1=matrix(rbinom(I1*I1,1,probsI25[bl]),ncol=10)
	Iall25[Ibl1to4[[bl]],Ibl1to4i[[bl]]]=Ib1
}


####Interaction Data with probsI=.05 inside the block and background outside
Iall05=matrix(rbinom(I*I,1,mean(InterF)),ncol=I)

probsI05=rep(.05,4)
Ibl1to4i=list(21:40, 61:80, 121:140, 161:180)
for(bl in 1:4){
	Ib1=matrix(rbinom(I1*I1,1,probsI05[bl]),ncol=10)
	Iall05[Ibl1to4[[bl]],Ibl1to4i[[bl]]]=Ib1
}

#save(I,J,Iall,Eall,Ibl1to4,Jbl1to4,cors,probsI,file="fake621_4r_const1")
save(I,J,Iall1,Iall25,Iall05,EallC,EallR,Ibl1to4,Jbl1to4,cors,probsI1,probsI25,probsI05,file="fake621_4r_incr1_12I")

#####Now we create the randomized version

set.seed(19729)

Isample=sample(1:I,I)
Jsample=sample(1:J,J)

Ibl1to4=list(21:40, 61:80, 121:140, 161:180)
Jbl1to4=list(6:15,26:35,46:55,66:75)

Ibl1to4R=lapply(Ibl1to4,function(x) match(x,Isample))
Jbl1to4R=lapply(Jbl1to4,function(x) match(x,Jsample))

EallCrand=EallC[Isample,Jsample]
EallRrand=EallR[Isample,Jsample]
Iall1rand=Iall1[Isample,Isample]
Iall25rand=Iall25[Isample,Isample]
Iall05rand=Iall05[Isample,Isample]

##Quick check
par(mfrow=c(2,2))
for(i in 1:4){
image(EallCrand[Ibl1to4R[[i]],Jbl1to4R[[i]]])
}

save(I,J,Iall1rand,Iall25rand,Iall05rand,EallCrand,EallRrand,Ibl1to4R,Jbl1to4R,cors,probsI1,probsI25,probsI05,file="fake621_4r_incr1_12Irand")

par(mfrow=c(2,5))
image(1:80,1:200,t(EallC),xlab="Experiments",ylab="Genes",main="EallC")
image(1:80,1:200,t(EallR),xlab="Experiments",ylab="Genes",main="EallR")
image(1:200,1:200,t(Iall1),xlab="Genes",ylab="Genes",main="Interation 1")
image(1:200,1:200,t(Iall25),xlab="Genes",ylab="Genes",main="Interation .25")
image(1:200,1:200,t(Iall05),xlab="Genes",ylab="Genes",main="Interation .05")

image(1:80,1:200,t(EallCrand),xlab="Experiments",ylab="Genes",main="EallC random")
image(1:80,1:200,t(EallRrand),xlab="Experiments",ylab="Genes",main="EallR random")
image(1:200,1:200,t(Iall1rand),xlab="Genes",ylab="Genes",main="Interation 1 random")
image(1:200,1:200,t(Iall25rand),xlab="Genes",ylab="Genes",main="Interation .25 random")
image(1:200,1:200,t(Iall05rand),xlab="Genes",ylab="Genes",main="Interation .05 random")


#image(Data[sI,sJ])
#image(Data)
#image(GInter[sI,sJ])
#image(GInter)






