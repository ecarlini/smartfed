package it.cnr.isti.smartfed.metascheduler;

import it.cnr.isti.smartfed.federation.resources.FederationDatacenter;
import it.cnr.isti.smartfed.metascheduler.MSPolicy.ConstraintScope;
import it.cnr.isti.smartfed.metascheduler.constraints.PolicyContainer;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;

public class MSPolicyFactory {

	private final static Logger log = Logger.getLogger(PolicyContainer.class.getSimpleName());
	
	public enum PolicyType{	
		DEFAULT_COST,
		DEFAULT_COST_NET,
		COST_PER_VM,
		LOCATION,
		LOCATION_NET, 
		LOCATION_NET_GLOBAL, GLOBAL_COST_BW, LOCAL_COST_BW;
	}

	public static PolicyContainer createPolicy(List<FederationDatacenter> dcList, PolicyType type){
		PolicyContainer policy = null;
		switch (type){
		case DEFAULT_COST: 
			policy = MSPolicyFactory.createPoliciesDefault(dcList, new double[]{1, 1, 1, 197}, ConstraintScope.Local);
			break;
		case DEFAULT_COST_NET: 
			policy = MSPolicyFactory.createPoliciesDefault(dcList, new double[]{ 1, 1, 1, 197}, ConstraintScope.Global);
			break;
		case GLOBAL_COST_BW: 
			policy = MSPolicyFactory.createPoliciesDefaultNetBw(dcList, new double[]{ 1, 1, 1, 196, 1}, ConstraintScope.Global);
			break;
		case LOCAL_COST_BW: 
			policy = MSPolicyFactory.createPoliciesDefaultNetBw(dcList, new double[]{ 1, 1, 1, 196, 1}, ConstraintScope.Local);
			break;
		case COST_PER_VM:
			policy = MSPolicyFactory.createPoliciesCostPerVm(dcList);
			break;
		case LOCATION_NET:
			policy = MSPolicyFactory.createPoliciesNet(dcList, ConstraintScope.Local);
			break;
		case LOCATION_NET_GLOBAL:
			policy = MSPolicyFactory.createPoliciesNet(dcList, ConstraintScope.Global);
			break;
		default:
			policy = MSPolicyFactory.createPoliciesDefault(dcList);
		}
		return policy;
	}

	public static PolicyContainer createPoliciesDefault(List<FederationDatacenter> dcList){
		return MSPolicyFactory.createPoliciesDefault(dcList, new double[]{1, 1, 1, 197}, ConstraintScope.Local);
	}

	private static PolicyContainer createPoliciesNet(List<FederationDatacenter> dcList, ConstraintScope netscope) {
		long highBw = findMaxBwAllDatacenters(dcList);
		PolicyContainer.highBwValue = highBw;
		
		System.out.println("         High Bw is " + highBw);
		PolicyContainer constraint = new PolicyContainer(new double[]{1,1});
		constraint.add(constraint.locationConstraint(1));
		constraint.add(constraint.networkConstraint(1, netscope));
		return constraint;
	}
	
	public static PolicyContainer createPoliciesDefaultNetBw(List<FederationDatacenter> dcList, double[] weights, ConstraintScope scope){
		System.out.println("*** Creating " + scope.toString() + " policy with net and bw ***");
		findCommonMaxValues(dcList);

		PolicyContainer constraint = new PolicyContainer(weights);
		constraint.add(constraint.ramConstraint(weights[0]));
		constraint.add(constraint.storageConstraint(weights[1]));
		constraint.add(constraint.locationConstraint(weights[2]));
		constraint.add(constraint.costPerResourceConstraint(weights[3], scope));
		constraint.add(constraint.networkConstraint(weights[4], scope));
		System.out.println(constraint);
		return constraint;
	}
	
	public static PolicyContainer createPoliciesDefault(List<FederationDatacenter> dcList, double[] weights, ConstraintScope scope){
		System.out.println("*** Creating default " + scope.toString() + " policy ***");
		findCommonMaxValues(dcList);

		PolicyContainer constraint = new PolicyContainer(weights);
		constraint.add(constraint.ramConstraint(weights[0]));
		constraint.add(constraint.storageConstraint(weights[1]));
		constraint.add(constraint.locationConstraint(weights[2]));
		constraint.add(constraint.costPerResourceConstraint(weights[3], scope));
		System.out.println(constraint);
		return constraint;
	}
	

