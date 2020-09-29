######These functions calculate the pre-criteria and criteria. . these probably will be in R initially


#nullExprData is a vector of c(null for mean,null for SD)
#etc.
#if ExpCrit.blockp complains give 2 more arrays
#useNull toggles use of a null distribution
#ECindex is an index MSE(1),MAD(2) criteria
#KendARCind is an index for full(1),row(2),or column(3) and applies only to the Kendall calculation
#CorIndex correlation index row (1), col (2)
#ERegindex is an index that specifies whether or not you use the regression criterion, 1 = LARS, 2 = FEM
#UseExprMean is an index that specifies whether or not you use the block total/row/column MEAN criterion
#MeanARCind is an index for full(1),row(2),or column(3) and applies only to the MEAN calculation
#ARCind is an index for full(1),row(2),or column(3) and applies only to MSE and MAD calculation
#RegARCind is an index for full(1), row(2),or column(3) and applies only LARS and GEE
#InterI is an index for whether or not the interaction subcriteria is added to the criteria vector
#FeatI is an index for whether or not the feature subcriteria is added to the criteria vector
#Invert is a toggle for inverting MSE,MSER,MSEC,MAD,MADR,MADC,EucR,EucC
#meanOnly toggle to report only the mean value for the appropriate axis (and no expr cor criteria)
#useAbs toggle 0/1 to use the absolute value versions of criteria
#frxnsign = T/F scale criteria by frxn sign agreement

Critp.final=function(Ic,Jc, expr_data,interact_data,nullMeanData,nullMSEData,nullRegData,nullKendData,nullCorData,nullEucData,nullInteractData,useNull,ECindex,ERegindex,KendARCind,Corindex,Eucindex,UseExprMean,MeanARCind,ARCind,RegARCind,InterI,Invert,meanOnly,useAbs,frxnsign=F) {
     critvec=critvecRaw=NULL
    I=dim(expr_data)[1]
    J=dim(expr_data)[2]
    
    allUseAbs <- 0
    if(sum(useAbs) > 0) {
        allUseAbs <- 1
    }
    
     colm_noabs <- 0
     rowm_noabs <- 0
     colm_abs <- 0
     rowm_abs <- 0
     frxnsignR <- 0
     frxnsignC <- 0
     frxnsignA <- 0
  
  
       if(sum(useAbs) < length(useAbs)) {
	  getstats_noabs <- rowColMeansFrxn(expr_data[Ic,Jc], ARCind,abs=F,Ic,Jc)	  
	  colm_noabs <- getstats_noabs[[1]]
	  rowm_noabs <- getstats_noabs[[2]]
	  	  
	  frxnsignR <- getstats_noabs[[3]]
	  frxnsignC <- getstats_noabs[[4]]
	  frxnsignA <- getstats_noabs[[5]]
	  }
	  
     if(sum(useAbs) > 0) {
     print("rowColMeansFrxn abs")
	  getstats_abs <- rowColMeansFrxn(expr_data[Ic,Jc], ARCind,abs=T,Ic,Jc)
	  colm_abs <- getstats_abs[[1]]
	  rowm_abs <- getstats_abs[[2]]
	  }
     
    
#MEAN criteria
	if(UseExprMean==TRUE) {
		Emean=ExpCritMed(expr_data,Ic,Jc,MeanARCind, allUseAbs)
		
        if(frxnsign) {
	  if(ARCind == 1 && useAbs[2] == 0) {
	   Emean <- frxnsignA * Emean
	 }
	 else if(ARCind == 2 && useAbs[2] == 0) {
	 Emean <- frxnsignR * Emean
	 }
	 else if(ARCind == 3 && useAbs[2] == 0) {
	 Emean <- frxnsignC * Emean
	 }
	}
	
                #print(paste("Emean",Emean))
		critvecRaw=c(critvecRaw,Emean)
		if(useNull==TRUE && !is.null(nullMeanData)) {
			#print(paste("Emean",Emean,"nullMeanData", nullMeanData))
                        Emean=pcauchy(Emean, nullMeanData[1], nullMeanData[2])
			#Emean=Rgee.blockp(Emean,nullMeanData)
			critvec=c(critvec,Emean)
		}
                else {
                    critvec=c(critvec,0)
                }
	}
        else {
            critvec=c(critvec,0)
            critvecRaw=c(critvecRaw,0)
        }
#print(critvecRaw)
#print("Mean")

#MSE criteria
    if(is.null(ECindex)==0 && ECindex != -1 && meanOnly != 1){
    	ECm=ECmRaw=ExpCritI(expr_data,Ic,Jc,ECindex,ARCind,useAbs[1])
	
        if((ECindex == 1 || ECindex == 2) && Invert == 1) {      
            ECmRaw <- 1/(ECm+1)
            ECm <- ECmRaw
	    
	    if(frxnsign){
	    if(ARCind == 1 && useAbs[1] == 0) {
	       ECm <- frxnsignA * ECm
	     }
	     else if(ARCind == 2 && useAbs[1] == 0) {
	       ECm <- frxnsignR * ECm
	     }
	     else if(ARCind == 3 && useAbs[1] == 0) {
	       ECm <- frxnsignC * ECm
	     }
	  }

            #print(cat("1/ECm ",ECm, ECmRaw ))
        }
	
      if((ECindex == 1 || ECindex == 2) && useNull==TRUE && !is.null(nullMSEData)) {
            ECm=pcauchy(ECm, nullMSEData[1], nullMSEData[2])
	    
            #print(cat("ECm=1-pcauchy",Ecm,ECmRaw))
            #ECm=ExpCrit.blockp(ECm,nullMSEData)
        }
        #print(paste("ECm",ECm,ECmRaw))
        critvec=c(critvec,ECm)
        critvecRaw=c(critvecRaw,ECmRaw)
  	}
        else {
            critvec=c(critvec,0)
            critvecRaw=c(critvecRaw,0)
        }
#print(critvecRaw)
#print("MSE")

#print(paste("ERegindex", ERegindex, RegARCind))
#FEM, LARS
#regression criteria
   if(is.null(ERegindex)==FALSE && ERegindex != -1){
   #print(paste("ERegindex ",ERegindex,RegARCind))
   thiscolm <- colm_noabs
   if(useAbs[3] == 1) {
     thiscolm <- colm_abs
   }
   thisrowm <- rowm_noabs
   if(useAbs[3] == 1) {
     thisrowm <- rowm_abs
   }
   
      print("ExpCritR thisrowm")
      print(thisrowm)
	   
	ECreg=ECregRaw=ExpCritR(expr_data,Ic,Jc,ERegindex,RegARCind,I,J,useAbs[3],thiscolm,thisrowm)
	
	if(frxnsign) {
	if(ARCind == 1 && useAbs[3] == 0) {
	  ECregRaw <- frxnsignA * ECregRaw
	  ECreg <- frxnsignA * ECreg
	}
	else if(ARCind == 2 && useAbs[3] == 0) {
	  ECregRaw <- frxnsignR * ECregRaw
	  ECreg <- frxnsignR * ECreg
	}
	else if(ARCind == 3 && useAbs[3] == 0) {
	  ECregRaw<- frxnsignC * ECregRaw
	  ECreg<- frxnsignC * ECreg
	}
	}
	
        #print(paste("ECreg",ECreg, ERegindex, RegARCind))

	if(useNull==TRUE && !is.null(nullRegData)) {#exists("nullRegData")
            #print(paste("attempt nullReg", nullRegData))
             ECreg=pcauchy(ECreg, nullRegData[1], nullRegData[2])
            #ECreg=ExpCritR.blockp(ECreg,nullRegData)
            }
        #print(paste("ECreg", ECreg))
	critvec=c(critvec,ECreg)
	critvecRaw=c(critvecRaw,ECregRaw)
	}
        else if(is.null(ERegindex)){
            critvec=c(critvec,0)
            critvecRaw=c(critvecRaw,0)
        }
#print(paste("Ereg",critvecRaw))
#print("Reg")       
        
#Kendall criterion
       if(is.null(KendARCind)==0 && (KendARCind == 1 || KendARCind==2 || KendARCind==3)){
       
       #print("before")
       #print(kendall(expr_data[Ic,Jc]))

       #uses Kendall T value not p-value
         KCm=KCmRaw=KendExp.crit(expr_data,Ic,Jc,KendARCind,useAbs[2])[1]
	 #print(paste("Kend first",KCm,sep=" "))
	 
	 if(frxnsign) {
	 if(ARCind == 1 && useAbs[2] == 0) {
	  KCmRaw <- frxnsignA * KCmRaw
          KCm <- frxnsignA * KCm
	}
	else if(ARCind == 2 && useAbs[2] == 0) {
	KCmRaw <- frxnsignR * KCmRaw
	  KCm <- frxnsignR * KCm
	}
	else if(ARCind == 3 && useAbs[2] == 0) {
	KCmRaw <- frxnsignC * KCmRaw
	  KCm <- frxnsignC * KCm
	  #print(paste("frxnsignC",frxnsignC,sep=" "))
	}
	}
	
         if(useNull==TRUE && !is.null(nullKendData)){
             KCm=pcauchy(KCm, nullKendData[1], nullKendData[2])
             }
         critvec=c(critvec,KCm)
         critvecRaw=c(critvecRaw,KCmRaw)
	 #print(paste("Kend null",critvecRaw,useNull,sep=" "))
       }
       else {
            critvec=c(critvec,0)
            critvecRaw=c(critvecRaw,0)
	    #print(paste("Kend ",critvecRaw,useNull,sep=" "))
        }
#print(critvecRaw)
#print("Kend")

#Pearson correlation criterion
        if(is.null(Corindex)==0 && (Corindex==1 || Corindex==2 ||Corindex==3)){
            PCm=PCmRaw=Corr.block(expr_data,Ic,Jc, Corindex,useAbs[2])
	    
	  if(frxnsign){
	    if(ARCind == 1 && useAbs[1] == 0) {
	       PCmRaw <- frxnsignA * PCmRaw
	     }
	     else if(ARCind == 2 && useAbs[1] == 0) {
	       PCmRaw <- frxnsignR * PCmRaw
	     }
	     else if(ARCind == 3 && useAbs[1] == 0) {
	       PCmRaw <- frxnsignC * PCmRaw
	     }
	     PCm <- PCmRaw
	  }
	    
            if(useNull==TRUE && !is.null(nullCorData)){
             PCm=pcauchy(PCm, nullCorData[1], nullCorData[2])
             }
            critvec=c(critvec,PCm)
            critvecRaw=c(critvecRaw,PCmRaw)
       }
       else {
            critvec=c(critvec,0)
            critvecRaw=c(critvecRaw,0)
        }
#print("Cor")

#Euclidean distance
        if(is.null(Eucindex)==0 && (Eucindex==2 || Eucindex==3)){
            UCm=UCmRaw=Euclidean.block(expr_data,Ic,Jc, Eucindex,useAbs[2])
        	    
	  if(frxnsign){
	    if(ARCind == 1 && useAbs[1] == 0) {
	       UCmRaw <- frxnsignA * UCmRaw
	     }
	     else if(ARCind == 2 && useAbs[1] == 0) {
	       UCmRaw <- frxnsignR * UCmRaw
	     }
	     else if(ARCind == 3 && useAbs[1] == 0) {
	       UCmRaw <- frxnsignC * UCmRaw
	     }
	     UCm <- UCmRaw
	  }
	  
    	if(Invert == 1) {      
            UCmRaw = 1/(UCm+1)
            #print(cat("1/ECm ",ECm, ECmRaw ))
            UCm = UCmRaw
        }
         if(useNull==TRUE && !is.null(nullEucData)){
             UCm=pcauchy(UCm, nullEucData[1], nullEucData[2])
        }
            #print(paste("UCm",UCm,UCmRaw))
            critvec=c(critvec,UCm)
            critvecRaw=c(critvecRaw,UCmRaw)
       }
       else {
            critvec=c(critvec,0)
            critvecRaw=c(critvecRaw,0)
        }
#print("Euc")
               
#interaction criterion
   if(InterI==TRUE && useNull == TRUE && !is.null(nullInteractData)){
   	ICm=ICmRaw=InterCrit.block(interact_data,Ic)
        ICm=pcauchy(ICm, nullInteractData[1], nullInteractData[2])
        #cat("ICmRaw",ICmRaw)
        #cat("ICm",ICm)
   	critvec=c(critvec,ICm)
	critvecRaw=c(critvecRaw,ICmRaw)
   	}
    else if(InterI==TRUE) {
   	ICmRaw=InterCrit.block(interact_data,Ic) 
        #cat("PPI ICmRaw ",ICmRaw)
   	critvec=c(critvec,ICmRaw)
	critvecRaw=c(critvecRaw,ICmRaw)
   	}
        else {
            critvec=c(critvec,0)
            critvecRaw=c(critvecRaw,0)
        }
#print(critvecRaw)
#print("PPI")

### much faster in Java
#feature criterion        
   	#if(FeatI==TRUE){
	###OLD#FbM = FeatureWblock(Ic,Jc,feat_data,dim(expr_data)[1],dim(expr_data)[2])
        #FbM = FbMRaw = FeatHamming.block(feat_data, Ic)
        ###FbM = FbMRaw = FeatureWblock_larsI(Ic,feat_data,dim(feat_data)[1])[1] #.,mst)
        ###print(paste("FeatureWblock_larsI ",FbM))
        #if(useNull==TRUE && !is.null(nullFeatData)) {
        #    FbM=pcauchy(FbM, nullFeatData[1], nullFeatData[2])
        #}
        ###cat("FbM",FbM)
	#critvec=c(critvec,FbM)
        ###cat("FbMRaw", FbMRaw)
	#critvecRaw=c(critvecRaw,FbMRaw)
   	#}
        #else {
            critvec=c(critvec,0)
            critvecRaw=c(critvecRaw,0)
        #}
#print(critvecRaw)
#print("Feat")

        #placeholders for TF crit
        critvec=c(critvec,0)
        critvecRaw=c(critvecRaw,0)

#print(critvec)
#print(critvecRaw)
	list(critvec=critvec,critvecRaw=critvecRaw)
}

