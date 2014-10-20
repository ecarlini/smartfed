package it.cnr.isti.smartfed.papers.qbrokage;

import it.cnr.isti.smartfed.federation.mapping.AbstractAllocator;
import it.cnr.isti.smartfed.federation.mapping.GeneticAllocator;
import it.cnr.isti.smartfed.federation.utils.UtilityPrint;
import it.cnr.isti.smartfed.metascheduler.MSPolicyFactory.PolicyType;

import java.io.File;
import java.io.IOException;

public class ExtBrokageWorkflow extends BrokageScalability{
	
	PaperDataset createDataset(int numOfVertex, int numOfCloudlets, int numOfDatacenter, int numHost, long seed){
		return new ExtBrokageWorkflowDataset(numOfVertex, numOfCloudlets, numOfDatacenter, numHost, seed);
	}

	public static void main(String[] args) throws IOException{
			ExtBrokageWorkflow n = new ExtBrokageWorkflow();
			n.repetitions = 2;
			n.numDatacenters = new int[]{50};
			
			String initial = "#dc,cost,time,lock,costSTD,timeSTD,berger" + n.dcToString() + "\n";
			String str = initial;
			
			GeneticAllocator allocator = new GeneticAllocator();
			allocator.setPolicyType(PolicyType.DEFAULT_COST_NET);
			str += n.execute(allocator);
			str += "\n";

			write(str, new File("/tmp/cost-dc" + n.dcToString() +"cross0.35-mut10"+".dat"));
			
			System.out.println(counter);

	}

}
