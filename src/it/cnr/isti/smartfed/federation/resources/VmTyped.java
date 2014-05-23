package it.cnr.isti.smartfed.federation.resources;

import it.cnr.isti.smartfed.federation.resources.VmFactory.VmType;

import org.cloudbus.cloudsim.CloudletScheduler;
import org.cloudbus.cloudsim.Vm;

/**
 * 
 * @author gae
 *
 */
public class VmTyped extends Vm {

	public VmTyped(int id, int userId, double mips, int numberOfPes, int ram,
			long bw, long size, String vmm, CloudletScheduler cloudletScheduler) {
		
		super(id, userId, mips, numberOfPes, ram, bw, size, vmm, cloudletScheduler);
	}
	
	public VmTyped(Vm vm, VmType vm_type){
		super(	vm.getId(),
				vm.getUserId(), 
				vm.getMips(), 
				vm.getNumberOfPes(), 
				vm.getRam(),
				vm.getBw(),
				vm.getSize(),
				vm.getVmm(), 
				vm.getCloudletScheduler());
		setType(vm_type);
	}

	VmType type = VmType.CUSTOM;
	
	public VmType getType() {
		return type;
	}

	public void setType(VmType type) {
		this.type = type;
	}

	public String toString(){
		String s = "";
		s+= "RAM:" + this.getRam() + " PES:" +  this.getNumberOfPes();
		s+= " type:" + this.getType();
		return s;
	}
	

}
