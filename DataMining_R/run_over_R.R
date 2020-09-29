library("Hmisc")

harmonicmean=function(a, b) { 2 * (a*b)/(a+b)}

axis <- "r"
firstlabel <- paste("fake",axis,"_","over","_",sep="")
orient <- toupper(axis)

inpath <- paste("./",sep="")

setwd(inpath)

numtrue <- 4

extdata <- read.table(paste("./",firstlabel,"eval_extmax.txt",sep=""),sep="\t",header=T)
rownames(extdata) <- extdata[,1]
extdata<- extdata[,-1]
extdata <- data.matrix(extdata)
extdatasensspec <- cbind(harmonicmean(extdata[,5],extdata[,9]),harmonicmean(extdata[,6],extdata[,10]),harmonicmean(extdata[,7],extdata[,11]),harmonicmean(extdata[,8],extdata[,12]))
extrowmean <- rowMeans(extdatasensspec)
extdatasensspec <- cbind(extdatasensspec, extrowmean)
extdatasensspec <- extdatasensspec[order(extdatasensspec[,1],decreasing=T),]
extdim <- dim(extdatasensspec)

extdataprecrec <- cbind(harmonicmean(extdata[,13],extdata[,17]),harmonicmean(extdata[,14],extdata[,18]),harmonicmean(extdata[,15],extdata[,19]),harmonicmean(extdata[,16],extdata[,20]))
extrowmeanpr <- rowMeans(extdataprecrec)
extdatasensspec <- cbind(extdataprecrec, extrowmean)
extdataprecrec <- extdataprecrec[order(extdataprecrec[,1],decreasing=T),]
extdimpr <- dim(extdataprecrec)

#alllabels <- c("MSEpre_PPI_BS","MSEpre_PPI_bslc","Kendpre_bslc","MSEKendpre_bs", "Kendpre_BS","Kendpre_Kendnonull_bslc","MSEpre_BS","MSEpre_Kendnonull_BS","MSEpre_bslc","nonull_Kendpre_BS","nonull_MSEpre_BS","nonull_MSEpre_bslc")
alllabels <- c("")

