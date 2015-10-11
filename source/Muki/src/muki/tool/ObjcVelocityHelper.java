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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import muki.tool.model.DeleteOperationType;
import muki.tool.model.GetOperationType;
import muki.tool.model.ListAttrType;
import muki.tool.model.ModelDefinitionsType;
import muki.tool.model.ModelType;
import muki.tool.model.PathParamType;
import muki.tool.model.PostOperationType;
import muki.tool.model.PutOperationType;
import muki.tool.model.QueryParamType;
import muki.tool.model.SimpleAttrType;
import muki.tool.model.ControllerType;

/**
 * This class offers support during the code generation with Velocity templates. The templates include invocations
 * to the operations of this helper to create Objective-C code fragments that otherwise would be complicated to generate with
 * the template itself.
 */
public class ObjcVelocityHelper extends VelocityHelper {
	
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

	private String getObjcPrimitiveType(String typeName) {
		return this.getUtility().getObjcPrimitiveType(typeName);
	}
	
	public String getTypeInterfaceImportDeclarations(ModelType type) {
		StringBuffer result = new StringBuffer();
		Set<String> uniqueNames = new HashSet<String>();
		for (SimpleAttrType attribute : type.getSimpleAttr()) {
			String typeName = attribute.getType();
			if(!this.isPrimitiveType(typeName)) {
				uniqueNames.add(typeName);
			}
		}
		for (ListAttrType attribute : type.getListAttr()) {
			uniqueNames.add(attribute.getItemsType());
		}
		for (String typeName : uniqueNames) {
			result.append("#import \"").append(typeName).append(".h\"\r");
		}
		return result.toString();
	}
	
	public String getAttributeDeclaration(SimpleAttrType attribute) {
		StringBuffer result = new StringBuffer();
		String typeName = attribute.getType();
		if(this.isPrimitiveType(typeName)) {
			String builtInType = this.getObjcPrimitiveType(typeName);
			result.append(builtInType);
			result.append(" ");
			if(this.isStringType(typeName)) {
				result.append("*");
			}
		} else {
			result.append(attribute.getType());
			result.append(" *");
		}
		result.append(attribute.getName());
		return result.toString(); 
	}
	
	public String getAttributeDeclaration(ListAttrType attribute) {
		StringBuffer result = new StringBuffer();
		result.append("NSMutableArray *").append(attribute.getName());
		return result.toString(); 
	}

	public String getPropertyDeclaration(SimpleAttrType attribute) {
		StringBuffer result = new StringBuffer();
		String typeName = attribute.getType();
		if(this.isBooleanType(typeName) || this.isDoubleType(typeName) || this.isIntegerType(typeName) || this.isLongType(typeName)) {
			result.append("@property ").append(this.getAttributeDeclaration(attribute));
		} else {
			result.append("@property (nonatomic, strong) ").append(this.getAttributeDeclaration(attribute));
		}
		return result.toString(); 
	}

	public String getPropertyDeclaration(ListAttrType attribute) {
		StringBuffer result = new StringBuffer();
		result.append("@property (nonatomic, strong) ").append(this.getAttributeDeclaration(attribute));
		return result.toString(); 
	}

	public String getAddMethodDeclaration(ListAttrType attribute) {
		StringBuffer result = new StringBuffer();
		result.append("- (void)addTo").append(this.toCapitalizedCase(attribute.getName()));
		result.append(": (").append(attribute.getItemsType()).append(" *)anObject");
		return result.toString();
	}

	public String getRemoveMethodDeclaration(ListAttrType attribute) {
		StringBuffer result = new StringBuffer();
		result.append("- (void)removeFrom").append(this.toCapitalizedCase(attribute.getName()));
		result.append(": (").append(attribute.getItemsType()).append(" *)anObject");
		return result.toString();
	}
	
	public String getTypeImplImportDeclarations(ModelType type) {
		StringBuffer result = new StringBuffer();
		result.append("#import \"").append(type.getName()).append(".h\"\r");
		result.append(this.getTypeInterfaceImportDeclarations(type));
		return result.toString();
	}
	
	public String getInit(ListAttrType attribute) {
		StringBuffer result = new StringBuffer();
		result.append("self.").append(attribute.getName()).append(" = [[NSMutableArray alloc] init];");
		return result.toString();
	}

	public String getDealloc(SimpleAttrType attribute) {
		StringBuffer result = new StringBuffer();
		String typeName = attribute.getType();
		if (this.isComplexType(typeName) || this.isStringType(typeName)) {
			result.append("self.").append(attribute.getName()).append(" = nil;");
		}
		return result.toString(); 
	}

	public String getDealloc(ListAttrType attribute) {
		StringBuffer result = new StringBuffer();
		result.append("self.").append(attribute.getName()).append(" = nil;");
		return result.toString(); 
	}

	public String getParserInterfaceImportDeclarations(ModelType type) {
		StringBuffer result = new StringBuffer();
		Set<String> uniqueNames = new HashSet<String>();
		uniqueNames.add(type.getName());
		for (SimpleAttrType attribute : type.getSimpleAttr()) {
			String typeName = attribute.getType();
			if(!this.isPrimitiveType(typeName)) {
				uniqueNames.add(typeName);
			}
		}
		for (ListAttrType attribute : type.getListAttr()) {
			uniqueNames.add(attribute.getItemsType());
		}
		for (String typeName : uniqueNames) {
			result.append("#import \"").append(typeName).append(".h\"\r");
		}
		return result.toString();
	}
	
	public String getParserImplementationImportDeclarations(ModelType type) {
		StringBuffer result = new StringBuffer();
		Set<String> uniqueNames = new HashSet<String>();
		uniqueNames.add(this.getParserInterfaceName(type));
		for (SimpleAttrType attribute : type.getSimpleAttr()) {
			String typeName = attribute.getType();
			if(!this.isPrimitiveType(typeName)) {
				uniqueNames.add(this.getParserInterfaceName(attribute));
			}
		}
		for (ListAttrType attribute : type.getListAttr()) {
			uniqueNames.add(this.getParserInterfaceName(attribute));
		}
		for (String typeName : uniqueNames) {
			result.append("#import \"").append(typeName).append(".h\"\r");
		}
		result.append(this.getParserInterfaceImportDeclarations(type));
		return result.toString();
	}

	public String getParserInterfaceName(ModelType type) {
		StringBuffer result = new StringBuffer();
		result.append(type.getName()).append("ParserDelegate");
		return result.toString(); 
	}

	public String getParserInterfaceName(SimpleAttrType attribute) {
		StringBuffer result = new StringBuffer();
		result.append(this.toCapitalizedCase(attribute.getType())).append("ParserDelegate");
		return result.toString(); 
	}

