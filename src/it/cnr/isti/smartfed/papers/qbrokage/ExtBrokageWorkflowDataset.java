package it.cnr.isti.smartfed.papers.qbrokage;

import it.cnr.isti.smartfed.federation.application.Application;
import it.cnr.isti.smartfed.test.WorkflowApplication;

import java.util.ArrayList;
import java.util.List;

public class ExtBrokageWorkflowDataset extends PaperDataset {

	public ExtBrokageWorkflowDataset(int numVertex, int numberOfCloudlets,
			int numOfDatacenter, int numHost, long seed) {
		super(numVertex, numberOfCloudlets, numOfDatacenter, numHost, seed);
	}

	@Override
	public List<Application> createApplications(int userId) 
	{
		List<Application> list = new ArrayList<>(1);
		list.add(new WorkflowApplication(userId));
		return list;
	}
	

}
