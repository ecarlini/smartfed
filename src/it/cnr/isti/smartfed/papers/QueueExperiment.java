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

package it.cnr.isti.smartfed.papers;

import it.cnr.isti.smartfed.federation.Federation;
import it.cnr.isti.smartfed.federation.FederationLog;
import it.cnr.isti.smartfed.federation.FederationQueue;
import it.cnr.isti.smartfed.federation.FederationQueueProfile;
import it.cnr.isti.smartfed.federation.FederationQueueProvider;
import it.cnr.isti.smartfed.federation.MonitoringHub;
import it.cnr.isti.smartfed.federation.application.Application;
import it.cnr.isti.smartfed.federation.mapping.AbstractAllocator;
import it.cnr.isti.smartfed.federation.mapping.RoundRobinAllocator;
import it.cnr.isti.smartfed.federation.resources.FederationDatacenter;
import it.cnr.isti.smartfed.federation.resources.FederationDatacenterFactory;
import it.cnr.isti.smartfed.federation.resources.FederationDatacenterProfile;
import it.cnr.isti.smartfed.federation.resources.FederationDatacenterProfile.DatacenterParams;
import it.cnr.isti.smartfed.federation.resources.HostFactory;
import it.cnr.isti.smartfed.federation.resources.HostProfile;
import it.cnr.isti.smartfed.federation.resources.HostProfile.HostParams;
import it.cnr.isti.smartfed.federation.resources.ResourceCounter;
import it.cnr.isti.smartfed.federation.utils.UtilityPrint;
import it.cnr.isti.smartfed.networking.InternetEstimator;
import it.cnr.isti.smartfed.test.DataSet;
import it.cnr.isti.smartfed.test.SimpleApplication;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;


/**
 * QueueExperiment is an experiment for using SmartFed with an application queue.
 * @author anastasi
 *
 */
public class QueueExperiment {

	private static long randomSeed = System.currentTimeMillis();

	
	public static List<FederationDatacenter> generate(int numHosts){
		List<FederationDatacenter> list = new ArrayList<FederationDatacenter>();
		List<Storage> storageList = new ArrayList<Storage>(); // if empty, no SAN attached

		// create the virtual processors (PE)
		List<Pe> peList = new ArrayList<Pe>();
		int mips = 250000;
		peList.add(new Pe(0, new PeProvisionerSimple(mips)));
		peList.add(new Pe(1, new PeProvisionerSimple(mips)));
		peList.add(new Pe(2, new PeProvisionerSimple(mips)));
		peList.add(new Pe(3, new PeProvisionerSimple(mips)));
		peList.add(new Pe(4, new PeProvisionerSimple(mips)));
		peList.add(new Pe(5, new PeProvisionerSimple(mips)));
		// ************************************

		// create the hosts for DC 1
		List<Host> hostList = new ArrayList<Host>();
		HostProfile prof = HostProfile.getDefault();
		prof.set(HostParams.RAM_AMOUNT_MB, 15*1024 + "");
		prof.set(HostParams.STORAGE_MB, 160*1024*1024 + "");
		prof.set(HostParams.BW_AMOUNT, "2048000");
		

		for (int k=0; k<numHosts; k++) {
			hostList.add(HostFactory.get(prof, peList));
		}

		FederationDatacenterProfile amazon = FederationDatacenterProfile.getAmazon();
		amazon.set(DatacenterParams.COUNTRY, "USA");
		
		FederationDatacenterProfile rackspace = FederationDatacenterProfile.getRackspace();
		rackspace.set(DatacenterParams.COUNTRY, "Italy");
		
		FederationDatacenterProfile aruba = FederationDatacenterProfile.getAruba();
		aruba.set(DatacenterParams.COUNTRY, "Italy");
		
		list.add(FederationDatacenterFactory.get("EC2", amazon, hostList, storageList));
		list.add(FederationDatacenterFactory.get("Aruba", aruba, hostList, storageList));
		
		return list;
	}
	
	void run(){
		Log.printLine("Starting CloudSim");
		try {
			FederationLog.setDebug(true);
			int num_user = 1;   // number of grid users
			Calendar calendar = Calendar.getInstance();
			boolean trace_flag = false;  // mean trace events

			CloudSim.init(num_user, calendar, trace_flag);

			// create the federation
			AbstractAllocator allocator = new RoundRobinAllocator();
			Federation federation = new Federation(allocator, randomSeed);
			CloudSim.addEntity(federation);

			// reset counter for the resources
			ResourceCounter.reset();
			
			List<FederationDatacenter> dcs= generate(10);
			FederationDatacenter d0 = dcs.get(0);
			FederationDatacenter d1 = dcs.get(1);
			
			// create net estimator
			InternetEstimator internetEstimator = DataSet.createDefaultInternetEstimator(dcs);
			
			// create monitoring
			int schedulingInterval = 5; // probably simulation time
			MonitoringHub monitor = new MonitoringHub(dcs, schedulingInterval);
			CloudSim.addEntity(monitor);
				
			// creating the applications
			List<Application> applications = new ArrayList<Application>();
			applications.add(new SimpleApplication(federation.getId(), 4));
			applications.add(new SimpleApplication(federation.getId(), 2));
			applications.add(new SimpleApplication(federation.getId(), 1));
			//applications.add(new ThreeTierBusinessApplication(federation.getId()));
			
			// setup the allocator
			allocator.setMonitoring(monitor);
			allocator.setNetEstimator(internetEstimator);
			allocator.setRandomSeed(randomSeed);
			
			// create the queue
			FederationQueueProfile queueProfile = FederationQueueProfile.getDefault();
			queueProfile.set(FederationQueueProfile.QueueParams.INTER_ARRIVAL_PARAMS, "100");
			FederationQueue queue = FederationQueueProvider.getFederationQueue(queueProfile, federation, applications);
			
			CloudSim.addEntity(queue);

			
			System.out.println();
			System.out.println(d0.toStringDetail());
			System.out.println();
			System.out.println(d1.toStringDetail());
			System.out.println();
			
			CloudSim.startSimulation();

			List<Cloudlet> newList = federation.getReceivedCloudlet();

			CloudSim.terminateSimulation(1000);
			CloudSim.stopSimulation();

			UtilityPrint.printCloudletList(newList);

			d0.printDebts();
			System.out.println();
			d1.printDebts();
		}

		catch (Exception e)
		{
			e.printStackTrace();
			Log.printLine("The simulation has been terminated due to an unexpected error");
		}
	}
	
	public static void main(String[] args) {
		QueueExperiment e = new QueueExperiment();
		e.run();
	}
}

