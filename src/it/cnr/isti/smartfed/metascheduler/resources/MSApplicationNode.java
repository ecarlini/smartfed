package it.cnr.isti.smartfed.metascheduler.resources;

import java.util.HashMap;

public class MSApplicationNode implements Cloneable{	
	private int ID;
	private MSApplicationComputing appComp;
	private MSApplicationNetwork appNet;
	private MSApplicationStorage appSto;
	
	private HashMap<String, Object> characteristic;
	private HashMap<String, Object> desiredCharacteristic = null;
	
	public MSApplicationNode(){
		new MSApplicationNode(-1, new HashMap<String, Object>(), new MSApplicationComputing(), new MSApplicationStorage(), new MSApplicationNetwork());
	}
	
	public void setDesiredCharacteristics(HashMap<String, Object> characteristic){
		this.desiredCharacteristic = characteristic;
	}
	
	public HashMap<String, Object> getDesiredCharacteristic(){
		return this.desiredCharacteristic;
	}
	
	public MSApplicationNode(int ID, HashMap<String, Object> characteristic, MSApplicationComputing comp, MSApplicationStorage st, MSApplicationNetwork net){
		this.ID = ID;
		appComp = comp;
		appSto = st;
		appNet = net;
		this.characteristic = characteristic;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Object clone() {
		MSApplicationNode node = null;
		try {
			node = (MSApplicationNode) super.clone();
			node.appComp = (MSApplicationComputing) appComp.clone();
			node.appNet = (MSApplicationNetwork) appNet.clone();
			node.appSto = (MSApplicationStorage) appSto.clone();
			node.characteristic = (HashMap<String, Object>) characteristic.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return node;
	}
	
	public void setID(int id){
		ID = id;
	}
	public int getID(){
		return ID;
	}

	public MSApplicationComputing getComputing() {
		return appComp;
	}
	public MSApplicationNetwork getNetwork() {
		return appNet;
	}
	
	public void setNetwork(MSApplicationNetwork network) {
		if(network != null)
			appNet = network;
	}

	public void setComputing(MSApplicationComputing computing) {
		if(computing != null)
			appComp = computing;
	}

	public void setStorage(MSApplicationStorage storage) {
		if(storage != null)
			appSto = storage;	
	}
	
	public MSApplicationStorage getStorage() {
		return appSto;
	}
	
	public void setCharacteristic(HashMap<String, Object> characteristic){
		this.characteristic = characteristic;
	}
	
	public HashMap<String, Object> getCharacteristic(){
		return characteristic;
	}

	
}
