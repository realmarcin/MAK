
#library(RXKCD)
#library(tm)
#library(wordcloud)

rm(list=ls())

library(RColorBrewer)
library(ggplot2)
library(pheatmap)
library(amap)
source("~/Documents/java/MAK/src/DataMining_R/Miner.R")


#setwd("~/Documents/integr8_genom/Miner/miner_results/yeast/REF_REGULON_OVERLAP/expdata_rand/")
#setwd("~/Documents/integr8_genom/Miner/miner_results/JJE/bicluster_dash/SOMR1_fit_data_test")
#setwd("~/Documents/VIMSS/biomanu/ChrisP/KEIO/MAK/EXPDATA_COL_merge_collabels_pdf/")
#setwd("~/Documents/integr8_genom/Miner/miner_results/yeast/final_paper_results/EXEMPLAR_RESULTS/EXPDATA")

#setwd("~/Documents/VIMSS/ontology/NCATS/notebooks/cq-notebooks_local/Orange_QB2_Other_CQs/Orange_disease_phenotype_compendium/EXPDATA/")

#setwd("~/Documents/VIMSS/ontology/NCATS/HPO/EXPDATA")

#setwd("~/Documents/VIMSS/biomanu/ChrisP/KEIO/MAK/expdata/")
#totaldata <- read.csv("../Final_20160906_reformat_matrix_NAto0_nomyst_noalb.txt",sep="\t",header=T,row.names=1)
#range(as.matrix(totaldata))
#range(log(totaldata,2))

setwd("~/Documents/integr8_genom/Miner/miner_results/yeast/final_paper_results/EXEMPLAR_RESULTS/EXPDATA/")

###
###data heatmaps
files <- list.files("./")

#mypalette <- rev(brewer.pal(9, "Blues"))
#mypalette <- mypalette[-c(length(mypalette)-1,length(mypalette))]

#mypalette <- rev(brewer.pal(3, "Blues"))

mypalette <- rev(brewer.pal(6, "Blues"))
mypalette <- c(mypalette, brewer.pal(6, "YlOrBr"))

cellwidth <- 6
cellheight <- 6
fontsize_row <- 7
fontsize_col <- 7


#mypalette <- brewer.pal(6, "Greys")

breaks <- seq(-3, 3,0.5 )
###LOG2
#breaks <- seq(0,  17.10914, 17.10914/ length(mypalette))

#breaks <- c(0,0.5,1)

for(i in 1:length(files)) {
  print(files[i])
  
  tryCatch({datathis <- read.table(paste("./",files[i],sep=""),sep="\t",header=T,row.names=1)})
  
  if(dim(datathis)) {
    
    datathis <- t(datathis)
    row.names(datathis) <- strtrim(row.names(datathis), 30)
    
    
    outf <- paste(files[i],"_cluster_heat.png",sep="")
    #print(outf)
    print(range(datathis))
    
    #png(outf, width=1000, height=1000)
    png(outf, width=10, height=10, units="in", res=100)
    
    print(range(datathis))
    
    mat <- as.matrix(datathis)
    
    #mat <- log(mat+0.001, 2)
    
    #print(dim(mat))
    #countmiss <- rowSums(is.na(mat))
    #print(which(countmiss > 3))
    #print(countmiss[countmiss > 3])
    #print(mat[which(countmiss > 3)],)
    
    #if(length(which(countmiss > 3)) > 0) {
    #    mat <- mat[-(which(countmiss > 3)),]
    #    }
    
    #print(dim(mat))
    
    #if(dim(mat)[1] > 1 && dim(mat)[2] > 1) {
    #mat <- as.matrix(mat)
    #probind <- which(countmiss == dim(mat)[2])
    #print(probind)
    #if(length(probind)) {
    #    mat[probind,] <- 0  
    #}
    
    #mat <- apply(mat,1,missfxn)
    
    #mat[mat > 3] <- 3
    #mat[mat < -3] <- -3
    
    tryCatch({
      clustmat <- mat
      countna <- sum(is.na(clustmat))
      if(countna > 0) {
        clustmat <- apply(mat,1,missfxn)
      }
      
      #print(mat)
      cordata <- 1-cor(clustmat)
      #pheatmap(log(mat+0.001,2),dist=cordata,cellwidth=11,cellheight=11,breaks=breaks,color=mypalette,show_rownames=T,show_colnames=T,legend=F,
      #         fontsize_row=14,fontsize_col=14)#cellwidth=2,cellheight=2,
      pheatmap(clustmat,dist=cordata,cellwidth=cellwidth,cellheight=cellheight,breaks=breaks,color=mypalette,show_rownames=T,show_colnames=T,legend=F,
               fontsize_row=fontsize_row,fontsize_col=fontsize_col)#cellwidth=2,cellheight=2,
      dev.off()
    })
    #}
    
    outf <- paste(files[i],"_heat.png",sep="")
    print(outf)
    #png(outf, width=1000, height=1000)
    png(outf, width=10, height=10, units="in", res=100)
    
    mat <- apply(mat,1,missfxn)
    
    rm <- rowMeans(mat)
    cm <- colMeans(mat)
    colorder <- order(cm, decreasing=T)
    roworder <- order(rm, decreasing=T)
    
    print(row.names(mat)[roworder])
    write.table(colnames(mat)[colorder], paste(files[i],"_colorder.txt",sep=""))
    write.table(row.names(mat)[roworder], paste(files[i],"_roworder.txt",sep=""))
    
    mat <- mat[roworder,]
    mat <- mat[,colorder]
    
    #pheatmap(log(mat+0.001,2),breaks=breaks,cellwidth=11,cellheight=11,cluster_rows=F,cluster_cols=F,color=mypalette,show_rownames=T,show_colnames=T,legend=F,
    #         fontsize_row=14,fontsize_col=14)
    
    pheatmap(mat,breaks=breaks,cellwidth=cellwidth,cellheight=cellheight,cluster_rows=F,cluster_cols=F,color=mypalette,show_rownames=T,show_colnames=T,legend=T,
             fontsize_row=fontsize_row,fontsize_col=fontsize_col)
    dev.off()
  }
}