	public String getParserInterfaceName(ListAttrType attribute) {
		StringBuffer result = new StringBuffer();
		result.append(this.toCapitalizedCase(attribute.getItemsType())).append("ParserDelegate");
		return result.toString(); 
	}
	
	/**
	 * self.object.id = [ value integerValue];
	 */
	public String getDidStartElementAssigmentExpression(SimpleAttrType attribute) {
		StringBuffer result = new StringBuffer();
		result.append("self.object.").append(attribute.getName()).append(" = ");
		String typeName = attribute.getType();
		if(this.isLongType(typeName)) {
			result.append("[value longLongValue]");
		}
		if(this.isIntegerType(typeName)) {
			result.append("[value integerValue]");
		}
		if(this.isDoubleType(typeName)) {
			result.append("[value doubleValue]");
		}
		if(this.isBooleanType(typeName)) {
			result.append("[self toBOOL: value]");
		}
		return result.toString();
	}

	/**
	 * self.object.name = self.currentStringValue;
	 */
	public String getDidEndElementAssigmentExpression(SimpleAttrType attribute) {
		if(!this.isStringType(attribute.getType())) {
			return null;
		}
		StringBuffer result = new StringBuffer();
		result.append("self.object.").append(attribute.getName()).append(" = self.currentStringValue ");
		return result.toString();
	}

	public String getParserInjectSelectorNameFor(SimpleAttrType attribute) {
		StringBuffer result = new StringBuffer();
		if (this.isComplexType(attribute.getType())) {
			result.append("inject").append(this.toCapitalizedCase(attribute.getName())).append(":");
		}
		return result.toString();
	}

	public String getParserInjectSelectorNameFor(ListAttrType attribute) {
		StringBuffer result = new StringBuffer();
		result.append("injectInto").append(this.toCapitalizedCase(attribute.getName())).append(":");
		return result.toString();
	}

	public String getParserInjectMethodDeclaration(SimpleAttrType attribute) {
		StringBuffer result = new StringBuffer();
		if (this.isComplexType(attribute.getType())) {
			result.append("- (void)inject").append(this.toCapitalizedCase(attribute.getName()));
			result.append(": (").append(attribute.getType()).append(" *)anObject");			
		}
		return result.toString();
	}

	public String getParserInjectMethodBody(SimpleAttrType attribute) {
		StringBuffer result = new StringBuffer();
		if (this.isComplexType(attribute.getType())) {
			result.append("self.object.").append(attribute.getName());
			result.append(" = anObject");
		}
		return result.toString();
	}

	public String getParserInjectMethodDeclaration(ListAttrType attribute) {
		StringBuffer result = new StringBuffer();
		result.append("- (void)injectInto").append(this.toCapitalizedCase(attribute.getName()));
		result.append(": (").append(attribute.getItemsType()).append(" *)anObject");
		return result.toString();
	}

	public String getParserInjectMethodBody(ListAttrType attribute) {
		StringBuffer result = new StringBuffer();
		result.append("[self.object addTo").append(this.toCapitalizedCase(attribute.getName()));
		result.append(": anObject]");
		return result.toString();
	}

	public String getXmlSerializerInterfaceImportDeclarations(ModelDefinitionsType definitions) {
		StringBuffer result = new StringBuffer();
		for (ModelType type : definitions.getModel()) {
			result.append("#import \"").append(type.getName()).append(".h\"\r");
		}
		return result.toString();
	}
	
	/**
	 * - (void)serializeTrack:(Track *)anObject;
	 */
	public String getXmlSerializerSerializeMethodDeclaration(ModelType type) {
		StringBuffer result = new StringBuffer();
		result.append("- (void)serialize").append(type.getName());
		result.append(": (").append(type.getName()).append(" *)anObject");			
		return result.toString();
	}

	/**
	 * - (void)serializeTrack:(Track *)anObject element:(NSString *)elementName;
	 */
	public String getXmlSerializerSerializeElementMethodDeclaration(ModelType type) {
		StringBuffer result = new StringBuffer();
		result.append("- (void)serialize").append(type.getName());
		result.append(": (").append(type.getName()).append(" *)anObject element:(NSString *)elementName");			
		return result.toString();
	}

	public String getXmlSerializerSerializeMethodBody(ModelType type) {
		StringBuffer result = new StringBuffer();
		result.append("[self serialize").append(type.getName());
		result.append(": ").append("anObject element: ");			
		result.append("@\"").append(this.toLowerCase(type.getName())).append("\"]");			
		return result.toString();		
	}
	
