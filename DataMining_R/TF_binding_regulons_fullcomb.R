library(clValid)
library(pheatmap)
library(RColorBrewer)
#setwd("~/Documents/integr8_genom/Miner/miner_results/yeast/")
#source("~/Documents/java/MAK/src/DataMining_R/Miner.R")
setwd("/usr2/people/marcin/integr8functgenom/Miner/miner_results/yeast")
source("~/Genomics/html/applet/MicroArrayUp/MicroArrayUp/DataMining_R/Miner.R")

mypalette <- rev(brewer.pal(6, "Blues"))
mypalette <- c(mypalette, brewer.pal(6, "YlOrBr"))
breaks <- seq(-3, 3,0.5 )

data <- read.table("./yeast_cmonkey.txt",header=T,row.names=1,sep="\t")
#motifdata <- read.table("./20120126_allMotifData1.01_trim.rdat",header=T,row.names=1,sep="\t")

regdata <- read.table("./RegulationMatrix_Documented_20111009_forR.txt",header=T,row.names=1,sep="\t")
colnames(regdata) <- toupper(colnames(regdata))
#regdata <- t(read.table("./SGD_regulator_mapping_pairs_matrix.txt",header=T,row.names=1,sep="\t"))

godata <- read.table("./go_slim_mapping.tab",header=F,sep="\t",fill=T)

#tabdata <- read.table("./4932_trim.tab",header=T,sep="\t")
#tabdata[,9] <- toupper(tabdata[,9])

sgdxref <- read.table("./SGD_dbxref.txt",header=F,sep="\t")

vbl <- list()
vbllabel <- list()
vblsplit <- list()
vblsplitlabel <- list()

colsum <- apply(regdata, 2,sum)
###include all regulatory combination cases
trimregdata <- regdata# [,which(colsum > 4)]#]

yeastgeneids <- read.table("./yeast_cmonkey_geneids.txt",sep="\t",header=F)
yeastgeneids <- rapply(yeastgeneids, as.character, classes="factor", how="replace")[[2]]#[[1]]

yeastgenenames <- read.table("./yeast_cmonkey_genenames.txt",sep="",header=F)
yeastgenenames <- rapply(yeastgenenames, as.character, classes="factor", how="replace")[[1]]
trimnames <- colnames(trimregdata)
indices <- match(trimnames, yeastgenenames)

###only consider genes in expression data which are in the regulation dataset
data_trim <- data[indices[!is.na(indices)],]

frxnunmapped <- mat.or.vec(0,0)
profiles <- list()
profile_names <- c()
dim <- dim(trimregdata)
for(i in 1:dim[2]) {
array <- which(trimregdata[,i] != 0)
  if(sum(array) > 0) {
  profiles <- c(profiles, list(array))
  profile_names <- c(profile_names, colnames(trimregdata)[i])
  }
}


doneprof <- mat.or.vec(length(profiles), 1)
profile_sets <- list()
profile_set_names <- list()

for(i in 1:length(profiles)) {
  if(doneprof[i] != 1) {
    curset <- list(i)
    cat("curset start ", unlist(curset),length(profile_sets), "\n")
      for(j in 1:length(profiles)) {
        if(i != j && doneprof[j] != 1) {
          if(all(profiles[[i]] == profiles[[j]])) {
            curset <- c(curset, j)
            doneprof[j] <- 1
          }
        }
      }
    
    doneprof[i] <- 1
    
    ###ignore if set has only 1 gene
      if(length(curset) > 1) {
      cat("length", length(curset),"\n")
        profile_sets <- c(profile_sets, list(curset))
        profile_set_names <- c(profile_set_names, paste(row.names(trimregdata)[unlist(profiles[[i]])], collapse="__"))
      }
  }
}


#write.table(unlist(profile_sets), file="regulon_clusters_yeastract_profilesets_v3.txt", sep="\t")

profiledata <- mat.or.vec(length(profile_sets), length(profiles))

###for number of unique TF profiles
for(i in 1:length(profile_sets)) {
profiledata[i,unlist(profile_sets[[i]])] <- 1 
}

row.names(profiledata) <- unlist(profile_set_names)
colnames(profiledata) <- unlist(profile_names)

write.table(profiledata, file="regulon_clusters_yeastract_profiledata_v3.txt", sep="\t")



