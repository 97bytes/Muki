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

import muki.tool.model.ContextParamType;
import muki.tool.model.DeleteOperationType;
import muki.tool.model.FormParamType;
import muki.tool.model.GetOperationType;
import muki.tool.model.ListAttrType;
import muki.tool.model.PathParamType;
import muki.tool.model.PostOperationType;
import muki.tool.model.PutOperationType;
import muki.tool.model.QueryParamType;
import muki.tool.model.ControllerType;
import muki.tool.model.SimpleAttrType;

/**
 * This class offers support during the code generation with Velocity templates. The templates include invocations
 * to the operations of this helper to create Java code fragments that otherwise would be complicated to generate with
 * the template itself.
 */
public class JavaVelocityHelper extends VelocityHelper {
	
	/**
	 * Returns the name of the variable capitalized, to generate the getter and setter.
	 * Example: name = "clients" -> "Clients"
	 * Then the value is used to generate: "getClients()" and "setClients()"
	 */
	public String toCapitalizedCase(String aName) {
		String firstLetter = aName.substring(0, 1).toUpperCase();
		String remain = aName.substring(1);
		return firstLetter + remain;
	}

	/**
	 * Returns the name converted to lower case letters
	 */
	public String toLowerCase(String aName) {
		return aName.toLowerCase();
	}

	private String getJavaPrimitiveType(String typeName) {
		return this.getUtility().getJavaPrimitiveType(typeName);
	}
	
	/**
	 * Returns the fragment for an attribute declaration. The attribute is a of primitive type.
	 * Example:
	 *   private String title;
	 */
	public String getAttributeDeclaration(SimpleAttrType attribute) {
		StringBuffer result = new StringBuffer();
		String typeName = attribute.getType();
		if(this.isPrimitiveType(typeName)) {
			typeName = this.getJavaPrimitiveType(typeName);
		}
		result.append("private ").append(typeName).append(" ").append(attribute.getName());
		return result.toString();
	}
	
	public String getAttributeDeclaration(ListAttrType attribute) {
		StringBuffer result = new StringBuffer();
		String typeName = attribute.getItemsType();
		result.append("private List<").append(typeName).append("> ").
			append(attribute.getName()).append(" = new ArrayList<").append(typeName).append(">()");
		return result.toString();
	}
	
	/**
	 * Returns the fragment for the getter method of a basic type attribute.
	 * Example:
	 *   @XmlElement(name = "title")
     *   public String getTitle() {
	 */
	public String getGetterMethodDeclaration(SimpleAttrType attribute) {
		StringBuffer result = new StringBuffer();
		String typeName = attribute.getType();
		String javaType = null;
		if(this.isBooleanType(typeName) || this.isDoubleType(typeName) || this.isIntegerType(typeName) || this.isLongType(typeName)) {
			result.append("@XmlAttribute(name = \"").append(attribute.getName()).append("\")\r");
			javaType = this.getJavaPrimitiveType(typeName);
		} 
		if(this.isStringType(typeName)) {
			result.append("@XmlElement(name = \"").append(attribute.getName()).append("\")\r");
			javaType = this.getJavaPrimitiveType(typeName);
		} 
		if(this.isComplexType(typeName)) {
			result.append("@XmlElement(name = \"").append(attribute.getName()).append("\")\r");
			javaType = typeName;
		} 
		result.append("    public ").append(javaType);
		if(this.isBooleanType(attribute.getType())) {
			result.append(" is");
		} else {
			result.append(" get");
		}
		result.append(this.toCapitalizedCase(attribute.getName())).append("()");
		return result.toString();
	}

	/**
	 * Returns the fragment for the setter method of a prmitive type attribute.
	 * Example:
	 *   public void setTitle(String newValue) {
	 */
	public String getSetterMethodDeclaration(SimpleAttrType attribute) {
		StringBuffer result = new StringBuffer();
		String typeName = attribute.getType();
		if(this.isPrimitiveType(typeName)) {
			typeName = this.getJavaPrimitiveType(typeName);
		}
		result.append("public void set").append(this.toCapitalizedCase(attribute.getName())).append("(").append(typeName).append(" newValue)");
		return result.toString();
	}
	
