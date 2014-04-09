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
