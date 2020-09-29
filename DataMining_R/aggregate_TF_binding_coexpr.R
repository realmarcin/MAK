
setwd("~/Documents/integr8_genom/Miner/miner_results/yeast/EXPR_round12345_merge_refine/netmod87/")


###
###data heatmaps
files <- list.files("./")


top_TFbindcoexpr <- mat.or.vec(1,1)
for(i in 1:length(files)) {
  
    if(length(grep("_TFbindcoexpr",files[i])) > 0 ) {
        print(files[i])
        datain <- readLines(files[i])#,sep="_",header=F,fill=T)
        
        dataintrim <- datain[-which(datain == "null")]
        #dataintrim <- dataintrim[-which(dataintrim == "")]
        datasplit <- sapply(dataintrim , function(x) strsplit(x, "_"))
        datamat <- matrix(unlist(datasplit), ncol = 50, byrow = TRUE)
        top <- sort(table(datamat), decreasing=T)
        print(top)
        top_TFbindcoexpr <- c(top_TFbindcoexpr, top[1])
    }
}
top_TFbindcoexpr <- top_TFbindcoexpr[-1]


top_TFbind <- mat.or.vec(1,1)
for(i in 1:length(files)) {
  
    if(length(grep("_TFbind",files[i])) > 0 ) {
        print(files[i])
        datain <- readLines(files[i])#,sep="_",header=F,fill=T)
        
        dataintrim <- datain[-which(datain == "null")]
        #dataintrim <- dataintrim[-which(dataintrim == "")]
        datasplit <- sapply(dataintrim , function(x) strsplit(x, "_"))
        datamat <- matrix(unlist(datasplit), ncol = 50, byrow = TRUE)
        print(sort(table(datamat), decreasing=T))
         top <- sort(table(datamat), decreasing=T)
        print(top)
        top_TFbind <- c(top_TFbind, top[1])
    }
}
top_TFbind <- top_TFbind[-1]

