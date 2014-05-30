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

package it.cnr.isti.smartfed.federation.mapping;

import java.util.List;
import java.util.Set;

import org.cloudbus.cloudsim.Cloudlet;

import it.cnr.isti.smartfed.federation.MonitoringHub;
import it.cnr.isti.smartfed.federation.application.Application;
import it.cnr.isti.smartfed.federation.application.ApplicationVertex;
import it.cnr.isti.smartfed.federation.resources.FederationDatacenter;
import it.cnr.isti.smartfed.networking.InternetEstimator;

public class RoundRobinAllocator extends AbstractAllocator {

	public RoundRobinAllocator(MonitoringHub monitoring, InternetEstimator netEstimator) {
		super();
		this.setMonitoring(monitoring);
		this.setNetEstimator(netEstimator);
	}
	
	public RoundRobinAllocator() {
		super();
	}

	@Override
	public MappingSolution[] findAllocation(Application application) {
		List<FederationDatacenter> dcs = getMonitoringHub().getView();
		if (dcs.size() == 0) 
			throw new RuntimeException("No datacenters!!!");
		else 
			System.out.println(dcs.size());

		MappingSolution s = new MappingSolution(application);
		// for all the vertex of the graph
		Set<ApplicationVertex> vertexes =  application.vertexSet();
		int i = 0;
		
		for (ApplicationVertex vertex : vertexes) {
			List<Cloudlet> cloudlets = vertex.getCloudlets();

			for (Cloudlet c: cloudlets) {
				FederationDatacenter fd = dcs.get(i++ % dcs.size());
				
				s.set(c, fd);
			}
		}
		solution = s;
		solution.setAllocatorName(this.getClass().getSimpleName());
		return new MappingSolution[]{solution};
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
