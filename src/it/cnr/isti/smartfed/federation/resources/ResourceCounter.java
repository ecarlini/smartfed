package it.cnr.isti.smartfed.federation.resources;

public class ResourceCounter 
{
	private static int HOST_ID = 0;
	private static int VM_ID = 0;
	private static int CLOUDLET_ID = 0;
	private static int DATACENTER_ID = 0;
	
	public static int nextCloudletID()
	{
		return CLOUDLET_ID++;
	}
	
	public static int nextVmID()
	{
		return VM_ID++;
	}
	
	public static int nextHostID()
	{
		return HOST_ID++;
	}
	
	public static int nextDatacenterID()
	{
		return DATACENTER_ID++;
	}
	
	public static void reset()
	{
		HOST_ID = 0;
		VM_ID = 0;
		CLOUDLET_ID = 0;
		DATACENTER_ID = 0;
	}
}
