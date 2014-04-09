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
import it.cnr.isti.smartfed.federation.resources.FederationDatacenter;

import java.util.Arrays;
import java.util.HashMap;


public class InternetEstimator
{
	private InternetLink[][] links = null;
	private HashMap<Long, InternetLink[][]> sessions = null;
	private HashMap<Integer, Integer> datacenterIdTranslation = null;
	
	private long sessionId = 0;
	private int datacenterTraslationId = -1;
	
	
	public InternetEstimator(int datacenters)
	{
		links = new InternetLink[datacenters][datacenters];
		sessions = new HashMap<Long, InternetLink[][]>();
		datacenterIdTranslation = new HashMap<Integer, Integer>();
		
	}
	
	public void defineDirectLink(FederationDatacenter source, FederationDatacenter dest, 
			long bandwidth, int latency, SecuritySupport security)
	{
		
		int sourceDc = translate(source);
		int destDc = translate(dest);
		
		links[sourceDc][destDc] = new InternetLink(bandwidth, latency, security);
	}
	
	public void defineUndirectLink(FederationDatacenter source, FederationDatacenter dest, 
			long bandwidth, int latency, SecuritySupport security)
	{
		
		int sourceDc = translate(source);
		int destDc = translate(dest);
		
		links[sourceDc][destDc] = new InternetLink(bandwidth, latency, security);
		links[destDc][sourceDc] = new InternetLink(bandwidth, latency, security);
	}
	
	public long createSession()
	{	
		sessionId++;
		
		InternetLink[][] session = this.cloneLinks();		
		sessions.put(sessionId, session);
		
		return sessionId;
	}
	
	public void disposeAllocationSession(long id) throws NullPointerException 
	{
		Object[] retValue = sessions.remove(id);
		if(retValue == null) 
			throw new NullPointerException("No Such session with given ID");
	}

	
	public boolean allocateLink (long sessionId, FederationDatacenter source, FederationDatacenter dest, 
			ApplicationEdge edge, Application application)
	{	
		InternetLink[][] session =  sessions.get(sessionId);
		
		int sourceDc = translate(source);
		int destDc = translate(dest);
		
		return session[sourceDc][destDc].mapEdge(application, edge);
	}
	
	public boolean allocateLink (FederationDatacenter source, FederationDatacenter dest, ApplicationEdge edge,
			Application application)
	{
		
		int sourceDc = translate(source);
		int destDc = translate(dest);
		
		return links[sourceDc][destDc].mapEdge(application, edge);
		
	}

	public void deallocateLink (long sessionId, FederationDatacenter source, FederationDatacenter dest, 
			ApplicationEdge edge, Application application)
	{
	
		InternetLink[][] session =  sessions.get(sessionId);
		
		int sourceDc = translate(source);
		int destDc = translate(dest);
		
		session[sourceDc][destDc].unmapEdge(application, edge);
	}
	
	public void deallocateLink (FederationDatacenter source, FederationDatacenter dest, ApplicationEdge edge,
			Application application)
	{
		
		int sourceDc = translate(source);
		int destDc = translate(dest);
		
		links[sourceDc][destDc].unmapEdge(application, edge);	
	}
	
	/** Current Assumption: the original bandwidth matrix does not changes during the allocation procedure */
	public void consolidateAllocationSession(long id)
	{
		InternetLink[][] session =  sessions.get(sessionId);
		links = session;
	}
	
	private InternetLink[][] cloneLinks()
	{
		InternetLink[][] clone = new InternetLink[links.length][];
			
		for (int i=0; i<links.length; i++)
		{
			clone[i] = Arrays.copyOf(links[i], links[i].length);
		}
		
		return clone;
	}
	
	private int translate(FederationDatacenter source)
	{	
		if(datacenterIdTranslation.containsKey(source.getId())) 
			return datacenterIdTranslation.get(source.getId());
		else{
			datacenterTraslationId++;
			datacenterIdTranslation.put(source.getId(), datacenterTraslationId);
			return datacenterTraslationId;
		}
	}

}
