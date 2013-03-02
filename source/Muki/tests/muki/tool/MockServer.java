/**
 *  Copyright 2013 Gabriel Casarini
 *  
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package muki.tool;

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.webapp.WebAppContext;

public class MockServer {

	private static String TEMP_DIR = TestHelper.TEMP_DIR;

	/**
	 * This class runs a mock web service. This is used to test the Cocoa clients. This
	 * server requieres that the web application (*.war) is generated at TEMP_DIR + "/webapp".
	 * Steps:
	 * 1) To generate the application, run one of the test cases in JavaCompilationDeploymentTestCase. Its setup
	 * generates the clases and the full deployment file. 
	 * 
	 * 2) After that, run this mock to start the server.
	 * 
	 * 3) Verify that the server is up and running. Type following URL in a browser and check the result.
	 * This seems to work only with Firefox; Safari doesn't show the result!!
	 * - URL: http://localhost:8089/webapp/store/pathGetOperation1
	 * - Expected result: "getOperation1"
	 * 
	 * 4) Run the Cocoa client.
	 */
	public static void main(String[] args) throws Exception {
		MockServer server = new MockServer();
		server.run();
	}
	
	private void run() throws Exception {
		WebAppContext webapp = new WebAppContext();
		webapp.setContextPath("/webapp");
		webapp.setWar(TEMP_DIR + "/webapp");
		
		Server newServer = new Server();
		Connector connector = new SelectChannelConnector();
		connector.setPort(8089);
		newServer.setConnectors(new Connector[]{connector});
		newServer.setHandler(webapp);
		newServer.start();
		newServer.join();
	}

}