ExpCritI=function(data,Ic,Jc,cInd1,iARC,useAbs){
    #print(cInd1)
    switch(cInd1,
        MSE= MSEI(data,Ic,Jc,iARC,useAbs),
        MAD= MADI(data,Ic,Jc,iARC,useAbs))
}

ExpCritR=function(data,Ic,Jc,rInd1,iARC,I,J,useAbs, colm, rowm){
    #print(paste("rInd1",rInd1))
    switch(rInd1,
        critRlars= Rlars(data,Ic,Jc,iARC,I,J,useAbs),
        critRgee= Rgee(data,Ic,Jc,iARC,I,J,useAbs,colm,rowm))
}

Rlars=function(data,Ic,Jc,cInd,I,J,useAbs){
    switch(cInd,
        larsall = ElarsCrit.block(data,Ic,Jc,1,I,J,useAbs),
        larsrow = ElarsCrit.block(data,Ic,Jc,2,I,J,useAbs),
        larscol = ElarsCrit.block(data,Ic,Jc,3,I,J,useAbs))
}

Rgee=function(data,Ic,Jc,cInd,I,J,useAbs,colm, rowm){
    switch(cInd,
        #data,Ii,Jj,ARC=1,I,J,useAbs,colm,rowm
        #geeall = FEModel.block(data,Ic,Jc,1,I,J,useAbs,colm,rowm),
        #geerow = FEModel.block(data,Ic,Jc,2,I,J,useAbs,colm,rowm),
        #geecol = FEModel.block(data,Ic,Jc,3,I,J,useAbs,colm,rowm))
	
	 geeall = NULL,
        geerow = FEModel.block(data,Ic,Jc,2,I,J,useAbs,colm,rowm),#EgeeCrit.block(data,Ic,Jc,2,I,J,useAbs),
        geecol = FEModel.block(data,Ic,Jc,3,I,J,useAbs,colm,rowm))#EgeeCrit.block(data,Ic,Jc,3,I,J,useAbs))
}

ExpCritMed=function(data,Ic,Jc,cInd,useAbs){
#print(paste("ExpCritMed",cInd))
    switch(cInd,
        EMall = ExpCritMed.block(data,Ic,Jc,1,useAbs),
        EMrow = ExpCritMed.block(data,Ic,Jc,2,useAbs),
        EMcol = ExpCritMed.block(data,Ic,Jc,3,useAbs))
}

MSEI=function(data,Ic,Jc,cInd,useAbs){
#print(paste("MSEI",cInd))
    switch(cInd,
        MSEall = ExpCrit.block(data,Ic,Jc,1,useAbs),
        MSErow = ExpCrit.block(data,Ic,Jc,2,useAbs),
        MSEcol = ExpCrit.block(data,Ic,Jc,3,useAbs))
}
MADI=function(data,Ic,Jc,cInd,useAbs){
    switch(cInd,
        MSEall = ExpCritMdAMd.block(data,Ic,Jc,1,useAbs),
        MSErow = ExpCritMdAMd.block(data,Ic,Jc,2,useAbs),
        MSEcol = ExpCritMdAMd.block(data,Ic,Jc,3,useAbs))
}

###########################################################################################################
#################### ALL criteria subfunctions

######Functions to deal with missingness

mean.m=function(vec){
#print(is.nan(vec))
#print(class(vec))
#print(class(is.nan(vec)))
#print(class(as.vector(is.nan(vec))))
	vec[as.vector(is.nan(vec))]=NA
    n=sum(!is.na(vec))
    summ=sum(vec,na.rm=TRUE)
    summ/n
}



median.m=function(vec){
    vec1=vec[!is.na(vec)]
    median(vec1,na.rm=TRUE)
}

############Criteria functions

preScore_vec=function(xsc,ARCpre){
	sc.out=xsc
        #print(length(xsc))
	if(ARCpre==2){
        #print(dim(xsc))
    	rmeans=rowMeans(xsc,na.rm=TRUE)#apply(xsc,1,mean.m)
    	rsds=apply(xsc,1,sd,na.rm=TRUE)      
    	sc.out=sweep(xsc,1,rmeans)/rsds
  	}
  	if(ARCpre==3){   	
    	cmeans=colMeans(xsc,na.rm=TRUE)#apply(xsc,2,mean.m)
    	csds=apply(xsc,2,sd,na.rm=TRUE)      
    	sc.out=sweep(xsc,2,cmeans)/csds
  	}
  	sc.out
}

preScore_block=function(data,ARC){
	bl.out=data
        #print(dim(data))
        #print(data)
	bl.out=preScore_vec(data,ARC)
	bl.out
	}


