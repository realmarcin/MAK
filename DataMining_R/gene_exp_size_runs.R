setwd("~/Documents/integr8_genom/Miner/miner_results/yeast/EXPR_round12345_merge_refine/")
dataexpr <- read.table("./results_yeast_cmonkey_1_expr_MSEC_KendallC_GEECE_refine12345_top_plusbypass.vbl",header=T,sep="\t")

table(paste(dataexpr[,22],"_",dataexpr[,23],sep=""))
hist(table(paste(dataexpr[,22],"_",dataexpr[,23],sep="")))

> range(dataexpr[,22])
[1]  16 200
> range(dataexpr[,23])
[1]   7 124

tabl <- sort(table(paste(dataexpr[,22],"_",dataexpr[,23],sep="")))
tabl[tabl> 50]

100_45  40_41  46_33  54_41  55_35  59_52  62_58  65_43  67_35  69_41  77_33  81_39  85_43  91_63  97_39 
     5    253    281    319    327    348    358    370    377    386    417    438    454    469    484 
> 
> (200-16) + (124- 7)
[1] 301
> 
> (200-16) * (124- 7)
[1] 21528


dim(dataexpr)
[1] 3961   23

#pick 3961 size pairs from 21528 possible, with replacement -- what is distribution of repeats?

max <- mat.or.vec(1,1)
for(i in 1:100) {
data <- sort(table(sample(seq(1:3961),3961,replace=T)))
max <- c(max, max(data))
}
mean(max)
sd(max)

> mean(max)
[1] 6.19802
> sd(max)
[1] 0.9275754


densa <- density(sort(table(sample(seq(1:3961),3961,replace=T))),from=0,to=7)
densb <- density(tabl,from=0,to=100)
plot(densb$x,log(densb$y))
lines(densa$x, log(densa$y),col="red")