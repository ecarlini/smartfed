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

import it.cnr.isti.smartfed.federation.application.CloudletProfile.CloudletParams;
import it.cnr.isti.smartfed.federation.resources.ResourceCounter;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.UtilizationModel;
import org.cloudbus.cloudsim.UtilizationModelFull;

public class CloudletProvider 
{	
	private static Cloudlet createCloudlet(CloudletProfile profile)
	{
		// Instantiate utilization models
		UtilizationModel uCPU = null;
		UtilizationModel uRAM = null; 
		UtilizationModel uBW = null;
		
		try
		{
			uCPU = (UtilizationModel)Class.forName(profile.get(CloudletParams.CPU_MODEL)).newInstance();
			uRAM = (UtilizationModel)Class.forName(profile.get(CloudletParams.RAM_MODEL)).newInstance();
			uBW = (UtilizationModel)Class.forName(profile.get(CloudletParams.BW_MODEL)).newInstance();
		}
		catch (Exception e)
		{
			// TODO: log the error
			e.printStackTrace();
		}
		
		
		Cloudlet c = new Cloudlet(ResourceCounter.nextCloudletID(), 
				Integer.parseInt(profile.get(CloudletParams.LENGTH)), 
				Integer.parseInt(profile.get(CloudletParams.PES_NUM)), 
				Integer.parseInt(profile.get(CloudletParams.FILE_SIZE)),
				Integer.parseInt(profile.get(CloudletParams.OUTPUT_SIZE)),
				uCPU, uRAM, uBW);
		
		return c;
	}

	public static UtilizationModel getDefaultUtilModel(){
		CloudletProfile profile = CloudletProfile.getDefault();
		UtilizationModel uCPU = null;
		try {
			uCPU = (UtilizationModel)Class.forName(profile.get(CloudletParams.CPU_MODEL)).newInstance();
		} 
		catch (Exception e) {
			uCPU = new UtilizationModelFull();
		}
		return uCPU;
	}
	
	public static Cloudlet getDefault()
	{
		return createCloudlet(CloudletProfile.getDefault());
	}
	
	public static Cloudlet get(CloudletProfile profile)
	{
		return createCloudlet(profile);
	}
}
