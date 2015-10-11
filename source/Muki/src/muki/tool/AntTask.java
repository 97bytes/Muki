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

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/**
 * This class implements a task that can be invoked from an Ant script
 * to generate Java and Objective-C classes. The task itself simply takes the arguments and delegates to
 * an instance of MukiGenerator. Invocation example:
 * 
 * <target name="generate">
 *   <path id="tools.classpath">
 *     <fileset dir="${project.dir}/lib">
 *       <include name="*.jar" />
 *     </fileset>
 *   </path>
 *   <taskdef name="muki-generator" classpathref="tools.classpath" classname="muki.tool.AntTask"/>
 *   <muki-generator option="generate-java" projectFile="c:/temp/project.xml" outputDirectory="c:/project/generated" />
 * </target>
 */
public class AntTask extends Task {
	
	private String option;
	private String projectFile;
	private String outputDirectory;
	
	public AntTask() {
	}
	
	public void execute() throws BuildException {
		ExecutionResult result = new ExecutionResult();
		MukiGenerator generator = new MukiGenerator();
		System.out.println();
		System.out.println("*** Muki v" + Version.id() + " Created by Gabriel Casarini ***");		
		System.out.print("*** STARTING ***");
		try {
			generator.run(this.getOption(), this.getProjectFile(), this.getOutputDirectory(), result);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BuildException(e);
		}
		System.out.println(result.getLog());
		System.out.println("*** FINISHED ***");
	}
	
 	public String getOption() {
		return option;
	}
	public void setOption(String option) {
		this.option = option;
	}
	public String getProjectFile() {
		return projectFile;
	}
	public void setProjectFile(String projectFile) {
		this.projectFile = projectFile;
	}
	public String getOutputDirectory() {
		return outputDirectory;
	}
	public void setOutputDirectory(String outputDirectory) {
		this.outputDirectory = outputDirectory;
	}

}
