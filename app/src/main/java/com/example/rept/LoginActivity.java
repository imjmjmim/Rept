package com.example.rept;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.recaptcha.Recaptcha;
import com.google.android.recaptcha.RecaptchaAction;
import com.google.android.recaptcha.RecaptchaTasksClient;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private EditText et_id, et_pass;
    private Button btn_login,btn_register,btn_captcha;

    @Nullable private RecaptchaTasksClient recaptchaTasksClient = null;


    private void saveUserID(String userID) {
        // SharedPreferences에 저장하기
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userID", userID);
        editor.apply();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initializeRecaptchaClient();

        et_id = findViewById(R.id.et_id);
        et_pass = findViewById(R.id.et_pass);
        btn_login = findViewById(R.id.btn_login);
        btn_register = findViewById(R.id.btn_register);
        btn_captcha = findViewById(R.id.btn_captcha);



        btn_captcha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (recaptchaTasksClient != null) {
                    executeLoginAction();
                } else {
                    Toast.makeText(LoginActivity.this, "Recaptcha 클라이언트가 초기화되지 않았습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });



        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });



        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userID = et_id.getText().toString();
                String userPassword = et_pass.getText().toString();

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.substring(response.indexOf("{")));
                            boolean success = jsonObject.getBoolean("success");
                            if (success) {
                                String userID = jsonObject.getString("userID");
                                String userPassword = jsonObject.getString("userPassword");
                                Toast.makeText(getApplicationContext(),"로그인에 성공하였습니다",Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                                intent.putExtra("userID",userID);
                                intent.putExtra("userPassword",userPassword);
                                startActivity(intent);

                            } else {
                                Toast.makeText(getApplicationContext(), "로그인에 실패하였습니다", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                LoginRequest loginRequest = new LoginRequest(userID,userPassword,responseListener);
                RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                queue.add(loginRequest);
            }
        });



    }
    private void initializeRecaptchaClient() {
        Recaptcha
                .getTasksClient(getApplication(), "6LcN3HYmAAAAACuKcpnkiNvirC-mCfBDTedaYg_2")
                .addOnSuccessListener(
                        this,
                        new OnSuccessListener<RecaptchaTasksClient>() {
                            @Override
                            public void onSuccess(RecaptchaTasksClient client) {
                                System.out.println("***************************************"+client);

                                LoginActivity.this.recaptchaTasksClient = client;

                            }
                        })
                .addOnFailureListener(
                        this,
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                System.out.println("***************************************"+e);
                                // Handle communication errors ...
                                // See "Handle communication errors" section
                            }
                        });
    }

    private void executeLoginAction() {
        if (recaptchaTasksClient != null) {
            recaptchaTasksClient
                    .executeTask(RecaptchaAction.LOGIN)
                    .addOnSuccessListener(
                            this,
                            new OnSuccessListener<String>() {
                                @Override
                                public void onSuccess(String token) {
                                    // Handle success ...
                                    // See "What's next" section for instructions
                                    // about handling tokens.
                                    Toast.makeText(LoginActivity.this, "캡챠 인증이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                                }
                            })
                    .addOnFailureListener(
                            this,
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Handle communication errors ...
                                    // See "Handle communication errors" section
                                    Toast.makeText(LoginActivity.this, "Recaptcha 실행에 실패했습니다.", Toast.LENGTH_SHORT).show();
                                }
                            });
        } else {
            Toast.makeText(LoginActivity.this, "Recaptcha 클라이언트가 초기화되지 않았습니다.", Toast.LENGTH_SHORT).show();
        }
    }

}