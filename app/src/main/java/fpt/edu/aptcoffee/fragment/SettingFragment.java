package fpt.edu.aptcoffee.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import fpt.edu.aptcoffee.MainActivity;
import fpt.edu.aptcoffee.R;
import fpt.edu.aptcoffee.dao.NguoiDungDAO;
import fpt.edu.aptcoffee.model.NguoiDung;
import fpt.edu.aptcoffee.ui.DoiMatKhauActivity;
import fpt.edu.aptcoffee.ui.LienHeActivity;
import fpt.edu.aptcoffee.ui.SignInActivity;
import fpt.edu.aptcoffee.ui.ThietLapTaiKhoanActivity;
import fpt.edu.aptcoffee.utils.ImageToByte;
import fpt.edu.aptcoffee.utils.MyToast;
import pl.droidsonroids.gif.GifImageView;

public class SettingFragment extends Fragment implements View.OnClickListener {

    public static final int PICK_IMAGE = 1;
    public static final String MA_NGUOIDUNG = "MA_NGUOIDUNG";

    TextView tvDanhGia, tvLienHe, tvThietLapTaiKhoan, tvDoiMatKhuau, tvDangXuat, tvTenNguoiDung, tvChucVu, tvEmail;
    MainActivity mainActivity;
    NguoiDungDAO nguoiDungDAO;
    CircleImageView civHinhAnh;
    ImageView ivDoiHinhAnh;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        initView(view);

        mainActivity = ((MainActivity) getActivity());
        nguoiDungDAO = new NguoiDungDAO(getContext());

        fillActivity();

        tvDanhGia.setOnClickListener(this);
        tvLienHe.setOnClickListener(this);
        tvThietLapTaiKhoan.setOnClickListener(this);
        tvDoiMatKhuau.setOnClickListener(this);
        tvDangXuat.setOnClickListener(this);
        ivDoiHinhAnh.setOnClickListener(this);

        return view;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivDoiHinhAnh:
                requestPermissionPickImage();
                break;
            case R.id.tvDanhGia:
                showRatingDialog();
                break;
            case R.id.tvLienHe:
                openLienHeActivity();
                break;
            case R.id.tvThietLapTaiKhoan:
                openTLTKActivity();
                break;
            case R.id.tvDoiMatKhau:
                openDoiMatKhauAcitvity();
                break;
            case R.id.tvDangXuat:
                logout();
                break;
        }
    }

    private void initView(View view) {
        tvDanhGia = view.findViewById(R.id.tvDanhGia);
        tvLienHe = view.findViewById(R.id.tvLienHe);
        tvThietLapTaiKhoan = view.findViewById(R.id.tvThietLapTaiKhoan);
        tvDoiMatKhuau = view.findViewById(R.id.tvDoiMatKhau);
        tvDangXuat = view.findViewById(R.id.tvDangXuat);
        civHinhAnh = view.findViewById(R.id.civHinhAnh);
        tvTenNguoiDung = view.findViewById(R.id.tvTenNguoiDung);
        tvChucVu = view.findViewById(R.id.tvChucVu);
        tvEmail = view.findViewById(R.id.tvEmail);
        ivDoiHinhAnh = view.findViewById(R.id.ivDoiHinhAnh);
    }

    private void fillActivity() {
        NguoiDung nguoiDung = getNguoiDung();
        Bitmap bitmap = BitmapFactory.decodeByteArray(nguoiDung.getHinhAnh(),
                0,
                nguoiDung.getHinhAnh().length);
        // Gán dữ liệu cho View
        civHinhAnh.setImageBitmap(bitmap);
        tvTenNguoiDung.setText(nguoiDung.getHoVaTen());
        tvChucVu.setText(nguoiDung.getChucVu());
        tvEmail.setText(nguoiDung.getEmail());

    }

    private NguoiDung getNguoiDung() {
        // Lấy mã người dùng từ MainActivity theo fun(getKeyUser)
        String maNguoiDung = Objects.requireNonNull(mainActivity).getKeyUser();
        return nguoiDungDAO.getByMaNguoiDung(maNguoiDung);
    }

    private void requestPermissionPickImage() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            // cấp quyền cho ứng dụng nếu chưa được cấp quyền
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    1);
        } else {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
        }
    }

    private void openLienHeActivity() {
        startActivity(new Intent(getContext(), LienHeActivity.class));
        ((Activity) requireContext()).overridePendingTransition(R.anim.anim_in_right, R.anim.anim_out_left);
    }

    private void openTLTKActivity() {
        Intent intent = new Intent(getContext(), ThietLapTaiKhoanActivity.class);
        intent.putExtra(MA_NGUOIDUNG, getNguoiDung().getMaNguoiDung());
        startActivity(intent);
        ((Activity) requireContext()).overridePendingTransition(R.anim.anim_in_right, R.anim.anim_out_left);
    }

    private void openDoiMatKhauAcitvity() {
        Intent intent = new Intent(getContext(), DoiMatKhauActivity.class);
        intent.putExtra(MA_NGUOIDUNG, getNguoiDung().getMaNguoiDung());
        startActivity(intent);
        ((Activity) requireContext()).overridePendingTransition(R.anim.anim_in_right, R.anim.anim_out_left);
    }

    private void openSignInActivity() {
        startActivity(new Intent(getContext(), SignInActivity.class));
        ((Activity) requireContext()).overridePendingTransition(R.anim.anim_in_left, R.anim.anim_out_right);
    }

    private void updateAvatarUser(){
        NguoiDung nguoiDung = getNguoiDung();
        nguoiDung.setHinhAnh(ImageToByte.circleImageViewToByte(getContext(), civHinhAnh));

        if (nguoiDungDAO.updateNguoiDung(nguoiDung)) {
            MyToast.successful(getContext(), "Cập nhật ảnh đại diện thành công");
            fillActivity();
        } else {
            MyToast.error(getContext(), "Lỗi");
        }
    }

    private void logout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AlertDialogTheme)
                .setMessage("Bạn có muốn đăng xuất?")
                .setPositiveButton("Đăng xuất", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Đăng xuất hệ thống
                        openSignInActivity();
                    }
                })
                .setNegativeButton("Huỷ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @SuppressLint("InflateParams")
    private void showRatingDialog() {
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.layout_danh_gia);

        RatingBar ratingBar = dialog.findViewById(R.id.rtbDanhGia);
        Button btnDanhGia = dialog.findViewById(R.id.btnDanhGia);
        TextView tvBoQua = dialog.findViewById(R.id.tvBoQua);
        GifImageView gif = dialog.findViewById(R.id.imgGif);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
        int height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setLayout(width, height);

        tvBoQua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                // Chọn sao đánh giá
                if (ratingBar.getRating() <= 3) {
                    gif.setImageResource(R.drawable.git_sad);
                } else {
                    gif.setImageResource(R.drawable.gif_danh_gia);
                }
            }
        });

        btnDanhGia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Đánh giá sao cho hệ thống
                MyToast.successful(getContext(), "Đã đánh giá " + ratingBar.getRating() + " sao");
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                InputStream stream = requireContext().getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(stream);
                civHinhAnh.setImageBitmap(bitmap);
                updateAvatarUser();
            } catch (Exception e) {
                e.getMessage();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        fillActivity();
    }
}