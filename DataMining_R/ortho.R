setwd("~/Documents/integr8_genom/data/MO/")

data <- read.table("yeast.ortho.txt",sep="\t",header=T)




data <- read.table("yeast.ortho_binary.txt",sep="\t",header=T)

csums <- colSums(data)
rsums <- rowSums(data)

csums[which(csums > 5 & csums < 600)]
  orth5068.locusId orth475271.locusId orth237561.locusId orth273372.locusId 
                 7                  6                182                  7 
orth148634.locusId orth273371.locusId   orth5480.locusId orth186762.locusId 
                 6                  6                  6                  7 
  orth4959.locusId  orth34391.locusId   orth5518.locusId  orth29833.locusId 
                 6                  6                  6                  6 
 orth82268.locusId  orth28985.locusId orth381046.locusId orth291364.locusId 
                 6                  8                  7                  6 
  orth5530.locusId orth153609.locusId orth224130.locusId  orth54734.locusId 
                 6                  7                  6                  6 
orth121759.locusId orth322104.locusId orth561896.locusId  orth60187.locusId 
                 7                573                368                  7 
orth520522.locusId  orth27293.locusId   orth5334.locusId   orth4897.locusId 
                 8                  7                  6                  6 
  orth4899.locusId  orth43049.locusId orth117179.locusId  orth27337.locusId 
                 7                  6                  6                  6 
  orth4952.locusId 
                 8
              

pdf("./perorganism_all.pdf",width=8,height=11)
hist(csums)
dev.off(2)

pdf("./perorganism_real.pdf",width=8,height=11)
hist(csums[which(csums >= 181)])
dev.off(2)

pdf("./perorf_all.pdf",width=8,height=11)
hist(rsums)
dev.off(2)

rsums <- rowSums(data[,which(csums >= 181)])

pdf("./perorf_real.pdf",width=8,height=11)
hist(rsums)
dev.off(2)

datatrim <- data[,-which(csums < 181)]
write.table(datatrim, "yeast.ortho_binary_trim.txt",sep="\t")