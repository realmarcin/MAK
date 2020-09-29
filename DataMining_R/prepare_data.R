rm(list=ls())
setwd("~/Documents/integr8_genom/Miner/rda/common/fake_data_101217/")
load("./fake621_4r_incr1_12Irand")
ls()

expr_data <- EallRrand
interact_data <- Iall25rand
write.table(expr_data,"synth_r_incr_nono_rand_ppi0.25_101217_expr.txt",sep="\t")
write.table(interact_data,"synth_incr_nono_rand_ppi0.25_101217_ppi.txt",sep="\t")
save(expr_data, interact_data, file="synth_r_incr_nono_rand_ppi0.25_101217")

expr_data <- EallCrand
write.table(expr_data,"synth_c_incr_nono_rand_ppi0.25_101217_expr.txt",sep="\t")
save(expr_data, interact_data, file="synth_c_incr_nono_rand_ppi0.25_101217")

###
setwd("c:/projects/integr8_genom/Miner/rda/")
load("fake621_4r_incr1R")
Data <- Eall
InterF <- Iall
save(Eall, Iall, file="synthdata090621_4r_incr_nono_rand")
write.table(Data, file="synthdata090621_expr_4r_incr_nono_rand.txt")
write.table(InterF, file="synthdata090621_inter_4r_incr_nono_rand.txt")

load("fake621_4r_const1R")
Data <- Eall
InterF <- Iall
save(Eall, Iall, file="synthdata090621_4r_const_nono_rand")
write.table(Data, file="synthdata090621_expr_4r_const_nono_rand.txt")
write.table(InterF, file="synthdata090621_inter_4r_const_nono_rand.txt")