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

import java.io.FileWriter;
import java.net.URL;

import muki.tool.model.ModelDefinitionsType;
import muki.tool.model.ModelType;
import muki.tool.model.Project;
import muki.tool.model.ControllerDefinitionsType;
import muki.tool.model.ControllerType;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

/**
 * This class generates the following Objective-C artifacts for a Cocoa restful client:
 * - Interfaces and implementation classes of the types defined in the project
 * - Delegate classes to parse XML
 * - XmlOuputter class to serialize to XML
 * - Support classes used  by the implementation: XmlAttribute and ObjectParserDelegate
 * - Client Stub to access the remote restful service
 */
public class ObjcGenerator {

	private IOUtility io;
	
	public ObjcGenerator() throws Exception {
		this.init();
	}

	private void init() throws Exception {
		this.setIo(new IOUtility());
		Velocity.init(this.getIo().getProperties("velocity.properties"));
	}

	/**
	 * This is the main method that generates all the artifacts
	 */
	public void generateAll(Project project, String outputDirectory) throws Exception {
		ModelDefinitionsType allTypes = project.getModelDefinitions();
		for (ModelType aType : allTypes.getModel()) {
			this.generateTypeInterface(aType, outputDirectory);
			this.generateTypeImpl(aType, outputDirectory);
			this.generateParserDelegateInterface(aType, outputDirectory);
			this.generateParserDelegateImpl(aType, outputDirectory);
		}
		ControllerDefinitionsType allControllers = project.getControllerDefinitions();
		for (ControllerType aController : allControllers.getController()) {
			this.generateControllerStubInterface(aController, allControllers, allTypes, outputDirectory);
			this.generateControllerStubImpl(aController, allTypes, outputDirectory);
		}
		this.generateSupportClasses(outputDirectory);
		this.generateXmlSerializerInterface(allTypes, outputDirectory);
		this.generateXmlSerializerImpl(allTypes, outputDirectory);
		this.generateJsonSerializerInterface(allTypes, outputDirectory);
		this.generateJsonSerializerImpl(allTypes, outputDirectory);
		this.generateJsonDeserializerInterface(allTypes, outputDirectory);
		this.generateJsonDeserializerImpl(allTypes, outputDirectory);
	}
	
	public void generateTypeInterface(ModelType type, String outputDirectory) throws Exception {
		this.getIo().createDirectory(outputDirectory);
		VelocityContext context = new VelocityContext();
		context.put("type", type);
		context.put("helper", new ObjcVelocityHelper());
		Template template = Velocity.getTemplate("/templates/template_objc_type.h.vm");
		String fileName = outputDirectory + "/" + type.getName() + ".h";
		FileWriter writer = new FileWriter(fileName);
		template.merge(context, writer);
		writer.flush();
		writer.close();
	}
	
	public void generateTypeImpl(ModelType type, String outputDirectory) throws Exception {
		this.getIo().createDirectory(outputDirectory);
		VelocityContext context = new VelocityContext();
		context.put("type", type);
		context.put("helper", new ObjcVelocityHelper());
		Template template = Velocity.getTemplate("/templates/template_objc_type.m.vm");
		String fileName = outputDirectory + "/" + type.getName() + ".m";
		FileWriter writer = new FileWriter(fileName);
		template.merge(context, writer);
		writer.flush();
		writer.close();
	}

