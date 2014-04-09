/*
Copyright 2013 ISTI-CNR
 
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

package it.cnr.isti.smartfed.federation.mapping;

import it.cnr.isti.smartfed.federation.MonitoringHub;
import it.cnr.isti.smartfed.federation.application.Application;
import it.cnr.isti.smartfed.federation.application.ApplicationVertex;
import it.cnr.isti.smartfed.federation.resources.FederationDatacenter;
import it.cnr.isti.smartfed.networking.InternetEstimator;

import java.util.List;
import java.util.Random;
import java.util.Set;

import org.cloudbus.cloudsim.Cloudlet;

public class RandomAllocator extends AbstractAllocator
{	
	public RandomAllocator()
	{
		super();
	}
	
	public RandomAllocator(MonitoringHub monitoring, InternetEstimator netEstimator)
	{
		super();
		this.setMonitoring(monitoring);
		this.setNetEstimator(netEstimator);
	}

	@Override
	public MappingSolution[] findAllocation(Application application)
	{
		Random rand = new Random(randomSeed);
		
		List<FederationDatacenter> dcs = getMonitoringHub().getView();
		this.solution = new MappingSolution(application);
			
		// for all the vertex of the graph
		Set<ApplicationVertex> vertexes =  application.vertexSet();
		for (ApplicationVertex vertex : vertexes)
		{
			List<Cloudlet> cloudlets = vertex.getCloudlets();
				
			for (Cloudlet c: cloudlets)
			{
				// choose a random datacenter
				FederationDatacenter fd = dcs.get(rand.nextInt(dcs.size()));
				solution.set(c, fd);
			}
		}
		solution.setAllocatorName(this.getClass().getSimpleName());
		return new MappingSolution[]{solution};
	}
}
