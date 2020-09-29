source("/global/home/users/marcin/common/Miner_FEM.R")
load("/global/home/users/marcin/common/yeast_cmonkey")
library(lars)
library(irr)
library(geepack)
library(amap)
set.seed(1)

I=dim(expr_data)[1]
J=dim(expr_data)[2]


#NA issue

Ic<-c(83,108,171,223,349,379,495,605,979,1176,1437,1462,1496,1500,1522,1621,1805,1853,1880,1990,2125,2203,2406,2672,2833,2855,2857,2865,2867,2943,3197,3505,3509,3577,3623,4007,4169,4305,4364,4369,4389,4411,4416,4580,4629,4687,4868,5110,5135,5180,5208,5212,5256,5258,5296,5520,5544,5654,5952)
Jc<-c(111,332,334,338,382,400,404)
nullMSEData<-c(0.5089233,0.0110735)
nullRegData<-c(0.001818649,0.001065473)
nullKendData<-c(0.04284655,0.04612461)
Critp.final(Ic,Jc,expr_data,interact_data,nullMeanData,nullMSEData,nullRegData,nullKendData,nullCorData,nullEucData,nullPPIData,nullFeatData,feature_data,useNull=TRUE,ECindex=1,ERegindex=2,Kendindex=3,Corindex=-1,Eucindex=-1,UseExprMean=FALSE,MeanARCind=-1,ARCind=3,RegARCind=3,InterI=FALSE,FeatI=FALSE,Invert=1,meanOnly=0,useAbs=1)

#all NA in row
Ic<-c(8,25,26,51,54,59,80,103,144,167,168,170,182,194,206,251,264,271,273,298,305,312,365,407,421,422,452,460,475,501,509,819,906,978,981,1077,1107,1155,1195,1201,1205,1343,1349,1371,1389,1530,1538,1679,1729,1801,1851,1877,1891,2163,2179,2237,2281,2283,2710,2732,2864,2957,3241,3299,3582,3671,3896,4058,4279,4414,4631,4653,4974,5038,5039,5050,5190,5688)
Jc<-c(1,2,3,4,5,6,33,34,69,70,161,162,163,165,167,203,430,436,437,440,448)

rowSums(is.na(expr_data[Ic,Jc]))


###KendallC fail on yeast
#1
Ic<-c(25, 26, 35, 812, 229, 363, 388, 2259, 3841, 553, 636, 642, 653, 772, 840, 841, 850, 1026, 1057, 1062, 1134, 1161, 1209, 1238, 1274, 3661, 1315, 1337, 1347, 1362, 1380, 1404, 1482, 1511, 1638, 1800, 1819, 1847, 1910, 2061, 2064, 2073, 2080, 2111, 2210, 2228, 2245, 2314, 2391, 2400, 2403, 2446, 2541, 2584, 2617, 2696, 5597, 2775, 2879, 3147, 3222, 3264, 3292, 3341, 3513, 3540, 3574, 3652, 3660, 3668, 3711, 3724, 3772, 3870, 4240, 4361, 4389, 4616, 4722, 4805, 4908, 4986, 5057, 5149, 5158, 5368, 5380, 5494, 5549, 5632, 5674, 5683, 5713, 5731, 5805, 5838, 5873, 5879, 6126, 6127)
Jc<-c(27, 192, 196, 219, 286, 304, 399, 444, 447, 538, 610)
Critp.final(Ic,Jc,expr_data,interact_data,nullMeanData,nullMSEData,nullRegData,nullKendData,nullCorData,nullEucData,nullPPIData,nullFeatData,feature_data,useNull=FALSE,ECindex=-1,ERegindex=NULL ,Kendindex=3,Corindex=-1,Eucindex=-1,UseExprMean=FALSE,MeanARCind=-1,ARCind=-1,RegARCind=-1,InterI=FALSE,FeatI=FALSE,Invert=0,meanOnly=0,useAbs=1)

