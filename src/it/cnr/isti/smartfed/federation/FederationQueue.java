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

import it.cnr.isti.smartfed.federation.application.Application;

import java.util.LinkedList;

import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.SimEntity;
import org.cloudbus.cloudsim.core.SimEvent;

public class FederationQueue extends SimEntity
{
	private Federation federation;
	private Object[] applicationsAndTimestamps;
	private LinkedList<Application> applications = new LinkedList<Application>();
	private int remainingApps;
	
	public FederationQueue(Federation federation, Object[] applicationsAndTimestamps)
	{
		super("Federation_Queue");
		
		this.federation = federation;
		//this.applications = applications;
		
		this.applicationsAndTimestamps = applicationsAndTimestamps;
		
		Application[] applications = (Application[]) applicationsAndTimestamps[0];
		this.remainingApps = applications.length;
		
		// schedule the events
		this.scheduleEvents();
	}
	
	
	private void scheduleEvents()
	{
		
		long[] longs = (long[]) applicationsAndTimestamps[1];
		Application[] applications = (Application[]) applicationsAndTimestamps[0];
		
		// schedule an event for each application
		for (int i=0; i<applications.length;i++)
		{
			CloudSim.send(this.getId(), this.getId(), longs[i], FederationTags.APPLICATION_IN_QUEUE, applications[i]);
		}
	}


	@Override
	public void processEvent(SimEvent ev)
	{
		switch (ev.getTag())
		{
		case FederationTags.APPLICATION_IN_QUEUE:
			Application app = (Application) ev.getData();
			applications.add(app);
			CloudSim.send(this.getId(), federation.getId(), 0, FederationTags.APPLICATION_IN_QUEUE, applications);
			remainingApps--;
			if (remainingApps == 0)
				CloudSim.send(this.getId(), federation.getId(), 0, FederationTags.EMPTY_QUEUE, null);
			break;
		}
	}


	@Override
	public void shutdownEntity() {
		Log.printLine("FederationQueue is shutting down...");		
	}


	@Override
	public void startEntity() 
	{
		FederationLog.debugLog("FederationQueue is starting...");
	}
	
	
	
}
