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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


import org.apache.velocity.app.Velocity;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import muki.tool.model.ModelType;
import muki.tool.model.Project;
import muki.tool.model.ControllerDefinitionsType;
import muki.tool.model.ControllerType;
import muki.tool.IOUtility;
import muki.tool.JavaGenerator;

public class JavaGeneratorTestCase {

	public static String TEMP_DIR = TestHelper.TEMP_DIR;
	private JavaGenerator generator;
	private IOUtility io;

	@Before
	public void setUp() throws Exception {
		this.setGenerator(new JavaGenerator());
		this.setIo(new IOUtility());
	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Verifies the correct inicialization of Velocity.
	 * Notice that the generator sets some static inicialization in Velocity
	 * when it is instanciated.
	 * The test fails if an exception is thrown.
	 */
	@Test
	public void testInit() throws Exception {
		new JavaGenerator();
		String value = (String)Velocity.getProperty("class.resource.loader.class");
		assertEquals("org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader", value);
	}

	/**
	 * This test verifies the generation of a Java class file for a type
	 */
	@Test
	public void testGenerateModelWithBasicTypes() throws Exception {
		String outputDirectory = TEMP_DIR;
		this.getIo().deleteDirectory(outputDirectory);
		this.getIo().createDirectory(outputDirectory);
		
		Project project = TestHelper.getFullValidProject();
		ModelType track = project.getModelDefinitions().getModel().get(0);
		
		this.getGenerator().generateModelClass(track, outputDirectory, "store.model");
		
		String fileName = outputDirectory + "/store/model/Track.java";
		assertTrue(this.getIo().existsFile(fileName));
		String fileContents = this.getIo().readTextFile(fileName);
		assertTrue(fileContents.indexOf("package store.model;") > -1);
		assertTrue(fileContents.indexOf(" * Generated by Muki") > -1);
		assertTrue(fileContents.indexOf("@XmlRootElement(name = \"track\")") > 5);
		assertTrue(fileContents.indexOf("@XmlType(name = \"Track\", propOrder = {})") > 5);
		assertTrue(fileContents.indexOf("public class Track implements Serializable {") > 5);		
		assertTrue(fileContents.indexOf("private static final long serialVersionUID = 1L;") > 5);		
		assertTrue(fileContents.indexOf("private String title;") > 5);
		assertTrue(fileContents.indexOf("private int durationInSeconds;") > 5);		
		assertTrue(fileContents.indexOf("private long catalogId;") > 5);		
		assertTrue(fileContents.indexOf("private boolean newRelease;") > 5);		
		assertTrue(fileContents.indexOf("private double price;") > 5);		
		assertTrue(fileContents.indexOf("public Track () {") > 5);		
		assertTrue(fileContents.indexOf("@XmlElement(name = \"title\")") > 5);		
		assertTrue(fileContents.indexOf("public String getTitle() {") > 5);		
		assertTrue(fileContents.indexOf("return this.title;") > 5);		
		assertTrue(fileContents.indexOf("public void setTitle(String newValue) {") > 5);
		assertTrue(fileContents.indexOf("this.title = newValue;") > 5);
		assertTrue(fileContents.indexOf("@XmlAttribute(name = \"durationInSeconds\")") > 5);
		assertTrue(fileContents.indexOf("public int getDurationInSeconds() {") > 5);
		assertTrue(fileContents.indexOf("@XmlAttribute(name = \"catalogId\")") > 5);
		assertTrue(fileContents.indexOf("public long getCatalogId() {") > 5);
		assertTrue(fileContents.indexOf("@XmlAttribute(name = \"newRelease\")") > 5);
		assertTrue(fileContents.indexOf("public boolean isNewRelease() {") > 5);
		assertTrue(fileContents.indexOf("@XmlAttribute(name = \"price\")") > 5);
		assertTrue(fileContents.indexOf("public double getPrice() {") > 5);
	}

	/**
	 * This test verifies the generation of a Java class file for type
	 */
	@Test
	public void testGenerateModelWithNestedTypes() throws Exception {
		String outputDirectory = TEMP_DIR;
		this.getIo().deleteDirectory(outputDirectory);
		this.getIo().createDirectory(outputDirectory);

		Project project = TestHelper.getFullValidProject();
		ModelType cd = project.getModelDefinitions().getModel().get(1);
		
		this.getGenerator().generateModelClass(cd, outputDirectory, "store.model");
		
		String fileName = outputDirectory + "/store/model/Cd.java";
		assertTrue(this.getIo().existsFile(fileName));
		String fileContents = this.getIo().readTextFile(fileName);
		assertTrue(fileContents.indexOf("package store.model;") > -1);		
		assertTrue(fileContents.indexOf(" * Generated by Muki") > -1);
		assertTrue(fileContents.indexOf("@XmlRootElement(name = \"cd\")") > 5);
		assertTrue(fileContents.indexOf("@XmlType(name = \"Cd\", propOrder = {})") > 5);
		assertTrue(fileContents.indexOf("public class Cd implements Serializable {") > 5);		
		assertTrue(fileContents.indexOf("private static final long serialVersionUID = 1L;") > 5);		
		assertTrue(fileContents.indexOf("private String title;") > 5);		
		assertTrue(fileContents.indexOf("private String artist;") > 5);		
		assertTrue(fileContents.indexOf("private Track mainTrack;") > 5);		
		assertTrue(fileContents.indexOf("private List<Track> tracks = new ArrayList<Track>();") > 5);		
		assertTrue(fileContents.indexOf("@XmlElementWrapper(name = \"tracks\")") > 5);		
		assertTrue(fileContents.indexOf("@XmlElement(name = \"track\")") > 5);		
		assertTrue(fileContents.indexOf("public List<Track> getTracks() {") > 5);		
		assertTrue(fileContents.indexOf("return this.tracks;") > 5);		
		assertTrue(fileContents.indexOf("public void setTracks(List<Track> newList)") > 5);		
		assertTrue(fileContents.indexOf("this.tracks = newList;") > 5);		
		assertTrue(fileContents.indexOf("public void addToTracks(Track aValue) {") > 5);		
		assertTrue(fileContents.indexOf("this.tracks .add(aValue);") > 5);		
		assertTrue(fileContents.indexOf("public void removeFromTracks(Track aValue) {") > 5);		
		assertTrue(fileContents.indexOf("this.tracks .remove(aValue);") > 5);		
	}

	/**
	 * This test verifies the generation of a Java class file for the rest application class
	 */
	@Test
	public void testGenerateControllerClass() throws Exception {
		String outputDirectory = TEMP_DIR;
		this.getIo().deleteDirectory(outputDirectory);
		this.getIo().createDirectory(outputDirectory);

		Project project = TestHelper.getFullValidProject();
		ControllerType controller = project.getControllerDefinitions().getController().get(0);

		this.getGenerator().generateControllerClass(controller, outputDirectory, project.getControllerDefinitions().getJavaPackage(), project.getModelDefinitions().getJavaPackage());
		
		String fileName = outputDirectory + "/store/controller/Controller1.java";
		assertTrue(this.getIo().existsFile(fileName));
		String fileContents = this.getIo().readTextFile(fileName);
		assertTrue(fileContents.indexOf("package store.controller;") > -1);		
		assertTrue(fileContents.indexOf(" * Generated by Muki") > -1);
		assertTrue(fileContents.indexOf("import store.model.*;") > 5);
		assertTrue(fileContents.indexOf("@Path(\"/controller1\")") > 5);
		assertTrue(fileContents.indexOf("public class Controller1 {") > 5);
		assertTrue(fileContents.indexOf("private Controller1Delegate delegate;") > 5);
		assertTrue(fileContents.indexOf("public Controller1Delegate getDelegate() {") > 5);
		assertTrue(fileContents.indexOf("public void setDelegate(Controller1Delegate delegate) {") > 5);
		assertTrue(fileContents.indexOf("@GET") > 5);
		assertTrue(fileContents.indexOf("@Path(\"/pathGetOperation1\")") > 5);
		assertTrue(fileContents.indexOf("public String getOperation1() {") > 5);
		assertTrue(fileContents.indexOf("String result = this.getDelegate().getOperation1();") > 5);
		assertTrue(fileContents.indexOf("if (result != null) {") > 5);
		assertTrue(fileContents.indexOf("throw new MukiResourceNotFoundException();") > 5);
		assertTrue(fileContents.indexOf("@Path(\"/pathGetOperation4\")") > 5);
		assertTrue(fileContents.indexOf("public String getOperation4(@QueryParam(\"date\") String date) {") > 5);
		assertTrue(fileContents.indexOf("@Path(\"/pathGetOperation5\")") > 5);
		assertTrue(fileContents.indexOf("public String getOperation5(@QueryParam(\"date\") String date, @QueryParam(\"page\") String page) {") > 5);
		assertTrue(fileContents.indexOf("@Path(\"/pathGetOperation6/{id}\")") > 5);
		assertTrue(fileContents.indexOf("public String getOperation6(@PathParam(\"id\") String id, @QueryParam(\"page\") String page) {") > 5);
		assertTrue(fileContents.indexOf("@Path(\"/pathGetOperation7Xml\")") > 5);
		assertTrue(fileContents.indexOf("@Produces(\"application/xml\")") > 5);
		assertTrue(fileContents.indexOf("public Track getOperation7Xml() {") > 5);
		assertTrue(fileContents.indexOf("@Path(\"/pathGetOperation7Json\")") > 5);
		assertTrue(fileContents.indexOf("@Produces(\"application/json\")") > 5);
		assertTrue(fileContents.indexOf("public Track getOperation7Json() {") > 5);
		assertTrue(fileContents.indexOf("@Path(\"/pathGetOperation8Xml/{id}\")") > 5);
		assertTrue(fileContents.indexOf("public Cd getOperation8Xml(@PathParam(\"id\") String id) {") > 5);
		assertTrue(fileContents.indexOf("@Path(\"/pathGetOperation8Json/{id}\")") > 5);
		assertTrue(fileContents.indexOf("public Cd getOperation8Json(@PathParam(\"id\") String id) {") > 5);
		assertTrue(fileContents.indexOf("@Path(\"/pathGetOperation9Xml/{id}/{name}\")") > 5);
		assertTrue(fileContents.indexOf("public Track getOperation9Xml(@PathParam(\"id\") String id, @PathParam(\"name\") String name) {") > 5);
		assertTrue(fileContents.indexOf("@Path(\"/pathGetOperation9Json/{id}/{name}\")") > 5);
		assertTrue(fileContents.indexOf("public Track getOperation9Json(@PathParam(\"id\") String id, @PathParam(\"name\") String name) {") > 5);
		assertTrue(fileContents.indexOf("@Path(\"/pathGetOperation10Xml\")") > 5);
		assertTrue(fileContents.indexOf("public Cd getOperation10Xml(@QueryParam(\"date\") String date) {") > 5);
		assertTrue(fileContents.indexOf("@Path(\"/pathGetOperation10Json\")") > 5);
		assertTrue(fileContents.indexOf("public Cd getOperation10Json(@QueryParam(\"date\") String date) {") > 5);
		assertTrue(fileContents.indexOf("@Path(\"/pathGetOperation11Xml\")") > 5);
		assertTrue(fileContents.indexOf("public Track getOperation11Xml(@QueryParam(\"date\") String date, @QueryParam(\"page\") String page) {") > 5);
		assertTrue(fileContents.indexOf("@Path(\"/pathGetOperation11Json\")") > 5);
		assertTrue(fileContents.indexOf("public Track getOperation11Json(@QueryParam(\"date\") String date, @QueryParam(\"page\") String page) {") > 5);
		assertTrue(fileContents.indexOf("@Path(\"/pathGetOperation12Xml/{id}\")") > 5);
		assertTrue(fileContents.indexOf("public Cd getOperation12Xml(@PathParam(\"id\") String id, @QueryParam(\"page\") String page) {") > 5);
		assertTrue(fileContents.indexOf("@Path(\"/pathGetOperation12Json/{id}\")") > 5);
		assertTrue(fileContents.indexOf("public Cd getOperation12Json(@PathParam(\"id\") String id, @QueryParam(\"page\") String page) {") > 5);
		assertTrue(fileContents.indexOf("public Cd getOperation13Xml(@PathParam(\"id\") String id, @Context SecurityContext securityInfo) {") > 5);
		assertTrue(fileContents.indexOf("public Cd getOperation13Json(@PathParam(\"id\") String id, @Context SecurityContext securityInfo) {") > 5);
		assertTrue(fileContents.indexOf("public Cd getOperation14Xml(@PathParam(\"id\") String id, @Context SecurityContext securityInfo, @Context Request request)") > 5);
		assertTrue(fileContents.indexOf("public Cd getOperation14Json(@PathParam(\"id\") String id, @Context SecurityContext securityInfo, @Context Request request)") > 5);
		assertTrue(fileContents.indexOf("@Path(\"/pathPostOperation1\")") > 5);
		assertTrue(fileContents.indexOf("public void postOperation1() {") > 5);
		assertTrue(fileContents.indexOf("@Path(\"/pathPostOperation2\")") > 5);
		assertTrue(fileContents.indexOf("public void postOperation2(String param) {") > 5);
		assertTrue(fileContents.indexOf("@Path(\"/pathPostOperation3\")") > 5);
		assertTrue(fileContents.indexOf("public String postOperation3() {") > 5);
		assertTrue(fileContents.indexOf("@Path(\"/pathPostOperation4\")") > 5);
		assertTrue(fileContents.indexOf("public String postOperation4(String param) {") > 5);
		assertTrue(fileContents.indexOf("@Path(\"/pathPostOperation5Xml\")") > 5);
		assertTrue(fileContents.indexOf("@Consumes(\"application/xml\")") > 5);
		assertTrue(fileContents.indexOf("public void postOperation5Xml(Cd param) {") > 5);
		assertTrue(fileContents.indexOf("@Path(\"/pathPostOperation5Json\")") > 5);
		assertTrue(fileContents.indexOf("@Consumes(\"application/json\")") > 5);
		assertTrue(fileContents.indexOf("public void postOperation5Json(Cd param) {") > 5);
		assertTrue(fileContents.indexOf("@Path(\"/pathPostOperation6Xml\")") > 5);
		assertTrue(fileContents.indexOf("public Cd postOperation6Xml() {") > 5);
		assertTrue(fileContents.indexOf("@Path(\"/pathPostOperation6Json\")") > 5);
		assertTrue(fileContents.indexOf("public Cd postOperation6Json() {") > 5);
		assertTrue(fileContents.indexOf("@Path(\"/pathPostOperation7Xml\")") > 5);
		assertTrue(fileContents.indexOf("public Track postOperation7Xml(Cd param) {") > 5);
		assertTrue(fileContents.indexOf("@Path(\"/pathPostOperation7Json\")") > 5);
		assertTrue(fileContents.indexOf("public Track postOperation7Json(Cd param) {") > 5);
		assertTrue(fileContents.indexOf("public Track postOperation8Xml(@Context SecurityContext securityInfo)") > 5);
		assertTrue(fileContents.indexOf("public Track postOperation8Json(@Context SecurityContext securityInfo)") > 5);
		assertTrue(fileContents.indexOf("public Track postOperation9Xml(@FormParam(\"name\") String name, @FormParam(\"date\") String date)") > 5);
		assertTrue(fileContents.indexOf("public Track postOperation9Json(@FormParam(\"name\") String name, @FormParam(\"date\") String date)") > 5);
		assertTrue(fileContents.indexOf("public Track postOperation10Xml(Cd param, @Context SecurityContext securityInfo, @Context Request request) {") > 5);
		assertTrue(fileContents.indexOf("public Track postOperation10Json(Cd param, @Context SecurityContext securityInfo, @Context Request request) {") > 5);
		assertTrue(fileContents.indexOf("@Path(\"/pathPostOperation11Xml/{id}\")") > 5);
		assertTrue(fileContents.indexOf("public Track postOperation11Xml(@PathParam(\"id\") String id, @QueryParam(\"page\") String page, Cd param)") > 5);
		assertTrue(fileContents.indexOf("@Path(\"/pathPostOperation11Json/{id}\")") > 5);
		assertTrue(fileContents.indexOf("public Track postOperation11Json(@PathParam(\"id\") String id, @QueryParam(\"page\") String page, Cd param)") > 5);
		assertTrue(fileContents.indexOf("@Path(\"/pathPutOperation1\")") > 5);
		assertTrue(fileContents.indexOf("public void putOperation1() {") > 5);
		assertTrue(fileContents.indexOf("@Path(\"/pathPutOperation2\")") > 5);
		assertTrue(fileContents.indexOf("public void putOperation2(String param) {") > 5);
		assertTrue(fileContents.indexOf("@Path(\"/pathPutOperation3\")") > 5);
		assertTrue(fileContents.indexOf("public String putOperation3() {") > 5);
		assertTrue(fileContents.indexOf("@Path(\"/pathPutOperation4\")") > 5);
		assertTrue(fileContents.indexOf("public String putOperation4(String param) {") > 5);
		assertTrue(fileContents.indexOf("@Path(\"/pathPutOperation5Xml\")") > 5);
		assertTrue(fileContents.indexOf("@Consumes(\"application/xml\")") > 5);
		assertTrue(fileContents.indexOf("public void putOperation5Xml(Cd param) {") > 5);
		assertTrue(fileContents.indexOf("@Path(\"/pathPutOperation5Json\")") > 5);
		assertTrue(fileContents.indexOf("@Consumes(\"application/json\")") > 5);
		assertTrue(fileContents.indexOf("public void putOperation5Xml(Cd param) {") > 5);
		assertTrue(fileContents.indexOf("@Path(\"/pathPutOperation6Xml\")") > 5);
		assertTrue(fileContents.indexOf("public Cd putOperation6Xml() {") > 5);
		assertTrue(fileContents.indexOf("@Path(\"/pathPutOperation6Json\")") > 5);
		assertTrue(fileContents.indexOf("public Cd putOperation6Json() {") > 5);
		assertTrue(fileContents.indexOf("@Path(\"/pathPutOperation7Xml\")") > 5);
		assertTrue(fileContents.indexOf("public Track putOperation7Xml(Cd param) {") > 5);
		assertTrue(fileContents.indexOf("@Path(\"/pathPutOperation7Json\")") > 5);
		assertTrue(fileContents.indexOf("public Track putOperation7Json(Cd param) {") > 5);
		assertTrue(fileContents.indexOf("public Track putOperation8Xml(Cd param, @Context SecurityContext securityInfo) {") > 5);
		assertTrue(fileContents.indexOf("public Track putOperation8Json(Cd param, @Context SecurityContext securityInfo) {") > 5);
		assertTrue(fileContents.indexOf("public Track putOperation9Xml(@FormParam(\"name\") String name, @FormParam(\"date\") String date)") > 5);
		assertTrue(fileContents.indexOf("public Track putOperation9Json(@FormParam(\"name\") String name, @FormParam(\"date\") String date)") > 5);
		assertTrue(fileContents.indexOf("public Track putOperation10Xml(Cd param, @Context SecurityContext securityInfo, @Context Request request) {") > 5);
		assertTrue(fileContents.indexOf("public Track putOperation10Json(Cd param, @Context SecurityContext securityInfo, @Context Request request) {") > 5);
		assertTrue(fileContents.indexOf("@Path(\"/pathPutOperation11Xml/{id}\")") > 5);
		assertTrue(fileContents.indexOf("public Track putOperation11Xml(@PathParam(\"id\") String id, @QueryParam(\"page\") String page, Cd param)") > 5);
		assertTrue(fileContents.indexOf("@Path(\"/pathPutOperation11Json/{id}\")") > 5);
		assertTrue(fileContents.indexOf("public Track putOperation11Json(@PathParam(\"id\") String id, @QueryParam(\"page\") String page, Cd param)") > 5);
		assertTrue(fileContents.indexOf("@DELETE") > 5);
		assertTrue(fileContents.indexOf("@Path(\"/pathDeleteOperation1\")") > 5);
		assertTrue(fileContents.indexOf("public void deleteOperation1() {") > 5);
		assertTrue(fileContents.indexOf("public void deleteOperation2(@Context SecurityContext securityInfo) {") > 5);
		assertTrue(fileContents.indexOf("public void deleteOperation3(@Context SecurityContext securityInfo, @Context Request request) {") > 5);
		assertTrue(fileContents.indexOf("@Path(\"/pathDeleteOperation4/{id}/{name}\")") > 5);
		assertTrue(fileContents.indexOf("public void deleteOperation4(@PathParam(\"id\") String id, @PathParam(\"name\") String name) {") > 5);
	}

	/**
	 * This test verifies the generation of a Java class file for the rest application class
	 */
	@Test
	public void testGenerateRestApplication() throws Exception {
		String outputDirectory = TEMP_DIR;
		this.getIo().deleteDirectory(outputDirectory);
		this.getIo().createDirectory(outputDirectory);

		Project project = TestHelper.getFullValidProject();
		ControllerDefinitionsType controllers = project.getControllerDefinitions();
		
		this.getGenerator().generateRestApplicationClass(controllers, outputDirectory);
		
		String fileName = outputDirectory + "/store/controller/RestApplication.java";
		assertTrue(this.getIo().existsFile(fileName));
		String fileContents = this.getIo().readTextFile(fileName);
		assertTrue(fileContents.indexOf("package store.controller;") > -1);		
		assertTrue(fileContents.indexOf(" * Generated by Muki") > -1);
		assertTrue(fileContents.indexOf("public class RestApplication extends Application {") > 5);
		assertTrue(fileContents.indexOf("private Set<Object> singletons = new HashSet<Object>();") > 5);
		assertTrue(fileContents.indexOf("private Set<Class<?>> empty = new HashSet<Class<?>>();") > 5);		
		assertTrue(fileContents.indexOf("protected void init() {") > 5);		
	}

	/**
	 * This test verifies the generation of a Java class file for the delegate class
	 */
	@Test
	public void testGenerateDelegateInterface() throws Exception {
		String outputDirectory = TEMP_DIR;
		this.getIo().deleteDirectory(outputDirectory);
		this.getIo().createDirectory(outputDirectory);

		Project project = TestHelper.getFullValidProject();
		ControllerType controller = project.getControllerDefinitions().getController().get(0);

		this.getGenerator().generateDelegateInterface(controller, outputDirectory, project.getControllerDefinitions().getJavaPackage(), project.getModelDefinitions().getJavaPackage());
		
		String fileName = outputDirectory + "/store/controller/Controller1Delegate.java";
		assertTrue(this.getIo().existsFile(fileName));
		String fileContents = this.getIo().readTextFile(fileName);
		assertTrue(fileContents.indexOf("package store.controller;") > -1);		
		assertTrue(fileContents.indexOf(" * Generated by Muki") > -1);
		assertTrue(fileContents.indexOf("import store.model.*;") > 5);
		assertTrue(fileContents.indexOf("public interface Controller1Delegate {") > 5);
		assertTrue(fileContents.indexOf("public String getOperation1();") > 5);		
		assertTrue(fileContents.indexOf("public String getOperation4(String date);") > 5);		
		assertTrue(fileContents.indexOf("public String getOperation5(String date, String page);") > 5);		
		assertTrue(fileContents.indexOf("public String getOperation6(String id, String page);") > 5);		
		assertTrue(fileContents.indexOf("public Track getOperation7Xml();") > 5);		
		assertTrue(fileContents.indexOf("public Track getOperation7Json();") > 5);		
		assertTrue(fileContents.indexOf("public Cd getOperation8Xml(String id);") > 5);		
		assertTrue(fileContents.indexOf("public Cd getOperation8Json(String id);") > 5);		
		assertTrue(fileContents.indexOf("public Track getOperation9Xml(String id, String name);") > 5);		
		assertTrue(fileContents.indexOf("public Track getOperation9Json(String id, String name);") > 5);		
		assertTrue(fileContents.indexOf("public Cd getOperation10Xml(String date);") > 5);		
		assertTrue(fileContents.indexOf("public Cd getOperation10Json(String date);") > 5);		
		assertTrue(fileContents.indexOf("public Track getOperation11Xml(String date, String page);") > 5);		
		assertTrue(fileContents.indexOf("public Track getOperation11Json(String date, String page);") > 5);		
		assertTrue(fileContents.indexOf("public Cd getOperation12Xml(String id, String page);") > 5);		
		assertTrue(fileContents.indexOf("public Cd getOperation12Json(String id, String page);") > 5);		
		assertTrue(fileContents.indexOf("public Cd getOperation13Xml(String id, SecurityContext securityInfo);") > 5);		
		assertTrue(fileContents.indexOf("public Cd getOperation13Json(String id, SecurityContext securityInfo);") > 5);		
		assertTrue(fileContents.indexOf("public Cd getOperation14Xml(String id, SecurityContext securityInfo, Request request);") > 5);		
		assertTrue(fileContents.indexOf("public Cd getOperation14Json(String id, SecurityContext securityInfo, Request request);") > 5);		
		assertTrue(fileContents.indexOf("public void postOperation1();") > 5);		
		assertTrue(fileContents.indexOf("public void postOperation2(String param);") > 5);		
		assertTrue(fileContents.indexOf("public String postOperation3();") > 5);		
		assertTrue(fileContents.indexOf("public String postOperation4(String param);") > 5);		
		assertTrue(fileContents.indexOf("public void postOperation5Xml(Cd param);") > 5);		
		assertTrue(fileContents.indexOf("public void postOperation5Json(Cd param);") > 5);		
		assertTrue(fileContents.indexOf("public Cd postOperation6Xml();") > 5);		
		assertTrue(fileContents.indexOf("public Cd postOperation6Json();") > 5);		
		assertTrue(fileContents.indexOf("public Track postOperation7Xml(Cd param);") > 5);		
		assertTrue(fileContents.indexOf("public Track postOperation7Json(Cd param);") > 5);		
		assertTrue(fileContents.indexOf("public Track postOperation8Xml(SecurityContext securityInfo);") > 5);		
		assertTrue(fileContents.indexOf("public Track postOperation8Json(SecurityContext securityInfo);") > 5);		
		assertTrue(fileContents.indexOf("public Track postOperation9Xml(String name, String date);") > 5);		
		assertTrue(fileContents.indexOf("public Track postOperation9Json(String name, String date);") > 5);		
		assertTrue(fileContents.indexOf("public Track postOperation10Xml(Cd param, SecurityContext securityInfo, Request request);") > 5);
		assertTrue(fileContents.indexOf("public Track postOperation10Json(Cd param, SecurityContext securityInfo, Request request);") > 5);
		assertTrue(fileContents.indexOf("public Track postOperation11Xml(String id, String page, Cd param);") > 5);
		assertTrue(fileContents.indexOf("public Track postOperation11Json(String id, String page, Cd param);") > 5);
		assertTrue(fileContents.indexOf("public void putOperation1();") > 5);	
		assertTrue(fileContents.indexOf("public void putOperation2(String param);") > 5);		
		assertTrue(fileContents.indexOf("public String putOperation3();") > 5);		
		assertTrue(fileContents.indexOf("public String putOperation4(String param);") > 5);		
		assertTrue(fileContents.indexOf("public void putOperation5Xml(Cd param);") > 5);		
		assertTrue(fileContents.indexOf("public void putOperation5Json(Cd param);") > 5);		
		assertTrue(fileContents.indexOf("public Cd putOperation6Xml();") > 5);		
		assertTrue(fileContents.indexOf("public Cd putOperation6Json();") > 5);		
		assertTrue(fileContents.indexOf("public Track putOperation7Xml(Cd param);") > 5);		
		assertTrue(fileContents.indexOf("public Track putOperation7Json(Cd param);") > 5);		
		assertTrue(fileContents.indexOf("public Track putOperation8Xml(Cd param, SecurityContext securityInfo);") > 5);		
		assertTrue(fileContents.indexOf("public Track putOperation8Json(Cd param, SecurityContext securityInfo);") > 5);		
		assertTrue(fileContents.indexOf("public Track putOperation9Xml(String name, String date);") > 5);		
		assertTrue(fileContents.indexOf("public Track putOperation9Json(String name, String date);") > 5);		
		assertTrue(fileContents.indexOf("public Track putOperation10Xml(Cd param, SecurityContext securityInfo, Request request);") > 5);		
		assertTrue(fileContents.indexOf("public Track putOperation10Json(Cd param, SecurityContext securityInfo, Request request);") > 5);		
		assertTrue(fileContents.indexOf("public Track putOperation11Xml(String id, String page, Cd param);") > 5);
		assertTrue(fileContents.indexOf("public Track putOperation11Json(String id, String page, Cd param);") > 5);
		assertTrue(fileContents.indexOf("public void deleteOperation1();") > 5);		
		assertTrue(fileContents.indexOf("public void deleteOperation2(SecurityContext securityInfo);") > 5);		
		assertTrue(fileContents.indexOf("public void deleteOperation3(SecurityContext securityInfo, Request request);") > 5);		
		assertTrue(fileContents.indexOf("public void deleteOperation4(String id, String name);") > 5);		
		assertTrue(fileContents.indexOf("public void deleteOperation5(String id, String date);") > 5);		
	}
	
	/**
	 * This test verifies the generation of a exception and exception mapper classes
	 */
	@Test
	public void testGenerateExceptionClass() throws Exception {
		String outputDirectory = TEMP_DIR;
		this.getIo().deleteDirectory(outputDirectory);
		this.getIo().createDirectory(outputDirectory);

		Project project = TestHelper.getFullValidProject();
		ControllerType controller = project.getControllerDefinitions().getController().get(0);

		this.getGenerator().generateExceptionClass(outputDirectory, project.getControllerDefinitions().getJavaPackage());
		
		String fileName = outputDirectory + "/store/controller/MukiResourceNotFoundException.java";
		assertTrue(this.getIo().existsFile(fileName));
		String fileContents = this.getIo().readTextFile(fileName);
		assertTrue(fileContents.indexOf("package store.controller;") > -1);		
		assertTrue(fileContents.indexOf("public class MukiResourceNotFoundException extends WebApplicationException") > 5);

		fileName = outputDirectory + "/store/controller/MukiExceptionMapper.java";
		assertTrue(this.getIo().existsFile(fileName));
		fileContents = this.getIo().readTextFile(fileName);
		assertTrue(fileContents.indexOf("package store.controller;") > -1);		
		assertTrue(fileContents.indexOf("public class MukiExceptionMapper") > 5);
	}
	
    
	/**
	 * This test verifies the generation of a Java class file for the rest application class
	 */
	@Test
	public void testGenerateAll() throws Exception {
		String outputDirectory = TEMP_DIR;
		this.getIo().deleteDirectory(outputDirectory);
		this.getIo().createDirectory(outputDirectory);

		Project project = TestHelper.getFullValidProject();

		this.getGenerator().generateAll(project, outputDirectory);
		assertTrue(this.getIo().existsFile(outputDirectory + "/store/model/Track.java"));
		assertTrue(this.getIo().existsFile(outputDirectory + "/store/model/Cd.java"));
		assertTrue(this.getIo().existsFile(outputDirectory + "/store/controller/Controller1.java"));
		assertTrue(this.getIo().existsFile(outputDirectory + "/store/controller/Controller2.java"));
		assertTrue(this.getIo().existsFile(outputDirectory + "/store/controller/Controller1Delegate.java"));
		assertTrue(this.getIo().existsFile(outputDirectory + "/store/controller/Controller2Delegate.java"));
		assertTrue(this.getIo().existsFile(outputDirectory + "/store/controller/RestApplication.java"));
	}
		
	private JavaGenerator getGenerator() {
		return generator;
	}

	private void setGenerator(JavaGenerator generator) {
		this.generator = generator;
	}

	private IOUtility getIo() {
		return io;
	}

	private void setIo(IOUtility io) {
		this.io = io;
	}
	

}
