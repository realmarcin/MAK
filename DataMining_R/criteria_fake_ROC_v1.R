setwd("~/Documents/integr8_genom/Miner/miner_results/eval/")

label <- "nonull_Kendpre_BS"

meanpath <- paste("~/Documents/integr8_genom/Miner/miner_results/eval/faker_eval.txt_",label,"_mean.txt",sep="")
datamean <- as.matrix(read.table(meanpath,sep="\t", header=T))
rown <- datamean[,1]
rownames(datamean) <- rown
datamean<- datamean[,-1]
datamean <- data.matrix(datamean)
datamean[is.nan(datamean)] <- 0
datamean[is.na(datamean)] <- 0

sdpath <- paste("~/Documents/integr8_genom/Miner/miner_results/eval/faker_eval.txt_",label,"_sd.txt",sep="")
datasd <- as.matrix(read.table(sdpath, sep="\t", header=T))
rownames(datasd) <- rown
datasd<- datasd[,-1]
datasd <- data.matrix(datasd)
datasd[is.nan(datasd)] <- 0
datasd[is.na(datasd)] <- 0

uniqset <- unique(rown)
dim <- dim(datamean)

data1 <- c(1:158)
data2 <- c(1:158)
data3 <- c(1:158)
data4 <- c(1:158)

#for all sets (criteria + parameter combos)
for(i in 1:length(uniqset)) {
    curdata1 <- c()
    curdata2 <- c()
    curdata3 <- c()
    curdata4 <- c()
    cat(i,uniqset[i], "\n", " ")
    #find all rows in current set
    for(j in 1:dim[1]) {
        
        ind <- match(rown[j], uniqset[i])
        if(!is.na(ind) && length(rown[j]) == length(uniqset[i])) {
            #cat(i,j,uniqset[i],rown[j], "\n", " ")
            diff1 <- as.numeric(datamean[j,8]) - as.numeric(datamean[j,20])
            diff2 <- as.numeric(datamean[j,9]) - as.numeric(datamean[j,21])
            diff3 <- as.numeric(datamean[j,10]) - as.numeric(datamean[j,22])
            diff4 <- as.numeric(datamean[j,11]) - as.numeric(datamean[j,23])            
            
            curdata1 <- c(curdata1, diff1)
            curdata2 <- c(curdata2, diff2)
            curdata3 <- c(curdata3, diff3)
            curdata4 <- c(curdata4, diff4)
        }
    }
    curlen <- length(curdata1)
    cat("curlen", curlen, "\n", " ")
    #print(length(data1))
    if(curlen > 1) {
        #sort each pair by decreasing mean F1 score
        #print(length(curdata1))
        curdata1 <- sort(curdata1, decreasing=T)
        cat("length(curdata1)",length(curdata1), "\n", " ")
        curdata2 <- sort(curdata2, decreasing=T)
        curdata3 <- sort(curdata3, decreasing=T)
        curdata4 <- sort(curdata4, decreasing=T)
        
        cat("length(data1)", length(data1), "\n", " ")
        if(length(data1) == 0) {
            data1 <- curdata1
            data2 <- curdata2
            data3 <- curdata3
            data4 <- curdata4
            cat("length(data1)", length(data1), "\n", " ")
        }
        else {
            curdim <- dim(data1)
            cat("curdim", curdim, "\n", " ")
                if(is.null(curdim)) {
                    masterlen <- length(data1)
                } else {
                     masterlen <- curdim[1]
                }
            cat(curlen, masterlen,"\n", " ")
            #pad current vector
                if(curlen < masterlen) {
                    add <- rep(0, (masterlen - curlen))
                   curdata1 <- append(curdata1, add, length(curdata1))
                    curdata2 <- append(curdata2, add, length(curdata2))
                     curdata3 <- append(curdata3, add, length(curdata3))
                      curdata4 <- append(curdata4, add, length(curdata4))
                }
            }
            data1 <- cbind(data1, curdata1)
            data2 <- cbind(data2, curdata2)
            data3 <- cbind(data3, curdata3)
            data4 <- cbind(data4, curdata4)
        }
        cat("dim(data1)",dim(data1), "\n", " ")
}

data1 <- data1[,-1]
data2 <- data2[,-1]
data3 <- data3[,-1]
data4 <- data4[,-1]

dimdata <- dim(data1)
cat("dimdata",dimdata,"\n", " ")

colnames(data1) <- uniqset
colnames(data2) <- uniqset
colnames(data3) <- uniqset
colnames(data4) <- uniqset
write.table(data1, file=paste("faker_data1_",label,".txt",sep=""),sep="\t")
write.table(data2, file=paste("faker_data2_",label,".txt",sep=""),sep="\t")
write.table(data3, file=paste("faker_data3_",label,".txt",sep=""),sep="\t")
write.table(data4, file=paste("faker_data4_",label,".txt",sep=""),sep="\t")

    pdf(paste("faker_",label,"_mean_F1improve.pdf",sep=""),width=8,height=11)
    par(mfrow=c(2,2))
    
    xvals <- c(1:dimdata[1])
    plot( xvals,data1[,1], type="l",  ylim=c(-1,1), ylab="F1 - start F1")
    for(i in 2:dimdata[2]) {
        #print(data1[,i])
        #print(length(data1[,i]))
        lines(xvals,data1[,i])
    }
    plot(xvals, data2[,1],   type="l", ylim=c(-1,1), ylab="F1 - start F1")
    for(i in 2:dimdata[2]) {
        lines(xvals,data2[,i]  )
    }
    plot(xvals,data3[,1],   type="l", ylim=c(-1,1), ylab="F1 - start F1")
    for(i in 2:dimdata[2]) {
        lines(xvals,data3[,i])
    }
    plot(xvals,data4[,1],   type="l", ylim=c(-1,1), ylab="F1 - start F1")
    for(i in 2:dimdata[2]) {
        lines(xvals,data4[,i])
    }
    
    dev.off(2)