	public void generateSupportClasses(String outputDirectory) throws Exception {
		this.getIo().createDirectory(outputDirectory);
		URL url = this.getClass().getResource("/templates/template_objc_NSDataBase64.h.vm");
		byte[] data = this.getIo().readBytes(url);
		this.getIo().writeBinaryFile(outputDirectory + "/NSDataBase64.h", data);
		url = this.getClass().getResource("/templates/template_objc_NSDataBase64.m.vm");
		data = this.getIo().readBytes(url);
		this.getIo().writeBinaryFile(outputDirectory + "/NSDataBase64.m", data);
		
		url = this.getClass().getResource("/templates/template_objc_ObjectParserDelegate.h.vm");
		data = this.getIo().readBytes(url);
		this.getIo().writeBinaryFile(outputDirectory + "/ObjectParserDelegate.h", data);
		url = this.getClass().getResource("/templates/template_objc_ObjectParserDelegate.m.vm");
		data = this.getIo().readBytes(url);
		this.getIo().writeBinaryFile(outputDirectory + "/ObjectParserDelegate.m", data);

		url = this.getClass().getResource("/templates/template_objc_XmlAttribute.h.vm");
		data = this.getIo().readBytes(url);
		this.getIo().writeBinaryFile(outputDirectory + "/XmlAttribute.h", data);
		url = this.getClass().getResource("/templates/template_objc_XmlAttribute.m.vm");
		data = this.getIo().readBytes(url);
		this.getIo().writeBinaryFile(outputDirectory + "/XmlAttribute.m", data);
		
		url = this.getClass().getResource("/templates/template_objc_MukiControllerStub.h.vm");
		data = this.getIo().readBytes(url);
		this.getIo().writeBinaryFile(outputDirectory + "/MukiControllerStub.h", data);
		url = this.getClass().getResource("/templates/template_objc_MukiControllerStub.m.vm");
		data = this.getIo().readBytes(url);
		this.getIo().writeBinaryFile(outputDirectory + "/MukiControllerStub.m", data);
		
		// Files of the JSON-Framework (Google)
		String[] fileNames= new String[] {"NSObject+SBJson.h", "NSObject+SBJson.m", "SBJson.h", "SBJsonParser.h", "SBJsonParser.m", "SBJsonStreamParser.h", "SBJsonStreamParser.m",
		"SBJsonStreamParserAccumulator.h", "SBJsonStreamParserAccumulator.m", "SBJsonStreamParserAdapter.h", "SBJsonStreamParserAdapter.m",
		"SBJsonStreamParserState.h", "SBJsonStreamParserState.m", "SBJsonStreamWriter.h", "SBJsonStreamWriter.m", "SBJsonStreamWriterAccumulator.h",
		"SBJsonStreamWriterAccumulator.m", "SBJsonStreamWriterState.h", "SBJsonStreamWriterState.m", "SBJsonTokeniser.h", "SBJsonTokeniser.m",
		"SBJsonUTF8Stream.h", "SBJsonUTF8Stream.m", "SBJsonWriter.h", "SBJsonWriter.m"};
		for (int i = 0; i < fileNames.length; i++) {
			url = this.getClass().getResource("/templates/" + fileNames[i]);
			data = this.getIo().readBytes(url);
			this.getIo().writeBinaryFile(outputDirectory + "/" + fileNames[i], data);
		}
	}

	public void generateParserDelegateInterface(ModelType type, String outputDirectory) throws Exception {
		this.getIo().createDirectory(outputDirectory);
		VelocityContext context = new VelocityContext();
		context.put("type", type);
		context.put("helper", new ObjcVelocityHelper());
		Template template = Velocity.getTemplate("/templates/template_objc_type_parser.h.vm");
		String fileName = outputDirectory + "/" + type.getName() + "ParserDelegate.h";
		FileWriter writer = new FileWriter(fileName);
		template.merge(context, writer);
		writer.flush();
		writer.close();
	}

	public void generateParserDelegateImpl(ModelType type, String outputDirectory) throws Exception {
		this.getIo().createDirectory(outputDirectory);
		VelocityContext context = new VelocityContext();
		context.put("type", type);
		context.put("helper", new ObjcVelocityHelper());
		Template template = Velocity.getTemplate("/templates/template_objc_type_parser.m.vm");
		String fileName = outputDirectory + "/" + type.getName() + "ParserDelegate.m";
		FileWriter writer = new FileWriter(fileName);
		template.merge(context, writer);
		writer.flush();
		writer.close();
	}

	public void generateXmlSerializerInterface(ModelDefinitionsType definitions, String outputDirectory) throws Exception {
		this.getIo().createDirectory(outputDirectory);
		VelocityContext context = new VelocityContext();
		context.put("definitions", definitions);
		context.put("helper", new ObjcVelocityHelper());
		Template template = Velocity.getTemplate("/templates/template_objc_XmlSerializer.h.vm");
		String fileName = outputDirectory + "/XmlSerializer.h";
		FileWriter writer = new FileWriter(fileName);
		template.merge(context, writer);
		writer.flush();
		writer.close();
	}

