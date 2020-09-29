
rm(list=ls())
setwd("~/integr8functgenom/Miner/rda/common/yeast_data/")
source("~/Genomics/html/applet/MicroArrayUp/MicroArrayUp/DataMining_R/Miner.R")
load("~/integr8functgenom/Miner/rda/common/yeast_data/yeast_cmonkey")

#> max(expr_data)
#[1] 93.61678
#> min(expr_data)
#[1] -73.21265

#reduced input data for testing
#expr_data <- expr_data[1:300,1:300]
expr_datarow <- apply(expr_data,1,missfxn)
expr_datacol <- apply(expr_data,2,missfxn)
datadimrow <- dim(expr_datarow)
datadimcol <- dim(expr_datacol)

library(mvtnorm)
set.seed(8274)

###########Sim-ed experiements
J <- 40*2
J1 <- 15
J2 <- 10
J3 <- 20
J4 <- 15

I <- 100*2
I1 <- 30
I2 <- 20
I3 <- 25
I4 <- 15

n <- I*J
sb <- 1000

varscale <- 1

#columns  <-  J
m1b <- matrix(0,nrow=sb,ncol=J)
cov1 <- cov(expr_datacol[,sample(1:datadimcol[2],J)])
cor1 <- cor(expr_datacol[,sample(1:datadimcol[2],J)])
m1 <- m1b[1,] <- colMeans(expr_datacol[,sample(1:datadimcol[2],J)])
block_median_col <- mat.or.vec(sb,0)
block_median_col_R <- mat.or.vec(sb,0)
for(b in 1:(sb-1)){
	d1 <- expr_datacol[,sample(1:datadimcol[2],J)]
	cov1 <- cov1+cov(d1)
	cor1 <- cor1+cor(d1)
	meanvec <- colMeans(d1)
	m1 <- m1+meanvec
	m1b[b+1,] <- meanvec
	gene_ind <- sample(1:datadimcol[1],I)
	exp_ind <- sample(1:datadimcol[2],J)
	#print(gene_ind)
	#print(exp_ind)
	block_median_col[b] <- AbsCorr.block(expr_datacol,gene_ind,exp_ind,3)
	block_median_col_R[b] <- AbsCorr.block(expr_datacol,gene_ind,exp_ind,2)
	}
#mallC <- rmvnorm(I,m1/sb,cov1/sb)
AvgVarR <- varscale*mean(diag(cov1/sb))

#> print(mean(block_median_col))
#[1] 0.1562385
#> print(mean(block_median_col_R))
#[1] 0.1761676
print(mean(block_median_col))
print(mean(block_median_col_R))

#quant_col <- quantile(block_median_col,probs=seq(0, 1, 0.1))
#expcorseries <- c


#row  <-  I
m2b <- matrix(0,nrow=sb,ncol=I)
cov2 <- cov(expr_datarow[,sample(1:datadimrow[1],I)])
cor2 <- cor(expr_datarow[,sample(1:datadimrow[1],I)])
m2 <- m2b[1,] <- colMeans(expr_datarow[,sample(1:datadimrow[1],I)])
block_median_row <- mat.or.vec(sb,0)
block_median_row_C <- mat.or.vec(sb,0)
for(b in 1:(sb-1)){
	d2 <- expr_datarow[,sample(1:datadimrow[1],I)]
	cov2 <- cov2+cov(d2)
	cor2 <- cor2+cor(d2)
	meanvec <- colMeans(d2)
	m2 <- m2+meanvec
	m2b[b+1,] <- meanvec
	block_median_row[b] <- AbsCorr.block(expr_datacol,gene_ind,exp_ind,3)
	block_median_row_C[b] <- AbsCorr.block(expr_datacol,gene_ind,exp_ind,2)
	}
#mallR <- t(rmvnorm(J,m2/sb,cov2/sb))#*t(rmvnorm(J,m2/sb,cov2/sb)) #unif(I*J,0.9,1.1),ncol <- J)
AvgVarC <- varscale*mean(diag(cov2/sb))
#> print(mean(block_median_row))
#[1] 0.1670193
#> print(mean(block_median_row_C))
#[1] 0.1655252 
print(mean(block_median_row))
print(mean(block_median_row_C))


mallR=matrix(NA,ncol=J,nrow=I)
for(i in 1:I){
	mallR[i,]=rmvnorm(1,m1/sb+rnorm(1,0,0.1),cov1/sb)
}

#mallC <- t(rmvnorm(J,m2/sb,cov2/sb)) #+rnorm(1)
mallC=matrix(NA,ncol=J,nrow=I)
for(j in 1:J){
	mallC[,j]=rmvnorm(1,m2/sb+rnorm(1,0,0.1),cov2/sb)
}


save(mallR, mallC, AvgVarR, AvgVarC, datadimrow, datadimcol, file="FakeData_over.Rdata")


#quant_row <- quantile(block_median_row,probs=seq(0, 1, 0.1))
#genecorseries <- c(quant_row[7],quant_row[9],quant_row[10],quant_row[10]*1.05,)

#####

###compute pairwise correlations
#genecor <- cor(expr_datarow)
#mediangenecor <- median(genecor)
#mediangenecor

#expcor <- cor(expr_datacol)
#medianexpcor <- median(expcor)
#medianexpcor

library(mvtnorm)
set.seed(8274)

rm(list=ls())
source("~/Genomics/html/applet/MicroArrayUp/MicroArrayUp/DataMining_R/Miner.R")
setwd("~/integr8functgenom/Miner/rda/common/yeast_data/")
load("FakeData_over.Rdata")
load("yeast_cmonkey")

###########Sim-ed experiements
J <- 40*2
J1 <- 15
J2 <- 10
J3 <- 21
J4 <- 25

I <- 100*2
I1 <- 30
I2 <- 20
I3 <- 21
I4 <- 15

n <- I*J
sb <- 1000

varscale <- 1


Itrue1=1:30
Itrue2=23:42
Itrue3=50:70
Itrue4=65:79
Jtrue1=1:15
Jtrue2=10:19
Jtrue3=30:50
Jtrue4=40:64

#overlap
Itrue12=23:30
Jtrue12=10:15
Itrue34=65:70
Jtrue34=40:50

