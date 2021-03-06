package com.example.acer.login.Login_Related;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.acer.login.ProfileActivity;
import com.example.acer.login.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText editTextEmail, editTextPassword;
    private Button buttonLogin;
    private ProgressDialog progressDialog;

    private TextView textViewLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if(SharedPrefManager.getInstance(this).isLoggedIn()){
            finish();
            startActivity(new Intent(this,ProfileActivity.class));
            return;
        }

        editTextEmail = (EditText)findViewById(R.id.editTextEmail);
        editTextPassword =(EditText)findViewById(R.id.editTextPassword);
        buttonLogin = (Button)findViewById(R.id.buttonLogin);

        textViewLogin = (TextView)findViewById(R.id.textViewLogin);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");

        buttonLogin.setOnClickListener(this);
        textViewLogin.setOnClickListener(this);
    }

    private void userLogin(){
        final String email = editTextEmail.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();

        progressDialog.show();
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constants.URL_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject obj = new JSONObject(response);
                            if(!obj.getBoolean("error")){
                                SharedPrefManager.getInstance(getApplicationContext()).userLogin(
                                        obj.getString("email"),
                                        obj.getString("name"),
                                        obj.getString("birthday"),
                                        obj.getInt("exp"),
                                        obj.getInt("level"));


                                Toast.makeText(getApplicationContext(),
                                        "User login successful",
                                        Toast.LENGTH_LONG).show();
                                startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
                                finish();

                            }else{
                                Toast.makeText(getApplicationContext(),
                                        obj.getString("message"),
                                        Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(),
                                error.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("email",email);
                params.put("password",password);
                return params;
            }
        };

        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);

    }

    @Override
    public void onClick(View v) {
        if(v == buttonLogin){
            userLogin();
        }
        if( v == textViewLogin){
            startActivity(new Intent(this, MainActivity.class));
        }
    }
}
