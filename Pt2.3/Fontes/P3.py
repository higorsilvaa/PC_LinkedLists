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

#Percorre o número de threads
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
	
	#Percorre os métodos
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

addPer = np.array(addPer)
remPer = np.array(remPer)
conPer = np.array(conPer)
addTot = np.array(addTot)
remTot = np.array(remTot)
conTot = np.array(conTot)
listSize = np.array(listSize)

maxfind = np.max(listSize)
minfind = np.min(listSize)

# TAMANHO MÉDIO DA LISTA POR ALGORITMO NO TEMPO
for j in range(len(nroThreads)):
	# Isso porque todos tem a mesma probabilidade, a priori, de add, rem e con
	title("Add(%%): %.2f, Remove(%%): %.2f, Contains(%%): %.2f, com %i threads"%(addPer[0][0],remPer[0][0],conPer[0][0],nroThreads[j]))
	for i in range(len(listSize[j])):
		x = range(1,len(listSize[j][i])+1)

		plot(x, listSize[j][i], "--", linewidth=2.0, label=name[i], color=color[i])
		plot(x, listSize[j][i], "o", color=color[i])

	ylim((minfind-0.05*minfind,maxfind+0.05*maxfind))
	grid()
	legend()
	ylabel("Tamanho Médio da Lista");
	xlabel("Tempo(s)")
	savefig("../Resultados/ListSize%iThreads.png"%nroThreads[j], dpi=300)
	clf()

# TAMANHO MÉDIO DA LISTA POR ALGORITMO POR THREAD
for i in range(len(listSize)):
	# Isso porque todos tem a mesma probabilidade, a priori, de add, rem e con
	title(name[i]+", Add(%%): %.2f, Remove(%%): %.2f, Contains(%%): %.2f"%(addPer[0][0],remPer[0][0],conPer[0][0]))
	for j in range(len(nroThreads)):
		x = range(1,len(listSize[j][i])+1)
		plot(x, listSize[j][i], "--", linewidth=2.0, label=str(nroThreads[j])+" threads", color=color[j])
		plot(x, listSize[j][i], "o", color=color[j])
	
	#maxfind = np.max(listSize[:][i])
	#minfind = np.min(listSize[:][i])
	#ylim((minfind-0.05*minfind,maxfind+0.05*maxfind))
	grid()
	legend()
	ylabel("Tamanho Médio da Lista");
	xlabel("Tempo(s)")
	savefig("../Resultados/ListSize%s.png"%name[i], dpi=300)
	clf()

maxfind_add = np.max(addTot)
minfind_add = np.min(addTot)
maxfind_rem = np.max(remTot)
minfind_rem = np.min(remTot)
maxfind_con = np.max(conTot)
minfind_con = np.min(conTot)

maxfind = max([maxfind_add, maxfind_rem, maxfind_con])
minfind = min([minfind_add, minfind_rem, minfind_con])

for i in range(len(listSize[0])):
	# Isso porque todos tem a mesma probabilidade, a priori, de add, rem e con
	title(name[i])
	
	plot(nroThreads, addTot[:,i], "--", linewidth=2.0, label="Add", color=color[0])
	plot(nroThreads, addTot[:,i], "o", color=color[0])
	plot(nroThreads, remTot[:,i], "--", linewidth=2.0, label="Remove", color=color[1])
	plot(nroThreads, remTot[:,i], "o", color=color[1])
	plot(nroThreads, conTot[:,i], "--", linewidth=2.0, label="Contains", color=color[2])
	plot(nroThreads, conTot[:,i], "o", color=color[2])
	
	ylim((minfind-0.05*minfind,maxfind+0.05*maxfind))
	
	grid()
	legend()
	ylabel("Vazão");
	xlabel("Nro Threads")
	savefig("../Resultados/Operations%s.png"%name[i], dpi=300)
	clf()


	
