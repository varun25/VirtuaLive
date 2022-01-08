package reverseminds.virtualive;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import reverseminds.virtualive.login.Constants;
import reverseminds.virtualive.login.LoginFragment;

/**
 * Created by Kushal I on 28/10/2017.
 */

public class LoginActivity extends AppCompatActivity {

    private SharedPreferences pref;
    Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.d("VIRTUALIVE","BACK IN LOGIN");
        pref = this.getSharedPreferences("VIRTUALIVE", Context.MODE_PRIVATE);
        Log.d("VIRTUALIVE","LOGIN ACTIVITY "+pref.getBoolean(Constants.IS_LOGGED_IN,false));
        if(pref.getBoolean(Constants.IS_LOGGED_IN,false)){
            Log.d("VIRTUALIVE","LOGGED IN");
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }else {
            Log.d("VIRTUALIVE","LOGGED OUT >> SO LOGIN");
            fragment = new LoginFragment();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_frame,fragment);
            ft.commit();
        }
    }
}