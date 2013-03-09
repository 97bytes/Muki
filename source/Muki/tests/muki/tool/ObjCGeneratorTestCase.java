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
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.apache.velocity.app.Velocity;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import muki.tool.model.ModelDefinitionsType;
import muki.tool.model.ModelType;
import muki.tool.model.PathParamType;
import muki.tool.model.Project;
import muki.tool.model.ControllerDefinitionsType;
import muki.tool.model.ControllerType;

public class ObjCGeneratorTestCase {

	public static String TEMP_DIR = "/Users/gabriel/projects/Java/Muki/private/Muki-Cocoa/Muki-Cocoa/generated/";
	//public static String TEMP_DIR = "c:/temp/objc/generated";
	private ObjcGenerator generator;
	private IOUtility io;

	@Before
	public void setUp() throws Exception {
		this.setGenerator(new ObjcGenerator());
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
		new ObjcGenerator();
		String value = (String)Velocity.getProperty("class.resource.loader.class");
		assertEquals("org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader", value);
	}

	/**
	 * This test verifies the generation of an Objective-C interface file for type
	 */
	@Test
	public void testGenerateTypeInterfaceWithBasicTypes() throws Exception {
		String outputDirectory = TEMP_DIR;
		this.getIo().deleteDirectory(outputDirectory);
		this.getIo().createDirectory(outputDirectory);
		
		Project project = TestHelper.getFullValidProject();
		ModelType track = project.getModelDefinitions().getModel().get(0);
		
		this.getGenerator().generateTypeInterface(track, outputDirectory);
		
		String fileName = outputDirectory + "/Track.h";
		assertTrue(this.getIo().existsFile(fileName));
		String fileContents = this.getIo().readTextFile(fileName);
		assertTrue(fileContents.indexOf("#import <Foundation/Foundation.h>") > -1);
		assertTrue(fileContents.indexOf("@interface Track : NSObject {") > -1);
		assertTrue(fileContents.indexOf("NSString *title;") > -1);
		assertTrue(fileContents.indexOf("NSInteger durationInSeconds;") > -1);
		assertTrue(fileContents.indexOf("long long catalogId;") > -1);
		assertTrue(fileContents.indexOf("BOOL newRelease;") > -1);
		assertTrue(fileContents.indexOf("double price;") > -1);
		assertTrue(fileContents.indexOf("@property (nonatomic, strong) NSString *title;") > -1);
		assertTrue(fileContents.indexOf("@property NSInteger durationInSeconds;") > -1);
		assertTrue(fileContents.indexOf("@property long long catalogId;") > -1);
		assertTrue(fileContents.indexOf("- (id)init;") > -1);
	}

	/**
	 * This test verifies the generation of an Objective-C interface file for type
	 */
	@Test
	public void testGenerateTypeInterfaceWithNestedTypes() throws Exception {
		String outputDirectory = TEMP_DIR;
		this.getIo().deleteDirectory(outputDirectory);
		this.getIo().createDirectory(outputDirectory);

		Project project = TestHelper.getFullValidProject();
		ModelType cd = project.getModelDefinitions().getModel().get(1);
		
		this.getGenerator().generateTypeInterface(cd, outputDirectory);
		
		String fileName = outputDirectory + "/Cd.h";
		assertTrue(this.getIo().existsFile(fileName));
		String fileContents = this.getIo().readTextFile(fileName);
		assertTrue(fileContents.indexOf("#import <Foundation/Foundation.h>") > -1);
		assertTrue(fileContents.indexOf("#import \"Track.h\"") > -1);
		assertTrue(fileContents.indexOf("@interface Cd : NSObject {") > -1);
		assertTrue(fileContents.indexOf("NSString *title;") > -1);
		assertTrue(fileContents.indexOf("NSString *artist;") > -1);
		assertTrue(fileContents.indexOf("Track *mainTrack;") > -1);
		assertTrue(fileContents.indexOf("NSMutableArray *tracks;") > -1);
		assertTrue(fileContents.indexOf("@property (nonatomic, strong) NSString *title;") > -1);
		assertTrue(fileContents.indexOf("@property (nonatomic, strong) NSString *artist;") > -1);
		assertTrue(fileContents.indexOf("- (id)init;") > -1);
		assertTrue(fileContents.indexOf("- (void)addToTracks: (Track *)anObject;") > -1);
		assertTrue(fileContents.indexOf("- (void)removeFromTracks: (Track *)anObject;") > -1);
	}	
	
