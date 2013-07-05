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

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import muki.tool.model.DeleteOperationType;
import muki.tool.model.GetOperationType;
import muki.tool.model.ListAttrType;
import muki.tool.model.ModelDefinitionsType;
import muki.tool.model.ModelType;
import muki.tool.model.PathParamType;
import muki.tool.model.PostOperationType;
import muki.tool.model.Project;
import muki.tool.model.PutOperationType;
import muki.tool.model.SimpleAttrType;
import muki.tool.model.ControllerDefinitionsType;
import muki.tool.model.ControllerType;

/**
 * This class is a utility used to validate the model after it has been uploaded from an XML file.
 * It also implements some functionality used by the Velocity helpers to generate code fragments.
 * This implements the mapping from the basic types used to define the model (INT, LONG, STRING, BOOLEAN, DOUBLE) to
 * specific Java primitive types (int, long, String, boolean, double) and Objective-C (int, long long, NSString, BOOL, double)
 */
public class ModelUtility {
	
	public static String STRING_TYPE = "STRING";
	public static String LONG_TYPE = "LONG";
	public static String INTEGER_TYPE = "INT";
	public static String DOUBLE_TYPE = "DOUBLE";
	public static String BOOLEAN_TYPE = "BOOLEAN";
	private Map<String, String> javaTypesMapping;
	private Map<String, String> objcTypesMapping;
	private List<String> basicTypes;
	private Project project;

	public ModelUtility() {
		Map<String, String> mapping = new HashMap<String, String>();
		mapping.put(STRING_TYPE, "String");
		mapping.put(LONG_TYPE, "long");
		mapping.put(INTEGER_TYPE, "int");
		mapping.put(DOUBLE_TYPE, "double");
		mapping.put(BOOLEAN_TYPE, "boolean");
		this.setJavaTypesMapping(mapping);
		
		mapping = new HashMap<String, String>();
		mapping.put(STRING_TYPE, "NSString");
		mapping.put(LONG_TYPE, "long long");
		mapping.put(INTEGER_TYPE, "NSInteger");
		mapping.put(DOUBLE_TYPE, "double");
		mapping.put(BOOLEAN_TYPE, "BOOL");
		this.setObjcTypesMapping(mapping);
		List<String> types = new ArrayList<String>();
		types.add(STRING_TYPE);
		types.add(LONG_TYPE);
		types.add(INTEGER_TYPE);
		types.add(DOUBLE_TYPE);
		types.add(BOOLEAN_TYPE);
		this.setBasicTypes(types);
	}
	
	/**
	 * Opens the XML file with the project definition and instanciates the model with
	 * objects. The project is returned.
	 */
	public Project openProject(String fileName) throws Exception {
		JAXBContext context = JAXBContext.newInstance(Project.class);
		Unmarshaller unmarshaller = context.createUnmarshaller();
		Project newProject = (Project)unmarshaller.unmarshal(new File(fileName));
		return newProject;
	}
	
