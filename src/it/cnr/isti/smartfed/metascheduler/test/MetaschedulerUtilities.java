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

package it.cnr.isti.smartfed.metascheduler.test;


import it.cnr.isti.smartfed.federation.Federation;
import it.cnr.isti.smartfed.federation.resources.FederationDatacenter;
import it.cnr.isti.smartfed.metascheduler.constraints.PolicyContainer;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class MetaschedulerUtilities {

	public enum PolicyType{	
		DEFAULT_COST,
		DEFAULT_COST_NET,
		COST_PER_VM,
		LOCATION,
		LOCATION_NET, 
		DEFAULT_COST_EQUAL_RIGHTS;
	}

	public static PolicyContainer createPolicy(List<FederationDatacenter> dcList, PolicyType type){
		PolicyContainer policy = null;
		switch (type){
		case DEFAULT_COST: 
			policy = MetaschedulerUtilities.createPoliciesDefault(dcList);
			break;
		case DEFAULT_COST_EQUAL_RIGHTS: 
			policy = MetaschedulerUtilities.createPoliciesDefault(dcList, new double[]{5,5,95,95});
			break;
		case DEFAULT_COST_NET: 
			policy = MetaschedulerUtilities.createPoliciesDefaultNet(dcList);
			break;
		case COST_PER_VM:
			policy = MetaschedulerUtilities.createPoliciesCostPerVm(dcList);
			break;
		case LOCATION:
			policy = MetaschedulerUtilities.createPoliciesDefault(dcList);
			break;
		case LOCATION_NET:
			policy = MetaschedulerUtilities.createPoliciesNet(dcList);
			break;
		default:
			policy = MetaschedulerUtilities.createPoliciesDefault(dcList);
		}
		return policy;
	}

	private static PolicyContainer createPoliciesNet(List<FederationDatacenter> dcList) {
		long highBw = findMaxBwAllDatacenters(dcList);
		PolicyContainer.highBwValue = highBw;
		
		System.out.println("         " + highBw);
		PolicyContainer constraint = new PolicyContainer(new double[]{1,1});
		constraint.add(constraint.locationConstraint(1));
		constraint.add(constraint.networkConstraint(1));
		return constraint;
	}

	public static PolicyContainer createPoliciesDefaultNet(List<FederationDatacenter> dcList ){
		System.out.println("*** Creating default policy with net***");
		// finding the datacenter with the highest cost per ram (default criteria in the compare method)
		Collections.sort(dcList);
		double highCostRam_dc = dcList.get(dcList.size()-1).getMSCharacteristics().getCostPerMem();
		PolicyContainer.highCostValueRam = highCostRam_dc;

		long highBw = findMaxBwAllDatacenters(dcList);
		PolicyContainer.highBwValue = highBw;
		
		double highRam_dc = findMaxRamAllDatacenters(dcList);
		PolicyContainer.highRamValue = (int) highRam_dc;

		double highStorage_dc = findMaxStorageAllDatacenters(dcList);
		PolicyContainer.highStorageValue = (long) highStorage_dc;
		PolicyContainer.highCostValueStorage = findDatacenterMaxStorage(dcList).getMSCharacteristics().getCostPerStorage();

		PolicyContainer constraint = new PolicyContainer(new double[]{1, 1, 1, 1, 1});
		constraint.add(constraint.networkConstraint(1));
		constraint.add(constraint.ramConstraint(1));
		constraint.add(constraint.storageConstraint(1));
		constraint.add(constraint.locationConstraint(1));
		constraint.add(constraint.costRamStorageConstraint(1));
		System.out.println(constraint);
		return constraint;
	}
	
	public static PolicyContainer createPoliciesDefault(List<FederationDatacenter> dcList, double[] weights){
		System.out.println("*** Creating default policy ***");
		// finding the datacenter with the highest cost per ram (default criteria in the compare method)
		Collections.sort(dcList);
		double highCostRam_dc = dcList.get(dcList.size()-1).getMSCharacteristics().getCostPerMem();
		PolicyContainer.highCostValueRam = highCostRam_dc;


		double highRam_dc = findMaxRamAllDatacenters(dcList);
		PolicyContainer.highRamValue = (int) highRam_dc;

		double highStorage_dc = findMaxStorageAllDatacenters(dcList);
		PolicyContainer.highStorageValue = (long) highStorage_dc;
		PolicyContainer.highCostValueStorage = findDatacenterMaxStorage(dcList).getMSCharacteristics().getCostPerStorage();

		PolicyContainer constraint = new PolicyContainer(weights);
		constraint.add(constraint.ramConstraint(weights[0]));
		constraint.add(constraint.storageConstraint(weights[1]));
		constraint.add(constraint.locationConstraint(weights[2]));
		constraint.add(constraint.costRamStorageConstraint(weights[3]));
		System.out.println(constraint);
		return constraint;
	}
	
	public static PolicyContainer createPoliciesDefault(List<FederationDatacenter> dcList){
		return createPoliciesDefault(dcList, new double[]{1, 1, 1, 197});
	}

	public static PolicyContainer createPoliciesCostPerVm(List<FederationDatacenter> dcList){
		Collections.sort(dcList);

		double highRam_dc = MetaschedulerUtilities.findMaxRamAllDatacenters(dcList);
		System.out.println(highRam_dc);
		PolicyContainer.highRamValue = (int) highRam_dc;

		double high_storage_value = MetaschedulerUtilities.findMaxStorageAllDatacenters(dcList);
		System.out.println(high_storage_value);
		PolicyContainer.highStorageValue = (long) high_storage_value;

		PolicyContainer.highCostValueVm = 0.680;
		PolicyContainer constraint = new PolicyContainer(new double[]{1,28,1});
		constraint.add(constraint.ramConstraint(1));
		// constraint.add(PolicyContainer.costRamStorageConstraint(1));
		constraint.add(constraint.costVmConstraint(28));
		constraint.add(constraint.storageConstraint(1));
		return constraint;
	}

	public static long findMaxBwAllDatacenters(List<FederationDatacenter> dcList){
		FederationDatacenter max = Collections.max(dcList, new Comparator<FederationDatacenter>() {
			@Override
			public int compare(FederationDatacenter first, FederationDatacenter second) {
				if (first.getMSCharacteristics().getHighestBw() > second.getMSCharacteristics().getHighestBw())
					return 1;
				else if (first.getMSCharacteristics().getHighestBw() < second.getMSCharacteristics().getHighestBw())
					return -1;
				return 0;
			}
		});
		long highBw_dc = max.getMSCharacteristics().getHighestBw();
		return highBw_dc;
	}
	
	// finding the datacenter with the highest ram quantity 
	public static double findMaxRamAllDatacenters(List<FederationDatacenter> dcList) {
		FederationDatacenter max = Collections.max(dcList, new Comparator<FederationDatacenter>() {
			@Override
			public int compare(FederationDatacenter first, FederationDatacenter second) {
				if (first.getMSCharacteristics().getHighestRam() > second.getMSCharacteristics().getHighestRam())
					return 1;
				else if (first.getMSCharacteristics().getHighestRam() < second.getMSCharacteristics().getHighestRam())
					return -1;
				return 0;
			}
		});
		double highRam_dc = max.getMSCharacteristics().getHighestRam();
		return highRam_dc;
	}

	// finding the datacenter with the highest storage quantity 
	public static FederationDatacenter findDatacenterMaxStorage(List<FederationDatacenter> dcList) {
		FederationDatacenter max = Collections.max(dcList, new Comparator<FederationDatacenter>() {
			@Override
			public int compare(FederationDatacenter first, FederationDatacenter second) {
				if (first.getMSCharacteristics().getHighestStorage() > second.getMSCharacteristics().getHighestStorage())
					return 1;
				else if (first.getMSCharacteristics().getHighestStorage() < second.getMSCharacteristics().getHighestStorage())
					return -1;
				return 0;
			}
		});
		return max;
	}

	// finding the highest storage quantity among all datacenters
	public static double findMaxStorageAllDatacenters(List<FederationDatacenter> dcList) {
		FederationDatacenter max = findDatacenterMaxStorage(dcList);
		double high = max.getMSCharacteristics().getHighestStorage();
		return high;
	}

	protected static void printCloudSimResults(List<FederationDatacenter> dcList, Federation fed){
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ RESULTS ~~~~~~~~~~~~~~~~~~~~~~~~~~~ " + fed.getVmToDatacenter().size() );

		System.out.println("FederationSim Results");
		HashMap<Integer, Integer> allocatedToDatacenter = fed.getVmToDatacenter();
		Iterator<Integer> keys = allocatedToDatacenter.keySet().iterator();
		while (keys.hasNext()) {
			Integer i = keys.next();
			System.out.println("VM #" + i + " Allocated in datacenter #" + allocatedToDatacenter.get(i));
		}
	}


	public static void saveFederationToFile(String path, List<FederationDatacenter> fed ){
		try{
			FileOutputStream fout = new FileOutputStream(path);
			ObjectOutputStream oos = new ObjectOutputStream(fout);
			oos.writeObject(fed);
			oos.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}


	public static List<FederationDatacenter> readFederationFromFile(String path){
		List<FederationDatacenter> dclist = null;
		try{
			FileInputStream fin = new FileInputStream(path);
			ObjectInputStream ois = new ObjectInputStream(fin);
			dclist = (List<FederationDatacenter>)ois.readObject();
			ois.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		return dclist;
	}


}
