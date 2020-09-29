setwd("~/Documents/integr8_genom/Miner/miner_results/yeast/OUT_HCLstart/")
all_R <- read.table("./yeast_cmonkey_STARTS_abs_MSER_Kendall_GEERE.txt",sep="\t",header=T,comment="@")
all_C <- read.table("./yeast_cmonkey_STARTS_abs_MSEC_KendallC_GEECE.txt",sep="\t",header=T,comment="@")
plot(all_R$full_crit,all_C$full_crit, xlim=c(0,1), ylim=c(0,1))

seq <- seq(1:100)
seq <- seq/100.0
lines(seq,seq)

plot(density(all_R$full_crit,from=0,to=1),type="l",col="black",main="Row and column criteria scores for all starting points",xlab="Criterion value",xlim=c(0,1))
lines(density(all_C$full_crit,from=0,to=1),type="l",col="orange")
legend(0.1,3,c("Row","Column"), col=c("black","orange"),lw=4)


R_R <- read.table("./yeast_cmonkey_STARTS_abs_R_MSER_Kendall_GEERE.txt",sep="\t",header=T,comment="@")
C_R <- read.table("./yeast_cmonkey_STARTS_abs_R_MSEC_KendallC_GEECE.txt",sep="\t",header=T,comment="@")
C_C <- read.table("./yeast_cmonkey_STARTS_abs_C_MSEC_KendallC_GEECE.txt",sep="\t",header=T,comment="@")
R_C <- read.table("./yeast_cmonkey_STARTS_abs_C_MSER_Kendall_GEERE.txt",sep="\t",header=T,comment="@")

plot(density(R_R$full_crit,from=0,to=1),type="l",col="black",main="Row and column criteria scores for gene->exp and exp->gene starting points",xlab="Criterion value",xlim=c(0,1),lty=1)
lines(density(C_R$full_crit,from=0,to=1),type="l",col="black",lty=2)
lines(density(C_C$full_crit,from=0,to=1),type="l",col="orange",lty=1)
lines(density(R_C$full_crit,from=0,to=1),type="l",col="orange",lty=2)
legend(0,5,c("Row score row starts","Column score row starts","Column score column starts","Row score column starts"), col=c("black","black","orange","orange"),lty=c(1,2,1,2),lw=4)