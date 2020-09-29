
###Created from Fakeexpr_data621_5.R, this one is for rows, where rows are correlated,
###and each column is a draw of a set of genes with a set correlation structure

rm(list=ls())
setwd("~/integr8functgenom/Miner/rda/common/yeast_data/")
source("~/Genomics/html/applet/MicroArrayUp/MicroArrayUp/DataMining_R/Miner.R")
load("yeast_cmonkey")

#> max(expr_data)
#[1] 93.61678
#> min(expr_data)
#[1] -73.21265

expr_datarow <- apply(expr_data,1,missfxn)
expr_datacol <- apply(expr_data,2,missfxn)
datadimrow <- dim(expr_datarow)
datadimcol <- dim(expr_datacol)

library(mvtnorm)
set.seed(18274)

###########Sim-ed experiements
J <- 40*2
J1 <- 15
J2 <- 10

I <- 100*2
I1 <- 30
I2 <- 20

n <- I*J
sb <- 1000

varscale <- 1#000

#columns  <-  J
m1b <- matrix(0,nrow=sb,ncol=J)
cov1 <- cov(expr_datacol[,sample(1:datadimcol[2],J)])
cor1 <- cor(expr_datacol[,sample(1:datadimcol[2],J)])
m1 <- m1b[1,] <- colMeans(expr_datacol[,sample(1:datadimcol[2],J)])
block_median_col <- mat.or.vec(sb,0)
block_median_col_R <- mat.or.vec(sb,0)
block_col_MSE <- mat.or.vec(sb,0)
block_col_MSER <- mat.or.vec(sb,0)
block_col_MSEC <- mat.or.vec(sb,0)
for(b in 1:(sb-1)){
print(paste(b, sb))
	d1 <- expr_datacol[,sample(1:datadimcol[2],J)]	
	cov1 <- cov1+cov(d1)
	cor1 <- cor1+cor(d1)
	meanvec <- colMeans(d1)
	m1 <- m1+meanvec
	m1b[b+1,] <- meanvec
	gene_ind <- sample(1:datadimcol[1],I)
	exp_ind <- sample(1:datadimcol[2],J)
	block_median_col[b] <- Corr.block(expr_datacol,gene_ind,exp_ind,3,useAbs=0)
	block_median_col_R[b] <- Corr.block(expr_datacol,gene_ind,exp_ind,2,useAbs=0)
	block_col_MSE[b] <- ExpCrit.block(expr_datacol,gene_ind,exp_ind,1,useAbs=0)
        block_col_MSER[b] <- ExpCrit.block(expr_datacol,gene_ind,exp_ind,2,useAbs=0)
        block_col_MSEC[b] <- ExpCrit.block(expr_datacol,gene_ind,exp_ind,3,useAbs=0)
	}

#> print(mean(block_median_col))
#[1] 0.1564148
#> print(mean(block_median_col_R))
#[1] 0.1761721
#> print(mean(block_col_MSE))
#[1] 0.6879593
#> print(mean(block_col_MSER))
#[1] 0.9220871
#> print(mean(block_col_MSEC))
#[1] 0.9435018
print(mean(block_median_col))
print(mean(block_median_col_R))
print(mean(block_col_MSE))
print(mean(block_col_MSER))
print(mean(block_col_MSEC))

#quant_col <- quantile(block_median_col,probs=seq(0, 1, 0.1))
#expcorseries <- c

#row  <-  I	
m2b <- matrix(0,nrow=sb,ncol=I)
cov2 <- cov(expr_datarow[,sample(1:datadimrow[1],I)])
cor2 <- cor(expr_datarow[,sample(1:datadimrow[1],I)])
m2 <- m2b[1,] <- colMeans(expr_datarow[,sample(1:datadimrow[1],I)])
block_median_row <- mat.or.vec(sb,0)
block_median_row_C <- mat.or.vec(sb,0)
block_row_MSE <- mat.or.vec(sb,0)
block_row_MSER <- mat.or.vec(sb,0)
block_row_MSEC <- mat.or.vec(sb,0)
for(b in 1:(sb-1)){
	d2 <- expr_datarow[,sample(1:datadimrow[1],I)]	
	cov2 <- cov2+cov(d2)
	cor2 <- cor2+cor(d2)
	meanvec <- colMeans(d2)
	m2 <- m2+meanvec
	m2b[b+1,] <- meanvec
	gene_ind <- sample(1:datadimrow[1],I)
	exp_ind <- sample(1:datadimrow[2],J)
	block_median_row[b] <- Corr.block(expr_datarow,gene_ind,exp_ind,3,useAbs=0)
	block_median_row_C[b] <- Corr.block(expr_datarow,gene_ind,exp_ind,2,useAbs=0)
	block_row_MSE[b] <- ExpCrit.block(expr_datarow,gene_ind,exp_ind,1,useAbs=0)
        block_row_MSER[b] <- ExpCrit.block(expr_datarow,gene_ind,exp_ind,2,useAbs=0)
        block_row_MSEC[b] <- ExpCrit.block(expr_datarow,gene_ind,exp_ind,3,useAbs=0)
	}
print(mean(block_median_row))
print(mean(block_median_row_C))
print(mean(block_row_MSE))
print(mean(block_row_MSER))
print(mean(block_row_MSEC))

#> print(mean(block_median_row))
#[1] 0.1493247
#> print(mean(block_median_row_C))
#[1] 0.1769574
#> print(mean(block_row_MSE))
#[1] 0.6861978
#> print(mean(block_row_MSER))
#[1] 0.9414403
#> print(mean(block_row_MSEC))
#[1] 0.9268591

AvgVarR <- varscale*mean(diag(cov1/sb))	
AvgVarC <- varscale*mean(diag(cov2/sb))


#mallR <- rmvnorm(I,m1/sb,cov1/sb)	#+rnorm(1)
mallR=matrix(NA,ncol=J,nrow=I)
for(i in 1:I){
	mallR[i,]=rmvnorm(1,m1/sb+rnorm(1,0,0.1),cov1/sb)
}

#mallC <- t(rmvnorm(J,m2/sb,cov2/sb)) #+rnorm(1)
mallC=matrix(NA,ncol=J,nrow=I)
for(j in 1:J){
	mallC[,j]=rmvnorm(1,m2/sb+rnorm(1,0,0.1),cov2/sb)
}

