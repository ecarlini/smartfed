package it.cnr.isti.smartfed.metascheduler.resources;

import it.cnr.isti.smartfed.metascheduler.resources.iface.IMSAppComputing;

import java.util.HashMap;


public class MSApplicationComputing implements IMSAppComputing, Cloneable{
	private int ID;
	
	//testing
	private HashMap<String, Object> characteristic;
	
	public MSApplicationComputing(){
		new MSApplicationComputing(-1, new HashMap<String, Object>());
	}
	
	public MSApplicationComputing( int ID,HashMap<String, Object> characteristic ){
		this.ID = ID;
		this.characteristic = characteristic;		
	}
	
	@Override
	public int compareTo(Object o) {
		if (o == null)
			return 1;
		MSApplicationComputing appc = (MSApplicationComputing) o;
		return ID - appc.ID;
	}

	@Override
	public void setID(int ID) {
		this.ID = ID;
	}

	@Override
	public int getID() {
		return ID;
	}
	
	public Object clone(){
		MSApplicationComputing appC = null;
		try {
			appC = (MSApplicationComputing) super.clone();
			appC.characteristic = (HashMap<String, Object>) this.characteristic.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return appC ;
	}
	
	@Override
	public void setCharacteristic(HashMap<String, Object> characteristic) {
		this.characteristic = characteristic;
	}

	@Override
	public HashMap<String, Object> getCharacteristic() {
		return characteristic;
	}

}
