package it.cnr.isti.smartfed.test;

import it.cnr.isti.smartfed.federation.Federation;
import it.cnr.isti.smartfed.federation.application.ApplicationEdge;
import it.cnr.isti.smartfed.federation.application.ApplicationVertex;
import it.cnr.isti.smartfed.federation.resources.FederationDatacenter;

import java.util.List;
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
			//System.out.println("Executing "+next_task.getCloudletId()+" depth: "+next_task.getDepth());			
			//System.out.println("Number of children: "+next_task.getChildList().size());
			
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
			
			double latency = 0;
			double transfer_time = 0;
			if (edge != null)
			{
				latency = edge.getLatency(); //TODO	
				FederationDatacenter fd = Federation.findDatacenter(dcs, next_task.getResourceId());
				transfer_time = (edge.getMessageLength() * 1024) / fd.getMSCharacteristics().getHighestBw();
				
				System.out.println("Message Length (KB): " + edge.getMessageLength());
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
}
