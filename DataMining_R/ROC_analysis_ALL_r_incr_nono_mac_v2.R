methods <- c("CC","HCL","ISA","eans","OPSM","xMotif","SAMBA","QUBIC")
seeds <- c(759820,9110656,6942092,651415,7269251,2192957,8290484,3377544,1297150,8057408)
criteria <- c("MSER", "MADR", "Kendall", "MSER_PPI", "MADR_PPI", "Kendall_PPI", "LARSRE_PPI", 
"LARSRE", "GEERE_PPI", "GEERE","MSER_LARSRE_PPI","MADR_LARSRE_PPI","Kendall_LARSRE_PPI", "MSER_LARSRE","MADR_LARSRE","Kendall_LARSRE", 
"MSER_GEERE_PPI", "MADR_GEERE_PPI", "Kendall_GEERE_PPI", "MSER_GEERE", "MADR_GEERE", "Kendall_GEERE", 
"MSE_PPI", "MSE", "MSE_LARSRE_PPI", "MSE_LARSRE", "MSE_GEERE_PPI", "MSE_GEERE", "MSEC_PPI", "MSEC", "MSEC_LARSRE_PPI", 
"MSEC_LASRE", "MSEC_GEERE_PPI", "MSEC_GEERE", "MEAN","MEDRMEAN", "MEDCMEAN")

print(paste("criteria",length(criteria)))

setwd("/Users/marcin/Documents/integr8_genom/Miner/miner_results/")
source("ROC_analysis_ALL_methods.R")
setwd("/Users/marcin/Documents/integr8_genom/Miner/miner_results/ROC_r_incr_nono_rand_pG_batch_metro/")

tallyg <- mat.or.vec(length(methods), length(criteria))
tallye <- mat.or.vec(length(methods), length(criteria))
tallyge <- mat.or.vec(length(methods), length(criteria))
thresholdg <- mat.or.vec(length(methods), length(criteria))
thresholde <- mat.or.vec(length(methods), length(criteria))
thresholdge <- mat.or.vec(length(methods), length(criteria))
sumg <- mat.or.vec(length(methods), length(criteria))
sume <- mat.or.vec(length(methods), length(criteria))
sumge <- mat.or.vec(length(methods), length(criteria))

row.names(tallyg) <- methods
row.names(tallye) <- methods
row.names(tallyge) <- methods
row.names(thresholdg) <- methods
row.names(thresholde) <- methods
row.names(thresholdge) <- methods
row.names(sumg) <- methods
row.names(sume) <- methods
row.names(sumge) <- methods

colnames(tallyg) <- criteria
colnames(tallye) <- criteria
colnames(tallyge) <- criteria
colnames(thresholdg) <- criteria
colnames(thresholde) <- criteria
colnames(thresholdge) <- criteria
colnames(sumg) <- criteria
colnames(sume) <- criteria
colnames(sumge) <- criteria

domax <- 1

