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

package it.cnr.isti.smartfed.federation;

import it.cnr.isti.smartfed.federation.FederationQueueProfile.QueueParams;
import it.cnr.isti.smartfed.federation.application.Application;
import it.cnr.isti.smartfed.federation.application.interarrival.InterArrivalModelItf;

import java.util.List;

public class FederationQueueProvider {

	
	public static FederationQueue createFederationQueue(FederationQueueProfile profile, Federation federation, 
			List<Application> applications){
		
		Application[] apps_array = new Application[applications.size()];
		applications.toArray(apps_array);
		
		String arrivalModelName = profile.get(QueueParams.INTER_ARRIVAL_MODEL);
		FederationQueue fq = null;
		
		try {
			
			InterArrivalModelItf arrivalModel = (InterArrivalModelItf) Class.forName(arrivalModelName).newInstance();
			Object[] ret = arrivalModel.getSchedulingTime(apps_array);
			
			fq = new FederationQueue(federation, ret);
			
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		return fq;
		
	}
	
	public static FederationQueue getFederationQueue(FederationQueueProfile profile, Federation federation, List<Application> applications){
		return createFederationQueue(profile, federation, applications);
	}
	
}
