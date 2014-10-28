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

import java.math.RoundingMode;
import java.text.DecimalFormat;

import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.core.CloudSim;

public class FederationLog extends Log 
{		
	
	private static boolean debug = false;
	
	public static void disable()
	{
		debug = false;
	}
	
	public static void print(Object message)
	{
		println(String.valueOf(message));
	}
	
	public static void print(String message)
	{
		debugLog(message);
	}
	
	public static void println(String message)
	{
		debugLog(message + "\n");
	}
	
	public static void setDebug(boolean flag)
	{
		debug = flag;
	}
	
	public static void debugLog(String message)
	{
		if (debug)
			printLine("[SmartFed] "+ message);
	}
	
	public static void timeLog(String message)
	{
		if (debug)
			printLine("[SmartFed "+getSimTime()+"] "+ message);
	}
	
	public static void timeLogDebug(String message)
	{
		if (debug)
			printLine("[SmartFed "+getSimTime()+"] "+ message);
	}
	
	private static String getSimTime()
	{
	    DecimalFormat df = new DecimalFormat("0.###");
	    df.setRoundingMode(RoundingMode.DOWN);
	    return df.format(CloudSim.clock());
	}
}
