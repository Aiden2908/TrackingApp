package com.example.mapapp;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.Header;

public class RegisterActivity extends Activity {
    ProgressDialog progressDialog;
    TextView errorMessage;
    EditText nameET;
    EditText emailET;
    EditText passwordET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.register);

        errorMessage = (TextView) findViewById(R.id.login_error);
        nameET = (EditText) findViewById(R.id.registerName);
        emailET = (EditText) findViewById(R.id.registerEmail);
        passwordET = (EditText) findViewById(R.id.registerPassword);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait..");
        progressDialog.setCancelable(false);
    }

    public void registerUser(View view){
        String name = nameET.getText().toString();
        String email = emailET.getText().toString();
        String password = passwordET.getText().toString();
        RequestParams params = new RequestParams();

        if(UtilityClass.isNotNull(name) && UtilityClass.isNotNull(email) && UtilityClass.isNotNull(password)) {
            if(UtilityClass.validate(email)){
                params.put("name", name);
                params.put("username", email);
                params.put("password", password);
                invokeWS(params);
            } else {
                Toast.makeText(getApplicationContext(), "Please enter valid email", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Please fill the form, don't leave any field blank", Toast.LENGTH_LONG).show();
        }
    }

    public void invokeWS(RequestParams params){
        progressDialog.show();

        AsyncHttpClient client = new AsyncHttpClient();

        client.get("localhost"/* REST WEBSERVICE URL */, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                progressDialog.hide();
                try {
                    JSONObject obj = new JSONObject(String.valueOf(responseBody));

                    if(obj.getBoolean("status")) {
                        setDefaultValues();
                        Toast.makeText(getApplicationContext(), "You are successfully logged in!", Toast.LENGTH_LONG).show();
                    } else {
                        errorMessage.setText(obj.getString("error_msg"));
                        Toast.makeText(getApplicationContext(), obj.getString("error_msg"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Error occurred", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                progressDialog.hide();

                if(statusCode == 404) {
                    Toast.makeText(getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
                } else if(statusCode == 500) {
                    Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                } else{
                    Toast.makeText(getApplicationContext(), "Unexpected Error occurred!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void navigateToLoginActivity(){
        Intent homeIntent = new Intent(getApplicationContext(), MainActivity.class);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
    }

    public void setDefaultValues(){
        nameET.setText("");
        emailET.setText("");
        passwordET.setText("");
    }
}
