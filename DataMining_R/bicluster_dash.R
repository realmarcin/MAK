library(pheatmap)
library(tiff)
library(Hmisc)
library(gplots)
library(RColorBrewer)
library(png)
library(fields)
source("~/Documents/java/MAK/src/DataMining_R/Miner.R")


#setwd("~/Documents/integr8_genom/Miner/miner_results/yeast/EXPR_round12345_merge_refine/bicluster_dash_all/")
#setwd("~/Documents/integr8_genom/Miner/miner_results/yeast/EXPR_round12345_merge_refine/bicluster_dash_0.5/")
#setwd("~/Documents/integr8_genom/Miner/miner_results/yeast/OUT_cmonkey/bicluster_dash/")
#setwd("~/Documents/integr8_genom/Miner/miner_results/yeast/OVERLAPS/bicluster_dash")

#setwd("~/Documents/integr8_genom/Miner/miner_results/SOMR1_fit/bicluster_dash")

#summaryraw <- readLines("../146_summary.txt")
#summaryraw <- readLines("../results_yeast_cmonkey_1_expr_MSEC_KendallC_GEECE_refine12345_top_0.5_1.0_c_liven_reconstructed_pval0.001_cut_0.01_summary.txt")
#summaryraw <- readLines("../cMonkey_yeast_MSEC_KendallC_GEECE_inter_feat_0.25_1.0_c_liven_reconstructed_pval0.001_cut_0.01_summary.txt")
#summaryraw <- readLines("../cMonkey_yeast_MSEC_KendallC_GEECE_inter_feat_pval0.001_cut_0.01_summary.txt")

#summaryraw <- readLines("../SOMR1_fitness_refine_top_0.25_1.0_c_reconstructed_pval0.001_cut_0.01_summary.txt")
setwd("~/Documents/integr8_genom/Miner/miner_results/yeast/final_paper_results/EXEMPLAR_RESULTS/bicluster_dash_all/")

#summaryraw <- readLines("./results_yeast_cmonkey_ws27_29_34_ROW_round12_cut_scoreperc66.0_exprNaN_0.0__nr_0.25_norm__nr_0.25_score_pval0.001_cut_0.01_summary.txt")
#dim(summaryraw)
#summarysplit <-  sapply(summaryraw, function(x) strsplit(x,"\t"))
#summary <- matrix(unlist(summarysplit), ncol = 28, byrow = TRUE)

summary <- read.csv("../results_yeast_cmonkey_ws27_29_34_ROW_new_round12_cut_scoreperc66.0_exprNaN_0.0__nr_0.25_score_root.txt_norm__nr_0.25_score_root_pval0.001_cut_0.01_summary.txt", sep="\t",header=T)
head(summary)
#colnames(summary) <- summary[1,]#
#summary <- summary[-1,]

#GO column
#summary[,13]
#pathway column
#summary[,15]
#TF column
#summary[,17]

###bicluster dash

files <- list.files("./")

#prefix <- "netmod87_top3.vbl__"
#prefix <- "results_yeast_cmonkey_1_expr_MSEC_KendallC_GEECE_refine12345_top_0.5_1.0_c_liven_reconstructed.txt__"
#prefix <- "cMonkey_yeast_MSEC_KendallC_GEECE_inter_feat_0.25_1.0_c_liven_reconstructed.txt__"
#prefix <- "cMonkey_yeast_MSEC_KendallC_GEECE_inter_feat.txt__"


#results_yeast_cmonkey_ws27_29_34_ROW_round12_cut_scoreperc66.0_exprNaN_0.0__nr_0.25_norm__nr_0.25_score.txt__0_exps.txt
prefix <- "results_yeast_cmonkey_ws27_29_34_ROW_new_round12_cut_scoreperc66.0_exprNaN_0.0__nr_0.25_score_root.txt_norm__nr_0.25_score_root.txt__"#"overlap_select_5.txt__"

mypalette <- rev(brewer.pal(6, "Blues"))
mypalette <- c(mypalette, brewer.pal(6, "YlOrBr"))
breaks <- seq(-3, 3,0.5 ) 

start <- 0
count_coexpr_bind <- 0
count_anticoexpr_bind <- 0
countGO_sig <- 0

