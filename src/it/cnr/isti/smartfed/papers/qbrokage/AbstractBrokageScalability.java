package it.cnr.isti.smartfed.papers.qbrokage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.core.CloudSim;

import it.cnr.isti.smartfed.federation.Allocation;
import it.cnr.isti.smartfed.federation.CostComputer;
import it.cnr.isti.smartfed.federation.Federation;
import it.cnr.isti.smartfed.federation.FederationQueue;
import it.cnr.isti.smartfed.federation.FederationQueueProfile;
import it.cnr.isti.smartfed.federation.FederationQueueProvider;
import it.cnr.isti.smartfed.federation.MonitoringHub;
import it.cnr.isti.smartfed.federation.application.Application;
import it.cnr.isti.smartfed.federation.generation.GenerationType;
import it.cnr.isti.smartfed.federation.mapping.AbstractAllocator;
import it.cnr.isti.smartfed.federation.mapping.GeneticAllocator;
import it.cnr.isti.smartfed.federation.resources.FederationDatacenter;
import it.cnr.isti.smartfed.federation.resources.ResourceCounter;
import it.cnr.isti.smartfed.metascheduler.JGAPMapping;
import it.cnr.isti.smartfed.networking.InternetEstimator;
import it.cnr.isti.smartfed.test.ExperimentDistance;
import it.cnr.isti.smartfed.test.InterfaceDataSet;
import it.cnr.isti.smartfed.test.PreciseDataset;
import it.cnr.isti.smartfed.test.WorkflowApplication;
import it.cnr.isti.smartfed.test.WorkflowComputer;

abstract class AbstractBrokageScalability {

	public final int[] numCloudlets = {12};
	public int[] numDatacenters = {5, 50, 100, 150, 200, 250, 300, 350, 400, 450, 500};
	public int repetitions = 20;
	GenerationType gentype = null;
	String file_header = "";
	
	static int counter = 0;
	
	public AbstractBrokageScalability(GenerationType t){
		gentype = t;
		file_header = "#dc,cost,time,lock,costSTD,timeSTD,berger" + dcToString() + "\n";
		setGeneticAllocatorConfiguration();
	}
	
	protected static void setGeneticAllocatorConfiguration(){
		JGAPMapping.MUTATION = 10;
		JGAPMapping.POP_SIZE = 50;
		JGAPMapping.CROSSOVER = 0.35;
		JGAPMapping.EVOLUTION_STEP = 120; 
	}
	
	protected String dcToString(){
		String str = "" + numDatacenters[0];
		if (numDatacenters.length > 1)
			str += ":" + (numDatacenters[numDatacenters.length-1]-numDatacenters[numDatacenters.length-2]) + ":";
		str += numDatacenters[numDatacenters.length-1];
		return str;
	}
	
	protected String executeSingleSetCost_lessVariability(AbstractAllocator allocator, int numOfCloudlets, int numOfDatacenter) throws IOException{
		counter = 0;
		int numOfVertex = 3;
		
		String str = "";
		int numHost = 100  * numOfDatacenter;
		PaperDataset dataset = null;
		ExperimentDistance e = new ExperimentDistance();

		long seed = 0;
		double optimum = 0;
		double baseTime = 0;
		long j = seed;
		while (optimum == 0){
			dataset = this.createDataset(numOfVertex, numOfCloudlets, numOfDatacenter, numHost, j, gentype);
			double res[] = computeOptimum(dataset, j);
			optimum = res[0];
			baseTime = res[1];
			if (optimum == 0) {
				counter++;
			}
			seed = j++;
		}
		
		ExperimentDistance.Baseline b = e.new Baseline(optimum, baseTime, 0);
		for (int i=0; i<repetitions; i++) {
			e.setDataset(dataset);
			e.setBaseline(b);
			e.setRandomSeed(seed);
			((GeneticAllocator) allocator).resetConstraints();
			e.run(allocator);	
			seed++;
		}
		
		str += logResults();
		return str;
	}

	private String executeSingleSetCost(AbstractAllocator allocator, int numOfCloudlets, int numOfDatacenter) throws IOException{
		counter = 0;
		int numOfVertex = 3;
	
		String str = "";
		int numHost = 100  * numOfDatacenter;
		ExperimentDistance e = new ExperimentDistance();

		long seed = 0;
		int i = 0;
		while (i < repetitions){
			double optimum = 0;
			double baseTime = 0;
			double completion = 0;
			PaperDataset dataset = null;
			long j = seed;
			while (optimum == 0){
				dataset = this.createDataset(numOfVertex, numOfCloudlets, numOfDatacenter, numHost, j, gentype);
				double res[] = computeOptimum(dataset, j);
				optimum = res[0];
				baseTime = res[1];
				completion = res[2];
				if (optimum == 0) {
					counter++;
				}
				seed = j++;
			}
			if (optimum == 0) throw new IOException();
			ExperimentDistance.Baseline b = e.new Baseline(optimum, baseTime, completion);
			e.setDataset(dataset);
			e.setBaseline(b);
			e.setRandomSeed(seed);
			
			((GeneticAllocator) allocator).resetConstraints();
			
			if (e.run(allocator)){
				i++;
			}
			
			seed++;
		}

		str += logResults();
		return str;
	}
	
	protected String execute(AbstractAllocator allocator) throws IOException{
		String str = file_header;
		for (int z=0; z<numCloudlets.length; z++){
			for (int k=0; k<numDatacenters.length; k++){
				str += numDatacenters[k] + "\t";
				str += executeSingleSetCost(allocator, numCloudlets[z], numDatacenters[k]);
				str += "\n";
			}
		}
		str += "\n";
		return str;
	}
	
	protected String execute_lessVariability(AbstractAllocator allocator) throws IOException{
		String str = "";
		for (int z=0; z<numCloudlets.length; z++){
			for (int k=0; k<numDatacenters.length; k++){
				str += numDatacenters[k] + "\t";
				str += executeSingleSetCost_lessVariability(allocator, numCloudlets[z], numDatacenters[k]);
				str += "\n";
			}
		}
		str += "\n";
		return str;
	}
	
	protected double[] computeOptimum(PaperDataset da, long seed){
		AbstractAllocator allocator = createOptimumAllocator();
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
		double time = allocator.getRealDuration();
		Allocation a = null;
		double completion = 0;
		
		if (federation.getAllocations().iterator().hasNext())
			a = federation.getAllocations().iterator().next();
		if (a != null && a.isCompleted()){
			res = CostComputer.actualCost(a);
			if (applications.get(0) instanceof WorkflowApplication)
				completion = WorkflowComputer.getPipeCompletionTime((WorkflowApplication) applications.get(0), datacenters);
		}
		else 
			res = 0;

		System.out.println("Optimum is " + res);
		System.out.println("Time is " + time);
		System.out.println("####################################################\n");
		return new double[]{res, time, completion};
	}
	
	
	protected static void write(String toWrite, File f) throws IOException{
		FileWriter fw3 = new FileWriter(f);
		fw3.write(toWrite);
		fw3.flush();
		fw3.close();
	}
	
	abstract public PaperDataset createDataset(int numOfVertex, int numOfCloudlets, int numOfDatacenter, int numHost, long seed, GenerationType t);
	abstract public AbstractAllocator createOptimumAllocator(); 
	abstract public String logResults();
	
}
