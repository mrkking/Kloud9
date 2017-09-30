package com.example.krudeboy.kloud9.dataHandler;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.krudeboy.kloud9.customRequests.UserRequest;
import com.example.krudeboy.kloud9.models.UserModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Krudeboy on 9/29/2017.
 */

public class UserHandler {
    private static final String BASE_LINK_LOC = "http://10.0.2.2:3000";
    private static final String REG_USER_LNK = "/user";
    private static final String LOG_USER_LNK = "/user/login";
    private static final String VAL_USER_LNK = "/users/me";

    RequestQueue rQue;
    Context context;

    public UserHandler(Context c){
        rQue = Volley.newRequestQueue(c);
        context = c;
    }

    public void register(String email, String pwd){
        JSONObject params = new JSONObject();
        try {
            params.put("email", email);
            params.put("password", pwd);
        }catch (JSONException e){

        }

        UserRequest<UserModel> mReq = new UserRequest<UserModel>(Request.Method.POST,
                BASE_LINK_LOC + REG_USER_LNK,
                UserModel.class,
                params,
                context,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if(error.networkResponse != null)
                            Log.e("ERROR", "FAILED"+new String(error.networkResponse.data));
                        else
                            Log.e("Error", "FAILED");
                        //Log.e("STATUS_CODE", String.valueOf(error.networkResponse.data.));
                    }
                },
                new Response.Listener<UserModel>() {
                    @Override
                    public void onResponse(UserModel response) {
                        Log.i("EMAIL", response.getEmail());
                    }
                });

        rQue.add(mReq);
    }

    public void userLogin(String email, String pwd){
        JSONObject params = new JSONObject();
        try {
            params.put("email", email);
            params.put("password", pwd);
        }catch (JSONException e){

        }

        UserRequest<UserModel> mReq = new UserRequest<UserModel>(Request.Method.POST,
                BASE_LINK_LOC + LOG_USER_LNK,
                UserModel.class,
                params,
                context,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if(error.networkResponse != null)
                            Log.e("ERROR", "FAILED"+new String(error.networkResponse.data));
                        else
                            Log.e("Error", "FAILED");
                        //Log.e("STATUS_CODE", String.valueOf(error.networkResponse.data.));
                    }
                },
                new Response.Listener<UserModel>() {
                    @Override
                    public void onResponse(UserModel response) {


                        SharedPreferences m = PreferenceManager.getDefaultSharedPreferences(context);
                        String mResponse = m.getString("auth", "");

                        Log.i("AUTH TOKEN", mResponse);
                        Log.i("EMAIL", response.getEmail()+mResponse);
                    }
                });

        rQue.add(mReq);
    }

    public void isLoggedin(){
        SharedPreferences m = PreferenceManager.getDefaultSharedPreferences(context);
        String key = m.getString("auth", "");

        if(key == "") {
            Log.i("IS_LOGGED_IN", "FAILED - NO AUTH KEY");
            return;
        }else{
            UserRequest<UserModel> mReq = new UserRequest<UserModel>(Request.Method.GET,
                    BASE_LINK_LOC + VAL_USER_LNK,
                    UserModel.class,
                    null,
                    context,
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            if(error.networkResponse != null)
                                Log.e("ERROR", "FAILED"+new String(error.networkResponse.data));
                            else
                                Log.e("Error", "FAILED");
                            //Log.e("STATUS_CODE", String.valueOf(error.networkResponse.data.));
                        }
                    },
                    new Response.Listener<UserModel>() {
                        @Override
                        public void onResponse(UserModel response) {

                            Log.i("EMAIL - VALIDATE", response.getEmail());

                        }
                    }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {

                    SharedPreferences m = PreferenceManager.getDefaultSharedPreferences(context);
                    String mResponse = m.getString("auth", "");

                    Map<String, String> params = super.getParams();

                    if(params == null)
                        params = new HashMap<>();
                    params.put("x-auth", mResponse);
                    Log.i("PARAMS", params.toString());
                    return params;
                }

                @Override
                protected String getParamsEncoding() {
                    return "Content-Type: text/html; charset=utf-8";
                }
            };
            rQue.add(mReq);
        }

    }


}
