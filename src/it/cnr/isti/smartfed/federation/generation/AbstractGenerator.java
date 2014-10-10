package it.cnr.isti.smartfed.federation.generation;

import org.apache.commons.math3.distribution.AbstractRealDistribution;
import org.apache.commons.math3.distribution.UniformRealDistribution;

public class AbstractGenerator 
{
	protected AbstractRealDistribution distribution;
	protected long seed;
	protected GenerationType type;	
	
	public AbstractGenerator(long seed)
	{
		distribution = new UniformRealDistribution();
		type = GenerationType.NON_UNIFORM;
		this.resetSeed(seed);
	}
	
	/**
	 * Change the seed of the generator
	 * @param seed
	 */
	public void resetSeed(long seed)
	{
		this.seed = seed;
		distribution.reseedRandomGenerator(seed);
	}

	public GenerationType getType() 
	{
		return type;
	}

	public void setType(GenerationType type) 
	{
		this.type = type;
	}
}
