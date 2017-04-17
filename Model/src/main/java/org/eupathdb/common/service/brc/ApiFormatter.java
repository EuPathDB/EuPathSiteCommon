package org.eupathdb.common.service.brc;

import org.json.JSONArray;
import org.json.JSONObject;

public class ApiFormatter {
	
  public static JSONObject getJson() {
	return new JSONObject()
	  .put("inputTypes", new JSONArray()
	    .put(new JSONObject()
		  .put("type", "gene")
		  .put("displayName", "String")
		  .put("description", "String")
		  .put("idSources", new JSONArray()
            .put("genebank"))
		  .put("thresholdTypes", new JSONArray()
            .put("specificity"))
		  .put("additionalFlags", new JSONArray()
		    .put(new JSONObject()
			  .put("key","useOrthology")
			  .put("jsonType", "Boolean")))));
  }
}
