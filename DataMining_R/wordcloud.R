library(RXKCD)
library(tm)
library(wordcloud)
library(RColorBrewer)
library(ggplot2)
library(pheatmap)
source("~/Documents/java/MAK/src/DataMining_R/Miner.R")

setwd("/Users/marcin/Documents/integr8_genom/Miner/miner_results/yeast/EXPR_round12345_merge_refine/conditionVenn_GO")


files <- list.files("./")

limit <- 9
count <- 0
roundcount <- 0
  



###tables g
setwd("/Users/marcin/Documents/integr8_genom/Miner/miner_results/yeast/EXPR_round12345_merge_refine/conditionVenn_GO")

files <- list.files("./")
fileslist <- c("5_GO.txt" , "5_43__43_GO.txt", "5_54__54_GO.txt", "5_43__5_GO.txt", "43_GO.txt" ,  "43_54__54_GO.txt"  , "5_54__5_GO.txt" ,  "43_54__43_GO.txt", "54_GO.txt" )
diag <- c("5_GO.txt" , "43_GO.txt" , "54_GO.txt")
offdiaglabels <- c( "5_43__43_GO.txt", "5_54__54_GO.txt", "5_43__5_GO.txt",  "43_54__54_GO.txt"  , "5_54__5_GO.txt" ,  "43_54__43_GO.txt" )
offdiagmap <- c(c(1,2), c(1,3), c(2,1), c(2,3), c(3,1), c(3,2))

offdiag <- readLines("/Users/marcin/Documents/integr8_genom/Miner/miner_results/yeast/EXPR_round12345_merge_refine/netmod_main3/unique_pval0.001_cut_0.01_member_graph_nodeGO_forR.noa")#,sep="_",header=F,fill=T)
offdiag <- sapply(offdiag, function(x) strsplit(x, "_"))

ondiag <- readLines("/Users/marcin/Documents/integr8_genom/Miner/miner_results/yeast/EXPR_round12345_merge_refine/netmod_main3/single_pval0.001_cut_0.01_member_graph_nodeGO.noa")#,sep="_",header=F,fill=T)
ondiag <- sapply(ondiag, function(x) strsplit(x, "_"))

GOrefcount <- read.table("/Users/marcin/Documents/integr8_genom/Miner/miner_results/yeast/EXPR_round12345_merge_refine/GO_count.txt",sep="\t",header=F)

par(mfrow=c(3,3))
#pdf("GO_tables.pdf",width=11,height=8.5)


###diagonal frequencies of enrichments from member biclusters
###off diagonal enriched terms for intersections

for(i in 1:length(fileslist)) {

                print(paste("file ", fileslist[i],sep=" "))
            tryCatch(data <- read.table(paste("./",fileslist[i],sep=""),sep="\t",header=F))
            
            print(paste("dim ",dim(data),sep=" "))
                if(dim(data)) {
                
                            print("data[,1]")
                            print(paste(data[,1], collapse=","))
                            print(dim(data))
                            #print(length(data))
                        
                        txt <- as.vector(data[,1])
                        weights <- as.vector(data[,2])
                                                
                        names(weights) <- txt                        
                        
                        tabletxt <- table(txt)
                         print(paste("tabletxt",dim(tabletxt),sep=" "))
                        tabletxt <- weights
                        
                        #for off diag filter out non-enriched
                        offcoordind <- grep(fileslist[i], offdiaglabels)
                        if(length(offcoordind) > 0) {
                             len <- length(tabletxt)
                             for(z in 1:len) {
                             
                             if(is.na(tabletxt[z])){
                             break
                             }
                             rem <- grep( names(tabletxt)[z], offdiag[[offcoordind]])
                              print(paste("test removed ", tabletxt[z], i,offcoordind, sep=" "))
                             #remove not enriched
                                                        if(length(rem) == 0 || is.na(rem)) {
                                                        print(tabletxt)
                                                        print(paste("removed b ", tabletxt[z],names(tabletxt)[z], rem, z, sep=" "))
                                                        tabletxt <- tabletxt[-z]
                                                        z <- z-1
                                                         len <- length(tabletxt)
                                                         print(paste("removed a ", tabletxt[z],names(tabletxt)[z], rem, z, sep=" "))
                                                         print(tabletxt)
                                                        }
                            print(paste("length ", length(tabletxt),sum(is.na(tabletxt)), z, sep=" "))
                             }                            
                         }
                         #do diag stuff
                         else {
                            for(z in 1:length(tabletxt) && !is.na(tabletxt[z])) {
                              coordind <- grep(fileslist[i], diaglabels)
                             rem <- grep( names(tabletxt)[z], diag[[coordind]])
                              print(paste("test removed d ", tabletxt[z], i,coordind, sep=" "))
                                                        if(length(rem) == 0 || is.na(rem)) {
                                                        print(tabletxt)
                                                        print(paste("removed d ", tabletxt[z],names(tabletxt)[z], rem, z, sep=" "))
                                                        tabletxt <- tabletxt[-z]
                                                        z <- z-1
                                                         print(paste("removed d ", tabletxt[z],names(tabletxt)[z], rem, z, sep=" "))
                                                         print(tabletxt)
                                                        }
                            print(paste("length d ", length(tabletxt),sum(is.na(tabletxt)), z, sep=" "))
                             }  
                         }
                        
                        sortnames <- names(sort(tabletxt, decreasing =T)[1:5])
                        data <- cbind(sortnames, sort(tabletxt, decreasing =T)[1:5])
                       
                        #normalize by total label occurrence
                        for(a in 1:length(sortnames)) {
                            index <- which(GOrefcount[,1] == sortnames[a])
                            print( sortnames[a])
                            print(index)
                            #normalized to frequency                            
                            test <- round(as.numeric(data[a,2])/GOrefcount[index,2], 2)
                             print(paste("ratio", data[a,2], GOrefcount[index,2], test, sep=" "))
                            data[a,2]  <- test
                        }
                        data[,2] <- round(100 * as.numeric(data[,2]),1)
                        textplot(data, show.rownames=F, show.colnames=F, ps =4, cex =0.7)
                        
                                        
                            }                
        }
        
