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

package it.cnr.isti.smartfed.metascheduler.test;

import it.cnr.isti.smartfed.federation.application.Application;
import it.cnr.isti.smartfed.federation.resources.FederationDatacenter;
import it.cnr.isti.smartfed.federation.resources.FederationDatacenterFactory;
import it.cnr.isti.smartfed.federation.resources.FederationDatacenterProfile;
import it.cnr.isti.smartfed.federation.resources.FederationDatacenterProfile.DatacenterParams;
import it.cnr.isti.smartfed.federation.resources.HostFactory;
import it.cnr.isti.smartfed.federation.resources.HostProfile;
import it.cnr.isti.smartfed.federation.resources.HostProfile.HostParams;
import it.cnr.isti.smartfed.metascheduler.Constant;
import it.cnr.isti.smartfed.networking.InternetEstimator;
import it.cnr.isti.smartfed.networking.SecuritySupport;
import it.cnr.isti.smartfed.test.DatacenterFacilities;
import it.cnr.isti.smartfed.test.InterfaceDataSet;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;


public class DataSetMS implements InterfaceDataSet
{
	InternetEstimator net;
	public static Random rand;
	private Properties dc_prop;
	private Properties app_prop;
	public int numDatacenters = 0;
	public int numHostPerDc = 0;
	
	private void construct(Properties p, Properties app_properties){
		dc_prop = p;
		app_prop = app_properties;
		numDatacenters = Integer.parseInt(dc_prop.getProperty(Constant.DATACENTER_NUMBER));
		numHostPerDc = Integer.parseInt(dc_prop.getProperty(Constant.DATACENTER_SIZE));
	}
	
	public DataSetMS(Properties dc_properties, Properties app_properties){
		construct(dc_properties, app_properties);
	}
	
	public static Properties datacentersProp(){
		Properties tmp = new Properties();
		tmp.setProperty("datacenter_number", "3");
		tmp.setProperty("datacenter_size", "5");
		tmp.setProperty("datacenter_places", "Italy, Italy, Italy");
		tmp.setProperty("higher_ram_amount_mb", "8192");
		tmp.setProperty("higher_cost_per_mem", "0.01"); //prices per hour per GB
		tmp.setProperty("cost_per_storage", "0.05");
		// tmp.setProperty("storage_mb", "870400"); // 850GB, value of VM Large
		tmp.setProperty("mips", "250000");
		return tmp;
	}
	
	public static Properties applicationProp(){
		Properties tmp2  = new Properties();
		tmp2.setProperty("application_places", "Italy,Italy,Italy");
		tmp2.setProperty("application_budget", "10.0,20.0,20.0");
		tmp2.setProperty("application_cloudlets", "3");
		return tmp2;
	}
	
	public DataSetMS(){
		Properties tmp = datacentersProp();
		Properties tmp2  = applicationProp();
		construct(tmp, tmp2);
	}
	
	public DataSetMS(int numDatacenters, int numCloudlet){
		String num = Integer.toString(numDatacenters);
		String numCloudletPerApp = Integer.toString(numCloudlet);
		
		Properties tmp = datacentersProp();
		tmp.setProperty("datacenter_number", num);
		
		Properties tmp2  = applicationProp();
		tmp2.setProperty("application_cloudlets", numCloudletPerApp);
		
		construct(tmp, tmp2);
	}
	
	public List<FederationDatacenter> createDatacenters() {
		List<FederationDatacenter> list = null;
		// list = DatacenterFacilities.getUniformDistribution(numDatacenters, numHostPerDc);
		list = getDatacenterForMetascheduler();
		for(FederationDatacenter d : list)
			System.out.println(d);
		return list;
	}
	
	private List<FederationDatacenter> getDatacenterForMetascheduler() {
		List<FederationDatacenter> list = new ArrayList<FederationDatacenter>();
		String[] dc_places = dc_prop.get(Constant.DATACENTER_PLACES).toString().split(",");
		for( int i=0; i < numDatacenters; i++){
			list.add(createDatacenter(i,dc_prop, dc_places[i%dc_places.length]));			
		}
		return list;
	}
	
	/*
	public static double getSolutionCost(HashMap<Integer, Integer> map, List<FederationDatacenter> dclist, Application app){
		List<Vm> vmlist = app.getAllVms();
		double solutionCost=0;
		for(Vm v : vmlist){
			FederationDatacenter dc = dclist.get(map.get(v.getId()));
			double providerRamPrice = dc.getMSCharacteristics().getCostPerMem();
			System.out.print("(" + v.getRam() + "*" + providerRamPrice + ")");
			int vmRam =v.getRam();
			solutionCost+= providerRamPrice * vmRam;
		}
		System.out.print("=   " + solutionCost  + "\t");
		return solutionCost;
	}
	*/
	