	/**
	 * Saves the object model of a project to a XML file.
	 * Note that the directory path must be created, otherwise an exception is thrown when
	 * writing the file to the disk.
	 */
	public void saveProject(Project project, String fileName) throws Exception {	
		JAXBContext context = JAXBContext.newInstance(Project.class);
		Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.FALSE);
		FileWriter writer = new FileWriter(fileName);
		marshaller.marshal(project, writer);
	}
	
	/**
	 * Validates the model checking the constraints. Ex: the project needs a name, the GET operations must
	 * return value, etc.
	 * If no constraint is violated, then we return null. Otherwise, return a string with a list of
	 * constraints that have been violated.
	 */
	public void validate(Project aProject, ExecutionResult result) {
		this.setProject(aProject);
		if(this.isUndefined(aProject.getName())) {
			result.append("The project name is undefined");
			result.setOk(false);
		}
		if(aProject.getModelDefinitions() == null) {
			result.append("The project doesn't have model definitions");
			result.setOk(false);
		}
		if(aProject.getControllerDefinitions() == null) {
			result.append("The project doesn't have controller definitions");
			result.setOk(false);
		}
		this.validate(aProject.getModelDefinitions(), result);
		this.validate(aProject.getControllerDefinitions(), result);
	}
	
	private void validate(ControllerDefinitionsType definitions, ExecutionResult result) {
		result.append("-> Validating controllers...");
		if(this.isUndefined(definitions.getJavaPackage())) {
			result.append("The Java package is undefined for the controller definitions");
			result.setOk(false);
		}
		if(this.isUndefined(definitions.getJavaPackage())) {
			result.append("The Java package is undefined for the controller definitions");
			result.setOk(false);
		}
		for (int i = 0; i < definitions.getController().size(); i++) {
			ControllerType controller = definitions.getController().get(i);
			this.validate(controller, i + 1, result);
		}
	}
	
	private void validate(ControllerType controller, int index, ExecutionResult result) {
		result.append("-> Validating controller (" + index + ") ...");
		if(this.isUndefined(controller.getName())) {
			result.append("The name for controller (" + index + ") is undefined");
			result.setOk(false);
		}
		if(this.isUndefined(controller.getHttpPath())) {
			result.append("The HTTP path for controller (" + index + ") is undefined");
			result.setOk(false);
		}
		for (int i = 0; i < controller.getGetOperation().size(); i++) {
			GetOperationType operation = controller.getGetOperation().get(i);
			this.validate(operation, i + 1, result);
		}
		for (int i = 0; i < controller.getPostOperation().size(); i++) {
			PostOperationType operation = controller.getPostOperation().get(i);
			this.validate(operation, i + 1, result);
		}
		for (int i = 0; i < controller.getPutOperation().size(); i++) {
			PutOperationType operation = controller.getPutOperation().get(i);
			this.validate(operation, i + 1, result);
		}
		for (int i = 0; i < controller.getDeleteOperation().size(); i++) {
			DeleteOperationType operation = controller.getDeleteOperation().get(i);
			this.validate(operation, i + 1, result);
		}
	}
	
	private void validate(GetOperationType operation, int index, ExecutionResult result) {
		result.append("-> Validating GET operation (" + index + ") ...");
		if(this.isUndefined(operation.getName())) {
			result.append("The name of the GET operation (" + index + ") is undefined");
			result.setOk(false);
		}
		if(this.isUndefined(operation.getHttpPath())) {
			result.append("The HTTP path of the GET operation (" + index + ") is undefined");
			result.setOk(false);
		}
		String returnType = operation.getReturnType();
		if(this.isUndefined(returnType)) {
			result.append("The return type of the GET operation (" + index + ") is undefined");
			result.setOk(false);
		} else if(!this.isStringType(returnType) && !this.existsComplexType(returnType)) {
			result.append("The return type of the GET operation (" + index + ") must be STRING or a model defined in the project");			
			result.setOk(false);
		} else if(this.isComplexType(returnType) && !this.isXmlSerialization(operation.getSerializationType()) && !this.isJsonSerialization(operation.getSerializationType())) {
			result.append("The serialization type of the GET operation (" + index + ") must be XML or JSON");			
			result.setOk(false);
		}
		for (PathParamType param : operation.getPathParam()) {
			if(operation.getHttpPath().indexOf("{" + param.getName() + "}") < 0) {
				result.append("The param " + param.getName() + " in GET operation (" + index + ") is not declared in the HTTP Path");			
				result.setOk(false);
			}
		}
	}

	private void validate(PostOperationType operation, int index, ExecutionResult result) {
		result.append("-> Validating POST operation (" + index + ") ...");
		if(this.isUndefined(operation.getName())) {
			result.append("The name of the POST operation (" +index + ") is undefined");
			result.setOk(false);
		}
		if(this.isUndefined(operation.getHttpPath())) {
			result.append("The HTTP path of the POST operation (" +index + ") is undefined");
			result.setOk(false);
		}
		String returnType = operation.getReturnType();
		if(!this.isUndefined(returnType)) {
			if(!this.isStringType(returnType) && !this.existsComplexType(returnType)) {
				result.append("The return type of the POST operation (" + index + ") must be STRING or a model defined in the project");			
				result.setOk(false);
			}
		}
		String paramType = operation.getParamType();
		if(operation.getFormParam() != null && !operation.getFormParam().isEmpty() && !this.isUndefined(paramType)) {
			result.append("The POST operation (" + index + ") has form parameters and also a model type param. Only one is allowed.");
			result.setOk(false);
		}
		if(!this.isUndefined(paramType)) {
			if(!this.isStringType(paramType) && !this.existsComplexType(paramType)) {
				result.append("The parameter type of the POST operation (" + index + ") must be STRING or a model defined in the project");
				result.setOk(false);
			}
		}
		if((!this.isUndefined(paramType) && this.isComplexType(paramType)) || (!this.isUndefined(returnType) && this.isComplexType(returnType))) {
			if (!this.isXmlSerialization(operation.getSerializationType()) && !this.isJsonSerialization(operation.getSerializationType())) {			
				result.append("The serialization type of the POST operation (" + index + ") must be XML or JSON");			
				result.setOk(false);
			}
		}
		for (PathParamType param : operation.getPathParam()) {
			if(operation.getHttpPath().indexOf("{" + param.getName() + "}") < 0) {
				result.append("The param " + param.getName() + " in POST operation (" + index + ") is not declared in the HTTP Path");			
				result.setOk(false);
			}
		}
	}

	private void validate(PutOperationType operation, int index, ExecutionResult result) {
		result.append("-> Validating PUT operation (" + index + ") ...");
		if(this.isUndefined(operation.getName())) {
			result.append("The name of the PUT operation (" +index + ") is undefined");
			result.setOk(false);
		}
		if(this.isUndefined(operation.getHttpPath())) {
			result.append("The HTTP path of the PUT operation (" +index + ") is undefined");
			result.setOk(false);
		}
		String returnType = operation.getReturnType();
		if(!this.isUndefined(returnType)) {
			if(!this.isStringType(returnType) && !this.existsComplexType(returnType)) {
				result.append("The return type of the PUT operation (" + index + ") must be STRING or a model defined in the project");			
				result.setOk(false);
			}
		}
		String paramType = operation.getParamType();
		if(operation.getFormParam() != null && !operation.getFormParam().isEmpty() && !this.isUndefined(paramType)) {
			result.append("The PUT operation (" + index + ") has form parameters and also a model type param. Only one is allowed.");
			result.setOk(false);
		}
		if(!this.isUndefined(paramType)) {
			if(!this.isStringType(paramType) && !this.existsComplexType(paramType)) {
				result.append("The parameter type of the PUT operation (" + index + ") must be STRING or a model defined in the project");
				result.setOk(false);
			}
		}
		if((!this.isUndefined(paramType) && this.isComplexType(paramType)) || (!this.isUndefined(returnType) && this.isComplexType(returnType))) {
			if (!this.isXmlSerialization(operation.getSerializationType()) && !this.isJsonSerialization(operation.getSerializationType())) {			
				result.append("The serialization type of the PUT operation (" + index + ") must be XML or JSON");			
				result.setOk(false);
			}
		}
		for (PathParamType param : operation.getPathParam()) {
			if(operation.getHttpPath().indexOf("{" + param.getName() + "}") < 0) {
				result.append("The param " + param.getName() + " in PUT operation (" + index + ") is not declared in the HTTP Path");			
				result.setOk(false);
			}
		}
	}

	private void validate(DeleteOperationType operation, int index, ExecutionResult result) {
		result.append("-> Validating DELETE operation (" + index + ") ...");
		if(this.isUndefined(operation.getName())) {
			result.append("The name of the DELETE operation (" + index + ") is undefined");
			result.setOk(false);
		}
		if(this.isUndefined(operation.getHttpPath())) {
			result.append("The HTTP path of the DELETE operation (" + index + ") is undefined");
			result.setOk(false);
		}
		for (PathParamType param : operation.getPathParam()) {
			if(operation.getHttpPath().indexOf("{" + param.getName() + "}") < 0) {
				result.append("The param " + param.getName() + " in DELETE operation (" + index + ") is not declared in the HTTP Path");			
				result.setOk(false);
			}
		}
	}

	private void validate(ModelDefinitionsType definitions, ExecutionResult result) {
		result.append("-> Validating models ...");
		if(this.isUndefined(definitions.getJavaPackage())) {
			result.append("The Java package is undefined for the model definitions");
			result.setOk(false);
		}
		for (int i = 0; i < definitions.getModel().size(); i++) {
			ModelType type = definitions.getModel().get(i);
			this.validate(type, i + 1, result);
		}
	}
	
	private void validate(ModelType type, int index, ExecutionResult result) {
		result.append("-> Validating model (" + index + ") ...");
		if(this.isUndefined(type.getName())) {
			result.append("The name of the model (" + index + ") is undefined");
			result.setOk(false);
		}
		for (int i = 0; i < type.getSimpleAttr().size(); i++) {
			SimpleAttrType attribute = type.getSimpleAttr().get(i);
			this.validate(attribute, i + 1, result);
		}
		for (int i = 0; i < type.getListAttr().size(); i++) {
			ListAttrType attribute = type.getListAttr().get(i);
			this.validate(attribute, i + 1, result);
		}
	}
	
	private void validate(SimpleAttrType attribute, int index, ExecutionResult result) {
		result.append("-> Validating simple attribute (" + index + ") ...");
		if(this.isUndefined(attribute.getName())) {
			result.append("The name of the simple attribute (" + index + ") is undefined");
			result.setOk(false);
		}
		if(this.isUndefined(attribute.getType())) {
			result.append("The type of the simple attribute (" + index + ") is undefined");
			result.setOk(false);
		} else if (!this.isPrimitiveType(attribute.getType()) && !this.existsComplexType(attribute.getType())) {
				result.append("The type of the simple attribute (" + index + ") must be a basic type (STRING, LONG, etc) or another model defined in the project");
				result.setOk(false);
		}
	}
	
	/**
	 * Returns true if the project includes a model definition with
	 * the name in the paramenter.
	 */
	private boolean existsComplexType(String name) {
		for (ModelType model :  this.getProject().getModelDefinitions().getModel()) {
			if (model.getName().equals(name)) {
				return true;
			}
		}
		return false;
	}
	
	private void validate(ListAttrType attribute, int index, ExecutionResult result) {
		result.append("-> Validating list attribute (" + index + ") ...");
		if(this.isUndefined(attribute.getName())) {
			result.append("The name of the list attribute (" + index + ") is undefined");
			result.setOk(false);
		}
		if(this.isUndefined(attribute.getItemsType())) {
			result.append("The items type of the list attribute (" + index + ") is undefined");
			result.setOk(false);
		}
		if(!this.existsComplexType(attribute.getItemsType())) {
			result.append("The items type of the list attribute (" + index + ") must be another model defined in the project. Basic types are not supported in lists.");
			result.setOk(false);
		}
	}
	
	private boolean isUndefined(String aString) {
		return aString == null || aString.trim().equals("");
	}
	
	private boolean isType(String modelType, String mukiType) {
		return modelType != null && modelType.equals(mukiType);
	}
	
	public boolean isStringType(String modelType) {
		return this.isType(modelType, STRING_TYPE);
	}
	
	public boolean isLongType(String modelType) {
		return this.isType(modelType, LONG_TYPE);
	}
	
	public boolean isIntegerType(String modelType) {
		return this.isType(modelType, INTEGER_TYPE);
	}
	
	public boolean isDoubleType(String modelType) {
		return this.isType(modelType, DOUBLE_TYPE);
	}
	
	public boolean isBooleanType(String modelType) {
		return this.isType(modelType, BOOLEAN_TYPE);
	}
	
	public boolean isPrimitiveType(String modelType) {
		return modelType != null && (this.isStringType(modelType) || this.isLongType(modelType) || this.isIntegerType(modelType) || this.isDoubleType(modelType) || this.isBooleanType(modelType));
	}
	
	public boolean isXmlSerialization(String serializationType) {
		return serializationType != null && serializationType.toLowerCase().equals("xml");
	}
	
	public boolean isJsonSerialization(String serializationType) {
		return serializationType != null && serializationType.toLowerCase().equals("json");
	}
	
	public boolean isComplexType(String modelType) {
		return modelType != null && !this.isPrimitiveType(modelType);
	}

	public String getJavaPrimitiveType(String typeName) {
		return this.getJavaTypesMapping().get(typeName);
	}
	
	public String getObjcPrimitiveType(String typeName) {
		return this.getObjcTypesMapping().get(typeName);
	}
	
	private Map<String, String> getJavaTypesMapping() {
		return javaTypesMapping;
	}

	private void setJavaTypesMapping(Map<String, String> javaTypesMapping) {
		this.javaTypesMapping = javaTypesMapping;
	}

	private Map<String, String> getObjcTypesMapping() {
		return objcTypesMapping;
	}

	private void setObjcTypesMapping(Map<String, String> objcTypesMapping) {
		this.objcTypesMapping = objcTypesMapping;
	}

	private List<String> getBasicTypes() {
		return basicTypes;
	}

	private void setBasicTypes(List<String> basicTypes) {
		this.basicTypes = basicTypes;
	}

	private Project getProject() {
		return project;
	}

	private void setProject(Project project) {
		this.project = project;
	}

}
