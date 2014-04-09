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

package it.cnr.isti.smartfed.networking;

import it.cnr.isti.smartfed.federation.application.Application;
import it.cnr.isti.smartfed.federation.application.ApplicationEdge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class InternetLink
{
	private long bandwidth; // bps
	private int latency; //ms
	private SecuritySupport security;
	
	private Map<Application, List<ApplicationEdge>> mappings;
	
	public InternetLink(long bandwidth, int latency, SecuritySupport security)
	{
		this.bandwidth = bandwidth;
		this.latency = latency;
		this.security = security;
		
		mappings = new HashMap<Application, List<ApplicationEdge>>();
	}
	
	public boolean mapEdge(Application application, ApplicationEdge edge)
	{
		// Admission control
		if ((edge.getBandwidth() <= this.getBandwidth()) &&
				(edge.getLatency() >= this.getLatency()) &&
				(edge.getSecurity() == this.getSecurity()))
		{		
			List<ApplicationEdge> edges = mappings.get(application);
			if (edges == null)
				edges = new ArrayList<ApplicationEdge>();
			
			if (edges.contains(edge)) // do nothing
				return true;
			
			edges.add(edge);
			mappings.put(application, edges);
			return true;
			
			// TODO: im not doing any resource reduction here
		}
		else
		{
			return false;
		}
	}
	
	public void unmapEdge(Application application, ApplicationEdge edge)
	{
		List<ApplicationEdge> edges = mappings.get(application);
		if (edges != null)
			edges.remove(edge);
	}
	
	public List<ApplicationEdge> getEdges(Application application)
	{
		return mappings.get(application);
	}

	public long getBandwidth()
	{
		return bandwidth;
	}

	public void setBandwidth(long bandwidth)
	{
		this.bandwidth = bandwidth;
	}

	public int getLatency() {
		return latency;
	}

	public void setLatency(int latency)
	{
		this.latency = latency;
	}

	public SecuritySupport getSecurity()
	{
		return security;
	}

	public void setSecurity(SecuritySupport security)
	{
		this.security = security;
	}
	
	
	
}
