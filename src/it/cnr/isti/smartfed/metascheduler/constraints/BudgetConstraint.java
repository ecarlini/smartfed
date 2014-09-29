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

import java.util.List;
import java.util.Set;

import org.jgap.Gene;
import org.jgap.IChromosome;

import it.cnr.isti.smartfed.federation.CostComputer;
import it.cnr.isti.smartfed.federation.application.ApplicationEdge;
import it.cnr.isti.smartfed.metascheduler.Constant;
import it.cnr.isti.smartfed.metascheduler.MSPolicy;
import it.cnr.isti.smartfed.metascheduler.CIntegerGene;
import it.cnr.isti.smartfed.metascheduler.resources.MSApplication;
import it.cnr.isti.smartfed.metascheduler.resources.MSApplicationNode;
import it.cnr.isti.smartfed.metascheduler.resources.iface.IMSApplication;
import it.cnr.isti.smartfed.metascheduler.resources.iface.IMSProvider;

public class BudgetConstraint extends MSPolicy {

	private double highRamCost;
	private double highStorageCost;
	
	public BudgetConstraint(double weight, double highestValue, char c) {
		super(weight, MSPolicy.DESCENDENT_TYPE, c);
		highRamCost = highestValue;
	}
	
	/**
	 * Allows for setting highest costs for multiple resources
	 * @param weight
	 * @param highestValues
	 */
	public BudgetConstraint(double weight, double[] highestValues) {
		super(weight, MSPolicy.DESCENDENT_TYPE, MSPolicy.LOCAL_CONSTRAINT);
		highRamCost = highestValues[0];
		highStorageCost = highestValues[1];
	}
	
	private static double storageCost(MSApplicationNode node, IMSProvider prov){
		double costPerStorage = (Double) prov.getStorage().getCharacteristic().get(Constant.COST_STORAGE);
		long storage = (long) node.getStorage().getCharacteristic().get(Constant.STORE);
		Double cost = storage * costPerStorage;
		return cost;
	}
	
	private static double ramCost(MSApplicationNode node, IMSProvider prov){
		Double costPerMem = (Double) prov.getCharacteristic().get(Constant.COST_MEM);
		Integer ram = (Integer) node.getComputing().getCharacteristic().get(Constant.RAM);
		Double cost = ram * costPerMem;
		return cost;
	}
	
	private static double cpuCost(MSApplicationNode node, IMSProvider prov){
		Double costPerCPU = (Double) prov.getCharacteristic().get(Constant.COST_SEC);
		Integer cpu_number = (Integer) node.getComputing().getCharacteristic().get(Constant.CPU_NUMBER);
		Double cost = cpu_number * costPerCPU;
		return cost;
	}
	
	private static double netCost(int gene_index, IChromosome chromos, IMSApplication app, IMSProvider prov){
		Double costPerNet = (Double) prov.getNetwork().getCharacteristic().get(Constant.COST_BW);
		
		Gene[] genes = chromos.getGenes();
		int current_prov = (int) genes[gene_index].getAllele();
		MSApplicationNode curr_node = app.getNodes().get(gene_index); // this is safe
		int geneVmId = curr_node.getID();
		MSApplication am = (MSApplication) app;
		double cost = 0;
		Set<ApplicationEdge> set = am.getEdges();
		for (ApplicationEdge e: set){
			if (e.getSourceVmId() == geneVmId){
				int target_index = getChromosIndexFromNodeId(e.getTargetVmId(), genes, app);
				
				int tProvId = (int) genes[target_index].getAllele();
				cost += CostComputer.computeLinkCost(e, geneVmId, current_prov, tProvId, costPerNet);
			}
		}
		return cost;
	}
	
	private static int getChromosIndexFromNodeId(int vmId, Gene[] genes, IMSApplication app){
		int target_index = 0;
		boolean trovato = false;
		for (int i=0; i<genes.length && !trovato; i++){
			if (app.getNodes().get(i).getID() == vmId)
				target_index = i;
		}
		return target_index;
	}
	
	
	public static Double calculateCost(MSApplicationNode node, IMSProvider prov){
		Double cpu_cost = cpuCost(node, prov);
		Double r_cost = ramCost(node, prov);
		Double s_cost = storageCost(node, prov);
		// System.out.println(r_cost + " + " + s_cost);
		return r_cost + s_cost + cpu_cost;
	}
	
	private static Double calculateCost_Network(int i, IChromosome chromos, IMSApplication app, IMSProvider prov){
		MSApplicationNode node = app.getNodes().get(i);
		Double cpu_cost = cpuCost(node, prov);
		Double r_cost = ramCost(node, prov);
		Double s_cost = storageCost(node, prov);
		Double net_cost = netCost(i, chromos, app, prov);
		// System.out.println(r_cost + " + " + s_cost);
		return r_cost + s_cost + cpu_cost + net_cost;
	}
	
	@Override
	public double evaluateGlobalPolicy(int gene_index, IChromosome chromos, IMSApplication app, IMSProvider prov){
		List<MSApplicationNode> nodes = app.getNodes();
		MSApplicationNode node = nodes.get(gene_index);
		Double budget = (Double) node.getCharacteristic().get(Constant.BUDGET);
		Double cost = calculateCost_Network(gene_index, chromos, app, prov);
		
		((CIntegerGene) chromos.getGene(gene_index)).setAllocationCost(cost);
		
		Double maxCost = budget;
		double distance = calcDistanceErrHandling(cost, budget, maxCost);
		
		return distance * getWeight();
	}
	
	private double calcDistanceErrHandling(Double cost, Double budget, Double maxCost){
		double distance;
		try {
			distance = evaluateDistance(cost, budget, maxCost);
		} catch (Exception e) {
			distance = RUNTIME_ERROR; // a positive value in order to not consider this constraint
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		if (DEBUG)
			System.out.println("\tEval on budget " + cost + "-" + budget + "/" + maxCost + "=" + distance);
		return distance;
	}
	
	@Override
	public double evaluateLocalPolicy(Gene gene, MSApplicationNode node, IMSProvider prov) {
		Double budget = (Double) node.getCharacteristic().get(Constant.BUDGET);
		Double s_maxCost = (highStorageCost * StorageConstraint.getHighStorageValue());
		Double r_maxCost = (highRamCost * RamConstraint.getHighRamValue());
		
		Double cost = ramCost(node, prov) + storageCost(node, prov);
		((CIntegerGene) gene).setAllocationCost(cost);
		
		Double maxCost = r_maxCost + s_maxCost;
		// maxCost = (budget > maxCost) ? budget : maxCost; // the max value could be the budget
		maxCost = (budget);
					
		double distance = calcDistanceErrHandling(cost, budget, maxCost);
		return distance * getWeight();
	}

	public static double calculateCostDesired(MSApplicationNode node, IMSProvider prov) {
		double costPerStorage = (Double) prov.getStorage().getCharacteristic().get(Constant.COST_STORAGE);
		long storage = (long) node.getDesiredCharacteristic().get(Constant.STORE);
		Double costS = storage * costPerStorage;
	
		Double costPerMem = (Double) prov.getCharacteristic().get(Constant.COST_MEM);
		Integer ram = (Integer) node.getDesiredCharacteristic().get(Constant.RAM);
		Double costM = ram * costPerMem;

		Double costPerCPU = (Double) prov.getCharacteristic().get(Constant.COST_SEC);
		Integer cpu_number = (Integer) node.getDesiredCharacteristic().get(Constant.CPU_NUMBER);
		Double costCPU = cpu_number * costPerCPU;
		return costS + costM + costCPU ;
	}

}
