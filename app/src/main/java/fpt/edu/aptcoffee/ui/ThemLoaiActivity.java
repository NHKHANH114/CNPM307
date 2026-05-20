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

public class ThemLoaiActivity extends AppCompatActivity implements View.OnClickListener{
    private static final int PICK_IMAGE = 1;
    boolean pickImageSatus = false;

    ImageView ivBack, ivHinhAnh, ivPickImage;
    TextInputLayout tilTenLoai;
    Button btnAdd;
    LoaiHangDAO loaiHangDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_them_loai);
        initView();

        loaiHangDAO = new LoaiHangDAO(this);

        ivBack.setOnClickListener(this);
        ivPickImage.setOnClickListener(this);
        btnAdd.setOnClickListener(this);

    }

    private void initView() {
        ivBack = findViewById(R.id.ivBack);
        ivHinhAnh = findViewById(R.id.ivHinhAnh);
        ivPickImage = findViewById(R.id.ivPickImage);
        tilTenLoai = findViewById(R.id.tilTenLoaiHang);
        btnAdd = findViewById(R.id.btnAdd);
    }

    private void addProduct() {
        // Thêm loại hàng mới
        if (!pickImageSatus) {
            MyToast.error(ThemLoaiActivity.this, "Vui lòng chọn ảnh");
        } else {
            String tenLoai = Objects.requireNonNull(tilTenLoai.getEditText()).getText().toString().trim();
            if (tenLoai.isEmpty()) {
                MyToast.error(ThemLoaiActivity.this, "Vui lòng nhập tên loại");
            } else {
                LoaiHang loaiHang = new LoaiHang();
                loaiHang.setTenLoai(tenLoai);
                loaiHang.setHinhAnh(ImageToByte.imageViewToByte(getApplicationContext(), ivHinhAnh));
                if (loaiHangDAO.insertLoaiHang(loaiHang)) {
                    MyToast.successful(ThemLoaiActivity.this, "Thêm thành công");

                    tilTenLoai.getEditText().setText("");
                    ivHinhAnh.setImageResource(R.drawable.pick_image1);
                    pickImageSatus = false;
                } else {
                    MyToast.error(ThemLoaiActivity.this, "Thêm không thành công");
                }
            }
        }
    }

    private void pickImage() {
        // Chọn hình trong Phone
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            // cấp quyền cho ứng dụng
            ActivityCompat.requestPermissions(ThemLoaiActivity.this,
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
                pickImageSatus = true;
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
            case R.id.btnAdd:
                addProduct();
                break;
        }
    }
}