	/**
	 * // Basic type attributes, except String
	 * NSMutableArray *attributes = [[NSMutableArray alloc] init];
	 * XmlAttribute *attr = nil;
	 * attr = [[XmlAttribute alloc] initName: @"durationInSeconds" integerValue: anObject.durationInSeconds];
	 * [attributes addObject: attr];
	 * attr = [[XmlAttribute alloc] initName: @"catalogId" longValue: anObject.catalogId];
	 * [attributes addObject: attr];
	 * attr = [[XmlAttribute alloc] initName: @"newRelease" booleanValue: anObject.newRelease];
	 * [attributes addObject: attr];
	 * attr = [[XmlAttribute alloc] initName: @"price" doubleValue: anObject.price];
	 * [attributes addObject: attr];
	 * [self openElement: elementName attributes: attributes];
	 * for (XmlAttribute *anAttribute in attributes) {
	 *   [anAttribute release];
	 * }
	 * [attributes release];
	 * 
	 * // String type attributes
	 * [self openElement: @"title"];
	 * [self.xmlOutput appendString: anObject.title];
	 * [self closeElement: @"title"];
	 * [self closeElement: elementName];
	 * 
	 * // Complex type attributes
	 * [self serializeTrack: anObject.mainTrack element: @"mainTrack"];
	 * [self openElement: @"tracks"];
	 * 
	 * // List attributes
	 * for (Track* anItem in anObject.tracks) {
	 *   [self serializeTrack: anItem];
	 * }
	 * [self closeElement: @"tracks"];
	 * [self closeElement: elementName];
	 * 
	 */
	public String getXmlSerializerSerializeElementMethodBody(ModelType type) {
		StringBuffer result = new StringBuffer();
		result.append("NSMutableArray *attributes = [[NSMutableArray alloc] init];").append("\r");
		result.append("    XmlAttribute *attr = nil;").append("\r");
		// Basic type attributes, except String
		for (SimpleAttrType attribute : type.getSimpleAttr()) {
			String typeName = attribute.getType();
			if(this.isBooleanType(typeName)) {
				result.append("    attr = [[XmlAttribute alloc] initName: @\"").append(attribute.getName()).append("\"");
				result.append(" booleanValue: anObject.").append(attribute.getName()).append("];").append("\r");
				result.append("    [attributes addObject: attr];").append("\r");
			}
			if(this.isIntegerType(typeName)) {
				result.append("    attr = [[XmlAttribute alloc] initName: @\"").append(attribute.getName()).append("\"");
				result.append(" integerValue: anObject.").append(attribute.getName()).append("];").append("\r");
				result.append("    [attributes addObject: attr];").append("\r");
			}
			if(this.isLongType(typeName)) {
				result.append("    attr = [[XmlAttribute alloc] initName: @\"").append(attribute.getName()).append("\"");
				result.append(" longValue: anObject.").append(attribute.getName()).append("];").append("\r");
				result.append("    [attributes addObject: attr];").append("\r");
			}
			if(this.isDoubleType(typeName)) {
				result.append("    attr = [[XmlAttribute alloc] initName: @\"").append(attribute.getName()).append("\"");
				result.append(" doubleValue: anObject.").append(attribute.getName()).append("];").append("\r");
				result.append("    [attributes addObject: attr];").append("\r");
			}
		}
		result.append("    [self openElement: elementName").append(" attributes: attributes];").append("\r");
		//result.append("    for (XmlAttribute *anAttribute in attributes) {").append("\r");
		//result.append("        [anAttribute release];").append("\r");
		//result.append("    }").append("\r");
		//result.append("    [attributes release];").append("\r");
		
		// String type attributes
		for (SimpleAttrType attribute : type.getSimpleAttr()) {
			if(this.isStringType(attribute.getType())) {
				result.append("    [self openElement: @\"").append(attribute.getName()).append("\"];").append("\r");
				result.append("    [self.xmlOutput appendString: [self encodeString:anObject.").append(attribute.getName()).append("]];").append("\r");
				result.append("    [self closeElement: @\"").append(attribute.getName()).append("\"];").append("\r");
			}
		}
		// Complex type attributes
		for (SimpleAttrType attribute : type.getSimpleAttr()) {
			if(this.isComplexType(attribute.getType())) {
				result.append("    [self serialize").append(attribute.getType()).append(": anObject.").append(attribute.getName()).append(" element: @\"").append(attribute.getName()).append("\"];").append("\r");
			}
		}

		// List type attributes
		for (ListAttrType attribute : type.getListAttr()) {
			result.append("    [self openElement: @\"").append(attribute.getName()).append("\"];").append("\r");
			result.append("    for (").append(attribute.getItemsType()).append("* anItem in anObject.").append(attribute.getName()).append(") {").append("\r");
			result.append("        [self serialize").append(attribute.getItemsType()).append(": anItem];").append("\r");
			result.append("    }").append("\r");
			result.append("    [self closeElement: @\"").append(attribute.getName()).append("\"];").append("\r");
		}
		result.append("    [self closeElement: elementName];").append("\r");
		return result.toString();
	}

	public String getControllerStubInterfaceImportDeclarations(ModelDefinitionsType definitions) {
		StringBuffer result = new StringBuffer();
		for (ModelType type : definitions.getModel()) {
			result.append("#import \"").append(type.getName()).append(".h\"\r");
		}
		return result.toString();
	}
	
	public String getControllerStubImplImportDeclarations(ModelDefinitionsType definitions, ControllerType service) {
		StringBuffer result = new StringBuffer();
		result.append("#import \"").append(this.getControllerStubInterfaceName(service)).append(".h\"\r");
		for (ModelType type : definitions.getModel()) {
			result.append("#import \"").append(type.getName()).append(".h\"\r");
			result.append("#import \"").append(this.getParserInterfaceName(type)).append(".h\"\r");
		}
		return result.toString();
	}

	public String getControllerStubInterfaceName(ControllerType controller) {
		StringBuffer result = new StringBuffer();
		result.append(controller.getName()).append("Stub");
		return result.toString(); 
	}
	
	/**
	 * - (NSString*)operation1Error:(NSError*)error;
	 * - (Cd*)operation2Error:(NSError*)error;
	 * - (Track*)operation3Id: (NSString *)aString error:(NSError*)error;
	 * - (Cd*)operation7Id: (NSString *)aString1 name: (NSString *)aString2 error:(NSError*)error
	 */
	public String getControllerStubInterfaceMethodDeclaration(GetOperationType operation) {
		StringBuffer result = new StringBuffer();
		result.append("- (");
		if(this.isStringType(operation.getReturnType())) {
			result.append(this.getObjcPrimitiveType(operation.getReturnType())).append("*)");
		} else {
			result.append(operation.getReturnType()).append("*)");			
		}
		result.append(operation.getName());
		int paramCount = 1;
		String paramName = null;
		for (PathParamType param : operation.getPathParam()) {
			paramName = param.getName();
			if(paramCount == 1) {
				paramName = this.toCapitalizedCase(paramName);
			} else {
				result.append(" ");
			}
			result.append(paramName).append(":").append(" (NSString *)aString").append(paramCount++);	
		}
		// Add the "error" param to the list
		List<QueryParamType> params2 = new ArrayList<QueryParamType>(operation.getQueryParam());
		QueryParamType errorParam = new QueryParamType();
		errorParam.setName("error");
		params2.add(errorParam);
		for (QueryParamType param : params2) {
			paramName = param.getName();
			if(paramCount == 1) {
				paramName = this.toCapitalizedCase(paramName);
			} else {
				result.append(" ");
			}
			if (paramName.toLowerCase().equals("error")) {
				result.append(paramName).append(":").append(" (NSError **)error");
			} else {
				result.append(paramName).append(":").append(" (NSString *)aString").append(paramCount++);
			}
		}
		return result.toString();
	}

