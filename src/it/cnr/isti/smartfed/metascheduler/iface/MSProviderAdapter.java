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

package it.cnr.isti.smartfed.metascheduler.iface;


import it.cnr.isti.smartfed.federation.CostComputer;
import it.cnr.isti.smartfed.federation.resources.DatacenterCharacteristicsMS;
import it.cnr.isti.smartfed.federation.resources.FederationDatacenter;
import it.cnr.isti.smartfed.metascheduler.Constant;
import it.cnr.isti.smartfed.metascheduler.resources.MSProvider;
import it.cnr.isti.smartfed.metascheduler.resources.MSProviderComputing;
import it.cnr.isti.smartfed.metascheduler.resources.MSProviderNetwork;
import it.cnr.isti.smartfed.metascheduler.resources.MSProviderStorage;
import it.cnr.isti.smartfed.metascheduler.resources.iface.IMSProvider;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.cloudbus.cloudsim.Host;


public class MSProviderAdapter {

	private static String hashToString(HashMap<String, Object> map, String indent){
		String ret = "";
		Iterator<String> keys = map.keySet().iterator();
		while (keys.hasNext()){
			String next  = keys.next();
			Object value = map.get(next);
			next = next.toLowerCase();
			if (value instanceof Integer)
				ret += indent + next + ":  " + (Integer) value + "\n";
			else if(value instanceof Double)
				ret += indent + next + ":  " + (Double) value + "\n";
			else if (value instanceof Long)
				ret += indent + next + ":  " + (Long) value + "\n";
			else if(next instanceof String)
				ret += indent + next + ":  " + (String) value + "\n";
		}
		return ret;
	}

	public static String providerListToString(List<IMSProvider> list){
		String ret = "";
		String indent = "    ";
		for(int i=0; i<list.size(); i++){
			ret += hashToString(list.get(i).getCharacteristic(), indent);
			ret += hashToString(list.get(i).getComputing().getCharacteristic(), indent);
			ret += hashToString(list.get(i).getNetwork().getCharacteristic(), indent);
			ret += hashToString(list.get(i).getStorage().getCharacteristic(), indent);
			ret +="\n";
		}
		return ret;
	}

	private static HashMap<String, Object> aggregateHostInfo(List<Host> hostList){
		//		System.out.println("### AGGREGATE INFO: DATACENTER_UTILITY");
		HashMap<String, Object> map = new HashMap<String, Object>();
		long storage =0;
		int ram =0;
		long bw =0;
		double mips =0;
		for(int i=0; i<hostList.size(); i++){
			storage += hostList.get(i).getStorage();
			ram += hostList.get(i).getRam();
			bw += hostList.get(i).getBw();
			mips += hostList.get(i).getTotalMips();
		}
		map.put(Constant.STORE, storage);
		map.put(Constant.MIPS, mips);
		map.put(Constant.RAM, ram);
		map.put(Constant.BW, bw);

		return map;
	}

	public static IMSProvider datacenterToMSProvider(FederationDatacenter datacenter){
		MSProvider provider = new MSProvider();
		HashMap<String, Object> providerCharacteristic = new HashMap<String, Object>();
		HashMap<String, Object> networkCharacteristic = new HashMap<String, Object>();
		HashMap<String, Object> computingCharacteristic = new HashMap<String, Object>();
		HashMap<String, Object> storageCharacteristic = new HashMap<String, Object>();

		List<Host> hostList = datacenter.getHostList();
		DatacenterCharacteristicsMS dcCharacter = datacenter.getMSCharacteristics();
		//aggregating host list
		HashMap<String, Object> aggregateHost = new HashMap<String, Object>(); //aggregateHostInfo(hostList);
		aggregateHost.put(Constant.STORE, hostList.get(0).getStorage());
		aggregateHost.put(Constant.MIPS, hostList.get(0).getAvailableMips());
		aggregateHost.put(Constant.RAM, hostList.get(0).getRam());
		aggregateHost.put(Constant.BW, hostList.get(0).getBw());

		//computing
		computingCharacteristic.put(Constant.RAM, aggregateHost.get(Constant.RAM));
		computingCharacteristic.put(Constant.MIPS, aggregateHost.get(Constant.MIPS));

		//network
		networkCharacteristic.put(Constant.BW, dcCharacter.getHighestBw());
		// networkCharacteristic.put(Constant.COST_BW, dcCharacterisitc.getCostPerBw());
		networkCharacteristic.put(Constant.COST_BW, CostComputer.getCostPerBw(datacenter));

		//storage
		storageCharacteristic.put(Constant.STORE, aggregateHost.get(Constant.STORE));
		storageCharacteristic.put(Constant.COST_STORAGE, CostComputer.getCostPerStorage(datacenter));

		//provider
		providerCharacteristic.put(Constant.ID, dcCharacter.getId() );
		providerCharacteristic.put(Constant.COST_SEC, dcCharacter.getCostPerSecond());
		providerCharacteristic.put(Constant.COST_MEM, CostComputer.getCostPerMem(datacenter));
		providerCharacteristic.put(Constant.PLACE, dcCharacter.getCountry());
		providerCharacteristic.put(Constant.VM_INSTANCES, hostList.size());
		providerCharacteristic.put(Constant.COST_VM, dcCharacter.getCostVmTypes());

		provider.setID(dcCharacter.getId());
		provider.setCharacteristic(providerCharacteristic);
		provider.setComputing(new MSProviderComputing());
		provider.setNetwork(new MSProviderNetwork());
		provider.setStorage(new MSProviderStorage());
		provider.getComputing().setCharacteristic(computingCharacteristic);
		provider.getStorage().setCharacteristic(storageCharacteristic);
		provider.getNetwork().setCharacteristic(networkCharacteristic);

		return provider;
	}

	public static IMSProvider findProviderById(List<IMSProvider> providerList, Integer providerID) {
		IMSProvider p = null;
		boolean found = false;
		for (int i =0; i<providerList.size() && !found; i++){
			if (providerList.get(i).getID() == providerID){
				found = true;
				p = providerList.get(i);
			}
		}
		return p;
	}

}
