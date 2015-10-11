/**
 *  Copyright 2015 Gabriel Casarini
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

import static org.junit.Assert.*;

import java.io.File;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.ProjectHelper;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.webapp.WebAppContext;

import muki.tool.model.Project;
import muki.tool.ExecutionResult;
import muki.tool.IOUtility;
import muki.tool.ModelUtility;
import muki.tool.MukiGenerator;

public class JavaCompilationDeploymentTestCase {

	private static String TEMP_DIR = TestHelper.TEMP_DIR;
	private static String URL_RESOURCE1 = "http://localhost:8089/webapp/controller1";
	private static String URL_RESOURCE2 = "http://localhost:8089/webapp/controller2";
	private HttpClient httpClient;
	private Server server;

	/**
	 * Generate and compile application
	 */
	@BeforeClass
	public static void oneTimeSetUp() throws Exception {
		IOUtility io = new IOUtility();
		ModelUtility utility = new ModelUtility();
		String srcDirectory = TEMP_DIR + "/generated/src";
		String binDirectory = TEMP_DIR + "/generated/bin";
		io.createDirectory(srcDirectory);
		io.createDirectory(binDirectory);
		String fileName = io.getAbsolutePathForLocalResource("/tests/store-project-ok.xml");
		Project project = utility.openProject(fileName);
		
		// Step 1: Generate Java classes for the model provided
		ExecutionResult result = new ExecutionResult();
		MukiGenerator generator = new MukiGenerator();
		generator.setOutputDirectory(srcDirectory);
		generator.setProject(project);
		generator.generateJava(result);
		
		// Calculate absolute path of the project
		String path = io.getAbsolutePathForLocalResource("/muki");
		int i = path.indexOf("/bin");
		String eclipseProjectPath = path.substring(0, i);;

		// Step 2: Configure Ant script for invoking from here
		String buildFile = io.getAbsolutePathForLocalResource("/tests/build-compilation-tests.xml");
		String webFile = io.getAbsolutePathForLocalResource("/tests/web.xml");

		DefaultLogger consoleLogger = new DefaultLogger();
		consoleLogger.setErrorPrintStream(System.err);
		consoleLogger.setOutputPrintStream(System.out);
		consoleLogger.setMessageOutputLevel(org.apache.tools.ant.Project.MSG_INFO);

		org.apache.tools.ant.Project antProject = new org.apache.tools.ant.Project();
		antProject.setUserProperty("ant.file", buildFile);
		antProject.setUserProperty("project.dir", eclipseProjectPath);
		antProject.setUserProperty("temp.dir", TEMP_DIR);
		antProject.setUserProperty("web.file", webFile);
		antProject.setUserProperty("webapp.dir", TEMP_DIR + "/webapp");
		antProject.addBuildListener(consoleLogger);
		antProject.fireBuildStarted();
		antProject.init();
		
		ProjectHelper helper = ProjectHelper.getProjectHelper();
		antProject.addReference("ant.projectHelper", helper);
		helper.parse(antProject, new File(buildFile));

		// Step 3: Copy mock templates to the src directory
		antProject.executeTarget("copy-mocks");

		// Step 4: Compile generated classes
		antProject.executeTarget("compile");

		// Step 5: Generate directory for the web application
		antProject.executeTarget("generate-webapp");
    }
	
	@AfterClass
	public static void oneTimeTearDown() throws Exception {
    }

	@Before
	public void setUp() throws Exception {
		/**
		*/
		WebAppContext webapp = new WebAppContext();
		webapp.setContextPath("/webapp");
		webapp.setWar(TEMP_DIR + "/webapp");
		
		Server newServer = new Server();
		Connector connector = new SelectChannelConnector();
		connector.setPort(8089);
		newServer.setConnectors(new Connector[]{connector});
		newServer.setHandler(webapp);
		newServer.start();
		this.setServer(newServer);
		this.setHttpClient(new HttpClient());
	}

	@After
	public void tearDown() throws Exception {
		if(this.getServer() != null) {
			this.getServer().stop();
		}
	}

	@Test
	public void testGetOperation1() throws Exception {		
		String url = URL_RESOURCE1 + "/pathGetOperation1";
		GetMethod method = new GetMethod(url);
		
		int statusCode = this.getHttpClient().executeMethod(method);
		assertTrue("Method failed: " + method.getStatusLine(), statusCode == HttpStatus.SC_OK);
		byte[] responseBody = method.getResponseBody();
		String actualResponse = new String(responseBody);
		String expectedResponse = "getOperation1";
		assertEquals(expectedResponse, actualResponse);
	}
	
	@Test
	public void testGetOperation2() throws Exception {		
		String url = URL_RESOURCE2 + "/pathGetOperation2/2";
		GetMethod method = new GetMethod(url);
		
		int statusCode = this.getHttpClient().executeMethod(method);
		assertTrue("Method failed: " + method.getStatusLine(), statusCode == HttpStatus.SC_OK);
		byte[] responseBody = method.getResponseBody();
		String actualResponse = new String(responseBody);
		String expectedResponse = "2";
		assertEquals(expectedResponse, actualResponse);
	}
	
	@Test
	public void testGetOperation3() throws Exception {	
		String url = URL_RESOURCE2 + "/pathGetOperation3/3/myname";
		GetMethod method = new GetMethod(url);
		
		int statusCode = this.getHttpClient().executeMethod(method);
		assertTrue("Method failed: " + method.getStatusLine(), statusCode == HttpStatus.SC_OK);
		byte[] responseBody = method.getResponseBody();
		String actualResponse = new String(responseBody);
		String expectedResponse = "3-myname";
		assertEquals(expectedResponse, actualResponse);
	}
	
	@Test
	public void testGetOperation4() throws Exception {	
		String url = URL_RESOURCE1 + "/pathGetOperation4/?date=2010-07-08";
		GetMethod method = new GetMethod(url);
		
		int statusCode = this.getHttpClient().executeMethod(method);
		assertTrue("Method failed: " + method.getStatusLine(), statusCode == HttpStatus.SC_OK);
		byte[] responseBody = method.getResponseBody();
		String actualResponse = new String(responseBody);
		String expectedResponse = "2010-07-08";
		assertEquals(expectedResponse, actualResponse);
	}
	
	@Test
	public void testGetOperation5() throws Exception {	
		String url = URL_RESOURCE1 + "/pathGetOperation5/?date=2010-07-08&page=5";
		GetMethod method = new GetMethod(url);
		
		int statusCode = this.getHttpClient().executeMethod(method);
		assertTrue("Method failed: " + method.getStatusLine(), statusCode == HttpStatus.SC_OK);
		byte[] responseBody = method.getResponseBody();
		String actualResponse = new String(responseBody);
		String expectedResponse = "2010-07-08-5";
		assertEquals(expectedResponse, actualResponse);
	}
	
	@Test
	public void testGetOperation6() throws Exception {	
		String url = URL_RESOURCE1 + "/pathGetOperation6/6?page=2";
		GetMethod method = new GetMethod(url);
		
		int statusCode = this.getHttpClient().executeMethod(method);
		assertTrue("Method failed: " + method.getStatusLine(), statusCode == HttpStatus.SC_OK);
		byte[] responseBody = method.getResponseBody();
		String actualResponse = new String(responseBody);
		String expectedResponse = "6-2";
		assertEquals(expectedResponse, actualResponse);
	}
	
	@Test
	public void testGetOperation7Xml() throws Exception {	
		String url = URL_RESOURCE1 + "/pathGetOperation7Xml/";
		GetMethod method = new GetMethod(url);
		
		int statusCode = this.getHttpClient().executeMethod(method);
		assertTrue("Method failed: " + method.getStatusLine(), statusCode == HttpStatus.SC_OK);
		byte[] responseBody = method.getResponseBody();
		String xmlResponse = new String(responseBody);
		assertTrue(xmlResponse.indexOf("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>") > -1);
		assertTrue(xmlResponse.indexOf("<track") > -1);
		assertTrue(xmlResponse.indexOf("price=\"21.3\"") > -1);
		assertTrue(xmlResponse.indexOf("newRelease=\"true\"") > -1);
		assertTrue(xmlResponse.indexOf("durationInSeconds=\"90\"") > -1);
		assertTrue(xmlResponse.indexOf("catalogId=\"1\"") > -1);
		assertTrue(xmlResponse.indexOf("<title>getOperation7</title>") > -1);
		assertTrue(xmlResponse.indexOf("</track>") > -1);
	}
	
	@Test
	public void testGetOperation7Json() throws Exception {	
		String url = URL_RESOURCE1 + "/pathGetOperation7Json/";
		GetMethod method = new GetMethod(url);
		
		int statusCode = this.getHttpClient().executeMethod(method);
		assertTrue("Method failed: " + method.getStatusLine(), statusCode == HttpStatus.SC_OK);
		byte[] responseBody = method.getResponseBody();
		String jsonResponse = new String(responseBody);
		assertTrue(jsonResponse.indexOf("{") > -1);
		assertTrue(jsonResponse.indexOf("\"price\":21.3") > -1);
		assertTrue(jsonResponse.indexOf("\"newRelease\":true") > -1);
		assertTrue(jsonResponse.indexOf("\"durationInSeconds\":90") > -1);
		assertTrue(jsonResponse.indexOf("\"catalogId\":1") > -1);
		assertTrue(jsonResponse.indexOf("\"title\":\"getOperation7\"") > -1);
	}
	
	/**
	 * In this case the cd contains tracks.
	 */
	@Test
	public void testGetOperation8XmlCdWithTracks() throws Exception {	
		String url = URL_RESOURCE1 + "/pathGetOperation8Xml/8";
		GetMethod method = new GetMethod(url);
		
		int statusCode = this.getHttpClient().executeMethod(method);
		assertTrue("Method failed: " + method.getStatusLine(), statusCode == HttpStatus.SC_OK);
		byte[] responseBody = method.getResponseBody();
		String xmlResponse = new String(responseBody);
		assertTrue(xmlResponse.indexOf("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>") > -1);
		assertTrue(xmlResponse.indexOf("<title>8</title>") > -1);
		assertTrue(xmlResponse.indexOf("<tracks>") > -1);
		assertTrue(xmlResponse.indexOf("</track>") > -1);
	}
	
	/**
	 * In this case the cd is empty.
	 */
	@Test
	public void testGetOperation8XmlCdWithoutTracks() throws Exception {	
		String url = URL_RESOURCE1 + "/pathGetOperation8Xml/emptyCd";
		GetMethod method = new GetMethod(url);
		
		int statusCode = this.getHttpClient().executeMethod(method);
		assertTrue("Method failed: " + method.getStatusLine(), statusCode == HttpStatus.SC_OK);
		byte[] responseBody = method.getResponseBody();
		String xmlResponse = new String(responseBody);
		assertTrue(xmlResponse.indexOf("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>") > -1);
		assertTrue(xmlResponse.indexOf("<tracks/>") > -1);
	}
	
	/**
	 * In this case the cd contains tracks.
	 * {
	 * 	"title":"8",
	 * 	"artist":"Mozart",
	 * 	"mainTrack":{
	 * 		"title":"My Track 1",
	 * 		"durationInSeconds":90,
	 * 		"catalogId":1,
	 * 		"newRelease":true,
	 * 		"price":21.3
	 * 	},
	 * 	"tracks":[
	 * 		{"title":"My Track 1","durationInSeconds":90,"catalogId":1,"newRelease":true,"price":21.3},
	 * 		{"title":"My Track 2","durationInSeconds":65,"catalogId":1,"newRelease":true,"price":15.7}
	 * 	]
	 * }
	 */
	@Test
	public void testGetOperation8JsonCdWithTracks() throws Exception {	
		String url = URL_RESOURCE1 + "/pathGetOperation8Json/8";
		GetMethod method = new GetMethod(url);
		
		int statusCode = this.getHttpClient().executeMethod(method);
		assertTrue("Method failed: " + method.getStatusLine(), statusCode == HttpStatus.SC_OK);
		byte[] responseBody = method.getResponseBody();
		String jsonResponse = new String(responseBody);
		assertTrue(jsonResponse.indexOf("\"artist\":\"Mozart\"") > -1);
		assertTrue(jsonResponse.indexOf("\"mainTrack\":{") > -1);
		assertTrue(jsonResponse.indexOf("\"price\":21.3") > -1);
	}

	/**
	 * In this case the cd is empty.
	 * {
	 * 	"title":"Empty Cd",
	 * 	"artist":"No artist",
	 * 	"mainTrack":{
	 * 		"title":"My Track 1",
	 * 		"durationInSeconds":90,
	 * 		"catalogId":1,"newRelease":true,
	 * 		"price":21.3
	 * 		},
	 * 	"tracks":[]
	 * }
	 */
	@Test
	public void testGetOperation8JsonCdWithoutTracks() throws Exception {	
		String url = URL_RESOURCE1 + "/pathGetOperation8Json/emptyCd";
		GetMethod method = new GetMethod(url);
		
		int statusCode = this.getHttpClient().executeMethod(method);
		assertTrue("Method failed: " + method.getStatusLine(), statusCode == HttpStatus.SC_OK);
		byte[] responseBody = method.getResponseBody();
		String jsonResponse = new String(responseBody);
		assertTrue(jsonResponse.indexOf("\"title\":\"Empty Cd\"") > -1);
		assertTrue(jsonResponse.indexOf("\"tracks\":[]") > -1);
	}
	
	@Test
	public void testGetOperation9Xml() throws Exception {	
		String url = URL_RESOURCE1 + "/pathGetOperation9Xml/9/myname";
		GetMethod method = new GetMethod(url);
		
		int statusCode = this.getHttpClient().executeMethod(method);
		assertTrue("Method failed: " + method.getStatusLine(), statusCode == HttpStatus.SC_OK);
		byte[] responseBody = method.getResponseBody();
		String xmlResponse = new String(responseBody);
		assertTrue(xmlResponse.indexOf("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>") > -1);
		assertTrue(xmlResponse.indexOf("<title>9-myname</title>") > -1);
	}
	
	/**
	 * {
	 * 	"title":"9-myname",
	 * 	"durationInSeconds":90,
	 * 	"catalogId":1,
	 * 	"newRelease":true,
	 * 	"price":21.3
	 * }
	 */
	@Test
	public void testGetOperation9Json() throws Exception {	
		String url = URL_RESOURCE1 + "/pathGetOperation9Json/9/myname";
		GetMethod method = new GetMethod(url);
		
		int statusCode = this.getHttpClient().executeMethod(method);
		assertTrue("Method failed: " + method.getStatusLine(), statusCode == HttpStatus.SC_OK);
		byte[] responseBody = method.getResponseBody();
		String jsonResponse = new String(responseBody);
		assertTrue(jsonResponse.indexOf("\"durationInSeconds\":90,") > -1);
		assertTrue(jsonResponse.indexOf("\"title\":\"9-myname\"") > -1);
	}
		
	@Test
	public void testGetOperation10Xml() throws Exception {	
		String url = URL_RESOURCE1 + "/pathGetOperation10Xml/?date=2010-07-08";
		GetMethod method = new GetMethod(url);
		
		int statusCode = this.getHttpClient().executeMethod(method);
		assertTrue("Method failed: " + method.getStatusLine(), statusCode == HttpStatus.SC_OK);
		byte[] responseBody = method.getResponseBody();
		String xmlResponse = new String(responseBody);
		assertTrue(xmlResponse.indexOf("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>") > -1);
		assertTrue(xmlResponse.indexOf("<title>2010-07-08</title>") > -1);
	}
	
	/**
	 * {
	 * 	"title":"2010-07-08",
	 * 	"artist":"Mozart",
	 * 	"mainTrack":{
	 * 		"title":"My Track 1",
	 * 		"durationInSeconds":90,
	 * 		"catalogId":1,
	 * 		"newRelease":true,
	 * 		"price":21.3
	 * 	},
	 * 	"tracks":[
	 * 		{"title":"My Track 1","durationInSeconds":90,"catalogId":1,"newRelease":true,"price":21.3},
	 * 		{"title":"My Track 2","durationInSeconds":65,"catalogId":1,"newRelease":true,"price":15.7}
	 * 	]
	 * 	}
	 */
	@Test
	public void testGetOperation10Json() throws Exception {	
		String url = URL_RESOURCE1 + "/pathGetOperation10Json/?date=2010-07-08";
		GetMethod method = new GetMethod(url);
		
		int statusCode = this.getHttpClient().executeMethod(method);
		assertTrue("Method failed: " + method.getStatusLine(), statusCode == HttpStatus.SC_OK);
		byte[] responseBody = method.getResponseBody();
		String jsonResponse = new String(responseBody);
		assertTrue(jsonResponse.indexOf("\"title\":\"2010-07-08\"") > -1);
		assertTrue(jsonResponse.indexOf("\"tracks\":[") > -1);
	}
	
	@Test
	public void testGetOperation11Xml() throws Exception {	
		String url = URL_RESOURCE1 + "/pathGetOperation11Xml/?date=2010-07-08&page=11";
		GetMethod method = new GetMethod(url);
		
		int statusCode = this.getHttpClient().executeMethod(method);
		assertTrue("Method failed: " + method.getStatusLine(), statusCode == HttpStatus.SC_OK);
		byte[] responseBody = method.getResponseBody();
		String xmlResponse = new String(responseBody);
		assertTrue(xmlResponse.indexOf("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>") > -1);
		assertTrue(xmlResponse.indexOf("<title>2010-07-08-11</title>") > -1);
	}
	
	/**
	 * {
	 * 	"title":"2010-07-08-11",
	 * 	"durationInSeconds":90,
	 * 	"catalogId":1,
	 * 	"newRelease":true,
	 * 	"price":21.3
	 * }
	 */
	@Test
	public void testGetOperation11Json() throws Exception {	
		String url = URL_RESOURCE1 + "/pathGetOperation11Json/?date=2010-07-08&page=11";
		GetMethod method = new GetMethod(url);
		
		int statusCode = this.getHttpClient().executeMethod(method);
		assertTrue("Method failed: " + method.getStatusLine(), statusCode == HttpStatus.SC_OK);
		byte[] responseBody = method.getResponseBody();
		String jsonResponse = new String(responseBody);
		assertTrue(jsonResponse.indexOf("\"catalogId\":1,") > -1);
		assertTrue(jsonResponse.indexOf("\"price\":21.3") > -1);
	}
	
	@Test
	public void testGetOperation12Xml() throws Exception {	
		String url = URL_RESOURCE1 + "/pathGetOperation12Xml/12?page=15";
		GetMethod method = new GetMethod(url);
		
		int statusCode = this.getHttpClient().executeMethod(method);
		assertTrue("Method failed: " + method.getStatusLine(), statusCode == HttpStatus.SC_OK);
		byte[] responseBody = method.getResponseBody();
		String xmlResponse = new String(responseBody);
		assertTrue(xmlResponse.indexOf("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>") > -1);
		assertTrue(xmlResponse.indexOf("<title>12-15</title>") > -1);
	}
	
	@Test
	public void testGetOperation12Json() throws Exception {	
		String url = URL_RESOURCE1 + "/pathGetOperation12Json/12?page=15";
		GetMethod method = new GetMethod(url);
		
		int statusCode = this.getHttpClient().executeMethod(method);
		assertTrue("Method failed: " + method.getStatusLine(), statusCode == HttpStatus.SC_OK);
		byte[] responseBody = method.getResponseBody();
		String jsonResponse = new String(responseBody);
		assertTrue(jsonResponse.indexOf("\"title\":\"12-15\"") > -1);
		assertTrue(jsonResponse.indexOf("\"price\":21.3") > -1);
	}
	
	@Test
	public void testGetOperation13Xml() throws Exception {
		String url = URL_RESOURCE1 + "/pathGetOperation13Xml/13";
		GetMethod method = new GetMethod(url);
		
		int statusCode = this.getHttpClient().executeMethod(method);
		assertTrue("Method failed: " + method.getStatusLine(), statusCode == HttpStatus.SC_OK);
		byte[] responseBody = method.getResponseBody();
		String xmlResponse = new String(responseBody);
		assertTrue(xmlResponse.indexOf("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>") > -1);
		assertTrue(xmlResponse.indexOf("<title>getOperation13</title>") > -1);
	}

	/**
	 * {
	 * 	"title":"getOperation13",
	 * 	"artist":"Mozart",
	 * 	"mainTrack":{
	 * 		"title":"My Track 1",
	 * 		"durationInSeconds":90,
	 * 		"catalogId":1,
	 * 		"newRelease":true,
	 * 		"price":21.3},
	 * 		"tracks":[
	 * 			{"title":"My Track 1","durationInSeconds":90,"catalogId":1,"newRelease":true,"price":21.3},
	 * 			{"title":"My Track 2","durationInSeconds":65,"catalogId":1,"newRelease":true,"price":15.7}
	 * 		]
	 * } 
	 */
	@Test
	public void testGetOperation13Json() throws Exception {	
		String url = URL_RESOURCE1 + "/pathGetOperation13Json/13";
		GetMethod method = new GetMethod(url);
		
		int statusCode = this.getHttpClient().executeMethod(method);
		assertTrue("Method failed: " + method.getStatusLine(), statusCode == HttpStatus.SC_OK);
		byte[] responseBody = method.getResponseBody();
		String jsonResponse = new String(responseBody);
		assertTrue(jsonResponse.indexOf("\"title\":\"getOperation13\"") > -1);
		assertTrue(jsonResponse.indexOf("\"tracks\":[{") > -1);
	}
	
	@Test
	public void testGetOperation14Xml() throws Exception {	
		String url = URL_RESOURCE1 + "/pathGetOperation14Xml/14";
		GetMethod method = new GetMethod(url);
		
		int statusCode = this.getHttpClient().executeMethod(method);
		assertTrue("Method failed: " + method.getStatusLine(), statusCode == HttpStatus.SC_OK);
		byte[] responseBody = method.getResponseBody();
		String xmlResponse = new String(responseBody);
		assertTrue(xmlResponse.indexOf("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>") > -1);
		assertTrue(xmlResponse.indexOf("<title>getOperation14</title>") > -1);
	}
	
	/**
	 * {
	 * 	"title":"getOperation14",
	 * 	"artist":"Mozart",
	 * 	"mainTrack":{
	 * 		"title":"My Track 1",
	 * 		"durationInSeconds":90,
	 * 		"catalogId":1,
	 * 		"newRelease":true,
	 * 		"price":21.3
	 * 	},
	 * 	"tracks":[
	 * 		{"title":"My Track 1","durationInSeconds":90,"catalogId":1,"newRelease":true,"price":21.3},
	 * 		{"title":"My Track 2","durationInSeconds":65,"catalogId":1,"newRelease":true,"price":15.7}
	 * 	]
	 * } 
	 */
	@Test
	public void testGetOperation14Json() throws Exception {	
		String url = URL_RESOURCE1 + "/pathGetOperation14Json/14";
		GetMethod method = new GetMethod(url);
		
		int statusCode = this.getHttpClient().executeMethod(method);
		assertTrue("Method failed: " + method.getStatusLine(), statusCode == HttpStatus.SC_OK);
		byte[] responseBody = method.getResponseBody();
		String jsonResponse = new String(responseBody);
		assertTrue(jsonResponse.indexOf("\"title\":\"getOperation14\"") > -1);
		assertTrue(jsonResponse.indexOf("\"tracks\":[{") > -1);
		assertTrue(jsonResponse.indexOf("\"title\":\"My Track 2\"") > -1);
	}
	
	@Test
	public void testGetOperation15Json() throws Exception {	
		String url = URL_RESOURCE1 + "/pathGetOperation15Json/artists/madonna/tracks/4";
		GetMethod method = new GetMethod(url);
		
		int statusCode = this.getHttpClient().executeMethod(method);
		assertTrue("Method failed: " + method.getStatusLine(), statusCode == HttpStatus.SC_OK);
		byte[] responseBody = method.getResponseBody();
		String jsonResponse = new String(responseBody);
		assertTrue(jsonResponse.indexOf("\"title\":\"madonna-4\"") > -1);
	}
	
	@Test
	public void testPostOperation1() throws Exception {	
		String url = URL_RESOURCE1 + "/pathPostOperation1";
		PostMethod method = new PostMethod(url);
		
		int statusCode = this.getHttpClient().executeMethod(method);
		assertTrue("Method failed: " + method.getStatusLine(), statusCode == HttpStatus.SC_NO_CONTENT);
		byte[] responseBody = method.getResponseBody();
		assertNull(responseBody);
	}
	
	@Test
	public void testPostOperation2() throws Exception {	
		String url = URL_RESOURCE1 + "/pathPostOperation2";
		PostMethod method = new PostMethod(url);
		String param = "this is string parameter!";
		method.setRequestEntity(new StringRequestEntity(param, "application/xml", null));
		
		int statusCode = this.getHttpClient().executeMethod(method);
		assertTrue("Method failed: " + method.getStatusLine(), statusCode == HttpStatus.SC_NO_CONTENT);
		byte[] responseBody = method.getResponseBody();
		assertNull(responseBody);
	}
	
	@Test
	public void testPostOperation3() throws Exception {	
		String url = URL_RESOURCE1 + "/pathPostOperation3";
		PostMethod method = new PostMethod(url);
		
		int statusCode = this.getHttpClient().executeMethod(method);
		assertTrue("Method failed: " + method.getStatusLine(), statusCode == HttpStatus.SC_OK);
		byte[] responseBody = method.getResponseBody();
		String actualResponse = new String(responseBody);
		String expectedResponse = "postOperation3";
		assertEquals(expectedResponse, actualResponse);
	}
	
	@Test
	public void testPostOperation4() throws Exception {	
		String url = URL_RESOURCE1 + "/pathPostOperation4";
		PostMethod method = new PostMethod(url);
		String param = "this is string parameter!";
		method.setRequestEntity(new StringRequestEntity(param, "application/xml", null));
		
		int statusCode = this.getHttpClient().executeMethod(method);
		assertTrue("Method failed: " + method.getStatusLine(), statusCode == HttpStatus.SC_OK);
		byte[] responseBody = method.getResponseBody();
		String actualResponse = new String(responseBody);
		String expectedResponse = "this is string parameter!";
		assertEquals(expectedResponse, actualResponse);
	}
	
	@Test
	public void testPostOperation5Xml() throws Exception {	
		String url = URL_RESOURCE1 + "/pathPostOperation5Xml";
		PostMethod method = new PostMethod(url);
		String param = this.getXmlCd();
		method.setRequestEntity(new StringRequestEntity(param, "application/xml", null));
		
		int statusCode = this.getHttpClient().executeMethod(method);
		assertTrue("Method failed: " + method.getStatusLine(), statusCode == HttpStatus.SC_NO_CONTENT);
		byte[] responseBody = method.getResponseBody();
		assertNull(responseBody);
	}
	
	@Test
	public void testPostOperation5Json() throws Exception {	
		String url = URL_RESOURCE1 + "/pathPostOperation5Json";
		PostMethod method = new PostMethod(url);
		String param = this.getJsonCd();
		method.setRequestEntity(new StringRequestEntity(param, "application/json", null));
		
		int statusCode = this.getHttpClient().executeMethod(method);
		assertTrue("Method failed: " + method.getStatusLine(), statusCode == HttpStatus.SC_NO_CONTENT);
		byte[] responseBody = method.getResponseBody();
		assertNull(responseBody);
	}
	
	@Test
	public void testPostOperation6Xml() throws Exception {	
		String url = URL_RESOURCE1 + "/pathPostOperation6Xml";
		PostMethod method = new PostMethod(url);
		
		int statusCode = this.getHttpClient().executeMethod(method);
		assertTrue("Method failed: " + method.getStatusLine(), statusCode == HttpStatus.SC_OK);
		byte[] responseBody = method.getResponseBody();
		String xmlResponse = new String(responseBody);
		assertTrue(xmlResponse.indexOf("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>") > -1);
		assertTrue(xmlResponse.indexOf("<title>postOperation6</title>") > -1);
	}
	
	@Test
	public void testPostOperation6Json() throws Exception {	
		String url = URL_RESOURCE1 + "/pathPostOperation6Json";
		PostMethod method = new PostMethod(url);
		
		int statusCode = this.getHttpClient().executeMethod(method);
		assertTrue("Method failed: " + method.getStatusLine(), statusCode == HttpStatus.SC_OK);
		byte[] responseBody = method.getResponseBody();
		String jsonResponse = new String(responseBody);
		assertTrue(jsonResponse.indexOf("\"title\":\"postOperation6\"") > -1);
	}
	
	@Test
	public void testPostOperation7Xml() throws Exception {	
		String url = URL_RESOURCE1 + "/pathPostOperation7Xml";
		PostMethod method = new PostMethod(url);
		String param = this.getXmlCd();
		method.setRequestEntity(new StringRequestEntity(param, "application/xml", null));
		
		int statusCode = this.getHttpClient().executeMethod(method);
		assertTrue("Method failed: " + method.getStatusLine(), statusCode == HttpStatus.SC_OK);
		byte[] responseBody = method.getResponseBody();
		String xmlResponse = new String(responseBody);
		assertTrue(xmlResponse.indexOf("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>") > -1);
		assertTrue(xmlResponse.indexOf("<title>My Track 1</title>") > -1);
	}
	
	@Test
	public void testPostOperation7Json() throws Exception {	
		String url = URL_RESOURCE1 + "/pathPostOperation7Json";
		PostMethod method = new PostMethod(url);
		String param = this.getJsonCd();
		method.setRequestEntity(new StringRequestEntity(param, "application/json", null));
		
		int statusCode = this.getHttpClient().executeMethod(method);
		assertTrue("Method failed: " + method.getStatusLine(), statusCode == HttpStatus.SC_OK);
		byte[] responseBody = method.getResponseBody();
		String jsonResponse = new String(responseBody);
		assertTrue(jsonResponse.indexOf("\"title\":\"My Track 1\"") > -1);
	}
	
	@Test
	public void testPostOperation8Xml() throws Exception {	
		String url = URL_RESOURCE1 + "/pathPostOperation8Xml";
		PostMethod method = new PostMethod(url);
		
		int statusCode = this.getHttpClient().executeMethod(method);
		assertTrue("Method failed: " + method.getStatusLine(), statusCode == HttpStatus.SC_OK);
		byte[] responseBody = method.getResponseBody();
		String xmlResponse = new String(responseBody);
		assertTrue(xmlResponse.indexOf("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>") > -1);
		assertTrue(xmlResponse.indexOf("<title>postOperation8</title>") > -1);
	}
	
	@Test
	public void testPostOperation8Json() throws Exception {	
		String url = URL_RESOURCE1 + "/pathPostOperation8Json";
		PostMethod method = new PostMethod(url);
		
		int statusCode = this.getHttpClient().executeMethod(method);
		assertTrue("Method failed: " + method.getStatusLine(), statusCode == HttpStatus.SC_OK);
		byte[] responseBody = method.getResponseBody();
		String jsonResponse = new String(responseBody);
		assertTrue(jsonResponse.indexOf("\"title\":\"postOperation8\"") > -1);
	}
	
	/**
	 * This test does nothing as we have to post form params!!
	 */
	@Test
	public void testPostOperation9Xml() throws Exception {	
		String url = URL_RESOURCE1 + "/pathPostOperation9Xml";
		PostMethod method = new PostMethod(url);
	}
	
	/**
	 * This test does nothing as we have to post form params!!
	 */
	@Test
	public void testPostOperation9Json() throws Exception {	
		String url = URL_RESOURCE1 + "/pathPostOperation9Json";
		PostMethod method = new PostMethod(url);
	}
	
	@Test
	public void testPostOperation10Xml() throws Exception {	
		String url = URL_RESOURCE1 + "/pathPostOperation10Xml";
		PostMethod method = new PostMethod(url);
		String param = this.getXmlCd();
		method.setRequestEntity(new StringRequestEntity(param, "application/xml", null));
		
		int statusCode = this.getHttpClient().executeMethod(method);
		assertTrue("Method failed: " + method.getStatusLine(), statusCode == HttpStatus.SC_OK);
		byte[] responseBody = method.getResponseBody();
		String xmlResponse = new String(responseBody);
		assertTrue(xmlResponse.indexOf("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>") > -1);
		assertTrue(xmlResponse.indexOf("<title>My Track 1</title>") > -1);
	}

	@Test
	public void testPostOperation10Json() throws Exception {	
		String url = URL_RESOURCE1 + "/pathPostOperation10Json";
		PostMethod method = new PostMethod(url);
		String param = this.getJsonCd();
		method.setRequestEntity(new StringRequestEntity(param, "application/json", null));
		
		int statusCode = this.getHttpClient().executeMethod(method);
		assertTrue("Method failed: " + method.getStatusLine(), statusCode == HttpStatus.SC_OK);
		byte[] responseBody = method.getResponseBody();
		String jsonResponse = new String(responseBody);
		assertTrue(jsonResponse.indexOf("\"title\":\"My Track 1\"") > -1);
	}

	@Test
	public void testPostOperation11Xml() throws Exception {	
		String url = URL_RESOURCE1 + "/pathPostOperation11Xml/21?page=31";
		PostMethod method = new PostMethod(url);
		String param = this.getXmlCd();
		method.setRequestEntity(new StringRequestEntity(param, "application/xml", null));
		
		int statusCode = this.getHttpClient().executeMethod(method);
		assertTrue("Method failed: " + method.getStatusLine(), statusCode == HttpStatus.SC_OK);
		byte[] responseBody = method.getResponseBody();
		String xmlResponse = new String(responseBody);
		assertTrue(xmlResponse.indexOf("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>") > -1);
		assertTrue(xmlResponse.indexOf("<title>postOperation11-21-31</title>") > -1);
	}

	@Test
	public void testPostOperation11Json() throws Exception {	
		String url = URL_RESOURCE1 + "/pathPostOperation11Json/21?page=31";
		PostMethod method = new PostMethod(url);
		String param = this.getJsonCd();
		method.setRequestEntity(new StringRequestEntity(param, "application/json", null));
		
		int statusCode = this.getHttpClient().executeMethod(method);
		assertTrue("Method failed: " + method.getStatusLine(), statusCode == HttpStatus.SC_OK);
		byte[] responseBody = method.getResponseBody();
		String jsonResponse = new String(responseBody);
		assertTrue(jsonResponse.indexOf("\"title\":\"postOperation11-21-31\"") > -1);
	}

	@Test
	public void testPostOperation12Xml() throws Exception {	
		String url = URL_RESOURCE1 + "/pathPostOperation12Xml";
		PostMethod method = new PostMethod(url);
		String param = this.getXmlCd();
		method.setRequestEntity(new StringRequestEntity(param, "application/xml", null));
		
		int statusCode = this.getHttpClient().executeMethod(method);
		assertTrue("Method failed: " + method.getStatusLine(), statusCode == HttpStatus.SC_OK);
		byte[] responseBody = method.getResponseBody();
		String xmlResponse = new String(responseBody);
		assertTrue(xmlResponse.indexOf("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>") > -1);
		assertTrue(xmlResponse.indexOf("<title>My Track 1</title>") > -1);
	}
	
	@Test
	public void testPostOperation12Json() throws Exception {	
		String url = URL_RESOURCE1 + "/pathPostOperation12Json";
		PostMethod method = new PostMethod(url);
		String param = this.getJsonCd();
		method.setRequestEntity(new StringRequestEntity(param, "application/json", null));
		
		int statusCode = this.getHttpClient().executeMethod(method);
		assertTrue("Method failed: " + method.getStatusLine(), statusCode == HttpStatus.SC_OK);
		byte[] responseBody = method.getResponseBody();
		String jsonResponse = new String(responseBody);
		assertTrue(jsonResponse.indexOf("\"newRelease\":true") > -1);
		assertTrue(jsonResponse.indexOf("\"title\":\"My Track 1\"") > -1);
	}
	
	@Test
	public void testPostOperation13Json() throws Exception {	
		String url = URL_RESOURCE1 + "/pathPostOperation13Json/artists/madonna/tracks/4";
		PostMethod method = new PostMethod(url);
		String param = this.getJsonCd();
		method.setRequestEntity(new StringRequestEntity(param, "application/json", null));
		
		int statusCode = this.getHttpClient().executeMethod(method);
		assertTrue("Method failed: " + method.getStatusLine(), statusCode == HttpStatus.SC_OK);
		byte[] responseBody = method.getResponseBody();
		String jsonResponse = new String(responseBody);
		assertTrue(jsonResponse.indexOf("\"title\":\"madonna-4\"") > -1);
	}
	
	/**
	 * Verifies that a 204 status code is returned when the operation returns null.
	 */
	@Test
	public void testPostOperationNull1() throws Exception {		
		String url = URL_RESOURCE2 + "/pathPostOperationNull1";
		PostMethod method = new PostMethod(url);
		
		int statusCode = this.getHttpClient().executeMethod(method);
		assertTrue("Method failed: " + method.getStatusLine(), statusCode == HttpStatus.SC_NO_CONTENT);
		byte[] responseBody = method.getResponseBody();
		assertNull(responseBody);
	}
	
	/**
	 * Verifies that a 204 status code is returned when the operation returns null.
	 */
	@Test
	public void testPostOperationNull2() throws Exception {		
		String url = URL_RESOURCE2 + "/pathPostOperationNull2";
		PostMethod method = new PostMethod(url);
		
		int statusCode = this.getHttpClient().executeMethod(method);
		assertTrue("Method failed: " + method.getStatusLine(), statusCode == HttpStatus.SC_NO_CONTENT);
		byte[] responseBody = method.getResponseBody();
		assertNull(responseBody);
	}
	
	/**
	 * Verifies that a 204 status code is returned when the operation returns null.
	 */
	@Test
	public void testPostOperationNull3() throws Exception {		
		String url = URL_RESOURCE2 + "/pathPostOperationNull3";
		PostMethod method = new PostMethod(url);
		
		int statusCode = this.getHttpClient().executeMethod(method);
		assertTrue("Method failed: " + method.getStatusLine(), statusCode == HttpStatus.SC_NO_CONTENT);
		byte[] responseBody = method.getResponseBody();
		assertNull(responseBody);
	}
	
	@Test
	public void testPutOperation1() throws Exception {	
		String url = URL_RESOURCE1 + "/pathPutOperation1";
		PutMethod method = new PutMethod(url);
		
		int statusCode = this.getHttpClient().executeMethod(method);
		assertTrue("Method failed: " + method.getStatusLine(), statusCode == HttpStatus.SC_NO_CONTENT);
		byte[] responseBody = method.getResponseBody();
		assertNull(responseBody);
	}
	
	@Test
	public void testPutOperation2() throws Exception {	
		String url = URL_RESOURCE1 + "/pathPutOperation2";
		PutMethod method = new PutMethod(url);
		String param = "this is string parameter!";
		method.setRequestEntity(new StringRequestEntity(param, "application/xml", null));
		
		int statusCode = this.getHttpClient().executeMethod(method);
		assertTrue("Method failed: " + method.getStatusLine(), statusCode == HttpStatus.SC_NO_CONTENT);
		byte[] responseBody = method.getResponseBody();
		assertNull(responseBody);
	}

	@Test
	public void testPutOperation3() throws Exception {	
		String url = URL_RESOURCE1 + "/pathPutOperation3";
		PutMethod method = new PutMethod(url);
		
		int statusCode = this.getHttpClient().executeMethod(method);
		assertTrue("Method failed: " + method.getStatusLine(), statusCode == HttpStatus.SC_OK);
		byte[] responseBody = method.getResponseBody();
		String actualResponse = new String(responseBody);
		String expectedResponse = "putOperation3";
		assertEquals(expectedResponse, actualResponse);
	}

	@Test
	public void testPutOperation4() throws Exception {	
		String url = URL_RESOURCE1 + "/pathPutOperation4";
		PutMethod method = new PutMethod(url);
		String param = "this is string parameter!";
		method.setRequestEntity(new StringRequestEntity(param, "application/xml", null));
		
		int statusCode = this.getHttpClient().executeMethod(method);
		assertTrue("Method failed: " + method.getStatusLine(), statusCode == HttpStatus.SC_OK);
		byte[] responseBody = method.getResponseBody();
		String actualResponse = new String(responseBody);
		String expectedResponse = "this is string parameter!";
		assertEquals(expectedResponse, actualResponse);
	}

	@Test
	public void testPutOperation5Xml() throws Exception {	
		String url = URL_RESOURCE1 + "/pathPutOperation5Xml";
		PutMethod method = new PutMethod(url);
		String param = this.getXmlCd();
		method.setRequestEntity(new StringRequestEntity(param, "application/xml", null));
		
		int statusCode = this.getHttpClient().executeMethod(method);
		assertTrue("Method failed: " + method.getStatusLine(), statusCode == HttpStatus.SC_NO_CONTENT);
		byte[] responseBody = method.getResponseBody();
		assertNull(responseBody);
	}

	@Test
	public void testPutOperation5Json() throws Exception {	
		String url = URL_RESOURCE1 + "/pathPutOperation5Json";
		PutMethod method = new PutMethod(url);
		String param = this.getJsonCd();
		method.setRequestEntity(new StringRequestEntity(param, "application/json", null));
		
		int statusCode = this.getHttpClient().executeMethod(method);
		assertTrue("Method failed: " + method.getStatusLine(), statusCode == HttpStatus.SC_NO_CONTENT);
		byte[] responseBody = method.getResponseBody();
		assertNull(responseBody);
	}

	@Test
	public void testPutOperation6Xml() throws Exception {	
		String url = URL_RESOURCE1 + "/pathPutOperation6Xml";
		PutMethod method = new PutMethod(url);
		
		int statusCode = this.getHttpClient().executeMethod(method);
		assertTrue("Method failed: " + method.getStatusLine(), statusCode == HttpStatus.SC_OK);
		byte[] responseBody = method.getResponseBody();
		String xmlResponse = new String(responseBody);
		assertTrue(xmlResponse.indexOf("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>") > -1);
		assertTrue(xmlResponse.indexOf("<title>putOperation6</title>") > -1);
	}

	@Test
	public void testPutOperation6Json() throws Exception {	
		String url = URL_RESOURCE1 + "/pathPutOperation6Json";
		PutMethod method = new PutMethod(url);
		
		int statusCode = this.getHttpClient().executeMethod(method);
		assertTrue("Method failed: " + method.getStatusLine(), statusCode == HttpStatus.SC_OK);
		byte[] responseBody = method.getResponseBody();
		String jsonResponse = new String(responseBody);
		assertTrue(jsonResponse.indexOf("\"title\":\"putOperation6\"") > -1);
	}

	@Test
	public void testPutOperation7Xml() throws Exception {	
		String url = URL_RESOURCE1 + "/pathPutOperation7Xml";
		PutMethod method = new PutMethod(url);
		String param = this.getXmlCd();
		method.setRequestEntity(new StringRequestEntity(param, "application/xml", null));
		
		int statusCode = this.getHttpClient().executeMethod(method);
		assertTrue("Method failed: " + method.getStatusLine(), statusCode == HttpStatus.SC_OK);
		byte[] responseBody = method.getResponseBody();
		String xmlResponse = new String(responseBody);
		assertTrue(xmlResponse.indexOf("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>") > -1);
		assertTrue(xmlResponse.indexOf("<title>My Track 1</title>") > -1);
	}

	@Test
	public void testPutOperation7Json() throws Exception {	
		String url = URL_RESOURCE1 + "/pathPutOperation7Json";
		PutMethod method = new PutMethod(url);
		String param = this.getJsonCd();
		method.setRequestEntity(new StringRequestEntity(param, "application/json", null));
		
		int statusCode = this.getHttpClient().executeMethod(method);
		assertTrue("Method failed: " + method.getStatusLine(), statusCode == HttpStatus.SC_OK);
		byte[] responseBody = method.getResponseBody();
		String jsonResponse = new String(responseBody);
		assertTrue(jsonResponse.indexOf("\"title\":\"My Track 1\"") > -1);
	}

	@Test
	public void testPutOperation8Xml() throws Exception {	
		String url = URL_RESOURCE1 + "/pathPutOperation8Xml";
		PutMethod method = new PutMethod(url);
		String param = this.getXmlCd();
		method.setRequestEntity(new StringRequestEntity(param, "application/xml", null));
		
		int statusCode = this.getHttpClient().executeMethod(method);
		assertTrue("Method failed: " + method.getStatusLine(), statusCode == HttpStatus.SC_OK);
		byte[] responseBody = method.getResponseBody();
		String xmlResponse = new String(responseBody);
		assertTrue(xmlResponse.indexOf("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>") > -1);
		assertTrue(xmlResponse.indexOf("<title>putOperation8</title>") > -1);
	}

	@Test
	public void testPutOperation8Json() throws Exception {	
		String url = URL_RESOURCE1 + "/pathPutOperation8Json";
		PutMethod method = new PutMethod(url);
		String param = this.getJsonCd();
		method.setRequestEntity(new StringRequestEntity(param, "application/json", null));
		
		int statusCode = this.getHttpClient().executeMethod(method);
		assertTrue("Method failed: " + method.getStatusLine(), statusCode == HttpStatus.SC_OK);
		byte[] responseBody = method.getResponseBody();
		String jsonResponse = new String(responseBody);
		assertTrue(jsonResponse.indexOf("\"title\":\"putOperation8\"") > -1);
	}

	/**
	 * This test does nothing as we have to put form params!!
	 */
	@Test
	public void testPutOperation9Xml() throws Exception {	
		String url = URL_RESOURCE1 + "/pathPutOperation9Xml";
		PutMethod method = new PutMethod(url);
	}

	/**
	 * This test does nothing as we have to put form params!!
	 */
	@Test
	public void testPutOperation9Json() throws Exception {	
		String url = URL_RESOURCE1 + "/pathPutOperation9Json";
		PutMethod method = new PutMethod(url);
	}

	@Test
	public void testPutOperation10Xml() throws Exception {	
		String url = URL_RESOURCE1 + "/pathPutOperation10Xml";
		PutMethod method = new PutMethod(url);
		String param = this.getXmlCd();
		method.setRequestEntity(new StringRequestEntity(param, "application/xml", null));
		
		int statusCode = this.getHttpClient().executeMethod(method);
		assertTrue("Method failed: " + method.getStatusLine(), statusCode == HttpStatus.SC_OK);
		byte[] responseBody = method.getResponseBody();
		String jsonResponse = new String(responseBody);
		assertTrue(jsonResponse.indexOf("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>") > -1);
		assertTrue(jsonResponse.indexOf("<title>My Track 1</title>") > -1);
	}

	@Test
	public void testPutOperation10Json() throws Exception {	
		String url = URL_RESOURCE1 + "/pathPutOperation10Json";
		PutMethod method = new PutMethod(url);
		String param = this.getJsonCd();
		method.setRequestEntity(new StringRequestEntity(param, "application/json", null));
		
		int statusCode = this.getHttpClient().executeMethod(method);
		assertTrue("Method failed: " + method.getStatusLine(), statusCode == HttpStatus.SC_OK);
		byte[] responseBody = method.getResponseBody();
		String jsonResponse = new String(responseBody);
		assertTrue(jsonResponse.indexOf("\"title\":\"My Track 1\"") > -1);
	}

	@Test
	public void testPutOperation11Xml() throws Exception {	
		String url = URL_RESOURCE1 + "/pathPutOperation11Xml/41?page=51";
		PutMethod method = new PutMethod(url);
		String param = this.getXmlCd();
		method.setRequestEntity(new StringRequestEntity(param, "application/xml", null));
		
		int statusCode = this.getHttpClient().executeMethod(method);
		assertTrue("Method failed: " + method.getStatusLine(), statusCode == HttpStatus.SC_OK);
		byte[] responseBody = method.getResponseBody();
		String xmlResponse = new String(responseBody);
		assertTrue(xmlResponse.indexOf("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>") > -1);
		assertTrue(xmlResponse.indexOf("<title>putOperation11-41-51</title>") > -1);
	}

	@Test
	public void testPutOperation11Json() throws Exception {	
		String url = URL_RESOURCE1 + "/pathPutOperation11Json/41?page=51";
		PutMethod method = new PutMethod(url);
		String param = this.getJsonCd();
		method.setRequestEntity(new StringRequestEntity(param, "application/json", null));
		
		int statusCode = this.getHttpClient().executeMethod(method);
		assertTrue("Method failed: " + method.getStatusLine(), statusCode == HttpStatus.SC_OK);
		byte[] responseBody = method.getResponseBody();
		String jsonResponse = new String(responseBody);
		assertTrue(jsonResponse.indexOf("\"title\":\"putOperation11-41-51\"") > -1);
	}

	@Test
	public void testPutOperation12Json() throws Exception {	
		String url = URL_RESOURCE1 + "/pathPutOperation12Json/artists/madonna/tracks/4";
		PutMethod method = new PutMethod(url);
		String param = this.getJsonCd();
		method.setRequestEntity(new StringRequestEntity(param, "application/json", null));
		
		int statusCode = this.getHttpClient().executeMethod(method);
		assertTrue("Method failed: " + method.getStatusLine(), statusCode == HttpStatus.SC_OK);
		byte[] responseBody = method.getResponseBody();
		String jsonResponse = new String(responseBody);
		assertTrue(jsonResponse.indexOf("\"title\":\"madonna-4\"") > -1);
	}
	
	/**
	 * Verifies that a 204 status code is returned when the operation returns null.
	 */
	@Test
	public void testPutOperationNull1() throws Exception {		
		String url = URL_RESOURCE2 + "/pathPutOperationNull1";
		PutMethod method = new PutMethod(url);
		
		int statusCode = this.getHttpClient().executeMethod(method);
		assertTrue("Method failed: " + method.getStatusLine(), statusCode == HttpStatus.SC_NO_CONTENT);
		byte[] responseBody = method.getResponseBody();
		assertNull(responseBody);
	}	
	
	/**
	 * Verifies that a 204 status code is returned when the operation returns null.
	 */
	@Test
	public void testPutOperationNull2() throws Exception {		
		String url = URL_RESOURCE2 + "/pathPutOperationNull2";
		PutMethod method = new PutMethod(url);
		
		int statusCode = this.getHttpClient().executeMethod(method);
		assertTrue("Method failed: " + method.getStatusLine(), statusCode == HttpStatus.SC_NO_CONTENT);
		byte[] responseBody = method.getResponseBody();
		assertNull(responseBody);
	}	
	
	/**
	 * Verifies that a 204 status code is returned when the operation returns null.
	 */
	@Test
	public void testPutOperationNull3() throws Exception {		
		String url = URL_RESOURCE2 + "/pathPutOperationNull3";
		PutMethod method = new PutMethod(url);
		
		int statusCode = this.getHttpClient().executeMethod(method);
		assertTrue("Method failed: " + method.getStatusLine(), statusCode == HttpStatus.SC_NO_CONTENT);
		byte[] responseBody = method.getResponseBody();
		assertNull(responseBody);
	}	
	
	@Test
	public void testDeleteOperation1() throws Exception {	
		String url = URL_RESOURCE1 + "/pathDeleteOperation1";
		DeleteMethod method = new DeleteMethod(url);
		
		int statusCode = this.getHttpClient().executeMethod(method);
		assertTrue("Method failed: " + method.getStatusLine(), statusCode == HttpStatus.SC_NO_CONTENT);
		byte[] responseBody = method.getResponseBody();
		assertNull(responseBody);
	}

	@Test
	public void testDeleteOperation2() throws Exception {	
		String url = URL_RESOURCE1 + "/pathDeleteOperation2";
		DeleteMethod method = new DeleteMethod(url);
		
		int statusCode = this.getHttpClient().executeMethod(method);
		assertTrue("Method failed: " + method.getStatusLine(), statusCode == HttpStatus.SC_NO_CONTENT);
		byte[] responseBody = method.getResponseBody();
		assertNull(responseBody);
	}

	@Test
	public void testDeleteOperation3() throws Exception {	
		String url = URL_RESOURCE1 + "/pathDeleteOperation3";
		DeleteMethod method = new DeleteMethod(url);
		
		int statusCode = this.getHttpClient().executeMethod(method);
		assertTrue("Method failed: " + method.getStatusLine(), statusCode == HttpStatus.SC_NO_CONTENT);
		byte[] responseBody = method.getResponseBody();
		assertNull(responseBody);
	}

	@Test
	public void testDeleteOperation4() throws Exception {	
		String url = URL_RESOURCE1 + "/pathDeleteOperation4/4/5";
		DeleteMethod method = new DeleteMethod(url);
		
		int statusCode = this.getHttpClient().executeMethod(method);
		assertTrue("Method failed: " + method.getStatusLine(), statusCode == HttpStatus.SC_NO_CONTENT);
		byte[] responseBody = method.getResponseBody();
		assertNull(responseBody);
	}

	@Test
	public void testDeleteOperation5() throws Exception {	
		String url = URL_RESOURCE1 + "/pathDeleteOperation5/55?date=2010-07-08";
		DeleteMethod method = new DeleteMethod(url);
		
		int statusCode = this.getHttpClient().executeMethod(method);
		assertTrue("Method failed: " + method.getStatusLine(), statusCode == HttpStatus.SC_NO_CONTENT);
		byte[] responseBody = method.getResponseBody();
		assertNull(responseBody);
	}

	/**
	 * Verifies that a 404 status code is returned when the operation returns null.
	 * Note that the response body is not empty: it contains the HTML error that is usually
	 * displayed in the browser
	 */
	@Test
	public void testGetOperationNull1() throws Exception {		
		String url = URL_RESOURCE1 + "/pathGetOperationNull1";
		GetMethod method = new GetMethod(url);
		
		int statusCode = this.getHttpClient().executeMethod(method);
		assertTrue("Method failed: " + method.getStatusLine(), statusCode == HttpStatus.SC_NOT_FOUND);
		byte[] responseBody = method.getResponseBody();
		assertNotNull(responseBody);
		String htmlResponse = new String(responseBody);		
		assertNotNull(htmlResponse);
	}
	
	/**
	 * Verifies that a 404 status code is returned when the operation returns null.
	 * Note that the response body is not empty: it contains the HTML error that is usually
	 * displayed in the browser
	 */
	@Test
	public void testGetOperationNull2() throws Exception {		
		String url = URL_RESOURCE1 + "/pathGetOperationNull2";
		GetMethod method = new GetMethod(url);
		
		int statusCode = this.getHttpClient().executeMethod(method);
		assertTrue("Method failed: " + method.getStatusLine(), statusCode == HttpStatus.SC_NOT_FOUND);
		byte[] responseBody = method.getResponseBody();
		assertNotNull(responseBody);
		String htmlResponse = new String(responseBody);		
		assertNotNull(htmlResponse);
	}
	
	/**
	 * Verifies that a 404 status code is returned when the operation returns null.
	 * Note that the response body is not empty: it contains the HTML error that is usually
	 * displayed in the browser
	 */
	@Test
	public void testGetOperationNull3() throws Exception {		
		String url = URL_RESOURCE1 + "/pathGetOperationNull3";
		GetMethod method = new GetMethod(url);
		
		int statusCode = this.getHttpClient().executeMethod(method);
		assertTrue("Method failed: " + method.getStatusLine(), statusCode == HttpStatus.SC_NOT_FOUND);
		byte[] responseBody = method.getResponseBody();
		assertNotNull(responseBody);
		String htmlResponse = new String(responseBody);		
		assertNotNull(htmlResponse);
	}
	
	/**
	 * Verifies the XML serialization of invalid XML characters: '<', '>', '&', ''', '"'.
	 * These characteres must be encoded properly.
	 */
	@Test
	public void testGetOperationInvalidCharsXml() throws Exception {		
		String url = URL_RESOURCE2 + "/pathGetOperationInvalidCharsXml";
		GetMethod method = new GetMethod(url);
		
		int statusCode = this.getHttpClient().executeMethod(method);
		assertTrue("Method failed: " + method.getStatusLine(), statusCode == HttpStatus.SC_OK);
		byte[] responseBody = method.getResponseBody();
		assertNotNull(responseBody);
		String xmlResponse = new String(responseBody);
		assertTrue(xmlResponse.indexOf("<title>&lt;My Track 1/&gt; &amp; 'My Track' &quot;2&quot;</title>") > -1);
	}
	
	@Test
	public void testPostOperationInvalidCharsXml() throws Exception {		
		String url = URL_RESOURCE2 + "/pathPostOperationInvalidCharsXml";
		PostMethod method = new PostMethod(url);
		String param = this.getXmlTrackInvalidChars();
		method.setRequestEntity(new StringRequestEntity(param, "application/xml", null));
		
		int statusCode = this.getHttpClient().executeMethod(method);
		assertTrue("Method failed: " + method.getStatusLine(), statusCode == HttpStatus.SC_OK);
		byte[] responseBody = method.getResponseBody();
		String xmlResponse = new String(responseBody);
		assertTrue(xmlResponse.indexOf("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>") > -1);
		assertTrue(xmlResponse.indexOf("<title>&lt;My Track 1/&gt; &amp; 'My Track' &quot;2&quot;</title>") > -1);
	}
	
	@Test
	public void testGetOperationInvalidCharsJson() throws Exception {	
		String url = URL_RESOURCE2 + "/pathGetOperationInvalidCharsJson";
		GetMethod method = new GetMethod(url);
		
		int statusCode = this.getHttpClient().executeMethod(method);
		assertTrue("Method failed: " + method.getStatusLine(), statusCode == HttpStatus.SC_OK);
		byte[] responseBody = method.getResponseBody();
		assertNotNull(responseBody);
		String jsonResponse = new String(responseBody);
		System.out.println(jsonResponse);
		// The JSON string is: {"catalogId":1,"newRelease":true,"price":21.3,"title":"{My {\"Track} 1 & {'My Track' ","durationInSeconds":90}
		assertTrue(jsonResponse.indexOf("{My {\\\"Track} 1 & {'My Track' ") > -1);
	}
	
	/**
	 * The JSON string contains special chars in the values: '{', '}', '"' that must
	 * be enconded and arrive correctly on the server.
	 */
	@Test
	public void testPostOperationInvalidCharsJson() throws Exception {		
		String url = URL_RESOURCE2 + "/pathPostOperationInvalidCharsJson";
		PostMethod method = new PostMethod(url);
		String param = this.getJsonTrackInvalidChars();
		method.setRequestEntity(new StringRequestEntity(param, "application/json", null));
		
		int statusCode = this.getHttpClient().executeMethod(method);
		assertTrue("Method failed: " + method.getStatusLine(), statusCode == HttpStatus.SC_OK);
		byte[] responseBody = method.getResponseBody();
		String jsonResponse = new String(responseBody);
		assertTrue(jsonResponse.indexOf("{My {\\\"Track} 1 & {'My Track' ") > -1);
	}
	
	private String getXmlCd() {
		String xml = 
			"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
			"<cd>" +
				"<artist>Mozart</artist>" +
				"<mainTrack>" +
					"<catalogId>1</catalogId>" +
					"<durationInSeconds>90</durationInSeconds>" +
					"<idsSimilarTracks>" +
						"<long>2</long>" +
						"<long>4</long>" +
					"</idsSimilarTracks>" +
					"<newRelease>true</newRelease>" +
					"<price>21.3</price>" +
					"<title>My Track 1</title>" +
				"</mainTrack>" +
				"<title>Compilation</title>" +
				"<tracks>" +
					"<track>" +
						"<catalogId>1</catalogId>" +
						"<durationInSeconds>90</durationInSeconds>" +
						"<idsSimilarTracks>" +
							"<long>2</long>" +
							"<long>4</long>" +
						"</idsSimilarTracks>" +
						"<newRelease>true</newRelease>" +
						"<price>21.3</price>" +
						"<title>My Track 1</title>" +
					"</track>" +
					"<track>" +
						"<catalogId>1</catalogId>" +
						"<durationInSeconds>65</durationInSeconds>" +
						"<idsSimilarTracks>" +
							"<long>1</long>" +
							"<long>4</long>" +
						"</idsSimilarTracks>" +
						"<newRelease>true</newRelease>" +
						"<price>15.7</price>" +
						"<title>My Track 2</title>" +
					"</track>" +
				"</tracks>" +
			"</cd>";
		return xml;
	}
	
	/**
	 * El título del track tiene caracteres inválidos en XML que están codificados con los
	 * caracteres de escape correspondientes.
	 */
	private String getXmlTrackInvalidChars() {
		String xml = 
			"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
				"<track>" +
					"<catalogId>1</catalogId>" +
					"<durationInSeconds>90</durationInSeconds>" +
					"<idsSimilarTracks>" +
						"<long>2</long>" +
						"<long>4</long>" +
					"</idsSimilarTracks>" +
					"<newRelease>true</newRelease>" +
					"<price>21.3</price>" +
					"<title>&lt;My Track 1/&gt; &amp; 'My Track' &quot;2&quot;</title>" +
				"</track>";
		return xml;
	}

	private String getJsonCd() {
		String json = 
			"{" +
				"\"title\":\"getOperation14\"," +
				"\"artist\":\"Mozart\"," +
				"\"mainTrack\":{" +
					"\"title\":\"My Track 1\"," +
					"\"durationInSeconds\":90," +
					"\"catalogId\":1," +
					"\"newRelease\":true," +
					"\"price\":21.3" +
				"}," +
				"\"tracks\":[" +
					"{\"title\":\"My Track 1\",\"durationInSeconds\":90,\"catalogId\":1,\"newRelease\":true,\"price\":21.3}," +
					"{\"title\":\"My Track 2\",\"durationInSeconds\":65,\"catalogId\":1,\"newRelease\":true,\"price\":15.7}" +
				"]" +
			"}";
		return json;
	}

	/**
	 * The title is: {My {\"Track} 1 & {'My Track' 
	 */
	private String getJsonTrackInvalidChars() {
		String json = 
			"{" +
				"\"catalogId\":1," +
				"\"newRelease\":true," +
				"\"price\":21.3," +
				"\"title\":\"{My {\\\"Track} 1 & {'My Track' \"," +
				"\"durationInSeconds\":90" +
			"}";
		return json;
	}

	private HttpClient getHttpClient() {
		return httpClient;
	}

	private void setHttpClient(HttpClient httpClient) {
		this.httpClient = httpClient;
	}

	private Server getServer() {
		return server;
	}

	private void setServer(Server server) {
		this.server = server;
	}
	
}
