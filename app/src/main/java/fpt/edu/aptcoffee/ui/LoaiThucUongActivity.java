package fpt.edu.aptcoffee.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import java.util.ArrayList;

import fpt.edu.aptcoffee.R;
import fpt.edu.aptcoffee.adapter.LoaiHangAdapter;
import fpt.edu.aptcoffee.dao.LoaiHangDAO;
import fpt.edu.aptcoffee.interfaces.ItemLoaiHangOnClick;
import fpt.edu.aptcoffee.model.LoaiHang;
import fpt.edu.aptcoffee.utils.MyToast;

public class LoaiThucUongActivity extends AppCompatActivity {

    public static final String MA_LOAI_HANG = "maLoaiHang";
    RecyclerView recyclerViewLoai;
    Toolbar toolbar;
    LoaiHangDAO loaiHangDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loai_thuc_uong);
        initToolBar();
        initView();

        loaiHangDAO = new LoaiHangDAO(this);

        loadData();
    }

    private void initView() {
        recyclerViewLoai = findViewById(R.id.recyclerViewLoai);
    }

    private void initToolBar() {
        toolbar = findViewById(R.id.toolbar_loai);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void loadData() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerViewLoai.setLayoutManager(linearLayoutManager);
        ArrayList<LoaiHang> listLoaiHang = loaiHangDAO.getAll();
        LoaiHangAdapter loaiHangAdapter = new LoaiHangAdapter(listLoaiHang, new ItemLoaiHangOnClick() {
            @Override
            public void itemOclick(View view, LoaiHang loaiHang) {
                PopupMenu popup = new PopupMenu(LoaiThucUongActivity.this, view);
                popup.getMenuInflater().inflate(R.menu.menu_more, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @SuppressLint("NonConstantResourceId")
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu_update:
                                updateLoaiHang(loaiHang);
                                break;
                            case R.id.menu_delete:
                                deleteLoaiHang(loaiHang);
                                break;
                        }
                        return true;
                    }
                });

                popup.show(); //showing
            }
        });
        recyclerViewLoai.setAdapter(loaiHangAdapter);
    }

    private void updateLoaiHang(LoaiHang loaiHang) {
        // Cập nhật loại hàng
        Intent intent = new Intent(LoaiThucUongActivity.this, SuaLoaiActivity.class);
        intent.putExtra(MA_LOAI_HANG, String.valueOf(loaiHang.getMaLoai()));
        startActivity(intent);
        overridePendingTransition(R.anim.anim_in_right, R.anim.anim_out_left);
    }

    private void deleteLoaiHang(LoaiHang loaiHang) {
        // Xoá loại hàng
        AlertDialog.Builder builder = new AlertDialog.Builder(LoaiThucUongActivity.this, R.style.AlertDialogTheme);
        builder.setMessage("Bạn có muốn xoá loại " + loaiHang.getTenLoai()+"?");

        builder.setPositiveButton("Xoá", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (loaiHangDAO.deleteLoaiHang(String.valueOf(loaiHang.getMaLoai()))) {
                    MyToast.successful(LoaiThucUongActivity.this, "Xoá thành công");
                    loadData();
                } else {
                    MyToast.error(LoaiThucUongActivity.this, "Có thức uống thuộc mã loại này");
                }
            }
        });

        builder.setNegativeButton("Huỷ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_add) {
            startActivity(new Intent(LoaiThucUongActivity.this, ThemLoaiActivity.class));
            overridePendingTransition(R.anim.anim_in_right, R.anim.anim_out_left);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.anim_in_left, R.anim.anim_out_right);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }
}