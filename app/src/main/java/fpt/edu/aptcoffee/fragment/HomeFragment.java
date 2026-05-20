package fpt.edu.aptcoffee.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import fpt.edu.aptcoffee.MainActivity;
import fpt.edu.aptcoffee.R;
import fpt.edu.aptcoffee.adapter.PhotoAdapter;
import fpt.edu.aptcoffee.adapter.ThucUongHomeFragmentAdapter;
import fpt.edu.aptcoffee.dao.HangHoaDAO;
import fpt.edu.aptcoffee.dao.NguoiDungDAO;
import fpt.edu.aptcoffee.model.HangHoa;
import fpt.edu.aptcoffee.model.NguoiDung;
import fpt.edu.aptcoffee.model.Photo;
import fpt.edu.aptcoffee.ui.DoanhThuActivity;
import fpt.edu.aptcoffee.ui.HoaDonActivity;
import fpt.edu.aptcoffee.ui.LoaiThucUongActivity;
import fpt.edu.aptcoffee.ui.NhanVienActivity;
import fpt.edu.aptcoffee.ui.QuanLyBanActivity;
import fpt.edu.aptcoffee.ui.ThucUongActivity;
import fpt.edu.aptcoffee.utils.MyToast;
import me.relex.circleindicator.CircleIndicator3;

public class HomeFragment extends Fragment implements View.OnClickListener {
    TextView tvHi;
    CircleImageView civHinhAnh;
    ViewPager2 vpSlideImage;
    CircleIndicator3 indicator3;
    CardView cvBan, cvLoai, cvThucUong, cvNhanVien, cvHoaDon, cvDoanhThu;
    MainActivity mainActivity;
    NguoiDungDAO nguoiDungDAO;
    HangHoaDAO hangHoaDAO;
    RecyclerView recyclerViewThucUong;
    Handler handler;
    Runnable runnable;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initView(view);
        initOnClickCard();
        loadSlideImage();

        mainActivity = ((MainActivity) getActivity());
        nguoiDungDAO = new NguoiDungDAO(getContext());
        hangHoaDAO = new HangHoaDAO(getContext());

