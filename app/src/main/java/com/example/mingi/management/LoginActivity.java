package com.example.mingi.management;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    String loginId, loginPwd;
    String userID;
    String userPassword;
    CheckBox checkBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        checkBox = (CheckBox) findViewById(R.id.auto);
        Intent fromSplash = getIntent();
        final String isGPSEnable = fromSplash.getStringExtra("isGPSEnable");
        final String nowLat = fromSplash.getStringExtra("nowLat");
        final String nowLon = fromSplash.getStringExtra("nowLon");
        final String nowName = fromSplash.getStringExtra("nowName");

        final EditText idText = (EditText) findViewById(R.id.idText);
        final EditText passwordText = (EditText) findViewById(R.id.passwordText);
        final Button loginButton = (Button) findViewById(R.id.loginButton);
        final TextView registerButton = (TextView) findViewById(R.id.registerButton);


        SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);

        loginId = auto.getString("inputId",null);
        loginPwd = auto.getString("inputPwd",null);



        // 자동 로그인
        if(loginId !=null && loginPwd != null) {
            Log.d(" 자동로그인 ", "ID : " + loginId);
            // 인텐드에 넣기
            Intent intent = new Intent(LoginActivity.this, CarJoinActivity.class);
            intent.putExtra("userID", loginId);
            intent.putExtra("nowLat", nowLat);
            intent.putExtra("nowLon", nowLon);
            intent.putExtra("isGPSEnable", isGPSEnable);
            intent.putExtra("nowName", nowName);
            LoginActivity.this.startActivity(intent);
            // 화면전환 넣기 //
            finish();
        }


            registerButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                LoginActivity.this.startActivity(registerIntent);
            }

        });


        // 로그인 버튼 클릭
        loginButton.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {

                userID = idText.getText().toString();
                userPassword = passwordText.getText().toString();

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            // 제이슨 생성
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if (success) {  // 성공
                                Log.d("  로그인 상공 : ", "");

                                String userID = jsonResponse.getString("userID");
                                String userPassword = jsonResponse.getString("userPassword");


                                if(checkBox.isChecked() == true) {
                                    SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);
                                    //auto의 loginId와 loginPwd에 값을 저장해 줍니다.
                                    SharedPreferences.Editor autoLogin = auto.edit();
                                    autoLogin.putString("inputId", userID);
                                    autoLogin.putString("inputPwd", userPassword);
                                    autoLogin.commit();
                                }


                                // 인텐드에 넣기
                                Intent intent = new Intent(LoginActivity.this, CarJoinActivity.class);
                                intent.putExtra("userID", userID);
                                intent.putExtra("userPassword", userPassword);
                                intent.putExtra("nowLat", nowLat);
                                intent.putExtra("nowLon", nowLon);
                                intent.putExtra("isGPSEnable", isGPSEnable);
                                intent.putExtra("nowName", nowName);
                                LoginActivity.this.startActivity(intent);
                                // 화면전환 넣기 //
                                finish();


                            } else {
                                Log.d("  로그인 실패 : ", "");
                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                builder.setMessage("로그인에 실패하였습니다.")
                                        .setNegativeButton("다시시도", null)
                                        .create()
                                        .show();
                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                };

                // 로그인 리퀘스트 생성
                LoginRequest loginRequest = new LoginRequest(userID, userPassword, responseListener);
                RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);

                // 요청
                queue.add(loginRequest);


            }


        });


    }
}
