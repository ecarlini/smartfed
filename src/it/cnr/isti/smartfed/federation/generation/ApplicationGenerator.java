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

package it.cnr.isti.smartfed.federation.generation;

import it.cnr.isti.smartfed.federation.application.Application;
import it.cnr.isti.smartfed.federation.application.ApplicationVertex;
import it.cnr.isti.smartfed.federation.application.CloudletProvider;
import it.cnr.isti.smartfed.federation.resources.VmFactory;

import java.util.ArrayList;

import org.apache.commons.math3.distribution.AbstractRealDistribution;
import org.apache.commons.math3.distribution.UniformRealDistribution;
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Vm;

public class ApplicationGenerator extends AbstractGenerator
{	
	protected static final double BUDGET = 50;
	
	protected Range coreAmount;
	protected Range mipsAmount;
	protected Range ramAmount;
	protected Range bwAmount;
	protected Range diskAmount;
	

	public ApplicationGenerator(long seed)
	{
		super(seed);
		ramAmount = new Range(512, 1024*16);
		bwAmount = new Range(10*1024, 10*1024*1024);
		diskAmount = new Range(4096, 10*1024*1024); // 10TB max
		coreAmount = new Range(1, 8);
		mipsAmount = new Range(1000, 25000);
	}
	
	/**
	 * Return an application whose cloudlets are assigned to vertex with 
	 * an uniform distribution. 
	 * @param userId
	 * @param numVertex
	 * @param numCloudlet
	 * @return
	 */
	public Application getApplication(int userId, int numVertex, int numCloudlet)
	{
		UniformRealDistribution urd = new UniformRealDistribution();
		urd.reseedRandomGenerator(this.seed);
		return this.getApplication(userId, numVertex, numCloudlet, urd);
	}
	
	/**
	 * Return an application whose cloudlets are assigned to vertex with 
	 * a custom distribution.
	 * @param userId
	 * @param numVertex
	 * @param numCloudlet
	 * @return
	 */
	public Application getApplication(int userId, int numVertex, int numCloudlet, AbstractRealDistribution distribution)
	{
		if (numCloudlet < numVertex)
			throw new Error("Cannot create an application with more vertexes ("+numVertex+") than cloudlets ("+numCloudlet+")");
		
		int[] assignment = DistributionAssignment.getAssignmentArray(numVertex, numCloudlet, distribution);
		
		return getApplication(userId, assignment);
	}
	
	protected Application getApplication(int userId, int[] assignment)
	{	
		Application application = new Application();
		int numVertex = assignment.length;
		
		for (int i=0; i<numVertex; i++)
		{
			if (assignment[i] > 0)
			{
				int mips, cores, ramMB, bandMB, diskMB;
				
				if (type == GenerationType.UNIFORM)
				{
					double value = distribution.sample();
					mips = (int) mipsAmount.denormalize(value);
					cores = (int) coreAmount.denormalize(value);
					ramMB = (int) ramAmount.denormalize(value);
					bandMB = (int) bwAmount.denormalize(value);
					diskMB = (int) diskAmount.denormalize(value);
				}
				else
				{
					mips = (int) mipsAmount.denormalize(distribution.sample());
					cores = (int) coreAmount.denormalize(distribution.sample());
					ramMB = (int) ramAmount.denormalize(distribution.sample());
					bandMB = (int) bwAmount.denormalize(distribution.sample());
					diskMB = (int) diskAmount.denormalize(distribution.sample());
				}
				
				

				
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