for(i in (1:length(methods))) {
print(paste("LOOP",i,methods[i],sep=" "))
pdf(paste("r_incr_nono_rand_pG_batch_metro_g_allcrit_",methods[i],".pdf",sep=""),width=8,height=11)
par(mfrow=c(2,2))
print(paste("true1","g"))
get <- doCase("true1", "g",i,methods[i], tallyg, thresholdg, sumg, criteria, domax)
tallyg <- get[[1]]
thresholdg <- get[[2]]
sumg <- get[[3]]
print(paste("true2","g"))
get <- doCase("true2", "g",i,methods[i], tallyg, thresholdg, sumg, criteria, domax)
tallyg <- get[[1]]
thresholdg <- get[[2]]
sumg <- get[[3]]
print(paste("true3","g"))
get <- doCase("true3", "g",i,methods[i], tallyg, thresholdg, sumg, criteria, domax)
tallyg <- get[[1]]
thresholdg <- get[[2]]
sumg <- get[[3]]
print(paste("true4","g"))
get <- doCase("true4", "g",i,methods[i], tallyg, thresholdg, sumg, criteria, domax)
tallyg <- get[[1]]
thresholdg <- get[[2]]
sumg <- get[[3]]
dev.off(2)
	
pdf(paste("r_incr_nono_rand_pG_batch_metro_e_allcrit_",methods[i],".pdf",sep=""),width=8,height=11)
par(mfrow=c(2,2))
print(paste("true1","e"))
get <- doCase("true1", "e",i,methods[i], tallye, thresholde, sume, criteria, domax)
tallye <- get[[1]]
thresholde <- get[[2]]
sume <- get[[3]]
print(paste("true2","e"))
get <- doCase("true2", "e",i,methods[i], tallye, thresholde, sume, criteria, domax)
tallye <- get[[1]]
thresholde <- get[[2]]
sume <- get[[3]]
print(paste("true3","e"))
get <- doCase("true3", "e",i,methods[i], tallye, thresholde, sume, criteria, domax)
tallye <- get[[1]]
thresholde <- get[[2]]
sume <- get[[3]]
print(paste("true4","e"))
get <- doCase("true4", "e",i,methods[i], tallye, thresholde, sume, criteria, domax)
tallye <- get[[1]]
thresholde <- get[[2]]
sume <- get[[3]]
dev.off(2)

pdf(paste("r_incr_nono_rand_pG_batch_metro_ge_allcrit_",methods[i],".pdf",sep=""),width=8,height=11)
par(mfrow=c(2,2))
print(paste("true1","ge"))
get <- doCase("true1", "ge",i,methods[i], tallyge, thresholdge, sumge, criteria, domax)
tallyge <- get[[1]]
thresholdge <- get[[2]]
sumge <- get[[3]]
print(paste("true2","ge"))
get <- doCase("true2", "ge",i,methods[i], tallyge, thresholdge, sumge, criteria, domax)
tallyge <- get[[1]]
thresholdge <- get[[2]]
sumge <- get[[3]]
print(paste("true3","ge"))
get <- doCase("true3", "ge",i,methods[i], tallyge, thresholdge, sumge, criteria, domax)
tallyge <- get[[1]]
thresholdge <- get[[2]]
sumge <- get[[3]]
print(paste("true4","ge"))
get <- doCase("true4", "ge",i,methods[i], tallyge, thresholdge, sumge, criteria, domax)
tallyge <- get[[1]]
thresholdge <- get[[2]]
sumge <- get[[3]]
dev.off(2)
}

write.table(t(tallyg), file="r_incr_nono_rand_pG_batch_metro_g_tally.txt", sep="\t")
write.table(t(tallye), file="r_incr_nono_rand_pG_batch_metro_e_tally.txt", sep="\t")
write.table(t(tallyge), file="r_incr_nono_rand_pG_batch_metro_ge_tally.txt", sep="\t")


thresholdg <- thresholdg/tallyg
thresholde <- thresholde/tallye
thresholdge <- thresholdge/tallyge
write.table(1-t(thresholdg), file="r_incr_nono_rand_pG_batch_metro_g_threshold.txt", sep="\t")
write.table(1-t(thresholde), file="r_incr_nono_rand_pG_batch_metro_e_threshold.txt", sep="\t")
write.table(1-t(thresholdge), file="r_incr_nono_rand_pG_batch_metro_ge_threshold.txt", sep="\t")

sumg <- sumg/tallyg
sume <- sume/tallye
sumge <- sumge/tallyge
write.table(t(sumg), file="r_incr_nono_rand_pG_batch_metro_g_sum.txt", sep="\t")
write.table(t(sume), file="r_incr_nono_rand_pG_batch_metro_e_sum.txt", sep="\t")
write.table(t(sumge), file="r_incr_nono_rand_pG_batch_metro_ge_sum.txt", sep="\t")