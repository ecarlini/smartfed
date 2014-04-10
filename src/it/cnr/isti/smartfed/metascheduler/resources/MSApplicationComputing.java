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
