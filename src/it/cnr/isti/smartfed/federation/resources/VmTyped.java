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

	VmType type = VmType.CUSTOM;
	
	public VmTyped(int id, int userId, double mips, int numberOfPes, int ram,
			long bw, long size, String vmm, CloudletScheduler cloudletScheduler, VmType vm_type) {
		
		super(id, userId, mips, numberOfPes, ram, bw, size, vmm, cloudletScheduler);
		type = vm_type;
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
