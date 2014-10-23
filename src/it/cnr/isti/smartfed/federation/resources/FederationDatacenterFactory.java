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

import it.cnr.isti.smartfed.federation.resources.FederationDatacenterProfile.DatacenterParams;

import java.util.List;

import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.VmAllocationPolicy;

public class FederationDatacenterFactory 
{	
	
	
	private static FederationDatacenter createFederationDatacenter(FederationDatacenterProfile profile, List<Host> hosts, List<Storage> storages) {
		return createFederationDatacenter("datacenter_"+ResourceCounter.nextDatacenterID(), profile, hosts, storages);
	}
	
	private static FederationDatacenter createFederationDatacenter(String dcName, FederationDatacenterProfile profile, List<Host> hosts, List<Storage> storages) {
		// create the datacenter characteristics
		DatacenterCharacteristicsMS dc = new DatacenterCharacteristicsMS(
				Country.valueOf(profile.get(DatacenterParams.COUNTRY)),
				profile.get(DatacenterParams.ARCHITECTURE),
				profile.get(DatacenterParams.OS),
				profile.get(DatacenterParams.VMM),
				hosts,
				Double.parseDouble(profile.get(DatacenterParams.TIME_ZONE)),
				Double.parseDouble(profile.get(DatacenterParams.COST_PER_SEC)),
				Double.parseDouble(profile.get(DatacenterParams.COST_PER_MEM)),
				Double.parseDouble(profile.get(DatacenterParams.COST_PER_STORAGE)),
				Double.parseDouble(profile.get(DatacenterParams.COST_PER_BW)),
				new double[]{Double.parseDouble(profile.get(DatacenterParams.COST_PER_VM_SMALL)),
						Double.parseDouble(profile.get(DatacenterParams.COST_PER_VM_MEDIUM)),
						Double.parseDouble(profile.get(DatacenterParams.COST_PER_VM_LARGE)),
						Double.parseDouble(profile.get(DatacenterParams.COST_PER_VM_XLARGE))}
				);

		dc.setHighestBw(Long.parseLong(profile.get(DatacenterParams.MAX_BW_FOR_VM)));
		
		// creating vm allocation policy class
		VmAllocationPolicy vmAllocationPolicy = null;
		try {
			Class clazz = Class.forName(profile.get(DatacenterParams.VM_ALLOCATION_POLICY));
			vmAllocationPolicy = (VmAllocationPolicy) clazz.getDeclaredConstructor(List.class).newInstance(
					hosts
					);
		}
		catch (Exception e) {
			// TODO: log the error
			e.printStackTrace();
		}
		
		// creating the federation datacenter
		FederationDatacenter fc = null;
		try {
			// fc = new FederationDatacenter("datacenter_"+ResourceCounter.nextDatacenterID(), dc, vmAllocationPolicy, storages, 
			fc = new FederationDatacenter(dcName, dc, vmAllocationPolicy, storages, 
					Double.parseDouble(profile.get(DatacenterParams.SCHEDULING_INTERNAL)));
		}
		catch (Exception e) {
			// TODO: log the error
			e.printStackTrace();
		}
		
		return fc;
		
	}

	public static FederationDatacenter getDefault(List<Host> hosts, List<Storage> storages){
		return createFederationDatacenter(FederationDatacenterProfile.getDefault(), hosts, storages);
	}
	
	public static FederationDatacenter get(FederationDatacenterProfile profile, List<Host> hosts, List<Storage> storages){
		return createFederationDatacenter(profile, hosts, storages);
	}
	
	public static FederationDatacenter get(String name, FederationDatacenterProfile profile, List<Host> hosts, List<Storage> storages){
		return createFederationDatacenter(name, profile, hosts, storages);
	}
}
