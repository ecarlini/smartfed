package it.cnr.isti.smartfed.metascheduler.constraints;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import it.cnr.isti.smartfed.federation.CostComputer;
import it.cnr.isti.smartfed.federation.application.ApplicationEdge;
import it.cnr.isti.smartfed.metascheduler.MSPolicy;
import it.cnr.isti.smartfed.metascheduler.resources.MSApplication;
import it.cnr.isti.smartfed.metascheduler.resources.MSApplicationNode;
import it.cnr.isti.smartfed.metascheduler.resources.iface.IMSApplication;
import it.cnr.isti.smartfed.metascheduler.resources.iface.IMSProvider;

import org.jgap.Gene;
import org.jgap.IChromosome;

public class LatencyConstraint extends MSPolicy
{

	public LatencyConstraint(double weight, char type) {
		super(weight, type);
	}

	@Override
	protected double evaluateLocalPolicy(Gene g, MSApplicationNode node, IMSProvider prov) 
	{
		throw new Error("Local evaluation not supported");
	}
	
	protected double evaluateGlobalPolicy(int gene_index, IChromosome chromos, IMSApplication app, IMSProvider prov)
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
				
				// check this edge against the InternetEstimator
			}
		}

		
		
		return 0;
	}
	
}