###MSE crit
ExpCrit.block=function(data,Ii,Jj,ARC,useAbs){ #want to max
    curdata <- data[Ii,Jj]
    
     if(useAbs == 1) {
            curdata <- abs(curdata)
         }
     
    #print(paste("ExpCrit.block",ARC))
	#print(class(curdata))

    MSEall <- mean.m((curdata-mean.m(curdata))^2)

    if(ARC==1) {
     MSE <- MSEall #MSE of expression measures within the block
    }
    else if(ARC==2){    	
    rmeans <- rowMeans(curdata,na.rm=TRUE)#apply(curdata,1,mean.m)
    rsds <- apply(curdata,1,sd,na.rm=TRUE)
    MSE <- mean.m((sweep(curdata,1,rmeans))^2/MSEall)
    }    
    else if(ARC==3){   		
    cmeans <- colMeans(curdata,na.rm=TRUE)#apply(curdata,2,mean.m)
    csds <- apply(curdata,2,sd,na.rm=TRUE)
    MSE <- mean.m((sweep(curdata,2,cmeans))^2/MSEall)
    }
	MSE
}

###mean expression crit
ExpCritMed.block=function(data,Ii,Jj,ARC,useAbs){ #want to max
    curdata <- data[Ii,Jj]
     
#print(paste("ExpCritMed.block",ARC))
    if(ARC==1) {
      curdataAll <- curdata
        if(useAbs == 1) {
           curdataAll <- abs(curdata)
        }
        MSE=mean.m(as.vector(curdataAll))
    }
   else if(ARC==2){
    rmeans=rowMeans(curdata,na.rm=TRUE)#apply(curdata,1,mean.m)
        if(useAbs == 1) {
            rmeans <- abs(rmeans)
        }
        MSE=median.m(rmeans)
    }    
    else if(ARC==3){   		
    cmeans=colMeans(curdata,na.rm=TRUE)#apply(curdata,2,mean.m)
    
    if(useAbs == 1) {
            cmeans <-abs(cmeans)
        } 
        MSE=median.m(cmeans)
    }
	MSE
}

###LARS crit
ElarsCrit.block=function(data,Ii,Jj,ARC,I,J,useAbs) {#,pscore=NULL){ #want to max #rowMiss,colMiss,
      
      #print(I)
      #print(J)
      
      #print(data[Ii,Jj])
    if(ARC==2 || ARC == 1){
		curdataR <- data[,Jj]
		
            narows <- (rowSums(is.na(curdataR)))
            remnarows <- (rowSums(is.na(curdataR)) > length(Jj)-2)
               if(sum(remnarows) > 0) {
                    rem_index <- which(remnarows)
                    curdataR <- curdataR[-rem_index,]
                    I <- I-length(rem_index)
                    for(k in 1:length(Ii)) {
                      sum <- sum(Ii[k] > rem_index) 
                                   if(sum > 0) {
                                                  Ii[k] <- Ii[k] - sum
                                   }
                    }
              }
              
              if(sum(narows) > 0) {
            curdataR=apply(curdataR,2,missfxn)        
            }          
		if(useAbs==1) curdataR <- abs(curdataR)
                
                #if(is.null(pscore)==0) curdataR=preScore_block(curdataR,ARC)
		IindR=rep(0,I)
   		IindR[Ii]=1
   	}
    #possible axis switched for lars?
	 if(ARC==3 || ARC == 1){
		curdataC <- data[Ii,]
                   
            nacols <- (colSums(is.na(curdataC)))
            remnacols <- (colSums(is.na(curdataC)) > length(Ii)-2)
                 if(sum(remnacols) > 0) {
                    rem_index <- which(remnacols)
                    curdataC <- curdataC[,-rem_index]
                    J <- J-length(rem_index)
                    for(k in 1:length(Jj)) {
                             sum <- sum(Jj[k] > rem_index) 
                                   if(sum > 0) {
                                        Jj[k] <- Jj[k] - sum                                      
                                   }
                    }
                }
                
            if(sum(nacols) > 0) {
                curdataC=apply(curdataC,1,missfxn)
            }
            else {
                curdataC <- t(curdataC)
            }
            
            #if(sum(nacols) > 0) {
            #    curdataC=t(apply(curdataC,1,missfxn))
            #}
            
                if(useAbs==1) curdataC <- abs(curdataC)
                              				
                #if(is.null(pscore)==0) curdataC=preScore_block(curdataC,ARC)
		IindC=rep(0,J)
   		IindC[Jj]=1
   	}
 
	  if(ARC == 2 || ARC == 1) {
   		Q=lars(curdataR,as.matrix(IindR))
		Qcv=cv.lars(curdataR,IindR,K = 5,plot.it=FALSE)
		min(Qcv$cv)
		s1=Qcv$index[which.min(Qcv$cv)]
		fts=predict(Q,newx=curdataR,s=s1,type="fit",mode="fraction")
   		SSE=sum((IindR-fts$fit)^2)
   		SST=sum((IindR-mean(IindR))^2)
   		LARSR <- 1-SSE/SST
		LARS <- LARSR
		#print(paste("R",LARSR))
		}
	  if(ARC == 3 || ARC == 1) {
	  #print(IindC)
	  #print(curdataC)
   		Q=lars(curdataC,as.matrix(IindC))
		#print("1")
		#print(Q)
		Qcv=cv.lars(curdataC,IindC,K = 5,plot.it=FALSE)
		#print("2")
		#print(Q)
		min(Qcv$cv)
		print("3")
		s1=Qcv$index[which.min(Qcv$cv)]
	        #print("4")
		#print(s1)
		fts=predict(Q,newx=curdataC,s=s1,type="fit",mode="fraction")
		#print("5")
		#print(fts)
   		SSE=sum((IindC-fts$fit)^2)
		#print("6")
		#print(SSE)
   		SST=sum((IindC-mean(IindC))^2)
		#print("7")
		#print(SST)
   		LARSC <- 1-SSE/SST
		#print("8")
		#print(LARSC)
		    if(ARC == 3) {
			 LARS <- LARSC
		    }
		    else if(ARC == 1) {
			 LARS <- (LARSR + LARSC) /2
		    }
		    #print(paste("C",LARSC))
		}		
		#print(paste("result",LARS))
		LARS
}

###GEE crit
EgeeCrit_slow.block=function(data,Ii,Jj,ARC,I,J,seAbs,pscore=NULL, RepM=0){ #want to max #rowMiss,colMiss,u
#print(ARC)

       if(ARC==2){
               curdata=data[,Jj]
               
              narows <- (rowSums(is.na(curdata)))
            remnarows <- (rowSums(is.na(curdata)) > length(Jj)-2)
               if(sum(remnarows) > 0) {
                    rem_index <- which(remnarows)
                    curdata <- curdata[-rem_index,]
                    I <- I-length(rem_index)
                    for(k in 1:length(Ii)) {
                      sum <- sum(Ii[k] > rem_index) 
                                   if(sum > 0) {
                                                  Ii[k] <- Ii[k] - sum
                                   }
                    }
              }
              
            if(sum(narows) > 0) curdata=apply(curdata,2,missfxn)
               
               if(useAbs==1) curdata=abs(curdata)
               
               #if(is.null(pscore)==0) curdata=preScore_block(curdata,ARC)
               Iind=rep(0,I)
               Iind[Ii]=1
               geI=matrix(t(matrix(rep(Iind,length(Jj)),ncol=length(Jj))),ncol=1)
               idD=NULL
               for(iy in 1:I) idD=c(idD,rep(iy,length(Jj)))
                idA=rep(1:length(Jj),I)
       }
         else if(ARC==3){
               curdata=data[Ii,]
               
                nacols <- (colSums(is.na(curdata)))
                remnacols <- (colSums(is.na(curdata)) > length(Ii)-2)
                 if(sum(remnacols) > 0) {
                    rem_index <- which(remnacols)
                    curdata <- curdata[,-rem_index]
                    J <- J-length(rem_index)
                    for(k in 1:length(Jj)) {
                             sum <- sum(Jj[k] > rem_index) 
                                   if(sum > 0) {
                                        Jj[k] <- Jj[k] - sum                                      
                                   }
                    }
                }
                
            if(sum(nacols) > 0) curdata=apply(curdata,1,missfxn)            
            else curdata <- t(curdata)
            
             if(useAbs==1) curdata=abs(curdata) 
                              
               #print("1")
               #if(is.null(pscore)==0) curdata=preScore_block(curdata,ARC)
                #print("2")
               Jind=rep(0,J)
               Jind[Jj]=1
                #print("3")
                geI=matrix(t(matrix(rep(Jind,length(Ii)),ncol=length(Ii))),ncol=1)
                #print("4")
                idD=NULL
               for(iy in 1:J) idD=c(idD,rep(iy,length(Ii)))
                idA=rep(1:length(Ii),J)
       }
       reps=dim(curdata)[2]
       yY=matrix(t(curdata),ncol=1)       
        gee1=lm(yY~geI:factor(idA))
        #print("6")
     if(RepM==1) {
        corD=cor(t(matrix(residuals(gee1),nrow=reps)))
        #geeInd=geeglm(yY~geI,family=gaussian,id=idD)
        gee1=geeglm(yY~geI,family=gaussian,id=idD,zcor=corD)    
    }
        #print("8")
   SSE=sum((yY-fitted(gee1))^2)
   SST=sum((yY-mean(yY))^2)
   1-SSE/SST
}