	/**
	 * Returns the declaration of a list attribute with the XML annotations.
	 * Note that the lists cannot contain primitive types (boolean, strings, int, double, long).
	 * If this is required, create a complex type to store the value inside and then create
	 * a list of items of the complex type.
	 */
	public String getGetterMethodDeclaration(ListAttrType attribute) {
		StringBuffer result = new StringBuffer();
		result.append("@XmlElementWrapper(name = \"").append(attribute.getName()).append("\")\r");
		String typeName = attribute.getItemsType();
		result.append("    @XmlElement(name = \"").append(typeName.toLowerCase()).append("\")\r");
		result.append("    public List<").append(typeName).append(">").
			append(" get").append(this.toCapitalizedCase(attribute.getName())).append("()");
		return result.toString();
	}

	public String getSetterMethodDeclaration(ListAttrType attribute) {
		StringBuffer result = new StringBuffer();
		String typeName = attribute.getItemsType();
		result.append("public void set").append(this.toCapitalizedCase(attribute.getName())).append("(List<").
			append(typeName).append("> newList)");
		return result.toString();
	}
	
	public String getAddMethodDeclaration(ListAttrType attribute) {
		StringBuffer result = new StringBuffer();
		String typeName = attribute.getItemsType();
		result.append("public void addTo").append(this.toCapitalizedCase(attribute.getName())).
			append("(").append(typeName).append(" aValue)");
		return result.toString();
	}

	public String getRemoveMethodDeclaration(ListAttrType attribute) {
		StringBuffer result = new StringBuffer();
		String typeName = attribute.getItemsType();
		result.append("public void removeFrom").append(this.toCapitalizedCase(attribute.getName())).
			append("(").append(typeName).append(" aValue)");
		return result.toString();
	}

	public String getDelegateInterfaceName(ControllerType service) {
		return service.getName() + "Delegate";
	}

	public String getHttpPath(GetOperationType operation) {
		return operation.getHttpPath();
	}

	public String getHttpPath(PostOperationType operation) {
		return operation.getHttpPath();
	}

	public String getHttpPath(PutOperationType operation) {
		return operation.getHttpPath();
	}

	public String getHttpPath(DeleteOperationType operation) {
		return operation.getHttpPath();
	}

	/**
	 * Returns the declaration of the service implementation for a GET operation
	 */
	public String getMethodDeclarationFor(GetOperationType operation) {
		StringBuffer xmlAnnotations = new StringBuffer();
		if(this.isComplexType(operation.getReturnType())) {
			if(this.isXmlSerialization(operation.getSerializationType())) {
				xmlAnnotations.append("@Produces(\"application/xml\")\n    ");
			}
			if(this.isJsonSerialization(operation.getSerializationType())) {
				xmlAnnotations.append("@Produces(\"application/json\")\n    ");
			}
		}
		StringBuffer result = new StringBuffer();
		result.append(xmlAnnotations);
		if(this.isStringType(operation.getReturnType())) {
			result.append("public String ");
		} else {
			result.append("public ").append(operation.getReturnType()).append(" ");
		}
		// Operation name
		result.append(operation.getName()).append("(");
		// Parameters
		boolean isFirst = true;
		for (PathParamType param : operation.getPathParam()) {
			if(!isFirst) {
				result.append(", ");
			}
			result.append("@PathParam(\"").append(param.getName()).append("\") String ").append(param.getName());
			isFirst = false;
		}
		for (QueryParamType param : operation.getQueryParam()) {
			if(!isFirst) {
				result.append(", ");
			}
			result.append("@QueryParam(\"").append(param.getName()).append("\") String ").append(param.getName());
			isFirst = false;
		}
		for (ContextParamType param : operation.getContextParam()) {
			if(!isFirst) {
				result.append(", ");
			}
			result.append("@Context ").append(param.getJavaClass()).append(" ").append(param.getName());
			isFirst = false;
		}
		result.append(")");
		return result.toString(); 
	}
	
	/**
	 * Returns the declaration of the service implementation for a DELETE operation
	 */
	public String getMethodDeclarationFor(DeleteOperationType operation) {
		StringBuffer result = new StringBuffer();
		// Operation name
		result.append("public void ").append(operation.getName()).append("(");
		// Parameters
		boolean isFirst = true;
		for (PathParamType param : operation.getPathParam()) {
			if(!isFirst) {
				result.append(", ");
			}
			result.append("@PathParam(\"").append(param.getName()).append("\") String ").append(param.getName());
			isFirst = false;
		}
		for (ContextParamType param : operation.getContextParam()) {
			if(!isFirst) {
				result.append(", ");
			}
			result.append("@Context ").append(param.getJavaClass()).append(" ").append(param.getName());
			isFirst = false;
		}
		for (QueryParamType param : operation.getQueryParam()) {
			if(!isFirst) {
				result.append(", ");
			}
			result.append("@QueryParam(\"").append(param.getName()).append("\") String ").append(param.getName());
			isFirst = false;
		}
		result.append(")");
		return result.toString(); 
	}
	
