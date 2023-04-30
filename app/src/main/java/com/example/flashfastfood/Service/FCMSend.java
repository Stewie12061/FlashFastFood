package com.example.flashfastfood.Service;

import android.content.Context;
import android.os.StrictMode;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FCMSend {
    private static String BASE_URL = "https://fcm.googleapis.com/fcm/send";
    private static String SERVER_KEY = "key=AAAAQEYMMdk:APA91bGuwm5g_Zsf8hE5VRyCA0M_Q6BnIM3_742kCu7ubOEifMAuliJIPAhpylcJeZ4ZcI28BicfVr-zCZWhPNNOGHoob9o0X86vZojUWVzVq5MLo5LgML_6RGbkzRAQsIFkLTQcFvjN";

    public static void pushNotificationI(Context context, String token,String title, String message){
        RequestQueue queue = Volley.newRequestQueue(context);

        try {
            JSONObject json = new JSONObject();
            json.put("to",token);
            JSONObject notification = new JSONObject();
            notification.put("title",title);
            notification.put("body",message);
            json.put("notification",notification);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, BASE_URL, json, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    System.out.println("FCM "+ response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("Content-Type","application/json");
                    params.put("Authorization",SERVER_KEY);
                    return params;
                }
            };
            queue.add(jsonObjectRequest);
        }catch (Exception e){

        }
    }
}
