
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

I <- 100*2
I1 <- 30
I2 <- 20

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

I <- 100*2
I1 <- 30
I2 <- 20

n <- I*J
sb <- 1000

varscale <- 1


Itrue1=1:30
Itrue2=23:42
Jtrue1=1:15
Jtrue2=10:19

#overlap
Itrue12=23:30
Jtrue12=10:15

cors=c(0.5,0.8)
     
     ####Created with row correlation
     ##creating block one
     bl1=matrix(NA,nrow=I1,ncol=J1)
     bl2=matrix(NA,nrow=I2,ncol=J2)
     
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
     #bl1=t(rmvnorm(J1,mbl1,cormat1))
	for(j in 1:J1){
		diag(cormat1)<-bl1var+runif(I1,0,0.1)
		bl1[,j]=rmvnorm(1,mbl1+rnorm(I1,0,0.1),cormat1)
	}
	
     ########Creating block 2
     #mbl1=sample(c(-4:-2,2:4),J2, replace=TRUE)
     mbl2=c(mbl1[23:30],sample(c(seq(-4,-2,by=0.25),seq(2,4,by=0.25)),(12), replace=TRUE))
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
     #bl2=t(rmvnorm(J2,mbl2,cormat2))
	for(j in 1:J2){
                diag(cormat2)<-bl2var+runif(I2,0,0.1) 
		bl2[,j]=rmvnorm(1,mbl2+rnorm(I2,0,0.1),cormat2)#,0,10
	}

     mallR[Itrue1,Jtrue1]=0
     mallR[Itrue2,Jtrue2]=0

     mallR[Itrue1,Jtrue1]=mallR[Itrue1,Jtrue1]+bl1
     mallR[Itrue2,Jtrue2]=mallR[Itrue2,Jtrue2]+bl2
     mallR[Itrue12,Jtrue12]=mallR[Itrue12,Jtrue12]/2
     #image(mallR)
     EallR=mallR

     ####Created with column correlation
     ##creating block one
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
     #mbl1=sample(c(-4:-2,2:4),J2, replace=TRUE)
     mbl2=c(mbl1[10:15],sample(c(seq(-4,-2,by=0.25),seq(2,4,by=0.25)),(4), replace=TRUE))
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

     mallC[Itrue1,Jtrue1]=0
     mallC[Itrue2,Jtrue2]=0

     mallC[Itrue1,Jtrue1]=mallC[Itrue1,Jtrue1]+bl1
     mallC[Itrue2,Jtrue2]=mallC[Itrue2,Jtrue2]+bl2
     mallC[Itrue12,Jtrue12]=mallC[Itrue12,Jtrue12]/2
     #image(mallC)
     EallC=mallC
     
AbsCorr.block(mallR,Itrue1,Jtrue1,2)
AbsCorr.block(mallR,Itrue1,Jtrue1,3)
ExpCrit.block(mallR,Itrue1,Jtrue1,1)
ExpCrit.block(mallR,Itrue1,Jtrue1,2)
ExpCrit.block(mallR,Itrue1,Jtrue1,3)

#> AbsCorr.block(mallR,Itrue1,Jtrue1,2)
#[1] 0.4413821
#> AbsCorr.block(mallR,Itrue1,Jtrue1,3)
#[1] 0.9372617
#> ExpCrit.block(mallR,Itrue1,Jtrue1,1)
#[1] 5.595912
#> ExpCrit.block(mallR,Itrue1,Jtrue1,2)
#[1] 0.1056560
#> ExpCrit.block(mallR,Itrue1,Jtrue1,3)
#[1] 0.9524666

AbsCorr.block(mallR,Itrue2,Jtrue2,2)
AbsCorr.block(mallR,Itrue2,Jtrue2,3)
ExpCrit.block(mallR,Itrue2,Jtrue2,1)
ExpCrit.block(mallR,Itrue2,Jtrue2,2)
ExpCrit.block(mallR,Itrue2,Jtrue2,3)

#> AbsCorr.block(mallR,Itrue2,Jtrue2,2)
#[1] 0.6290087
#> AbsCorr.block(mallR,Itrue2,Jtrue2,3)
#[1] 0.9775726
#> ExpCrit.block(mallR,Itrue2,Jtrue2,1)
#[1] 7.762811
#> ExpCrit.block(mallR,Itrue2,Jtrue2,2)
#[1] 0.05792616
#> ExpCrit.block(mallR,Itrue2,Jtrue2,3)
#[1] 0.9630672

