library(RColorBrewer)
 library(gplots)
 library(zoo)
 
 data <- matrix(0,100,10)
 
 for(i in 1:15) {
 data[i,] <- seq( 0.2, 0.39, 0.02) * runif(10,0.3,1) + sign(runif(1,-1,1)) *runif(10,0,0.01)#  + sign(runif(1,-1,1)) *  runif(1,0,i/100.0)
 }
 
  for(i in 16:79) {
 data[i,] <- runif(10,-0.1,0.1) * runif(10,0.5,1.5) + sign(runif(1,-1,1)) * runif(1,0,0.3)
 }
 
 
  for(i in 80:100) {
 data[i,] <- - seq( 0.2, 0.39, 0.02) *runif(10,0.3,1) - sign(runif(1,-1,1)) *runif(10,0,0.01)#  + sign(runif(1,-1,1)) * runif(1,0,i/100.0)
 }
 
 mypalette <- bluered(20)
 heatmap.2(data, Colv=FALSE,col=mypalette, trace="none", xlab = "Time", ylab = "Proteins")
 
 
 plot(data, type="n", xlim=c(1,10), ylim=c(-0.4,0.4),ylab="Production",xlab="Time")
 for(i in 1:100) {
  if(i < 15) {
  col <- "red"
  } else if(i < 80 && i > 15) {
  col <- "gray"
  } else if(i> 80) {
  col <- "blue"
  }
 lines(data[i,], col=col)
 }