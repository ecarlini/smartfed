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

import java.util.Set;

import org.jgap.Gene;
import org.jgap.IChromosome;

import it.cnr.isti.smartfed.federation.application.ApplicationEdge;
import it.cnr.isti.smartfed.metascheduler.Constant;
import it.cnr.isti.smartfed.metascheduler.MSPolicy;
import it.cnr.isti.smartfed.metascheduler.Monitor;
import it.cnr.isti.smartfed.metascheduler.resources.MSApplication;
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
		super(weight, MSPolicy.ASCENDENT_TYPE);
		highNetworkValue = highestValue;
	}

	public double evaluateLocalPolicy(Gene g, MSApplicationNode node, IMSProvider prov) {
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
			System.out.println("\tEval on network (node, prov)" + printMBperSec(nodeStore) + "-" + printMBperSec(provStore) 
					+ "/" + printMBperSec(highNetworkValue) + "=" + distance);
		return distance * getWeight();
	}
	
	private String printMBperSec(double val){
		String s = "";
		double res = val /1024 /1024;
		s += res + "MB/s";
		return s;
	}
	
	@Override
	public double evaluateGlobalPolicy(int gene_index, IChromosome chromos, IMSApplication app, IMSProvider prov){
		// NON SENSE CODE, SAFELY REMOVE IT
		Gene[] genes = chromos.getGenes();
		int current_prov = (int) genes[gene_index].getAllele();
		// if (DEBUG)
			System.out.println("\tMe is app " + gene_index + " on prov " + current_prov);
		MSApplication am = (MSApplication) app;
		
		int counter = 0;
		int total = 0;
		Set<ApplicationEdge> set = am.getEdges();
		for (ApplicationEdge e: set){
			if (e.getSourceVmId() == gene_index){
				int pair_prov = (int) genes[e.getTargetVmId()].getAllele();
				total ++;
				if (pair_prov == current_prov)
					counter++;
			}
		}
		System.out.println("Having " + counter + "/" + total + " pairs on same prov" );
		return 0;
	}

}
