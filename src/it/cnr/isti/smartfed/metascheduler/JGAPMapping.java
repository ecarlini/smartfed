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

import it.cnr.isti.smartfed.federation.FederationLog;
import it.cnr.isti.smartfed.metascheduler.resources.MSApplicationNode;
import it.cnr.isti.smartfed.metascheduler.resources.iface.IMSProvider;

import java.util.List;

import org.jgap.Chromosome;
import org.jgap.Configuration;
import org.jgap.DefaultFitnessEvaluator;
import org.jgap.Gene;
import org.jgap.Genotype;
import org.jgap.IChromosome;
import org.jgap.InvalidConfigurationException;
import org.jgap.event.EventManager;
import org.jgap.impl.BestChromosomesSelector;
import org.jgap.impl.ChromosomePool;
import org.jgap.impl.CrossoverOperator;
import org.jgap.impl.MutationOperator;
import org.jgap.util.ICloneable;


public class JGAPMapping {

	public static int POP_SIZE = 50;
	public static int EVOLUTION_STEP = 150;
	
	public static final int INTERNAL_SOLUTION_NUMBER = 10;
	public static final int SOLUTION_NUMBER = 3;
	public static int MUTATION = 0;
	public static double CROSSOVER = 0;
	public static Genotype population = null;
	static Configuration conf = null;
	
	public static Solution[] execute(MSExternalState state, List<MSPolicy> policy, long randomSeed)
	{
		List<IMSProvider> providerList = state.getProviders();
		
		Solution sol[] = new Solution[SOLUTION_NUMBER];
		try {
			Configuration conf = new InternalDefaultConfiguration();
			// making gene
			int providerNumber = providerList.size();
			List<MSApplicationNode> nodes = state.getApplication().getNodes();
			Gene[] genes = new Gene[nodes.size()];
			for (int i = 0; i < nodes.size(); i++){
				// precondition: providerList is ordered
				int firstInteger = providerList.get(0).getID();
				int lastInteger =  providerList.get(providerList.size()-1).getID();
				// genes[i] = new CIntegerGene(conf, 0, providerNumber-1);
				genes[i] = new CIntegerGene(conf, firstInteger, lastInteger);
			}

			IChromosome sampleCh = new Chromosome(conf, genes);
			conf.setSampleChromosome(sampleCh);
			conf.setPopulationSize(JGAPMapping.POP_SIZE);
			
			conf.setRandomGenerator(new CRandGenerator(providerNumber, randomSeed));
			//conf.setRandomGenerator(new CRandGenerator(providerNumber));
			
			MSFitnessFunction fitness = new MSFitnessFunction(state, policy);
			conf.setFitnessFunction(fitness);

			Genotype.setStaticConfiguration(conf);

			population = Genotype.randomInitialGenotype(conf);
			System.out.println("*** Starting metascheduler evolution...");
			List<String> message = population.evolve(new Monitor(JGAPMapping.EVOLUTION_STEP));
			System.out.println("*** Stopping metascheduler evolution...");
			
			for(String s : message){
				FederationLog.print(s);
			}
			
			// IChromosome bestSolutionSoFar = population.getPopulation().determineFittestChromosome();
			List<IChromosome> list = population.getFittestChromosomes(JGAPMapping.INTERNAL_SOLUTION_NUMBER);
			IChromosome[] array = new IChromosome[list.size()];
			int k=0;
			for (IChromosome ic: list){ // converting list to array - not using list.toArray(array); because it will call wrong constructor for genes
				array[k] = ic;
				k++;
			}
			
			k = 0;
			
			/*
			boolean[] acceptable = selectingSatisfactorySolutions(array);
			for (int i=0; i<acceptable.length && k < JGAPMapping.SOLUTION_NUMBER; i++){
				if (acceptable[i]){
					Gene[] mygenes = array[i].getGenes();
					sol[k] = new Solution(array[i], nodes);
					sol[k].chromosome.setGenes(mygenes);
					sol[k].setCostAmount(calculateCostSolution(nodes, providerList, mygenes));
					k++;
				}
			}
			
			if (k != JGAPMapping.SOLUTION_NUMBER)
				System.out.println("\n\nAlert!!!! Not all solution were satisfactory\n");
			*/	
			
			if (k == 0){
				for (int i=0; i<JGAPMapping.SOLUTION_NUMBER && i<array.length; i++){
					Gene[] mygenes = array[i].getGenes();
					sol[i] = new Solution(array[i], nodes);
					sol[i].chromosome.setGenes(mygenes);
					// sol[i].setCostAmount(calculateCostSolution(nodes, providerList, array[i]));
					sol[i].setCostAmount(calculateCostSolution(array[i]));
				}
			}
			
			Configuration.reset();
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}
		return sol;
	}
	
	
	private static boolean[] selectingSatisfactorySolutions(IChromosome[] solarray){
		boolean[] accept = new boolean[solarray.length];
		for (int i=0; i<accept.length; i++){
			Gene[] mygenes = solarray[i].getGenes();
			boolean scarta = false;
			for (int j=0; j<mygenes.length && !scarta; j++){
				if (((CIntegerGene) mygenes[j]).getLocalFitness() == 0.0){
					scarta = true;
				}
			}
			if (scarta == false){
				accept[i] = true;
			}
			else {
				accept[i] = false;
			}
		}
		return accept;
	}
	
	// private static double calculateCostSolution(List<MSApplicationNode> nodes,  List<IMSProvider> providerList, IChromosome c){
	 private static double calculateCostSolution(IChromosome c){
		Gene [] genes = c.getGenes();
		double tmp = 0;
		for (int j=0; j<genes.length; j++){
			// IMSProvider provider = MSProviderAdapter.findProviderById(providerList, (int) genes[j].getAllele());
			tmp += ((CIntegerGene) genes[j]).getAllocationCost();
			// tmp += CostPerVmConstraint.vmCost(nodes.get(j), provider, c);
		}
		return tmp;
	}
	
}

class InternalDefaultConfiguration extends Configuration implements ICloneable {
	private static final long serialVersionUID = 1L;

	public InternalDefaultConfiguration() {
		super("", "");
		
		if (JGAPMapping.MUTATION == 0 || JGAPMapping.CROSSOVER == 0){
			// throw new RuntimeException();
			JGAPMapping.MUTATION = 10;
			JGAPMapping.CROSSOVER = 0.35;
		}
		
		BestChromosomesSelector bestSelector;
		try {
			// setBreeder(new GABreeder());
			bestSelector = new BestChromosomesSelector(this, 0.90d);
			bestSelector.setDoubletteChromosomesAllowed(true);
			this.addNaturalSelector(bestSelector, false);
			this.setPreservFittestIndividual(true);
			// setMinimumPopSizePercent(0);
			// setSelectFromPrevGen(1.0d);
			setKeepPopulationSizeConstant(true);
			
			this.setEventManager(new EventManager());
			addGeneticOperator(new CrossoverOperator(this, JGAPMapping.CROSSOVER));
			addGeneticOperator(new MutationOperator(this, JGAPMapping.MUTATION)); // 0 disable the mutation
			this.setFitnessEvaluator(new DefaultFitnessEvaluator());
			this.setChromosomePool(new ChromosomePool());
			
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}

}
