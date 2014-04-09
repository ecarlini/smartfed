package it.cnr.isti.smartfed.metascheduler.resources.iface;

import it.cnr.isti.smartfed.metascheduler.resources.MSProviderComputing;
import it.cnr.isti.smartfed.metascheduler.resources.MSProviderNetwork;
import it.cnr.isti.smartfed.metascheduler.resources.MSProviderStorage;

import java.util.HashMap;

import org.jgap.IApplicationData;


public interface IMSProvider extends IApplicationData{

	public void setID(int id);
	public int getID();

	//testing
	public void setCharacteristic(HashMap<String, Object> characteristic);
	public HashMap<String, Object> getCharacteristic();
		
	public void setNetwork(MSProviderNetwork network);
	public void setComputing(MSProviderComputing computing);
	public void setStorage(MSProviderStorage storage);
	
	public MSProviderComputing getComputing();
	public MSProviderStorage getStorage();
	public MSProviderNetwork getNetwork();
}
