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

import android.widget.AdapterView;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import java.util.ArrayList;

import fpt.edu.aptcoffee.R;
import fpt.edu.aptcoffee.adapter.SpinnerAdapterLoaiHang;
import fpt.edu.aptcoffee.adapter.ThucUongAdapter;
import fpt.edu.aptcoffee.dao.HangHoaDAO;
import fpt.edu.aptcoffee.dao.LoaiHangDAO;
import fpt.edu.aptcoffee.interfaces.ItemHangHoaOnClick;
import fpt.edu.aptcoffee.model.HangHoa;
import fpt.edu.aptcoffee.model.LoaiHang;
import fpt.edu.aptcoffee.utils.MyToast;

public class ThucUongActivity extends AppCompatActivity {
    public static final String MA_HANG_HOA = "maHangHoa";
    Toolbar toolbar;
    Spinner spFill;
    LoaiHangDAO loaiHangDAO;
    HangHoaDAO hangHoaDAO;
    RecyclerView recyclerViewThucUong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thuc_uong);
        initToolBar();
        initView();
        loaiHangDAO = new LoaiHangDAO(this);
        hangHoaDAO = new HangHoaDAO(this);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        fillSpinner();
        loadData();

        spFill.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    loadData();
                } else {
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ThucUongActivity.this, RecyclerView.VERTICAL, false);
                    recyclerViewThucUong.setLayoutManager(linearLayoutManager);
                    LoaiHang loaiHang = loaiHangDAO.getbyTenLoai((String) spFill.getSelectedItem());
                    ThucUongAdapter thucUongAdapter = new ThucUongAdapter(hangHoaDAO.getByMaLoai(String.valueOf(loaiHang.getMaLoai())), new ItemHangHoaOnClick() {
                        @Override
                        public void itemOclick(View view, HangHoa hangHoa) {
                            showMenuPopup(view, hangHoa);
                        }
                    });
                    recyclerViewThucUong.setAdapter(thucUongAdapter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void showMenuPopup(View view, HangHoa hangHoa) {
        // Hiển thị menu (Cập nhật xoá)
        PopupMenu popup = new PopupMenu(ThucUongActivity.this, view);
        popup.getMenuInflater().inflate(R.menu.menu_more, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @SuppressLint("NonConstantResourceId")
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_update:
                        openCapNhatThucUongActivity(hangHoa);
                        break;
                    case R.id.menu_delete:
                        deleteHangHoa(hangHoa);
                        break;
                }
                return true;
            }
        });

        popup.show(); //showing
    }

    private void openCapNhatThucUongActivity(HangHoa hangHoa) {
        Intent intent = new Intent(ThucUongActivity.this, CapNhatHangHoaActivity.class);
        intent.putExtra(MA_HANG_HOA, String.valueOf(hangHoa.getMaHangHoa()));
        startActivity(intent);
        overridePendingTransition(R.anim.anim_in_right, R.anim.anim_out_left);
    }

    private void initView() {
        recyclerViewThucUong = findViewById(R.id.recyclerViewThucUong);
        spFill = findViewById(R.id.spinnerFill);
    }

    private void initToolBar() {
        toolbar = findViewById(R.id.toolbarThuUong);
        setSupportActionBar(toolbar);
    }

    private void loadData() {
        // Hiên thị danh sách Loại thức uống
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerViewThucUong.setLayoutManager(linearLayoutManager);

        ThucUongAdapter thucUongAdapter = new ThucUongAdapter(hangHoaDAO.getAll(), new ItemHangHoaOnClick() {
            @Override
            public void itemOclick(View view, HangHoa hangHoa) {
               showMenuPopup(view, hangHoa);
            }
        });
        recyclerViewThucUong.setAdapter(thucUongAdapter);
    }

    private void deleteHangHoa(HangHoa hangHoa) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ThucUongActivity.this, R.style.AlertDialogTheme);
        builder.setMessage("Bạn có muốn xoá loại " + hangHoa.getTenHangHoa() + "?");
        builder.setPositiveButton("Xoá", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (hangHoaDAO.deleteHangHoa(String.valueOf(hangHoa.getMaHangHoa()))) {
                    // Xoá thức uống
                    MyToast.successful(ThucUongActivity.this, "Xoá thành công");
                    loadData();
                    fillSpinner();
                } else {
                    MyToast.error(ThucUongActivity.this, "Xoá không thành công, Có hoá đơn tồn tại mã thức uống này");
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

    private void fillSpinner() {
        ArrayList<String> list = new ArrayList<>();
        list.add("Tất cả");
        for (LoaiHang loaiHang : loaiHangDAO.getAll()) {
            list.add(loaiHang.getTenLoai());
        }
        SpinnerAdapterLoaiHang adapterLoaiHang = new SpinnerAdapterLoaiHang(this, list);
        spFill.setAdapter(adapterLoaiHang);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_add) {
            startActivity(new Intent(ThucUongActivity.this, ThemHangHoaActivity.class));
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
        fillSpinner();
    }
}