AbsCorr.block(mallC,Itrue1,Jtrue1,2)
AbsCorr.block(mallC,Itrue1,Jtrue1,3)
ExpCrit.block(mallC,Itrue1,Jtrue1,1)
ExpCrit.block(mallC,Itrue1,Jtrue1,2)
ExpCrit.block(mallC,Itrue1,Jtrue1,3)

#> AbsCorr.block(mallC,Itrue1,Jtrue1,2)
#[1] 0.9547363
#> AbsCorr.block(mallC,Itrue1,Jtrue1,3)
#[1] 0.506219
#> ExpCrit.block(mallC,Itrue1,Jtrue1,1)
#[1] 6.699234
#> ExpCrit.block(mallC,Itrue1,Jtrue1,2)
#[1] 0.948573
#> ExpCrit.block(mallC,Itrue1,Jtrue1,3)
#[1] 0.0956533

AbsCorr.block(mallC,Itrue2,Jtrue2,2)
AbsCorr.block(mallC,Itrue2,Jtrue2,3)
ExpCrit.block(mallC,Itrue2,Jtrue2,1)
ExpCrit.block(mallC,Itrue2,Jtrue2,2)
ExpCrit.block(mallC,Itrue2,Jtrue2,3)

#> AbsCorr.block(mallC,Itrue2,Jtrue2,2)
#[1] 0.9797137
#> AbsCorr.block(mallC,Itrue2,Jtrue2,3)
#[1] 0.6927773
#> ExpCrit.block(mallC,Itrue2,Jtrue2,1)
#[1] 7.73611
#> ExpCrit.block(mallC,Itrue2,Jtrue2,2)
#[1] 0.9466634
#> ExpCrit.block(mallC,Itrue2,Jtrue2,3)
#[1] 0.07382697




     ###########Interaction Data

     ######Prob=1
     probI=1
     Iall1=matrix(rbinom(I*I,1,mean(interact_data)),ncol=I)
     Ib1=matrix(rbinom(I1*I1,1,probI),ncol=I1)
     Ib2=matrix(rbinom(I2*I2,1,probI),ncol=I2)
     Ib12=matrix(rbinom((8)^2,1,probI),ncol=8)

     Iall1[Itrue1,Itrue1]=Ib1
     Iall1[Itrue2,Itrue2]=Ib2
     Iall1[Itrue12,Itrue12]=Ib12

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

     Iall25[Itrue1,Itrue1]=Ib1
     Iall25[Itrue2,Itrue2]=Ib2
     Iall25[Itrue12,Itrue12]=Ib12

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

     Iall05[Itrue1,Itrue1]=Ib1
     Iall05[Itrue2,Itrue2]=Ib2
     Iall05[Itrue12,Itrue12]=Ib12

     Iall05a=Iall05
     for(i in 1:I){ for(j in 1:i){
     		 Iall05[i,j]=Iall05[j,i]
     		 }}
     diag(Iall05)=diag(Iall05a)
     #image(Iall25)

     save(I,J,Iall1,Iall25,Iall05,EallC,EallR,Itrue1,Itrue2,Itrue12,Jtrue1,Jtrue2,Jtrue12,cors,file="fake110427_4_incr_over")

     #####Now we create the randomized version

     set.seed(9729)
     Isample=sample(1:I,I)
     Jsample=sample(1:J,J)

     Itrue1R=match(Itrue1,Isample)
     Itrue2R=match(Itrue2,Isample)
     Itrue12R=match(Itrue12,Isample)
     Jtrue1R=match(Jtrue1,Jsample)
     Jtrue2R=match(Jtrue2,Jsample)
     Jtrue12R=match(Jtrue12,Jsample)

     EallCrand=EallC[Isample,Jsample]
     EallRrand=EallR[Isample,Jsample]
     Iall1rand=Iall1[Isample,Isample]
     Iall25rand=Iall25[Isample,Isample]
     Iall05rand=Iall05[Isample,Isample]


write.table(EallRrand, file="fake110427_4r_incr_over_rand_expr.txt",sep="\t")
write.table(EallCrand, file="fake110427_4c_incr_over_rand_expr.txt",sep="\t")

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

Rmeans <- rowMeans(EallRrand)
Rsd <- apply(EallRrand, 1, sd)
EallRrandnorm <- (EallRrand - Rmeans) /Rsd

Cmeans <- rowMeans(EallCrand)
Csd <- apply(EallCrand, 1, sd)
EallCrandnorm <- (EallCrand - Cmeans) /Csd

