package team10.kryptochat;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
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
import org.spongycastle.crypto.PBEParametersGenerator;
import org.spongycastle.crypto.digests.SHA256Digest;
import org.spongycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.spongycastle.crypto.params.KeyParameter;
import org.spongycastle.util.io.pem.PemObject;
import org.spongycastle.util.io.pem.PemReader;

import java.io.StringReader;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.spec.EncodedKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class ChatActivity extends AppCompatActivity {
    EditText receive,msg;
    String pubkey, timestamp;
    int iterations = 10000;
    protected KryptoChat kC;
    byte[] sig_recipient, sig_service, key_recipient_enc ,content_enc ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        receive = (EditText) findViewById(R.id.editText4);
        msg = (EditText) findViewById(R.id.editText5);

        kC = (KryptoChat) getApplication();

    }
    public void senden(View button) {

        final String receiver = receive.getText().toString();
        final String message = msg.getText().toString();

        final RequestQueue queue = Volley.newRequestQueue(this);

       // String url ="http://10.0.2.2:3000/"+receiver+"/pubkey";
        String url ="https://webengserver.herokuapp.com/"+receiver+"/pubkey";
        // Request a string response from the provided URL.
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            pubkey = response.getString("pubkey_user");
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

        final byte[] key_recipient = SecureRandom.getSeed(16);
        final byte[] iv = SecureRandom.getSeed(16);

        try {
            //PubKey des Empfängers erzeugen
            StringReader stringReader = new StringReader(pubkey);
            PemReader pemReader = new PemReader(stringReader);

            PemObject obj = pemReader.readPemObject();
            pemReader.close();

            KeyFactory keyFactory = null;
            keyFactory = KeyFactory.getInstance("RSA");

            X509EncodedKeySpec spec = new X509EncodedKeySpec(obj.getContent());
            PublicKey pubkey_recipient = keyFactory.generatePublic(spec);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec skc = new SecretKeySpec(key_recipient, "AES");
            cipher.init(Cipher.ENCRYPT_MODE, skc, new IvParameterSpec(iv));
            content_enc = cipher.doFinal(message.getBytes());

            byte[] salt_masterkey = Base64.decode(kC.getSalt_masterkey(), Base64.DEFAULT);
            char[] chars = kC.getPassword().toCharArray();

            PKCS5S2ParametersGenerator generator = new PKCS5S2ParametersGenerator(new SHA256Digest());
            generator.init(PBEParametersGenerator.PKCS5PasswordToUTF8Bytes(chars), salt_masterkey, iterations);
            KeyParameter masterkey_bytes = (KeyParameter)generator.generateDerivedMacParameters(256);


            Cipher cipher3 = Cipher.getInstance("AES/ECB/PKCS5Padding");
            SecretKeySpec skc2 = new SecretKeySpec(masterkey_bytes.getKey(), "AES");
            cipher3.init(Cipher.DECRYPT_MODE, skc2);
            byte[] privkey_user = cipher3.doFinal(Base64.decode(kC.getPrivkey_user_enc().getBytes("utf-8"), Base64.DEFAULT));


            KeyFactory keyFactoryy = KeyFactory.getInstance("RSA");
            EncodedKeySpec specc = new PKCS8EncodedKeySpec(privkey_user);
            PrivateKey privKey = keyFactoryy.generatePrivate(specc);


            Cipher cipher2 = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher2.init(Cipher.ENCRYPT_MODE, pubkey_recipient);
            key_recipient_enc = cipher2.doFinal(key_recipient);

            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(privKey);
            signature.update(content_enc);
            sig_recipient = signature.sign();
            sig_service = signature.sign();

            long tsLong = System.currentTimeMillis()/1000;
            timestamp = Integer.toString(((int)tsLong));

            class SendTask extends AsyncTask<String, Integer, Boolean> {
                Boolean result = false;
                protected Boolean doInBackground(String... eingabe) {

                    //url ="http://10.0.2.2:3000/"+receiver+"/message";
                    String url = "https://webengserver.herokuapp.com/" + receiver+"/message";
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
                            params.put("pubkey_user", kC.getPubkey());
                            params.put("timestamp", timestamp);
                            params.put("iv", Base64.encodeToString(iv, Base64.DEFAULT));
                            params.put("content_enc", Base64.encodeToString(content_enc, Base64.DEFAULT));
                            params.put("sig_recipient", Base64.encodeToString(sig_recipient, Base64.DEFAULT));
                            params.put("sig_service", Base64.encodeToString(sig_service, Base64.DEFAULT));
                            params.put("key_recipient_enc", Base64.encodeToString(key_recipient_enc, Base64.DEFAULT));
                            result = true;
                            return params;
                        }

                    };
                    queue.add(sr);
                    return result;
                }

            }
            new SendTask().execute();
        } catch(Exception e) {e.printStackTrace();}
    }
}