	/**
	 * Returns the body of the service implementation for a GET operation
	 */
	public String getMethodBodyFor(GetOperationType operation) {
		// Generate list of parameters to invoke delegate
		StringBuffer params = new StringBuffer();
		boolean isFirst = true;
		for (PathParamType param : operation.getPathParam()) {
			if(!isFirst) {
				params.append(", ");
			}
			params.append(param.getName());
			isFirst = false;
		}
		for (QueryParamType param : operation.getQueryParam()) {
			if(!isFirst) {
				params.append(", ");
			}
			params.append(param.getName());
			isFirst = false;
		}
		for (ContextParamType param : operation.getContextParam()) {
			if(!isFirst) {
				params.append(", ");
			}
			params.append(param.getName());
			isFirst = false;
		}
		StringBuffer body = new StringBuffer();
		if(this.isStringType(operation.getReturnType())) {
			body.append("String");
		} else {
			body.append(operation.getReturnType());
		}
		body.append(" result = this.getDelegate().").append(operation.getName()).append("(");
		body.append(params);
		body.append(");");
		return body.toString(); 
	}

	/**
	 * Returns the body of the service implementation for a DELETE operation
	 */
	public String getMethodBodyFor(DeleteOperationType operation) {
		// Generate list of parameters to invoke delegate
		StringBuffer params = new StringBuffer();
		boolean isFirst = true;
		for (PathParamType param : operation.getPathParam()) {
			if(!isFirst) {
				params.append(", ");
			}
			params.append(param.getName());
			isFirst = false;
		}
		for (QueryParamType param : operation.getQueryParam()) {
			if(!isFirst) {
				params.append(", ");
			}
			params.append(param.getName());
			isFirst = false;
		}
		for (ContextParamType param : operation.getContextParam()) {
			if(!isFirst) {
				params.append(", ");
			}
			params.append(param.getName());
			isFirst = false;
		}
		StringBuffer body = new StringBuffer();
		body.append("this.getDelegate().").append(operation.getName()).append("(");
		body.append(params);
		body.append(");");
		return body.toString(); 
	}

	public String getDelegateMethodDeclarationFor(GetOperationType operation) {
		StringBuffer result = new StringBuffer();
		if(this.isStringType(operation.getReturnType())) {
			result.append("public String ");
		} else {
			result.append("public ").append(operation.getReturnType()).append(" ");
		}
		// Operation name
		result.append(operation.getName());
		// Parameters
		StringBuffer params = new StringBuffer();
		boolean isFirst = true;
		for (PathParamType param : operation.getPathParam()) {
			if(!isFirst) {
				params.append(", ");
			}
			params.append("String ");
			params.append(param.getName());
			isFirst = false;
		}
		for (QueryParamType param : operation.getQueryParam()) {
			if(!isFirst) {
				params.append(", ");
			}
			params.append("String ");
			params.append(param.getName());
			isFirst = false;
		}
		for (ContextParamType param : operation.getContextParam()) {
			if(!isFirst) {
				params.append(", ");
			}
			params.append(param.getJavaClass()).append(" ");
			params.append(param.getName());
			isFirst = false;
		}
		result.append("(").append(params).append(")");
		return result.toString();
	}

	public String getDelegateMethodDeclarationFor(DeleteOperationType operation) {
		StringBuffer result = new StringBuffer();
		// Operation name
		result.append("public void ").append(operation.getName());
		// Parameters
		StringBuffer params = new StringBuffer();
		boolean isFirst = true;
		for (PathParamType param : operation.getPathParam()) {
			if(!isFirst) {
				params.append(", ");
			}
			params.append("String ");
			params.append(param.getName());
			isFirst = false;
		}
		for (QueryParamType param : operation.getQueryParam()) {
			if(!isFirst) {
				params.append(", ");
			}
			params.append("String ");
			params.append(param.getName());
			isFirst = false;
		}
		for (ContextParamType param : operation.getContextParam()) {
			if(!isFirst) {
				params.append(", ");
			}
			params.append(param.getJavaClass()).append(" ");
			params.append(param.getName());
			isFirst = false;
		}
		result.append("(").append(params).append(")");
		return result.toString();
	}