	/**
	 * - (void)operation8Error:(NSError*)error;
	 * - (NSString*)operation11: (NSString *)aString error:(NSError*)error;
	 * - (Cd*)operation13: (NSString *)aString error:(NSError*)error;
	 * - (Cd*)operation16: (Track *)anObject error:(NSError*)error;
	 * - (Cd*)operation17: (Track *)anObject id:(NSString *)aString1 date:(NSString *)aString2 error:(NSError*)error;
	 */
	public String getControllerStubInterfaceMethodDeclaration(PostOperationType operation) {
		StringBuffer result = new StringBuffer();
		result.append("- (");
		String returnType = operation.getReturnType();
		if(this.isUndefinedType(returnType)) {
			result.append("void)");
		}
		if(this.isStringType(returnType)) {
			result.append(this.getObjcPrimitiveType(returnType)).append("*)");
		} 
		if(this.isComplexType(returnType)) {
			result.append(returnType).append("*)");			
		}
		result.append(operation.getName());
		int paramCount = 1;
		if(this.isStringType(operation.getParamType())) {
			result.append(": (NSString *)aString");
			paramCount++;
		}
		if(this.isComplexType(operation.getParamType())) {
			result.append(":").append(" (").append(operation.getParamType()).append(" *)anObject");
			paramCount++;
		}
		String paramName = null;
		for (PathParamType param : operation.getPathParam()) {
			paramName = param.getName();
			if(paramCount == 1) {
				paramName = this.toCapitalizedCase(paramName);
			} else {
				result.append(" ");
			}
			result.append(paramName).append(":").append(" (NSString *)aString").append(paramCount++);	
		}
		// Add the "error" param to the list
		List<QueryParamType> params2 = new ArrayList<QueryParamType>(operation.getQueryParam());
		QueryParamType errorParam = new QueryParamType();
		errorParam.setName("error");
		params2.add(errorParam);
		for (QueryParamType param : params2) {
			paramName = param.getName();
			if(paramCount == 1) {
				paramName = this.toCapitalizedCase(paramName);
			} else {
				result.append(" ");
			}
			if (paramName.toLowerCase().equals("error")) {
				result.append(paramName).append(":").append(" (NSError **)error");
			} else {
				result.append(paramName).append(":").append(" (NSString *)aString").append(paramCount++);
			}
		}
		return result.toString();
	}

	/**
	 * - (void)operation8Error:(NSError*)error;
	 * - (NSString*)operation11: (NSString *)aString error:(NSError*)error;
	 * - (Cd*)operation13: (NSString *)aString  error:(NSError*)error;
	 * - (Cd*)operation16: (Track *)anObject  error:(NSError*)error;
	 * - (Cd*)operation17: (Track *)anObject id:(NSString *)aString1 date:(NSString *)aString2  error:(NSError*)error;
	 */
	public String getControllerStubInterfaceMethodDeclaration(PutOperationType operation) {
		StringBuffer result = new StringBuffer();
		result.append("- (");
		String returnType = operation.getReturnType();
		if(this.isUndefinedType(returnType)) {
			result.append("void)");
		}
		if(this.isStringType(returnType)) {
			result.append(this.getObjcPrimitiveType(returnType)).append("*)");
		} 
		if(this.isComplexType(returnType)) {
			result.append(returnType).append("*)");			
		}
		result.append(operation.getName());
		int paramCount = 1;
		if(this.isStringType(operation.getParamType())) {
			result.append(": (NSString *)aString");
			paramCount++;
		}
		if(this.isComplexType(operation.getParamType())) {
			result.append(":").append(" (").append(operation.getParamType()).append(" *)anObject");
			paramCount++;
		}
		String paramName = null;
		for (PathParamType param : operation.getPathParam()) {
			paramName = param.getName();
			if(paramCount == 1) {
				paramName = this.toCapitalizedCase(paramName);
			} else {
				result.append(" ");
			}
			result.append(paramName).append(":").append(" (NSString *)aString").append(paramCount++);	
		}
		// Add the "error" param to the list
		List<QueryParamType> params2 = new ArrayList<QueryParamType>(operation.getQueryParam());
		QueryParamType errorParam = new QueryParamType();
		errorParam.setName("error");
		params2.add(errorParam);
		for (QueryParamType param : params2) {
			paramName = param.getName();
			if(paramCount == 1) {
				paramName = this.toCapitalizedCase(paramName);
			} else {
				result.append(" ");
			}
			if (paramName.toLowerCase().equals("error")) {
				result.append(paramName).append(":").append(" (NSError **)error");
			} else {
				result.append(paramName).append(":").append(" (NSString *)aString").append(paramCount++);
			}
		}
		return result.toString();
	}

	/**
	 * - (void)operation1Error:(NSError*)error;
	 * - (void)operation2Error:(NSError*)error;
	 * - (void)operation3Id: (NSString *)aString error:(NSError*)error;
	 * - (void)operation7Id: (NSString *)aString1 name: (NSString *)aString2 error:(NSError*)error;
	 */
	public String getControllerStubInterfaceMethodDeclaration(DeleteOperationType operation) {
		StringBuffer result = new StringBuffer();
		result.append("- (void)");
		result.append(operation.getName());
		int paramCount = 1;
		String paramName = null;
		for (PathParamType param : operation.getPathParam()) {
			paramName = param.getName();
			if(paramCount == 1) {
				paramName = this.toCapitalizedCase(paramName);
			} else {
				result.append(" ");
			}
			result.append(paramName).append(":").append(" (NSString *)aString").append(paramCount++);	
		}
		// Add the "error" param to the list
		List<QueryParamType> params2 = new ArrayList<QueryParamType>(operation.getQueryParam());
		QueryParamType errorParam = new QueryParamType();
		errorParam.setName("error");
		params2.add(errorParam);
		for (QueryParamType param : params2) {
			paramName = param.getName();
			if(paramCount == 1) {
				paramName = this.toCapitalizedCase(paramName);
			} else {
				result.append(" ");
			}
			if (paramName.toLowerCase().equals("error")) {
				result.append(paramName).append(":").append(" (NSError **)error");
			} else {
				result.append(paramName).append(":").append(" (NSString *)aString").append(paramCount++);
			}
		}
		return result.toString();
	}

