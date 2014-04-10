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


import it.cnr.isti.smartfed.federation.mapping.AbstractAllocator;
import it.cnr.isti.smartfed.federation.mapping.GeneticAllocator;
import it.cnr.isti.smartfed.federation.mapping.online.GreedyRollbackAllocatorAppliance;
import it.cnr.isti.smartfed.test.Experiment;

import java.util.Properties;


public class ExperimentMapping 
{


	private static Properties datacentersProp(){
		Properties tmp = new Properties();
		tmp.setProperty("datacenter_number", "3");
		tmp.setProperty("datacenter_size", "5");
		tmp.setProperty("datacenter_places", "Italy, Italy, Italy");
		tmp.setProperty("higher_ram_amount_mb", "8192");
		tmp.setProperty("higher_cost_per_mem", "0.01"); //prices per hour per GB
		tmp.setProperty("cost_per_storage", "0.00056");
		// tmp.setProperty("storage_mb", "870400"); // 850GB, value of VM Large
		tmp.setProperty("mips", "250000");
		return tmp;
	}
	
	private static Properties applicationProp(){
		Properties tmp2  = new Properties();
		tmp2.setProperty("application_places", "Italy,Italy,Italy");
		tmp2.setProperty("application_budget", "200.0,300.0,200.0");
		tmp2.setProperty("application_cloudlets", "3");
		return tmp2;
	}


	public static void main(String[] args) {
		AbstractAllocator genetic = new GeneticAllocator();
		AbstractAllocator greedy = new GreedyRollbackAllocatorAppliance();
		
		DataSetMS d = new DataSetMS(datacentersProp(), applicationProp());
		
		Experiment e1 = new Experiment(genetic, d);
		e1.run();
		
		/*
		Experiment e2 = new Experiment(greedy, d);
		e2.run();
		*/
	}

}
