  rm(list=ls())
  
  library(pheatmap)
  library(tiff)
  library(Hmisc)
  library(gplots)
  library(RColorBrewer)
  library(png)
  #library(tm)
  source("~/Documents/java/MAK/src/DataMining_R/Miner.R")
  
  
  #MAK <- TRUE
  method <- "MAK" #"COALESCE"#"COALESCE" "cMonkey"
  GO <- F
  
  if(method ==  "MAK") {
    setwd("~/Documents/integr8_genom/Miner/miner_results/yeast/final_paper_results/EXEMPLAR_RESULTS/bicluster_dash_details_NOVEL_test")
    getwd()
    #setwd("~/Documents/integr8_genom/Miner/miner_results/yeast/EXPR_round12345_merge_refine/bicluster_dash_all_new/")
    #setwd("~/Documents/integr8_genom/Miner/miner_results/SOMR1_fit/bicluster_dash")
  } else if(method == "cMonkey") { 
    setwd("~/Documents/integr8_genom/Miner/miner_results/yeast/OUT_cmonkey/bicluster_dash/") 
  } else if(method == "COALESCE") { 
    setwd("~/Documents/integr8_genom/Miner/miner_results/yeast/OUT_COALESCE/bicluster_dash/") }
  
  if(method ==  "MAK") {
    summaryraw <- read.csv("../results_yeast_cmonkey_ws27_29_34_ROW_new_round12_cut_scoreperc66.0_exprNaN_0.0__nr_0.25_score_root.txt_norm__nr_0.25_score_root_summary_novel.txt",sep="\t",header=T)
    #summaryraw <- readLines("../results_yeast_cmonkey_1_expr_MSEC_KendallC_GEECE_refine12345_top_0.25_1.0_c_liven_reconstructed_pval0.001_cut_0.01_summary.txt")
    #summaryraw <- readLines("../SOMR1_fitness_refine_top_0.25_1.0_c_reconstructed_pval0.001_cut_0.01_summary.txt")
  } else if(method == "cMonkey") { 
    summaryraw <- readLines("../cMonkey_yeast_MSEC_KendallC_GEECE_inter_feat_0.25_1.0_c_liven_reconstructed_pval0.001_cut_0.01_summary.txt") 
    } else if(method == "COALESCE") { 
    summaryraw <- readLines("../motifs_summary.txt_MSEC_KendallC_GEECE_inter_feat_0.25_1.0_c_liven_reconstructed_pval0.001_cut_0.01_summary.txt") }
  
  #MAK_expr_less0.2_overlap.vbl
  
  #summarysplit <-  sapply(summaryraw, function(x) strsplit(x,"\t"))
  #summary <- matrix(unlist(summarysplit), ncol = 28, byrow = TRUE)
  #colnames(summary) <- summary[1,]#
  #summary <- summary[-1,]
  summary <- summaryraw
  
  #GO column
  #summary[,13]
  #pathway column
  #summary[,15]
  #TF column
  #summary[,17]
  
  ###bicluster dash
  
  files <- list.files("./")
  
  #prefix <- "netmod87_top3.vbl__"
  if(method ==  "MAK") {
    #results_yeast_cmonkey_ws27_29_34_ROW_round12_cut_scoreperc66.0_exprNaN_0.0__nr_0.25_norm__nr_0.25_score_pval0.001_cut_0.01_summary.txt
    prefix <- "results_yeast_cmonkey_ws27_29_34_ROW_new_round12_cut_scoreperc66.0_exprNaN_0.0__nr_0.25_score_root.txt_norm__nr_0.25_score_root_novel.txt__"
    #prefix <- "results_yeast_cmonkey_1_expr_MSEC_KendallC_GEECE_refine12345_top_0.25_1.0_c_liven_reconstructed.txt__"
    #prefix <- "SOMR1_fitness_refine_top_0.25_1.0_c_reconstructed.txt__"
  } else if(method == "cMonkey") { prefix <- "cMonkey_yeast_MSEC_KendallC_GEECE_inter_feat_0.25_1.0_c_liven_reconstructed.txt__" 
  } else if(method == "COALESCE") { prefix <- "motifs_summary.txt_MSEC_KendallC_GEECE_inter_feat_0.25_1.0_c_liven_reconstructed.txt__" }
  
  suffix <- "root_novel.txt__"
  
  mypalette <- rev(brewer.pal(6, "Blues"))
  mypalette <- c(mypalette, brewer.pal(6, "YlOrBr"))
  breaks <- seq(-3, 3,0.5 )
  
  start <- 0#1165
  count_coexpr_bind <- 0
  count_anticoexpr_bind <- 0
  countGO_sig <- 0
  limit <- length(files)#20 #20#200#
  
  print(length(files))
  finaldimmax <- c(0,0)
  nosigTF <- 0
  nosigTF_labels <- c()
  for(i in start:limit) {#40){ #l
    if(length(grep("_TFbindcoexpr.txt",files[i])) > 0 ) {
      print(files[i])
      #index <- regexpr("__",files[i], fixed=T)[1]+2
      index <- regexpr(suffix,files[i], fixed=T)[1]+nchar(suffix)
      
      short <- substr(files[i], index, nchar(files[i]))
      label <- substr(short, 1, regexpr("_",short, fixed=T)[1]-1)
      print(short)
      print(label)
      
      #if(label == 229) {
      
      top_TFbind <- NULL
      top_TFbindcoexpr <- NULL
      top_TFbindanticoexpr <- NULL
      top_GO <- NULL
      top_exp <- NULL
      
      
      ##TFbind pvalues
      datamat_TFbindpval <- NULL
      datamat_TFbindpval_top <- NULL
      bind_name <- NULL
      
      TFbindpval_file <- paste(prefix , label,"_TFbind_pval0.001",".txt",sep="")
      print(TFbindpval_file)
      
      try(datamat_TFbindpval <- read.csv(TFbindpval_file,sep="\t", header=F))
      try(datamat_TFbindpval_top <- datamat_TFbindpval[order(datamat_TFbindpval[,4]/datamat_TFbindpval[,5],decreasing=T),])#[1:5,]
      try(bind_name <- names(datamat_TFbindpval_top[,2]))
      
      #for(j in 1:dim(datamat_TFbindpval_top)[1]) {
      #  
      #}
      
      ###TFbindcoexpr            
      TFbindcoexpr_file <- paste(prefix , label,"_TFbindcoexpr",".txt",sep="")
      print(TFbindcoexpr_file)
      
      datamat_TFbindcoexpr_raw <- readLines(TFbindcoexpr_file)
      datamat_TFbindcoexpr_raw <- datamat_TFbindcoexpr_raw[-which(datamat_TFbindcoexpr_raw == "null")]
      datamat_TFbindcoexpr_raw <- datamat_TFbindcoexpr_raw[-which(datamat_TFbindcoexpr_raw == "")]
      if(length(datamat_TFbindcoexpr_raw) > 0) {
        datamat_TFbindcoexpr_split <- sapply(datamat_TFbindcoexpr_raw , function(x) strsplit(x, "_"))    
        len <- length(datamat_TFbindcoexpr_split)
        
        ml <- max(sapply(datamat_TFbindcoexpr_split,length))
        datamat_TFbindcoexpr <- do.call(rbind, lapply(datamat_TFbindcoexpr_split, function(x) `length<-`(unlist(x), ml)))
        row.names(datamat_TFbindcoexpr) <- NULL#seq(1, dim(datamat_TFbindcoexpr)[1], by=1)
        #max_length <- max(sapply(datamat_TFbindcoexpr_split,length))
        #datamat_TFbindcoexpr_split_fill <- sapply(datamat_TFbindcoexpr_split, function(x){
        #  c(x, rep(NA, max_length - length(x)))
        #})
        #datamat_TFbindcoexpr <- matrix(unlist(datamat_TFbindcoexpr_split_fill), nrow=len, ncol=max_length,byrow=T)
        
        top_TFbindcoexpr <- sort(table(unlist(datamat_TFbindcoexpr)), decreasing=T)
      }
      
      
      
      ###TFbindanticoexpr    
      TFbindanticoexpr_file <- paste(prefix , label,"_TFbindanticoexpr",".txt",sep="")
      print(TFbindanticoexpr_file)
      
      datamat_TFbindanticoexpr_raw <- readLines(TFbindanticoexpr_file)
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
      TFbind_file <- paste(prefix , label,"_TFbind",".txt",sep="")
      print(TFbind_file)
      
      datamat_TFbind_raw <- read.csv(TFbind_file, sep="\t",colClasses=c('character'),stringsAsFactors=FALSE,header=T)
      datamat_TFbind_raw <- datamat_TFbind_raw[,1]
      #datamat_TFbind_raw_test <- readLines(TFbind_file)
       
      #rem <- which(datamat_TFbind_raw == "null")
      #if(length(rem) > 0) {
      #  datamat_TFbind_raw <- datamat_TFbind_raw[-rem]
      #}
      
      if(length(datamat_TFbind_raw) > 0) {
        datamat_TFbind_split <- sapply(datamat_TFbind_raw , function(x) strsplit(x, "_"))    
        len <- length(datamat_TFbind_split)
        max_length <- max(sapply(datamat_TFbind_split,length))
        
        datamat_TFbind_split_fill <- sapply(datamat_TFbind_split, function(x){
          c(x, rep(NA, max_length - length(x)))
        })
        
        datamat_TFbind <- matrix(unlist(datamat_TFbind_split_fill), nrow=len, ncol=max_length,byrow=T)
        top_TFbind <- sort(table(unlist(datamat_TFbind)), decreasing=T)
        #if(names(top_TFbind) == "null") {
        #  top_TFbind <- top_TFbind[-1]
        #}
        top_TFbind_5 <- top_TFbind[1:(min(5,length(top_TFbind)))]#top_TFbind#
        #top_TFbind_5 <- top_TFbind[1:length(top_TFbind)]
        bind_name <- names(top_TFbind_5)
      }
      TFsigsplit <- NULL
      #TF significant enrich YEASTRACT
      if(!is.null(datamat_TFbindpval_top)) {
        TFsig <- toupper(summary[as.numeric(label)+1,18])
        if(TFsig != "none") {
          if(length(grep("_", TFsig)) > 0) {
          TFsigsplit <- strsplit(TFsig, "_")
          TFsigsplit <- lapply(TFsigsplit, function(x) substr(x, 1,length(x)+1))
          }
          else {
            TFsigsplit <- TFsig
          }
          
          TFsigsplit <- unlist(TFsigsplit)
          crossindex <- c()
          for(c in 1:length(TFsigsplit)) {          
            matchnow <- match(TFsigsplit[c], toupper(datamat_TFbindpval_top[,2]))
            if(length(matchnow) > 0 && !is.na(matchnow)) {
              crossindex <- c(crossindex, matchnow)
            }
          }
          if(!is.null(crossindex) && length(crossindex) > 0) {
            #crossindex <- match(TFsigsplit, names(top_TFbind_5))
            #print(paste("significant TF?",crossindex,sep=" "))
            if(length(crossindex) > 0) {
              for(c in 1:length(crossindex)) {
                newstr <- paste("*",as.character(datamat_TFbindpval_top[crossindex[c],2]),sep="")
                levels(datamat_TFbindpval_top[,2]) <- c(levels(datamat_TFbindpval_top[,2]),newstr)
                datamat_TFbindpval_top[crossindex[c],2] <- newstr
                #print(paste("significant TF",names(datamat_TFbindpval_top[c,2]),sep=" "))
              }
            }
          } 
        }
      }
      
      ###GO
      GO_file <- paste(prefix , label,"_GO",".txt",sep="")
      print(GO_file)
      
      datain_GO <- readLines(GO_file)     
      datasplit_GO  <- sapply(datain_GO , function(x) strsplit(x, "\t"))
      datamat_GO  <- matrix(unlist(datasplit_GO), ncol = 2, byrow = TRUE)
      datamag_GO <- datamat_GO[order(as.numeric(datamat_GO[,2]), decreasing=T),]
      if(!is.null(dim(datamag_GO))) {
        end <- min(dim(datamag_GO)[1],5)#dim(datamag_GO)[1]# 
        top_GO <- datamag_GO[1:end,]
        #row.names(top_GO) <- names(top_GO)
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
      origGOnames <- names(top_GO)
      GOstrtmp <- toupper(summary[as.numeric(label)+1,14])  
      #print(paste("GOstrtmp ",GOstrtmp))
      if(GOstrtmp != "NONE") {
        GOsig <- substr(GOstrtmp, 5, nchar(GOstrtmp))
        GOsigsplit <- strsplit(GOsig, "_")
        GOsigsplit <- unlist(GOsigsplit)  
        crossindex <- c()
        for(c in 1:length(GOsigsplit)) {          
          matchnow <- match(GOsigsplit[c], toupper(names(top_GO)))
          if(length(matchnow) > 0 && !is.na(matchnow)) {
            crossindex <- c(crossindex, matchnow)
          }
        }
        if(!is.null(crossindex)) {
          #crossindex <- match(GOsigsplit, toupper(names(top_GO)))
          #print(paste("significant GO?",crossindex,sep=" "))
          #if(!is.na(crossindex)) {
          for(c in 1:length(crossindex)) {
            names(top_GO)[c] <- paste("*", names(top_GO[c]),sep="")
            #print(paste("significant GO",c, names(top_GO[c]),sep=" "))
          }
          count_GOsig <- countGO_sig + 1
          #}
        }
      }
      
      
      
      ##Exp
      exp_file <- paste(prefix , label,"_raw_exps.txt",sep="")
      print(exp_file)
      
      datain_exp <- readLines(exp_file)     
      
      datasplit_exp  <- unlist(sapply(datain_exp , function(x) strsplit(x, "\t")))
      datamat_exp  <- matrix(unlist(datasplit_exp), ncol = 2, byrow = TRUE)
      datamag_exp <- datamat_exp[order(as.numeric(datamat_exp[,2]), decreasing=T),]
      
      if(!is.null(dim(datamag_exp))) {
        end <- min(length(datamag_exp[,1]),5)#length(datamag_exp[,1])#
        top_exp <- datamag_exp[1:end,]
        #row.names(top_exp) <- top_exp[,1]
        top_exp[,2] <- as.numeric(top_exp[,2])
        top_exp <- top_exp[,-1]
        top_exp <- as.numeric(top_exp)
        names(top_exp) <- datamag_exp[1:end,1]
      } else {
        end <- 1
        top_exp <- mat.or.vec(1,2)
        top_exp[1,1] <- datamag_exp[1]
        top_exp[1,2] <- datamag_exp[2]
        
        #row.names(top_exp) <- top_exp[,1]
        top_exp[,2] <- as.numeric(top_exp[,2])
        top_exp <- top_exp[,-1]
        top_exp <- as.numeric(top_exp)
        names(top_exp) <- datamag_exp[1]
      }
      
      print(top_exp) 
      
      
      ###RESTRICT TO ONLY TOP 5 labels in each category
      ###MAKE SEPARATE HEATMAP ORDERED BY REGULATION
      
      ##Exp map
      exp_map_file <- paste(prefix , label,"_raw_exps.txt_map.txt",sep="")
      print(exp_map_file)
      
      datain_exp_map <- readLines(exp_map_file)
      expindex <- match(datain_exp_map, datamag_exp)#[1:min(dim(datamag_exp)[1],5)]
      order_exp <- expindex#order(datain_exp_map)
      order_exp[is.na(order_exp)] <- max(order_exp[!is.na(order_exp)] ) + 1
      
      ##GO map
      #if(GO) {
        #yesrowmean <- FALSE
        matchGO <- c()
        if(!is.null(dim(datamag_GO))) {
          matchGO <- datamag_GO[,1]
        } else {
          matchGO <- datamag_GO[1]
        }
        
        GO_map_file <- paste(prefix , label,"_GO.txt_map.txt",sep="")
        print(GO_map_file)
        
        datain_GO_map <- readLines(GO_map_file)     
        datasplit_GO_map  <- sapply(datain_GO_map , function(x) strsplit(x, "_"))
        order_GO_l <- list()
        for(z in 1:length(datasplit_GO_map)) {
          #print(datasplit_GO_map[[z]])
          goindex <- match(datasplit_GO_map[[z]], matchGO)#origGOnames)
          #print(goindex)
          #if(length(goindex) > 0) {
          #order_GO <- c(order_GO, min(goindex))#order(datasplit_GO_map_order)
          #order_GO <- rbind(order_GO, goindex)
          order_GO_l <- append(order_GO_l, list(goindex[!is.na(goindex)]))
          #}
        }
        print(files[i])
        
        ml <- max(lengths(order_GO_l))
        order_GO <- do.call(rbind, lapply(order_GO_l, function(x) `length<-`(unlist(x), ml)))
        
        
        #print(order_GO)
        #maxGO <- max(order_GO[!is.na(order_GO) & is.finite(order_GO)])
        #print(paste("maxGO", i, maxGO))
        #order_GO[!is.finite(order_GO)] <- NA
        #if((sum(is.na(order_GO))  < length(order_GO)) && is.finite(maxGO)) { 
          #maxGO <- NA    
          #order_GO[is.na(order_GO)] <- maxGO + 1
        #} else {
        #  print("reverting to row means")
          #order_GO <- order(rowMeans(data_matrix_imp))
          #yesrowmean <- TRUE
       # }
      #} 
      
      #else {    
        #TF binding map
        #yesrowmean <- FALSE
          TFbindmaster <- sort(table(datamat_TFbind),decreasing=T)
          #if(names(TFbindmaster) == "null") {
          #  TFbindmaster <- TFbindmaster[-1]
          #}
          
          
        order_TF_l <- list()
        order_TF <- c()
        if(!is.null(datamat_TFbindpval_top) && (length(datamat_TFbindpval_top[,2]) - sum(is.na(datamat_TFbindpval_top[,2])) > 0)) {
          order_TF_l <- list()
          HAP5count <- 0
         for(z in 1:dim(datamat_TFbind)[1]) {
           TFindex <- match(datamat_TFbind[z,], names(TFbindmaster))#datamat_TFbindpval_top[!is.na(datamat_TFbindpval_top[,2]),2])
           #print(which(TFindex == 28))
           if(length(which(TFindex == 28)) > 0) {
             HAP5count <- HAP5count + 1
           }
           #print(paste("len", length(TFindex[!is.na(TFindex)]), sep=" "))
           #print(TFindex)
           if(length(TFindex) > 0) {
             order_TF_l <- append(order_TF_l, list(TFindex[!is.na(TFindex)]))#min(TFindex[!is.na(TFindex)]))#order(datasplit_TF_map_order)
           } else {
             print("no match")
           }
           #print(dim(order_TF))
           #print(which(order_TF == 3))
         #maxTF <- max(order_TF[!is.na(order_TF)])
         #order_TF[is.na(order_TF)] <- maxTF+1
         #order_TF[!is.finite(order_TF)] <- maxTF+1
         }
         
         #order_TF <- do.call("rbind",order_TF_l)#sapply (order_TF_l, function (x) {length (x) <- max_length; return (x)})
        
         ml <- max(lengths(order_TF_l))
         order_TF <- do.call(rbind, lapply(order_TF_l, function(x) `length<-`(unlist(x), ml)))
         
         } else {#f(is.null(datamat_TFbindpval_top))  {
          print("reverting to row means")
        #order_GO <- order(rowMeans(data_matrix_imp))
          #yesrowmean <- TRUE
          nosigTF <- nosigTF + 1
          nosigTF_labels <- c(nosigTF_labels, label)
        }
      #}
        
        print(paste("HAP5count",HAP5count))
    
      ###heatmap
      
      read <- paste(prefix , label,"_expdata",".txt",sep="")
      print(paste("reading ", read,sep=""))
      data_matrix <- read.table(read,sep="\t",row.names=1,header=T)     
      datadim <- dim(data_matrix)
      print(paste("dim",dim(data_matrix), sep="\t"))
      data_matrix_imp <- t(apply(data_matrix,1,missfxn))
      impdim <- dim(data_matrix_imp)
      if(impdim[1] == 1) {
        data_matrix_imp <- t(data_matrix_imp)
      }
      #standard ordering by means
      rm <- rowMeans(data_matrix_imp)
      cm <- colMeans(data_matrix_imp)
      data_matrix_imp_meanorder <- data_matrix_imp[order(rm), order(cm)]
      if(impdim[1] == 1) {
        data_matrix_imp_meanorder <- t(data_matrix_imp_meanorder)
      }
      ###threshold bicluster data for viz
      data_matrix_imp[data_matrix_imp > 3] <- 3
      data_matrix_imp[data_matrix_imp < -3] <- -3
      #symnum( cordata <- cor(data_matrix_imp) )
      
      ###assemble sorted data matrix for bicluster
      ###sort rows and columns by Gene and Exp labels
      #data_matrix_imp <- data_matrix_imp[ order_exp, order_GO]
      data_matrix_imp_order <- c()
      exp_labels_index <- sort(unique(order_exp), decreasing=F)
      #print(length(exp_labels_index))
      data_matrix_imp_order_collabels <- c()
      for(m in 1:length(exp_labels_index)) {     
        curind <- which(order_exp == exp_labels_index[m])      
        data_matrix_imp_order_cur <- c()
        #print(dim(data_matrix_imp))
        #print(data_matrix_imp)
        for(n in 1:length(curind)) {
          #print(length(curind))
          #print(curind)
          #print(n)
          #print(curind[n])
          data_matrix_imp_order_cur <- cbind(data_matrix_imp_order_cur, data_matrix_imp[,curind[n]])
        }
        
        data_matrix_imp_order_collabels <- c(data_matrix_imp_order_collabels, names(top_exp)[m])
        data_matrix_imp_order_collabels <- c(data_matrix_imp_order_collabels, rep("", length(curind)-1))
        
        cur_cm <- order(colMeans(data_matrix_imp_order_cur))
        data_matrix_imp_order_cur <- data_matrix_imp_order_cur[,cur_cm]        
        data_matrix_imp_order <- cbind(data_matrix_imp_order, data_matrix_imp_order_cur)
        
        #spacing
        if(m < length(exp_labels_index)) {
          data_matrix_imp_order <- cbind(data_matrix_imp_order, rep(0, datadim[1]),rep(0, datadim[1]),  rep(0, datadim[1]))
          data_matrix_imp_order_collabels <- c(data_matrix_imp_order_collabels, rep("", 3))
        }
      }
      
      
      heatmap_done <- F
      
      data_matrix_imp_orderGO <- c()
      #GO ordering
      newdatadim <- dim(data_matrix_imp_order)
      #if(GO) {
      #  if(!yesrowmean) {
      data_matrix_imp_orderGO <- c()
      go_labels_index <- sort(table(order_GO), decreasing=T)
      data_matrix_imp_orderGO_rowlabels <- c()
      GO_max <- 15
      
      dimorder <- dim(order_GO)
      
      if(dimorder[2] > 0) {
      for(m in 1:GO_max) {
          #print(names(GOmaster)[m]) 
          data_matrix_imp_orderGO_cur <- c()
          countmatch <- 0
          ###loop over all genes in bicluster
          for(a in 1:dimorder[1]) {
            ###find TF matche for that gen
            curind <- which(order_GO[a,] == names(go_labels_index)[m])#table_order_TF_top[mtf])#TF_labels_index[mtf])
            ##if match then add row to this heatmap segment
            if(length(curind) >0 ){
              #print("match")
              data_matrix_imp_orderGO_cur <- rbind(data_matrix_imp_orderGO_cur, data_matrix_imp_order[a,])
              countmatch <- countmatch+1
            }
          }
          
          print(paste("countmatch",countmatch,sep=" "))
          
          #curind <- which(order_GO == go_labels_index[m])
          #data_matrix_imp_orderGO_cur <- c()
          #for(n in 1:length(curind)) {
          #  data_matrix_imp_orderGO_cur <- rbind(data_matrix_imp_orderGO_cur, data_matrix_imp_order[curind[n],])
          #}
          
          data_matrix_imp_orderGO_rowlabels <- c(data_matrix_imp_orderGO_rowlabels, matchGO[as.numeric(names(go_labels_index))[m]])
          data_matrix_imp_orderGO_rowlabels <- c(data_matrix_imp_orderGO_rowlabels, rep("", countmatch-1))
          #reorder by row mean
          cur_rm <- order(rowMeans(data_matrix_imp_orderGO_cur))
          data_matrix_imp_orderGO_cur <- data_matrix_imp_orderGO_cur[cur_rm, ]        
          data_matrix_imp_orderGO <- rbind(data_matrix_imp_orderGO, data_matrix_imp_orderGO_cur)
          
          #spacing
          if(m < GO_max)  {
            print("spacing GO")
            data_matrix_imp_orderGO <- rbind(data_matrix_imp_orderGO,rep(0, newdatadim[2]), rep(0, newdatadim[2]), rep(0, newdatadim[2]))
            data_matrix_imp_orderGO_rowlabels <- c(data_matrix_imp_orderGO_rowlabels, rep("", 3))
          }
        }
      print("GO dims")
      print(length(data_matrix_imp_orderGO_rowlabels))
      print(dim(data_matrix_imp_orderGO))
      
        #} else {
        #  print("reverting to row means")
        #  data_matrix_imp_order2 <- data_matrix_imp_order[order(rowMeans(data_matrix_imp)), ]
        #}
  
      dim2GO <- dim(data_matrix_imp_orderGO)
      if(is.null(dim2GO) || dim2GO[1] == 1) {
        data_matrix_imp_orderGO <- t(data_matrix_imp_orderGO)
      }
      
      row.names(data_matrix_imp_orderGO) <- data_matrix_imp_orderGO_rowlabels
      colnames(data_matrix_imp_orderGO) <- data_matrix_imp_order_collabels
      
      
      outpng <- paste(prefix ,"_",label,"_GO_heat.png",sep="")
      
      png(outpng, width=8, height=11.5, units="in", res=300) 
      pheatmap(data_matrix_imp_orderGO, cluster_rows=F, cluster_cols=F, breaks=breaks,color=mypalette,cellwidth=1,cellheight=1,show_rownames=F,show_colnames=F, legend=F)#dist=cordata,##, mai=c(0,0,0,0)# clustering_distance_cols="euclidean",treeheight_row=0, treeheight_col=0, #clustering_distance_rows="euclidean",  
      dev.off(2);
      
      #pdf
      outpdf <- paste(prefix ,"_",label,"_GO_heat.pdf",sep="")
      pdf(outpdf, width=8, height=11.5, onefile = F)
      pheatmap(data_matrix_imp_orderGO, cluster_rows=F, cluster_cols=F, breaks=breaks,color=mypalette,cellwidth=1,cellheight=1,show_rownames=T,show_colnames=T, legend=F, onefile=FALSE,
               fontsize_col=7, fontsize_row=7)#dist=cordata,##, mai=c(0,0,0,0)# clustering_distance_cols="euclidean",treeheight_row=0, treeheight_col=0, #clustering_distance_rows="euclidean",      
      dev.off(2);
      
      heatmap_done <- T
      }
      
      
      #if(!yesrowmean) {
        #TF ordering
        data_matrix_imp_orderTF <- c()    
        data_matrix_imp_orderTF_rowlabels <- c()
        
        #table_order_TF <- sort(table(order_TF),decreasing=T)
        #table_order_TF_top <- table_order_TF[c(1:5)]
        TFmax <- 15
        ###loop over the top 5 most common TF binding sites for this gene set
        if(!is.null(order_TF)) {
        for(mtf in 1:TFmax) { #length(table_order_TF_top)) {
          
          #print(as.character(datamat_TFbindpval_top[mtf,2]))
          
          print(names(TFbindmaster)[mtf]) 
          dimorder <- dim(order_TF)
          data_matrix_imp_orderTF_cur <- c()
          countmatch <- 0
          ###loop over all genes in bicluster
          for(a in 1:dimorder[1]) {
            ###find TF matche for that gene
            curind <- which(order_TF[a,] == mtf)#table_order_TF_top[mtf])#TF_labels_index[mtf])
            ##if match then add row to this heatmap segment
            if(length(curind) >0 ){
              #print("match")
              data_matrix_imp_orderTF_cur <- rbind(data_matrix_imp_orderTF_cur, data_matrix_imp_order[a,])
              countmatch <- countmatch+1
              }
          }
         
          
          data_matrix_imp_orderTF_rowlabels <- c(data_matrix_imp_orderTF_rowlabels, names(TFbindmaster)[mtf])#as.character(datamat_TFbindpval_top[mtf,2]))
          data_matrix_imp_orderTF_rowlabels <- c(data_matrix_imp_orderTF_rowlabels, rep("", countmatch-1))
          ###reorder by row mean
          cur_rm <- order(rowMeans(data_matrix_imp_orderTF_cur))
          data_matrix_imp_orderTF_cur <- data_matrix_imp_orderTF_cur[cur_rm, ]        
          data_matrix_imp_orderTF <- rbind(data_matrix_imp_orderTF, data_matrix_imp_orderTF_cur)
          
          ###spacing
          if(mtf < TFmax) {
            data_matrix_imp_orderTF <- rbind(data_matrix_imp_orderTF,  rep(0, newdatadim[2]), rep(0, newdatadim[2]), rep(0, newdatadim[2]))
            data_matrix_imp_orderTF_rowlabels <- c(data_matrix_imp_orderTF_rowlabels, rep("", 3))
            }
        }
          
          dim2TF <- dim(data_matrix_imp_orderTF)
          if(is.null(dim2TF) || dim2TF[1] == 1) {
            data_matrix_imp_orderTF <- t(data_matrix_imp_orderTF)
          }  
          
          row.names(data_matrix_imp_orderTF) <- data_matrix_imp_orderTF_rowlabels
          colnames(data_matrix_imp_orderTF) <- data_matrix_imp_order_collabels
          
          outpng <- paste(prefix ,"_",label,"_TF_heat.png",sep="")
          
          png(outpng, width=8, height=11.5, units="in", res=300) 
          pheatmap(data_matrix_imp_orderTF, cluster_rows=F, cluster_cols=F, breaks=breaks,color=mypalette,cellwidth=1,cellheight=1,show_rownames=F,show_colnames=F, legend=F)#dist=cordata,##, mai=c(0,0,0,0)# clustering_distance_cols="euclidean",treeheight_row=0, treeheight_col=0, #clustering_distance_rows="euclidean",  
          dev.off(2);
          
          outpdf <- paste(prefix ,"_",label,"_TF_heat.pdf",sep="")
          pdf(outpdf, width=8, height=11.5, onefile = F)
          pheatmap(data_matrix_imp_orderTF, cluster_rows=F, cluster_cols=F, breaks=breaks,color=mypalette,cellwidth=1,cellheight=1,show_rownames=T,show_colnames=T, legend=F, onefile=FALSE,
                   fontsize_col=7, fontsize_row=7)#dist=cordata,##, mai=c(0,0,0,0)# clustering_distance_cols="euclidean",treeheight_row=0, treeheight_col=0, #clustering_distance_rows="euclidean",      
          dev.off(2);
          
          heatmap_done <- T
        }
        
        
        if(heatmap_done == F) {
          print("generic heat map")
          outpng <- paste(prefix ,"_",label,"_GO_heat.png",sep="")
          
          png(outpng, width=8, height=11.5, units="in", res=300) 
          pheatmap(data_matrix_imp_order, cluster_rows=F, cluster_cols=F, breaks=breaks,color=mypalette,cellwidth=1,cellheight=1,show_rownames=F,show_colnames=F, legend=F)#dist=cordata,##, mai=c(0,0,0,0)# clustering_distance_cols="euclidean",treeheight_row=0, treeheight_col=0, #clustering_distance_rows="euclidean",  
          dev.off(2);
        }
        
  
      pngload <-  readPNG(outpng, TRUE)
      
      outf <- paste(prefix ,"_",label,"_order.pdf",sep="")
      print(outf)
      pdf(outf,width=8, height=11.5, onefile = F)
      par(mfrow=c(1,2))
      
      #make empty plot for image
      plot(0,xaxt='n',yaxt='n',bty='n',pch='',ylab='',xlab='',xlim=c(0,1),ylim=c(0,1))#, mai=c(0,0,0,0)
      rng1 <- par("usr")
      width <- rng1[2] - rng1[1]
      height <- rng1[4] - rng1[3]
      
      #cmonkey = 202, 121
      #MAK(expr) = 508, 516
      finaldim <- dim(data_matrix_imp_orderGO)#data_matrix_imp_orderTF)
      finaldimmax[1] <- max(finaldimmax[1], finaldim[1])
      finaldimmax[2] <- max(finaldimmax[2], finaldim[2])
      
      if(method == "MAK") {
      xratio <- 1#finaldim[2]/(40)# + 100)#202#937
        #if(GO) {
        yratio <- finaldim[1]/(40)#(541)# + 200)##121#618
        #} else {
        #  yratio <- finaldim[1]/(40)#541
        #}
      } else if(method == "cMonkey") { 
      xratio <- finaldim[2]/(202+150)# + 100)##
      yratio <- finaldim[1]/(121+200)# + 200)##
      } else if(method == "COALESCE") { 
        xratio <- finaldim[2]/(99+100)# + 100)##
        yratio <- finaldim[1]/(90+100)# + 200)##
      }
      
      xlen <- width * xratio
      ylen <- height * yratio
          
      #PostScriptTrace(outps)
      
      print(paste("finaldim", finaldim))
      print(paste("rng1", rng1))
      print(paste("xlen", xlen))
      print(paste("ylen", ylen))
      print(paste("rng1[1] + xlen",  rng1[1] + xlen))
      print(paste("rng1[3] + ylen", rng1[3] + ylen))
      
      try(rasterImage(pngload,rng1[1], rng1[3], rng1[1] + xlen, rng1[3] + ylen, mai=c(0,0,0,0),interpolate=F))#0.568  0.000 15.500  1.000# 0.6, -1.080, 1.4, 1)#                
      
      #print("1")
      coexpr_bind <- intersect(bind_name, names(top_TFbindcoexpr[1:(min(5,length(top_TFbindcoexpr)))]))
      #print("2")
      anticoexpr_bind <- intersect(bind_name, names(top_TFbindanticoexpr[1:(min(5,length(top_TFbindanticoexpr)))]))
      #print("3")
      coexpr_bind_ind <- which(bind_name == coexpr_bind)
      print("bind_name")
      print(bind_name)
      print("coexpr_bind")
      print(coexpr_bind)
      #print("4")
      anticoexpr_bind_ind <- which(bind_name == anticoexpr_bind)
      #print("5")    
      
      #color according to coexpression
      defcols <- rep("black", length(top_TFbind_5))    
      cols <- defcols
      print(paste("coexpr_bind_ind",coexpr_bind_ind))
      if(length(coexpr_bind_ind) >0) {
        cols[coexpr_bind_ind] <- "red"
      }
      if(length(anticoexpr_bind_ind) >0) {
        cols[anticoexpr_bind_ind] <- "blue"
      }
      cols <- c(cols, defcols,defcols)
      #print(cols)
      
      alllabels <- c()
      #levels(datamat_TFbindpval_top[,2]) <- c( levels(datamat_TFbindpval_top[,2]) , "")
      #datamat_TFbindpval_top[is.na(datamat_TFbindpval_top[,2]), 2] <- ""
      try(alllabels <- paste(datamat_TFbindpval_top[,2][1:5],round(datamat_TFbindpval_top[,4]/datamat_TFbindpval_top[,5], 2)[1:5],sep=" "))
      while(length(alllabels) < 5) {
        alllabels <- c(alllabels, "")
      }
      alllabels <- c(alllabels, paste(names(top_GO)[1:5],round(top_GO/datadim[1], 2)[1:5],sep=" "))
      alllabels <- c(alllabels, paste(names(top_exp)[1:5],round(top_exp/datadim[2], 2)[1:5],sep=" "))
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
      
      
      tryCatch(
        {
          image(1:length(alllabels),1:1,as.matrix(as.numeric(m[,1])),col=c("white"), axes=FALSE, xlab="",ylab="",main="",ylim=c(0,1))#xlim=c(0,1), 
      
      allratio <- row(m)/length(row(m))
      #print("TF")
      #print(allratio)
      rng <- par("usr")
      heights <- rev(allratio - allratio[1]/2)
      par(lheight=.3) 
      #print(alllabels)
      text(1,heights, labels=alllabels, col=cols,adj=c(0,0))
      lines(c(rng[1],rng[2]), c(heights[5], heights[5])- allratio[1]/2,col="gray")
      lines(c(rng[1],rng[2]), c(heights[10], heights[10])- allratio[1]/2,col="gray")
      text(0.5,0.01,as.numeric(label)+1, adj=c(0,0))
        },
      error=function(cond) {
          message(paste("Here's the original error message for ", files[i]))
          message(cond)
      }
      )
      
      #dev.off()
      dev.off(2);
      dev.off(3);
      dev.off(4);
      dev.off(5);
      
      write.table(alllabels, file= paste(prefix ,"_",label,"_order_alllabels.txt",sep=""))
    }
  }
  #}
  
  print(paste("count_coexpr_bind ", count_coexpr_bind,sep=" "))
  print(paste("count_anticoexpr_bind ", count_anticoexpr_bind,sep=" "))
  print(paste("countGO_sig ", countGO_sig,sep=" "))
  
  print(paste("finaldimmax", finaldimmax))
  
  print(paste("nosigTF", nosigTF))
  
  write.table(nosigTF_labels, file="nosigTF_labels.txt",row.names=F)
  
