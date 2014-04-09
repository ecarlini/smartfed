package it.cnr.isti.smartfed.metascheduler.resources;

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

	// testing
	private HashMap<String, Object> characteristic;

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

	@Override
	public void setID(int id) {
		this.ID = id;
	}
	
	public String toString(){
		String s = new String();
		s+= "MSApplication] id=" + ID;
		if (characteristic != null){
			Set<String> set = characteristic.keySet();

			for (String st: set){
				s+= characteristic.get(st).toString();
			}
		}
		return s;
	}

	public double getBudget() {
		return budget;
	}

	public void setBudget(double budget) {
		this.budget = budget;
	}
	
	/*disabled
	 @Override
	public void setPlace(String place) {
		// TODO Auto-generated method stub
		if (place != null && place.length() > 0)
			this.place = place;

	}

	
	@Override
	public String getPlace() {
		// TODO Auto-generated method stub
		return place;
	}
	 */

}
