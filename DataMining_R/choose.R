
choosedata <- mat.or.vec((250-2)*(50-2),1)
for(i in 2:250) {
        for(j in 2:50) {
    choosedata[(i-1)*(j-1)+(i-2)] <- choose(1000,i) * choose(100,j)
    }
}

choosedata_mat <- mat.or.vec((250-2),(50-2))
for(i in 2:250) {
        for(j in 2:50) {
                val <- choose(1000,i) * choose(100,j) 
                #cat((i-1),(j-1),val,"\n",sep=" ")
                if(is.na(val) || is.nan(val) || is.na(log(val))) {
                   cat((i-1),(j-1),val,"choose(1000,i) * choose(100,j) \n",sep=" ")     
                }
                choosedata_mat[(i-1)][(j-1)] <- val
    }
}

pdf("choose.pdf",width=8.5,height=11)
plot(log(choosedata), type='l')
dev.off(2)

pdf("choose_mat.pdf",width=8.5,height=11)
image(log(choosedata_mat))
dev.off(2)