package it.src.isti.smartfed.federation;

import it.cnr.isti.smartfed.federation.application.Application;
import it.cnr.isti.smartfed.federation.application.WorkflowApplication;
import it.cnr.isti.smartfed.federation.mapping.AbstractAllocator;
import it.cnr.isti.smartfed.federation.mapping.MappingSolution;
import it.cnr.isti.smartfed.federation.resources.FederationDatacenter;
import it.cnr.isti.smartfed.federation.resources.FederationDatacenterFactory;
import it.cnr.isti.smartfed.federation.resources.FederationDatacenterProfile;
import it.cnr.isti.smartfed.federation.resources.FederationDatacenterProfile.DatacenterParams;
import it.cnr.isti.smartfed.federation.resources.HostFactory;
import it.cnr.isti.smartfed.federation.resources.HostProfile;
import it.cnr.isti.smartfed.federation.resources.HostProfile.HostParams;
import it.cnr.isti.smartfed.networking.InternetEstimator;
import it.cnr.isti.smartfed.test.Experiment;
import it.cnr.isti.smartfed.test.ExperimentDistance;
import it.cnr.isti.smartfed.test.InterfaceDataSet;
import it.cnr.isti.smartfed.test.TestResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.junit.Test;
import org.workflowsim.Task;

public class WorkflowComputerTest
{

	@Test
	public void foo() throws IOException
	{
		CloudSim.init(1, Calendar.getInstance(), true);
		
		AbstractAllocator allocator = new MyAllocator();
		ExperimentDistance e = new ExperimentDistance();
		e.setDataset(new MyDataset());
		e.run(allocator);
				
		double tc = TestResult.getCompletion().getMean();
		System.out.println(tc);
	}
	
	class MyAllocator extends AbstractAllocator
	{		
		@Override
		public MappingSolution[] findAllocation(Application application) 
		{
			MappingSolution ms = new MappingSolution(application);
			List<FederationDatacenter> dcs = getMonitoringHub().getView();
					
			ms.set(application.getAllCloudlets().get(0), dcs.get(0));
			ms.set(application.getAllCloudlets().get(1), dcs.get(1));
			
			this.solution = ms;
			return new MappingSolution[]{ms};
		}
		
	}

	class MyDataset implements InterfaceDataSet
	{

		@Override
		public List<FederationDatacenter> createDatacenters() {
			List<FederationDatacenter> list = new ArrayList<FederationDatacenter>();
			List<Storage> storageList = new ArrayList<Storage>(); // if empty, no SAN attached
		
			
			//************* DATACENTER 1 ***************** //
			//****
			
			// create the virtual processor (PE)
			List<Pe> peList1 = new ArrayList<Pe>();
			int mips = 25000;
			for (int i=0; i < 2; i++)
				peList1.add(new Pe(i, new PeProvisionerSimple(mips)));
			
			
			// create the hosts for DC 1
			List<Host> hostList1 = new ArrayList<Host>();
			HostProfile prof1 = HostProfile.getDefault();
			prof1.set(HostParams.RAM_AMOUNT_MB, 2*1024+"");
			prof1.set(HostParams.STORAGE_MB, "1000");
			prof1.set(HostParams.BW_AMOUNT, "1024");

			for (int k=0; k<100; k++)
			{
				hostList1.add(HostFactory.get(prof1, peList1));
			}
			
			// Creating DC1
			FederationDatacenterProfile fcp1 = FederationDatacenterProfile.getDefault();
			fcp1.set(DatacenterParams.COUNTRY, "Italy");
			fcp1.set(DatacenterParams.COST_PER_MEM, "0");
			fcp1.set(DatacenterParams.COST_PER_STORAGE, "0");
			fcp1.set(DatacenterParams.COST_PER_SEC, "0");
			fcp1.set(DatacenterParams.COST_PER_BW, "0");
			
			FederationDatacenter dc1 = FederationDatacenterFactory.get(fcp1, hostList1, storageList);
			
			
			
			//************* DATACENTER 1 ***************** //
			//****
			
			// create the virtual processor (PE)
			List<Pe> peList2 = new ArrayList<Pe>();
			mips = 10000;
			for (int i=0; i < 2; i++)
				peList2.add(new Pe(i, new PeProvisionerSimple(mips)));
			
			
			// create the hosts for DC 2
			List<Host> hostList2 = new ArrayList<Host>();
			HostProfile prof2 = HostProfile.getDefault();
			prof2.set(HostParams.RAM_AMOUNT_MB, 2*1024+"");
			prof2.set(HostParams.STORAGE_MB, "1000");
			prof2.set(HostParams.BW_AMOUNT, "1024");

			for (int k=0; k<100; k++)
			{
				hostList2.add(HostFactory.get(prof2, peList2));
			}
			
				
			// Creating DC2
			FederationDatacenterProfile fcp2 = FederationDatacenterProfile.getDefault();
			fcp2.set(DatacenterParams.COUNTRY, "Italy");
			fcp2.set(DatacenterParams.COST_PER_MEM, "0");
			fcp2.set(DatacenterParams.COST_PER_STORAGE, "0");
			fcp2.set(DatacenterParams.COST_PER_SEC, "0");
			fcp2.set(DatacenterParams.COST_PER_BW, "0");
			
			FederationDatacenter dc2 = FederationDatacenterFactory.get(fcp2, hostList2, storageList);
			
			
			list.add(dc1);
			list.add(dc2);
			return list;
		}

		@Override
		public List<Application> createApplications(int userId) 
		{
			// create thw workflow
			Task t1 = new Task(1, 0);
			t1.setDepth(1);
			
			Task t2 = new Task(2, 0);
			t2.setDepth(2);
			
			t1.addChild(t2);
			t2.addParent(t1);
			
			List<Task> list = new ArrayList<Task>(2);
			list.add(t1);
			list.add(t2);
			
			WorkflowApplication workflow = new WorkflowApplication(list, 1, false);
			
			List<Application> apps = new ArrayList<Application>(1);
			apps.add(workflow);
			return apps;
		}

		@Override
		public InternetEstimator createInternetEstimator(List<FederationDatacenter> datacenters) 
		{
			return new InternetEstimator(datacenters);
		}
		
	}
}
