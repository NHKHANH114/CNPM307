package fpt.edu.aptcoffee.adapter;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import fpt.edu.aptcoffee.R;
import fpt.edu.aptcoffee.interfaces.ItemHangHoaOnClick;
import fpt.edu.aptcoffee.model.HangHoa;

public class ThucUongAdapter extends RecyclerView.Adapter<ThucUongAdapter.ThucUongViewHolder> {
    ArrayList<HangHoa> list;
    ItemHangHoaOnClick itemHangHoaOnClick;

    public ThucUongAdapter(ArrayList<HangHoa> list, ItemHangHoaOnClick itemHangHoaOnClick) {
        this.list = list;
        this.itemHangHoaOnClick = itemHangHoaOnClick;
    }

    @NonNull
    @Override
    public ThucUongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_thuc_uong, parent, false);
        return new ThucUongViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ThucUongViewHolder holder, int position) {
        HangHoa hangHoa = list.get(position);
        if (hangHoa == null) {
            return;
        }
        Bitmap bitmap = BitmapFactory.decodeByteArray(hangHoa.getHinhAnh(),
                0,
                hangHoa.getHinhAnh().length);
        holder.ivHinhAnh.setImageBitmap(bitmap);
        holder.tvTenHangHoa.setText(hangHoa.getTenHangHoa());
        holder.tvGiaTien.setText(hangHoa.getGiaTien() + "VND");
        if (hangHoa.getTrangThai() == 0) {
            holder.tvTrangThai.setText("Hết hàng");
            holder.tvTrangThai.setTextColor(Color.GRAY);
        } else {
            holder.tvTrangThai.setText("Còn hàng");
            holder.tvTrangThai.setTextColor(Color.BLUE);
        }
        holder.ivMenuMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemHangHoaOnClick.itemOclick(view, hangHoa);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    public static class ThucUongViewHolder extends RecyclerView.ViewHolder {
        ImageView ivHinhAnh, ivMenuMore;
        TextView tvTenHangHoa, tvGiaTien, tvTrangThai;

        public ThucUongViewHolder(@NonNull View itemView) {
            super(itemView);
            ivHinhAnh = itemView.findViewById(R.id.ivHinhAnh);
            ivMenuMore = itemView.findViewById(R.id.ivMenuMore);
            tvTenHangHoa = itemView.findViewById(R.id.tvTenHangHoa);
            tvGiaTien = itemView.findViewById(R.id.tvGiaTien);
            tvTrangThai = itemView.findViewById(R.id.tvTrangThai);
        }
    }
}
