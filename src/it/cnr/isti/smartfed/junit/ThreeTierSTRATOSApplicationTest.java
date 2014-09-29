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

package it.cnr.isti.smartfed.junit;

import it.cnr.isti.smartfed.federation.application.Application;
import it.cnr.isti.smartfed.federation.application.ApplicationEdge;
import it.cnr.isti.smartfed.federation.application.ApplicationVertex;
import it.cnr.isti.smartfed.federation.application.CloudletProfile;
import it.cnr.isti.smartfed.federation.application.CloudletProvider;
import it.cnr.isti.smartfed.federation.mapping.AbstractAllocator;
import it.cnr.isti.smartfed.federation.mapping.GeneticAllocator;
import it.cnr.isti.smartfed.federation.mapping.GreedyAllocator;
import it.cnr.isti.smartfed.federation.mapping.RandomAllocator;
import it.cnr.isti.smartfed.federation.resources.FederationDatacenter;
import it.cnr.isti.smartfed.federation.resources.FederationDatacenterFactory;
import it.cnr.isti.smartfed.federation.resources.FederationDatacenterProfile;
import it.cnr.isti.smartfed.federation.resources.FederationDatacenterProfile.DatacenterParams;
import it.cnr.isti.smartfed.federation.resources.HostFactory;
import it.cnr.isti.smartfed.federation.resources.HostProfile;
import it.cnr.isti.smartfed.federation.resources.HostProfile.HostParams;
import it.cnr.isti.smartfed.federation.resources.VmFactory;
import it.cnr.isti.smartfed.federation.resources.VmFactory.VmType;
import it.cnr.isti.smartfed.metascheduler.JGAPMapping;
import it.cnr.isti.smartfed.metascheduler.test.MetaschedulerUtilities.PolicyType;
import it.cnr.isti.smartfed.networking.InternetEstimator;
import it.cnr.isti.smartfed.networking.SecuritySupport;
import it.cnr.isti.smartfed.test.Experiment;
import it.cnr.isti.smartfed.test.InterfaceDataSet;
import it.cnr.isti.smartfed.test.TestResult;

import java.util.ArrayList;
import java.util.List;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ThreeTierSTRATOSApplicationTest 
{
	private static ThreeTierDataset data;

	//public static String costPerMem = Double.toString(0.00005 / 1024);
	//public static String costPerStorage = Double.toString(0.000694 / 1024);
	public static int vmMem = 1024;
	public static long vmDisk = 100;

	private static double expectedRes = 0.495;

	@BeforeClass
	public static void testSetup() 
	{
		data = new ThreeTierDataset();
	}

	@Before
	public void reset()
	{
		TestResult.reset();
	}

	@Test
	public void testGenetic()
	{
		
		GeneticAllocator allocator = new GeneticAllocator();
		JGAPMapping.POP_SIZE = 20;
		JGAPMapping.EVOLUTION_STEP = 3;
		JGAPMapping.CROSSOVER = 0.35;
		JGAPMapping.MUTATION = 12;
		allocator.setPolicyType(PolicyType.COST_PER_VM);
		Experiment e = new Experiment(allocator, data);
		e.run();		

		Assert.assertEquals(expectedRes, TestResult.getCost().getMean(), 0.001d);
	}
	
	//@Test
	public void testGreedy()
	{
		AbstractAllocator greedyAllocator = new GreedyAllocator();	
		Experiment e = new Experiment(greedyAllocator, data);
		e.run();		

		Assert.assertEquals(expectedRes, TestResult.getCost().getMean(), 0.001d);
	}

	//@Test
	public void testRandom()
	{
		RandomAllocator allocator = new RandomAllocator();
		Experiment e = new Experiment(allocator, data);
		e.run();		

		Assert.assertEquals(expectedRes, TestResult.getCost().getMean(), 0.001d);
	}

}

class ThreeTierDataset implements InterfaceDataSet
{

	public static List<FederationDatacenter> generate(){
		List<FederationDatacenter> list = new ArrayList<FederationDatacenter>();
		List<Storage> storageList = new ArrayList<Storage>(); // if empty, no SAN attached

		// create the virtual processor (PE)
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
		

		for (int k=0; k<100; k++)
		{
			hostList.add(HostFactory.get(prof, peList));
		}


		FederationDatacenterProfile amazon = FederationDatacenterProfile.getAmazon();
		amazon.set(DatacenterParams.COUNTRY, "Italy");
		
		FederationDatacenterProfile rackspace = FederationDatacenterProfile.getRackspace();
		rackspace.set(DatacenterParams.COUNTRY, "Italy");
		
		FederationDatacenterProfile aruba = FederationDatacenterProfile.getAruba();
		aruba.set(DatacenterParams.COUNTRY, "Italy");
		
		list.add(FederationDatacenterFactory.get("EC2", amazon, hostList, storageList));
		list.add(FederationDatacenterFactory.get("RS", rackspace, hostList, storageList));
		// list.add(FederationDatacenterFactory.get("Aruba", aruba, hostList, storageList));
		
		return list;
	}
	
