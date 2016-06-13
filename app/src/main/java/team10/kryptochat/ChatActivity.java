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
    KryptoChat kC = (KryptoChat) getApplication();
    final RequestQueue queue = Volley.newRequestQueue(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        EditText receiver = (EditText) findViewById(R.id.editText4);
        EditText message = (EditText) findViewById(R.id.editText5);

        final String receiverr = receiver.getText().toString();



        String url ="https://webengserver.herokuapp.com/"+receiverr+"/pubkey";
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


        url = "https://webengserver.herokuapp.com/" + receiverr+"/Message";
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
                params.put("recipient", receiverr);
                params.put("sender", kC.getUserName());
                params.put("pubkey_user", publicKey.toString());
                params.put("privkey_user_enc", Base64.encodeToString(privkey_user_enc, Base64.DEFAULT));
                return params;
            }

        };
        queue.add(sr);

    }
}
