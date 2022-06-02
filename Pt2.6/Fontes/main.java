import java.io.*;
import java.util.ArrayList;
import java.util.Random;

public class main {
	static int I = 2;       //Quantidade de threads inicial
	static int P = 2;       //Acréscimo de P threads em I por teste
	static int Q = 8;       //Quantidade de vezes que I será incrementado P vezes
	static double add = 0.4, remove = 0.4, contains = 0.2;//distribuição de probabilidade para operações add, delete, contains
	static int popIni = 100000, popMax = 500000;
	static int warmup = 10;//em seg
	static int exectime = 60;//em seg
	
	static final String DIR = "../Resultados/";
	static final String NAM = "Teste.txt";
	
	static int[][] addOp;
	static int[][] removeOp;
	static int[][] containsOp;
	static int linha;
	static int div; //pega id da primeira thread
	
	static Random random = new Random();

	public static class ThreadS extends Thread {
		GrainList<Integer> GL;
		
		public ThreadS(GrainList<Integer> GL){
			this.GL = GL;
		}
	
		public void run() {
			double num_rand;
			int coluna = ((int)getId())%div;
			boolean check;
			
			while(true){
				num_rand = random.nextDouble(); //entre 0 e 1
				
				if(num_rand < add){
					check = GL.add(Integer.valueOf(random.nextInt(popMax)));
					try{
						//if(check == true)
							addOp[linha][coluna] ++;
					} catch (ArrayIndexOutOfBoundsException e) {}
				}
				else if(num_rand < remove+add){
					check = GL.remove(Integer.valueOf(random.nextInt(popMax)));
					try{
						//if(check == true)
							removeOp[linha][coluna] ++;
					} catch (ArrayIndexOutOfBoundsException e) {}
				}
				else{
					GL.contains(Integer.valueOf(random.nextInt(popMax)));
					try{
						containsOp[linha][coluna] ++;
					} catch (ArrayIndexOutOfBoundsException e) {}
				}
			}
		}
	}
	
	//"Threads,Algoritmo,Operação,Total,Percent(Add.Rem.Con),ListSize\n"
	//write(arqname, N, name, addTot, removeTot, containsTot, listSize.get(linha));
	
