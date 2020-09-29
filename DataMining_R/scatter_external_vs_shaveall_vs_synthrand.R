doCase=function(path, title) {
setwd(path)
files <- list.files(".")
datalabels <- mat.or.vec(0,0)
plotlabels <- mat.or.vec(0,0)
datax <- c()
datay <- c()
for(i in (1:length(files))) {
	tmp <- read.table(files[i], sep="\t",fill=T,header=T)
	if(length(plotlabels) ==0) {
		plotlabels <- as.vector(tmp[,1])
		#cat(plotlabels)
	}
	tmpdim <- dim(tmp)
	tmp[,26][is.na(tmp[,26])] <- 0
	tmp[,27][is.na(tmp[,27])] <- 0
	datax <- cbind(datax, tmp[,26])
	datay <- cbind(datay, tmp[,27])
	
	datalabels <- c(datalabels, files[i])
}
datax.col.names <- datalabels
row.names(datax) <- plotlabels
datay.col.names <- datalabels
row.names(datay) <- plotlabels
plotlabels[4] <- c("K-means")

plotcase(datax, datay, title)
}

plotcase=function(datax, datay, title) {

	col <- rgb(col2rgb("black")[1],col2rgb("black")[2],col2rgb("black")[3],100,maxColorValue = 255)
	plot(datax, datay, xlim=c(0,1),ylim=c(0,1),col=col,xlab="gene+experiment recall", ylab="gene+experiment precision",main=title)
	
	}

pdf("c:/projects/integr8_genom/Miner/miner_results/scatter_external_vs_shaveall_vs_synthrand.pdf",width = 8, height = 8)
par(mfrow=c(4,2))

doCase("c:/projects/integr8_genom/Miner/miner_results/results_synth_external_r_analyze/true1", "Overlapping row correlations 1")
doCase("c:/projects/integr8_genom/Miner/miner_results/results_synth_external_r_analyze/true2", "Overlapping row correlations 2")
doCase("c:/projects/integr8_genom/Miner/miner_results/results_synth_external_c_analyze/true1", "Overlapping column correlations 1")
doCase("c:/projects/integr8_genom/Miner/miner_results/results_synth_external_c_analyze/true2", "Overlapping column correlations 2")
doCase("c:/projects/integr8_genom/Miner/miner_results/results_synth_external_e_analyze/true1", "Embedded row correlations 1")
doCase("c:/projects/integr8_genom/Miner/miner_results/results_synth_external_e_analyze/true2", "Embedded row correlations 2")
doCase("c:/projects/integr8_genom/Miner/miner_results/results_synth_external_e_c_analyze/true1", "Embedded column correlations 1")
doCase("c:/projects/integr8_genom/Miner/miner_results/results_synth_external_e_c_analyze/true2", "Embedded column correlations 2")

dev.off(3)
dev.off(2)