cors=c(0.4,0.9,0.1,0.6)
     
     ####Created with row correlation
     
     bl1=matrix(NA,nrow=I1,ncol=J1)
     bl2=matrix(NA,nrow=I2,ncol=J2)
     bl3=matrix(NA,nrow=I3,ncol=J3)
     bl4=matrix(NA,nrow=I4,ncol=J4)
     
     ########Creating block 1      
     mbl1=sample(c(seq(-3,-1.5,by=0.25),seq(1.5,3,by=0.25)),I1, replace=TRUE)
     bl1var=rep(AvgVarR,I1)
     bl1cor=rnorm(I1+choose(I1,2),cors[1],.001)

     cormat1=matrix(1,ncol=I1,nrow=I1)
     k=0
     for(i in 1:I1){ for(j in 1:i){
     		k=k+1
     		 cormat1[i,j]=cormat1[j,i]=bl1cor[k]
     		 }}
     diag(cormat1)=1
     cormat1=bl1var*cormat1
	for(j in 1:J1){
		diag(cormat1)<-bl1var+runif(I1,0,0.1)
		bl1[,j]=rmvnorm(1,mbl1+rnorm(I1,0,0.1),cormat1)
	}
	
     ########Creating block 2
     mbl2=c(mbl1[23:30],sample(c(seq(-3,-1.5,by=0.25),seq(1.5,3,by=0.25)),12, replace=TRUE))
     bl2var=rep(AvgVarR,I2)
     bl2cor=rnorm(I2+choose(I2,2),cors[2],.001)

     cormat2=matrix(1,ncol=I2,nrow=I2)
     k=0
     for(i in 1:I2){ for(j in 1:i){
     		k=k+1
     		 cormat2[i,j]=cormat2[j,i]=bl2cor[k]
     		 }}
     diag(cormat2)=1
     cormat2=bl2var*cormat2
	for(j in 1:J2){
                diag(cormat2)<-bl2var+runif(I2,0,0.1) 
		bl2[,j]=rmvnorm(1,mbl2+rnorm(I2,0,0.1),cormat2)
	}
	
	########Creating block 3      
     mbl3=sample(c(seq(-3,-1.5,by=0.25),seq(1.5,3,by=0.25)),I3, replace=TRUE)
     bl3var=rep(AvgVarR,I3)
     bl3cor=rnorm(I3+choose(I3,2),cors[3],.001)

     cormat3=matrix(1,ncol=I3,nrow=I3)
     k=0
     for(i in 1:I3){ for(j in 1:i){
     		k=k+1
     		 cormat3[i,j]=cormat3[j,i]=bl3cor[k]
     		 }}
     diag(cormat3)=1
     cormat3=bl3var*cormat3
	for(j in 1:J3){
		diag(cormat3)<-bl3var+runif(I3,0,0.1)
		bl3[,j]=rmvnorm(1,mbl3+rnorm(I3,0,0.1),cormat3)
	}
	
     ########Creating block 4
     mbl4=c(mbl3[16:21],sample(c(seq(-3,-1.5,by=0.25),seq(1.5,3,by=0.25)),9, replace=TRUE))
     bl4var=rep(AvgVarR,I4)
     bl4cor=rnorm(I4+choose(I4,2),cors[4],.001)

     cormat4=matrix(1,ncol=I4,nrow=I4)
     k=0
     for(i in 1:I4){ for(j in 1:i){
     		k=k+1
     		 cormat4[i,j]=cormat4[j,i]=bl4cor[k]
     		 }}
     diag(cormat4)=1
     cormat4=bl4var*cormat4
	for(j in 1:J4){
                diag(cormat4)<-bl4var+runif(I4,0,0.1) 
		bl4[,j]=rmvnorm(1,mbl4+rnorm(I4,0,0.1),cormat4)
	}
###End block creation

     mallR[Itrue1,Jtrue1]=0
     mallR[Itrue2,Jtrue2]=0
mallR[Itrue3,Jtrue3]=0
mallR[Itrue4,Jtrue4]=0

     mallR[Itrue1,Jtrue1]=mallR[Itrue1,Jtrue1]+bl1
     mallR[Itrue2,Jtrue2]=mallR[Itrue2,Jtrue2]+bl2
     mallR[Itrue12,Jtrue12]=mallR[Itrue12,Jtrue12]/2
     mallR[Itrue3,Jtrue3]=mallR[Itrue3,Jtrue3]+bl3
     mallR[Itrue4,Jtrue4]=mallR[Itrue4,Jtrue4]+bl4
     mallR[Itrue34,Jtrue34]=mallR[Itrue34,Jtrue34]/2
     #image(mallR)
     EallR=mallR
     
     
     
     
###########CONSTANT OVERLAP ROW
J <- 40*2
J1 <- 15
J2 <- 10
J3 <- 21
J4 <- 25

I <- 100*2
I1 <- 30
I2 <- 20
I3 <- 21
I4 <- 15

n <- I*J
sb <- 1000

varscale <- 1


Itrue1=1:30
Itrue2=23:42
Itrue3=50:70
Itrue4=65:79
Jtrue1=1:15
Jtrue2=10:19
Jtrue3=30:50
Jtrue4=40:64

#overlap
Itrue12=23:30
Jtrue12=10:15
Itrue34=65:70
Jtrue34=40:50

