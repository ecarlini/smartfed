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

package it.cnr.isti.smartfed.metascheduler.constraints;

import it.cnr.isti.smartfed.metascheduler.Constant;
import it.cnr.isti.smartfed.metascheduler.MSPolicy;
import it.cnr.isti.smartfed.metascheduler.resources.MSApplicationNode;
import it.cnr.isti.smartfed.metascheduler.resources.iface.IMSApplication;
import it.cnr.isti.smartfed.metascheduler.resources.iface.IMSProvider;

public class NetworkConstraint extends MSPolicy {

	private static double highNetworkValue;

	public static double getHighNetworkValue() {
		return highNetworkValue;
	}

	public void setHighNetworkValue(double highNetValue) {
		highNetworkValue = highNetValue;
	}

	public NetworkConstraint(double weight, double highestValue) {
		super(weight, MSPolicy.ASCENDENT_TYPE, MSPolicy.LOCAL_CONSTRAINT);
		highNetworkValue = highestValue;
	}

	public double evaluateLocalPolicy(MSApplicationNode node, IMSProvider prov) {
		// System.out.println(prov.getNetwork().getCharacteristic().get(Constant.BW));
		long nodeStore =  (Long) node.getNetwork().getCharacteristic().get(Constant.BW); // what I want
		long provStore =  (Long) prov.getNetwork().getCharacteristic().get(Constant.BW); // what I have
		double distance;
		try {
			distance = evaluateDistance(provStore, nodeStore, highNetworkValue);
		} catch (Exception e) {
			distance = RUNTIME_ERROR; // a positive value in order to not consider this constraint
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		if (DEBUG)
			System.out.println("\tEval on network (node, prov)" + nodeStore + "-" + provStore + "/" + highNetworkValue + "=" + distance);
		return distance * getWeight();
	}
	
	public double evaluateGlobalPolicy(IMSApplication app, IMSProvider prov) {
		return 0;
	}

}
