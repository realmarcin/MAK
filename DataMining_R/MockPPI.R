


Ib1a <- matrix(rbinom(20*20,1,0.03),ncol=20)
Ib1b <- Ib1a - matrix(rbinom(20*20,1,0.01),ncol=20) + matrix(rbinom(20*20,1,0.01),ncol=20)
Ib1b[which(Ib1b == 2)] <- 1
Ib1b[which(Ib1b == -1)] <- 1
Ib1ab <- Ib1a + Ib1b
#Ib1ab[which(Ib1ab == 2)] <- 1

Ib1c <- Ib1a - matrix(rbinom(20*20,1,0.01),ncol=20) + matrix(rbinom(20*20,1,0.01),ncol=20)
Ib1c[which(Ib1c == 2)] <- 1
Ib1c[which(Ib1c == -1)] <- 1

Ib1abc <- Ib1ab + Ib1c
#Ib1abc[which(Ib1abc == 2)] <- 1
#Ib1abc[which(Ib1abc == 2)] <- 1

Ib2a <- matrix(rbinom(10*10,1,0.03),ncol=10)
Ib2b <- matrix(rbinom(20*20,1,0.03),ncol=20)
Ib2ab <- matrix(apply(Ib2a, c(2), "+", Ib2b), nrow=20, ncol=20)
Ib2ab[6,8] <- 2
Ib2ab[20,12] <- 2
Ib2ab[7,11] <- 2

Ib3a <- matrix(rbinom(50*50,1,0.02),ncol=50)


jpeg("Ib1a.jpeg")
image(Ib1a, xaxt="n", yaxt="n")
dev.off(2)

jpeg("Ib1b.jpeg")
image(Ib1b, xaxt="n", yaxt="n")
dev.off(2)

jpeg("Ib1c.jpeg")
image(Ib1c, xaxt="n", yaxt="n")
dev.off(2)

jpeg("Ib1ab.jpeg")
image(Ib1ab, xaxt="n", yaxt="n", col=c("red", "white", "gray"))
dev.off(2)

jpeg("Ib1abc.jpeg")
image(Ib1abc, xaxt="n", yaxt="n", col=c("red", "white", "gray", "black"))
dev.off(2)


jpeg("Ib2a.jpeg")
image(Ib2a, xaxt="n", yaxt="n")
dev.off(2)

jpeg("Ib2b.jpeg")
image(Ib2b, xaxt="n", yaxt="n")
dev.off(2)

jpeg("Ib2ab.jpeg")
image(Ib2ab, xaxt="n", yaxt="n")
dev.off(2)


jpeg("Ib3a.jpeg")
image(Ib3a, xaxt="n", yaxt="n")
dev.off(2)