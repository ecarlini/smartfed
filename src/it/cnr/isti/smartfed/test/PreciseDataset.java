package it.cnr.isti.smartfed.test;

import it.cnr.isti.smartfed.federation.application.Application;
import it.cnr.isti.smartfed.federation.resources.FederationDatacenter;
import it.cnr.isti.smartfed.federation.resources.FederationDatacenterFactory;
import it.cnr.isti.smartfed.federation.resources.HostFactory;
import it.cnr.isti.smartfed.federation.resources.HostProfile;
import it.cnr.isti.smartfed.federation.resources.HostProfile.HostParams;
import it.cnr.isti.smartfed.networking.InternetEstimator;
import it.cnr.isti.smartfed.networking.SecuritySupport;

import java.util.ArrayList;
import java.util.List;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;

/**
 * The strategy of this class is the following.
 * (i) it creates the application according to the number
 * of cloudlets passed as parameter.
 * (ii) it creates an "host list" that contains all and only the hosts 
 * that can support the application. 
 * (iii) it creates the datacenters, distributing the host according
 * to the probability passed as parameter.
 *  
 * NOTE: Steps (i) and (ii) can be done inside the constructor.
 * The 3rd step MUST be done after the Cloudsim object has been 
 * initialized (i.e. it can not be done in the constructor of the
 * class).
 *  
 * TODO: what about the assumption the a DC
 * can contain only hosts of the same type?
 * 
 * @author carlini
 *
 */
public class PreciseDataset implements InterfaceDataSet
{
	private int numberOfCloudlets; 
	private double probNewDc;
	
	private Application application = null;
	private List<FederationDatacenter> datacenters;
	private List<Host> hostList = new ArrayList<Host>();
	
	/**
	 * 
	 * @param numberOfCloudlets
	 * @param probNewDc
	 */
	
	public PreciseDataset(int numberOfCloudlets, double probNewDc)
	{
		this.numberOfCloudlets = numberOfCloudlets;
		this.probNewDc = probNewDc;
	}

	private void _internalCreateApplications(int userId) 
	{
		/*
		Double number = new Double(numberOfCloudlets);
		if (number < 3){
			Log.printLine("Setting to three the number of vertices (it is the minimum allowed value");
			number = 3d;
		}
		
		int frontend = new Double(Math.ceil(number * 20 / 100)).intValue();
		int database = new Double(Math.ceil(number * 20 / 100)).intValue();
		int appserver = number.intValue() - frontend - database;
		
		application = new ThreeTierBusinessApplication(userId, frontend, appserver, database);
		*/
		
		application = new SimpleApplication(userId, 1);
		
	}
	
	private void _internalCreateDatacenter()
	{
		int pe_index = 0;
		
		for (Vm vm :application.getAllVms())
		{
			// create the pe
			List<Pe> peList = new ArrayList<Pe>();
			double expected_mips = vm.getMips();
			Pe pe = new Pe(pe_index++, new PeProvisionerSimple(expected_mips+1));
			peList.add(pe);
			
			// create the host
			HostProfile profile = HostProfile.getDefault();
			profile.set(HostParams.RAM_AMOUNT_MB, vm.getCurrentRequestedRam()+"");
			Host host = HostFactory.get(profile, peList);
			hostList.add(host);
		}
	}
	
	
	public void init(int userId)
	{		
		_internalCreateApplications(userId);
		_internalCreateDatacenter();
	}
	
	@Override
	public List<FederationDatacenter> createDatacenters() 
	{
		List<FederationDatacenter> list = new ArrayList<FederationDatacenter>();
		List<Storage> storageList = new ArrayList<Storage>(); // if empty, no SAN attached
		
		// create the DCs, for now one host per DC
		for (Host h: hostList)
		{
			List<Host> tmpList = new ArrayList<Host>();
			tmpList.add(h);
			list.add(FederationDatacenterFactory.getDefault(tmpList, storageList));
		}
		
		datacenters = list;
		
		return list;
	}
	
	@Override
	public InternetEstimator createInternetEstimator(List<FederationDatacenter> datacenters)
	{
		InternetEstimator inetEst = new InternetEstimator(datacenters);
		return inetEst;
	}

	@Override
	public List<Application> createApplications(int userId) 
	{
		List<Application> apps = new ArrayList<Application>();
		apps.add(application);
		return apps;
	}
	
}
