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

import it.cnr.isti.smartfed.federation.FederationLog;
import it.cnr.isti.smartfed.federation.mapping.AbstractAllocator;
import it.cnr.isti.smartfed.federation.mapping.GeneticAllocator;
import it.cnr.isti.smartfed.federation.mapping.GreedyAllocator;
import it.cnr.isti.smartfed.metascheduler.JGAPMapping;
import it.cnr.isti.smartfed.metascheduler.MSPolicyFactory.PolicyType;
import it.cnr.isti.smartfed.test.Experiment;
import it.cnr.isti.smartfed.test.ExperimentDistance;
import it.cnr.isti.smartfed.test.TestResult;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;

public class ExtBrokageScalability extends BrokageScalability{	
	
	static final int[] numCloudlets = {12};
	static final int[] numDatacenters = {5, 50, 100};//, 150, 200, 250, 300, 350, 400, 450, 500};
	
	static int counter = 0;
	private static String executeSingleSetCost(AbstractAllocator allocator, int numOfCloudlets, int numOfDatacenter){
		
		
		int numOfVertex = 3;
		int repetitions = 20;

		String str = "";
		int numHost = 100  * numOfDatacenter;
		ExperimentDistance e = new ExperimentDistance();

		long seed = 0;
		for (int i=0; i<repetitions; i++) {
			double optimum = 0;
			PaperDataset dataset = null;
			long j = seed;
			while (optimum == 0){
				dataset = new ExtBrokageDataset(numOfVertex, numOfCloudlets, numOfDatacenter, numHost, j);
				// AbstractAllocator optimumAlloc = new GreedyAllocator();
				AbstractAllocator optimumAlloc = new GeneticAllocator();
				((GeneticAllocator) optimumAlloc).resetConstraints();
				optimum = computeOptimum(optimumAlloc, dataset, j);
				if (optimum == 0) 
					counter++;
				seed = j++;
			}
			((GeneticAllocator) allocator).resetConstraints();
			e.setDataset(dataset);
			e.setOptimum(optimum);
			e.setRandomSeed(seed);
			e.run(allocator);	
			seed++;
			
		}
		
		double result = TestResult.getCostDistance().getMean(); 
		double resultSTD = TestResult.getCostDistance().getStandardDeviation();
		double time = TestResult.getMappingTime().getMean();
		double timeSTD = TestResult.getMappingTime().getStandardDeviation();
		double lockin = TestResult.getLockDegree().getMean();
		double berger = TestResult.getBerger().getMean();
		str += String.format(Locale.ENGLISH, "%.5f", result) + "\t";
		str += String.format(Locale.ENGLISH, "%.2f", time) + "\t"; 
		str += String.format(Locale.ENGLISH, "%.2f", lockin) + "\t"; 
		str += String.format(Locale.ENGLISH, "%.2f", resultSTD) + "\t"; 
		str += String.format(Locale.ENGLISH, "%.2f", timeSTD) + "\t"; 
		str += String.format(Locale.ENGLISH, "%.2f", berger) + "\t"; 
		TestResult.reset();
		return str;
	}
	
	
	
	public static String executeConfrontation(AbstractAllocator allocator, int[] numOfCloudlets, int[] numOfDatacenter) throws IOException{
		String str = "";
		for (int z=0; z<numOfCloudlets.length; z++){
			for (int k=0; k<numOfDatacenter.length; k++){
				str += numOfDatacenter[k] + "\t";
				str += executeSingleSetCost(allocator, numOfCloudlets[z], numOfDatacenter[k]);
				str += "\n";
				try {
					Thread.sleep(50);
				}
				catch (Exception e) {}
				System.out.println("############################################# Ending " + numOfDatacenter[k] + " dcs");
			}
		}
		return str;
	}
	

	public static void main (String[] args) throws IOException{
		ExtBrokageScalability ext = new ExtBrokageScalability();
		// FederationLog.disable();
		String initial = "#dc,cost,time,lockin,costSTD,timeSTD,berger" + ext.dcToString() + "\n";
		String str = initial;
		
		/*
		GeneticAllocator gen_allocator = new GeneticAllocator();
		gen_allocator.setPolicyType(PolicyType.DEFAULT_COST);
		str += ExtBrokageScalability.execute(gen_allocator, numCloudlets, numDatacenters);
		str += "\n";
		write(str, new File("plots/cost-dc" + dcToString() +"cross0.35-mut10"+".dat"));
		*/
		
		GeneticAllocator gen_allocator = new GeneticAllocator();
		str = initial;
		gen_allocator = new GeneticAllocator();
		gen_allocator.setPolicyType(PolicyType.DEFAULT_COST_NET);
		
		str += ExtBrokageScalability.executeConfrontation(gen_allocator, numCloudlets, numDatacenters);
		str += "\n";
		write(str, new File("plots/cost-dc" + ext.dcToString() +"cross0.35-mut10-costNet"+".dat"));
		System.out.println("Fallito " + counter + " times");
		
	}

	public static String executeSingleSetCost_lessVariability(AbstractAllocator allocator, int seed, int numOfCloudlets, int numOfDatacenter){
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
		double lockin = TestResult.getNetCost().getMean();
		double berger = TestResult.getBerger().getMean();
		str += String.format(Locale.ENGLISH, "%.2f", result) + "\t";
		str += String.format(Locale.ENGLISH, "%.2f", time) + "\t"; 
		str += String.format(Locale.ENGLISH, "%.2f", lockin) + "\t"; 
		str += String.format(Locale.ENGLISH, "%.2f", resultSTD) + "\t"; 
		str += String.format(Locale.ENGLISH, "%.2f", timeSTD) + "\t"; 
		str += String.format(Locale.ENGLISH, "%.2f", berger) + "\t"; 
		TestResult.reset();
		return str;
	}
	
}