	private Host createSimplerHost(Double ram, Double mips){
		List<Pe> peList = new ArrayList<Pe>();
		peList.add(new Pe(0, new PeProvisionerSimple(mips)));
		peList.add(new Pe(1, new PeProvisionerSimple(mips))); // if removing this core the demo will not work anymore but it should!
		
		HostProfile prof = HostProfile.getDefault();
		prof.set(HostParams.RAM_AMOUNT_MB,  Integer.toString(new Double(ram).intValue()))  ;  
		return HostFactory.get(prof, peList);
	}
	
	private static int decrement = 9;
	
	private FederationDatacenter createDatacenter(int dc_id, Properties prop, String place){
		double cost = Double.parseDouble(prop.getProperty("higher_cost_per_mem")); //in GB
		
		String costStorage = prop.getProperty(Constant.COST_STORAGE);
		
		final int factor = 1000;
		double random_cost_per_gb_mem = ((cost*factor) - decrement--)  / factor ;
		double random_cost_per_mb_mem = random_cost_per_gb_mem ; // for simplicity 
		random_cost_per_mb_mem = (random_cost_per_mb_mem < 0) ? cost/1024 : random_cost_per_mb_mem;
		// double random_cost_per_mb_mem_year = random_cost_per_mb_mem * 24 * 365; // from hour to year 
		System.out.println("Random cost per mb " + random_cost_per_mb_mem);
		
		FederationDatacenterProfile prof = FederationDatacenterProfile.getDefault();
		prof.set(DatacenterParams.COST_PER_MEM, Double.toString(random_cost_per_mb_mem));
		prof.set(DatacenterParams.COST_PER_STORAGE, costStorage);
		prof.set(DatacenterParams.COUNTRY, place);
		
		// int randInt = Math.abs( (rand.nextInt() % numDatacenters) );
		// randInt++; // exclude 0
		double ram = Double.parseDouble(prop.getProperty(Constant.HRAM));// / randInt;
		
		double mips = Double.parseDouble(prop.getProperty(Constant.MIPS));
		int hostListSize = numHostPerDc;
		List<Host> hostList = new ArrayList<Host>();
		for(int i=0; i < hostListSize; i++){
			hostList.add(createSimplerHost(ram, mips));
		}
		List<Storage> storageList = new ArrayList<Storage>();
		return FederationDatacenterFactory.get(prof, hostList, storageList);
	}
	
	public static void printHostInfo(Host host){
		Log.printLine("  hostid:           "+ host.getId());
		Log.printLine("  host ram:         " + host.getRam());
		Log.printLine("  host store:       " + host.getStorage());
		Log.printLine("  host mips:        " + host.getTotalMips());
		Log.printLine("  host net:         " + host.getBw());
		Log.printLine("  host pelist size: " + host.getPeList().size());
		Log.printLine();
	}
	
	

	public Application generateApplication(int userId, int numberOfCloudlets) 
	{
		String[] places = app_prop.getProperty(Constant.APPLICATION_PLACES).toString().split(",");
		String[] budgets = app_prop.getProperty(Constant.APPLICATION_BUDGET).toString().split(",");
		for (String b : budgets)
			System.out.println("Budgets for each cloudlet " + b);
		Double number = new Double(numberOfCloudlets);
		if (number < 3)
			number = 3d;
		
		int frontend = new Double(Math.ceil(number * 20 / 100)).intValue();
		int database = new Double(Math.ceil(number * 20 / 100)).intValue();
		int appserver = number.intValue() - frontend - database;
		
		return new ThreeTierBusinessApplicationMS(userId, places, budgets, frontend, appserver, database);
	}

	@Override
	public List<Application> createApplications(int userId) {
		String cloudletNumbers = app_prop.getProperty(Constant.APPLICATION_CLOUDLETS).toString();
		int cloudletNumber = Integer.parseInt(cloudletNumbers);
		List<Application> applications = new ArrayList<Application>();
		applications.add(this.generateApplication(userId, cloudletNumber));
		return applications;
	}

	@Override
	public InternetEstimator createInternetEstimator(List<FederationDatacenter> list) {
		InternetEstimator inetEst = new InternetEstimator(list.size());
		for (FederationDatacenter top: list){
			for (FederationDatacenter bot: list){
				inetEst.defineDirectLink(top, bot, 1024*1024*10, 100, SecuritySupport.ADVANCED);
			}
		}
		return inetEst;
	}
}
