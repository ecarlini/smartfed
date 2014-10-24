package it.cnr.isti.smartfed.papers.qbrokage;

import it.cnr.isti.smartfed.federation.FederationLog;
import it.cnr.isti.smartfed.federation.generation.GenerationType;
import it.cnr.isti.smartfed.federation.mapping.GeneticAllocator;
import it.cnr.isti.smartfed.metascheduler.MSPolicyFactory.PolicyType;

import java.io.File;
import java.io.IOException;

public class ExtBrokageWorkflow extends ExtBrokageScalability {
	
	public ExtBrokageWorkflow(GenerationType t) {
		super(t);
	}

	@Override
	public PaperDataset createDataset(int numOfVertex, int numOfCloudlets, int numOfDatacenter, 
										int numHost, long seed, GenerationType t){
		return new ExtBrokageWorkflowDataset(numOfVertex, numOfCloudlets, numOfDatacenter, numHost, seed, t);
	}

	public static void main(String[] args) throws IOException
	{
		FederationLog.disable();
		ExtBrokageWorkflow ext = new ExtBrokageWorkflow(GenerationType.NON_UNIFORM);
		GeneticAllocator gen_allocator = new GeneticAllocator();
		gen_allocator.setPolicyType(PolicyType.DEFAULT_COST_NET);
		String str = ext.execute(gen_allocator);
		write(str, new File("plots/cost-dc" + ext.dcToString() +"cross0.35-mut10_workflow-" + ext.gentype +".dat"));
		System.out.println(counter);
		System.out.println("Fallito " + counter + " times");
		
		ext = new ExtBrokageWorkflow(GenerationType.UNIFORM);
		gen_allocator = new GeneticAllocator();
		gen_allocator.setPolicyType(PolicyType.DEFAULT_COST_NET);
		str = ext.execute(gen_allocator);
		write(str, new File("plots/cost-dc" + ext.dcToString() +"cross0.35-mut10_workflow-" + ext.gentype +".dat"));
		System.out.println(counter);
		System.out.println("Fallito " + counter + " times");
		
	}

}
