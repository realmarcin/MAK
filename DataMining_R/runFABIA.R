library(fabia)

list2ascii <- function(x,file=paste(deparse(substitute(x)),".txt",sep="")) {
   tmp.wid = getOption("width")  # save current width
   options(width=10000)          # increase output width
   sink(file)                    # redirect output to file
   print(x)                      # print the object
   sink()                        # cancel redirection
   options(width=tmp.wid)        # restore linewidth
   return(invisible(NULL))       # return (nothing) from function

}

###yeast
setwd("~/Documents/integr8_genom/Miner/rda/common/yeast_data/")
ydata <- read.table("yeast_cmonkey_impute_forR.txt",header = TRUE, sep = "\t")
#row.names(ydata) <- ydata[,1]
#ydata <- ydata[,-1]
# 20 biclusters; sparseness 0.1; 500 cycles
ptm <- proc.time()
res <- fabia(ydata,30,0.1,500)
proc.time() - ptm
summary(res)
rb <- extractBic(res)
rb$bic[1,]
rb$bic[2,]
rb$bic[3,]
rb$bic[4,]
rb$bic[5,]

dims <- gsub("\"", "", paste(length(rb$bic[1,]$bixn),length(rb$bic[1,]$biypn),sep=" "))
outlist <- list(dims )
genes <- gsub("gene", "", paste(rb$bic[1,]$bixn,sep=" ")) 
outlist <- c(outlist,list(genes))
outlist <- c(outlist,list(paste(rb$bic[1,]$biypn,sep=" ")))
for(i in 2:5) {
    dims <- gsub("\"", "", paste(length(rb$bic[i,]$bixn),length(rb$bic[i,]$biypn),sep=" "))
    outlist <- c(outlist, dims)
    genes <- list(gsub("gene", "", paste(rb$bic[i,]$bixn,sep=" ")))
    outlist <- c(outlist, genes)
    outlist <- c(outlist, list(paste(rb$bic[i,]$biypn,sep=" ")))
}

list2ascii(outlist, file="yeast_cmonkey_expr_FABIA_30biclusters.txt")


###fake
setwd("~/Documents/integr8_genom/Miner/rda/common/yeast_data/fake_0.1noise_1var/")
fakedata <- read.table("fake110427_4c_incr_rand_expr.txt",header = TRUE, sep = "\t")
row.names(fakedata) <- fakedata[,1]
fakedata <- fakedata[,-1]
# 5 biclusters; sparseness 0.1; 500 cycles
res <- fabia(fakedata,5,0.1,500)
summary(res)
rb <- extractBic(res)
rb$bic[1,]
rb$bic[2,]
rb$bic[3,]
rb$bic[4,]
rb$bic[5,]

dims <- gsub("\"", "", paste(length(rb$bic[1,]$bixn),length(rb$bic[1,]$biypn),sep=" "))
outlist <- list(dims )
genes <- gsub("gene", "", paste(rb$bic[1,]$bixn,sep=" ")) 
outlist <- c(outlist,list(genes))
outlist <- c(outlist,list(paste(rb$bic[1,]$biypn,sep=" ")))
for(i in 2:5) {
    dims <- gsub("\"", "", paste(length(rb$bic[i,]$bixn),length(rb$bic[i,]$biypn),sep=" "))
    outlist <- c(outlist, dims)
    genes <- list(gsub("gene", "", paste(rb$bic[i,]$bixn,sep=" ")))
    outlist <- c(outlist, genes)
    outlist <- c(outlist, list(paste(rb$bic[i,]$biypn,sep=" ")))
}

#sink("fake110427_4c_incr_rand_expr_FABIA.txt")
#outlist
#sink()
list2ascii(outlist, file="fake110427_4c_incr_rand_expr_FABIA.txt")

#ROW
fakedata <- read.table("fake110427_4r_incr_rand_expr.txt",header = TRUE, sep = "\t")
row.names(fakedata) <- fakedata[,1]
fakedata <- fakedata[,-1]
# 5 biclusters; sparseness 0.1; 500 cycles
res <- fabia(fakedata,5,0.1,500)
summary(res)
rb <- extractBic(res)
rb$bic[1,]
rb$bic[2,]
rb$bic[3,]
rb$bic[4,]
rb$bic[5,]

