setwd("~/Documents/integr8_genom/Miner/miner_results/yeast/")
library(gplots)

###C
data <- read.table("results_yeast_cmonkey_1_C_GOprofile_expand.txt", head=T, sep="\t")
row.names(data) <- data[,1]
data <- data[,-1]
#datamax <- apply(data, 2, max) 

#TODO remove GO terms with no assignments
tiff("results_yeast_cmonkey_1_C_GOprofile_expand_cluster.tiff",height=1200, width = 1000)
heatmap.2(as.matrix(data),trace="none",col=bluered(10))
#heatmap(as.matrix(data))
dev.off(2)


#setwd("~/Documents/integr8_genom/Miner/miner_results/yeast/")
data <- read.table("results_yeast_cmonkey_1_C_TFprofile_expand.txt", head=T, sep="\t")
row.names(data) <- data[,1]
data <- data[,-1]

#TODO remove TFs with no assignments
tiff("results_yeast_cmonkey_1_C_TFprofile_expand_cluster.tiff",height=1200, width = 1000)
#heatmap(as.matrix(data))
heatmap.2(as.matrix(data),trace="none",col=bluered(10))
dev.off(2)


#setwd("~/Documents/integr8_genom/Miner/miner_results/yeast/")
data <- read.table("results_yeast_cmonkey_1_C_TIGRprofile_expand.txt", head=T, sep="\t")
row.names(data) <- data[,1]
data <- data[,-1]

#TODO remove TIGRs with no assignments

tiff("results_yeast_cmonkey_1_C_TIGRprofile_expand_cluster.tiff",height=1200, width = 1000)
#heatmap(as.matrix(data))
heatmap.2(as.matrix(data),trace="none",col=bluered(10))
dev.off(2)

#setwd("~/Documents/integr8_genom/Miner/miner_results/yeast/")
data <- read.table("results_yeast_cmonkey_1_C_TIGRroleprofile_expand.txt", head=T, sep="\t")
row.names(data) <- data[,1]
data <- data[,-1]

#TODO remove TIGRs with no assignments

tiff("results_yeast_cmonkey_1_C_TIGRroleprofile_expand_cluster.tiff",height=1200, width = 1000)
#heatmap(as.matrix(data))
heatmap.2(as.matrix(data),trace="none",col=bluered(10))
dev.off(2)



###C TF
library(gplots)
setwd("~/Documents/integr8_genom/Miner/miner_results/yeast/")
data <- read.table("results_yeast_cmonkey_TF_1_C_GOprofile_expand.txt", head=T, sep="\t")
row.names(data) <- data[,1]
data <- data[,-1]
#datamax <- apply(data, 2, max) 

#TODO remove GO terms with no assignments

tiff("results_yeast_cmonkey_TF_1_C_GOprofile_expand_cluster.tiff",height=1200, width = 1000)
heatmap.2(as.matrix(data),trace="none",col=bluered(10))
#heatmap(as.matrix(data))
dev.off(2)


#setwd("~/Documents/integr8_genom/Miner/miner_results/yeast/")
data <- read.table("results_yeast_cmonkey_TF_1_C_TFprofile_expand.txt", head=T, sep="\t")
row.names(data) <- data[,1]
data <- data[,-1]

#TODO remove TFs with no assignments

tiff("results_yeast_cmonkey_TF_1_C_TFprofile_expand_cluster.tiff",height=1200, width = 1000)
#heatmap(as.matrix(data))
heatmap.2(as.matrix(data),trace="none",col=bluered(10))
dev.off(2)


#setwd("~/Documents/integr8_genom/Miner/miner_results/yeast/")
data <- read.table("results_yeast_cmonkey_TF_1_C_TIGRprofile_expand.txt", head=T, sep="\t")
row.names(data) <- data[,1]
data <- data[,-1]

#TODO remove TIGRs with no assignments

tiff("results_yeast_cmonkey_TF_1_C_TIGRprofile_expand_cluster.tiff",height=1200, width = 1000)
#heatmap(as.matrix(data))
heatmap.2(as.matrix(data),trace="none",col=bluered(10))
dev.off(2)


#setwd("~/Documents/integr8_genom/Miner/miner_results/yeast/")
data <- read.table("results_yeast_cmonkey_TF_1_C_TIGRroleprofile_expand.txt", head=T, sep="\t")
row.names(data) <- data[,1]
data <- data[,-1]

#TODO remove TIGRs with no assignments

tiff("results_yeast_cmonkey_TF_1_C_TIGRroleprofile_expand_cluster.tiff",height=1200, width = 1000)
#heatmap(as.matrix(data))
heatmap.2(as.matrix(data),trace="none",col=bluered(10))
dev.off(2)



###C PPI TF
library(gplots)
setwd("~/Documents/integr8_genom/Miner/miner_results/yeast/C_PPI_TF")
data <- read.table("results_yeast_cmonkey_TF_PPI_1_C_GOprofile_expand.txt", head=T, sep="\t")
row.names(data) <- data[,1]
data <- data[,-1]
#datamax <- apply(data, 2, max) 

#TODO remove GO terms with no assignments

tiff("results_yeast_cmonkey_TF_PPI_1_C_GOprofile_expand_cluster.tiff",height=1200, width = 1000)
heatmap.2(as.matrix(data),trace="none",col=bluered(10))
#heatmap(as.matrix(data))
dev.off(2)


#setwd("~/Documents/integr8_genom/Miner/miner_results/yeast/")
data <- read.table("results_yeast_cmonkey_TF_PPI_1_C_TFprofile_expand.txt", head=T, sep="\t")
row.names(data) <- data[,1]
data <- data[,-1]

#TODO remove TFs with no assignments

tiff("results_yeast_cmonkey_TF_PPI_1_C_TFprofile_expand_cluster.tiff",height=1200, width = 1000)
#heatmap(as.matrix(data))
heatmap.2(as.matrix(data),trace="none",col=bluered(10))
dev.off(2)


#setwd("~/Documents/integr8_genom/Miner/miner_results/yeast/")
data <- read.table("results_yeast_cmonkey_TF_PPI_1_C_TIGRprofile_expand.txt", head=T, sep="\t")
row.names(data) <- data[,1]
data <- data[,-1]

#TODO remove TIGRs with no assignments

tiff("results_yeast_cmonkey_TF_PPI_1_C_TIGRprofile_expand_cluster.tiff",height=1200, width = 1000)
#heatmap(as.matrix(data))
heatmap.2(as.matrix(data),trace="none",col=bluered(10))
dev.off(2)


#setwd("~/Documents/integr8_genom/Miner/miner_results/yeast/")
data <- read.table("results_yeast_cmonkey_TF_PPI_1_C_TIGRroleprofile_expand.txt", head=T, sep="\t")
row.names(data) <- data[,1]
data <- data[,-1]

#TODO remove TIGRs with no assignments

tiff("results_yeast_cmonkey_TF_PPI_1_C_TIGRroleprofile_expand_cluster.tiff",height=1200, width = 1000)
#heatmap(as.matrix(data))
heatmap.2(as.matrix(data),trace="none",col=bluered(10))
dev.off(2)