	/**
	 * Returns the declaration of the service implementation for
	 * a POST operation
	 */
	public String getMethodDeclarationFor(PostOperationType operation) {
		StringBuffer xmlAnnotations = new StringBuffer();
		if(this.isComplexType(operation.getParamType())) {
			if(this.isXmlSerialization(operation.getSerializationType())) {
				xmlAnnotations.append("@Consumes(\"application/xml\")\n    ");
			}
			if(this.isJsonSerialization(operation.getSerializationType())) {
				xmlAnnotations.append("@Consumes(\"application/json\")\n    ");
			}
		}
		if(this.isComplexType(operation.getReturnType())) {
			if(this.isXmlSerialization(operation.getSerializationType())) {
				xmlAnnotations.append("@Produces(\"application/xml\")\n    ");
			}
			if(this.isJsonSerialization(operation.getSerializationType())) {
				xmlAnnotations.append("@Produces(\"application/json\")\n    ");
			}
		}
		StringBuffer result = new StringBuffer();
		result.append(xmlAnnotations);
		if(this.isUndefinedType(operation.getReturnType())) {
			result.append("public void ");
		} else if (this.isStringType(operation.getReturnType())) {
			result.append("public String ");
		} else {
			result.append("public ").append(operation.getReturnType()).append(" ");
		}
		// Operation name
		result.append(operation.getName());
		// Parameters
		boolean isFirst = true;
		StringBuffer params = new StringBuffer();
		for (PathParamType param : operation.getPathParam()) {
			if(!isFirst) {
				params.append(", ");
			}
			params.append("@PathParam(\"").append(param.getName()).append("\") String ").append(param.getName());
			isFirst = false;
		}
		for (QueryParamType param : operation.getQueryParam()) {
			if(!isFirst) {
				params.append(", ");
			}
			params.append("@QueryParam(\"").append(param.getName()).append("\") String ").append(param.getName());
			isFirst = false;
		}
		if (this.isStringType(operation.getParamType())) {
			if(!isFirst) {
				params.append(", ");
			}
			params.append("String param");
			isFirst = false;
		} 
		if(this.isComplexType(operation.getParamType())) {
			if(!isFirst) {
				params.append(", ");
			}
			params.append(operation.getParamType()).append(" param");;
			isFirst = false;
		}
		for (FormParamType param : operation.getFormParam()) {
			if(!isFirst) {
				params.append(", ");
			}
			params.append("@FormParam(\"").append(param.getName()).append("\") String ").append(param.getName());
			isFirst = false;
		}
		for (ContextParamType param : operation.getContextParam()) {
			if(!isFirst) {
				params.append(", ");
			}
			params.append("@Context ").append(param.getJavaClass()).append(" ").append(param.getName());
			isFirst = false;
		}
		result.append("(").append(params).append(")");			
		return result.toString(); 
	}
	
	/**
	 * Returns the declaration of the service implementation for
	 * a PUT operation
	 */
	public String getMethodDeclarationFor(PutOperationType operation) {
		StringBuffer xmlAnnotations = new StringBuffer();
		if(this.isComplexType(operation.getParamType())) {
			if(this.isXmlSerialization(operation.getSerializationType())) {
				xmlAnnotations.append("@Consumes(\"application/xml\")\n    ");
			}
			if(this.isJsonSerialization(operation.getSerializationType())) {
				xmlAnnotations.append("@Consumes(\"application/json\")\n    ");
			}
		}
		if(this.isComplexType(operation.getReturnType())) {
			if(this.isXmlSerialization(operation.getSerializationType())) {
				xmlAnnotations.append("@Produces(\"application/xml\")\n    ");
			}
			if(this.isJsonSerialization(operation.getSerializationType())) {
				xmlAnnotations.append("@Produces(\"application/json\")\n    ");
			}
		}
		StringBuffer result = new StringBuffer();
		result.append(xmlAnnotations);
		if(this.isUndefinedType(operation.getReturnType())) {
			result.append("public void ");
		} else if (this.isStringType(operation.getReturnType())) {
			result.append("public String ");
		} else {
			result.append("public ").append(operation.getReturnType()).append(" ");
		}
		// Operation name
		result.append(operation.getName());
		// Parameters
		boolean isFirst = true;
		StringBuffer params = new StringBuffer();
		for (PathParamType param : operation.getPathParam()) {
			if(!isFirst) {
				params.append(", ");
			}
			params.append("@PathParam(\"").append(param.getName()).append("\") String ").append(param.getName());
			isFirst = false;
		}
		for (QueryParamType param : operation.getQueryParam()) {
			if(!isFirst) {
				params.append(", ");
			}
			params.append("@QueryParam(\"").append(param.getName()).append("\") String ").append(param.getName());
			isFirst = false;
		}
		if (this.isStringType(operation.getParamType())) {
			if(!isFirst) {
				params.append(", ");
			}
			params.append("String param");
			isFirst = false;
		} 
		if(this.isComplexType(operation.getParamType())) {
			if(!isFirst) {
				params.append(", ");
			}
			params.append(operation.getParamType()).append(" param");;
			isFirst = false;
		}
		for (FormParamType param : operation.getFormParam()) {
			if(!isFirst) {
				params.append(", ");
			}
			params.append("@FormParam(\"").append(param.getName()).append("\") String ").append(param.getName());
			isFirst = false;
		}
		for (ContextParamType param : operation.getContextParam()) {
			if(!isFirst) {
				params.append(", ");
			}
			params.append("@Context ").append(param.getJavaClass()).append(" ").append(param.getName());
			isFirst = false;
		}
		result.append("(").append(params).append(")");			
		return result.toString(); 
	}
	