cors=c(0.4,0.9,0.1,0.6)
     
     ####Created with row correlation
     
     bl1=matrix(NA,nrow=I1,ncol=J1)
     bl2=matrix(NA,nrow=I2,ncol=J2)
     bl3=matrix(NA,nrow=I3,ncol=J3)
     bl4=matrix(NA,nrow=I4,ncol=J4)
     
     ########Creating block 1      
     mbl1=sample(seq(-3,-1.5,by=0.25),I1, replace=TRUE)
     bl1var=rep(AvgVarR,I1)
     bl1cor=rnorm(I1+choose(I1,2),cors[1],.001)

     cormat1=matrix(1,ncol=I1,nrow=I1)
     k=0
     for(i in 1:I1){ for(j in 1:i){
     		k=k+1
     		 cormat1[i,j]=cormat1[j,i]=bl1cor[k]
     		 }}
     diag(cormat1)=1
     cormat1=bl1var*cormat1
	for(j in 1:J1){
		diag(cormat1)<-bl1var+runif(I1,0,0.1)
		bl1[,j]=rmvnorm(1,mbl1+rnorm(I1,0,0.1),cormat1)
	}
	
     ########Creating block 2
     mbl2=c(mbl1[23:30],sample(seq(-3,-1.5,by=0.25),12, replace=TRUE))
     bl2var=rep(AvgVarR,I2)
     bl2cor=rnorm(I2+choose(I2,2),cors[2],.001)

     cormat2=matrix(1,ncol=I2,nrow=I2)
     k=0
     for(i in 1:I2){ for(j in 1:i){
     		k=k+1
     		 cormat2[i,j]=cormat2[j,i]=bl2cor[k]
     		 }}
     diag(cormat2)=1
     cormat2=bl2var*cormat2
	for(j in 1:J2){
                diag(cormat2)<-bl2var+runif(I2,0,0.1) 
		bl2[,j]=rmvnorm(1,mbl2+rnorm(I2,0,0.1),cormat2)
	}
	
	########Creating block 3      
     mbl3=sample(seq(1.5,3,by=0.25),I3, replace=TRUE)
     bl3var=rep(AvgVarR,I3)
     bl3cor=rnorm(I3+choose(I3,2),cors[3],.001)

     cormat3=matrix(1,ncol=I3,nrow=I3)
     k=0
     for(i in 1:I3){ for(j in 1:i){
     		k=k+1
     		 cormat3[i,j]=cormat3[j,i]=bl3cor[k]
     		 }}
     diag(cormat3)=1
     cormat3=bl3var*cormat3
	for(j in 1:J3){
		diag(cormat3)<-bl3var+runif(I3,0,0.1)
		bl3[,j]=rmvnorm(1,mbl3+rnorm(I3,0,0.1),cormat3)
	}
	
     ########Creating block 4
     mbl4=c(mbl3[16:21],sample(seq(1.5,3,by=0.25),9, replace=TRUE))
     bl4var=rep(AvgVarR,I4)
     bl4cor=rnorm(I4+choose(I4,2),cors[4],.001)

     cormat4=matrix(1,ncol=I4,nrow=I4)
     k=0
     for(i in 1:I4){ for(j in 1:i){
     		k=k+1
     		 cormat4[i,j]=cormat4[j,i]=bl4cor[k]
     		 }}
     diag(cormat4)=1
     cormat4=bl4var*cormat4
	for(j in 1:J4){
                diag(cormat4)<-bl4var+runif(I4,0,0.1) 
		bl4[,j]=rmvnorm(1,mbl4+rnorm(I4,0,0.1),cormat4)
	}
###End block creation

     mallR[Itrue1,Jtrue1]=0
     mallR[Itrue2,Jtrue2]=0
	mallR[Itrue3,Jtrue3]=0
	mallR[Itrue4,Jtrue4]=0

     mallR[Itrue1,Jtrue1]=mallR[Itrue1,Jtrue1]+bl1
     mallR[Itrue2,Jtrue2]=mallR[Itrue2,Jtrue2]+bl2
     mallR[Itrue12,Jtrue12]=mallR[Itrue12,Jtrue12]/2
     mallR[Itrue3,Jtrue3]=mallR[Itrue3,Jtrue3]+bl3
     mallR[Itrue4,Jtrue4]=mallR[Itrue4,Jtrue4]+bl4
     mallR[Itrue34,Jtrue34]=mallR[Itrue34,Jtrue34]/2
     #image(mallR)
     EallR=mallR

     

     ####Created with column correlation

     ########Creating block 1
     mbl1=sample(c(seq(-3,-1.5,by=0.25),seq(1.5,3,by=0.25)),J1, replace=TRUE)
     bl1var=rep(AvgVarC,J1)
     bl1cor=rnorm(J1+choose(J1,2),cors[1],.001)

     cormat1=matrix(1,ncol=J1,nrow=J1)
     k=0
     for(i in 1:J1){
	for(j in 1:i){
     		k=k+1
     		 cormat1[i,j]=cormat1[j,i]=bl1cor[k]
     		 }
		 }
     diag(cormat1)=1
     cormat1=bl1var*cormat1
     bl1=rmvnorm(I1,mbl1,cormat1)

for(j in 1:I1){
		diag(cormat1)<-bl1var+runif(J1,0,0.1)
		bl1[j,]=rmvnorm(1,mbl1+rnorm(J1,0,0.1),cormat1)
	}
	
     ########Creating block 2
     mbl2=c(mbl1[10:15],sample(c(seq(-3,-1.5,by=0.25),seq(1.5,3,by=0.25)),4, replace=TRUE))
     bl2var=rep(AvgVarC,J2)
     bl2cor=rnorm(J2+choose(J2,2),cors[2],.001)

     cormat2=matrix(1,ncol=J2,nrow=J2)
     k=0
     for(i in 1:J2){ for(j in 1:i){
     		k=k+1
     		 cormat2[i,j]=cormat2[j,i]=bl2cor[k]
     		 }}
     diag(cormat2)=1
     cormat2=bl2var*cormat2
     bl2=rmvnorm(I2,mbl2,cormat2)
     
     for(j in 1:I2){
		diag(cormat2)<-bl2var+runif(J2,0,0.1)
		bl2[j,]=rmvnorm(1,mbl2+rnorm(J2,0,0.1),cormat2)
	}

     ########Creating block 3
     mbl3=sample(c(seq(-3,-1.5,by=0.25),seq(1.5,3,by=0.25)),J3, replace=TRUE)
     bl3var=rep(AvgVarC,J3)
     bl3cor=rnorm(J3+choose(J3,2),cors[3],.001)

     cormat3=matrix(1,ncol=J3,nrow=J3)
     k=0
     for(i in 1:J3){
	for(j in 1:i){
     		k=k+1
     		 cormat3[i,j]=cormat3[j,i]=bl3cor[k]
     		 }
		 }
     diag(cormat3)=1
     cormat3=bl3var*cormat3
     bl3=rmvnorm(I3,mbl3,cormat3)

