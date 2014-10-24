package it.cnr.isti.smartfed.papers.qbrokage;

import it.cnr.isti.smartfed.federation.FederationLog;
import it.cnr.isti.smartfed.federation.application.Application;
import it.cnr.isti.smartfed.federation.generation.DatacenterGenerator;
import it.cnr.isti.smartfed.federation.generation.GenerationType;
import it.cnr.isti.smartfed.federation.mapping.AbstractAllocator;
import it.cnr.isti.smartfed.federation.mapping.GeneticAllocator;
import it.cnr.isti.smartfed.federation.resources.Country;
import it.cnr.isti.smartfed.federation.resources.FederationDatacenter;
import it.cnr.isti.smartfed.metascheduler.MSPolicyFactory.PolicyType;
import it.cnr.isti.smartfed.test.TestResult;
import it.cnr.isti.smartfed.test.WorkflowApplication;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ExtBrokageWorkflow extends AbstractBrokageScalability {
	
	public ExtBrokageWorkflow(GenerationType t) {
		super(t);
		file_header = "#dc,cost,time,lock,costSTD,timeSTD,timeDiff" + dcToString() + "\n";
	}

	@Override
	public PaperDataset createDataset(int numOfVertex, int numOfCloudlets, int numOfDatacenter, 
										int numHost, long seed, GenerationType t){
		return new ExtBrokageWorkflowDataset(numOfVertex, numOfCloudlets, numOfDatacenter, numHost, seed, t);
	}

	public static void main(String[] args) throws IOException
	{
		FederationLog.disable();
		ExtBrokageWorkflow ext = new ExtBrokageWorkflow(GenerationType.UNIFORM);
		GeneticAllocator gen_allocator = new GeneticAllocator();
		gen_allocator.setPolicyType(PolicyType.DEFAULT_COST_NET);
		String str = ext.execute(gen_allocator);
		write(str, new File("plots/cost-dc" + ext.dcToString() + "-" + ext.repetitions + "rep" +"_workflow-" + ext.gentype +".dat"));
		System.out.println("Fallito " + counter + " times");
		
	}
	
	@Override
	public AbstractAllocator createOptimumAllocator() {
		AbstractAllocator optimumAlloc = new GeneticAllocator();
		((GeneticAllocator) optimumAlloc).resetConstraints();
		return optimumAlloc;
	}

	@Override
	public String logResults() {
		String str = "";
		double result = TestResult.getCostDistance().getMean(); 
		double resultSTD = TestResult.getCostDistance().getStandardDeviation();
		double time = TestResult.getMappingTime().getMean();
		double timeSTD = TestResult.getMappingTime().getStandardDeviation();
		double lockin = TestResult.getLockDegree().getMean();
		double timeDiff = TestResult.getTimeDifference().getMean();
		double timeDiffStd = TestResult.getTimeDifference().getStandardDeviation();
		str += String.format(Locale.ENGLISH, "%.5f", result) + "\t";
		str += String.format(Locale.ENGLISH, "%.2f", time) + "\t"; 
		str += String.format(Locale.ENGLISH, "%.2f", lockin) + "\t"; 
		str += String.format(Locale.ENGLISH, "%.2f", resultSTD) + "\t"; 
		str += String.format(Locale.ENGLISH, "%.2f", timeSTD) + "\t"; 
		str += String.format(Locale.ENGLISH, "%.2f", timeDiff) + "\t"; 
		str += String.format(Locale.ENGLISH, "%.2f", timeDiffStd) + "\t"; 
		TestResult.reset();
		return str;
	}

}

class ExtBrokageWorkflowDataset extends PaperDataset {

	public ExtBrokageWorkflowDataset(int numVertex, int numberOfCloudlets,
			int numOfDatacenter, int numHost, long seed, GenerationType t) {
		super(numVertex, numberOfCloudlets, numOfDatacenter, numHost, seed, t);
	}

	@Override
	public List<FederationDatacenter> createDatacenters() 
	{
		DatacenterGenerator dg = new DatacenterGenerator(this.seed * 15);
		dg.setType(gentype);
		dg.setCountries(new Country[]{Country.Italy, Country.France});
		return dg.getDatacenters(numOfDatacenters, numHost);
	}
	
	@Override
	public List<Application> createApplications(int userId) 
	{
		List<Application> list = new ArrayList<>(1);
		list.add(new WorkflowApplication(userId, true));
		return list;
	}
}
