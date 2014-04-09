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

package it.cnr.isti.smartfed.federation.application.interarrival;

import it.cnr.isti.smartfed.federation.application.Application;

public class NormalModel implements InterArrivalModelItf {

	private final long intervalAmongJobs = 100;
	
	@Override
	public Object[] getSchedulingTime(Application[] applications, String ...params) {
		
		long interval;
		
		if (params.length != 0){
			interval = Long.parseLong(params[0]);
		} else {
			interval = intervalAmongJobs;
		}
		
		long[] timestamps = new long[applications.length];
		long currentTimestamp = 0;
		for(int i=0;i<applications.length;i++){
			
			timestamps[i] = currentTimestamp;
			currentTimestamp += interval;
			
		}
		
		Object[] result =  new Object[2];
		result[0] = applications;
		result[1] = timestamps;
		
		return result;
	}

}
