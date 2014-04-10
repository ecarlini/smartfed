/*
Copyright 2014 ISTI-CNR
 
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

package it.cnr.isti.smartfed.metascheduler.test;


import it.cnr.isti.smartfed.test.ThreeTierBusinessApplication;

/**
 * This class extends the definition of a simple three tier business application:
 * web-fronted, web-backend and database.
 * @author Gaetano
 *
 */
public class ThreeTierBusinessApplicationMS extends ThreeTierBusinessApplication {
	
	/* NO MORE USED
	public ThreeTierBusinessApplicationMS(String[] places, String[] budgets, int frontendNumber, int backendNumber, int databaseNumber)
	{
		super(frontendNumber, backendNumber, databaseNumber);
		vertexFrontend.setPlace(places[0]);
		vertexFrontend.setBudget(Double.parseDouble(budgets[0]));
		
		vertexBackend.setPlace(places[1]);
		vertexBackend.setBudget(Double.parseDouble(budgets[1]));
		
		vertexDatabase.setPlace(places[2]);
		vertexDatabase.setBudget(Double.parseDouble(budgets[2]));
	}
	*/
	
	public ThreeTierBusinessApplicationMS(int userId, String[] places, String[] budgets, int frontendNumber, int backendNumber, int databaseNumber)
	{
		super(userId, frontendNumber, backendNumber, databaseNumber);
		vertexFrontend.setCountry(places[0]);
		vertexFrontend.setBudget(Double.parseDouble(budgets[0]));
		
		vertexBackend.setCountry(places[1]);
		vertexBackend.setBudget(Double.parseDouble(budgets[1]));
		
		vertexDatabase.setCountry(places[2]);
		vertexDatabase.setBudget(Double.parseDouble(budgets[2]));
	}
}
