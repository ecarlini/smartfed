package it.cnr.isti.smartfed.metascheduler;

import it.cnr.isti.smartfed.metascheduler.iface.MSProviderAdapter;
import it.cnr.isti.smartfed.metascheduler.resources.MSApplicationNode;
import it.cnr.isti.smartfed.metascheduler.resources.iface.IMSApplication;
import it.cnr.isti.smartfed.metascheduler.resources.iface.IMSProvider;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jgap.FitnessFunction;
import org.jgap.Gene;
import org.jgap.IChromosome;

/**
 * 
 * @author gae
 *
 */
public class MSFitnessFunction extends FitnessFunction {

	public final static int AWARD = 100;
	private final double EQUALITY = 0.0001;
	private final static Logger log = Logger.getLogger(Logger.class.getName());

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	static private IMSApplication application;
	static private List<IMSProvider> providerList;
	static private List<MSPolicy> policy;

	private HashMap<Integer, Integer> association;
	
	public MSFitnessFunction(IMSApplication app, List<IMSProvider> plist, List<MSPolicy> policyList){
		application = app;
		providerList = plist;
		policy = policyList;
	}
	
	private double evaluateGene(Gene gene, int index){
		double fitness = -1;
		double [] weightedDistance = new double[policy.size()];
		Integer providerID = (Integer) gene.getAllele();
		IMSProvider provider = MSProviderAdapter.findProviderById(providerList, providerID);
		List<MSApplicationNode> nodes = application.getNodes();
		MSApplicationNode node = nodes.get(index);
		for (int i = 0; i < policy.size(); i++) {
			weightedDistance[i] = policy.get(i).evaluateLocalPolicy(node,provider);
		}
		
		for (int i=0; i<weightedDistance.length; i++){
			if (weightedDistance[i] > 0){
				weightedDistance[i] = 0;
				fitness = 0; // for construction, distances in the positive space are not good, not satisfying constraints as inequality
			}
			else if (weightedDistance[i] == 0){
				weightedDistance[i] = EQUALITY;
			}
			else {
				weightedDistance[i] *= -1; // absolute value of negative numbers
			}	
		}
		/*
		for (int i=0; i<weightedDistance.length; i++){
			System.out.print(weightedDistance[i] + "  + ");
		}
		System.out.print(" = ");
		*/
		
		if (fitness != 0){
			fitness = 0;
			for (int i=0; i<weightedDistance.length; i++)
				fitness += weightedDistance[i]; 
		}
		return fitness;
	}
	
	@Override
	protected double evaluate(IChromosome chromos) {
		log.setLevel(Level.INFO);
		double fitness = 0;
		Gene[] genes = chromos.getGenes();
		double g_fit = 0;
		
		for (int i = 0; i < genes.length; i++) {
			g_fit = evaluateGene(genes[i], i) * AWARD;		//awarding those with highest number of good genes
			((CIntegerGene) genes[i]).setLocalFitness(g_fit);
			fitness += g_fit;
		}
		
		if (MSPolicy.DEBUG)
			printGenes(genes, chromos, fitness);
		
		return fitness;
	}
	
	void printGenes(Gene[] genes, IChromosome chromos, double fitness){
		System.out.print("Fitness of each gene: ");
		for (int i = 0; i < genes.length; i++) {
			System.out.print(((CIntegerGene) genes[i]).getLocalFitness() + " | ");
		}
		System.out.println();
		System.out.println("\tChromosoma " + Monitor.chromosomeToString(chromos) + " con fit_value " + fitness);
		System.out.println();
	}
}
