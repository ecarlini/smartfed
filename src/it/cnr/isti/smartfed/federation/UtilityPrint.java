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

package it.cnr.isti.smartfed.federation;

import java.text.DecimalFormat;
import java.util.List;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Vm;

public class UtilityPrint 
{
	public static String toString(Cloudlet c)
	{		
		return "Cloudlet"+c.getCloudletId() + " ";
		// return "Cloudlet [ID="+c.getCloudletId()+",PES="+c.getNumberOfPes()+"]";
	}

	public static void printCloudletList(List<Cloudlet> list){
		System.out.println(getCloudletList(list));
	}
	
	public static String getCloudletList(List<Cloudlet> list) {
		String s = new String();
		String indent = "    ";
		s += "\n";
		s += "========== OUTPUT ==========" + "\n";
		s += "Cloudlet ID" + indent + "STATUS" + indent
				+ "DataCenter ID" + indent + "VM ID" + indent + "Time"
				+ indent + "Start Time" + indent + "Finish Time" + "\n";

		DecimalFormat dft = new DecimalFormat("###.##");
		for (Cloudlet cloudlet : list ) {
			s += indent + cloudlet.getCloudletId() + indent + indent;

			if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS) {
				s += "SUCCESS";
				s += indent + indent + cloudlet.getResourceId()
						+ indent + indent + indent + cloudlet.getVmId()
						+ indent + indent
						+ dft.format(cloudlet.getActualCPUTime()) + indent
						+ indent + dft.format(cloudlet.getExecStartTime())
						+ indent + indent
						+ dft.format(cloudlet.getFinishTime()) + "\n";
			}
			else {
				s += "Status is " + Cloudlet.getStatusString(cloudlet.getCloudletStatus());
				s += indent + indent + cloudlet.getResourceId();
			}
		}
		return s;
	}

	public static String toString(Vm vm)
	{
		return "VM #"+vm.getId(); 
	}
	
	public static String toStringDetail(Vm vm)
	{
		return "RAM: "+vm.getRam()+" DISK: "+vm.getSize() + " MIPS: "+vm.getMips()+ " NET: "+vm.getBw();
	}

	public static String toStringDetail(Host host)
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append("ram:").append(host.getRam()).append(",");
		sb.append("net:").append(host.getBw()).append(",");
		sb.append("storage:").append(host.getStorage()).append(",");
		sb.append("mips:").append(host.getTotalMips());
		
		return sb.toString();
	}
}