save(mallR, mallC, AvgVarR, AvgVarC, datadimrow, datadimcol, file="FakeData_bgnoise0.1.Rdata")#="FakeData_bgnoise0.1.Rdata")

load("FakeData_bgnoise0.1.Rdata")

rm(list=ls())
source("~/Genomics/html/applet/MicroArrayUp/MicroArrayUp/DataMining_R/Miner.R")
setwd("~/integr8functgenom/Miner/rda/common/yeast_data/")
load("FakeData_bgnoise0.1.Rdata")
load("yeast_cmonkey")

library(mvtnorm)
set.seed(18274)

###########Sim-ed experiements
J <- 40*2
J1 <- 15
J2 <- 10

I <- 100*2
I1 <- 30
I2 <- 20

n <- I*J
sb <- 1000


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


###SIGNED BLOCKS

########Creating signed blocks for Row correlation
#genecorseries <- c(0.2,0.4,0.6,0.8) #
genecorseries <- c(0.1,0.4,0.6,0.9) #
#genecorseries <- c(1.25*mediangenecor, 2*mediangenecor, 4*mediangenecor, 10*mediangenecor)
#expcorseries <- rep(0.5,4)

#gene size: 20, 20, 20, 20
#exp size: 10, 10, 10, 10
###nonoverlapping
Ibl1to4 <- list(21:40, 61:80, 121:140, 161:180)
Jbl1to4 <- list(6:15,26:35,46:55,66:75)
###overlapping
#genes overlap by 7
#Ibl1to4 <- list(21:40, 34:53, 47:66, 60:79)
#exps overlap by 4
#Jbl1to4 <- list(6:15,12:21,18:27,24:33)

I1 <- 20
J1 <- 10
truecor_col <- mat.or.vec(4,1)
truecor_col_R <- mat.or.vec(4,1)
true_col_MSER <- mat.or.vec(4,1)
true_col_MSEC <- mat.or.vec(4,1)
true_col_MSE <- mat.or.vec(4,1)
bl1=matrix(NA,nrow=I1,ncol=J1)
#for each block
for(bl in 1:4){
	mbl1 <- sample(c(seq(-3,-1.5,by=0.25),seq(1.5,3,by=0.25)),I1, replace=TRUE)
	bl1cor <- rep(genecorseries[bl],I1+choose(I1,2))
	#cors[b1]+runif(I1+choose(I1,2),-.1,.1)#rnorm(I1+choose(I1,2),cors[bl],.0001) #adds a bit of randomness to the correlations
	bl1var <- rep(AvgVarC,I1)#AvgVarR+runif(I1,0,.1)

	cormat1 <- matrix(1,ncol <- I1,nrow <- I1)
	k <- 0
	#for each row
	for(i in 1:I1){
		#for each column
		for(j in 1:i){
		k <- k+1
		 cormat1[i,j] <- cormat1[j,i] <- bl1cor[k]*bl1var[i]
		 }
		 }
	#diag(cormat1) <- bl1var	
###for each column adding noise to mean and variance
	for(j in 1:J1){
		diag(cormat1)<-bl1var+runif(I1,0,0.1)
		bl1[,j]=rmvnorm(1,mbl1+rnorm(I1,0,0.1),cormat1)
	}
	#bl1 <- rmvnorm(J1,mbl1,cormat1)#*matrix(rnorm(I1*J1),ncol <- J1)#*matrix(runif(I1*J1,0.9,1.1),ncol=1)
	
	mallC[Ibl1to4[[bl]],Jbl1to4[[bl]]] <- bl1
	truecor_col[bl] <- Corr.block(mallC,Ibl1to4[[bl]],Jbl1to4[[bl]],3,useAbs=0)
	truecor_col_R[bl] <-Corr.block(mallC,Ibl1to4[[bl]],Jbl1to4[[bl]],2,useAbs=0)
	true_col_MSE[bl] <- ExpCrit.block(mallC,Ibl1to4[[bl]],Jbl1to4[[bl]],1,useAbs=0)
        true_col_MSER[bl] <- ExpCrit.block(mallC,Ibl1to4[[bl]],Jbl1to4[[bl]],2,useAbs=0)
        true_col_MSEC[bl] <- ExpCrit.block(mallC,Ibl1to4[[bl]],Jbl1to4[[bl]],3,useAbs=0)
}
EallR <- mallC

truecor_col
truecor_col_R
true_col_MSE
true_col_MSER
true_col_MSEC

#0.1 noise
#> truecor_col
#[1] 0.9008044 0.9239867 0.9531061 0.9766899
#> truecor_col_R
#[1] 0.2887986 0.4821863 0.5927291 0.8567951
#> true_col_MSE
#[1] 5.854923 5.271380 5.734878 6.135965
#> true_col_MSER
#[1] 0.1086454 0.1261311 0.1114099 0.1285662
#> true_col_MSEC
#[1] 0.9850035 0.9402789 0.9332110 0.8904425

#OLD
#> truecor_col
#[1] 0.9260182 0.9474013 0.9675844 0.9887918
#> truecor_col_R
#[1] 0.4179312 0.3266668 0.7363317 0.9342658
#> true_col_MSE
#[1] 5.698457 5.593139 5.226669 5.397499
#> true_col_MSER
#[1] 0.11337115 0.06607583 0.11186668 0.14119554
#> true_col_MSEC
#[1] 0.9513342 0.9822717 0.9174612 0.8683354

#0.1 mean noise
#> truecor_col
#[1] 0.8478056 0.8934172 0.9098643 0.9001213
#> truecor_col_R
#[1] 0.5524024 0.6579141 0.8894508 0.8177768
#> true_col_MSE
#[1] 7.177896 6.906511 9.670405 7.211223
#> true_col_MSER
#[1] 0.2793678 0.2411267 0.4153196 0.3725262
#> true_col_MSEC
#[1] 0.8384530 0.8427068 0.6373476 0.6950470

#NEW
#> truecor_col
#[1] 0.8605616 0.8494676 0.9007821 0.9024973
#> truecor_col_R
#[1] 0.2971696 0.4451167 0.3837474 0.5021467
#> true_col_MSE
#[1] 6.015283 5.210478 6.347461 6.249084
#> true_col_MSER
#[1] 0.1643185 0.2334806 0.1384001 0.1673522
#> true_col_MSEC
#[1] 0.9605475 0.8934909 0.9494352 0.9170986

