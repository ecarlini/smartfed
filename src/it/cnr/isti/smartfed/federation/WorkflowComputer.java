package it.cnr.isti.smartfed.federation;

import it.cnr.isti.smartfed.federation.application.ApplicationEdge;
import it.cnr.isti.smartfed.federation.application.ApplicationVertex;
import it.cnr.isti.smartfed.federation.application.WorkflowApplication;
import it.cnr.isti.smartfed.federation.resources.FederationDatacenter;
import it.cnr.isti.smartfed.networking.InternetEstimator;

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
		ApplicationVertex target_vertex = workflow.getEdgeTarget(edge);
		Task target_task = (Task) workflow.getCloudletFromVertex(target_vertex);
		
		FederationDatacenter dc_source = Federation.findDatacenter(dcs, t.getResourceId());
		FederationDatacenter dc_target = Federation.findDatacenter(dcs, target_task.getResourceId());
		double latency = 0;
		if (dc_source.getId() != dc_target.getId())
		{
			// latency = internet.getInternetLink(dc_source, dc_target).getLatency();
			latency = 100;
		}
		
		double transfer_time = (edge.getMessageLength() * 1024) / dc_source.getMSCharacteristics().getHighestBw();
		
		return latency + transfer_time;
	}
}
