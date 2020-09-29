
#fxn gives the highest (largest) possible 2-step HCL initial blocks that meet criteria
#this removes almost duplicates from pervious method
#Issue: still giving a lot of blocks.  Should do some sort of random pick,

Dmerge=function(MergeMat){ #function to analyze output from hcluster function
	nm=length(MergeMat[,1])
	sizeG=OrigInd=matrix(NA,ncol=1,nrow=(nm))
	ListI=vector("list",length=(nm))
	DisInd=matrix(NA,ncol=1,nrow=(nm))
	for(i in 1:(nm)){
		x=MergeMat[i,]
		if(all(x<0)){
			sizeG[i,1]=2
			ListI[[i]]=x
			OrigInd[i]=i
			}
		if(sum(x>0)==1){
			x1=x[which(x>0)] #which(x>0) will alwasy be less than i
			sizeG[i]=sizeG[x1]+1
			ListI[[i]]=c(ListI[[x[which(x>0)]]],x[which(x<0)])
			OrigInd[i]=OrigInd[x1]
		}
		if(all(x>0)){
			sizeG[i]=sizeG[x[1]]+sizeG[x[2]] ##size of cluster created at ith step in tree
			ListI[[i]]=c(ListI[[x[1]]],ListI[[x[2]]]) ##members of the cluster created at ith step in the tree
			OrigInd[i]=OrigInd[x[which.min(x)]]
			DisInd[i]=OrigInd[x[which.max(x)]]
		}
	}
	list(ListI=ListI,sizeG=sizeG,OrigInd=OrigInd,DisInd=DisInd)
}

IcJctoijID=function(Ic,Jc){
    Ic=Ic[order(Ic)]
    Jc=Jc[order(Jc)]
    paste(c(paste(Ic,collapse=","),paste(Jc,collapse=",")),collapse="/")
}


allpossibleInitial=function(Dmat,Imin,Imax,Jmin,Jmax){ #function to determine all possible initial blocks from a 2-step hcluster
	InitclustI=InitclustJ=NULL
	####starting with exp scale
		#start by clustering exps by gene vector
		hc1=hcluster(t(Dmat),method="correlation")
		out1=Dmerge(hc1$merge)
		xJ=which(out1$sizeG<=Jmax & out1$sizeG>=Jmin)
		rmxJ=match(unique(out1$DisInd[xJ]),unique(out1$OrigInd[xJ]))
		{if(length(rmxJ[!is.na(rmxJ)])==0) uni_xJ=unique(out1$OrigInd[xJ])
			else uni_xJ=unique(out1$OrigInd[xJ])[-rmxJ[!is.na(rmxJ)]]}
		xJ1=xJ[(length(out1$OrigInd[xJ])-match(uni_xJ,rev((out1$OrigInd[xJ]))))+1] #index where unique and highest level lies
		#cluster each of xI clusters on exp scale
		for(jl in 1:length(xJ1)){
			Jc_jl=-out1$ListI[[xJ1[jl]]]
			Dmat_jl=Dmat[,Jc_jl]
			hc_jl=hcluster((Dmat_jl),method="correlation")
			out1_jl=Dmerge(hc_jl$merge)
			xI=which(out1_jl$sizeG<=Imax & out1_jl$sizeG>=Imin)
			rmxI=match(unique(out1_jl$DisInd[xI]),unique(out1_jl$OrigInd[xI]))
			{if(length(rmxI[!is.na(rmxI)])==0) uni_xI=unique(out1_jl$OrigInd[xI])
				else uni_xI=unique(out1_jl$OrigInd[xI])[-rmxI[!is.na(rmxI)]]}
			xI1=xI[(length(out1_jl$OrigInd[xI])-match(uni_xI,rev((out1_jl$OrigInd[xI]))))+1] #index where unique and highest level lies
			for(il in 1:length(xI1)){
				Ic_il=-out1_jl$ListI[[xI1[il]]]
				InitclustI=c(InitclustI,IcJctoijID(Ic_il,Jc_jl))
			}
		}
	####starting with gene scale
		#start by clustering genes by exp vector
		hc1=hcluster((Dmat),method="correlation")
		out1=Dmerge(hc1$merge)
		xI=which(out1$sizeG<=Imax & out1$sizeG>=Imin)
		rmxI=match(unique(out1$DisInd[xI]),unique(out1$OrigInd[xI]))
		{if(length(rmxI[!is.na(rmxI)])==0) uni_xI=unique(out1$OrigInd[xI])
			else uni_xI=unique(out1$OrigInd[xI])[-rmxI[!is.na(rmxI)]]}
		xI1=xI[(length(out1$OrigInd[xI])-match(uni_xI,rev((out1$OrigInd[xI]))))+1] #index where unique and highest level lies
		#cluster each of xI clusters on exp scale
		for(il in 1:length(xI1)){
			Ic_il=-out1$ListI[[xI1[il]]]
			Dmat_il=Dmat[Ic_il,]
			hc_il=hcluster(t(Dmat_il),method="correlation")
			out1_il=Dmerge(hc_il$merge)
			xJ=which(out1_il$sizeG<=Jmax & out1_il$sizeG>=Jmin)
			rmxJ=match(unique(out1_il$DisInd[xJ]),unique(out1_il$OrigInd[xJ]))
			{if(length(rmxJ[!is.na(rmxJ)])==0) uni_xJ=unique(out1_il$OrigInd[xJ])
				else uni_xJ=unique(out1_il$OrigInd[xJ])[-rmxJ[!is.na(rmxJ)]]}
			xJ1=xJ[(length(out1_il$OrigInd[xJ])-match(uni_xJ,rev((out1_il$OrigInd[xJ]))))+1] #index where unique and highest level lies
			for(jl in 1:length(xJ1)){
				Jc_jl=-out1_il$ListI[[xJ1[jl]]]
				InitclustJ=c(InitclustJ,IcJctoijID(Ic_il,Jc_jl))
			}
		}
		unique(c(InitclustI,InitclustJ))
}