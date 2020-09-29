library(RXKCD)
library(tm)
library(wordcloud)
library(RColorBrewer)
library(ggplot2)
library(pheatmap)
source("/java/DataMining_R/Miner.R")

setwd("~/Documents/integr8_genom/Miner/miner_results/yeast/EXPR_round12345_merge_refine/conditionVenn/")

#path <- system.file("xkcd", package = "RXKCD")
#datafiles <- list.files(path)
#xkcd.df <- read.csv(file.path(path, datafiles))

codelist <- c("GO","PATH","TIGR","TF")
biclist <- c(402, 1531, 690, 388, 1406, 812, 445)

files <- list.files("./clouds")

outf <- paste("wordclouds_v3.pdf",sep="")
print(outf)
pdf(outf, 8.5, 14)
par(mfrow=c(11,4))
                            
for( a in 1:length(biclist)) {
    for( b in 1:length(codelist)) {        
        done <- 0
        for(i in 1:length(files)) {
            prefix <- paste(biclist[a],"_",codelist[b],"_cloud.txt",sep="")
            #print(prefix)
            index <- grep(prefix, files[i])
            #print(class(index))
            cat(index, prefix,"\n",sep=" ")
            if(length(index)) {                
                print(files[i])
            tryCatch(txt <- read.table(paste("./clouds/",files[i],sep=""),sep="",header=F))
                if(dim(txt)) {
                  
                  print(dim(txt))
                  print(class(txt))  
                    #txt <- system.file("texts", "txt", package = "tm")
                    #(xkcd.corpus  <- Corpus(txt, readerControl = list(language = "lat")))#DirSource(txt)

txt <- as.vector(txt[,1])

txt <- gsub("[(]","",txt)
txt <- gsub("[)]","",txt)
txt <- gsub("[_]","",txt)
txt <- gsub("[-]","",txt)
txt <- gsub("[,]","",txt)
txt <- tolower(txt)
txt <- gsub("riboflavinfmnandfadbiosynthesis","riboflavinfmnfad",txt)
txt <- gsub("superpathwayofglutamatebiosynthesis","superpathwayglu",txt)
txt <- gsub("phosphatidylinositolphosphatebiosynthesis","PIP",txt)
txt <- gsub("biosynthesisofcofactorsprostheticgroupsandcarriers","cofactprosthcarriers",txt)
txt <- gsub("purinespyrimidinesnucleosidesandnucleotides","purpyrnuclnucl",txt)
txt <- gsub("denovobiosynthesisofpurinenucleotides","denovopurinenucl",txt)
txt <- gsub("transportandbindingproteins","transportbindingprot",txt)
txt <- gsub("cellularcarbohydratemetabolicprocess","celcarbmetprocess",txt)
txt <- gsub("generationofprecursormetabolitesandenergy","precursormetabolitesenergy",txt)
txt <- gsub("superpathwayofglucosefermentation","superpathwayglucferm",txt)
txt <- gsub("glucose6phosphatebiosynthesis","glucose6phosphate",txt)
txt <- gsub("4aminobutyratedegradation","4aminobutyratedegr",txt)
txt <- gsub("responsetochemicalstimulus","resp.chemicalstimulus",txt)
txt <- gsub("transcriptiondnadependent","transcriptiondnadep",txt)
txt <- gsub("lipidlinkedoligosaccharidebiosynthesis","lipidlinkedoligosacch",txt)
txt <- gsub("denovobiosynthesisofpyrimidinedeoxyribonucleotides","denovopyrdribonucl",txt)
txt <- gsub("tryptophandegradationviakynurenine","trpdegrviakynurenine",txt)
txt <- gsub("fattyacidoxidationpathway","fattyacidoxid",txt)
txt <- gsub("verylongchainfattyacidbiosynthesis","vlchainfattyacidbiosynth",txt)
txt <- gsub("nonoxidativebranchofthepentosephosphatepathway","nonoxidpentosephosph",txt)
txt <- gsub("phosphatidatebiosynthesisiitheglycerol3phosphatepathway","phosphbiosynthglyc3phosph",txt)
txt <- gsub("ubiquibiosynthesisfrom4hydroxybenzoate","ubiquibiosynth4hydroxybenz",txt)


print(class(txt))  

                    #cat(unlist(txt), "\n")
                    #cat(txt[[1]],"\n")
                    print(txt)
                    
                    xkcd.corpus <- Corpus(VectorSource(txt))
                    #xkcd.corpus <- Corpus(DataframeSource(data.frame(xkcd.df[, 3])))
                    xkcd.corpus <- tm_map(xkcd.corpus, removePunctuation)
                    xkcd.corpus <- tm_map(xkcd.corpus, tolower)
                    xkcd.corpus <- tm_map(xkcd.corpus, function(x) removeWords(x, stopwords("english")))
                    tdm <- TermDocumentMatrix(xkcd.corpus)
                    m <- as.matrix(tdm)
                    #sort alphabetically
                    v <- sort(rowSums(m),decreasing=TRUE)
                    d <- data.frame(word = names(v),freq=v)
                    pal <- brewer.pal(9, "Blues")
                    pal <- pal[-(1:3)]
                    
                    cur <- cbind(as.character(d$word), d$freq)
                    #cur <- cur[order(cur[,1]),]
                    print(cur[,1])
                    
                        if(max(d$freq > 1)) {
                            #outf <- paste(files[i],".pdf",sep="")
                            #print(outf)
                            #pdf(outf)
                            cat("cloud\n")
                            cat(cur[,2], "\n")
                            wordcloud(cur[,1], as.numeric(cur[,2]), scale=c(1,.1),min.freq=2,max.words=200, random.order=F, rot.per=0, colors=pal, vfont=c("sans serif","plain"))
                            done <- 1
                        }
                }
            }
        }
        cat("done ",done,"\n")
        if(done == 0) {
            datanone <- as.vector("none")
            print(datanone)
            xkcd.corpus <- Corpus(VectorSource(datanone))
            #xkcd.corpus <- Corpus(DataframeSource(data.frame(xkcd.df[, 3])))
            xkcd.corpus <- tm_map(xkcd.corpus, removePunctuation)
            xkcd.corpus <- tm_map(xkcd.corpus, tolower)
            xkcd.corpus <- tm_map(xkcd.corpus, function(x) removeWords(x, stopwords("english")))
            tdm <- TermDocumentMatrix(xkcd.corpus)
            m <- as.matrix(tdm)
            #sort alphabetically
            v <- sort(rowSums(m),decreasing=TRUE)
            d <- data.frame(word = names(v),freq=v)
            cur <- cbind(as.character(d$word), d$freq)
            cat("none ",cur[,1],"\n")

            wordcloud(cur[,1], as.numeric(cur[,2]), scale=c(1,.1),min.freq=1,max.words=200, random.order=F, rot.per=0, colors="Blue", vfont=c("sans serif","plain"))
            done <- 1
        }
    }
}
dev.off(2)



