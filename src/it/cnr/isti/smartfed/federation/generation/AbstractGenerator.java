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