#no noise
#> truecor_col
#[1] 0.9323487 0.9527757 0.9664617 0.9882842
#> truecor_col_R
#[1] 0.3928868 0.3391255 0.8096152 0.9197470
#> true_col_MSE
#[1] 6.052752 5.902596 5.289480 5.334876
#> true_col_MSER
#[1] 0.10225346 0.06336944 0.14248380 0.11612659
#> true_col_MSEC
#[1] 0.9584053 0.9812616 0.8854280 0.8936710

#ORIG
#> truecor_col
#[1] 0.6323417 0.6198292 0.5056064 0.5760933
#> truecor_col_R
#[1] 0.5398330 0.6228465 0.5797479 0.5240923
#> true_col_MSE
#[1] 6.033069 5.837587 5.112506 6.920426
#> true_col_MSER
#[1] 0.9312633 0.9745491 0.8204518 0.8799668
#> true_col_MSEC
#[1] 0.6244997 0.6934933 0.5090804 0.5181356

########Creating signed blocks for Column correlation
#expcorseries <- c(0.2,0.4,0.6,0.8)  #
expcorseries <- c(0.1,0.4,0.6,0.9) #
#expcorseries <- c(1.25*medianexpcor, 2*medianexpcor, 4*medianexpcor, 10*medianexpcor)
#expcorseries <- rep(0.5,4)
#rep(0.5,6)#c(0,.2,.4,.6,.8,1)

Ibl1to4 <- list(21:40, 61:80, 121:140, 161:180)
Jbl1to4 <- list(6:15,26:35,46:55,66:75)
# Ibl1to6 <- list(16:30, 46:60, 76:90, 16:30, 46:60, 76:90)
# Jbl1to6 <- list(6:15,26:35,6:15,26:35,6:15,26:35)

I1 <- 20
J1 <- 10
truecor_row <- mat.or.vec(4,1)
truecor_row_C <- mat.or.vec(4,1)
true_row_MSER <- mat.or.vec(4,1)
true_row_MSEC <- mat.or.vec(4,1)
true_row_MSE <- mat.or.vec(4,1)
for(bl in 1:4){
	mbl1 <- sample(c(seq(-3,-1.5,by=0.25),seq(1.5,3,by=0.25)),J1, replace=TRUE)
	bl1cor <- rep(expcorseries[bl],J1+choose(J1,2))
	#cors[b1]+runif(I1+choose(I1,2),-.1,.1)#rnorm(I1+choose(I1,2),cors[bl],.0001) #adds a bit of randomness to the correlations
	bl1var <- rep(AvgVarR,J1)#AvgVarR+runif(I1,0,.1)

	cormat1 <- matrix(1,ncol <- J1,nrow <- J1)
	k <- 0
	for(i in 1:J1){
		for(j in 1:i){
		k <- k+1
		 cormat1[i,j] <- cormat1[j,i] <- bl1cor[k]*bl1var[i]
		 }
		 }
	#diag(cormat1) <- bl1var
###adding noise to mean and variance
	for(i in 1:I1){
                diag(cormat1)<-bl1var+runif(J1,0,0.1)
		bl1[i,]=rmvnorm(1,mbl1+rnorm(J1,0,0.1),cormat1)
		#bl1[i,]=rmvnorm(1,mbl1,cormat1)
	}
	#bl1 <- t(rmvnorm(I1,mbl1,cormat1))#*matrix(rnorm(I1*J1),ncol=J1)#*matrix(runif(I1*J1,0.9,1.1),ncol=J1)
	
	mallR[Ibl1to4[[bl]],Jbl1to4[[bl]]] <- bl1
	
	truecor_row[bl] <- Corr.block(mallR,Ibl1to4[[bl]],Jbl1to4[[bl]],2,useAbs=0)
	truecor_row_C[bl] <- Corr.block(mallR,Ibl1to4[[bl]],Jbl1to4[[bl]],3,useAbs=0)
	true_row_MSE[bl] <- ExpCrit.block(mallR,Ibl1to4[[bl]],Jbl1to4[[bl]],1,useAbs=0)
        true_row_MSER[bl] <- ExpCrit.block(mallR,Ibl1to4[[bl]],Jbl1to4[[bl]],2,useAbs=0)
        true_row_MSEC[bl] <- ExpCrit.block(mallR,Ibl1to4[[bl]],Jbl1to4[[bl]],3,useAbs=0)
}
EallC <- mallR

truecor_row
truecor_row_C
true_row_MSE
true_row_MSER
true_row_MSEC

#0.1 noise
#> truecor_row
#[1] 0.8836786 0.9411169 0.9468483 0.9810444
#> truecor_row_C
#[1] 0.1846761 0.4750650 0.3735048 0.8392540
#> true_row_MSE
#[1] 5.349441 6.210506 5.088714 6.996305
#> true_row_MSER
#[1] 0.9833102 0.9362567 0.9581604 0.9007826
#> true_row_MSEC
#[1] 0.1334325 0.1224968 0.0949802 0.1170517

#OLD
#> truecor_row
#[1] 0.9409398 0.9431029 0.9742315 0.9865724
#> truecor_row_C
#[1] 0.4293019 0.5188734 0.6875997 0.8733313
#> true_row_MSE
#[1] 5.698457 5.593139 5.226669 5.397499
#> true_row_MSER
#[1] 0.11337115 0.06607583 0.11186668 0.14119554
#> true_row_MSEC
#[1] 0.9513342 0.9822717 0.9174612 0.8683354

#0.1 mean noise
#> truecor_row
#[1] 0.8625172 0.8753551 0.9393632 0.9074928
#> truecor_row_C
#[1] 0.5272074 0.7247846 0.6977660 0.7582695
#> true_row_MSE
#[1] 6.675129 7.263736 8.191338 7.428737
#> true_row_MSER
#[1] 0.8497002 0.7272533 0.8493748 0.7494034
#> true_row_MSEC
#[1] 0.2634311 0.3669632 0.2067605 0.3238582