        welcomeUser();
        loadListThucUong();
        autoRunSildeImage();
        return view;
    }

    private void autoRunSildeImage() {
        // Tự động chuyển ảnh trong SlideImage
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                if(vpSlideImage.getCurrentItem() == getListImage().size() -1){
                    vpSlideImage.setCurrentItem(0, false);
                }else {
                    vpSlideImage.setCurrentItem(vpSlideImage.getCurrentItem() + 1);
                }
            }
        };
        handler.postDelayed(runnable, 2000);

        // Sự kiện Slide Image chuyển ảnh
        vpSlideImage.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                handler.removeCallbacks(runnable);
                handler.postDelayed(runnable, 2000);            }
        });
    }

    private void loadListThucUong() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false);
        recyclerViewThucUong.setLayoutManager(linearLayoutManager);

        // Lấy danh sách thức uống hiển thị trên recyclerView
        ArrayList<HangHoa> listHangHoa = hangHoaDAO.getAll();
        ThucUongHomeFragmentAdapter adapter = new ThucUongHomeFragmentAdapter(listHangHoa);
        recyclerViewThucUong.setAdapter(adapter);
    }

    private void initView(View view) {
        vpSlideImage = view.findViewById(R.id.vpSlideImage);
        indicator3 = view.findViewById(R.id.circleIndicator3);
        cvBan = view.findViewById(R.id.cardBan);
        cvLoai = view.findViewById(R.id.cardLoaiThucUong);
        cvThucUong = view.findViewById(R.id.cardThucUong);
        cvNhanVien = view.findViewById(R.id.cardNhanVien);
        cvHoaDon = view.findViewById(R.id.cardHoaDon);
        cvDoanhThu = view.findViewById(R.id.cardDoanhThu);
        tvHi = view.findViewById(R.id.tvHi);
        civHinhAnh = view.findViewById(R.id.hinhAnh);
        recyclerViewThucUong = view.findViewById(R.id.recyclerViewThucUong);
    }

    private void initOnClickCard() {
        cvBan.setOnClickListener(this);
        cvLoai.setOnClickListener(this);
        cvThucUong.setOnClickListener(this);
        cvNhanVien.setOnClickListener(this);
        cvHoaDon.setOnClickListener(this);
        cvDoanhThu.setOnClickListener(this);
    }

    private void loadSlideImage() {
        // Hiển thị Slide image
        PhotoAdapter adapter = new PhotoAdapter(getListImage());

        vpSlideImage.setAdapter(adapter);
        vpSlideImage.setOffscreenPageLimit(2);
        indicator3.setViewPager(vpSlideImage);
    }

    @NonNull
    private ArrayList<Photo> getListImage() {
        ArrayList<Photo> list = new ArrayList<>();
        list.add(new Photo(R.drawable.slide_image1));
        list.add(new Photo(R.drawable.slide_image2));
        list.add(new Photo(R.drawable.slide_image3));
        list.add(new Photo(R.drawable.silde_image4));
        list.add(new Photo(R.drawable.slide_image5));

        return list;
    }

    @SuppressLint("SetTextI18n")
    private void welcomeUser() {
        NguoiDung nguoiDung = getNguoiDung();
        Bitmap bitmap = BitmapFactory.decodeByteArray(nguoiDung.getHinhAnh(),
                0,
                nguoiDung.getHinhAnh().length);

        // Gán dữ liệu cho view
        tvHi.setText("Hello, " + nguoiDung.getHoVaTen());
        civHinhAnh.setImageBitmap(bitmap);
    }

    private NguoiDung getNguoiDung() {
        // Lấy mã người dùng từ MainActivity thông qua hàm getKeyUser
        String maNguoiDung = Objects.requireNonNull(mainActivity).getKeyUser();
        // Lây đối tượng người dùng theo mã
        return nguoiDungDAO.getByMaNguoiDung(maNguoiDung);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cardBan:
                // Mở màng hình quản lý bàn
                startActivity(new Intent(getContext(), QuanLyBanActivity.class));
                (requireActivity()).overridePendingTransition(R.anim.anim_in_right, R.anim.anim_out_left);
                break;
            case R.id.cardLoaiThucUong:
                // Mở màng hình quản lý loại hàng
                startActivity(new Intent(getContext(), LoaiThucUongActivity.class));
                (requireActivity()).overridePendingTransition(R.anim.anim_in_right, R.anim.anim_out_left);
                break;
            case R.id.cardThucUong:
                // Mở màng hình quản lý thức uống
                startActivity(new Intent(getContext(), ThucUongActivity.class));
                (requireActivity()).overridePendingTransition(R.anim.anim_in_right, R.anim.anim_out_left);
                break;
            case R.id.cardNhanVien:
                if (getNguoiDung().isAdmin()) {
                    // Người dùng có chức vụ ="Admin" -> Mở màng hình quản lý nhân viên
                    startActivity(new Intent(getContext(), NhanVienActivity.class));
                    (requireActivity()).overridePendingTransition(R.anim.anim_in_right, R.anim.anim_out_left);
                } else {
                    // Người dung có chức vụ = "NhanVien"
                    MyToast.error(getContext(), "Chức năng dành cho Admin");
                }
                break;
            case R.id.cardHoaDon:
                // Mở màng hình quản lý hoá đơn
                startActivity(new Intent(getContext(), HoaDonActivity.class));
                (requireActivity()).overridePendingTransition(R.anim.anim_in_right, R.anim.anim_out_left);
                break;
            case R.id.cardDoanhThu:
                if (getNguoiDung().isAdmin()) {
                    // Người dùng có chức vụ ="Admin" -> Mở màng hình quản lý doanh thu
                    startActivity(new Intent(getContext(), DoanhThuActivity.class));
                    (requireActivity()).overridePendingTransition(R.anim.anim_in_right, R.anim.anim_out_left);
                } else {
                    // Người dùng có chức vụ = "NhanVien"
                    MyToast.error(getContext(), "Chức năng dành cho Admin");
                }
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        welcomeUser();
        loadListThucUong();
    }
}