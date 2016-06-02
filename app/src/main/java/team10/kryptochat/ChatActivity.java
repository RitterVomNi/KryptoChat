package team10.kryptochat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

public class ChatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        EditText pubkey = (EditText) findViewById(R.id.editText3);
        Intent iin= getIntent();
        Bundle b = iin.getExtras();

        try {
            pubkey.setText(b.get("pubkey").toString());
        } catch (Exception e) {}

    }
}