	/**
	 * This test verifies the generation of an Objective-C interface file for type
	 */
	@Test
	public void testGenerateTypeImplWithBasicTypes() throws Exception {
		String outputDirectory = TEMP_DIR;
		this.getIo().deleteDirectory(outputDirectory);
		this.getIo().createDirectory(outputDirectory);
		
		Project project = TestHelper.getFullValidProject();
		ModelType track = project.getModelDefinitions().getModel().get(0);
		
		this.getGenerator().generateTypeImpl(track, outputDirectory);
		
		String fileName = outputDirectory + "/Track.m";
		assertTrue(this.getIo().existsFile(fileName));
	}

	/**
	 * This test verifies the generation of an Objective-C interface file for type
	 */
	@Test
	public void testGenerateTypeImplWithNestedTypes() throws Exception {
		String outputDirectory = TEMP_DIR;
		this.getIo().deleteDirectory(outputDirectory);
		this.getIo().createDirectory(outputDirectory);

		Project project = TestHelper.getFullValidProject();
		ModelType cd = project.getModelDefinitions().getModel().get(1);
		
		this.getGenerator().generateTypeImpl(cd, outputDirectory);
		
		String fileName = outputDirectory + "/Cd.m";
		assertTrue(this.getIo().existsFile(fileName));
	}	
	
	/**
	 * This test verifies the generation of the parsers superclass and
	 * other support classes
	 */
	@Test
	public void testGenerateSupportClasses() throws Exception {
		String outputDirectory = TEMP_DIR;
		this.getIo().deleteDirectory(outputDirectory);
		this.getIo().createDirectory(outputDirectory);
		
		this.getGenerator().generateSupportClasses(outputDirectory);
		
		assertTrue(this.getIo().existsFile(outputDirectory + "/ObjectParserDelegate.h"));
		assertTrue(this.getIo().existsFile(outputDirectory + "/ObjectParserDelegate.m"));
		assertTrue(this.getIo().existsFile(outputDirectory + "/XmlAttribute.h"));
		assertTrue(this.getIo().existsFile(outputDirectory + "/XmlAttribute.m"));
		assertTrue(this.getIo().existsFile(outputDirectory + "/NSDataBase64.h"));
		assertTrue(this.getIo().existsFile(outputDirectory + "/NSDataBase64.m"));
		assertTrue(this.getIo().existsFile(outputDirectory + "/NSObject+SBJson.h"));
		assertTrue(this.getIo().existsFile(outputDirectory + "/SBJsonStreamParserAccumulator.m"));
		assertTrue(this.getIo().existsFile(outputDirectory + "/SBJsonWriter.m"));
	}	
	
	/**
	 * This test verifies the generation of an Objective-C interface of
	 * the XML parser delegate
	 */
	@Test
	public void testGenerateParserDelegateInterfaceWithPrimitiveTypes() throws Exception {
		String outputDirectory = TEMP_DIR;
		this.getIo().deleteDirectory(outputDirectory);
		this.getIo().createDirectory(outputDirectory);

		Project project = TestHelper.getFullValidProject();
		ModelType cd = project.getModelDefinitions().getModel().get(0);
		
		this.getGenerator().generateParserDelegateInterface(cd, outputDirectory);
		
		String fileName = outputDirectory + "/TrackParserDelegate.h";
		assertTrue(this.getIo().existsFile(fileName));
	}

	/**
	 * This test verifies the generation of an Objective-C interface of
	 * the XML parser delegate
	 */
	@Test
	public void testGenerateParserDelegateInterfaceWithNestedTypes() throws Exception {
		String outputDirectory = TEMP_DIR;
		this.getIo().deleteDirectory(outputDirectory);
		this.getIo().createDirectory(outputDirectory);

		Project project = TestHelper.getFullValidProject();
		ModelType cd = project.getModelDefinitions().getModel().get(1);
		
		this.getGenerator().generateParserDelegateInterface(cd, outputDirectory);
		
		String fileName = outputDirectory + "/CdParserDelegate.h";
		assertTrue(this.getIo().existsFile(fileName));
	}	

