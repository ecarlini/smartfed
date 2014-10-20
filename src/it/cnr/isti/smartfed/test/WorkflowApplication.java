package it.cnr.isti.smartfed.test;

import it.cnr.isti.smartfed.federation.application.Application;
import it.cnr.isti.smartfed.federation.application.ApplicationEdge;
import it.cnr.isti.smartfed.federation.application.ApplicationVertex;
import it.cnr.isti.smartfed.federation.resources.ResourceCounter;
import it.cnr.isti.smartfed.federation.resources.VmFactory;
import it.cnr.isti.smartfed.federation.resources.VmFactory.VmType;
import it.cnr.isti.smartfed.federation.resources.VmTyped;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.File;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;
import org.jgrapht.Graph;
import org.workflowsim.Task;
import org.workflowsim.WorkflowParser;
import org.workflowsim.utils.ReplicaCatalog;

public class WorkflowApplication extends Application{
	public String daxPath = "/home/gae/.workspace/workflowsim-1.0/config/dax/Montage_25.xml";
	
	public WorkflowApplication(int userId){
		ReplicaCatalog.FileSystem file_system = ReplicaCatalog.FileSystem.SHARED;
		ReplicaCatalog.init(file_system);
		
		WorkflowParser parser = new WorkflowParser(userId, null, null, daxPath);
		parser.parse();
		List<Task> tasks = parser.getTaskList();
		
		for (Task t: tasks){
			List<Cloudlet> cloudlets = new ArrayList<>();
			cloudlets.add(t);
			Vm vm = VmFactory.getDesiredVm(userId, 
					8022, 
					2, 
					new Double(7.5 * 1024).intValue(), // 7.5 GB
					new Long(1 * 1024 * 1024), // i assume at least 1MB p/s  
					new Long(850 * 1024));
			ApplicationVertex v = new ApplicationVertex(userId, cloudlets, vm);
			v.setCountry("Italy");
			v.setBudget(50);
			addVertex(v);
		}
		
		for (Task t: tasks){
			ApplicationVertex base = this.getVertexForCloudlet(t);
			long outputSize = 0;
			double mrate = 1;
			List<File> files = t.getFileList();
			for (File f: files){
				if (f.getType() == 2)//output as constructed by parser
					outputSize += files.size();
			}
	
			List<Task> childs = t.getChildList();
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
        
		WorkflowApplication g = new WorkflowApplication(0);
		System.out.println(g);
	}
}
