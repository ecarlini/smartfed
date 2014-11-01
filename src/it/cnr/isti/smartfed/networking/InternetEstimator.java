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

import it.cnr.isti.smartfed.federation.resources.FederationDatacenter;

import java.util.List;

import org.cloudbus.cloudsim.core.CloudSim;
import org.jgrapht.graph.Multigraph;

/**
 * This class encloses methods for the generation of a "provider networks" that
 * would model the state and the nature of the links among different federation
 * providers. 
 * The information contained by this class are static, in the sense that the
 * class is not supposed to track what applications are currently running on 
 * providers. For example, if the bandwidth available among two provides is
 * 100KB and then an application requiring 10KB is deployed, this class would 
 * still return 100KB as link capacity.
 * 
 * @author carlini
 *
 */

public class InternetEstimator
{
	private Multigraph<FederationDatacenter, InternetLink> graph;
	
	public InternetEstimator(List<FederationDatacenter> list)
	{
		graph = new Multigraph<FederationDatacenter, InternetLink>(InternetLink.class);
		
		// populate the vertexes
		for (FederationDatacenter d: list)
		{
			graph.addVertex(d);
		}
		
		// populate the edges
		for (FederationDatacenter outer: list)
		{
			for (FederationDatacenter inner: list)
			{
				// a self edges will exits, even if probably will be never used
				if (outer.getId() == inner.getId())
				{
					// DO NOTHING!
					// InternetLink il = new InternetLink(Long.MAX_VALUE, 0, SecuritySupport.ADVANCED);
					// graph.addEdge(outer, inner, il);
				}
				else // regular edge
				{
					InternetLink il = new InternetLink(1024*1024*10, 0.1, SecuritySupport.ADVANCED);
					graph.addEdge(outer, inner, il);
				}
			}
		}
	}
	
	/**
	 * Return the InternetLink between two datacenters. 
	 * @param a
	 * @param b
	 * @return
	 * @throws Exception 
	 */
	public InternetLink getInternetLink(FederationDatacenter a, FederationDatacenter b) throws Exception
	{
		if ((a == null) || (graph.containsVertex(a) == false))
			throw new Exception("Vertex not found or null: "+a);
		
		if ((b == null) || (graph.containsVertex(b) == false))
			throw new Exception("Vertex not found or null: "+b);
		
		return graph.getEdge(a, b);
	}
	
	/**
	 * Return the InternetLink between the two datacenters
	 * with the parameter ids.
	 * @param id_a
	 * @param id_b
	 * @return
	 * @throws Exception 
	 */
	public InternetLink getInternetLink(Integer id_a, Integer id_b) throws Exception
	{
		FederationDatacenter a = (FederationDatacenter) CloudSim.getEntity(id_a);
		FederationDatacenter b = (FederationDatacenter) CloudSim.getEntity(id_b);
		
		return this.getInternetLink(a, b);
	}
	
	/**
	 * Return the highest value for latency among all the links 
	 * @return
	 */
	public double getHighestLatency()
	{
		double max = 0;
		for (InternetLink link: graph.edgeSet())
		{
			if (link.getLatency() > max)
				max = link.getLatency();
		}
		
		return max;
	}
}
