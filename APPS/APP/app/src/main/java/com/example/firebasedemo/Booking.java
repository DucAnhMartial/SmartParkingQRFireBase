package com.example.firebasedemo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class Booking extends AppCompatActivity {

    ArrayList<Button> buttonArrayList = new ArrayList<>();
    ImageButton imageButtonExit;
    ArrayList<TextView> textViewArrayList = new ArrayList<>();

    ArrayList<String> maChoList = new ArrayList<>();
    FirebaseDatabase db;
    DatabaseReference ref;
    String username = AccountSingleton.getInstance().getUsername();
    String password = AccountSingleton.getInstance().getPassword();
    String cccd = AccountSingleton.getInstance().getCCCD();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this); // Khởi tạo FirebaseApp
        setContentView(R.layout.activity_booking);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Liên kết
        db = FirebaseDatabase.getInstance();
        ref = db.getReference();
        // Khởi tạo các button và textView
        initButtonsAndTextViews();
        imageButtonExit = findViewById(R.id.imageButton1);
        imageButtonExit.setOnClickListener(v -> finish());
        // Tải dữ liệu từ Firebase và cập nhật trạng thái của các button và textView
        setUpTextViewMaCho();
    }

    // Phương thức để khởi tạo các button và textView
    private void initButtonsAndTextViews() {
        for (int i = 0; i < 10; i++) {
            int textViewId = getResources().getIdentifier("tvA" + (i + 1) + "_Booking", "id", getPackageName());
            int buttonId = getResources().getIdentifier("btnBook" + (i + 1), "id", getPackageName());

            TextView textView = findViewById(textViewId);
            Button button = findViewById(buttonId);

            buttonArrayList.add(button);
            textViewArrayList.add(textView);
        }
    }



    // Tải dữ liệu từ Firebase và thiết lập UI
    private void setUpTextViewMaCho() {
        ref.child("CHO_DO").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    CHO_DO choDo = snapshot.getValue(CHO_DO.class);
                    if (choDo != null) {
                        maChoList.add(choDo.getMaCho());
                        updateButtonState(buttonArrayList.get(maChoList.size()-1),choDo.getTrangThai());
                    }
                }
                setupUI();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Booking.this, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }



    // Phương thức để thiết lập UI sau khi tải dữ liệu
    private void setupUI() {
        showBookingDialog(buttonArrayList, username, maChoList);
        switchStatuesButton(buttonArrayList, textViewArrayList);
    }

    // Hiển thị Dialog View khi nhấn nút
    private void showBookingDialog(ArrayList<Button> buttonArrayList, String username, ArrayList<String> maChoList) {
        for (int i = 0; i < buttonArrayList.size(); i++) {
            final int position = i; // Biến tạm để lưu vị trí của button
            Button button = buttonArrayList.get(i);
            String maCho1 = maChoList.get(i);
            button.setOnClickListener(v -> {
                // Sử dụng biến position thay vì i trong đoạn code bên trong
                final String tempMaCho = maCho1; // Biến tạm để lưu giá trị maCho1
                AlertDialog.Builder builder = new AlertDialog.Builder(Booking.this);
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.model, null);
                builder.setView(dialogView);
                builder.setTitle("BOOKING"); // Tiêu đề của dialog


                Button btnBookNow_model = dialogView.findViewById(R.id.btnBookNow_model);
                TextView textViewMaCho = dialogView.findViewById(R.id.tvSlot_BookNow);
                EditText editBienSo = dialogView.findViewById(R.id.editBienSoXe);
                TimePicker timePicker =dialogView.findViewById(R.id.timePicker);
                // Lấy giờ và phút được chọn từ TimePicker
                int selectedHour = timePicker.getHour();
                int selectedMinute = timePicker.getMinute();

                // Xét chỗ
                textViewMaCho.setText(tempMaCho);

                // Lắng nghe sự kiện khi nhấn nút BOOK NOW
                btnBookNow_model.setOnClickListener(bookNowClickListener(tempMaCho, username, editBienSo,timePicker ));
                // Thiết lập nút "Đóng" cho dialog
                builder.setPositiveButton("Đóng", (dialog, which) -> dialog.dismiss());

                // Khởi tạo và hiển thị AlertDialog
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            });
        }
    }

    private View.OnClickListener bookNowClickListener(String maCho, String username, EditText editBienSoXe, TimePicker timePicker) {
        return v -> {
            String bienSoXe = editBienSoXe.getText().toString(); // Lấy giá trị từ EditText
            // Lấy thời gian hiện tại
            Calendar currentTime = Calendar.getInstance();
            int currentHour = currentTime.get(Calendar.HOUR_OF_DAY);
            int currentMinute = currentTime.get(Calendar.MINUTE);

            // Lấy giờ và phút được chọn từ TimePicker
            int selectedHour = timePicker.getHour();
            int selectedMinute = timePicker.getMinute();

            // Tính toán thời gian được chọn so với thời gian hiện tại
            int selectedTotalMinutes = selectedHour * 60 + selectedMinute;
            int currentTotalMinutes = currentHour * 60 + currentMinute;
            int timeDifference = selectedTotalMinutes - currentTotalMinutes;

            if (bienSoXe.isEmpty() || timeDifference <= 30) {
                Toast.makeText(Booking.this, "Vui lòng nhập đầy đủ thông tin và chọn thời gian lớn hơn 30 phút", Toast.LENGTH_SHORT).show();
            } else {
                String gioRa = String.format("%02d:%02d", selectedHour, selectedMinute);
                String gioVao = String.format("%02d:%02d", currentHour, currentMinute);

                String MaVe = (maCho + "/" + Math.abs(selectedMinute + selectedHour * 60 - currentMinute - currentHour * 60)).toString();
                AddNewCar(bienSoXe);
                addTicKet(MaVe, cccd, bienSoXe, gioVao, gioRa, maCho, (5000 * timeDifference));
                Intent intent = new Intent(Booking.this,Booking.class);
                ref.child("CHO_DO").child(maCho).child("trangThai").setValue("Full");
                updateButtonState(buttonArrayList.get(maChoList.indexOf(maCho)), "Full");
                Toast.makeText(this, "Bạn đã đăng ký thành công", Toast.LENGTH_SHORT).show();
                finish();
                startActivity(intent);
            }
        };
    }



    // Method để khởi tạo Spinner
    private void initSpinnerMinutes(Spinner spinner) {
        String[] items = new String[60];
        for (int i = 0; i < 60; i++) {
            items[i] = String.valueOf(i);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(Booking.this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void initSpinnerHours(Spinner spinner) {
        String[] items = new String[24];
        for (int i = 0; i < 24; i++) {
            items[i] = String.valueOf(i);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(Booking.this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    // Cập nhật trạng thái của các button và textView
    protected void switchStatuesButton(ArrayList<Button> buttonArrayList, ArrayList<TextView> textViewArrayList) {
        for (int i = 0; i < maChoList.size(); i++) {
            textViewArrayList.get(i).setText(maChoList.get(i));
        }

        ref.child("CHO_DO").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    CHO_DO choDo = snapshot.getValue(CHO_DO.class);
                    if (choDo != null) {
                        updateButtonState(buttonArrayList.get(maChoList.indexOf(choDo.getMaCho())), choDo.getTrangThai());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Booking.this, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Phương thức cập nhật trạng thái button
    protected void updateButtonState(Button button, String trangThai) {
        if (trangThai.equals("Full")) {
            button.setText("Full");
            button.setEnabled(false);
        } else {
            button.setText("Book");
            button.setEnabled(true);
        }
    }

    // Phương thức thêm xe
    private void AddNewCar(String bienSoXe) {
        XE xe = new XE(bienSoXe,"");
        ref.child("XE").child(xe.getBienSo()).setValue(xe);
    }

    //Phương thức
    private void addTicKet(String mave, String cccd, String biensoxe, String gioVao, String gioRa, String macho, double tongtien) {
        VE ve = new VE(mave, gioRa, gioVao, tongtien, biensoxe, cccd, macho);
        ref.child("VE").child(ve.getMaVe()).setValue(ve);
    }


    // Phương thức cập nhật trạng thái chỗ
    private void updateSlotStatus(String maCho) {
        // Implement your logic here
    }
}
