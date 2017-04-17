package org.eupathdb.common.service.brc;

import java.util.Set;

import org.gusdb.wdk.model.WdkModelException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BrcFormatter {
	
  public static JSONArray getJson(Set<BrcBean> beans) throws JSONException, WdkModelException {
	JSONArray array = new JSONArray();
	for(BrcBean bean : beans) {
      BrcGeneListBean idLists = bean.getIdLists();
      JSONArray idListsJson = new JSONArray();
      JSONObject idListJson = getGeneListJson(idLists, true);
      idListsJson.put(idListJson);
	  array.put(getBrcJson(bean)
			  .put("idLists", idListsJson));
	}
	return array;
  }
  
  public static JSONObject getJson(BrcBean bean) throws JSONException, WdkModelException {
    return getBrcJson(bean)
   	 .put("tables", bean.getDatasetRecordTablesJson());
  }

  public static JSONObject getBrcJson(BrcBean bean) throws JSONException, WdkModelException {
    return new JSONObject()
      .put("experimentalIdentifier", bean.getExperimentalIdentifier())
      .put("displayName", bean.getDisplayName())
      .put("type", bean.getType())
      .put("description", bean.getDescription())
      .put("uri", bean.getUri())
      .put("species", bean.getSpecies())
      .put("genomeVersion", bean.getGenomeVersion());
  }
  
  public static JSONObject getGeneListJson(BrcGeneListBean bean, boolean search) {
    JSONObject json = new JSONObject()
      .put("listIdentifier", bean.getListIdentifier())
      .put("displayName", bean.getDisplayName())
      .put("description", bean.getDescription())
      .put("uri", bean.getUri())
      .put("type", bean.getType())
      .put("provenance", bean.getProvenance())
      .put("significance", bean.getSignificance())
      .put("significanceType", bean.getSignificanceType());    
    if(search) {
      json
        .put("hitCount", bean.getHitCount())
        .put("percentCount", bean.getPercentCount());
    }
    return json;
  }

}
