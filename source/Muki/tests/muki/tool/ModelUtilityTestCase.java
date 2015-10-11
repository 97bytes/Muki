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

import static org.junit.Assert.*;

import java.net.URL;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import muki.tool.model.FormParamType;
import muki.tool.model.GetOperationType;
import muki.tool.model.ListAttrType;
import muki.tool.model.ModelDefinitionsType;
import muki.tool.model.ModelType;
import muki.tool.model.PathParamType;
import muki.tool.model.PostOperationType;
import muki.tool.model.Project;
import muki.tool.model.PutOperationType;
import muki.tool.model.QueryParamType;
import muki.tool.model.SimpleAttrType;
import muki.tool.model.ControllerDefinitionsType;
import muki.tool.model.ControllerType;
import muki.tool.ExecutionResult;
import muki.tool.IOUtility;
import muki.tool.ModelUtility;

public class ModelUtilityTestCase {
	
	private static String TEMP_DIR = TestHelper.TEMP_DIR;
	private ModelUtility utility;
	private IOUtility io;

	@Before
	public void setUp() throws Exception {
		this.setUtility(new ModelUtility());
		this.setIo(new IOUtility());
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testValidateSmallProjectOk() {
		Project project = this.getSmallValidProject();
		ExecutionResult result = new ExecutionResult();
		this.getUtility().validate(project, result);
		assertTrue(result.isOk());
	}

	@Test
	public void testValidateBigProjectOk() throws Exception {
		//Project project = TestHelper.getFullValidProject();
		URL url = this.getClass().getResource("/tests/store-project-ok.xml");
		String fileName = url.getFile();
		Project project = this.getUtility().openProject(fileName);
		ExecutionResult result = new ExecutionResult();
		this.getUtility().validate(project, result);
		System.out.println(result.getLog());
		assertTrue(result.isOk());
	}

	/**
	 * In this case, we verify that the validation of an invalid model actually
	 * returns a list of errors.
	 */
	@Test
	public void testValidateProjectNotOk() {
		SimpleAttrType title = new SimpleAttrType();
		title.setName("");
		title.setType("LONG2");
		SimpleAttrType artist = new SimpleAttrType();
		artist.setName(null);
		artist.setType(null);

		ListAttrType idsSimilarTracks = new ListAttrType();
		idsSimilarTracks.setName("idsSimilarTracks");
		idsSimilarTracks.setItemsType("Track");

		ModelType track = new ModelType();
		track.setName("Track");
		track.getSimpleAttr().add(title);
		track.getListAttr().add(idsSimilarTracks);
		
		ListAttrType listOfTracks = new ListAttrType();
		listOfTracks.setName("tracks");
		listOfTracks.setItemsType("Track");
		
		ModelType cd = new ModelType();
		cd.setName("");
		cd.getSimpleAttr().add(title);
		cd.getSimpleAttr().add(artist);
		cd.getListAttr().add(listOfTracks);
		
		ModelDefinitionsType models = new ModelDefinitionsType();
		models.setJavaPackage(" ");
		models.getModel().add(track);
		models.getModel().add(cd);
		
		PathParamType idParam = new PathParamType();
		idParam.setName("");
		
		QueryParamType dateParam = new QueryParamType();
		dateParam.setName(null);
		
		GetOperationType getOperation1 = new GetOperationType();
		getOperation1.setName("");
		getOperation1.setHttpPath(null);
		getOperation1.setReturnType("STRING");
		
		FormParamType form = new FormParamType();
		form.setName("form1");
		PostOperationType postOperation1 = new PostOperationType();
		postOperation1.setName("");
		postOperation1.setHttpPath("");
		postOperation1.setSerializationType("xml2");
		postOperation1.getFormParam().add(form);
		
		PutOperationType putOperation1 = new PutOperationType();
		PathParamType pathParam1 = new PathParamType();
		pathParam1.setName("id");
		putOperation1.setName("");
		putOperation1.setHttpPath("/{id}");
		putOperation1.setReturnType("long");
		putOperation1.setParamType("STRING");
		putOperation1.setSerializationType("xml2");
		putOperation1.getFormParam().add(form);
		putOperation1.getPathParam().add(pathParam1);
		
		ControllerType controller1 = new ControllerType();
		controller1.setName("MusicStoreService");
		controller1.setHttpPath("/store");
		controller1.getGetOperation().add(getOperation1);
		controller1.getPostOperation().add(postOperation1);
		controller1.getPutOperation().add(putOperation1);
		
		ControllerDefinitionsType controllers = new ControllerDefinitionsType();
		controllers.setJavaPackage(" ");
		controllers.getController().add(controller1);
		
		Project project = new Project();
		project.setName("MyProject");
		project.setModelDefinitions(models);
		project.setControllerDefinitions(controllers);
		
		ExecutionResult result = new ExecutionResult();
		this.getUtility().validate(project, result);
		assertFalse(result.isOk());
		System.out.println(result.getLog());
		assertTrue(result.getLog().length() > 10);
	}

	@Test
	public void testSaveProject() throws Exception {
		this.getIo().deleteDirectory(TEMP_DIR);
		this.getIo().createDirectory(TEMP_DIR);
		String fileName = TEMP_DIR + "/my_project.xml";
		assertTrue(!this.getIo().existsFile(fileName));
		
		Project project = TestHelper.getFullValidProject();
		this.getUtility().saveProject(project, fileName);
		assertTrue(this.getIo().existsFile(fileName));
	}
	
	@Test
	public void testOpenProject() throws Exception {
		URL url = this.getClass().getResource("/tests/store-project-ok.xml");
		String fileName = url.getFile();
		
		Project project = this.getUtility().openProject(fileName);
		assertNotNull(project);
		assertEquals("MyProject", project.getName());
	}
	
	/**
	 * Returns a project object that defines a Cd with a list of tracks
	 * and a controller with several operations
	 */
	private Project getSmallValidProject() {
		SimpleAttrType title = new SimpleAttrType();
		title.setName("title");
		title.setType("STRING");
		SimpleAttrType artist = new SimpleAttrType();
		artist.setName("artist");
		artist.setType("STRING");
		SimpleAttrType durationInSeconds = new SimpleAttrType();
		durationInSeconds.setName("durationInSeconds");
		durationInSeconds.setType("INT");
		SimpleAttrType catalogId = new SimpleAttrType();
		catalogId.setName("catalogId");
		catalogId.setType("LONG");
		SimpleAttrType newRelease = new SimpleAttrType();
		newRelease.setName("newRelease");
		newRelease.setType("BOOLEAN");
		SimpleAttrType price = new SimpleAttrType();
		price.setName("price");
		price.setType("DOUBLE");
		SimpleAttrType mainTrack = new SimpleAttrType();
		mainTrack.setName("mainTrack");
		mainTrack.setType("Track");

		ModelType track = new ModelType();
		track.setName("Track");
		track.getSimpleAttr().add(title);
		track.getSimpleAttr().add(durationInSeconds);
		track.getSimpleAttr().add(catalogId);
		track.getSimpleAttr().add(newRelease);
		track.getSimpleAttr().add(price);
		
		ListAttrType listOfTracks = new ListAttrType();
		listOfTracks.setName("tracks");
		listOfTracks.setItemsType("Track");
		
		ModelType cd = new ModelType();
		cd.setName("Cd");
		cd.getSimpleAttr().add(title);
		cd.getSimpleAttr().add(artist);
		cd.getSimpleAttr().add(mainTrack);
		cd.getListAttr().add(listOfTracks);
		
		ModelDefinitionsType models = new ModelDefinitionsType();
		models.setJavaPackage("store.model");
		models.getModel().add(track);
		models.getModel().add(cd);
		
		PathParamType idParam = new PathParamType();
		idParam.setName("id");
		PathParamType nameParam = new PathParamType();
		nameParam.setName("name");
		
		QueryParamType dateParam = new QueryParamType();
		dateParam.setName("date");
		QueryParamType pageParam = new QueryParamType();
		pageParam.setName("page");
		
		GetOperationType getOperation1 = new GetOperationType();
		getOperation1.setName("getOperation1");
		getOperation1.setHttpPath("/getOperation1");
		getOperation1.setReturnType("STRING");
		
		GetOperationType getOperation2 = new GetOperationType();
		getOperation2.setName("getOperation2");
		getOperation2.setHttpPath("/getOperation2/{id}");
		getOperation2.setReturnType("STRING");
		getOperation2.getPathParam().add(idParam);

		PostOperationType postOperation1 = new PostOperationType();
		postOperation1.setName("postOperation1");
		postOperation1.setHttpPath("/postOperation1");
		
		PostOperationType postOperation2 = new PostOperationType();
		postOperation2.setName("postOperation2");
		postOperation2.setHttpPath("/postOperation2");
		postOperation2.setParamType("STRING");
				
		ControllerType controller1 = new ControllerType();
		controller1.setName("MusicStoreService");
		controller1.setHttpPath("/store");
		controller1.getGetOperation().add(getOperation1);
		controller1.getGetOperation().add(getOperation2);
		controller1.getPostOperation().add(postOperation1);
		controller1.getPostOperation().add(postOperation2);
		
		ControllerDefinitionsType controllers = new ControllerDefinitionsType();
		controllers.setJavaPackage("store.model");
		controllers.getController().add(controller1);

		Project project = new Project();
		project.setName("MyProject");
		project.setModelDefinitions(models);
		project.setControllerDefinitions(controllers);
		return project;		
	}

	private ModelUtility getUtility() {
		return utility;
	}

	private void setUtility(ModelUtility utility) {
		this.utility = utility;
	}

	private IOUtility getIo() {
		return io;
	}

	private void setIo(IOUtility io) {
		this.io = io;
	}

}
