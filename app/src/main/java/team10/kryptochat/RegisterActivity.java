package team10.kryptochat;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


import java.security.*;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class RegisterActivity extends AppCompatActivity {

    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setMax(100);


    }

    public void register (View button) {
        final RequestQueue queue = Volley.newRequestQueue(this);
        final EditText pass = (EditText) findViewById(R.id.editText2);
        final EditText login = (EditText) findViewById(R.id.editText);
        progressBar.setVisibility(View.VISIBLE);


        class RegisterTask extends AsyncTask<String, Integer, Boolean> {
            final byte[] salt_masterkey = SecureRandom.getSeed(64);
            int iterations = 10000;
            Boolean result;

            protected Boolean doInBackground(String... eingabe) {
                char[] chars = eingabe[1].toCharArray();
                final String login = eingabe[0];
                publishProgress(10);
            try {
                PBEKeySpec spec = new PBEKeySpec(chars, salt_masterkey, iterations, 32 * 8);
                SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
                publishProgress(20);
                Key masterkey = skf.generateSecret(spec);


                KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
                kpg.initialize(2048);
                publishProgress(30);
                KeyPair kp = kpg.genKeyPair();
                publishProgress(32);
                final PublicKey publicKey = kp.getPublic();
                publishProgress(36);
                Key privateKey = kp.getPrivate();

                publishProgress(40);

                Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
                publishProgress(60);
                cipher.init(Cipher.WRAP_MODE,masterkey);
                publishProgress(70);
                final byte[] privkey_user_enc = cipher.wrap(privateKey);

                publishProgress(80);

                String url = "https://webengserver.herokuapp.com/" + login;
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
                        params.put("login", login);
                        params.put("salt_masterkey", Base64.encodeToString(salt_masterkey, Base64.DEFAULT));
                        params.put("pubkey_user", publicKey.toString());
                        params.put("privkey_user_enc", Base64.encodeToString(privkey_user_enc, Base64.DEFAULT));
                        return params;
                    }

                };
                queue.add(sr);
                result = true;
            } catch (Exception e) {
                e.printStackTrace();
                result = false;}

               publishProgress(100);
                return result;
                }
            @Override
            protected void onProgressUpdate(Integer... progress) {
                progressBar.setProgress(progress[0]);
            }

            protected void onPostExecute(Boolean result) {
                if(result) {
                    Toast.makeText(RegisterActivity.this, "Erfolgreich registriert", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                }
                else
                    Toast.makeText(RegisterActivity.this,"Ein Fehler ist aufgetreten", Toast.LENGTH_LONG).show();
            }
        } new RegisterTask().execute(login.getText().toString(), pass.getText().toString());


    }

}