### This does the same thing EgeeCrit.block does but with simply calculating the SSE and SST
### for the fixed effects model rather than fitting the full model 
FEModel.block=function(data,Ii,Jj,ARC=1,I,J,useAbs,thiscolm,thisrowm){ 
       if(ARC==2){
           curdata=data[,Jj]
           
           narows <- (rowSums(is.na(curdata)))
           #require at least 2 data points
            remnarows <- (rowSums(is.na(curdata)) > length(Jj)-2)
               if(sum(remnarows) > 0) {
                    rem_index <- which(remnarows)
                    curdata <- curdata[-rem_index,]
                    I <- I-length(rem_index)
                    #remove all 'NA' rows
                    for(k in 1:length(Ii)) {
                      sum <- sum(Ii[k] > rem_index) 
                                   if(sum > 0) {
                                                  Ii[k] <- Ii[k] - sum
                                   }
                    }
              }
              
           if(sum(narows) > 0) curdata = apply(curdata,2,missfxn)                
           
           if(useAbs==1) curdata=abs(curdata)

           #if(is.null(pscore)==0) curdata=preScore_block(curdata,ARC)
	   
	   
           err1=sweep(curdata[Ii,],2,colMeans(curdata[Ii,]))
           err2=curdata[-Ii,]-mean(as.vector(curdata[-Ii,]))
       }
       if(ARC==3){
           curdata=data[Ii,]
           
            nacols <- (colSums(is.na(curdata)))
                remnacols <- (colSums(is.na(curdata)) > length(Ii)-2)
                 if(sum(remnacols) > 0) {
                    rem_index <- which(remnacols)
                    curdata <- curdata[,-rem_index]
                    J <- J-length(rem_index)
                    for(k in 1:length(Jj)) {
                             sum <- sum(Jj[k] > rem_index) 
                                   if(sum > 0) {
                                        Jj[k] <- Jj[k] - sum                                      
                                   }
                    }
                }
                
            if(sum(nacols) > 0) {
                curdata=t(apply(curdata,1,missfxn))      
            }            

           if(useAbs==1) curdata=abs(curdata)

           #if(is.null(pscore)==0) curdata=preScore_block(curdata,ARC)
	   
	   print("FEModel.block curdata[,Jj]")
	   print(curdata[89,Jj])
	   print(mean(curdata[89,Jj]))
	   print("FEModel.block rowMeans")
	   print(rowMeans(curdata[,Jj]))
	   print("diff")
	   print(thisrowm - rowMeans(curdata[,Jj]))
	   
	   
           err1=sweep(curdata[,Jj],1,rowMeans(curdata[,Jj]))
           err2=curdata[,-Jj]-mean(as.vector(curdata[,-Jj]))

       }
       SSE=sum(c(err1,err2)^2)
       SST=sum((curdata-mean(curdata))^2)
       1-SSE/SST
}



### This does the same thing EgeeCrit.block does but with simply calculating the SSE and SST
### for the fixed effects model rather than fitting the full model 
FEModelNEW.block=function(data,Ii,Jj,ARC,I,J,useAbs,colm,rowm) {#pscore=NULL,

#print("FEM")
#print(ARC)

#print(colm)
#print(rowm)
#print(data[Ii,Jj])

curdata <- NULL
curdataR <- data[,Jj]
curdataC <- data[Ii,]


     if(ARC==1){
           #print("ARC == 1")
           narows <- (rowSums(is.na(curdataR)))
           #require at least 2 data points
            remnarows <- (rowSums(is.na(curdataR)) > length(Jj)-2)
               if(sum(remnarows) > 0) {
                    rem_index <- which(remnarows)
                    curdataR <- curdataR[-rem_index,]
                    I <- I-length(rem_index)
                    #remove all 'NA' rows
                    for(k in 1:length(Ii)) {
                      sum <- sum(Ii[k] > rem_index) 
			 if(sum > 0) {
			      Ii[k] <- Ii[k] - sum
			 }
                    }
              }
              
           if(sum(narows) > 0) {
	       curdataR = apply(curdataR,2,missfxn) 
	       #print("imputed R")
	   }
           
           if(useAbs==1) {
	   #print("row abs")
	   curdataR=abs(curdataR)
	   }
	   #print(length(which(curdataR < 0)))
	   #print(curdataR)

           #if(is.null(pscore)==0) curdataR=preScore_block(curdataR,ARC)
	   
	   #colmthis <- colMeans(curdataR[Ii,])	   
           err1R=sweep(curdataR[Ii,],2,colm)#colmthis)
           err2R=curdataR[-Ii,]-mean(curdataR[-Ii,])#as.vector()
           
            nacols <- (colSums(is.na(curdataC)))
                remnacols <- (colSums(is.na(curdataC)) > length(Ii)-2)
                 if(sum(remnacols) > 0) {
                    rem_index <- which(remnacols)
                    curdataC <- curdataC[,-rem_index]
                    J <- J-length(rem_index)
                    for(k in 1:length(Jj)) {
                             sum <- sum(Jj[k] > rem_index) 
                                   if(sum > 0) {
                                        Jj[k] <- Jj[k] - sum                                      
                                   }
                    }
                }
                
            if(sum(nacols) > 0) {
                curdataC=t(apply(curdataC,1,missfxn))
		#print("imputed C")
            }
	     #print(curdataC)


          if(useAbs==1)  {
	  #print("col abs")
	  curdataC=abs(curdataC)
	  }	   
           #if(is.null(pscore)==0) curdataC=preScore_block(curdataC,ARC)
	   
	   #print(length(which(curdataC < 0)))
	   
	   #rowmthis <- rowMeans(curdataC[,Jj])
           err1C=sweep(curdataC[,Jj],1,rowm)#rowmthis)
           err2C=curdataC[,-Jj]-mean(curdataC[,-Jj])#as.vector()
	   #print("errC")
	   #print(err1C)
	   #print(err2C)
	   #print("curdata")
	   #print(curdataC)
	   #print(curdataC[,Jj])
	   #print(curdataC[,-Jj])
       }
       
       if(ARC==2){
           curdata=data[,Jj]
           
           narows <- (rowSums(is.na(curdata)))
           #require at least 2 data points
            remnarows <- (rowSums(is.na(curdata)) > length(Jj)-2)
               if(sum(remnarows) > 0) {
                    rem_index <- which(remnarows)
                    curdata <- curdata[-rem_index,]
                    I <- I-length(rem_index)
                    #remove all 'NA' rows
                    for(k in 1:length(Ii)) {
                      sum <- sum(Ii[k] > rem_index) 
                                   if(sum > 0) {
                                                  Ii[k] <- Ii[k] - sum
                                   }
                    }
              }
              
           if(sum(narows) > 0) curdata = apply(curdata,2,missfxn)                
           
           if(useAbs==1) curdata=abs(curdata)

           #if(is.null(pscore)==0) curdata=preScore_block(curdata,ARC)
	   
           err1=sweep(curdata[Ii,],2,colm)#colMeans(curdata[Ii,]))
           err2=curdata[-Ii,]-mean(as.vector(curdata[-Ii,]))
       }
       if(ARC==3){
           curdata=data[Ii,]
           
            nacols <- (colSums(is.na(curdata)))
                remnacols <- (colSums(is.na(curdata)) > length(Ii)-2)
                 if(sum(remnacols) > 0) {
                    rem_index <- which(remnacols)
                    curdata <- curdata[,-rem_index]
                    J <- J-length(rem_index)
                    for(k in 1:length(Jj)) {
                             sum <- sum(Jj[k] > rem_index) 
                                   if(sum > 0) {
                                        Jj[k] <- Jj[k] - sum                                      
                                   }
                    }
                }
                
            if(sum(nacols) > 0) {
                curdata=t(apply(curdata,1,missfxn))      
            }            

           if(useAbs==1) curdata=abs(curdata)
	  
           #if(is.null(pscore)==0) curdata=preScore_block(curdata,ARC)	   
	   #rowm <- rowMeans(curdata[,Jj])
           err1=sweep(curdata[,Jj],1,rowm)#rowMeans(curdata[,Jj]))
           err2=curdata[,-Jj]-mean(as.vector(curdata[,-Jj]))
       }
       
       
       SSE <- 0
       SST <- 0
       SSER <- 0
       SSTR <- 0
       SSEC <- 0
       SSTC <- 0

       if(ARC != 1) {
	  SSE=sum(c(err1,err2)^2)
	  SST=sum((curdata-mean(curdata))^2)	  
       }
       else {
     	  SSER=sum(c(err1R,err2R)^2)
	  #print("SSER")
	  #print(err1R)
	  #print("err2R")
	  #print(SSER)
     	  SSTR=sum((curdataR-mean(curdataR))^2)
	  
	  SSEC=sum(c(err1C,err2C)^2)
	  #print("SSEC")
	  #print(err1C)
	  #print(err2C)
	  #print(SSEC)
     	  SSTC=sum((curdataC-mean(curdataC))^2)
	  
	  #print("SSE/SST")
	  #print(paste(SSER,SSTR,SSEC,SSTC,sep=" "))
	  #print(paste(SSER/SSTR,sep=" "))
	  #print(paste(SSEC/SSTC,sep=" "))
	  #print(paste((1-SSEC/SSTC) , (1-SSER/SSTR) , (1-SSEC/SSTC) + (1-SSER/SSTR) ))
       }
     if(ARC != 1) {
	  FEM <- 0
     if(SST != 0) {
	   FEM <- 1-SSE/SST
	  }
       FEM
       }
     else if (ARC == 1) {
	  FEMC <- 0
	  if(SSEC != 0) {
	       FEMC <- 1-SSEC/SSTC
	  }
	  FEMR <- 0
	  if(SSTR != 0) {
	       FEMR <- 1-SSER/SSTR
	  }
       (FEMC + FEMR)/2
       }     

}