for(j in 1:I3){
		diag(cormat3)<-bl3var+runif(J3,0,0.1)
		bl3[j,]=rmvnorm(1,mbl3+rnorm(J3,0,0.1),cormat3)
	}
	
     ########Creating block 4
     mbl4=c(mbl3[11:21],sample(c(seq(-3,-1.5,by=0.25),seq(1.5,3,by=0.25)),14, replace=TRUE))
     bl4var=rep(AvgVarC,J4)
     bl4cor=rnorm(J4+choose(J4,2),cors[4],.001)

     cormat4=matrix(1,ncol=J4,nrow=J4)
     k=0
     for(i in 1:J4){ for(j in 1:i){
     		k=k+1
     		 cormat4[i,j]=cormat4[j,i]=bl4cor[k]
     		 }}
     diag(cormat4)=1
     cormat4=bl4var*cormat4
     bl4=rmvnorm(I4,mbl4,cormat4)
     
     for(j in 1:I4){
		diag(cormat4)<-bl4var+runif(J4,0,0.1)
		bl4[j,]=rmvnorm(1,mbl4+rnorm(J4,0,0.1),cormat4)
	}


###End block creation
     mallC[Itrue1,Jtrue1]=0
     mallC[Itrue2,Jtrue2]=0
mallC[Itrue3,Jtrue3]=0
     mallC[Itrue4,Jtrue4]=0

     mallC[Itrue1,Jtrue1]=mallC[Itrue1,Jtrue1]+bl1
     mallC[Itrue2,Jtrue2]=mallC[Itrue2,Jtrue2]+bl2
     mallC[Itrue12,Jtrue12]=mallC[Itrue12,Jtrue12]/2
     mallC[Itrue3,Jtrue3]=mallC[Itrue3,Jtrue3]+bl3
     mallC[Itrue4,Jtrue4]=mallC[Itrue4,Jtrue4]+bl4
     mallC[Itrue34,Jtrue34]=mallC[Itrue34,Jtrue34]/2
     #image(mallC)
     EallC=mallC
     
     
     ###CONST OVERLAP COLUMN
    ####Created with column correlation

     ########Creating block 1
     mbl1=sample(seq(-3,-1.5,by=0.25),J1, replace=TRUE)
     bl1var=rep(AvgVarC,J1)
     bl1cor=rnorm(J1+choose(J1,2),cors[1],.001)

     cormat1=matrix(1,ncol=J1,nrow=J1)
     k=0
     for(i in 1:J1){
	for(j in 1:i){
     		k=k+1
     		 cormat1[i,j]=cormat1[j,i]=bl1cor[k]
     		 }
		 }
     diag(cormat1)=1
     cormat1=bl1var*cormat1
     bl1=rmvnorm(I1,mbl1,cormat1)

for(j in 1:I1){
		diag(cormat1)<-bl1var+runif(J1,0,0.1)
		bl1[j,]=rmvnorm(1,mbl1+rnorm(J1,0,0.1),cormat1)
	}
	
     ########Creating block 2
     mbl2=c(mbl1[10:15],sample(seq(-3,-1.5,by=0.25),4, replace=TRUE))
     bl2var=rep(AvgVarC,J2)
     bl2cor=rnorm(J2+choose(J2,2),cors[2],.001)

     cormat2=matrix(1,ncol=J2,nrow=J2)
     k=0
     for(i in 1:J2){ for(j in 1:i){
     		k=k+1
     		 cormat2[i,j]=cormat2[j,i]=bl2cor[k]
     		 }}
     diag(cormat2)=1
     cormat2=bl2var*cormat2
     bl2=rmvnorm(I2,mbl2,cormat2)
     
     for(j in 1:I2){
		diag(cormat2)<-bl2var+runif(J2,0,0.1)
		bl2[j,]=rmvnorm(1,mbl2+rnorm(J2,0,0.1),cormat2)
	}

     ########Creating block 3
     mbl3=sample(seq(1.5,3,by=0.25),J3, replace=TRUE)
     bl3var=rep(AvgVarC,J3)
     bl3cor=rnorm(J3+choose(J3,2),cors[3],.001)

     cormat3=matrix(1,ncol=J3,nrow=J3)
     k=0
     for(i in 1:J3){
	for(j in 1:i){
     		k=k+1
     		 cormat3[i,j]=cormat3[j,i]=bl3cor[k]
     		 }
		 }
     diag(cormat3)=1
     cormat3=bl3var*cormat3
     bl3=rmvnorm(I3,mbl3,cormat3)

for(j in 1:I3){
		diag(cormat3)<-bl3var+runif(J3,0,0.1)
		bl3[j,]=rmvnorm(1,mbl3+rnorm(J3,0,0.1),cormat3)
	}
	
     ########Creating block 4
     mbl4=c(mbl3[11:21],sample(seq(1.5,3,by=0.25),14, replace=TRUE))
     bl4var=rep(AvgVarC,J4)
     bl4cor=rnorm(J4+choose(J4,2),cors[4],.001)

     cormat4=matrix(1,ncol=J4,nrow=J4)
     k=0
     for(i in 1:J4){ for(j in 1:i){
     		k=k+1
     		 cormat4[i,j]=cormat4[j,i]=bl4cor[k]
     		 }}
     diag(cormat4)=1
     cormat4=bl4var*cormat4
     bl4=rmvnorm(I4,mbl4,cormat4)
     
     for(j in 1:I4){
		diag(cormat4)<-bl4var+runif(J4,0,0.1)
		bl4[j,]=rmvnorm(1,mbl4+rnorm(J4,0,0.1),cormat4)
	}


###End block creation
     mallC[Itrue1,Jtrue1]=0
     mallC[Itrue2,Jtrue2]=0
mallC[Itrue3,Jtrue3]=0
     mallC[Itrue4,Jtrue4]=0

     mallC[Itrue1,Jtrue1]=mallC[Itrue1,Jtrue1]+bl1
     mallC[Itrue2,Jtrue2]=mallC[Itrue2,Jtrue2]+bl2
     mallC[Itrue12,Jtrue12]=mallC[Itrue12,Jtrue12]/2
     mallC[Itrue3,Jtrue3]=mallC[Itrue3,Jtrue3]+bl3
     mallC[Itrue4,Jtrue4]=mallC[Itrue4,Jtrue4]+bl4
     mallC[Itrue34,Jtrue34]=mallC[Itrue34,Jtrue34]/2
     #image(mallC)
     EallC=mallC


