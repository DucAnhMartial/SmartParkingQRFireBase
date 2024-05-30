package com.example.firebasedemo;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.firebasedemo.zalopaymentandroid.Helper.CreateOrder;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import org.json.JSONObject;

import vn.zalopay.sdk.Environment;
import vn.zalopay.sdk.ZaloPayError;
import vn.zalopay.sdk.ZaloPaySDK;
import vn.zalopay.sdk.listeners.PayOrderListener;

public class Personal extends AppCompatActivity {
    TextView tvName_Personal, tvCCCD_Personal, tvSDT_Personal, tvSoCho_Personal, tvslot_ticket, tvBienso_ticket, tvgiovao_ticket, tvgiora_ticket;
    ImageView img1;
    LinearLayout linearLayoutTickets;
    Button btn_ticket_personal_qr,btn_ticket_personal_invoice;
    int socho = 0;
    String qrcode1 = "";

    private FirebaseDatabase db;
    private DatabaseReference ref;

    String username = AccountSingleton.getInstance().getUsername();
    String password = AccountSingleton.getInstance().getPassword();
    String cccd = AccountSingleton.getInstance().getCCCD();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this); // Khởi tạo FirebaseApp
        setContentView(R.layout.activity_personal);


        //zalo pay
        StrictMode.ThreadPolicy policy = new
                StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // ZaloPay SDK Init
        ZaloPaySDK.init(2553, Environment.SANDBOX);

        // Khởi tạo linearLayoutTickets sau khi setContentView
        linearLayoutTickets = findViewById(R.id.linear_layout_tickets);
        db = FirebaseDatabase.getInstance();
        ref = db.getReference();
        tvName_Personal = findViewById(R.id.tvName_Personal);
        tvCCCD_Personal = findViewById(R.id.tvCCCD_Personal);
        tvSDT_Personal = findViewById(R.id.tvSDT_Personal);
        tvSoCho_Personal = findViewById(R.id.tvSoCho_Personal);
        btn_ticket_personal_qr = linearLayoutTickets.findViewById(R.id.btn_ticket_personal_qr);
        btn_ticket_personal_invoice = linearLayoutTickets.findViewById(R.id.btn_ticket_personal_invoice);

        img1 = findViewById(R.id.img1);
        img1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Personal.this,AfterLogin.class);
                startActivity(i);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });
        truyvanKhachHang();

    }
    //Phương thức đếm tổng vé
    private void countVe() {
        ref.child("VE").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count = 0;
                for (DataSnapshot veSnapshot : snapshot.getChildren()) {
                    for (DataSnapshot maVeSnapshot : veSnapshot.getChildren()) {
                        String cccdVe = maVeSnapshot.child("cccd").getValue(String.class);
                        if (cccdVe != null && cccdVe.equals(cccd)) {

                            // Tùy chỉnh thông tin vé trên View này
                            View ticketView = getLayoutInflater().inflate(R.layout.ticket_personal, null);
                            TextView vitri = ticketView.findViewById(R.id.edt_ticket_personal_slot);
                            TextView bienso = ticketView.findViewById(R.id.edt_ticket_personal_bienso);
                            TextView giovao = ticketView.findViewById(R.id.edt_ticket_personal_gio_vao);
                            TextView giora = ticketView.findViewById(R.id.edt_ticket_personal_gio_ra);
                            Button btnHuy = ticketView.findViewById(R.id.btn_ticket_personal_huy);
                            Button btnQr = ticketView.findViewById(R.id.btn_ticket_personal_qr);
                            Button invoice = ticketView.findViewById(R.id.btn_ticket_personal_invoice);
                            TextView ve = ticketView.findViewById(R.id.Ve);
                            // Lấy dữ liệu từ DataSnapshot và thiết lập cho các TextView tương ứng
                            String ve1 = maVeSnapshot.child("maVe").getValue(String.class);
                            String bienSoXe = maVeSnapshot.child("bienSo").getValue(String.class);
                            String gioVao = maVeSnapshot.child("gioVao").getValue(String.class);
                            String gioRa = maVeSnapshot.child("gioRa").getValue(String.class);
                            String macho = maVeSnapshot.child("maCho").getValue(String.class);
                            Long tien = maVeSnapshot.child("tongTien").getValue(Long.class);
                            ve.setText(ve1);
                            bienso.setText(bienSoXe);
                            giovao.setText(gioVao);
                            giora.setText(gioRa);
                            vitri.setText(macho);
                            // Thêm sự kiện cho nút hủy vé
                            // Thêm sự kiện cho nút hủy vé
                            btnHuy.setOnClickListener(v -> {
                                // Xác nhận xóa vé trước khi thực hiện
                                AlertDialog.Builder builder = new AlertDialog.Builder(Personal.this);
                                builder.setMessage("Bạn có chắc chắn muốn hủy vé này?")
                                        .setPositiveButton("Có", (dialog, which) -> {
                                            // Xóa vé từ cơ sở dữ liệu
                                            String maVeToRemove = maVeSnapshot.getKey();
                                            ref.child("VE").child(veSnapshot.getKey()).child(maVeToRemove).removeValue()
                                                    .addOnSuccessListener(aVoid -> {
                                                        // Xóa vé thành công, cập nhật lại giao diện
                                                        linearLayoutTickets.removeView(ticketView);
                                                        Toast.makeText(getApplicationContext(), "Đã hủy vé thành công", Toast.LENGTH_SHORT).show();

                                                        // Cập nhật trạng thái của chỗ đỗ từ "Full" thành "Empty"
                                                        ref.child("CHO_DO").child(veSnapshot.getKey()).child("trangThai").setValue("Empty")
                                                                .addOnSuccessListener(aVoid1 -> {
                                                                    // Cập nhật trạng thái thành công
                                                                })
                                                                .addOnFailureListener(e -> {
                                                                    // Xảy ra lỗi khi cập nhật trạng thái
                                                                    Toast.makeText(getApplicationContext(), "Xảy ra lỗi khi cập nhật trạng thái chỗ đỗ", Toast.LENGTH_SHORT).show();
                                                                });
                                                    })
                                                    .addOnFailureListener(e -> {
                                                        // Xảy ra lỗi khi xóa vé
                                                        Toast.makeText(getApplicationContext(), "Xảy ra lỗi khi hủy vé", Toast.LENGTH_SHORT).show();
                                                    });
                                        })
                                        .setNegativeButton("Không", (dialog, which) -> dialog.dismiss())
                                        .show();
                            });

                            linearLayoutTickets.addView(ticketView);
                            count++;
                            btnQr.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    try {

                                        Bitmap qr = generateQRCode(maVeSnapshot.child("maVe").getValue(String.class),500,500);
                                        showQRCodeDialog(qr);
                                    } catch (WriterException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            });
                            invoice.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {


                                    invoice.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            // Tạo dialog và thiết lập layout cho dialog
                                            Dialog dialog = new Dialog(Personal.this);
                                            dialog.setContentView(R.layout.ticket_pay_zalo);

                                            // Thiết lập các TextView và Button từ layout của dialog
                                            TextView tvMaVe = dialog.findViewById(R.id.tvMaVe);
                                            TextView tvBienSo = dialog.findViewById(R.id.tvBienSo);
                                            TextView tvGioVao = dialog.findViewById(R.id.tvGioVao);
                                            TextView tvGioRa = dialog.findViewById(R.id.tvGioRa);
                                            TextView tvMaCho = dialog.findViewById(R.id.tvMaCho);
                                            TextView tvTongTien = dialog.findViewById(R.id.tvTongTien);
                                            Button btnThanhToanZaloPay = dialog.findViewById(R.id.btnThanhToanZaloPay);

                                            // Thiết lập kích thước cho dialog
                                            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                                            lp.copyFrom(dialog.getWindow().getAttributes());
                                            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                                            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                                            dialog.getWindow().setAttributes(lp);

                                            // Thiết lập thông tin vé
                                            tvMaVe.setText("Mã vé: " + ve1);
                                            tvBienSo.setText("Biển số xe: " + bienSoXe);
                                            tvGioVao.setText("Giờ vào: " + gioVao);
                                            tvGioRa.setText("Giờ ra: " + gioRa);
                                            tvMaCho.setText("Mã chỗ: " + macho);
                                            tvTongTien.setText("Tổng tiền: " + tien+" VND");

                                            // Thiết lập sự kiện click cho nút thanh toán ZaloPay
                                            btnThanhToanZaloPay.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    // Gọi phương thức RequestZalo với số tiền cần thanh toán
                                                    RequestZalo(tien);
                                                    dialog.dismiss();
                                                }
                                            });

                                            // Hiển thị dialog
                                            dialog.show();
                                        }
                                    });



                                }
                            });
                        }
                    }
                }
                tvSoCho_Personal.setText(String.valueOf(count));

                Log.d("CCCD_Count", "Số lần xuất hiện của CCCD trong VE: " + count);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("CountVe", "Failed to count ve", error.toException());
            }
        });
    }

    //Khách Hàng
    private void truyvanKhachHang(){
        ref.child("KHACH_HANG").child(cccd).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    tvName_Personal.setText(snapshot.child("hoTen").getValue(String.class));
                    tvCCCD_Personal.setText(snapshot.child("cccd").getValue(String.class));
                    tvSDT_Personal.setText(snapshot.child("sdt").getValue(String.class));
                    countVe();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private Bitmap generateQRCode(String data, int width, int height) throws WriterException {
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        BitMatrix bitMatrix = multiFormatWriter.encode(data, BarcodeFormat.QR_CODE, width, height);
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
            }
        }

        return bitmap;
    }

    // Method to show QR code in a dialog
    private void showQRCodeDialog(Bitmap qrCodeBitmap) {
        // Create dialog

        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.qrcode);
        dialog.setTitle("QR mã vé");

        ImageView imageViewQRCode = dialog.findViewById(R.id.imageView_qr_code);


        imageViewQRCode.setImageBitmap(qrCodeBitmap);


        dialog.show();
    }
    protected void updateButtonState(Button button, String trangThai) {
        if (trangThai.equals("Full")) {
            button.setText("Full");
            button.setEnabled(false);
        } else {
            button.setText("Book");
            button.setEnabled(true);
        }
    }

    //Request zalo
