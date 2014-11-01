package it.cnr.isti.smartfed.papers.qbrokage2;

import java.util.ArrayList;
import java.util.List;

import it.cnr.isti.smartfed.federation.application.Application;
import it.cnr.isti.smartfed.federation.application.WorkflowApplication;
import it.cnr.isti.smartfed.federation.generation.DatacenterGenerator;
import it.cnr.isti.smartfed.federation.generation.GenerationType;
import it.cnr.isti.smartfed.federation.resources.Country;
import it.cnr.isti.smartfed.federation.resources.FederationDatacenter;
import it.cnr.isti.smartfed.networking.InternetEstimator;
import it.cnr.isti.smartfed.test.InterfaceDataSet;

public class WorkflowDataset implements InterfaceDataSet
{
	protected long seed = 77;
	protected GenerationType gentype = GenerationType.UNIFORM;
	
	private String filename;
	private int numOfDatacenters;
	
	public WorkflowDataset(int numOfDatacenters, String filename)
	{
		this.filename = filename;
		this.numOfDatacenters = numOfDatacenters;
	}
	
	public void setSeed(long seed)
	{
		this.seed = seed;
	}
	
	@Override
	public List<FederationDatacenter> createDatacenters()
	{
		DatacenterGenerator dg = new DatacenterGenerator(this.seed * 15);
		dg.setType(gentype);
		dg.setCountries(new Country[]{Country.Italy, Country.France});
		int numHost = 100  * numOfDatacenters; // it will assign more or less 100 host to each datacenter
		List<FederationDatacenter> dcs = dg.getDatacenters(numOfDatacenters, numHost);
		return dcs;
	}
	
	@Override
	public List<Application> createApplications(int userId)
	{
		List<Application> apps = new ArrayList<Application>();
		apps.add(new WorkflowApplication(filename, userId, false));
		return apps;
	}

	@Override
	public InternetEstimator createInternetEstimator(List<FederationDatacenter> datacenters) {
		InternetEstimator inetEst = new InternetEstimator(datacenters);
		return inetEst;
	}
	
}
