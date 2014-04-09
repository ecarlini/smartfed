package it.cnr.isti.smartfed.metascheduler.resources.iface;

import java.util.HashMap;

import org.jgap.IApplicationData;

public interface IMSProviderNetwork extends IApplicationData{

	/*disabled
	public void setBandwidth(int bandwidth);
	public void setUnitCost(double unitCost);
	public void setPlace(String place);
	public long getBandwidth();
	public double getUnitCost();
	public String getPlace();

	*/
		
	//testing
	public void setCharacteristic(HashMap<String, Object> characteristic);
	public HashMap<String, Object> getCharacteristic();
	
	
	
}
