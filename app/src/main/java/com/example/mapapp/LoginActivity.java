package com.example.mapapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final EditText etEmail=findViewById(R.id.username);
        final EditText etPassword=findViewById(R.id.password);
        final TextView tvMessage=findViewById(R.id.message);
        final ProgressBar progressBar=findViewById(R.id.loading);
        Button btnLogin=findViewById(R.id.login);

        etEmail.setText("testa@gmail.com");
        etPassword.setText("test");

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvMessage.setText("");
                tvMessage.setTextColor(Color.RED);
                progressBar.setVisibility(View.VISIBLE);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            XML_Request xml_request=new XML_Request(XML_Request.LOGIN_URL,"POST");
                            final String result=xml_request.loginUser(etEmail.getText().toString(),etPassword.getText().toString());
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(result==null){
                                        tvMessage.setText("Could not establish a connection to the server error.");
                                    }else if(result.contains("doesnt-exist")){
                                        createAccount(etEmail,etPassword,tvMessage,progressBar);
                                    }else if(result.contains("user")){
                                        startActivity(new Intent(getApplicationContext(), MapActivity.class));
                                        overridePendingTransition(0,0);
                                    }
                                    progressBar.setVisibility(View.GONE);
                                   // tvMessage.setText(result);
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });


    }
    private void createAccount(final EditText etEmail,final EditText etPassword,final TextView tvMessage,final ProgressBar progressBar){
        new Thread(new Runnable() {
            @Override
            public void run() {
                XML_Request xml_request=new XML_Request(XML_Request.API,"POST");
                try {
                    final String result=xml_request.registerUser(etEmail.getText().toString(),etPassword.getText().toString());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(result.contains("userAlreadyExists")){
                                tvMessage.setText("Invalid email or password.");
                                progressBar.setVisibility(View.GONE);
                            }else if(result.contains("user")){
                                tvMessage.setTextColor(Color.GREEN);
                                tvMessage.setText("Account created");
                                progressBar.setVisibility(View.GONE);
                            }
                           // tvMessage.setText(result);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }
}