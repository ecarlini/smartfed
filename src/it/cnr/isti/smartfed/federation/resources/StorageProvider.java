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

import it.cnr.isti.smartfed.federation.resources.StorageProfile.StorageParams;

import org.cloudbus.cloudsim.Storage;

public class StorageProvider 
{
	public Storage createStorage(StorageProfile profile)
	{
		Storage storage = null;
		
		try
		{
			Class clazz = Class.forName(profile.get(StorageParams.CLASS));
			Double capacity = Double.parseDouble(profile.get(StorageParams.CAPACITY));
			storage = (Storage)clazz.getDeclaredConstructor(Double.class).newInstance(capacity);
		}
		catch (Exception e)
		{
			// TODO: log the error
			e.printStackTrace();
		}
		
		return storage;
	}
}
