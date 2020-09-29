library("scatterplot3d")
setwd("~/Documents/integr8_genom/Miner/miner_results/yeast/EXPR_round12_merge_refine/")
data <- read.table("./ListMergeMembers_Loverlap.txt",sep="\t",header=F)

grayX <- rgb(30,30,30,50,maxColorValue =255)

scatterplot3d(data$V11,data$V13, data$V15, highlight.3d = TRUE, angle = 120,
col.axis = "blue", col.grid = "lightblue", cex.axis = 1.3,
cex.lab = 1.1, cex.symbols=0.2, main = "Gratio vs Eratio vs overlap", pch = 20,xlab="Gene ratio A/B",ylab="Exp. ratio B/A",zlab="overlap")

plot(data$V11,data$V13, xlab="Gene ratio A/B",ylab="Exp. ratio B/A",col=grayX)
plot(data$V11,data$V13, xlab="Gene ratio",ylab="Exp. ratio", xlim=c(0,8),ylim=c(0,8),col=grayX)

elevation.df = data.frame(x = data$V11,
  y = data$V13, z = data$V15)
  
elevation.loess = loess(z ~ x*y, data = elevation.df, degree = 2, span = 0.75)
elevation.fit = expand.grid(list(x = seq(1.25, 8, 0.05), y = seq(1.25, 4, 0.0025)))
z = predict(elevation.loess, newdata = elevation.fit)
elevation.fit$Height = as.numeric(z)

library(ggplot2)
###
ggplot(elevation.fit, aes(x, y, fill = Height)) + geom_tile() +
  xlab("Gene ratio A/B") + ylab("Exp. ratio B/A") +
  opts(title = "Bicluster overlaps on axis ratio grid") +
  scale_fill_gradient(limits = c(0, 0.7132789), low = "black", high = "white") +
  scale_x_continuous(expand = c(0,0)) +
  scale_y_continuous(expand = c(0,0))
  

###3D contour
library(rgl)
data(volcano)
z <- 2 * volcano # Exaggerate the relief
x <- 10 * (1:nrow(z)) # 10 meter spacing (S to N)
y <- 10 * (1:ncol(z)) # 10 meter spacing (E to W)
zlim <- range(y)
zlen <- zlim[2] - zlim[1] + 1
colorlut <- terrain.colors(zlen,alpha=0) # height color lookup table
col <- colorlut[ z-zlim[1]+1 ] # assign colors to heights for each point
open3d()
rgl.surface(data$V11,data$V13, data$V15, color=col, alpha=0.75, back="lines")


colorlut <- heat.colors(zlen,alpha=1) # use different colors for the contour map
col <- colorlut[ z-zlim[1]+1 ] 
rgl.surface(x, y, matrix(1, nrow(z), ncol(z)),color=col, back="fill")