/*
Copyright 2014 ISTI-CNR
 
This file is part of SmartFed.

SmartFed is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.
 
SmartFed is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU General Public License for more details.
 
You should have received a copy of the GNU General Public License
along with SmartFed. If not, see <http://www.gnu.org/licenses/>.

*/

package it.cnr.isti.smartfed.papers.qbrokage;

import it.cnr.isti.smartfed.federation.mapping.AbstractAllocator;
import it.cnr.isti.smartfed.federation.mapping.GeneticAllocator;
import it.cnr.isti.smartfed.federation.mapping.GreedyAllocator;
import it.cnr.isti.smartfed.federation.mapping.RandomAllocator;
import it.cnr.isti.smartfed.metascheduler.JGAPMapping;
import it.cnr.isti.smartfed.metascheduler.test.DataSetMS;
import it.cnr.isti.smartfed.test.Experiment;
import it.cnr.isti.smartfed.test.TestResult;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;

public class BrokageScalability {	
	static final int[] numCloudlets = {12};
	static final int[] numDatacenters = {5, 50, 100, 150, 200, 250, 300, 350, 400, 450, 500};
	
	private static String dcToString(){
		String str = "" + numDatacenters[0];
		for (int i=1; i<numDatacenters.length; i++)
			str += "_" + numDatacenters[i];
		return str;
	}


	public static String executeSingleSetCost(AbstractAllocator allocator, int seed, int numOfCloudlets, int numOfDatacenter){
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
		double failure = TestResult.getFailures().getN();
		double berger = TestResult.getBerger().getMean();
		str += String.format(Locale.ENGLISH, "%.2f", result) + "\t";
		str += String.format(Locale.ENGLISH, "%.2f", time) + "\t"; 
		str += String.format(Locale.ENGLISH, "%.2f", failure) + "\t"; 
		TestResult.reset();
		return str;
	}

	public static String executeAndWrite(AbstractAllocator allocator, int step, int[] numOfCloudlets, int[] numOfDatacenter) throws IOException{
		String str = "";
		// str+= step + "\t";

		for (int z=0; z<numOfCloudlets.length; z++){
			for (int k=0; k<numOfDatacenter.length; k++){
				str += numOfDatacenter[k] + "\t";
				str += executeSingleSetCost(allocator, k, numOfCloudlets[z], numOfDatacenter[k]);
				str += "\n";
			}
		}
		return str;
	}

	public static void main (String[] args) throws IOException{
		String initial = "#evo_step,cost,time,failure" + dcToString() + "\n";
		String str = initial;
		AbstractAllocator allocator = new GreedyAllocator();
		int seed = 5;
		str += executeAndWrite(allocator, seed, numCloudlets, numDatacenters);
		str += "\n";
		FileWriter fw = new FileWriter(new File("plots/greedy-dc"+ dcToString() + ".dat"));
		fw.write(str);
		fw.flush();
		fw.close();


		JGAPMapping.MUTATION = 10;
		JGAPMapping.POP_SIZE = 50;
		JGAPMapping.CROSSOVER = 0.35;
		JGAPMapping.EVOLUTION_STEP = 120; 
		allocator = new GeneticAllocator();
		str = initial;
		BrokageTuning.execute(allocator, 5, numCloudlets, numDatacenters);// just for warm-up
		allocator = new GeneticAllocator();
		str += executeAndWrite(allocator, seed, numCloudlets, numDatacenters);
		str += "\n";

		FileWriter fw2 = new FileWriter(new File("plots/cost-dc" + dcToString() +"cross0.35-mut10"+".dat"));
		fw2.write(str);
		fw2.flush();
		fw2.close();

	}

	public static void oldExp (String[] args) throws IOException {
		AbstractAllocator randomAllocator = new RandomAllocator();
		AbstractAllocator geneticAllocator = new GeneticAllocator();

		int numOfDatacenter = 10;

		StringBuilder sb = new StringBuilder();
		sb.append("vm\tur\tb_r\tu_genetic\tberger_genetic\n");
		int[] cloudlets = {10, 100};// {10,100,1000};

		int repetitions = 20;

		for (int numberOfCloudlets: cloudlets){
			System.out.println("\n Running:                  ");
			sb.append(numberOfCloudlets).append("\t");

			DataSetMS uniformData = new DataSetMS(numOfDatacenter, numberOfCloudlets);

			Experiment ue1 = new Experiment(randomAllocator, uniformData);

			for (int i=0; i<repetitions; i++)
			{
				ue1.setRandomSeed(i);
				ue1.run();				
			}

			sb.append(TestResult.getMappingTime().getMean()).append("\t");
			// sb.append(TestResult.getBerger().getMean()).append("\t");

			// reset measurements
			TestResult.reset();

			Experiment ue2 = new Experiment(geneticAllocator, uniformData);

			for (int i=0; i<repetitions; i++)
			{
				ue2.setRandomSeed(i);
				ue2.run();
			}

			sb.append(TestResult.getMappingTime().getMean()).append("\t");
			// sb.append(TestResult.getBerger().getMean()).append('\n');

		}	
		FileWriter fw = new FileWriter(new File(numOfDatacenter+"datacenter.dat"));
		fw.write(sb.toString());
		fw.flush();
		fw.close();
	}
}