	/**
	 * This test verifies the generation of an Objective-C implementation of
	 * the XML parser delegate
	 */
	@Test
	public void testGenerateParserDelegateImplWithBasicTypes() throws Exception {
		String outputDirectory = TEMP_DIR;
		this.getIo().deleteDirectory(outputDirectory);
		this.getIo().createDirectory(outputDirectory);

		Project project = TestHelper.getFullValidProject();
		ModelType cd = project.getModelDefinitions().getModel().get(0);
		
		this.getGenerator().generateParserDelegateImpl(cd, outputDirectory);
		
		String fileName = outputDirectory + "/TrackParserDelegate.m";
		assertTrue(this.getIo().existsFile(fileName));
	}

	/**
	 * This test verifies the generation of an Objective-C implementation of
	 * the XML parser delegate
	 */
	@Test
	public void testGenerateParserDelegateImplWithNestedTypes() throws Exception {
		String outputDirectory = TEMP_DIR;
		this.getIo().deleteDirectory(outputDirectory);
		this.getIo().createDirectory(outputDirectory);

		Project project = TestHelper.getFullValidProject();
		ModelType cd = project.getModelDefinitions().getModel().get(1);
		
		this.getGenerator().generateParserDelegateImpl(cd, outputDirectory);
		
		String fileName = outputDirectory + "/CdParserDelegate.m";
		assertTrue(this.getIo().existsFile(fileName));
	}

	/**
	 * This test verifies the generation of the interface of the class
	 * that serializes the objects to XML
	 */
	@Test
	public void testGenerateXmlSerializerInterface() throws Exception {
		String outputDirectory = TEMP_DIR;
		this.getIo().deleteDirectory(outputDirectory);
		this.getIo().createDirectory(outputDirectory);

		Project project = TestHelper.getFullValidProject();
		ModelDefinitionsType definitions = project.getModelDefinitions();
		
		this.getGenerator().generateXmlSerializerInterface(definitions, outputDirectory);
		
		String fileName = outputDirectory + "/XmlSerializer.h";
		assertTrue(this.getIo().existsFile(fileName));
	}	
	
	/**
	 * This test verifies the generation of the implementation of the class
	 * that serializes the objects to XML
	 */
	@Test
	public void testGenerateXmlSerializerImpl() throws Exception {
		String outputDirectory = TEMP_DIR;
		this.getIo().deleteDirectory(outputDirectory);
		this.getIo().createDirectory(outputDirectory);

		Project project = TestHelper.getFullValidProject();
		ModelDefinitionsType definitions = project.getModelDefinitions();
		
		this.getGenerator().generateXmlSerializerImpl(definitions, outputDirectory);
		
		String fileName = outputDirectory + "/XmlSerializer.m";
		assertTrue(this.getIo().existsFile(fileName));
	}	
	
	/**
	 * This test verifies the generation of the interface of the service stub.
	 */
	@Test
	public void testGenerateControllerStubInterface() throws Exception {
		String outputDirectory = TEMP_DIR;
		this.getIo().deleteDirectory(outputDirectory);
		this.getIo().createDirectory(outputDirectory);

		Project project = TestHelper.getFullValidProject();
		ControllerType controller = project.getControllerDefinitions().getController().get(0);
		ControllerDefinitionsType controllerDefinitions = project.getControllerDefinitions();
		ModelDefinitionsType modelDefinitions = project.getModelDefinitions();
		
		this.getGenerator().generateControllerStubInterface(controller, controllerDefinitions, modelDefinitions, outputDirectory);
		
		String fileName = outputDirectory + "/Controller1Stub.h";
		assertTrue(this.getIo().existsFile(fileName));
	}	
	
	/**
	 * This test verifies the generation of the implementation of the service stub.
	 */
	@Test
	public void testGenerateServiceStubImpl() throws Exception {
		String outputDirectory = TEMP_DIR;
		this.getIo().deleteDirectory(outputDirectory);
		this.getIo().createDirectory(outputDirectory);

		Project project = TestHelper.getFullValidProject();
		ControllerType controller = project.getControllerDefinitions().getController().get(0);
		ModelDefinitionsType models = project.getModelDefinitions();
		
		this.getGenerator().generateControllerStubImpl(controller, models, outputDirectory);
		
		String fileName = outputDirectory + "/Controller1Stub.m";
		assertTrue(this.getIo().existsFile(fileName));
	}	
	
