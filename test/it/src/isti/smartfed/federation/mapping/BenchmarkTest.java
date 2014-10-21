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

package it.src.isti.smartfed.federation.mapping;

import static org.junit.Assert.assertEquals;
import it.cnr.isti.smartfed.federation.Federation;
import it.cnr.isti.smartfed.federation.MonitoringHub;
import it.cnr.isti.smartfed.federation.application.Application;
import it.cnr.isti.smartfed.federation.application.ApplicationVertex;
import it.cnr.isti.smartfed.federation.mapping.GeneticAllocator;
import it.cnr.isti.smartfed.federation.mapping.MappingSolution;
import it.cnr.isti.smartfed.federation.resources.FederationDatacenter;
import it.cnr.isti.smartfed.federation.resources.FederationDatacenterFactory;
import it.cnr.isti.smartfed.federation.resources.FederationDatacenterProfile;
import it.cnr.isti.smartfed.federation.resources.FederationDatacenterProfile.DatacenterParams;
import it.cnr.isti.smartfed.federation.resources.HostFactory;
import it.cnr.isti.smartfed.federation.resources.HostProfile;
import it.cnr.isti.smartfed.federation.resources.HostProfile.HostParams;
import it.cnr.isti.smartfed.metascheduler.MSPolicyFactory.PolicyType;
import it.cnr.isti.smartfed.networking.InternetEstimator;
import it.cnr.isti.smartfed.test.InterfaceDataSet;
import it.cnr.isti.smartfed.test.SimpleApplication;
import it.cnr.isti.smartfed.test.TestResult;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class BenchmarkTest 
{
	private static BenchmarkDataset data;

	//public static String costPerMem = Double.toString(0.00005 / 1024);
	//public static String costPerStorage = Double.toString(0.000694 / 1024);
	public static int vmMem = 1024;
	public static long vmDisk = 100;

	private static double expectedRes = 0.495;

	private static List<FederationDatacenter> datacenters;
	private static List<Application> applications;
	private static MonitoringHub monitor;
	private static InternetEstimator netEstimator;
	private static GeneticAllocator allocator = new GeneticAllocator();
	
	@BeforeClass
	public static void testSetup() 
	{
		allocator.setPolicyType(PolicyType.LOCATION_NET);
		data = new BenchmarkDataset();
		Calendar calendar = Calendar.getInstance();
		CloudSim.init(1, calendar, true);
		Federation federation = new Federation(allocator, 3);
		CloudSim.addEntity(federation);
		
		datacenters =  data.createDatacenters();
		applications = data.createApplications(federation.getId());
		monitor = new MonitoringHub(datacenters, 1000);
		netEstimator = data.createInternetEstimator(datacenters);
	}
	
	@Before
	public void reset()
	{
		TestResult.reset();
	}

	private Application getAppByName(String name){
		Application app = null;
		for (Application a: applications){
			BenchmarkApplication ab = (BenchmarkApplication) a;
			if (ab.getName().equals(name))
				app = a;
		}
		return app;
	}
	
	@Test
	public void testBenchmark1() {
		String name = "";
		allocator.setPolicyType(PolicyType.LOCATION);
		allocator.setNetEstimator(netEstimator);
		allocator.setMonitoring(monitor);
		MappingSolution[] ss2 = allocator.findAllocation(getAppByName("App1"));
		MappingSolution s = ss2[0];
		HashMap<Cloudlet, FederationDatacenter> f = s.getMapping();
		Set<Cloudlet> cs = f.keySet();
		for (Cloudlet c : cs){
			name = f.get(c).getName();
			break;
		}
		assertEquals(name, "P1");
	}
	
	@Test
	public void testBenchmark2()
	{
		String name = "";
		allocator.setPolicyType(PolicyType.LOCATION_NET);
		//allocator.setNetEstimator(netEstimator);
		//allocator.setMonitoring(monitor);
		
		MappingSolution[] ss2 = allocator.findAllocation(getAppByName("App2"));
		MappingSolution s = ss2[0];
		HashMap<Cloudlet, FederationDatacenter> f = s.getMapping();
		Set<Cloudlet> cs = f.keySet();
		for (Cloudlet c : cs){
			name = f.get(c).getName();
			break;
		}
		assertEquals(name, "P3");
	}
	
	
}

