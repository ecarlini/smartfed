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

import it.cnr.isti.smartfed.metascheduler.resources.iface.IMSAppNetwork;

import java.util.HashMap;



public class MSApplicationNetwork implements IMSAppNetwork, Cloneable {

	private int ID;
	
	//testing
	private HashMap<String, Object> characteristic;
	
	public MSApplicationNetwork(){
		new MSApplicationNetwork(-1, new HashMap<String, Object>());
	}
	public MSApplicationNetwork(int id, HashMap<String, Object> characteristic){
		this.characteristic = characteristic;
		this.ID = id;
	}
	
	@Override
	public int compareTo(Object o) {
		if(o == null)
			return 1;
		MSApplicationNetwork appN = (MSApplicationNetwork)o;
		return ID - appN.ID;
		
	}
	
	@SuppressWarnings("unchecked")
	public Object clone(){
		MSApplicationNetwork appN = null;
		try {
			appN = (MSApplicationNetwork) super.clone();
			appN.characteristic = (HashMap<String, Object>) characteristic.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return appN;
	}
	@Override
	public int getID() {
		return ID;
	}
	@Override
	public void setID(int ID) {
		this.ID = ID;
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
