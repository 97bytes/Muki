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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.ProjectHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import muki.tool.model.GetOperationType;
import muki.tool.model.ListAttrType;
import muki.tool.model.ModelDefinitionsType;
import muki.tool.model.ModelType;
import muki.tool.model.PathParamType;
import muki.tool.model.PostOperationType;
import muki.tool.model.Project;
import muki.tool.model.QueryParamType;
import muki.tool.model.SimpleAttrType;
import muki.tool.model.ControllerDefinitionsType;
import muki.tool.model.ControllerType;
import muki.tool.ExecutionResult;
import muki.tool.IOUtility;
import muki.tool.ModelUtility;
import muki.tool.MukiGenerator;

public class RestProjectGeneratorTestCase {

	public static String TEMP_DIR = TestHelper.TEMP_DIR;
	private IOUtility io;
	private ModelUtility utility;

	@Before
	public void setUp() throws Exception {
		this.setIo(new IOUtility());
		this.setUtility(new ModelUtility());
	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Invokes the generation of the Java classes with a well-formed project model.
	 */
	@Test
	public void testGenerateJavaWithInvalidModel() throws Exception {
		SimpleAttrType title = new SimpleAttrType();
		title.setName("");
		title.setType("");
		SimpleAttrType artist = new SimpleAttrType();
		artist.setName(null);
		artist.setType(null);

		ListAttrType idsSimilarTracks = new ListAttrType();
		idsSimilarTracks.setName("");
		idsSimilarTracks.setItemsType("  ");

		ModelType track = new ModelType();
		track.setName("Track");
		track.getSimpleAttr().add(title);
		track.getListAttr().add(idsSimilarTracks);
		
		ListAttrType listOfTracks = new ListAttrType();
		listOfTracks.setName("tracks");
		listOfTracks.setItemsType("Track");
		
		ModelType cd = new ModelType();
		cd.setName("");
		cd.getSimpleAttr().add(title);
		cd.getSimpleAttr().add(artist);
		cd.getListAttr().add(listOfTracks);
		
		ModelDefinitionsType definitions = new ModelDefinitionsType();
		definitions.setJavaPackage(" ");
		definitions.getModel().add(track);
		definitions.getModel().add(cd);
		
		PathParamType idParam = new PathParamType();
		idParam.setName("");
		
		QueryParamType dateParam = new QueryParamType();
		dateParam.setName(null);
		
		GetOperationType getOperation1 = new GetOperationType();
		getOperation1.setName("");
		getOperation1.setHttpPath(null);
		getOperation1.setReturnType("  ");
		
		PostOperationType postOperation1 = new PostOperationType();
		postOperation1.setName(null);
		postOperation1.setHttpPath("");
		
		ControllerDefinitionsType controllers = new ControllerDefinitionsType();
		controllers.setJavaPackage("store.controller");
		ControllerType controller = new ControllerType();
		controller.setName("Controller1");
		controller.setHttpPath("/store");
		controller.getGetOperation().add(getOperation1);
		controller.getPostOperation().add(postOperation1);
		
		Project project = new Project();
		project.setName("MyProject");
		project.setModelDefinitions(definitions);
		project.setControllerDefinitions(controllers);
		
		// Clean output directory
		IOUtility utility = new IOUtility();
		String outputDirectory = TEMP_DIR + "/generated/src";
		utility.deleteDirectory(outputDirectory);
		assertTrue(!utility.existsFile(outputDirectory));

		// Invoke generation
		ExecutionResult result = new ExecutionResult();
		MukiGenerator generator = new MukiGenerator();
		generator.setProject(project);
		generator.setOutputDirectory(outputDirectory);
		generator.generateJava(result);
		assertFalse(result.isOk());
	}
	
	/**
	 * Invokes the generation of the Java classes with a well-formed project model.
	 */
	@Test
	public void testGenerateJavaWithValidModel() throws Exception {
		// Clean output directory
		String outputDirectory = TEMP_DIR + "/generated/java";
		this.getIo().deleteDirectory(outputDirectory);
		assertTrue(!this.getIo().existsFile(outputDirectory));

		// Invoke generation
		ExecutionResult result = new ExecutionResult();
		MukiGenerator generator = new MukiGenerator();
		generator.setProject(this.getFullValidProject());
		generator.setOutputDirectory(outputDirectory);
		generator.generateJava(result);
		System.out.println(result.getLog());
		assertTrue(result.isOk());
		
		// Validate that files are generated
		assertTrue(this.getIo().existsFile(outputDirectory + "/store/model/Track.java"));
		assertTrue(this.getIo().existsFile(outputDirectory + "/store/model/Cd.java"));
		assertTrue(this.getIo().existsFile(outputDirectory + "/store/controller/Controller1.java"));
		assertTrue(this.getIo().existsFile(outputDirectory + "/store/controller/Controller1Delegate.java"));
		assertTrue(this.getIo().existsFile(outputDirectory + "/store/controller/RestApplication.java"));
	}

	@Test
	public void testGenerateAndCompileJava() throws Exception {
		this.getIo().deleteDirectory(TEMP_DIR);
		String srcDirectory = TEMP_DIR + "/generated/src";
		String binDirectory = TEMP_DIR + "/generated/bin";
		this.getIo().createDirectory(srcDirectory);
		this.getIo().createDirectory(binDirectory);
		String fileName = this.getIo().getAbsolutePathForLocalResource("/tests/store-project-ok.xml");
		Project project = this.getUtility().openProject(fileName);
		
		// Step 1: Generate Java classes for the model provided
		ExecutionResult result = new ExecutionResult();
		MukiGenerator generator = new MukiGenerator();
		generator.setOutputDirectory(srcDirectory);
		generator.setProject(project);
		generator.generateJava(result);
		assertTrue(result.isOk());
		
		// Step 2: Configure Ant script for invoking from here
		String eclipseProjectPath = this.getProjectPath();
		String buildFile = this.getIo().getAbsolutePathForLocalResource("/tests/build-compilation-tests.xml");
		String webFile = this.getIo().getAbsolutePathForLocalResource("/tests/web.xml");

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

		// Step 3: Compile generated classes
		antProject.executeTarget("compile");
		String antResult = antProject.getProperty("compile.result");
		assertEquals("ok", antResult);
	}
	
	@Test
	public void testRunArgumentHelp() throws Exception {
		ExecutionResult result = new ExecutionResult();
		MukiGenerator generator = new MukiGenerator();
		generator.run(new String[]{"help"}, result);
		assertFalse(result.isOk());
		assertTrue(result.getLog().indexOf(MukiGenerator.COMMAND_HELP) > -1);
	}

	@Test
	public void testRunArgumentsInvalidOption() throws Exception {
		String fileName = this.getIo().getAbsolutePathForLocalResource("/tests/store-project-ok.xml");
		ExecutionResult result = new ExecutionResult();
		MukiGenerator generator = new MukiGenerator();
		generator.run(new String[]{"invalid-optionhelp", fileName, TEMP_DIR}, result);
		assertFalse(result.isOk());
		assertTrue(result.getLog().indexOf(MukiGenerator.COMMAND_HELP) > -1);
	}

	@Test
	public void testRunArgumentsInvalidFile() throws Exception {
		String fileName = "tests/test-project.xml";
		ExecutionResult result = new ExecutionResult();
		MukiGenerator generator = new MukiGenerator();
		generator.run(new String[]{MukiGenerator.GENERATE_JAVA, fileName, TEMP_DIR}, result);
		assertFalse(result.isOk());
		assertTrue(result.getLog().indexOf("The project file doesn't exists:") > -1);
	}

	@Test
	public void testRunArgumentsInvalidOutputDirectory() throws Exception {
		String fileName = this.getIo().getAbsolutePathForLocalResource("/tests/store-project-ok.xml");
		ExecutionResult result = new ExecutionResult();
		MukiGenerator generator = new MukiGenerator();
		generator.run(new String[]{MukiGenerator.GENERATE_JAVA, fileName, "Invalid-directory/tests"}, result);
		assertFalse(result.isOk());
		assertTrue(result.getLog().indexOf("The output directory doesn't exists:") > -1);
	}

	@Test
	public void testRunArgumentsOk() throws Exception {
		this.getIo().deleteDirectory(TEMP_DIR);
		this.getIo().createDirectory(TEMP_DIR);
		String fileName = this.getIo().getAbsolutePathForLocalResource("/tests/store-project-ok.xml");
		ExecutionResult result = new ExecutionResult();
		MukiGenerator generator = new MukiGenerator();
		generator.run(new String[]{MukiGenerator.GENERATE_JAVA, fileName, TEMP_DIR}, result);
		assertTrue(result.isOk());		
		assertTrue(this.getIo().existsFile(TEMP_DIR + "/store/model/Track.java"));
		assertTrue(this.getIo().existsFile(TEMP_DIR + "/store/model/Cd.java"));
		assertTrue(this.getIo().existsFile(TEMP_DIR + "/store/controller/Controller1.java"));
		assertTrue(this.getIo().existsFile(TEMP_DIR + "/store/controller/Controller1Delegate.java"));
		assertTrue(this.getIo().existsFile(TEMP_DIR + "/store/controller/RestApplication.java"));
	}

	/**
	 * Calculates the path to the Eclipse project in the file system.
	 * We obtain the full path to something in the classpath and then substract 
	 * the part that is not needed  
	 */
	private String getProjectPath() {
		IOUtility utility = new IOUtility();
		String path = utility.getAbsolutePathForLocalResource("/muki");
		int i = path.indexOf("/bin");
		path = path.substring(0, i);
		return path;
	}

	/**
	 * Returns a project object that defines a Cd with a list of tracks
	 * and a service with several operations
	 */
	private Project getFullValidProject() {
		SimpleAttrType title = new SimpleAttrType();
		title.setName("title");
		title.setType("STRING");
		SimpleAttrType artist = new SimpleAttrType();
		artist.setName("artist");
		artist.setType("STRING");
		SimpleAttrType durationInSeconds = new SimpleAttrType();
		durationInSeconds.setName("durationInSeconds");
		durationInSeconds.setType("INT");
		SimpleAttrType catalogId = new SimpleAttrType();
		catalogId.setName("catalogId");
		catalogId.setType("LONG");
		SimpleAttrType newRelease = new SimpleAttrType();
		newRelease.setName("newRelease");
		newRelease.setType("BOOLEAN");
		SimpleAttrType price = new SimpleAttrType();
		price.setName("price");
		price.setType("DOUBLE");
		SimpleAttrType mainTrack = new SimpleAttrType();
		mainTrack.setName("mainTrack");
		mainTrack.setType("Track");

		ModelType track = new ModelType();
		track.setName("Track");
		track.getSimpleAttr().add(title);
		track.getSimpleAttr().add(durationInSeconds);
		track.getSimpleAttr().add(catalogId);
		track.getSimpleAttr().add(newRelease);
		track.getSimpleAttr().add(price);
		
		ListAttrType listOfTracks = new ListAttrType();
		listOfTracks.setName("tracks");
		listOfTracks.setItemsType("Track");
		
		ModelType cd = new ModelType();
		cd.setName("Cd");
		cd.getSimpleAttr().add(title);
		cd.getSimpleAttr().add(artist);
		cd.getSimpleAttr().add(mainTrack);
		cd.getListAttr().add(listOfTracks);
		
		ModelDefinitionsType models = new ModelDefinitionsType();
		models.setJavaPackage("store.model");
		models.getModel().add(track);
		models.getModel().add(cd);
		
		PathParamType idParam = new PathParamType();
		idParam.setName("id");
		PathParamType nameParam = new PathParamType();
		nameParam.setName("name");
		
		QueryParamType dateParam = new QueryParamType();
		dateParam.setName("date");
		QueryParamType pageParam = new QueryParamType();
		pageParam.setName("page");
		
		GetOperationType getOperation1 = new GetOperationType();
		getOperation1.setName("getOperation1");
		getOperation1.setHttpPath("/getOperation1");
		getOperation1.setReturnType("STRING");
		
		GetOperationType getOperation2 = new GetOperationType();
		getOperation2.setName("getOperation2");
		getOperation2.setHttpPath("/getOperation2/{id}");
		getOperation2.setReturnType("STRING");
		getOperation2.getPathParam().add(idParam);

		PostOperationType postOperation1 = new PostOperationType();
		postOperation1.setName("postOperation1");
		postOperation1.setHttpPath("/postOperation1");
		
		PostOperationType postOperation2 = new PostOperationType();
		postOperation2.setName("postOperation2");
		postOperation2.setHttpPath("/postOperation2");
		postOperation2.setParamType("STRING");
				
		ControllerType controller1 = new ControllerType();
		controller1.setName("Controller1");
		controller1.setHttpPath("/store");
		controller1.getGetOperation().add(getOperation1);
		controller1.getGetOperation().add(getOperation2);
		controller1.getPostOperation().add(postOperation1);
		controller1.getPostOperation().add(postOperation2);
		
		ControllerDefinitionsType controllers = new ControllerDefinitionsType();
		controllers.setJavaPackage("store.controller");
		controllers.getController().add(controller1);

		Project project = new Project();
		project.setName("MyProject");
		project.setModelDefinitions(models);
		project.setControllerDefinitions(controllers);
		return project;		
	}

	private IOUtility getIo() {
		return io;
	}

	private void setIo(IOUtility io) {
		this.io = io;
	}

	private ModelUtility getUtility() {
		return utility;
	}

	private void setUtility(ModelUtility utility) {
		this.utility = utility;
	}

}