#2
Ic<-c(39, 88, 315, 323, 337, 404, 457, 479, 485, 553, 621, 768, 704, 961, 964, 974, 1165, 1185, 1205, 1402, 1425, 1440, 1624, 1674, 1708, 1836, 1900, 1913, 2074, 2085, 2094, 2121, 2221, 2308, 2330, 2506, 2516, 2531, 2706, 2741, 2833, 2867, 2870, 2910, 2992, 3090, 3369, 3602, 3663, 3747, 3814, 3840, 3841, 3937, 3945, 4041, 4055, 4121, 4440, 4449, 4471, 4474, 4625, 4663, 4686, 4939, 5022, 5080, 5120, 5176, 5206, 5242, 5320, 5442, 5493, 5511, 5525, 5568, 5860, 5888, 5916, 6038, 6092)
Jc<-c(129, 159, 162, 372, 386, 498, 568, 617, 654, 656, 659)
Critp.final(Ic,Jc,expr_data,interact_data,nullMeanData,nullMSEData,nullRegData,nullKendData,nullCorData,nullEucData,nullPPIData,nullFeatData,feature_data,useNull=FALSE,ECindex=-1,ERegindex=NULL ,Kendindex=3,Corindex=-1,Eucindex=-1,UseExprMean=FALSE,MeanARCind=-1,ARCind=-1,RegARCind=-1,InterI=FALSE,FeatI=FALSE,Invert=0,meanOnly=0,useAbs=1)


###old GEECE fail
Ic<-c(812,4000,4413,4638,5890)
Jc<-c(191,38,52,154,236,326,394,405,425,455,473,550,556,560,587,615)
nullRegData <- NULL
Critp.final(Ic,Jc,expr_data,interact_data,nullMeanData,nullMSEData,nullRegData,nullKendData,nullCorData,nullEucData,nullPPIData,nullFeatData,feature_data,useNull=FALSE,ECindex=-1,ERegindex=2,Kendindex=-1,Corindex=-1,Eucindex=-1,UseExprMean=FALSE,MeanARCind=-1,ARCind=-1,RegARCind=3,InterI=FALSE,FeatI=FALSE,Invert=0,meanOnly=0,useAbs=1)

###old GEECE fail
Ic<-c(2088,3914,4429,5626)
Jc<-c(1,17,28,174)
nullRegData <- NULL
Critp.final(Ic,Jc,expr_data,interact_data,nullMeanData,nullMSEData,nullRegData,nullKendData,nullCorData,nullEucData,nullPPIData,nullFeatData,feature_data,useNull=FALSE,ECindex=-1,ERegindex=2,Kendindex=-1,Corindex=-1,Eucindex=-1,UseExprMean=FALSE,MeanARCind=-1,ARCind=-1,RegARCind=3,InterI=FALSE,FeatI=FALSE,Invert=0,meanOnly=0,useAbs=1)

###LARSCE fail real data
source("/global/home/users/marcin/common/Miner.R")
load("/global/home/users/marcin/common/yeast_cmonkey")
library(lars)
library(irr)
library(geepack)
library(amap)
set.seed(1)

I=dim(expr_data)[1]
J=dim(expr_data)[2]
Ic<-c(103,587,801,822,855,1255,2247,2462,2767,3367,3577,3973,5984,4722,5247,5517)
Jc<-c(20,26,402,34,44,47,72,77,116,138,151,152,161,165,183,186,191,217,229,232,248,254,258,263,284,285,320,325,350,377,380,381,504,385,552,421,419,428,487,492,531,548,579,613,645,326,666)
Critp.final(Ic,Jc,expr_data,interact_data,nullMeanData,nullMSEData,nullRegData,nullKendData,nullCorData,nullEucData,nullPPIData,nullFeatData,feature_data,useNull=FALSE,ECindex=-1,ERegindex=1,Kendindex=-1,Corindex=-1,Eucindex=-1,UseExprMean=FALSE,MeanARCind=-1,ARCind=-1,RegARCind=3,InterI=FALSE,FeatI=FALSE,Invert=0,meanOnly=0,useAbs=1)


###LARSCE fail fake data
source("/global/home/users/marcin/common/Miner.R")
load("/global/home/users/marcin/common/fake110427_4r_incr_0.25ppi_rand")
library(lars)
library(irr)
library(geepack)
library(amap)
set.seed(1)
I=dim(expr_data)[1]
J=dim(expr_data)[2]

nullRegData <- NULL

Ic<-c(1, 9, 12, 33, 52, 90, 94, 96, 98, 113, 137, 147, 148, 159, 185, 191)
Jc<-c(1, 4, 6, 10, 11, 13, 14, 17, 18, 19, 20, 22, 24, 25, 26, 27, 28, 29, 31, 32, 33, 35, 38, 39, 40, 42, 43, 44, 45, 46, 49, 52, 55, 56, 57, 64, 66, 69, 71, 73, 77, 79, 80)