###stats for row and col means and frxns 
rowColMeansFrxn=function(data, ArcI,abs=F,Ii,Jj) {

	  data_impR <- apply(data, 2, missfxn)
	   data_impC <- t(apply(data, 1, missfxn))
	   
	   if(abs) {
	   data_impR <- abs(data_impR)
	    data_impC <- abs(data_impC)
	   }
	   
	   print(Ii)
	   print("rowColMeansFrxn data_impR")
	   print(dim(data_impR))
	   print(max(Ii))
	   print(max(Jj))
	   print(data_impR[89,])
	   print(mean(data_impR[89,]))

	  rowm <- rowMeans(data_impR)
	    rowp <- length(which(rowm > 0))/length(rowm)
	    rown <- length(which(rowm< 0))/length(rowm)
	    frxnsignR <- max(rowp, rown)
	    rowmaxpos <- 0
	       if(frxnsignR == rowp) {
		  rowmaxpos <- 1
	       }
	      
	      
	   
	    colm <- colMeans(data_impC)
	    colp <- length(which(colm > 0))/length(colm)
	    coln <- length(which(colm < 0))/length(colm)
	    frxnsignC <- max(colp, coln)
	    colmaxpos <- 0
	       if(frxnsignC == colp) {
		  colmaxpos <- 1
	       }
	       
	       frxnsignA <- 0
	       if(ArcI == 1) {
	       #print(paste(rowp,colp,(rowp+colp)/2))
	       #print(paste(rown, coln, (rown + coln)/2))
	       frxnsignA <- max((rowp+colp)/2, (rown + coln)/2)
	       }
	       
	       list( colm, rowm, frxnsignR, frxnsignC, frxnsignA)
}

###Mean Sq Dev from Mean
ExpCritMnSMn.block=function(data,Ii,Jj){ #want to max
    curdata <- data[Ii,Jj]
    MSEall=mean.m((curdata - mean.m(curdata))^2) #MSE of expression measures within the block
    cmeans=colMeans(curdata,na.rm=TRUE)#apply(curdata,2,mean.m)
    rmeans=rowMeans(curdata,na.rm=TRUE)#apply(curdata,1,mean.m)
    MSEc=mean.m(sweep(curdata,2,cmeans)^2)
    MSEr=mean.m(sweep(curdata,1,rmeans)^2)
    c(MSEall,MSEr,MSEc)
}

###Median Sq Dev from Mean
ExpCritMdSMn.block=function(data,Ii,Jj){ #want to max
    curdata <- data[Ii,Jj]
    MSEall=median.m((data[Ii,Jj]-mean.m(curdata))^2) #MSE of expression measures within the block
    cmeans=colMeans(curdata,na.rm=TRUE)#apply(curdata,2,mean.m)
    rmeans=rowMeans(curdata,na.rm=TRUE)#apply(curdata,1,mean.m)
    MSEc=median.m(sweep(curdata,2,cmeans)^2)
    MSEr=median.m(sweep(curdata,1,rmeans)^2)
    c(MSEall,MSEr,MSEc)
}

###Mean Sq Dev from Median
ExpCritMnSMd.block=function(data,Ii,Jj){ #want to max
    curdata <- data[Ii,Jj]
    MSEall=mean.m((curdata-median.m(curdata))^2) #MSE of expression measures within the block
    cmeans=apply(curdata,2,median.m)
    rmeans=apply(curdata,1,median.m)
    MSEc=mean.m(sweep(curdata,2,cmeans)^2)
    MSEr=mean.m(sweep(curdata,1,rmeans)^2)
    c(MSEall,MSEr,MSEc)
}

###Median Sq Dev from Median
ExpCritMdSMd.block=function(data,Ii,Jj){ #want to max
    curdata <- data[Ii,Jj]
    MSEall=median.m((curdata-median.m(curdata))^2) #MSE of expression measures within the block
    cmeans=apply(curdata,2,median.m)
    rmeans=apply(curdata,1,median.m)
    MSEc=median.m(sweep(curdata,2,cmeans)^2)
    MSEr=median.m(sweep(curdata,1,rmeans)^2)
    c(MSEall,MSEr,MSEc)
}

###Mean Abs Dev from Median
ExpCritMnAMd.block=function(data,Ii,Jj){ #want to max
    curdata <- data[Ii,Jj]
    MSEall=mean.m(abs(curdata-median.mcurdata)) #MSE of expression measures within the block
    cmeans=apply(curdata,2,median.m)
    rmeans=apply(curdata,1,median.m)
    MSEc=mean.m(abs(sweep(curdata,2,cmeans)))
    MSEr=mean.m(abs(sweep(curdata,1,rmeans)))
    c(MSEall,MSEr,MSEc)
}

###Median Abs Dev from Median
ExpCritMdAMd.block=function(data,Ii,Jj,ARC,useAbs){ #want to max
    curdata <- data[Ii,Jj]
    
    if(useAbs==1) curdata <- abs(curdata)
    
    if(ARC==1)  MSE=median.m(abs(curdata-median.m(curdata))) #MSE of expression measures within the block
    else if(ARC==2){
    	rmeans=apply(curdata,1,median.m)
    	MSE=median.m(abs(sweep(curdata,1,rmeans)))
    }
    else if(ARC==3){
    	cmeans=apply(curdata,2,median.m)
    	MSE=median.m(abs(sweep(curdata,2,cmeans)))
    }
    MSE
}


###
KendExp.critOLD=function(data,Ii,Jj, ArcI,useAbs=1){
    curdataC <- c()
    curdata <- data[Ii, Jj]
            
        if(ArcI == 1) {            
            curdataC <- t(data[Ii, Jj])
        }
        else if(ArcI == 2) {            
            narows <- (rowSums(is.na(curdata)))
            if(sum(narows) > 0) {
                curdata=apply(curdata,2,missfxn)        
            }               
        }
        else if(ArcI == 3) {            
            nacols <- (colSums(is.na(curdata)))               
            if(sum(nacols) > 0) {
                curdata=apply(curdata,1,missfxn)        
            } else {
                curdata <- t(curdata)
            }
        }        
        
	ktest=kendall(curdata) #should omit data on its own
        #averages for total block criterion
        if(ArcI == 1) {
         ktestC=kendall(curdataC)
         ktest$value <- (ktest$value + ktestC$value) / 2
         ktest$p.val <- (ktest$p.val+ ktestC$p.val) / 2
        }
 	#c(ktest$value,ktest$statistic)
	#ktest$statistic
	c(ktest$value,ktest$p.val)#,ktest$statistic)
	}
        

###
KendExp.crit=function(data,Ii,Jj, ArcI,useAbs){
    curdataC <- c()
    curdata <- data[Ii, Jj]

    #print(curdata)
    #print(curdataC)
        if(ArcI != 2) {
            curdataC <- data[Ii, Jj]
        }

        if(ArcI == 2) {
            narows <- (rowSums(is.na(curdata)))
            if(sum(narows) > 0) {
                curdata=apply(curdata,2,missfxn)        
            }	    
        }
        else  {            #if(ArcI == 3)
            nacols <- (colSums(is.na(curdataC)))               
            if(sum(nacols) > 0) {
                curdataC=apply(curdataC,1,missfxn)        
            } else {
                curdataC <- t(curdataC)
            }
        }        
        
	 if(useAbs==1) {
	 curdata=abs(curdata)
         if(ArcI == 3) {
              curdataC=abs(curdataC)
         }
	 }

	if(ArcI != 3) {
	  ktest=kendall(curdata) #should omit data on its own
	}
	#print(ktest)
    val <- 0
    pval <- 0
    ktestC <- 0

     if(ArcI != 2) {
        ktestC <- kendall(curdataC)
        #print(ktestC)
     }
        #averages for total block criterion
    if(ArcI == 1) {

    #print(ktest$value)
    #print(ktestC$value)
            val <- (ktest$value + ktestC$value) / 2
             pval <- (ktest$p.val+ ktestC$p.val) / 2
    }
    else if(ArcI == 2) {
        val <- ktest$value
        pval <- ktest$p.val
    }
    else if(ArcI == 3) {
        val <- ktestC$value
        pval <- ktestC$p.val
    }

	c(val,pval)#,ktest$statistic)
	}
        
	
###
Cauchy.blockp=function(val, null) {
    n=pcauchy(val, null[1], null[2])
    n
}

###no data imputation for interaction criterion
InterCrit.block=function(GIblock,Ii){
    mean(GIblock[Ii,Ii],na.rm=TRUE)
    #edges <- sum(GIblock[Ii,Ii][lower.tri(GIblock[Ii,Ii], diag=TRUE)])
    #2 * (edges) / (length(Ii)*(length(Ii)))
}

###Pearson correlation crit
CorrMove.block=function(Iold,Jold,move_objects,isGene,expr_data,useAbs){  #Measures the absolute correlation of the mean of the move objects with mean of the old
    Cor=0
    if(length(move_objects) > 0) {
        if(isGene==1){
            Cor <- cor(rowMeans(expr_data[Iold,Jold],na.rm=TRUE),rowMeans(expr_data[move_objects,Jold],na.rm=TRUE),use="pairwise.complete.obs")
            if(useAbs==1) {
                Cor <- abs(cor)
            }
        }
        if(isGene==0){
            Cor <- cor(colMeans(expr_data[Iold,Jold],na.rm=TRUE),colMeans(expr_data[Iold,move_objects],na.rm=TRUE),use="pairwise.complete.obs")
            if(useAbs==1) {
                Cor <- abs(cor)
            }
        }
    }
    Cor  
}

