import json
import pprint
import pandas as pd
from matplotlib.pyplot import *
import numpy as np
import random

color = ['red', 'blue', 'lime', 'black', 'm', 'c', 'gray', 'violet', 'turquoise']

f = lambda: int(random.random()*100)

y = pd.read_csv("../Resultados/Teste.txt")

aux = []
for j in y["ListSize"]:
	auxaux = []
	for i in list(j.split('.')):
		auxaux.append(int(i))
	aux.append(auxaux)

y["ListSize"] = aux
#print(y)

algoritmo = y.Algoritmo.unique()
thread = y.Threads.unique()
percent = y["Percent(Add.Rem.Con)"].unique()

#Vazão total
z = y.groupby(by=["Percent(Add.Rem.Con)","Algoritmo",'Threads'])["Total"].sum()
#print(z)

for i in percent:
	for j in algoritmo:
		z[i][j].plot(label=j)
	legend()
	xlabel("Threads")
	ylabel("Total de operações")
	title("Vazão total dos algoritmos com " + i)
	grid()
	#show()
	savefig("../Resultados/Vazão"+i+".png",dpi=300)
	clf()


#Vazão por add
z = y[y["Operação"]=='add'].groupby(by=["Percent(Add.Rem.Con)","Algoritmo",'Threads'])["Total"].sum()
#print(z)

for i in percent:
	for j in algoritmo:
		z.loc[i].loc[j].squeeze().plot(label=j)
	legend()
	xlabel("Threads")
	ylabel("Total de operações")
	title("Vazão de add dos algoritmos com " + i)
	#show()
	grid()
	savefig("../Resultados/VazãoAdd"+i+".png",dpi=300)
	clf()

#Vazão por rem
z = y[y["Operação"]=='rem'].groupby(by=["Percent(Add.Rem.Con)","Algoritmo",'Threads'])["Total"].sum()
#print(z)

for i in percent:
	for j in algoritmo:
		z.loc[i].loc[j].squeeze().plot(label=j)
	legend()
	xlabel("Threads")
	ylabel("Total de operações")
	title("Vazão de remove dos algoritmos com " + i)
	#show()
	grid()
	savefig("../Resultados/VazãoRem"+i+".png",dpi=300)
	clf()

#Vazão por con
z = y[y["Operação"]=='con'].groupby(by=["Percent(Add.Rem.Con)","Algoritmo",'Threads'])["Total"].sum()
#print(z)

for i in percent:
	for j in algoritmo:
		z.loc[i].loc[j].squeeze().plot(label=j)
	legend()
	xlabel("Threads")
	ylabel("Total de operações")
	title("Vazão de contains dos algoritmos com " + i)
	#show()
	grid()
	savefig("../Resultados/VazãoCon"+i+".png",dpi=300)
	clf()	

#Tamanho da lista média por método (tempo - eixo x e tamanho - eixo y)

z = y.groupby(by=["Percent(Add.Rem.Con)",'Threads',"Algoritmo"])["ListSize"].first()
#print(z)

for i in percent:
	for j in thread:
		m = 0
		for k in algoritmo:
			h = 61.0/float(len(z[i][j][k]))
			x = np.arange(0.0,60.0+0.1*h,h)
			x[-1] = 60.0
			plot(x,z[i][j][k],label=k,color=color[m])
			m += 1
		legend()
		xlabel("Tempo(s)")
		ylabel("Tamanho da Lista")
		title("Tamanho da lista por algoritmo com " + str(j) + " threads e com " + i)
		#show()
		grid()
		savefig("../Resultados/ListSize"+i+"-"+str(j)+"threads.png",dpi=300)
		clf()


#Tamanho da lista por thread (threads - eixo x e tamanho - eixo y)
z = y.groupby(by=["Percent(Add.Rem.Con)","Algoritmo",'Threads'])["ListSize"].first()
#print(z)

for i in percent:
	for j in algoritmo:
		m = 0
		for k in thread:
			h = 61.0/float(len(z[i][j][k]))
			x = np.arange(0.0,60.0+0.1*h,h)
			x[-1] = 60.0
			plot(x,z[i][j][k],label=str(k)+" threads",color=color[m])
			m += 1
		legend()
		xlabel("Tempo(s)")
		ylabel("Tamanho da Lista")
		title("Tamanho da lista por threads para " + j + " com " + i)
		#show()
		grid()
		savefig("../Resultados/ListSize"+i+j+".png",dpi=300)
		clf()


















