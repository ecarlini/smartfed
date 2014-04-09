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


import java.util.HashMap;
import java.util.Map;

public class FederationQueueProfile {
	
	private static final int DEFAULT_LENGTH = 10;
	private static final String DEFAULT_MODEL = "it.cnr.isti.smartfed.federation.application.interarrival.NormalModel";

	public enum QueueParams{
		INTER_ARRIVAL_MODEL(DEFAULT_MODEL),
		INTER_ARRIVAL_PARAMS(""),
		LENGTH(new Integer(DEFAULT_LENGTH).toString());
		
		private String def;		
		private QueueParams(String def)
		{
             this.def = def;
		}
	}
	
	private  Map<QueueParams, String> data;
	
	private FederationQueueProfile()
	{
		data = new HashMap<QueueParams, String>();
		for (QueueParams p : QueueParams.values())
		{
			data.put(p, p.def);
		}
	}

	public static FederationQueueProfile getDefault()
	{
		return new FederationQueueProfile();
	}
	
	public String get(QueueParams par)
	{
		return data.get(par);
	}
	
	public void set(QueueParams par, String value)
	{
		data.put(par, value);
	}
}
