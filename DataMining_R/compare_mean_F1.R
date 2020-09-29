harmonicmean=function(a, b) { 2 * (a*b)/(a+b)}

x <- c(0:20)/10
x <- x + 0.000001
y <- rev(x)
y <- y + 0.000001

ardata <- c(mapply(armean, x, y),mapply(armean, x, rev(y)),mapply(armean, rev(x), y),mapply(armean, rev(x), rev(y)))

meandata <- c((x+y)/2, (x+rev(y))/2, (rev(x)+y)/2, (rev(x)+rev(y))/2)

plot(meandata, ardata)


