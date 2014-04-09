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

import java.util.HashMap;
import java.util.Map;

public class CloudletProfile 
{
	public enum CloudletParams
	{
		LENGTH("4000"), //it seems the smallest lenght for having something back
		PES_NUM("1"),
		FILE_SIZE("100"),
		OUTPUT_SIZE("100"),
		CPU_MODEL("org.cloudbus.cloudsim.UtilizationModelFull"),
		RAM_MODEL("org.cloudbus.cloudsim.UtilizationModelFull"),
		BW_MODEL("org.cloudbus.cloudsim.UtilizationModelFull");
		
		private String def;
		
		private CloudletParams(String def)
		{
             this.def = def;
		}
	}
	
	private  Map<CloudletParams, String> data;
	
	private CloudletProfile()
	{
		data = new HashMap<CloudletParams, String>();
		
		for (CloudletParams p : CloudletParams.values())
		{
			data.put(p, p.def);
		}
	}

	public static CloudletProfile getDefault()
	{
		return new CloudletProfile();
	}
	
	public String get(CloudletParams par)
	{
		return data.get(par);
	}
	
	public void set(CloudletParams par, String value)
	{
		data.put(par, value);
	}
		
}
