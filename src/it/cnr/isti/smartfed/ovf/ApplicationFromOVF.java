package it.cnr.isti.smartfed.ovf;

import it.cnr.isti.smartfed.federation.application.Application;
import it.cnr.isti.smartfed.federation.application.ApplicationEdge;
import it.cnr.isti.smartfed.federation.application.ApplicationVertex;
import it.cnr.isti.smartfed.federation.application.CloudletProfile;
import it.cnr.isti.smartfed.federation.application.CloudletProvider;
import it.cnr.isti.smartfed.federation.resources.VmFactory;
import it.cnr.isti.smartfed.federation.resources.VmFactory.VmType;
import it.cnr.isti.smartfed.networking.SecuritySupport;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Vm;
import org.ow2.contrail.common.ParserManager;
import org.ow2.contrail.common.implementation.application.ApplianceDescriptor;
import org.ow2.contrail.common.implementation.application.ApplicationDescriptor;
import org.ow2.contrail.common.implementation.ovf.Disk;
import org.ow2.contrail.federation.federationcore.aem.mapping.ApplicationGraph;
import org.ow2.contrail.federation.federationcore.aem.mapping.ContrailEdge;
import org.ow2.contrail.federation.federationcore.exception.ApplicationOperationException;
import org.ow2.contrail.federation.federationcore.utils.GraphUtils;

public class ApplicationFromOVF {

	static ApplicationVertex createTypedSimulationVertex(int cloudletNumber, VmType vmtype){
		ArrayList<Cloudlet> frontendList = new ArrayList<Cloudlet>();
		CloudletProfile profile = CloudletProfile.getDefault();

		for (int i=0; i < cloudletNumber; i++)
		{
			Cloudlet c = CloudletProvider.get(profile);
			frontendList.add(c);
		}
		ApplicationVertex vertex = new ApplicationVertex(0, frontendList, vmtype);
		return vertex;
	}

	static ApplicationVertex createCustomSimulationVertex(int cloudletNumber, Vm vm){
		ArrayList<Cloudlet> frontendList = new ArrayList<Cloudlet>();
		CloudletProfile profile = CloudletProfile.getDefault();

		for (int i=0; i < cloudletNumber; i++)
		{
			Cloudlet c = CloudletProvider.get(profile);
			frontendList.add(c);
		}
		ApplicationVertex vertex = new ApplicationVertex(0, frontendList, vm);
		return vertex;
	}

	static ApplicationEdge createAppEdgeFromContrailEdge(ContrailEdge e){
		System.out.println(e.getEdgeName() + " " + e.getWeight());
		return new ApplicationEdge(1024, SecuritySupport.BASE, 1000);
	}


	public static Application getApplicationFromOVF(URI ovfFile){
		ParserManager pm = null;
		try {
			pm = new ParserManager(ovfFile);
		} 
		catch (Exception e1) {
			e1.printStackTrace();
			return null;
		}
		ApplicationDescriptor ad = pm.getApplication();
		ApplicationGraph<String, ContrailEdge> appGraphDesc = null;
		try {
			appGraphDesc = GraphUtils.MakeGraph(-1, ad);
		} catch (ApplicationOperationException e1) {
			e1.printStackTrace();
		}

		Application simApp = new Application();

		System.out.println(appGraphDesc.name);
		Collection<ApplianceDescriptor> appl =  appGraphDesc.getAppliances();

		for (ApplianceDescriptor a : appl){		
			ApplicationVertex av = null;
			int ramMB = (int) a.getRequiredMemory().getVirtualQuantity()/1024;
			int pes = (int) a.getRequiredCPU().getVirtualQuantity();

			long diskMB = 0;
			Collection<Disk> disks = a.getDisks();
			for (Disk d: disks)
				diskMB += (d.getFile().getSize() /1024);


			System.out.println("Memory: " + a.getRequiredMemory().getVirtualQuantity()/1024 + "MB ");
			System.out.println("CPU: " + pes);
			System.out.println("storage " + diskMB);

			double default_mips = 2000;
			long default_bandMB = 2000;
			Vm vm = VmFactory.getCustomVm(0, default_mips, pes, ramMB, default_bandMB, diskMB);
			av = createCustomSimulationVertex(1, vm);
			
			av.setName(a.getID());
			simApp.addVertex(av);
		}

		Set<ApplicationVertex> avSet = simApp.vertexSet();
		ApplicationVertex[] av1 = new ApplicationVertex[avSet.size()];
		int i=0;
		for (ApplicationVertex a : avSet){
			av1[i] = a;
			i++;
		}
		
		ApplianceDescriptor[] a1 = new ApplianceDescriptor[appl.size()];
		i=0;
		for (ApplianceDescriptor a : appl){
			a1[i] = a;
			i++;
		}

		for (i=0; i< a1.length-1; i++){
			for (int j=i+1; j< a1.length; j++){
				ContrailEdge e = appGraphDesc.getEdge(a1[i].getID(), a1[j].getID());
				if (e != null){
					ApplicationEdge ae = createAppEdgeFromContrailEdge(e);
					simApp.addEdge(ae, av1[i], av1[j]); // devo avere un mapping tra i vertici nelle due rappresentazioni
				}
			}
		}

		String res = new String();

		System.out.println(appGraphDesc);
		System.out.println(simApp);
		System.out.println(simApp.getAllVms().get(0).getSize());
		System.out.println(simApp.getAllVms().get(1).getSize());
		return simApp;
	}

	public static ApplicationVertex myswitch(long ram){
		ApplicationVertex av = null;
		if ((ram) <= 1740){
			av = createTypedSimulationVertex(1, VmType.SMALL);
		}
		else if (ram <= 3840){
			av = createTypedSimulationVertex(1, VmType.MEDIUM);
		}
		else if (ram <= 7680) {
			av = createTypedSimulationVertex(1, VmType.LARGE);
		}
		else {
			av = createTypedSimulationVertex(1, VmType.XLARGE);
		}
		return av;
	}
	
	public static void main(String[] args) throws Exception {
		final URI ovf = new URI("resources/ovf-2vms-vep2.0.ovf");
		ApplicationFromOVF.getApplicationFromOVF(ovf);
	}

}
