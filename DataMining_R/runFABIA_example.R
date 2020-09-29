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

list2ascii(outlist, file="fake110427_4c_incr_rand_expr_FABIA.txt")