library(lars)
#source("/usr2/people/marcin/Genomics/html/applet/MicroArrayUp/MicroArrayUp/DataMining_R/Miner.R")
load("yeast_cmonkey")

Ic<-c(2088,3914,4429,5626)
#Jc<-c(11,15,25,39)
#Critp.final(Ic,Jc,expr_data,interact_data,nullMeanData,nullMSEData,nullRegData,nullKendData,nullCorData,nullEucData,nullPPIData,nullFeatData,feat_data,useNull=FALSE,ECindex=-1,ERegindex=NULL ,Kendindex=-1,Corindex=-1,Eucindex=-1,UseExprMean=FALSE,MeanARCind=-1,ARCind=-1,RegARCind=-1,InterI=FALSE,FeatI=TRUE,Invert=0,meanOnly=0,useAbs=c(1,1,1))
###
#FeatureWblock_larsI(Ic,Jc,feat_data,dim(feat_data)[1],dim(feat_data)[2])[1]

###
I <- dim(feat_data)[1]
Ii <- Ic
GFeat <- feat_data
msteps <- NULL
GIndex=rep(0,I) #Gives vector specifying 1/0 if gene is in or not in the block
    GIndex[Ii]=1
    if(is.null(msteps)){
    	Q=lars(GFeat,GIndex)
	Qcv=cv.lars(GFeat,GIndex,K = 5,plot.it=FALSE)
    } else if(!is.null(msteps)){
	Q=lars(GFeat,GIndex,max.steps=msteps)
	Qcv=cv.lars(GFeat,GIndex,K = 5,plot.it=FALSE,max.steps=msteps)
	}
	CVR2I=min(Qcv$cv)
	s1=Qcv$index[which.min(Qcv$cv)]
	cfs=predict(Q,s=s1,type="coefficients",mode="fraction")$coefficients
	cfr=predict(Q,newx=GFeat,s=s1,type="fit",mode="fraction")$fit
	R2v=1-sum((GIndex-cfr)^2)/sum((GIndex-mean(GIndex))^2)
	c(R2v,cfs) 