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

import java.util.ArrayList;
import java.util.List;

import org.apache.velocity.app.Velocity;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import muki.tool.model.ControllerType;
import muki.tool.model.ModelDefinitionsType;
import muki.tool.model.ModelType;
import muki.tool.model.PathParamType;
import muki.tool.model.Project;

public class SwiftGeneratorTestCase {

	public static String TEMP_DIR = "/Users/gabriel/projects/Java/Muki/private/Muki-Swift/Muki-Swift/generated";
	//public static String TEMP_DIR = "c:/temp/objc/generated";
	private SwiftGenerator generator;
	private IOUtility io;

	@Before
	public void setUp() throws Exception {
		this.setGenerator(new SwiftGenerator());
		this.setIo(new IOUtility());
	}

	@After
	public void tearDown() throws Exception {
	}

	private SwiftGenerator getGenerator() {
		return generator;
	}

	private void setGenerator(SwiftGenerator generator) {
		this.generator = generator;
	}

	private IOUtility getIo() {
		return io;
	}

	private void setIo(IOUtility io) {
		this.io = io;
	}

	
	/**
	 * Verifies the correct inicialization of Velocity.
	 * Notice that the generator sets some static inicialization in Velocity
	 * when it is instanciated.
	 * The test fails if an exception is thrown.
	 */
	@Test
	public void testInit() throws Exception {
		new SwiftGenerator();
		String value = (String)Velocity.getProperty("class.resource.loader.class");
		assertEquals("org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader", value);
	}

	/**
	 * This test verifies the generation of a Swift file for a type.
	 */
	@Test
	public void testGenerateTypeWithBasicTypes() throws Exception {
		String outputDirectory = TEMP_DIR;
		//this.getIo().deleteDirectory(outputDirectory);
		//this.getIo().createDirectory(outputDirectory);
		
		Project project = TestHelper.getFullValidProject();
		ModelType track = project.getModelDefinitions().getModel().get(0);
		
		this.getGenerator().generateType(track, outputDirectory);
		
		String fileName = outputDirectory + "/Track.swift";
		assertTrue(this.getIo().existsFile(fileName));
		String fileContents = this.getIo().readTextFile(fileName);
		assertTrue(fileContents.indexOf("import Foundation") > -1);
		assertTrue(fileContents.indexOf("class Track : NSObject {") > -1);
		assertTrue(fileContents.indexOf("var title = \"\"") > -1);
		assertTrue(fileContents.indexOf("var durationInSeconds = 0") > -1);
		assertTrue(fileContents.indexOf("var catalogId: Int64 = 0") > -1);
		assertTrue(fileContents.indexOf("var newRelease = false") > -1);
		assertTrue(fileContents.indexOf("var price = 0.0") > -1);
	}
	
	/**
	 * This test verifies the generation of a Swift file for a type.
	 */
	@Test
	public void testGenerateTypeWithNestedTypes() throws Exception {
		String outputDirectory = TEMP_DIR;
		//this.getIo().deleteDirectory(outputDirectory);
		//this.getIo().createDirectory(outputDirectory);

		Project project = TestHelper.getFullValidProject();
		ModelType cd = project.getModelDefinitions().getModel().get(1);
		
		this.getGenerator().generateType(cd, outputDirectory);
		
		String fileName = outputDirectory + "/Cd.swift";
		assertTrue(this.getIo().existsFile(fileName));
		String fileContents = this.getIo().readTextFile(fileName);
		assertTrue(fileContents.indexOf("class Cd : NSObject {") > -1);
		assertTrue(fileContents.indexOf("var title = \"\"") > -1);
		assertTrue(fileContents.indexOf("var artist = \"\"") > -1);
		assertTrue(fileContents.indexOf("var mainTrack: Track?") > -1);
		assertTrue(fileContents.indexOf("var tracks = [Track]()") > -1);
		assertTrue(fileContents.indexOf("func addToTracks(anObject: Track) {") > -1);
		assertTrue(fileContents.indexOf("self.tracks.append(anObject)") > -1);
		assertTrue(fileContents.indexOf("func removeFromTracks(anObject: Track) {") > -1);
		assertTrue(fileContents.indexOf("let index = find(self.tracks, anObject)") > -1);
		assertTrue(fileContents.indexOf("if (index != nil) {") > -1);
		assertTrue(fileContents.indexOf("self.tracks.removeAtIndex(index!)") > -1);
		assertTrue(fileContents.indexOf("}") > -1);
	}	
	
