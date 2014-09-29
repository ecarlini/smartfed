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

package it.cnr.isti.smartfed.federation.mapping.online;

import it.cnr.isti.smartfed.federation.MonitoringHub;
import it.cnr.isti.smartfed.federation.application.Application;
import it.cnr.isti.smartfed.federation.application.ApplicationEdge;
import it.cnr.isti.smartfed.federation.application.ApplicationVertex;
import it.cnr.isti.smartfed.federation.mapping.AbstractAllocator;
import it.cnr.isti.smartfed.federation.mapping.MappingSolution;
import it.cnr.isti.smartfed.federation.resources.FederationDatacenter;
import it.cnr.isti.smartfed.federation.utils.UtilityPrint;
import it.cnr.isti.smartfed.networking.InternetEstimator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Vm;

public class GreedyRollbackAllocator extends AbstractAllocator
{
	public GreedyRollbackAllocator()
	{
		super();
	}
	
	public GreedyRollbackAllocator(MonitoringHub monitoring, InternetEstimator netEstimator)
	{
		super();
		this.setMonitoring(monitoring);
		this.setNetEstimator(netEstimator);
	}

	@Override
	public MappingSolution[] findAllocation(Application application)
	{
		MappingSolution solution = new MappingSolution(application);
		System.out.println("There are "+application.getAllCloudlets().size()+" Cloudlets to allocate.");
		
		// keep trace of the status of the assignment and visited datacenters
		List<Cloudlet> assigned = new ArrayList<Cloudlet>();
		Map<Cloudlet, Set<FederationDatacenter>> visited = new HashMap<Cloudlet, Set<FederationDatacenter>>();
			
		// Initialize the netestimator session
		// Long netSession = netEstimator.createSession();
		
		
		// main loop: until all the cloudlet are assigned
		Cloudlet cloudlet = null;
		while ((cloudlet = takeNonAssigned(assigned, application.getAllCloudlets())) != null)
		{
			// take the vm for the cloudlet
			Vm vm = application.getVertexForCloudlet(cloudlet).getAssociatedVm(cloudlet);
			boolean is_assigned = false; // true when the cloudlet assigned
			Set<FederationDatacenter> visitedDatacenter = visited.get(cloudlet);
			if (visitedDatacenter == null)
			{
				visitedDatacenter = new HashSet<FederationDatacenter>();
				visited.put(cloudlet, visitedDatacenter);
			}
				
			// internal loop: until there are unvistited providers or the cloudlet is assigned
			while (visitedDatacenter.size() < monitoring.getView().size() && (is_assigned == false))
			{
				System.out.println(UtilityPrint.toString(cloudlet)+": DC visited = "+visitedDatacenter.size());
				
				// pick a datacenter 
				FederationDatacenter fd = pickDatacenter(monitoring.getView(), visitedDatacenter);
				// VmAllocationPolicySimple v;
				
				// check the datacenter and the connection using the netestimator
				boolean ok_vm = fd.getVmAllocationPolicy().allocateHostForVm(vm);
				boolean ok_net = true; //this.checkNetwork(cloudlet, fd, solution, application, netSession);
				
				boolean suitable = ok_vm && ok_net;
								
				System.out.println(UtilityPrint.toString(cloudlet)+" to "+fd+" = VM:"+ok_vm+" Net:"+ok_net);
				
				if (suitable) // host found
				{
					solution.set(cloudlet, fd);
					assigned.add(cloudlet);
					is_assigned = true;
					//System.out.println(UtilityPrint.toString(cloudlet)+" assigned to "+fd);
					
					//reset the visited datacenters
					// visitedDatacenter.clear();
					visitedDatacenter.add(fd);
				}
				else // host not found
				{
					visitedDatacenter.add(fd);
				}
			}
			
			if (is_assigned == false) // the VM cannot be received by any provider
			{
				if (assigned.size() > 0)
				{
					// remove the cloudlet from the set of the assigned
					Cloudlet removed = assigned.remove(assigned.size() - 1);
					FederationDatacenter fdRemoved = solution.getMapping().get(removed);
					
					// deallocate network
					for (Cloudlet other: application.getAllCloudletLinked(removed))
					{
						if (solution.getMapping().keySet().contains(other))
						{
							FederationDatacenter otherFD = solution.getMapping().get(other);
							if (otherFD.equals(fdRemoved) == false) // we consider only the case of different datacenter
							{
								ApplicationVertex source = application.getVertexForCloudlet(removed);
								ApplicationVertex dest = application.getVertexForCloudlet(other);
								// netEstimator.deallocateLink(fdRemoved, otherFD, application.edgeBetween(source, dest), application);
							}
						}
					}
					
					
					// deallocate the corresponding vm from the datacenters
					fdRemoved.getVmAllocationPolicy().deallocateHostForVm(application.getVertexForCloudlet(removed).getAssociatedVm(removed));
					
					// clean up the visited datacenters
					// visitedDatacenter.clear();
					visitedDatacenter.add(fdRemoved);
					
					// remove the assignemnt in the solution
					solution.unset(removed);
					
					System.out.println("Rollback. "+UtilityPrint.toString(removed)+" removed from "+fdRemoved);
				}
				else // failing
				{
					System.out.println("Mapping failed");
					solution.setValid(false);
					return new MappingSolution[]{solution};
				}
			}
			
		}
	
		// netEstimator.consolidateAllocationSession(netSession);
		return new MappingSolution[]{solution};
	}
	
	
	/**
	 * returns null if all cloudlet are assigned
	 */
	private Cloudlet takeNonAssigned(List<Cloudlet> assigned, List<Cloudlet> all)
	{
		for (Cloudlet c: all)
		{
			if (assigned.contains(c) == false)
				return c;
		}
		
		return null;
	}
	