	//escreve os resultados num arquivo
	public static void write(String name, int N, String nameMethod,
		int addTot, int remTot, int conTot,
		ArrayList<Integer> listSize)
	{
		File arquivo = new File(name);
		String aux;
		try( FileWriter fw = new FileWriter(arquivo,true)){
			//2,CoarseList,add,50,40.40.20,1.2.3.5
			aux = "";
			for(int i = 0; i < listSize.size(); i ++){
				aux += listSize.get(i);
				if(i+1 != listSize.size())
					aux += ".";
			}
			
			fw.write(N+","+nameMethod+",add,"+addTot+","+(int)Math.ceil(add*100)+"."+(int)Math.ceil(remove*100)+"."+(int)Math.ceil(contains*100)+","+aux+"\n");
			fw.write(N+","+nameMethod+",rem,"+remTot+","+(int)Math.ceil(add*100)+"."+(int)Math.ceil(remove*100)+"."+(int)Math.ceil(contains*100)+","+aux+"\n");
			fw.write(N+","+nameMethod+",con,"+conTot+","+(int)Math.ceil(add*100)+"."+(int)Math.ceil(remove*100)+"."+(int)Math.ceil(contains*100)+","+aux+"\n");
			fw.flush();
		}catch(IOException ex){
		  ex.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		if(remove+add >= 1.0){
			System.out.println("Algo deu errado!");
			System.exit(1);
		}
		
		long tempo;
		ArrayList<ThreadS> threads;
		ArrayList<GrainList<Integer>> GL;
		String name = "", arqname;
		ArrayList<ArrayList<Integer>> listSize;
		int addTot, removeTot, containsTot, avgListSize;
		
		try( FileWriter fw = new FileWriter(DIR+NAM,false) ){
			fw.write("Threads,Algoritmo,Operação,Total,Percent(Add.Rem.Con),ListSize\n");
			fw.flush();
		}catch(IOException ex){
		  ex.printStackTrace();
		}
		
		for(; add >= 0.1; add -= 0.2, remove += 0.2){
			System.out.println("==========================================================");
			System.out.println("PROCENTAGEM: "+"("+((int)Math.ceil(add*100))+","+((int)Math.ceil(remove*100))+","+((int)Math.ceil(contains*100))+")");
		
			for(int N = I; N < Q*P+I; N+=P){
				System.out.println("----------------------------------------------------------");
				System.out.println(N + " THREADS");
			
				GL = new ArrayList<GrainList<Integer>>();
				
				GL.add(new CoarseList<Integer>());
				GL.add(new FineList<Integer>());
				GL.add(new LazyList<Integer>());
				GL.add(new OptimisticList<Integer>());
				GL.add(new LockFreeList<Integer>());
			
				listSize = new ArrayList<ArrayList<Integer>>();
				
				addOp = new int[GL.size()][N];
				removeOp = new int[GL.size()][N];
				containsOp = new int[GL.size()][N];
				
				for(int i = 0; i < GL.size(); i ++)
					listSize.add(new ArrayList<Integer>());
				
				System.out.println("\nTEMPO DE INICIALIZAÇÃO DOS MÉTODOS");
				
				for(int i = 0; i < GL.size(); i ++){
					tempo = System.currentTimeMillis();
					
					//inicializar aleatoriamente para ver o impacto nos resultados
					for(int j = popIni-1; j >= 0 ; j --)
						GL.get(i).add(Integer.valueOf(j));
						
					System.out.println((System.currentTimeMillis()-tempo)/Math.pow(10.0,3.0));
				}
				
				for(linha = 0; linha < GL.size(); linha ++){
					if(GL.get(linha) instanceof CoarseList)
						name = "CoarseList";
					else if(GL.get(linha) instanceof FineList)
						name = "FineList";
					else if(GL.get(linha) instanceof LazyList)
						name = "LazyList";
					else if(GL.get(linha) instanceof OptimisticList)
						name = "OptimisticList";
					else if(GL.get(linha) instanceof LockFreeList)
						name = "LockFreeList";
					
					System.out.println("\n"+name);
					
					threads = new ArrayList<ThreadS>();
					for(int j = 0; j < N; j ++)
						threads.add(new ThreadS(GL.get(linha)));
					
					div = (int)threads.get(0).getId(); //pega o id da primeira thread
					
					for(ThreadS t : threads)
						t.start();
					
					// Espera warmup
					try {
						Thread.sleep(warmup*1000);
					} catch (InterruptedException e) {};
					
					//desconsidera todos os valores pegos pelo warmup
					for(int j = 0; j < N; j ++){
						addOp[linha][j] = 0;
						removeOp[linha][j] = 0;
						containsOp[linha][j] = 0;
					}
					
					//epera tempo de execução
					//conta o tamanho da lista a cada 1 segundo
					tempo = System.currentTimeMillis();
					while(tempo+(exectime*1000) > System.currentTimeMillis()) {
						listSize.get(linha).add(GL.get(linha).size());
						System.out.println(listSize.get(linha).get(listSize.get(linha).size()-1));
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {};
					}
					
					// Encerra as threads
					try {
						for(ThreadS t : threads)
							t.interrupt();
					} catch (Exception e) {};
					
					// Calcula o número de operações total por operação
					addTot = 0;
					removeTot = 0;
					containsTot = 0;
					for(int j = 0; j < N; j ++){
						addTot += addOp[linha][j];
						removeTot += removeOp[linha][j];
						containsTot += containsOp[linha][j];
					}
					
					// Calcula o tamanho média da lista
					avgListSize = 0;
					for(int j = 0; j < listSize.get(linha).size(); j ++)
						avgListSize += listSize.get(linha).get(j);
					avgListSize /= listSize.get(linha).size();
					
					System.out.println("\nSTATISTICS");
					System.out.println("Average list size: " + avgListSize);
					System.out.println("Final list size:   " + GL.get(linha).size());
					System.out.println("Add/sec:           " + (addTot/exectime));
					System.out.println("Remove/sec:        " + (removeTot/exectime));
					System.out.println("Contains/sec:      " + (containsTot/exectime)+"\n");
					
					write(DIR+NAM, N, name, addTot, removeTot, containsTot, listSize.get(linha));
				}
			}
			if((int)Math.ceil(add*100) == 40 && (int)Math.ceil(remove*100) == 40){
				add = 0.9;     //vai ficar igual a 0.7
				remove = -0.1; //vai ficar igual a 0.1
			}
		}
		System.exit(1); //de alguma forma ele não quer encerrar o programa
		
		//vazão total = soma de tomas as operações
		//vazão por operação
	}
}