#NEW
#> truecor_row
#[1] 0.8706761 0.8847624 0.8538440 0.7841836
#> truecor_row_C
#[1] 0.5524558 0.6682325 0.6864968 0.7713455
#> true_row_MSE
#[1] 6.670505 7.499548 7.448312 4.180830
#> true_row_MSER
#[1] 0.8284183 0.7832056 0.7344066 0.5440955
#> true_row_MSEC
#[1] 0.2866575 0.3090750 0.3725191 0.5776257 

#orig
#> truecor_row
#[1] 0.3074652 0.3918663 0.4363427 0.6002887
#> truecor_row_C
#[1] 0.8994358 0.9190427 0.9633580 0.9478503
#> true_row_MSE
#[1] 5.746412 6.053537 6.453389 5.702617
#> true_row_MSER
#[1] 0.10424115 0.08679298 0.05919341 0.11767582
#> true_row_MSEC
#[1] 0.9867231 0.9863056 0.9743194 0.9279697




####CONSTANT BLOCKS
set.seed(18274)
J <- 40*2
I <- 100*2
n <- I*J
sb <- 1000

varscale <- 1#000
m1b <- matrix(0,nrow=sb,ncol=J)
cov1 <- cov(expr_datacol[,sample(1:datadimcol[2],J)])
cor1 <- cor(expr_datacol[,sample(1:datadimcol[2],J)])
m2b <- matrix(0,nrow=sb,ncol=I)
cov2 <- cov(expr_datarow[,sample(1:datadimrow[1],I)])
cor2 <- cor(expr_datarow[,sample(1:datadimrow[1],I)])
m2 <- m2b[1,] <- colMeans(expr_datarow[,sample(1:datadimrow[1],I)])
AvgVarR <- varscale*mean(diag(cov1/sb))
AvgVarC <- varscale*mean(diag(cov2/sb))




###ROW NONOVERLAPPING
set.seed(18274)
J <- 40*2
I <- 100*2
n <- I*J
sb <- 1000

varscale <- 1#000
m1b <- matrix(0,nrow=sb,ncol=J)
cov1 <- cov(expr_datacol[,sample(1:datadimcol[2],J)])
cor1 <- cor(expr_datacol[,sample(1:datadimcol[2],J)])
m2b <- matrix(0,nrow=sb,ncol=I)
cov2 <- cov(expr_datarow[,sample(1:datadimrow[1],I)])
cor2 <- cor(expr_datarow[,sample(1:datadimrow[1],I)])
m2 <- m2b[1,] <- colMeans(expr_datarow[,sample(1:datadimrow[1],I)])
AvgVarR <- varscale*mean(diag(cov1/sb))
AvgVarC <- varscale*mean(diag(cov2/sb))

########Creating CONST NONOVERLAP blocks for ROW correlation
#genecorseries <- c(0.2,0.4,0.6,0.8) #
genecorseries <- c(0.1,0.4,0.6,0.9) #
#genecorseries <- c(1.25*mediangenecor, 2*mediangenecor, 4*mediangenecor, 10*mediangenecor)
#expcorseries <- rep(0.5,4)

#gene size: 20, 20, 20, 20
#exp size: 10, 10, 10, 10
###nonoverlapping
Ibl1to4 <- list(21:40, 61:80, 121:140, 161:180)
Jbl1to4 <- list(6:15,26:35,46:55,66:75)


I1 <- 20
J1 <- 10
truecor_col <- mat.or.vec(4,1)
truecor_col_R <- mat.or.vec(4,1)
true_col_MSER <- mat.or.vec(4,1)
true_col_MSEC <- mat.or.vec(4,1)
true_col_MSE <- mat.or.vec(4,1)
bl1const <- matrix(NA,nrow=I1,ncol=J1)

mallCconst <- mallC

#for each block
for(bl in 1:4){
	mbl1const <- sample(seq(1.5,3,by=0.25),I1, replace=TRUE)
	
	if((bl %% 2) == 1) {
	mbl1const <- -(mbl1const)
	}	
	print(mbl1const)
	
	bl1corconst <- rep(genecorseries[bl],I1+choose(I1,2))
	bl1varconst <- rep(AvgVarC,I1)

	cormat1const <- matrix(1,ncol <- I1,nrow <- I1)
	k <- 0
	#for each row
	for(i in 1:I1){
		#for each column
		for(j in 1:i){
		k <- k+1
		 cormat1const[i,j] <- cormat1const[j,i] <- bl1corconst[k]*bl1varconst[i]
		 }
		 }	
###for each column adding noise to mean and variance
	for(j in 1:J1){
		diag(cormat1const)<-bl1varconst+runif(I1,0,0.1)
		bl1const[,j]=rmvnorm(1,mbl1const+rnorm(I1,0,0.1),cormat1const)
	}
	
	mallCconst[Ibl1to4[[bl]],Jbl1to4[[bl]]] <- bl1const
	truecor_col[bl] <- Corr.block(mallCconst,Ibl1to4[[bl]],Jbl1to4[[bl]],3,useAbs=0)
	truecor_col_R[bl] <-Corr.block(mallCconst,Ibl1to4[[bl]],Jbl1to4[[bl]],2,useAbs=0)
	true_col_MSE[bl] <- ExpCrit.block(mallCconst,Ibl1to4[[bl]],Jbl1to4[[bl]],1,useAbs=0)
        true_col_MSER[bl] <- ExpCrit.block(mallCconst,Ibl1to4[[bl]],Jbl1to4[[bl]],2,useAbs=0)
        true_col_MSEC[bl] <- ExpCrit.block(mallCconst,Ibl1to4[[bl]],Jbl1to4[[bl]],3,useAbs=0)
}
EallRconst_nono <- mallCconst

truecor_col
truecor_col_R
true_col_MSE
true_col_MSER
true_col_MSEC

#0.1 noise
> truecor_col
[1] 0.8312686 0.8204520 0.7698696 0.8018832
> truecor_col_R
[1] 0.0583845574 0.0000179996 0.0259088316 0.0603714859
> true_col_MSE
[1] 0.3769557 0.3162460 0.2402993 0.3079095
> true_col_MSER
[1] 0.1720759 0.1732625 0.2316932 0.1982492
> true_col_MSEC
[1] 0.9832148 0.9920236 0.9796459 0.9812471




