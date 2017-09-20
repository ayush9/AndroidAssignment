package com.coding.android.androidassignment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    Context ctx;
    ListView lv=null;
    private ProgressDialog pDialog;

    // URL to get contacts JSON
    private static String url = "https://api.myjson.com/bins/uze25";
    // contacts JSONArray
    JSONArray contacts = null;
    JSONArray contact1=null;

    // Hashmap for ListView
    ArrayList<HashMap<String, String>> contactList = new ArrayList<HashMap<String, String>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx = this;
        setContentView(R.layout.activity_main);
        new GetContacts().execute();
    }

    public static void setLikeButtonStatus(Context con, boolean isShown) {
        SharedPreferences sh = con.getSharedPreferences("is_shown", 0);
        SharedPreferences.Editor ed = sh.edit();
        ed.putBoolean("is_shown", isShown);
        ed.apply();
    }

    public static boolean getLikeButtonStatus(Context con) {
        SharedPreferences sh = con.getSharedPreferences("is_shown", 0);
        return sh.getBoolean("is_shown", false);
    }

    private class GetContacts extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog

             pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);
            Log.d("Response: ", "> " + jsonStr);

            if (jsonStr != null) {
                try {
                    // Getting JSON Array node
                    JSONObject p=(new JSONObject(jsonStr));
                    contacts = new JSONArray(p.getString("materials"));

                    // looping through All Contacts
                    for (int i = 0; i < contacts.length(); i++) {
                        JSONObject c = contacts.getJSONObject(i);

                        String name = c.getString("name");
                        String imageUrl = c.getString("imageUrl");
                        String title = c.getString("title");
                        String text = c.getString("text");
                        int time = c.getInt("time");
                        String description = c.getString("description");

                        HashMap<String, String> contact = new HashMap<String, String>();

                        // adding each child node to HashMap key => value
                        contact.put("name", name);
                        contact.put("imageUrl", imageUrl);
                        contact.put("title", title);
                        contact.put("text", text);
                        contact.put("description", description);

                        SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
                        String dateString = formatter.format(new Date(time));
                        contact.put("time", dateString);
                        // adding contact to contact list
                        contactList.add(contact);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            lv=(ListView) findViewById(R.id.list);
            lv.setAdapter(new MainAdapter(MainActivity.this, contactList));
            registerForContextMenu(lv);
        }

    }

}
