import java.io.*;
import java.util.ArrayList;
import java.util.Random;

public class main {
	static int numThreads = 10; //número de threads
	static int itPerThread = 100000000;//número de iterações por thread
	static double add = 0.25, remove = 0.25, contains = 0.5;//distribuição de probabilidade para operações add, delete, contains
	//o espaço de chaves para sortear operações. Como assim??
	//Se vai usar tempo de experimento, pra que usar iterações por thread???
	static int popIni = 10000, popMax = 500000;
	//quando chegar na popMax eu simplesmente não deixo mais adicionar na lista???
	static int warmup = 10*1000;//em mseg
	static int exectime = 60*1000;//em mseg
	//ficar verificando se não deu tempo antes de cancelar o processo??
	static volatile int numAdd = 0, numRem = 0, numCon = 0;
	static int numAddTot = 0, numRemTot = 0, numConTot = 0;
	
	//utilizar contains para contar
	
	static Random random = new Random();

	public static class ThreadS extends Thread {
		GrainList<Integer> GL;
		
		public ThreadS(GrainList<Integer> GL){
			this.GL = GL;
		}
		
		public void setGL(GrainList<Integer> GL){
			this.GL = GL;
		}
	
		public void run() {
			double num_rand;
			for(int i = 0; i < itPerThread; i++){
				num_rand = random.nextDouble();
				
				if(num_rand < add){
					GL.add(Integer.valueOf(random.nextInt(100000000))); //100000000 isso define o tamanho max
					numAdd++;
				}
				else if(num_rand < remove+add){
					GL.remove(Integer.valueOf(random.nextInt(100000000)));
					numRem++;
				}
				else if(num_rand < contains+remove+add){
					GL.contains(Integer.valueOf(random.nextInt(100000000)));
					numCon++;
				}
				//System.out.println("T"+getId());
			}
		}
	}
	
	public static void main(String[] args) {
		if(contains+remove+add > 1.0){
			System.out.println("Algo deu errado!");
			System.exit(-1);
		}
		
		String nameArq;
		File arquivo;
		ArrayList<ThreadS> threads;
		CoarseList<Integer> x;
		ArrayList<GrainList<Integer>> GL = new ArrayList<GrainList<Integer>>();
		long tempo, aux;
		
		GL.add(new CoarseList<Integer>());
		GL.add(new FineList<Integer>());
		
		for(int i = 0; i < GL.size(); i ++){
			tempo = System.currentTimeMillis();
			System.out.println(i);
			for(int j = 0; j < popIni; j ++)
				GL.get(i).add(Integer.valueOf(j));
			System.out.println((System.currentTimeMillis()-tempo)/Math.pow(10.0,3.0));
		}
		
		for(int i = 0; i < GL.size(); i ++){
			nameArq = "GL";
			arquivo = new File(nameArq+i+".txt");
			threads = new ArrayList<ThreadS>();
			
			for(int j = 0; j < numThreads; j ++)
				threads.add(new ThreadS(GL.get(i)));
			
			for(ThreadS t : threads)
				t.start();
			
			//espera warmup
			tempo = System.currentTimeMillis();
			while(tempo+warmup > System.currentTimeMillis()) {};
			
			//epera tempo de execução
			tempo = System.currentTimeMillis();
			while(tempo+exectime > System.currentTimeMillis()) {
				aux = System.currentTimeMillis();
				//System.out.println(aux);
				if((int)(aux-tempo)%1000 == 0){ //1s
					//para saber o tamanho das listas só com volatile nas classes que soma um com adicção e retira um com remoção ou fazendo uma função que conta, mas isso é arriscado
					System.out.println("Add("+numAdd+") Rem("+numRem+") Con("+numCon+") Op("+(numAdd+numRem+numCon)+")");
					System.out.println("\t\tAddTot("+numAddTot+") RemTot("+numRemTot+") ConTot("+numConTot+") OpTot("+(numAddTot+numRemTot+numConTot)+")");
					numAddTot += numAdd;
					numRemTot += numRem;
					numConTot += numCon;
					
					try( FileWriter fw = new FileWriter(arquivo, true) ){
						fw.write(((aux-tempo)/Math.pow(10.0,3.0))+","+numAdd+","+numRem+","+numCon+","+(numAdd+numRem+numCon)+"\n");
						fw.flush();
					}catch(IOException ex){
					  ex.printStackTrace();
					}
					//salvar valores num arquivo
					numAdd = 0;
					numRem = 0;
					numCon = 0;
					while(aux == System.currentTimeMillis()) {};//inserir um sleep
				}
			}
			
			try {
				for(ThreadS t : threads)
					t.interrupt(); //deixar as threads em loop infinito
			} catch (SecurityException e) {};
		}
	}
}
