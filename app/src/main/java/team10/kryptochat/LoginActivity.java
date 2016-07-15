package team10.kryptochat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    Button btn;
    protected KryptoChat kC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        final EditText login = (EditText) findViewById(R.id.editText);
        final EditText pass = (EditText) findViewById(R.id.editText2);
        kC = (KryptoChat) getApplication();

        final RequestQueue queue = Volley.newRequestQueue(this);


        btn = (Button) findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(login != null && pass != null) {
                 //   String url ="http://10.0.2.2:3000/"+login.getText();
                String url ="https://webengserver.herokuapp.com/"+login.getText();
                // Request a string response from the provided URL.
                    JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Intent i = new Intent(LoginActivity.this, ChatActivity.class);
                                try {
                                    kC.setPassword(pass.getText().toString());
                                    kC.setPubkey(response.getString("pubkey_user"));
                                    kC.setPrivkey_user_enc(response.getString("privkey_user_enc"));
                                    kC.setUserName(login.getText().toString());
                                    kC.setSalt_masterkey(response.getString("salt_masterkey"));
                                    startActivity(i);
                                    Toast.makeText(LoginActivity.this,"Hallo "+ kC.getUserName(), Toast.LENGTH_LONG).show();
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
            }}
        });
    }
}
