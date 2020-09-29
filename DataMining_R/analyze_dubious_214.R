setwd("~/Documents/integr8_genom/Miner/miner_results/yeast/")

geneids <- read.table("./yeast_cmonkey_geneids.txt",sep="\t")

unknownids <- read.table("~/Documents/integr8_genom/paper/submit/Supplement/SupFiles/yeast/MAK/214_dubious.txt",sep="\t")
dubiousids <- read.table("~/Documents/integr8_genom/Miner/miner_results/yeast/EXPR_round12345_merge_refine/bicluster_vignettes/213_dubious_ids.txt",sep="\t")

dubiousindex <- match(as.character(unlist(dubiousids)), as.vector(geneids[,2]))
dubiousindexstr <- paste(dubiousindex, collapse=",")