package it.cnr.isti.smartfed.metascheduler.resources;

import it.cnr.isti.smartfed.metascheduler.resources.iface.IMSProviderStorage;

import java.util.HashMap;




public class MSProviderStorage implements IMSProviderStorage, Cloneable {
	
	private int ID;
	/*
	private String place;
	private int amount;
	private double unitCost;
	*/
	//testing
	private HashMap<String, Object> characteristic;
	
	public MSProviderStorage(){
		new MSProviderStorage(-1, new HashMap<String, Object>());
	}
	
	public MSProviderStorage(int ID, HashMap<String, Object> characteristic){
		this.ID = ID;
		this.characteristic = characteristic;
	}
	

	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		if(o == null)
			return 1;
		MSProviderStorage provS = (MSProviderStorage)o;
		return ID - provS.ID;
	}
	@Override
	public int getID() {
		// TODO Auto-generated method stub
		return ID;
	}
	
	@SuppressWarnings("unchecked")
	public Object clone(){
		MSProviderStorage pStore = null;
		try {
			pStore = (MSProviderStorage) super.clone();
			pStore.characteristic = (HashMap<String, Object>) characteristic.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return pStore;
	}
	
	@Override
	public void setCharacteristic(HashMap<String, Object> characteristic) {
		this.characteristic = characteristic;
	}

	@Override
	public HashMap<String, Object> getCharacteristic() {
		return characteristic;
	}
	@Override
	public void setID(int id) {
		this.ID = id;
	}
	
/* disabled
	@Override
	public void setAmount(int amount) {
		// TODO Auto-generated method stub
		if(amount >0 )
			this.amount = amount;
		
	}

	@Override
	public void setUnitCost(double unitCost) {
		// TODO Auto-generated method stub
		if(unitCost >0)
			this.unitCost = unitCost;
		
	}

	@Override
	public void setPlace(String place) {
		// TODO Auto-generated method stub
		if(place != null && place.length() >0)
			this.place = place;
		
	}
	
	@Override
	public int getAmount() {
		// TODO Auto-generated method stub
		return amount;
	}

	@Override
	public double getUnitCost() {
		// TODO Auto-generated method stub
		return unitCost;
	}

	@Override
	public String getPlace() {
		// TODO Auto-generated method stub
		return place;
	}
*/


	
	
	

}
