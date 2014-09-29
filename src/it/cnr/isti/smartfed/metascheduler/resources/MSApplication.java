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

package it.cnr.isti.smartfed.metascheduler.resources;

import it.cnr.isti.smartfed.federation.application.ApplicationEdge;
import it.cnr.isti.smartfed.metascheduler.resources.iface.IMSApplication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;



public class MSApplication implements IMSApplication, Cloneable {
	
	private int ID;
	private String place;
	private double budget;

	private List<MSApplicationNode> nodes;
	
	/* for simplicity using the same edges of smartfed applications.
	 * If you want to remove this dependency (includes dependency to jgrapht)
	 * use an adapter to MSApplicationNetwork and/or include an adjacency list
	 * for graph vertices (ApplicationEdge also embeds info about source and target vertices)
	 */
	private Set<ApplicationEdge> edges;
	private int firstVmIndex;

	public MSApplication() {
		new MSApplication(-1, null, 0, null);
	}

	public MSApplication(int ID, String place, double badget,
			List<MSApplicationNode> nodes) {
		this.ID = ID;
		this.place = place;
		this.setBudget(badget);
		this.nodes = nodes;

	}

	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		/*
		 * RIVEDERE!!!!!!
		 */
		if (o == null)
			return 1;
		MSApplication app = (MSApplication) o;
		if (ID == app.ID && place.equalsIgnoreCase(app.place))
			return 0;
		return ID = app.ID;
	}

	public Object clone() {
		MSApplication app = null;
		try {
			app = (MSApplication) super.clone();
			app.nodes = new ArrayList<MSApplicationNode>();
			for( MSApplicationNode item : nodes){
				app.nodes.add((MSApplicationNode)item.clone());
			}
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return app;		
				
	}

	@Override
	public int getID() {
		return ID;
	}

	@Override
	public void setNodes(List<MSApplicationNode> nodes) {
		this.nodes = nodes;
	}

	@Override
	public List<MSApplicationNode> getNodes() {
		return nodes;
	}
	
	public void setEdges(Set<ApplicationEdge> edges) {
		this.edges = edges;
	}

	public Set<ApplicationEdge> getEdges() {
		return this.edges;
	}

	@Override
	public void setID(int id) {
		this.ID = id;
	}
	
	public String toString(){
		String s = new String();
		s+= "MSApplication] id=" + ID;
		return s;
	}

	public double getBudget() {
		return budget;
	}

	public void setBudget(double budget) {
		this.budget = budget;
	}

	public int getFirstVmIndex() {
		return firstVmIndex;
	}

	public void setFirstVmIndex(int firstVmIndex) {
		this.firstVmIndex = firstVmIndex;
	}

}
