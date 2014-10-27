package it.cnr.isti.smartfed.test;

import it.cnr.isti.smartfed.federation.Federation;
import it.cnr.isti.smartfed.federation.application.ApplicationEdge;
import it.cnr.isti.smartfed.federation.application.ApplicationVertex;
import it.cnr.isti.smartfed.federation.resources.FederationDatacenter;
import it.cnr.isti.smartfed.networking.InternetEstimator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.workflowsim.Task;

public class WorkflowComputer 
{	
	
	public static double getPipeCompletionTime(WorkflowApplication workflow, List<FederationDatacenter> dcs)
	{
		System.out.println("\n\nFREE BEER! Now that I have your attention, be warned: " +
				"\nThe completion time is done considering the workflow as a PIPELINE\n\n");
		
		int depth = 1;
		Task next_task = workflow.getTasksWithDepth(depth).get(0);
		double tc = 0;
		
		
		while (next_task != null)
		{
			System.out.println("Executing "+next_task.getCloudletId()+" depth: "+next_task.getDepth());			
			System.out.println("Number of children: "+next_task.getChildList().size());
			
			// service time of the node
			long filesize = next_task.getCloudletLength();	
			double expected_service_time = filesize / workflow.getVertexForCloudlet(next_task).getAssociatedVm(next_task).getMips();
			double cloudsim_service_time = next_task.getActualCPUTime();
			
					
			System.out.println("Cloudlet Length: "+filesize);
			System.out.println("Cloudsim Service time: "+cloudsim_service_time+" Expected: "+expected_service_time);
			
			// service time of the link
			ApplicationVertex av = workflow.getVertexForCloudlet(next_task);
			Set<ApplicationEdge> outedges = workflow.outgoingEdgesOf(av);
			
			ApplicationEdge edge = outedges.iterator().hasNext() ? outedges.iterator().next() : null;
			System.out.println("Numero di edge: "+outedges.size());
			
			double latency = 0;
			double transfer_time = 0;
			if (edge != null)
			{
				latency = edge.getLatency(); //TODO	
			
				FederationDatacenter fd = Federation.findDatacenter(dcs, next_task.getResourceId());	
				transfer_time = (edge.getMessageLength() / 1024) / 40;//fd.getMSCharacteristics().getHighestBw();
				
				System.out.println("Message Length: "+edge.getMessageLength()/1024);
				System.out.println("Highest bw: "+fd.getMSCharacteristics().getHighestBw());
				System.out.println("Transfer Time: "+transfer_time);
			}
			
			// add to the global tc
			tc = tc + expected_service_time + latency + transfer_time;
			
			// prepare next round
			depth ++;
			List<Task> tasks = workflow.getTasksWithDepth(depth);
			next_task = tasks.size() == 1 ? tasks.get(0) : null;
			System.out.println("\n");
		}
		
		System.out.println("Tempo di completamento: "+tc);
		return tc;
	}

	public static double getFlowCompletionTime(WorkflowApplication workflow, List<FederationDatacenter> dcs, InternetEstimator internet)
	{
		int depth = 1;
		List<Task> tasks = workflow.getTasksWithDepth(depth);
		Map<ApplicationEdge, Double> edgeTimeMap = new HashMap<ApplicationEdge, Double>();
		double total_time = 0;
		
		while (tasks.size() != 0)
		{
			for (Task t: tasks)
			{
				ApplicationVertex av = workflow.getVertexForCloudlet(t);
				
				// check for the entering edges to compute the time of 
				Set<ApplicationEdge> in_edges = workflow.incomingEdgesOf(av);
				double offset_time = 0;
				for (ApplicationEdge ae: in_edges)
				{
					double time = edgeTimeMap.get(ae);
					if (time > offset_time)
						offset_time = time;
				}
				
				// compute the time of the task here
				double task_time = taskTime(t, workflow);
				
				// set the time for the outer edges
				Set<ApplicationEdge> out_edges = workflow.outgoingEdgesOf(av);
				for (ApplicationEdge ae: out_edges)
				{
					double edge_time = edgeTime(ae, workflow, t, dcs, internet);
					total_time = offset_time + task_time + edge_time;
					edgeTimeMap.put(ae, total_time);
				}	
			}
			
			depth ++;
			tasks = workflow.getTasksWithDepth(depth);
		}
		
		System.out.println("Total time: "+total_time);
		return total_time;
	}

	private static double taskTime(Task t, WorkflowApplication workflow)
	{
		long filesize = t.getCloudletLength();	
		double expected_service_time = filesize / workflow.getVertexForCloudlet(t).getAssociatedVm(t).getMips();
		double cloudsim_service_time = t.getActualCPUTime();
		
		return expected_service_time;
	}
	
	private static double edgeTime(ApplicationEdge edge, WorkflowApplication workflow, Task t, List<FederationDatacenter> dcs, InternetEstimator internet)
	{
		
		ApplicationVertex av;
		t.getChildList();
		double latency = edge.getLatency(); // TODO from InternetEstimator
		
		FederationDatacenter dc_source = Federation.findDatacenter(dcs, t.getResourceId());
		FederationDatacenter dc_target = null;
		// double latency = internet.getInternetLink(dc_source, dc_target).getLatency();
			
		double transfer_time = (edge.getMessageLength() / 1024) / 40;
		
		return latency + transfer_time;
	}
}