###COLUMN
########Creating SIGNED nonooverlapping blocks for COL correlation
#expcorseries <- c(0.2,0.4,0.6,0.8)  #
expcorseries <- c(0.1,0.4,0.6,0.9) #
#expcorseries <- c(1.25*medianexpcor, 2*medianexpcor, 4*medianexpcor, 10*medianexpcor)
#expcorseries <- rep(0.5,4)
#rep(0.5,6)#c(0,.2,.4,.6,.8,1)

Ibl1to4 <- list(21:40, 61:80, 121:140, 161:180)
Jbl1to4 <- list(6:15,26:35,46:55,66:75)
# Ibl1to6 <- list(16:30, 46:60, 76:90, 16:30, 46:60, 76:90)
# Jbl1to6 <- list(6:15,26:35,6:15,26:35,6:15,26:35)

I1 <- 20
J1 <- 10
truecor_row <- mat.or.vec(4,1)
truecor_row_C <- mat.or.vec(4,1)
true_row_MSER <- mat.or.vec(4,1)
true_row_MSEC <- mat.or.vec(4,1)
true_row_MSE <- mat.or.vec(4,1)

bl1 <- matrix(NA,nrow=I1,ncol=J1)

for(bl in 1:4){
	mbl1 <- sample(c(seq(-3,-1.5,by=0.25),seq(1.5,3,by=0.25)),J1, replace=TRUE)
	bl1cor <- rep(expcorseries[bl],J1+choose(J1,2))
	#cors[b1]+runif(I1+choose(I1,2),-.1,.1)#rnorm(I1+choose(I1,2),cors[bl],.0001) #adds a bit of randomness to the correlations
	bl1var <- rep(AvgVarR,J1)#AvgVarR+runif(I1,0,.1)

	cormat1 <- matrix(1,ncol <- J1,nrow <- J1)
	k <- 0
	for(i in 1:J1){
		for(j in 1:i){
		k <- k+1
		 cormat1[i,j] <- cormat1[j,i] <- bl1cor[k]*bl1var[i]
		 }
		 }
	#diag(cormat1) <- bl1var
###adding noise to mean and variance
	for(i in 1:I1){
                diag(cormat1)<-bl1var+runif(J1,0,0.1)
		bl1[i,]=rmvnorm(1,mbl1+rnorm(J1,0,0.1),cormat1)
		#bl1[i,]=rmvnorm(1,mbl1,cormat1)
	}
	#bl1 <- t(rmvnorm(I1,mbl1,cormat1))#*matrix(rnorm(I1*J1),ncol=J1)#*matrix(runif(I1*J1,0.9,1.1),ncol=J1)
	
	mallR[Ibl1to4[[bl]],Jbl1to4[[bl]]] <- bl1
	
	truecor_row[bl] <- Corr.block(mallR,Ibl1to4[[bl]],Jbl1to4[[bl]],2,useAbs=0)
	truecor_row_C[bl] <- Corr.block(mallR,Ibl1to4[[bl]],Jbl1to4[[bl]],3,useAbs=0)
	true_row_MSE[bl] <- ExpCrit.block(mallR,Ibl1to4[[bl]],Jbl1to4[[bl]],1,useAbs=0)
        true_row_MSER[bl] <- ExpCrit.block(mallR,Ibl1to4[[bl]],Jbl1to4[[bl]],2,useAbs=0)
        true_row_MSEC[bl] <- ExpCrit.block(mallR,Ibl1to4[[bl]],Jbl1to4[[bl]],3,useAbs=0)
}
EallC <- mallR

truecor_row
truecor_row_C
true_row_MSE
true_row_MSER
true_row_MSEC

#0.1 noise
#> truecor_row
#[1] 0.8836786 0.9411169 0.9468483 0.9810444
#> truecor_row_C
#[1] 0.1846761 0.4750650 0.3735048 0.8392540
#> true_row_MSE
#[1] 5.349441 6.210506 5.088714 6.996305
#> true_row_MSER
#[1] 0.9833102 0.9362567 0.9581604 0.9007826
#> true_row_MSEC
#[1] 0.1334325 0.1224968 0.0949802 0.1170517


########Creating nonooverlapping CONST blocks for COL correlation
#expcorseries <- c(0.2,0.4,0.6,0.8)  #
expcorseries <- c(0.1,0.4,0.6,0.9) #
#expcorseries <- c(1.25*medianexpcor, 2*medianexpcor, 4*medianexpcor, 10*medianexpcor)
#expcorseries <- rep(0.5,4)
#rep(0.5,6)#c(0,.2,.4,.6,.8,1)

Ibl1to4 <- list(21:40, 61:80, 121:140, 161:180)
Jbl1to4 <- list(6:15,26:35,46:55,66:75)
# Ibl1to6 <- list(16:30, 46:60, 76:90, 16:30, 46:60, 76:90)
# Jbl1to6 <- list(6:15,26:35,6:15,26:35,6:15,26:35)

I1 <- 20
J1 <- 10
truecor_row <- mat.or.vec(4,1)
truecor_row_C <- mat.or.vec(4,1)
true_row_MSER <- mat.or.vec(4,1)
true_row_MSEC <- mat.or.vec(4,1)
true_row_MSE <- mat.or.vec(4,1)

bl1 <- matrix(NA,nrow=I1,ncol=J1)

