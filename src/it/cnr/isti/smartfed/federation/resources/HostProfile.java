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

public class HostProfile 
{
	public enum HostParams
	{
		RAM_PROVISIONER("org.cloudbus.cloudsim.provisioners.RamProvisionerSimple"),
		RAM_AMOUNT_MB(new Integer(1024*8)+""), // 8GB
		BW_PROVISIONER("org.cloudbus.cloudsim.provisioners.BwProvisionerSimple"),
		BW_AMOUNT(10*1024*1024+""), // 10MB/sec, overall amount of bandwidth available in the host
		STORAGE_MB(new Long(10l*1024*1024)+""), // 10TB
		VM_SCHEDULER("org.cloudbus.cloudsim.VmSchedulerSpaceShared");
		
		private String def;
		
		private HostParams(String def)
		{
             this.def = def;
		}
	}
	
	
	private  Map<HostParams, String> data;
	
	private HostProfile()
	{
		data = new HashMap<HostParams, String>();
		
		for (HostParams p : HostParams.values())
		{
			data.put(p, p.def);
		}
	}

	public static HostProfile getDefault()
	{
		return new HostProfile();
	}
	
	public String get(HostParams par)
	{
		return data.get(par);
	}
	
	public void set(HostParams par, String value)
	{
		data.put(par, value);
	}
}
