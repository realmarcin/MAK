
#data <- read.table("./20120126_allMotifData1.01_trim.rdat",sep="\t",header=T,row.names=1)
data <- read.table("./20120126_allMotifData1.01.rdat",sep="\t",header=T,row.names=1)
databin <- data
databin[databin < 0.99] <- 0
databin[databin >= 0.99] <- 1

colsums <- colSums(databin)
sort(colsums,decreasing=F)

#columns to remove
YMR164C_204
761

YER109C_67
1802

YER169W_279
1744

YDR096W_88
1694

> grep("YER169W",colnames(data))
 [1] 1738 1739 1740 1741 1742 1743 1744 1745 1746 1747 1748 1749 1750 1751 1752

> grep("YDR096W",colnames(data))
[1] 1694 1695 1696


#trim 1
trimind <- c(761, 1802, 1694, 1695, 1696, 1738, 1739, 1740, 1741, 1742, 1743, 1744, 1745, 1746, 1747, 1748, 1749, 1750, 1751, 1752)
datatrim <- data[,-trimind]

databintrim <- databin[,-trimind]
colsumstrim <- colSums(databintrim)


namessplit <- strsplit(colnames(datatrim),"_")
namessplit2 <- matrix(unlist(namessplit), ncol=2, byrow=T)

uniquenames <- unique(namessplit2[,1])

trimoutlier <- c()
for(i in 1:length(uniquenames)) {
    index <- grep(uniquenames[i], colnames(datatrim))
   
    #print(colnames(datatrim)[index])
    max <- max(colsumstrim[index])
    mean <- mean(colsumstrim[nomaxindex])
    sd <- sd(colsumstrim[index])
            
     if(max != 47 && max > 500) {
            print(index)
            print(max)
            print(mean)
         
        maxind <- which(colsumstrim[index] == max)
        nomaxindex <- index[-maxind]
        
        if(length(index) > 2) {
           #print(mean)
           
            print(mean+2*sd)
            #if(max > mean + 2 * sd) {
                trimoutlier <- c(trimoutlier, index[maxind])
            #}
        }
        else {        
            #if(max(colsumstrim[index]) > 100 * min(colsumstrim[index])) {
                  trimoutlier <- c(trimoutlier, index[maxind])
            #}
        }    
    }

}

> trimoutlier
[1]  443  576  596  681  768 1338 1405

datatrim <- datatrim[,-trimoutlier]
write.table(datatrim, file="./20120126_allMotifData1.01_trim.rdat",sep="\t")


###OTHER REMOVAL EXAMPLES
YFL031W_96
2109 binding sites (HAC1)

YDR451C_422
1862 binding sites (YPH1)

YOR113W_30
1548 binding site (AZF1)

YGL254W_545
1336 binding sites (FZF1)