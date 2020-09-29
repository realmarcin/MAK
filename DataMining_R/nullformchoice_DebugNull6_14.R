#
#  testSpatialNull
#
#  Created by Cathy Tuglus on 2010-03-10.
#  Copyright (c) 2010 __MyCompanyName__. All rights reserved.
#


#############################
####Plot code to compare the null methods, interpolation and otherwise
load(VGAM) #provides laplace

setwd("~/Documents/integr8_genom/Miner/rda/common/null_optimization/faker")

##Comparison for fNulls_4rincr1 
my_MSErm=as.matrix(read.table("synth_c_incr_nono_rand_110427_GEERE_median_raw.txt",sep="\t"))
my_MSErsd=as.matrix(read.table("synth_c_incr_nono_rand_110427_GEERE_0.5IQR_raw.txt",sep="\t"))

load("~/Documents/integr8_genom/Miner/rda/common/yeast_data/fake_0.1noise_1var/fake110427_4r_incr_0.25ppi_rand")

dim <- dim(expr_data)

indI=seq(from=5,to=125,by=5)
indJ=seq(from=5,to=65,by=5)

m_MSE=m_MAD=m_MSEr=m_MADr=m_MSEc=m_MADc=pm_MSEr=alpha_MSEr=lm_MSEr=lsd_MSEr=cm_MSEr=csd_MSEr=
sd_MSE=sd_MAD=sd_MSEr=sd_MADr=sd_MSEc=sd_MADc=lasd_MSEr=lam_MSEr=array(NA,dim=c(dim[1],dim[2],1))
cm_Inter=csd_Inter=m_Inter=sd_Inter=array(NA,dim=c(dim[1],1,1))

for (i in 1:dim[1]){ #Imin:Imax){  #8
    for(j in 1:dim[2]){ #Jmin:Jmax){

		##for Pareto
		pm_MSEr[i,j,]=min(ExpCMSEr.null[i,j,])
		alpha_MSEr[i,j,]=length(ExpCMSEr.null[i,j,])/sum(log(ExpCMSEr.null[i,j,])-log(min(ExpCMSEr.null[i,j,])))
		
		##for Log Normal
		lm_MSEr[i,j,]=mean(log(ExpCMSEr.null[i,j,]))
		lsd_MSEr[i,j,]=sd(log(ExpCMSEr.null[i,j,]))
		
		##for Cauchy
		cm_MSEr[i,j,]=median((ExpCMSEr.null[i,j,]))
		csd_MSEr[i,j,]=.5*(IQR(ExpCMSEr.null[i,j,]))
		
		##for logisitic
		m_MSEr[i,j,]=mean(ExpCMSEr.null[i,j,])
		sd_MSEr[i,j,]=sd(ExpCMSEr.null[i,j,])
		
		##for laplace
		lam_MSEr[i,j,]=mean(ExpCMSEr.null[i,j,])
		lasd_MSEr[i,j,]=sqrt(var(ExpCMSEr.null[i,j,])/2) #scale para b s.t. var(x)=2b^2

	}
	
	##for Cauchy
	cm_Inter[i,,]=median((Inter.nullm[i,1,]))
	csd_Inter[i,,]=.5*(IQR(Inter.nullm[i,1,]))
	
	##for Cauchy
	m_Inter[i,,]=mean((Inter.nullm[i,1,]))
	sd_Inter[i,,]=(sd(Inter.nullm[i,1,]))

}

i=5
j=7
hist(ExpCMSEr.null[i,j,],xlim=c(0,1),freq=FALSE)
x=seq(0,1,by=.01)
lines(x,dnorm(x,m_MSEr[i,j,],sd_MSEr[i,j,]))
lines(x,dcauchy(x,cm_MSEr[i,j,],csd_MSEr[i,j,]),col=2)
lines(x,dcauchy(x,m_MSEr[i,j,],sd_MSEr[i,j,]),col=2,lty=2)
lines(x,dlogis(x,m_MSEr[i,j,],sd_MSEr[i,j,]),col=3)
#lines(x,dlogis(x,m_MSEr[i,j,],sd_MSEr[i,j,]*sqrt(3)/pi),col=3,lty=2)
lines(x,dlnorm(x,lm_MSEr[i,j,],lsd_MSEr[i,j,]),col=4)
lines(x,dpareto(x,pm_MSEr[i,j,],alpha_MSEr[i,j,]),col=5)
lines(x,dlaplace(x,lam_MSEr[i,j,],lasd_MSEr[i,j,]),col=5)
legend("topright",c("norm","cauchy","logis","lognorm","pareto","laplace"),col=1:5,lty=rep(1,5))


