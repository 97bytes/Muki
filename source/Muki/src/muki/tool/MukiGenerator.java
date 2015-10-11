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

import muki.tool.model.Project;

/**
 * This class coordinates all the code generation process to automatically build support classes to implement
 * a complete application that allows a Cocoa client to communicate with a Java server application through
 * rest web-services. Client and server exchange XML documents during the communication.
 * 
 * So this class' mission is to orchestrate the code generation process, building all the support classes required both by
 * the Java server application and the Cocoa client. 
 * 
 * The input to the generation process is the description of the service and data structures in a model provided in an XML file.
 * The model contains the definition of the service operations and object structures.
 * 
 * The code generation process takes the model and generates the following artifacts:
 * 
 * - SERVER SIDE: Java beans (models) and resource implementation classes to build a restful web-service. The generated beans include XML annotations
 * for serialization using JaxB. The resource interfaces and implementation follows the Rest-WS standard and works with Resteasy (JBoss
 * implementation of the standard)
 * 
 * - CLIENT SIDE: Objective-C type classes (equivalent to Java beans), support to marshal/unmarshal XML data, service 
 * stub classes for the communication with the Java server using restful web-services.
 * 
 */
public class MukiGenerator {
	
	public static String GENERATE_JAVA = "generate-java";
	public static String GENERATE_OBJC = "generate-objc";
	public static String GENERATE_SWIFT = "generate-swift";
	public static String COMMAND_HELP = "MukiGenerator <generate-java|generate-objc|generate-swift> <path-to-project.xml> <output directory>";
	private Project project;
	private String outputDirectory;
	private IOUtility io;
	
	public MukiGenerator() {
		this.setIo(new IOUtility());
	}

	public static void main(String[] args) throws Exception {
		MukiGenerator generator = new MukiGenerator();
		ExecutionResult result = new ExecutionResult();
		result.setOk(true);
		generator.run(args, result);
		System.out.print("*** Muki v" + Version.id() + " Created by Gabriel Casarini ***");
		System.out.println(result.getLog());
	}

	public void run(String[] args, ExecutionResult result) throws Exception {
		if(args != null && args.length == 1) {
			String value = args[0].trim().toLowerCase();
			if(value.equals("help") || value.equals("?")) {
				result.append("Usage: " + COMMAND_HELP);
				result.setOk(false);
				return;
			}
		}
		if (args == null || args.length != 3) {
			result.append("Usage: " + COMMAND_HELP);
			result.setOk(false);
			return;
		}
		String option = args[0];
		String projectFile = args[1];
		String outputDirectory = args[2];
		this.run(option, projectFile, outputDirectory, result);
	}
	
	public void run(String option, String projectFile, String outputDirectory, ExecutionResult result) throws Exception {
		if (option == null || (!option.equals(GENERATE_JAVA) && !option.equals(GENERATE_OBJC) && !option.equals(GENERATE_SWIFT))) {
			result.append("-> Invalid option! The command line is:");
			result.append(COMMAND_HELP);
			result.setOk(false);
			return;
		}
		if(projectFile == null || !this.getIo().existsFile(projectFile)) {
			result.append("-> The project file doesn't exists: " + projectFile);
			result.setOk(false);
			return;
		}
		if(outputDirectory == null || !this.getIo().existsFile(outputDirectory)) {
			result.append("-> The output directory doesn't exists: " + outputDirectory);
			result.setOk(false);
			return;
		}
		ModelUtility modelUtility = new ModelUtility();
		Project newProject = modelUtility.openProject(projectFile);
		this.setProject(newProject);
		this.setOutputDirectory(outputDirectory);
		if(option.equals(GENERATE_JAVA)) {
			this.generateJava(result);
			return;
		}
		if(option.equals(GENERATE_OBJC)) {
			this.generateObjC(result);
			return;
		}
		if(option.equals(GENERATE_SWIFT)) {
			this.generateSwift(result);
			return;
		}
	}
	
	public void generateJava(ExecutionResult result) {
		this.validateModel(result);
		if(!result.isOk()) {
			return;
		}
		this.getIo().deleteDirectory(this.getOutputDirectory());
		result.append("-> Generating Java classes...");
		try {
			JavaGenerator generator = new JavaGenerator();
			generator.generateAll(this.getProject(), this.getOutputDirectory());
			result.append("-> *** Code generation OK ***");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public void generateObjC(ExecutionResult result) throws Exception {
		this.validateModel(result);
		if(!result.isOk()) {
			return;
		}
		this.getIo().deleteDirectory(this.getOutputDirectory());
		result.append("-> Generating Objective-C classes...");
		try {
			ObjcGenerator generator = new ObjcGenerator();
			generator.generateAll(this.getProject(), this.getOutputDirectory());
			result.append("-> *** Code generation OK ***");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public void generateSwift(ExecutionResult result) throws Exception {
		this.validateModel(result);
		if(!result.isOk()) {
			return;
		}
		this.getIo().deleteDirectory(this.getOutputDirectory());
		result.append("-> Generating Swift classes...");
		try {
			SwiftGenerator generator = new SwiftGenerator();
			generator.generateAll(this.getProject(), this.getOutputDirectory());
			result.append("-> *** Code generation OK ***");
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Validates the model and adds error messages to the ExecutionResult
	 */
	private void validateModel(ExecutionResult result) {
		result.append("-> Validating the project...");
		ModelUtility modelUtility = new ModelUtility();
		modelUtility.validate(this.getProject(), result);
		if(result.isOk()) {
			result.append("-> Validation is OK");
		} else {
			result.append("-> *** ERROR: The project validation FAILED! See messages above. ***");
			result.append("-> *** You need to fix the issues before code can be generated.  ***");
		}
	}
	
	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public String getOutputDirectory() {
		return outputDirectory;
	}

	public void setOutputDirectory(String outputDirectory) {
		this.outputDirectory = outputDirectory;
	}

	private IOUtility getIo() {
		return io;
	}

	private void setIo(IOUtility io) {
		this.io = io;
	}
	
}
