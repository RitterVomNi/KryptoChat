package team10.kryptochat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        final KryptoChat kC = (KryptoChat) getApplication();

        EditText receive = (EditText) findViewById(R.id.editText4);
        EditText msg = (EditText) findViewById(R.id.editText5);

        final String receiver = receive.getText().toString();
        final String message = msg.getText().toString();

        final RequestQueue queue = Volley.newRequestQueue(this);

        String url ="http://10.0.2.2:3000/"+receiver+"/pubkey";
        //String url ="https://webengserver.herokuapp.com/"+receiver+"/pubkey";
        // Request a string response from the provided URL.
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String pubkey = response.getString("pubkey_user");
                        } catch (Exception e) {e.printStackTrace();}
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        // Add the request to the RequestQueue.
        queue.add(jsonRequest);

        url ="http://10.0.2.2:3000/"+receiver+"/Message";
       // url = "https://webengserver.herokuapp.com/" + receiver+"/Message";
        StringRequest sr = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<>();
                params.put("recipient", receiver);
                params.put("sender", kC.getUserName());
                params.put("content_enc", message);
               // params.put("pubkey_user", publicKey.toString());
               // params.put("privkey_user_enc", Base64.encodeToString(privkey_user_enc, Base64.DEFAULT));
                return params;
            }

        };
        queue.add(sr);

    }
}
