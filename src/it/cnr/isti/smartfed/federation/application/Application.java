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

package it.cnr.isti.smartfed.federation.application;


import it.cnr.isti.smartfed.federation.resources.VmTyped;
import it.cnr.isti.smartfed.federation.utils.UtilityPrint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Vm;
import org.jgrapht.graph.Multigraph;

/**
 * 
 * @author carlini
 *
 */

public class Application // extends Multigraph<ApplicationVertex, ApplicationEdge>
{
	private Multigraph<ApplicationVertex, ApplicationEdge> graph;
	private List<Cloudlet> cloudlets;
	private Map<Integer,Cloudlet> idToCloudlet;
	private Hashtable<Cloudlet, ApplicationVertex> cloudletToVertex;
	private Hashtable<Vm, ApplicationVertex> vmToVertex;

	public Application() 
	{
		graph = new Multigraph<ApplicationVertex, ApplicationEdge>(ApplicationEdge.class);
		cloudlets = new ArrayList<Cloudlet>();
		cloudletToVertex = new Hashtable<Cloudlet, ApplicationVertex>();
		vmToVertex = new Hashtable<Vm, ApplicationVertex>();
		idToCloudlet = new HashMap<Integer, Cloudlet>();
	}

	
	/*** CREATION METHODS ***/
	
	public void addVertex(ApplicationVertex av)
	{
		cloudlets.addAll(av.getCloudlets());
		
		for (Cloudlet c: av.getCloudlets())
		{
			cloudletToVertex.put(c, av);
			idToCloudlet.put(c.getCloudletId(), c);
		}
		
		for (Vm vm: av.getVms())
		{
			vmToVertex.put(vm, av);
		}
			
		graph.addVertex(av);
	}
	
	public void addEdge(ApplicationEdge ed, ApplicationVertex av1, ApplicationVertex av2)
	{
		graph.addEdge(av1, av2, ed);
	}
		
	
	/*** RETRIEVAL METHODS ***/
	
	/**
	 * Get all cloudletes directly connected with the given
	 * cloudlet.
	 * @param cloudlet
	 * @return
	 */
	public Set<Cloudlet> getAllCloudletLinked(Cloudlet cloudlet)
	{
		Set<Cloudlet> set = new HashSet<Cloudlet>();
		
		// adds all the cloudlets in the same vertex
		ApplicationVertex av = this.getVertexForCloudlet(cloudlet);
		set.addAll(av.getCloudlets());
		
		// adds the cloudlets from the connected vertex
		for (ApplicationEdge ae: graph.edgesOf(av))
		{
			ApplicationVertex source = graph.getEdgeSource(ae);
			if (source.equals(av) == false)
				set.addAll(source.getCloudlets());
			
			ApplicationVertex target = graph.getEdgeTarget(ae);
			if (target.equals(av) == false)
				set.addAll(source.getCloudlets());
		}
		
		return set;
	}
	
	/**
	 * Returns all the ApplicationEdge of the vertex.
	 * @param av1
	 * @return
	 */
	public Set<ApplicationEdge> edgesOf(ApplicationVertex av1)
	{
		return graph.edgesOf(av1); 
	}
	
	/**
	 * Returns the cloudlet with the given Id.
	 * @param id
	 * @return
	 */
	public Cloudlet getCloudletFromId(Integer id)
	{
		return idToCloudlet.get(id);
	}

	/**
	 * Returns the edge between the two vertices.
	 * @param av1
	 * @param av2
	 * @return
	 */
	public ApplicationEdge edgeBetween(ApplicationVertex av1, ApplicationVertex av2)
	{
		return graph.getEdge(av1, av2);
	}

	/**
	 * Returns the set containing all the application vertices.
	 * @return
	 */
	public Set<ApplicationVertex> vertexSet()
	{
		return graph.vertexSet();
	}
	
	/**
	 * Returns the list of all cloudlet associated
	 * with the application
	 * @return
	 */
	public List<Cloudlet> getAllCloudlets()
	{
		return cloudlets;
	}
	
	/**
	 * Returns the vertex that contains the given cloudlet.
	 * @param cloudlet
	 * @return
	 */
	public ApplicationVertex getVertexForCloudlet(Cloudlet cloudlet)
	{
		return cloudletToVertex.get(cloudlet);
	}
	
	/**
	 * Returns the vertex that contains the given VM.
	 * @param vm
	 * @return
	 */
	public ApplicationVertex getVertexForVm(Vm vm)
	{
		return vmToVertex.get(vm);
	}

	/**
	 * Returns the list of all the VMs associated
	 * with the application.
	 * @return
	 */
	// FIXME: to remove
	public List<Vm> getAllVms()
	{
		List<Vm> list = new ArrayList<Vm>();
		
		for (ApplicationVertex av: vertexSet())
		{
			list.addAll(av.getVms());
		}
		
		return list;
	}
	
	public List<VmTyped> getAllVmsTyped()
	{
		List<VmTyped> list = new ArrayList<VmTyped>();
		
		for (ApplicationVertex av: vertexSet())
		{
			List<Vm> l = av.getVms();
			for (Vm v : l){
				list.add(new VmTyped(v, av.getVmType()));
			}
		}
		
		return list;
	}
	
	public String allVMsString(){
			String str = "Application \n";
			List<Vm> vmList = this.getAllVms();
			for (Vm a : vmList){
				str += "vmID:"+ a.getId() + "\t" + this.getVertexForVm(a).getCountry()+"\n";
				str += "    Size (MB): " + a.getSize() +"\n";
				str += "    Ram (MB): "+ a.getRam() + "\n";
				str += "    Mips: " + a.getMips()+"\n";
				str += "    Net: " + a.getBw()+"\n";
				str += "    Budget: " + this.getVertexForVm(a).getBudget() + "\n";
			}
			return str;
	}
	
	@Override
	public String toString()
	{
		StringBuilder res = new StringBuilder();
		{
			String prefix = "";
			for (ApplicationVertex av: this.vertexSet())
			{
				res.append(prefix);
				prefix = "\n";
				res.append(av.toCompleteString());
			}			
		}
		
		return res.toString();
		/*
		for (ApplicationVertex v: set){
			res += "\t";
			Set<ApplicationEdge> setedge = this.edgesOf(v);
			for (ApplicationEdge e: setedge){
				res += e.toString();
				res += "[" + e.getBandwidth() + "]";
			}
			res += "\n";
		}
		return res;
		*/
	}
}
