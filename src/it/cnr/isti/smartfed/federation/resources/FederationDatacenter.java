/*
Copyright 2013 ISTI-CNR
 
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

package it.cnr.isti.smartfed.federation.resources;

import it.cnr.isti.smartfed.federation.CostComputer;
import it.cnr.isti.smartfed.federation.FederationLog;
import it.cnr.isti.smartfed.federation.resources.VmFactory.VmType;
import it.cnr.isti.smartfed.federation.utils.UtilityPrint;

import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletScheduler;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.HostDynamicWorkload;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicy;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.core.SimEvent;

public class FederationDatacenter extends Datacenter implements Comparable<FederationDatacenter> {

	public FederationDatacenter(String name, DatacenterCharacteristics characteristics, 
			VmAllocationPolicy vmAllocationPolicy, List<Storage> storageList, double schedulingInterval)
			throws Exception 
	{
		super(name, characteristics, vmAllocationPolicy, storageList, schedulingInterval);
	}

	public String getDatacenterRepresentation(){
		long ram = 0;
		long net = 0;
		long netTot = 0;
		long mips = 0;
		long storage = 0;
		String  result = new String();
		List<Host> hostlist = this.getHostList();
		List<Vm> l = null;
		for (int i = 0; i < hostlist.size(); i++) {
			if (hostlist.get(i) instanceof HostDynamicWorkload){
				HostDynamicWorkload h = (HostDynamicWorkload) hostlist.get(i);
				ram += h.getRam()- h.getUtilizationOfRam();
				//net += hostlist.get(i).getBw()- hostlist.get(i).getUtilizationOfBw();
				net += h.getUtilizationOfBw();
				netTot += h.getBw();
				storage += h.getStorage();
				mips += h.getTotalMips()- h.getUtilizationMips();
			}
			else {
				Host h = (Host) hostlist.get(i);
				ram += h.getRam();
				l = h.getVmList();
			}
		}
		result += "@@@@@@@@@@@@@@ Datacenter " + this.getId() + " @@@@@@@@@@@ " + hostlist.size() + " host\n";
		result += "Location: " + ((DatacenterCharacteristicsMS) super.getCharacteristics()).getCountry() + "\n";
		result += "VMs: ";
		if (l != null)
			for (Vm vm : l)
				result +=  vm.getId() + "\n";
		else 
			result += "none" + "\n";
		result += "RAM: " + ram + "\n";
		result += "NET used: " + net + "\n";
		result += "NET tot: " + netTot + "\n";
		result += "STORAGE: " + storage + "\n";
		result += "MIPS: " + mips + "\n";
		return result;
	}
	
	public String getDatacenterCharacteristicString(){
		return ((DatacenterCharacteristicsMS) super.getCharacteristics()).toString();
	}
	
	public String toStringDetail()
	{
		StringBuilder sb = new StringBuilder();
		DatacenterCharacteristicsMS chars = this.getMSCharacteristics();
		sb.append("location:").append(chars.getCountry()).append(",");
		
		// host description
		sb.append("host-num:").append(this.getHostList().size()).append(",");
		Host host = this.getHostList().get(0);
		if (host != null)
		{
			sb.append("host-desc:{");
			sb.append(UtilityPrint.toStringDetail(host));
			sb.append("}").append(",");
		}
		
		
		// custom costs
		sb.append("cost-custom:").append("{");
		sb.append("ram:").append(chars.getCostPerMem()).append(",");
		sb.append("storage:").append(chars.getCostPerStorage()).append(",");
		sb.append("mips:").append(chars.getCostPerMi()).append(",");
		sb.append("bw:").append(chars.getCostPerBw()).append(",");
		sb.append("cps:").append(chars.getCostPerSecond()).append("}").append(",");
		
		// specific per vm costs
		double[] types = chars.getCostVmTypes();
		sb.append("cost-vm:{");
		String prefix = "";
		for (int i=0; i<types.length; i++)
		{
			sb.append(prefix);
			prefix = ",";
			sb.append(types[i]);
		}
		sb.append("}");

		return sb.toString();
	}
	
	@Override
	public String toString() {
		return "FederationDatacenter [id: " + this.getId() +", " + this.getName() +"]"; // + getDatacenterCharacteristic();
	}
	
	public DatacenterCharacteristicsMS getMSCharacteristics(){
		return (DatacenterCharacteristicsMS) super.getCharacteristics();
	}
	
	@Override
	public int compareTo(FederationDatacenter o) {
		double thiscost = this.getCharacteristics().getCostPerMem();
		double ocost = o.getCharacteristics().getCostPerMem();
		if (thiscost > ocost)
			return 1;
		if (thiscost == ocost)
			return 0;
		return -1;
	}
	
	// no changes from superclass, actually
	protected void processCloudletSubmit(SimEvent ev, boolean ack) {
		updateCloudletProcessing();

		try {
			// gets the Cloudlet object
			Cloudlet cl = (Cloudlet) ev.getData();

			// checks whether this Cloudlet has finished or not
			if (cl.isFinished()) {
				Log.printLine("Already finished");
				String name = CloudSim.getEntityName(cl.getUserId());
				Log.printLine(getName() + ": Warning - Cloudlet #" + cl.getCloudletId() + " owned by " + name
						+ " is already completed/finished.");
				Log.printLine("Therefore, it is not being executed again");
				Log.printLine();

				// NOTE: If a Cloudlet has finished, then it won't be processed.
				// So, if ack is required, this method sends back a result.
				// If ack is not required, this method don't send back a result.
				// Hence, this might cause CloudSim to be hanged since waiting
				// for this Cloudlet back.
				if (ack) {
					int[] data = new int[3];
					data[0] = getId();
					data[1] = cl.getCloudletId();
					data[2] = CloudSimTags.FALSE;

					// unique tag = operation tag
					int tag = CloudSimTags.CLOUDLET_SUBMIT_ACK;
					sendNow(cl.getUserId(), tag, data);
				}

				sendNow(cl.getUserId(), CloudSimTags.CLOUDLET_RETURN, cl);

				return;
			}

			// process this Cloudlet to this CloudResource
			cl.setResourceParameter(getId(), getCharacteristics().getCostPerSecond(), getCharacteristics().getCostPerBw());
			int userId = cl.getUserId();
			int vmId = cl.getVmId();

			// time to transfer the files
			double fileTransferTime = predictFileTransferTime(cl.getRequiredFiles());

			Host host = getVmAllocationPolicy().getHost(vmId, userId);
			Vm vm = host.getVm(vmId, userId);
			CloudletScheduler scheduler = vm.getCloudletScheduler();
			double estimatedFinishTime = scheduler.cloudletSubmit(cl, fileTransferTime);
			FederationLog.timeLog("Estimated finish time for cloudlet " + cl.getCloudletId() + estimatedFinishTime);

			// if this cloudlet is in the exec queue
			if (estimatedFinishTime > 0.0 && !Double.isInfinite(estimatedFinishTime)) {
				estimatedFinishTime += fileTransferTime;
				send(getId(), estimatedFinishTime, CloudSimTags.VM_DATACENTER_EVENT);
			}

			if (ack) {
				int[] data = new int[3];
				data[0] = getId();
				data[1] = cl.getCloudletId();
				data[2] = CloudSimTags.TRUE;

				// unique tag = operation tag
				int tag = CloudSimTags.CLOUDLET_SUBMIT_ACK;
				sendNow(cl.getUserId(), tag, data);
			}
		} catch (ClassCastException c) {
			Log.printLine(getName() + ".processCloudletSubmit(): " + "ClassCastException error.");
			c.printStackTrace();
		} catch (Exception e) {
			Log.printLine(getName() + ".processCloudletSubmit(): " + "Exception error.");
			e.printStackTrace();
		}

		checkCloudletCompletion();
	}

	/**
	 * Returns the debts for the given user.
	 * @param user_id
	 * @return
	 */
	public Double getDebtsForUser(Integer user_id)
	{
		return this.getDebts().get(user_id);
	}
	
	@Override
	public void startEntity()
	{
		// remove the unwanted log printing
		Log.disable();
		super.startEntity();
		Log.enable();
	}
	
	@Override
	public void shutdownEntity()
	{
		// remove the unwanted log printing
		Log.disable();
		super.shutdownEntity();
		Log.enable();
	}
	
	/**
	 * Process the event for an User/Broker who wants to create a VM in this Datacenter. This
	 * Datacenter will then send the status back to the User/Broker.
	 * 
	 * @param ev a Sim_event object
	 * @param ack the ack
	 * @pre ev != null
	 * @post $none
	 */
	protected void processVmCreate(SimEvent ev, boolean ack) {
		VmTyped vm; 
		try {
			vm = (VmTyped) ev.getData();
		}
		catch (ClassCastException e){
			Vm generic_vm = (Vm) ev.getData();
			vm = new VmTyped(generic_vm, VmType.CUSTOM);
		}

		boolean result = getVmAllocationPolicy().allocateHostForVm(vm);

		if (ack) {
			int[] data = new int[3];
			data[0] = getId();
			data[1] = vm.getId();

			if (result) {
				data[2] = CloudSimTags.TRUE;
			} else {
				data[2] = CloudSimTags.FALSE;
			}
			send(vm.getUserId(), 0.1, CloudSimTags.VM_CREATE_ACK, data);
		}

		if (result) {
			double amount = 0.0;
			double myamount = 0.0;
			if (getDebts().containsKey(vm.getUserId())) {
				amount = getDebts().get(vm.getUserId());
			}
			
			// cloudsim code
			// amount += getCharacteristics().getCostPerMem() * vm.getRam();
			// amount += getCharacteristics().getCostPerStorage() * vm.getSize();
			
			// our code
			myamount = CostComputer.singleVmCost(vm, vm.getType(), this);
			amount += myamount;
			
			getDebts().put(vm.getUserId(), amount);

			getVmList().add(vm);

			if (vm.isBeingInstantiated()) {
				vm.setBeingInstantiated(false);
			}

			vm.updateVmProcessing(CloudSim.clock(), getVmAllocationPolicy().getHost(vm).getVmScheduler()
					.getAllocatedMipsForVm(vm));
		}

	}
	
	/**
	 * Prints the debts.
	 */
	public void printDebts() {
		Log.printLine("*****Datacenter: " + getName() + "*****");
		Log.printLine("User id\t\tDebt");

		Set<Integer> keys = getDebts().keySet();
		Iterator<Integer> iter = keys.iterator();
		DecimalFormat df = new DecimalFormat("#.###");
		while (iter.hasNext()) {
			int key = iter.next();
			double value = getDebts().get(key);
			Log.printLine(key + "\t\t" + df.format(value));
		}
		
	}

}
