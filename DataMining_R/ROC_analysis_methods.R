

doCase=function(reflabel, mode) {

	index1 <- -1
	index2 <- -1
	geneexpmode <- regexpr("ge",mode, fixed=T)
	if(!is.na(geneexpmode) && geneexpmode != -1) {
	index1 <- 26
	index2 <- 27
	}
	else {
	geneexpmode <- regexpr("g",mode, fixed=T)
		if(!is.na(geneexpmode) && geneexpmode != -1) {	
			index1 <- 21
			index2 <- 22
			}
		else {
			geneexpmode <- regexpr("e",mode, fixed=T)
			if(!is.na(geneexpmode) && geneexpmode != -1) {	
				index1 <- 24
				index2 <- 25
			}
		}
	}
	
	
	
	print(paste("mode ", mode, " index ",index1, index2,sep=" "))
	
	files <- list.files(".",pattern = "results.")

	thresholds <- c()
	LARSRE__specificitySAMBA <- c()
	LARSRE__sensitivitySAMBA <- c()
	LARSRE__specificityQUBIC <- c()
	LARSRE__sensitivityQUBIC<- c()
	MSER_GEERE__specificitySAMBA <- c()
	MSER_GEERE__sensitivitySAMBA <- c()
	MSER_GEERE__specificityQUBIC <- c()
	MSER_GEERE__sensitivityQUBIC<- c()
	
	MSEC_GEERE__specificitySAMBA <- c()
	MSEC_GEERE__sensitivitySAMBA <- c()
	MSEC_GEERE__specificityQUBIC <- c()
	MSEC_GEERE__sensitivityQUBIC<- c()
	
	MSER__specificitySAMBA <- c()
	MSER__sensitivitySAMBA <- c()
	MSER__specificityQUBIC <- c()
	MSER__sensitivityQUBIC<- c()
	GEERE__specificitySAMBA <- c()
	GEERE__sensitivitySAMBA <- c()
	GEERE__specificityQUBIC <- c()
	GEERE__sensitivityQUBIC<- c()
	MSER_GEERE_PPI__specificitySAMBA <- c()
	MSER_GEERE_PPI__sensitivitySAMBA <- c()
	MSER_GEERE_PPI__specificityQUBIC <- c()
	MSER_GEERE_PPI__sensitivityQUBIC<- c()
	GEERE_PPI__specificitySAMBA <- c()
	GEERE_PPI__sensitivitySAMBA <- c()
	GEERE_PPI__specificityQUBIC <- c()
	GEERE_PPI__sensitivityQUBIC<- c()
	externalp <- c()
	externalr <- c()
	
	labels <- c()

	for(i in (1:length(files))) {
		#print(files[i])
		split <- strsplit(files[i],"_")
		split <- unlist(split)
		thresholds <- c(thresholds, as.numeric(split[length(split)]))
		#print(thresholds)	
		
		if(length(externalr) == 0) {
		externalpath <- paste("./",files[i],"/",reflabel,"/external__",reflabel,"_analyze.txt",sep="")
			print(externalpath)
		tmp <- read.table(externalpath, sep="\t",fill=T,skip=1,header=F)
		externalp <- tmp[,index1]
		externalr <- tmp[,index2]
		}

		aftercritpath <- paste("./",files[i],"/",reflabel,"/LARSRE__",reflabel,"_analyze.txt",sep="")
		#tmp <- read.table(aftercritpath, sep="\t",fill=T,header=F)
		#print(tmp[1,])
		if(length(labels) == 0) {		
			labels <- tmp[,1]
		}
		tmp <- read.table(aftercritpath, sep="\t",fill=T,skip=1,header=F)
		LARSRE__specificitySAMBA <- cbind(LARSRE__specificitySAMBA, tmp[7,index1])
		LARSRE__sensitivitySAMBA <- cbind(LARSRE__sensitivitySAMBA, tmp[7,index2])	
		LARSRE__specificityQUBIC <- cbind(LARSRE__specificityQUBIC, tmp[5,index1])
		LARSRE__sensitivityQUBIC <- cbind(LARSRE__sensitivityQUBIC, tmp[5,index2])
		
		aftercritpath <- paste("./",files[i],"/",reflabel,"/MSER_GEERE__",reflabel,"_analyze.txt",sep="")
		#tmp <- read.table(aftercritpath, sep="\t",fill=T,header=F)
		#print(tmp[1,])
		tmp <- read.table(aftercritpath, sep="\t",fill=T,skip=1,header=F)
		MSER_GEERE__specificitySAMBA <- cbind(MSER_GEERE__specificitySAMBA, tmp[7,index1])
		MSER_GEERE__sensitivitySAMBA <- cbind(MSER_GEERE__sensitivitySAMBA, tmp[7,index2])	
		MSER_GEERE__specificityQUBIC <- cbind(MSER_GEERE__specificityQUBIC, tmp[5,index1])
		MSER_GEERE__sensitivityQUBIC <- cbind(MSER_GEERE__sensitivityQUBIC, tmp[5,index2])	
		
		aftercritpath <- paste("./",files[i],"/",reflabel,"/MSER__",reflabel,"_analyze.txt",sep="")
		#tmp <- read.table(aftercritpath, sep="\t",fill=T,header=F)
		#print(tmp[1,])
		tmp <- read.table(aftercritpath, sep="\t",fill=T,skip=1,header=F)
		MSER__specificitySAMBA <- cbind(MSER__specificitySAMBA, tmp[7,index1])
		MSER__sensitivitySAMBA <- cbind(MSER__sensitivitySAMBA, tmp[7,index2])	
		MSER__specificityQUBIC <- cbind(MSER__specificityQUBIC, tmp[5,index1])
		MSER__sensitivityQUBIC <- cbind(MSER__sensitivityQUBIC, tmp[5,index2])	
		
		aftercritpath <- paste("./",files[i],"/",reflabel,"/GEERE__",reflabel,"_analyze.txt",sep="")
		#tmp <- read.table(aftercritpath, sep="\t",fill=T,header=F)
		#print(tmp[1,])
		tmp <- read.table(aftercritpath, sep="\t",fill=T,skip=1,header=F)
		GEERE__specificitySAMBA <- cbind(GEERE__specificitySAMBA, tmp[7,index1])
		GEERE__sensitivitySAMBA <- cbind(GEERE__sensitivitySAMBA, tmp[7,index2])	
		GEERE__specificityQUBIC <- cbind(GEERE__specificityQUBIC, tmp[5,index1])
		GEERE__sensitivityQUBIC <- cbind(GEERE__sensitivityQUBIC, tmp[5,index2])	
		
		aftercritpath <- paste("./",files[i],"/",reflabel,"/MSER_GEERE_PPI__",reflabel,"_analyze.txt",sep="")
		#tmp <- read.table(aftercritpath, sep="\t",fill=T,header=F)
		#prPPI(tmp[1,])
		tmp <- read.table(aftercritpath, sep="\t",fill=T,skip=1,header=F)
		MSER_GEERE_PPI__specificitySAMBA <- cbind(MSER_GEERE_PPI__specificitySAMBA, tmp[7,index1])
		MSER_GEERE_PPI__sensitivitySAMBA <- cbind(MSER_GEERE_PPI__sensitivitySAMBA, tmp[7,index2])	
		MSER_GEERE_PPI__specificityQUBIC <- cbind(MSER_GEERE_PPI__specificityQUBIC, tmp[5,index1])
		MSER_GEERE_PPI__sensitivityQUBIC <- cbind(MSER_GEERE_PPI__sensitivityQUBIC, tmp[5,index2])	
		
		aftercritpath <- paste("./",files[i],"/",reflabel,"/GEERE_PPI__",reflabel,"_analyze.txt",sep="")
		#tmp <- read.table(aftercritpath, sep="\t",fill=T,header=F)
		#prPPI(tmp[1,])
		tmp <- read.table(aftercritpath, sep="\t",fill=T,skip=1,header=F)
		GEERE_PPI__specificitySAMBA <- cbind(GEERE_PPI__specificitySAMBA, tmp[7,index1])
		GEERE_PPI__sensitivitySAMBA <- cbind(GEERE_PPI__sensitivitySAMBA, tmp[7,index2])	
		GEERE_PPI__specificityQUBIC <- cbind(GEERE_PPI__specificityQUBIC, tmp[5,index1])
		GEERE_PPI__sensitivityQUBIC <- cbind(GEERE_PPI__sensitivityQUBIC, tmp[5,index2])	
		
	}
	#print(labels)
	#print("externalp")
	#print(externalp)
	externalp <- 1-externalp
	#print("1-externalp")
	#print(externalp)
	LARSRE__specificitySAMBA <- 1-LARSRE__specificitySAMBA
	LARSRE__specificityQUBIC <- 1-LARSRE__specificityQUBIC
	MSER_GEERE__specificitySAMBA <- 1-MSER_GEERE__specificitySAMBA
	MSER_GEERE__specificityQUBIC <- 1-MSER_GEERE__specificityQUBIC
	MSER__specificitySAMBA <- 1-MSER__specificitySAMBA
	MSER__specificityQUBIC <- 1-MSER__specificityQUBIC
	GEERE__specificitySAMBA <- 1-GEERE__specificitySAMBA
	GEERE__specificityQUBIC <- 1-GEERE__specificityQUBIC
	MSER_GEERE_PPI__specificitySAMBA <- 1-MSER_GEERE_PPI__specificitySAMBA
	MSER_GEERE_PPI__specificityQUBIC <- 1-MSER_GEERE_PPI__specificityQUBIC
	GEERE_PPI__specificitySAMBA <- 1-GEERE_PPI__specificitySAMBA
	GEERE_PPI__specificityQUBIC <- 1-GEERE_PPI__specificityQUBIC
	sortedthresholds <- sort.int(thresholds)
	#print(sortedthresholds)
	
	sortLARSRE__specificitySAMBA <- c()
	sortLARSRE__sensitivitySAMBA <- c()
	sortLARSRE__specificityQUBIC <- c()
	sortLARSRE__sensitivityQUBIC <- c()	
	sortMSER_GEERE__specificitySAMBA <- c()
	sortMSER_GEERE__sensitivitySAMBA <- c()
	sortMSER_GEERE__specificityQUBIC <- c()
	sortMSER_GEERE__sensitivityQUBIC <- c()
	sortMSER__specificitySAMBA <- c()
	sortMSER__sensitivitySAMBA <- c()
	sortMSER__specificityQUBIC <- c()
	sortMSER__sensitivityQUBIC <- c()
	sortGEERE__specificitySAMBA <- c()
	sortGEERE__sensitivitySAMBA <- c()
	sortGEERE__specificityQUBIC <- c()
	sortGEERE__sensitivityQUBIC <- c()
	sortMSER_GEERE_PPI__specificitySAMBA <- c()
	sortMSER_GEERE_PPI__sensitivitySAMBA <- c()
	sortMSER_GEERE_PPI__specificityQUBIC <- c()
	sortMSER_GEERE_PPI__sensitivityQUBIC <- c()
	sortGEERE_PPI__specificitySAMBA <- c()
	sortGEERE_PPI__sensitivitySAMBA <- c()
	sortGEERE_PPI__specificityQUBIC <- c()
	sortGEERE_PPI__sensitivityQUBIC <- c()

	for(i in (1:length(thresholds))) {
		index <- match(sortedthresholds[i],thresholds)
		sortLARSRE__specificitySAMBA <- cbind(sortLARSRE__specificitySAMBA, LARSRE__specificitySAMBA[index])
		sortLARSRE__sensitivitySAMBA <- cbind(sortLARSRE__sensitivitySAMBA, LARSRE__sensitivitySAMBA[index])
		sortLARSRE__specificityQUBIC <- cbind(sortLARSRE__specificityQUBIC, LARSRE__specificityQUBIC[index])
		sortLARSRE__sensitivityQUBIC <- cbind(sortLARSRE__sensitivityQUBIC, LARSRE__sensitivityQUBIC[index])

		sortMSER_GEERE__specificitySAMBA <- cbind(sortMSER_GEERE__specificitySAMBA, MSER_GEERE__specificitySAMBA[index])
		sortMSER_GEERE__sensitivitySAMBA <- cbind(sortMSER_GEERE__sensitivitySAMBA, MSER_GEERE__sensitivitySAMBA[index])
		sortMSER_GEERE__specificityQUBIC <- cbind(sortMSER_GEERE__specificityQUBIC, MSER_GEERE__specificityQUBIC[index])
		sortMSER_GEERE__sensitivityQUBIC <- cbind(sortMSER_GEERE__sensitivityQUBIC, MSER_GEERE__sensitivityQUBIC[index])
	
		sortMSER__specificitySAMBA <- cbind(sortMSER__specificitySAMBA, MSER__specificitySAMBA[index])
		sortMSER__sensitivitySAMBA <- cbind(sortMSER__sensitivitySAMBA, MSER__sensitivitySAMBA[index])
		sortMSER__specificityQUBIC <- cbind(sortMSER__specificityQUBIC, MSER__specificityQUBIC[index])
		sortMSER__sensitivityQUBIC <- cbind(sortMSER__sensitivityQUBIC, MSER__sensitivityQUBIC[index])
		
		sortGEERE__specificitySAMBA <- cbind(sortGEERE__specificitySAMBA, GEERE__specificitySAMBA[index])
		sortGEERE__sensitivitySAMBA <- cbind(sortGEERE__sensitivitySAMBA, GEERE__sensitivitySAMBA[index])
		sortGEERE__specificityQUBIC <- cbind(sortGEERE__specificityQUBIC, GEERE__specificityQUBIC[index])
		sortGEERE__sensitivityQUBIC <- cbind(sortGEERE__sensitivityQUBIC, GEERE__sensitivityQUBIC[index])
		
		sortMSER_GEERE_PPI__specificitySAMBA <- cbind(sortMSER_GEERE_PPI__specificitySAMBA, MSER_GEERE_PPI__specificitySAMBA[index])
		sortMSER_GEERE_PPI__sensitivitySAMBA <- cbind(sortMSER_GEERE_PPI__sensitivitySAMBA, MSER_GEERE_PPI__sensitivitySAMBA[index])
		sortMSER_GEERE_PPI__specificityQUBIC <- cbind(sortMSER_GEERE_PPI__specificityQUBIC, MSER_GEERE_PPI__specificityQUBIC[index])
		sortMSER_GEERE_PPI__sensitivityQUBIC <- cbind(sortMSER_GEERE_PPI__sensitivityQUBIC, MSER_GEERE_PPI__sensitivityQUBIC[index])
		
		sortGEERE_PPI__specificitySAMBA <- cbind(sortGEERE_PPI__specificitySAMBA, GEERE_PPI__specificitySAMBA[index])
		sortGEERE_PPI__sensitivitySAMBA <- cbind(sortGEERE_PPI__sensitivitySAMBA, GEERE_PPI__sensitivitySAMBA[index])
		sortGEERE_PPI__specificityQUBIC <- cbind(sortGEERE_PPI__specificityQUBIC, GEERE_PPI__specificityQUBIC[index])
		sortGEERE_PPI__sensitivityQUBIC <- cbind(sortGEERE_PPI__sensitivityQUBIC, GEERE_PPI__sensitivityQUBIC[index])
	}

	plot(sortLARSRE__specificitySAMBA, sortLARSRE__sensitivitySAMBA,lwd="2", type="l", xlim=c(0,1), ylim=c(0,1),xlab="False Positive Rate", ylab="True Positive Rate",col="salmon")
	#points(sortLARSRE__specificitySAMBA, sortLARSRE__sensitivitySAMBA,pch=1)
	lines(sortLARSRE__specificityQUBIC, sortLARSRE__sensitivityQUBIC,col="salmon")
	lines(sortMSER_GEERE__specificitySAMBA, sortMSER_GEERE__sensitivitySAMBA,lwd="2",col="chocolate4")
	lines(sortMSER_GEERE__specificityQUBIC, sortMSER_GEERE__sensitivityQUBIC,col="chocolate4")
	lines(sortMSER__specificitySAMBA, sortMSER__sensitivitySAMBA,lwd="2",col="darkblue")
	lines(sortMSER__specificityQUBIC, sortMSER__sensitivityQUBIC,col="darkblue")
	lines(sortGEERE__specificitySAMBA, sortGEERE__sensitivitySAMBA,lwd="2",col="darkgreen")
	lines(sortGEERE__specificityQUBIC, sortGEERE__sensitivityQUBIC,col="darkgreen")
	lines(sortMSER_GEERE_PPI__specificitySAMBA, sortMSER_GEERE_PPI__sensitivitySAMBA,lwd="2",col="darkseagreen1")
	lines(sortMSER_GEERE_PPI__specificityQUBIC, sortMSER_GEERE_PPI__sensitivityQUBIC,col="darkseagreen1")
	lines(sortGEERE_PPI__specificitySAMBA, sortGEERE_PPI__sensitivitySAMBA,lwd="2",col="green")
	lines(sortGEERE_PPI__specificityQUBIC, sortGEERE_PPI__sensitivityQUBIC,col="green")
	#poPPIs(sortMSER_PPI__specificityQUBIC, sortMSER_PPI__sensitivityQUBIC,pch=1)
	points(externalp, externalr,pch=17,col="black")
	lines(c(0,1),c(0,1),lty=10,col="gray")
}