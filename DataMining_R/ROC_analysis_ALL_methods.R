

doCase=function(reflabel, mode, extmethod, extmethodlabel, tallyM, thresholdM, sumM, criteria, domax) {

columnlabels <- c("criterion","firstref.count","lastref.count","regulon_size",
"firstref.ratio","lastref.ratio","numgenesfirst","numgeneslast","percentnumgeneslast",
"numexpsfirst","numexpslast","percentexplast","last.percentOrigGenes",
"last.percentOrigExp","first.precrit","last.precrit","numbermoves","passed","passed_final",
"F1g","specificityg","sensitivityg","F1e","specificitye","sensitivitye","specificityge","sensitivityge","F1recallge","runtime")
	index1 <- -1
	index2 <- -1
	geneexpmode <- regexpr("ge",mode, fixed=T)
	print(paste("test ge", mode, geneexpmode))
	if(geneexpmode != -1) {
	index1 <- 26
	index2 <- 27
	}
	else {
	geneexpmode <- regexpr("g",mode, fixed=T)
	print(paste("test g", mode, geneexpmode))
	}
	if(geneexpmode != -1) {
	index1 <- 21
	index2 <- 22
	}
	else {
	geneexpmode <- regexpr("e",mode, fixed=T)
	print(paste("test e", mode, geneexpmode))
		if(!is.na(geneexpmode)) {
			index1 <- 24
			index2 <- 25
		}
	}

	#print(paste(mode, index1, index2, sep=" "))
	dirs <- list.files(".", pattern = "results.")

	thresholds <- c()

	curlist <- paste("./",dirs[1],"/",reflabel,sep="")
	files <- list.files(curlist)
	#print(files)
	all_crit_spec <- matrix(NA, ncol = length(dirs), nrow = length(files))
	all_crit_sens <- matrix(NA, ncol = length(dirs), nrow = length(files))
	#print(dim(all_crit_spec),sep=" ")
	externalspec <- c()
	externalsens <- c()
	crit_labels <- c()
	external_labels <- c()

	maxcritlist <- c()
	sumlist <- c()
	labelslist <- c()
	done <- mat.or.vec(dim(tallyM)[1],dim(tallyM)[2])
	for(i in (1:length(dirs))) {
		#print(dirs[i])
			split <- strsplit(dirs[i],"_")
			split <- unlist(split)
			curthreshold <-  as.numeric(split[length(split)])
			thresholds <- c(thresholds,curthreshold)		
			if(length(externalspec) == 0) {
				externalpath <- paste("./",dirs[i],"/",reflabel,"/external__",reflabel,"_analyze.txt",sep="")
				print(externalpath)
				tmp <- read.table(externalpath, sep="\t",fill=T,col.names=columnlabels, skip=1,header=F)
				#print(externalpath)
				#print(tmp)
				externalspec <- tmp[,index1]
				externalsens <- tmp[,index2]
			}
			
			curlist <- paste("./",dirs[i],"/",reflabel,sep="")
			files <- list.files(curlist) 
				
			max <- 0
			maxdata <- c()
	  		for(j in (1:length(files))) {			
	  		path <- paste("./",dirs[i],"/",reflabel,"/",files[j],sep="")
	    		if(length(external_labels) == 0) {		
	    			external_labels <- tmp[,1]
	    		}
	    		#print(path)
	  		tmp <- read.table(path, sep="\t",fill=T,col.names=columnlabels, skip=1,header=F) 
	  		#print(paste(extmethod, index1, index2))
	  		#print(tmp)
	  		#print(paste(all_crit_spec[j,i], all_crit_sens[j,i]))
	  		#print(dim(tmp))
	  		#print(tmp)
	  		#print(paste(tmp[extmethod,index1],tmp[extmethod,index2]))
	  		#print(paste(charmatch("NA",tmp[extmethod,index1],  nomatch=-1),charmatch("NA",tmp[extmethod,index2],  nomatch=-1)))
	    		if(charmatch("NA",tmp[extmethod,index1],  nomatch=-1)!=-1 && charmatch("NA",tmp[extmethod,index2], nomatch=-1)!=-1) {
	    			all_crit_spec[j,i] <- 0
	    			all_crit_sens[j,i] <- 0
	    		}
	    		else if(charmatch("NA",tmp[extmethod,index1],  nomatch=-1)==-1 && charmatch("NA",tmp[extmethod,index2], nomatch=-1)==-1) {    			
	    			all_crit_spec[j,i] <- tmp[extmethod,index1]
	    			all_crit_sens[j,i] <- tmp[extmethod,index2]
	    #maximize the sum of specificity and sensitivity
	    			sum <- all_crit_spec[j,i] + all_crit_sens[j,i]
	    			if(is.na(sum)) {
	    			sum <- 0
	    			}
	    			#print(paste(all_crit_spec[j,i], all_crit_sens[j,i]))
	    			#print(sum)
	    			sumlist <- c(sumlist,sum)
	    			
	    			split <- strsplit(files[j],"__")
	    			split <- unlist(split)
	    			label <- paste(split[1], curthreshold,sep="__")
	    			labelslist <- c(labelslist, label)
	    			if(!is.na(sum) && sum > max) {
	    				max <- sum
	    				maxdata <- c(all_crit_spec[j,i],all_crit_sens[j,i] )
	    # specificity and sensitivity thresholds
	    				if(all_crit_spec[j,i] > 0.9 && all_crit_sens[j,i] > 0.5) {
	    					
	    					index <- c()
	    					if(length(maxcritlist) > 0) {
	    						index <- match(split[1],maxcritlist)    	
	    						if(is.na(index) || max(index) == -1){
	    						#print(split[1])
	    							maxcritlist <- cbind(maxcritlist, split[1])	
	    						}
	    					}
	    					else {
	    					#print(split[1])
	    					maxcritlist <- c(maxcritlist, split[1])	
	    					}
	            }#if(all_crit_spec[j,i] > 0.9 && all_crit_sens[j,i] > 0.5)
	            	            	
	    			maxfile <- files[j]
	    			}#if(!is.na(sum) && sum > max)
	    		critindex <- match(split[1], criteria)
	    		if(domax == 0 && is.na(done[critindex])) {						
			            tallyM[extmethod, critindex] <- tallyM[extmethod, critindex]+1
						sumM[extmethod, critindex] <- sumM[extmethod, critindex]+sum
						thresholdM[extmethod, critindex] <- thresholdM[extmethod, critindex]+curthreshold
						done[critindex] <- 1
					}
				
	    			
	    	}#if(!is.null(tmp[extmethod,index1]) && !is.null(tmp[extmethod,index2]))	
	  	}#end loop file
	}#end loop dir
	
	if(domax == 1 && !is.null(sumlist)) {
	maxrank <- rank(sumlist, na.last="NA", ties.method="min")
	maxmaxrank <- max(maxrank)
	#maxindex <- which(maxrank ==  sum(complete.cases(maxrank)))
	maxindex <- which(maxrank == maxmaxrank)  
	
	print(paste(extmethodlabel, mode, "max is", maxindex, labelslist[maxindex], sumlist[maxindex]))
	done <- mat.or.vec(length(criteria),0)
	for(i in (1:length(maxindex))) {
	maxlabel <- strsplit(labelslist[maxindex[i]],"__")
	maxlabel <- unlist(maxlabel)
	thisthresh <- as.numeric(maxlabel[2])
	maxlabel <- maxlabel[1]
	critindex <- match(maxlabel, criteria)
		if(is.na(done[critindex])) {
		print(paste("incrementing",extmethod, critindex,criteria[critindex],tallyM[extmethod, critindex]))
		tallyM[extmethod, critindex] <- tallyM[extmethod, critindex]+1
		sumM[extmethod, critindex] <- sumM[extmethod, critindex]+sumlist[maxindex[i]]
		thresholdM[extmethod, critindex] <- thresholdM[extmethod, critindex]+thisthresh
		done[critindex] <- 1
		}
	}
	}

		all_crit_spec <- 1 - all_crit_spec
		externalspec <- 1 - externalspec

		sortedthresholds <- sort.int(thresholds)
		#print(sortedthresholds)
		sorted_external_spec <- c()
		sorted_external_sens <- c()
		sorted_all_crit_spec <- c()
		sorted_all_crit_sens <- c()

		for(i in (1:length(thresholds))) {
			matchindex <- match(sortedthresholds[i],thresholds)
			sorted_external_spec <-cbind(sorted_external_spec, externalspec[matchindex])
			sorted_external_sens <-cbind(sorted_external_sens, externalsens[matchindex])

			sorted_all_crit_spec <- cbind(sorted_all_crit_spec, all_crit_spec[,matchindex])
			sorted_all_crit_sens <- cbind(sorted_all_crit_sens, all_crit_sens[,matchindex])
		}

		dim <- dim(sorted_all_crit_spec)
		coldata <- col2rgb("darkgray")
		pcol <- rgb(coldata[1],coldata[2],coldata[3],180,maxColorValue = 255)
		plot(sorted_all_crit_spec[1,] + runif(length(sorted_all_crit_spec[1,] ),0,.001), sorted_all_crit_sens[1,]+ runif(length(sorted_all_crit_spec[1,] ),0,.001),lwd="1", type="l", xlim=c(0,0.1), ylim=c(0,1),xlab="False positive rate", ylab="True positive rate",col=pcol)
		for(k in (2:dim[1])) {
		lines(sorted_all_crit_spec[k,]+ runif(length(sorted_all_crit_spec[1,] ),0,.001), sorted_all_crit_sens[k,]+ runif(length(sorted_all_crit_spec[1,] ),0,.001),lwd="1",col=pcol)
		}

		points(sorted_external_spec[extmethod], sorted_external_sens[extmethod],pch=10,col="black")
	lines(c(0,1),c(0,1),lty=10,col=pcol)

	list(tallyM,thresholdM,sumM)
}