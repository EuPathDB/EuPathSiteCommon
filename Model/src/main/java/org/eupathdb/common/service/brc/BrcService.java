package org.eupathdb.common.service.brc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.gusdb.fgputil.IoUtil;
import org.gusdb.wdk.model.WdkModelException;
import org.gusdb.wdk.service.request.exception.RequestMisformatException;
import org.gusdb.wdk.service.service.WdkService;
import org.json.JSONObject;

@Path("/brc")
public class BrcService extends WdkService {
	
  private static Logger LOG = Logger.getLogger(BrcService.class);
  private NewCookie authCookie;
  private NewCookie sessionCookie;
  private final String EXPERIMENT_ID_PATH_PARAM = "experimentId";
  private final String ID_LIST_ID_PATH_PARAM = "idListId";
  
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response getBrc(String body) {
    JSONObject requestJson = new JSONObject(body);
    try {
      BrcRequest brcRequest = BrcRequest.createFromJson(requestJson);
      String userId = callUserService();
      callDatasetService(userId, brcRequest);
      JSONObject answerJson = callAnswerService(brcRequest);
      LOG.info("Answer Service JSON Result: " + answerJson.toString(2));
      Set<BrcBean> brcBeans = BrcBean.parseAnswerJson(answerJson);
      return Response.ok(BrcFormatter.getJson(brcBeans).toString()).build();
    }
    catch(WdkModelException | RequestMisformatException e) {
      throw new BadRequestException(e);
    }
  }
  
  @GET
  @Path("/experiment/{experimentId}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getExperiment(@PathParam(EXPERIMENT_ID_PATH_PARAM) String experimentId) {
	try {
      ExperimentRequest experimentRequest = new ExperimentRequest();
      experimentRequest.setExperimentId(experimentId);
      LOG.info("The experiment id is " + experimentId);
      JSONObject datasetRecordJson = callDatasetRecordService(experimentRequest);
      BrcBean brcBean = BrcBean.parseDatasetRecordJson(datasetRecordJson, false);
      return Response.ok(BrcFormatter.getJson(brcBean).toString()).build();
	}
	catch(WdkModelException e) {
	  throw new BadRequestException(e);
	}
  }
  
  @GET
  @Path("/experiment/{experimentId}/id-list/{idListId}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getIdList(@PathParam(EXPERIMENT_ID_PATH_PARAM) String experimentId,
		  @PathParam(ID_LIST_ID_PATH_PARAM) String idListId) {
  try {
    ExperimentRequest experimentRequest = new ExperimentRequest();
    experimentRequest.setExperimentId(experimentId);
    
    JSONObject datasetRecordJson = callDatasetRecordService(experimentRequest);
    BrcGeneListBean brcGeneListBean = BrcBean.parseDatasetRecordJson(datasetRecordJson, true).getIdLists();
    return Response.ok(BrcFormatter.getGeneListJson(brcGeneListBean, false).toString()).build();
	}
	catch(WdkModelException e) {
	  throw new BadRequestException(e);
	}
  }
  
  @GET
  @Path("/api")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getApi() {
    return Response.ok(ApiFormatter.getJson().toString()).build();
  }
  
  protected JSONObject callAnswerService(BrcRequest brcRequest) throws WdkModelException {
	
    LOG.info("JSON to AnswerService: " + brcRequest.getWdkAnswerJson().toString(2));
	  
    Client client = ClientBuilder
      .newBuilder()
      .build();
    Response response = client
      .target("http://localhost:8080/plasmodb/service/answer")
      .property("Content-Type", MediaType.APPLICATION_JSON)
      .request(MediaType.APPLICATION_JSON)
      .cookie(authCookie)
      .cookie(sessionCookie)
      .post(Entity.entity(brcRequest.getWdkAnswerJson().toString(), MediaType.APPLICATION_JSON));
    try {
      if (response.getStatus() == 200) {
        LOG.info("200 OK");
        // Success!  Read result into buffer and convert to JSON
        InputStream resultStream = (InputStream)response.getEntity();
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        IoUtil.transferStream(buffer, resultStream);
        return new JSONObject(new String(buffer.toByteArray()));
      }
      else {
        throw new WdkModelException("Bad status");
      }
    }
    catch(IOException ioe) {
      throw new WdkModelException(ioe);
    }
    finally {
      response.close();
      client.close();
    }
  }
  
  protected String callUserService() throws WdkModelException {
    Client client = ClientBuilder
      .newBuilder()
      .build();
	Response response = client
      .target("http://localhost:8080/plasmodb/service/user/current")
      .request(MediaType.APPLICATION_JSON)
      .get();
    try {
      if (response.getStatus() == 200) {
        LOG.info("User id request successful");
        Map<String, NewCookie> cookies = response.getCookies();
        authCookie = cookies.get("wdk_check_auth");
        sessionCookie = cookies.get("JSESSIONID");
        InputStream resultStream = (InputStream)response.getEntity();
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        IoUtil.transferStream(buffer, resultStream);
        JSONObject json = new JSONObject(new String(buffer.toByteArray()));
        LOG.info("User id is " + json.get("id"));
        return String.valueOf(json.getInt("id"));
      }
      else {
        throw new WdkModelException("Bad status");
      }
    }
    catch(IOException ioe) {
      throw new WdkModelException(ioe);
    }
    finally {
      response.close();
      client.close();
    }
  }
  
  protected void callDatasetService(String userId, BrcRequest brcRequest) throws WdkModelException {
    Client client = ClientBuilder
      .newBuilder()
      .build();
    Response response = client
      .target("http://localhost:8080/plasmodb/service/user/" + userId + "/dataset")
	  .property("Content-Type", MediaType.APPLICATION_JSON)
      .request(MediaType.APPLICATION_JSON)
      .cookie(authCookie)
      .cookie(sessionCookie)
      .post(Entity.entity(brcRequest.getDatasetJson().toString(), MediaType.APPLICATION_JSON));
   try {
      if (response.getStatus() == 200) {
        LOG.info("Dataset request successful");
        InputStream resultStream = (InputStream)response.getEntity();
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        IoUtil.transferStream(buffer, resultStream);
        JSONObject json = new JSONObject(new String(buffer.toByteArray()));
        brcRequest.setDatasetId(String.valueOf(json.getInt("id")));
      }
      else {
        throw new WdkModelException("Bad status");
      }
    }
    catch(IOException ioe) {
      throw new WdkModelException(ioe);
    }
    finally {
      response.close();
      client.close();
    }
  }
  
  protected JSONObject callDatasetRecordService(ExperimentRequest experimentRequest) throws WdkModelException {

   LOG.info("JSON to DatasetRecordService: " + experimentRequest.getDatasetRecordJson().toString(2));

	  
	Client client = ClientBuilder
      .newBuilder()
      .build();
    Response response = client
      .target("http://localhost:8080/plasmodb/service/record/DatasetRecordClasses.DatasetRecordClass/instance")
      .property("Content-Type", MediaType.APPLICATION_JSON)
      .request(MediaType.APPLICATION_JSON)
	  .post(Entity.entity(experimentRequest.getDatasetRecordJson().toString(), MediaType.APPLICATION_JSON));
    try {
      if (response.getStatus() == 200) {
        LOG.info("200 OK");
        InputStream resultStream = (InputStream)response.getEntity();
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        IoUtil.transferStream(buffer, resultStream);
        return new JSONObject(new String(buffer.toByteArray()));
      }
      else {
        throw new WdkModelException("Bad status");
      }
    }
    catch(IOException ioe) {
      throw new WdkModelException(ioe);
    }
    finally {
      response.close();
      client.close();
    }
  }

}
