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

package it.cnr.isti.smartfed.test;

import it.cnr.isti.smartfed.federation.resources.FederationDatacenter;
import it.cnr.isti.smartfed.federation.resources.FederationDatacenterFactory;
import it.cnr.isti.smartfed.federation.resources.FederationDatacenterProfile;
import it.cnr.isti.smartfed.federation.resources.HostFactory;
import it.cnr.isti.smartfed.federation.resources.HostProfile;
import it.cnr.isti.smartfed.federation.resources.HostProfile.HostParams;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;

public class DatacenterFacilities 
{
	private static final int minNumOfCores = 4;
	private static final int maxNumOfCores = 8;
	
	public static List<FederationDatacenter> getUniformDistribution(int numOfDatacenters, int numOfHost){
		return getUniformDistribution(numOfDatacenters, numOfHost, FederationDatacenterProfile.getDefault());
	}
	
	public static List<FederationDatacenter> getUniformDistribution(int numOfDatacenters, int numOfHost, FederationDatacenterProfile profile)
	{
		Random r =  new Random(13213);	
		int core_variance = maxNumOfCores - minNumOfCores;
		int delta_cores = core_variance > 0 ? r.nextInt(core_variance) : 0;
		
		if (numOfHost < 1)
			numOfHost = 1;
		
		List<FederationDatacenter> list = new ArrayList<FederationDatacenter>();
		
		for (int i=0; i<numOfDatacenters; i++)
		{
			// create the virtual processor (PE)
			List<Pe> peList = new ArrayList<Pe>();
			int mips = 25000;
			for (int j=0; j<minNumOfCores + delta_cores; j++)
			{
				peList.add(new Pe(j, new PeProvisionerSimple(mips)));
			}

			// create the hosts
			List<Host> hostList = new ArrayList<Host>();
			HostProfile prof = HostProfile.getDefault();
			prof.set(HostParams.RAM_AMOUNT_MB, 16*1024+"");

			for (int k=0; k<numOfHost; k++)
			{
				hostList.add(HostFactory.get(prof, peList));
			}
			
			// create the storage
			List<Storage> storageList = new ArrayList<Storage>(); // if empty, no SAN attached
			
			// create the datacenters
			list.add(FederationDatacenterFactory.get(profile, hostList, storageList));
		}
		return list;
	}
	
	public static List<FederationDatacenter> getNormalDistribution(int numOfDatacenters, int numHost)
	{
		Random r =  new Random(13213);
		
		int core_variance = maxNumOfCores - minNumOfCores;
		int delta_cores = core_variance > 0 ? r.nextInt(core_variance) : 0;
		
		List<FederationDatacenter> list = new ArrayList<FederationDatacenter>();
		NormalDistribution nd = new NormalDistribution(numOfDatacenters / 2d, numOfDatacenters / 4d);
		
		// System.out.println("Aa"+numHost);
		
		for (int i=0; i<numOfDatacenters; i++)
		{
			// create the virtual processor (PE)
			List<Pe> peList = new ArrayList<Pe>();
			int mips = 25000;
			for (int j=0; j<minNumOfCores + delta_cores; j++)
			{
				peList.add(new Pe(j, new PeProvisionerSimple(mips)));
			}
			

			// create the hosts
			List<Host> hostList = new ArrayList<Host>();
			HostProfile prof = HostProfile.getDefault();
			prof.set(HostParams.RAM_AMOUNT_MB, 16*1024+"");

			int num;
			if (numOfDatacenters == 1)
			{
				num = numHost;
			}
			else
			{
				Double value = new Double(nd.density(i)) * numHost;
				num = value.intValue();
			}
			
			if (num < 1)
				num = 1;

			for (int k=0; k<num; k++)
			{
				hostList.add(HostFactory.get(prof, peList));
			}
			
			// create the storage
			List<Storage> storageList = new ArrayList<Storage>(); // if empty, no SAN attached
			
			// create the datacenters
			list.add(FederationDatacenterFactory.getDefault(hostList, storageList));
		}
		
				
		return list;
	}
	
}
