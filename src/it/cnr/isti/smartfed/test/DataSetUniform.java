package it.cnr.isti.smartfed.test;

import it.cnr.isti.smartfed.federation.resources.FederationDatacenter;

import java.util.List;

public class DataSetUniform extends DataSet
{

	public DataSetUniform(int numberOfCloudlets, int numOfDatacenter, int numHost) 
	{
		super(numberOfCloudlets, numOfDatacenter, numHost);
	}
	
	@Override
	public List<FederationDatacenter> createDatacenters() 
	{
		return DatacenterFacilities.getUniformDistribution(numOfDatacenters, numHost);
	}	
}
