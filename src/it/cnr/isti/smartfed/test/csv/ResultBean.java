package it.cnr.isti.smartfed.test.csv;

import org.supercsv.cellprocessor.ParseDouble;
import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.ift.CellProcessor;

public class ResultBean
{
	private int cloudlets;
	private int datacenters;
	private double randomCost;
	private double greedyCost;
	private double geneticCost;
	private int randomDifference;
	private int greedyDifference;
	private int geneticDifference;
	private int randomFailures;
	private int greedyFailures;
	private int geneticFailures;
		
	public static final String[] header = new String[] {"cloudlets", "datacenters", "randomCost", 
		"greedyCost","geneticCost","randomDifference", "greedyDifference", "geneticDifference", 
		"randomFailures", "greedyFailures", "geneticFailures"};

	public static final CellProcessor[] processors = new CellProcessor[] 
	{
		new ParseInt(),
		new ParseInt(),
		new ParseDouble(),
		new ParseDouble(),
		new ParseDouble(),
		new ParseInt(),
		new ParseInt(),
		new ParseInt(),
		new ParseInt(),
		new ParseInt(),
		new ParseInt(),
	};
	
	public ResultBean(int cloudlets, int datacenters, double randomCost,
			double greedyCost, double geneticCost, int randomDifference,
			int greedyDifference, int geneticDifference, int randomFailures,
			int greedyFailures, int geneticFailures) {
		super();
		this.cloudlets = cloudlets;
		this.datacenters = datacenters;
		this.randomCost = randomCost;
		this.greedyCost = greedyCost;
		this.geneticCost = geneticCost;
		this.randomDifference = randomDifference;
		this.greedyDifference = greedyDifference;
		this.geneticDifference = geneticDifference;
		this.randomFailures = randomFailures;
		this.greedyFailures = greedyFailures;
		this.geneticFailures = geneticFailures;
	}

	public ResultBean() {}
	
	public int getCloudlets() {
		return cloudlets;
	}

	public void setCloudlets(int cloudlets) {
		this.cloudlets = cloudlets;
	}

	public int getDatacenters() {
		return datacenters;
	}

	public void setDatacenters(int datacenters) {
		this.datacenters = datacenters;
	}

	public double getRandomCost() {
		return randomCost;
	}

	public void setRandomCost(double randomCost) {
		this.randomCost = randomCost;
	}

	public double getGreedyCost() {
		return greedyCost;
	}

	public void setGreedyCost(double greedyCost) {
		this.greedyCost = greedyCost;
	}

	public double getGeneticCost() {
		return geneticCost;
	}

	public void setGeneticCost(double geneticCost) {
		this.geneticCost = geneticCost;
	}

	public int getRandomDifference() {
		return randomDifference;
	}

	public void setRandomDifference(int randomDifference) {
		this.randomDifference = randomDifference;
	}

	public int getGreedyDifference() {
		return greedyDifference;
	}

	public void setGreedyDifference(int greedyDifference) {
		this.greedyDifference = greedyDifference;
	}

	public int getGeneticDifference() {
		return geneticDifference;
	}

	public void setGeneticDifference(int geneticDifference) {
		this.geneticDifference = geneticDifference;
	}

	public int getRandomFailures() {
		return randomFailures;
	}

	public void setRandomFailures(int randomFailures) {
		this.randomFailures = randomFailures;
	}

	public int getGreedyFailures() {
		return greedyFailures;
	}

	public void setGreedyFailures(int greedyFailures) {
		this.greedyFailures = greedyFailures;
	}

	public int getGeneticFailures() {
		return geneticFailures;
	}

	public void setGeneticFailures(int geneticFailures) {
		this.geneticFailures = geneticFailures;
	}
		
}
