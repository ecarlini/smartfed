package it.cnr.isti.smartfed.test;

import it.cnr.isti.smartfed.federation.Allocation;
import it.cnr.isti.smartfed.federation.CostComputer;
import it.cnr.isti.smartfed.federation.Federation;
import it.cnr.isti.smartfed.federation.FederationQueue;
import it.cnr.isti.smartfed.federation.FederationQueueProfile;
import it.cnr.isti.smartfed.federation.FederationQueueProvider;
import it.cnr.isti.smartfed.federation.MonitoringHub;
import it.cnr.isti.smartfed.federation.application.Application;
import it.cnr.isti.smartfed.federation.application.ApplicationVertex;
import it.cnr.isti.smartfed.federation.mapping.AbstractAllocator;
import it.cnr.isti.smartfed.federation.mapping.MappingSolution;
import it.cnr.isti.smartfed.federation.resources.FederationDatacenter;
import it.cnr.isti.smartfed.federation.resources.ResourceCounter;
import it.cnr.isti.smartfed.federation.utils.UtilityPrint;
import it.cnr.isti.smartfed.networking.InternetEstimator;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.core.CloudSim;

public class ExperimentDistance extends Experiment
{
	public ExperimentDistance() {
		super(null, null);
	}


	private double optimum;
	
	public double getOptimum() {
		return optimum;
	}

	public void setOptimum(double optimum) {
		this.optimum = optimum;
	}

	public void setDataset(InterfaceDataSet d){
		dataset = d;
	}

	public void run (AbstractAllocator allocator) {
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

		// actually start the simulation
		CloudSim.startSimulation();
		
		// print the cloudlet
		List<Cloudlet> newList = federation.getReceivedCloudlet();
		// UtilityPrint.printCloudletList(newList);	
		
		// calculates the vendor lock-in metric on the mapping plan
		MappingSolution sol = allocator.getSolution();
		//System.out.println(sol);
		Set<FederationDatacenter> myset = new HashSet<FederationDatacenter>();
		for (FederationDatacenter fd: sol.getMapping().values()){
			myset.add(fd);
		}
		
		// MetaschedulerUtilities.saveFederationToTxt("datacenters" + datacenters.size() + ".txt", new ArrayList<FederationDatacenter>(myset));
		
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
			
				double total = CostComputer.actualCost(a);
				System.out.println("TOTAL --------> "+total);
				
				if (optimum != 0){
					double dop = (total - optimum) / optimum;
					TestResult.getCostDistance().addValue(dop);
					System.out.println("MYTOTAL --------> " + dop);
				}
				System.out.println("OPT --------> " + optimum);
				
				double totalNet = CostComputer.actualNetCost(a);
				TestResult.getNetCost().addValue(totalNet);
				System.out.println("NETCOST --------> " + totalNet);
				
				TestResult.getCost().addValue(total);
				TestResult.getBerger().addValue(Math.log(total / budget));
			}
			else
				System.out.println("Not completed");
		}
		// UtilityPrint.printCloudletList(newList);
	}
	

	public String toString(){
		return "ExperimentDistance with " + allocator.getClass().getSimpleName();
	}

}
