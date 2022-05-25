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
	title("Tamanho da lista por algoritmo com (%i%%,%i%%,%i%%) e %i threads"%(addPer[0][0]*100,remPer[0][0]*100,conPer[0][0]*100,nroThreads[j]))
	for i in range(len(listSize[j])):
		x = range(1,len(listSize[j][i])+1)

		plot(x, listSize[j][i], "--", linewidth=2.0, label=name[i], color=color[i])
		plot(x, listSize[j][i], "o", color=color[i])

	ylim((minfind-0.05*minfind,maxfind+0.05*maxfind))
	grid()
	legend()
	ylabel("Tamanho");
	xlabel("Tempo(s)")
	savefig("../Resultados/ListSize%iThreads(%i%%,%i%%,%i%%).png"%(nroThreads[j],addPer[0][0]*100,remPer[0][0]*100,conPer[0][0]*100), dpi=300)
	clf()

# TAMANHO MÉDIO DA LISTA POR ALGORITMO POR THREAD
for i in range(len(listSize[0])):
	# Isso porque todos tem a mesma probabilidade, a priori, de add, rem e con
	title("Tamanho da lista por thread utilizando %s com (%i%%,%i%%,%i%%)"%(name[i],addPer[0][0]*100,remPer[0][0]*100,conPer[0][0]*100))
	for j in range(len(nroThreads)):
		x = range(1,len(listSize[j][i])+1)
		plot(x, listSize[j][i], "--", linewidth=2.0, label=str(nroThreads[j])+" threads", color=color[j])
		plot(x, listSize[j][i], "o", color=color[j])
	
	grid()
	legend()
	ylabel("Tamanho");
	xlabel("Tempo(s)")
	savefig("../Resultados/ListSize%s(%i%%,%i%%,%i%%).png"%(name[i],addPer[0][0]*100,remPer[0][0]*100,conPer[0][0]*100), dpi=300)
	clf()

barWidth = 0.25

r1 = np.arange(len(addTot[:,0]))
r2 = [x + barWidth for x in r1]
r3 = [x + barWidth for x in r2]

for i in range(len(listSize[0])):
	bar(r1, addTot[:,i], color="red", width=barWidth, label="Add")
	bar(r2, remTot[:,i], color="orange", width=barWidth, label="Remove")
	bar(r3, conTot[:,i], color="gold", width=barWidth, label="Contains")

	xticks([r+barWidth for r in range(len(addTot[:,i]))],nroThreads)
	ylabel("Vazão")
	xlabel("Threads")
	title("Vazão do %s com (%i%%,%i%%,%i%%)"%(name[i],addPer[0][i]*100,remPer[0][i]*100,conPer[0][i]*100))
	legend()
	savefig("../Resultados/Vazão%s(%i%%,%i%%,%i%%).png"%(name[i],addPer[0][0]*100,remPer[0][0]*100,conPer[0][0]*100), dpi=300)
	clf()

	