for(bl in 1:4){
	mbl1 <- sample(seq(1.5,3,by=0.25),J1, replace=TRUE)
	bl1cor <- rep(expcorseries[bl],J1+choose(J1,2))
	#cors[b1]+runif(I1+choose(I1,2),-.1,.1)#rnorm(I1+choose(I1,2),cors[bl],.0001) #adds a bit of randomness to the correlations
	bl1var <- rep(AvgVarR,J1)#AvgVarR+runif(I1,0,.1)

	cormat1 <- matrix(1,ncol <- J1,nrow <- J1)
	k <- 0
	for(i in 1:J1){
		for(j in 1:i){
		k <- k+1
		 cormat1[i,j] <- cormat1[j,i] <- bl1cor[k]*bl1var[i]
		 }
		 }
	#diag(cormat1) <- bl1var
###adding noise to mean and variance
	for(i in 1:I1){
                diag(cormat1)<-bl1var+runif(J1,0,0.1)
		bl1[i,]=rmvnorm(1,mbl1+rnorm(J1,0,0.1),cormat1)
		#bl1[i,]=rmvnorm(1,mbl1,cormat1)
	}
	#bl1 <- t(rmvnorm(I1,mbl1,cormat1))#*matrix(rnorm(I1*J1),ncol=J1)#*matrix(runif(I1*J1,0.9,1.1),ncol=J1)
	
	mallR[Ibl1to4[[bl]],Jbl1to4[[bl]]] <- bl1
	
	truecor_row[bl] <- Corr.block(mallR,Ibl1to4[[bl]],Jbl1to4[[bl]],2,useAbs=0)
	truecor_row_C[bl] <- Corr.block(mallR,Ibl1to4[[bl]],Jbl1to4[[bl]],3,useAbs=0)
	true_row_MSE[bl] <- ExpCrit.block(mallR,Ibl1to4[[bl]],Jbl1to4[[bl]],1,useAbs=0)
        true_row_MSER[bl] <- ExpCrit.block(mallR,Ibl1to4[[bl]],Jbl1to4[[bl]],2,useAbs=0)
        true_row_MSEC[bl] <- ExpCrit.block(mallR,Ibl1to4[[bl]],Jbl1to4[[bl]],3,useAbs=0)
}
EallCconst_nono <- mallR

truecor_row
truecor_row_C
true_row_MSE
true_row_MSER
true_row_MSEC

> truecor_row
[1] 0.7198264 0.8126138 0.6626168 0.8465205
> truecor_row_C
[1]  1.396714e-02  9.625492e-05 -1.398138e-02 -3.217699e-02
> true_row_MSE
[1] 0.1589543 0.2850352 0.1481391 0.3525884
> true_row_MSER
[1] 0.9676343 0.9787514 0.9730935 0.9889952
> true_row_MSEC
[1] 0.2970537 0.2121715 0.3484241 0.1643003



#################Now create three interaction datasets

####Interaction expr_data with probsI <- 1 inside the block and background outside
Iall1 <- matrix(rbinom(I*I,1,mean(interact_data)),ncol=I)

probsI1 <- rep(1,4)
Ibl1to4i <- Ibl1to4
for(bl in 1:4){
	Ib1=matrix(rbinom(I1*I1,1,probsI1[bl]),ncol=10)
	Iall1[Ibl1to4[[bl]],Ibl1to4i[[bl]]] <- Ib1
}


####Interaction expr_data with probsI <- .25 inside the block and background outside
Iall25 <- matrix(rbinom(I*I,1,mean(interact_data)),ncol=I)

probsI25 <- rep(.25,4)
Ibl1to4i <- Ibl1to4
for(bl in 1:4){
	Ib1 <- matrix(rbinom(I1*I1,1,probsI25[bl]),ncol=10)
	Iall25[Ibl1to4[[bl]],Ibl1to4i[[bl]]] <- Ib1
}


####Interaction expr_data with probsI <- .05 inside the block and background outside
Iall05 <- matrix(rbinom(I*I,1,mean(interact_data)),ncol=I)

probsI05 <- rep(.05,4)
Ibl1to4i <- Ibl1to4
for(bl in 1:4){
	Ib1 <- matrix(rbinom(I1*I1,1,probsI05[bl]),ncol=10)
	Iall05[Ibl1to4[[bl]],Ibl1to4i[[bl]]] <- Ib1
}

#####

save(I,J,Iall1,Iall25,Iall05,EallC,EallR,Ibl1to4,Jbl1to4,truecor_row,truecor_col,truecor_row_C,truecor_col_R,genecorseries,expcorseries,probsI1,probsI25,probsI05,file="fake110427_4_incr_nobgnoise")

#####Now we create the randomized version

set.seed(19729)

Isample <- sample(1:I,I)
Jsample <- sample(1:J,J)

Ibl1to4R <- lapply(Ibl1to4,function(x) match(x,Isample))
Jbl1to4R <- lapply(Jbl1to4,function(x) match(x,Jsample))

EallRrand <- EallR[Isample,Jsample]
EallCrand <- EallC[Isample,Jsample]

EallRconst_nono_rand <- EallRconst_nono[Isample,Jsample]
EallCconst_nono_rand  <- EallCconst_nono[Isample,Jsample]



Iall1rand <- Iall1[Isample,Isample]
Iall25rand <- Iall25[Isample,Isample]
Iall05rand <- Iall05[Isample,Isample]

print(max(EallCrand[Ibl1to4R[[1]],Jbl1to4R[[1]]]))
print(min(EallCrand[Ibl1to4R[[1]],Jbl1to4R[[1]]]))
print(max(EallRrand[Ibl1to4R[[1]],Jbl1to4R[[1]]]))
print(min(EallRrand[Ibl1to4R[[1]],Jbl1to4R[[1]]]))

##Quick check
pdf("fakec_rand_blocks.pdf",height=11, width=8.5)
par(mfrow=c(2,2))
for(i in 1:4){
image(EallCrand[Ibl1to4R[[i]],Jbl1to4R[[i]]])
}
dev.off(2)

pdf("faker_rand_blocks.pdf",height=11, width=8.5)
par(mfrow=c(2,2))
for(i in 1:4){
image(EallRrand[Ibl1to4R[[i]],Jbl1to4R[[i]]])
}
dev.off(2)


range <- range(EallRconst_nono_rand)
unit <- (range[2]-range[1])/12
breaks <- seq(from=range[1],to=range[2],by=unit)

##Quick check
pdf("EallRconst_nono_rand.pdf",height=11, width=8.5)
par(mfrow=c(2,2))
for(i in 1:4){
image(EallRconst_nono_rand[Ibl1to4R[[i]],Jbl1to4R[[i]]], breaks=breaks)
}
dev.off(2)

pdf("EallRconst_nono_rand_all.pdf",height=11, width=8.5)
image(EallRconst_nono_rand)
dev.off(2)

pdf("EallRconst_nono.pdf",height=11, width=8.5)
image(EallRconst_nono)
dev.off(2)

range <- range(EallRconst_nono_rand)
unit <- (range[2]-range[1])/12
breaks <- seq(from=range[1],to=range[2],by=unit)

##Quick check
pdf("EallCconst_nono_rand.pdf",height=11, width=8.5)
par(mfrow=c(2,2))
for(i in 1:4){
image(EallCconst_nono_rand[Ibl1to4R[[i]],Jbl1to4R[[i]]], breaks=breaks)
}
dev.off(2)