	/**
	 * This test verifies the generation of the parsers superclass and
	 * other support classes
	 */
	@Test
	public void testGenerateSupportClasses() throws Exception {
		String outputDirectory = TEMP_DIR;
		//this.getIo().deleteDirectory(outputDirectory);
		//this.getIo().createDirectory(outputDirectory);
		
		this.getGenerator().generateSupportClasses(outputDirectory);
		
		assertTrue(this.getIo().existsFile(outputDirectory + "/ObjectParserDelegate.swift"));
		assertTrue(this.getIo().existsFile(outputDirectory + "/XmlAttribute.swift"));
		assertTrue(this.getIo().existsFile(outputDirectory + "/SwiftyJSON.swift"));
		assertTrue(this.getIo().existsFile(outputDirectory + "/MukiControllerStub.swift"));
	}	
	
	/**
	 * This test verifies the generation of a Swift class of the XML parser delegate
	 */
	@Test
	public void testGenerateParserDelegateWithPrimitiveTypes() throws Exception {
		String outputDirectory = TEMP_DIR;
		//this.getIo().deleteDirectory(outputDirectory);
		//this.getIo().createDirectory(outputDirectory);

		Project project = TestHelper.getFullValidProject();
		ModelType track = project.getModelDefinitions().getModel().get(0);
		
		this.getGenerator().generateParserDelegate(track, outputDirectory);
		
		String fileName = outputDirectory + "/TrackParserDelegate.swift";
		assertTrue(this.getIo().existsFile(fileName));
	}

	/**
	 * This test verifies the generation of a Swift class of the XML parser delegate
	 */
	@Test
	public void testGenerateParserDelegateWithNestedTypes() throws Exception {
		String outputDirectory = TEMP_DIR;
		//this.getIo().deleteDirectory(outputDirectory);
		//this.getIo().createDirectory(outputDirectory);

		Project project = TestHelper.getFullValidProject();
		ModelType cd = project.getModelDefinitions().getModel().get(1);
		
		this.getGenerator().generateParserDelegate(cd, outputDirectory);
		
		String fileName = outputDirectory + "/CdParserDelegate.swift";
		assertTrue(this.getIo().existsFile(fileName));
	}

	/**
	 * This test verifies the generation of the class that serializes the objects to XML
	 */
	@Test
	public void testGenerateXmlSerializer() throws Exception {
		String outputDirectory = TEMP_DIR;
		//this.getIo().deleteDirectory(outputDirectory);
		//this.getIo().createDirectory(outputDirectory);

		Project project = TestHelper.getFullValidProject();
		ModelDefinitionsType definitions = project.getModelDefinitions();
		
		this.getGenerator().generateXmlSerializer(definitions, outputDirectory);
		
		String fileName = outputDirectory + "/XmlSerializer.swift";
		assertTrue(this.getIo().existsFile(fileName));
	}	

	/**
	 * This test verifies the generation of the service stub.
	 */
	@Test
	public void testGenerateServiceStub() throws Exception {
		String outputDirectory = TEMP_DIR;
		//this.getIo().deleteDirectory(outputDirectory);
		//this.getIo().createDirectory(outputDirectory);

		Project project = TestHelper.getFullValidProject();
		ControllerType controller = project.getControllerDefinitions().getController().get(0);
		ModelDefinitionsType models = project.getModelDefinitions();
		
		this.getGenerator().generateControllerStub(controller, models, outputDirectory);
		
		String fileName = outputDirectory + "/Controller1Stub.swift";
		assertTrue(this.getIo().existsFile(fileName));
	}
	
