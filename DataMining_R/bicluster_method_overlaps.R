setwd("~/Documents/integr8_genom/Miner/miner_results/yeast/AllvsAll/")

coal_vs_cmon <- read.table("coalesce_0.5_vs_cmonkey_0.5.txt_gene.txt",sep="\t",header=T,row.names=1)
mak_vs_cmon <- read.table("MAK_0.5_vs_cmonkey_0.5.txt_gene.txt",sep="\t",header=T,row.names=1)
mak_vs_coal <- read.table("MAK_0.5_vs_coalesce_0.5.txt_gene.txt",sep="\t",header=T,row.names=1)
isa_vs_mak <- read.table("MAK_0.5_vs_ISA_0.5.txt_gene.txt",sep="\t",header=T,row.names=1)
isa_vs_cmon <- read.table("cmonkey_0.5_vs_ISA_0.5.txt_gene.txt",sep="\t",header=T,row.names=1)
isa_vs_coal <- read.table("coalesce_0.5_vs_ISA_0.5.txt_gene.txt",sep="\t",header=T,row.names=1)

breaks <- seq(0, 0.65, 0.05)
hist1 <- hist(coal_vs_cmon[lower.tri(as.matrix(coal_vs_cmon))], breaks=breaks)
hist2 <- hist(mak_vs_cmon[lower.tri(as.matrix(mak_vs_cmon))], breaks=breaks)
hist3 <- hist(mak_vs_coal[lower.tri(as.matrix(mak_vs_coal))], breaks=breaks)
hist4 <- hist(isa_vs_mak[lower.tri(as.matrix(isa_vs_mak))], breaks=breaks)
hist5 <- hist(isa_vs_cmon[lower.tri(as.matrix(isa_vs_cmon))], breaks=breaks)
hist6 <- hist(isa_vs_coal[lower.tri(as.matrix(isa_vs_coal))], breaks=breaks)

comparelab <- c("MAK(expr.) - cMonkey", "MAK(expr.) - COALESCE ", "MAK(expr.) - ISA", "ISA - cMonkey", "ISA - COALESCE","cMonkey - COALESCE")
#cols <- c("purple", "red","orange", "blue","green", "gray")
cols <- c("black", "black","black", "gray","gray", "gray")
ltys <- c(1, 2, 3, 1, 2, 3)
plot(breaks[-length(breaks)], log10(hist2$counts), type="l", xlim=c(0,0.65), xlab="Gene Jaccard index for all pairs of biclusters", ylab="log10(count)", col=cols[1], lwd=2, lty=ltys[1])
lines(breaks[-length(breaks)], log10(hist3$counts), col=cols[2], lwd=2, lty=ltys[2])
lines(breaks[-length(breaks)], log10(hist4$counts), col=cols[3], lwd=2, lty=ltys[3])
lines(breaks[-length(breaks)], log10(hist5$counts), col=cols[4], lwd=2, lty=ltys[4])
lines(breaks[-length(breaks)], log10(hist6$counts), col=cols[5], lwd=2, lty=ltys[5])
lines(breaks[-length(breaks)], log10(hist1$counts), col=cols[6], lwd=2, lty=ltys[6])
legend(0.35, 4.7, comparelab, col=cols, lwd=2,lty=ltys)

###mean MAK vs other VS other vs other

meanMAK <- mean(c(mak_vs_cmon[lower.tri(as.matrix(mak_vs_cmon))], mak_vs_coal[lower.tri(as.matrix(mak_vs_coal))], isa_vs_mak[lower.tri(as.matrix(isa_vs_mak))]))
meanOTHER <- mean(c(coal_vs_cmon[lower.tri(as.matrix(coal_vs_cmon))], isa_vs_cmon[lower.tri(as.matrix(isa_vs_cmon))], isa_vs_coal[lower.tri(as.matrix(isa_vs_coal))]))


###max per row
max_coal_vs_cmon <- apply(coal_vs_cmon, 1, max)
max_mak_vs_cmon <- apply(mak_vs_cmon, 1, max)
max_mak_vs_coal <- apply(mak_vs_coal, 1, max)
max_isa_vs_mak <- apply(isa_vs_mak, 1, max)
max_isa_vs_cmon <- apply(isa_vs_cmon, 1, max)
max_isa_vs_coal <- apply(isa_vs_coal, 1, max)

meanMAK <- mean(c(max_mak_vs_cmon, max_mak_vs_coal, max_isa_vs_mak))
meanOTHER <- mean(c(max_coal_vs_cmon, max_isa_vs_cmon, max_isa_vs_coal))

breaks <- seq(0, 0.65, 0.05)
hist1 <- hist(max_coal_vs_cmon, breaks=breaks)
hist2 <- hist(max_mak_vs_cmon, breaks=breaks)
hist3 <- hist(max_mak_vs_coal, breaks=breaks)
hist4 <- hist(max_isa_vs_mak, breaks=breaks)
hist5 <- hist(max_isa_vs_cmon, breaks=breaks)
hist6 <- hist(max_isa_vs_coal, breaks=breaks)

comparelab <- c("MAK(expr.) - cMonkey", "MAK(expr.) - COALESCE ", "MAK(expr.) - ISA", "ISA - cMonkey", "ISA - COALESCE","cMonkey - COALESCE")
#cols <- c("purple", "red","orange", "blue","green", "gray")
cols <- c("black", "black","black", "gray","gray", "gray")
ltys <- c(1, 2, 3, 1, 2, 3)
plot(breaks[-length(breaks)], log10(hist2$counts), type="l", xlim=c(0,0.6), ylim=c(0,2.3), xlab="Maximum gene Jaccard index between two sets of biclusters", ylab="log10(count)", col=cols[1], lwd=2, lty=ltys[1])
lines(breaks[-length(breaks)], log10(hist3$counts), col=cols[2], lwd=2, lty=ltys[2])
lines(breaks[-length(breaks)], log10(hist4$counts), col=cols[3], lwd=2, lty=ltys[3])
lines(breaks[-length(breaks)], log10(hist5$counts), col=cols[4], lwd=2, lty=ltys[4])
lines(breaks[-length(breaks)], log10(hist6$counts), col=cols[5], lwd=2, lty=ltys[5])
lines(breaks[-length(breaks)], log10(hist1$counts), col=cols[6], lwd=2, lty=ltys[6])
legend(0.3, 2.3, comparelab, col=cols, lwd=2,lty=ltys)



###
dens1 <- density(coal_vs_cmon[lower.tri(as.matrix(coal_vs_cmon))])
dens2 <- density(mak_vs_cmon[lower.tri(as.matrix(mak_vs_cmon))])
dens3 <- density(mak_vs_coal[lower.tri(as.matrix(mak_vs_coal))])

plot(dens1$x, log2(dens1$y), col="red", xlim=c(0,0.5), type="p")
points(dens2$x, log2(dens2$y), col="black")
points(dens3$x, log2(dens3$y),, col="blue")