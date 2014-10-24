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

package it.cnr.isti.smartfed.federation.application;


import it.cnr.isti.smartfed.networking.SecuritySupport;

import org.cloudbus.cloudsim.Vm;
import org.jgrapht.graph.DefaultEdge;

public class ApplicationEdge extends DefaultEdge
{
	private static final long serialVersionUID = 1423234l;
	
	/* requirements */
	private double bandwidth;
	private SecuritySupport security;
	private double latency;
	private double messageLength;
	private double messageRate;
	
	@Deprecated
	public ApplicationEdge(double bandwidth, SecuritySupport security, double latency)
	{
		this.bandwidth = bandwidth;
		this.security = security;
		this.latency = latency;
	}
	
	/**
	 * 
	 * @param mlenght message length in KB sent in this link 
	 * @param mlenght message rate in Hz sent in this link 
	 * @param security
	 * @param latency
	 */
	public ApplicationEdge(double mlength, double mrate, SecuritySupport security, double latency)
	{
		this.messageLength = mlength;
		this.messageRate = mrate;
		this.bandwidth = messageLength / messageRate;
		this.security = security;
		this.latency = latency;
	}
	
	/**
	 * 
	 * @param mlenght message length in KB sent in this link 
	 * @param mlenght message rate in Hz sent in this link 
	 * @param latency
	 */
	public ApplicationEdge(double mlength, double mrate)
	{
		this(mlength,mrate,SecuritySupport.NO, 0);
	}
	
	public ApplicationEdge(double mlength, double mrate, double latency)
	{
		this(mlength,mrate,SecuritySupport.NO, latency);
	}
	
	/**
	 * Estimated required bandwidth of this link in KB/s
	 * @return
	 */
	public double getBandwidth()
	{
		return this.bandwidth;
	}

	
	public double getMessageLength()
	{
		return this.messageLength;
	}

	public SecuritySupport getSecurity()
	{
		return this.security;
	}

	public double getLatency()
	{
		return this.latency;
	}
	
	public double getMBperHour(){
		double res = this.bandwidth / 1024 * 3600;
		return res;
	}
	
	public String toString(){
		return "(" + super.getSource() + "->" + super.getTarget() + ")";
	}
	
	public Vm getSourceVm(){
		Vm vm;
		try {
			ApplicationVertex v = (ApplicationVertex) super.getSource();
			vm = v.getVms().get(0);
		}
		catch (Exception e){
			vm = null;
		}
		return vm;
	}
	
	public Vm getTargetVm(){
		Vm vm;
		try {
			ApplicationVertex v = (ApplicationVertex) super.getTarget();
			vm =  v.getVms().get(0);
		}
		catch (Exception e){
			vm = null;
		}
		return vm;
	}
	
	public int getSourceVmId(){
		int id;
		try {
			ApplicationVertex v = (ApplicationVertex) super.getSource();
			id = v.getVms().get(0).getId();
		}
		catch (Exception e){
			id = -1;
		}
		return id;
	}
	
	public int getTargetVmId(){
		int id;
		try {
			ApplicationVertex v = (ApplicationVertex) super.getTarget();
			id = v.getVms().get(0).getId();
		}
		catch (Exception e){
			id = -1;
		}
		return id;
	}
}
