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

package it.cnr.isti.smartfed.metascheduler;

import java.util.Random;
import org.jgap.RandomGenerator;

public class CRandGenerator implements RandomGenerator {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int n_range;

	private Random random;
	
	public CRandGenerator(int max_val, long seed){
		n_range = max_val;
		random = new Random(seed);
	}
	
	public CRandGenerator(int size){
		n_range = size;
		random = new Random(System.currentTimeMillis());
	}

	/*
	public void setSeed(long seed) {
		random.setSeed(seed);
	}
	*/
	
	@Override
	public boolean nextBoolean() {
		// NOT IMPLEMENTED
		return false;
	}

	@Override
	public double nextDouble() {
		return random.nextDouble();
	}

	@Override
	public float nextFloat() {
		return random.nextFloat();
	}

	@Override
	public int nextInt() {
		int rand = random.nextInt();
		// int rand = random.nextInt(n_range);
		// System.out.println("rand: " + rand);
		return rand;
	}

	
	@Override
	public int nextInt(int arg0) {
		return random.nextInt(arg0);
		// return Math.abs(random.nextInt(n_range) % arg0);
	}

	@Override
	public long nextLong() {
		return nextLong();
	}

}
