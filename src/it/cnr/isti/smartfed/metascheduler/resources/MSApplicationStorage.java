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

import it.cnr.isti.smartfed.metascheduler.resources.iface.IMSAppStorage;

import java.util.HashMap;

public class MSApplicationStorage implements IMSAppStorage, Cloneable {
	//private static int NEXT_ID = 1;
	/* disabled
	private double budget;
	private String place;
	private int amount;
	private char unit;
	*/
	private int ID;
	
	//testing
	private HashMap<String, Object> characteristic;
	
	
	public MSApplicationStorage(){
		new MSApplicationStorage(-1, new HashMap<String, Object>());
	}
	
	public MSApplicationStorage(int ID, HashMap<String, Object> characteristic){
		this.ID = ID;
		this.characteristic = characteristic;
	}
	/*
	@Override
	public void merge(CApplicationStorage store) {
		// TODO Auto-generated method stub
//		budget += store.getBadget();
		amount += store.getAmount();
	}
	*/
	
	public int getID(){
		return ID;
	}

	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		/* compare solo sull'ID */
		if( o == null)
			return 1;
		MSApplicationStorage appStore = (MSApplicationStorage)o;
		
		return ID - appStore.getID();
	}
	
	public Object clone(){
		MSApplicationStorage appStore = null;
		try {
			appStore = (MSApplicationStorage) super.clone();
			appStore.characteristic = (HashMap<String, Object>) characteristic.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return appStore;
	}

	@Override
	public void setID(int ID) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void setCharacteristic(HashMap<String, Object> characteristic) {
		// TODO Auto-generated method stub
		this.characteristic = characteristic;
	}

	@Override
	public HashMap<String, Object> getCharacteristic() {
		// TODO Auto-generated method stub
		return characteristic;
	}
}
