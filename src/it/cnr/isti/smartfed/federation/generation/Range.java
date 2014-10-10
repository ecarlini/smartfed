package it.cnr.isti.smartfed.federation.generation;

public class Range 
{
	private double lower;
	private double upper;
	private double difference;
	
	public Range(double lower, double upper) 
	{
		super();
		
		if (upper < lower)
			throw new IllegalArgumentException("value must be between 0 and 1");
		
		this.lower = lower;
		this.upper = upper;
		this.difference = upper - lower;
	}
	
	/**
	 * Given a value between 0 and 1 scales it
	 * to the range.
	 * @param value
	 * @return
	 */
	public double denormalize(double value)
	{
		if (value < 0 && value > 1)
		{
			throw new IllegalArgumentException("value must be between 0 and 1");
		}
		double res = lower + (difference * value);
		return res;
	}
	
	public double getLower() {
		return lower;
	}
	public double getUpper() {
		return upper;
	}
	
	

}
