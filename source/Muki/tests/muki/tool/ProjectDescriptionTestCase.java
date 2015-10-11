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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import muki.tool.model.GetOperationType;
import muki.tool.model.PostOperationType;
import muki.tool.model.Project;
import muki.tool.model.ControllerType;
import muki.tool.model.ModelType;;


public class ProjectDescriptionTestCase {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * This test verifies the serialization of a model into XML
	 */
	@Test
	public void testSerializeProject() throws Exception {
		Project project = TestHelper.getFullValidProject();
		
		JAXBContext context = JAXBContext.newInstance(Project.class);
		Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.FALSE);
		StringWriter writer = new StringWriter();
		marshaller.marshal(project, writer);
		String xml = writer.toString();
		//System.out.println(xml);
		assertNotNull(xml);
		assertTrue(xml.indexOf("<model-definitions java-package=\"store.model\">") > 10);
		assertTrue(xml.indexOf("<model name=\"Cd\">") > 10);
		assertTrue(xml.indexOf("<simple-attr ") > 10);
		assertTrue(xml.indexOf("<controller-definitions java-package=\"store.controller\">") > 10);
		assertTrue(xml.indexOf("http-path=\"/controller1\"") > 10);
	}
	
	/**
	 * This test verfifies the instanciation of a model from an XML
	 */
	@Test
	public void testDeserializeProject() throws Exception {
		String xml = 
		"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" + 
		"<ns2:project name=\"MyProject\" xmlns:ns2=\"http://muki/service-description/\">" +
			"<model-definitions java-package=\"store.types\">" +
				"<model name=\"Track\">" +
					"<simple-attr type=\"STRING\" name=\"title\" />" +
					"<simple-attr type=\"INT\" name=\"durationInSeconds\" />" +
					"<simple-attr type=\"LONG\" name=\"catalogId\" />" +
					"<simple-attr type=\"BOOLEAN\" name=\"newRelease\" />" +
					"<simple-attr type=\"DOUBLE\" name=\"price\" />" +
				"</model>" +
				"<model name=\"Cd\">" +
					"<simple-attr type=\"string\" name=\"title\" />" +
					"<simple-attr type=\"STRING\" name=\"artist\" />" +
					"<simple-attr type=\"Track\" name=\"mainTrack\" />" +
					"<list-attr items-type=\"Track\" name=\"tracks\" />" +
				"</model>" +
			"</model-definitions>" +
			"<controller-definitions java-package=\"store.controllers\">" +
				"<controller http-path=\"/controller1\" name=\"Controller1\">" +
					"<get-operation serialization-type=\"xml\" http-path=\"/pathGetOperation8Xml/{id}\" return-type=\"Cd\" name=\"getOperation8Xml\">" +
						"<path-param name=\"id\" />" +
					"</get-operation>" +
					"<post-operation http-path=\"/pathPostOperation1\" name=\"postOperation1\" />" +
					"<put-operation param-type=\"string\" http-path=\"/pathPutOperation2\" name=\"putOperation2\" />" +
					"<delete-operation http-path=\"/pathDeleteOperation3\" name=\"deleteOperation3\">" +
						"<context-param javaClass=\"SecurityContext\" name=\"securityInfo\" />" +
						"<context-param javaClass=\"Request\" name=\"request\" />" +
					"</delete-operation>" +
				"</controller>" +
				"<controller http-path=\"/controller2\" name=\"Controller2\">" +
					"<get-operation http-path=\"/pathGetOperation2/{id}\" return-type=\"string\" name=\"getOperation2\">" +
					"<path-param name=\"id\" />" +
					"</get-operation>" +
				"</controller>" +
			"</controller-definitions>" +
		"</ns2:project>";
		
		JAXBContext context = JAXBContext.newInstance(Project.class);
		Unmarshaller unmarshaller = context.createUnmarshaller();
		Project project = (Project)unmarshaller.unmarshal(new StringReader(xml));

		assertNotNull(project);
		
		Project expectedProject = new Project();
		expectedProject.setName("MyProject");
		assertEquals(expectedProject.getName(), project.getName());
		
		ModelType cd = this.getModelTypeByName(project, "Cd");
		assertNotNull(cd);
		assertEquals(3, cd.getSimpleAttr().size());
		assertEquals(1, cd.getListAttr().size());

		ControllerType controller = this.getControllerTypeByName(project, "Controller1");
		GetOperationType getCd = controller.getGetOperation().get(0);
		assertEquals("getOperation8Xml", getCd.getName());
		assertEquals("/pathGetOperation8Xml/{id}", getCd.getHttpPath());
		assertEquals("Cd", getCd.getReturnType());
		assertEquals(1, getCd.getPathParam().size());
		assertEquals(0, getCd.getQueryParam().size());
		
		PostOperationType addCd = controller.getPostOperation().get(0);
		assertEquals("postOperation1", addCd.getName());
	}
	
	private ModelType getModelTypeByName(Project aProject, String name) {
		for (ModelType type : aProject.getModelDefinitions().getModel()) {
			if(type.getName().equals(name)) {
				return type;
			}
		}
		return null;
	}
	
	private ControllerType getControllerTypeByName(Project aProject, String name) {
		for (ControllerType type : aProject.getControllerDefinitions().getController()) {
			if(type.getName().equals(name)) {
				return type;
			}
		}
		return null;
	}

}
