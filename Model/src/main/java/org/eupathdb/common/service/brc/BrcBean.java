package org.eupathdb.common.service.brc;

import java.util.HashSet;
import java.util.Set;

import org.gusdb.wdk.model.WdkModelException;
import org.json.JSONArray;
import org.json.JSONObject;

public class BrcBean {
  private String experimentalIdentifier;
  private String displayName;
  private String type;
  private String description;
  private String uri;
  private String species;
  private String genomeVersion;
  private BrcGeneListBean idLists;
  private JSONObject datasetRecordTablesJson;
  
  public static Set<BrcBean> parseAnswerJson(JSONObject answerJson) throws WdkModelException {
	Set<BrcBean> brcBeans = new HashSet<>();  
	JSONArray recordsJson = answerJson.getJSONArray("records");
	for(int i = 0; i < recordsJson.length(); i++) {
	  JSONObject recordJson = recordsJson.getJSONObject(i);
	  BrcBean brcBean = parseRecordJson(recordJson);
	  brcBean.setIdLists(BrcGeneListBean.parseRecordGeneListJson(recordJson, true));
	  brcBeans.add(brcBean);
	}
	return brcBeans;
  }
  
  protected static BrcBean parseRecordJson(JSONObject recordJson) {
    BrcBean brcBean = new BrcBean();
    brcBean.setExperimentalIdentifier(
      String.valueOf(((JSONObject)recordJson.getJSONArray("id").get(0)).get("value"))
    );
    JSONObject attributesJson = recordJson.getJSONObject("attributes");
    brcBean.setDisplayName(attributesJson.getString("display_name"));
    brcBean.setType("NA");
    brcBean.setDescription(attributesJson.getString("description"));
    brcBean.setUri("NA");
    brcBean.setSpecies(attributesJson.getString("organism_prefix"));
    brcBean.setGenomeVersion("NA");
    return brcBean;
  }
  
  public static BrcBean parseDatasetRecordJson(JSONObject datasetRecordJson, boolean showGeneList) throws WdkModelException {
	BrcBean brcBean = parseRecordJson(datasetRecordJson);
	if(showGeneList) {
	  brcBean.setIdLists(BrcGeneListBean.parseRecordGeneListJson(datasetRecordJson, false));
	}
	else {
	  brcBean.setDatasetRecordTablesJson(datasetRecordJson.getJSONObject("tables"));
	}  
	return brcBean;
  }
   
  public String getExperimentalIdentifier() {
	return experimentalIdentifier;
  }
  
  public void setExperimentalIdentifier(String experimentalIdentifier) {
	this.experimentalIdentifier = experimentalIdentifier;
  }
  
  public String getDisplayName() {
	return displayName;
  }
  
  public void setDisplayName(String displayName) {
	this.displayName = displayName;
  }
  
  public String getDescription() {
	return description;
  }
  
  public void setDescription(String description) {
	this.description = description;
  }
  
  public String getUri() {
	return uri;
  }
  
  public void setUri(String uri) {
	this.uri = uri;
  }
  
  public String getType() {
	return type;
  }
  
  public void setType(String type) {
	this.type = type;
  }

  public String getSpecies() {
	return species;
  }

  public void setSpecies(String species) {
	this.species = species;
  }

  public String getGenomeVersion() {
	return genomeVersion;
  }

  public void setGenomeVersion(String genomeVersion) {
	this.genomeVersion = genomeVersion;
  }

  public BrcGeneListBean getIdLists() {
	return idLists;
  }

  public void setIdLists(BrcGeneListBean idLists) {
	this.idLists = idLists;
  }

  public JSONObject getDatasetRecordTablesJson() {
	return datasetRecordTablesJson;
  }

  public void setDatasetRecordTablesJson(JSONObject datasetRecordTablesJson) {
	this.datasetRecordTablesJson = datasetRecordTablesJson;
  }
  
  

}
