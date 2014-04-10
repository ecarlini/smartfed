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