for(a in 1:length(alllabels)) {
label <- alllabels[a]

meanpath <- paste(firstlabel,"eval_mean.txt",sep="")
datamean <- as.matrix(read.table(meanpath,sep="\t", header=T))
rown <- datamean[,1]
rownames(datamean) <- rown
datamean<- datamean[,-1]
datamean <- data.matrix(datamean)
datamean[is.nan(datamean)] <- 0
datamean[is.na(datamean)] <- 0

if(length(grep("PPI", label)) != 0) {
    datamean_topcrit_geere <- datamean[which(row.names(datamean) == paste("GEE",orient,"E_PPI",sep="")), ]
    fastindex <- which(row.names(datamean) == paste("MSE",orient,"_COR",orient,"_PPI",sep=""))
    if(length(fastindex)== 0) {
        fastindex <- which(row.names(datamean) == paste("MSE",orient,"_PPI",sep=""))
    }
    datamean_topcrit_mserkendall <- datamean[fastindex, ]
}
else {
    datamean_topcrit_geere <- datamean[which(row.names(datamean) == paste("GEE",orient,"E",sep="")), ]
    datamean_topcrit_mserkendall <- datamean[which(row.names(datamean) == paste("MSE",orient,"_COR",orient,sep="")), ]
}

sdpath <- paste(firstlabel, "eval_sd.txt",sep="")
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

collabels <- c()
#for all sets (criteria + parameter combos)
for(i in 1:length(uniqset)) {
    curdata1 <- c()
    curdata2 <- c()
    curdata3 <- c()
    curdata4 <- c()
    cat(i,uniqset[i], "\n", " ")
    #find all rows in current set matching crit
    for(j in 1:dim[1]) {
        
        ind <- match(rown[j], uniqset[i])
        if(!is.na(ind) && length(rown[j]) == length(uniqset[i])) {
            #cat(i,j,uniqset[i],rown[j], "\n", " ")
            diff1 <- as.numeric(datamean[j,8]) - as.numeric(datamean[j,20])
            diff2 <- as.numeric(datamean[j,9]) - as.numeric(datamean[j,21])
            diff3 <- as.numeric(datamean[j,10]) - as.numeric(datamean[j,22])
            diff4 <- as.numeric(datamean[j,11]) - as.numeric(datamean[j,23])
            
            #crit <- as.numeric(datamean[j,2])
            
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
        if(length(collabels) ==0) {
            collabels <- c(collabels, uniqset[i])
        }else {
            collabels <- c(collabels, uniqset[i])
        }
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
cat("collabels",length(collabels),"\n", " ")

colnames(data1) <- collabels
colnames(data2) <- collabels
colnames(data3) <- collabels
colnames(data4) <- collabels
write.table(data1, file=paste("./",firstlabel,"data1.txt",sep=""),sep="\t")
write.table(data2, file=paste("./",firstlabel,"data2.txt",sep=""),sep="\t")
write.table(data3, file=paste("./",firstlabel,"data3.txt",sep=""),sep="\t")
write.table(data4, file=paste("./",firstlabel,"data4.txt",sep=""),sep="\t")

    pdf(paste("./",firstlabel,"mean_F1improve.pdf",sep=""),width=8,height=11)
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
row.names(summarydata) <- collabels
colnames(summarydata) <- c("true1","true2","true3","true4","mean")
order5 <- order(summarydata[,5],decreasing=T)
summarydata <- summarydata[order5,]
#row.names(summarydata) <- row.names(summarydata)[order5]

write.table(summarydata,paste("./",firstlabel,"summary.txt",sep=""),sep="\t")

pdf(paste("./",firstlabel,"mean_summary.pdf",sep=""),width=8,height=11)
matplot(c(1:dim(summarydata)[1]), summarydata,type="l",ylab="F1 improve area", xlab="Criterion or method", ylim=c(0,30), col=c("gray85","gray70","gray50","black","black"), lty=c("solid","solid","solid","solid","dashed"))
dev.off(2)



###Select max sens+spec for each true block

maxdata <- c(1:4)
maxdataimprove <- c(1:4)

#for all sets (criteria + parameter combos)
for(i in 1:length(collabels)) {
    curdata1 <- 0
    curdata2 <- 0
    curdata3 <- 0
    curdata4 <- 0
    crit1 <- 0
    crit2 <- 0
    crit3 <- 0
    crit4 <- 0
    curdata1improve <- 0
    curdata2improve <- 0
    curdata3improve <- 0
    curdata4improve <- 0
    crit1improve <- 0
    crit2improve <- 0
    crit3improve <- 0
    crit4improve <- 0
    cat(i,collabels[i], "\n", " ")
    #find all rows in current set matching crit
    for(j in 1:dim[1]) {        
        ind <- match(rown[j], collabels[i])
        if(!is.na(ind) ) {#&& length(rown[j]) == length(collabels[i])
            #cat(i,j,collabels[i],rown[j], "\n", " ")

            sum1 <- harmonicmean(as.numeric(datamean[j,12]) , as.numeric(datamean[j,16]))
            sum2 <- harmonicmean(as.numeric(datamean[j,13]) , as.numeric(datamean[j,17]))
            sum3 <- harmonicmean(as.numeric(datamean[j,14]) , as.numeric(datamean[j,18]))
            sum4 <- harmonicmean(as.numeric(datamean[j,15]) , as.numeric(datamean[j,19]))

            sum1improve <- sum1 - harmonicmean(as.numeric(datamean[j,24]) , as.numeric(datamean[j,28]))
            sum2improve <- sum2 - harmonicmean(as.numeric(datamean[j,25]) , as.numeric(datamean[j,29]))
            sum3improve <- sum3 - harmonicmean(as.numeric(datamean[j,26]) , as.numeric(datamean[j,30]))
            sum4improve <- sum4 - harmonicmean(as.numeric(datamean[j,27]) , as.numeric(datamean[j,31]))
            
            if(sum1 > curdata1) {
                curdata1 <- sum1
                crit1 <- as.numeric(datamean[j,2])
            }
            if(sum2 > curdata2) {
                curdata2 <- sum2
                crit2 <- as.numeric(datamean[j,2])
            }
            if(sum3 > curdata3) {
                curdata3 <- sum3
                crit3 <- as.numeric(datamean[j,2])
            }
            if(sum4 > curdata4) {
                curdata4 <- sum4
                crit4 <- as.numeric(datamean[j,2])
            }
            
            if(sum1improve > curdata1improve) {
                curdata1improve <- sum1improve
                crit1improve <- as.numeric(datamean[j,2]) - as.numeric(datamean[j,32])
            }
            if(sum2improve > curdata2improve) {
                curdata2improve <- sum2improve
                crit2improve <- as.numeric(datamean[j,2])- as.numeric(datamean[j,32])
            }
            if(sum3improve > curdata3improve) {
                curdata3improve <- sum3improve
                crit3improve <- as.numeric(datamean[j,2])- as.numeric(datamean[j,32])
            }
            if(sum4improve > curdata4improve) {
                curdata4improve <- sum4improve
                crit4improve <- as.numeric(datamean[j,2])- as.numeric(datamean[j,32])
            }
        }
    }
    maxdata <- rbind(maxdata,c(curdata1, curdata2, curdata3, curdata4, crit1,crit2,crit3,crit4))
    maxdataimprove <- rbind(maxdataimprove ,c(curdata1improve , curdata2improve , curdata3improve , curdata4improve , crit1improve ,crit2improve ,crit3improve ,crit4improve ))
    
    cat("dim(maxdata)",dim(maxdata), "\n", " ")
}
maxdata <- maxdata[-1,]
maxdata[is.infinite(maxdata)] <- 0
rowmeans <- rowMeans(maxdata[,1:4])
maxdata <- cbind(maxdata, rowmeans)
row.names(maxdata) <- collabels
colnames(maxdata) <- c("sensspec1","sensspec2", "sensspec3","sensspec4","crit1","crit2","crit3","crit4","mean")

ordermax <- order(maxdata[,9],decreasing=T)
maxdata <- maxdata[ordermax,]

write.table(maxdata,paste("./",firstlabel,"max.txt",sep=""),sep="\t")

maxdataimprove <- maxdataimprove[-1,]
maxdataimprove[is.infinite(maxdataimprove)] <- 0
rowmeansimprove <- rowMeans(maxdataimprove[,1:4])
maxdataimprove <- cbind(maxdataimprove, rowmeansimprove)
row.names(maxdataimprove) <- collabels
colnames(maxdataimprove) <- c("sensspec1","sensspec2", "sensspec3","sensspec4","crit1","crit2","crit3","crit4","mean")

ordermaximprove <- order(maxdataimprove[,9],decreasing=T)
maxdataimprove <- maxdataimprove[ordermaximprove,]

write.table(maxdataimprove,paste("./",firstlabel,"max_improve.txt",sep=""),sep="\t")

print(paste("maxdata",dim(maxdata),sep=" "))

pdf(paste("./",firstlabel,"mean_maxsensspec.pdf",sep=""),width=8,height=11)
xvals <- c(1:dim(maxdata)[1])
matplot(xvals,maxdata[,c(1:4,9)], type="l",  ylim=c(0,2), ylab="Mean sensitivity + specificity",xlab="criterion", col=c("gray85","gray70","gray50","black","black"), lty=c("solid","solid","solid","solid","dashed"),lwd=c(1,1,1,1,2))
 for(j in 1:extdim[1]) { 
  for(i in 1:(extdim[2]-1)) {  
    points(j*5-4,extdatasensspec[j,i], pch=(j-1), cex=((i-1)*0.5+0.5))
    }
}
dev.off(2)

pdf(paste("./",firstlabel,"mean_maxsensspec_vs_crit.pdf",sep=""),width=8,height=11)
xvals <- c(1:dim(maxdata)[1])
matplot(maxdata[,5:8],maxdata[,1:4], type="p",  xlim=c(0,1), ylim=c(0,2), ylab="Mean sensitivity + specificity",xlab="Mean criterion value", col=c("gray85","gray70","gray50","black"))
 for(j in 1:extdim[1]) { 
  for(i in 1:(extdim[2]-1)) {  
    points(0, extdatasensspec[j,i],pch=(j-1), cex=((i-1)*0.5+0.5))
    }
}
dev.off(2)

print(paste("maxdataimprove",dim(maxdataimprove),sep=" "))
pdf(paste("./",firstlabel,"mean_maxsensspec_improve.pdf",sep=""),width=8,height=11)
xvals <- c(1:dim(maxdataimprove)[1])
matplot(xvals,maxdataimprove[,c(1:4,9)], type="l",  ylim=c(0,2), ylab="Mean sensitivity + specificity improve",xlab="criterion", col=c("gray85","gray70","gray50","black","black"), lty=c("solid","solid","solid","solid","dashed"),lwd=c(1,1,1,1,2))
dev.off(2)

pdf(paste("./",firstlabel,"mean_maxsensspec_vs_crit_improve.pdf",sep=""),width=8,height=11)
xvals <- c(1:dim(maxdataimprove)[1])
matplot(maxdataimprove[,5:8],maxdataimprove[,1:4], type="p",  xlim=c(0,1), ylim=c(0,2), ylab="Mean sensitivity + specificity improve",xlab="Mean criterion value improve", col=c("gray85","gray70","gray50","black"))
dev.off(2)


###Select top 4 and store sens+spec
numtop <- numtrue
topdata <- rep(0,numtrue)
topdataimprove <- rep(0,numtrue)

#for all sets (criteria + parameter combos)
for(i in 1:length(collabels)) {
    cat("collabels ",collabels[i], "\n")
    topROCvals <- mat.or.vec(4,2)
    topsum <- rep(0,4)
    topsumimprove <- rep(0,4)
    topcrit <- rep(0,4)
    topcritimprove <-  rep(0,4)
    indices <- rep(0,4)
    occupied <- rep(0,numtrue)
    
    #find max performance for true blocks 1-4
    #find all rows in current set matching crit
    for(j in 1:dim[1]) {        
        ind <- match(rown[j], collabels[i])
        #if matches current ind
        if(!is.na(ind) ) {
            #if not all 4 true blocks covered yet
                sumcomp <- rep(0,4)
                sumcomp[1] <- harmonicmean(as.numeric(datamean[j,12]) , as.numeric(datamean[j,16]))
                sumcomp[2] <- harmonicmean(as.numeric(datamean[j,13]) , as.numeric(datamean[j,17]))

                sumcomp[3] <- harmonicmean(as.numeric(datamean[j,14]) , as.numeric(datamean[j,18]))
                sumcomp[4] <- harmonicmean(as.numeric(datamean[j,15]) , as.numeric(datamean[j,19]))
                
                rankcomp <- rank(1/sumcomp, ties.method="min")
                            
                uniqrankcomp <- sort(unique(rankcomp))
                #for all ranks
                for(a in 1:length(uniqrankcomp)) {
                #a <- 1
                    #find which blocks match the current rank
                    curmaxind <- which(rankcomp == uniqrankcomp[a])
                   
                    if(length(curmaxind) > 1) {
                        cat("curmaxind great than 1: ",curmaxind,"\n")
                    }
                    
                    #for all hits matching the rank
                     for(b in 1:length(curmaxind)) {
                         cat("rankcomp ",a, "uniqrankcomp ", uniqrankcomp[a],"curmax ",curmaxind,"b ", b,curmaxind[b],"rank ",rankcomp,sumcomp,"\n")
                    #if top rank and not assigned yet for true block
                    if(uniqrankcomp[a] == 1 && occupied[curmaxind[b]] == 0 ) {
                        cat("if 1 1st  ",b, "curmaxind ",curmaxind[b], "occupied ", occupied[curmaxind[b]],"\n")
                        thiscurmaxind <- curmaxind[b]
                        occupied[thiscurmaxind] <- 1
                        topsum[thiscurmaxind] <- sumcomp[thiscurmaxind]
                                              
                        offset <- thiscurmaxind-1
                        topROCvals[thiscurmaxind,] <- c(as.numeric(datamean[j,12+offset]) , as.numeric(datamean[j,16+offset])) 
                        topsumimprove[thiscurmaxind] <- harmonicmean(as.numeric(datamean[j,12+offset]) , as.numeric(datamean[j,16+offset])) - harmonicmean(as.numeric(datamean[j,24+offset]) , as.numeric(datamean[j,28+offset]))
                        topcrit[thiscurmaxind] <- as.numeric(datamean[j,2])
                        
                        cat("occupied 1 ",occupied,"*",topsum,"*",topcrit,"\n")
                         
                        topcritimprove[thiscurmaxind] <- as.numeric(datamean[j,2]) - as.numeric(datamean[j,32])
                        indices[thiscurmaxind] <- j
                    }
                    #if top rank -- and criterion value greater than current criterion value
                    else if(uniqrankcomp[a] == 1  && as.numeric(datamean[j,2]) > topcrit[curmaxind[b]]) {
                        cat("if 2 crit ",b,"curmaxind ",curmaxind[b], "occupied ", occupied[curmaxind[b]],"topcrit ", topcrit[curmaxind[b]],"candidate ",datamean[j,2],"\n")
                        thiscurmaxind <- curmaxind[b]
                        occupied[thiscurmaxind] <- 1
                        topsum[thiscurmaxind] <- sumcomp[thiscurmaxind]
                                              
                        offset <- thiscurmaxind-1
                        topROCvals[thiscurmaxind,] <- c(as.numeric(datamean[j,12+offset]) , as.numeric(datamean[j,16+offset])) 
                        topsumimprove[thiscurmaxind] <- harmonicmean(as.numeric(datamean[j,12+offset]) , as.numeric(datamean[j,16+offset])) - harmonicmean(as.numeric(datamean[j,24+offset]) , as.numeric(datamean[j,28+offset]))    
                        topcrit[thiscurmaxind] <- as.numeric(datamean[j,2])
                        
                        cat("occupied 2 ",occupied,"*",topsum,"*",topcrit,"\n")
                         
                        topcritimprove[thiscurmaxind] <- as.numeric(datamean[j,2]) - as.numeric(datamean[j,32])
                        indices[thiscurmaxind] <- j
                    }
                    #if non top rank but not occupied -- and criterion value greater than current criterion value
                    else if(uniqrankcomp[a] == 2 && occupied[curmaxind[b]] == 0  && as.numeric(datamean[j,2]) > topcrit[curmaxind[b]]) {
                        cat("if 3 crit ",b,"curmaxind ",curmaxind[b], "occupied ", occupied[curmaxind[b]],"topcrit ", topcrit[curmaxind[b]],"candidate ",datamean[j,2],"\n")
                        thiscurmaxind <- curmaxind[b]
                        #occupied[thiscurmaxind] <- 1
                        topsum[thiscurmaxind] <- sumcomp[thiscurmaxind]
                                              
                        offset <- thiscurmaxind-1
                        topROCvals[thiscurmaxind,] <- c(as.numeric(datamean[j,12+offset]) , as.numeric(datamean[j,16+offset])) 
                        topsumimprove[thiscurmaxind] <- harmonicmean(as.numeric(datamean[j,12+offset]) , as.numeric(datamean[j,16+offset])) - harmonicmean(as.numeric(datamean[j,24+offset]) , as.numeric(datamean[j,28+offset]))     
                        topcrit[thiscurmaxind] <- as.numeric(datamean[j,2])
                        
                        cat("occupied 3 ",occupied,"*",topsum,"*",topcrit,"\n")
                         
                        topcritimprove[thiscurmaxind] <- as.numeric(datamean[j,2]) - as.numeric(datamean[j,32])
                        indices[thiscurmaxind] <- j
                    }
                }
                }
        }
    }
    
    add <- c(topsum, topcrit, topROCvals[,1], topROCvals[,2], mean(topsum[1:numtrue]), sd(topsum[1:numtrue]), mean(1-topROCvals[,2]), sd(1-topROCvals[,2]))
    
    
    print(length(add))
    print(paste("add",add,sep=" "))
    print(paste("topcrit",topcrit,sep=" "))
    print(paste("sumcomp",sumcomp,sep=" "))
    print(paste("indices",indices,sep=" "))
    topdata <- rbind(topdata,add)
    print(paste("dim(topdata)",dim(topdata),sep=" "))
    topdataimprove <- rbind(topdataimprove ,c(topsumimprove , topcritimprove))
}

topdata <- topdata[-1,]
topdata[is.infinite(topdata)] <- 0
#rowmeans <- rowMeans(topdata[,1:numtrue])
#topdata <- cbind(topdata, rowmeans)

row.names(topdata) <- collabels
colnames(topdata) <- c("sensspec1","sensspec2", "sensspec3","sensspec4","crit1","crit2","crit3","crit4","sens1","sens2","sens3","sens4","spec1","spec2","spec3","spec4", "meanF1","SDF1", "meanFP", "SDFP")

meanFP <- 1- apply(topdata[,13:(13+numtrue-1)], 1, mean)
#ordermax <- order(meanFP,decreasing=F)
ordermax <- order(topdata[,17],decreasing=T)
topdata <- topdata[ordermax,]

write.table(topdata,paste("./",firstlabel,"top.txt",sep=""),sep="\t")

topdataimprove <- topdataimprove[-1,]
topdataimprove[is.infinite(topdataimprove)] <- 0
rowmeansimprove <- rowMeans(topdataimprove[,1:numtrue])
topdataimprove <- cbind(topdataimprove, rowmeansimprove)
row.names(topdataimprove) <- collabels
colnames(topdataimprove) <- c("sensspec1","sensspec2", "sensspec3","sensspec4","crit1","crit2","crit3","crit4","mean")

ordermaximprove <- order(topdataimprove[,9],decreasing=T)
topdataimprove <- topdataimprove[ordermaximprove,]

write.table(topdataimprove,paste("./",firstlabel,"top_improve.txt",sep=""),sep="\t")

print(paste("dim(topdata)",dim(topdata),sep=" "))

pdf(paste("./",firstlabel,"mean_maxsensspec_top4.pdf",sep=""),width=8,height=11)
xvals <- c(1:dim(topdata)[1])
matplot(xvals,topdata[,c(1:4,9)], type="l",  ylim=c(0,2), ylab="Sensitivity + specificity",xlab="Criterion or method", col=c("gray85","gray70","gray50","black","black"), lty=c("solid","solid","solid","solid","dashed"),lwd=c(1,1,1,1,2))
 for(j in 1:extdim[1]) { 
  for(i in 1:(extdim[2]-1)) {  
    points(j*5-4,extdatasensspec[j,i], pch=(j-1), cex=((i-1)*0.5+0.5))
    }
}
dev.off(2)

pdf(paste("./",firstlabel,"mean_maxsensspec_top4_final.pdf",sep=""),width=8,height=11)
xvals <- c(1:dim(topdata)[1])
maxdata <- apply(topdata[,c(1:numtrue)], 1, max)
mindata <- apply(topdata[,c(1:numtrue)], 1, min)
#plotdata <- cbind(maxdata, mindata, topdata[,9])
matplot(xvals,topdata[,17], type="l",  ylim=c(0,2), col=c("gray70","gray85","black"), lty=c("solid","solid","solid"),lwd=c(1,1,1))
points(xvals, topdata[,17])
#errbar(xvals, topdata[,9], maxdata, mindata, ylim=c(0,2))
Js <- 0
means <- 0
maxs <- 0
mins <- 0
 for(j in 1:extdim[1]) { 
  #for(i in 1:(extdim[2]-1)) {  
    points(j*5-4,mean(extdatasensspec[j,]), pch=(j-1), cex=2)
    Js <- c(Js, j*5-4 + 0.5)
    means <- c(means, mean(extdatasensspec[j,1:numtrue]))
    maxs <- c(maxs, max(extdatasensspec[j,1:numtrue]))
    mins <- c(mins, min(extdatasensspec[j,1:numtrue]))
    #points(j*5-4,min(extdatasensspec[j,]), pch=(j-1), cex=1)
    #points(j*5-4,max(extdatasensspec[j,]), pch=(j-1), cex=1)
    #}
}
Js <- Js[-1]
means <- means[-1]
maxs<- maxs[-1]
mins<- mins[-1]
errbar(c(xvals,Js),c(topdata[,17],means), xaxt="n",c(maxdata,maxs), c(mindata,mins),ylim=c(0,2), lwd=0.5,ylab="Sensitivity + specificity",xlab="Criterion or method",col=c(rep("black",length(maxdata)),rep("red", 4*2)),cex=c(rep(1, length(maxdata)),rep(1.25, 8)), pch=c(rep(0,length(maxdata)),c(1:(extdim[1]))))
legend(2,0.75,legend=c("AK-clust",row.names(extdatasensspec)),pch=c(0:(extdim[1])))
dev.off(2)


pdf(paste("./",firstlabel,"mean_maxsensspec_top4_final_ROC.pdf",sep=""),width=8,height=11)
par(mfrow=c(1,2))
meanTP <- apply(extdata[,5:(5+numtrue-1)], 1, mean)
sdTP <- apply(extdata[,5:(5+numtrue-1)], 1, sd)
meanFP <- 1 - apply(extdata[,9:(9+numtrue-1)], 1, mean)
sdFP <- apply(extdata[,9:(9+numtrue-1)], 1, sd)

meanTP <- c(mean(topdata[1,9:(9+numtrue-1)]),mean(topdata[2,9:(9+numtrue-1)]),mean(topdata[3,9:(9+numtrue-1)]), meanTP)
meanFP <- c(1- mean(topdata[1,13:(13+numtrue-1)]), 1- mean(topdata[2,13:(13+numtrue-1)]),1- mean(topdata[3,13:(13+numtrue-1)]),meanFP)
sdTP <- c(sd(topdata[1,9:(9+numtrue-1)]), sd(topdata[2,9:(9+numtrue-1)]), sd(topdata[3,9:(9+numtrue-1)]), sdTP)
sdFP <- c(sd(topdata[1,13:(13+numtrue-1)]), sd(topdata[2,13:(13+numtrue-1)]), sd(topdata[3,13:(13+numtrue-1)]), sdFP)

lnames <- c(row.names(topdata)[1],row.names(topdata)[2], row.names(topdata)[3],  rownames(extdata))
plot(meanFP,meanTP,pch=c(0,0,0,1:(extdim[1])), xlab="FP rate",ylab="TP rate", xlim=c(0,0.3), ylim=c(0,1), cex=c(2,1.5,1,rep(1, extdim[1])))

for(i in 1:length(meanTP)) {
a <- sdFP[i]
b <- sdTP[i]
theta <- seq(0, 2 * pi, length=1000)
x <- meanFP[i] + a * cos(theta)
y <- meanTP[i]  + b * sin(theta)
lines(x, y, type = "l")
}

legend(0.1,0.4,lnames,pch=c(0,0,0,1:(extdim[1])), pt.cex=c(2,1.5,1,rep(1, extdim[1])))

plot(meanFP,meanTP,pch=c(0,0,0,1:(extdim[1])), xlab="FP rate",ylab="TP rate", xlim=c(0,0.05), ylim=c(0,1), cex=c(2,1.5,1,rep(1, extdim[1])))

for(i in 1:length(meanTP)) {
a <- sdFP[i]
b <- sdTP[i]
theta <- seq(0, 2 * pi, length=1000)
x <- meanFP[i] + a * cos(theta)
y <- meanTP[i]  + b * sin(theta)
lines(x, y, type = "l")
}

#library(tripack)
#circles(meanFP,meanTP,(sdFP+sdFP)/2)
legend(0.1,0.4,lnames,pch=c(0,0,0,1:(extdim[1])), pt.cex=c(2,1.5,1,rep(1, extdim[1])))

dev.off(2)



pdf(paste("./",firstlabel,"mean_maxsensspec_vs_crit_top4.pdf",sep=""),width=8,height=11)
xvals <- c(1:dim(topdata)[1])
matplot(topdata[,5:8],topdata[,1:4], type="p",  xlim=c(0,1), ylim=c(0,2), ylab="Mean sensitivity + specificity",xlab="Mean criterion value", col=c("gray85","gray70","gray50","black"))
 for(j in 1:extdim[1]) { 
  for(i in 1:(extdim[2]-1)) {  
    points(0, extdatasensspec[j,i],pch=(j-1), cex=((i-1)*0.5+0.5))
    }
}
dev.off(2)

print(paste("dim(topdataimprove)",dim(topdataimprove),sep=" "))
pdf(paste("./",firstlabel,"mean_maxsensspec_improve_top4.pdf",sep=""),width=8,height=11)
xvals <- c(1:dim(topdataimprove)[1])
matplot(xvals,topdataimprove[,c(1:4,9)], type="l",  ylim=c(0,2), ylab="Mean sensitivity + specificity improve",xlab="Criterion or method", col=c("gray85","gray70","gray50","black","black"), lty=c("solid","solid","solid","solid","dashed"),lwd=c(1,1,1,1,2))
dev.off(2)

print("1")
pdf(paste("./",firstlabel,"mean_maxsensspec_vs_crit_improve_top4.pdf",sep=""),width=8,height=11)
xvals <- c(1:dim(topdataimprove)[1])
matplot(topdataimprove[,5:8],topdataimprove[,1:4], type="p",  xlim=c(0,1), ylim=c(0,2), ylab="Mean sensitivity + specificity improve",xlab="Mean criterion value improve", col=c("gray85","gray70","gray50","black"))
dev.off(2)

print("2")
###point ROC plots
curcol <- rgb(col2rgb("black")[1],col2rgb("black")[2],col2rgb("black")[3],40,maxColorValue=255)

pdf(paste("./",firstlabel,"mean_ROC_points.pdf",sep=""),width=8,height=11)
plot((1-as.numeric(datamean[,16])),(as.numeric(datamean[,12])),cex=(as.numeric(datamean[,2])*2),pch='1',xlim=c(0,0.05),ylim=c(0,1), xlab="FP rate",ylab="TP rate", col=curcol, cex.axis=2,cex.lab=2)
points((1-as.numeric(datamean[,17])),(as.numeric(datamean[,13])),cex=(as.numeric(datamean[,2])*2),pch='2', col=curcol)
points((1-as.numeric(datamean[,18])),(as.numeric(datamean[,14])),cex=(as.numeric(datamean[,2])*2),pch='3', col=curcol)
points((1-as.numeric(datamean[,19])),(as.numeric(datamean[,15])),cex=(as.numeric(datamean[,2])*2),pch='4', col=curcol)
dev.off(2)

print("3")

pdf(paste("./",firstlabel,"mean_ROC_points_improve.pdf",sep=""),width=8,height=11)
plot((1-as.numeric(datamean[,16]) - (1- as.numeric(datamean[,24]))),(as.numeric(datamean[,12])-as.numeric(datamean[,28])),cex=((as.numeric(datamean[,2])-as.numeric(datamean[,32]))*4),pch='1',xlim=c(0,0.05),ylim=c(0,1), xlab="FP rate",ylab="TP rate", col=curcol, cex.axis=2,cex.lab=2)
points((1-as.numeric(datamean[,17])- (1- as.numeric(datamean[,25]))),(as.numeric(datamean[,13])-as.numeric(datamean[,29])),cex=((as.numeric(datamean[,2])-as.numeric(datamean[,32]))*4),pch='2', col=curcol)
points((1-as.numeric(datamean[,18])- (1- as.numeric(datamean[,26]))),(as.numeric(datamean[,14])-as.numeric(datamean[,30])),cex=((as.numeric(datamean[,2])-as.numeric(datamean[,32]))*4),pch='3', col=curcol)
points((1-as.numeric(datamean[,19])- (1- as.numeric(datamean[,27]))),(as.numeric(datamean[,15])-as.numeric(datamean[,31])),cex=((as.numeric(datamean[,2])-as.numeric(datamean[,32]))*4),pch='4', col=curcol)
dev.off(2)

#print("4")

###top no null crit MSER_Kendall
#pdf(paste("./",firstlabel,"mean_ROC_points_topcrit_GEE",orient,"E.pdf",sep=""),width=8,height=11)
#plot((1-as.numeric(datamean_topcrit_geere[,16])),(as.numeric(datamean_topcrit_geere[,12])),cex=(as.numeric(datamean_topcrit_geere[,2])*2),pch='1',xlim=c(0,0.05),ylim=c(0,1), xlab="FP rate",ylab="TP rate", col=curcol, cex.axis=2,cex.lab=2)
#points((1-as.numeric(datamean_topcrit_geere[,17])),(as.numeric(datamean_topcrit_geere[,13])),cex=(as.numeric(datamean_topcrit_geere[,2])*2),pch='2', col=curcol)
#points((1-as.numeric(datamean_topcrit_geere[,18])),(as.numeric(datamean_topcrit_geere[,14])),cex=(as.numeric(datamean_topcrit_geere[,2])*2),pch='3', col=curcol)
#points((1-as.numeric(datamean_topcrit_geere[,19])),(as.numeric(datamean_topcrit_geere[,15])),cex=(as.numeric(datamean_topcrit_geere[,2])*2),pch='4', col=curcol)
#dev.off(2)

#print("5")

#order_datamean_topcrit_geere <- datamean_topcrit_geere[order(datamean_topcrit_geere[,2],decreasing=T),]
#cat("1:10",dim(order_datamean_topcrit_geere),"\n")
#order_datamean_topcrit_geere <- order_datamean_topcrit_geere[1:10,]
#cat("1:10",dim(order_datamean_topcrit_geere),"\n")
#pdf(paste("./",firstlabel,"mean_ROC_points_topcrit_top10_GEE",orient,"E.pdf",sep=""),width=8,height=11)
#plot((1-as.numeric(order_datamean_topcrit_geere[,16])),(as.numeric(order_datamean_topcrit_geere[,12])),cex=(as.numeric(order_datamean_topcrit_geere[,2])*2),pch='1',xlim=c(0,0.05),ylim=c(0,1), xlab="FP rate",ylab="TP rate", col=curcol, cex.axis=2,cex.lab=2)
#points((1-as.numeric(order_datamean_topcrit_geere[,17])),(as.numeric(order_datamean_topcrit_geere[,13])),cex=(as.numeric(order_datamean_topcrit_geere[,2])*2),pch='2', col=curcol)
#points((1-as.numeric(order_datamean_topcrit_geere[,18])),(as.numeric(order_datamean_topcrit_geere[,14])),cex=(as.numeric(order_datamean_topcrit_geere[,2])*2),pch='3', col=curcol)
#points((1-as.numeric(order_datamean_topcrit_geere[,19])),(as.numeric(order_datamean_topcrit_geere[,15])),cex=(as.numeric(order_datamean_topcrit_geere[,2])*2),pch='4', col=curcol)
#dev.off(2)

#print("6")

#pdf(paste("./",firstlabel,"mean_ROC_points_topcrit_MSE",orient,"_COR",orient,".pdf",sep=""),width=8,height=11)
#plot((1-as.numeric(datamean_topcrit_mserkendall[,16])),(as.numeric(datamean_topcrit_mserkendall[,12])),cex=(as.numeric(datamean_topcrit_mserkendall[,2])*2),pch='1',xlim=c(0,0.05),ylim=c(0,1), xlab="FP rate",ylab="TP rate", col=curcol, cex.axis=2,cex.lab=2)
#points((1-as.numeric(datamean_topcrit_mserkendall[,17])),(as.numeric(datamean_topcrit_mserkendall[,13])),cex=(as.numeric(datamean_topcrit_mserkendall[,2])*2),pch='2', col=curcol)
#points((1-as.numeric(datamean_topcrit_mserkendall[,18])),(as.numeric(datamean_topcrit_mserkendall[,14])),cex=(as.numeric(datamean_topcrit_mserkendall[,2])*2),pch='3', col=curcol)
#points((1-as.numeric(datamean_topcrit_mserkendall[,19])),(as.numeric(datamean_topcrit_mserkendall[,15])),cex=(as.numeric(datamean_topcrit_mserkendall[,2])*2),pch='4', col=curcol)
#dev.off(2)

#print("7")

#order_datamean_topcrit_mserkendall <- datamean_topcrit_mserkendall[order(datamean_topcrit_mserkendall[,2],decreasing=T),]
#order_datamean_topcrit_mserkendall <- order_datamean_topcrit_mserkendall[1:10,]
#pdf(paste("./",firstlabel,"mean_ROC_points_topcrit_top10_MSE",orient,"_COR",orient,".pdf",sep=""),width=8,height=11)
#plot((1-as.numeric(order_datamean_topcrit_mserkendall[,16])),(as.numeric(order_datamean_topcrit_mserkendall[,12])),cex=(as.numeric(order_datamean_topcrit_mserkendall[,2])*2),pch='1',xlim=c(0,0.05),ylim=c(0,1), xlab="FP rate",ylab="TP rate", col=curcol, cex.axis=2,cex.lab=2)
#points((1-as.numeric(order_datamean_topcrit_mserkendall[,17])),(as.numeric(order_datamean_topcrit_mserkendall[,13])),cex=(as.numeric(order_datamean_topcrit_mserkendall[,2])*2),pch='2', col=curcol)
#points((1-as.numeric(order_datamean_topcrit_mserkendall[,18])),(as.numeric(order_datamean_topcrit_mserkendall[,14])),cex=(as.numeric(order_datamean_topcrit_mserkendall[,2])*2),pch='3', col=curcol)
#points((1-as.numeric(order_datamean_topcrit_mserkendall[,19])),(as.numeric(order_datamean_topcrit_mserkendall[,15])),cex=(as.numeric(order_datamean_topcrit_mserkendall[,2])*2),pch='4', col=curcol)
#dev.off(2)


###Select top 4 and store precision+recall
numtop <- numtrue
topdata <- rep(0,numtrue)
topdataimprove <- rep(0,numtrue)

#for all sets (criteria + parameter combos)
for(i in 1:length(collabels)) {
    cat("collabels ",collabels[i], "\n")
    topROCvals <- mat.or.vec(4,2)
    topsum <- rep(0,4)
    topsumimprove <- rep(0,4)
    topcrit <- rep(0,4)
    topcritimprove <-  rep(0,4)
    indices <- rep(0,4)
    occupied <- rep(0,numtrue)
    
    #find max performance for true blocks 1-4
    #find all rows in current set matching crit
    for(j in 1:dim[1]) {        
        ind <- match(rown[j], collabels[i])
        #if matches current ind
        if(!is.na(ind) ) {
            #if not all 4 true blocks covered yet
                sumcomp <- rep(0,4)
                sumcomp[1] <- harmonicmean(as.numeric(datamean[j,33]) , as.numeric(datamean[j,37]))
                sumcomp[2] <- harmonicmean(as.numeric(datamean[j,34]) , as.numeric(datamean[j,38]))

                sumcomp[3] <- harmonicmean(as.numeric(datamean[j,35]) , as.numeric(datamean[j,39]))
                sumcomp[4] <- harmonicmean(as.numeric(datamean[j,36]) , as.numeric(datamean[j,40]))
                
                rankcomp <- rank(1/sumcomp, ties.method="min")
                            
                uniqrankcomp <- sort(unique(rankcomp))
                #for all ranks
                for(a in 1:length(uniqrankcomp)) {
                #a <- 1
                    #find which blocks match the current rank
                    curmaxind <- which(rankcomp == uniqrankcomp[a])
                   
                    if(length(curmaxind) > 1) {
                        cat("curmaxind great than 1: ",curmaxind,"\n")
                    }
                    
                    #for all hits matching the rank
                     for(b in 1:length(curmaxind)) {
                         cat("rankcomp ",a, "uniqrankcomp ", uniqrankcomp[a],"curmax ",curmaxind,"b ", b,curmaxind[b],"rank ",rankcomp,sumcomp,"\n")
                    #if top rank and not assigned yet for true block
                    if(uniqrankcomp[a] == 1 && occupied[curmaxind[b]] == 0 ) {
                        cat("if 1 1st  ",b, "curmaxind ",curmaxind[b], "occupied ", occupied[curmaxind[b]],"\n")
                        thiscurmaxind <- curmaxind[b]
                        occupied[thiscurmaxind] <- 1
                        topsum[thiscurmaxind] <- sumcomp[thiscurmaxind]
                                              
                        offset <- thiscurmaxind-1
                        topROCvals[thiscurmaxind,] <- c(as.numeric(datamean[j,33+offset]) , as.numeric(datamean[j,37+offset])) 
                        topsumimprove[thiscurmaxind] <- harmonicmean(as.numeric(datamean[j,33+offset]) , as.numeric(datamean[j,37+offset])) - harmonicmean(as.numeric(datamean[j,41+offset]) , as.numeric(datamean[j,45+offset]))
                        topcrit[thiscurmaxind] <- as.numeric(datamean[j,2])
                        
                        cat("occupied 1 ",occupied,"*",topsum,"*",topcrit,"\n")
                         
                        topcritimprove[thiscurmaxind] <- as.numeric(datamean[j,2]) - as.numeric(datamean[j,32])
                        indices[thiscurmaxind] <- j
                    }
                    #if top rank -- and criterion value greater than current criterion value
                    else if(uniqrankcomp[a] == 1  && as.numeric(datamean[j,2]) > topcrit[curmaxind[b]]) {
                        cat("if 2 crit ",b,"curmaxind ",curmaxind[b], "occupied ", occupied[curmaxind[b]],"topcrit ", topcrit[curmaxind[b]],"candidate ",datamean[j,2],"\n")
                        thiscurmaxind <- curmaxind[b]
                        occupied[thiscurmaxind] <- 1
                        topsum[thiscurmaxind] <- sumcomp[thiscurmaxind]
                                              
                        offset <- thiscurmaxind-1
                        topROCvals[thiscurmaxind,] <- c(as.numeric(datamean[j,33+offset]) , as.numeric(datamean[j,37+offset])) 
                        topsumimprove[thiscurmaxind] <- harmonicmean(as.numeric(datamean[j,33+offset]) , as.numeric(datamean[j,37+offset])) - harmonicmean(as.numeric(datamean[j,41+offset]) , as.numeric(datamean[j,45+offset]))                            
                        topcrit[thiscurmaxind] <- as.numeric(datamean[j,2])
                        
                        cat("occupied 2 ",occupied,"*",topsum,"*",topcrit,"\n")
                         
                        topcritimprove[thiscurmaxind] <- as.numeric(datamean[j,2]) - as.numeric(datamean[j,32])
                        indices[thiscurmaxind] <- j
                    }
                    #if non top rank but not occupied -- and criterion value greater than current criterion value
                    else if(uniqrankcomp[a] == 2 && occupied[curmaxind[b]] == 0  && as.numeric(datamean[j,2]) > topcrit[curmaxind[b]]) {
                        cat("if 3 crit ",b,"curmaxind ",curmaxind[b], "occupied ", occupied[curmaxind[b]],"topcrit ", topcrit[curmaxind[b]],"candidate ",datamean[j,2],"\n")
                        thiscurmaxind <- curmaxind[b]
                        #occupied[thiscurmaxind] <- 1
                        topsum[thiscurmaxind] <- sumcomp[thiscurmaxind]
                                              
                        offset <- thiscurmaxind-1
                        topROCvals[thiscurmaxind,] <- c(as.numeric(datamean[j,33+offset]) , as.numeric(datamean[j,37+offset])) 
                        topsumimprove[thiscurmaxind] <- harmonicmean(as.numeric(datamean[j,33+offset]) , as.numeric(datamean[j,37+offset])) - harmonicmean(as.numeric(datamean[j,41+offset]) , as.numeric(datamean[j,45++offset]))                            
                        topcrit[thiscurmaxind] <- as.numeric(datamean[j,2])
                        
                        cat("occupied 3 ",occupied,"*",topsum,"*",topcrit,"\n")
                         
                        topcritimprove[thiscurmaxind] <- as.numeric(datamean[j,2]) - as.numeric(datamean[j,32])
                        indices[thiscurmaxind] <- j
                    }
                }
                }
        }
    }
    
    add <- c(topsum, topcrit, topROCvals[,1], topROCvals[,2])
    print(length(add))
    print(paste("add",add,sep=" "))
    print(paste("topcrit",topcrit,sep=" "))
    print(paste("sumcomp",sumcomp,sep=" "))
    print(paste("indices",indices,sep=" "))
    topdata <- rbind(topdata,add)
    print(paste("dim(topdata)",dim(topdata),sep=" "))
    topdataimprove <- rbind(topdataimprove ,c(topsumimprove , topcritimprove))
}

topdata <- topdata[-1,]
topdata[is.infinite(topdata)] <- 0
rowmeans <- rowMeans(topdata[,1:numtrue])
topdata <- cbind(topdata, rowmeans)
row.names(topdata) <- collabels
colnames(topdata) <- c("sensspec1","sensspec2", "sensspec3","sensspec4","crit1","crit2","crit3","crit4","sens1","sens2","sens3","sens4","spec1","spec2","spec3","spec4","mean")

meanFP <- 1- apply(topdata[,13:(13+numtrue-1)], 1, mean)
#ordermax <- order(meanFP,decreasing=F)
ordermax <- order(topdata[,17],decreasing=T)
topdata <- topdata[ordermax,]

write.table(topdata,paste("./",firstlabel,"precisionrecall_top.txt",sep=""),sep="\t")

topdataimprove <- topdataimprove[-1,]
topdataimprove[is.infinite(topdataimprove)] <- 0
rowmeansimprove <- rowMeans(topdataimprove[,1:numtrue])
topdataimprove <- cbind(topdataimprove, rowmeansimprove)
row.names(topdataimprove) <- collabels
colnames(topdataimprove) <- c("sensspec1","sensspec2", "sensspec3","sensspec4","crit1","crit2","crit3","crit4","mean")

ordermaximprove <- order(topdataimprove[,9],decreasing=T)
topdataimprove <- topdataimprove[ordermaximprove,]

write.table(topdataimprove,paste("./",firstlabel,"precisionrecall_top_improve.txt",sep=""),sep="\t")

print(paste("dim(topdata)",dim(topdata),sep=" "))

pdf(paste("./",firstlabel,"precisionrecall_mean_maxsensspec_top4.pdf",sep=""),width=8,height=11)
xvals <- c(1:dim(topdata)[1])
matplot(xvals,topdata[,c(1:4,9)], type="l",  ylim=c(0,2), ylab="Precision + Recall",xlab="Criterion or method", col=c("gray85","gray70","gray50","black","black"), lty=c("solid","solid","solid","solid","dashed"),lwd=c(1,1,1,1,2))
 for(j in 1:extdimpr[1]) { 
  for(i in 1:(extdimpr[2]-1)) {  
    points(j*5-4,extdataprecrec[j,i], pch=(j-1), cex=((i-1)*0.5+0.5))
    }
}
dev.off(2)

pdf(paste("./",firstlabel,"precisionrecall_mean_maxsensspec_top4_final.pdf",sep=""),width=8,height=11)
xvals <- c(1:dim(topdata)[1])
maxdata <- apply(topdata[,c(1:numtrue)], 1, max)
mindata <- apply(topdata[,c(1:numtrue)], 1, min)
#plotdata <- cbind(maxdata, mindata, topdata[,9])
matplot(xvals,topdata[,17], type="l",  ylim=c(0,2), col=c("gray70","gray85","black"), lty=c("solid","solid","solid"),lwd=c(1,1,1))
points(xvals, topdata[,17])
#errbar(xvals, topdata[,9], maxdata, mindata, ylim=c(0,2))
Js <- 0
means <- 0
maxs <- 0
mins <- 0
 for(j in 1:extdimpr[1]) { 
    points(j*5-4,mean(extdataprecrec[j,]), pch=(j-1), cex=2)
    Js <- c(Js, j*5-4 + 0.5)
    means <- c(means, mean(extdataprecrec[j,1:numtrue]))
    maxs <- c(maxs, max(extdataprecrec[j,1:numtrue]))
    mins <- c(mins, min(extdataprecrec[j,1:numtrue]))
}
Js <- Js[-1]
means <- means[-1]
maxs<- maxs[-1]
mins<- mins[-1]
errbar(c(xvals,Js),c(topdata[,17],means), xaxt="n",c(maxdata,maxs), c(mindata,mins),ylim=c(0,2), lwd=0.5,ylab="Precision + Recall",xlab="Criterion or method",col=c(rep("black",length(maxdata)),rep("red", 4*2)),cex=c(rep(1, length(maxdata)),rep(1.25, 8)), pch=c(rep(0,length(maxdata)),c(1:(extdimpr[1]))))
legend(2,0.75,legend=c("AK-clust",row.names(extdataprecrec)),pch=c(0:(extdimpr[1])))
dev.off(2)


pdf(paste("./",firstlabel,"precisionrecall_mean_maxsensspec_top4_final_ROC.pdf",sep=""),width=8,height=11)
meanTP <- apply(extdata[,13:(13+numtrue-1)], 1, mean)
sdTP <- apply(extdata[,13:(13+numtrue-1)], 1, sd)
meanFP <- apply(extdata[,17:(17+numtrue-1)], 1, mean)
sdFP <- apply(extdata[,17:(17+numtrue-1)], 1, sd)

meanTP <- c(mean(topdata[1,9:(9+numtrue-1)]),mean(topdata[2,9:(9+numtrue-1)]),mean(topdata[3,9:(9+numtrue-1)]), meanTP)
meanFP <- c(mean(topdata[1,13:(13+numtrue-1)]), mean(topdata[2,13:(13+numtrue-1)]),mean(topdata[3,13:(13+numtrue-1)]),meanFP)
sdTP <- c(sd(topdata[1,9:(9+numtrue-1)]), sd(topdata[2,9:(9+numtrue-1)]), sd(topdata[3,9:(9+numtrue-1)]), sdTP)
sdFP <- c(sd(topdata[1,13:(13+numtrue-1)]), sd(topdata[2,13:(13+numtrue-1)]), sd(topdata[3,13:(13+numtrue-1)]), sdFP)

lnames <- c(row.names(topdata)[1],row.names(topdata)[2], row.names(topdata)[3],  rownames(extdata))
plot(meanFP,meanTP,pch=c(0,0,0,1:(extdimpr[1])), xlab="Recall",ylab="Precision", xlim=c(0,1), ylim=c(0,1), cex=c(2,1.5,1,rep(1, extdimpr[1])))

for(i in 1:length(meanTP)) {
a <- sdFP[i]
b <- sdTP[i]
theta <- seq(0, 2 * pi, length=1000)
x <- meanFP[i] + a * cos(theta)
y <- meanTP[i]  + b * sin(theta)
lines(x, y, type = "l")
}

legend(0.0,1.0,lnames,pch=c(0,0,0,1:(extdimpr[1])), pt.cex=c(2,1.5,1,rep(1, extdimpr[1])))

dev.off(2)
#loop a
}
