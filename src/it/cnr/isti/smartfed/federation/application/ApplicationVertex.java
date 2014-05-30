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

package it.cnr.isti.smartfed.federation.application;

import it.cnr.isti.smartfed.federation.resources.VmFactory;
import it.cnr.isti.smartfed.federation.resources.VmFactory.VmType;
import it.cnr.isti.smartfed.federation.resources.VmTyped;
import it.cnr.isti.smartfed.federation.utils.UtilityPrint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Vm;

/**
 * This class abstracts the application vertex.
 * An application vertex can contain one or more Cloudlet, 
 * each representing an instance of a code that can be run
 * on a VM (i.e. a web server).
 * 
 * For the sake of simplicity, we consider each cloudlet running on 
 * a dedicated VM. All the VMs inside an application vertex are of the
 * same type.
 *
 * This class also contains the method to get the associated VM from
 * a cloudlet, and viceversa.
 * 
 * @author carlini, anastasi
 *
 */
public class ApplicationVertex
{
	private List<Cloudlet> cloudlets;
	private List<Vm> vms;
	private Map<Cloudlet, Vm> cloudletMap;
	private Map<Vm, Cloudlet> vmMap;
	private String name;
	private String country = "";
	private double budget = 1.0;
	private VmType vm_type;
	private Vm desiredVm = null; 
	
	public Vm getDesiredVm() {
		return desiredVm;
	}

	/**
	 * Setting desired Vm, that may be different from that one specified by VmType as available in Amazon. 
	 * Introduced for STRATOS comparison, assuming homogeneus Vms for all cloudlets.
	 * @param desiredVm
	 */
	public void setDesiredVm(Vm desiredVm) {
		this.desiredVm = desiredVm;
	}

	private void construct (int userId, List<Cloudlet> cloudlets, VmType vmtype)
	{
		this.cloudlets = cloudlets;
		this.cloudletMap = new HashMap<Cloudlet, Vm>();
		this.vmMap = new HashMap<Vm, Cloudlet>();
		this.vms = new ArrayList<Vm>();
		
		for (Cloudlet c : cloudlets)
		{
			Vm clone = VmFactory.getVm(vmtype, userId);
			VmTyped cloned = new VmTyped(clone, vmtype);
			this.vms.add(cloned);
			this.cloudletMap.put(c, cloned);
			this.vmMap.put(cloned, c);
		}
		this.vm_type = vmtype;
	}
	
	public ApplicationVertex(String name, int userId, List<Cloudlet> cloudlets, VmType vmtype)
	{
		this.name = name;
		construct(userId, cloudlets, vmtype);
	}
	
	/**
	 * Constructor for ApplicationVertex
	 * @param userId
	 * @param cloudlets
	 * @param vmtype
	 */
	public ApplicationVertex(int userId, List<Cloudlet> cloudlets, VmType vmtype)
	{
		this("", userId, cloudlets, vmtype);
	}
	
	public ApplicationVertex(int userId, List<Cloudlet> cloudlets, Vm sample)
	{
		this.vm_type = VmType.CUSTOM;
		this.cloudlets = cloudlets;
		this.cloudletMap = new HashMap<Cloudlet, Vm>();
		this.vmMap = new HashMap<Vm, Cloudlet>();
		this.vms = new ArrayList<Vm>();
		
		for (Cloudlet c : cloudlets)
		{
			Vm clone = VmFactory.cloneVMnewId(sample);
			VmTyped cloned = new VmTyped(clone, vm_type);
			this.vms.add(cloned);
			this.cloudletMap.put(c, cloned);
			this.vmMap.put(cloned, c);
		}
		
	}
	
	public String getCountry() {
		return country;
	}

	public void setCountry(String place) {
		this.country = place;
	}

	public double getBudget() {
		return budget;
	}

	public void setBudget(double budget) {
		this.budget = budget;
	}

	public void setName(String n) {
		this.name = n;
	}
	
	public String getName() {
		return this.name;
	}
	
	public List<Cloudlet> getCloudlets() {
		return this.cloudlets;
	}
	
	public List<Vm> getVms() {
		return this.vms;
	}
	
	public Vm getAssociatedVm(Cloudlet cloudlet) {
		return cloudletMap.get(cloudlet);
	}
	
	public Cloudlet getAssociatedCloudlet(Vm vm) {
		return vmMap.get(vm);
	}

	@Override
	public String toString() {
		return this.name;
	}
	
	public String toCompleteString() 
	{
		StringBuilder res = new StringBuilder();
		
		res.append("Vertex [");
		res.append("size: ").append(this.getCloudlets().size());
		res.append(" budget: ").append(this.getBudget());
		res.append(" VMs: {");
		String prefix = "";
		for (Vm vm: this.getVms())
		{
			res.append(prefix);
			prefix = ",";
			res.append("#").append(vm.getId());
		}
		res.append(" - ").append(UtilityPrint.toStringDetail(this.getVms().get(0)));
		res.append("}]");
		
		return res.toString();
	}

	public VmType getVmType() {
		return vm_type;
	}
	
	public char getVmTypeChar() {
		char c;
		switch (vm_type){
		case SMALL:
			c= 's';
			break;
		case MEDIUM:
			c='m';
			break;
		case LARGE:
			c = 'l';
			break;
		case XLARGE:
			c = 'x';
			break;
		case CUSTOM:
			c = 'c';
			break;
		default:
				c='c';
		}
		return c;
	}
}
