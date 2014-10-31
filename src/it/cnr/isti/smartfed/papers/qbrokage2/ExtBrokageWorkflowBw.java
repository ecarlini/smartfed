package it.cnr.isti.smartfed.papers.qbrokage2;

import it.cnr.isti.smartfed.federation.FederationLog;
import it.cnr.isti.smartfed.federation.application.WorkflowApplication;
import it.cnr.isti.smartfed.federation.generation.GenerationType;
import it.cnr.isti.smartfed.federation.mapping.AbstractAllocator;
import it.cnr.isti.smartfed.federation.mapping.GeneticAllocator;
import it.cnr.isti.smartfed.metascheduler.MSPolicyFactory.PolicyType;
import it.cnr.isti.smartfed.test.TestResult;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

public class ExtBrokageWorkflowBw extends ExtBrokageWorkflow {

	public ExtBrokageWorkflowBw() {
		super(GenerationType.UNIFORM);
	}

	public static void main(String[] args) throws IOException{
		FederationLog.disable();
		ExtBrokageWorkflowBw ext = new ExtBrokageWorkflowBw();
		GeneticAllocator gen_allocator = new GeneticAllocator();
		gen_allocator.setPolicyType(PolicyType.GLOBAL_COST_BW);
		String str = ext.execute(gen_allocator);
		write(str, new File("plots/cost-dc" + ext.dcToString() + "-" + ext.repetitions + "rep" +"_bw"
				+ WorkflowApplication.fileName + "-" + ext.gentype +".dat"));
		System.out.println("Fallito " + counter + " times");
		
	}
	
	@Override
	public AbstractAllocator createOptimumAllocator() {
		AbstractAllocator optimumAlloc = new GeneticAllocator();
		((GeneticAllocator) optimumAlloc).setPolicyType(PolicyType.LOCAL_COST_BW);
		((GeneticAllocator) optimumAlloc).resetConstraints();
		return optimumAlloc;
	}

	/*
	@Override
	public String logResults() {
		String str = "";
		double result = TestResult.getCostDistance().getMean(); 
		double resultSTD = TestResult.getCostDistance().getStandardDeviation();
		double time = TestResult.getMappingTime().getMean();
		double timeSTD = TestResult.getMappingTime().getStandardDeviation();
		double lockin = TestResult.getLockDegree().getMean();
		double berger = TestResult.getTimeDifference().getMean();
		str += String.format(Locale.ENGLISH, "%.5f", result) + "\t";
		str += String.format(Locale.ENGLISH, "%.2f", time) + "\t"; 
		str += String.format(Locale.ENGLISH, "%.2f", lockin) + "\t"; 
		str += String.format(Locale.ENGLISH, "%.2f", resultSTD) + "\t"; 
		str += String.format(Locale.ENGLISH, "%.2f", timeSTD) + "\t"; 
		str += String.format(Locale.ENGLISH, "%.2f", berger) + "\t"; 
		TestResult.reset();
		return str;
	}
	*/

}

