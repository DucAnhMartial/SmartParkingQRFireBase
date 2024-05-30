package com.example.firebasedemo;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.ktx.Firebase;

import java.sql.Connection;
import java.sql.ResultSet;

public class Login extends AppCompatActivity {
    Button btnLogin_login, btnThoat_login, btnSignUp_login;
    EditText edtTenDN_login, edtMatKhau_login;
    TextView tv1;
    FirebaseDatabase db;
    DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this); // Khởi tạo FirebaseApp
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        db = FirebaseDatabase.getInstance();
        ref = db.getReference();
        btnLogin_login = findViewById(R.id.btnLogin_login);
        btnThoat_login = findViewById(R.id.btnThoat_login);
        btnSignUp_login = findViewById(R.id.btnSignUp_login);
        edtTenDN_login = findViewById(R.id.edtTenDN_login);
        edtMatKhau_login = findViewById(R.id.edtMatKhau_Login);
        tv1 = findViewById(R.id.tv1);
        tv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent i = new Intent(Login.this,forgetPassword.class);
                startActivity(i);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
        //Bug chưa thoát được
        btnThoat_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDestroy();
            }
        });
        if (isNetworkAvailable()) {
            btnLogin();
            btnSignUp();
        } else {
            Toast.makeText(this, "Không có kết nối Internet", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    private void btnSignUp() {
        btnSignUp_login.setOnClickListener(v -> {
            Intent i = new Intent(Login.this, sign_up.class);
            startActivity(i);
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });
    }

    private void btnLogin() {
        btnLogin_login.setOnClickListener(v -> {
            String userName = edtTenDN_login.getText().toString();
            String passWord = edtMatKhau_login.getText().toString();

            if (userName.isEmpty() || passWord.isEmpty()) {
                Toast.makeText(Login.this, "Vui lòng nhập đầy đủ tài khoản và mật khẩu", Toast.LENGTH_SHORT).show();
            } else {
                ref.child("TAI_KHOAN").orderByChild("tenDn").equalTo(userName).get().addOnCompleteListener(tenDntask -> {
                    if (tenDntask.isSuccessful()) {
                        if (tenDntask.getResult().exists()) {
                            ref.child("TAI_KHOAN").orderByChild("matKhau").equalTo(passWord).get().addOnCompleteListener(passTask -> {
                                if (passTask.isSuccessful()) {
                                    if (passTask.getResult().exists()) {
                                        // Mật khẩu đúng, đăng nhập thành công
                                        Toast.makeText(this, "Bạn đã đăng nhập thành công", Toast.LENGTH_SHORT).show();
                                        AccountSingleton.getInstance().setAccountInfo(userName, passWord);
                                        Intent i = new Intent(Login.this, AfterLogin.class);
                                        startActivity(i);
                                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                    } else {
                                        // Mật khẩu không đúng
                                        Toast.makeText(this, "Mật khẩu không chính xác", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    // Xảy ra lỗi khi kiểm tra mật khẩu
                                    Log.d("Lỗi: ", passTask.getException().getMessage());
                                }
                            });
                        } else {
                            // Tên đăng nhập không tồn tại
                            Toast.makeText(this, "Tên đăng nhập không tồn tại", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Xảy ra lỗi khi kiểm tra tên đăng nhập
                        Log.d("Lỗi: ", tenDntask.getException().getMessage());
                    }
                });
            }
        });
    }
}
