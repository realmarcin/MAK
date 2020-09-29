
##updated to deal with missingness 4-17-10
# 
# ##test data
# x=rnorm(100000)
# Dmat=matrix(x,ncol=100)
# Dmat1=Dmat[,sample(1:100)]
# mvec=c(rep(1,(dim(Dmat)[1]-3)),0,0,0)
# #mvec=rep(1,dim(Dmat)[1])
# t1=BatchCreate(Dmat,mvec,1:200,1:60,1,1,pSize=.2,dmethod="euclidean")
# t1a=BatchCreate(Dmat1,mvec,1:200,1:60,1,1,pSize=.2,dmethod="euclidean")
# t2=BatchCreate(Dmat,mvec,1:200,1:60,1,0,pSize=.2,dmethod="euclidean")
# 
# mvec=c(rep(1,(dim(Dmat)[2]-3)),0,0,0)
# #mvec=rep(1,dim(Dmat)[2])
# t3=BatchCreate(Dmat,mvec,1:200,1:60,0,1,pSize=.2,dmethod="euclidean")
# t3a=BatchCreate(Dmat1,mvec,1:200,1:60,0,1,pSize=.2,dmethod="euclidean")
# t4=BatchCreate(Dmat,mvec,1:200,1:60,0,0,pSize=.2,dmethod="euclidean")
# 

library(amap)

BatchCreate=function(Dmat,missvec,Ii,Jj,Ig,Ia,pSize=.2,dmethod="correlation",Ulim,respectLim=TRUE){
	# ##Function parameters
	# Dmat is the data matrix, in this case the expression matrix (rows=genes, columns=experiments)
	# missvec is a vector of 0,1's if Ig=1, its length should be dim(Data)[1], if Ig=0 its length should be dim(Data)[2]
	# Ig indicator for a gene move
	# Ia indicator for an addition move
	# pSize is max proportion of block added or subtracted, default 20% of current block can be add/subtracted
	# dmethod is the distance method used to calculate the distance matrix
	# Ulim is the upper limit of block size in the dimension indicated by Ig (if Ig==1, Ulim=Imax)
	# respectLim is default true, and forces maxSize to be small enough to add to the block, if respectLim is set as true block must not already be too big
	
	if(Ia==1){ #addition move
		if(Ig==1){
			xmat=abs(t(Dmat[-Ii,Jj][(missvec==1)[-Ii],]))
			xmat=t(apply(xmat,2,function(xm1){ xm1[is.na(xm1)]=mean(xm1,na.rm=TRUE); (xm1)})) #replace with row mean
			IndB=(1:dim(Dmat)[1])[-Ii][(missvec==1)[-Ii]]
		} 
		if(Ig!=1){
			xmat=abs((Dmat[Ii,-Jj][,(missvec==1)[-Jj]]))
			xmat=t(apply(xmat,2,function(xm1){ xm1[is.na(xm1)]=mean(xm1,na.rm=TRUE); xm1}))	#replace with col mean
			IndB=(1:dim(Dmat)[2])[-Jj][(missvec==1)[-Jj]]
		} 
	}
	if(Ia!=1){ #subtraction move
		if(Ig==1){
			xmat=abs(t(Dmat[Ii,Jj])) #if gene move
			xmat=t(apply(xmat,2,function(xm1){ xm1[is.na(xm1)]=mean(xm1,na.rm=TRUE); xm1}))
			IndB=Ii
		}
		if(Ig!=1){
			xmat=abs((Dmat[Ii,Jj])) #if exp move
			xmat=t(apply(xmat,2,function(xm1){ xm1[is.na(xm1)]=mean(xm1,na.rm=TRUE); xm1}))
			IndB=Jj
		} 
	}

	# hc=diana(xmat,diss=FALSE,stand=TRUE,keep.diss=FALSE,keep.data=FALSE)
	# 
	# d1=dist(xmat,method=dmethod)
	# hc <- hclust(d1, "ward")
	hc=hcluster(xmat,method=dmethod)
	if(Ig==1) csize=length(Ii)
	if(Ig==0) csize=length(Jj)
	maxSize=pSize*(csize)+(pSize/100)*(csize)^2
	if(maxSize>(.5*csize)) maxSize=.5*(csize)
	if(Ia==1 & respectLim==TRUE){
		if((maxSize+csize)>Ulim) maxSize=Ulim-csize
	}
	cfinal=cutree(hc,h=FindCut(hc,maxSize))
	split(IndB,cfinal)
}



FindCut=function(hcout,maxSize){
       stopp=0
       first1=0
       min1=min(hcout$height)
       max1=max(hcout$height)
       while(stopp==0){
               if(first1==0){
                   h1=(max1+min1)/2
                   first1=1
               }
               tab1=table(cutree(hcout,h=h1))
               if(all(tab1<maxSize)){ ##if all groups are too small, increase it towards max and reset min
                   h2=(max1+h1)/2
				   h1old=h1
                   min1=h1
               }
               else if(any(tab1>maxSize)){  ##if any are too large, decrease towards min and reset max
                   h2=(h1+min1)/2
				   h1old=h1
                   max1=h1
               }
               else if(any(tab1==maxSize) & all(tab1<=maxSize)){ ##if any at max, and all others below, stop
                   h2=h1
				   h1old=h1
                   stopp=1
               }
               if((abs(h1-h2)/h1)<0.01 & all(tab1<=maxSize)){
					h1old=h1
					stopp=1
			   }
				if(h1==min1) stopp=1
			   h1=h2
			#print(h1)
			   #print(table(cutree(hcout,h=h1old)))
       }
       h1old
}



