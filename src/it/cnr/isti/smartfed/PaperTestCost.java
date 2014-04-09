package it.cnr.isti.smartfed;

import it.cnr.isti.smartfed.federation.FederationLog;
import it.cnr.isti.smartfed.federation.application.Application;
import it.cnr.isti.smartfed.federation.mapping.AbstractAllocator;
import it.cnr.isti.smartfed.federation.mapping.GeneticAllocator;
import it.cnr.isti.smartfed.federation.mapping.GreedyAllocator;
import it.cnr.isti.smartfed.federation.mapping.RandomAllocator;
import it.cnr.isti.smartfed.federation.resources.FederationDatacenter;
import it.cnr.isti.smartfed.test.DataSet;
import it.cnr.isti.smartfed.test.Experiment;
import it.cnr.isti.smartfed.test.TestResult;
import it.cnr.isti.smartfed.test.csv.ResultBean;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;
/*
 * TODO:
 * - review the cost (absolute value is too high)
 */

public class PaperTestCost
{
	public static void main (String[] args) throws IOException
	{
		FederationLog.setDebug(false);
		
		List<AbstractAllocator> allocators = new ArrayList<AbstractAllocator>(3);
		AbstractAllocator randomAllocator = new RandomAllocator();
		AbstractAllocator geneticAllocator = new GeneticAllocator();
		AbstractAllocator greedyAllocator = new GreedyAllocator();
		allocators.add(randomAllocator);
		allocators.add(geneticAllocator);
		allocators.add(greedyAllocator);
							
		
		ICsvBeanWriter beanWriter = new CsvBeanWriter(new FileWriter("plots/data.csv"),
				CsvPreference.STANDARD_PREFERENCE);
		
		beanWriter.writeHeader(ResultBean.header);

		int[] numOfDatacenter = {50};//{100,200,300,400,500,600,700,800,900,1000};
		int numOfVertex = 3;
		int[] numOfCloudlets = {3,6,9,12};
		int repetitions = 1;
		
		ResultBean result = new ResultBean();
		
		for (int z=0; z<numOfCloudlets.length; z++)
		{
			result.setCloudlets(numOfCloudlets[z]);
			
			for (int i=0; i<numOfDatacenter.length; i++)
			{
				result.setDatacenters(numOfDatacenter[i]);
				
				// int numHost = numOfCloudlets[z] * numOfDatacenter[i] * 2;
				int numHost = 100  * numOfDatacenter[i];
				PaperDataset data = new PaperDataset(numOfVertex, numOfCloudlets[z], numOfDatacenter[i], numHost, i);
				
				for (AbstractAllocator allocator: allocators)
				{
					Experiment exp = new Experiment(allocator, data);
					
					for (int k=0; k<repetitions; k++)
					{				
						exp.run();
						exp.setRandomSeed(k);
					}
					
					if (allocator instanceof GreedyAllocator)
					{
						result.setGreedyCost(TestResult.getCost().getMean());
						result.setGreedyFailures((int)TestResult.getFailures().getN());
						result.setGreedyDifference((int)TestResult.getVmDifference().getMean());
					}
					
					if (allocator instanceof GeneticAllocator)
					{
						result.setGeneticCost(TestResult.getCost().getMean());
						result.setGeneticFailures((int)TestResult.getFailures().getN());
						result.setGeneticDifference((int)TestResult.getVmDifference().getMean());
					}
					
					if (allocator instanceof RandomAllocator)
					{
						result.setRandomCost(TestResult.getCost().getMean());
						result.setRandomFailures((int)TestResult.getFailures().getN());
						result.setRandomDifference((int)TestResult.getVmDifference().getMean());
					}

					TestResult.reset();
				}
				
				// here write the csv
				beanWriter.write(result, ResultBean.header, ResultBean.processors);
			}
		}
		
		beanWriter.close();
		
	}
}

class PaperDataset extends DataSet
{	
	private int numVertex;
	private long seed;
	
	public PaperDataset(int numVertex, int numberOfCloudlets, int numOfDatacenter, int numHost)
	{
		super(numberOfCloudlets, numOfDatacenter, numHost);
		this.seed = System.currentTimeMillis();
		this.numVertex = numVertex;
	}
	
	public PaperDataset(int numVertex, int numberOfCloudlets, int numOfDatacenter, int numHost, long seed)
	{
		super(numberOfCloudlets, numOfDatacenter, numHost);
		this.seed = seed;
		this.numVertex = numVertex;
	}


	@Override
	public List<FederationDatacenter> createDatacenters() 
	{
		DatacenterGenerator dg = new DatacenterGenerator();
		dg.resetSeed(this.seed * 15);
		return dg.getDatacenters(numOfDatacenters, numHost);
	}
	
	@Override
	public List<Application> createApplications(int userId) 
	{
		ApplicationGenerator ag = new ApplicationGenerator();
		ag.resetSeed(this.seed + 13);
		Application app = ag.getApplication(userId, numVertex, this.numberOfCloudlets);
		List<Application> list = new ArrayList<>(1);
		list.add(app);
		return list;
	}
}


