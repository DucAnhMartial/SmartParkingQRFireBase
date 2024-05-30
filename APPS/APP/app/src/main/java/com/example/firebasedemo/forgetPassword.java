package com.example.firebasedemo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class forgetPassword extends AppCompatActivity {
    TextView sendcode;
    Button btnQuayLai, btnKPMK;
    EditText edtEmail_forgerPass, edtPhonenumber_forgetPassword, edtCCCD_forgetPass, edtverifycode;

    //Tạo lịch kiểm tra
    FirebaseDatabase db;
    DatabaseReference ref;
    private Handler schedule = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this); // Khởi tạo FirebaseApp
        setContentView(R.layout.activity_forget_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        db = FirebaseDatabase.getInstance();
        ref = db.getReference();
        sendcode = findViewById(R.id.sendcode);
        btnQuayLai = findViewById(R.id.btnQuayLai);
        btnKPMK = findViewById(R.id.btnKPMK);
        edtPhonenumber_forgetPassword = findViewById(R.id.edtPhonenumber_forgetPassword);
        edtCCCD_forgetPass = findViewById(R.id.edtCCCD_forgetPass);
        edtverifycode = findViewById(R.id.edtverifycode);
        edtEmail_forgerPass = findViewById(R.id.edtEmail_forgerPass);


        selectInformUser();
        sendcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long lastClickTime = 0;
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastClickTime < 3000) { // Nếu thời gian hiện tại trừ thời gian nhấn lần trước ít hơn 30 giây
                    Toast.makeText(forgetPassword.this, "Vui lòng chờ 30 giây để gửi lại mã", Toast.LENGTH_SHORT).show();
                } else {
                    lastClickTime = currentTime; // Cập nhật thời gian nhấn nút
                    String code = generatorCode();

                    sencodoTo(code);

                    sendcode.setEnabled(false); // Vô hiệu hóa nút trong 30 giây
                    schedule.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            sendcode.setEnabled(true); // Kích hoạt lại nút sau 30 giây
                        }
                    }, 3000); // 30,000 milliseconds = 30 seconds
                }
            }
        });
        btnQuayLai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(forgetPassword.this, Login.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();




            }
        });
    }

    public class SendEmailTask extends AsyncTask<Void, Void, Void> {

        private Context mContext;
        private Session mSession;
        private String mEmail;

        private String mCode;
        private ProgressDialog mProgressDialog;

        // Constructor
        public SendEmailTask(Context context, String email, String verifyCode) {
            mContext = context;
            mEmail = email;
            mCode = verifyCode;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Show progress dialog while sending email
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // Gán giá trị của ProgressDialog cho biến mProgressDialog
                    mProgressDialog = ProgressDialog.show(mContext, "Sending message", "Please wait...", false, false);
                }
            });
        }


        @Override
        protected Void doInBackground(Void... params) {
            // Creating properties
            Properties props = new Properties();
            // Bật STARTTLS để sử dụng port 587
            props.put("mail.smtp.starttls.enable", "true");
            //Xác thực người dùng
            props.put("mail.smtp.auth", "true");
            // Configuring properties for Gmail
            props.put("mail.smtp.host", "smtp.office365.com");
            props.put("mail.smtp.port", "587");

//            props.put("mail.smtp.socketFactory.port", "587");

            //Phiên bản của giao thức
            props.put("mail.smtp.ssl.protocols", "TLSv1.2");


            // Creating a new session
            mSession = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
                // Authenticating the password
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication("huutruong0379@gmail.com", "Dangcongsan#123");
                }
            });

            try {
                // Creating MimeMessage object
                MimeMessage mm = new MimeMessage(mSession);

                // Setting sender address
                mm.setFrom(new InternetAddress("huutruong0379@gmail.com"));
                // Adding receiver
                mm.addRecipient(Message.RecipientType.TO, new InternetAddress(mEmail));
                // Setting subject
                mm.setSubject("Khôi phục mật khẩu");
                // Setting message
                mm.setText("Mã xác nhận của bạn là: " + mCode);

                // Sending email
                Transport.send(mm);
            } catch (MessagingException e) {
                Log.e("SendEmailTask", "Lỗi gửi mã xác nhận", e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
            }
            Toast.makeText(mContext, "Mã xác nhận đã được gửi,Nếu chưa nhận được mã vui lòng chờ 30s tiếp theo", Toast.LENGTH_SHORT).show();
        }
    }


    private void selectInformUser() {
        btnKPMK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = edtPhonenumber_forgetPassword.getText().toString();
                String cccdNuber = edtCCCD_forgetPass.getText().toString();
                String code = edtverifycode.getText().toString();
                String emailN = edtEmail_forgerPass.getText().toString();
                if (phoneNumber.isEmpty() || cccdNuber.isEmpty() || code.isEmpty() || emailN.isEmpty()) {
                    Toast.makeText(forgetPassword.this, "Vui Lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                } else {
                    Date currenttime = new Date();
                    checkTimeFinish(emailN, phoneNumber, cccdNuber,code, currenttime);
                }
            }
        });
    }

    // Hàm để chuyển đổi chuỗi sang đối tượng Date
    private Date Chuyendoithoigian(String dateString) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
            return dateFormat.parse(dateString);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Kiểm tra thời gian hết hạn của mã


    private void checkTimeFinish(String emailN, String phoneNumber, String cccdNuber, String code, Date currenttime) {
        ref.child("KHACH_HANG").orderByChild("email").equalTo(emailN).get().addOnCompleteListener(emailTask -> {
            if (emailTask.isSuccessful()) {
                DataSnapshot emailSnapshot = emailTask.getResult();
                if (emailSnapshot.exists()) {
                    boolean phoneNumberMatch = false;
                    boolean cccdMatch = false;
                    for (DataSnapshot child : emailSnapshot.getChildren()) {
                        String sdt = child.child("sdt").getValue(String.class);
                        String cccd = child.child("cccd").getValue(String.class);
                        if (sdt != null && sdt.equals(phoneNumber)) {
                            phoneNumberMatch = true;
                        }
                        if (cccd != null && cccd.equals(cccdNuber)) {
                            cccdMatch = true;
                        }
                    }
                    if (phoneNumberMatch && cccdMatch) {
                        ref.child("VERIFY_CODE").orderByChild("code").equalTo(code).get().addOnCompleteListener(codeTask -> {
                            if (codeTask.isSuccessful()) {
                                DataSnapshot codeSnapshot = codeTask.getResult();
                                if (codeSnapshot.exists()) {
                                    for (DataSnapshot data : codeSnapshot.getChildren()) {
                                        String tgian = data.child("tgianhethan").getValue(String.class);
                                        if (tgian != null) {
                                            Date expirationTime = Chuyendoithoigian(tgian);
                                            if (expirationTime != null) {
                                                long currentTimeMillis = currenttime.getTime();
                                                long expirationTimeMillis = expirationTime.getTime();
                                                long timeDifferenceMillis = expirationTimeMillis - currentTimeMillis;
                                                long minutesDifference = timeDifferenceMillis / (60 * 1000);
                                                if (minutesDifference < 0 || minutesDifference >= 30) {
                                                    Toast.makeText(forgetPassword.this, "Mã code đã hết hạn", Toast.LENGTH_SHORT).show();
                                                    Log.d("Firebase", "Verification code expired");
                                                } else {
                                                    showResetPasswordDialog(cccdNuber);
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    Toast.makeText(this, "Mã code không hợp lệ hoặc đã hết hạn", Toast.LENGTH_SHORT).show();
                                    Log.d("Firebase", "Verification code does not exist");
                                }
                            } else {
                                Log.e("Firebase", "Error getting verification code: " + codeTask.getException().getMessage());
                            }
                        });
                    } else {
                        if (!phoneNumberMatch) {
                            Toast.makeText(this, "Số điện thoại chưa được đăng ký hoặc không trùng Khớp", Toast.LENGTH_SHORT).show();
                        }
                        if (!cccdMatch) {
                            Toast.makeText(this, "CCCD chưa được đăng ký hoặc không trùng Khớp", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(this, "Email chưa được đăng ký hoặc không trùng Khớp", Toast.LENGTH_SHORT).show();
                    Log.d("Firebase", "Email does not exist");
                }
            } else {
                Log.e("Firebase", "Error getting email: " + emailTask.getException().getMessage());
            }
        }).addOnFailureListener(e -> {
            Log.e("Firebase", "Email task failed: " + e.getMessage());
        }).addOnCanceledListener(() -> {
            Log.d("Firebase", "Email task canceled.");
        });
    }


    private void showResetPasswordDialog(String cccdNuber) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.activity_reset_pass_word, null);
                Button btn_ok = dialogView.findViewById(R.id.btn_ok);
                Button btn_huy = dialogView.findViewById(R.id.btn_huy);
                EditText edt_new_pass = dialogView.findViewById(R.id.edt_new_pass);
                EditText edt_new_pass2 = dialogView.findViewById(R.id.edt_new_pass2);
                AlertDialog.Builder builder = new AlertDialog.Builder(forgetPassword.this);
                builder.setView(dialogView);
                AlertDialog dialog = builder.create();
                dialog.show();
                btn_huy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                btn_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String pass1 = edt_new_pass.getText().toString();
                        String pass2 = edt_new_pass2.getText().toString();
                        if (edt_new_pass.getText().toString().isEmpty() || edt_new_pass2.getText().toString().isEmpty()) {
                            Toast.makeText(forgetPassword.this, "Hãy nhập dữ liệu", Toast.LENGTH_SHORT).show();
                            return;
                        } else if (!pass1.equals(pass2)) {
                            Toast.makeText(forgetPassword.this, "Mật khẩu không khớp, hãy nhập lại", Toast.LENGTH_SHORT).show();
                            edt_new_pass.setText("");
                            edt_new_pass2.setText("");
                        } else if (!isValidPassword(edt_new_pass.getText().toString()) || edt_new_pass.getText().toString().length() < 8 || !isValidPassword(edt_new_pass2.getText().toString()) || edt_new_pass2.getText().toString().length() < 8) {
                            Toast.makeText(forgetPassword.this, "Mật khẩu phải lớn hơn 8 ký tự và chứa chữ cái, số, ký tự đặc biệt", Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            UpdateMK(cccdNuber,pass2);
                            Toast.makeText(forgetPassword.this, "Mật khẩu được đổi thành công", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(forgetPassword.this, Login.class);
                            try {
                                Thread.sleep(3000);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                            startActivity(i);
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            finish();

                            dialog.dismiss();
                        }
                    }
                });
            }
        });

    }
    //update MatKhau
    private void UpdateMK(String cccd, String newpass) {
        ref.child("TAI_KHOAN").orderByChild("cccd").equalTo(cccd).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot dataSnapshot = task.getResult();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        String key = childSnapshot.getKey();
                        if (key != null) {
                            ref.child("TAI_KHOAN").child(key).child("matKhau").setValue(newpass)
                                    .addOnCompleteListener(updateTask -> {
                                        if (updateTask.isSuccessful()) {
                                            Log.d("Firebase", "Password updated successfully");
                                        } else {
                                            Log.e("Firebase", "Error updating password: " + updateTask.getException().getMessage());
                                        }
                                    });
                        } else {
                            Log.d("updateMK", "Key for CCCD not found");
                        }
                    }
                } else {
                    Log.d("updateMK", "CCCD does not exist");
                }
            } else {
                Log.e("Firebase", "Error getting CCCD: " + task.getException().getMessage());
            }
        }).addOnFailureListener(e -> {
            Log.e("Firebase", "CCCD task failed: " + e.getMessage());
        }).addOnCanceledListener(() -> {
            Log.d("Firebase", "CCCD task canceled.");
        });
    }

    //Ràng buộc mật khẩu
    private boolean isValidPassword(String password) {
        boolean hasDigit = password.matches(".*\\d.*"); //Kiểm tra chuỗi có chứa số hay không
        boolean hasSpecialChar = password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*");
        boolean hasLowercase = password.matches(".*[a-z].*");
        return hasDigit && hasSpecialChar && hasLowercase;
    }

    private void sencodoTo(String verifyCode) {
        String emailInput = edtEmail_forgerPass.getText().toString();
        if (emailInput.isEmpty()) {
            runOnUiThread(() -> Toast.makeText(forgetPassword.this, "Vui lòng nhập email", Toast.LENGTH_SHORT).show());
            return;
        } else if (!checkEmail(emailInput)) {
            Toast.makeText(this, "Vui lòng nhập đúng định dạng email", Toast.LENGTH_SHORT).show();
            return;
        }

        Date currentTime = new Date();
        Date thoigian = Chuyendoithoigian(String.valueOf(currentTime));

        verifyCode vr = new verifyCode(verifyCode,emailInput , thoigian.toString());



        ref.child("KHACH_HANG").orderByChild("email").equalTo(emailInput).get().addOnCompleteListener(emailTask -> {
            if (emailTask.isSuccessful()) {
                if (emailTask.getResult().exists()) {
                    new SendEmailTask(forgetPassword.this, emailInput, verifyCode).execute();
                    ref.child("VERIFY_CODE").child(vr.getCode()).setValue(vr)
                            .addOnCompleteListener(codeTask -> {
                                if (codeTask.isSuccessful()) {

                                } else {
                                    // Xảy ra lỗi khi gửi mã xác nhận
                                    Log.e("Error", "Lỗi khi gửi mã xác nhận: " + codeTask.getException().getMessage());
                                    Toast.makeText(forgetPassword.this, "Đã xảy ra lỗi khi gửi mã xác nhận", Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    // Email chưa được đăng ký
                    Toast.makeText(forgetPassword.this, "Email chưa được đăng ký", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Xảy ra lỗi khi truy vấn email
                Log.e("Error", "Lỗi khi truy vấn email: " + emailTask.getException().getMessage());
                Toast.makeText(forgetPassword.this, "Đã xảy ra lỗi khi kiểm tra email", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void checkDuplicate(String code, CodeGenerationListener listener) {
    ref.child("VERIFY_CODE").orderByChild("code").equalTo(code).get().addOnCompleteListener(codeTask -> {
            if (codeTask.isSuccessful()) {
                if (codeTask.getResult().exists()) {
                    // Mã code đã tồn tại, tạo mã mới và kiểm tra lại
                    String newCode = generatorCode();
                    checkDuplicate(newCode, listener);
                } else {
                    // Mã code không tồn tại, trả về mã code mới qua listener
                    listener.onCodeGenerated(code);
                }
            } else {
                // Xảy ra lỗi khi truy vấn mã code
                Log.e("Error", "Error getting code: " + codeTask.getException().getMessage());
            }
        });
    }

    // Interface để lắng nghe sự kiện khi mã code được tạo ra không trùng lặp
    interface CodeGenerationListener {
        void onCodeGenerated(String code);
    }




    // Tạo mã code
    private String generatorCode() {
        String CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZqwertyuiopasdfghjklzxcvbnm";
        int code_length = 6;
        Random randomCode = new Random();
        StringBuilder newCode = new StringBuilder();
        for (int i = 0; i < code_length; i++) {
            int index = randomCode.nextInt(CHARACTERS.length());
            newCode.append(CHARACTERS.charAt(index));
        }
        return newCode.toString();
    }


    private boolean checkEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" +
                "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

}