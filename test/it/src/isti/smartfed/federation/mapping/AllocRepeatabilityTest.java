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

package it.src.isti.smartfed.federation.mapping;

import it.cnr.isti.smartfed.federation.MonitoringHub;
import it.cnr.isti.smartfed.federation.application.Application;
import it.cnr.isti.smartfed.federation.generation.ApplicationGenerator;
import it.cnr.isti.smartfed.federation.generation.DatacenterGenerator;
import it.cnr.isti.smartfed.federation.mapping.GeneticAllocator;
import it.cnr.isti.smartfed.federation.mapping.GreedyAllocator;
import it.cnr.isti.smartfed.federation.mapping.MappingSolution;
import it.cnr.isti.smartfed.federation.mapping.RandomAllocator;
import it.cnr.isti.smartfed.federation.resources.FederationDatacenter;
import it.cnr.isti.smartfed.networking.InternetEstimator;

import java.util.Calendar;
import java.util.List;

import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.core.CloudSim;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class AllocRepeatabilityTest 
{
	private List<FederationDatacenter> datacenters;
	private Application application;
	private MonitoringHub monitor;
	private InternetEstimator netEstimator;
	
	@BeforeClass
	public static void testSetup() 
	{
		Log.setDisabled(true);
	}
	
	
	@Before
	public void reset()
	{
		// start cloudsim
		CloudSim.init(1, Calendar.getInstance(), true);
		
		// create the datacenter
		DatacenterGenerator dg = new DatacenterGenerator(124);
		datacenters = dg.getDatacenters(10, 20);
		
		// create the application
		ApplicationGenerator ag = new ApplicationGenerator(1235566);
		application = ag.getApplication(1, 2, 5);
		
		// create the monitorin hub
		monitor = new MonitoringHub(datacenters, 1000);
		
		// internet estimator
		netEstimator = this.createInternetEstimator(datacenters);
	}
	
	private InternetEstimator createInternetEstimator(List<FederationDatacenter> datacenters){
		InternetEstimator inetEst = new InternetEstimator(datacenters);
		return inetEst;
	}
	
	@Test
	public void testRandom()
	{
		RandomAllocator allocator1 = new RandomAllocator(monitor, netEstimator);
		allocator1.setRandomSeed(45);
		MappingSolution[] sols1 = allocator1.findAllocation(application);
		MappingSolution solution1 = sols1[0];
		
		RandomAllocator allocator2 = new RandomAllocator(monitor, netEstimator);
		allocator2.setRandomSeed(45);
		MappingSolution[] sols2 = allocator2.findAllocation(application);
		MappingSolution solution2 = sols2[0];
		
		RandomAllocator allocator3 = new RandomAllocator(monitor, netEstimator);
		allocator3.setRandomSeed(49);
		MappingSolution[] sols3 = allocator3.findAllocation(application);
		MappingSolution solution3 = sols3[0];
		
		Assert.assertTrue(solution1.isSameSolution(solution2));
		Assert.assertFalse(solution1.isSameSolution(solution3));
		Assert.assertFalse(solution2.isSameSolution(solution3));
	}
	
	@Test
	public void testGreedy()
	{
		GreedyAllocator a1 = new GreedyAllocator(monitor, netEstimator);
		a1.setRandomSeed(111);
		MappingSolution[] ss1 = a1.findAllocation(application);
		MappingSolution s1 = ss1[0];
		
		GreedyAllocator a2 = new GreedyAllocator(monitor, netEstimator);
		a2.setRandomSeed(111);
		MappingSolution[] ss2 = a2.findAllocation(application);
		MappingSolution s2 = ss2[0];
		/*
		 * Note: current greedy allocator implementation is deterministic.
		 * Hence, a check against different random seed is not required. 
		 */
		Assert.assertTrue(s1.isSameSolution(s2));
	}
	
	@Test
	public void testGenetic()
	{
		GeneticAllocator g1 = new GeneticAllocator(monitor, netEstimator);
		GeneticAllocator g2 = new GeneticAllocator(monitor, netEstimator);
		
		g1.setRandomSeed(42);
		g2.setRandomSeed(42);
		
		
		MappingSolution[] ss1 = g1.findAllocation(application);		
		MappingSolution[] ss2 = g2.findAllocation(application);
		
		Assert.assertTrue(ss1[0].isSameSolution(ss2[0]));
	}
}
