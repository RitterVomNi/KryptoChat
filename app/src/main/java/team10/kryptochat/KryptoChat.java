package team10.kryptochat;

import android.app.Application;
import android.content.res.Configuration;

/**
 * Created by matthias on 29.05.16.
 */
public class KryptoChat extends Application {

    private String userName;
    private String privkey_user_enc;
    private String pubkey;
    private String salt_masterkey;
    private String password;

    @Override
    public void onCreate()
    {
        super.onCreate();

        // Initialize the singletons so their instances
        // are bound to the application process.
        initSingletons();
    }

    protected void initSingletons()
    {
        // Initialize the instance of MySingleton
        MySingleton.initInstance();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public String getPubkey() {
        return pubkey;
    }
    public void setPubkey(String pubkey) {
        this.pubkey = pubkey;
    }
    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getPrivkey_user_enc() {
        return privkey_user_enc;
    }
    public void setPrivkey_user_enc(String privkey_user_enc) {
        this.privkey_user_enc = privkey_user_enc;
    }
    public String getSalt_masterkey() {
        return salt_masterkey;
    }
    public void setSalt_masterkey(String salt_masterkey) {
        this.salt_masterkey = salt_masterkey;
    }
}
