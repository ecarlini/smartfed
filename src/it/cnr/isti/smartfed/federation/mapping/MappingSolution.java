/*
Copyright 2013 ISTI-CNR
 
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

package it.cnr.isti.smartfed.federation.mapping;

import it.cnr.isti.smartfed.federation.application.Application;
import it.cnr.isti.smartfed.federation.resources.FederationDatacenter;
import it.cnr.isti.smartfed.federation.utils.UtilityPrint;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Vm;

/**
 * 
 * @author ema, gae
 *
 */
public class MappingSolution 
{
	private HashMap<Cloudlet, FederationDatacenter> mapping;
	private boolean valid = true;
	private String allocatorName = "UNKNOWN";
	private Application application;
	
	public MappingSolution(Application application)
	{
		this.application = application;
		mapping = new HashMap<Cloudlet, FederationDatacenter>();
	}

	public HashMap<Cloudlet, FederationDatacenter> getMapping() {
		return mapping;
	}
	
	public void set(Cloudlet cloudlet, FederationDatacenter dc){
		mapping.put(cloudlet, dc);
	}
	
	public FederationDatacenter unset(Cloudlet cloudlet){
		return mapping.remove(cloudlet);
	}
	
	public Application getApplication(){
		return application;
	}
	
	public boolean isValid(){
		return valid;
	}
	
	public void setValid(boolean valid){
		this.valid = valid;
	}


	public String getAllocatorName() {
		return allocatorName;
	}

	public void setAllocatorName(String allocatorName) {
		this.allocatorName = allocatorName;
	}

	@Override
	public String toString() {
		final int maxLen = 5;
		// printMapping();
		return "[MappingSolution] First " + maxLen + " results"
				+ (mapping != null ? toString(mapping.entrySet(), maxLen): null)
				;
	}
	
	public static
	<T extends Comparable<? super T>> List<T> asSortedList(Collection<T> c) {
	  List<T> list = new ArrayList<T>(c);
	  java.util.Collections.sort(list);
	  return list;
	}
	
	public void printMapping(){
		Set<Cloudlet> s = mapping.keySet();
		SortedSet<Cloudlet> ss  = new TreeSet<Cloudlet>(new Comparator<Cloudlet>() {
			@Override
			public int compare(Cloudlet first, Cloudlet second) {
				if (first.getCloudletId() > second.getCloudletId()) 
					return 1; //greater
				else if (first.getCloudletId() < second.getCloudletId())
					return -1; //smaller
				return 0; // equal
			}
		});
		for (Cloudlet c : s)
			ss.add(c);
		
		for (Cloudlet c : ss)
			System.out.println("c# "+ c.getCloudletId() + ", d#" +  mapping.get(c).getId());
	}
	
	/**
	 * toString helper
	 */
	private String toString(Collection<Entry<Cloudlet, FederationDatacenter>> collection, int maxLen) {
		StringBuilder builder = new StringBuilder();
		builder.append( "\n ");
		int i = 0;
		for (Iterator<?> iterator = collection.iterator(); iterator.hasNext() && i < maxLen; i++) {
			if (i > 0)
				builder.append(" ");
			Entry<Cloudlet, FederationDatacenter> n = (Entry<Cloudlet, FederationDatacenter>) iterator.next();
			Vm vm = application.getVertexForCloudlet(n.getKey()).getAssociatedVm(n.getKey());
			builder.append(UtilityPrint.toString(n.getKey()) + "("+UtilityPrint.toString(vm)+")-> " + n.getValue()+"\n");
		}
		builder.append("Is valid: "+this.isValid());
		builder.append("\nAllocator: "+this.allocatorName);
		return builder.toString();
	}

	/**
	 * Check if this mapping is the same solution than the 
	 * given target mapping	 
	 * @param target
	 * @return
	 */
	public boolean isSameSolution(MappingSolution target)
	{
		// Check that this and target have the same size
		if (mapping.size() != target.getMapping().size())
			return false;
		
		// Check the values of this against target
		for (Cloudlet c: mapping.keySet())
		{
			FederationDatacenter targetValue = target.getMapping().get(c);
			FederationDatacenter thisValue = mapping.get(c);
			
			if (targetValue != null && thisValue != null)
			{
				if (targetValue.getId() != thisValue.getId())
					return false;
			}
		}
		
		return true;
	}
}