	@Override
	public List<FederationDatacenter> createDatacenters() 
	{
		return generate();
	}

	@Override
	public List<Application> createApplications(int userId)
	{
		
		Application app = new StratosAmazonThreeTierBusinessApplication(userId, 2, 1, 1);
		List<Application> apps = new ArrayList<Application>();
		apps.add(app);
		return apps;
	}

	@Override
	public InternetEstimator createInternetEstimator(List<FederationDatacenter> datacenters){
		InternetEstimator inetEst = new InternetEstimator(datacenters);
		return inetEst;
	}

}

class StratosAmazonThreeTierBusinessApplication extends Application{
	// cloudlet profiles definition
	CloudletProfile profileDatabase = CloudletProfile.getDefault();
	CloudletProfile profileFronted = CloudletProfile.getDefault();
	CloudletProfile profileBackend = CloudletProfile.getDefault();

	protected ApplicationVertex vertexFrontend = null;
	protected ApplicationVertex vertexBackend = null;
	protected ApplicationVertex vertexDatabase = null;

	private ApplicationVertex createFrontend(int userId, int number)
	{
		ArrayList<Cloudlet> frontendList = new ArrayList<Cloudlet>();
		for (int i=0; i < number; i++)
		{
			Cloudlet c = CloudletProvider.get(profileFronted);
			frontendList.add(c);
		}
		return new ApplicationVertex("FrontEnd[Small]", userId, frontendList, VmType.SMALL);	 
	}

	private ApplicationVertex createBackend(int userId, int number)
	{
		ArrayList<Cloudlet> backendList = new ArrayList<Cloudlet>();
		for (int i=0; i<number; i++)
		{
			Cloudlet c = CloudletProvider.get(profileBackend);
			backendList.add(c);
		}	
		return new ApplicationVertex("BackEnd[Small]", userId, backendList, VmType.SMALL);	 
	}

	private ApplicationVertex createDatabase(int userId, int number){
		// Database tier
		ArrayList<Cloudlet> databaseList = new ArrayList<Cloudlet>();
		for (int i=0; i< number; i++)
		{
			Cloudlet c = CloudletProvider.get(profileDatabase);
			databaseList.add(c);
		}	
		
		return new ApplicationVertex("DB[Large]", userId, databaseList, VmType.XLARGE);
	}

	/**
	 * 
	 * @param userId
	 * @param frontendNumber
	 * @param backendNumber
	 * @param databaseNumber
	 */
	public StratosAmazonThreeTierBusinessApplication(int userId, int frontendNumber, int backendNumber, int databaseNumber) {
		vertexFrontend = createFrontend(userId, frontendNumber);
		vertexFrontend.setCountry("Italy");
		vertexFrontend.setBudget(0.5);
		vertexFrontend.setDesiredVm(VmFactory.getDesiredVm(userId, 1000, 1, 1*1024, 1000, 160*1024));
		
		vertexBackend = createBackend(userId, backendNumber);
		vertexBackend.setCountry("Italy");
		vertexBackend.setBudget(0.5);
		vertexBackend.setDesiredVm(VmFactory.getDesiredVm(userId, 1000, 1, 1*1024, 1000, 160*1024));
		
		vertexDatabase = createDatabase(userId, databaseNumber);	
		vertexDatabase.setCountry("Italy");
		vertexDatabase.setBudget(0.5);
		vertexDatabase.setDesiredVm(VmFactory.getDesiredVm(userId, 1000, 6, 4*1024, 1000, 160*1024));
		createConnections();
	}


	private void createConnections(){
		// Add the vertexes to the graph
		this.addVertex(vertexFrontend);
		this.addVertex(vertexBackend);
		this.addVertex(vertexDatabase);

		// Network
		// ApplicationEdge frontToBack = new ApplicationEdge(1024, SecuritySupport.BASE, 1000);
		// ApplicationEdge backToDB = new ApplicationEdge(512, SecuritySupport.BASE, 1000);

		// this.addEdge(frontToBack, vertexFrontend, vertexBackend);
		// this.addEdge(backToDB, vertexBackend, vertexDatabase);
	}
}