i=19
hist(Inter.nullm[i,1,],xlim=c(0,.1),freq=FALSE)
x=seq(0,.1,by=.0001)
lines(x,dnorm(x,m_Inter[i,1,],sqrt((m_Inter[i,1,]*(1-m_Inter[i,1,]))/(i^2))))
lines(x,dcauchy(x,m_Inter[i,1,],sqrt((m_Inter[i,1,]*(1-m_Inter[i,1,]))/(i^2))),col=2)
lines(x,dcauchy(x,cm_Inter[i,1,],csd_Inter[i,1,]),col=2,lty=2)













df_raw <- expand.grid(x=1:25, y=1:13)
df_interp <- expand.grid(x=1:121, y=1:61)
df_raw$z=as.vector(m_MSEr[,,1])
df_interp$z=as.vector(my_MSErm)
df_raw$s=as.vector(sd_MSEr[,,1])
df_interp$s=as.vector(my_MSErsd)
by1_raw=unique(quantile(df_raw$z,prob=seq(0,1,by=.1)))
by1_rawS=unique(quantile(df_raw$s,prob=seq(0,1,by=.1)))
by1_interp=unique(quantile(df_raw$z,prob=seq(0,1,by=.1)))

p_myraw=ggplot(df_raw, aes(x=x,y=y, fill = z))+geom_tile() +
#scale_fill_gradientn(name="lactate",colours = topo.colors(length(by1_raw)-2),breaks=by1_raw,labels=signif(by1_raw,2))
scale_fill_gradient(name="C_raw",low="yellow",high="purple",breaks=by1_raw,labels=signif(by1_raw,2))

p_myinterp=ggplot(df_interp, aes(x=x,y=y, fill = z))+geom_tile() +
#scale_fill_gradientn(name="lactate",colours = topo.colors(length(by1_raw)-2),breaks=by1_raw,labels=signif(by1_raw,2))
scale_fill_gradient(name="C_full",low="yellow",high="purple",breaks=by1_raw,labels=signif(by1_raw,2))

p_myrawSD=ggplot(df_raw, aes(x=x,y=y, fill = s))+geom_tile() +
#scale_fill_gradientn(name="lactate",colours = topo.colors(length(by1_raw)-2),breaks=by1_raw,labels=signif(by1_raw,2))
scale_fill_gradient(name="C_raw",low="yellow",high="purple",breaks=by1_rawS,labels=signif(by1_rawS,2))

p_myinterpSD=ggplot(df_interp, aes(x=x,y=y, fill = s))+geom_tile() +
#scale_fill_gradientn(name="lactate",colours = topo.colors(length(by1_raw)-2),breaks=by1_raw,labels=signif(by1_raw,2))
scale_fill_gradient(name="C_full",low="yellow",high="purple",breaks=by1_rawS,labels=signif(by1_rawS,2))


M_MSErm=t(as.matrix(read.table("/Users/ctuglus/Documents/Berkeley/Arkin/FromMarcin/DebugNull3_7_10/autonulls/4r_incr_nono_rand_expr_MSER_mean_full.txt",sep="\t")))
M_MSErsd=as.matrix(read.table("/Users/ctuglus/Documents/Berkeley/Arkin/FromMarcin/DebugNull3_7_10/autonulls/4r_incr_nono_rand_expr_MSER_sd_full.txt",sep="\t"))

Mraw_MSErm=t(as.matrix(read.table("/Users/ctuglus/Documents/Berkeley/Arkin/FromMarcin/DebugNull3_7_10/autonulls/4r_incr_nono_rand_expr_MSER_mean_raw.txt",sep="\t")))
Mraw_MSErsd=as.matrix(read.table("/Users/ctuglus/Documents/Berkeley/Arkin/FromMarcin/DebugNull3_7_10/autonulls/4r_incr_nono_rand_expr_MSER_sd_raw.txt",sep="\t"))


df_rawM <- expand.grid(x=1:25, y=1:13)
df_interpM <- expand.grid(x=1:121, y=1:61)
df_rawM$z=as.vector(Mraw_MSErm)
df_interpM$z=as.vector(M_MSErm)
df_rawM$s=as.vector(Mraw_MSErsd)
df_interpM$s=as.vector(M_MSErsd)
by1_rawM=unique(quantile(df_rawM$z,prob=seq(0,1,by=.1)))
by1_rawMs=unique(quantile(df_rawM$s,prob=seq(0,1,by=.1)))
by1_interpM=unique(quantile(df_rawM$z,prob=seq(0,1,by=.1)))

p_Mraw=ggplot(df_rawM, aes(x=x,y=y, fill = z))+geom_tile() +
#scale_fill_gradientn(name="lactate",colours = topo.colors(length(by1_raw)-2),breaks=by1_raw,labels=signif(by1_raw,2))
scale_fill_gradient(name="M_raw",low="yellow",high="purple",breaks=by1_rawM,labels=signif(by1_rawM,2))

