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
		
		// find all target providers
		for (ApplicationEdge e: set)
		{
			if (e.getSourceVmId() == geneVmId)
			{
				int target_index = MSPolicy.getGeneIndexFromNodeId(e.getTargetVmId(), chromos.getGenes(), app);			
				int targetProvider = (int) chromos.getGenes()[target_index].getAllele();
				
				// check this edge's latency requirement against internet estimator
				int internet_latency = internet.getInternetLink(prov.getID(), targetProvider).getLatency();
				int application_latency = (int) e.getLatency();
				
				// TODO: evaluate the distance?
				// calculateDistance_ErrHandling(internet_latency, application_latency, 10000); //TODO
				// faccio la media
			}
		}

		
		
		return 0;
	}
	
}
