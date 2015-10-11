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
import muki.tool.model.ModelDefinitionsType;
import muki.tool.model.PathParamType;
import muki.tool.model.PostOperationType;
import muki.tool.model.Project;
import muki.tool.model.PutOperationType;
import muki.tool.model.QueryParamType;
import muki.tool.model.ModelType;
import muki.tool.model.SimpleAttrType;
import muki.tool.model.ControllerDefinitionsType;
import muki.tool.model.ControllerType;

/**
 * This class centralizes some functionality that is shared among the test cases
 */
public class TestHelper {

	public static String TEMP_DIR = "/Users/gabriel/temp/muki-tests";
	//public static String TEMP_DIR = "c:/temp/muki-tests";

	/**
	 * Returns a project object that defines a Cd with a list of tracks
	 * and a service with several operations
	 */
	public static Project getFullValidProject() {
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
		
		FormParamType dateFormParam = new FormParamType();
		dateFormParam.setName("date");
		FormParamType nameFormParam = new FormParamType();
		nameFormParam.setName("name");
		
		ContextParamType securityContext = new ContextParamType();
		securityContext.setName("securityInfo");
		securityContext.setJavaClass("SecurityContext");
		
		ContextParamType requestContext = new ContextParamType();
		requestContext.setName("request");
		requestContext.setJavaClass("Request");
		
		GetOperationType getOperationNull1 = new GetOperationType();
		getOperationNull1.setName("getOperationNull1");
		getOperationNull1.setHttpPath("/pathGetOperationNull1");
		getOperationNull1.setReturnType("STRING");
		
		GetOperationType getOperationNull2 = new GetOperationType();
		getOperationNull2.setName("getOperationNull2");
		getOperationNull2.setHttpPath("/pathGetOperationNull2");
		getOperationNull2.setReturnType("Track");
		getOperationNull2.setSerializationType("xml");
		
		GetOperationType getOperationNull3 = new GetOperationType();
		getOperationNull3.setName("getOperationNull3");
		getOperationNull3.setHttpPath("/pathGetOperationNull3");
		getOperationNull3.setReturnType("Track");
		getOperationNull3.setSerializationType("json");
		
		GetOperationType getOperation1 = new GetOperationType();
		getOperation1.setName("getOperation1");
		getOperation1.setHttpPath("/pathGetOperation1");
		getOperation1.setReturnType("STRING");
		
		GetOperationType getOperation2 = new GetOperationType();
		getOperation2.setName("getOperation2");
		getOperation2.setHttpPath("/pathGetOperation2/{id}");
		getOperation2.setReturnType("STRING");
		getOperation2.getPathParam().add(idParam);

		GetOperationType getOperation3 = new GetOperationType();
		getOperation3.setName("getOperation3");
		getOperation3.setHttpPath("/pathGetOperation3/{id}/{name}");
		getOperation3.setReturnType("STRING");
		getOperation3.getPathParam().add(idParam);
		getOperation3.getPathParam().add(nameParam);

		GetOperationType getOperation4 = new GetOperationType();
		getOperation4.setName("getOperation4");
		getOperation4.setHttpPath("/pathGetOperation4");
		getOperation4.setReturnType("STRING");
		getOperation4.getQueryParam().add(dateParam);

		GetOperationType getOperation5 = new GetOperationType();
		getOperation5.setName("getOperation5");
		getOperation5.setHttpPath("/pathGetOperation5");
		getOperation5.setReturnType("STRING");
		getOperation5.getQueryParam().add(dateParam);
		getOperation5.getQueryParam().add(pageParam);

		GetOperationType getOperation6 = new GetOperationType();
		getOperation6.setName("getOperation6");
		getOperation6.setHttpPath("/pathGetOperation6/{id}");
		getOperation6.setReturnType("STRING");
		getOperation6.getPathParam().add(idParam);
		getOperation6.getQueryParam().add(pageParam);

		GetOperationType getOperation7Xml = new GetOperationType();
		getOperation7Xml.setName("getOperation7Xml");
		getOperation7Xml.setHttpPath("/pathGetOperation7Xml");
		getOperation7Xml.setReturnType("Track");
		getOperation7Xml.setSerializationType("xml");
		
		GetOperationType getOperation7Json = new GetOperationType();
		getOperation7Json.setName("getOperation7Json");
		getOperation7Json.setHttpPath("/pathGetOperation7Json");
		getOperation7Json.setReturnType("Track");
		getOperation7Json.setSerializationType("json");
		
		GetOperationType getOperation8Xml = new GetOperationType();
		getOperation8Xml.setName("getOperation8Xml");
		getOperation8Xml.setHttpPath("/pathGetOperation8Xml/{id}");
		getOperation8Xml.setReturnType("Cd");
		getOperation8Xml.setSerializationType("xml");
		getOperation8Xml.getPathParam().add(idParam);

		GetOperationType getOperation8Json = new GetOperationType();
		getOperation8Json.setName("getOperation8Json");
		getOperation8Json.setHttpPath("/pathGetOperation8Json/{id}");
		getOperation8Json.setReturnType("Cd");
		getOperation8Json.setSerializationType("json");
		getOperation8Json.getPathParam().add(idParam);

		GetOperationType getOperation9Xml = new GetOperationType();
		getOperation9Xml.setName("getOperation9Xml");
		getOperation9Xml.setHttpPath("/pathGetOperation9Xml/{id}/{name}");
		getOperation9Xml.setReturnType("Track");
		getOperation9Xml.setSerializationType("xml");
		getOperation9Xml.getPathParam().add(idParam);
		getOperation9Xml.getPathParam().add(nameParam);

		GetOperationType getOperation9Json = new GetOperationType();
		getOperation9Json.setName("getOperation9Json");
		getOperation9Json.setHttpPath("/pathGetOperation9Json/{id}/{name}");
		getOperation9Json.setReturnType("Track");
		getOperation9Json.setSerializationType("json");
		getOperation9Json.getPathParam().add(idParam);
		getOperation9Json.getPathParam().add(nameParam);

		GetOperationType getOperation10Xml = new GetOperationType();
		getOperation10Xml.setName("getOperation10Xml");
		getOperation10Xml.setHttpPath("/pathGetOperation10Xml");
		getOperation10Xml.setReturnType("Cd");
		getOperation10Xml.setSerializationType("xml");
		getOperation10Xml.getQueryParam().add(dateParam);

		GetOperationType getOperation10Json = new GetOperationType();
		getOperation10Json.setName("getOperation10Json");
		getOperation10Json.setHttpPath("/pathGetOperation10Json");
		getOperation10Json.setReturnType("Cd");
		getOperation10Json.setSerializationType("json");
		getOperation10Json.getQueryParam().add(dateParam);

		GetOperationType getOperation11Xml = new GetOperationType();
		getOperation11Xml.setName("getOperation11Xml");
		getOperation11Xml.setHttpPath("/pathGetOperation11Xml");
		getOperation11Xml.setReturnType("Track");
		getOperation11Xml.setSerializationType("xml");
		getOperation11Xml.getQueryParam().add(dateParam);
		getOperation11Xml.getQueryParam().add(pageParam);

		GetOperationType getOperation11Json = new GetOperationType();
		getOperation11Json.setName("getOperation11Json");
		getOperation11Json.setHttpPath("/pathGetOperation11Json");
		getOperation11Json.setReturnType("Track");
		getOperation11Json.setSerializationType("json");
		getOperation11Json.getQueryParam().add(dateParam);
		getOperation11Json.getQueryParam().add(pageParam);

		GetOperationType getOperation12Xml = new GetOperationType();
		getOperation12Xml.setName("getOperation12Xml");
		getOperation12Xml.setHttpPath("/pathGetOperation12Xml/{id}");
		getOperation12Xml.setReturnType("Cd");
		getOperation12Xml.setSerializationType("xml");
		getOperation12Xml.getPathParam().add(idParam);
		getOperation12Xml.getQueryParam().add(pageParam);

		GetOperationType getOperation12Json = new GetOperationType();
		getOperation12Json.setName("getOperation12Json");
		getOperation12Json.setHttpPath("/pathGetOperation12Json/{id}");
		getOperation12Json.setReturnType("Cd");
		getOperation12Json.setSerializationType("json");
		getOperation12Json.getPathParam().add(idParam);
		getOperation12Json.getQueryParam().add(pageParam);

		GetOperationType getOperation13Xml = new GetOperationType();
		getOperation13Xml.setName("getOperation13Xml");
		getOperation13Xml.setHttpPath("/pathGetOperation13Xml/{id}");
		getOperation13Xml.setReturnType("Cd");
		getOperation13Xml.setSerializationType("xml");
		getOperation13Xml.getPathParam().add(idParam);
		getOperation13Xml.getContextParam().add(securityContext);

		GetOperationType getOperation13Json = new GetOperationType();
		getOperation13Json.setName("getOperation13Json");
		getOperation13Json.setHttpPath("/pathGetOperation13Json/{id}");
		getOperation13Json.setReturnType("Cd");
		getOperation13Json.setSerializationType("json");
		getOperation13Json.getPathParam().add(idParam);
		getOperation13Json.getContextParam().add(securityContext);

		GetOperationType getOperation14Xml = new GetOperationType();
		getOperation14Xml.setName("getOperation14Xml");
		getOperation14Xml.setHttpPath("/pathGetOperation14Xml/{id}");
		getOperation14Xml.setReturnType("Cd");
		getOperation14Xml.setSerializationType("xml");
		getOperation14Xml.getPathParam().add(idParam);
		getOperation14Xml.getContextParam().add(securityContext);
		getOperation14Xml.getContextParam().add(requestContext);

		GetOperationType getOperation14Json = new GetOperationType();
		getOperation14Json.setName("getOperation14Json");
		getOperation14Json.setHttpPath("/pathGetOperation14Json/{id}");
		getOperation14Json.setReturnType("Cd");
		getOperation14Json.setSerializationType("json");
		getOperation14Json.getPathParam().add(idParam);
		getOperation14Json.getContextParam().add(securityContext);
		getOperation14Json.getContextParam().add(requestContext);

		GetOperationType getOperation15Json = new GetOperationType();
		getOperation15Json.setName("getOperation15Json");
		getOperation15Json.setHttpPath("/pathGetOperation15Json/artists/{name}/tracks/{id}");
		getOperation15Json.setReturnType("Track");
		getOperation15Json.setSerializationType("json");
		getOperation15Json.getPathParam().add(nameParam);
		getOperation15Json.getPathParam().add(idParam);

		GetOperationType getOperationInvalidCharsXml = new GetOperationType();
		getOperationInvalidCharsXml.setName("getOperationInvalidCharsXml");
		getOperationInvalidCharsXml.setHttpPath("/pathGetOperationInvalidCharsXml");
		getOperationInvalidCharsXml.setReturnType("Track");
		getOperationInvalidCharsXml.setSerializationType("xml");

		GetOperationType getOperationInvalidCharsJson = new GetOperationType();
		getOperationInvalidCharsJson.setName("getOperationInvalidCharsJson");
		getOperationInvalidCharsJson.setHttpPath("/pathGetOperationInvalidCharsJson");
		getOperationInvalidCharsJson.setReturnType("Track");
		getOperationInvalidCharsJson.setSerializationType("json");

		PostOperationType postOperationNull1 = new PostOperationType();
		postOperationNull1.setName("postOperationNull1");
		postOperationNull1.setHttpPath("/pathPostOperationNull1");
		postOperationNull1.setReturnType("STRING");
		
		PostOperationType postOperationNull2 = new PostOperationType();
		postOperationNull2.setName("postOperationNull2");
		postOperationNull2.setHttpPath("/pathPostOperationNull2");
		postOperationNull2.setReturnType("Track");
		postOperationNull2.setSerializationType("xml");
		
		PostOperationType postOperationNull3 = new PostOperationType();
		postOperationNull3.setName("postOperationNull3");
		postOperationNull3.setHttpPath("/pathPostOperationNull3");
		postOperationNull3.setReturnType("Track");
		postOperationNull3.setSerializationType("json");
		
		PostOperationType postOperation1 = new PostOperationType();
		postOperation1.setName("postOperation1");
		postOperation1.setHttpPath("/pathPostOperation1");
		
		PostOperationType postOperation2 = new PostOperationType();
		postOperation2.setName("postOperation2");
		postOperation2.setHttpPath("/pathPostOperation2");
		postOperation2.setParamType("STRING");
		
		PostOperationType postOperation3 = new PostOperationType();
		postOperation3.setName("postOperation3");
		postOperation3.setHttpPath("/pathPostOperation3");
		postOperation3.setReturnType("STRING");
		
		PostOperationType postOperation4 = new PostOperationType();
		postOperation4.setName("postOperation4");
		postOperation4.setHttpPath("/pathPostOperation4");
		postOperation4.setParamType("STRING");
		postOperation4.setReturnType("STRING");
		
		PostOperationType postOperation5Xml = new PostOperationType();
		postOperation5Xml.setName("postOperation5Xml");
		postOperation5Xml.setHttpPath("/pathPostOperation5Xml");
		postOperation5Xml.setParamType("Cd");
		postOperation5Xml.setSerializationType("xml");
		
		PostOperationType postOperation5Json = new PostOperationType();
		postOperation5Json.setName("postOperation5Json");
		postOperation5Json.setHttpPath("/pathPostOperation5Json");
		postOperation5Json.setParamType("Cd");
		postOperation5Json.setSerializationType("json");
		
		PostOperationType postOperation6Xml = new PostOperationType();
		postOperation6Xml.setName("postOperation6Xml");
		postOperation6Xml.setHttpPath("/pathPostOperation6Xml");
		postOperation6Xml.setReturnType("Cd");
		postOperation6Xml.setSerializationType("xml");
		
		PostOperationType postOperation6Json = new PostOperationType();
		postOperation6Json.setName("postOperation6Json");
		postOperation6Json.setHttpPath("/pathPostOperation6Json");
		postOperation6Json.setReturnType("Cd");
		postOperation6Json.setSerializationType("json");
		
		PostOperationType postOperation7Xml = new PostOperationType();
		postOperation7Xml.setName("postOperation7Xml");
		postOperation7Xml.setHttpPath("/pathPostOperation7Xml");
		postOperation7Xml.setParamType("Cd");
		postOperation7Xml.setReturnType("Track");
		postOperation7Xml.setSerializationType("xml");
		
		PostOperationType postOperation7Json = new PostOperationType();
		postOperation7Json.setName("postOperation7Json");
		postOperation7Json.setHttpPath("/pathPostOperation7Json");
		postOperation7Json.setParamType("Cd");
		postOperation7Json.setReturnType("Track");
		postOperation7Json.setSerializationType("json");
		
		PostOperationType postOperation8Xml = new PostOperationType();
		postOperation8Xml.setName("postOperation8Xml");
		postOperation8Xml.setHttpPath("/pathPostOperation8Xml");
		postOperation8Xml.getContextParam().add(securityContext);
		postOperation8Xml.setReturnType("Track");
		postOperation8Xml.setSerializationType("xml");
		
		PostOperationType postOperation8Json = new PostOperationType();
		postOperation8Json.setName("postOperation8Json");
		postOperation8Json.setHttpPath("/pathPostOperation8Json");
		postOperation8Json.getContextParam().add(securityContext);
		postOperation8Json.setReturnType("Track");
		postOperation8Json.setSerializationType("json");
		
		PostOperationType postOperation9Xml = new PostOperationType();
		postOperation9Xml.setName("postOperation9Xml");
		postOperation9Xml.setHttpPath("/pathPostOperation9Xml");
		postOperation9Xml.getFormParam().add(nameFormParam);
		postOperation9Xml.getFormParam().add(dateFormParam);
		postOperation9Xml.setReturnType("Track");
		postOperation9Xml.setSerializationType("xml");
		
		PostOperationType postOperation9Json = new PostOperationType();
		postOperation9Json.setName("postOperation9Json");
		postOperation9Json.setHttpPath("/pathPostOperation9Json");
		postOperation9Json.getFormParam().add(nameFormParam);
		postOperation9Json.getFormParam().add(dateFormParam);
		postOperation9Json.setReturnType("Track");
		postOperation9Json.setSerializationType("json");
		
		PostOperationType postOperation10Xml = new PostOperationType();
		postOperation10Xml.setName("postOperation10Xml");
		postOperation10Xml.setHttpPath("/pathPostOperation10Xml");
		postOperation10Xml.setParamType("Cd");
		postOperation10Xml.getContextParam().add(securityContext);
		postOperation10Xml.getContextParam().add(requestContext);
		postOperation10Xml.setReturnType("Track");
		postOperation10Xml.setSerializationType("xml");
		
		PostOperationType postOperation10Json = new PostOperationType();
		postOperation10Json.setName("postOperation10Json");
		postOperation10Json.setHttpPath("/pathPostOperation10Json");
		postOperation10Json.setParamType("Cd");
		postOperation10Json.getContextParam().add(securityContext);
		postOperation10Json.getContextParam().add(requestContext);
		postOperation10Json.setReturnType("Track");
		postOperation10Json.setSerializationType("json");
		
		PostOperationType postOperation11Xml = new PostOperationType();
		postOperation11Xml.setName("postOperation11Xml");
		postOperation11Xml.setHttpPath("/pathPostOperation11Xml/{id}");
		postOperation11Xml.setParamType("Cd");
		postOperation11Xml.setReturnType("Track");
		postOperation11Xml.setSerializationType("xml");
		postOperation11Xml.getPathParam().add(idParam);
		postOperation11Xml.getQueryParam().add(pageParam);
		
		PostOperationType postOperation11Json = new PostOperationType();
		postOperation11Json.setName("postOperation11Json");
		postOperation11Json.setHttpPath("/pathPostOperation11Json/{id}");
		postOperation11Json.setParamType("Cd");
		postOperation11Json.setReturnType("Track");
		postOperation11Json.setSerializationType("json");
		postOperation11Json.getPathParam().add(idParam);
		postOperation11Json.getQueryParam().add(pageParam);
		
		PostOperationType postOperation12Xml = new PostOperationType();
		postOperation12Xml.setName("postOperation12Xml");
		postOperation12Xml.setHttpPath("/pathPostOperation12Xml");
		postOperation12Xml.setParamType("Cd");
		postOperation12Xml.setReturnType("Cd");
		postOperation12Xml.setSerializationType("xml");
		
		PostOperationType postOperation12Json = new PostOperationType();
		postOperation12Json.setName("postOperation12Json");
		postOperation12Json.setHttpPath("/pathPostOperation12Json");
		postOperation12Json.setParamType("Cd");
		postOperation12Json.setReturnType("Cd");
		postOperation12Json.setSerializationType("json");
		
		PostOperationType postOperation13Json = new PostOperationType();
		postOperation13Json.setName("postOperation13Json");
		postOperation13Json.setHttpPath("/pathPostOperation13Json/artists/{name}/tracks/{id}");
		postOperation13Json.setReturnType("Track");
		postOperation13Json.setParamType("Cd");
		postOperation13Json.setSerializationType("json");
		postOperation13Json.getPathParam().add(nameParam);
		postOperation13Json.getPathParam().add(idParam);

		PostOperationType postOperationInvalidCharsXml = new PostOperationType();
		postOperationInvalidCharsXml.setName("postOperationInvalidCharsXml");
		postOperationInvalidCharsXml.setHttpPath("/pathPostOperationInvalidCharsXml");
		postOperationInvalidCharsXml.setParamType("Track");
		postOperationInvalidCharsXml.setReturnType("Track");
		postOperationInvalidCharsXml.setSerializationType("xml");
		
		PostOperationType postOperationInvalidCharsJson = new PostOperationType();
		postOperationInvalidCharsJson.setName("postOperationInvalidCharsJson");
		postOperationInvalidCharsJson.setHttpPath("/pathPostOperationInvalidCharsJson");
		postOperationInvalidCharsJson.setParamType("Track");
		postOperationInvalidCharsJson.setReturnType("Track");
		postOperationInvalidCharsJson.setSerializationType("json");
		
		PutOperationType putOperationNull1 = new PutOperationType();
		putOperationNull1.setName("putOperationNull1");
		putOperationNull1.setHttpPath("/pathPutOperationNull1");
		putOperationNull1.setReturnType("STRING");
		
		PutOperationType putOperationNull2 = new PutOperationType();
		putOperationNull2.setName("putOperationNull2");
		putOperationNull2.setHttpPath("/pathPutOperationNull2");
		putOperationNull2.setReturnType("Track");
		putOperationNull2.setSerializationType("json");
		
		PutOperationType putOperationNull3 = new PutOperationType();
		putOperationNull3.setName("putOperationNull3");
		putOperationNull3.setHttpPath("/pathPutOperationNull3");
		putOperationNull3.setReturnType("Track");
		putOperationNull3.setSerializationType("xml");
		
		PutOperationType putOperation1 = new PutOperationType();
		putOperation1.setName("putOperation1");
		putOperation1.setHttpPath("/pathPutOperation1");

		PutOperationType putOperation2 = new PutOperationType();
		putOperation2.setName("putOperation2");
		putOperation2.setHttpPath("/pathPutOperation2");
		putOperation2.setParamType("STRING");

		PutOperationType putOperation3 = new PutOperationType();
		putOperation3.setName("putOperation3");
		putOperation3.setHttpPath("/pathPutOperation3");
		putOperation3.setReturnType("STRING");

		PutOperationType putOperation4 = new PutOperationType();
		putOperation4.setName("putOperation4");
		putOperation4.setHttpPath("/pathPutOperation4");
		putOperation4.setParamType("STRING");
		putOperation4.setReturnType("STRING");

		PutOperationType putOperation5Xml = new PutOperationType();
		putOperation5Xml.setName("putOperation5Xml");
		putOperation5Xml.setHttpPath("/pathPutOperation5Xml");
		putOperation5Xml.setParamType("Cd");
		putOperation5Xml.setSerializationType("xml");

		PutOperationType putOperation5Json = new PutOperationType();
		putOperation5Json.setName("putOperation5Json");
		putOperation5Json.setHttpPath("/pathPutOperation5Json");
		putOperation5Json.setParamType("Cd");
		putOperation5Json.setSerializationType("json");

		PutOperationType putOperation6Xml = new PutOperationType();
		putOperation6Xml.setName("putOperation6Xml");
		putOperation6Xml.setHttpPath("/pathPutOperation6Xml");
		putOperation6Xml.setReturnType("Cd");
		putOperation6Xml.setSerializationType("xml");

		PutOperationType putOperation6Json = new PutOperationType();
		putOperation6Json.setName("putOperation6Json");
		putOperation6Json.setHttpPath("/pathPutOperation6Json");
		putOperation6Json.setReturnType("Cd");
		putOperation6Json.setSerializationType("json");

		PutOperationType putOperation7Xml = new PutOperationType();
		putOperation7Xml.setName("putOperation7Xml");
		putOperation7Xml.setHttpPath("/pathPutOperation7Xml");
		putOperation7Xml.setParamType("Cd");
		putOperation7Xml.setReturnType("Track");
		putOperation7Xml.setSerializationType("xml");

		PutOperationType putOperation7Json = new PutOperationType();
		putOperation7Json.setName("putOperation7Json");
		putOperation7Json.setHttpPath("/pathPutOperation7Json");
		putOperation7Json.setParamType("Cd");
		putOperation7Json.setReturnType("Track");
		putOperation7Json.setSerializationType("json");

		PutOperationType putOperation8Xml = new PutOperationType();
		putOperation8Xml.setName("putOperation8Xml");
		putOperation8Xml.setHttpPath("/pathPutOperation8Xml");
		putOperation8Xml.getContextParam().add(securityContext);
		putOperation8Xml.setParamType("Cd");
		putOperation8Xml.setReturnType("Track");
		putOperation8Xml.setSerializationType("xml");

		PutOperationType putOperation8Json = new PutOperationType();
		putOperation8Json.setName("putOperation8Json");
		putOperation8Json.setHttpPath("/pathPutOperation8Json");
		putOperation8Json.getContextParam().add(securityContext);
		putOperation8Json.setParamType("Cd");
		putOperation8Json.setReturnType("Track");
		putOperation8Json.setSerializationType("json");

		PutOperationType putOperation9Xml = new PutOperationType();
		putOperation9Xml.setName("putOperation9Xml");
		putOperation9Xml.setHttpPath("/pathPutOperation9Xml");
		putOperation9Xml.getFormParam().add(nameFormParam);
		putOperation9Xml.getFormParam().add(dateFormParam);
		putOperation9Xml.setReturnType("Track");
		putOperation9Xml.setSerializationType("xml");

		PutOperationType putOperation9Json = new PutOperationType();
		putOperation9Json.setName("putOperation9Json");
		putOperation9Json.setHttpPath("/pathPutOperation9Json");
		putOperation9Json.getFormParam().add(nameFormParam);
		putOperation9Json.getFormParam().add(dateFormParam);
		putOperation9Json.setReturnType("Track");
		putOperation9Json.setSerializationType("xml");

		PutOperationType putOperation10Xml = new PutOperationType();
		putOperation10Xml.setName("putOperation10Xml");
		putOperation10Xml.setHttpPath("/pathPutOperation10Xml");
		putOperation10Xml.setParamType("Cd");
		putOperation10Xml.getContextParam().add(securityContext);
		putOperation10Xml.getContextParam().add(requestContext);
		putOperation10Xml.setReturnType("Track");
		putOperation10Xml.setSerializationType("xml");
		
		PutOperationType putOperation10Json = new PutOperationType();
		putOperation10Json.setName("putOperation10Json");
		putOperation10Json.setHttpPath("/pathPutOperation10Json");
		putOperation10Json.setParamType("Cd");
		putOperation10Json.getContextParam().add(securityContext);
		putOperation10Json.getContextParam().add(requestContext);
		putOperation10Json.setReturnType("Track");
		putOperation10Json.setSerializationType("json");
		
		PutOperationType putOperation11Xml = new PutOperationType();
		putOperation11Xml.setName("putOperation11Xml");
		putOperation11Xml.setHttpPath("/pathPutOperation11Xml/{id}");
		putOperation11Xml.setParamType("Cd");
		putOperation11Xml.setReturnType("Track");
		putOperation11Xml.setSerializationType("xml");
		putOperation11Xml.getPathParam().add(idParam);
		putOperation11Xml.getQueryParam().add(pageParam);
		
		PutOperationType putOperation11Json = new PutOperationType();
		putOperation11Json.setName("putOperation11Json");
		putOperation11Json.setHttpPath("/pathPutOperation11Json/{id}");
		putOperation11Json.setParamType("Cd");
		putOperation11Json.setReturnType("Track");
		putOperation11Json.setSerializationType("json");
		putOperation11Json.getPathParam().add(idParam);
		putOperation11Json.getQueryParam().add(pageParam);
		
		PutOperationType putOperation12Json = new PutOperationType();
		putOperation12Json.setName("putOperation12Json");
		putOperation12Json.setHttpPath("/pathPutOperation12Json/artists/{name}/tracks/{id}");
		putOperation12Json.setReturnType("Track");
		putOperation12Json.setParamType("Cd");
		putOperation12Json.setSerializationType("json");
		putOperation12Json.getPathParam().add(nameParam);
		putOperation12Json.getPathParam().add(idParam);

		DeleteOperationType deleteOperation1 = new DeleteOperationType();
		deleteOperation1.setName("deleteOperation1");
		deleteOperation1.setHttpPath("/pathDeleteOperation1");
		
		DeleteOperationType deleteOperation2 = new DeleteOperationType();
		deleteOperation2.setName("deleteOperation2");
		deleteOperation2.setHttpPath("/pathDeleteOperation2");
		deleteOperation2.getContextParam().add(securityContext);
		
		DeleteOperationType deleteOperation3 = new DeleteOperationType();
		deleteOperation3.setName("deleteOperation3");
		deleteOperation3.setHttpPath("/pathDeleteOperation3");
		deleteOperation3.getContextParam().add(securityContext);
		deleteOperation3.getContextParam().add(requestContext);
		
		DeleteOperationType deleteOperation4 = new DeleteOperationType();
		deleteOperation4.setName("deleteOperation4");
		deleteOperation4.setHttpPath("/pathDeleteOperation4/{id}/{name}");
		deleteOperation4.getPathParam().add(idParam);
		deleteOperation4.getPathParam().add(nameParam);

		DeleteOperationType deleteOperation5 = new DeleteOperationType();
		deleteOperation5.setName("deleteOperation5");
		deleteOperation5.setHttpPath("/pathDeleteOperation5/{id}");
		deleteOperation5.getPathParam().add(idParam);
		deleteOperation5.getQueryParam().add(dateParam);

		DeleteOperationType deleteOperation6 = new DeleteOperationType();
		deleteOperation6.setName("deleteOperation6");
		deleteOperation6.setHttpPath("/pathDeleteOperation6/artists/{name}/tracks/{id}");
		deleteOperation6.getPathParam().add(nameParam);
		deleteOperation6.getPathParam().add(idParam);

		ControllerType r1 = new ControllerType();
		r1.setName("Controller1");
		r1.setHttpPath("/controller1");
		r1.getGetOperation().add(getOperation1);
		r1.getGetOperation().add(getOperation4);
		r1.getGetOperation().add(getOperation5);
		r1.getGetOperation().add(getOperation6);
		r1.getGetOperation().add(getOperation7Xml);
		r1.getGetOperation().add(getOperation7Json);
		r1.getGetOperation().add(getOperation8Xml);
		r1.getGetOperation().add(getOperation8Json);
		r1.getGetOperation().add(getOperation9Xml);
		r1.getGetOperation().add(getOperation9Json);
		r1.getGetOperation().add(getOperation10Xml);
		r1.getGetOperation().add(getOperation10Json);
		r1.getGetOperation().add(getOperation11Xml);
		r1.getGetOperation().add(getOperation11Json);
		r1.getGetOperation().add(getOperation12Xml);
		r1.getGetOperation().add(getOperation12Json);
		r1.getGetOperation().add(getOperation13Xml);
		r1.getGetOperation().add(getOperation13Json);
		r1.getGetOperation().add(getOperation14Xml);
		r1.getGetOperation().add(getOperation14Json);
		r1.getGetOperation().add(getOperation15Json);
		r1.getPostOperation().add(postOperation1);
		r1.getPostOperation().add(postOperation2);
		r1.getPostOperation().add(postOperation3);
		r1.getPostOperation().add(postOperation4);
		r1.getPostOperation().add(postOperation5Xml);
		r1.getPostOperation().add(postOperation5Json);
		r1.getPostOperation().add(postOperation6Xml);
		r1.getPostOperation().add(postOperation6Json);
		r1.getPostOperation().add(postOperation7Xml);
		r1.getPostOperation().add(postOperation7Json);
		r1.getPostOperation().add(postOperation8Xml);
		r1.getPostOperation().add(postOperation8Json);
		r1.getPostOperation().add(postOperation9Xml);
		r1.getPostOperation().add(postOperation9Json);
		r1.getPostOperation().add(postOperation10Xml);
		r1.getPostOperation().add(postOperation10Json);
		r1.getPostOperation().add(postOperation11Xml);
		r1.getPostOperation().add(postOperation11Json);
		r1.getPostOperation().add(postOperation12Xml);
		r1.getPostOperation().add(postOperation12Json);
		r1.getPostOperation().add(postOperation13Json);
		r1.getPutOperation().add(putOperation1);
		r1.getPutOperation().add(putOperation2);
		r1.getPutOperation().add(putOperation3);
		r1.getPutOperation().add(putOperation4);
		r1.getPutOperation().add(putOperation5Xml);
		r1.getPutOperation().add(putOperation5Json);
		r1.getPutOperation().add(putOperation6Xml);
		r1.getPutOperation().add(putOperation6Json);
		r1.getPutOperation().add(putOperation7Xml);
		r1.getPutOperation().add(putOperation7Json);
		r1.getPutOperation().add(putOperation8Xml);
		r1.getPutOperation().add(putOperation8Json);
		r1.getPutOperation().add(putOperation9Xml);
		r1.getPutOperation().add(putOperation9Json);
		r1.getPutOperation().add(putOperation10Xml);
		r1.getPutOperation().add(putOperation10Json);
		r1.getPutOperation().add(putOperation11Xml);
		r1.getPutOperation().add(putOperation11Json);
		r1.getPutOperation().add(putOperation12Json);
		r1.getDeleteOperation().add(deleteOperation1);
		r1.getDeleteOperation().add(deleteOperation2);
		r1.getDeleteOperation().add(deleteOperation3);
		r1.getDeleteOperation().add(deleteOperation4);
		r1.getDeleteOperation().add(deleteOperation5);
		r1.getDeleteOperation().add(deleteOperation6);
		
		ControllerType r2 = new ControllerType();
		r2.setName("Controller2");
		r2.setHttpPath("/controller2");
		r2.getGetOperation().add(getOperationNull1);
		r2.getGetOperation().add(getOperationNull2);
		r2.getGetOperation().add(getOperationNull3);
		r2.getGetOperation().add(getOperation2);
		r2.getGetOperation().add(getOperation3);
		r2.getGetOperation().add(getOperationInvalidCharsXml);
		r2.getGetOperation().add(getOperationInvalidCharsJson);
		r2.getPostOperation().add(postOperationNull1);
		r2.getPostOperation().add(postOperationNull2);
		r2.getPostOperation().add(postOperationNull3);
		r2.getPostOperation().add(postOperationInvalidCharsXml);
		r2.getPostOperation().add(postOperationInvalidCharsJson);
		r2.getPutOperation().add(putOperationNull1);
		r2.getPutOperation().add(putOperationNull2);
		r2.getPutOperation().add(putOperationNull3);
		
		ControllerDefinitionsType controllers = new ControllerDefinitionsType();
		controllers.setJavaPackage("store.controller");
		controllers.getController().add(r1);
		controllers.getController().add(r2);

		Project project = new Project();
		project.setName("MyProject");
		project.setModelDefinitions(models);
		project.setControllerDefinitions(controllers);
		return project;		
	}

}