for(i in start:length(files)) {
  #for(i in start:10) {
    if(length(grep("_TFbindcoexpr.txt",files[i])) > 0 ) {
        print(files[i])
        
        #index <- regexpr("\\__[^\\__]*$", files[i])
        index <- regexpr("root.txt__",files[i], fixed=T)[1]+nchar("root.txt__")
        
        short <- substr(files[i], index, nchar(files[i]))
        label <- substr(short, 1, regexpr("_",short, fixed=T)[1]-1)
        print(short)
        print(label)
      
        print(paste("bicluster ",index," ",i,short, paste("bicluster",label,sep=""), sep=" "))  
        
        top_TFbind <- NULL
        top_TFbindcoexpr <- NULL
        top_TFbindanticoexpr <- NULL
        top_GO <- NULL
        top_exp <- NULL
        
        
        ##TFbind pvalues
        datamat_TFbindpval <- NULL
        datamat_TFbindpval_top <- NULL
        bind_name <- NULL
        try(datamat_TFbindpval <- read.table(paste(prefix , label,"_TFbind_pval0.001",".txt",sep=""),sep="\t", header=F))
        try(datamat_TFbindpval_top <- datamat_TFbindpval[order(datamat_TFbindpval[,4]/datamat_TFbindpval[,5],decreasing=T),][1:5,])
        try(bind_name <- names(datamat_TFbindpval_top[,2]))
        
                
        ###TFbindcoexpr                    
        datamat_TFbindcoexpr_raw <- readLines(paste(prefix , label,"_TFbindcoexpr",".txt",sep=""))
        datamat_TFbindcoexpr_raw <- datamat_TFbindcoexpr_raw[-which(datamat_TFbindcoexpr_raw == "null")]
        if(length(datamat_TFbindcoexpr_raw) > 0) {
          datamat_TFbindcoexpr_split <- sapply(datamat_TFbindcoexpr_raw , function(x) strsplit(x, "_"))    
          len <- length(datamat_TFbindcoexpr_split)
          max_length <- max(sapply(datamat_TFbindcoexpr_split,length))
          datamat_TFbindcoexpr_split_fill <- sapply(datamat_TFbindcoexpr_split, function(x){
            c(x, rep(NA, max_length - length(x)))
          })
          
          datamat_TFbindcoexpr <- matrix(unlist(datamat_TFbindcoexpr_split_fill), nrow=len, ncol=max_length,byrow=T)
          top_TFbindcoexpr <- sort(table(unlist(datamat_TFbindcoexpr)), decreasing=T)
        }
        
    
        
        ###TFbindanticoexpr    
        datamat_TFbindanticoexpr_raw <- readLines(paste(prefix , label,"_TFbindanticoexpr",".txt",sep=""))
        datamat_TFbindanticoexpr_raw <- datamat_TFbindanticoexpr_raw[-which(datamat_TFbindanticoexpr_raw == "null")]
        if(length(datamat_TFbindanticoexpr_raw) > 0) {
          datamat_TFbindanticoexpr_split <- sapply(datamat_TFbindanticoexpr_raw , function(x) strsplit(x, "_"))    
          len <- length(datamat_TFbindanticoexpr_split)
          max_length <- max(sapply(datamat_TFbindanticoexpr_split,length))
          datamat_TFbindanticoexpr_split_fill <- sapply(datamat_TFbindanticoexpr_split, function(x){
            c(x, rep(NA, max_length - length(x)))
          })
          datamat_TFbindanticoexpr <- matrix(unlist(datamat_TFbindanticoexpr_split_fill), nrow=len, ncol=max_length,byrow=T)
          top_TFbindanticoexpr <- sort(table(unlist(datamat_TFbindanticoexpr)), decreasing=T)
        }
            
        
        ###TFbind              
        datamat_TFbind_raw <- readLines(paste(prefix , label,"_TFbind",".txt",sep=""))
        datamat_TFbind_raw <- datamat_TFbind_raw[-which(datamat_TFbind_raw == "null")]
        if(length(datamat_TFbind_raw) > 0) {
          datamat_TFbind_split <- sapply(datamat_TFbind_raw , function(x) strsplit(x, "_"))    
          len <- length(datamat_TFbind_split)
          max_length <- max(sapply(datamat_TFbind_split,length))
          datamat_TFbind_split_fill <- sapply(datamat_TFbind_split, function(x){
            c(x, rep(NA, max_length - length(x)))
          })
          
          datamat_TFbind <- matrix(unlist(datamat_TFbind_split_fill), nrow=len, ncol=max_length,byrow=T)
          top_TFbind <- sort(table(unlist(datamat_TFbind)), decreasing=T)
          top_TFbind_5 <- top_TFbind[1:(min(5,length(top_TFbind)))]
          #bind_name <- names(top_TFbind_5)
        }
        print(top_TFbind)
        print(top_TFbind_5)
        
        TFsigsplit <- NULL
        #TF significant enrich YEASTRACT
        if(!is.null(datamat_TFbindpval_top)) {
        if(is.matrix(summary)) {
          TFsig <- toupper(summary[as.numeric(label)+1,17])
          }
          else {
          TFsig <- toupper(summary[17])
          }
          if(TFsig != "NONE") {
            TFsigsplit <- strsplit(TFsig, "_")
            TFsigsplit <- lapply(TFsigsplit, function(x) substr(x, 1,length(x)+1))
         
          TFsigsplit <- unlist(TFsigsplit)
          crossindex <- c()
          for(c in 1:length(TFsigsplit)) {          
            matchnow <- match(TFsigsplit[c], toupper(datamat_TFbindpval_top[,2]))
            if(!is.na(matchnow)) {
              crossindex <- c(crossindex, matchnow)
            }
          }
          if(!is.null(crossindex) && length(crossindex) > 0) {
            #crossindex <- match(TFsigsplit, names(top_TFbind_5))
            print(paste("significant TF?",crossindex,sep=" "))
            if(!is.na(crossindex)) {
              for(c in 1:length(crossindex)) {
                levels(datamat_TFbindpval_top[,2]) <- c(levels(datamat_TFbindpval_top[,2]),newstr)
                newstr <- paste("*",as.character(datamat_TFbindpval_top[crossindex[c],2]),sep="")
                levels(datamat_TFbindpval_top[,2]) <- c(levels(datamat_TFbindpval_top[,2]),newstr)
                datamat_TFbindpval_top[crossindex[c],2] <- newstr
                print(paste("significant TF",names(datamat_TFbindpval_top[c,2]),sep=" "))
              }
            }
          } 
          }
        }
        
        ###GO
        datain_GO <- readLines(paste(prefix , label,"_GO",".txt",sep=""))     
        datasplit_GO  <- sapply(datain_GO , function(x) strsplit(x, "\t"))
        datamat_GO  <- matrix(unlist(datasplit_GO), ncol = 2, byrow = TRUE)
        datamag_GO <- datamat_GO[order(as.numeric(datamat_GO[,2]), decreasing=T),]
        if(!is.null(dim(datamag_GO))) {
          end <- min(dim(datamag_GO)[1],5)
          top_GO <- datamag_GO[1:end,]
          row.names(top_GO) <- top_GO[,1]
          top_GO[,2] <- as.numeric(top_GO[,2])
          top_GO <- top_GO[,-1]
          top_GO <- as.numeric(top_GO)
          names(top_GO) <- datamag_GO[1:end,1]
        } else {
          end <- 1
          top_GO <- as.numeric(datamag_GO[2])
          names(top_GO) <- datamag_GO[1]
        }                
        print(top_GO)    
        
        #GO significant enrich
        if(is.matrix(summary)) {
        GOstrtmp <- toupper(summary[as.numeric(label)+1,13])
        }
        else {
        GOstrtmp <- toupper(summary[13])
        }
        print(paste("GOstrtmp ",GOstrtmp))
        if(GOstrtmp != "NONE") {
          GOsig <- substr(GOstrtmp, 6, nchar(GOstrtmp)-1)
          GOsigsplit <- strsplit(GOsig, "_")
        }
        GOsigsplit <- unlist(GOsigsplit)  
        crossindex <- c()
        for(c in 1:length(GOsigsplit)) {          
          matchnow <- match(GOsigsplit[c], toupper(names(top_GO)))
          if(!is.na(matchnow)) {
            crossindex <- c(crossindex, matchnow)
          }
        }
        if(!is.null(crossindex)) {
          #crossindex <- match(GOsigsplit, toupper(names(top_GO)))
          print(paste("significant GO?",crossindex,sep=" "))
          if(!is.na(crossindex)) {
            for(c in 1:length(crossindex)) {
              names(top_GO)[c] <- paste("*", names(top_GO[c]),sep="")
              print(paste("significant GO",c, names(top_GO[c]),sep=" "))
            }
            count_GOsig <- countGO_sig + 1
          }
        }
        
        ##Exp
        datain_exp <- readLines(paste(prefix , label,"_raw_exps.txt",sep=""))     
        datasplit_exp  <- sapply(datain_exp , function(x) strsplit(x, "\t"))
        datamat_exp  <- matrix(unlist(datasplit_exp), ncol = 2, byrow = TRUE)
        datamag_exp <- datamat_exp[order(as.numeric(datamat_exp[,2]), decreasing=T),]
        end <- -1
        
        #tryCatch(irr(x), error = function(e) NULL)
        
        tryCatch({
          
          end <- min(length(datamag_exp[,1]),5)
          top_exp <- datamag_exp[1:end,]
          
          row.names(top_exp) <- top_exp[,1]
          top_exp[,2] <- as.numeric(top_exp[,2])
          top_exp <- top_exp[,-1]
          top_exp <- as.numeric(top_exp)
          names(top_exp) <- datamag_exp[1:end,1]
          print(top_exp) 
          
          },error=function(error_message) {
            message(error_message)
            return(NA)
          },
          finally={
            if(end == -1) {
              end <- 1
              top_exp <- datamag_exp 
              
              names(top_exp) <- top_exp[1]
              top_exp[2] <- as.numeric(top_exp[2])
              top_exp <- top_exp[-1]
              top_exp <- as.numeric(top_exp)
              names(top_exp) <- datamag_exp[1]
              print(top_exp) 
            }
          }
          )
        
        
        
        
        ###heatmap
        
        read <- paste(prefix , label,"_expdata",".txt",sep="")
        print(paste("reading ", read,sep=""))
        data_matrix <- read.table(read,sep="\t",row.names=1,header=T)     
        print(dim(data_matrix))
        data_matrix_imp <- apply(data_matrix,1,missfxn)

        rm <- rowMeans(data_matrix_imp)
        cm <- colMeans(data_matrix_imp)
        data_matrix_imp <- data_matrix_imp[order(rm), order(cm)]
        
        ###threshold
        data_matrix_imp[data_matrix_imp > 3] <- 3
        data_matrix_imp[data_matrix_imp < -3] <- -3
        #symnum( cordata <- cor(data_matrix_imp) )

        
        outpng <- paste(prefix ,"_",label,"_heat.png",sep="")
        pheatmap(t(data_matrix_imp), cluster_rows=F, cluster_cols=F, breaks=breaks,color=mypalette,cellwidth=1,cellheight=1,show_rownames=F,show_colnames=F, legend=F, filename = outpng)#dist=cordata,##, mai=c(0,0,0,0)# clustering_distance_cols="euclidean",treeheight_row=0, treeheight_col=0, #clustering_distance_rows="euclidean",
        
        #test
        #pheatmap(t(data_matrix_imp), cluster_rows=F, cluster_cols=F, breaks=breaks,color=mypalette,cellwidth=1,cellheight=1,show_rownames=F,show_colnames=F, legend=F)
        #pheatmap(t(data_matrix_imp),dist=cordata,breaks=breaks,color=mypalette,cellwidth=1,cellheight=1,show_rownames=F,show_colnames=F, legend=F)
        
        pngload <-  readPNG(outpng, TRUE)
  
        pdf(paste(prefix ,"_",label,".pdf",sep=""),width=8, height=8)
        par(mfrow=c(1,2))
        #quartz(width=600, height=600)
        
        #make empty plot for image
        #plot(0,xaxt='n',yaxt='n',bty='n',pch='',ylab='',xlab='')#, mai=c(0,0,0,0)
        plot(1,1,xlim=c(1,res[1]),ylim=c(1,res[2]),asp=1,type='n',xaxs='i',yaxs='i',xaxt='n',yaxt='n',xlab='',ylab='',bty='n')
        
        
        #rng1 <- par("usr")
        
        res <- dim(pngload)[2:1]
        rasterImage(pngload,1,1,res[1],res[2])
        #rasterImage(pngload,rng1[1], rng1[3], rng1[2], rng1[4], mai=c(0,0,0,0))#0.568  0.000 15.500  1.000# 0.6, -1.080, 1.4, 1)#                

        #add_image(pngload, x=0.5, y=0.1, size=0.5)
        #add.image( 0,0, pngload, adj.x=0, adj.y=0,image.width=0.5, image.height = 1) 
       
        print("1")
        coexpr_bind <- intersect(bind_name, names(top_TFbindcoexpr[1:(min(5,length(top_TFbindcoexpr)))]))
         print("2")
        anticoexpr_bind <- intersect(bind_name, names(top_TFbindanticoexpr[1:(min(5,length(top_TFbindanticoexpr)))]))
         print("3")
        coexpr_bind_ind <- which(bind_name == coexpr_bind)
         print("4")
        anticoexpr_bind_ind <- which(bind_name == anticoexpr_bind)
         print("5")
        
        cols <- c("black", "black", "black", "black", "black")
        if(length(coexpr_bind_ind) >0) {
          cols[coexpr_bind_ind] <- "red"
          count_coexpr_bind <- count_coexpr_bind + 1
        }
        if(length(anticoexpr_bind_ind) >0) {
          cols[anticoexpr_bind_ind] <- "blue"
          count_anticoexpr_bind <- count_anticoexpr_bind + 1
        }
        #textplot(bind_name, ps =4, cex =0.7)
          print("6")
        #txttableplot(top_TFbind_5, coexpr_bind_ind, anticoexpr_bind_ind, "")
        
         
    
    #color according to coexpression
    defcols <- rep("black", length(top_TFbind_5))    
        cols <- defcols
        if(length(coexpr_bind_ind) >0) {
          cols[coexpr_bind_ind] <- "red"
        }
        if(length(anticoexpr_bind_ind) >0) {
          cols[anticoexpr_bind_ind] <- "blue"
        }
        cols <- c(cols, defcols,defcols)
        print(cols)
        
        alllabels <- c()
        try(alllabels <- paste(datamat_TFbindpval_top[,2],round(datamat_TFbindpval_top[,4]/datamat_TFbindpval_top[,5], 2),sep=" "))
        while(length(alllabels) < 5) {
          alllabels <- c(alllabels, "")
        }
        alllabels <- c(alllabels, paste(names(top_GO),round(top_GO/dim(data_matrix_imp)[2], 2),sep=" "))
        alllabels <- c(alllabels, paste(names(top_exp),round(top_exp/dim(data_matrix_imp)[1], 2),sep=" "))
        #alllabels <- c(alllabels, label, sep=" ")
        
        #printlabel <- label
        #names(printlabel) <- label
        
        m <- mat.or.vec(0,0)
        if(!is.null(datamat_TFbindpval_top)) {
          datamat_TFbindpval_top_vec <- datamat_TFbindpval_top[,4]
          names(datamat_TFbindpval_top_vec) <- datamat_TFbindpval_top[,2]
          m <- as.matrix(datamat_TFbindpval_top[,4])
          row.names(m) <-  names(datamat_TFbindpval_top_vec) 
        }

        while(dim(m)[1] < 5) {
          v <- "0"
          names(v) <- "c"
          if(dim(m)[2] == 0) {
           m <- as.matrix(v) 
          }
          else {
          m <- rbind(m,v)
          }
        }
        m <- rbind(m, as.matrix(top_GO),as.matrix(top_exp))
        
      
        image(1:length(alllabels),1:1,as.matrix(as.numeric(m[,1])),col=c("white"), axes=FALSE, xlab="",ylab="",main="",ylim=c(0,1))#xlim=c(0,1), 
        allratio <- row(m)/length(row(m))
        print("TF")
        print(allratio)
        rng <- par("usr")
        heights <- rev(allratio - allratio[1]/2)
        par(lheight=.3) 
        print(alllabels)
        text(1,heights, labels=alllabels, col=cols,adj=c(0,0))
        lines(c(rng[1],rng[2]), c(heights[5], heights[5])- allratio[1]/2,col="gray")
        lines(c(rng[1],rng[2]), c(heights[10], heights[10])- allratio[1]/2,col="gray")
        text(0.5,0.01,as.numeric(label)+1, adj=c(0,0))
                    
        dev.off(2);
    
    }
    
    print("end iteration")
}


print(paste("count_coexpr_bind ", count_coexpr_bind,sep=" "))
print(paste("count_anticoexpr_bind ", count_anticoexpr_bind,sep=" "))
print(paste("countGO_sig ", countGO_sig,sep=" "))



add_image<-function(image, x, y, size){
  dims<-dim(image)[1:2] #number of x-y pixels for the logo (aspect ratio)
  AR<-dims[1]/dims[2]
  par(usr=c(0, 1, 0, 1))
  rasterImage(image, x-(size/2), y-(AR*size/2), x+(size/2), y+(AR*size/2), interpolate=TRUE)
}

