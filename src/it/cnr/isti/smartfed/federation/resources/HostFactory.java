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

import it.cnr.isti.smartfed.federation.resources.HostProfile.HostParams;

import java.util.List;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.VmScheduler;
import org.cloudbus.cloudsim.provisioners.BwProvisioner;
import org.cloudbus.cloudsim.provisioners.RamProvisioner;

public class HostFactory 
{	
	private static Host createHost(HostProfile profile, List<Pe> pes)
	{
		RamProvisioner ramP = null;
		BwProvisioner bwP = null;
		VmScheduler vmSched = null;
		
		try
		{
			int ram = Integer.parseInt(profile.get(HostParams.RAM_AMOUNT_MB));
			ramP = (RamProvisioner)Class.forName(profile.get(HostParams.RAM_PROVISIONER))
					.getDeclaredConstructor(int.class).newInstance(ram);
			
			long bw = Long.parseLong(profile.get(HostParams.BW_AMOUNT));
			bwP = (BwProvisioner)Class.forName(profile.get(HostParams.BW_PROVISIONER))
					.getDeclaredConstructor(long.class).newInstance(bw);
			
			vmSched = (VmScheduler)Class.forName(profile.get(HostParams.VM_SCHEDULER))
					.getDeclaredConstructor(List.class).newInstance(pes);
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		
		Host host = new SimpleHost(ResourceCounter.nextHostID(), 
				ramP, bwP, 
				Long.parseLong(profile.get(HostParams.STORAGE_MB)),
				pes, 
				vmSched);
		
		VmAllocationPolicySimple a;
		return host;
	}
	
	public static Host getDefault(List<Pe> pes)
	{
		return createHost(HostProfile.getDefault(), pes);
	}
	
	public static Host get(HostProfile profile, List<Pe> pes)
	{
		return createHost(profile, pes);
	}
}