###implanted blocks STATS
     
true1Rstats <- c(Corr.block(mallR,Itrue1,Jtrue1,2,useAbs=0),
Corr.block(mallR,Itrue1,Jtrue1,3,useAbs=0),
ExpCrit.block(mallR,Itrue1,Jtrue1,1,useAbs=0),
ExpCrit.block(mallR,Itrue1,Jtrue1,2,useAbs=0),
ExpCrit.block(mallR,Itrue1,Jtrue1,3,useAbs=0))

true2Rstats <- c(Corr.block(mallR,Itrue2,Jtrue2,2,useAbs=0),
Corr.block(mallR,Itrue2,Jtrue2,3,useAbs=0),
ExpCrit.block(mallR,Itrue2,Jtrue2,1,useAbs=0),
ExpCrit.block(mallR,Itrue2,Jtrue2,2,useAbs=0),
ExpCrit.block(mallR,Itrue2,Jtrue2,3,useAbs=0))

true3Rstats <- c(Corr.block(mallR,Itrue3,Jtrue3,2,useAbs=0),
Corr.block(mallR,Itrue3,Jtrue3,3,useAbs=0),
ExpCrit.block(mallR,Itrue3,Jtrue3,1,useAbs=0),
ExpCrit.block(mallR,Itrue3,Jtrue3,2,useAbs=0),
ExpCrit.block(mallR,Itrue3,Jtrue3,3,useAbs=0))

true4Rstats <- c(Corr.block(mallR,Itrue4,Jtrue4,2,useAbs=0),
Corr.block(mallR,Itrue4,Jtrue4,3,useAbs=0),
ExpCrit.block(mallR,Itrue4,Jtrue4,1,useAbs=0),
ExpCrit.block(mallR,Itrue4,Jtrue4,2,useAbs=0),
ExpCrit.block(mallR,Itrue4,Jtrue4,3,useAbs=0))

true1Cstats <- c(Corr.block(mallC,Itrue1,Jtrue1,2,useAbs=0),
Corr.block(mallC,Itrue1,Jtrue1,3,useAbs=0),
ExpCrit.block(mallC,Itrue1,Jtrue1,1,useAbs=0),
ExpCrit.block(mallC,Itrue1,Jtrue1,2,useAbs=0),
ExpCrit.block(mallC,Itrue1,Jtrue1,3,useAbs=0))

true2Cstats <- c(Corr.block(mallC,Itrue2,Jtrue2,2,useAbs=0),
Corr.block(mallC,Itrue2,Jtrue2,3,useAbs=0),
ExpCrit.block(mallC,Itrue2,Jtrue2,1,useAbs=0),
ExpCrit.block(mallC,Itrue2,Jtrue2,2,useAbs=0),
ExpCrit.block(mallC,Itrue2,Jtrue2,3,useAbs=0))

true3Cstats <- c(Corr.block(mallC,Itrue3,Jtrue3,2,useAbs=0),
Corr.block(mallC,Itrue3,Jtrue3,3,useAbs=0),
ExpCrit.block(mallC,Itrue3,Jtrue3,1,useAbs=0),
ExpCrit.block(mallC,Itrue3,Jtrue3,2,useAbs=0),
ExpCrit.block(mallC,Itrue3,Jtrue3,3,useAbs=0))

true4Cstats <- c(Corr.block(mallC,Itrue4,Jtrue4,2,useAbs=0),
Corr.block(mallC,Itrue4,Jtrue4,3,useAbs=0),
ExpCrit.block(mallC,Itrue4,Jtrue4,1,useAbs=0),
ExpCrit.block(mallC,Itrue4,Jtrue4,2,useAbs=0),
ExpCrit.block(mallC,Itrue4,Jtrue4,3,useAbs=0))

true1Rstats
true2Rstats
true3Rstats
true4Rstats

true1Cstats
true2Cstats
true3Cstats
true4Cstats

###ROW CONSTANT
> true1Rstats
[1] 0.3086085 0.3092734 0.9191549 0.7290920 0.7535374
> true2Rstats
[1] 0.7795186 0.5566799 0.9102836 0.7548115 0.4204861
> true3Rstats
[1] 0.07567166 0.30015309 0.86439988 0.69055395 0.91151608
> true4Rstats
[1] 0.5111758 0.4949974 0.9335427 0.6821346 0.6282414


###COL CONSTANT
> true1Cstats
[1] 0.2350582 0.3486122 0.7421059 0.6832134 0.8145372
> true2Cstats
[1] 0.6695982 0.8529046 1.1610157 0.3158804 0.7922260
> true3Cstats
[1] 0.34731160 0.07112205 0.88665455 0.92431648 0.65469982
> true4Cstats
[1] 0.3556234 0.5992658 0.8259530 0.5218406 0.7898300


#> true1Rstats
#[1] 0.4454948 0.9305840 5.6182237 0.1216653 0.9433723
#> true2Rstats
#[1] 0.8602673 0.9695381 4.8801203 0.1709482 0.8530144
#> true3Rstats
#[1] 0.1826889 0.8793220 4.6979829 0.1320678 0.9851598
#> true4Rstats
#[1] 0.4444001 0.9368544 5.0743759 0.1158094 0.9458167
#> 
#> true1Cstats
#[1] 0.9345751 0.3236712 5.9235435 0.9628106 0.1005005
#> true2Cstats
#[1] 0.9816978 0.7205415 6.9473093 0.9457157 0.0736298
#> true3Cstats
#[1] 0.92103648 0.19844644 5.62312714 0.98926055 0.08913088
#> true4Cstats
#[1] 0.93315346 0.35754432 4.60484756 0.96277816 0.09945815

