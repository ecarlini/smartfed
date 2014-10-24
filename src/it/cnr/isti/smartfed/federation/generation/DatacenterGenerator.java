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

package it.cnr.isti.smartfed.federation.generation;

import it.cnr.isti.smartfed.federation.resources.Country;
import it.cnr.isti.smartfed.federation.resources.FederationDatacenter;
import it.cnr.isti.smartfed.federation.resources.FederationDatacenterFactory;
import it.cnr.isti.smartfed.federation.resources.FederationDatacenterProfile;
import it.cnr.isti.smartfed.federation.resources.FederationDatacenterProfile.DatacenterParams;
import it.cnr.isti.smartfed.federation.resources.HostFactory;
import it.cnr.isti.smartfed.federation.resources.HostProfile;
import it.cnr.isti.smartfed.federation.resources.HostProfile.HostParams;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.math3.distribution.AbstractRealDistribution;
import org.apache.commons.math3.distribution.UniformRealDistribution;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

public class DatacenterGenerator extends AbstractGenerator
{
	// dc variables
	protected Range costPerMem;
	protected Range costPerSto;
	protected Range costPerSec;
	protected Range costPerBw;
	
	// host variables
	protected Range ramAmount;
	protected Range bwAmount;
	protected Range stoAmount;
	
	// pes variables
	protected Range coreAmount;
	protected Range mipsAmount;
	
	protected Country[] countries;
	
	public void setCountries(Country[] c){
		this.countries = c;
	}
	
	public DatacenterGenerator(long seed)
	{
		super(seed);
		
		costPerMem = new Range(0.01, 0.10);
		costPerSto = new Range(0.0002, 0.0020);
		costPerSec = new Range(0.10, 0.80); //not used, see below
		costPerBw = new Range(0.05, 0.15); //former (0.001, 0.05)
		
		ramAmount = new Range(512, 1024*16);
		bwAmount = new Range(1*1024*1024, 1024*1024*1024);
		stoAmount = new Range(4096, 10*1024*1024); // 10TB max
		coreAmount = new Range(1, 8);
		mipsAmount = new Range(1000, 25000);
		
		countries = Country.values();
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
			
			int numCore, mips, ram, bw, sto;
			double costBw, costSto, costMem;
			if (type == GenerationType.UNIFORM)
			{
				double value = distribution.sample();
				numCore = (int) coreAmount.denormalize(value);
				mips = (int) mipsAmount.denormalize(value);
				ram = (int) ramAmount.denormalize(value);
				bw = (int) bwAmount.denormalize(value);
				sto = (int) stoAmount.denormalize(value);
				
				costBw = costPerBw.denormalize(value);
				costSto = costPerSto.denormalize(value);
				costMem = costPerMem.denormalize(value);
			}
			else
			{
				numCore = (int) coreAmount.denormalize(distribution.sample());
				mips = (int) mipsAmount.denormalize(distribution.sample());
				ram = (int) ramAmount.denormalize(distribution.sample());
				bw = (int) bwAmount.denormalize(distribution.sample());
				sto = (int) stoAmount.denormalize(distribution.sample());
				
				costBw = costPerBw.denormalize(distribution.sample());
				costSto = costPerSto.denormalize(distribution.sample());
				costMem = costPerMem.denormalize(distribution.sample());
			}
			
			
			// create the datacenters
			FederationDatacenterProfile profile = FederationDatacenterProfile.getDefault();
			profile.set(DatacenterParams.COST_PER_BW, costBw+"");
			profile.set(DatacenterParams.COST_PER_STORAGE, costSto+"");
			// profile.set(DatacenterParams.COST_PER_SEC, costPerSec.sample()+"");
			profile.set(DatacenterParams.COST_PER_SEC, "0");
			profile.set(DatacenterParams.COST_PER_MEM, costMem+"");
			profile.set(DatacenterParams.MAX_BW_FOR_VM, bw+"");
			
			// choose a random country
			Range rangecountry = new Range(0, countries.length);
			int index = (int) Math.floor(rangecountry.denormalize(distribution.sample()));
			Country place = countries[index];
			profile.set(DatacenterParams.COUNTRY, place.toString());
			
			List<Storage> storageList = new ArrayList<Storage>(); // if empty, no SAN attached
			List<Host> hostList = new ArrayList<Host>();
			List<Pe> peList = new ArrayList<Pe>();// create the virtual processor (PE)
			
			for (int j=0; j<numCore; j++)
			{
				peList.add(new Pe(j, new PeProvisionerSimple(mips)));
			}
			
			// create the hosts
			HostProfile prof = HostProfile.getDefault();
			
			prof.set(HostParams.RAM_AMOUNT_MB, ram+"");
			prof.set(HostParams.BW_AMOUNT, bw+"");
			prof.set(HostParams.STORAGE_MB, sto+"");
					
			
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
