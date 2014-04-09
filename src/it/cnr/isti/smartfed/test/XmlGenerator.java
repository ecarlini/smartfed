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

package it.cnr.isti.smartfed.test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

public class XmlGenerator {
	static String prefix = "/tmp/";
	
	public static String generateMCfile(int numOfMacroCloudlets, 
			int minCloudletLength, int maxCloudletLength, int cloudletFileSize, int cloudletOutputSize, int numOfPEsForCloudlet,
			int minCloudletInstances, int maxCloudletInstances, int ram, int mips, int bandwidth,
			int vmBandwidth, int maxPrice, String cpuType, double reliability) throws IOException {
		
		String filename = "MC."+numOfMacroCloudlets+"."+System.currentTimeMillis()+".xml";
		
		File MC = new File(prefix + filename);
		PrintWriter pw = new PrintWriter(new FileWriter(MC));
		
		pw.println("<Name> Test </Name>");
		pw.println("<MCList>");
		
		Random r = new Random();
		
		for(int i=0;i<numOfMacroCloudlets;i++){
			pw.println("\t<MacroCloudlet>");
			pw.println("\t\t<ID> "+i+" </ID>");
			pw.println("\t\t<cloudlet>");
			int deltalength = maxCloudletLength-minCloudletLength;
			int cloudletLength = deltalength > 0 ? (minCloudletLength+r.nextInt(deltalength)) : minCloudletLength;
			pw.println("\t\t\t<cloudletLength> "+cloudletLength+" </cloudletLength>");
			pw.println("\t\t\t<cloudletFileSize> "+cloudletFileSize+" </cloudletFileSize>");
			pw.println("\t\t\t<cloudletOutputSize> "+cloudletOutputSize+" </cloudletOutputSize>");
			pw.println("\t\t\t<numberOfPes> "+numOfPEsForCloudlet+" </numberOfPes>");
			pw.println("\t\t</cloudlet>");

			pw.println("\t\t<bottomRange> "+minCloudletInstances+" </bottomRange>");
			pw.println("\t\t<topRange> "+maxCloudletInstances+" </topRange>");
			int deltainstance = maxCloudletInstances - minCloudletInstances;
			int actualNumber = deltainstance > 0 ? (minCloudletInstances+r.nextInt(maxCloudletInstances-minCloudletInstances)) : minCloudletInstances;
			
			pw.println("\t\t<actualNumber> "+actualNumber+" </actualNumber>");
			
			pw.println("\t\t<ramSize> "+ram+" </ramSize>");
			pw.println("\t\t<mips> "+mips+" </mips>");
			pw.println("\t\t<bandwith> "+bandwidth+" </bandwith>");
			
			pw.println("\t\t<vmBw> "+vmBandwidth+" </vmBw>");
			pw.println("\t\t<priceConstraint> "+maxPrice+" </priceConstraint>");
			pw.println("\t\t<cpuType> "+cpuType+" </cpuType>");
			pw.println("\t\t<reliability> "+reliability+" </reliability>");
			
			pw.println("\t</MacroCloudlet>");
		}
		
		pw.println("</MCList>");
		pw.println("<MCEdges>");
		
		for (int i=0; i<numOfMacroCloudlets-1; i++){
			//for(int j=i+1;j<numOfMacroCloudlets;j++){
				pw.println("<edge> "+i+" "+(i+1)+" "+100+" "+0.1+" "+"RSA"+" </edge>");	
			//}
		}
		
		pw.println("</MCEdges>");
		
		pw.close();	
		return filename;
	}

	public static String generateDCfile(int numOfDatacenters, 
			int minNumOfMachines, int maxNumOfMachines, int minNumOfCores, int maxNumOfCores,
			String arch, String os, String vmm, int mips, int ram, int storage, int bandwidth,
			String timezone, double cost, double costPerMem, double costPerStorage, double costPerBw, 
			double DCBandwidth, double reliability) throws IOException {
		
		String filename = "DC."+numOfDatacenters+"."+System.currentTimeMillis()+".xml";
		
		File DC = new File(prefix + filename);
		PrintWriter pw = new PrintWriter(new FileWriter(DC));
		
		pw.println("<DataCenterList>");
		
		for (int i=0; i<numOfDatacenters; i++){
			pw.println("\t<Datacenter_characteristic>");
			
			pw.println("\t\t<HostList>");
			int variance = maxNumOfMachines - minNumOfMachines;
			Random r =  new Random();
			int delta = variance>0 ? r.nextInt(variance) : 0;
			
			for (int j=0; j<(minNumOfMachines+delta); j++){	
				pw.println("\t\t\t<Host>");
				int core_variance = maxNumOfCores - minNumOfCores;
				Random r_cores =  new Random();
				int delta_cores = core_variance > 0 ? r_cores.nextInt(core_variance) : 0;
				
				pw.println("\t\t\t\t<PeList>");
				for (int k=0;k<(minNumOfCores+delta_cores);k++){
					pw.println("\t\t\t\t\t<PE>");
					pw.println("\t\t\t\t\t\t<Peid> "+k+" </Peid>");
					pw.println("\t\t\t\t\t\t<mips> "+mips+" </mips>");
					pw.println("\t\t\t\t\t</PE>");
				}
				pw.println("\t\t\t\t</PeList>");
				
				pw.println("\t\t\t\t<hostId> "+j+" </hostId>");
				pw.println("\t\t\t\t<ram> "+ram+" </ram>");
				pw.println("\t\t\t\t<storage> "+storage+" </storage>");
				pw.println("\t\t\t\t<bw> "+bandwidth+" </bw>");
				
				pw.println("\t\t\t</Host>");
			}
			
			pw.println("\t\t</HostList>");
			
			pw.println("\t\t<arch> "+arch+" </arch>");
			pw.println("\t\t<os> "+os+" </os>");
			pw.println("\t\t<vmm> "+vmm+" </vmm>");
			pw.println("\t\t<time_zone> "+timezone+" </time_zone>");
						
			pw.println("\t\t<cost> "+cost+" </cost>");
			pw.println("\t\t<costPerMem> "+costPerMem+" </costPerMem>");
			pw.println("\t\t<costPerStorage> "+costPerStorage+" </costPerStorage>");
			pw.println("\t\t<costPerBw> "+costPerBw+" </costPerBw>");
			
			pw.println("\t\t<DCBw> "+DCBandwidth+" </DCBw>");
			
			pw.println("\t\t<DCName> Datacenter_"+i+" </DCName>");
			
			pw.println("\t\t<reliability> "+reliability+" </reliability>");
			
			pw.println("\t</Datacenter_characteristic>");
		}
		
		pw.println("</DataCenterList>");
		
		pw.println("<Network>");
		
		for (int i=0; i<numOfDatacenters; i++){
			for (int j=i+1; j<numOfDatacenters; j++){
				pw.print("\t<link> ");
				pw.print("Datacenter_"+i);
				pw.print(" Datacenter_"+j+" ");
				pw.print(1000+" ");
				pw.print(0.1+" ");
				pw.print("RSA MD5 MDS2");
				pw.println(" </link>");
			}
		}
		
		for (int i=0; i<numOfDatacenters; i++){
			pw.print("\t<link> ");
			pw.print("Broker Datacenter_"+i+" 1000 0.1");
			pw.println(" </link>");
		}
		
		pw.println("</Network>");
		
		pw.close();
		
		return filename;
	}
}
