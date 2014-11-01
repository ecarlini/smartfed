package it.cnr.isti.smartfed.papers.qbrokage2;

import it.cnr.isti.smartfed.federation.mapping.GeneticAllocator;
import it.cnr.isti.smartfed.metascheduler.MSPolicyFactory.PolicyType;
import it.cnr.isti.smartfed.test.Experiment;
import it.cnr.isti.smartfed.test.TestResult;

import java.util.Locale;

public class WorkflowExperimentRunner 
{
	private static StringBuilder sb = new StringBuilder();
	
	public static void main(String[] args)
	{
		sb.append("\t\t").append("bTc\t").append("bw/h\t").append("bc/h\t").append("nTc\t")
		.append("nw/h\t").append("nc/h").append("\n");
		
		// String[] files = new String[]{"Epigenomics_24"};
		String[] files = new String[]{"Montage_25", "Montage_50", "Montage_100"};//, "Montage_1000"};
		for (String file: files)
			runWorkflow(file);
		
		System.out.println(sb);
	}
	
	
	private static void runWorkflow(String filename)
	{
		WorkflowDataset dataset = new WorkflowDataset(100, filename);
		
		// *** Computing the baseline ***
		TestResult.reset();
		GeneticAllocator allocator = new GeneticAllocator();
		allocator.setPolicyType(PolicyType.DEFAULT_COST);
		
		
		for (int i=0; i<10; i++)
		{
			allocator.setRandomSeed(i);
			dataset.setSeed(i*7);
			Experiment experiment = new Experiment(allocator, dataset);		
			experiment.run();
		}
		
		double baseline_tc = TestResult.getCompletion().getMean();
		double baseline_cost = TestResult.getCost().getMean();
		
		// *** Computing the networked ***
		TestResult.reset();
		
		allocator = new GeneticAllocator();
		allocator.resetConstraints();
		allocator.setPolicyType(PolicyType.GLOBAL_COST_BW);
		
		for (int i=0; i<10; i++)
		{
			allocator.setRandomSeed(i);
			dataset.setSeed(i*7);
			Experiment experiment = new Experiment(allocator, dataset);		
			experiment.run();
		}
		
		double tc = TestResult.getCompletion().getMean();
		double cost = TestResult.getCost().getMean();
		
		sb.append(filename).append("\t");
		sb.append(f(baseline_tc)).append("\t").append(f(3600/baseline_tc)).append("\t")
		.append(f(baseline_cost)).append("\t");
		sb.append(f(tc)).append("\t").append(f(3600/ tc)).append("\t").append(f(cost)).append("\n");
	}
	
	
	private static String f(double value)
	{
		return String.format(Locale.ENGLISH, "%.1f", value);
	}
	
}
