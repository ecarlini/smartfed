package it.cnr.isti.smartfed.metascheduler.resources.iface;

import it.cnr.isti.smartfed.metascheduler.resources.MSApplicationNode;

import java.util.List;

import org.jgap.IApplicationData;


public interface IMSApplication extends IApplicationData {
	/* moved to application node
		public void setBudget(double budget);
		public double getBudget();
	*/
	
	/*disabled
	 public void setPlace(String place);
	 public String getPlace();
	 
	 */
	
	public void setID(int id);
	public int getID();
	
	public List<MSApplicationNode> getNodes();
	public void setNodes(List<MSApplicationNode> nodes);
}
