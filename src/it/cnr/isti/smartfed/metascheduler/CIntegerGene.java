package it.cnr.isti.smartfed.metascheduler;


import org.jgap.Configuration;
import org.jgap.Gene;
import org.jgap.InvalidConfigurationException;
import org.jgap.RandomGenerator;
import org.jgap.impl.IntegerGene;

public class CIntegerGene extends IntegerGene{
	private static final long serialVersionUID = 1L;
	
	double localFitness = 0;
	
	public CIntegerGene(Configuration a_config, int min, int max) throws InvalidConfigurationException {
		super(a_config, min, max);
	}
	
	public CIntegerGene(Configuration a_config, int min, int max, double fit) throws InvalidConfigurationException {
		super(a_config, min, max);
		localFitness = fit;
	}
	
	public Gene newGene() {
		try{
			Gene ret = new CIntegerGene(getConfiguration(), this.getLowerBounds(), this.getUpperBounds(), this.getLocalFitness());
			return ret;
		}catch (InvalidConfigurationException ex) {
	        throw new IllegalStateException(ex.getMessage());
		}
	}
	
	// the RandomGenerator to be used is the CRandGenerator and must be specified in the Configuration
	/*
	public void setToRandomValue(RandomGenerator arg0) {
		this.setAllele(arg0.nextInt(this.getUpperBounds() + 1)); // plus one for not excluding the upper bound
	}
	*/
	
	public void applyMutation(final int a_index, final double a_percentage) {
		// System.out.println("old value is " + this.getAllele());
		super.applyMutation(a_index, a_percentage);
		// System.out.println("new value is " + this.getAllele());
	}
	/**
	 * @return The fitness of each gene
	 */
	public double getLocalFitness() {
		return localFitness;
	}

	public void setLocalFitness(double localFitness) {
		this.localFitness = localFitness;
	}

	public String toString(){
		return super.toString() + " - " + this.getLocalFitness();
	}
}