	/**
	 * This test verifies the generation of the interface of the Json parser.
	 */
	@Test
	public void testGenerateJsonDeserializerInterface() throws Exception {
		String outputDirectory = TEMP_DIR;
		this.getIo().deleteDirectory(outputDirectory);
		this.getIo().createDirectory(outputDirectory);

		Project project = TestHelper.getFullValidProject();
		ModelDefinitionsType definitions = project.getModelDefinitions();
		
		this.getGenerator().generateJsonDeserializerInterface(definitions, outputDirectory);
		
		String fileName = outputDirectory + "/JsonDeserializer.h";
		assertTrue(this.getIo().existsFile(fileName));
		String fileContents = this.getIo().readTextFile(fileName);
		assertTrue(fileContents.indexOf("#import <Foundation/Foundation.h>") > -1);
		assertTrue(fileContents.indexOf("#import \"SBJson.h\"") > -1);
		assertTrue(fileContents.indexOf("#import \"Track.h\"") > -1);
		assertTrue(fileContents.indexOf("#import \"Cd.h\"") > -1);
		assertTrue(fileContents.indexOf("@interface JsonDeserializer : NSObject {") > -1);
		assertTrue(fileContents.indexOf("- (Track *)deserializeTrack:(NSString*)jsonString;") > -1);
		assertTrue(fileContents.indexOf("- (Cd *)deserializeCd:(NSString*)jsonString;") > -1);
	}	
	
	/**
	 * This test verifies the generation of the implementation of the Json parser.
	 */
	@Test
	public void testGenerateJsonDeserializerImpl() throws Exception {
		String outputDirectory = TEMP_DIR;
		this.getIo().deleteDirectory(outputDirectory);
		this.getIo().createDirectory(outputDirectory);

		Project project = TestHelper.getFullValidProject();
		ModelDefinitionsType definitions = project.getModelDefinitions();
		
		this.getGenerator().generateJsonDeserializerImpl(definitions, outputDirectory);
		
		String fileName = outputDirectory + "/JsonDeserializer.m";
		assertTrue(this.getIo().existsFile(fileName));
		String fileContents = this.getIo().readTextFile(fileName);
		assertTrue(fileContents.indexOf(" * Generated by Muki") > -1);
		assertTrue(fileContents.indexOf("#import <Foundation/Foundation.h>") > -1);
		assertTrue(fileContents.indexOf("#import \"JsonDeserializer.h\"") > -1);
	}	

	/**
	 * This test verifies the generation of the interface of the Json serializer.
	 */
	@Test
	public void testGenerateJsonSerializerInterface() throws Exception {
		String outputDirectory = TEMP_DIR;
		this.getIo().deleteDirectory(outputDirectory);
		this.getIo().createDirectory(outputDirectory);

		Project project = TestHelper.getFullValidProject();
		ModelDefinitionsType definitions = project.getModelDefinitions();
		
		this.getGenerator().generateJsonSerializerInterface(definitions, outputDirectory);
		
		String fileName = outputDirectory + "/JsonSerializer.h";
		assertTrue(this.getIo().existsFile(fileName));
		String fileContents = this.getIo().readTextFile(fileName);
		assertTrue(fileContents.indexOf(" * Generated by Muki") > -1);
		assertTrue(fileContents.indexOf("#import <Foundation/Foundation.h>") > -1);
		assertTrue(fileContents.indexOf("#import \"SBJson.h\"") > -1);
		assertTrue(fileContents.indexOf("#import \"Track.h\"") > -1);
		assertTrue(fileContents.indexOf("#import \"Cd.h\"") > -1);
		assertTrue(fileContents.indexOf("@interface JsonSerializer : NSObject {") > -1);
		assertTrue(fileContents.indexOf("- (NSString*)serializeTrack:(Track*)anObject;") > -1);
		assertTrue(fileContents.indexOf("- (NSString*)serializeCd:(Cd*)anObject;") > -1);
	}	

	/**
	 * This test verifies the generation of the implementation of the Json serializer.
	 */
	@Test
	public void testGenerateJsonSerializerImpl() throws Exception {
		String outputDirectory = TEMP_DIR;
		this.getIo().deleteDirectory(outputDirectory);
		this.getIo().createDirectory(outputDirectory);

		Project project = TestHelper.getFullValidProject();
		ModelDefinitionsType definitions = project.getModelDefinitions();
		
		this.getGenerator().generateJsonSerializerImpl(definitions, outputDirectory);
		
		String fileName = outputDirectory + "/JsonSerializer.m";
		assertTrue(this.getIo().existsFile(fileName));
		String fileContents = this.getIo().readTextFile(fileName);
		assertTrue(fileContents.indexOf(" * Generated by Muki") > -1);
		assertTrue(fileContents.indexOf("#import <Foundation/Foundation.h>") > -1);
		assertTrue(fileContents.indexOf("#import \"JsonSerializer.h\"") > -1);
		assertTrue(fileContents.indexOf("") > -1);
	}	

