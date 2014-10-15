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

package it.cnr.isti.smartfed.metascheduler;

import it.cnr.isti.smartfed.federation.application.Application;
import it.cnr.isti.smartfed.federation.resources.FederationDatacenter;
import it.cnr.isti.smartfed.metascheduler.iface.MSApplicationUtility;
import it.cnr.isti.smartfed.metascheduler.iface.MSProviderAdapter;
import it.cnr.isti.smartfed.metascheduler.resources.iface.IMSApplication;
import it.cnr.isti.smartfed.metascheduler.resources.iface.IMSProvider;
import it.cnr.isti.smartfed.networking.InternetEstimator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * This class gathers in a single point
 * the external state (i.e. providers, application and internet) 
 * useful for the metascheduler.
 * @author carlini
 *
 */
public class MSExternalState 
{
	private IMSApplication _application;
	private List<IMSProvider> _providers;
	private InternetEstimator _internet;
	
	public MSExternalState(Application application, List<FederationDatacenter> providers, InternetEstimator estimator)
	{
		this._internet = estimator;
		this._application = MSApplicationUtility.getMSApplication(application);
		
		/*
		 * Fill and sort provider list
		 */
		this._providers = new ArrayList<IMSProvider>();
		for (FederationDatacenter fd: providers)
			this._providers.add(MSProviderAdapter.datacenterToMSProvider(fd));
		
		
		// ascending sort by datacenter id
		Collections.sort(this._providers, new Comparator<IMSProvider>() {
			@Override
			public int compare(IMSProvider first, IMSProvider second) {
				if (first.getID() > second.getID()) 
					return 1; //greater
				else if (first.getID() < second.getID())
					return -1; //smaller
				return 0; // equal
			}
		});
	}

	public IMSApplication getApplication() {
		return _application;
	}

	public List<IMSProvider> getProviders() {
		return _providers;
	}

	public InternetEstimator getInternet() {
		return _internet;
	}
	
}
