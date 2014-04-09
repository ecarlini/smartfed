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

package it.cnr.isti.smartfed.federation;

import it.cnr.isti.smartfed.federation.application.Application;
import it.cnr.isti.smartfed.federation.application.ApplicationVertex;
import it.cnr.isti.smartfed.federation.mapping.MappingSolution;
import it.cnr.isti.smartfed.federation.resources.FederationDatacenter;
import it.cnr.isti.smartfed.federation.resources.VmFactory.VmType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;

/**
 * This class traces the allocation of a given application.
 * It considers the VM as the unit to allocate in datacenters.
 * 
 * @author carlini
 *
 */
public class Allocation 
{
	private double startSimTime = 0d;
	private double finishSimTime = 0d;
	private long startRealTime;
	private long finishRealTime; 

	/* The application considered */ 
	private Application application;

	/* the considered solution */
	private MappingSolution solution;
	
	/* the possible solutions computed by allocators */  
	private MappingSolution[] solutions;

	/* VMs that are requested for allocation, but are not allocated yet */ 
	private List<Vm> pending;

	/* VMs that are still to allocate */
	private List<Vm> remaining;

	/* VMs already allocated */
	private List<Vm> running;

	/* keeps the mapping VM to datacenter */
	private Map<Vm, FederationDatacenter> mapping;

	/* keeps the datacenter where a VM has been rejected*/
	private Map<Vm, List<Integer>> blacklistMap;

	/* tracks the datacenters mapping according to ApplicationVertex*/
	private Map<ApplicationVertex, Integer> avToDcid;
	
	/* local RNG */
	private Random random;
	
	public Allocation(Application application, MappingSolution[] solutions, long seed)
	{
		this.application = application;
		this.solutions = solutions;

		pending = new ArrayList<Vm>();
		running = new ArrayList<Vm>();
		remaining = new ArrayList<Vm>();
		mapping = new HashMap<Vm, FederationDatacenter>();
		blacklistMap = new HashMap<Vm, List<Integer>>();
		avToDcid = new HashMap<ApplicationVertex, Integer>();

		for (Vm vm: application.getAllVms())
		{
			blacklistMap.put(vm, new ArrayList<Integer>());
		}

		remaining.addAll(application.getAllVms());

		startSimTime = CloudSim.clock();
		startRealTime = System.currentTimeMillis();
		random = new Random(seed);
	}

	public Application getApplication()
	{
		return this.application;
	}

	public void setRunning(Vm vm, Integer datacenterId)
	{
		ApplicationVertex av = application.getVertexForVm(vm);
		avToDcid.put(av, datacenterId);

		pending.remove(vm);
		running.add(vm);
		mapping.put(vm, (FederationDatacenter)CloudSim.getEntity(datacenterId));
	}

	// FIXME: never called, do we need it?
/*	public void unsetRunning(Vm vm)
	{
		running.remove(vm);
		pending.add(vm);
		Integer datacenterId = mapping.remove(vm).getId();

		List<Integer> blacklist = blacklistMap.get(vm);
		blacklist.add(datacenterId);
	}
*/
	public void failedMapping(Vm vm, Integer datacenterId)
	{
		List<Integer> blacklist = blacklistMap.get(vm);
		blacklist.add(datacenterId);
		pending.remove(vm);
		remaining.add(vm);
	}

	public Vm getNextVm()
	{
		if (isCompleted() == false)
		{
			Vm vm = remaining.remove(0);
			pending.add(vm);
			return vm;
		}

		return null;
	}

	/**
	 * Returns true if the allocation is completed.
	 * @return
	 */
	public boolean isCompleted()
	{
		boolean result = ((remaining.size() == 0) && (pending.size() == 0));

		if (result)
		{
			finishSimTime = CloudSim.clock();
			finishRealTime = System.currentTimeMillis();
		}

		return result;
	}

	/**
	 * Return the DC id in which the vm is allocated.
	 * @param vm
	 * @return
	 */
	public Integer getAllocatedDatacenterId(Vm vm)
	{
		return mapping.get(vm).getId();
	}
	
	/**
	 * Return the dartacenter in which the vm is allocated.
	 * @param vm
	 * @return
	 */
	public FederationDatacenter getAllocatedDatacenter(Vm vm)
	{
		return mapping.get(vm);
	}


	/**
	 * This method is called by the federation.
	 * It returns the datacenter in which the VM is supposed to 
	 * be allocated about the plan.
	 * If the datacenter chosen already refused the VM,
	 * the methods return another _random_ datacenter.
	 * 
	 * @param vm
	 * @param datacenters
	 * @param net
	 * @return
	 */
	public Integer pickDatacenter(Vm vm, List<FederationDatacenter> datacenters)
	{
		// recover the cloudlet from the VM
		Cloudlet cloudlet = application.getVertexForVm(vm).getAssociatedCloudlet(vm);
		boolean foundDC = false;
		int i = 0;
		
		Integer dcid = null; 
		while (i < solutions.length && !foundDC) {
			FederationDatacenter dc = solutions[i].getMapping().get(cloudlet);
		
			if (dc != null)
				dcid = dc.getId();
		
			// if the dc is not the blacklist, we have found it
			if (blacklistMap.get(vm).contains(dcid) == false){
				foundDC = true;
				solution = solutions[i];
			}	
			i++;
		}
		
		if (foundDC) {
			return dcid;
		}
		else
		{			
			List<Integer> list = new ArrayList<Integer>();
			for (FederationDatacenter item : datacenters)
			{
				if (blacklistMap.get(vm).contains(item.getId()) == false)
					list.add(item.getId());
			}
			
			if (list.isEmpty())
				return -1; // no dc for the VM: failure.
			else
			{
				// return a random DC
				return list.get(random.nextInt(list.size()));
			}
		}		

	}

	public double getSimDuration()
	{
		return finishSimTime - startSimTime;
	}

	public long getRealDuration()
	{
		return finishRealTime - startRealTime;
	}
	

	/**
	 * Returns the number of VM that are allocated on a
	 * different datacenter w.r.t to the mapping solution
	 * @return
	 */
	public int differenceWithSolution()
	{
		int count = 0;
		
		for (List<Integer> list: blacklistMap.values())
		{
			if (list.size() > 0)
				count++;
		}
		
		/*
		for (Cloudlet c: solution.getMapping().keySet())
		{
			// recover the target dc
			FederationDatacenter target = solution.getMapping().get(c);
			
			// check if the association correspond to the mapping
			Vm vm = application.getVertexForCloudlet(c).getAssociatedVm(c);
			FederationDatacenter actual = mapping.get(vm);
		
			if (target.getId() != actual.getId())
				count++;
		}
		*/
		
		return count;
	}

	/**
	 * Counts the number of time any VM has been refused by a
	 * datacenter.
	 * @return
	 */
	public int getTotalReallocations() 
	{
		int result = 0;
		for (Vm vm: blacklistMap.keySet())
		{
			int count = blacklistMap.get(vm).size();
			result += count;
		}
		return result;
	}

}