	/**
	 * Returns the body of the service implementation for a POST operation
	 */
	public String getMethodBodyFor(PostOperationType operation) {
		// Deserialize of parameter
		StringBuffer deserialization = new StringBuffer();
		StringBuffer params = new StringBuffer();
		boolean isFirst = true;
		for (PathParamType param : operation.getPathParam()) {
			if(!isFirst) {
				params.append(", ");
			}
			params.append(param.getName());
			isFirst = false;
		}
		for (QueryParamType param : operation.getQueryParam()) {
			if(!isFirst) {
				params.append(", ");
			}
			params.append(param.getName());
			isFirst = false;
		}
		if(!this.isUndefinedType(operation.getParamType())) {
			if(!isFirst) {
				params.append(", ");
			}
			params.append("param");
			isFirst = false;
		}
		for (FormParamType param : operation.getFormParam()) {
			if(!isFirst) {
				params.append(", ");
			}
			params.append(param.getName());
			isFirst = false;
		}
		for (ContextParamType param : operation.getContextParam()) {
			if(!isFirst) {
				params.append(", ");
			}
			params.append(param.getName());
			isFirst = false;
		}
		StringBuffer body = new StringBuffer();
		body.append(deserialization);
		// If the operation doesn't return a value:
		if(this.isUndefinedType(operation.getReturnType())) {
			body.append("this.getDelegate().").append(operation.getName()).append("(").append(params).append(");\r");
		} else {
			body.append("return this.getDelegate().").append(operation.getName()).append("(").append(params).append(");\r");
		}
		return body.toString();
	}

	/**
	 * Returns the body of the service implementation for a PUT operation
	 */
	public String getMethodBodyFor(PutOperationType operation) {
		// Deserialize of parameter
		StringBuffer deserialization = new StringBuffer();
		StringBuffer params = new StringBuffer();
		boolean isFirst = true;
		for (PathParamType param : operation.getPathParam()) {
			if(!isFirst) {
				params.append(", ");
			}
			params.append(param.getName());
			isFirst = false;
		}
		for (QueryParamType param : operation.getQueryParam()) {
			if(!isFirst) {
				params.append(", ");
			}
			params.append(param.getName());
			isFirst = false;
		}
		if(!this.isUndefinedType(operation.getParamType())) {
			if(!isFirst) {
				params.append(", ");
			}
			params.append("param");
			isFirst = false;
		}
		for (FormParamType param : operation.getFormParam()) {
			if(!isFirst) {
				params.append(", ");
			}
			params.append(param.getName());
			isFirst = false;
		}
		for (ContextParamType param : operation.getContextParam()) {
			if(!isFirst) {
				params.append(", ");
			}
			params.append(param.getName());
			isFirst = false;
		}
		StringBuffer body = new StringBuffer();
		body.append(deserialization);
		// If the operation doesn't return a value:
		if(this.isUndefinedType(operation.getReturnType())) {
			body.append("this.getDelegate().").append(operation.getName()).append("(").append(params).append(");\r");
		} else {
			body.append("return this.getDelegate().").append(operation.getName()).append("(").append(params).append(");\r");
		}
		return body.toString();
	}

