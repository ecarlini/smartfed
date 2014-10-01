/*
Copyright 2014 ISTI-CNR
 
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

package it.cnr.isti.smartfed.federation.utils;

import it.cnr.isti.smartfed.federation.resources.FederationDatacenter;
import it.cnr.isti.smartfed.federation.resources.FederationDatacenterFactory;
import it.cnr.isti.smartfed.federation.resources.FederationDatacenterProfile;
import it.cnr.isti.smartfed.federation.resources.FederationDatacenterProfile.DatacenterParams;
import it.cnr.isti.smartfed.federation.resources.HostFactory;
import it.cnr.isti.smartfed.federation.resources.HostProfile;
import it.cnr.isti.smartfed.federation.resources.HostProfile.HostParams;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.distribution.AbstractIntegerDistribution;
import org.apache.commons.math3.distribution.AbstractRealDistribution;
import org.apache.commons.math3.distribution.UniformIntegerDistribution;
import org.apache.commons.math3.distribution.UniformRealDistribution;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;

public class DatacenterGenerator 
{
	
	// dc variables
	protected AbstractRealDistribution costPerMem;
	protected AbstractRealDistribution costPerSto;
	protected AbstractRealDistribution costPerSec;
	protected AbstractRealDistribution costPerBw;
	
	// host variables
	protected AbstractIntegerDistribution ramAmount;
	protected AbstractIntegerDistribution bwAmount;
	protected AbstractIntegerDistribution stoAmount;
	
	// pes variables
	protected AbstractIntegerDistribution coreAmount;
	protected AbstractIntegerDistribution mipsAmount;
	
	protected long seed;
	
	public DatacenterGenerator()
	{
		costPerMem = new UniformRealDistribution(0.01, 0.10);
		costPerSto = new UniformRealDistribution(0.0002, 0.0020);
		costPerSec = new UniformRealDistribution(0.10, 0.80); //not used, see below
		costPerBw = new UniformRealDistribution(0.001, 0.05);
		
		ramAmount = new UniformIntegerDistribution(512, 1024*16);
		bwAmount = new UniformIntegerDistribution(10*1024, 10*1024*1024);
		stoAmount = new UniformIntegerDistribution(4096, 10*1024*1024); // 10TB max
		coreAmount = new UniformIntegerDistribution(1, 8);
		mipsAmount = new UniformIntegerDistribution(1000, 25000);
	}
	
	public DatacenterGenerator(long seed)
	{
		this();
		resetSeed(seed);
	}
	
	public void resetSeed(long seed)
	{
		costPerMem.reseedRandomGenerator(seed);
		costPerSto.reseedRandomGenerator(seed);
		costPerSec.reseedRandomGenerator(seed);
		costPerBw.reseedRandomGenerator(seed);
		
		ramAmount.reseedRandomGenerator(seed);
		bwAmount.reseedRandomGenerator(seed);
		stoAmount.reseedRandomGenerator(seed);
		coreAmount.reseedRandomGenerator(seed);
		mipsAmount.reseedRandomGenerator(seed);
		
		this.seed = seed;
	}

	/**
	 * Generates the list of datacenters by assigning hosts to datacenters according
	 * to a uniform distribution. If a datacenter will result with 0 hosts, it will not
	 * be created.
	 * 
	 * @param numOfDatacenters
	 * @param numHost
	 * @return
	 */
	public List<FederationDatacenter> getDatacenters(int numOfDatacenters, int numHost)
	{
		UniformRealDistribution urd = new UniformRealDistribution();
		urd.reseedRandomGenerator(this.seed);
		
		return getDatacenters(numOfDatacenters, numHost, urd);
	}
	
	/**
	 * Generates the list of datacenters, and assigns the host to datacenters according
	 * the given distribution. 
	 * 
	 * Note that a distribution can very well assign zero hosts to a datacenter.
	 * However, since cloudsim does not support zero-host datacenter, we do not create 
	 * the empty datacenters.
	 * 
	 * @param approxNumberDatacenters - the approximate total number of datacenters that will be created
	 * @param numberTotalHost - the total number of host in all datacenters
	 * @param distribution
	 * @return
	 */
	public List<FederationDatacenter> getDatacenters(int approxNumberDatacenters, int numberTotalHost, AbstractRealDistribution distribution)
	{
		
		// create the list
		List<FederationDatacenter> list = new ArrayList<FederationDatacenter>(approxNumberDatacenters);
		
		// Here get the assignment vector
		int[] assign = DistributionAssignment.getAssignmentArray(approxNumberDatacenters, numberTotalHost, distribution);
		
		for (int i=0; i<approxNumberDatacenters; i++)
		{
			if (assign[i] <= 0)
				continue;
			
			// create the datacenters
			FederationDatacenterProfile profile = FederationDatacenterProfile.getDefault();
			profile.set(DatacenterParams.COST_PER_BW, costPerBw.sample()+"");
			profile.set(DatacenterParams.COST_PER_STORAGE, costPerSto.sample()+"");
			// profile.set(DatacenterParams.COST_PER_SEC, costPerSec.sample()+"");
			profile.set(DatacenterParams.COST_PER_SEC, "0");
			profile.set(DatacenterParams.COST_PER_MEM, costPerMem.sample()+"");
			
			// create the storage
			List<Storage> storageList = new ArrayList<Storage>(); // if empty, no SAN attached
			
			// create the hosts
			List<Host> hostList = new ArrayList<Host>();
			

			// create the virtual processor (PE)
			List<Pe> peList = new ArrayList<Pe>();
			int numCore = coreAmount.sample();
			int mips = mipsAmount.sample();

			for (int j=0; j<numCore; j++)
			{
				peList.add(new Pe(j, new PeProvisionerSimple(mips)));
			}
			
			// create the hosts
			HostProfile prof = HostProfile.getDefault();
			
			prof.set(HostParams.RAM_AMOUNT_MB, ramAmount.sample()+"");
			prof.set(HostParams.BW_AMOUNT, bwAmount.sample()+"");
			prof.set(HostParams.STORAGE_MB, stoAmount.sample()+"");
					
			
			for (int k=0; k<assign[i]; k++)
			{
				hostList.add(HostFactory.get(prof, peList));
			}

			// populate the list
			list.add(FederationDatacenterFactory.get(profile, hostList, storageList));		
		}
		
				
		return list;
	}


}
