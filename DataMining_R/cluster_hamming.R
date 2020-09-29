setwd("c:/projects/integr8_genom/Miner/miner_results/ROC_r_incr_nono_rand_pG_batch_metro_newbatch_single_v4/results_synth_external_r_incr_nono_rand_pG_batch_metro_sum_poolmax_0.75/hamming")
data <- read.table("MSE_GEERE_ppi_true1_HCL_Hamming.txt",sep="\t",header=T)
dim(data)
row.names(data) <- data[,1]
data <- data[,-1]