	public String getDelegateMethodDeclarationFor(PostOperationType operation) {
		StringBuffer result = new StringBuffer();
		if(this.isUndefinedType(operation.getReturnType())) {
			result.append("public void ");
		} else if (this.isStringType(operation.getReturnType())) {
			result.append("public String ");
		} else {
			result.append("public ").append(operation.getReturnType()).append(" ");
		}
		// Operation name
		result.append(operation.getName());
		// Parameter
		StringBuffer params = new StringBuffer();
		boolean isFirst = true;
		for (PathParamType param : operation.getPathParam()) {
			if(!isFirst) {
				params.append(", ");
			}
			params.append("String ");
			params.append(param.getName());
			isFirst = false;
		}
		for (QueryParamType param : operation.getQueryParam()) {
			if(!isFirst) {
				params.append(", ");
			}
			params.append("String ");
			params.append(param.getName());
			isFirst = false;
		}
		if (this.isStringType(operation.getParamType())) {
			if(!isFirst) {
				params.append(", ");
			}
			params.append("String param");
			isFirst = false;
		} 
		if(this.isComplexType(operation.getParamType())) {
			if(!isFirst) {
				params.append(", ");
			}
			params.append(operation.getParamType()).append(" param");
			isFirst = false;
		}
		for (FormParamType param : operation.getFormParam()) {
			if(!isFirst) {
				params.append(", ");
			}
			params.append("String ");
			params.append(param.getName());
			isFirst = false;
		}
		for (ContextParamType param : operation.getContextParam()) {
			if(!isFirst) {
				params.append(", ");
			}
			params.append(param.getJavaClass()).append(" ");
			params.append(param.getName());
			isFirst = false;
		}
		result.append("(").append(params).append(")");
		return result.toString();
	}
	
	public String getDelegateMethodDeclarationFor(PutOperationType operation) {
		StringBuffer result = new StringBuffer();
		if(this.isUndefinedType(operation.getReturnType())) {
			result.append("public void ");
		} else if (this.isStringType(operation.getReturnType())) {
			result.append("public String ");
		} else {
			result.append("public ").append(operation.getReturnType()).append(" ");
		}
		// Operation name
		result.append(operation.getName());
		// Parameter
		StringBuffer params = new StringBuffer();
		boolean isFirst = true;
		for (PathParamType param : operation.getPathParam()) {
			if(!isFirst) {
				params.append(", ");
			}
			params.append("String ");
			params.append(param.getName());
			isFirst = false;
		}
		for (QueryParamType param : operation.getQueryParam()) {
			if(!isFirst) {
				params.append(", ");
			}
			params.append("String ");
			params.append(param.getName());
			isFirst = false;
		}
		if (this.isStringType(operation.getParamType())) {
			if(!isFirst) {
				params.append(", ");
			}
			params.append("String param");
			isFirst = false;
		} 
		if(this.isComplexType(operation.getParamType())) {
			if(!isFirst) {
				params.append(", ");
			}
			params.append(operation.getParamType()).append(" param");
			isFirst = false;
		}
		for (FormParamType param : operation.getFormParam()) {
			if(!isFirst) {
				params.append(", ");
			}
			params.append("String ");
			params.append(param.getName());
			isFirst = false;
		}
		for (ContextParamType param : operation.getContextParam()) {
			if(!isFirst) {
				params.append(", ");
			}
			params.append(param.getJavaClass()).append(" ");
			params.append(param.getName());
			isFirst = false;
		}
		result.append("(").append(params).append(")");
		return result.toString();
	}
	
	private boolean isStringType(String modelType) {
		return this.getUtility().isStringType(modelType);
	}
	
	private boolean isLongType(String modelType) {
		return this.getUtility().isLongType(modelType);
	}
	
	private boolean isIntegerType(String modelType) {
		return this.getUtility().isIntegerType(modelType);
	}
	
	private boolean isDoubleType(String modelType) {
		return this.getUtility().isDoubleType(modelType);
	}
	
	private boolean isBooleanType(String modelType) {
		return this.getUtility().isBooleanType(modelType);
	}
	
	private boolean isUndefinedType(String modelType) {
		return modelType == null || modelType.trim().equals("");
	}
	
	private boolean isPrimitiveType(String modelType) {
		return this.getUtility().isPrimitiveType(modelType);
	}
	
	public boolean isXmlSerialization(String serializationType) {
		return this.getUtility().isXmlSerialization(serializationType);
	}
	
	public boolean isJsonSerialization(String serializationType) {
		return this.getUtility().isJsonSerialization(serializationType);
	}
	
	private boolean isComplexType(String modelType) {
		return this.getUtility().isComplexType(modelType);
	}

}
