package it.cnr.isti.smartfed.test;

import it.cnr.isti.smartfed.federation.application.Application;
import it.cnr.isti.smartfed.federation.application.ApplicationEdge;
import it.cnr.isti.smartfed.federation.application.ApplicationVertex;
import it.cnr.isti.smartfed.federation.resources.Country;
import it.cnr.isti.smartfed.federation.resources.ResourceCounter;
import it.cnr.isti.smartfed.federation.resources.VmFactory;
import it.cnr.isti.smartfed.federation.resources.VmTyped;
import it.cnr.isti.smartfed.federation.resources.VmFactory.VmType;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.File;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;
import org.workflowsim.Job;
import org.workflowsim.Task;
import org.workflowsim.WorkflowParser;
import org.workflowsim.clustering.BasicClustering;
import org.workflowsim.clustering.BlockClustering;
import org.workflowsim.clustering.HorizontalClustering;
import org.workflowsim.clustering.VerticalClustering;
import org.workflowsim.clustering.balancing.BalancedClustering;
import org.workflowsim.utils.ClusteringParameters;
import org.workflowsim.utils.DistributionGenerator;
import org.workflowsim.utils.OverheadParameters;
import org.workflowsim.utils.Parameters;
import org.workflowsim.utils.ReplicaCatalog;

public class WorkflowApplication extends Application
{
	public static String fileName = "Epigenomics_24";
	public String daxPath = "resources/" + fileName + ".xml";

	void setWorkflowSimConfig(){
		int vmNum = 20;//number of vms;
		Parameters.SchedulingAlgorithm sch_method = Parameters.SchedulingAlgorithm.MINMIN;
        Parameters.PlanningAlgorithm pln_method = Parameters.PlanningAlgorithm.INVALID;
        ReplicaCatalog.FileSystem file_system = ReplicaCatalog.FileSystem.SHARED;

        /**
         * clustering delay must be added, if you don't need it, you can set all the clustering
         * delay to be zero, but not null
         */
        Map<Integer, DistributionGenerator> clusteringDelay = new HashMap<Integer, DistributionGenerator>();
         
        int maxLevel = 11; // Montage has at most 11 horizontal levels 
        for (int level = 0; level < maxLevel; level++ ){
            DistributionGenerator cluster_delay = new DistributionGenerator(DistributionGenerator.DistributionFamily.WEIBULL, 10.0, 1.0);
            clusteringDelay.put(level, cluster_delay);//the clustering delay specified to each level is 1.0 seconds
        }
        // Add clustering delay to the overhead parameters
        OverheadParameters op = new OverheadParameters(0, null, null, null, clusteringDelay, 0);
        
        /**
         * You can only specify clusters.num or clusters.size
         * clusters.num is the number of clustered jobs per horizontal level
         * clusters.size is the number of tasks per clustered job
         * clusters.num * clusters.size = the number of tasks per horizontal level
         * Specifying the clusters.size = 2 means each job has two tasks
         */
       ClusteringParameters.ClusteringMethod method = ClusteringParameters.ClusteringMethod.HORIZONTAL;
       ClusteringParameters cp = new ClusteringParameters(1, 0, method, null); // this is for having a pipe (not really!)
        

        Parameters.init(vmNum, daxPath, null,
                null, op, cp, sch_method, pln_method,
                null, 0);
        ReplicaCatalog.init(file_system);
	}
	
	public WorkflowApplication(int userId, boolean clustering) {
		setWorkflowSimConfig();
		
		WorkflowParser parser = new WorkflowParser(userId, null, null, daxPath);
		parser.parse();
		List<Task> tasks = parser.getTaskList();
		
		if (clustering){
			List<Job> jobs = processClustering(tasks);
			build(userId, jobs);
		}
		else
			build(userId, tasks);
		
		
	}
	
	public List<Task> getTasksWithDepth(int depth)
	{
		List<Task> tasks = new ArrayList<Task>();
		if (depth < 0)
			return tasks;
		
		for (Cloudlet c: getAllCloudlets())
		{
			Task t = (Task) c;
			if (t.getDepth() == depth)
				tasks.add(t);
		}
		
		return tasks;
	}
	