	@Test
	public void testReplaceParams1() throws Exception {
		ObjcVelocityHelper helper = new ObjcVelocityHelper();
		String httpPath = "/pathGetOperationXml/{id}/{name}";
		PathParamType param1 = new PathParamType();
		param1.setName("id");
		PathParamType param2 = new PathParamType();
		param2.setName("name");
		List<PathParamType> params = new ArrayList<PathParamType>();
		params.add(param1);
		params.add(param2);
		String result = helper.replacePathParams(httpPath, params);
		assertEquals("/pathGetOperationXml/%@/%@", result);
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
		assertTrue(this.getIo().existsFile(outputDirectory + "/Track.h"));
		assertTrue(this.getIo().existsFile(outputDirectory + "/Track.m"));
		assertTrue(this.getIo().existsFile(outputDirectory + "/Cd.h"));
		assertTrue(this.getIo().existsFile(outputDirectory + "/Cd.m"));
		assertTrue(this.getIo().existsFile(outputDirectory + "/CdParserDelegate.h"));
		assertTrue(this.getIo().existsFile(outputDirectory + "/CdParserDelegate.m"));
		assertTrue(this.getIo().existsFile(outputDirectory + "/TrackParserDelegate.h"));
		assertTrue(this.getIo().existsFile(outputDirectory + "/TrackParserDelegate.m"));
		assertTrue(this.getIo().existsFile(outputDirectory + "/Controller1Stub.h"));
		assertTrue(this.getIo().existsFile(outputDirectory + "/Controller1Stub.m"));
		assertTrue(this.getIo().existsFile(outputDirectory + "/Controller2Stub.h"));
		assertTrue(this.getIo().existsFile(outputDirectory + "/Controller2Stub.m"));
		assertTrue(this.getIo().existsFile(outputDirectory + "/MukiControllerStub.h"));
		assertTrue(this.getIo().existsFile(outputDirectory + "/MukiControllerStub.m"));
		assertTrue(this.getIo().existsFile(outputDirectory + "/ObjectParserDelegate.h"));
		assertTrue(this.getIo().existsFile(outputDirectory + "/ObjectParserDelegate.m"));
		assertTrue(this.getIo().existsFile(outputDirectory + "/XmlSerializer.h"));
		assertTrue(this.getIo().existsFile(outputDirectory + "/XmlSerializer.m"));
		assertTrue(this.getIo().existsFile(outputDirectory + "/XmlAttribute.h"));
		assertTrue(this.getIo().existsFile(outputDirectory + "/XmlAttribute.m"));
		assertTrue(this.getIo().existsFile(outputDirectory + "/JsonSerializer.h"));
		assertTrue(this.getIo().existsFile(outputDirectory + "/JsonSerializer.m"));
		String[] fileNames= new String[] {"NSObject+SBJson.h", "NSObject+SBJson.m", "SBJson.h", "SBJsonParser.h", "SBJsonParser.m", "SBJsonStreamParser.h", "SBJsonStreamParser.m",
		"SBJsonStreamParserAccumulator.h", "SBJsonStreamParserAccumulator.m", "SBJsonStreamParserAdapter.h", "SBJsonStreamParserAdapter.m",
		"SBJsonStreamParserState.h", "SBJsonStreamParserState.m", "SBJsonStreamWriter.h", "SBJsonStreamWriter.m", "SBJsonStreamWriterAccumulator.h",
		"SBJsonStreamWriterAccumulator.m", "SBJsonStreamWriterState.h", "SBJsonStreamWriterState.m", "SBJsonTokeniser.h", "SBJsonTokeniser.m",
		"SBJsonUTF8Stream.h", "SBJsonUTF8Stream.m", "SBJsonWriter.h", "SBJsonWriter.m"};
		for (int i = 0; i < fileNames.length; i++) {
			assertTrue(this.getIo().existsFile(outputDirectory + "/" + fileNames[i]));
		}		
	}		

	private ObjcGenerator getGenerator() {
		return generator;
	}

	private void setGenerator(ObjcGenerator generator) {
		this.generator = generator;
	}

	private IOUtility getIo() {
		return io;
	}

	private void setIo(IOUtility io) {
		this.io = io;
	}

}