Critp.final(Ic,Jc,expr_data,interact_data,nullMeanData,nullMSEData,nullRegData,nullKendData,nullCorData,nullEucData,nullPPIData,nullFeatData,feature_data,useNull=FALSE,ECindex=-1,ERegindex=1,Kendindex=-1,Corindex=-1,Eucindex=-1,UseExprMean=FALSE,MeanARCind=-1,ARCind=-1,RegARCind=3,InterI=FALSE,FeatI=FALSE,Invert=0,meanOnly=0,useAbs=1)

###GEERE
Ic<-c(5,195,86,186,108,60,19,75,175,177,42,81,70,139,4,2,120,157,183,15)
Jc<-c(27,1,37,7,8,26,71,5,45,30)

Critp.final(Ic,Jc,expr_data,interact_data,nullMeanData,nullMSEData,nullRegData,nullKendData,nullCorData,nullEucData,nullPPIData,nullFeatData,feature_data,useNull=FALSE,ECindex=-1,ERegindex=2,Kendindex=-1,Corindex=-1,Eucindex=-1,UseExprMean=FALSE,MeanARCind=-1,ARCind=-1,RegARCind=2,InterI=FALSE,FeatI=FALSE,Invert=0,meanOnly=0,useAbs=1)
#new 0.4201529, 0.006567866
#old 0.4129806

EgeeCrit.block(expr_data,Ic,Jc,ARC=2,I,J,rowMiss,colMiss,useAbs=1,pscore=NULL, RepM=0)
[1] 0.4201529

EgeeCrit.block(expr_data,Ic,Jc,ARC=2,I,J,rowMiss,colMiss,useAbs=1,pscore=NULL, RepM=1)
[1] 0.4129806

FEModel.block(expr_data,Ic,Jc,ARC=2,I,J,rowMiss,colMiss,useAbs=1,pscore=NULL)       
[1] 0.4217103


EgeeCrit.block(expr_data,Ic,Jc,ARC=3,I,J,rowMiss,colMiss,useAbs=1,pscore=NULL, RepM=0)
[1] 0.01746402

EgeeCrit.block(expr_data,Ic,Jc,ARC=3,I,J,rowMiss,colMiss,useAbs=1,pscore=NULL, RepM=1)
[1] 3.245168e-05

FEModel.block(expr_data,Ic,Jc,ARC=3,I,J,rowMiss,colMiss,useAbs=1,pscore=NULL)       
[1] 0.03739983

###fake PPI

Ic<-c(48,114,186,189)
Jc<-c(15,25,35,47)
Critp.final(Ic,Jc,expr_data,interact_data,nullMeanData,nullMSEData,nullRegData,nullKendData,nullCorData,nullEucData,nullPPIData,nullFeatData,feature_data,useNull=FALSE,ECindex=-1,ERegindex=NULL ,Kendindex=-1,Corindex=-1,Eucindex=-1,UseExprMean=FALSE,MeanARCind=-1,ARCind=-1,RegARCind=-1,InterI=TRUE,FeatI=FALSE,Invert=0,meanOnly=0,useAbs=1)

Ic<-c(14,27,67,71)
Jc<-c(4,7,17,20,31,48,51,67,69)
Critp.final(Ic,Jc,expr_data,interact_data,nullMeanData,nullMSEData,nullRegData,nullKendData,nullCorData,nullEucData,nullPPIData,nullFeatData,feature_data,useNull=FALSE,ECindex=-1,ERegindex=NULL ,Kendindex=-1,Corindex=-1,Eucindex=-1,UseExprMean=FALSE,MeanARCind=-1,ARCind=-1,RegARCind=-1,InterI=TRUE,FeatI=FALSE,Invert=0,meanOnly=0,useAbs=1)

               
###GEECE

               curdata=expr_data[Ic,]
               
               nacols <- (colSums(is.na(curdata))> 2)
               rem_index <- which(nacols)
               curdata <- curdata[,-rem_index]
               J <- J-length(rem_index)
               for(k in 1:length(Jc)) {
                              if(sum(Jc[k] > rem_index) > 0) {
                                   Jc[k] <- Jc[k] - sum(Jc[k] > rem_index)                                       
                              }
               }
               
               curdata=t(apply(abs(curdata),2,missfxn))
               if(is.null(pscore)==0) curdata=preScore_block(curdata,ARC)
               Jind=rep(0,J)
               Jind[Jc]=1
                geI=matrix(t(matrix(rep(Jind,length(Ic)),ncol=length(Ic))),ncol=1)
                idD=NULL
               for(iy in 1:J) idD=c(idD,rep(iy,length(Ic)))
                idA=rep(1:length(Ic),J)

       reps=dim(curdata)[2]
       yY=matrix(t(curdata),ncol=1)
        gee1=lm(yY~geI:factor(idA))
        
        SSE=sum((yY-fitted(gee1))^2)
        
                
