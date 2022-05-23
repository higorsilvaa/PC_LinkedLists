from matplotlib.pyplot import *
import numpy as np

name = ["CoarseList.txt","FineList.txt"]

arq = open(name[1])

addTot = int(arq.readline())
removeTot = int(arq.readline())
containsTot = int(arq.readline())

listSize = []
for i in arq:
	listSize.append(i)

arq.close()

x = range(1,len(listSize)+1)

plot(x, listSize, "--", color="red", linewidth=2.0)
plot(x, listSize, "o", color="red")

ylabel("Tamanho Médio da Lista");
xlabel("Tempo")
#ylim((0,70000))
grid()
title(" ")
show()
 
