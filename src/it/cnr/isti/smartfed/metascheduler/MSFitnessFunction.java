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
	
	private double evaluateGene(int gene_index, IChromosome chromos){
		double fitness = -1;
		double [] weightedDistance = new double[policy.size()];
		Gene[] genes = chromos.getGenes();
		
		Integer providerID = (Integer) genes[gene_index].getAllele();
		IMSProvider provider = MSProviderAdapter.findProviderById(providerList, providerID);
		// List<MSApplicationNode> nodes = application.getNodes();
		// MSApplicationNode node = nodes.get(gene_index);
		for (int i = 0; i < policy.size(); i++) {
			// weightedDistance[i] = policy.get(i).evaluateLocalPolicy(node,provider);
			weightedDistance[i] = policy.get(i).evaluateGlobalPolicy(gene_index, chromos, application, provider);
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
			g_fit = evaluateGene(i, chromos) * AWARD;		//awarding those with highest number of good genes
			((CIntegerGene) genes[i]).setLocalFitness(g_fit);
			fitness += g_fit;
		}
		
		if (MSPolicy.DEBUG)
			printGenes(chromos, fitness);
		
		return fitness;
	}
	
	void printGenes(IChromosome chromos, double fitness){
		Gene[] genes = chromos.getGenes();
		System.out.print("Fitness of each gene: ");
		for (int i = 0; i < genes.length; i++) {
			System.out.print(((CIntegerGene) genes[i]).getLocalFitness() + " | ");
		}
		System.out.println();
		System.out.println("\tChromosoma " + Monitor.chromosomeToString(chromos) + " con fit_value " + fitness);
		System.out.println();
	}
}
