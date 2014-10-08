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
