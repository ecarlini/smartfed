package it.cnr.isti.smartfed.metascheduler.iface;



import it.cnr.isti.smartfed.federation.application.Application;
import it.cnr.isti.smartfed.federation.resources.FederationDatacenter;
import it.cnr.isti.smartfed.metascheduler.JGAPMapping;
import it.cnr.isti.smartfed.metascheduler.MSPolicy;
import it.cnr.isti.smartfed.metascheduler.Solution;
import it.cnr.isti.smartfed.metascheduler.resources.iface.IMSApplication;
import it.cnr.isti.smartfed.metascheduler.resources.iface.IMSProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Metascheduler {

	public static Solution[] getMapping(Application application, List<MSPolicy> policy, List<FederationDatacenter> dclist, long randomSeed){
		List<IMSProvider> providerList = new ArrayList<IMSProvider>();
		for (int i=0; i<dclist.size(); i++){
			providerList.add(MSProviderAdapter.datacenterToMSProvider(dclist.get(i)));
		}
		// ascending sort by datacenter id
		Collections.sort(providerList, new Comparator<IMSProvider>() {
			@Override
			public int compare(IMSProvider first, IMSProvider second) {
				if (first.getID() > second.getID()) 
					return 1; //greater
				else if (first.getID() < second.getID())
					return -1; //smaller
				return 0; // equal
			}
		});
		
		IMSApplication msApplication = MSApplicationUtility.getMSApplication(application);
		// System.out.println(msApplication);

		return JGAPMapping.execute(msApplication, providerList, policy, randomSeed);
	}

}
