package it.cnr.isti.smartfed.metascheduler;

import it.cnr.isti.smartfed.metascheduler.resources.MSApplicationNode;
import it.cnr.isti.smartfed.metascheduler.resources.iface.IMSApplication;
import it.cnr.isti.smartfed.metascheduler.resources.iface.IMSProvider;

public abstract class MSPolicy  {
	
	public static final char ASCENDENT_TYPE = 'A';
	public static final char DESCENDENT_TYPE = 'D';
	public static final char EQUAL_TYPE = 'E';
	public static final char GLOBAL_CONSTRAINT ='G';
	public static final char LOCAL_CONSTRAINT = 'L';
	
	protected final static int RUNTIME_ERROR = 1000;
	protected static final boolean DEBUG = false;
	
	private double weight;
	private char type;
	private char group;
	
	public MSPolicy(double weight, char type, char group){
		this.weight = weight;
		this.type = type;
		this.group = group;
	}

	public abstract double evaluateGlobalPolicy(IMSApplication app, IMSProvider prov);
	
	public abstract double evaluateLocalPolicy(MSApplicationNode node, IMSProvider prov);
	
	public char getType(){
		return type;
	}
	public char getGroup(){
		return group;
	}
	public double getWeight(){
		return weight;
	}
	public void setWeight(double w){
		weight = w;
	}	
	
	/*
	 * value is what I have, u_constraint is what I want
	 */
	public double evaluateDistance(double value, double u_constraint, double maxValue) throws Exception{
		if (maxValue == 0){
			throw new Exception("Max Value not set in method " + this.getClass().getName());
		}
		switch (type){
		case ASCENDENT_TYPE:
			return (u_constraint - value)/maxValue;
		case DESCENDENT_TYPE:
			return (value - u_constraint)/maxValue;
		case EQUAL_TYPE:
			throw new Exception("Wrong method for this type of constraint");
		default:
			return 0;
		}
	}
	
	public double evaluateDistance(String value, String u_constraint) throws Exception{
		value.trim();
		u_constraint.trim();
		final double epsilon = 0.00000000001;
		switch (type){
		case ASCENDENT_TYPE:
			throw new Exception("Wrong method for this type of constraint");
		case DESCENDENT_TYPE:
			throw new Exception("Wrong method for this type of constraint");
		case EQUAL_TYPE:
			double ret = (value.compareTo(u_constraint) == 0 ) ? 0 : 1; // 1 if different one each other
			return ret - epsilon;
		default:
			return 0;
		}
	}
}
