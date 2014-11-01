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

import java.util.Set;

import it.cnr.isti.smartfed.federation.application.Application;
import it.cnr.isti.smartfed.federation.application.ApplicationEdge;
import it.cnr.isti.smartfed.federation.application.ApplicationVertex;
import it.cnr.isti.smartfed.federation.mapping.MappingSolution;
import it.cnr.isti.smartfed.federation.resources.FederationDatacenter;
import it.cnr.isti.smartfed.federation.resources.VmFactory.VmType;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Vm;

/**
 * This class contains facilities to compute the cost
 * for VMs.
 * @author carlini, anastasi
 *
 */
public class CostComputer 
{
	/**
	 * Compute the cost for the given MappingSolution.
	 * If a cloudlet is not assigned, its cost is not considered.
	 * @param solution
	 * @return
	 */
	public static double expectedCost(MappingSolution solution)
	{
		Application application = solution.getApplication();
		double amount = 0d;
		
		for (Cloudlet c: application.getAllCloudlets())
		{
			FederationDatacenter datacenter = solution.getMapping().get(c);
			Vm vm = application.getVertexForCloudlet(c).getAssociatedVm(c);
			VmType type = application.getVertexForVm(vm).getVmType();
			
			if (datacenter != null)
				amount += singleVmCost(vm, type, datacenter);
		}
		
		return amount;
	}
	
	/**
	 * Computes the cost for the given allocation.
	 * If a VM is not allocated, its cost is not considered.
	 * @param allocation
	 * @return
	 */
	public static double actualCost(Allocation allocation)
	{
		double amount = 0d, cost = 0d;
		
		for (Vm vm: allocation.getApplication().getAllVms())
		{
			FederationDatacenter datacenter = allocation.getAllocatedDatacenter(vm);
			ApplicationVertex vertex = allocation.getApplication().getVertexForVm(vm);
			Set<ApplicationEdge> edges = allocation.getApplication().edgesOf(vertex);
			VmType type = vertex.getVmType();
			
			if (datacenter != null){	
					cost = singleVmCost(vm, type, datacenter); 
					
					// the vm is typed but the provider does not have price for that type, so we calculate cost for the desired vm 
					if (Double.isNaN(cost)) {
						if (vertex.getDesiredVm() == null)
							cost = calculateCostCustomVm(datacenter, vm);
						else
							cost = calculateCostCustomVm(datacenter, vertex.getDesiredVm());
					}
					amount += cost;
					//System.out.println("Net cost is for vm " + vm.getId() + " is " + computeNetCosts(vm, edges, allocation, datacenter));
					amount += computeNetCosts(vm, edges, allocation, datacenter);
			}
			// System.out.println("Partial Amount is " + cost);
		}
		return amount;
	}
	
	/**
	 * It calculates only the net costs (used by Experiment)
	 * @param allocation
	 * @return
	 */
	public static double actualNetCost(Allocation allocation){
		double amount = 0d;
		
		for (Vm vm: allocation.getApplication().getAllVms())
		{
			FederationDatacenter datacenter = allocation.getAllocatedDatacenter(vm);
			ApplicationVertex vertex = allocation.getApplication().getVertexForVm(vm);
			Set<ApplicationEdge> edges = allocation.getApplication().edgesOf(vertex);
			
			if (datacenter != null){	
					amount += computeNetCosts(vm, edges, allocation, datacenter);
			}
		}
		return amount;
	}
	
	/**
	 * @param vm
	 * @param es
	 * @param a
	 * @return
	 */
	private static double computeNetCosts(Vm vm, Set<ApplicationEdge> es, Allocation a, FederationDatacenter f){
		double cost = 0;
		int sourceVmId = vm.getId();
		int sourceProvId = a.getAllocatedDatacenterId(vm);
		for (ApplicationEdge e: es){
			Vm targetVm  = e.getTargetVm();
			int targetProvId = a.getAllocatedDatacenterId(targetVm);
			cost += computeLinkCost(e, sourceVmId, sourceProvId, targetProvId, getCostPerBw(f));
		}
		return cost;
	}
	
	/**
	 * Given an edge, compute the cost for the network to be charged to the source Vm.
	 * @param e The Edge
	 * @param sVmId The source Vm Id
	 * @param sProvId The provider Id for the source Vm
	 * @param tProvId The provider Id for the target Vm
	 * @param price	The cost of transmitting 1 MB
	 * @return
	 */
	public static double computeLinkCost(ApplicationEdge e, int sVmId, int sProvId, int tProvId, double price){
		double cost = 0;
		if (e.getSourceVmId() == sVmId){
			if (sProvId != tProvId){
				//System.out.println("Data: "+e.getMBperHour()+ "Price: "+price);
				cost += e.getMBperHour() * price;
			}
		}
		return cost;
	}
	
	/**
	 * Compute the cost of a single VM on the given datacenter.
	 * @param vm
	 * @param type
	 * @param datacenter
	 * @return
	 */
	public static double singleVmCost(Vm vm, VmType type, FederationDatacenter datacenter)
	{
		double costs[] = datacenter.getMSCharacteristics().getCostVmTypes();
		double amount = 0d;
		
		switch (type)
		{
		case SMALL:
			amount = costs[0];
			break;
		case MEDIUM:
			amount = costs[1];
			break;
		case LARGE:
			amount = costs[2];
			break;
		case XLARGE:
			amount = costs[3];
			break;
		case CUSTOM:
			amount = calculateCostCustomVm(datacenter, vm);
		}
		
		// the provider does not have a price for this type (case of providers with mixed cost models)
		if (Double.isNaN(amount)){
			amount = calculateCostCustomVm(datacenter, vm);
		}
		
		FederationLog.timeLogDebug("(CostComputer) total vm cost: " + amount);
		return amount;
	}	

	/**
	 * Cost per MB per hour (in the DataCenter characteristics it is 
	 * expressed in GB, as usual for providers, but VMs express it 
	 * in MB)
	 * @param fd
	 * @return
	 */
	public static double getCostPerMem(FederationDatacenter fd){
		double costPerMem = fd.getMSCharacteristics().getCostPerMem();
		return costPerMem / 1024;
	}
	
	/**
	 * Cost per MB per hour
	 * @param fd
	 * @return
	 */
	public static double getCostPerStorage(FederationDatacenter fd){
		double costPerSto = fd.getMSCharacteristics().getCostPerStorage();
		return costPerSto / 1024;
	}
	
	/**
	 * Cost per MB per hour
	 * @param fd
	 * @return
	 */
	public static double getCostPerBw(FederationDatacenter fd){
		double costPerBw = fd.getMSCharacteristics().getCostPerBw();
		return costPerBw / 1024d;
	}
	
	/**
	 * Calculating cost by considering Cpu, Ram, Storage.
	 * For bandwidth we assume free charge for internal networking and thus 
	 * it cannot be included here because we do not know the entire allocation. 
	 * Please see actualCost or expectedCost for methods that include bandwidth charge.
	 * @param fd
	 * @param vm
	 * @return
	 */
	private static double calculateCostCustomVm(FederationDatacenter fd, Vm vm)
	{
		double costPerSec = fd.getMSCharacteristics().getCostPerSecond(); // used for cost per cpu
		
		double costCPU = vm.getNumberOfPes() * costPerSec;
		double costRam = vm.getRam() * getCostPerMem(fd); 
		double costStorage = vm.getSize() * getCostPerStorage(fd);
		FederationLog.timeLogDebug("(CostComputer) custom_vm: " + costRam + " + " + costStorage + "+" + costCPU);
		double costVm = costRam + costStorage + costCPU;
		return costVm;
	}
}
