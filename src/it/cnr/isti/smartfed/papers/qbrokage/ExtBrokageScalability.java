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
import it.cnr.isti.smartfed.metascheduler.MSPolicyFactory.PolicyType;
import it.cnr.isti.smartfed.metascheduler.test.DataSetMS;
import it.cnr.isti.smartfed.test.Experiment;
import it.cnr.isti.smartfed.test.TestResult;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;

public class ExtBrokageScalability {	
	static final int[] numCloudlets = {12};
	static final int[] numDatacenters = {5, 50, 100, 150, 200, 250, 300, 350, 400, 450, 500};
	

	public static String executeSingleSetCost(AbstractAllocator allocator, int seed, int numOfCloudlets, int numOfDatacenter){
		int numOfVertex = 3;
		int repetitions = 20;

		String str = "";
		int numHost = 100  * numOfDatacenter;
		PaperDataset dataset = new ExtBrokageDataset(numOfVertex, numOfCloudlets, numOfDatacenter, numHost, seed);

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

	public static void main (String[] args) throws IOException{
		String initial = "#evo_step,cost,time,failure" + BrokageScalability.dcToString() + "\n";
		String str = initial;
		AbstractAllocator allocator = new GreedyAllocator();
		int seed = 5;
		str += BrokageScalability.executeAndWrite(allocator, seed, numCloudlets, numDatacenters);
		str += "\n";
		FileWriter fw = new FileWriter(new File("plots/greedy-dc"+ BrokageScalability.dcToString() + ".dat"));
		fw.write(str);
		fw.flush();
		fw.close();


		JGAPMapping.MUTATION = 10;
		JGAPMapping.POP_SIZE = 50;
		JGAPMapping.CROSSOVER = 0.35;
		JGAPMapping.EVOLUTION_STEP = 120; 
		
		allocator = new GeneticAllocator();
		str = initial;
		// BrokageTuning.execute(allocator, 5, numCloudlets, numDatacenters);// just for warm-up
		GeneticAllocator gen_allocator = new GeneticAllocator();
		gen_allocator.setPolicyType(PolicyType.DEFAULT_COST);
		str += BrokageScalability.executeAndWrite(gen_allocator, seed, numCloudlets, numDatacenters);
		str += "\n";
		FileWriter fw2 = new FileWriter(new File("plots/cost-dc" + BrokageScalability.dcToString() +"cross0.35-mut10"+".dat"));
		fw2.write(str);
		fw2.flush();
		fw2.close();
		
		str = initial;
		gen_allocator = new GeneticAllocator();
		gen_allocator.setPolicyType(PolicyType.DEFAULT_COST_NET);
		str += BrokageScalability.executeAndWrite(gen_allocator, seed, numCloudlets, numDatacenters);
		str += "\n";
		FileWriter fw3 = new FileWriter(new File("plots/cost-dc" + BrokageScalability.dcToString() +"cross0.35-mut10-costNet"+".dat"));
		fw3.write(str);
		fw3.flush();
		fw3.close();

	}

}