	public static PolicyContainer createPoliciesCostPerVm(List<FederationDatacenter> dcList){
		double[] weights = new double[]{1,28,1};
		Collections.sort(dcList);

		double highRam_dc = MSPolicyFactory.findMaxRamAllDatacenters(dcList);
		System.out.println(highRam_dc);
		PolicyContainer.highRamValue = (int) highRam_dc;

		double high_storage_value = MSPolicyFactory.findMaxStorageAllDatacenters(dcList);
		System.out.println(high_storage_value);
		PolicyContainer.highStorageValue = (long) high_storage_value;

		PolicyContainer.highCostValueVm = 0.680;
		PolicyContainer constraint = new PolicyContainer(weights);
		constraint.add(constraint.ramConstraint(weights[0]));
		constraint.add(constraint.costVmConstraint(weights[1]));
		constraint.add(constraint.storageConstraint(weights[2]));
		return constraint;
	}

	public static long findMaxBwAllDatacenters(List<FederationDatacenter> dcList){
		FederationDatacenter max = Collections.max(dcList, new Comparator<FederationDatacenter>() {
			@Override
			public int compare(FederationDatacenter first, FederationDatacenter second) {
				if (first.getMSCharacteristics().getHighestBw() > second.getMSCharacteristics().getHighestBw())
					return 1;
				else if (first.getMSCharacteristics().getHighestBw() < second.getMSCharacteristics().getHighestBw())
					return -1;
				return 0;
			}
		});
		long highBw_dc = max.getMSCharacteristics().getHighestBw();
		return highBw_dc;
	}
	
	// finding the datacenter with the highest ram quantity 
	public static double findMaxRamAllDatacenters(List<FederationDatacenter> dcList) {
		FederationDatacenter max = Collections.max(dcList, new Comparator<FederationDatacenter>() {
			@Override
			public int compare(FederationDatacenter first, FederationDatacenter second) {
				if (first.getMSCharacteristics().getHighestRam() > second.getMSCharacteristics().getHighestRam())
					return 1;
				else if (first.getMSCharacteristics().getHighestRam() < second.getMSCharacteristics().getHighestRam())
					return -1;
				return 0;
			}
		});
		double highRam_dc = max.getMSCharacteristics().getHighestRam();
		return highRam_dc;
	}

	// finding the datacenter with the highest storage quantity 
	public static FederationDatacenter findDatacenterMaxStorage(List<FederationDatacenter> dcList) {
		FederationDatacenter max = Collections.max(dcList, new Comparator<FederationDatacenter>() {
			@Override
			public int compare(FederationDatacenter first, FederationDatacenter second) {
				if (first.getMSCharacteristics().getHighestStorage() > second.getMSCharacteristics().getHighestStorage())
					return 1;
				else if (first.getMSCharacteristics().getHighestStorage() < second.getMSCharacteristics().getHighestStorage())
					return -1;
				return 0;
			}
		});
		return max;
	}

	// finding the highest storage quantity among all datacenters
	public static double findMaxStorageAllDatacenters(List<FederationDatacenter> dcList) {
		FederationDatacenter max = findDatacenterMaxStorage(dcList);
		double high = max.getMSCharacteristics().getHighestStorage();
		return high;
	}
	
	private static void findCommonMaxValues(List<FederationDatacenter> dcList){
		// finding the datacenter with the highest cost per ram (default criteria in the compare method)
		Collections.sort(dcList);
		double highCostRam_dc = dcList.get(dcList.size()-1).getMSCharacteristics().getCostPerMem();
		PolicyContainer.highCostValueRam = highCostRam_dc;

		long highBw = findMaxBwAllDatacenters(dcList);
		PolicyContainer.highBwValue = highBw;
		
		double highRam_dc = findMaxRamAllDatacenters(dcList);
		PolicyContainer.highRamValue = (int) highRam_dc;

		double highStorage_dc = findMaxStorageAllDatacenters(dcList);
		PolicyContainer.highStorageValue = (long) highStorage_dc;
		PolicyContainer.highCostValueStorage = findDatacenterMaxStorage(dcList).getMSCharacteristics().getCostPerStorage();
	}
}
