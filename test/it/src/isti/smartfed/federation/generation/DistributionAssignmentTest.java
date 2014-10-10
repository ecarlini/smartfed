package it.src.isti.smartfed.federation.generation;

import org.apache.commons.math3.distribution.UniformRealDistribution;
import org.junit.Assert;
import org.junit.Test;

import it.cnr.isti.smartfed.federation.generation.DistributionAssignment;

public class DistributionAssignmentTest 
{
	@Test
	public void testAssignement()
	{
		int dc = 10;
		int hs = 20;
		
		UniformRealDistribution urd = new UniformRealDistribution(0, 1);
		int[] dist = DistributionAssignment.getAssignmentArray(dc, hs, urd);
		
		// check the length of the returned array
		Assert.assertEquals(dc, dist.length);
		
		int sum = 0;
		for (int i=0; i<dist.length; i++)
			sum += dist[i];
		
		Assert.assertEquals(hs, sum);
	}

}
