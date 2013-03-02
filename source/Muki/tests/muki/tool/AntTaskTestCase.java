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

import static org.junit.Assert.*;

import java.io.File;

import muki.tool.IOUtility;
import muki.tool.MukiGenerator;

import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.ProjectHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


/**
 * This test case verifies the Ant task that generates the code
 */
public class AntTaskTestCase {

	private static String TEMP_DIR = TestHelper.TEMP_DIR;
	private IOUtility io;

	@Before
	public void setUp() throws Exception {
		this.setIo(new IOUtility());
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testRunTaskJavaOk() throws Exception {
		this.getIo().deleteDirectory(TEMP_DIR);
		assertTrue(!this.getIo().existsFile(TEMP_DIR));
		this.getIo().createDirectory(TEMP_DIR);
		String projectFile = this.getIo().getAbsolutePathForLocalResource("/tests/store-project-ok.xml");
				
		// Step 1: Configure Ant script for invoking from here
		String eclipseProjectPath = this.getProjectPath();
		String buildFile = this.getIo().getAbsolutePathForLocalResource("/tests/build-compilation-tests.xml");

		DefaultLogger consoleLogger = new DefaultLogger();
		consoleLogger.setErrorPrintStream(System.err);
		consoleLogger.setOutputPrintStream(System.out);
		consoleLogger.setMessageOutputLevel(org.apache.tools.ant.Project.MSG_INFO);

		org.apache.tools.ant.Project antProject = new org.apache.tools.ant.Project();
		antProject.setUserProperty("ant.file", buildFile);
		antProject.setUserProperty("project.dir", eclipseProjectPath);
		antProject.setUserProperty("option", MukiGenerator.GENERATE_JAVA);
		antProject.setUserProperty("outputDirectory", TEMP_DIR);
		antProject.setUserProperty("projectFile", projectFile);
		antProject.addBuildListener(consoleLogger);
		antProject.fireBuildStarted();
		antProject.init();
		
		ProjectHelper helper = ProjectHelper.getProjectHelper();
		antProject.addReference("ant.projectHelper", helper);
		helper.parse(antProject, new File(buildFile));

		// Step 2: invoke Ant script
		antProject.executeTarget("test-ant-task");
		String antResult = antProject.getProperty("test-ant-task.result");
		assertEquals("ok", antResult);
		assertTrue(this.getIo().existsFile(TEMP_DIR + "/store/model/Track.java"));
		assertTrue(this.getIo().existsFile(TEMP_DIR + "/store/model/Cd.java"));
		assertTrue(this.getIo().existsFile(TEMP_DIR + "/store/controller/Controller1.java"));
		assertTrue(this.getIo().existsFile(TEMP_DIR + "/store/controller/Controller2.java"));
		assertTrue(this.getIo().existsFile(TEMP_DIR + "/store/controller/Controller1Delegate.java"));
		assertTrue(this.getIo().existsFile(TEMP_DIR + "/store/controller/Controller2Delegate.java"));
		assertTrue(this.getIo().existsFile(TEMP_DIR + "/store/controller/RestApplication.java"));
	}

	@Test
	public void testRunTaskObjcOk() throws Exception {
		this.getIo().deleteDirectory(TEMP_DIR);
		assertTrue(!this.getIo().existsFile(TEMP_DIR));
		this.getIo().createDirectory(TEMP_DIR);
		String projectFile = this.getIo().getAbsolutePathForLocalResource("/tests/store-project-ok.xml");
				
		// Step 1: Configure Ant script for invoking from here
		String eclipseProjectPath = this.getProjectPath();
		String buildFile = this.getIo().getAbsolutePathForLocalResource("/tests/build-compilation-tests.xml");

		DefaultLogger consoleLogger = new DefaultLogger();
		consoleLogger.setErrorPrintStream(System.err);
		consoleLogger.setOutputPrintStream(System.out);
		consoleLogger.setMessageOutputLevel(org.apache.tools.ant.Project.MSG_INFO);

		org.apache.tools.ant.Project antProject = new org.apache.tools.ant.Project();
		antProject.setUserProperty("ant.file", buildFile);
		antProject.setUserProperty("project.dir", eclipseProjectPath);
		antProject.setUserProperty("option", MukiGenerator.GENERATE_OBJC);
		antProject.setUserProperty("outputDirectory", TEMP_DIR);
		antProject.setUserProperty("projectFile", projectFile);
		antProject.addBuildListener(consoleLogger);
		antProject.fireBuildStarted();
		antProject.init();
		
		ProjectHelper helper = ProjectHelper.getProjectHelper();
		antProject.addReference("ant.projectHelper", helper);
		helper.parse(antProject, new File(buildFile));

		// Step 2: invoke Ant script
		antProject.executeTarget("test-ant-task");
		String antResult = antProject.getProperty("test-ant-task.result");
		assertEquals("ok", antResult);
		assertTrue(this.getIo().existsFile(TEMP_DIR + "/Track.h"));
		assertTrue(this.getIo().existsFile(TEMP_DIR + "/Track.m"));
		assertTrue(this.getIo().existsFile(TEMP_DIR + "/Cd.h"));
		assertTrue(this.getIo().existsFile(TEMP_DIR + "/Cd.m"));
		assertTrue(this.getIo().existsFile(TEMP_DIR + "/CdParserDelegate.h"));
		assertTrue(this.getIo().existsFile(TEMP_DIR + "/CdParserDelegate.m"));
		assertTrue(this.getIo().existsFile(TEMP_DIR + "/TrackParserDelegate.h"));
		assertTrue(this.getIo().existsFile(TEMP_DIR + "/TrackParserDelegate.m"));
		assertTrue(this.getIo().existsFile(TEMP_DIR + "/Controller1Stub.h"));
		assertTrue(this.getIo().existsFile(TEMP_DIR + "/Controller1Stub.m"));
		assertTrue(this.getIo().existsFile(TEMP_DIR + "/Controller2Stub.h"));
		assertTrue(this.getIo().existsFile(TEMP_DIR + "/Controller2Stub.m"));
		assertTrue(this.getIo().existsFile(TEMP_DIR + "/ObjectParserDelegate.h"));
		assertTrue(this.getIo().existsFile(TEMP_DIR + "/ObjectParserDelegate.m"));
		assertTrue(this.getIo().existsFile(TEMP_DIR + "/XmlSerializer.h"));
		assertTrue(this.getIo().existsFile(TEMP_DIR + "/XmlSerializer.m"));
		assertTrue(this.getIo().existsFile(TEMP_DIR + "/XmlAttribute.h"));
		assertTrue(this.getIo().existsFile(TEMP_DIR + "/XmlAttribute.m"));
	}

	@Test
	public void testRunInvalidOutputDirectory() throws Exception {
		this.getIo().deleteDirectory(TEMP_DIR);
		assertTrue(!this.getIo().existsFile(TEMP_DIR));
		String projectFile = this.getIo().getAbsolutePathForLocalResource("/tests/store-project-ok.xml");
				
		// Step 1: Configure Ant script for invoking from here
		String eclipseProjectPath = this.getProjectPath();
		String buildFile = this.getIo().getAbsolutePathForLocalResource("/tests/build-compilation-tests.xml");

		DefaultLogger consoleLogger = new DefaultLogger();
		consoleLogger.setErrorPrintStream(System.err);
		consoleLogger.setOutputPrintStream(System.out);
		consoleLogger.setMessageOutputLevel(org.apache.tools.ant.Project.MSG_INFO);

		org.apache.tools.ant.Project antProject = new org.apache.tools.ant.Project();
		antProject.setUserProperty("ant.file", buildFile);
		antProject.setUserProperty("project.dir", eclipseProjectPath);
		antProject.setUserProperty("option", MukiGenerator.GENERATE_JAVA);
		antProject.setUserProperty("outputDirectory", TEMP_DIR);
		antProject.setUserProperty("projectFile", projectFile);
		antProject.addBuildListener(consoleLogger);
		antProject.fireBuildStarted();
		antProject.init();
		
		ProjectHelper helper = ProjectHelper.getProjectHelper();
		antProject.addReference("ant.projectHelper", helper);
		helper.parse(antProject, new File(buildFile));

		// Step 2: invoke Ant script
		antProject.executeTarget("test-ant-task");
		String antResult = antProject.getProperty("test-ant-task.result");
		assertEquals("ok", antResult);
	}

	@Test
	public void testRunInvalidProjectFile() throws Exception {
		this.getIo().deleteDirectory(TEMP_DIR);
		assertTrue(!this.getIo().existsFile(TEMP_DIR));
		this.getIo().createDirectory(TEMP_DIR);
				
		// Step 1: Configure Ant script for invoking from here
		String eclipseProjectPath = this.getProjectPath();
		String buildFile = this.getIo().getAbsolutePathForLocalResource("/tests/build-compilation-tests.xml");

		DefaultLogger consoleLogger = new DefaultLogger();
		consoleLogger.setErrorPrintStream(System.err);
		consoleLogger.setOutputPrintStream(System.out);
		consoleLogger.setMessageOutputLevel(org.apache.tools.ant.Project.MSG_INFO);

		org.apache.tools.ant.Project antProject = new org.apache.tools.ant.Project();
		antProject.setUserProperty("ant.file", buildFile);
		antProject.setUserProperty("project.dir", eclipseProjectPath);
		antProject.setUserProperty("option", MukiGenerator.GENERATE_JAVA);
		antProject.setUserProperty("outputDirectory", TEMP_DIR);
		antProject.setUserProperty("projectFile", "anyfile.xml");
		antProject.addBuildListener(consoleLogger);
		antProject.fireBuildStarted();
		antProject.init();
		
		ProjectHelper helper = ProjectHelper.getProjectHelper();
		antProject.addReference("ant.projectHelper", helper);
		helper.parse(antProject, new File(buildFile));

		// Step 2: invoke Ant script
		antProject.executeTarget("test-ant-task");
		String antResult = antProject.getProperty("test-ant-task.result");
		assertEquals("ok", antResult);
	}

	@Test
	public void testRunInvalidOption() throws Exception {
		this.getIo().deleteDirectory(TEMP_DIR);
		assertTrue(!this.getIo().existsFile(TEMP_DIR));
		this.getIo().createDirectory(TEMP_DIR);
		String projectFile = this.getIo().getAbsolutePathForLocalResource("/tests/store-project-ok.xml");
				
		// Step 1: Configure Ant script for invoking from here
		String eclipseProjectPath = this.getProjectPath();
		String buildFile = this.getIo().getAbsolutePathForLocalResource("/tests/build-compilation-tests.xml");

		DefaultLogger consoleLogger = new DefaultLogger();
		consoleLogger.setErrorPrintStream(System.err);
		consoleLogger.setOutputPrintStream(System.out);
		consoleLogger.setMessageOutputLevel(org.apache.tools.ant.Project.MSG_INFO);

		org.apache.tools.ant.Project antProject = new org.apache.tools.ant.Project();
		antProject.setUserProperty("ant.file", buildFile);
		antProject.setUserProperty("project.dir", eclipseProjectPath);
		antProject.setUserProperty("option", "any-option");
		antProject.setUserProperty("outputDirectory", TEMP_DIR);
		antProject.setUserProperty("projectFile", projectFile);
		antProject.addBuildListener(consoleLogger);
		antProject.fireBuildStarted();
		antProject.init();
		
		ProjectHelper helper = ProjectHelper.getProjectHelper();
		antProject.addReference("ant.projectHelper", helper);
		helper.parse(antProject, new File(buildFile));

		// Step 2: invoke Ant script
		antProject.executeTarget("test-ant-task");
		String antResult = antProject.getProperty("test-ant-task.result");
		assertEquals("ok", antResult);
	}

	@Test
	public void testRunInvalidModel() throws Exception {
		this.getIo().deleteDirectory(TEMP_DIR);
		assertTrue(!this.getIo().existsFile(TEMP_DIR));
		this.getIo().createDirectory(TEMP_DIR);
		String projectFile = this.getIo().getAbsolutePathForLocalResource("/tests/store-project-invalid.xml");
				
		// Step 1: Configure Ant script for invoking from here
		String eclipseProjectPath = this.getProjectPath();
		String buildFile = this.getIo().getAbsolutePathForLocalResource("/tests/build-compilation-tests.xml");

		DefaultLogger consoleLogger = new DefaultLogger();
		consoleLogger.setErrorPrintStream(System.err);
		consoleLogger.setOutputPrintStream(System.out);
		consoleLogger.setMessageOutputLevel(org.apache.tools.ant.Project.MSG_INFO);

		org.apache.tools.ant.Project antProject = new org.apache.tools.ant.Project();
		antProject.setUserProperty("ant.file", buildFile);
		antProject.setUserProperty("project.dir", eclipseProjectPath);
		antProject.setUserProperty("option", MukiGenerator.GENERATE_JAVA);
		antProject.setUserProperty("outputDirectory", TEMP_DIR);
		antProject.setUserProperty("projectFile", projectFile);
		antProject.addBuildListener(consoleLogger);
		antProject.fireBuildStarted();
		antProject.init();
		
		ProjectHelper helper = ProjectHelper.getProjectHelper();
		antProject.addReference("ant.projectHelper", helper);
		helper.parse(antProject, new File(buildFile));

		// Step 2: invoke Ant script
		antProject.executeTarget("test-ant-task");
		String antResult = antProject.getProperty("test-ant-task.result");
		assertEquals("ok", antResult);
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

	private IOUtility getIo() {
		return io;
	}

	private void setIo(IOUtility io) {
		this.io = io;
	}

}
