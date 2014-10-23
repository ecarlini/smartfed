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

import java.util.HashMap;
import java.util.Map;

public class FederationDatacenterProfile 
{
	
	public enum DatacenterParams
	{
		ARCHITECTURE("x86"),
		OS("Linux"),
		VMM("Xen"),
		TIME_ZONE("1"), // CET (?)
		COUNTRY(Country.Italy+""),
		COST_PER_SEC("3.0"),
		COST_PER_MEM("0.005"),
		COST_PER_STORAGE("0.001"),
		COST_PER_BW("0.0"),
		VM_ALLOCATION_POLICY("org.cloudbus.cloudsim.VmAllocationPolicySimple"),
		COST_PER_VM_SMALL("0.0"),
		COST_PER_VM_MEDIUM("0.0"),
		COST_PER_VM_LARGE("0.0"),
		COST_PER_VM_XLARGE("0.0"),
		MAX_BW_FOR_VM("0"),
		SCHEDULING_INTERNAL("0");
		
		private String def;
		
		private DatacenterParams(String def)
		{
             this.def = def;
		}
	}
	
	protected  Map<DatacenterParams, String> data;
	
	private FederationDatacenterProfile()
	{
		data = new HashMap<DatacenterParams, String>();
		
		for (DatacenterParams p : DatacenterParams.values())
		{
			data.put(p, p.def);
		}
	}

	public static FederationDatacenterProfile getDefault()
	{
		return new FederationDatacenterProfile();
	}
	
	public static FederationDatacenterProfile getAmazon()
	{
		FederationDatacenterProfile prof = new FederationDatacenterProfile();
		prof.data.put(DatacenterParams.COST_PER_SEC, "0.0");
		prof.data.put(DatacenterParams.COST_PER_MEM, "0.0");
		prof.data.put(DatacenterParams.COST_PER_STORAGE, "0.0");
		prof.data.put(DatacenterParams.COST_PER_BW, "0.12"); // $ per GB (amount of transmitted data must be calculated per hour)
		prof.data.put(DatacenterParams.COST_PER_VM_SMALL, "0.085"); // $ per hour
		prof.data.put(DatacenterParams.COST_PER_VM_MEDIUM,"0.340");
		prof.data.put(DatacenterParams.COST_PER_VM_LARGE,"0.340");
		prof.data.put(DatacenterParams.COST_PER_VM_XLARGE,"0.680");
		return prof;
	}
	
	public static FederationDatacenterProfile getRackspace()
	{
		FederationDatacenterProfile prof = new FederationDatacenterProfile();
		prof.data.put(DatacenterParams.COST_PER_SEC, "0.0");
		prof.data.put(DatacenterParams.COST_PER_MEM, "0.0");
		prof.data.put(DatacenterParams.COST_PER_STORAGE, "0.0");
		prof.data.put(DatacenterParams.COST_PER_BW, "0.12");
		prof.data.put(DatacenterParams.COST_PER_VM_SMALL, "0.240");
		prof.data.put(DatacenterParams.COST_PER_VM_MEDIUM,"0.240");
		prof.data.put(DatacenterParams.COST_PER_VM_LARGE,"0.240");
		prof.data.put(DatacenterParams.COST_PER_VM_XLARGE,"0.240");
		return prof;
	}
	
	public static FederationDatacenterProfile getAruba()
	{
		FederationDatacenterProfile prof = new FederationDatacenterProfile();
		prof.data.put(DatacenterParams.COST_PER_SEC, "0.026");
		prof.data.put(DatacenterParams.COST_PER_MEM, "0.005");//0.005 euro per GB per hour
		prof.data.put(DatacenterParams.COST_PER_STORAGE, "0.00039");//0.003 euro per 10 GB per hour
		prof.data.put(DatacenterParams.COST_PER_BW, "0.12");
		prof.data.put(DatacenterParams.COST_PER_VM_SMALL, "NaN");
		prof.data.put(DatacenterParams.COST_PER_VM_MEDIUM,"NaN");
		prof.data.put(DatacenterParams.COST_PER_VM_LARGE,"NaN");
		prof.data.put(DatacenterParams.COST_PER_VM_XLARGE,"NaN");
		return prof;
	}
	
	public String get(DatacenterParams par)
	{
		return data.get(par);
	}
	
	public void set(DatacenterParams par, String value)
	{
		data.put(par, value);
	}
}
