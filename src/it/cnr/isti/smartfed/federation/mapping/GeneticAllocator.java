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

import it.cnr.isti.smartfed.federation.FederationLog;
import it.cnr.isti.smartfed.federation.MonitoringHub;
import it.cnr.isti.smartfed.federation.application.Application;
import it.cnr.isti.smartfed.federation.application.ApplicationVertex;
import it.cnr.isti.smartfed.federation.resources.FederationDatacenter;
import it.cnr.isti.smartfed.metascheduler.MSPolicyFactory;
import it.cnr.isti.smartfed.metascheduler.Solution;
import it.cnr.isti.smartfed.metascheduler.MSPolicyFactory.PolicyType;
import it.cnr.isti.smartfed.metascheduler.constraints.PolicyContainer;
import it.cnr.isti.smartfed.metascheduler.iface.Metascheduler;
import it.cnr.isti.smartfed.networking.InternetEstimator;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;

public class GeneticAllocator extends AbstractAllocator {
	PolicyType type = PolicyType.DEFAULT_COST;
	PolicyContainer constraint = null;
	List<FederationDatacenter> dcs = null;
	Solution[] solutions = null;

	public Solution[] getMSSolutions() {
		return solutions;
	}

	// public boolean costPerVM = false;

	public GeneticAllocator()
	{
		super();
	}

	public GeneticAllocator(MonitoringHub monitoring, InternetEstimator netEstimator) {
		super();
		this.setMonitoring(monitoring);
		this.setNetEstimator(netEstimator);
	}

	@Override
	public MappingSolution[] findAllocation(Application application) 
	{
		startSimTime = CloudSim.clock();
		startRealTime = System.currentTimeMillis();

		if (monitoring != null){
			dcs = monitoring.getView(); // maybe we can avoid to perform algorithm if the view has not changed
		}
		solutions = Metascheduler.getMapping(application, constraint.getList(), dcs, this.netEstimator, randomSeed);
		System.out.println(chooseSolution(solutions));

		finishSimTime = CloudSim.clock();
		finishRealTime = System.currentTimeMillis();

		MappingSolution[] sols = new MappingSolution[solutions.length];
		for (int i=0; i < sols.length; i++)
			sols[i] = convert(solutions[i], application, dcs);
		
		
		this.setSolution(sols[0]);
		return sols;
	}

	private Solution chooseSolution(Solution[] sols) {
		return sols[0];
	}

	/**
	 * Convert the genetic solution object "Solution" in the 
	 * federation's MappingPlan 
	 * @param s
	 * @param application
	 * @param dcs
	 * @return
	 */
	private MappingSolution convert(Solution s, Application application, List<FederationDatacenter> dcs) 
	{
		if (s == null) return null;

		FederationLog.print(s);

		// ascending sort by datacenter id
		Collections.sort(dcs, new Comparator<FederationDatacenter>() {
			@Override
			public int compare(FederationDatacenter first, FederationDatacenter second) {
				if (first.getId() > second.getId()) 
					return 1; //greater
				else if (first.getId() < second.getId())
					return -1; //smaller
				return 0; // equal
			}
		});
		
		/* DEBUG
		for (int i=0; i<dcs.size(); i++)
		{
			FederationDatacenter fd = dcs.get(i);
			System.out.println("Provider "+fd.getId()+ " position "+i);
		}
		*/

		MappingSolution map = new MappingSolution(application);
		map.setAllocatorName(this.getClass().getSimpleName());

		//the map is HashMap<vmId,dc2Id>
		HashMap<Integer,Integer> hm = s.getAllocationMap();

		List<Vm> v_list = application.getAllVms();
		if (v_list.size() != hm.keySet().size())
			System.out.println("************ Big error ********************");
		
		for (Integer vmId: hm.keySet())
		{
			Vm vm = findForId(v_list, vmId);
			ApplicationVertex vertex = application.getVertexForVm(vm);
			Cloudlet cl = vertex.getAssociatedCloudlet(vm);
			
			// FederationDatacenter dc = dcs.get(hm.get(vmId)-3); // hm.get(vmId) is the datacenter position, with -3 we have ids for metascheduler
			FederationDatacenter dc = this.findDatacenter(dcs, hm.get(vmId));
			map.set(cl, dc);
		}
		return map;
	}

	private Vm findForId(List<Vm> v_list, Integer vmId) {
		boolean found = false;
		Vm vm = null;
		for (int i = 0; i < v_list.size() && !found; i++){
			if (v_list.get(i).getId() == vmId){
				// System.out.println(v_list.get(i).getId());
				found = true;
				vm = v_list.get(i);
			}
		}
		return vm;
	}
	
	private FederationDatacenter findDatacenter(List<FederationDatacenter> list, Integer id)
	{
		for (FederationDatacenter fd: list)
		{
			if (fd.getId() == id)
				return fd;
		}
		return null;
	}

	public List<FederationDatacenter> getDcs() {
		return dcs;
	}

	private void setDcs(List<FederationDatacenter> dcs) {
		this.dcs = dcs;
	}

	public PolicyContainer getConstraint() {
		return constraint;
	}

	private void setConstraint(PolicyContainer constraint) {
		this.constraint = constraint;
	}
	
	/**
	 * Old constraints are removed. This force to recreate it 
	 * when calling setMonitoring() method. 
	 */
	public void resetConstraints() {
		this.constraint = null;
	}
	
	/**
	 * To be called before setMonitoring for having effect.
	 * @param constraint
	 */
	public void setPolicyType(PolicyType t) {
		this.type = t;
		if (this.getDcs() != null){
			this.setConstraint(MSPolicyFactory.createPolicy(dcs, type));
		}
	}

	@Override
	public void setMonitoring(MonitoringHub monitoring) 
	{
		this.monitoring = monitoring;
		this.setDcs(monitoring.getView());
		if (constraint == null){
			this.setConstraint(MSPolicyFactory.createPolicy(dcs, type));
		}
	}
}
