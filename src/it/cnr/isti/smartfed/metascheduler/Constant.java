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

public class Constant {
	public static final String HRAM = "higher_ram_amount_mb";
	
	public static final String BW = "bw_amount";
	public static final String STORE = "storage_mb";
	public static final String RAM = "ram_amount_mb";
	public static final String MIPS = "mips";
	public static final String CPU_NUMBER = "cpu_number";
	public static final String VM_TYPE = "vm_type";
	
	public static final String BUDGET = "budget";
	public static final String VM_INSTANCES= "vm_instances";
	
	public static final String COST_SEC = "cost_per_sec";
	public static final String COST_MEM = "cost_per_mem";
	public static final String COST_STORAGE = "cost_per_storage";
	public static final String COST_BW = "cost_per_bw";
	public static final String COST_VM = "cost_per_vm";

	public static final String PLACE = "place";
	public static final String ID = "ID";
	
	//public static final String[] aggregationParam = {"ram_amount_mb", "bw_amount", "storage_mb"};
	
	public static final String DATACENTER_RAM_INC = "ram_increment";
	public static final String DATACENTER_SIZE = "datacenter_size";
	public static final String DATACENTER_NUMBER = "datacenter_number";
	public static final String DATACENTER_PLACES = "datacenter_places";
	public static final String APPLICATION_PLACES = "application_places";
	public static final String APPLICATION_BUDGET ="application_budget";
	public static final String APPLICATION_CLOUDLETS = "application_cloudlets";
	
}
