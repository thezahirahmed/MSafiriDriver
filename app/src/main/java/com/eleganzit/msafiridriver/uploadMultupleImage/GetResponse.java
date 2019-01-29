package com.eleganzit.msafiridriver.uploadMultupleImage;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by PC10 on 8/3/2016.
 */
public interface GetResponse {
    void onSuccesResult(JSONObject result) throws JSONException;
    void onFailureResult(String messgae) throws JSONException;

}