###demo
  library(gplots)
  data(mtcars)
x  <- as.matrix(mtcars)
  rc <- rainbow(nrow(x), start=0, end=.3)
  cc <- rainbow(ncol(x), start=0, end=.3)

#heatmap.2(x, col=cm.colors(255), scale="column", RowSideColors=rc, ColSideColors=cc, margin=c(5, 10), xlab="specification variables", ylab= "Car Models", main="heatmap(<Mtcars data>, ..., scale=\"column\")", tracecol="green", density="density")
pheatmap(x)


###
###data heatmaps
files <- list.files("./data")
#files <- list.files("./clouds_data")

mypalette <- rev(brewer.pal(6, "Blues"))
mypalette <- c(mypalette, brewer.pal(6, "YlOrBr"))

breaks <- seq(-3, 3,0.5 )

for(i in 1:length(files)) {
    print(files[i])
#tryCatch(datathis <- read.table(paste("./clouds_data/",files[i],sep=""),sep="\t",header=T,row.names=1))
tryCatch(datathis <- read.table(paste("./data/",files[i],sep=""),sep="\t",header=T,row.names=1))
if(dim(datathis)) {

outf <- paste(files[i],"_cluster_pheat_noname.pdf",sep="")
print(outf)
pdf(outf)
mat <- as.matrix(datathis)
mat <- apply(mat,1,missfxn)
#mat <- t(mat)
mat[mat > 3] <- 3
mat[mat < -3] <- -3
symnum( cordata <- cor(mat) )
#cormatreformat <- format(round(datanow, 2))

#heatmap.2(t(mat), trace="none",col=mypalette,key=F, distfun=function(x) as.dist(1-(1+cor(t(x)))/2), cexRow=0.25, cexCol=0.5)
pheatmap(t(mat),dist=cordata,breaks=breaks,color=mypalette,cellwidth=2,cellheight=2,show_rownames=F,show_colnames=F,legend=F)#clustering_distance_rows="correlation",clustering_distance_cols="correlation",#,fontsize_row=2,fontsize_col=2,
          #,cellnote=as.matrix(cormatreformat))#, Colv="none", dendrogram="row"
dev.off()


outf <- paste(files[i],"_pheat_noname.pdf",sep="")
print(outf)
pdf(outf)

rm <- rowMeans(mat)
cm <- colMeans(mat)
mat <- mat[order(rm, decreasing=T),]
mat <- mat[,order(cm, decreasing=T)]
#heatmap.2(t(mat) , Colv=F,Rowv=F, dendrogram="none",trace="none",col=mypalette,symkey=T, cexRow=0.25, cexCol=0.5)#,breaks=seq(-1,1,0.1052632),symKey=T#
pheatmap(t(mat),breaks=breaks,cluster_rows=F,cluster_cols=F,color=mypalette,cellwidth=2,cellheight=2,show_rownames=F,show_colnames=F,legend=F)#,fontsize_row=2,fontsize_col=2
dev.off()
}
}