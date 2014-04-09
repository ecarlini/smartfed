package it.cnr.isti.smartfed.metascheduler.resources.iface;

import java.util.HashMap;

import org.jgap.IApplicationData;

public interface IMSProviderStorage extends IApplicationData {
	
	/* disabled
	public void setAmount(int amount);
	public void setUnitCost(double unitCost);
	public void setPlace(String place);
	public int getAmount();
	public double getUnitCost();
	public String getPlace();
	
	*/
	
	
	//testing
	public void setCharacteristic(HashMap<String, Object> characteristic);
	public HashMap<String, Object> getCharacteristic();
	
	public void setID(int id);
	public int getID();
	

}
