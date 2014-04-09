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

import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.VmScheduler;
import org.cloudbus.cloudsim.VmSchedulerTimeSharedOverSubscription;
import org.cloudbus.cloudsim.power.PowerHostUtilizationHistory;
import org.cloudbus.cloudsim.power.models.PowerModelSpecPowerHpProLiantMl110G4Xeon3040;
import org.cloudbus.cloudsim.provisioners.BwProvisioner;
import org.cloudbus.cloudsim.provisioners.RamProvisioner;

public class HostProviderMetaScheduler 
{
	private static int ID_COUNTER = 0;
	
	private static PowerHostUtilizationHistory createHost(HostProfile profile, List<Pe> pes)
	{
		RamProvisioner ramP = null;
		BwProvisioner bwP = null;
		VmScheduler vmSched = null;
		
		try
		{
			int ram = Integer.parseInt(profile.get(HostParams.RAM_AMOUNT_MB));
			// System.out.println("ram dentro costruttore power host è " + ram);
			ramP = (RamProvisioner)Class.forName(profile.get(HostParams.RAM_PROVISIONER))
					.getDeclaredConstructor(int.class).newInstance(ram);
			
			long bw = Long.parseLong(profile.get(HostParams.BW_AMOUNT));
			bwP = (BwProvisioner)Class.forName(profile.get(HostParams.BW_PROVISIONER))
					.getDeclaredConstructor(long.class).newInstance(bw);
			
//			vmSched = (VmScheduler)Class.forName(profile.get(HostParams.VM_SCHEDULER))
//					.getDeclaredConstructor(List.class).newInstance(pes);
			vmSched = new VmSchedulerTimeSharedOverSubscription(pes);
		}
		catch (Exception e)
		{
			// TODO: log the error
			e.printStackTrace();
		}
		
		PowerHostUtilizationHistory host = new PowerHostUtilizationHistory(ID_COUNTER++, 
				ramP, bwP, 
				Long.parseLong(profile.get(HostParams.STORAGE_MB)),
				pes, 
				vmSched, new PowerModelSpecPowerHpProLiantMl110G4Xeon3040());

		return host;
	}
	
	public static PowerHostUtilizationHistory getDefault(List<Pe> pes)
	{
		return createHost(HostProfile.getDefault(), pes);
	}
	
	public static PowerHostUtilizationHistory get(HostProfile profile, List<Pe> pes)
	{
		return createHost(profile, pes);
	}
}