AbsCorr.block(EallRrand,Itrue1R,Jtrue1R,2)
AbsCorr.block(EallRrand,Itrue1R,Jtrue1R,3)
ExpCrit.block(EallRrand,Itrue1R,Jtrue1R,1)
ExpCrit.block(EallRrand,Itrue1R,Jtrue1R,2)
ExpCrit.block(EallRrand,Itrue1R,Jtrue1R,3)

#> AbsCorr.block(EallRrand,Itrue1R,Jtrue1R,2)
#[1] 0.4413821
#> AbsCorr.block(EallRrand,Itrue1R,Jtrue1R,3)
#[1] 0.8881472
#> ExpCrit.block(EallRrand,Itrue1R,Jtrue1R,1)
#[1] 2.576982
#> ExpCrit.block(EallRrand,Itrue1R,Jtrue1R,2)
#[1] 0.1793749
#> ExpCrit.block(EallRrand,Itrue1R,Jtrue1R,3)
#[1] 0.9212259

AbsCorr.block(EallRrand,Itrue2R,Jtrue2R,2)
AbsCorr.block(EallRrand,Itrue2R,Jtrue2R,3)
ExpCrit.block(EallRrand,Itrue2R,Jtrue2R,1)
ExpCrit.block(EallRrand,Itrue2R,Jtrue2R,2)
ExpCrit.block(EallRrand,Itrue2R,Jtrue2R,3)

#> AbsCorr.block(EallRrand,Itrue2R,Jtrue2R,2)
#[1] 0.6290087
#> AbsCorr.block(EallRrand,Itrue2R,Jtrue2R,3)
#[1] 0.9611669
#> ExpCrit.block(EallRrand,Itrue2R,Jtrue2R,1)
#[1] 3.288626
#> ExpCrit.block(EallRrand,Itrue2R,Jtrue2R,2)
#[1] 0.09639623
#> ExpCrit.block(EallRrand,Itrue2R,Jtrue2R,3)
#[1] 0.9392002

AbsCorr.block(EallCrand,Itrue1R,Jtrue1R,2)
AbsCorr.block(EallCrand,Itrue1R,Jtrue1R,3)
ExpCrit.block(EallCrand,Itrue1R,Jtrue1R,1)
ExpCrit.block(EallCrand,Itrue1R,Jtrue1R,2)
ExpCrit.block(EallCrand,Itrue1R,Jtrue1R,3)

#> AbsCorr.block(EallCrand,Itrue1R,Jtrue1R,2)
#[1] 0.9547363
#> AbsCorr.block(EallCrand,Itrue1R,Jtrue1R,3)
#[1] 0.3680845
#> ExpCrit.block(EallCrand,Itrue1R,Jtrue1R,1)
#[1] 3.495902
#> ExpCrit.block(EallCrand,Itrue1R,Jtrue1R,2)
#[1] 0.96766
#> ExpCrit.block(EallCrand,Itrue1R,Jtrue1R,3)
#[1] 0.07818561

AbsCorr.block(EallCrand,Itrue2R,Jtrue2R,2)
AbsCorr.block(EallCrand,Itrue2R,Jtrue2R,3)
ExpCrit.block(EallCrand,Itrue2R,Jtrue2R,1)
ExpCrit.block(EallCrand,Itrue2R,Jtrue2R,2)
ExpCrit.block(EallCrand,Itrue2R,Jtrue2R,3)

#> AbsCorr.block(EallCrand,Itrue2R,Jtrue2R,2)
#[1] 0.9797137
#> AbsCorr.block(EallCrand,Itrue2R,Jtrue2R,3)
##[1] 0.5128111
#> ExpCrit.block(EallCrand,Itrue2R,Jtrue2R,1)
#[1] 4.329976
#> ExpCrit.block(EallCrand,Itrue2R,Jtrue2R,2)
#[1] 0.9599598
#> ExpCrit.block(EallCrand,Itrue2R,Jtrue2R,3)
#[1] 0.07213099



##Quick check
pdf("fakec_over_rand_blocks.pdf",width=11, height=8.5)
par(mfrow=c(1,2))
image(EallCrand[Itrue1R,Jtrue1R])
image(EallCrand[Itrue2R,Jtrue2R])
dev.off(2)

pdf("faker_over_rand_blocks.pdf",width=11, height=8.5)
par(mfrow=c(1,2))
image(EallRrand[Itrue1R,Jtrue1R])
image(EallRrand[Itrue2R,Jtrue2R])
dev.off(2)

save(I,J,Iall1rand,Iall25rand,Iall05rand,EallCrand,EallRrand,Itrue1R,Itrue2R,Itrue12R,Jtrue1R,Jtrue2R,Jtrue12R,file="fake110427_4_incr_over_rand_norm")

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
