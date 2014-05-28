package it.cnr.isti.smartfed.junit;

import junit.framework.Assert;

import org.apache.commons.math3.distribution.UniformRealDistribution;
import org.junit.Test;

import it.cnr.isti.smartfed.federation.utils.DistributionAssignment;

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
