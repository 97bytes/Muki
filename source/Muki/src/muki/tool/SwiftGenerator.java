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

import java.io.FileWriter;
import java.net.URL;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import muki.tool.model.ControllerDefinitionsType;
import muki.tool.model.ControllerType;
import muki.tool.model.ModelDefinitionsType;
import muki.tool.model.ModelType;
import muki.tool.model.Project;

/**
 * This class generates the following Objective-C artifacts for a Cocoa restful client:
 * - Interfaces and implementation classes of the types defined in the project
 * - Delegate classes to parse XML
 * - XmlOuputter class to serialize to XML
 * - Support classes used  by the implementation: XmlAttribute and ObjectParserDelegate
 * - Client Stub to access the remote restful service
 */
public class SwiftGenerator {

	private IOUtility io;
	
	public SwiftGenerator() throws Exception {
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
			this.generateType(aType, outputDirectory);
			this.generateParserDelegate(aType, outputDirectory);
		}
		ControllerDefinitionsType allControllers = project.getControllerDefinitions();
		for (ControllerType aController : allControllers.getController()) {
			this.generateControllerStub(aController, allTypes, outputDirectory);
		}
		this.generateSupportClasses(outputDirectory);
		this.generateXmlSerializer(allTypes, outputDirectory);
		this.generateJsonSerializer(allTypes, outputDirectory);
		this.generateJsonDeserializer(allTypes, outputDirectory);
	}

	private IOUtility getIo() {
		return io;
	}

	private void setIo(IOUtility io) {
		this.io = io;
	}

	public void generateType(ModelType type, String outputDirectory) throws Exception {
		this.getIo().createDirectory(outputDirectory);
		VelocityContext context = new VelocityContext();
		context.put("type", type);
		context.put("helper", new SwiftVelocityHelper());
		Template template = Velocity.getTemplate("/templates/template_swift_type.vm");
		String fileName = outputDirectory + "/" + type.getName() + ".swift";
		FileWriter writer = new FileWriter(fileName);
		template.merge(context, writer);
		writer.flush();
		writer.close();
	}
		
	public void generateSupportClasses(String outputDirectory) throws Exception {
		this.getIo().createDirectory(outputDirectory);
		URL url = this.getClass().getResource("/templates/template_swift_ObjectParserDelegate.vm");
		byte[] data = this.getIo().readBytes(url);
		this.getIo().writeBinaryFile(outputDirectory + "/ObjectParserDelegate.swift", data);

		url = this.getClass().getResource("/templates/template_swift_XmlAttribute.vm");
		data = this.getIo().readBytes(url);
		this.getIo().writeBinaryFile(outputDirectory + "/XmlAttribute.swift", data);
		
		url = this.getClass().getResource("/templates/template_swift_MukiControllerStub.vm");
		data = this.getIo().readBytes(url);
		this.getIo().writeBinaryFile(outputDirectory + "/MukiControllerStub.swift", data);
		
		url = this.getClass().getResource("/templates/SwiftyJSON.swift");
		data = this.getIo().readBytes(url);
		this.getIo().writeBinaryFile(outputDirectory + "/SwiftyJSON.swift", data);		
	}

	public void generateParserDelegate(ModelType type, String outputDirectory) throws Exception {
		this.getIo().createDirectory(outputDirectory);
		VelocityContext context = new VelocityContext();
		context.put("type", type);
		context.put("helper", new SwiftVelocityHelper());
		Template template = Velocity.getTemplate("/templates/template_swift_type_parser.vm");
		String fileName = outputDirectory + "/" + type.getName() + "ParserDelegate.swift";
		FileWriter writer = new FileWriter(fileName);
		template.merge(context, writer);
		writer.flush();
		writer.close();
	}

	public void generateXmlSerializer(ModelDefinitionsType definitions, String outputDirectory) throws Exception {
		this.getIo().createDirectory(outputDirectory);
		VelocityContext context = new VelocityContext();
		context.put("definitions", definitions);
		context.put("helper", new SwiftVelocityHelper());
		Template template = Velocity.getTemplate("/templates/template_swift_XmlSerializer.vm");
		String fileName = outputDirectory + "/XmlSerializer.swift";
		FileWriter writer = new FileWriter(fileName);
		template.merge(context, writer);
		writer.flush();
		writer.close();
	}

	public void generateControllerStub(ControllerType controller, ModelDefinitionsType definitions, String outputDirectory) throws Exception {
		this.getIo().createDirectory(outputDirectory);
		VelocityContext context = new VelocityContext();
		context.put("controller", controller);
		context.put("definitions", definitions);
		context.put("helper", new SwiftVelocityHelper());
		Template template = Velocity.getTemplate("/templates/template_swift_Controller_stub.vm");
		String fileName = outputDirectory + "/" + controller.getName() + "Stub.swift";
		FileWriter writer = new FileWriter(fileName);
		template.merge(context, writer);
		writer.flush();
		writer.close();
	}
		
	public void generateJsonDeserializer(ModelDefinitionsType definitions, String outputDirectory) throws Exception {
		this.getIo().createDirectory(outputDirectory);
		VelocityContext context = new VelocityContext();
		context.put("definitions", definitions);
		context.put("helper", new SwiftVelocityHelper());
		Template template = Velocity.getTemplate("/templates/template_swift_JsonDeserializer.vm");
		String fileName = outputDirectory + "/JsonDeserializer.swift";
		FileWriter writer = new FileWriter(fileName);
		template.merge(context, writer);
		writer.flush();
		writer.close();
	}

	public void generateJsonSerializer(ModelDefinitionsType definitions, String outputDirectory) throws Exception {
		this.getIo().createDirectory(outputDirectory);
		VelocityContext context = new VelocityContext();
		context.put("definitions", definitions);
		context.put("helper", new SwiftVelocityHelper());
		Template template = Velocity.getTemplate("/templates/template_swift_JsonSerializer.vm");
		String fileName = outputDirectory + "/JsonSerializer.swift";
		FileWriter writer = new FileWriter(fileName);
		template.merge(context, writer);
		writer.flush();
		writer.close();
	}

		
}
