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
