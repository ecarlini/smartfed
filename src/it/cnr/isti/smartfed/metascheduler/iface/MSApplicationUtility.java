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

import it.cnr.isti.smartfed.federation.application.Application;
import it.cnr.isti.smartfed.federation.application.ApplicationEdge;
import it.cnr.isti.smartfed.federation.application.ApplicationVertex;
import it.cnr.isti.smartfed.metascheduler.Constant;
import it.cnr.isti.smartfed.metascheduler.resources.MSApplication;
import it.cnr.isti.smartfed.metascheduler.resources.MSApplicationComputing;
import it.cnr.isti.smartfed.metascheduler.resources.MSApplicationNetwork;
import it.cnr.isti.smartfed.metascheduler.resources.MSApplicationNode;
import it.cnr.isti.smartfed.metascheduler.resources.MSApplicationStorage;
import it.cnr.isti.smartfed.metascheduler.resources.iface.IMSApplication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.cloudbus.cloudsim.Vm;

public class MSApplicationUtility {
	public static String hashToString(HashMap<String, Object> map, String indent){
		String ret = "";
		Iterator<String> keys = map.keySet().iterator();
		while(keys.hasNext()){
			String next  = keys.next();
			Object value = map.get(next);
			next = next.toLowerCase();
			if( value instanceof Integer )
				ret += indent + next + ":  " + (Integer) value + "\n";
			else if( value instanceof Double)
				ret += indent + next + ":  " + (Double) value + "\n";
			else if (value instanceof Long)
				ret += indent + next + ":  " + (Long) value + "\n";
			else if(next instanceof String)
				ret += indent + next + ":  " + (String) value + "\n";
		}
		return ret;
	}
	
	public static String toStringMSApplication(IMSApplication app){
		String indent = "    ";
		String ret = "";
		
		List<MSApplicationNode> nodes = app.getNodes();
		for(int i=0; i<nodes.size(); i++){
			ret += " Node." + nodes.get(i).getID() + "\n";
			ret += hashToString(nodes.get(i).getComputing().getCharacteristic(), indent);
			ret += hashToString(nodes.get(i).getNetwork().getCharacteristic(), indent);
			ret += hashToString(nodes.get(i).getStorage().getCharacteristic(), indent);
		}
		return ret;
	}
	
	
	private static MSApplicationNode vmToMSApplicationNode(Vm vm, Set<ApplicationEdge> edges, String place, double budget, char vmtype){
		MSApplicationNode appNode = new MSApplicationNode();
		
		HashMap<String, Object> compParam =  new HashMap<String, Object>();
		HashMap<String, Object> netParam = new HashMap<String, Object>();
		HashMap<String, Object> storeParam = new HashMap<String, Object>();
		
		MSApplicationComputing computing = new MSApplicationComputing();
		MSApplicationNetwork network = new MSApplicationNetwork();
		MSApplicationStorage storage = new MSApplicationStorage();
		
		compParam.put(Constant.MIPS, vm.getMips());
		compParam.put(Constant.RAM, vm.getRam());
		compParam.put(Constant.VM_TYPE, vmtype);
		compParam.put(Constant.CPU_NUMBER, vm.getNumberOfPes());
		computing.setCharacteristic(compParam);
		
		storeParam.put(Constant.STORE, vm.getSize());
		storage.setCharacteristic(storeParam);
		
		// netParam.put(Constant.BW, aggregateEdgesBW(edges)+"");
		netParam.put(Constant.BW, vm.getBw());
		network.setCharacteristic(netParam);
		
		appNode.setComputing(computing);
		appNode.setNetwork(network);
		appNode.setStorage(storage);
		
		HashMap<String, Object> nodeCharacteristic = new HashMap<String, Object>();
		nodeCharacteristic.put(Constant.BUDGET, budget);
		nodeCharacteristic.put(Constant.PLACE,place);
//		nodeCharacteristic.put(Constant.VM_INSTANCES, new Integer(1));
		appNode.setCharacteristic(nodeCharacteristic);
		appNode.setID(vm.getId());
		return appNode;
	}
	
	private static long aggregateEdgesBW(Set<ApplicationEdge> edges){
		long bw =0;
		Iterator<ApplicationEdge> edge = edges.iterator();
		while (edge.hasNext()) {
			ApplicationEdge next = edge.next();
			bw += next.getBandwidth();
		}
		return bw;
	    
	}
	
	public static IMSApplication getMSApplication(Application app){
		MSApplication newApp = new MSApplication();
		ApplicationVertex vertex ;
		List<Vm> vmList = app.getAllVms();
		
		List<MSApplicationNode> nodeList = new ArrayList<MSApplicationNode>();
		for(int i=0; i<vmList.size(); i++){
			vertex = app.getVertexForVm(vmList.get(i));
			MSApplicationNode node = vmToMSApplicationNode(vmList.get(i), app.edgesOf(vertex), vertex.getCountry(), vertex.getBudget(), vertex.getVmTypeChar());
			
			if (vertex.getDesiredVm() != null){
				Vm desVm = vertex.getDesiredVm();
				HashMap<String, Object> desiredCharacteristic = new HashMap<String, Object>();
				desiredCharacteristic.put(Constant.STORE, desVm.getSize());
				desiredCharacteristic.put(Constant.RAM, desVm.getRam());
				desiredCharacteristic.put(Constant.CPU_NUMBER, desVm.getNumberOfPes());
				node.setDesiredCharacteristics(desiredCharacteristic);
			}
			nodeList.add(node);	
		}

		newApp.setNodes(nodeList);
//		newApp.setCharacteristic(appCharacteristic);
		return newApp;
	}

}
