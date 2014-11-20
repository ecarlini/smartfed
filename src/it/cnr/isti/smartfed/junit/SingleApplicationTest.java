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
import it.cnr.isti.smartfed.federation.application.ApplicationVertex;
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
import it.cnr.isti.smartfed.metascheduler.Solution;
import it.cnr.isti.smartfed.networking.InternetEstimator;
import it.cnr.isti.smartfed.networking.SecuritySupport;
import it.cnr.isti.smartfed.test.Experiment;
import it.cnr.isti.smartfed.test.InterfaceDataSet;
import it.cnr.isti.smartfed.test.SimpleApplication;
import it.cnr.isti.smartfed.test.TestResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class SingleApplicationTest 
{
	private static TestDataset data;
	
	public static String costPerMem = "0.05";
	public static String costPerStorage = "0.01";
	public static String costPerCPU = "0.02";
	public static int vmMem = 1024;
	public static int cpu = 1;
	public static long vmDisk = 512;
	
	private static double expectedResult;
	
	@BeforeClass
	public static void testSetup() 
	{
		data = new TestDataset();
		expectedResult = (new Double(vmMem) * Double.parseDouble(costPerMem) / 1024) +
				(new Double(vmDisk) * Double.parseDouble(costPerStorage) / 1024); 
				//  + (new Double(cpu) * Double.parseDouble(costPerCPU)); 
	}
	
	@Before
	public void reset()
	{
		TestResult.reset();
	}
	
	@Test
	public void testGreedy()
	{
		AbstractAllocator greedyAllocator = new GreedyAllocator();	
		Experiment e = new Experiment(greedyAllocator, data);
		e.run();		
			
		Assert.assertEquals(expectedResult, TestResult.getCost().getMean(), 0.0000000001d);
	}
	
	@Test
	public void testRandom()
	{
		RandomAllocator allocator = new RandomAllocator();
		Experiment e = new Experiment(allocator, data);
		e.run();		
		
		Assert.assertEquals(expectedResult, TestResult.getCost().getMean(), 0.0000000001d);
	}
	
	@Test
	public void testGenetic()
	{
		GeneticAllocator allocator = new GeneticAllocator();
		Experiment e = new Experiment(allocator, data);
		e.run();		
		Solution[] s = allocator.getMSSolutions();
		double internalMS_cost = s[0].getCostAmount();
			
		Assert.assertEquals(expectedResult, internalMS_cost, 0d);
		Assert.assertEquals(expectedResult, TestResult.getCost().getMean(), 0.0000000001d);
	}
	
}
	
class TestDataset implements InterfaceDataSet
{

	@Override
	public List<FederationDatacenter> createDatacenters() 
	{
		List<FederationDatacenter> list = new ArrayList<FederationDatacenter>();
		List<Storage> storageList = new ArrayList<Storage>(); // if empty, no SAN attached
	
		// create the virtual processor (PE)
		List<Pe> peList = new ArrayList<Pe>();
		int mips = 25000;
		for (int i=0; i < SingleApplicationTest.cpu; i++)
			peList.add(new Pe(i, new PeProvisionerSimple(mips)));
		
		// create the hosts for DC 1
		List<Host> hostList = new ArrayList<Host>();
		HostProfile prof = HostProfile.getDefault();
		prof.set(HostParams.RAM_AMOUNT_MB, 2*1024+"");
		prof.set(HostParams.STORAGE_MB, "1000");
		prof.set(HostParams.BW_AMOUNT, "1024");

		for (int k=0; k<100; k++)
		{
			hostList.add(HostFactory.get(prof, peList));
		}
		
				
		// Creating DC1
		FederationDatacenterProfile fcp1 = FederationDatacenterProfile.getDefault();
		fcp1.set(DatacenterParams.COUNTRY, "Italy");
		fcp1.set(DatacenterParams.COST_PER_MEM, SingleApplicationTest.costPerMem);
		fcp1.set(DatacenterParams.COST_PER_STORAGE, SingleApplicationTest.costPerStorage);
		fcp1.set(DatacenterParams.COST_PER_SEC, SingleApplicationTest.costPerCPU);
		fcp1.set(DatacenterParams.COST_PER_BW, "0.03");
		list.add(FederationDatacenterFactory.get(fcp1, hostList, storageList));
		
		return list;
	}

	@Override
	public List<Application> createApplications(int userId)
	{
		Vm type1 = VmFactory.getCustomVm(userId, 1000d, 1, SingleApplicationTest.vmMem, 100, SingleApplicationTest.vmDisk);
		
		Application app = new SimpleApplication(userId, 1, type1);
		
		ApplicationVertex av1 = app.vertexSet().iterator().next();
		av1.setCountry("Italy");
		av1.setBudget(Double.parseDouble("100.0"));
		
		List<Application> apps = new ArrayList<Application>();
		apps.add(app);
		return apps;
	}

	@Override
	public InternetEstimator createInternetEstimator(List<FederationDatacenter> datacenters){
		InternetEstimator inetEst = new InternetEstimator(datacenters.size());
		for (FederationDatacenter top: datacenters){
			for (FederationDatacenter bot: datacenters){
				inetEst.defineDirectLink(top, bot, 1024*1024*10, 100, SecuritySupport.ADVANCED);
			}
		}
		return inetEst;
	}
}


