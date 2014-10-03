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

package it.cnr.isti.smartfed.test;

import it.cnr.isti.smartfed.federation.application.Application;
import it.cnr.isti.smartfed.federation.application.ApplicationEdge;
import it.cnr.isti.smartfed.federation.application.ApplicationVertex;
import it.cnr.isti.smartfed.federation.application.CloudletProfile;
import it.cnr.isti.smartfed.federation.application.CloudletProvider;
import it.cnr.isti.smartfed.federation.resources.VmFactory.VmType;
import it.cnr.isti.smartfed.networking.SecuritySupport;

import java.util.ArrayList;

import org.cloudbus.cloudsim.Cloudlet;

/**
 * This class represents the definition of a simple three tier business application:
 * web-fronted, web-backend and database.
 * It is possible to define the cardinality of each tier at construction time.
 * @author carlini
 *
 */
public class ThreeTierBusinessApplication extends Application
{
	 // 20KB/req and 1000req/h
	public static final double DEFAULT_MRATE = 20;
	public static final double DEFAULT_MSIZE = 0.27;

	static private int DEFAULT_CLOUDLET_NUMBER = 1;
	
	// cloudlet profiles definition
	CloudletProfile profileDatabase = CloudletProfile.getDefault();
	CloudletProfile profileFronted = CloudletProfile.getDefault();
	CloudletProfile profileBackend = CloudletProfile.getDefault();

	protected ApplicationVertex vertexFrontend = null;
	protected ApplicationVertex vertexBackend = null;
	protected ApplicationVertex vertexDatabase = null;
	
	private ApplicationVertex createFrontend(int userId, int number)
	{
		ArrayList<Cloudlet> frontendList = new ArrayList<Cloudlet>();
		for (int i=0; i < number; i++)
		{
			Cloudlet c = CloudletProvider.get(profileFronted);
			frontendList.add(c);
		}
		return new ApplicationVertex("FrontEnd[Small]", userId, frontendList, VmType.SMALL);	 
	}
	
	private ApplicationVertex createBackend(int userId, int number)
	{
		ArrayList<Cloudlet> backendList = new ArrayList<Cloudlet>();
		for (int i=0; i<number; i++)
		{
			Cloudlet c = CloudletProvider.get(profileBackend);
			backendList.add(c);
		}	
		return new ApplicationVertex("BackEnd[Med]", userId, backendList, VmType.MEDIUM);	 
	}
	
	private ApplicationVertex createDatabase(int userId, int number){
		// Database tier
		ArrayList<Cloudlet> databaseList = new ArrayList<Cloudlet>();
		for (int i=0; i< number; i++)
		{
			Cloudlet c = CloudletProvider.get(profileDatabase);
			databaseList.add(c);
		}	
		return new ApplicationVertex("DB[Large]", userId, databaseList, VmType.LARGE);
	}
			
	public ThreeTierBusinessApplication(int userId)
	{
		vertexFrontend = createFrontend(userId, DEFAULT_CLOUDLET_NUMBER);
		vertexBackend = createBackend(userId, DEFAULT_CLOUDLET_NUMBER);
		vertexDatabase = createDatabase(userId, DEFAULT_CLOUDLET_NUMBER);		
		createConnections();
	}
	
	/**
	 * 
	 * @param userId
	 * @param frontendNumber
	 * @param backendNumber
	 * @param databaseNumber
	 */
	public ThreeTierBusinessApplication(int userId, int frontendNumber, int backendNumber, int databaseNumber) {
		vertexFrontend = createFrontend(userId, frontendNumber);
		vertexBackend = createBackend(userId, backendNumber);
		vertexDatabase = createDatabase(userId, databaseNumber);		
		createConnections();
	}
	

	private void createConnections(){
		// Add the vertexes to the graph
		this.addVertex(vertexFrontend);
		this.addVertex(vertexBackend);
		this.addVertex(vertexDatabase);

		// Network
		ApplicationEdge frontToBack = new ApplicationEdge(DEFAULT_MSIZE, DEFAULT_MRATE);
		ApplicationEdge backToDB = new ApplicationEdge(DEFAULT_MSIZE, DEFAULT_MRATE);

		this.addEdge(frontToBack, vertexFrontend, vertexBackend);
		this.addEdge(backToDB, vertexBackend, vertexDatabase);
	}
	
	
}