cat("done ",done,"\n")
        
#dev.off(2)






###tables e


setwd("/Users/marcin/Documents/integr8_genom/Miner/miner_results/yeast/EXPR_round12345_merge_refine/conditionVenn_e")

files <- c("5___exps.txt", "5_43__43_uniqueexps.txt", "5_54__54_uniqueexps.txt", "5_43__5_uniqueexps.txt", "43___exps.txt",  "43_54__54_uniqueexps.txt", "5_54__5_uniqueexps.txt", "43_54__43_uniqueexps.txt" , "54___exps.txt")
filesraw <- c("5___raw_exps.txt", "5_43__43_raw_uniqueexps.txt", "5_54__54_raw_uniqueexps.txt", "5_43__5_raw_uniqueexps.txt", "43___raw_exps.txt",  "43_54__54_raw_uniqueexps.txt", "5_54__5_raw_uniqueexps.txt", "43_54__43_raw_uniqueexps.txt" , "54___raw_exps.txt")

labelsraw <- c(0,1,1,1,0,1,1,1,0)
par(mfrow=c(3,3))#, ps =10, cex =1

for(i in 1:length(files)) {
if(grep(".txt",files[i])) {    
                print(files[i])
                print(paste("file ", files[i],sep=" "))
            tryCatch(data <- read.table(paste("./",files[i],sep=""),sep="\t",header=F))
             tryCatch(dataraw <- read.table(paste("./",filesraw[i],sep=""),sep="\t",header=F))
            print(paste("dim ",dim(data),sep=" "))
                if(dim(data)) {

                            data <- cbind(data, dataraw)
                            data <- data[order(as.numeric(data[,4]), decreasing=T),]
                            if(labelsraw[i] == 1) {
                                                        data[,2] <- 100 * data[,2]
                            }
                            
                            dataplot <- data[1:5,1:2]
                            dataplot[,2] <- round(as.numeric(dataplot[,2]),0)                                           
                        textplot(dataplot, show.rownames=F, show.colnames=F, ps =4, cex =0.7)                                                    
                            }
}
}
        
cat("done ",done,"\n")




###clouds
      
outf <- paste("wordclouds_GO_v3_",roundcount,".pdf",sep="")
print(outf)
pdf(outf, 11,8.5)
par(mfrow=c(3,3))
     
done <- 0


for(i in 1:length(files)) {#
         if(grep(".txt",files[i])) {    
                print(files[i])
            tryCatch(data <- read.table(paste("./",files[i],sep=""),sep="\t",header=F))
            
            print(dim(data))
                if(dim(data)) {
                
                            #print("data")
                            #print(data)
                            print(dim(data))
                            #print(length(data))
                        
                        txt <- as.vector(data[,1])
                        txt <- gsub("ca$","ca_alone",txt)
                        txt <- gsub("na$","na_alone",txt)
                        weights <- as.vector(data[,2])
                        
                        txt <- tolower(gsub("[[:punct:]]", "", txt))
                        
                        names(weights) <- txt
                        
                        tabletxt <- table(txt)
                        #tabletxt <- tabletxt[order(rownames(tabletxt))]
                        
                        curweights <- mat.or.vec(1,0)
                        for(i in 1:length(tabletxt)) {#
                              curname <- names(tabletxt)[i]
                              ind <- which(txt == curname)
                              
                              #cat(curname," , ",ind," , ",weights[ind]," , ", sum(weights[ind]),"\n")
                              curweights <- c(curweights, sum(weights[ind]))
                        }
                        names(curweights) <- names(tabletxt)
                        curweights <- curweights[order(names(curweights))]
                        cat("max weight ", max(curweights),  names(curweights)[which(curweights == max(curweights))],"\n")
                                            
                                            d <- data.frame(word = names(curweights),freq=curweights)
                                            pal <- brewer.pal(9, "Blues")
                                            pal <- pal[-(1:3)]
                                            
                                            cur <- cbind(as.character(d$word), d$freq)
                                            #print("cur")
                                            #print(cur)
                                            #print(d$freq )
                                            
                                                    cat(i, "\n")
                                                    cat(cur[,1], "\n")
                                                    cat(cur[,2], "\n")
                                                    wordcloud(cur[,1], as.numeric(cur[,2]), scale=c(1,.1),min.freq=1,max.words=200, random.order=F, rot.per=0, colors=pal, vfont=c("sans serif","plain"))
                                                    done <- 1
                                                    
                                                    count <- count+1
                                                    if(count == limit) {
                                                                dev.off(2)
                                                                                            
                                                                outf <- paste(files[i],"_wordcloud.pdf",sep="")
                                                                print(outf)
                                                                pdf(outf, 11,8.5)
                                                                par(mfrow=c(3,3))
                                                                count <- 0
                                                                roundcount <- roundcount +1
                                                    }
                                        
                                        }                
        }
        }
cat("done ",done,"\n")
        
dev.off(2)