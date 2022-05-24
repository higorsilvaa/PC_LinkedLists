from matplotlib.pyplot import *
import numpy as np

color = ["blue", "red", "black", "green", "pink"]

arq = open("../Resultados/Teste.txt")

x = arq.readlines()

x = ("".join(x)).split("//")
x[-1] = x[-1][:-1]

name = []
addPer = []
remPer = []
conPer = []
addTot = []
remTot = []
conTot = []
listSize = []
nroThreads = []

for m in x:
	n = m.split('\n')

	nroThreads.append(int(n[0]))
	
	addPerAux = []
	remPerAux = []
	conPerAux = []
	addTotAux = []
	remTotAux = []
	conTotAux = []
	listSizeAux = []
	
	#print(n[1:])
	for i in n[1:]:
		k = i.split("#")
		
		name.append(k[0]) #só preciso fazer pro primeiro x
		
		k_aux = k[1].split(";") #duas partes, percentagem e total
		
		k_aux_percent = k_aux[0].split(",")
		addPerAux.append(float(k_aux_percent[0]))
		remPerAux.append(float(k_aux_percent[1]))
		conPerAux.append(float(k_aux_percent[2]))
		
		k_aux_total = k_aux[1].split(",")
		addTotAux.append(int(k_aux_total[0]))
		remTotAux.append(int(k_aux_total[1]))
		conTotAux.append(int(k_aux_total[2]))
		
		aux = []
		for l in k[2].split(","):
			aux.append(int(l))
		listSizeAux.append(aux)
		
		#print(name[-1])
		#print(addPer[-1], remPer[-1], conPer[-1])
		#print(addTot[-1], remTot[-1], conTot[-1])
		#print(listSize[-1])
	addPer.append(addPerAux)
	remPer.append(remPerAux)
	conPer.append(conPerAux)
	
	addTot.append(addTotAux)
	remTot.append(remTotAux)
	conTot.append(conTotAux)
	
	listSize.append(listSizeAux)

arq.close()

#6
#sqrt6 = 2,43
# 2 3
#parte maior que 1 inteira
#n / parte inteira
fig, ax = subplots(2, 2)

for a in ax.flat:
    a.set(xlabel='Tempo', ylabel='Tamanho Médio da Lista')

# Hide x labels and tick labels for top plots and y ticks for right plots.
for a in ax.flat:
    a.label_outer()

for j in range(len(nroThreads)):
	# Isso porque todos tem a mesma probabilidade, a priori, de add, rem e con
	fig.suptitle("Add(%%): %.2f, Remove(%%): %.2f, Contains(%%): %.2f, com %i threads"%(addPer[0][0],remPer[0][0],conPer[0][0],nroThreads[j]))
	ax[int(j/2)][j%2].set_title("%i threads"%(nroThreads[j]))
	for i in range(len(listSize[j])):
		x = range(1,len(listSize[j][i])+1)

		ax[int(j/2)][j%2].plot(x, listSize[j][i], "--", linewidth=2.0, label=name[i], color=color[i])
		ax[int(j/2)][j%2].plot(x, listSize[j][i], "o", color=color[i])

		#ylabel("Tamanho Médio da Lista");
		#xlabel("Tempo")
		#grid()
		#legend()
		#title("Add(%%): %.2f, Remove(%%): %.2f, Contains(%%): %.2f"%(addPer[i],remPer[i],conPer[i]))
		#savefig(name[i]+".png", dpi=300)
		#clf()
	ax[int(j/2)][j%2].set_ylim((6000,23000))
	ax[int(j/2)][j%2].grid()
	ax[int(j/2)][j%2].legend()
	
#fig.ylabel("Tamanho Médio da Lista");
#fig.xlabel("Tempo")
savefig("../Resultados/ListSize%iThreads.png"%nroThreads[j], dpi=300)
clf()
	
