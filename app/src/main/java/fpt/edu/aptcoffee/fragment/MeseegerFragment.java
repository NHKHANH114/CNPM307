package fpt.edu.aptcoffee.fragment;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupMenu;

import java.util.ArrayList;
import java.util.Objects;

import fpt.edu.aptcoffee.R;
import fpt.edu.aptcoffee.adapter.ThongBaoAdapter;
import fpt.edu.aptcoffee.dao.ThongBaoDAO;
import fpt.edu.aptcoffee.interfaces.ItemThongBaoOnClick;
import fpt.edu.aptcoffee.model.ThongBao;
import fpt.edu.aptcoffee.utils.MyToast;


public class MeseegerFragment extends Fragment {
    RecyclerView recyclerViewThongBao;
    ThongBaoDAO thongBaoDAO;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_meseeger, container, false);
        initView(view);
        thongBaoDAO = new ThongBaoDAO(getContext());
        loadListNotification();

        return view;
    }

    private void initView(View view) {
        recyclerViewThongBao = view.findViewById(R.id.recyclerViewThongBao);
    }

    private void loadListNotification() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerViewThongBao.setLayoutManager(layoutManager);

        // Lấy danh sách thôn báo
        ArrayList<ThongBao> listNotification = thongBaoDAO.getAll();
        // Hiển thị dữ liệu lên danh sách
        ThongBaoAdapter adapterNofitication = new ThongBaoAdapter(getContext(), listNotification, new ItemThongBaoOnClick() {
            @Override
            public void itemOclick(View view, ThongBao thongBao) {
                showPopupMenuDelete(view, thongBao);
            }
        });

        recyclerViewThongBao.setAdapter(adapterNofitication);
    }

    private void showPopupMenuDelete(View view, ThongBao thongBao) {
        PopupMenu popup = new PopupMenu(getContext(), view);
        popup.getMenuInflater()
                .inflate(R.menu.menu_delete, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.menu_delete) {
                    showComfirmDeleteDialog(thongBao);
                }
                return true;
            }
        });

        popup.show();
    }

    private void showComfirmDeleteDialog(ThongBao thongBao) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext(), R.style.AlertDialogTheme)
                .setMessage("Bạn có muốn xoá thống báo")
                .setPositiveButton("Xoá", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Xoá thông báo
                        if (thongBaoDAO.deleteThongBao(String.valueOf(thongBao.getMaThongBao()))) {
                            MyToast.successful(getContext(), "Xoá thành công");
                            loadListNotification();
                        }
                    }
                })
                .setNegativeButton("Huỷ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

        builder.show();
    }

    private void updateStatusNotification() {
        ArrayList<ThongBao> listNotifiation = thongBaoDAO.getAll();

        for (ThongBao thongBao : listNotifiation) {
            // Cập nhật lại thông báo về trạng thái đã xem
            thongBao.setTrangThai(ThongBao.STATUS_DA_XEM);
            if (thongBaoDAO.updateThongBao(thongBao)) {
                Log.i("TAG", "TAG: Cập nhật thông báo thành công");
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadListNotification();
        updateStatusNotification();
    }


}