for(i in 1:2) {#length(profile_sets)) { #2) { #
  print(paste("i ",i))
  #for YEASTRACT data
  print(paste("profile_sets ",profile_sets[[i]]))
  index <- profile_names[ unlist(profile_sets[[i]]) ]#colnames(trimregdata[which(trimregdata[i,] > 0)])
  cat("index",index,"\n")
  indexRef <- as.character(sgdxref[match(index,sgdxref[,6]), 4])
  indexRef[is.na(indexRef)] <- index[is.na(indexRef)]
  cat("indexRef",indexRef,"\n")
  #fraction unmapped
  
  ###match indices from gene expression (data_trim) to regulation (indexRef)
  match <- match(indexRef[!is.na(indexRef)],row.names(data_trim))
  match <- sort(match, decreasing=F)
  cat("match",match,"\n")

  expdata_orig <- data[match[!is.na(match)],]
    
  expdata <- t(apply(expdata_orig,1,missfxn))
  maxclust <- 50 #(dim(expdata)[2]-1)  
  
  valid <- NULL
#t(abs(expdata)
  tryCatch(valid <- clValid(t(expdata), nClust=c(5:maxclust),clMethods=c("hierarchical"),validation=c("internal"),metric=c("correlation"),method=c("complete"), maxitems=667)  , error = function(e) print(paste("clValid error ", sd(t(expdata)))), finally=print("finished")) 
  
  if(length(valid) != 0 ) { #&& valid != NULL
    
  sil <- valid@measures[3,,]
  #at least 5 clusters
  #sil <- sil[5:49] 
  #at least three clusters
  numclust <- names(sil[which(sil == max(sil))])
  print(paste("numclust", numclust, sep=" "))
  km <- kmeans(t(abs(expdata)), as.numeric(numclust), iter.max = 1000, nstart=100)
 
  for(j in 1:numclust) {
    print(paste("j ",j))
    expclust <- which(km$cluster == j) 
    print(paste("length expclust",length(expclust),sep=" "))
    if(length(expclust) > 2) {
      curexpdata <- expdata[,expclust]
      curexpdata <- t(apply(curexpdata,1,missfxn))
      print(dim(curexpdata))
      rm <- rowMeans(curexpdata)
      curexpdata_pos <- curexpdata[rm >= 0,]
      curexpdata_neg <- curexpdata[rm < 0,]
      
      cm <- colMeans(abs(curexpdata))
      curexpdata <- curexpdata[order(rm), order(cm)]
      
      ###threshold
      curexpdata[curexpdata > 3] <- 3
      curexpdata[curexpdata < -3] <- -3
      
      label <- paste("yeastract_",names(trimregdata)[i],"_regulon_",i,"_cluster_", j,sep="")
      vbllabel <- c(vbllabel, label)
      outpng <- paste(label,"_trim_heat.pdf",sep="")
      pdf(outpng, width=8.5, height=11)
      pheatmap(curexpdata, cluster_rows=F, cluster_cols=F, breaks=breaks,color=mypalette,cellwidth=1,cellheight=1,show_rownames=F,show_colnames=F, legend=F)
      dev.off(2)
      print(outpng)   
      vbl <- c(vbl, paste(paste(match,collapse=","),paste(expclust,collapse=","), sep="/"))
      print(paste("vbl len", length(vbl),sep=" "))
      
      #pos and neg split      
      match_pos <- which(rm >= 0)
      match_neg <- which(rm < 0)
      rm_pos <- rm[rm >= 0]
      rm_neg <- rm[rm < 0]                          
           
      ###require at least two genes
      if(length(match_pos) >= 2) {      
        cm_pos <- colMeans(abs(curexpdata_pos))
        curexpdata_pos <- curexpdata_pos[order(rm_pos), order(cm_pos)]
        
        curexpdata_pos[curexpdata_pos > 3] <- 3
        curexpdata_pos[curexpdata_pos < -3] <- -3
        
        label <- paste("yeastract_",names(trimregdata)[i],"_regulon_",i,"_cluster_", j,"_pos",sep="") 
        outpng <- paste(label, "_trim_heat.pdf",sep="")
        vblsplitlabel <- c(vblsplitlabel, label)
        pdf(outpng, width=8.5, height=11)
        pheatmap(curexpdata_pos, cluster_rows=F, cluster_cols=F, breaks=breaks,color=mypalette,cellwidth=1,cellheight=1,show_rownames=F,show_colnames=F, legend=F)
        dev.off(2)
        #print(outpng)
        #print(match_pos)
        #print(row.names(curexpdata_pos[match_pos,]))
        
        geneindex <- match(names(match_pos), yeastgeneids)
        
        vblsplit <- c(vblsplit, paste(paste(geneindex,collapse=","),paste(expclust,collapse=","), sep="/"))
      }
      
      ###require at least two genes
      if(length(match_neg) >= 2) {        
        cm_neg <- colMeans(abs(curexpdata_neg))
        
        curexpdata_neg <- curexpdata_neg[order(rm_neg), order(cm_neg)]
        
        curexpdata_neg[curexpdata_neg > 3] <- 3
        curexpdata_neg[curexpdata_neg < -3] <- -3
        
        label <- paste("yeastract_",names(trimregdata)[i],"_regulon_",i,"_cluster_", j,"_neg",sep="")
        vblsplitlabel <- c(vblsplitlabel, label)
        outpng <- paste(label, "_trim_heat.pdf",sep="")
        pdf(outpng, width=8.5, height=11)
        pheatmap(curexpdata_neg, cluster_rows=F, cluster_cols=F, breaks=breaks,color=mypalette,cellwidth=1,cellheight=1,show_rownames=F,show_colnames=F, legend=F)
        dev.off(2)
        #print(outpng)
         geneindex <- match(names(match_neg), yeastgeneids)
         
        vblsplit <- c(vblsplit, paste(paste(geneindex,collapse=","),paste(expclust,collapse=","), sep="/"))
      }
    }
  }
  }
}

