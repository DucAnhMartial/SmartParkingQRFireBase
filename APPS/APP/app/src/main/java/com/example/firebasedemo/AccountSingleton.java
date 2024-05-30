package com.example.firebasedemo;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AccountSingleton {
    private static AccountSingleton instance;
    private String username;
    private String password;
    private String cccd;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private AccountSingleton() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("TAI_KHOAN");
    }

    public static AccountSingleton getInstance() {
        if (instance == null) {
            instance = new AccountSingleton();
        }
        return instance;
    }

    public void setAccountInfo(String username, String password) {
        this.username = username;
        this.password = password;
        // Lấy số CCCD từ Firebase khi thiết lập thông tin tài khoản
        queryCCCDFromFirebase();
    }

    private void queryCCCDFromFirebase() {
        // Thực hiện truy vấn Firebase để lấy số CCCD dựa trên tên đăng nhập
        databaseReference.child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Kiểm tra xem dữ liệu có tồn tại không
                if (dataSnapshot.exists()) {
                    // Lấy số CCCD từ dataSnapshot
                    cccd = dataSnapshot.child("cccd").getValue(String.class);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Xử lý khi truy vấn bị hủy bỏ
            }
        });
    }
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getCCCD() {
        return cccd;
    }

}