p_Minterp=ggplot(df_interpM, aes(x=x,y=y, fill = z))+geom_tile() +
#scale_fill_gradientn(name="lactate",colours = topo.colors(length(by1_raw)-2),breaks=by1_raw,labels=signif(by1_raw,2))
scale_fill_gradient(name="M_full",low="yellow",high="purple",breaks=by1_rawM,labels=signif(by1_rawM,2))

p_MrawSD=ggplot(df_rawM, aes(x=x,y=y, fill = s))+geom_tile() +
#scale_fill_gradientn(name="lactate",colours = topo.colors(length(by1_raw)-2),breaks=by1_raw,labels=signif(by1_raw,2))
scale_fill_gradient(name="M_raw",low="yellow",high="purple",breaks=by1_rawMs,labels=signif(by1_rawMs,2))

p_MinterpSD=ggplot(df_interpM, aes(x=x,y=y, fill = s))+geom_tile() +
#scale_fill_gradientn(name="lactate",colours = topo.colors(length(by1_raw)-2),breaks=by1_raw,labels=signif(by1_raw,2))
scale_fill_gradient(name="M_full",low="yellow",high="purple",breaks=by1_rawMs,labels=signif(by1_rawMs,2))


Layout <- grid.layout(nrow = 2, ncol = 2)
vplayout <- function(...) {
     grid.newpage()
     pushViewport(viewport(layout = Layout))
 }
subplot <- function(x, y) viewport(layout.pos.row = x,
     layout.pos.col = y)


mplot <- function(p1, p2, p3, p4) {
     vplayout()
     print(p1, vp = subplot(1, 1))
     print(p2, vp = subplot(2, 1))
     print(p3, vp = subplot(1, 2))
     print(p4, vp = subplot(2, 2))
 }
pdf("compare_MSEr_means")
mplot(p_myraw,p_Mraw,p_myinterp,p_Minterp)
dev.off()
pdf("compare_MSEr_sds")
mplot(p_myrawSD,p_MrawSD,p_myinterpSD,p_MinterpSD)
dev.off()
#by1=unique(quantile(df$z,prob=seq(0,1,by=.1)))
#brs=seq(min(df$z), max(df$z), by = .1) 
#col=topo.colors((length(brs)-1))
# ggplot(df, aes(x=x,y=y, group = t,fill = z))+geom_tile() + facet_wrap(~t, ncol = 2)+
# scale_fill_gradientn(name="lactate",colours = topo.colors(length(by1)-2),breaks=by1,labels=signif(by1,2))



source("/Users/ctuglus/Documents/Berkeley/Arkin/FromMarcin/Miner10_30.R")

Ic<-sample(200,30)#c(79,99,134,138,196)
Jc<-sample(80,10)#c(29,33,50,58,61)
CritS_pre(Ic,Jc,Data,InterF,null,null, null,35)


#############################
######Code below reformats the old nulls to the correct matrix formatting.

tr2=read.table("/Users/ctuglus/Documents/Berkeley/Arkin/Yeast/fNulls621_3ec/F621_MSErmean_e_c",sep="\t")
tr2=as.matrix(tr2)

trVec=NULL
for(i in 1:(dim(tr2)[1])){
	trVec=c(trVec,tr2[i,])
}

tr3=matrix(trVec,ncol=61)
image(tr3)

#Nfiles=c("fNulls621_1","fNulls621_1a","fNulls621_2","fNulls621","fNulls621_3ec","fNulls621_4rincr","fNulls621_4rconst","fNulls621_4rincr1","fNulls621_4rconst1")

Nfiles=c("fNulls621_4rincr","fNulls621_4rconst","fNulls621_4rincr1","fNulls621_4rconst1")

setwd("/Users/ctuglus/Documents/Berkeley/Arkin/Yeast")
dir.create("CorrectFormatNull")
for(fi in 1:length(Nfiles)){
	setwd(Nfiles[fi])
	#setwd("/Users/ctuglus/Documents/Berkeley/Arkin/Yeast/fNulls621_4rincr1")
	Ffiles=dir()
	fn1=Ffiles[as.vector(sapply(Ffiles,function(x) grep("F621_MSE",x)))==1]
	fn1=fn1[!is.na(fn1)]
	for(fi1 in 1:length(fn1)){
		if(fi1==1) gwd=getwd()
		setwd(gwd)
		mat1=as.matrix(read.table(fn1[fi1],sep="\t"))
		trVec=NULL
		for(i in 1:(dim(mat1)[1])){
			trVec=c(trVec,mat1[i,])
		}
		tr3=matrix(trVec,ncol=(dim(mat1)[2]))
		setwd("/Users/ctuglus/Documents/Berkeley/Arkin/Yeast/CorrectFormatNull")
		dir.create(Nfiles[fi])
		setwd(Nfiles[fi])
		write.matrix(t(tr3),file=fn1[fi1],sep = "\t")
	}
	setwd("/Users/ctuglus/Documents/Berkeley/Arkin/Yeast")
}







#