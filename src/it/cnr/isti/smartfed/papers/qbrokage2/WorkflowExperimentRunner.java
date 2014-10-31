package it.cnr.isti.smartfed.papers.qbrokage2;

import java.util.Locale;

import it.cnr.isti.smartfed.federation.mapping.GeneticAllocator;
import it.cnr.isti.smartfed.federation.mapping.GreedyAllocator;
import it.cnr.isti.smartfed.metascheduler.MSPolicyFactory.PolicyType;
import it.cnr.isti.smartfed.test.Experiment;
import it.cnr.isti.smartfed.test.TestResult;

public class WorkflowExperimentRunner 
{
	private static StringBuilder sb = new StringBuilder();
	
	public static void main(String[] args)
	{
		sb.append("\t\t").append("b Tc\t").append("b cost\t").append("net tc\t").append("net cost").append("\n");
		
		String[] files = new String[]{"Epigenomics_24"};
		//String[] files = new String[]{"Montage_25", "Montage_100"};
		for (String file: files)
			runWorkflow(file);
		
		System.out.println(sb);
	}
	
	
	private static void runWorkflow(String filename)
	{
		WorkflowDataset dataset = new WorkflowDataset(100, filename);
		
		// *** Computing the baseline ***
		GeneticAllocator allocator = new GeneticAllocator();
		allocator.setPolicyType(PolicyType.DEFAULT_COST);
		
		for (int i=0; i<1; i++)
		{
			Experiment experiment = new Experiment(allocator, dataset);		
			experiment.run();
		}
		
		double baseline_tc = TestResult.getCompletion().getMean();
		double baseline_cost = TestResult.getCost().getMean();
		
		
		// *** Computing the networked ***
		TestResult.reset();
		
		
		allocator = new GeneticAllocator();
		allocator.setPolicyType(PolicyType.GLOBAL_COST_BW);
		
		for (int i=0; i<1; i++)
		{
			Experiment experiment = new Experiment(allocator, dataset);		
			experiment.run();
		}
		
		double tc = TestResult.getCompletion().getMean();
		double cost = TestResult.getCost().getMean();
		
		sb.append(filename).append("\t");
		sb.append(f(baseline_tc)).append("\t").append(f(baseline_cost)).append("\t");
		sb.append(f(tc)).append("\t").append(f(cost)).append("\n");
	}
	
	
	private static String f(double value)
	{
		return String.format(Locale.ENGLISH, "%.2f", value);
	}
	
}