	/**
	 * Example:
	 * - (Track*)getOperation11XmlDate: (NSString *)aString1 page: (NSString *)aString2 error: (NSError **)error {
	 *     NSString *fullUrl = [NSString stringWithFormat: @"%@/pathGetOperation11Xml?date=%@&page=%@", self.controllerUrl, aString1, aString2];
	 *     NSError *localError;
	 *     NSString *reply = [self doGetInvocation: fullUrl contentType:@"application/xml; charset=UTF-8" error:&localError];
	 *     if (reply == nil) {
	 *         if (error != nil) {
	 *            *error = localError;
	 *         }
	 *         return nil;
	 *     }
	 *     Track *object = [self deserializeXmlTrack: reply];
	 *     return object;
	 *  }
	 */
	public String getControllerStubImplementationMethodBody(GetOperationType operation) {
		String path = this.replacePathParams(operation.getHttpPath(), operation.getPathParam());
		StringBuffer urlLine = new StringBuffer();
		urlLine.append("NSString *fullUrl = [NSString stringWithFormat: @\"%@").append(path);
		boolean isFirst = true;
		for (QueryParamType param : operation.getQueryParam()) {
			if(isFirst) {
				urlLine.append("?").append(param.getName()).append("=%@");
				isFirst = false;
			} else {
				urlLine.append("&").append(param.getName()).append("=%@");			
			}	
		}
		urlLine.append("\", self.controllerUrl");
		int paramCount = 1;
		for (PathParamType param : operation.getPathParam()) {
			urlLine.append(", aString").append(paramCount++);
		}
		for (QueryParamType param : operation.getQueryParam()) {
			urlLine.append(", aString").append(paramCount++);
		}
		urlLine.append("];");
		StringBuffer result = new StringBuffer();
		result.append(urlLine).append("\r");

		String contentType = "application/xml; charset=UTF-8";
		if (this.isJsonSerialization(operation.getSerializationType())) {
			contentType = "application/json; charset=UTF-8";
		}
		result.append("    NSError *localError;\r");
		result.append("    NSString *reply = [self doGetInvocation: fullUrl contentType:@\"").append(contentType).append("\" error:&localError];\r");
		result.append("    if (reply == nil) {\r");
		result.append("        if (error != nil) {\r");
		result.append("            *error = localError;\r");
		result.append("        }\r");
		result.append("        return nil;\r");
		result.append("    }\r");
		String returnType = operation.getReturnType();;
		if(this.isComplexType(returnType)) {
			String token = "Xml";
			if(this.isJsonSerialization(operation.getSerializationType())) {
				token = "Json";
			}
			// Cd *object = [self deserializeXmlCd: reply];
			// return object;
			result.append("    ").append(returnType).append(" *object = [self deserialize").append(token).append(returnType).append(": reply];").append("\r");;
			result.append("    return object;").append("\r");;
		}
		if(this.isStringType(returnType)) {
			// return reply;
			result.append("    return reply;").append("\r");;
		}
		return result.toString();
	}	

	/**
	 * Example:
	 * - (Track*)postOperation10Json: (Cd *)anObject error: (NSError **)error {
	 *     NSString *fullUrl = [NSString stringWithFormat: @"%@/pathPostOperation10Json", self.controllerUrl];
	 *     NSString *messageBody = [self serializeJsonCd: anObject];
	 *     NSError *localError;
	 *     NSString *reply = [self doPostInvocation: fullUrl body: messageBody contentType:@"application/json; charset=UTF-8" error:&localError];
	 *     if (reply == nil) {
	 *        if (error != nil) {
	 *           *error = localError;
	 *        }
	 *        return nil;
	 *     }
	 *     Track *object = [self deserializeJsonTrack: reply];
	 *     return object;
	 *   }
	 */
	public String getControllerStubImplementationMethodBody(PostOperationType operation) {
		String path = this.replacePathParams(operation.getHttpPath(), operation.getPathParam());
		StringBuffer urlLine = new StringBuffer();
		urlLine.append("NSString *fullUrl = [NSString stringWithFormat: @\"%@").append(path);
		boolean isFirst = true;
		for (QueryParamType param : operation.getQueryParam()) {
			if(isFirst) {
				urlLine.append("?").append(param.getName()).append("=%@");
				isFirst = false;
			} else {
				urlLine.append("&").append(param.getName()).append("=%@");			
			}	
		}
		urlLine.append("\", self.controllerUrl");
		int paramCount = 1;
		if(!this.isUndefinedType(operation.getParamType())) {
			paramCount++;
		}
		for (PathParamType param : operation.getPathParam()) {
			urlLine.append(", aString").append(paramCount++);
		}
		for (QueryParamType param : operation.getQueryParam()) {
			urlLine.append(", aString").append(paramCount++);
		}
		urlLine.append("];");
		StringBuffer result = new StringBuffer();
		result.append(urlLine).append("\r");
		StringBuffer invocation = new StringBuffer();
		String contentType = "application/xml; charset=UTF-8";
		if (this.isJsonSerialization(operation.getSerializationType())) {
			contentType = "application/json; charset=UTF-8";
		}
		String paramType = operation.getParamType();
		if(this.isComplexType(paramType)) {
			String token = "Xml";
			if(this.isJsonSerialization(operation.getSerializationType())) {
				token = "Json";
			}
			result.append("    NSString *messageBody = [self serialize").append(token).append(operation.getParamType()).append(": anObject];").append("\r");
			invocation.append("[self doPostInvocation: fullUrl body: messageBody contentType:@\"").append(contentType).append("\" error:&localError]");
		}
		if(this.isStringType(paramType)) {
			invocation.append("[self doPostInvocation: fullUrl body: aString contentType:@\"").append(contentType).append("\" error:&localError]");
		}
		if(this.isUndefinedType(paramType)) {
			invocation.append("[self doPostInvocation: fullUrl body: nil contentType:@\"").append(contentType).append("\" error:&localError]");
		}
		String returnType = operation.getReturnType();
		if(this.isComplexType(returnType)) {
			String token = "Xml";
			if(this.isJsonSerialization(operation.getSerializationType())) {
				token = "Json";
			}
			result.append("    NSError *localError;\r");
			result.append("    NSString *reply = ").append(invocation).append(";\r");
			result.append("    if (reply == nil) {\r");
			result.append("        if (error != nil) {\r");
			result.append("            *error = localError;\r");
			result.append("        }\r");
			result.append("        return nil;\r");
			result.append("    }\r");
			result.append("    ").append(returnType).append(" *object = [self deserialize").append(token).append(returnType).append(": reply];").append("\r");;
			result.append("    return object;").append("\r");;
		}
		if(this.isStringType(returnType)) {
			result.append("    NSError *localError;\r");
			result.append("    NSString *reply = ").append(invocation).append(";\r");
			result.append("    if (reply == nil) {\r");
			result.append("        if (error != nil) {\r");
			result.append("            *error = localError;\r");
			result.append("        }\r");
			result.append("        return nil;\r");
			result.append("    }\r");
			result.append("    return reply;").append("\r");;
		}
		if(this.isUndefinedType(returnType)) {
			result.append("    NSError *localError;\r");
			result.append("    ").append(invocation).append(";\r");
			result.append("    if (error != nil) {\r");
			result.append("        *error = localError;\r");
			result.append("    }\r");
		}
		return result.toString();
	}
	