// Phương thức Request zalo
    private void RequestZalo(Long gia) {
        CreateOrder orderApi = new CreateOrder();

        try {
            // Tạo đơn hàng từ API
            JSONObject data = orderApi.createOrder(String.valueOf(gia));

            // Kiểm tra xem trường "return_code" có tồn tại trong JSON không
            if (data.has("return_code")) {
                String code = data.getString("return_code");

                // Kiểm tra nếu return_code bằng 1 (thành công)
                if (code.equals("1")) {
                    // Lấy mã token từ đơn hàng
                    String token = data.getString("zp_trans_token");

                    // Thực hiện thanh toán với ZaloPaySDK
                    ZaloPaySDK.getInstance().payOrder(Personal.this, token, "demozpdk://app", new PayOrderListener() {
                        @Override
                        public void onPaymentSucceeded(String transactionId, String transToken, String appTransID) {
                            // Xử lý khi thanh toán thành công
                            Toast.makeText(Personal.this, "Thanh toán thành công", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onPaymentCanceled(String zpTransToken, String appTransID) {
                            // Xử lý khi thanh toán bị hủy
                            Toast.makeText(Personal.this, "Thanh toán bị hủy", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onPaymentError(ZaloPayError zaloPayError, String zpTransToken, String appTransID) {
                            // Lấy thông điệp mô tả lỗi từ ZaloPayError
//                            String errorMessage = zaloPayError.getMessage();

                            // Ghi thông điệp lỗi vào log
//                            Log.e("ZaloPayError", errorMessage);
                            Toast.makeText(Personal.this, "Thanh toán thất bại", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    // Xử lý khi return_code không phải là 1
                    // Có thể thêm các xử lý khác tại đây nếu cần
                }
            } else {
                // Xử lý khi không tìm thấy trường "return_code" trong JSON
                // Có thể thêm các xử lý khác tại đây nếu cần
            }

        } catch (Exception e) {
            e.printStackTrace();
            // Xử lý khi có lỗi xảy ra trong quá trình thực hiện request
        }
    }


}