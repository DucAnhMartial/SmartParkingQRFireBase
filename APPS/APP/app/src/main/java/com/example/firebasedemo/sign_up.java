package com.example.firebasedemo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class sign_up extends AppCompatActivity {
    Button btn1, btn2;
    TextView edtname, edtcccd, edtsdt, edttendn, edtmatkhau, edtEmail;

    private FirebaseDatabase db;
    private DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this); // Khởi tạo FirebaseApp
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = FirebaseDatabase.getInstance();
        ref = db.getReference();
        btn1 = findViewById(R.id.btnSignUp_signUp);
        btn2 = findViewById(R.id.btnQuayLai_signup);
        edtname = findViewById(R.id.edtName_Signup);
        edtcccd = findViewById(R.id.edtCCCD_Signup);
        edtsdt = findViewById(R.id.edtSDT_Signup);
        edttendn = findViewById(R.id.edtTENDN_Signup);
        edtmatkhau = findViewById(R.id.edtMatKhau_Signup);
        edtEmail = findViewById(R.id.edtEmail_Signup);

        btnsignup();
        btn2.setOnClickListener(v -> {
            Intent i = new Intent(sign_up.this,Login.class);
            startActivity(i);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        });
    }

    private void btnsignup() {
        btn1.setOnClickListener(v -> {
            String fullName = edtname.getText().toString();
            String userName = edttendn.getText().toString();
            String cccd = edtcccd.getText().toString();
            String passWord = edtmatkhau.getText().toString();
            String phoneNumber = edtsdt.getText().toString();
            String email = edtEmail.getText().toString();

            // Log the values to ensure they are not null
            Log.d("SIGN_UP", "fullName: " + fullName);
            Log.d("SIGN_UP", "userName: " + userName);
            Log.d("SIGN_UP", "cccd: " + cccd);
            Log.d("SIGN_UP", "passWord: " + passWord);
            Log.d("SIGN_UP", "phoneNumber: " + phoneNumber);
            Log.d("SIGN_UP", "email: " + email);

            if (fullName.isEmpty() || cccd.isEmpty() || phoneNumber.isEmpty() || passWord.isEmpty() || userName.isEmpty() || email.isEmpty()) {
                Toast.makeText(sign_up.this, "Vui lòng nhập đầy đủ thông tin người dùng", Toast.LENGTH_SHORT).show();
            } else if (fullName.length() < 8) {
                Toast.makeText(sign_up.this, "Hãy nhập đúng họ và tên", Toast.LENGTH_SHORT).show();
            } else if (cccd.length() != 12) {
                Toast.makeText(sign_up.this, "CCCD chưa hợp lệ, vui lòng nhập lại", Toast.LENGTH_SHORT).show();
            } else if (!isValidPassword(passWord)) {
                Toast.makeText(sign_up.this, "Mật khẩu phải lớn hơn 8 ký tự và chứa chữ cái, số, ký tự đặc biệt", Toast.LENGTH_SHORT).show();
            } else if (phoneNumber.length() != 10) {
                Toast.makeText(sign_up.this, "Số điện thoại không hợp lệ, vui lòng nhập lại", Toast.LENGTH_SHORT).show();
            } else if (!checkEmail(email)) {
                Toast.makeText(sign_up.this, "Địa chỉ email không hợp lệ", Toast.LENGTH_SHORT).show();
            } else {
                KHACH_HANG kh = new KHACH_HANG(cccd, fullName, phoneNumber, email);
                TAI_KHOAN tk = new TAI_KHOAN(userName, passWord, cccd);

                // Log the objects to ensure they are not null
                Log.d("SIGN_UP", "KHACH_HANG object: " + kh);
                Log.d("SIGN_UP", "TAI_KHOAN object: " + tk);

                ref.child("KHACH_HANG").orderByChild("sdt").equalTo(phoneNumber).get().addOnCompleteListener(phoneTask -> {
                    if (phoneTask.isSuccessful()) {
                        if (phoneTask.getResult().exists()) {
                            Toast.makeText(sign_up.this, "Số điện thoại đã tồn tại", Toast.LENGTH_SHORT).show();
                        } else {
                            ref.child("KHACH_HANG").child(cccd).get().addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    if (task.getResult().exists()) {
                                        Toast.makeText(sign_up.this, "Căn cước công dân đã tồn tại", Toast.LENGTH_SHORT).show();
                                    } else {
                                        ref.child("TAI_KHOAN").orderByChild("tenDn").equalTo(userName).get().addOnCompleteListener(userTask -> {
                                            if (userTask.isSuccessful() && userTask.getResult().exists()) {
                                                Toast.makeText(sign_up.this, "Tên đăng nhập đã tồn tại", Toast.LENGTH_SHORT).show();
                                            } else {
                                                ref.child("KHACH_HANG").orderByChild("email").equalTo(email).get().addOnCompleteListener(emailTask -> {
                                                    if (emailTask.isSuccessful() && emailTask.getResult().exists()) {
                                                        Toast.makeText(sign_up.this, "Email đã tồn tại", Toast.LENGTH_SHORT).show();
                                                    } else {

                                                        if (kh.getCccd() != null && !kh.getCccd().isEmpty()) {
                                                            ref.child("KHACH_HANG").child(kh.getCccd()).setValue(kh).addOnSuccessListener(aVoid -> {
                                                                Log.d("TAG", "Dữ liệu KHACH_HANG đã được đẩy lên Firebase thành công");
                                                                if (tk.getTenDn() != null && !tk.getTenDn().isEmpty()) {
                                                                    ref.child("TAI_KHOAN").child(tk.getTenDn()).setValue(tk).addOnSuccessListener(aVoid2 -> {
                                                                        Log.d("TAG", "Dữ liệu TAI_KHOAN đã được đẩy lên Firebase thành công");
                                                                        Toast.makeText(sign_up.this, "Bạn đã đăng kí thành công", Toast.LENGTH_SHORT).show();
                                                                        Intent i = new Intent(sign_up.this,Login.class);
                                                                        try {
                                                                            Thread.sleep(1000);
                                                                        } catch (
                                                                                InterruptedException e) {
                                                                            throw new RuntimeException(e);
                                                                        }
                                                                        startActivity(i);
                                                                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                                                    }).addOnFailureListener(e -> {
                                                                        Log.e("TAG", "Lỗi khi đẩy dữ liệu TAI_KHOAN lên Firebase: " + e.getMessage());
                                                                    });
                                                                } else {
                                                                    Log.e("TAG", "Giá trị TEN_DN của TAI_KHOAN là null hoặc rỗng");
                                                                }
                                                            }).addOnFailureListener(e -> {
                                                                Log.e("TAG", "Lỗi khi đẩy dữ liệu KHACH_HANG lên Firebase: " + e.getMessage());
                                                            });
                                                        } else {
                                                            Log.e("TAG", "Giá trị CCCD của KHACH_HANG là null hoặc rỗng");
                                                        }
                                                    }
                                                });
                                            }

                                        });
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
    }

    private boolean isValidPassword(String password) {
        boolean hasDigit = password.matches(".*\\d.*");
        boolean hasSpecialChar = password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>].*");
        return hasDigit && hasSpecialChar && password.length() >= 8;
    }

    private boolean checkEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" +
                "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