###LARSRE

Ii <- Ic
Jj <- Jc
               curdata=expr_data[,Jj]
               
               narows <- (rowSums(is.na(curdata))> 2/length(Jj))
               if(sum(narows)>0) {
                              rem_index <- which(narows)
                              curdata <- curdata[-rem_index,]
                              I <- I-length(rem_index)
                              for(k in 1:length(Ii)) {
                                sum <- sum(Ii[k] > rem_index)
                                             if(sum > 0) {
                                               cat("Ii[k] ",Ii[k]," - ",sum," = ",(Ii[k] - sum),"\n")
                                                            Ii[k] <- Ii[k] - sum
                                             }
                              }
               }

		if(is.null(pscore)==0) curdata=preScore_block(curdata,ARC)
		curdata=apply(curdata,2,missfxn)
		Iind=rep(0,I)
   		Iind[Ii]=1

   		Q=lars(curdata,as.matrix(Iind))
		Qcv=cv.lars(curdata,Iind,K = 5,plot.it=FALSE)
		min(Qcv$cv)
		s1=Qcv$frac[which.min(Qcv$cv)]
		fts=predict(Q,newx=curdata,s=s1,type="fit",mode="fraction")
   		SSE=sum((Iind-fts$fit)^2)
   		SST=sum((Iind-mean(Iind))^2)
   		1-SSE/SST
                
                
                                
###LARSCE

I=dim(expr_data)[1]
J=dim(expr_data)[2]
Ii <- Ic
Jj <- Jc
               curdata=expr_data[Ii,]

               nacols <- (colSums(is.na(curdata)))
            remnacols <- (colSums(is.na(curdata)) > length(Ii)-2)
            print(sum(nacols))
print(sum(remnacols))
            if(sum(remnacols) > 0) {
                    rem_index <- which(remnacols)
                    curdata <- curdata[,-rem_index]
                    J <- J-length(rem_index)
                    for(k in 1:length(Jj)) {
                              if(sum(Jj[k] == rem_index) > 0) {
                                             print("sum(Jj[k] == rem_index) ")
                                             print(sum(Jj[k] == rem_index))
                              }
                             sum <- sum(Jj[k] > rem_index)
                                   if(sum > 0) {
#print(cat("Jj[k] ",Jj[k]," - ",sum," = ",(Jj[k] - sum),"\n"))
                                        Jj[k] <- Jj[k] - sum
                                   }
                    }               
            }
            if(sum(nacols) > 0) {
            curdata=apply(curdata,1,missfxn)        
            }
            else {
               curdata <- t(curdata)
            }

                Iind=rep(0,J)
                Iind[Jj]=1

   		Q=lars(curdata,as.matrix(Iind))
                
		Qcv=cv.lars(curdata,Iind,K = 5,plot.it=FALSE)
		min(Qcv$cv)
		s1=Qcv$frac[which.min(Qcv$cv)]
		fts=predict(Q,newx=curdata,s=s1,type="fit",mode="fraction")
   		SSE=sum((Iind-fts$fit)^2)
   		SST=sum((Iind-mean(Iind))^2)
   		1-SSE/SST
                
                
###KendallC
I=dim(expr_data)[1]
J=dim(expr_data)[2]
Ii <- Ic
Jj <- Jc
ArcI <- 3
               curdataC <- c()  
               curdata <- t(expr_data[Ii, Jj])
                nacols <- colSums(is.na(curdata))
               ktest=kendall(curdata) #should omit data on its own
              
               c(ktest$value,ktest$p.val)#,ktest$statistic)
	
