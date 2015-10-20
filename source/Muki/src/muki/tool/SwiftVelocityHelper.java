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

import java.util.List;

import muki.tool.model.ControllerType;
import muki.tool.model.DeleteOperationType;
import muki.tool.model.GetOperationType;
import muki.tool.model.ListAttrType;
import muki.tool.model.ModelType;
import muki.tool.model.PathParamType;
import muki.tool.model.PostOperationType;
import muki.tool.model.PutOperationType;
import muki.tool.model.QueryParamType;
import muki.tool.model.SimpleAttrType;

/**
 * This class offers support during the code generation with Velocity templates. The templates include invocations
 * to the operations of this helper to create Swift code fragments that otherwise would be complicated to generate with
 * the template itself.
 */
public class SwiftVelocityHelper extends VelocityHelper {

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
	
	private String getSwiftPrimitiveType(String typeName) {
		return this.getUtility().getSwiftPrimitiveType(typeName);
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
			if (this.isBooleanType(typeName)) {
				result.append("var ").append(attribute.getName()).append(" = false");
			} else if (this.isIntegerType(typeName)) {
				result.append("var ").append(attribute.getName()).append(" = 0");
			} else if (this.isLongType(typeName)) {
				result.append("var ").append(attribute.getName()).append(": Int64 = 0");
			} else if (this.isDoubleType(typeName)) {
				result.append("var ").append(attribute.getName()).append(" = 0.0");
			} else if (this.isStringType(typeName)) {
				result.append("var ").append(attribute.getName()).append(" = \"\"");
			}
		} else {
			result.append("var ").append(attribute.getName()).append(": ").append(typeName).append("?");
		}
		return result.toString();
	}
	
	public String getAttributeDeclaration(ListAttrType attribute) {
		StringBuffer result = new StringBuffer();
		String typeName = attribute.getItemsType();
		result.append("var ").append(attribute.getName()).append(" = [").append(typeName).append("]()");
		return result.toString();
	}
	
	public String getAddMethodDeclaration(ListAttrType attribute) {
		StringBuffer result = new StringBuffer();
		String typeName = attribute.getItemsType();
		result.append("    func addTo").append(this.toCapitalizedCase(attribute.getName())).
			append("(anObject: ").append(typeName).append(") {\n" ).
			append("        self.").append(attribute.getName()).append(".append(anObject)\n").
			append("    }");
		return result.toString();
	}

	public String getRemoveMethodDeclaration(ListAttrType attribute) {
		StringBuffer result = new StringBuffer();
		String typeName = attribute.getItemsType();
		result.append("    func removeFrom").append(this.toCapitalizedCase(attribute.getName())).
			append("(anObject: ").append(typeName).append(") {\n" ).
			append("        let index = find(self.").append(attribute.getName()).append(", anObject)\n").
			append("        if (index != nil) {\n").
			append("            self.").append(attribute.getName()).append(".removeAtIndex(index!)\n").
			append("        }\n").
		    append("    }\n");
		return result.toString();
	}
	
	public String getParserObjectDeclaration(ModelType type) {
		StringBuffer result = new StringBuffer();
		result.append("var object: ").append(type.getName()).append("?");
		return result.toString(); 
	}

	public String getParserName(ModelType type) {
		StringBuffer result = new StringBuffer();
		result.append(type.getName()).append("ParserDelegate");
		return result.toString(); 
	}

	public String getParserName(SimpleAttrType attribute) {
		StringBuffer result = new StringBuffer();
		result.append(this.toCapitalizedCase(attribute.getType())).append("ParserDelegate");
		return result.toString(); 
	}

	public String getParserCallbackDeclaration(ModelType type) {
		StringBuffer result = new StringBuffer();
		result.append("var containerCallback: ((").append(type.getName()).append(") -> ())?");
		return result.toString(); 
	}

	public String getParserCallbackConstructor(ModelType type) {
		StringBuffer result = new StringBuffer();
		result.append("    convenience init(parent: ObjectParserDelegate?, aParser: NSXMLParser, ");
		result.append("containerCallback: (").append(type.getName()).append(") -> ()) {\n");
		result.append("        self.init(parent: parent, aParser: aParser)\n");
		result.append("        self.containerCallback = containerCallback\n");
		result.append("    }");
		return result.toString(); 
	}

	public String getParserInitObjectMethod(ModelType type) {
		StringBuffer result = new StringBuffer();
		result.append("    override func initObject() {\n");
		result.append("        self.object = ").append(type.getName()).append("()\n");			
		result.append("    }");			
		return result.toString();
	}

	public String getParserInjectMethod(SimpleAttrType attribute) {
		StringBuffer result = new StringBuffer();
		if (this.isComplexType(attribute.getType())) {
			result.append("    func inject").append(this.toCapitalizedCase(attribute.getName()));
			result.append("(anObject: ").append(attribute.getType()).append(") {\n");			
			result.append("        self.object?.").append(attribute.getName()).append(" = anObject\n");
			result.append("    }");			
		}
		return result.toString();
	}

	public String getParserInjectMethod(ListAttrType attribute) {
		StringBuffer result = new StringBuffer();
		result.append("    func injectInto").append(this.toCapitalizedCase(attribute.getName()));
		result.append("(anObject: ").append(attribute.getItemsType()).append(") {\n");			
		result.append("        self.object?.addTo").append(this.toCapitalizedCase(attribute.getName())).append("(anObject)\n");
		result.append("    }");			
		return result.toString();
	}
	
	/**
	 * self.object!.durationInSeconds = self.toInt(value)
	 */
	public String getDidStartElementAssigmentExpression(SimpleAttrType attribute) {
		StringBuffer result = new StringBuffer();
		result.append("self.object?.").append(attribute.getName()).append(" = ");
		String typeName = attribute.getType();
		if(this.isLongType(typeName)) {
			result.append("self.toInt64(value)");
		}
		if(this.isIntegerType(typeName)) {
			result.append("self.toInt(value)");
		}
		if(this.isDoubleType(typeName)) {
			result.append("self.toDouble(value)");
		}
		if(this.isBooleanType(typeName)) {
			result.append("self.toBool(value)");
		}
		return result.toString();
	}
		
	/**
	 * self.object.name = self.currentStringValue
	 */
	public String getDidEndElementAssigmentExpression(SimpleAttrType attribute) {
		if(!this.isStringType(attribute.getType())) {
			return null;
		}
		StringBuffer result = new StringBuffer();
		result.append("self.object?.").append(attribute.getName()).append(" = self.currentStringValue");
		return result.toString();
	}
	
	/**
	 * Generates the following fragment:
	 *   let callback = self.injectMainTrack
     *   let delegate = TrackParserDelegate(parent: self, aParser: parser, containerCallback: callback)
	 */
	public String getParserChildDelegateDeclaration(SimpleAttrType attribute) {
		StringBuffer result = new StringBuffer();
		result.append("let callback = self.inject").append(this.toCapitalizedCase(attribute.getName())).append("\n");
		result.append("            let delegate = ").append(attribute.getType()).append("ParserDelegate(parent: self, aParser: parser, containerCallback: callback)");
		return result.toString();
	}
	
	/**
	 * Generates the following fragment:
	 *   let callback = self.injectIntoTracks
	 *   let delegate = TrackParserDelegate(parent: self, aParser: parser, containerCallback: callback)
	 */
	public String getParserChildDelegateDeclaration(ListAttrType attribute) {
		StringBuffer result = new StringBuffer();
		result.append("let callback = self.injectInto").append(this.toCapitalizedCase(attribute.getName())).append("\n");
		result.append("            let delegate = ").append(attribute.getItemsType()).append("ParserDelegate(parent: self, aParser: parser, containerCallback: callback)");
		return result.toString();
	}
	
	/**
	 * Generates the following fragment:
	 *   func serializeTrack(anObject: Track) {
     *       self.serializeTrack(anObject, element: "track")
     *   }
	 */
	public String getXmlSerializerSerializeMethod(ModelType type) {
		StringBuffer result = new StringBuffer();
		result.append("    func serialize").append(type.getName()).append("(anObject: ").append(type.getName()).append(") {\n");
		result.append("        self.serialize").append(type.getName()).append("(anObject, elementName: \"").append(this.toLowerCase(type.getName())).append("\")\n");
		result.append("    }\n");
		return result.toString();
	}

	/**
	 * Generates the following fragment:
	 *   func serializeTrack(anObject: Track) {
     *       self.serializeTrack(anObject, element: "track")
     *   }
	 */
	public String getXmlSerializerSerializeElementMethod(ModelType type) {
		StringBuffer result = new StringBuffer();
		result.append("    func serialize").append(type.getName()).append("(anObject: ").append(type.getName()).append(", elementName: String) {\n");
		result.append("        var attributes = [XmlAttribute]()\n");
		result.append("        var attr: XmlAttribute?\n");
		// Basic type attributes, except String
		for (SimpleAttrType attribute : type.getSimpleAttr()) {
			String typeName = attribute.getType();
			if(this.isBooleanType(typeName)) {
				result.append("        attr = XmlAttribute(name: \"").append(attribute.getName()).append("\",");
				result.append(" booleanValue: anObject.").append(attribute.getName()).append(")\n");
				result.append("        attributes.append(attr!)\n");
			}
			if(this.isIntegerType(typeName)) {
				result.append("        attr = XmlAttribute(name: \"").append(attribute.getName()).append("\",");
				result.append(" int: anObject.").append(attribute.getName()).append(")\n");
				result.append("        attributes.append(attr!)\n");
			}
			if(this.isLongType(typeName)) {
				result.append("        attr = XmlAttribute(name: \"").append(attribute.getName()).append("\",");
				result.append(" long: anObject.").append(attribute.getName()).append(")\n");
				result.append("        attributes.append(attr!)\n");
			}
			if(this.isDoubleType(typeName)) {
				result.append("        attr = XmlAttribute(name: \"").append(attribute.getName()).append("\",");
				result.append(" double: anObject.").append(attribute.getName()).append(")\n");
				result.append("        attributes.append(attr!)\n");
			}
		}
		result.append("        self.openElement(elementName, attributes: attributes)\n");

		// String type attributes
		for (SimpleAttrType attribute : type.getSimpleAttr()) {
			if(this.isStringType(attribute.getType())) {
				result.append("        self.openElement(\"").append(attribute.getName()).append("\")\n");
				result.append("        self.xmlOutput = self.xmlOutput + self.encodeString(anObject.").append(attribute.getName()).append(")\n");
				result.append("        self.closeElement(\"").append(attribute.getName()).append("\")\n");
			}
		}
		// Complex type attributes
		for (SimpleAttrType attribute : type.getSimpleAttr()) {
			if(this.isComplexType(attribute.getType())) {
				result.append("        if (anObject.").append(attribute.getName()).append(" != nil) {\n");
				result.append("            self.serialize").append(attribute.getType()).append("(anObject.").append(attribute.getName()).append("!, elementName: \"").append(attribute.getName()).append("\")\n");
				result.append("        }\n");
			}
		}
		
		// List type attributes
		for (ListAttrType attribute : type.getListAttr()) {
			result.append("        self.openElement(\"").append(attribute.getName()).append("\")\n");
			result.append("        for anItem in anObject.").append(attribute.getName()).append(" {\n");
			result.append("            self.serialize").append(attribute.getItemsType()).append("(anItem)\n");
			result.append("        }\n");
			result.append("        self.closeElement(\"").append(attribute.getName()).append("\")\n");
		}
		result.append("        self.closeElement(elementName)\n");
		result.append("    }\n");
		return result.toString();
	}
	
	
	/**
	 * Generates the following code:
	 * func deserializeXmlCd(xml: String) -> Cd? {
	 *     let delegate = CdParserDelegate()
	 *     var xmlData = (xml as NSString).dataUsingEncoding(NSUTF8StringEncoding)
	 *     var parser = NSXMLParser(data: xmlData!)
	 *     parser.delegate = delegate
	 *     let success = parser.parse()
	 *     if (success == false) {
	 *         return nil
	 *     }
	 *     let object = delegate.object
	 *     return object
	 * }
	 */
	public String getControllerStubDeserializeMethod(ModelType type) {
		StringBuffer result = new StringBuffer();
		result.append("    func deserializeXml").append(type.getName()).append("(xml: String) -> ").append(type.getName()).append("? {\n");
		result.append("        let delegate = ").append(type.getName()).append("ParserDelegate()\n");
		result.append("        var xmlData = (xml as NSString).dataUsingEncoding(NSUTF8StringEncoding)\n");
		result.append("        var parser = NSXMLParser(data: xmlData!)\n");
		result.append("        parser.delegate = delegate\n");
		result.append("        let success = parser.parse()\n");
		result.append("        if (success == false) {\n");
		result.append("            return nil\n");
		result.append("        }\n");
		result.append("        let object = delegate.object\n");
		result.append("        return object\n");
		result.append("    }\n");
		return result.toString(); 
	}

	
	/**
	 * Generates the following code:
	 * func serializeXmlTrack(anObject: Track) -> String? {
	 *     let generator = XmlSerializer()
	 *     generator.serializeTrack(anObject)
	 *     let xml = generator.xmlOutput
	 *     return xml
	 * }
	 */
	public String getControllerStubSerializeMethod(ModelType type) {
		StringBuffer result = new StringBuffer();
		result.append("    func serializeXml").append(type.getName()).append("(anObject: ").append(type.getName()).append(") -> String? {\n");
		result.append("        let generator = XmlSerializer()\n");
		result.append("        generator.serialize").append(type.getName()).append("(anObject)\n");
		result.append("        let xml = generator.xmlOutput\n");
		result.append("        return xml\n");
		result.append("    }\n");
		return result.toString(); 
	}

	/**
	 * Generates the following code:
	 * func deserializeJsonCd(json: String) -> Cd? {
	 *    let deserializer = JsonDeserializer()
	 *    let anObject = deserializer.deserializeCd(json)
	 *    return anObject
	 * }
	 */
	public String getControllerStubJsonDeserializeMethod(ModelType type) {
		StringBuffer result = new StringBuffer();
		result.append("    func deserializeJson").append(type.getName()).append("(json: String) -> ").append(type.getName()).append("? {\n");
		result.append("        let deserializer = JsonDeserializer()\n");
		result.append("        let anObject = deserializer.deserialize").append(type.getName()).append("(json)\n");
		result.append("        return anObject\n");
		result.append("    }\n");
		return result.toString(); 
	}

	/**
	 * Generates the following code:
	 * func serializeJsonCd(anObject: Cd) -> String? {
	 *    let serializer = JsonSerializer()
	 *    let json = serializer.serializeCd(anObject)
	 *    return json
	 * }
	 */
	public String getControllerStubJsonSerializeMethod(ModelType type) {
		StringBuffer result = new StringBuffer();
		result.append("    func serializeJson").append(type.getName()).append("(anObject: ").append(type.getName()).append(") -> String? {\n");
		result.append("        let serializer = JsonSerializer()\n");
		result.append("        let json = serializer.serialize").append(type.getName()).append("(anObject)\n");
		result.append("        return json\n");
		result.append("    }\n");
		return result.toString(); 
	}

	public String getControllerStubName(ControllerType controller) {
		StringBuffer result = new StringBuffer();
		result.append(controller.getName()).append("Stub");
		return result.toString(); 
	}

	/**
	 * Generates the following code:
	 * func getOperation8Xml(id: String, error outError: NSErrorPointer) -> Cd? {
	 *     let fullUrl = "/pathGetOperation8/\(self.controllerUrl)/\(id)"
	 *     let reply = self.doGetInvocation(fullUrl, contentType: "application/xml; charset=UTF-8", error: outError)
	 *     if (reply == nil) {
	 *         return nil
	 *     } else {
	 *         let object = self.deserializeXmlCd(reply!)
	 *         return object
	 *     }
	 * }
	 */
	public String getControllerStubMethod(GetOperationType operation) {
		StringBuffer result = new StringBuffer();
		// Function header
		result.append("    func ").append(operation.getName()).append("(");
		boolean isFirst = true;
		for (PathParamType param : operation.getPathParam()) {
			if (isFirst) {
				isFirst = false;
			} else {
				result.append(", ");
			}
			result.append(param.getName()).append(":").append(" String");	
		}
		for (QueryParamType param : operation.getQueryParam()) {
			if (isFirst) {
				isFirst = false;
			} else {
				result.append(", ");
			}
			result.append(param.getName()).append(":").append(" String");	
		}
		// Add the "error" param to the list
		if (!isFirst) {
			result.append(", ");
		}
		result.append("error outError: NSErrorPointer) -> ");
		if(this.isStringType(operation.getReturnType())) {
			result.append(this.getSwiftPrimitiveType(operation.getReturnType())).append("? {\n");
		} else {
			result.append(operation.getReturnType()).append("? {\n");			
		}
		
		// Function body
		String path = this.replacePathParams(operation.getHttpPath(), operation.getPathParam());		
		StringBuffer urlLine = new StringBuffer();
		urlLine.append("let fullUrl = \"").append("\\(self.controllerUrl)").append(path);
		isFirst = true;
		for (QueryParamType param : operation.getQueryParam()) {
			if(isFirst) {
				urlLine.append("?");
				isFirst = false;
			} else {
				urlLine.append("&");			
			}	
			urlLine.append(param.getName()).append("=\\(").append(param.getName()).append(")");	
		}
		result.append("        ").append(urlLine).append("\"\n");
		result.append("        let reply = self.doGetInvocation(fullUrl, contentType: \"application/xml; charset=UTF-8\", error: outError)\n");
		result.append("        if (reply == nil) {\n");
		result.append("            return nil\n");
		result.append("        } else {\n");
		String returnType = operation.getReturnType();;
		if(this.isComplexType(returnType)) {
			String token = "Xml";
			if(this.isJsonSerialization(operation.getSerializationType())) {
				token = "Json";
			}
			// let object = self.deserializeXmlCd(reply!)
			// return object
			result.append("            let object = self.deserialize").append(token).append(returnType).append("(reply!)\n");
			result.append("            return object").append("\n");
		}
		if(this.isStringType(returnType)) {
			// return reply
			result.append("            return reply\n");
		}
		result.append("        }\n");
		result.append("    }\n");
		return result.toString();
	}
	
	/**
	 * Generates the following code:
	 * func postOperation7Xml(anObject: Cd, error outError: NSErrorPointer) -> Track? {
	 *     let fullUrl = "\(self.controllerUrl)/pathPostOperation7Xml"
	 *     let messageBody = self.serializeXmlCd(anObject)
	 *     let reply = self.doPostInvocation(fullUrl, messageBody: messageBody, contentType: "application/xml; charset=UTF-8", error: outError)
	 *     if (reply == nil) {
	 *         return nil
	 *     } else {
	 *         let object = self.deserializeXmlTrack(reply!)
	 *         return object
	 *     }
	 * }
	 */
	public String getControllerStubMethod(PostOperationType operation) {
		StringBuffer result = new StringBuffer();
		// Function header
		result.append("    func ").append(operation.getName()).append("(");
		boolean isFirst = true;
		if(this.isStringType(operation.getParamType())) {
			if (isFirst) {
				isFirst = false;
			} else {
				result.append(", ");
			}
			result.append("aString: String");
		}
		if(this.isComplexType(operation.getParamType())) {
			if (isFirst) {
				isFirst = false;
			} else {
				result.append(", ");
			}
			result.append("anObject: ").append(operation.getParamType());
		}
		for (PathParamType param : operation.getPathParam()) {
			if (isFirst) {
				isFirst = false;
			} else {
				result.append(", ");
			}
			result.append(param.getName()).append(":").append(" String");	
		}
		for (QueryParamType param : operation.getQueryParam()) {
			if (isFirst) {
				isFirst = false;
			} else {
				result.append(", ");
			}
			result.append(param.getName()).append(":").append(" String");	
		}
		// Add the "error" param to the list
		if (!isFirst) {
			result.append(", ");
		}
		if(this.isUndefinedType(operation.getReturnType())) {
			result.append("error outError: NSErrorPointer) {\n");
		} else if(this.isStringType(operation.getReturnType())) {
			result.append("error outError: NSErrorPointer) -> ").append(this.getSwiftPrimitiveType(operation.getReturnType())).append("? {\n");
		} else {
			result.append("error outError: NSErrorPointer) -> ").append(operation.getReturnType()).append("? {\n");			
		}
		
		// Function body
		String path = this.replacePathParams(operation.getHttpPath(), operation.getPathParam());		
		StringBuffer urlLine = new StringBuffer();
		urlLine.append("let fullUrl = \"").append("\\(self.controllerUrl)").append(path);
		isFirst = true;
		for (QueryParamType param : operation.getQueryParam()) {
			if(isFirst) {
				urlLine.append("?");
				isFirst = false;
			} else {
				urlLine.append("&");			
			}	
			urlLine.append(param.getName()).append("=\\(").append(param.getName()).append(")");	
		}
		result.append("        ").append(urlLine).append("\"\n");
		
		StringBuffer invocation = new StringBuffer();
		String contentType = "application/xml; charset=UTF-8";
		if (this.isJsonSerialization(operation.getSerializationType())) {
			contentType = "application/json; charset=UTF-8";
		}
		
		// Post body
		String paramType = operation.getParamType();
		if(this.isComplexType(paramType)) {
			// let messageBody = self.serializeXmlCd(anObject)
			// let reply = self.doPostInvocation(fullUrl, messageBody: messageBody, contentType: "application/xml; charset=UTF-8", error: outError)
			String token = "Xml";
			if(this.isJsonSerialization(operation.getSerializationType())) {
				token = "Json";
			}
			result.append("        let messageBody = self.serialize").append(token).append(operation.getParamType()).append("(anObject)\n");
			invocation.append("self.doPostInvocation(fullUrl, messageBody: messageBody, contentType: \"").append(contentType).append("\", error: outError)");
		}
		if(this.isStringType(paramType)) {
			invocation.append("self.doPostInvocation(fullUrl, messageBody: aString, contentType: \"").append(contentType).append("\", error: outError)");
		}
		if(this.isUndefinedType(paramType)) {
			invocation.append("self.doPostInvocation(fullUrl, messageBody: nil, contentType: \"").append(contentType).append("\", error: outError)");
		}
		String returnType = operation.getReturnType();
		if(this.isComplexType(returnType)) {
			String token = "Xml";
			if(this.isJsonSerialization(operation.getSerializationType())) {
				token = "Json";
			}
			result.append("        let reply = ").append(invocation).append("\n");
			result.append("        if (reply == nil) {\n");
			result.append("            return nil\n");
			result.append("        } else {\n");
			result.append("            let object = self.deserialize").append(token).append(returnType).append("(reply!)\n");
			result.append("            return object").append("\n");
			result.append("        }\n");
			result.append("    }\n");
		}
		if(this.isStringType(returnType)) {
			result.append("        let reply = ").append(invocation).append("\n");
			result.append("        return reply\n");
			result.append("    }\n");
		}
		if(this.isUndefinedType(returnType)) {
			result.append("        ").append(invocation).append(";\r");
			result.append("    }\n");
		}
		return result.toString();
	}
	
	/**
	 * This is similar to the PutOperationType's method.
	 * Generates the following code:
	 * func putOperation7Xml(anObject: Cd, error outError: NSErrorPointer) -> Track? {
	 *     let fullUrl = "\(self.controllerUrl)/pathPostOperation7Xml"
	 *     let messageBody = self.serializeXmlCd(anObject)
	 *     let reply = self.doPutInvocation(fullUrl, messageBody: messageBody, contentType: "application/xml; charset=UTF-8", error: outError)
	 *     if (reply == nil) {
	 *         return nil
	 *     } else {
	 *         let object = self.deserializeXmlTrack(reply!)
	 *         return object
	 *     }
	 * }
	 */
	public String getControllerStubMethod(PutOperationType operation) {
		StringBuffer result = new StringBuffer();
		// Function header
		result.append("    func ").append(operation.getName()).append("(");
		boolean isFirst = true;
		if(this.isStringType(operation.getParamType())) {
			if (isFirst) {
				isFirst = false;
			} else {
				result.append(", ");
			}
			result.append("aString: String");
		}
		if(this.isComplexType(operation.getParamType())) {
			if (isFirst) {
				isFirst = false;
			} else {
				result.append(", ");
			}
			result.append("anObject: ").append(operation.getParamType());
		}
		for (PathParamType param : operation.getPathParam()) {
			if (isFirst) {
				isFirst = false;
			} else {
				result.append(", ");
			}
			result.append(param.getName()).append(":").append(" String");	
		}
		for (QueryParamType param : operation.getQueryParam()) {
			if (isFirst) {
				isFirst = false;
			} else {
				result.append(", ");
			}
			result.append(param.getName()).append(":").append(" String");	
		}
		// Add the "error" param to the list
		if (!isFirst) {
			result.append(", ");
		}
		if(this.isUndefinedType(operation.getReturnType())) {
			result.append("error outError: NSErrorPointer) {\n");
		} else if(this.isStringType(operation.getReturnType())) {
			result.append("error outError: NSErrorPointer) -> ").append(this.getSwiftPrimitiveType(operation.getReturnType())).append("? {\n");
		} else {
			result.append("error outError: NSErrorPointer) -> ").append(operation.getReturnType()).append("? {\n");			
		}
		
		// Function body
		String path = this.replacePathParams(operation.getHttpPath(), operation.getPathParam());		
		StringBuffer urlLine = new StringBuffer();
		urlLine.append("let fullUrl = \"").append("\\(self.controllerUrl)").append(path);
		isFirst = true;
		for (QueryParamType param : operation.getQueryParam()) {
			if(isFirst) {
				urlLine.append("?");
				isFirst = false;
			} else {
				urlLine.append("&");			
			}	
			urlLine.append(param.getName()).append("=\\(").append(param.getName()).append(")");	
		}
		result.append("        ").append(urlLine).append("\"\n");
		
		StringBuffer invocation = new StringBuffer();
		String contentType = "application/xml; charset=UTF-8";
		if (this.isJsonSerialization(operation.getSerializationType())) {
			contentType = "application/json; charset=UTF-8";
		}
		
		// Post body
		String paramType = operation.getParamType();
		if(this.isComplexType(paramType)) {
			// let messageBody = self.serializeXmlCd(anObject)
			// let reply = self.doPutInvocation(fullUrl, messageBody: messageBody, contentType: "application/xml; charset=UTF-8", error: outError)
			String token = "Xml";
			if(this.isJsonSerialization(operation.getSerializationType())) {
				token = "Json";
			}
			result.append("        let messageBody = self.serialize").append(token).append(operation.getParamType()).append("(anObject)\n");
			invocation.append("self.doPutInvocation(fullUrl, messageBody: messageBody, contentType: \"").append(contentType).append("\", error: outError)");
		}
		if(this.isStringType(paramType)) {
			invocation.append("self.doPutInvocation(fullUrl, messageBody: aString, contentType: \"").append(contentType).append("\", error: outError)");
		}
		if(this.isUndefinedType(paramType)) {
			invocation.append("self.doPutInvocation(fullUrl, messageBody: nil, contentType: \"").append(contentType).append("\", error: outError)");
		}
		String returnType = operation.getReturnType();
		if(this.isComplexType(returnType)) {
			String token = "Xml";
			if(this.isJsonSerialization(operation.getSerializationType())) {
				token = "Json";
			}
			result.append("        let reply = ").append(invocation).append("\n");
			result.append("        if (reply == nil) {\n");
			result.append("            return nil\n");
			result.append("        } else {\n");
			result.append("            let object = self.deserialize").append(token).append(returnType).append("(reply!)\n");
			result.append("            return object").append("\n");
			result.append("        }\n");
			result.append("    }\n");
		}
		if(this.isStringType(returnType)) {
			result.append("        let reply = ").append(invocation).append("\n");
			result.append("        return reply\n");
			result.append("    }\n");
		}
		if(this.isUndefinedType(returnType)) {
			result.append("        ").append(invocation).append(";\r");
			result.append("    }\n");
		}
		return result.toString();
	}
	
	/**
	 * Generates the following code:
	 * func deleteOperation6(error outError: NSErrorPointer) {
	 *    let fullUrl = "\(self.controllerUrl)/pathPutOperation12Json/artists/"
	 *    self.doDeleteInvocation(fullUrl, error: outError)
	 * }
	 */
	public String getControllerStubMethod(DeleteOperationType operation) {
		StringBuffer result = new StringBuffer();
		// Function header
		result.append("    func ").append(operation.getName()).append("(");
		boolean isFirst = true;
		for (PathParamType param : operation.getPathParam()) {
			if (isFirst) {
				isFirst = false;
			} else {
				result.append(", ");
			}
			result.append(param.getName()).append(":").append(" String");	
		}
		for (QueryParamType param : operation.getQueryParam()) {
			if (isFirst) {
				isFirst = false;
			} else {
				result.append(", ");
			}
			result.append(param.getName()).append(":").append(" String");	
		}
		// Add the "error" param to the list
		if (!isFirst) {
			result.append(", ");
		}
		result.append("error outError: NSErrorPointer) {\n");
		
		// Function body
		String path = this.replacePathParams(operation.getHttpPath(), operation.getPathParam());		
		StringBuffer urlLine = new StringBuffer();
		urlLine.append("let fullUrl = \"").append("\\(self.controllerUrl)").append(path);
		isFirst = true;
		for (QueryParamType param : operation.getQueryParam()) {
			if(isFirst) {
				urlLine.append("?");
				isFirst = false;
			} else {
				urlLine.append("&");			
			}	
			urlLine.append(param.getName()).append("=\\(").append(param.getName()).append(")");	
		}
		result.append("        ").append(urlLine).append("\"\n");
		result.append("        self.doDeleteInvocation(fullUrl, error: outError)\n");
		result.append("    }\n");
		return result.toString();
	}
	
	/**
	 * Generates the following code:
	 * func deserializeTrack(jsonString: String) -> Track? {
	 *     if let dataFromString = jsonString.dataUsingEncoding(NSUTF8StringEncoding, allowLossyConversion: false) {
	 *         let json = JSON(data: dataFromString)
	 *         return self.extractTrack(json)
	 *     }
	 *     return nil
	 * }
	 */
	public String getJsonDeserializerDeserializeMethod(ModelType type) {
		StringBuffer result = new StringBuffer();
		result.append("    func deserialize").append(type.getName()).append("(jsonString: String) -> ").append(type.getName()).append("? {\n");
		result.append("        if let dataFromString = jsonString.dataUsingEncoding(NSUTF8StringEncoding, allowLossyConversion: false) {\n");
		result.append("            let json = JSON(data: dataFromString)\n");
		result.append("            return self.extract").append(type.getName()).append("(json)\n");
		result.append("        }\n");
		result.append("        return nil\n");
		result.append("    }\n");
		return result.toString(); 
	}
	
	/**
	 * Generates the following code:
	 * func extractCd(json: JSON) -> Cd? {
	 *   if (json.dictionary != nil) {
	 *      let result = Cd()
	 *      var value: JSON
	 *      value = json["title"]
	 *      result.title = self.toString(value)
	 *      value = json["artist"]
	 *      result.artist = self.toString(value)
	 *      value = json["mainTrack"]
	 *      result.mainTrack = self.extractTrack(value)
	 *      if let list = json["tracks"].array {
	 *         for jsonObject in list {
	 *             if let object = self.extractTrack(jsonObject) {
	 *                  result.addToTracks(object)
	 *             }
	 *         }
	 *      }
	 *      return result
	 *    } else {
	 *      return nil
	 *    }
	 * }
	 */
	public String getJsonDeserializerExtractMethod(ModelType type) {
		StringBuffer result = new StringBuffer();
		result.append("    func extract").append(type.getName()).append("(json: JSON) -> ").append(type.getName()).append("? {\n");
		result.append("        if (json.dictionary != nil) {\n");
		result.append("            let result = ").append(type.getName()).append("()\n");
		result.append("            var value: JSON\n");
		// Simple attributes
		for (SimpleAttrType attribute : type.getSimpleAttr()) {
			String typeName = attribute.getType();
			if(this.isStringType(typeName)) {
				result.append("            value = json[\"").append(attribute.getName()).append("\"]\n");
				result.append("            result.").append(attribute.getName()).append(" = self.toString(value)\n");
			}
			if(this.isBooleanType(typeName)) {
				result.append("            value = json[\"").append(attribute.getName()).append("\"]\n");
				result.append("            result.").append(attribute.getName()).append(" = self.toBool(value)\n");
			}
			if(this.isDoubleType(typeName)) {
				result.append("            value = json[\"").append(attribute.getName()).append("\"]\n");
				result.append("            result.").append(attribute.getName()).append(" = self.toDouble(value)\n");
			}
			if(this.isIntegerType(typeName)) {
				result.append("            value = json[\"").append(attribute.getName()).append("\"]\n");
				result.append("            result.").append(attribute.getName()).append(" = self.toInt(value)\n");
			}
			if(this.isLongType(typeName)) {
				result.append("            value = json[\"").append(attribute.getName()).append("\"]\n");
				result.append("            result.").append(attribute.getName()).append(" = self.toInt64(value)\n");
			}
			// Complex type attributes
			if(this.isComplexType(typeName)) {
				result.append("            value = json[\"").append(attribute.getName()).append("\"]\n");
				result.append("            result.").append(attribute.getName()).append(" = self.extract").append(attribute.getType()).append("(value)\n");
			}
		}
		// List type attributes
		for (ListAttrType attribute : type.getListAttr()) {
			result.append("            if let list = json[\"").append(attribute.getName()).append("\"].array {\n");
			result.append("                for jsonObject in list {\n");
			result.append("                    if let object = self.extract").append(attribute.getItemsType()).append("(jsonObject) {\n");
			result.append("                        result.addTo").append(this.toCapitalizedCase(attribute.getName())).append("(object)\n");
			result.append("                    }\n");
			result.append("                }\n");
			result.append("            }\n");
		}		
		result.append("            return result\n");
		result.append("        } else {\n");
		result.append("            return nil\n");
		result.append("        }\n");
		result.append("    }\n");
		return result.toString(); 
	}
	
	/**
	 * Generates the following code:
	 * func serializeTrack(anObject: Track) -> String? {
	 *     let attributes = self.prepareAttributesTrack(anObject)
	 *     return self.serializeToJson(attributes)
	 * }
	 */
	public String getJsonSerializerSerializeMethod(ModelType type) {
		StringBuffer result = new StringBuffer();
		result.append("    func serialize").append(type.getName()).append("(anObject: ").append(type.getName()).append(") -> String? {\n");
		result.append("        let attributes = self.prepareAttributes").append(type.getName()).append("(anObject)\n");
		result.append("        return self.serializeToJson(attributes)\n");
		result.append("    }\n");
		return result.toString(); 
	}	
	
	/**
	 * Generates the following code:
	 * func prepareAttributesCd(anObject: Cd) -> [ String: NSObject ] {
	 *     var attributes = [String : NSObject]()
	 *     attributes["title"] = anObject.title
	 *     attributes["artist"] = anObject.artist
	 *     attributes["catalogId"] = NSNumber(longLong:anObject.catalogId)
	 *     if (anObject.mainTrack != nil) {
	 *          attributes["mainTrack"] = self.prepareAttributesTrack(anObject.mainTrack!)
	 *     }
	 *     if (anObject.tracks.count > 0) {
	 *         var itemsArray = [NSObject]()
	 *         for item in anObject.tracks {
	 *             itemsArray.append(self.prepareAttributesTrack(item))
	 *         }
	 *         attributes["tracks"] = itemsArray
	 *     }
	 *     return attributes
	 * }
	 */
	public String getJsonSerializerPrepareAttributesMethod(ModelType type) {
		StringBuffer result = new StringBuffer();
		result.append("    func prepareAttributes").append(type.getName()).append("(anObject: ").append(type.getName()).append(") -> [ String: NSObject ] {\n");
		result.append("        var attributes = [String : NSObject]()\n");
		for (SimpleAttrType attribute : type.getSimpleAttr()) {
			String typeName = attribute.getType();
			if(this.isStringType(typeName)) {
				result.append("        attributes[\"").append(attribute.getName()).append("\"] = anObject.").append(attribute.getName()).append("\n");
			}
			if(this.isBooleanType(typeName)) {
				result.append("        attributes[\"").append(attribute.getName()).append("\"] = anObject.").append(attribute.getName()).append("\n");
			}
			if(this.isDoubleType(typeName)) {
				result.append("        attributes[\"").append(attribute.getName()).append("\"] = anObject.").append(attribute.getName()).append("\n");
			}
			if(this.isIntegerType(typeName)) {
				result.append("        attributes[\"").append(attribute.getName()).append("\"] = anObject.").append(attribute.getName()).append("\n");
			}
			// attributes["catalogId"] = NSNumber(longLong:anObject.catalogId)
			if(this.isLongType(typeName)) {
				result.append("        attributes[\"").append(attribute.getName()).append("\"] = NSNumber(longLong:anObject.").append(attribute.getName()).append(")\n");
			}
			// Complex type attributes
			// if (anObject.mainTrack != nil) {
			//     attributes["mainTrack"] = self.prepareAttributesTrack(anObject.mainTrack!)
			// }
			if(this.isComplexType(typeName)) {
				result.append("        if (anObject.").append(attribute.getName()).append(" != nil) {\n");
				result.append("            attributes[\"").append(attribute.getName()).append("\"] = self.prepareAttributes").append(attribute.getType()).append("(anObject.").append(attribute.getName()).append("!)\n");
				result.append("        }\n");
			}
		}
		// List type attributes
		// if (anObject.tracks.count > 0) {
		//    var itemsArray = [NSObject]()
		//    for item in anObject.tracks {
		//      itemsArray.append(self.prepareAttributesTrack(item))
		//    }
		//    attributes["tracks"] = itemsArray
		// }
		for (ListAttrType attribute : type.getListAttr()) {
			result.append("        if (anObject.").append(attribute.getName()).append(".count > 0) {\n");
			result.append("            var itemsArray = [NSObject]()\n");
			result.append("            for item in anObject.").append(attribute.getName()).append(" {\n");
			result.append("                itemsArray.append(self.prepareAttributes").append(attribute.getItemsType()).append("(item))\n");
			result.append("            }\n");
			result.append("            attributes[\"").append(attribute.getName()).append("\"] = itemsArray\n");
			result.append("        }\n");
		}
		
		
		result.append("        return attributes\n");
		result.append("    }\n");
		return result.toString(); 
	}	
	
	/**
	 * Replaces the params in the HTTP path with "\(paramName)". This is used to build the full URL of the 
	 * HTTP invocation
	 */
	public String replacePathParams(String httpPath, List<PathParamType> params) {
		String result = httpPath;
		for (PathParamType param : params) {
			result = result.replace("{" + param.getName() + "}", "\\(" + param.getName() + ")");
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
	
	public boolean isXmlSerialization(String serializationType) {
		return this.getUtility().isXmlSerialization(serializationType);
	}
	
	public boolean isJsonSerialization(String serializationType) {
		return this.getUtility().isJsonSerialization(serializationType);
	}
	
	public boolean isComplexType(String modelType) {
		return this.getUtility().isComplexType(modelType);
	}

}
