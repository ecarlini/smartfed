package it.cnr.isti.smartfed.metascheduler.constraints;

import it.cnr.isti.smartfed.metascheduler.Constant;
import it.cnr.isti.smartfed.metascheduler.MSPolicy;
import it.cnr.isti.smartfed.metascheduler.resources.MSApplicationNode;
import it.cnr.isti.smartfed.metascheduler.resources.iface.IMSApplication;
import it.cnr.isti.smartfed.metascheduler.resources.iface.IMSProvider;

public class BudgetConstraint extends MSPolicy {

	private double highRamCost;
	private double highStorageCost;
	
	public BudgetConstraint(double weight, double highestValue) {
		super(weight, MSPolicy.DESCENDENT_TYPE, MSPolicy.LOCAL_CONSTRAINT);
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
	

	@Override
	public double evaluateGlobalPolicy(IMSApplication app, IMSProvider prov) {
		return 0;
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
	
	public static Double calculateCost(MSApplicationNode node, IMSProvider prov){
		Double cpu_cost = cpuCost(node, prov);
		Double r_cost = ramCost(node, prov);
		Double s_cost = storageCost(node, prov);
		// System.out.println(r_cost + " + " + s_cost);
		return r_cost + s_cost + cpu_cost;
	}
	
	@Override
	public double evaluateLocalPolicy(MSApplicationNode node, IMSProvider prov) {
		Double budget = (Double) node.getCharacteristic().get(Constant.BUDGET);
		Double s_maxCost = (highStorageCost * StorageConstraint.getHighStorageValue());
		Double r_maxCost = (highRamCost * RamConstraint.getHighRamValue());
		
		Double cost = ramCost(node, prov) + storageCost(node, prov);
		Double maxCost = r_maxCost + s_maxCost;
		double distance;
		try {
			// maxCost = (budget > maxCost) ? budget : maxCost; // the max value could be the budget
			maxCost = (budget );
			distance = evaluateDistance(cost, budget, maxCost);
		} catch (Exception e) {
			distance = RUNTIME_ERROR; // a positive value in order to not consider this constraint
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		if (DEBUG)
			System.out.println("\tEval on cost_per_ram " + cost + "-" + budget + "/" + maxCost + "=" + distance);
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
