package it.cnr.isti.smartfed.junit;

import it.cnr.isti.smartfed.ApplicationGenerator;
import it.cnr.isti.smartfed.DatacenterGenerator;
import it.cnr.isti.smartfed.federation.application.Application;
import it.cnr.isti.smartfed.federation.resources.FederationDatacenter;
import it.cnr.isti.smartfed.federation.resources.ResourceCounter;

import java.util.Calendar;
import java.util.List;

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
			sb1.append(fd.toString()).append("\n");
		
		ResourceCounter.reset();
		CloudSim.init(1, Calendar.getInstance(), true);
		
		dg = new DatacenterGenerator(33);
		list = dg.getDatacenters(5, 10);
		
		StringBuilder sb2 = new StringBuilder();
		for (FederationDatacenter fd: list)
			sb2.append(fd.toString()).append("\n");
		
		Assert.assertEquals(sb1.toString(), sb2.toString());
	}
	
	@Test
	public void testApplicationGenerator()
	{		
		ApplicationGenerator ag = new ApplicationGenerator(43);
		Application app1 = ag.getApplication(1, 2, 5);

		ResourceCounter.reset();
		ag = new ApplicationGenerator(43);
		Application app2 = ag.getApplication(1, 2, 5);
		
		Assert.assertEquals(app1.allVMsString(), app2.allVMsString());
	}
}
