/*
Copyright 2014 ISTI-CNR
 
This file is part of SmartFed.

SmartFed is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.
 
SmartFed is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU General Public License for more details.
 
You should have received a copy of the GNU General Public License
along with SmartFed. If not, see <http://www.gnu.org/licenses/>.

*/

package it.cnr.isti.smartfed.papers.qbrokage;

import it.cnr.isti.smartfed.federation.FederationLog;
import it.cnr.isti.smartfed.federation.generation.GenerationType;
import it.cnr.isti.smartfed.federation.mapping.AbstractAllocator;
import it.cnr.isti.smartfed.federation.mapping.GeneticAllocator;
import it.cnr.isti.smartfed.metascheduler.MSPolicyFactory.PolicyType;
import it.cnr.isti.smartfed.test.TestResult;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

public class ExtBrokageScalability extends AbstractBrokageScalability {	
	
	final int[] numCloudlets = {12};
	final int[] numDatacenters = {5, 50, 100, 150, 200, 250, 300, 350, 400, 450, 500};

	public ExtBrokageScalability(GenerationType t) {
		super(t);
	}

	public static void main (String[] args) throws IOException{
		
		FederationLog.disable();
		ExtBrokageScalability ext = new ExtBrokageScalability(GenerationType.NON_UNIFORM);
		GeneticAllocator gen_allocator = new GeneticAllocator();
		gen_allocator.setPolicyType(PolicyType.DEFAULT_COST_NET);
		String str = ext.execute(gen_allocator);
		write(str, new File("plots/cost-dc" + ext.dcToString() +"cross0.35-mut10_costNet-" + ext.gentype +".dat"));
		System.out.println(counter);
		System.out.println("Fallito " + counter + " times");
		System.err.println("Ending exp");
		
		ext = new ExtBrokageScalability(GenerationType.UNIFORM);
		gen_allocator = new GeneticAllocator();
		gen_allocator.setPolicyType(PolicyType.DEFAULT_COST_NET);
		str = ext.execute_lessVariability(gen_allocator);
		write(str, new File("plots/cost-dc" + ext.dcToString() +"cross0.35-mut10_costNetLessVar-" + ext.gentype +".dat"));
		System.out.println(counter);
		System.out.println("Fallito " + counter + " times");
		
		System.err.println("Ending exp");
	}

	@Override
	public PaperDataset createDataset(int numOfVertex, int numOfCloudlets,
			int numOfDatacenter, int numHost, long seed, GenerationType t) {
		return new ExtBrokageDataset(numOfVertex, numOfCloudlets, numOfDatacenter, numHost, seed, t);
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
		double berger = TestResult.getBerger().getMean();
		str += String.format(Locale.ENGLISH, "%.5f", result) + "\t";
		str += String.format(Locale.ENGLISH, "%.2f", time) + "\t"; 
		str += String.format(Locale.ENGLISH, "%.2f", lockin) + "\t"; 
		str += String.format(Locale.ENGLISH, "%.2f", resultSTD) + "\t"; 
		str += String.format(Locale.ENGLISH, "%.2f", timeSTD) + "\t"; 
		str += String.format(Locale.ENGLISH, "%.2f", berger) + "\t"; 
		TestResult.reset();
		return str;
	}
	
}
