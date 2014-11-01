package it.cnr.isti.smartfed.test;

import it.cnr.isti.smartfed.federation.Allocation;
import it.cnr.isti.smartfed.federation.CostComputer;
import it.cnr.isti.smartfed.federation.Federation;
import it.cnr.isti.smartfed.federation.FederationQueue;
import it.cnr.isti.smartfed.federation.FederationQueueProfile;
import it.cnr.isti.smartfed.federation.FederationQueueProvider;
import it.cnr.isti.smartfed.federation.MonitoringHub;
import it.cnr.isti.smartfed.federation.UtilityPrint;
import it.cnr.isti.smartfed.federation.WorkflowComputer;
import it.cnr.isti.smartfed.federation.application.Application;
import it.cnr.isti.smartfed.federation.application.ApplicationVertex;
import it.cnr.isti.smartfed.federation.application.WorkflowApplication;
import it.cnr.isti.smartfed.federation.mapping.AbstractAllocator;
import it.cnr.isti.smartfed.federation.mapping.MappingSolution;
import it.cnr.isti.smartfed.federation.resources.FederationDatacenter;
import it.cnr.isti.smartfed.federation.resources.ResourceCounter;
import it.cnr.isti.smartfed.networking.InternetEstimator;

import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.core.CloudSim;

public class Experiment 
{
	protected AbstractAllocator allocator;
	protected InterfaceDataSet dataset;
	protected long randomSeed;

	/**
	 * If using this constructor you should set manually the random seed for the experiment.
	 * @param allocator
	 * @param d
	 */
	public Experiment(AbstractAllocator allocator, InterfaceDataSet d)
	{
		this.allocator = allocator;
		this.dataset = d;
	}
	
	public Experiment(AbstractAllocator allocator, InterfaceDataSet d, long seed)
	{
		this.allocator = allocator;
		this.dataset = d;
		this.randomSeed = seed;
	}

	public long getRandomSeed() {
		return randomSeed;
	}

	public void setRandomSeed(long randomSeed) {
		this.randomSeed = randomSeed;
	}

	public void run()
	{
		// init the cloudsim simulator
		Log.enable();
		int num_user = 1;   // users
		Calendar calendar = Calendar.getInstance();
		boolean trace_flag = true;  // trace events
		CloudSim.init(num_user, calendar, trace_flag);
			
		
		// create the federation
		Federation federation = new Federation(allocator, randomSeed);
		CloudSim.addEntity(federation);

		// init the dataset
		if (dataset instanceof PreciseDataset)
			((PreciseDataset)dataset).init(federation.getId());
			
		// reset counter for the resources
		ResourceCounter.reset();
		
		// create datacenters for the experiment
		List<FederationDatacenter> datacenters = dataset.createDatacenters();
		federation.setDatacenters(datacenters);
		
		// create net estimator
		InternetEstimator internetEstimator = dataset.createInternetEstimator(datacenters);
		
		// create monitoring
		int schedulingInterval = 1; // probably simulation time
		MonitoringHub monitor = new MonitoringHub(datacenters, schedulingInterval);
		CloudSim.addEntity(monitor);
			
		// creating the applications
		List<Application> applications = dataset.createApplications(federation.getId());
		
		// setup the allocator
		allocator.setMonitoring(monitor);
		allocator.setNetEstimator(internetEstimator);
		allocator.setRandomSeed(randomSeed);
		
		// create the queue (is that still needed)?
		FederationQueueProfile queueProfile = FederationQueueProfile.getDefault();
		FederationQueue queue = FederationQueueProvider.getFederationQueue(queueProfile, federation, applications);
		CloudSim.addEntity(queue);
		
		// manually setup the end of the simulation
		CloudSim.terminateSimulation(1000000); // in milliseconds
		
		// DEBUG: log the entities
		// for (SimEntity entity: CloudSim.getEntityList()) 
			// System.out.println(entity.getId()+ " - "+entity.getName());

		// actually start the simulation
		CloudSim.startSimulation();
		
		// print the cloudlet
		List<Cloudlet> newList = federation.getReceivedCloudlet();
		UtilityPrint.printCloudletList(newList);	
		
		// calculates the vendor lock-in metric on the mapping plan
		MappingSolution sol = allocator.getSolution();
		System.out.println(sol);
		Set<FederationDatacenter> myset = new HashSet<FederationDatacenter>();
		for (FederationDatacenter fd: sol.getMapping().values()){
			myset.add(fd);
		}
		
		int usedDc = myset.size();// / datacenters.size();
		TestResult.getLockDegree().addValue(usedDc);
		
		// add the values to the TestResult class
		for (Allocation a: federation.getAllocations())
		{
			if (a.isCompleted())
			{
				double budget = 0;
				for (ApplicationVertex av : a.getApplication().vertexSet())
					budget += av.getBudget();
			


				if (applications.get(0) instanceof WorkflowApplication)
				{			
					double completion = WorkflowComputer.getFlowCompletionTime((WorkflowApplication) applications.get(0), datacenters, internetEstimator);
					double cost = WorkflowComputer.getFlowCostPerHour(a, completion);
					TestResult.getCompletion().addValue(completion);
					TestResult.getCost().addValue(cost);
					System.out.println("COMPLETION -----------> " + completion);
				}
				else
				{
					
					double total = CostComputer.actualCost(a);
					double netcost = CostComputer.actualNetCost(a);
					System.out.println("TOTAL --------> "+total);
					
					TestResult.getCost().addValue(total);
					TestResult.getNetCost().addValue(netcost);
					TestResult.getBerger().addValue(Math.log(total / budget));
				}
					
			}
			else
				System.out.println("Not completed");
		}
		
	}
	

	public String toString(){
		return "Experiment with " + allocator.getClass().getSimpleName();
	}

}