#7/18/11
#> true1Rstats
#[1] 0.3377550 0.9205592 6.0034252 0.1116277 0.9622653
#> true2Rstats
#[1] 0.7795186 0.9678025 5.8224135 0.1180082 0.9093981
#> true3Rstats
#[1] 0.1828707 0.9118632 5.8640681 0.1017919 0.9869569
#> true4Rstats
#[1] 0.5111758 0.9537769 6.2097460 0.1025488 0.9441116
#> 
#> true1Cstats
#[1] 0.94000682 0.24446891 5.97172435 0.97651948 0.08315005
#> true2Cstats
#[1] 0.9771386 0.8117089 5.1473943 0.8991084 0.1227405
#> true3Cstats
#[1] 0.91584190 0.18114139 5.64558852 0.99461422 0.08916076
#> true4Cstats
#[1] 0.9512132 0.6446958 5.4742263 0.9197513 0.1229480
#> 

     ###########Interaction Data

     ######Prob=1
     probI=1
     Iall1=matrix(rbinom(I*I,1,mean(interact_data)),ncol=I)
     Ib1=matrix(rbinom(I1*I1,1,probI),ncol=I1)
     Ib2=matrix(rbinom(I2*I2,1,probI),ncol=I2)
     Ib12=matrix(rbinom((8)^2,1,probI),ncol=8)
     Ib3=matrix(rbinom(I3*I3,1,probI),ncol=I3)
     Ib4=matrix(rbinom(I4*I4,1,probI),ncol=I4)
     Ib34=matrix(rbinom((6)^2,1,probI),ncol=6)

     Iall1[Itrue1,Itrue1]=Ib1
     Iall1[Itrue2,Itrue2]=Ib2
     Iall1[Itrue12,Itrue12]=Ib12
     Iall1[Itrue3,Itrue3]=Ib3
     Iall1[Itrue4,Itrue4]=Ib4
     Iall1[Itrue34,Itrue34]=Ib34

     Iall1a=Iall1
     for(i in 1:I){ for(j in 1:i){
     		 Iall1[i,j]=Iall1[j,i]
     		 }}
     diag(Iall1)=diag(Iall1a)
     #image(Iall1)


     ######Prob=.25
     probI=0.25
     Iall25=matrix(rbinom(I*I,1,mean(interact_data)),ncol=I)
     Ib1=matrix(rbinom(I1*I1,1,probI),ncol=I1)
     Ib2=matrix(rbinom(I2*I2,1,probI),ncol=I2)
     Ib12=matrix(rbinom((8)^2,1,probI),ncol=8)
     Ib3=matrix(rbinom(I3*I3,1,probI),ncol=I3)
     Ib4=matrix(rbinom(I4*I4,1,probI),ncol=I4)
     Ib34=matrix(rbinom((6)^2,1,probI),ncol=6)

     Iall25[Itrue1,Itrue1]=Ib1
     Iall25[Itrue2,Itrue2]=Ib2
     Iall25[Itrue12,Itrue12]=Ib12
     Iall25[Itrue3,Itrue3]=Ib3
     Iall25[Itrue4,Itrue4]=Ib4
     Iall25[Itrue34,Itrue34]=Ib34
     
     Iall25a=Iall25
     for(i in 1:I){ for(j in 1:i){
     		 Iall25[i,j]=Iall25[j,i]
     		 }}
     diag(Iall25)=diag(Iall25a)
     #image(Iall25)


     ######Prob=.05
     probI=0.05
     Iall05=matrix(rbinom(I*I,1,mean(interact_data)),ncol=I)
     Ib1=matrix(rbinom(I1*I1,1,probI),ncol=I1)
     Ib2=matrix(rbinom(I2*I2,1,probI),ncol=I2)
     Ib12=matrix(rbinom((8)^2,1,probI),ncol=8)
     Ib3=matrix(rbinom(I3*I3,1,probI),ncol=I3)
     Ib4=matrix(rbinom(I4*I4,1,probI),ncol=I4)
     Ib34=matrix(rbinom((6)^2,1,probI),ncol=6)
     
     Iall05[Itrue1,Itrue1]=Ib1
     Iall05[Itrue2,Itrue2]=Ib2
     Iall05[Itrue12,Itrue12]=Ib12
     Iall05[Itrue3,Itrue3]=Ib3
     Iall05[Itrue4,Itrue4]=Ib4
     Iall05[Itrue34,Itrue34]=Ib34
     
     Iall05a=Iall05
     for(i in 1:I){ for(j in 1:i){
     		 Iall05[i,j]=Iall05[j,i]
     		 }}
     diag(Iall05)=diag(Iall05a)
     #image(Iall25)

     save(I,J,Iall1,Iall25,Iall05,EallC,EallR,Itrue1,Itrue2,Itrue12,Jtrue1,Jtrue2,Jtrue12,Itrue3,Itrue4,Itrue34,Jtrue3,Jtrue4,Jtrue34,cors,file="fake110427_4_incr_over")

#####
#####Now create the randomized version

     set.seed(9729)
     Isample=sample(1:I,I)
     Jsample=sample(1:J,J)

     Itrue1R=match(Itrue1,Isample)
     Itrue2R=match(Itrue2,Isample)
     Itrue12R=match(Itrue12,Isample)
     Itrue3R=match(Itrue3,Isample)
     Itrue4R=match(Itrue4,Isample)
     Itrue34R=match(Itrue34,Isample)
     Jtrue1R=match(Jtrue1,Jsample)
     Jtrue2R=match(Jtrue2,Jsample)
     Jtrue12R=match(Jtrue12,Jsample)
     Jtrue3R=match(Jtrue3,Jsample)
     Jtrue4R=match(Jtrue4,Jsample)
     Jtrue34R=match(Jtrue34,Jsample)
     
     EallCrand=EallC[Isample,Jsample]
     EallRrand=EallR[Isample,Jsample]
     Iall1rand=Iall1[Isample,Isample]
     Iall25rand=Iall25[Isample,Isample]
     Iall05rand=Iall05[Isample,Isample]


write.table(EallRrand, file="fake110427_4r_incr_over_rand_expr.txt",sep="\t")
write.table(EallCrand, file="fake110427_4c_incr_over_rand_expr.txt",sep="\t")

write.table(EallRrand, file="fake110427_4r_const_over_expr_rand.txt",sep="\t")
write.table(EallCrand, file="fake110427_4c_const_over_expr_rand.txt",sep="\t")

expr_data <- EallRrand
interact_data <- Iall05rand
save(I,J,interact_data,expr_data,file="fake110427_4r_incr_over_0.05ppi_rand")

