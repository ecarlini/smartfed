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

package it.cnr.isti.smartfed.networking;

/**
 * This class represents an Internet Link.
 * 
 * @author carlini
 *
 */
public class InternetLink
{
	private long bandwidth; // bps
	private double latency; // seconds
	private SecuritySupport security;
	
	
	public InternetLink(long bandwidth, double latency, SecuritySupport security)
	{
		this.bandwidth = bandwidth;
		this.latency = latency;
		this.security = security;
	}
	
	
	public long getBandwidth()
	{
		return bandwidth;
	}

	public void setBandwidth(long bandwidth)
	{
		this.bandwidth = bandwidth;
	}

	public double getLatency() {
		return latency;
	}

	public void setLatency(int latency)
	{
		this.latency = latency;
	}

	public SecuritySupport getSecurity()
	{
		return security;
	}

	public void setSecurity(SecuritySupport security)
	{
		this.security = security;
	}
}