class BenchmarkDataset implements InterfaceDataSet
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
		// ************************************

		// create the hosts for DC 1
		List<Host> hostListInfinite = new ArrayList<Host>();
		HostProfile prof = HostProfile.getDefault();
		prof.set(HostParams.RAM_AMOUNT_MB, 100*1024 + "");
		prof.set(HostParams.STORAGE_MB, 10*1024*1024 + "");
		prof.set(HostParams.BW_AMOUNT, Long.toString(500*1024*1024));
		

		for (int k=0; k<100; k++){
			hostListInfinite.add(HostFactory.get(prof, peList));
		}
		
		List<Host> hostList = new ArrayList<Host>();
		prof = HostProfile.getDefault();
		prof.set(HostParams.RAM_AMOUNT_MB, 100*1024 + "");
		prof.set(HostParams.STORAGE_MB, 10*1024 + "");
		prof.set(HostParams.BW_AMOUNT, "2048000");
		

		for (int k=0; k<100; k++){
			hostList.add(HostFactory.get(prof, peList));
		}


		FederationDatacenterProfile p1 = FederationDatacenterProfile.getDefault();
		p1.set(DatacenterParams.COUNTRY, "SouthEast Asia,NorthAmerica");
		
		FederationDatacenterProfile p2 = FederationDatacenterProfile.getDefault();
		p2.set(DatacenterParams.COUNTRY, "Asia,NorthAmerica");
		
		FederationDatacenterProfile p3 = FederationDatacenterProfile.getDefault();
		p3.set(DatacenterParams.COUNTRY, "USA");
		
		FederationDatacenterProfile p4 = FederationDatacenterProfile.getDefault();
		p4.set(DatacenterParams.COUNTRY, "Asia,Europe,NorthAmerica");
		
		FederationDatacenterProfile p5 = FederationDatacenterProfile.getDefault();
		p5.set(DatacenterParams.COUNTRY, "Asia,NorthAmerica");
		
		list.add(FederationDatacenterFactory.get("P1", p1, hostListInfinite, storageList));
		list.add(FederationDatacenterFactory.get("P2", p2, hostList, storageList));
		list.add(FederationDatacenterFactory.get("P3", p3, hostList, storageList));
		list.add(FederationDatacenterFactory.get("P4", p4, hostList, storageList));
		list.add(FederationDatacenterFactory.get("P5", p5, hostListInfinite, storageList));
		
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
		
		List<Application> apps = new ArrayList<Application>();
		apps.add(new BenchmarkApplication1(userId, 1));
		apps.add(new BenchmarkApplication2(userId, 1));
		return apps;
	}

	@Override
	public InternetEstimator createInternetEstimator(List<FederationDatacenter> datacenters){
		InternetEstimator inetEst = new InternetEstimator(datacenters);
		return inetEst;
	}

}

class BenchmarkApplication extends SimpleApplication{
	String name = null;
	
	public BenchmarkApplication(int userId, int vertexNumber) {
		super(userId, vertexNumber);
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
}

class BenchmarkApplication1 extends BenchmarkApplication{
	public BenchmarkApplication1(int userId, int vertexNumber) {
		super(userId, vertexNumber);
		Set<ApplicationVertex> vs = this.vertexSet();
		for (ApplicationVertex v : vs)
			// v.setCountry("SouthEast Asia");
		setName("App1");
	}
}

class BenchmarkApplication2 extends BenchmarkApplication{
	public BenchmarkApplication2(int userId, int vertexNumber) {
		super(userId, vertexNumber);
		Set<ApplicationVertex> vs = this.vertexSet();
		for (ApplicationVertex v : vs){
			// v.setCountry("USA");
			List<Vm> vms = v.getVms();
			vms.get(0).setBw(250*1024*1024);
		}
		setName("App2");
	}
	
	public String getName(){
		return name;
	}
}