pdf("EallCconst_nono_rand_all.pdf",height=11, width=8.5)
image(EallCconst_nono_rand)
dev.off(2)

pdf("EallCconst_nono.pdf",height=11, width=8.5)
image(EallCconst_nono)
dev.off(2)



write.table(EallRrand, file="fake110427_4r_incr_expr_rand.txt",sep="\t")
write.table(EallCrand, file="fake110427_4c_incr_expr_rand.txt",sep="\t")

write.table(EallRconst_nono_rand, file="fake110427_4r_const_nono_expr_rand.txt",sep="\t")
write.table(EallCconst_nono_rand, file="fake110427_4c_const_nono_expr_rand.txt",sep="\t")

write.table(EallRconst_over_rand, file="fake110427_4r_const_over_expr_rand.txt",sep="\t")
write.table(EallCconst_over_rand, file="fake110427_4c_const_over_expr_rand.txt",sep="\t")


###incr + ppi
expr_data <- EallRrand
interact_data <- Iall05rand
save(I,J,interact_data,expr_data,Ibl1to4R,Jbl1to4R,truecor_row,truecor_col,truecor_row_C,truecor_col_R,genecorseries,expcorseries,probsI05,file="fake110427_4r_incr_0.05ppi_rand")

expr_data <- EallCrand
interact_data <- Iall05rand
save(I,J,interact_data,expr_data,Ibl1to4R,Jbl1to4R,truecor_row,truecor_col,truecor_row_C,truecor_col_R,genecorseries,expcorseries,probsI05,file="fake110427_4c_incr_0.05ppi_rand")

expr_data <- EallRrand
interact_data <- Iall25rand
save(I,J,interact_data,expr_data,Ibl1to4R,Jbl1to4R,truecor_row,truecor_col,truecor_row_C,truecor_col_R,genecorseries,expcorseries,probsI25,file="fake110427_4r_incr_0.25ppi_rand")

expr_data <- EallCrand
interact_data <- Iall25rand
save(I,J,interact_data,expr_data,Ibl1to4R,Jbl1to4R,truecor_row,truecor_col,truecor_row_C,truecor_col_R,genecorseries,expcorseries,probsI25,file="fake110427_4c_incr_0.25ppi_rand")

save(I,J,Iall1rand,Iall25rand,Iall05rand,EallCrand,EallRrand,Ibl1to4R,Jbl1to4R,truecor_row,truecor_col,truecor_row_C,truecor_col_R,genecorseries,expcorseries,probsI1,probsI25,probsI05,file="fake110427_4_incr_rand")


#ROW
#truecor_col
#truecor_col_R
#true_col_MSE
#true_col_MSER
#true_col_MSEC

#COL
#truecor_col
#truecor_col_C
#true_col_MSE
#true_col_MSER
#true_col_MSEC

###CONST
expr_data <- EallRconst_nono_rand
save(I,J,expr_data,Ibl1to4R,Jbl1to4R,truecor_col,truecor_col_R,true_col_MSE,true_col_MSER,true_col_MSEC, genecorseries,file="fake110427_4r_const_nono_0.25ppi_rand")


expr_data <- EallRconst_over_rand
save(I,J,expr_data,Ibl1to4R,Jbl1to4R,truecor_col,truecor_col_C,true_col_MSE,true_col_MSER,true_col_MSEC,genecorseries,file="fake110427_4c_const_over_0.25ppi_rand")

expr_data <- EallCconst_nono_rand
save(I,J,expr_data,Ibl1to4R,Jbl1to4R,truecor_row,truecor_row_C,true_row_MSE,true_row_MSER,true_row_MSEC, genecorseries,file="fake110427_4c_const_nono_0.25ppi_rand")


expr_data <- EallCconst_over_rand
save(I,J,expr_data,Ibl1to4R,Jbl1to4R,truecor_row,truecor_row_C,true_row_MSE,true_row_MSER,true_row_MSEC,genecorseries,file="fake110427_4c_const_over_0.25ppi_rand")






Rmeans <- rowMeans(EallRrand)
Rsd <- apply(EallRrand, 1, sd)
EallRrandnorm <- (EallRrand - Rmeans) /Rsd

Cmeans <- rowMeans(EallCrand)
Csd <- apply(EallCrand, 1, sd)
EallCrandnorm <- (EallCrand - Cmeans) /Csd

AbsCorr.block(EallRrand,Ibl1to4R[[1]],Jbl1to4R[[1]],2)
AbsCorr.block(EallRrand,Ibl1to4R[[1]],Jbl1to4R[[1]],3)
ExpCrit.block(EallRrand,Ibl1to4R[[1]],Jbl1to4R[[1]],1)
ExpCrit.block(EallRrand,Ibl1to4R[[1]],Jbl1to4R[[1]],2)
ExpCrit.block(EallRrand,Ibl1to4R[[1]],Jbl1to4R[[1]],3)

AbsCorr.block(EallRrand,Ibl1to4R[[2]],Jbl1to4R[[2]],2)
AbsCorr.block(EallRrand,Ibl1to4R[[2]],Jbl1to4R[[2]],3)
ExpCrit.block(EallRrand,Ibl1to4R[[2]],Jbl1to4R[[2]],1)
ExpCrit.block(EallRrand,Ibl1to4R[[2]],Jbl1to4R[[2]],2)
ExpCrit.block(EallRrand,Ibl1to4R[[2]],Jbl1to4R[[2]],3)

AbsCorr.block(EallRrand,Ibl1to4R[[3]],Jbl1to4R[[3]],2)
AbsCorr.block(EallRrand,Ibl1to4R[[3]],Jbl1to4R[[3]],3)
ExpCrit.block(EallRrand,Ibl1to4R[[3]],Jbl1to4R[[3]],1)
ExpCrit.block(EallRrand,Ibl1to4R[[3]],Jbl1to4R[[3]],2)
ExpCrit.block(EallRrand,Ibl1to4R[[3]],Jbl1to4R[[3]],3)

