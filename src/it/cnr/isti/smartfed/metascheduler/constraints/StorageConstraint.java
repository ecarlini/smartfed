package it.cnr.isti.smartfed.metascheduler.constraints;

import it.cnr.isti.smartfed.metascheduler.Constant;
import it.cnr.isti.smartfed.metascheduler.MSPolicy;
import it.cnr.isti.smartfed.metascheduler.resources.MSApplicationNode;
import it.cnr.isti.smartfed.metascheduler.resources.iface.IMSApplication;
import it.cnr.isti.smartfed.metascheduler.resources.iface.IMSProvider;

public class StorageConstraint extends MSPolicy {

	private static double highStorageValue;

	public static double getHighStorageValue() {
		return highStorageValue;
	}

	public void setHighStorageValue(double highStorValue) {
		highStorageValue = highStorValue;
	}

	public StorageConstraint(double weight, double highestValue) {
		super(weight, MSPolicy.ASCENDENT_TYPE, MSPolicy.LOCAL_CONSTRAINT);
		highStorageValue = highestValue;
	}

	public double evaluateLocalPolicy(MSApplicationNode node, IMSProvider prov) {
		long nodeStore =  (Long) node.getStorage().getCharacteristic().get(Constant.STORE); // what I want
		long provStore =  (Long) prov.getStorage().getCharacteristic().get(Constant.STORE); // what I have
		double distance;
		try {
			distance = evaluateDistance(provStore, nodeStore, highStorageValue);
		} catch (Exception e) {
			distance = RUNTIME_ERROR; // a positive value in order to not consider this constraint
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		if (DEBUG)
			System.out.println("\tEval on storage " + nodeStore + "-" + provStore + "/" + highStorageValue + "=" + distance);
		return distance * getWeight();
	}
	
	public double evaluateGlobalPolicy(IMSApplication app, IMSProvider prov) {
		return 0;
	}

}
