package it.cnr.isti.smartfed.test;

import it.cnr.isti.smartfed.federation.application.Application;
import it.cnr.isti.smartfed.federation.resources.FederationDatacenter;
import it.cnr.isti.smartfed.networking.InternetEstimator;

import java.util.List;

public interface InterfaceDataSet {

	public List<FederationDatacenter> createDatacenters();

	public List<Application> createApplications(int userId);

	public InternetEstimator createInternetEstimator(List<FederationDatacenter> datacenters);
}