	/**
	 * Example:
	 * - (Track*)putOperation7Json: (Cd *)anObject error: (NSError **)error {
	 *      NSString *fullUrl = [NSString stringWithFormat: @"%@/pathPutOperation7Json", self.controllerUrl];
	 *      NSString *messageBody = [self serializeJsonCd: anObject];
	 *      NSError *localError;
	 *      NSString *reply = [self doPutInvocation: fullUrl body: messageBody contentType:@"application/json; charset=UTF-8" error:&localError];
	 *      if (reply == nil) {
	 *         if (error != nil) {
	 *            *error = localError;
	 *         }
	 *         return nil;
	 *      }
	 *      Track *object = [self deserializeJsonTrack: reply];
	 *      return object;
	 *  }
	 */
	public String getControllerStubImplementationMethodBody(PutOperationType operation) {
		String path = this.replacePathParams(operation.getHttpPath(), operation.getPathParam());
		StringBuffer urlLine = new StringBuffer();
		urlLine.append("NSString *fullUrl = [NSString stringWithFormat: @\"%@").append(path);
		boolean isFirst = true;
		for (QueryParamType param : operation.getQueryParam()) {
			if(isFirst) {
				urlLine.append("?").append(param.getName()).append("=%@");
				isFirst = false;
			} else {
				urlLine.append("&").append(param.getName()).append("=%@");			
			}	
		}
		urlLine.append("\", self.controllerUrl");
		int paramCount = 1;
		if(!this.isUndefinedType(operation.getParamType())) {
			paramCount++;
		}
		for (PathParamType param : operation.getPathParam()) {
			urlLine.append(", aString").append(paramCount++);
		}
		for (QueryParamType param : operation.getQueryParam()) {
			urlLine.append(", aString").append(paramCount++);
		}
		urlLine.append("];");
		StringBuffer result = new StringBuffer();
		result.append(urlLine).append("\r");
		StringBuffer invocation = new StringBuffer();
		String contentType = "application/xml; charset=UTF-8";
		if (this.isJsonSerialization(operation.getSerializationType())) {
			contentType = "application/json; charset=UTF-8";
		}
		String paramType = operation.getParamType();
		if(this.isComplexType(paramType)) {
			String token = "Xml";
			if(this.isJsonSerialization(operation.getSerializationType())) {
				token = "Json";
			}			
			result.append("    NSString *messageBody = [self serialize").append(token).append(operation.getParamType()).append(": anObject];").append("\r");
			invocation.append("[self doPutInvocation: fullUrl body: messageBody contentType:@\"").append(contentType).append("\" error:&localError]");
		}
		if(this.isStringType(paramType)) {
			invocation.append("[self doPutInvocation: fullUrl body: aString contentType:@\"").append(contentType).append("\" error:&localError]");
		}
		if(this.isUndefinedType(paramType)) {
			invocation.append("[self doPutInvocation: fullUrl body: nil contentType:@\"").append(contentType).append("\" error:&localError]");
		}
		String returnType = operation.getReturnType();
		if(this.isComplexType(returnType)) {
			String token = "Xml";
			if(this.isJsonSerialization(operation.getSerializationType())) {
				token = "Json";
			}
			result.append("    NSError *localError;\r");
			result.append("    NSString *reply = ").append(invocation).append(";\r");
			result.append("    if (reply == nil) {\r");
			result.append("        if (error != nil) {\r");
			result.append("            *error = localError;\r");
			result.append("        }\r");
			result.append("        return nil;\r");
			result.append("    }\r");
			result.append("    ").append(returnType).append(" *object = [self deserialize").append(token).append(returnType).append(": reply];").append("\r");;
			result.append("    return object;").append("\r");;
		}
		if(this.isStringType(returnType)) {
			result.append("    NSError *localError;\r");
			result.append("    NSString *reply = ").append(invocation).append(";\r");
			result.append("    if (reply == nil) {\r");
			result.append("        if (error != nil) {\r");
			result.append("            *error = localError;\r");
			result.append("        }\r");
			result.append("        return nil;\r");
			result.append("    }\r");
			result.append("    return reply;").append("\r");;
		}
		if(this.isUndefinedType(returnType)) {
			result.append("    NSError *localError;\r");
			result.append("    ").append(invocation).append(";\r");
			result.append("    if (error != nil) {\r");
			result.append("        *error = localError;\r");
			result.append("    }\r");
		}
		return result.toString();
	}
	
	/**
	 * Example:
	 * - (void)deleteOperation6Name: (NSString *)aString1 id: (NSString *)aString2 error: (NSError **)error {
	 *     NSString *fullUrl = [NSString stringWithFormat: @"%@/pathDeleteOperation6/artists/%@/tracks/%@", self.controllerUrl, aString1, aString2];
	 *     NSError *localError;
	 *     [self doDeleteInvocation: fullUrl error:&localError];
	 *     if (error != nil) {
	 *        *error = localError;
	 *     }
	 *  }
	 */
	public String getControllerStubImplementationMethodBody(DeleteOperationType operation) {
		String path = this.replacePathParams(operation.getHttpPath(), operation.getPathParam());
		StringBuffer urlLine = new StringBuffer();
		urlLine.append("NSString *fullUrl = [NSString stringWithFormat: @\"%@").append(path);
		boolean isFirst = true;
		for (QueryParamType param : operation.getQueryParam()) {
			if(isFirst) {
				urlLine.append("?").append(param.getName()).append("=%@");
				isFirst = false;
			} else {
				urlLine.append("&").append(param.getName()).append("=%@");			
			}	
		}
		urlLine.append("\", self.controllerUrl");
		int paramCount = 1;
		for (PathParamType param : operation.getPathParam()) {
			urlLine.append(", aString").append(paramCount++);
		}
		for (QueryParamType param : operation.getQueryParam()) {
			urlLine.append(", aString").append(paramCount++);
		}
		urlLine.append("];");
		StringBuffer result = new StringBuffer();
		result.append(urlLine).append("\r");
		
		result.append("    NSError *localError;\r");
		result.append("    [self doDeleteInvocation: fullUrl error:&localError];").append("\r");
		result.append("    if (error != nil) {\r");
		result.append("        *error = localError;\r");
		result.append("    }\r");
		return result.toString();
	}

	/**
	 * #import "Track.h"
	 * #import "Cd.h"
	 */
	public String getJsonDeserializerImportDeclarations(ModelDefinitionsType definitions) {
		StringBuffer result = new StringBuffer();
		for (ModelType type : definitions.getModel()) {
			result.append("#import \"").append(type.getName()).append(".h\"\n");
		}
		return result.toString();
	}

	/**
	 * #import "Track.h"
	 * #import "Cd.h"
	 */
	public String getJsonSerializerImportDeclarations(ModelDefinitionsType definitions) {
		return this.getJsonDeserializerImportDeclarations(definitions);
	}

	/**
	 * - (Track*)deserializeTrack:(NSString*)jsonString;
	 */
	public String getJsonDeserializerDeserializeMethodDeclaration(ModelType type) {
		StringBuffer result = new StringBuffer();
		result.append("- (").append(type.getName()).append(" *)");
		result.append("deserialize").append(type.getName());
		result.append(":(NSString*)jsonString");			
		return result.toString();
	}