	private FederationDatacenter pickDatacenter(List<FederationDatacenter> dcs, Set<FederationDatacenter> visitedDatacenter)
	{
		for (FederationDatacenter fd: dcs)
		{
			if (visitedDatacenter.contains(fd) == false)
				return fd;
		}
		
		return null; // should never get here.
	}

	private boolean checkNetwork(Cloudlet cloudlet, FederationDatacenter sourceFd, MappingSolution solution, Application application, long netSession)
	{
		ApplicationVertex sourceVertex = application.getVertexForCloudlet(cloudlet);
		
		for (Cloudlet c : solution.getMapping().keySet())
		{
			// this is the destination Vertex and Fd
			ApplicationVertex destinationVertex = application.getVertexForCloudlet(c);
			ApplicationEdge edge = application.edgeBetween(sourceVertex, destinationVertex);
			
			if (edge == null) // if no edge in the application, the cloudlet dont have to communicate
				return true;
			
			FederationDatacenter destinationFd = solution.getMapping().get(c);
		
			// four cases:
			// -- the cloudlets are in the same vertex, and same datacenter
			// -- the cloudlets are in the same vertex, but different datacenter
			// -- the cloudlets are in different vertex, but same datacenter
			// -- the cloudltes are in different vertex, and differente datacenter
			
			if (sourceVertex.equals(destinationVertex))
			{
				if (sourceFd.equals(destinationFd)) // same dc
				{
					// then OK, do nothing
				}
				else
				{
					// check whether the link between the two datacenters respect the assumption made for cloudlets inside the same vertex
				}
			}
			else // different vertices
			{
				if (sourceFd.equals(destinationFd)) // same dc
				{
					// then OK, do nothing
				}
				else
				{
					// TODO check whether the link between the two datacenters respect the connection in the application					
					// boolean result = netEstimator.allocateLink(netSession, sourceFd, destinationFd, edge, application);
					boolean result = true;
					if (result == false)
						return result;
				}
			}
			
		}
		
		
		return true;
	}
}