###creates summary dataset and plot for all criteria
summarydata <- mat.or.vec(0,4)
#for each criterion vs true block/column
for(i in 1:dimdata[2]) {
    cursum <- c(sum(data1[data1[,i] > 0,i]), sum(data2[data2[,i] > 0,i]), sum(data3[data3[,i] > 0,i]), sum(data4[data4[,i] > 0,i]))
    summarydata <- rbind(summarydata, cursum)
}
rmean <- rowMeans(summarydata)
summarydata <- cbind(summarydata, rmean)

summarydata <- summarydata[order(summarydata[,5],decreasing=T),]
row.names(summarydata) <- uniqset
colnames(summarydata) <- c("true1","true2","true3","true4","mean")

write.table(summarydata,paste(label,"_summary.txt",sep=""),sep="\t")

pdf(paste("faker_",label,"_mean_summary.pdf",sep=""),width=8,height=11)
matplot(c(1:dim(summarydata)[1]), summarydata,type="l",ylab="F1 improve area", xlab="criterion", ylim=c(0,30), col=c("gray25","gray50","gray75","black","black"), lty=c("solid","solid","solid","solid","dashed"))
dev.off(2)




###
### Fake ROC plot
###
sensdata1 <- c()
specdata1 <- c()
sensdata2 <- c()
specdata2 <- c()
sensdata3 <- c()
specdata3 <- c()
sensdata4 <- c()
specdata4 <- c()

#for all sets (criteria + parameter combos)
for(i in 1:length(uniqset)) {
    print(uniqset[i])
    curdata1 <- c()
    curdata2 <- c()
    curdata3 <- c()
    curdata4 <- c()
    #find all rows in current set
    for(j in 1:dim[1]) {
        #print(rown[j])        
        #print(uniqset[i])
        ind <- match(rown[j], uniqset[i])
        if(!is.na(ind)) {
            #print(datamean[j,21])
            curdata1 <- rbind(curdata1, c(as.numeric(datamean[j,12]),1 - as.numeric(datamean[j,16])))
            curdata2 <- rbind(curdata2, c(as.numeric(datamean[j,13]),1 - as.numeric(datamean[j,17])))
            curdata3 <- rbind(curdata3, c(as.numeric(datamean[j,14]),1 - as.numeric(datamean[j,18])))
            curdata4 <- rbind(curdata4, c(as.numeric(datamean[j,15]),1 - as.numeric(datamean[j,19])))
        }
    }
    curdim <- dim(curdata1)
    print(curdim)
    
    if(curdim[1] > 1) {
        #sort each pair by increasing specificity
        curdata1 <- curdata1[sort.list(curdata1[,2], decreasing=F), ]
        curdata2 <- curdata2[sort.list(curdata2[,2], decreasing=F), ]
        curdata3 <- curdata3[sort.list(curdata3[,2], decreasing=F), ]
        curdata4 <- curdata4[sort.list(curdata4[,2], decreasing=F), ]    
        
        sensdata1 <- cbind(sensdata1, curdata1[,1])
        specdata1 <- cbind(specdata1, curdata1[,2])
        sensdata2 <- cbind(sensdata2, curdata2[,1])
        specdata2 <- cbind(specdata2, curdata2[,2])
        sensdata3 <- cbind(sensdata3, curdata3[,1])
        specdata3 <- cbind(specdata3, curdata3[,2])
        sensdata4 <- cbind(sensdata4, curdata4[,1])
        specdata4 <- cbind(specdata4, curdata4[,2])
    }
}

print(dim(sensdata1))
print(dim(specdata1))

dimsens <- dim(sensdata1)

    pdf("faker_mean_ROC.pdf",width=8,height=11)
    par(mfrow=c(2,2))
    
    print(max(sensdata1[,1]))
    print(min(specdata1[,1]))
    plot( specdata1[,1],sensdata1[,1], type="l", xlim=c(0,.1), ylim=c(0,1))
    for(i in 2:dim(specdata1)[2]) {
        print(cat(max(sensdata1[,i]),min(specdata1[,i]),sep=" "))
        #points(sensdata1[,i], specdata1[,i],)
        lines(specdata1[,i],sensdata1[,i])
    }
    plot(specdata2[,1], sensdata2[,1],   type="l", xlim=c(0,.1), ylim=c(0,1))
    for(i in 2:dim(sensdata2)[2]) {
        #points(sensdata2[,i], specdata2[,i],)
        lines(specdata2[,i],sensdata2[,i]  )
    }
    plot(specdata3[,1],sensdata3[,1],   type="l", xlim=c(0,.1), ylim=c(0,1))
    for(i in 2:dim(sensdata3)[2]) {
        #points(sensdata3[,i], specdata3[,i],)
        lines(specdata3[,i],sensdata3[,i])
    }
    plot(specdata4[,1],sensdata4[,1],   type="l", xlim=c(0,.1), ylim=c(0,1))
    for(i in 2:dim(sensdata4)[2]) {
        #points(sensdata4[,i], specdata4[,i],)
        lines(specdata4[,i],sensdata4[,i])
    }
    
    dev.off(2)
    