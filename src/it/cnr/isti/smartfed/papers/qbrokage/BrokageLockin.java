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

import it.cnr.isti.smartfed.federation.application.Application;
import it.cnr.isti.smartfed.federation.application.ApplicationVertex;
import it.cnr.isti.smartfed.federation.application.CloudletProvider;
import it.cnr.isti.smartfed.federation.generation.DatacenterGenerator;
import it.cnr.isti.smartfed.federation.generation.Range;
import it.cnr.isti.smartfed.federation.mapping.AbstractAllocator;
import it.cnr.isti.smartfed.federation.mapping.GeneticAllocator;
import it.cnr.isti.smartfed.federation.resources.Country;
import it.cnr.isti.smartfed.federation.resources.FederationDatacenter;
import it.cnr.isti.smartfed.federation.resources.VmFactory;
import it.cnr.isti.smartfed.metascheduler.JGAPMapping;
import it.cnr.isti.smartfed.test.DataSet;
import it.cnr.isti.smartfed.test.Experiment;
import it.cnr.isti.smartfed.test.TestResult;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Vm;

public class BrokageLockin {	
	static final int NUMCLOUDLETS = 12;
	static final int[] numDatacenters = {150};

	private static String dcToString(){
		String str = "" + numDatacenters[0];
		for (int i=1; i<numDatacenters.length; i++)
			str += "_" + numDatacenters[i];
		return str;
	}


	public static String executeSingleSetCost(AbstractAllocator allocator, int seed, int numOfCloudlets, int numOfDatacenter){
		int repetitions = 20;

		String str = "";
		int numHost = 100 * numOfDatacenter;
		DataSet dataset = new LockInDatset(numOfCloudlets, numOfDatacenter, numHost, seed);
		
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
		double lockin = TestResult.getLockDegree().getMean();
		str += String.format(Locale.ENGLISH, "%.4f", result) + "\t";
		str += String.format(Locale.ENGLISH, "%.4f", time) + "\t"; 
		str += String.format(Locale.ENGLISH, "%.4f", lockin) + "\t"; 
		TestResult.reset();
		return str;
	}

	public static String executeAndWrite(AbstractAllocator allocator, int[] numOfDatacenter) throws IOException{
		String str = "";
		for (int k=0; k<numOfDatacenter.length; k++){
			str += numOfDatacenter[k] + "\t";
			str += executeSingleSetCost(allocator, k, NUMCLOUDLETS, numOfDatacenter[k]);
			str += "\n";
		}
		return str;
	}

	public static void main (String[] args) throws IOException{
		String initial = "#evo_step,cost,time,used_dc \n";
		String str = initial;
		AbstractAllocator allocator = null; 
		int seed = 1;
		
		/*
		str = initial;
		allocator = new GreedyAllocator();
		str += executeAndWrite(allocator, numDatacenters);
		str += "\n";
		FileWriter fw1 = new FileWriter(new File("plots/lock-greedy-dc"+ dcToString() + ".dat"));
		fw1.write(str);
		fw1.flush();
		fw1.close();
		*/
		
		JGAPMapping.MUTATION = 10;
		JGAPMapping.POP_SIZE = 50;
		JGAPMapping.CROSSOVER = 0.35;
		JGAPMapping.EVOLUTION_STEP = 100; 
		allocator = new GeneticAllocator();
		str = initial;
		
		// warmup with 5 dcs
		
		allocator = new GeneticAllocator();
		str += executeAndWrite(allocator, numDatacenters);
		str += "\n";
		FileWriter fw2 = new FileWriter(new File("plots/lock-genetic-dc" + dcToString() +"cross0.35-mut10"+".dat"));
		fw2.write(str);
		fw2.flush();
		fw2.close();
	}
}

class LockInDatset extends DataSet{

	protected long seed;

	public LockInDatset(int numberOfCloudlets, int numOfDatacenter, int numHost, long myseed) {
		super(numberOfCloudlets, numOfDatacenter, numHost);
		seed = myseed;
	}

	/* 
	 * characteristics: RAM_AMOUNT 16GB, mips = 25000, num_cores[4,8], STORAGE 10TB
	 */
	@Override
	public List<FederationDatacenter> createDatacenters(){
		DatacenterGenerator dg = new DatacenterGeneratorCost();
		dg.resetSeed(this.seed * 15);
		return dg.getDatacenters(numOfDatacenters, numHost);
	}

	@Override
	public List<Application> createApplications(int userId)
	{
		Vm type1 = VmFactory.getCustomVm(userId, 1000d, 1, 512, 10*1024, 4096);
		ArrayList<Cloudlet> cloudletList = new ArrayList<Cloudlet>();

		for (int c=0; c < this.numberOfCloudlets; c++)
			cloudletList.add(CloudletProvider.getDefault());

		Application app = new Application();
		ApplicationVertex av1 = new ApplicationVertex(userId, cloudletList, type1);
		av1.setCountry(Country.Italy);
		av1.setBudget(Double.parseDouble("5.0"));
		app.addVertex(av1);

		List<Application> apps = new ArrayList<Application>();
		apps.add(app);
		return apps;
	}
}

class DatacenterGeneratorCost extends DatacenterGenerator{

	public DatacenterGeneratorCost() {
		super(1);
		costPerMem = new Range(0.01, 0.10);
		costPerSto = new Range(0.0002, 0.0020);
		costPerSec = new Range(0.10, 0.80); //not used, see below
		costPerBw = new Range(0.001, 0.05);
		
		ramAmount = new Range(512, 1024*16);
		bwAmount = new Range(10*1024, 10*1024*1024);
		stoAmount = new Range(4096, 10*1024*1024); // 10TB max
		coreAmount = new Range(1, 8);
		mipsAmount = new Range(1000, 25000);
	}	
}