	public void generateXmlSerializerImpl(ModelDefinitionsType definitions, String outputDirectory) throws Exception {
		this.getIo().createDirectory(outputDirectory);
		VelocityContext context = new VelocityContext();
		context.put("definitions", definitions);
		context.put("helper", new ObjcVelocityHelper());
		Template template = Velocity.getTemplate("/templates/template_objc_XmlSerializer.m.vm");
		String fileName = outputDirectory + "/XmlSerializer.m";
		FileWriter writer = new FileWriter(fileName);
		template.merge(context, writer);
		writer.flush();
		writer.close();
	}

	public void generateControllerStubInterface(ControllerType controller, ControllerDefinitionsType controllerDefinitions, ModelDefinitionsType modelDefinitions, String outputDirectory) throws Exception {
		this.getIo().createDirectory(outputDirectory);
		VelocityContext context = new VelocityContext();
		context.put("controller", controller);
		context.put("modelDefinitions", modelDefinitions);
		context.put("helper", new ObjcVelocityHelper());
		Template template = Velocity.getTemplate("/templates/template_objc_controller_stub.h.vm");
		String fileName = outputDirectory + "/" + controller.getName() + "Stub.h";
		FileWriter writer = new FileWriter(fileName);
		template.merge(context, writer);
		writer.flush();
		writer.close();
	}
	
	public void generateControllerStubImpl(ControllerType controller, ModelDefinitionsType definitions, String outputDirectory) throws Exception {
		this.getIo().createDirectory(outputDirectory);
		VelocityContext context = new VelocityContext();
		context.put("controller", controller);
		context.put("definitions", definitions);
		context.put("helper", new ObjcVelocityHelper());
		Template template = Velocity.getTemplate("/templates/template_objc_controller_stub.m.vm");
		String fileName = outputDirectory + "/" + controller.getName() + "Stub.m";
		FileWriter writer = new FileWriter(fileName);
		template.merge(context, writer);
		writer.flush();
		writer.close();
	}
	
	public void generateJsonDeserializerInterface(ModelDefinitionsType definitions, String outputDirectory) throws Exception {
		this.getIo().createDirectory(outputDirectory);
		VelocityContext context = new VelocityContext();
		context.put("definitions", definitions);
		context.put("helper", new ObjcVelocityHelper());
		Template template = Velocity.getTemplate("/templates/template_objc_JsonDeserializer.h.vm");
		String fileName = outputDirectory + "/JsonDeserializer.h";
		FileWriter writer = new FileWriter(fileName);
		template.merge(context, writer);
		writer.flush();
		writer.close();
	}

	public void generateJsonDeserializerImpl(ModelDefinitionsType definitions, String outputDirectory) throws Exception {
		this.getIo().createDirectory(outputDirectory);
		VelocityContext context = new VelocityContext();
		context.put("definitions", definitions);
		context.put("helper", new ObjcVelocityHelper());
		Template template = Velocity.getTemplate("/templates/template_objc_JsonDeserializer.m.vm");
		String fileName = outputDirectory + "/JsonDeserializer.m";
		FileWriter writer = new FileWriter(fileName);
		template.merge(context, writer);
		writer.flush();
		writer.close();
	}

	public void generateJsonSerializerInterface(ModelDefinitionsType definitions, String outputDirectory) throws Exception {
		this.getIo().createDirectory(outputDirectory);
		VelocityContext context = new VelocityContext();
		context.put("definitions", definitions);
		context.put("helper", new ObjcVelocityHelper());
		Template template = Velocity.getTemplate("/templates/template_objc_JsonSerializer.h.vm");
		String fileName = outputDirectory + "/JsonSerializer.h";
		FileWriter writer = new FileWriter(fileName);
		template.merge(context, writer);
		writer.flush();
		writer.close();
	}

	public void generateJsonSerializerImpl(ModelDefinitionsType definitions, String outputDirectory) throws Exception {
		this.getIo().createDirectory(outputDirectory);
		VelocityContext context = new VelocityContext();
		context.put("definitions", definitions);
		context.put("helper", new ObjcVelocityHelper());
		Template template = Velocity.getTemplate("/templates/template_objc_JsonSerializer.m.vm");
		String fileName = outputDirectory + "/JsonSerializer.m";
		FileWriter writer = new FileWriter(fileName);
		template.merge(context, writer);
		writer.flush();
		writer.close();
	}

	private IOUtility getIo() {
		return io;
	}

	private void setIo(IOUtility io) {
		this.io = io;
	}

}
