/*
Copyright 2013 ISTI-CNR
 
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

package it.cnr.isti.smartfed.federation.mapping;

import it.cnr.isti.smartfed.federation.MonitoringHub;
import it.cnr.isti.smartfed.federation.application.Application;
import it.cnr.isti.smartfed.federation.resources.FederationDatacenter;
import it.cnr.isti.smartfed.networking.InternetEstimator;

import java.util.HashMap;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Vm;

/**
 * This class abstracts the engine that provides an allocation
 * for an application. Each allocator must extend this class
 * in order to be used by the Federation
 * 
 * @author carlini
 *
 */
public abstract class AbstractAllocator 
{
	protected HashMap<String, String> persistentStorage;
	protected MonitoringHub monitoring;
	protected InternetEstimator netEstimator;
	protected MappingSolution solution = null;
	
	protected double startSimTime = 0d;
	protected double finishSimTime = 0d;
	protected long startRealTime = 0;
	protected long finishRealTime = 0;
	
	protected long randomSeed;
	
	/**
	 * Builds an allocator, providing the state for the mapping.
	 * @param monitoring
	 * @param netEstimator
	 */
	public AbstractAllocator()
	{
		this.persistentStorage = new HashMap<String, String>();
		randomSeed = System.currentTimeMillis();
	}
	
	public MappingSolution getSolution() 
	{
		return solution;
	}

	public void setSolution(MappingSolution solution) {
		this.solution = solution;
	}
	
	/* EMA: I'm not sure if or why this is needed.
	public HashMap<String, String> getStorage()
	{
		return persistentStorage;
	}
	*/
	
	public void setRandomSeed(long randomSeed) {
		this.randomSeed = randomSeed;
	}

	public MonitoringHub getMonitoringHub()
	{
		return monitoring;
	}
	
	public InternetEstimator getNetEstimator() 
	{
		return netEstimator;
	}
	

	public void setNetEstimator(InternetEstimator netEstimator)
	{
		this.netEstimator = netEstimator;
	}
	

	public void setMonitoring(MonitoringHub monitoring) 
	{
		this.monitoring = monitoring;
	}
	

	/**
	 * Given an application, it returns a MappingSolution.
	 * @param application
	 * @return
	 */
	public abstract MappingSolution[] findAllocation(Application application);
	
	
	/**
	 * Returns the list of the suitable host in a FedearationDatacenter
	 * of the given VM. It uses the method "isSuitableForVm" of class
	 * Host already defined in the original CloudSim.
	 */
	protected Host getSuitableHost(FederationDatacenter dc, Vm vm)
	{
		for (Host h: dc.getHostList())
		{
			if (h.isSuitableForVm(vm))
				return h;
		}
		
		return null;
	}
	
	/**
	 * Returns the duration (in simulation time) of the mapping.
	 * @return
	 */
	public double getSimDuration(){
		return finishSimTime - startSimTime;
	}
	
	/**
	 * Returns the duration (in ms) of the mapping.
	 * @return
	 */
	public long getRealDuration(){
		return finishRealTime - startRealTime;
	}
}
