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

package it.cnr.isti.smartfed.metascheduler.test;


import it.cnr.isti.smartfed.federation.Federation;
import it.cnr.isti.smartfed.federation.resources.FederationDatacenter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class MetaschedulerUtilities {

	protected static void printCloudSimResults(List<FederationDatacenter> dcList, Federation fed){
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ RESULTS ~~~~~~~~~~~~~~~~~~~~~~~~~~~ " + fed.getVmToDatacenter().size() );

		System.out.println("FederationSim Results");
		HashMap<Integer, Integer> allocatedToDatacenter = fed.getVmToDatacenter();
		Iterator<Integer> keys = allocatedToDatacenter.keySet().iterator();
		while (keys.hasNext()) {
			Integer i = keys.next();
			System.out.println("VM #" + i + " Allocated in datacenter #" + allocatedToDatacenter.get(i));
		}
	}


	public static void saveFederationToFile(String path, List<FederationDatacenter> fed ){
		try{
			FileOutputStream fout = new FileOutputStream(path);
			ObjectOutputStream oos = new ObjectOutputStream(fout);
			oos.writeObject(fed);
			oos.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public static void saveFederationToTxtFile(String path, List<FederationDatacenter> fed ){
		PrintWriter pw = null;
		File DC = null;
		try{
			DC = new File(path);
			pw = new PrintWriter(new FileWriter(DC));
			for (FederationDatacenter f: fed){
				// pw.println(f.getDatacenterRepresentation());
				pw.println(f.getDatacenterCharacteristicString());
			}
		}catch(IOException e){
			e.printStackTrace();
		}
		finally{
			pw.close();
		}
	}


	public static List<FederationDatacenter> readFederationFromFile(String path){
		List<FederationDatacenter> dclist = null;
		try{
			FileInputStream fin = new FileInputStream(path);
			ObjectInputStream ois = new ObjectInputStream(fin);
			dclist = (List<FederationDatacenter>)ois.readObject();
			ois.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		return dclist;
	}


}
