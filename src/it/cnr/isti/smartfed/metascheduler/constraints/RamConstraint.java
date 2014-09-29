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

import org.jgap.Gene;

import it.cnr.isti.smartfed.metascheduler.Constant;
import it.cnr.isti.smartfed.metascheduler.MSPolicy;
import it.cnr.isti.smartfed.metascheduler.resources.MSApplicationNode;
import it.cnr.isti.smartfed.metascheduler.resources.iface.IMSApplication;
import it.cnr.isti.smartfed.metascheduler.resources.iface.IMSProvider;

public class RamConstraint extends MSPolicy {

	private static double highRamValue;

	public static double getHighRamValue() {
		return highRamValue;
	}

	public static void setHighRamValue(double highestValue) {
		highRamValue = highestValue;
	}

	public RamConstraint(double weight, double highestValue) {
		super(weight, MSPolicy.ASCENDENT_TYPE);
		highRamValue = highestValue;
	}

	public double evaluateLocalPolicy(Gene g, MSApplicationNode node, IMSProvider prov) {
		if (DEBUG)
			System.out.println("Eval before applying weights for " + "NodeID " + node.getID() + " - ProvID " + prov.getID());
		Integer nodeRam = (Integer) node.getComputing().getCharacteristic().get(Constant.RAM); //what I want
		Integer provRam = (Integer) prov.getComputing().getCharacteristic().get(Constant.RAM); //what I have
		double distance;
		try {
			distance = evaluateDistance(provRam, nodeRam,highRamValue);
		} catch (Exception e) {
			distance = RUNTIME_ERROR; // a positive value in order to not consider this constraint
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		if (DEBUG)
			System.out.println("\tEvaluation on ram: " + nodeRam + "-" + provRam + "/" + highRamValue + "=" + distance);
		return distance * getWeight();
	}
	
	public double evaluateGlobalPolicy(IMSApplication app, IMSProvider prov) {
		return 0;
	}

}