###Pearson correlation
Corr.block=function(data,Ii,Jj, CorIndex,useAbs){  #Measures the mean pairwise (absolute) correlation of the block
    AbCor <- 0
    AbCorC <- 0
    AbCorR <- 0
    curdata <- data[Ii,Jj]
   
   #print(CorIndex)
   #print(curdata)
    #row
        if(CorIndex==3 || CorIndex == 1){
	    cors <- cor(t(curdata), use="pairwise.complete.obs")
            if(useAbs == 0) {
		cors <- (cors + 1)/2
            }
	    else if(useAbs == 1) {
	       cors <- abs(cors)
	    }
	    AbCorC <- mean(cors[lower.tri(cors, diag=FALSE)])
	    AbCor <- AbCorC
        }
     #column
        if(CorIndex==2 || CorIndex == 1){
	    cors <- cor(curdata, use="pairwise.complete.obs")
            if(useAbs == 0) {
		cors <- (cors + 1)/2
            }
	    else if(useAbs == 1) {
	       cors <- abs(cors)
	    }
	    #print(cors)
            AbCorR <- mean(cors[lower.tri(cors, diag=FALSE)])
	    
         if(CorIndex != 1) {
	  AbCor <- AbCorR
	  }
	  else {
	  #print(AbCorR)
	  #print(AbCorC)
	  #print(paste(AbCorR, AbCorC))
	   AbCor <- ( AbCorR + AbCorC) /2
	  }
        }
	
	#print(AbCorR)
	#print(AbCorC)
    AbCor  
}

###Euclidean distance (row and column) criterion
Euclidean.block=function(data,Ii,Jj,cInd,useAbs){
    
curdata <- data[Ii,Jj]
        
	#print(dim(curdata))
	#print(cInd)
matmean <- NaN

    if(cInd == 2 || cInd == 1) {
        
        narows <- (rowSums(is.na(curdata)))          
        if(sum(narows) > 0) {
            curdata=apply(curdata,2,missfxn)            
        }
        if(useAbs==1) curdata <- abs(curdata)
	
        mat <- mat.or.vec(length(Ii),length(Ii))
        #print(mat)
        #print(dim(mat))
        diag(mat) <- 0
        for(i in 1:length(Ii)) {
           for(j in i:length(Ii)) {
            #print(cat(i,j,dim(curdata),sep=" "))
        if(i != j) {            
            mat[i,j] <- mat[j,i] <- Euclidean(curdata[i,],curdata[j,])
            }
        }  
        }
        matmeanR <- mean(mat[lower.tri(mat)])
	#print(paste("R",matmeanR))
	matmean <- matmeanR     
    }
     if(cInd == 3 || cInd == 1) {
        
        nacols <- (colSums(is.na(curdata)))
        if(sum(nacols) > 0) {
            curdata=t(apply(curdata,1,missfxn))
        }
        if(useAbs==1) curdata <- abs(curdata)
	
        mat <- mat.or.vec(length(Jj),length(Jj))
        diag(mat) <- 0
        for(i in 1:length(Jj)) {
           for(j in i:length(Jj)) {
            if(i != j) {
            mat[i,j] <- mat[j,i] <- Euclidean(curdata[,i],curdata[,j])
	  #print(curdata[,i])
	     #print(curdata[,j])
	  #Euclidean(curdata[,i],curdata[,j])
	  #  print(mat[i,j])
            }
        }  
        }
        matmeanC <- mean(mat[lower.tri(mat)])
	#print(paste("C",cInd,matmeanC))
	if(cInd == 3) {
	  matmean <- matmeanC
	  #print(paste("in",matmean,matmeanC))
	}
	else if(cInd == 1){
	#print(matmeanR)
	#print(matmeanC)
	 matmean <- (matmeanR + matmeanC) /2
	}
	
	#print(matmean)
    }
    matmean
}
  
Euclidean=function(x1, x2) {
   temp <- x1 - x2
   sqrt(sum(temp * temp))
}

GeneWblock=function(BlockM,expr_data,Ii,Jj){  #want to maximize (no p-value necessary)
    GIndex=as.vector(BlockM[,Jj]) #Gives vector specifying 1/0 if gene is in or not in the block
    Gexpr_data=as.vector(expr_data[,Jj])
    if(sum(is.na(Gexpr_data))>0){
        #mI=is.na(Gexpr_data)
        Gexpr_data[is.na(Gexpr_data)]=mean(Gexpr_data,na.rm=TRUE)
        #mvector=rep(0,length(Gexpr_data))
        #mvector[!is.na(Gexpr_data)]=1
        #p.fit=polymars(GIndex,cbind(Gexpr_data,mvector),classify=TRUE)
        p.fit=polymars(GIndex,Gexpr_data,classify=TRUE)
        #p.act=predict.polymars(p.fit,cbind(Gexpr_data,1),classify=TRUE)
    }
    CVR2=mean(p.fit$Rsquared) #CV Rsquared relating in block membership (0/1) with expression measures using data adaptive method
    CVR2
}

###missing data imputation function
missfxn=function(vec){
    mean <- mean(vec,na.rm=TRUE)
    vec[is.na(vec)]=mean
    vec[is.nan(vec)]=mean
    if(is.na(mean)) {
        cat("Error imputed mean is na ", mean, vec, "\n", which(is.na(mean)))
    }
    vec
}


#function to identify perfectly correlated feature data (variance of 0)
pre_Feature=function(GFeat,mcor=1){
    x1=GFeat[,c("HET_AV","HOM_AV")]
    x1[x1=="ND"]=NA
    GFeat[,c("HET_AV","HOM_AV")]=x1

    GFeat=apply(as.matrix(GFeat),2,as.numeric)
    if(sum(is.na(GFeat))>0){
        ###deal with all missing rows
        GFeat=apply(GFeat,2,missfxn)
    }
    Uni=(apply(GFeat,2,function(x) sum(duplicated(x)))!=(dim(GFeat)[1]-1))
    GFeat=GFeat[,Uni]
    corM=cor(GFeat)
    indc=which(corM>=mcor,arr.ind=TRUE)
    indc2=indc[apply(indc,1,duplicated)[2,]==FALSE,]
    dup1=unique(apply(indc2,1,function(x) paste(x[order(x)],collapse=",")))
    dup2=sapply(dup1,function(x) as.numeric(unlist(strsplit(x[1],","))))
    GFeat_new=GFeat[,-unique(as.vector(dup2[1,]))]
    GFeat_new
    }


FeatureWblock=function(Ii,Jj,GFeat,Ifactor,I,J){
	GIndex=rep(0,I) #Gives vector specifying 1/0 if gene is in or not in the block
	GIndex[Ii]=1
	PobjI=polymars(GIndex,GFeat,classify=TRUE,factors=Ifactor)
	CVR2I=sum(PobjI$Rsquared) 
	CVR2I#list(CVRI=CVR2I,IFt=PobjI$model[,1])
}

###The one below should work	
FeatureWblock_larsI=function(Ii,GFeat,I,msteps=NULL,Ifact=NULL){
    GIndex=rep(0,I) #Gives vector specifying 1/0 if gene is in or not in the block
    GIndex[Ii]=1
    #print("1")
    if(is.null(msteps)){
    	Q=lars(GFeat,GIndex)
        #print("2")
		Qcv=cv.lars(GFeat,GIndex,K = 5,plot.it=FALSE)
                #print("3")
    } else if(!is.null(msteps)){
		Q=lars(GFeat,GIndex,max.steps=msteps)
		Qcv=cv.lars(GFeat,GIndex,K = 5,plot.it=FALSE,max.steps=msteps)
	}
	CVR2I=min(Qcv$cv)
	s1=Qcv$index[which.min(Qcv$cv)]
        #print("4")
	cfs=predict(Q,s=s1,type="coefficients",mode="fraction")$coefficients
        #print("5")
	cfr=predict(Q,newx=GFeat,s=s1,type="fit",mode="fraction")$fit
        #print("6")
	R2v=1-sum((GIndex-cfr)^2)/sum((GIndex-mean(GIndex))^2)
        #print("7")
	c(R2v,cfs) #returns a vector with the first value the R2,and the rest the coefficients for the features
}

####The one below needs debugging still on my side
FeatureWblock_larsE=function(Ii,Jj,GFeat,Ifact1,expr_data,I,J){
	GIndex=expr_data[Ii,]  ## Does this work?  Can LARS do multivar outcomes
	Q=lars(GFeat,GIndex)
	Qcv=cv.lars(GFeat,GIndex,K = 5,plot.it=FALSE)
	CVR2I=min(Qcv$cv)
	s1=Qcv$index[which.min(Qcv$cv)]
	cfs=predict(Q,s=s1,type="coefficients",mode="fraction")$coefficients
    c(CVR2I,cfs) #returns a vector with the first value the R2,and the rest the coefficients for the features
}

###mean pairwise Hamming distance between gene binary vectors
FeatHamming.block=function(data,Ii){
    ham=1  
    curdata <- data[Ii,]
    dists <- HamDist(curdata)
    ###Hamming is a similarity measure, so invert
    ham = 1/( mean(dists[lower.tri(dists, diag=FALSE)]) + 1 )
    ham  
}


