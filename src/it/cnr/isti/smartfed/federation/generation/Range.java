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

package it.cnr.isti.smartfed.federation.generation;

/**
 * This class represents a range to be used in generators
 * @author carlini
 *
 */

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
