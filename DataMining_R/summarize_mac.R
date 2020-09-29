source("~/Documents/integr8_genom/Miner/miner_results/external_evaluation_batch_r_const_incr_methods_mac.R")

doSeriesScatter("r_incr","r_incr_pG")
doSeriesScatter("r_incr_pG","r_incr_pG_metro")
doSeriesScatter("r_incr_pG_metro","r_incr_pG_metro_10pre")
doSeriesScatter("r_incr_pG_metro_10pre","r_incr_pG_metro_10pre_rand")
doSeriesScatter("r_incr_pG_metro","r_incr_pG_metro_10pre_rand")

doSeries("r_incr")
doSeries("r_incr_pG")
doSeries("r_incr_pG_metro")
doSeries("r_incr_pG_metro_10pre")
doSeries("r_incr_pG_metro_10pre_rand")
doSeries("r_incr_pG_metro_10pre_randgene")

doSeries("r_const")
doSeries("r_const_pG")
doSeries("r_const_pG_metro")
doSeries("r_const_pG_metro_10pre")
doSeries("r_const_pG_metro_10pre_rand")
doSeries("r_const_pG_metro_10pre_randgene")

doSeries("r_incr_nono")
doSeries("r_incr_nono_pG")
doSeries("r_incr_nono_pG_metro")
doSeries("r_incr_nono_pG_metro_10pre")
doSeries("r_incr_nono_pG_metro_10pre_rand")
doSeries("r_incr_nono_pG_metro_10pre_randgene")

doSeries("r_const_nono")
doSeries("r_const_nono_pG")
doSeries("r_const_nono_pG_metro")
doSeries("r_const_nono_pG_metro_10pre")
doSeries("r_const_nono_pG_metro_10pre_rand")
doSeries("r_const_nono_pG_metro_10pre_randgene")


doSeriesScatter("r_incr","r_incr_pG")