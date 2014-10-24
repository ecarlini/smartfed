package it.cnr.isti.smartfed.metascheduler.constraints;

import it.cnr.isti.smartfed.federation.application.ApplicationEdge;
import it.cnr.isti.smartfed.metascheduler.MSPolicy;
import it.cnr.isti.smartfed.metascheduler.resources.MSApplication;
import it.cnr.isti.smartfed.metascheduler.resources.MSApplicationNode;
import it.cnr.isti.smartfed.metascheduler.resources.iface.IMSApplication;
import it.cnr.isti.smartfed.metascheduler.resources.iface.IMSProvider;
import it.cnr.isti.smartfed.networking.InternetEstimator;

import java.util.Set;

import org.jgap.Gene;
import org.jgap.IChromosome;

public class LatencyConstraint extends MSPolicy
{

	public LatencyConstraint(double weight, char type) {
		super(weight, MSPolicy.DESCENDENT_TYPE);
	}

	@Override
	protected double evaluateLocalPolicy(Gene g, MSApplicationNode node, IMSProvider prov, InternetEstimator internet) 
	{
		throw new Error("Local evaluation not supported");
	}
	
	protected double evaluateGlobalPolicy(int gene_index, IChromosome chromos, IMSApplication app, IMSProvider prov, InternetEstimator internet)
	{
		// get the vm id of the gene
		MSApplicationNode curr_node = app.getNodes().get(gene_index); 
		int geneVmId = curr_node.getID();
		
		// get the set of the edges
		MSApplication am = (MSApplication) app;
		Set<ApplicationEdge> set = am.getEdges();
		
		double maxLatency = internet.getHighestLatency();
		double sumofdifference = 0;
		double numofdifference = 0;
	
		double distance = 0;
		
		// find all target providers
		for (ApplicationEdge e: set)
		{
			if (e.getSourceVmId() == geneVmId)
			{
				int target_index = MSPolicy.getGeneIndexFromNodeId(e.getTargetVmId(), chromos.getGenes(), app);			
				int targetProvider = (int) chromos.getGenes()[target_index].getAllele();
				
				// check this edge's latency requirement against internet estimator
				double internet_latency = internet.getInternetLink(prov.getID(), targetProvider).getLatency();
				double  application_latency = e.getLatency();
				
				// evaluate the distance
				double res = calculateDistance_ErrHandling(internet_latency, application_latency, maxLatency);
				sumofdifference += res;
				numofdifference ++;
			}
		}
		
		if (numofdifference == 0)
		{
			distance = MAXSATISFACTION_DISTANCE;
		}
		else
		{
			distance = sumofdifference / numofdifference;
		}

		return distance * getWeight();
	}
	
}