AbsCorr.block(EallRrand,Ibl1to4R[[4]],Jbl1to4R[[4]],2)
AbsCorr.block(EallRrand,Ibl1to4R[[4]],Jbl1to4R[[4]],3)
ExpCrit.block(EallRrand,Ibl1to4R[[4]],Jbl1to4R[[4]],1)
ExpCrit.block(EallRrand,Ibl1to4R[[4]],Jbl1to4R[[4]],2)
ExpCrit.block(EallRrand,Ibl1to4R[[4]],Jbl1to4R[[4]],3)

AbsCorr.block(EallCrand,Ibl1to4R[[1]],Jbl1to4R[[1]],2)
AbsCorr.block(EallCrand,Ibl1to4R[[1]],Jbl1to4R[[1]],3)
ExpCrit.block(EallCrand,Ibl1to4R[[1]],Jbl1to4R[[1]],1)
ExpCrit.block(EallCrand,Ibl1to4R[[1]],Jbl1to4R[[1]],2)
ExpCrit.block(EallCrand,Ibl1to4R[[1]],Jbl1to4R[[1]],3)

AbsCorr.block(EallCrand,Ibl1to4R[[2]],Jbl1to4R[[2]],2)
AbsCorr.block(EallCrand,Ibl1to4R[[2]],Jbl1to4R[[2]],3)
ExpCrit.block(EallCrand,Ibl1to4R[[2]],Jbl1to4R[[2]],1)
ExpCrit.block(EallCrand,Ibl1to4R[[2]],Jbl1to4R[[2]],2)
ExpCrit.block(EallCrand,Ibl1to4R[[2]],Jbl1to4R[[2]],3)

AbsCorr.block(EallCrand,Ibl1to4R[[3]],Jbl1to4R[[3]],2)
AbsCorr.block(EallCrand,Ibl1to4R[[3]],Jbl1to4R[[3]],3)
ExpCrit.block(EallCrand,Ibl1to4R[[3]],Jbl1to4R[[3]],1)
ExpCrit.block(EallCrand,Ibl1to4R[[3]],Jbl1to4R[[3]],2)
ExpCrit.block(EallCrand,Ibl1to4R[[3]],Jbl1to4R[[3]],3)

AbsCorr.block(EallCrand,Ibl1to4R[[4]],Jbl1to4R[[4]],2)
AbsCorr.block(EallCrand,Ibl1to4R[[4]],Jbl1to4R[[4]],3)
ExpCrit.block(EallCrand,Ibl1to4R[[4]],Jbl1to4R[[4]],1)
ExpCrit.block(EallCrand,Ibl1to4R[[4]],Jbl1to4R[[4]],2)
ExpCrit.block(EallCrand,Ibl1to4R[[4]],Jbl1to4R[[4]],3)


save(I,J,Iall1rand,Iall25rand,Iall05rand,EallCrand,EallRrand,Ibl1to4R,Jbl1to4R,truecor_row,truecor_col,truecor_row_C,truecor_col_R,genecorseries,expcorseries,probsI1,probsI25,probsI05,file="fake110427_4_incr_rand_norm")

write.table(EallRrandnorm, file="fake110427_4r_incr_rand_expr_norm.txt",sep="\t")
write.table(Iall05rand, file="fake110427_4r_incr_0.05ppi_rand_ppi.txt",sep="\t")
write.table(Iall25rand, file="fake110427_4r_incr_0.25ppi_rand_ppi.txt",sep="\t")
write.table(EallCrandnorm, file="fake110427_4c_incr_rand_expr_norm.txt",sep="\t")
write.table(Iall05rand, file="fake110427_4c_incr_0.05ppi_rand_ppi.txt",sep="\t")
write.table(Iall25rand, file="fake110427_4c_incr_0.25ppi_rand_ppi.txt",sep="\t")

expr_data <- EallRrandnorm
interact_data <- Iall05rand
save(I,J,interact_data,expr_data,Ibl1to4R,Jbl1to4R,truecor_row,truecor_col,truecor_row_C,truecor_col_R,genecorseries,expcorseries,probsI05,file="fake110427_4r_incr_0.05ppi_rand_norm")

expr_data <- EallCrandnorm
interact_data <- Iall05rand
save(I,J,interact_data,expr_data,Ibl1to4R,Jbl1to4R,truecor_row,truecor_col,truecor_row_C,truecor_col_R,genecorseries,expcorseries,probsI05,file="fake110427_4c_incr_0.05ppi_rand_norm")

expr_data <- EallRrandnorm
interact_data <- Iall25rand
save(I,J,interact_data,expr_data,Ibl1to4R,Jbl1to4R,truecor_row,truecor_col,truecor_row_C,truecor_col_R,genecorseries,expcorseries,probsI25,file="fake110427_4r_incr_0.25ppi_rand_norm")

expr_data <- EallCrandnorm
interact_data <- Iall25rand
save(I,J,interact_data,expr_data,Ibl1to4R,Jbl1to4R,truecor_row,truecor_col,truecor_row_C,truecor_col_R,genecorseries,expcorseries,probsI25,file="fake110427_4c_incr_0.25ppi_rand_norm")

pdf("faker_expr.pdf",width=11, height=8.5)
par(mfrow=c(1,2))
image(1:80,1:200,t(EallR),xlab="Experiments",ylab="Genes",main="EallR")
image(1:80,1:200,t(EallRrand),xlab="Experiments",ylab="Genes",main="EallR random")
dev.off(2)
pdf("fakec_expr.pdf",width=11, height=8.5)
par(mfrow=c(1,2))
image(1:80,1:200,t(EallC),xlab="Experiments",ylab="Genes",main="EallC")
image(1:80,1:200,t(EallCrand),xlab="Experiments",ylab="Genes",main="EallC random")
dev.off(2)

pdf("fake_ppi.pdf",height=11, width=8.5)
par(mfrow=c(2,3))
image(1:200,1:200,t(Iall1),xlab="Genes",ylab="Genes",main="Interation 1")
image(1:200,1:200,t(Iall25),xlab="Genes",ylab="Genes",main="Interation .25")
image(1:200,1:200,t(Iall05),xlab="Genes",ylab="Genes",main="Interation .05")
image(1:200,1:200,t(Iall1rand),xlab="Genes",ylab="Genes",main="Interation 1 random")
image(1:200,1:200,t(Iall25rand),xlab="Genes",ylab="Genes",main="Interation .25 random")
image(1:200,1:200,t(Iall05rand),xlab="Genes",ylab="Genes",main="Interation .05 random")
dev.off(2)
