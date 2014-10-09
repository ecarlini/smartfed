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

package it.cnr.isti.smartfed.metascheduler;

import java.util.List;

import org.jgap.Gene;
import org.jgap.IChromosome;

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
	protected String constraintName = "Generic";
	
	public MSPolicy(double weight, char type, char group){
		this.weight = weight;
		this.type = type;
		this.group = group;
	}
	
	public MSPolicy(double weight, char type){
		this(weight, type, MSPolicy.LOCAL_CONSTRAINT);
	}

	public double evaluatePolicy(int gene_index, IChromosome chromos, IMSApplication app, IMSProvider prov){
		double res;
		
		switch (this.group){
		case MSPolicy.GLOBAL_CONSTRAINT: 
			res = evaluateGlobalPolicy(gene_index, chromos, app, prov);
			break;
		case MSPolicy.LOCAL_CONSTRAINT:
			res = evaluateLocalPolicy(chromos.getGene(gene_index), app.getNodes().get(gene_index), prov);
			break;
		default:
			res = evaluateLocalPolicy(chromos.getGene(gene_index), app.getNodes().get(gene_index), prov);
		}
		return res;
	}
	
	
	protected double evaluateGlobalPolicy(int gene_index, IChromosome chromos, IMSApplication app, IMSProvider prov){
		List<MSApplicationNode> nodes = app.getNodes();
		MSApplicationNode node = nodes.get(gene_index);
		return evaluateLocalPolicy(chromos.getGene(gene_index), node, prov);
	}
	
	
	protected abstract double evaluateLocalPolicy(Gene g, MSApplicationNode node, IMSProvider prov);
	
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
	
	protected double calculateDistance_ErrHandling(Double cost, Double budget, Double maxCost){
		String name = this.constraintName;
		double distance;
		try {
			distance = evaluateDistance(cost, budget, maxCost);
		} catch (Exception e) {
			distance = RUNTIME_ERROR; // a positive value in order to not consider this constraint
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		if (DEBUG)
			System.out.println("\tEval on " + name + " " + cost + "-" + budget + "/" + maxCost + "=" + distance);
		return distance;
	}

}
