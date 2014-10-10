package it.cnr.isti.smartfed.papers.qbrokage;

import it.cnr.isti.smartfed.federation.application.Application;
import it.cnr.isti.smartfed.federation.application.ApplicationEdge;
import it.cnr.isti.smartfed.federation.application.ApplicationVertex;
import it.cnr.isti.smartfed.federation.application.CloudletProvider;
import it.cnr.isti.smartfed.federation.resources.ResourceCounter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Vm;


public class ExtBrokageDataset extends PaperDataset {	
	
	public static final double msize = 20;
	public static final double mrate = 0.27;// 20KB/req and 1000req/h
	
	public ExtBrokageDataset(int numVertex, int numberOfCloudlets,
			int numOfDatacenter, int numHost, long seed) {
		super(numVertex, numberOfCloudlets, numOfDatacenter, numHost, seed);
	}

	@Override
	public List<Application> createApplications(int userId) 
	{
		Application app = super.createApplications(userId).get(0);
		List<Vm> vms = app.getAllVms();
		assert vms.size() == numVertex;

		ResourceCounter.reset();
		Application newApp = new Application();
		for (Vm customVm: vms){
				List<Cloudlet> cloudletList = new ArrayList<Cloudlet>();
				cloudletList.add(CloudletProvider.getDefault());
				
				ApplicationVertex v = new ApplicationVertex(userId, cloudletList, customVm);
				v.setBudget(app.getVertexForVm(customVm).getBudget());
				v.setCountry(app.getVertexForVm(customVm).getCountry());
				newApp.addVertex(v);
		}
		for (ApplicationVertex v: newApp.vertexSet()){
			Set<ApplicationVertex> set = newApp.vertexSet();
			Iterator<ApplicationVertex> i = set.iterator();
			while (i.hasNext()){
				ApplicationVertex n = i.next();
				if (v.getId() != n.getId())
					newApp.addEdge(new ApplicationEdge(msize, mrate), v, n);
			}
		}
		System.out.println("****    Adding " + newApp.getEdges().size() + " edges");
		List<Application> list = new ArrayList<>(1);
		list.add(newApp);
		return list;
	}
}
