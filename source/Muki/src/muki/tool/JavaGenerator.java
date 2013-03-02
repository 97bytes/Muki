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

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import muki.tool.model.ModelDefinitionsType;
import muki.tool.model.ModelType;
import muki.tool.model.Project;
import muki.tool.model.ControllerDefinitionsType;
import muki.tool.model.ControllerType;

/**
 * This class generates the following Java artifacts to implement a Restful server:
 * - Java beans (models) with Jaxb annotations for XML serialization/deserialization using JaxB
 * - Java controller interfaces
 * - Java controller skeleton implementation. This skeleton passes the invocations to a delegate object.
 */
public class JavaGenerator {

	private IOUtility io;

	public JavaGenerator() throws Exception {
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
			this.generateModelClass(aType, outputDirectory, allTypes.getJavaPackage());
		}
		ControllerDefinitionsType allControllers = project.getControllerDefinitions();
		for (ControllerType aController : allControllers.getController()) {
			this.generateControllerClass(aController, outputDirectory, allControllers.getJavaPackage(), allTypes.getJavaPackage());
			this.generateDelegateInterface(aController, outputDirectory, allControllers.getJavaPackage(), allTypes.getJavaPackage());
		}
		this.generateRestApplicationClass(allControllers, outputDirectory);
		this.generateExceptionClass(outputDirectory, allControllers.getJavaPackage());
	}
	
	public void generateModelClass(ModelType type, String outputDirectory, String javaPackage) throws Exception {
		VelocityContext context = new VelocityContext();
		context.put("type", type);
		context.put("javaPackage", javaPackage);
		context.put("helper", new JavaVelocityHelper());
		Template template = Velocity.getTemplate("/templates/template_java_bean.vm");
		String targetPath = this.createPackageDirectory(outputDirectory, javaPackage);
		String fileName = targetPath + "/" + type.getName() + ".java";
		FileWriter writer = new FileWriter(fileName);
		template.merge(context, writer);
		writer.flush();
		writer.close();
	}
	
	public void generateControllerClass(ControllerType controller, String outputDirectory, String javaPackage, String typesJavaPackage) throws Exception {
		VelocityContext context = new VelocityContext();
		context.put("controller", controller);
		context.put("javaPackage", javaPackage);
		context.put("typesJavaPackage", typesJavaPackage);
		context.put("helper", new JavaVelocityHelper());
		Template template = Velocity.getTemplate("/templates/template_java_controller.vm");
		String targetPath = this.createPackageDirectory(outputDirectory, javaPackage);
		String fileName = targetPath + "/" + controller.getName() + ".java";
		FileWriter writer = new FileWriter(fileName);
		template.merge(context, writer);
		writer.flush();
		writer.close();
	}
		
	public void generateRestApplicationClass(ControllerDefinitionsType controllers, String outputDirectory) throws Exception {
		VelocityContext context = new VelocityContext();
		context.put("controllers", controllers);
		context.put("javaPackage", controllers.getJavaPackage());
		context.put("helper", new JavaVelocityHelper());
		Template template = Velocity.getTemplate("/templates/template_java_rest_application.vm");
		String targetPath = this.createPackageDirectory(outputDirectory, controllers.getJavaPackage());
		String fileName = targetPath + "/RestApplication.java";
		FileWriter writer = new FileWriter(fileName);
		template.merge(context, writer);
		writer.flush();
		writer.close();
	}

	public void generateDelegateInterface(ControllerType controller, String outputDirectory, String javaPackage, String typesJavaPackage) throws Exception {
		VelocityContext context = new VelocityContext();
		context.put("controller", controller);
		context.put("javaPackage", javaPackage);
		context.put("typesJavaPackage", typesJavaPackage);
		context.put("helper", new JavaVelocityHelper());
		Template template = Velocity.getTemplate("/templates/template_java_delegate.vm");
		String targetPath = this.createPackageDirectory(outputDirectory, javaPackage);
		String fileName = targetPath + "/" + controller.getName() + "Delegate.java";
		FileWriter writer = new FileWriter(fileName);
		template.merge(context, writer);
		writer.flush();
		writer.close();
	}
	
	public void generateExceptionClass(String outputDirectory, String javaPackage) throws Exception {
		VelocityContext context = new VelocityContext();
		context.put("javaPackage", javaPackage);
		context.put("helper", new JavaVelocityHelper());
		// Exception class
		Template template = Velocity.getTemplate("/templates/template_java_MukiResourceNotFoundException.vm");
		String targetPath = this.createPackageDirectory(outputDirectory, javaPackage);
		String fileName = targetPath + "/" + "MukiResourceNotFoundException.java";
		FileWriter writer = new FileWriter(fileName);
		template.merge(context, writer);
		writer.flush();
		writer.close();
		// Mapper class
		template = Velocity.getTemplate("/templates/template_java_MukiExceptionMapper.vm");
		fileName = targetPath + "/" + "MukiExceptionMapper.java";
		writer = new FileWriter(fileName);
		template.merge(context, writer);
		writer.flush();
		writer.close();
	}
			
	private String createPackageDirectory(String outputDirectory, String javaPackage) {
		String targetPath = outputDirectory + "/" + javaPackage.replace('.', '/');
		this.getIo().createDirectory(targetPath);
		return targetPath;
	}

	private IOUtility getIo() {
		return io;
	}

	private void setIo(IOUtility io) {
		this.io = io;
	}

		
}
