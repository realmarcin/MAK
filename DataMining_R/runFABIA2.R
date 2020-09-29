options(width=75)
set.seed(0)
library(fabia)
fabiaVer<-packageDescription("fabia")$Version
source("/usr2/people/marcin/Genomics/html/applet/MicroArrayUp/MicroArrayUp/DataMining_R/Miner.R")


list2ascii <- function(x,file=paste(deparse(substitute(x)),".txt",sep="")) {
  tmp.wid = getOption("width")  # save current width
  options(width=10000)          # increase output width
  sink(file)                    # redirect output to file
  print(x)                      # print the object
  sink()                        # cancel redirection
  options(width=tmp.wid)        # restore linewidth
  return(invisible(NULL))       # return (nothing) from function
  
}


setwd("/usr2/people/marcin/integr8functgenom/Miner/miner_results/yeast/FABIA2")
#data <- read.csv("./yeast_cmonkey.txt",sep="\t",header=T,row.names=1)
#data_impute <- apply(data, 1, missfxn)

data <- read.csv("./yeast_cmonkey_impute_forR.txt",sep="\t",header=T,row.names=1)


Sys.time()
#[1] "2016-10-25 12:32:59 PDT"
res <- fabia(data,273,0.01,500)
Sys.time()

rb <- extractBic(res)
rb$bic[1,]


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

list2ascii(outlist, file="yeast_cmonkey_expr_FABIA_273biclusters.txt")