expr_data <- EallCrand
interact_data <- Iall05rand
save(I,J,interact_data,expr_data,file="fake110427_4c_incr_over_0.05ppi_rand")

expr_data <- EallRrand
interact_data <- Iall25rand
save(I,J,interact_data,expr_data,file="fake110427_4r_incr_over_0.25ppi_rand")

expr_data <- EallCrand
interact_data <- Iall25rand
save(I,J,interact_data,expr_data,file="fake110427_4c_incr_over_0.25ppi_rand")

###CONST
expr_data <- EallRrand
save(I,J,expr_data,file="fake110427_4r_const_over_0.25ppi_rand")

expr_data <- EallCrand
save(I,J,expr_data,file="fake110427_4c_const_over_0.25ppi_rand")


###ROW
range <- range(EallRrand)
unit <- (range[2]-range[1])/12
breaks <- seq(from=range[1],to=range[2],by=unit)

##Quick check
pdf("EallR_const_over_rand.pdf",height=11, width=8.5)
par(mfrow=c(3,2))
image(EallRrand[Itrue1R,Jtrue1R], breaks=breaks)
image(EallRrand[Itrue2R,Jtrue2R], breaks=breaks)
image(EallRrand[Itrue3R,Jtrue3R], breaks=breaks)
image(EallRrand[Itrue4R,Jtrue4R], breaks=breaks)
image(EallRrand[Itrue12R,Jtrue12R], breaks=breaks)
image(EallRrand[Itrue34R,Jtrue34R], breaks=breaks)
dev.off(2)

pdf("EallR_const_over_rand_all.pdf",height=11, width=8.5)
image(EallRrand)
dev.off(2)

pdf("EallR_const_over.pdf",height=11, width=8.5)
image(EallR)
dev.off(2)

###COL
range <- range(EallCrand)
unit <- (range[2]-range[1])/12
breaks <- seq(from=range[1],to=range[2],by=unit)

##Quick check
pdf("EallC_const_over_rand.pdf",height=11, width=8.5)
par(mfrow=c(3,2))
image(EallCrand[Itrue1R,Jtrue1R], breaks=breaks)
image(EallCrand[Itrue2R,Jtrue2R], breaks=breaks)
image(EallCrand[Itrue3R,Jtrue3R], breaks=breaks)
image(EallCrand[Itrue4R,Jtrue4R], breaks=breaks)
image(EallCrand[Itrue12R,Jtrue12R], breaks=breaks)
image(EallCrand[Itrue34R,Jtrue34R], breaks=breaks)
dev.off(2)

pdf("EallC_const_over_rand_all.pdf",height=11, width=8.5)
image(EallCrand)
dev.off(2)

pdf("EallC_const_over.pdf",height=11, width=8.5)
image(EallC)
dev.off(2)



Rmeans <- rowMeans(EallRrand)
Rsd <- apply(EallRrand, 1, sd)
EallRrandnorm <- (EallRrand - Rmeans) /Rsd

Cmeans <- rowMeans(EallCrand)
Csd <- apply(EallCrand, 1, sd)
EallCrandnorm <- (EallCrand - Cmeans) /Csd


true1RRstats <- c(Corr.block(EallRrand,Itrue1R,Jtrue1R,2),
Corr.block(EallRrand,Itrue1R,Jtrue1R,3),
ExpCrit.block(EallRrand,Itrue1R,Jtrue1R,1),
ExpCrit.block(EallRrand,Itrue1R,Jtrue1R,2),
ExpCrit.block(EallRrand,Itrue1R,Jtrue1R,3))

true2RRstats <- c(Corr.block(EallRrand,Itrue2R,Jtrue2R,2),
Corr.block(EallRrand,Itrue2R,Jtrue2R,3),
ExpCrit.block(EallRrand,Itrue2R,Jtrue2R,1),
ExpCrit.block(EallRrand,Itrue2R,Jtrue2R,2),
ExpCrit.block(EallRrand,Itrue2R,Jtrue2R,3))

true3RRstats <- c(Corr.block(EallRrand,Itrue3R,Jtrue3R,2),
Corr.block(EallRrand,Itrue3R,Jtrue3R,3),
ExpCrit.block(EallRrand,Itrue3R,Jtrue3R,1),
ExpCrit.block(EallRrand,Itrue3R,Jtrue3R,2),
ExpCrit.block(EallRrand,Itrue3R,Jtrue3R,3))

true4RRstats <- c(Corr.block(EallRrand,Itrue4R,Jtrue4R,2),
Corr.block(EallRrand,Itrue4R,Jtrue4R,3),
ExpCrit.block(EallRrand,Itrue4R,Jtrue4R,1),
ExpCrit.block(EallRrand,Itrue4R,Jtrue4R,2),
ExpCrit.block(EallRrand,Itrue4R,Jtrue4R,3))

true1RCstats <- c(Corr.block(EallCrand,Itrue1R,Jtrue1R,2),
Corr.block(EallCrand,Itrue1R,Jtrue1R,3),
ExpCrit.block(EallCrand,Itrue1R,Jtrue1R,1),
ExpCrit.block(EallCrand,Itrue1R,Jtrue1R,2),
ExpCrit.block(EallCrand,Itrue1R,Jtrue1R,3))

true2RCstats <- c(Corr.block(EallCrand,Itrue2R,Jtrue2R,2),
Corr.block(EallCrand,Itrue2R,Jtrue2R,3),
ExpCrit.block(EallCrand,Itrue2R,Jtrue2R,1),
ExpCrit.block(EallCrand,Itrue2R,Jtrue2R,2),
ExpCrit.block(EallCrand,Itrue2R,Jtrue2R,3))

true3RCstats <- c(Corr.block(EallCrand,Itrue3R,Jtrue3R,2),
Corr.block(EallCrand,Itrue3R,Jtrue3R,3),
ExpCrit.block(EallCrand,Itrue3R,Jtrue3R,1),
ExpCrit.block(EallCrand,Itrue3R,Jtrue3R,2),
ExpCrit.block(EallCrand,Itrue3R,Jtrue3R,3))

