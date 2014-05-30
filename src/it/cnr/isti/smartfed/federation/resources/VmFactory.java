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

package it.cnr.isti.smartfed.federation.resources;

import org.cloudbus.cloudsim.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.Vm;

/*
 * The VMs returned reflect the first generation machines at Amazon EC2
 * http://aws.amazon.com/ec2/instance-types/
 * 
 * CPU mips are taken from here:
 * http://www.cloudiquity.com/2009/01/amazon-ec2-instances-and-cpuinfo/
 * 
 * bandwidth is quite obscure yet.
 */
public class VmFactory 
{
	public enum VmType
	{
		SMALL,
		MEDIUM,
		LARGE,
		XLARGE,
		CUSTOM
	}
	
	public static Vm getVm(VmType type, int userId)
	{
		switch (type)
		{
			case SMALL:
			{
				return createSmall(userId);
			}
			case MEDIUM:
			{
				return createMedium(userId);
			}
			case LARGE:
			{
				return createLarge(userId);
			}
			case XLARGE:
			{
				return createXLarge(userId);
			}
			default:
			{
				return createSmall(userId);
			}
		}
	}
	

	private static Vm createSmall(int userId)
	{		
		Vm vm = new Vm(ResourceCounter.nextVmID(), 
				userId, 
				6502.18, 
				1, 
				new Double(1.7 * 1024 ).intValue(), // RAM: 1.7 GB
				new Long(1 * 1024 * 1024), // i assume at least 1MB p/s  
				new Long(160 * 1024), // DISK: 160 GB
				"Xen", 
				new CloudletSchedulerTimeShared());
		VmTyped vmt = new VmTyped(vm, VmType.SMALL);
		return vm;
	}
	
	private static Vm createMedium(int userId)
	{		
		Vm vm = new Vm(ResourceCounter.nextVmID(), 
				userId, 
				6502.18, // data not available, i assume as small instances
				1, 
				new Double(3.75 * 1024).intValue(), // 3.75 GB
				new Long(1 * 1024 * 1024), // i assume at least 1MB p/s  
				new Long(410 * 1024), // 410 GB
				"Xen", 
				new CloudletSchedulerTimeShared());
		VmTyped vmt = new VmTyped(vm, VmType.MEDIUM);
		return vm;
	}

	private static Vm createLarge(int userId)
	{		
		Vm vm = new Vm(ResourceCounter.nextVmID(), 
				userId, 
				8022, 
				2, 
				new Double(7.5 * 1024).intValue(), // 7.5 GB
				new Long(1 * 1024 * 1024), // i assume at least 1MB p/s  
				new Long(850 * 1024), // 850 GB
				"Xen", 
				new CloudletSchedulerTimeShared());
		VmTyped vmt = new VmTyped(vm, VmType.LARGE);
		return vm;
	}
	
	private static Vm createXLarge(int userId)
	{		
		Vm vm = new Vm(ResourceCounter.nextVmID(), 
				userId, 
				5202.15 * 4, 
				4, 
				new Double(15 * 1024).intValue(), // 15 GB
				new Long(1 * 1024 * 1024), // i assume at least 1MB p/s  
				new Long(1690 * 1024), // 1690 GB
				"Xen", 
				new CloudletSchedulerTimeShared());
		VmTyped vmt = new VmTyped(vm, VmType.XLARGE);
		return vm;
	}

	public static Vm getCustomVm(int userId, double mips, int cores, int ramMB, long bandMB, long diskMB)
	{		
		Vm vm = new Vm(ResourceCounter.nextVmID(), 
				userId, 
				mips, 
				cores, 
				ramMB,
				bandMB,
				diskMB,
				"Xen", 
				new CloudletSchedulerTimeShared());	
		VmTyped vmt = new VmTyped(vm, VmType.CUSTOM);
		return vmt;
	}
	
	public static Vm cloneVMnewId(Vm vm)
	{
		Vm result = new Vm(ResourceCounter.nextVmID(), 
				vm.getUserId(), 
				vm.getMips(), 
				vm.getNumberOfPes(), 
				vm.getRam(),
				vm.getBw(),
				vm.getSize(),
				vm.getVmm(), 
				vm.getCloudletScheduler());		
		return result;
	}
}
