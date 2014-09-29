package it.cnr.isti.smartfed.test;

import it.cnr.isti.smartfed.federation.application.Application;
import it.cnr.isti.smartfed.federation.resources.FederationDatacenter;
import it.cnr.isti.smartfed.networking.InternetEstimator;

import java.util.ArrayList;
import java.util.List;

import org.cloudbus.cloudsim.Log;

public abstract class DataSet implements InterfaceDataSet 
{

	protected int numberOfCloudlets;
	protected int numOfDatacenters;
	protected int numHost;
	
	public DataSet(int numberOfCloudlets, int numOfDatacenter, int numHost) 
	{
		this.numberOfCloudlets = numberOfCloudlets;
		this.numOfDatacenters = numOfDatacenter;
		this.numHost = numHost;
	}

	public abstract List<FederationDatacenter> createDatacenters();

	@Override
	public List<Application> createApplications(int userId) {
		Double number = new Double(numberOfCloudlets);
		if (number < 3){
			Log.printLine("Setting to three the number of vertices (it is the minimum allowed value");
			number = 3d;
		}
		
		int frontend = new Double(Math.ceil(number * 20 / 100)).intValue();
		int database = new Double(Math.ceil(number * 20 / 100)).intValue();
		int appserver = number.intValue() - frontend - database;
		
		Application app = new ThreeTierBusinessApplication(userId, frontend, appserver, database);
		List<Application> apps = new ArrayList<Application>();
		apps.add(app);
		return apps;
	}

	@Override
	public InternetEstimator createInternetEstimator(List<FederationDatacenter> datacenters){
		return createDefaultInternetEstimator(datacenters);
	}
	
	public static InternetEstimator createDefaultInternetEstimator(List<FederationDatacenter> datacenters){
		InternetEstimator inetEst = new InternetEstimator(datacenters);
		return inetEst;
	}
}

