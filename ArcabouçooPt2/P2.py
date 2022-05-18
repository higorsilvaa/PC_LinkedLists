from matplotlib.pyplot import *
import numpy as np

tempos = np.loadtxt(sys.argv[1],dtype=np.float64,delimiter=",")

x = tempos[:,0]
l = ["AddC","RemC","ConC","OpC"]
c = ["blue","red","black","yellow"]#,"green","gray","orange"
#for i in range(1,np.shape(tempos)[1]):
i = int(sys.argv[4])
y = tempos[:,i]
plot(x, y, "--", color=c[i-1], linewidth=2.0)
plot(x, y, "o", color=c[i-1], label=l[i-1])

tempos = np.loadtxt(sys.argv[2],dtype=np.float64,delimiter=",")

x = tempos[:,0]
l = ["AddF","RemF","ConF","OpF"]
c = ["green","gray","orange","pink"]
#for i in range(1,np.shape(tempos)[1]):
y = tempos[:,i]
plot(x, y, "--", color=c[i-1], linewidth=2.0)
plot(x, y, "o", color=c[i-1], label=l[i-1])

ylabel("Operações");
xlabel("Tempo(s)")
#ylim((0,70000))
grid()
title(sys.argv[3])
legend()
show()
 