true4RCstats <- c(Corr.block(EallCrand,Itrue4R,Jtrue4R,2),
Corr.block(EallCrand,Itrue4R,Jtrue4R,3),
ExpCrit.block(EallCrand,Itrue4R,Jtrue4R,1),
ExpCrit.block(EallCrand,Itrue4R,Jtrue4R,2),
ExpCrit.block(EallCrand,Itrue4R,Jtrue4R,3))

true1RRstats
true2RRstats
true3RRstats
true4RRstats

true1RCstats
true2RCstats
true3RCstats
true4RCstats

#> true1RRstats
#[1] 0.4454948 0.9305840 5.6182237 0.1216653 0.9433723
#> true2RRstats
#[1] 0.8602673 0.9695381 4.8801203 0.1709482 0.8530144
#> true3RRstats
#[1] 0.1826889 0.8793220 4.6979829 0.1320678 0.9851598
#> true4RRstats
#[1] 0.4444001 0.9368544 5.0743759 0.1158094 0.9458167
#> 
#> true1RCstats
#[1] 0.9345751 0.3236712 5.9235435 0.9628106 0.1005005
#> true2RCstats
#[1] 0.9816978 0.7205415 6.9473093 0.9457157 0.0736298
#> true3RCstats
#[1] 0.92103648 0.19844644 5.62312714 0.98926055 0.08913088
#> true4RCstats
#[1] 0.93315346 0.35754432 4.60484756 0.96277816 0.09945815


#7/18/11
#> true1RRstats
#[1] 0.3377550 0.9205592 6.0034252 0.1116277 0.9622653
#> true2RRstats
#[1] 0.7795186 0.9678025 5.8224135 0.1180082 0.9093981
#> true3RRstats
#[1] 0.1828707 0.9118632 5.8640681 0.1017919 0.9869569
#> true4RRstats
#[1] 0.5111758 0.9537769 6.2097460 0.1025488 0.9441116
#> 
#> true1RCstats
#[1] 0.94000682 0.24446891 5.97172435 0.97651948 0.08315005
#> true2RCstats
#[1] 0.9771386 0.8117089 5.1473943 0.8991084 0.1227405
#> true3RCstats
#[1] 0.91584190 0.18114139 5.64558852 0.99461422 0.08916076
#> true4RCstats
#[1] 0.9512132 0.6446958 5.4742263 0.9197513 0.1229480

##Quick check
pdf("fakec_over_rand_blocks.pdf",width=11, height=8.5)
par(mfrow=c(1,2))
image(EallCrand[Itrue1R,Jtrue1R])
image(EallCrand[Itrue2R,Jtrue2R])
image(EallCrand[Itrue3R,Jtrue3R])
image(EallCrand[Itrue4R,Jtrue4R])
dev.off(2)

pdf("faker_over_rand_blocks.pdf",width=11, height=8.5)
par(mfrow=c(1,2))
image(EallRrand[Itrue1R,Jtrue1R])
image(EallRrand[Itrue2R,Jtrue2R])
image(EallRrand[Itrue3R,Jtrue3R])
image(EallRrand[Itrue4R,Jtrue4R])
dev.off(2)

save(I,J,Iall1rand,Iall25rand,Iall05rand,EallCrand,EallRrand,Itrue1R,Itrue2R,Itrue12R,Itrue3R,Itrue4R,Itrue34R,Jtrue1R,Jtrue2R,Jtrue12R,Jtrue3R,Jtrue4R,Jtrue34R,file="fake110427_4_incr_over_rand")

write.table(EallRrandnorm, file="fake110427_4r_incr_over_rand_expr_norm.txt",sep="\t")
write.table(Iall05rand, file="fake110427_4r_incr_over_0.05ppi_rand_ppi.txt",sep="\t")
write.table(Iall25rand, file="fake110427_4r_incr_over_0.25ppi_rand_ppi.txt",sep="\t")
write.table(EallCrandnorm, file="fake110427_4c_incr_over_rand_expr_norm.txt",sep="\t")
write.table(Iall05rand, file="fake110427_4c_incr_over_0.05ppi_rand_ppi.txt",sep="\t")
write.table(Iall25rand, file="fake110427_4c_incr_over_0.25ppi_rand_ppi.txt",sep="\t")

expr_data <- EallRrandnorm
interact_data <- Iall05rand
save(I,J,interact_data,expr_data,file="fake110427_4r_incr_over_0.05ppi_rand_norm")

expr_data <- EallCrandnorm
interact_data <- Iall05rand
save(I,J,interact_data,expr_data,file="fake110427_4c_incr_over_0.05ppi_rand_norm")

expr_data <- EallRrandnorm
interact_data <- Iall25rand
save(I,J,interact_data,expr_data,file="fake110427_4r_incr_over_0.25ppi_rand_norm")

expr_data <- EallCrandnorm
interact_data <- Iall25rand
save(I,J,interact_data,expr_data,file="fake110427_4c_incr_over_0.25ppi_rand_norm")

pdf("faker_over_expr.pdf",width=11, height=8.5)
par(mfrow=c(1,2))
image(1:80,1:200,t(EallR),xlab="Experiments",ylab="Genes",main="EallR")
image(1:80,1:200,t(EallRrand),xlab="Experiments",ylab="Genes",main="EallR random")
dev.off(2)
pdf("fakec_over_expr.pdf",width=11, height=8.5)
par(mfrow=c(1,2))
image(1:80,1:200,t(EallC),xlab="Experiments",ylab="Genes",main="EallC")
image(1:80,1:200,t(EallCrand),xlab="Experiments",ylab="Genes",main="EallC random")
dev.off(2)

pdf("fake_over_ppi.pdf",height=11, width=8.5)
par(mfrow=c(2,3))
image(1:200,1:200,t(Iall1),xlab="Genes",ylab="Genes",main="Interation 1")
image(1:200,1:200,t(Iall25),xlab="Genes",ylab="Genes",main="Interation .25")
image(1:200,1:200,t(Iall05),xlab="Genes",ylab="Genes",main="Interation .05")
image(1:200,1:200,t(Iall1rand),xlab="Genes",ylab="Genes",main="Interation 1 random")
image(1:200,1:200,t(Iall25rand),xlab="Genes",ylab="Genes",main="Interation .25 random")
image(1:200,1:200,t(Iall05rand),xlab="Genes",ylab="Genes",main="Interation .05 random")
dev.off(2)