###Hamming distance function for a matrix
HamDist=function(data, sym=F) {
   dim <- dim(data)
   #print(dim)
   d <- mat.or.vec(dim[1],dim[1])
    for(j in 1:dim[1]) {
        print(".")
        for(i in 1:dim[1]) {
            if(i > j) {
                #cat(i,j,"\n")
                #cat(length(data[,i]), length(data[,j])
                d[i,j] <- hamming.distance(data[i,],data[j,])
                d[j,i] <- d[i,j]
            }
            else if(i ==j) {
                d[j,i] <- d[i,j] <- 0
            }
    }
   }
   d
}

###
hamming.distance <- function(x,y){
  sum(x != y)
}

###
ExtractFeatures=function(Ii,Jj,GFeat,Ifactor,I,J){
    GIndex=rep(0,I) #Gives vector specifying 1/0 if gene is in or not in the block
    GIndex[Ii]=1
    if(sum(is.na(GFeat))>0){
        GFeat=apply(GFeat,2,missfxn)
    }
    CVR2Im=polymars(GIndex,GFeat,classify=TRUE,factors=Ifactor)
    CVR2Im$model[,1]
    }

##Mean centering function
center=function(x){
    (x-mean(x))/sd(x)
    }
    
    
    ###The function to create an array from the multiple files
    createArray=function(nfiles,ncols,nboot,fileprefix){
        outarray=array(NA,dim=c(nfiles,ncols,nboot))
        for(i in 1:nfiles){
            outarray[i,,]=read.table(file=paste(fileprefix,i,sep="_"),sep = " ")
        }
        outarray
    }

##
###Code for batch moves
##
BatchCreate=function(Dmat,missvec,Ii,Jj,Ig,Ia,pSize=.2,dmethod="correlation",Ulim,useAbs=1,respectLim=TRUE){
	# ##Function parameters
	# Dmat is the data matrix, in this case the expression matrix (rows=genes, columns=experiments)
	# missvec is a vector of 0,1's if Ig=1, its length should be dim(expr_data)[1], if Ig=0 its length should be dim(expr_data)[2]
	# Ig indicator for a gene move
	# Ia indicator for an addition move
	# pSize is max proportion of block added or subtracted, default 20% of current block can be add/subtracted
	# dmethod is the distance method used to calculate the distance matrix
	# Ulim is the upper limit of block size in the dimension indicated by Ig (if Ig==1, Ulim=Imax)
	# respectLim is default true, and forces maxSize to be small enough to add to the block, if respectLim is set as true block must not already be too big
	
	if(Ia==1){ #addition move
		if(Ig==1){
			xmat=abs(t(Dmat[-Ii,Jj][(missvec==1)[-Ii],]))#xmat=t(Dmat[-Ii,Jj][(missvec==1)[-Ii],])#
			xmat=t(apply(xmat,2,function(xm1){ xm1[is.na(xm1)]=mean(xm1,na.rm=TRUE); (xm1)}))
			IndB=(1:dim(Dmat)[1])[-Ii][(missvec==1)[-Ii]]
		} 
		if(Ig!=1){
			xmat=abs((Dmat[Ii,-Jj][,(missvec==1)[-Jj]]))#xmat=(Dmat[Ii,-Jj][,(missvec==1)[-Jj]])#
			xmat=t(apply(xmat,2,function(xm1){ xm1[is.na(xm1)]=mean(xm1,na.rm=TRUE); xm1}))
			IndB=(1:dim(Dmat)[2])[-Jj][(missvec==1)[-Jj]]
		} 
	}
	if(Ia!=1){ #subtraction move
		if(Ig==1){
			xmat=abs(t(Dmat[Ii,Jj])) #xmat=t(Dmat[Ii,Jj])#
			xmat=t(apply(xmat,2,function(xm1){ xm1[is.na(xm1)]=mean(xm1,na.rm=TRUE); xm1}))
			IndB=Ii                        
                        #print(rowSums(is.na(xmat)))
		}
		if(Ig!=1){
			xmat=abs((Dmat[Ii,Jj])) #xmat=(Dmat[Ii,Jj])#
			xmat=t(apply(xmat,2,function(xm1){ xm1[is.na(xm1)]=mean(xm1,na.rm=TRUE); xm1}))
			IndB=Jj
                        #print(colSums(is.na(xmat)))
		} 
	}

	# hc=diana(xmat,diss=FALSE,stand=TRUE,keep.diss=FALSE,keep.data=FALSE)
	# 
	# d1=dist(xmat,method=dmethod)
	# hc <- hclust(d1, "ward")

        ###fastcluster version
        #xdist <- as.dist(CorDist(xmat,0)) #HARD CODED TO NOABS if abs(data) above
        #hc=hclust(xdist)
        
        ###amap version
	hc=hcluster(xmat,method=dmethod)
	
        if(Ig==1) csize=length(Ii)
	if(Ig==0) csize=length(Jj)
	maxSize=pSize*(csize)+(pSize/100)*(csize)^2
        ###do not move more than half the block
	if(maxSize>(.5*csize)) maxSize=.5*(csize)
	if(Ia==1 & respectLim==TRUE){
		if((maxSize+csize)>Ulim) maxSize=Ulim-csize
	}
	cfinal=cutree(hc,h=FindCut(hc,maxSize))
	split(IndB,cfinal)
}


###Function to cut a clustering tree given a parameter for the maximum size of a cluster after cutting
FindCut=function(hcout,maxSize){
      stopp=0
      first1=0
      min1=min(hcout$height)
      max1=max(hcout$height)
      if(is.infinite(min1)) min1=0
     count=0
      while(stopp==0){
              if(first1==0){
                  h1=max1 ###set to max to bias towards larger clusters #(max1+min1)/2#
                  first1=1
              }
              tab1=table(cutree(hcout,h=h1))
              
              #cat("tab", max1, min1, all(tab1<maxSize),any(tab1>maxSize),(any(tab1==maxSize) & all(tab1<=maxSize)),"\n")
              
              if(all(tab1<maxSize)){ ##if all groups are too small, increase it towards max and reset min
              #cat("1","\n")
                  h2=(max1+h1)/2
                  h1old=h1
                  min1=h1
              }
              else if(any(tab1>maxSize)){  ##if any are too large, decrease towards min and reset max
                    #cat("2","\n")
                  h2=(h1+min1)/2
                  h1old=h1
                  max1=h1
              }
              else if(any(tab1==maxSize) & all(tab1<=maxSize)){ ##if any at max, and all others below, stop
                 #cat("3","\n")
                  h2=h1
                  h1old=h1
                  stopp=1
              }
              
                  #cat("aftab",max1, min1, (abs(h1-h2)<abs(min(diff(hcout$height)))),(sum(tab1)==(length(tab1)+1)),"\n")
                  
              #if((abs(h1-h2)/h1)<0.01 & all(tab1<=maxSize))
                #if(abs(h1-h2)<abs(min(diff(hcout$height))) & all(tab1<=maxSize)){ ###if distance between max and current position less than smallest distance, stop
                 if((abs(h1-h2)<=abs(min(diff(hcout$height),na.rm=TRUE)) & all(tab1<=maxSize)) | h1==h2){
                    h1old=h1
                    stopp=1
                }
                
                if(sum(tab1)==(length(tab1)+1)) { ###if number of clusters is number of nodes
                    h1old=0
                    stopp=1
                }
        
                
                #cat("bf ",max1, min1, count, h1, h1old, h2, (h2-h1), stopp,"\n")
                bfh1 = h1
                h1=h2
                #cat("af ", max1, min1, count, h1, h1old, h2, (h2-h1), stopp,"\n")
                                
                count=count+1
                #print(table(cutree(hcout,h=h1old)))
        }
      h1old
}