	/**
	 * This test verifies the generation of the Json parser.
	 */
	@Test
	public void testGenerateJsonDeserializerInterface() throws Exception {
		String outputDirectory = TEMP_DIR;
		//this.getIo().deleteDirectory(outputDirectory);
		//this.getIo().createDirectory(outputDirectory);

		Project project = TestHelper.getFullValidProject();
		ModelDefinitionsType definitions = project.getModelDefinitions();
		
		this.getGenerator().generateJsonDeserializer(definitions, outputDirectory);
		
		String fileName = outputDirectory + "/JsonDeserializer.swift";
		assertTrue(this.getIo().existsFile(fileName));
		String fileContents = this.getIo().readTextFile(fileName);
		assertTrue(fileContents.contains("func deserializeTrack(jsonString: String) -> Track? {"));
	}	
	
	/**
	 * This test verifies the generation of Json serializer.
	 */
	@Test
	public void testGenerateJsonSerializerInterface() throws Exception {
		String outputDirectory = TEMP_DIR;
		//this.getIo().deleteDirectory(outputDirectory);
		//this.getIo().createDirectory(outputDirectory);

		Project project = TestHelper.getFullValidProject();
		ModelDefinitionsType definitions = project.getModelDefinitions();
		
		this.getGenerator().generateJsonSerializer(definitions, outputDirectory);
		
		String fileName = outputDirectory + "/JsonSerializer.swift";
		assertTrue(this.getIo().existsFile(fileName));
		String fileContents = this.getIo().readTextFile(fileName);
		assertTrue(fileContents.contains("func serializeTrack(anObject: Track) -> String? {"));
	}	
	
	@Test
	public void testReplaceParams1() throws Exception {
		SwiftVelocityHelper helper = new SwiftVelocityHelper();
		String httpPath = "/pathGetOperationXml/{id}/{name}";
		PathParamType param1 = new PathParamType();
		param1.setName("id");
		PathParamType param2 = new PathParamType();
		param2.setName("name");
		List<PathParamType> params = new ArrayList<PathParamType>();
		params.add(param1);
		params.add(param2);
		String result = helper.replacePathParams(httpPath, params);
		assertEquals("/pathGetOperationXml/\\(id)/\\(name)", result);
	}	
	
	/**
	 * This test verifies the complete generation process.
	 */
	@Test
	public void testGenerateAll() throws Exception {
		String outputDirectory = TEMP_DIR;
		this.getIo().createDirectory(outputDirectory);

		Project project = TestHelper.getFullValidProject();	

		this.getGenerator().generateAll(project, outputDirectory);
		assertTrue(this.getIo().existsFile(outputDirectory + "/Track.swift"));
		assertTrue(this.getIo().existsFile(outputDirectory + "/Cd.swift"));
		assertTrue(this.getIo().existsFile(outputDirectory + "/CdParserDelegate.swift"));
		assertTrue(this.getIo().existsFile(outputDirectory + "/TrackParserDelegate.swift"));
		assertTrue(this.getIo().existsFile(outputDirectory + "/Controller1Stub.swift"));
		assertTrue(this.getIo().existsFile(outputDirectory + "/Controller2Stub.swift"));
		assertTrue(this.getIo().existsFile(outputDirectory + "/MukiControllerStub.swift"));
		assertTrue(this.getIo().existsFile(outputDirectory + "/ObjectParserDelegate.swift"));
		assertTrue(this.getIo().existsFile(outputDirectory + "/XmlSerializer.swift"));
		assertTrue(this.getIo().existsFile(outputDirectory + "/XmlAttribute.swift"));
		assertTrue(this.getIo().existsFile(outputDirectory + "/JsonSerializer.swift"));
	}		
		
}
