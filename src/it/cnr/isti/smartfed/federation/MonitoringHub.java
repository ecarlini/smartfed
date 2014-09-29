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

package it.cnr.isti.smartfed.federation;

import it.cnr.isti.smartfed.federation.resources.FederationDatacenter;

import java.util.List;

import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.SimEntity;
import org.cloudbus.cloudsim.core.SimEvent;

import com.rits.cloning.Cloner;

/**
 * @author carlini
 * This class manages the monitoring abstraction for the federation.
 * The task of the monitoring hub is to get allocation and other data from the 
 * providers of the federation. This implementation works as follows.
 * Upon reception of the MONITOR_UPDATE event, this class updates its internal view
 * by cloning the datacenter state. At the same time, it exposes as public the view
 * taken at the previous step.
 */
public class MonitoringHub extends SimEntity
{
	private List<FederationDatacenter> public_view;
	private List<FederationDatacenter> internal_view;
	private List<FederationDatacenter> dcs;
	private int schedInterval_ms; // milliseconds
	
	private boolean isShutdown = false;
	
	public MonitoringHub(List<FederationDatacenter> dcs, int schedulingInterval)
	{
		super("MonitoringHub");
		this.dcs = dcs;
		this.schedInterval_ms = schedulingInterval;
		
		// schedule the event and prepare the views
		CloudSim.send(this.getId(), this.getId(), schedInterval_ms, FederationTags.MONITOR_UPDATE, null);
		internal_view = cloneList(dcs);
		public_view = internal_view;
	}
	
	public List<FederationDatacenter> getView()
	{
		return public_view;
	}

	@Override
	public void processEvent(SimEvent event) 
	{
		if (event.getTag() == FederationTags.MONITOR_UPDATE)
		{
			// update the view
			internal_view = cloneList(dcs);
			public_view = internal_view;
			FederationLog.timeLog(this.getName()+" received MONITOR_UPDATE ("+event.getTag()+")");
			
			// reschedule the next monitoring update event
			if (this.isShutdown == false)
				CloudSim.send(this.getId(), this.getId(), schedInterval_ms, FederationTags.MONITOR_UPDATE, null);
		}
	}

	@Override
	public void shutdownEntity() 
	{
		System.out.println(this.getName() +" is shutting down...");
		this.isShutdown = true;	
	}

	@Override
	public void startEntity() {
		Log.printLine("Monitoring hub (cloudSim id "+ getId() + ") is starting ...");
	}
	
	private List<FederationDatacenter> cloneList(List<FederationDatacenter> list)
	{
		Cloner cloner = new Cloner();
		return cloner.deepClone(list);
	}
}
