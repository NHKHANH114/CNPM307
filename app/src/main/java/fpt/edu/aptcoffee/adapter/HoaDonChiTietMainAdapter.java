package fpt.edu.aptcoffee.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import fpt.edu.aptcoffee.R;
import fpt.edu.aptcoffee.dao.HoaDonChiTietDAO;
import fpt.edu.aptcoffee.interfaces.ItemTangGiamSoLuongOnClick;
import fpt.edu.aptcoffee.model.HangHoa;
import fpt.edu.aptcoffee.model.HoaDonChiTiet;
import fpt.edu.aptcoffee.utils.MyToast;

public class HoaDonChiTietMainAdapter extends RecyclerView.Adapter<HoaDonChiTietMainAdapter.HoaDonChiTietMainViewHolder>{
    Context context;
    ArrayList<HangHoa> list;
    ArrayList<HoaDonChiTiet> listHDCT;
    ItemTangGiamSoLuongOnClick itemTangGiamSoLuongOnClick;
    int index = 1;

    public HoaDonChiTietMainAdapter(Context context, ArrayList<HangHoa> list, ArrayList<HoaDonChiTiet> listHDCT, ItemTangGiamSoLuongOnClick itemTangGiamSoLuongOnClick) {
        this.context = context;
        this.list = list;
        this.listHDCT = listHDCT;
        this.itemTangGiamSoLuongOnClick =  itemTangGiamSoLuongOnClick;
    }

    @NonNull
    @Override
    public HoaDonChiTietMainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_thuc_uong_oder_main, parent, false);
        return new HoaDonChiTietMainViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull HoaDonChiTietMainViewHolder holder, int position) {
        HangHoa hangHoa = list.get(position);
        HoaDonChiTiet hoaDonChiTiet = listHDCT.get(position);
        if (hangHoa == null){
            return;
        }
        Bitmap bitmap = BitmapFactory.decodeByteArray(hangHoa.getHinhAnh(),
                0,
                hangHoa.getHinhAnh().length);
        holder.ivHinhAnh.setImageBitmap(bitmap);
        holder.tvTenHangHoa.setText(hangHoa.getTenHangHoa());
        holder.tvGiaTienBanDau.setText(hangHoa.getGiaTien()+"VND");

        holder.ivTang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                index ++;
                if(index > 10){
                    index = 1;
                }
                holder.tvSoluong.setText(String.valueOf(index));
                holder.tvGiaTien.setText(hangHoa.getGiaTien() * index+"VND");
                itemTangGiamSoLuongOnClick.itemOclick(view, index, hoaDonChiTiet, hangHoa);
            }
        });
        holder.ivGiam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                index --;
                if(index <= 0){
                    index = 10;
                }
                holder.tvSoluong.setText(String.valueOf(index));
                holder.tvGiaTien.setText(hangHoa.getGiaTien() * index+"VND");
                itemTangGiamSoLuongOnClick.itemOclick(view, index, hoaDonChiTiet, hangHoa);
            }
        });
        if(hoaDonChiTiet.getMaHangHoa() == hangHoa.getMaHangHoa()){
            holder.tvSoluong.setText(String.valueOf(hoaDonChiTiet.getSoLuong()));
            holder.tvGiaTien.setText(hangHoa.getGiaTien() * hoaDonChiTiet.getSoLuong()+"VND");
        }

        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                itemTangGiamSoLuongOnClick.itemOclickDeleteHDCT(view, hoaDonChiTiet);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        if (list == null){
            return 0;
        }
        return list.size();
    }

    public static class HoaDonChiTietMainViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView ivHinhAnh, ivGiam, ivTang;
        TextView tvTenHangHoa, tvSoluong, tvGiaTien, tvGiaTienBanDau;
        public HoaDonChiTietMainViewHolder(@NonNull View itemView) {
            super(itemView);
            ivHinhAnh = itemView.findViewById(R.id.ivHinhAnh);
            tvTenHangHoa = itemView.findViewById(R.id.tvTenHangHoa);
            tvSoluong = itemView.findViewById(R.id.tvSoluong);
            tvGiaTien = itemView.findViewById(R.id.tvGiaTien);
            tvGiaTienBanDau = itemView.findViewById(R.id.tvGiaTienBanDau);
            ivGiam = itemView.findViewById(R.id.ivGiamSoLuong);
            ivTang = itemView.findViewById(R.id.ivTangSoLuong);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }
}
