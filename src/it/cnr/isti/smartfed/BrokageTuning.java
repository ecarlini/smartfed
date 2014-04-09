package it.cnr.isti.smartfed;

import it.cnr.isti.smartfed.federation.mapping.AbstractAllocator;
import it.cnr.isti.smartfed.federation.mapping.GeneticAllocator;
import it.cnr.isti.smartfed.federation.mapping.GreedyAllocator;
import it.cnr.isti.smartfed.metascheduler.JGAPMapping;
import it.cnr.isti.smartfed.test.Experiment;
import it.cnr.isti.smartfed.test.TestResult;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Locale;



public class BrokageTuning {

	static final int[] numCloudlets = {12};
	static final int[] numDatacenters = {50};//{100,200,300,400,500,600,700,800,900,1000};
	
	public static String executeSingleSet(AbstractAllocator allocator, int seed, int numOfCloudlets, int numOfDatacenter){
		int numOfVertex = 3;
		int repetitions = 20;

		String str = "";
		int numHost = 100  * numOfDatacenter;
		PaperDataset dataset = new PaperDataset(numOfVertex, numOfCloudlets, numOfDatacenter, numHost, seed);

		Experiment e = new Experiment(allocator, dataset);
		for (int i=0; i<repetitions; i++) {
			e.setRandomSeed(i);
			e.run();		
		}
		double result = TestResult.getCost().getMean(); 
		double resultSTD = TestResult.getCost().getStandardDeviation();
		double time = TestResult.getMappingTime().getMean();
		double timeSTD = TestResult.getMappingTime().getStandardDeviation();
		double failure = TestResult.getFailures().getMean();
		double berger = TestResult.getBerger().getMean();
		str += String.format(Locale.ENGLISH, "%.2f", result) + "\t";
		str += String.format(Locale.ENGLISH, "%.2f", resultSTD) + "\t";
		str += String.format(Locale.ENGLISH, "%.2f", time) + "\t";
		str += String.format(Locale.ENGLISH, "%.2f", timeSTD) + "\t";
		str += String.format(Locale.ENGLISH, "%.2f", failure) + "\t";
		str += String.format(Locale.ENGLISH, "%.2f", berger) + "\t";
		TestResult.reset();
		return str;
	}
	
	public static String execute(AbstractAllocator allocator, int step, int[] numOfCloudlets, int[] numOfDatacenter){
		String str = "";
		str+= step + "\t";

		for (int z=0; z<numOfCloudlets.length; z++){
			for (int k=0; k<numOfDatacenter.length; k++){
				str += executeSingleSet(allocator, k, numOfCloudlets[z], numOfDatacenter[k]);
			}
		}

		str += "\n";
		return str;
	}

	public static void main (String[] args) throws IOException {
		String str = "", greedy="";
		StringBuilder sb = new StringBuilder();

		str += "#evo_step,cost,costSTD,time,timeSTD,failure,berger\n";

		// GeneticAllocator geneticAllocator = new GeneticAllocator();
		AbstractAllocator allocator = new GreedyAllocator();
		greedy += execute(allocator, 0, numCloudlets, numDatacenters);
		FileWriter fw = new FileWriter(new File("plots/greedy.dat"));
		fw.write(str + greedy);
		fw.flush();
		fw.close();

		executeMutations(str);
		// executeCrossover(str);
	}
	
	private static void executeCrossover(String initial) throws IOException {
		double[] crossover = {0.35,0.6,0.8};

		for (int k=0; k<crossover.length; k++){
			String str = initial;
			JGAPMapping.MUTATION = 10;
			JGAPMapping.POP_SIZE = 50;
			JGAPMapping.CROSSOVER = crossover[k];
			AbstractAllocator allocator = new GeneticAllocator();
			
			execute(allocator, 5, numCloudlets, numDatacenters);// just for warm-up
			for (int i=10; i<=120; i+=10){
				JGAPMapping.EVOLUTION_STEP = i; 
				allocator = new GeneticAllocator();
				str += execute(allocator, JGAPMapping.EVOLUTION_STEP, numCloudlets, numDatacenters);
			}

			FileWriter fw = new FileWriter(new File("plots/tuning-cross" + JGAPMapping.CROSSOVER + "-mut" + JGAPMapping.MUTATION +".dat"));
			fw.write(str);
			fw.flush();
			fw.close();
		}
	}

	private static void executeMutations(String initial) throws IOException{
		int[] mutations = {10,12};

		for (int k=0; k<mutations.length; k++){
			String str = initial;
			JGAPMapping.MUTATION = mutations[k];
			JGAPMapping.POP_SIZE = 50;
			JGAPMapping.CROSSOVER = 0.35;
			AbstractAllocator allocator = new GeneticAllocator();
			
			execute(allocator, 5, numCloudlets, numDatacenters);// just for warm-up
			for (int i=10; i<=120; i+=10){
				JGAPMapping.EVOLUTION_STEP = i; 
				allocator = new GeneticAllocator();
				str += execute(allocator, JGAPMapping.EVOLUTION_STEP, numCloudlets, numDatacenters);
			}

			FileWriter fw = new FileWriter(new File("plots/tuning-mut" + JGAPMapping.MUTATION +"-cross"+ JGAPMapping.CROSSOVER +".dat"));
			fw.write(str);
			fw.flush();
			fw.close();
		}
	}
}