##
###Code for null distribution sampling expansion and smoothing
##
tpsSmooth=function(prefix, min1, max1, min2, max2,usePseudo=F,useLog=T){
	#assumes raw coordinates are the first row and column of the tab-delim txt file
	#requires a vector of character file names which have "_raw" in their filename
	#returns a new file with the same name but with "raw" replaced with "full"
	#returned matrix has coordinates as specified by fullCors
        
        fullCors=expand.grid(x1=min1:max1,x2=min2:max2)
        
    filesN <- list.files("./",pattern="*raw.txt")    
    filesN=filesN[as.vector(sapply(filesN,function(x) length(grep(paste(prefix,"PPI",sep=""),x)))) == 0]
    filesN=filesN[as.vector(sapply(filesN,function(x) length(grep(paste(prefix,"MAXTF",sep=""),x)))) == 0]
    filesN=filesN[as.vector(sapply(filesN,function(x) length(grep(paste(prefix,"feat",sep=""),x)))) == 0]
    print(filesN)
    neg <- 0
       require(fields)
       if(length(filesN) > 0) {
       for(fi in 1:length(filesN)){
                print(paste("tpsSmooth",fi,filesN[fi], sep=" "))
               tr1=read.table(filesN[fi],sep="\t")
               tr1scale=100*tr1
               #print(dim(tr1))
               yc=as.matrix(tr1[-1,1])  #as.numeric(rownames(tr1))
               xc=as.matrix(tr1[1,-1])  #as.numeric(colnames(tr1))
               txtfile=as.matrix(tr1[-1,-1])
		
	       findmin1 <- min(txtfile)
	       txtfileplus <- txtfile
	       #shift values to positive for log
	       if(findmin1 <0) {
		 neg <- 1
		 txtfileplus <- txtfile + abs(findmin1)		 
		 #print("shift to positive")
		 #print(abs(findmin1))
	       }
	       
	       haszero <- 0
	       if(findmin1 >= 0) {
	        haszero <-  length(which(txtfile == 0))
	       }
	       else if(findmin1 < 0) {
	        haszero <-  length(which(txtfileplus == 0))
	       }

#print(paste("haszero",haszero))
#print(head(txtfile))

	       #if zeros then add arbitrary pseudocount
	       if(usePseudo && haszero > 0) {
		 txtfileinf <- txtfileplus
		 txtfileinf[txtfileinf == 0] <- Inf
		 findminnonzero <- min(txtfileinf)	       
		 #print(findminnonzero)
		 pseudo <- findminnonzero/1000
		 print(paste("pseudocount", pseudo))
		 ###adds pseudocount
		 txtfileplus <- txtfileplus + pseudo
	       }
	        else {
		     print("no pseudocount")
		    }
		    
	       if(useLog) {
		    txtfileplus <- log(txtfileplus)
	       }
               cors1=expand.grid(yc,xc)
	        #print("1")
		#print(txtfileplus)
               tps1=Tps(cors1,as.vector(txtfileplus),scale.type="unscaled")
	      
	      
	    #also is at the limit of the bandwidth range
               corsA=fullCors   #expand.grid(min:max,min:max)
               tps1p=matrix(predict(tps1,x=corsA),ncol=length(unique(fullCors[,2])))
	     if(useLog) {
	  	       tps1p <- exp(tps1p)
	       }
               #tps1p=tpsSmooth(ifiles1[1],rawCors=NULL,fullCors=expand.grid(x1=min:max,x2=min:max))
               Ffile=sub("raw","full",filesN[fi])
           if(min(tps1p) < 0) {
                    yc=as.matrix(tr1scale[-1,1])  #as.numeric(rownames(tr1))
                    xc=as.matrix(tr1scale[1,-1])  #as.numeric(colnames(tr1))
                    txtfile=as.matrix(tr1scale[-1,-1])
		    
		    findmin1 <- min(txtfile)
		    txtfileplus <- txtfile
		    #shift values to positive for log
		    if(findmin1 <0) {
		      neg <- 1
		      txtfileplus <- txtfile + abs(findmin1)
		    }
		    
		     haszero <- 0
	       if(findmin1 >= 0) {
	        haszero <-  length(which(txtfile == 0))
	       }
	       else if(findmin1 < 0) {
	        haszero <-  length(which(txtfileplus == 0))
	       }
	       
#print(paste("haszero",haszero))

		    if(usePseudo && haszero > 0) {
			 txtfileinf <- txtfileplus
			 txtfileinf[txtfileinf == 0] <- Inf
			 findminnonzero <- min(txtfileinf)
			 #print(findminnonzero)
			 pseudo <- findminnonzero/1000
			 print(paste("pseudocount", pseudo))
			 ###adds pseudocount
			 txtfileplus <- txtfileplus + pseudo
		    }
		    else {
		     print("no pseudocount")
		    }

		    if(useLog) {
	  		    txtfileplus <- log(txtfileplus)
		    }
                    cors1=expand.grid(yc,xc)
                    tps1=Tps(cors1,as.vector(txtfileplus),scale.type="unscaled")
                    #also is at the limit of the bandwidth range
                    corsA=fullCors   #expand.grid(min:max,min:max)
                    tps1p=matrix(predict(tps1,x=corsA),ncol=length(unique(fullCors[,2])))
		    if(useLog) {
			 tps1p <- exp(tps1p)
		    }
                    tps1p <- tps1p/100
                }
	       #deshift smoothed values back to negative
	       if(neg == 1) {
		    tps1p <- tps1p - abs(findmin1)
		    #print("8")      
	       }
               #write(t(tps1p),file=Ffile,ncolumns=dim(tps1p)[2],sep="\t")
               write(t(tps1p),ncolumns=dim(tps1p)[2],file=Ffile,sep="\t")
               print(cat("wrote ",Ffile))
       }
    }
}

tpsSmooth1D=function(crit, min, max){
ifiles <- list.files("./",pattern="*raw.txt")
iIfiles1=ifiles[as.vector(sapply(ifiles,function(x) grep(crit,x)*grep("raw",x)))==1]
iIfiles1=iIfiles1[!is.na(iIfiles1)]
len <- length(iIfiles1)
if(len > 0) {
for(fi in 1:len){
print(paste("tpsSmooth1D",iIfiles1[fi],sep=" "))
    tr1=matrix(scan(iIfiles1[fi]),nrow=2,byrow=TRUE)
    tr1scale=tr1*100
    #print(dim(tr1))
    #print(tr1)
    xc=tr1[1,]
    corsA=min:max
    txtfile=tr1[-1,]
    txtfile=log(txtfile)
     tps1=log(txtfile)
    tps1=sreg(xc,as.vector(txtfile))   
    tps1p=matrix(predict(tps1,x=corsA),ncol=1)
     tps1p=exp(tps1p)
    if(min(tps1p) < 0) {
        xc=tr1scale[1,]
        corsA=min:max
        txtfile=tr1scale[-1,]
	tps1=log(txtfile)
        tps1=sreg(xc,as.vector(txtfile))
        tps1p=matrix(predict(tps1,x=corsA),ncol=1)
        tps1p=exp(tps1p)
        tps1p=tps1p/100
    }
    Ffile=sub("raw","full",iIfiles1[fi])
    write(t(tps1p),file=Ffile,ncolumns=1,sep="\t")
    }
}
}

##
###Code for initial blocks
###fxn gives the highest (largest) possible 2-step HCL initial blocks that meet criteria
###this removes almost duplicates from pervious method
##
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



###function to determine all possible initial blocks from a 2-step hcluster
allpossibleInitial=function(Datamat,Imin,Imax,Jmin,Jmax,distance,useAbs,isCol){ 
	InitclustI=InitclustJ=NULL
        
        if(isCol == 1) {
	####starting with exp scale
		#start by clustering exps by gene vector

            if(useAbs ==1) {
                Datamat <- abs(Datamat)
            }
                hc1=hcluster(t(Datamat),method=distance)
                save(hc1,file="exp_hcluster")
		out1=Dmerge(hc1$merge)
		xJ=which(out1$sizeG<=Jmax & out1$sizeG>=Jmin)
		rmxJ=match(unique(out1$DisInd[xJ]),unique(out1$OrigInd[xJ]))
		{if(length(rmxJ[!is.na(rmxJ)])==0) uni_xJ=unique(out1$OrigInd[xJ])
			else uni_xJ=unique(out1$OrigInd[xJ])[-rmxJ[!is.na(rmxJ)]]}
		xJ1=xJ[(length(out1$OrigInd[xJ])-match(uni_xJ,rev((out1$OrigInd[xJ]))))+1] #index where unique and highest level lies
		#cluster each of xI clusters on exp scale
		for(jl in 1:length(xJ1)){
			Jc_jl=-out1$ListI[[xJ1[jl]]]
                        
                        Datamat_jl=Datamat[,Jc_jl]
                        hc_jl=hcluster((Datamat_jl),method=distance)
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
                }
                else {
	####starting with gene scale
		#start by clustering genes by exp vector
		
		if(useAbs ==1) {
                Datamat <- abs(Datamat)
            }	    
                hc1=hcluster(Datamat,method=distance)
                save(hc1,file="gene_hcluster")
		out1=Dmerge(hc1$merge)
		xI=which(out1$sizeG<=Imax & out1$sizeG>=Imin)
		rmxI=match(unique(out1$DisInd[xI]),unique(out1$OrigInd[xI]))
		{if(length(rmxI[!is.na(rmxI)])==0) uni_xI=unique(out1$OrigInd[xI])
			else uni_xI=unique(out1$OrigInd[xI])[-rmxI[!is.na(rmxI)]]}
		xI1=xI[(length(out1$OrigInd[xI])-match(uni_xI,rev((out1$OrigInd[xI]))))+1] #index where unique and highest level lies
		#cluster each of xI clusters on exp scale
		for(il in 1:length(xI1)){
			Ic_il=-out1$ListI[[xI1[il]]]
			Datamat_il=Datamat[Ic_il,]
                        hc_il=hcluster(t(Datamat_il),method=distance)
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
                }
		print(length(InitclustI))
		print(length(InitclustJ))
		unique(c(InitclustI,InitclustJ))
}


###correlation distance function for a matrix, allowing abs
CorDist=function(data, useAbs=1, norm=1, sym=F) {
   dim <- dim(data)
   #print(dim)
   d <- mat.or.vec(dim[1],dim[1])
   
    for(j in 1:dim[1]) {
        for(i in 1:dim[1]) {
            if((sym && j > i) || (!sym && i != j)) {
                #cat(i,j,"\n")
                #cat(length(data[!is.na(data[i,]),]),length(data[!is.na(data[j,]),]),"\n")
                
                #cat(length(data[,i]), length(data[,j]))

                d[i,j] <- cor(data[i,],data[j,], use="complete.obs")
                if(useAbs==1) {
                    d[i,j] <- 1-abs(d[i,j])
                } else if(norm == 1){
                    d[i,j] <- 1-(d[i,j]+1)/2                    
                }
                if(sym) {
                    d[j,i] <- d[i,j]
                }
            }
            else if(i ==j) {
                d[j,i] <- d[i,j] <- 1
            }
    }
   }
   d
}