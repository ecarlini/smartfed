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

package it.src.isti.smartfed.federation.generation;

import it.cnr.isti.smartfed.federation.application.Application;
import it.cnr.isti.smartfed.federation.generation.ApplicationGenerator;
import it.cnr.isti.smartfed.federation.generation.DatacenterGenerator;
import it.cnr.isti.smartfed.federation.resources.FederationDatacenter;
import it.cnr.isti.smartfed.federation.resources.ResourceCounter;

import java.util.Calendar;
import java.util.List;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.core.CloudSim;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class GeneratorsRepeatability 
{
	@BeforeClass
	public static void testSetup() 
	{
		Log.setDisabled(true);
	}

	@Test
	public void testDatacenterGenerator()
	{
		CloudSim.init(1, Calendar.getInstance(), true);

		DatacenterGenerator dg = new DatacenterGenerator(33);
		List<FederationDatacenter> list = dg.getDatacenters(5, 10);

		StringBuilder sb1 = new StringBuilder();
		for (FederationDatacenter fd: list)
			sb1.append(fd.getDatacenterCharacteristicString()).append("\n");

		ResourceCounter.reset();
		CloudSim.init(1, Calendar.getInstance(), true);

		dg = new DatacenterGenerator(33);
		list = dg.getDatacenters(5, 10);

		StringBuilder sb2 = new StringBuilder();
		for (FederationDatacenter fd: list)
			sb2.append(fd.getDatacenterCharacteristicString()).append("\n");

		Assert.assertEquals(sb1.toString(), sb2.toString());

		for (FederationDatacenter f : list){
			List<Host> l = f.getHostList();
			for (Host h : l){
				Assert.assertEquals(h.getStorage(), f.getMSCharacteristics().getHighestStorage());
			}
			System.out.println(f.getMSCharacteristics());
		}
	}

	@Test
	public void testApplicationGenerator()
	{		
		ResourceCounter.reset();
		ApplicationGenerator ag = new ApplicationGenerator(43);
		Application app1 = ag.getApplication(1, 2, 5);

		ResourceCounter.reset();
		ag = new ApplicationGenerator(43);
		Application app2 = ag.getApplication(1, 2, 5);

		Assert.assertEquals(app1.allVMsString(), app2.allVMsString());
	}
}