	/**
	 * Generates the body of the following method:
	 * - (Track *)deserializeTrack:(NSString*)jsonString {
	 * 		SBJsonParser *jsonParser = [SBJsonParser new];
	 *  	NSDictionary *attributeDict = [jsonParser objectWithString:jsonString error:NULL];
	 *  	return [self extractTrack:attributeDict];
	 *  }
	 */
	public String getJsonDeserializerDeserializeMethodBody(ModelType type) {
		StringBuffer result = new StringBuffer();
		result.append("SBJsonParser *jsonParser = [SBJsonParser new];\n");
		result.append("    NSDictionary *attributeDict = [jsonParser objectWithString:jsonString error:NULL];\n");
		result.append("    return [self extract").append(type.getName()).append(":attributeDict]");
		return result.toString();
	}

	/**
	 * - (Track*)extractTrack:(NSDictionary*)attributeDict
	 */
	public String getJsonDeserializerExtractMethodDeclaration(ModelType type) {
		StringBuffer result = new StringBuffer();
		result.append("- (").append(type.getName()).append(" *)");
		result.append("extract").append(type.getName());
		result.append(":(NSDictionary*)attributeDict");			
		return result.toString();
	}

	/**
	 * Generates the body of the following method:
	 * 
	 * - (Track *)extractTrack:(NSDictionary*)attributeDict {
	 *     Track *result = [[[Track alloc] init] autorelease];
	 *     NSString *stringValue = nil;
	 *     NSNumber *numericValue = nil;
	 *     NSDictionary *objectValue = nil;
	 *     stringValue = [attributeDict objectForKey: @"title"];
	 *     if (stringValue) {
	 *     	result.title = stringValue;
	 *     }
	 *     numericValue = [attributeDict objectForKey: @"durationInSeconds"];
	 *     if (numericValue) {
	 *     	result.durationInSeconds = [numericValue integerValue];
	 *     }
	 *     numericValue = [attributeDict objectForKey: @"catalogId"];
	 *     if (numericValue) {
	 *     	result.catalogId = [numericValue longLongValue];
	 *     }
	 *     numericValue = [attributeDict objectForKey: @"newRelease"];
	 *     if (numericValue) {
	 *     	result.newRelease = [numericValue boolValue];
	 *     }
	 *     numericValue = [attributeDict objectForKey: @"price"];
	 *     if (numericValue) {
	 *     	result.price = [numericValue doubleValue];
	 *     }
	 *     objectValue = [attributeDict objectForKey: @"tracks"];
	 *     if (objectValue) {
	 *     	for (NSDictionary *anItem in objectValue) {
	 *     		Track *anObject = [self extractTrack:anItem];
	 *     		[result addToTracks:anObject];
	 *     	}
	 *     }
	 *     return result;
	 * }
	 */
	public String getJsonDeserializerExtracMethodBody(ModelType type) {
		StringBuffer result = new StringBuffer();
		result.append(type.getName()).append(" *result = [[").append(type.getName()).append(" alloc] init];\n");
		result.append("    NSString *stringValue = nil;\n");
		result.append("    NSString *numericValue = nil;\n");
		result.append("    NSDictionary *objectValue = nil;\n");
		// Simple attributes
		for (SimpleAttrType attribute : type.getSimpleAttr()) {
			String typeName = attribute.getType();
			if(this.isStringType(typeName)) {
				result.append("    stringValue = [attributeDict objectForKey: @\"").append(attribute.getName()).append("\"];\n");
				result.append("    if (stringValue) {\n");
				result.append("        result.").append(attribute.getName()).append(" = stringValue;\n");
				result.append("    }\n");
			}
			if(this.isBooleanType(typeName)) {
				result.append("    numericValue = [attributeDict objectForKey: @\"").append(attribute.getName()).append("\"];\n");
				result.append("    if (numericValue) {\n");
				result.append("        result.").append(attribute.getName()).append(" = [numericValue boolValue];\n");
				result.append("    }\n");
			}
			if(this.isDoubleType(typeName)) {
				result.append("    numericValue = [attributeDict objectForKey: @\"").append(attribute.getName()).append("\"];\n");
				result.append("    if (numericValue) {\n");
				result.append("        result.").append(attribute.getName()).append(" = [numericValue doubleValue];\n");
				result.append("    }\n");
			}
			if(this.isIntegerType(typeName)) {
				result.append("    numericValue = [attributeDict objectForKey: @\"").append(attribute.getName()).append("\"];\n");
				result.append("    if (numericValue) {\n");
				result.append("        result.").append(attribute.getName()).append(" = [numericValue integerValue];\n");
				result.append("    }\n");
			}
			if(this.isLongType(typeName)) {
				result.append("    numericValue = [attributeDict objectForKey: @\"").append(attribute.getName()).append("\"];\n");
				result.append("    if (numericValue) {\n");
				result.append("        result.").append(attribute.getName()).append(" = [numericValue longLongValue];\n");
				result.append("    }\n");
			}
			// Complex type attributes
			if(this.isComplexType(typeName)) {
				result.append("    objectValue = [attributeDict objectForKey: @\"").append(attribute.getName()).append("\"];\n");
				result.append("    if (objectValue) {\n");
				result.append("        result.").append(attribute.getName()).append(" = [self extract").append(attribute.getType()).append(":objectValue];\n");
				result.append("    }\n");
			}
		}
		// List type attributes
		for (ListAttrType attribute : type.getListAttr()) {
			result.append("    objectValue = [attributeDict objectForKey: @\"").append(attribute.getName()).append("\"];\n");
			result.append("    if (objectValue) {\n");
			result.append("        for (NSDictionary *anItem in objectValue) {\n");
			result.append("            ").append(attribute.getItemsType()).append(" *anObject = [self extract").append(attribute.getItemsType()).append(":anItem];\n");
			result.append("            [result addTo").append(this.toCapitalizedCase(attribute.getName())).append(":anObject];\n");
			result.append("        }\n");
			result.append("    }\n");
		}
		result.append("    return result");
		return result.toString();
	}
	
	/**
	 * Generates the body of the following:
	 * 
	 * - (NSString*)serializeTrack:(Track*)anObject {
	 *		NSDictionary *attributes = [self prepareAttributesTrack:anObject];
	 *  	SBJsonWriter *writer = [[SBJsonWriter alloc] init];
	 *  	NSError *error;
	 *  	NSString *jsonString = [writer stringWithObject: attributes error:&error];
	 *  	[writer release];
	 *  	return jsonString;
	 * }
	 */
	public String getJsonSerializerSerializeMethodBody(ModelType type) {
		StringBuffer result = new StringBuffer();
		result.append("NSDictionary *attributes = [self prepareAttributes").append(type.getName()).append(":anObject];\n");
		result.append("    SBJsonWriter *writer = [[SBJsonWriter alloc] init];\n");
		result.append("    NSError *error;\n");
		result.append("    NSString *jsonString = [writer stringWithObject: attributes error:&error];\n");
		result.append("    return jsonString");
		return result.toString();
	}

