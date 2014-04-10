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

import it.cnr.isti.smartfed.federation.application.Application;
import it.cnr.isti.smartfed.federation.application.ApplicationVertex;
import it.cnr.isti.smartfed.federation.application.CloudletProvider;
import it.cnr.isti.smartfed.federation.resources.VmFactory;
import it.cnr.isti.smartfed.federation.utils.DistributionAssignment;

import java.util.ArrayList;

import org.apache.commons.math3.distribution.AbstractIntegerDistribution;
import org.apache.commons.math3.distribution.AbstractRealDistribution;
import org.apache.commons.math3.distribution.UniformIntegerDistribution;
import org.apache.commons.math3.distribution.UniformRealDistribution;
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Vm;

public class ApplicationGenerator 
{	
	private static final double BUDGET = 50;
	private AbstractIntegerDistribution coreAmount;
	private AbstractIntegerDistribution mipsAmount;
	private AbstractIntegerDistribution ramAmount;
	private AbstractIntegerDistribution bwAmount;
	private AbstractIntegerDistribution diskAmount;
	
	private long seed;
	
	public ApplicationGenerator()
	{
		ramAmount = new UniformIntegerDistribution(512, 1024*16);
		bwAmount = new UniformIntegerDistribution(10*1024, 10*1024*1024);
		diskAmount = new UniformIntegerDistribution(4096, 10*1024*1024); // 10TB max
		coreAmount = new UniformIntegerDistribution(1, 8);
		mipsAmount = new UniformIntegerDistribution(1000, 25000);
	}
	
	public ApplicationGenerator(long seed)
	{
		this();
		this.resetSeed(seed);
	}
	
	public void resetSeed(long seed)
	{
		ramAmount.reseedRandomGenerator(seed);
		bwAmount.reseedRandomGenerator(seed);
		diskAmount.reseedRandomGenerator(seed);
		coreAmount.reseedRandomGenerator(seed);
		mipsAmount.reseedRandomGenerator(seed);
		
		this.seed = seed;
	}
	
	public Application getApplication(int userId, int numVertex, int numCloudlet)
	{
		UniformRealDistribution urd = new UniformRealDistribution();
		urd.reseedRandomGenerator(this.seed);
		return this.getApplication(userId, numVertex, numCloudlet, urd);
	}
	
	public Application getApplication(int userId, int numVertex, int numCloudlet, AbstractRealDistribution distribution)
	{
		if (numCloudlet < numVertex)
			throw new Error("Cannot create an application with more cloudlets ("+numCloudlet+") than vertexes ("+numVertex+")");
		
		int[] assignment = DistributionAssignment.getAssignmentArray(numVertex, numCloudlet, distribution);
				
		Application application = new Application();
		
		for (int i=0; i<numVertex; i++)
		{
			if (assignment[i] > 0)
			{
				int mips = mipsAmount.sample();
				int cores = coreAmount.sample();
				int ramMB = ramAmount.sample();
				int bandMB = bwAmount.sample();
				int diskMB = diskAmount.sample();
				
				Vm sample = VmFactory.getCustomVm(userId, mips, cores, ramMB, bandMB, diskMB);
				
				ArrayList<Cloudlet> cloudletList = new ArrayList<Cloudlet>();
				for (int c=0; c<assignment[i]; c++)
					cloudletList.add(CloudletProvider.getDefault());
				
				ApplicationVertex av = new ApplicationVertex(userId, cloudletList, sample);
				av.setBudget(ApplicationGenerator.BUDGET);
				av.setCountry("Italy");
				application.addVertex(av);
			}
		}
		
		return application;
	}
}
