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

import it.cnr.isti.smartfed.federation.Allocation;
import it.cnr.isti.smartfed.federation.CostComputer;
import it.cnr.isti.smartfed.federation.Federation;
import it.cnr.isti.smartfed.federation.FederationQueue;
import it.cnr.isti.smartfed.federation.FederationQueueProfile;
import it.cnr.isti.smartfed.federation.FederationQueueProvider;
import it.cnr.isti.smartfed.federation.MonitoringHub;
import it.cnr.isti.smartfed.federation.application.Application;
import it.cnr.isti.smartfed.federation.mapping.AbstractAllocator;
import it.cnr.isti.smartfed.federation.mapping.GeneticAllocator;
import it.cnr.isti.smartfed.federation.mapping.GreedyAllocator;
import it.cnr.isti.smartfed.federation.resources.FederationDatacenter;
import it.cnr.isti.smartfed.federation.resources.ResourceCounter;
import it.cnr.isti.smartfed.metascheduler.JGAPMapping;
import it.cnr.isti.smartfed.networking.InternetEstimator;
import it.cnr.isti.smartfed.test.ExperimentDistance;
import it.cnr.isti.smartfed.test.InterfaceDataSet;
import it.cnr.isti.smartfed.test.PreciseDataset;
import it.cnr.isti.smartfed.test.TestResult;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.core.CloudSim;

public class BrokageScalability {	
	private static final int[] numCloudlets = {12};
	private static final int[] numDatacenters = {5, 50, 100, 150, 200, 250, 300, 350, 400, 450, 500};

	protected static String dcToString(){
		String str = "" + numDatacenters[0];
		str += ":" + (numDatacenters[numDatacenters.length-1]-numDatacenters[numDatacenters.length-2]);
		str += ":" + numDatacenters[numDatacenters.length-1];
		return str;
	}

	private static String executeSingleSetCost(AbstractAllocator allocator, int numOfCloudlets, int numOfDatacenter){
		int numOfVertex = 3;
		int repetitions = 20;

		String str = "";
		int numHost = 100  * numOfDatacenter;
		ExperimentDistance e = new ExperimentDistance(allocator);

		long seed = 0;
		for (int i=0; i<repetitions; i++) {
			double optimum = 0;
			PaperDataset dataset = null;
			long j = seed;
			while (optimum == 0){
				dataset = new PaperDataset(numOfVertex, numOfCloudlets, numOfDatacenter, numHost, j);
				optimum = computeOptimum(new GreedyAllocator(), dataset, j);
				seed = j++;
			}
			e.setDataset(dataset);
			e.setOptimum(optimum);
			e.setRandomSeed(seed);
			e.run();	
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

	private static String executeAndWrite(AbstractAllocator allocator, int[] numOfCloudlets, int[] numOfDatacenter) throws IOException{
		String str = "";
		for (int z=0; z<numOfCloudlets.length; z++){
			for (int k=0; k<numOfDatacenter.length; k++){
				str += numOfDatacenter[k] + "\t";
				str += executeSingleSetCost(allocator, numOfCloudlets[z], numOfDatacenter[k]);
				str += "\n";
			}
		}
		return str;
	}

	public static void main (String[] args) throws IOException{
		String initial = "#dc,cost,time,lock,costSTD,timeSTD,berger" + dcToString() + "\n";
		String str = initial;
		
		JGAPMapping.MUTATION = 10;
		JGAPMapping.POP_SIZE = 50;
		JGAPMapping.CROSSOVER = 0.35;
		JGAPMapping.EVOLUTION_STEP = 120; 
		AbstractAllocator allocator = new GeneticAllocator();
		
		str += executeAndWrite(allocator, numCloudlets, numDatacenters);
		str += "\n";

		ExtBrokageScalability.write(str, new File("plots/cost-dc" + dcToString() +"cross0.35-mut10"+".dat"));
	}
	
	protected static double computeOptimum(AbstractAllocator allocator, PaperDataset da, long seed){
		InterfaceDataSet dataset = da;
		Log.enable();
		int num_user = 1;   // users
		Calendar calendar = Calendar.getInstance();
		boolean trace_flag = true;  // trace events
		CloudSim.init(num_user, calendar, trace_flag);
		Federation federation = new Federation(allocator, seed);
		CloudSim.addEntity(federation);

		// init the dataset
		if (dataset instanceof PreciseDataset)
			((PreciseDataset)dataset).init(federation.getId());

		ResourceCounter.reset();
		List<FederationDatacenter> datacenters = dataset.createDatacenters();
		federation.setDatacenters(datacenters);
		
		InternetEstimator internetEstimator = dataset.createInternetEstimator(datacenters);
		int schedulingInterval = 1; // probably simulation time
		MonitoringHub monitor = new MonitoringHub(datacenters, schedulingInterval);
		CloudSim.addEntity(monitor);
		
		List<Application> applications = dataset.createApplications(federation.getId());

		// setup the allocator
		allocator.setMonitoring(monitor);
		allocator.setNetEstimator(internetEstimator);
		allocator.setRandomSeed(seed);

		FederationQueueProfile queueProfile = FederationQueueProfile.getDefault();
		FederationQueue queue = FederationQueueProvider.getFederationQueue(queueProfile, federation, applications);
		CloudSim.addEntity(queue);

		CloudSim.terminateSimulation(1000000); // in milliseconds
		CloudSim.startSimulation();
		
		double res = 0;
		try {
			for (Allocation a: federation.getAllocations()){
				if (a.isCompleted()){
					res = CostComputer.actualCost(a);
					break;
				}
			}
		}
		catch (Exception e){
			res = 0;
		}
		System.out.println("Optimum is " + res);
		return res;
	}
	
	protected static void write(String toWrite, File f) throws IOException{
		FileWriter fw3 = new FileWriter(f);
		fw3.write(toWrite);
		fw3.flush();
		fw3.close();
	}

}
