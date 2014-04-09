package it.cnr.isti.smartfed.metascheduler.resources.iface;

import java.util.HashMap;

import org.jgap.IApplicationData;

public interface IMSProviderComputing extends IApplicationData {
	
	/*disabled
	public void setPlace(String place);
	public void setCost(double cost);
	public void setMips(double mips);
	public void setRam(int size);
	public int getRam();
	public double getMips();
	public double getCost();
	public String getPlace();
	*/
	
	public void setID(int id);
	
	//testing
	public void setCharacteristic(HashMap<String, Object> characteristic);
	public HashMap<String, Object> getCharacteristic();
	

	public int getID();
	

}
