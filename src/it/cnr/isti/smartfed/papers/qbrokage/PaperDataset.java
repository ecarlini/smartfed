package it.cnr.isti.smartfed.papers.qbrokage;

import it.cnr.isti.smartfed.federation.application.Application;
import it.cnr.isti.smartfed.federation.generation.ApplicationGenerator;
import it.cnr.isti.smartfed.federation.generation.DatacenterGenerator;
import it.cnr.isti.smartfed.federation.generation.GenerationType;
import it.cnr.isti.smartfed.federation.resources.Country;
import it.cnr.isti.smartfed.federation.resources.FederationDatacenter;
import it.cnr.isti.smartfed.test.DataSet;

import java.util.ArrayList;
import java.util.List;

public class PaperDataset extends DataSet
{	
	protected int numVertex;
	protected long seed;
	public GenerationType gentype;
	
	
	public PaperDataset(int numVertex, int numberOfCloudlets, int numOfDatacenter, int numHost)
	{
		super(numberOfCloudlets, numOfDatacenter, numHost);
		this.seed = System.currentTimeMillis();
		this.numVertex = numVertex;
	}
	
	public PaperDataset(int numVertex, int numberOfCloudlets, int numOfDatacenter, int numHost, long seed)
	{
		this(numVertex, numberOfCloudlets, numOfDatacenter, numHost, seed, GenerationType.UNIFORM);
	}
	
	public PaperDataset(int numVertex, int numberOfCloudlets, int numOfDatacenter, int numHost, long seed, GenerationType mytype)
	{
		super(numberOfCloudlets, numOfDatacenter, numHost);
		this.seed = seed;
		this.numVertex = numVertex;
		this.gentype = mytype;
	}


	@Override
	public List<FederationDatacenter> createDatacenters() 
	{
		DatacenterGenerator dg = new DatacenterGenerator(this.seed * 15);
		dg.setType(gentype);
		dg.setCountries(new Country[]{Country.Italy});
		return dg.getDatacenters(numOfDatacenters, numHost);
	}
	
	@Override
	public List<Application> createApplications(int userId) 
	{
		ApplicationGenerator ag = new ApplicationGenerator(this.seed + 13);
		ag.setType(gentype);
		Application app = ag.getApplication(userId, numVertex, this.numberOfCloudlets);
		List<Application> list = new ArrayList<>(1);
		list.add(app);
		return list;
	}
}