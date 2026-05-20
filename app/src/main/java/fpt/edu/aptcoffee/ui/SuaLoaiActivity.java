package fpt.edu.aptcoffee.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.material.textfield.TextInputLayout;

import java.io.InputStream;
import java.util.Objects;

import fpt.edu.aptcoffee.R;
import fpt.edu.aptcoffee.dao.LoaiHangDAO;
import fpt.edu.aptcoffee.model.LoaiHang;
import fpt.edu.aptcoffee.utils.ImageToByte;
import fpt.edu.aptcoffee.utils.MyToast;

public class SuaLoaiActivity extends AppCompatActivity implements View.OnClickListener{
    private static final int PICK_IMAGE = 1;
    ImageView ivBack, ivHinhAnh, ivPickImage;
    TextInputLayout tilTenLoai, tilMaLoai;
    Button btnUpdate;
    LoaiHangDAO loaiHangDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sua_loai);
        initView();
        loaiHangDAO = new LoaiHangDAO(this);
        fillData();
        ivBack.setOnClickListener(this);
        ivPickImage.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);

    }

    private void initView() {
        ivBack = findViewById(R.id.ivBack);
        ivHinhAnh = findViewById(R.id.ivHinhAnh);
        ivPickImage = findViewById(R.id.ivPickImage);
        tilTenLoai = findViewById(R.id.tilTenLoaiHang);
        tilMaLoai = findViewById(R.id.tilMaLoaiHang);
        btnUpdate = findViewById(R.id.btnUpdate);
    }

    private void fillData() {
        // Set data lên activity
        LoaiHang loaiHang = getLoaiHang();
        Bitmap bitmap = BitmapFactory.decodeByteArray(loaiHang.getHinhAnh(),
                0,
                loaiHang.getHinhAnh().length);
        ivHinhAnh.setImageBitmap(bitmap);
        Objects.requireNonNull(tilMaLoai.getEditText()).setText(String.valueOf(loaiHang.getMaLoai()));
        Objects.requireNonNull(tilTenLoai.getEditText()).setText(loaiHang.getTenLoai());
    }

    private LoaiHang getLoaiHang() {
        // Lấy đối tượng LoaiHang
        Intent intent = getIntent();
        return loaiHangDAO.getByMaLoai(intent.getStringExtra(LoaiThucUongActivity.MA_LOAI_HANG));
    }


    private void updateProduct() {
        // Cập nhật loại hàng
        LoaiHang loaiHang = getLoaiHang();
        String tenLoai = Objects.requireNonNull(tilTenLoai.getEditText()).getText().toString().trim();
        if (tenLoai.isEmpty()) {
            MyToast.error(SuaLoaiActivity.this, "Vui lòng nhập tên loại");
        } else {
            loaiHang.setTenLoai(tenLoai);
            loaiHang.setHinhAnh(ImageToByte.imageViewToByte(getApplicationContext(), ivHinhAnh));
            if (loaiHangDAO.updateLoaiHang(loaiHang)) {
                MyToast.successful(SuaLoaiActivity.this, "Cập nhật thành công");
            } else {
                MyToast.error(SuaLoaiActivity.this, "Thêm không thành công");
            }
        }
    }

    private void pickImage() {
        // Chọn ảnh trong Phone
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            // cấp quyền cho ứng dụng
            ActivityCompat.requestPermissions(SuaLoaiActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    1);
        } else {

            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.anim_in_left, R.anim.anim_out_right);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                InputStream stream = getApplicationContext().getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(stream);
                ivHinhAnh.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.getMessage();
            }
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ivBack:
                onBackPressed();
                break;
            case R.id.ivPickImage:
                pickImage();
                break;
            case R.id.btnUpdate:
                updateProduct();
                break;
        }
    }
}