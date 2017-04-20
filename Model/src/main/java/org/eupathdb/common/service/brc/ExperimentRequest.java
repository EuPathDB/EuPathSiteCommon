package org.eupathdb.common.service.brc;

import org.json.JSONArray;
import org.json.JSONObject;

public class ExperimentRequest {
  private String experimentId;
  
  public JSONObject getDatasetRecordJson() {
    return new JSONObject()
      .put("primaryKey", new JSONArray()
        .put(new JSONObject()
          .put("name", "dataset_id")
          .put("value", experimentId)))
      .put("attributes", new JSONArray()
        .put("display_name")
        .put("description")
        .put("organism_prefix"))
      .put("tables", new JSONArray()
        .put("DatasetGeneTable")
        .put("GenomeHistory")
        .put("Version")
        .put("HyperLinks"));
  }

  public String getExperimentId() {
	return experimentId;
  }

  public void setExperimentId(String experimentId) {
	this.experimentId = experimentId;
  }
  
  

}