#mat




###positive data
files <- list.files("./")

#mypalette <- brewer.pal(9, "YlOrBr")

max <- 0

maxes <- c()
for(i in 1:length(files)) {
  print(files[i])
  tryCatch(datathis <- read.table(paste("./",files[i],sep=""),sep="\t",header=T))#,row.names=1
  
  print(dim(datathis))
  if(dim(datathis)) {
    print(range(datathis))
    if(max < range(datathis)[2]) {
      max <- range(datathis)[2]
    }
    maxes <- c(maxes, range(datathis)[2])
  }
}

numbreak <- 9  
breaks <- seq(0, max, max/numbreak)

median <- median(maxes)

for(i in 1:length(files)) {
  print(files[i])
  tryCatch(datathis <- read.table(paste("./",files[i],sep=""),sep="\t",header=T,row.names=1))
  print(datathis)
  
  if(dim(datathis)) {
    
    
    tryCatch({
      outf <- paste(files[i],"_cluster_heat.pdf",sep="")
      print(outf)
      #png(outf, width=500, height=500)
      pdf(outf, width=8.5, height=11)
      dev.off()
      
      print(range(datathis))
      
      mat <- as.matrix(datathis)
      print(dim(mat))
      countmiss <- rowSums(is.na((mat)))
      #print(which(countmiss > 3))
      #print(countmiss[countmiss > 3])
      #print(mat[which(countmiss > 3)],)
      
      #if(length(which(countmiss > 3)) > 0) {
      #    mat <- mat[-(which(countmiss > 3)),]
      #    }
      
      print(dim(mat))
    }
    ,
    error=function() {
      print("clustering failed")
    }
    )
    
    
    
    if(dim(mat)[1] > 1 && dim(mat)[2] > 1) {
      #mat <- as.matrix(mat)
      probind <- which(countmiss == dim(mat)[2])
      print(probind)
      if(length(probind)) {
        mat[probind,] <- 0  
      }
      
      #mat <- apply(mat,1,missfxn)
      
      #mat[mat > median] <- median
      
      #symnum( cordata <- cor(mat) )
      
      #pheatmap(t(mat),dist=cordata,breaks=breaks,color=mypalette,show_rownames=T,show_colnames=T,legend=T)#cellwidth=2,cellheight=2,
      
      #dev.off()
      
      
      outf <- paste(files[i],"_heat.pdf",sep="")
      print(outf)
      #png(outf, width=500, height=500)
      pdf(outf, width=8.5, height=11)
      rm <- rowMeans(mat)
      cm <- colMeans(mat)
      colorder <- order(cm, decreasing=T)
      roworder <- order(rm, decreasing=T)
      
      print(row.names(mat)[roworder])
      write.table(colnames(mat)[colorder], paste(files[i],"_colorder.txt",sep=""))
      write.table(row.names(mat)[roworder], paste(files[i],"_roworder.txt",sep=""))
      
      mat <- mat[roworder,]
      mat <- mat[,colorder]
      
      pheatmap(t(mat),breaks=breaks,cluster_rows=F,cluster_cols=F,color=mypalette,show_rownames=T,show_colnames=T,legend=T)
      dev.off()
    }
  }
}

