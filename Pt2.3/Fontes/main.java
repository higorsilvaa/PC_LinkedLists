import java.io.*;
import java.util.ArrayList;
import java.util.Random;

public class main {
	static int I = 2;       //Quantidade de threads inicial
	static int P = 2;       //Acréscimo de P threads em I por teste
	static int Q = 4;       //Quantidade de vezes que I será incrementado P vezes
	//static int numThreads = 10; //número de threads
	static double add = 0.4, remove = 0.4, contains = 0.2;//distribuição de probabilidade para operações add, delete, contains
	static int popIni = 10000, popMax = 15000;
	static int warmup = 1;//em seg
	static int exectime = 10;//em seg
	
	static int[][] addOp;
	static int[][] removeOp;
	static int[][] containsOp;
	static int linha;
	static int div = 1; //id da primeira thread
	
	static Random random = new Random();

	public static class ThreadS extends Thread {
		GrainList<Integer> GL;
		
		public ThreadS(GrainList<Integer> GL){
			this.GL = GL;
		}
	
		public void run() {
			double num_rand;
			int coluna = ((int)getId())%div;
			
			while(true){
				num_rand = random.nextDouble();
				
				if(num_rand < add){
					GL.add(Integer.valueOf(random.nextInt(popMax)));
					try{
						addOp[linha][coluna] ++;
					} catch (ArrayIndexOutOfBoundsException e) {
						//System.out.println("Add coluna " + coluna);
					}
				}
				else if(num_rand < remove+add){
					GL.remove(Integer.valueOf(random.nextInt(popMax)));
					try{
						removeOp[linha][coluna] ++;
					} catch (ArrayIndexOutOfBoundsException e) {
						//System.out.println("Remove [linha,coluna]: [" + linha + "," + coluna + "]");
					}
				}
				else if(num_rand < contains+remove+add){
					GL.contains(Integer.valueOf(random.nextInt(popMax)));
					try{
						containsOp[linha][coluna] ++;
					} catch (ArrayIndexOutOfBoundsException e) {
						//System.out.println("Contains coluna " + coluna);
					}
				}
			}
		}
	}
	
	//escreve os resultados num arquivo
	public static void write(String name, String nameMethod,
		int addTot, int removeTot, int containsTot,
		ArrayList<Integer> listSize,
		String separator)
	{
		File arquivo = new File(name+".txt");
		try( FileWriter fw = new FileWriter(arquivo,true) ){
			fw.write(nameMethod+"#");
			fw.write(add+","+remove+","+contains+";");
			fw.write(addTot+","+removeTot+","+containsTot+"#");
			for(int l=0; l < listSize.size(); l ++){
				if(l+1 != listSize.size())
					fw.write(listSize.get(l)+",");
				else
					fw.write(listSize.get(l)+separator);
			}
			fw.flush();
		}catch(IOException ex){
		  ex.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		if(contains+remove+add > 1.0){
			System.out.println("Algo deu errado!");
			System.exit(-1);
		}
		
		File arquivo;
		String name = "";
		
		ArrayList<ThreadS> threads;
		ArrayList<GrainList<Integer>> GL;
		long tempo, aux;
		String separator;
		
		int addTot, removeTot, containsTot, avgListSize;
		ArrayList<ArrayList<Integer>> listSize;
		
		//Realiza o experimento para cada algoritmo
		for(int N = I; N < Q*P+I; N+=P){
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
			
			for(int i = 0; i < GL.size(); i ++){
				listSize.add(new ArrayList<Integer>());
				for(int j = 0; j < N; j ++){
					addOp[i][j] = 0;
					removeOp[i][j] = 0;
					containsOp[i][j] = 0;
				}
			}
			
			for(int i = 0; i < GL.size(); i ++){
				tempo = System.currentTimeMillis();
				
				for(int j = 0; j < popIni; j ++)
					GL.get(i).add(Integer.valueOf(j));
					
				System.out.println((System.currentTimeMillis()-tempo)/Math.pow(10.0,3.0));
			}
			
			try( FileWriter fw = new FileWriter("../Resultados/Teste.txt",true) ){
				fw.write(N+"\n");
				fw.flush();
			}catch(IOException ex){
			  ex.printStackTrace();
			}
			
			for(linha = 0; linha < GL.size(); linha ++){
				if(GL.get(linha) instanceof CoarseList){
					name = "CoarseList";
				}
				else if(GL.get(linha) instanceof FineList){
					name = "FineList";
				}
				else if(GL.get(linha) instanceof LazyList){
					name = "LazyList";
				}
				else if(GL.get(linha) instanceof OptimisticList){
					name = "OptimisticList";
				}
				else if(GL.get(linha) instanceof LockFreeList){
					name = "LockFreeList";
				}
				
				System.out.println(name);
				
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
				
				if(linha+1 != GL.size() || (linha+1 == GL.size() && N+P >= Q*P+I))
					separator = "\n";
				else
					separator = "//";
				
				write("../Resultados/Teste", name, addTot/exectime, removeTot/exectime, containsTot/exectime, listSize.get(linha), separator);
			}
		}
		System.exit(1); //de alguma forma ele não quer encerrar o programa
	}
}