write.table(unlist(vbl), "regulon_clusters_yeastract_v3.txt", sep="\t")
write.table(unlist(vblsplit), "regulon_clusters_yeastract_split_v3.txt", sep="\t")
write.table(unlist(vbllabel), "regulon_clusters_yeastract_label_v3.txt", sep="\t", row.names=F, col.names=F)
write.table(unlist(vblsplitlabel), "regulon_clusters_yeastract_split_label_v3.txt", sep="\t", row.names=F, col.names=F)


####
####
###OLD

  #for SGD reg data
  #index <- names(trimregdata[i,which(trimregdata[i,] > 0)])
  #indexRef <- index
  
  #for YEASTRACT data
  index <- colnames(trimregdata[which(trimregdata[i,] > 0)])
  #indexRef <- godata[match(index,godata[,2]), 1]
  indexRef <- as.character(sgdxref[match(index,sgdxref[,6]), 4])
  #indexRef[is.na(indexRef)] <- godata[match(index[is.na(indexRef)],godata[,2]), 1]
  indexRef[is.na(indexRef)] <- index[is.na(indexRef)]
  
  #fraction unmapped
  #sum(is.na(indexRef))/length(index)
  match <- match(indexRef[!is.na(indexRef)],row.names(data_trim))
  match <- sort(match, decreasing=F)
  
  
  if(length(match) > 3) {
    #for SGD
    #frxn <- sum(1 - (length(match))/length(indexRef))
    
    #for yeastract
    frxn <- 1 -length(match)/length(indexRef)    # (sum(is.na(match)))
  print(paste("frxn unmapped ", frxn ,sep=" "))
    frxnunmapped <- c(frxnunmapped, frxn)
    
  if(frxn > 1) {
    print(paste("frxn unmapped ERROR ", frxn ,sep=" "))
  }
    
    
  expdata_orig <- data[match[!is.na(match)],]
  expdata <- t(apply(expdata_orig,1,missfxn))
  maxclust <- 50 #(dim(expdata)[2]-1)
  valid <- clValid(t(abs(expdata)), nClust=c(5:maxclust),clMethods=c("hierarchical"),validation=c("internal"),metric=c("correlation"),method=c("complete"), maxitems=667)
  
  sil <- valid@measures[3,,]
  #at least 5 clusters
  #sil <- sil[5:49] 
  #at least three clusters
  numclust <- names(sil[which(sil == max(sil))])
  print(paste("numclust", numclust, sep=" "))
  km <- kmeans(t(abs(expdata)), as.numeric(numclust), iter.max = 1000, nstart=100)
 
  for(j in 1:numclust) {
    print(j)
    expclust <- which(km$cluster == j) 
    print(paste("length expclust",length(expclust),sep=" "))
    if(length(expclust) > 2) {
      curexpdata <- expdata[,expclust]
      curexpdata <- t(apply(curexpdata,1,missfxn))
      print(dim(curexpdata))
      rm <- rowMeans(curexpdata)
      curexpdata_pos <- curexpdata[rm >= 0,]
      curexpdata_neg <- curexpdata[rm < 0,]
      
      cm <- colMeans(abs(curexpdata))
      curexpdata <- curexpdata[order(rm), order(cm)]
      
      ###threshold
      curexpdata[curexpdata > 3] <- 3
      curexpdata[curexpdata < -3] <- -3
      
      label <- paste("yeastract_",names(trimregdata)[i],"_regulon_",i,"_cluster_", j,sep="")
      vbllabel <- c(vbllabel, label)
      outpng <- paste(label,"_trim_heat.png",sep="")
      pheatmap(curexpdata, cluster_rows=F, cluster_cols=F, breaks=breaks,color=mypalette,cellwidth=1,cellheight=1,show_rownames=F,show_colnames=F, legend=F, filename = outpng)
      print(outpng)   
      vbl <- c(vbl, paste(paste(match,collapse=","),paste(expclust,collapse=","), sep="/"))
      print(paste("vbl len", length(vbl),sep=" "))
      
      #pos and neg split      
      match_pos <- which(rm >= 0)
      match_neg <- which(rm < 0)
      rm_pos <- rm[rm >= 0]
      rm_neg <- rm[rm < 0]                          
           
      if(length(match_pos) >= 2) {      
        cm_pos <- colMeans(abs(curexpdata_pos))
        curexpdata_pos <- curexpdata_pos[order(rm_pos), order(cm_pos)]
        
        curexpdata_pos[curexpdata_pos > 3] <- 3
        curexpdata_pos[curexpdata_pos < -3] <- -3
        
        label <- paste("yeastract_",names(trimregdata)[i],"_regulon_",i,"_cluster_", j,"_pos",sep="") 
        outpng <- paste(label, "_trim_heat.png",sep="")
        vblsplitlabel <- c(vblsplitlabel, label)
        pheatmap(curexpdata_pos, cluster_rows=F, cluster_cols=F, breaks=breaks,color=mypalette,cellwidth=1,cellheight=1,show_rownames=F,show_colnames=F, legend=F, filename = outpng)
        #print(outpng)
        #print(match_pos)
        #print(row.names(curexpdata_pos[match_pos,]))
        
        geneindex <- match(names(match_pos), yeastgeneids)
        
        vblsplit <- c(vblsplit, paste(paste(geneindex,collapse=","),paste(expclust,collapse=","), sep="/"))
      }
      
      if(length(match_neg) >= 2) {        
        cm_neg <- colMeans(abs(curexpdata_neg))
        
        curexpdata_neg <- curexpdata_neg[order(rm_neg), order(cm_neg)]
        
        curexpdata_neg[curexpdata_neg > 3] <- 3
        curexpdata_neg[curexpdata_neg < -3] <- -3
        
        label <- paste("yeastract_",names(trimregdata)[i],"_regulon_",i,"_cluster_", j,"_neg",sep="")
        vblsplitlabel <- c(vblsplitlabel, label)
        outpng <- paste(label, "_trim_heat.png",sep="")
        pheatmap(curexpdata_neg, cluster_rows=F, cluster_cols=F, breaks=breaks,color=mypalette,cellwidth=1,cellheight=1,show_rownames=F,show_colnames=F, legend=F, filename = outpng)
        #print(outpng)
         geneindex <- match(names(match_neg), yeastgeneids)
         
        vblsplit <- c(vblsplit, paste(paste(geneindex,collapse=","),paste(expclust,collapse=","), sep="/"))
      }
    }
  }
}
}

write.table(unlist(vbl), "regulon_clusters_yeastract_trimgreat4.txt", sep="\t")
write.table(unlist(vblsplit), "regulon_clusters_yeastract_split_trimgreat4.txt", sep="\t")
write.table(unlist(vbllabel), "regulon_clusters_yeastract_label_trimgreat4.txt", sep="\t", row.names=F, col.names=F)
write.table(unlist(vblsplitlabel), "regulon_clusters_yeastract_split_label_trimgreat4.txt", sep="\t", row.names=F, col.names=F)
write.table(frxnunmapped, "frxnunmapped_yeastract_trimgreat4.txt")