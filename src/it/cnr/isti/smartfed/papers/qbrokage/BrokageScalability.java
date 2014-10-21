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
import it.cnr.isti.smartfed.federation.mapping.GreedyAllocator;
import it.cnr.isti.smartfed.test.TestResult;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

public class BrokageScalability extends AbstractBrokageScalability{	
	

	public PaperDataset createDataset(int numOfVertex, int numOfCloudlets, int numOfDatacenter, int numHost, long seed, GenerationType t){
		return new PaperDataset(numOfVertex, numOfCloudlets, numOfDatacenter, numHost, seed, t);
	}
	
	public AbstractAllocator createOptimumAllocator() {
		return new GreedyAllocator();
	}

	public String logResults(){
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
	
	public BrokageScalability(GenerationType t){
		super(t);
	}
	
	public BrokageScalability(){
		this(GenerationType.UNIFORM);
	}
	
	public static void main (String[] args) throws IOException{
		FederationLog.disable();
		BrokageScalability n = new BrokageScalability(GenerationType.UNIFORM);
		AbstractAllocator allocator = new GeneticAllocator();
		String str = "";
		str = n.execute(allocator);
		write(str, new File("plots/cost-dc" + n.dcToString() +"cross0.35-mut10_" + n.gentype +".dat"));
		System.out.println(counter);
		
		n = new BrokageScalability(GenerationType.NON_UNIFORM);
		allocator = new GeneticAllocator();
		str = n.execute(allocator);
		write(str, new File("plots/cost-dc" + n.dcToString() +"cross0.35-mut10_" + n.gentype +".dat"));
		System.out.println(counter);
		
		/* Uniform + less variability */
		n = new BrokageScalability(GenerationType.UNIFORM);
		allocator = new GeneticAllocator();
		str = n.execute_lessVariability(allocator);
		write(str, new File("plots/cost-dc" + n.dcToString() +"cross0.35-mut10_" + n.gentype + "LessVar" + ".dat"));
		System.out.println(counter);
		
		/* Non_Uniform + less variability */
		n = new BrokageScalability(GenerationType.NON_UNIFORM);
		allocator = new GeneticAllocator();
		str = n.execute_lessVariability(allocator);
		write(str, new File("plots/cost-dc" + n.dcToString() +"cross0.35-mut10_" + n.gentype + "LessVar" +".dat"));
		System.out.println(counter);
	}
	
	

}
