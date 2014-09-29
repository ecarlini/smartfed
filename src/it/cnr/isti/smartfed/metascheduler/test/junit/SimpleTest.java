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

package it.cnr.isti.smartfed.metascheduler.test.junit;

import static org.junit.Assert.*;
import it.cnr.isti.smartfed.federation.application.*;
import it.cnr.isti.smartfed.federation.resources.*;
import it.cnr.isti.smartfed.federation.resources.FederationDatacenterProfile.DatacenterParams;
import it.cnr.isti.smartfed.federation.resources.HostProfile.HostParams;
import it.cnr.isti.smartfed.metascheduler.*;
import it.cnr.isti.smartfed.metascheduler.constraints.PolicyContainer;
import it.cnr.isti.smartfed.metascheduler.iface.Metascheduler;
import it.cnr.isti.smartfed.metascheduler.test.DataSetMS;
import it.cnr.isti.smartfed.metascheduler.test.MetaschedulerUtilities;
import it.cnr.isti.smartfed.test.SimpleApplication;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.Set;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.power.PowerHostUtilizationHistory;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.jgap.IChromosome;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SimpleTest {

	protected static Properties dc_prop;
	protected static Properties app_prop;
	protected Application app = null;
	final static int best = 5;

	private PowerHostUtilizationHistory createHost(Properties prop, int ram){
		List<Pe> peList = new ArrayList<Pe>();
		peList.add(new Pe(0, new PeProvisionerSimple(Double.parseDouble(prop.getProperty(Constant.MIPS)))));
		HostProfile prof = HostProfile.getDefault();
		prof.set(HostParams.RAM_AMOUNT_MB,  Integer.toString(ram));  
		return  HostProviderMetaScheduler.get(prof, peList);
	}

	private FederationDatacenter createDatacenter(int dc_id, Properties prop, String place, String cost_per_mem, int ram){	
		FederationDatacenterProfile prof = FederationDatacenterProfile.getDefault();
		prof.set(DatacenterParams.COST_PER_MEM, cost_per_mem);
		prof.set(DatacenterParams.COUNTRY, "italia");
		
		int hostListSize = Integer.parseInt(prop.getProperty(Constant.DATACENTER_SIZE));
		List<Host> hostList = new ArrayList<Host>();
		for(int i=0; i< hostListSize; i++){
			hostList.add(createHost(prop, ram));
		}
		List<Storage> storageList = new ArrayList<Storage>();
		return FederationDatacenterFactory.get(prof, hostList, storageList);
	}
	
	
	
	@Before
	public void setUp() throws Exception {
		int num_user = 1; // number of cloud users
		Calendar calendar = Calendar.getInstance();
		boolean trace_flag = false; // mean trace events
		CloudSim.init(num_user, calendar, trace_flag);
		
		dc_prop = new  Properties();
		dc_prop.setProperty("seed", "1");
		dc_prop.setProperty("datacenter_size", "1");
		dc_prop.setProperty("datacenter_number", "2");
		dc_prop.setProperty("mips", "250000");
		// dc_prop.setProperty(Constant.COST_VM, "0.085,0.085,0.340,0.680");

		/*
		app_prop = new Properties();
		app_prop.setProperty("application_places", "Italia,Italia,Italia");
		app_prop.setProperty("application_budget", "4000.0,5000.0,6000.0");
		app_prop.setProperty("application_cloudlets", "3");
		*/

		long seed = Long.parseLong(dc_prop.getProperty("seed"));
		DataSetMS.rand = new Random(seed);
		FileWriter out = new FileWriter("metascheduler.txt");

		// Datacenter
		DataSetMS dataSet= new DataSetMS(dc_prop, app_prop);
		FederationDatacenter dc1 = createDatacenter(0,dc_prop,"italia", "1.0", 2740);
		FederationDatacenter dc2 = createDatacenter(1,dc_prop,"italia", "3.0", 4740);
		List<FederationDatacenter> dcList = new ArrayList<FederationDatacenter>();
		dcList.add(dc1);
		dcList.add(dc2);
		
		for (FederationDatacenter dc: dcList)
			System.out.println(dc.getDatacenterRepresentation());

		// finding the datacenter with the highest cost per ram (default criteria in the compare method)
		
		
		// Application
		int vNumber = 3;
		List<Application> apps = new ArrayList<Application>();
		app = new SimpleApplication(1, vNumber);
		Set<ApplicationVertex> s = app.vertexSet();
		Iterator<ApplicationVertex> i = s.iterator();
		i.next().setBudget(5220);
		i.next().setBudget(4000);
		i.next().setBudget(6000);
		apps.add(app);
		
		System.out.println(apps.get(0).allVMsString());
		
		// PolicyContainer constraint = MetaschedulerUtilities.createPoliciesCostPerVm(dcList);
		PolicyContainer constraint = MetaschedulerUtilities.createPoliciesDefault(dcList);
		Solution[] sol = Metascheduler.getMapping(apps.get(0),constraint.getList(), dcList, 1);
	}

	private void testMaxValues(){
		assertTrue(PolicyContainer.highRamValue == 4740);
		assertEquals(PolicyContainer.highCostValueRam, 3.0, 0.0001); // è valido solo utilizzando la policy default
		assertTrue(PolicyContainer.highStorageValue == 10485760);
	}
	
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test(){
		testMaxValues();
		
		IChromosome c = JGAPMapping.population.getFittestChromosome();
		assertTrue(c.getGenes().length == app.vertexSet().size()); 
		System.out.println("fitness del migliore è " + c.getFitnessValue());
		System.out.println("geni del miglior chromosoma hanno valore (alleli) " + Monitor.chromosomeToString(c));
		
		List<?> l = JGAPMapping.population.getFittestChromosomes(best);
		Iterator<?> i = l.iterator();
		while (i.hasNext()){
			IChromosome cc = (IChromosome) i.next();
			System.out.println("(fitness, gene) (" + cc.getFitnessValue() + ", " + Monitor.chromosomeToString(cc));
			
		}
	}

}
