package it.cnr.isti.smartfed.metascheduler.constraints;

import it.cnr.isti.smartfed.metascheduler.Constant;
import it.cnr.isti.smartfed.metascheduler.MSPolicy;
import it.cnr.isti.smartfed.metascheduler.resources.MSApplicationNode;
import it.cnr.isti.smartfed.metascheduler.resources.iface.IMSApplication;
import it.cnr.isti.smartfed.metascheduler.resources.iface.IMSProvider;

public class CountryConstraint extends MSPolicy{

	
	public CountryConstraint(double weight) {
		super(weight, MSPolicy.EQUAL_TYPE, MSPolicy.LOCAL_CONSTRAINT);
	}

	@Override
	public double evaluateLocalPolicy(MSApplicationNode node, IMSProvider prov) {
		String nodePlace = (String) node.getCharacteristic().get(Constant.PLACE); //what I want
		String provPlace = (String)prov.getCharacteristic().get(Constant.PLACE); //what I have
		
		nodePlace = nodePlace.toLowerCase().trim();
		provPlace = provPlace.toLowerCase().trim();
		
		double distance = 0;
		String[] places = provPlace.split(",");
		if (places.length > 1){
			distance = calculateDistance(nodePlace, places[0]);
			for (int i=1; i<places.length; i++){
				double tmp = calculateDistance(nodePlace, places[i]);
				distance = tmp < distance ? tmp : distance;
			}
		}
		else {
			distance = calculateDistance(nodePlace, provPlace);
		}
		
		if (DEBUG)
			System.out.println("\tEvaluation on country: " + nodePlace + " vs " + provPlace + "=" + distance);
		return distance * getWeight();
	}
	
	private double calculateDistance(String nodePlace, String provPlace){
		double distance;
		try {
			distance = evaluateDistance(nodePlace, provPlace);
		} catch (Exception e) {
			distance = MSPolicy.RUNTIME_ERROR; // a positive value in order to not consider this constraint
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		return distance;
	}
	
	@Override
	public double evaluateGlobalPolicy(IMSApplication app, IMSProvider prov) {
		return 0;
	}

}