	private Vm createSmallVm_NoID(int userId){
		return VmFactory.getDesiredVm(
					userId, 
					6502.18, 
					1, 
					new Double(1.7 * 1024 ).intValue(), // RAM: 1.7 GB
					new Long(1 * 1024 * 1024), // i assume at least 1MB p/s  
					new Long(160 * 1024) // DISK: 160 GB
					);
	}
	
	private Vm createXLargeVm_NoID(int userId){
		return VmFactory.getDesiredVm(
			userId, 
			5202.15 * 4, 
			4, 
			new Double(15 * 1024).intValue(), // 15 GB
			new Long(1 * 1024 * 1024), // i assume at least 1MB p/s  
			new Long(1690 * 1024) // 1690 GB
			);
	}
	
	private <T extends Cloudlet> void build(int userId, List<T> tasks){
		for (T t: tasks){
			List<Cloudlet> cloudlets = new ArrayList<>();
			cloudlets.add(t);
			Vm vm = null;
			if (t.getCloudletLength() > 50000)
				vm = createXLargeVm_NoID(userId);
			else 
				vm = createSmallVm_NoID(userId);
			
			ApplicationVertex v = new ApplicationVertex(userId, cloudlets, vm);
			v.setCountry(Country.Italy);
			v.setBudget(50);
			addVertex(v);
		}
		
		for (T t: tasks){
			ApplicationVertex base = this.getVertexForCloudlet(t);
			double outputSize = 0;
			double mrate = 1;
			List<File> files = ((Task) t).getFileList();
			for (File f: files){
				if (f.getType() == 2)//output as constructed by parser
					outputSize += f.getSize(); // this size is in bytes
			}
	
			outputSize = outputSize / 1024d; // obtaining KB as applicationEdge requires
			
			List<Task> childs = ((Task) t).getChildList();
			for (Task c: childs){
				ApplicationVertex child = this.getVertexForCloudlet(c);
				addEdge(new ApplicationEdge(outputSize, mrate), base, child);
			}
		}
		System.out.println(super.vertexSet().size() + "+" + super.getEdges().size());
	}
	
	public static void main(String[] args){
		int num_user = 1;   // number of grid users
        Calendar calendar = Calendar.getInstance();
        boolean trace_flag = false;  // mean trace events

        // Initialize the CloudSim library
        CloudSim.init(num_user, calendar, trace_flag);
        
		WorkflowApplication g;
		try {
			boolean taskClustering = true;
			g = new WorkflowApplication(0, taskClustering);
			String add = taskClustering == true ? "clust" : "";
			g.export("plots/" + fileName + add + ".dot");
			System.out.println(g);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	protected List<Job> processClustering(List<Task> tasks) {

		BasicClustering engine = null;
        ClusteringParameters params = Parameters.getClusteringParameters();


        switch (params.getClusteringMethod()) {
            /**
             * Perform Horizontal Clustering
             */
            case HORIZONTAL:
                /**
                 * if clusters.num is set in configuration file
                 */
                if (params.getClustersNum() != 0) {
                    engine = new HorizontalClustering(params.getClustersNum(), 0);
                } /**
                 * else if clusters.size is set in configuration file
                 */
                else if (params.getClustersSize() != 0) {
                    engine = new HorizontalClustering(0, params.getClustersSize());
                }/**
                 * else does no clustering
                 */
                else {
                }
                break;
            /**
             * Perform Vertical Clustering
             */
            case VERTICAL:
                int depth = 1;
                engine = new VerticalClustering(depth);
                break;
            /**
             * Perform Block Clustering
             */
            case BLOCK:
                engine = new BlockClustering(params.getClustersNum(), params.getClustersSize());
                break;
            /**
             * Perform Balanced Clustering
             */
            case BALANCED:
                engine = new BalancedClustering(params.getClustersNum());
                break;
            /**
             * By default, it does no clustering
             */
            default:
                engine = new BasicClustering();
        }
        engine.setTaskList(tasks);
        engine.run();
        return engine.getJobList();
    }
}

