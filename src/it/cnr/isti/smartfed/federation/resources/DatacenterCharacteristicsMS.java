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

package it.cnr.isti.smartfed.federation.resources;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Host;

public class DatacenterCharacteristicsMS extends DatacenterCharacteristics {

	private String country;
	private double[] costVmTypes = null;
		
	public double[] getCostVmTypes() {
		return costVmTypes;
	}

	public DatacenterCharacteristicsMS(String place,String architecture, String os,
			String vmm, List<? extends Host> hostList, double timeZone,
			double costPerSec, double costPerMem, double costPerStorage,
			double costPerBw) {
		super(architecture, os, vmm, hostList, timeZone, costPerSec, costPerMem,
				costPerStorage, costPerBw);
		this.country = place;	
	}
	
	public DatacenterCharacteristicsMS(String place,String architecture, String os,
			String vmm, List<? extends Host> hostList, double timeZone,
			double costPerSec, double costPerMem, double costPerStorage,
			double costPerBw, double[] costPerVm) {
		super(architecture, os, vmm, hostList, timeZone, costPerSec, costPerMem,
				costPerStorage, costPerBw);
		this.country = place;	
		this.costVmTypes = costPerVm;
	}
	
	public String toString(){
		String str = new String();
		str += " Loc: " + country ;
		str += " Cost " + "(mem: " + super.getCostPerMem();
		str += " and sto: " + super.getCostPerStorage();
		str += " and bw: " + super.getCostPerBw();
		str += ") ";
		str += "Hosts: " + super.getHostList().size();
		str += " and Ram " + super.getHostList().get(0).getRam();
		str += " and Mips " + super.getHostList().get(0).getTotalMips();
		str += " and Storage " + super.getHostList().get(0).getStorage();
		if (costVmTypes != null){
			str += " with costPerVms:" ;
			for (double c: costVmTypes)
				str += "c";
		}
		return str;
	}
	
	public String getCountry(){
		return country;
	}
	
	/*
	 * Return the highest ram value among the hosts of the datacenter
	 */
	public double getHighestRam(){
		List<Host> list = super.getHostList();
		Host max = Collections.max(list, new Comparator<Host>() {
		    @Override
		    public int compare(Host first, Host second) {
		        if (first.getRam() > second.getRam())
		            return 1;
		        else if (first.getRam() < second.getRam())
		            return -1;
		        return 0;
		    }
		});
		return max.getRam();
	}

	/*
	 * Return the highest storage value among the hosts of the datacenter
	 */
	public long getHighestStorage() {
		List<Host> list = super.getHostList();
		Host max = Collections.max(list, new Comparator<Host>() {
		    @Override
		    public int compare(Host first, Host second) {
		    	// System.out.println(first.getStorage() + " " + second.getStorage());
		        if (first.getStorage() > second.getStorage())
		            return 1;
		        else if (first.getStorage() < second.getStorage())
		            return -1;
		        return 0;
		    }
		});
		// System.out.println(max.getStorage());
		return max.getStorage();
	}
	
	public long getHighestBw() {
		List<Host> list = super.getHostList();
		Host max = Collections.max(list, new Comparator<Host>() {
		    @Override
		    public int compare(Host first, Host second) {
		    	// System.out.println(first.getStorage() + " " + second.getStorage());
		        if (first.getBw() > second.getBw())
		            return 1;
		        else if (first.getBw() < second.getBw())
		            return -1;
		        return 0;
		    }
		});
		// System.out.println(max.getBw());
		return max.getBw();
	}
}
