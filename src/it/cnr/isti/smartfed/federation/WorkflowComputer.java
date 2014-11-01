package it.cnr.isti.smartfed.federation;

import it.cnr.isti.smartfed.federation.application.ApplicationEdge;
import it.cnr.isti.smartfed.federation.application.ApplicationVertex;
import it.cnr.isti.smartfed.federation.application.WorkflowApplication;
import it.cnr.isti.smartfed.federation.resources.FederationDatacenter;
import it.cnr.isti.smartfed.networking.InternetEstimator;
import it.cnr.isti.smartfed.networking.InternetLink;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.workflowsim.Task;

public class WorkflowComputer 
{	
	
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
				
				if (out_edges.size() <= 0) // last node
				{
					total_time = offset_time + task_time;
				}
				else
				{
					for (ApplicationEdge ae: out_edges)
					{
						double edge_time = edgeTime(ae, workflow, t, dcs, internet);
						total_time = offset_time + task_time + edge_time;
						edgeTimeMap.put(ae, total_time);
					}
				}
				
				// System.out.println("Task "+t.getCloudletId()+ " offset: "+offset_time+ " task time: "+task_time+ " total_time: "+total_time);
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
		ApplicationVertex target_vertex = workflow.getEdgeTarget(edge);
		Task target_task = (Task) workflow.getCloudletFromVertex(target_vertex);
		
		FederationDatacenter dc_source = Federation.findDatacenter(dcs, t.getResourceId());
		FederationDatacenter dc_target = Federation.findDatacenter(dcs, target_task.getResourceId());
		double latency = 0;
		if (dc_source.getId() != dc_target.getId())
		{
			InternetLink link = null;
			try { link = internet.getInternetLink(dc_source, dc_target);} 
			catch (Exception e) {e.printStackTrace();}
					
			latency = link.getLatency();
		}
		
		double transfer_time = (edge.getMessageLength() * 1024) / dc_source.getMSCharacteristics().getHighestBw();
		
		//System.out.println("--- Length: "+edge.getMessageLength() * 1024);
		//System.out.println("--- Band:   "+dc_source.getMSCharacteristics().getHighestBw());
		
		
		return latency + transfer_time;
	}

	public static double getFlowCostPerHour(Allocation allocation, double completionTime)
	{
		double total = CostComputer.actualCost(allocation);
		double net = CostComputer.actualNetCost(allocation);
		
		// cost of only resources per hour
		double resourcesPerHour = total - net;
		
		// times we execute a workflow per hour
		double workflowPerHour = 3600d / completionTime;
		
		// cost of network for one run of the workflow
		double netOneRun = net / 3600;
		
		// cost of only network per hour
		double netPerHour = netOneRun * workflowPerHour;
		
		return resourcesPerHour + netPerHour;
	}
}