	/**
	 * Generates the following:
	 * 
	 * - (NSString*)serializeTrack:(Track*)anObject;
	 */
	public String getJsonSerializerSerializeMethodDeclaration(ModelType type) {
		StringBuffer result = new StringBuffer();
		result.append("- (NSString*)serialize").append(type.getName()).append(":(");
		result.append(type.getName());
		result.append("*)anObject");			
		return result.toString();
	}

	/**
	 * Generates the following:
	 * 
	 * - (NSMutableDictionary*)prepareAttributesTrack:(Track*)anObject
	 */
	public String getJsonSerializerPrepareAttributesMethodDeclaration(ModelType type) {
		StringBuffer result = new StringBuffer();
		result.append("- (NSMutableDictionary*)prepareAttributes").append(type.getName()).append(":(");
		result.append(type.getName());
		result.append("*)anObject");			
		return result.toString();
	}

	/**
	 * Generates the body of the following method:
	 * 
	 * - (NSMutableDictionary*)prepareAttributesTrack:(Track*)anObject {
	 * 		NSMutableDictionary *attributes = [NSMutableDictionary dictionary];
	 * 		if (anObject.title) {
	 * 			[attributes setObject:anObject.title forKey:@"title"];
	 * 		}
	 * 		[attributes setObject:[NSNumber numberWithInteger: anObject.durationInSeconds] forKey:@"durationInSeconds"];
	 * 		[attributes setObject:[NSNumber numberWithLongLong: anObject.catalogId] forKey:@"catalogId"];
	 * 		[attributes setObject:[NSNumber numberWithBool: anObject.newRelease] forKey:@"newRelease"];
	 * 		[attributes setObject:[NSNumber numberWithDouble: anObject.price] forKey:@"price"];
	 *     	if (anObject.mainTrack) {
	 *     		[attributes setObject:[self prepareAttributesTrack:anObject.mainTrack] forKey:@"mainTrack"];
	 *     	}
	 *     	if (anObject.tracks) {
	 *     		NSMutableArray *items = [NSMutableArray array];
	 *     		for (Track *item in anObject.tracks) {
	 *     			[items addObject:[self prepareAttributesTrack:item]];
	 *     		}
	 *     		[attributes setObject:items forKey:@"tracks"];
	 *     	}
	 * 		return attributes;
	 * }
	 */
	public String getJsonSerializerPrepareAttributesMethodBody(ModelType type) {
		StringBuffer result = new StringBuffer();
		result.append("NSMutableDictionary *attributes = [NSMutableDictionary dictionary];\n");
		// Simple attributes
		for (SimpleAttrType attribute : type.getSimpleAttr()) {
			String typeName = attribute.getType();
			String name = attribute.getName();
			if(this.isStringType(typeName)) {
				result.append("    if (anObject.").append(name).append(") {\n");
				result.append("        [attributes setObject:anObject.").append(name).append(" forKey:@\"").append(name).append("\"];\n");
				result.append("    }\n");
			}
			if(this.isBooleanType(typeName)) {
				result.append("    [attributes setObject:[NSNumber numberWithBool: anObject.").append(name).append("] forKey:@\"").append(name).append("\"];\n");
			}
			if(this.isDoubleType(typeName)) {
				result.append("    [attributes setObject:[NSDecimalNumber numberWithDouble: anObject.").append(name).append("] forKey:@\"").append(name).append("\"];\n");
			}
			if(this.isIntegerType(typeName)) {
				result.append("    [attributes setObject:[NSNumber numberWithInteger: anObject.").append(name).append("] forKey:@\"").append(name).append("\"];\n");
			}
			if(this.isLongType(typeName)) {
				result.append("    [attributes setObject:[NSNumber numberWithDouble: anObject.").append(name).append("] forKey:@\"").append(name).append("\"];\n");
			}
			// Complex type attributes
			if(this.isComplexType(typeName)) {
				result.append("    if (anObject.").append(name).append(") {\n");
				result.append("        [attributes setObject:[self prepareAttributes").append(typeName).append(":anObject.").append(name).append("] forKey:@\"").append(name).append("\"];\n");
				result.append("    }\n");
			}
		}
		// List type attributes
		for (ListAttrType attribute : type.getListAttr()) {
			String name = attribute.getName();
			result.append("    if (anObject.").append(name).append(") {\n");
			result.append("        NSMutableArray *items = [NSMutableArray array];\n");
			result.append("        for (").append(attribute.getItemsType()).append(" *item in anObject.").append(name).append(") {\n");
			result.append("            [items addObject:[self prepareAttributes").append(attribute.getItemsType()).append(":item]];\n");
			result.append("        }\n");
			result.append("        [attributes setObject:items forKey:@\"").append(name).append("\"];\n");
			result.append("    }\n");
		}
		result.append("    return attributes");
		return result.toString();
	}

	/**
	 * Replaces the params in the HTTP path with "%@". This is used to build the full URL of the 
	 * HTTP invocation
	 */
	public String replacePathParams(String httpPath, List<PathParamType> params) {
		String result = httpPath;
		for (PathParamType param : params) {
			result = result.replace("{" + param.getName() + "}", "%@");
		}
		return result;
	}
	
	public boolean isStringType(String modelType) {
		return this.getUtility().isStringType(modelType);
	}
	
	public boolean isLongType(String modelType) {
		return this.getUtility().isLongType(modelType);
	}
	
	public boolean isIntegerType(String modelType) {
		return this.getUtility().isIntegerType(modelType);
	}
	
	public boolean isDoubleType(String modelType) {
		return this.getUtility().isDoubleType(modelType);
	}
	
	public boolean isBooleanType(String modelType) {
		return this.getUtility().isBooleanType(modelType);
	}
	
	public boolean isUndefinedType(String modelType) {
		return modelType == null || modelType.trim().equals("");
	}
	
	public boolean isPrimitiveType(String modelType) {
		return this.getUtility().isPrimitiveType(modelType);
	}
	
	public boolean isComplexType(String modelType) {
		return this.getUtility().isComplexType(modelType);
	}

	public boolean isXmlSerialization(String serializationType) {
		return this.getUtility().isXmlSerialization(serializationType);
	}
	
	public boolean isJsonSerialization(String serializationType) {
		return this.getUtility().isJsonSerialization(serializationType);
	}
	
}