dims <- gsub("\"", "", paste(length(rb$bic[1,]$bixn),length(rb$bic[1,]$biypn),sep=" "))
outlist <- list(dims )
genes <- gsub("gene", "", paste(rb$bic[1,]$bixn,sep=" ")) 
outlist <- c(outlist,list(genes))
outlist <- c(outlist,list(paste(rb$bic[1,]$biypn,sep=" ")))
for(i in 2:5) {
    dims <- gsub("\"", "", paste(length(rb$bic[i,]$bixn),length(rb$bic[i,]$biypn),sep=" "))
    outlist <- c(outlist, dims)
    genes <- list(gsub("gene", "", paste(rb$bic[i,]$bixn,sep=" ")))
    outlist <- c(outlist, genes)
    outlist <- c(outlist, list(paste(rb$bic[i,]$biypn,sep=" ")))
}

list2ascii(outlist, file="fake110427_4r_incr_rand_expr_FABIA.txt")


setwd("~/Documents/integr8_genom/Miner/rda/common/yeast_data/fake_over_0.1noise_1var_4blocks/")
fakedata <- read.table("fake110427_4c_incr_over_rand_expr.txt",header = TRUE, sep = "\t")
row.names(fakedata) <- fakedata[,1]
fakedata <- fakedata[,-1]
# 5 biclusters; sparseness 0.1; 500 cycles
res <- fabia(fakedata,4,0.1,500)
summary(res)
rb <- extractBic(res)
rb$bic[1,]$bixn
rb$bic[2,]$bixn
rb$bic[3,]$bixn
rb$bic[4,]$bixn

dims <- gsub("\"", "", paste(length(rb$bic[1,]$bixn),length(rb$bic[1,]$biypn),sep=" "))
outlist <- list(dims )
genes <- gsub("gene", "", paste(rb$bic[1,]$bixn,sep=" ")) 
outlist <- c(outlist,list(genes))
outlist <- c(outlist,list(paste(rb$bic[1,]$biypn,sep=" ")))
for(i in 2:4) {
    dims <- gsub("\"", "", paste(length(rb$bic[i,]$bixn),length(rb$bic[i,]$biypn),sep=" "))
    outlist <- c(outlist, dims)
    genes <- list(gsub("gene", "", paste(rb$bic[i,]$bixn,sep=" ")))
    outlist <- c(outlist, genes)
    outlist <- c(outlist, list(paste(rb$bic[i,]$biypn,sep=" ")))
}

#sink("fake110427_4c_incr_rand_expr_FABIA.txt")
#outlist
#sink()
list2ascii(outlist, file="fake110427_4c_incr_over_4blocks_rand_expr_FABIA.txt")

#ROW
fakedata <- read.table("fake110427_4r_incr_over_rand_expr.txt",header = TRUE, sep = "\t")
row.names(fakedata) <- fakedata[,1]
fakedata <- fakedata[,-1]
# 5 biclusters; sparseness 0.1; 500 cycles
res <- fabia(fakedata,4,0.1,500)
summary(res)
rb <- extractBic(res)
rb$bic[1,]
rb$bic[2,]
rb$bic[3,]
rb$bic[4,]

dims <- gsub("\"", "", paste(length(rb$bic[1,]$bixn),length(rb$bic[1,]$biypn),sep=" "))
outlist <- list(dims )
genes <- gsub("gene", "", paste(rb$bic[1,]$bixn,sep=" ")) 
outlist <- c(outlist,list(genes))
outlist <- c(outlist,list(paste(rb$bic[1,]$biypn,sep=" ")))
for(i in 2:4) {
    dims <- gsub("\"", "", paste(length(rb$bic[i,]$bixn),length(rb$bic[i,]$biypn),sep=" "))
    outlist <- c(outlist, dims)
    genes <- list(gsub("gene", "", paste(rb$bic[i,]$bixn,sep=" ")))
    outlist <- c(outlist, genes)
    outlist <- c(outlist, list(paste(rb$bic[i,]$biypn,sep=" ")))
}

list2ascii(outlist, file="fake110427_4r_incr_over_4blocks_rand_expr_FABIA.txt")
