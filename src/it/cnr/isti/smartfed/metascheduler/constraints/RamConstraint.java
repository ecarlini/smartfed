package it.cnr.isti.smartfed.metascheduler.constraints;

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
		super(weight, MSPolicy.ASCENDENT_TYPE,MSPolicy.LOCAL_CONSTRAINT);
		highRamValue = highestValue;
	}

	public double evaluateLocalPolicy(MSApplicationNode node, IMSProvider prov) {
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
