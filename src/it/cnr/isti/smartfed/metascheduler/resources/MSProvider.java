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

import it.cnr.isti.smartfed.metascheduler.resources.iface.IMSProvider;

import java.util.HashMap;




public class MSProvider implements IMSProvider, Cloneable {
	private int ID;
	
	private MSProviderComputing provCom;
	private MSProviderStorage provSto;
	private MSProviderNetwork provNet;
	
	private HashMap<String, Object> characteristic;
	
	public MSProvider(){
		MSProviderComputing comp =  new MSProviderComputing();
		MSProviderNetwork net = new MSProviderNetwork();
		MSProviderStorage store = new MSProviderStorage();
		new MSProvider(-1, new HashMap<String, Object>(), comp ,store , net);
	}
	
	public MSProvider(int id, HashMap<String, Object> characteristic, MSProviderComputing comp, 
			MSProviderStorage store, MSProviderNetwork net ){
		this.ID = id;
		this.characteristic = characteristic;
		this.provCom = comp;
		this.provNet = net;
		this.provSto = store;
		
	}
	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		if( o == null)
			return 1;
		MSProvider prov = (MSProvider)o;
		return ID -  prov.ID;
	}
	@SuppressWarnings("unchecked")
	public Object clone(){
		MSProvider prov = null;
		try {
			prov = (MSProvider)super.clone();
			prov.provCom =(MSProviderComputing) provCom.clone();
			prov.provNet = (MSProviderNetwork) provNet.clone();
			prov.provSto = (MSProviderStorage) provSto.clone();
			prov.characteristic = (HashMap<String, Object>) characteristic.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return prov;
	}
	
	
	
	@Override
	public void setID(int id) {
		ID = id;
		
	}
	
	@Override
	public void setNetwork(MSProviderNetwork network) {
		if(network != null)
			this.provNet = network;
	}
	
	@Override
	public void setComputing(MSProviderComputing computing) {
		if(computing != null)
			provCom = computing;
	}
	
	@Override
	public void setStorage(MSProviderStorage storage) {
		if(storage != null)
			provSto = storage;
		
	}
	
	@Override
	public int getID() {
		return ID;
	}

	@Override
	public MSProviderComputing getComputing() {
		return provCom;
	}
	
	@Override
	public MSProviderStorage getStorage() {
		return provSto;
	}
	
	@Override
	public MSProviderNetwork getNetwork() {
		return provNet;
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
