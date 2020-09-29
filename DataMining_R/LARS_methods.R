ElarsCrit.block=function(Data,Ii,Jj,ARC=1,I,J,pscore=NULL){ #want to max,NullDist should be nboot by 3 matrix
	
	if(ARC==2){
		alld=Data[Ii,]
		if(is.null(pscore)==0) alld=preScore_block(alld,ARC)
		alld=t(apply(alld,2,missfxn))
		Iind=rep(0,J)
   		Iind[Jj]=1
   	}
   	if(ARC==3){
		alld=Data[,Jj]
		if(is.null(pscore)==0) alld=preScore_block(alld,ARC)
		alld=apply(alld,2,missfxn)
		Iind=rep(0,I)
   		Iind[Ii]=1
   	}
   		Q=lars(alld,as.matrix(Iind))
		Qcv=cv.lars(alld,Iind,K = 5,plot.it=FALSE)
		min(Qcv$cv)
		s1=Qcv$frac[which.min(Qcv$cv)]
		fts=predict(Q,newx=alld,s=s1,type="fit",mode="fraction")
   		SSE=sum((Iind-fts$fit)^2)
   		SST=sum((Iind-mean(Iind))^2)
   		1-SSE/SST   
}


###The one below should work	
FeatureWblock_larsI=function(Ii,Jj,GFeat,Ifact=NULL,I,J,msteps=NULL){
    GIndex=rep(0,I) #Gives vector specifying 1/0 if gene is in or not in the block
    GIndex[Ii]=1
    if(is.null(msteps)==1){
    	Q=lars(GFeat,GIndex)
		Qcv=cv.lars(GFeat,GIndex,K = 5,plot.it=FALSE)
	}
    if(is.null(msteps)==0){
		Q=lars(GFeat,GIndex,max.steps=msteps)
		Qcv=cv.lars(GFeat,GIndex,K = 5,plot.it=FALSE,max.steps=msteps)
	}
	CVR2I=min(Qcv$cv)
	s1=Qcv$frac[which.min(Qcv$cv)]
	cfs=predict(Q,s=s1,type="coefficients",mode="fraction")$coefficients
	cfr=predict(Q,newx=GFeat,s=s1,type="fit",mode="fraction")$fit
	R2v=1-sum((GIndex-cfr)^2)/sum((GIndex-mean(GIndex))^2)
	c(R2v,cfs) #returns a vector with the first value the R2,and the rest the coefficients for the features
}