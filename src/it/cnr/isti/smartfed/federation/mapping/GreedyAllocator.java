package it.cnr.isti.smartfed.federation.mapping;

import it.cnr.isti.smartfed.federation.CostComputer;
import it.cnr.isti.smartfed.federation.FederationLog;
import it.cnr.isti.smartfed.federation.MonitoringHub;
import it.cnr.isti.smartfed.federation.application.Application;
import it.cnr.isti.smartfed.federation.application.ApplicationVertex;
import it.cnr.isti.smartfed.federation.resources.FederationDatacenter;
import it.cnr.isti.smartfed.federation.resources.VmFactory.VmType;
import it.cnr.isti.smartfed.networking.InternetEstimator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Vm;


public class GreedyAllocator extends AbstractAllocator
{
	public GreedyAllocator()
	{
		super();
	}
	
	public GreedyAllocator(MonitoringHub monitoring, InternetEstimator netEstimator)
	{
		super();
		this.setMonitoring(monitoring);
		this.setNetEstimator(netEstimator);
	}
	
	@Override
	public MappingSolution[] findAllocation(Application application) 
	{
		
		// keeps the list of datanceters
		List<FederationDatacenter> dcs = getMonitoringHub().getView();
		
		// keeps a counter of available host per each datacenter
		Map<FederationDatacenter,Integer> countMap = new HashMap<FederationDatacenter,Integer>(dcs.size());
		for (FederationDatacenter fd: dcs)
		{
			countMap.put(fd, fd.getHostList().size());
		}
		
		this.solution = new MappingSolution(application);
				
		// fill-up the cost map (which is ordered according to cost)
		for (Vm vm: application.getAllVms())
		{
			VmType type = application.getVertexForVm(vm).getVmType();
			// TreeMap<Double, FederationDatacenter> dcToCost = new TreeMap<Double, FederationDatacenter>();
			ArrayList<CostDatacenter> costList = new ArrayList<GreedyAllocator.CostDatacenter>(dcs.size());
			
			// compute the cost for the vm for each DC
			for (FederationDatacenter fd: dcs)
			{
				double cost = CostComputer.singleVmCost(vm, type, fd);
				costList.add(new CostDatacenter(cost, fd));
			}
					
			// sort the list
			Collections.sort(costList);
			
			// keep polling until we found an acceptable solution
			boolean stillSearching = true;
			while (costList.size() > 0 && stillSearching)
			{
				// poll the first datacenter
				CostDatacenter entry = costList.remove(0);
				
				// if dc has no more empty spot, we do not consider it 
				int count = countMap.get(entry.getDc());
				if (count <= 0)
					continue;
				
				FederationDatacenter datacenter = entry.getDc();
				
				FederationLog.debugLog("GreedyAllocator -> Trying "+datacenter.getName()+" for VM: #" + vm.getId());
				
				/* Check whether the DC can support the VM
				 ******************** IMPORTANT 
				 * NOTE: this implementation works ONLY when all the hosts inside a DC
				 * are of the same TYPE
				 */
				Host host = datacenter.getHostList().get(0);
				
				String tentative = "("+costList.size()+"\\"+dcs.size()+")";
				
				if (host.getRam() < vm.getRam())
				{
					FederationLog.debugLog("GreedyAllocator -> VM: #" + vm.getId() + " cannot be mapped for RAM "+tentative+": "+vm.getRam()+ " vs "+host.getRam()+" on "+datacenter.getName());
					continue;
				}
				
				if (host.getStorage() < vm.getSize())
				{
					FederationLog.debugLog("GreedyAllocator -> VM: #" + vm.getId() + "  cannot be mapped for STORAGE "+tentative+": "+vm.getSize()+ " vs "+host.getStorage()+" on "+datacenter.getName());
					continue;
				}
				
				/*
				if (host.getNumberOfPes() < vm.getNumberOfPes())
				{
					FederationLog.debugLog("GreedyAllocator -> VM: #" + vm.getId() + "  cannot be mapped for PES "+tentative+": "+vm.getSize()+ " vs "+host.getStorage()+" on "+datacenter.getName());
					continue;
				}
				*/
				
				ApplicationVertex vertex = application.getVertexForVm(vm);
				if ((vertex.getCountry() != "") && 
					(datacenter.getMSCharacteristics().getCountry().toString().equalsIgnoreCase(vertex.getCountry()) == false))
				{
					FederationLog.debugLog("GreedyAllocator -> VM: "+vm+" cannot be mapped for LOCATION "+tentative+": "+vertex.getCountry()+ " vs "+datacenter.getMSCharacteristics().getCountry()+" on "+datacenter.getName());
					continue;
				}
				
				// if here everything went good
				stillSearching = false;
				solution.set(application.getVertexForVm(vm).getAssociatedCloudlet(vm), datacenter);
				countMap.put(entry.getDc(), --count);
				solution.setValid(true);
			}
			
			// if we are out of the cycle and still searching, this means allocator 
			// cannot find a valid mapping.
			if (stillSearching)
			{
				// TODO: we should measure/log the failure
				System.out.println("GreedyAllocator -> cannot find a mapping for VM: "+vm.getId());
				// throw new Error(application+" cannot be mapped.");
				solution.setValid(false);
				return new MappingSolution[]{solution};
			}
		}
		
		solution.setAllocatorName(this.getClass().getSimpleName());
		return new MappingSolution[]{solution};
	}
	
	
	class CostDatacenter implements Comparable<CostDatacenter>
	{
		private Double cost;
		private FederationDatacenter dc;
		
		public CostDatacenter(Double cost, FederationDatacenter dc)
		{
			this.dc = dc;
			this.cost = cost;
		}
		
			
		public Double getCost() {
			return cost;
		}

		public FederationDatacenter getDc() {
			return dc;
		}

		@Override
		public int compareTo(CostDatacenter item) 
		{
			if (this.cost == item.getCost())
				return 0;
			
			if (this.cost > item.getCost())
				return 1;
			else
				return -1;
		}

	}
}
