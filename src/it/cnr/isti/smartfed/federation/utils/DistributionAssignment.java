package it.cnr.isti.smartfed.federation.utils;

import java.util.Arrays;

import org.apache.commons.math3.distribution.AbstractRealDistribution;

public class DistributionAssignment 
{
	/**
	 * Generates an assignment balls to bins according to the
	 * given distribution. The i-th position of the array represents 
	 * the number of balls to assign to the i-th bin. 
	 * @param bins
	 * @param balls
	 * @param distribution
	 * @return
	 */
	public static int[] getAssignmentArray(int bins, int balls, AbstractRealDistribution distribution)
	{
		// generates and sort samples from the distribution
		double[] samples = distribution.sample(balls);
		Arrays.sort(samples);
		
		// manage the samples bounds
		double min = samples[0];
		double max = samples[samples.length - 1];
		double delta = (max - min) / bins;
		
		// generate the assignment
		int[] ass = new int[bins];
		int z = 0;
		
		for (int i=0; i<ass.length; i++)
		{
			double threshold = min + (delta * (i+1));
			
			while (z <samples.length && samples[z] <= threshold)
			{
				ass[i]++;
				z++;
			}
		}	
		
		return ass;
	}
}
