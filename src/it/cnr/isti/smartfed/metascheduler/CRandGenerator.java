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
