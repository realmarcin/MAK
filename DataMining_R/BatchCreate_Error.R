
setwd("~/Documents/integr8_genom/Miner/rda/common/yeast_data/")
load("yeast_cmonkey")
source("Miner.R")
library(amap)

Iibatch <- c(9,54,86,146,215,247,296,457,469,496,535,541,589,695,758,793,837,841,957,975,990,1015,1020,1023,1024,1025,1063,1100,1128,1170,1173,1219,1271,1292,1295,1299,1321,1328,1364,1372,1412,1478,1506,1507,1542,1621,1656,1667,1696,1697,1699,1705,1795,1839,1863,1883,1888,1915,1991,1996,2085,2108,2118,2156,2162,2180,2184,2228,2358,2466,2616,2688,2749,3195,3208,3565,3613,3698,3758,3843,4132,4139,4145,4150,4329,4352,4385,4431,4676,4753,4795,5025,5072,5398,5409,5446,5956,6020,6064)
Jjbatch <- c(151,152,153,188,192,193,206,249,308,334,385,392,393,397,407,463,466,481,485,500,503,504,505,506,507,508,512,513,518,519,520,521,522,523,524,525,526,527,528,536,558,559,565)

pSize <- 0.4
Ig <- 1
Ia <- 0
useAbs=1
respectLim=TRUE

#currentbatch <- BatchCreate(expr_data,null,Iibatch,Jjbatch,Ig,Ia,pSize, Ulim=200)
###BatchCreate reconstituted for testing
xmat=abs(t(expr_data[Iibatch,Jjbatch]))
xmat=t(apply(xmat,2,function(xm1){ xm1[is.na(xm1)]=mean(xm1,na.rm=TRUE); xm1}))
IndB=Iibatch   

dmethod="correlation"
hc=hcluster(xmat,method=dmethod)

csize=length(Iibatch)

maxSize=pSize*(csize)+(pSize/100)*(csize)^2
        ###do not move more than half the block
	if(maxSize>(.5*csize)) maxSize=.5*(csize)
	if(Ia==1 & respectLim==TRUE){
		if((maxSize+csize)>Ulim) maxSize=Ulim-csize
	}
	
	maxSize
[1] 49.5

cfinal=cutree(hc,h=FindCut(hc,maxSize))
Error in if (abs(h1 - h2) < abs(min(diff(hcout$height))) & all(tab1 <=  : 
  missing value where TRUE/FALSE needed

	
	cfinal=cutree(hc,h=FindCut(hc,maxSize))
	split(IndB,cfinal)
	
	
	hc$height
	FindCut(hc,maxSize)