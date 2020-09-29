###new
setwd("~/Documents/integr8_genom/Miner/rauf/fake_nulls/")

files <- list.files("./")

for(i in 1:length(files)) {

    #if(length(grep("full",files[i],fixed=T)) > 0) {
        newnull<- read.table(file=files[i],sep="\t",header=FALSE)
        pdf(paste(files[i],".pdf",sep=""),width=8.5,height=11)
        heatmap(as.matrix(newnull), col = cm.colors(255),Rowv=NA, Colv=NA)
        dev.off(2)
    #}    
}




###old
setwd("~/Documents/integr8_genom/Miner/rda/common/")

newnull<- read.table(file="4r_incr_nono_rand_expr35_mean_full.txt",sep="\t",header=FALSE)
newnullsd<- read.table(file="4r_incr_nono_rand_expr35_sd_full.txt",sep="\t",header=FALSE)
#newnullraw<- read.table(file="synthdata090621_expr_4r_incr_nono_rand_null_expr35_mean_-1332417407.txt",sep="\t",header=FALSE)
#newnullrawsd<- read.table(file="synthdata090621_expr_4r_incr_nono_rand_null_expr35_sd_-1332417407.txt",sep="\t",header=FALSE)


orignull<- read.table(file="F621_MSErmean_4rincr1.txt",sep="\t",header=FALSE)
orignullsd<- read.table(file="F621_MSErmean_4rincr1.txt",sep="\t",header=FALSE)

diff <- orignull - newnull
diffsd <- orignullsd - newnullsd

rc <- rainbow(nrow(diff), start=0, end=.3)
cc <- rainbow(ncol(diff), start=0, end=.3)

min <- min(diff)
max <- max(diff)
print(min, 10,10)
print(max, 10,20)

heatmap(as.matrix(diff), col = cm.colors(255),Rowv=NA, Colv=NA)#,RowSideColors = rc, ColSideColors = cc)
heatmap(as.matrix(diffsd), col = cm.colors(255),Rowv=NA, Colv=NA)#,RowSideColors = rc, ColSideColors = cc)

heatmap(as.matrix(orignull), col = cm.colors(255),Rowv=NA, Colv=NA)#,RowSideColors = rc, ColSideColors = cc)
heatmap(as.matrix(newnull), col = cm.colors(255),Rowv=NA, Colv=NA)#,RowSideColors = rc, ColSideColors = cc)
heatmap(as.matrix(newnullraw), col = cm.colors(255),Rowv=NA, Colv=NA)#,RowSideColors = rc, ColSideColors = cc)

heatmap(as.matrix(orignullsd), col = cm.colors(255),Rowv=NA, Colv=NA)#,RowSideColors = rc, ColSideColors = cc)
heatmap(as.matrix(newnullsd), col = cm.colors(255),Rowv=NA, Colv=NA)#,RowSideColors = rc, ColSideColors = cc)
heatmap(as.matrix(newnullrawsd), col = cm.colors(255),Rowv=NA, Colv=NA)#,RowSideColors = rc, ColSideColors = cc)