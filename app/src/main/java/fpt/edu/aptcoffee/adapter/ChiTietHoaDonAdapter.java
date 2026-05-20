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
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import fpt.edu.aptcoffee.R;
import fpt.edu.aptcoffee.dao.HangHoaDAO;
import fpt.edu.aptcoffee.model.HangHoa;
import fpt.edu.aptcoffee.model.HoaDon;
import fpt.edu.aptcoffee.model.HoaDonChiTiet;

public class ChiTietHoaDonAdapter extends RecyclerView.Adapter<ChiTietHoaDonAdapter.ChiTietHoaDonViewHolder>{
    Context context;
    ArrayList<HangHoa> list;
    ArrayList<HoaDonChiTiet> listHDCT;

    public ChiTietHoaDonAdapter(Context context, ArrayList<HangHoa> list, ArrayList<HoaDonChiTiet> listHDCT) {
        this.context = context;
        this.list = list;
        this.listHDCT = listHDCT;
    }

    @NonNull
    @Override
    public ChiTietHoaDonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_thuc_uong_hoadonchitiet, parent, false);
        return new ChiTietHoaDonViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ChiTietHoaDonViewHolder holder, int position) {
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
        if(hoaDonChiTiet.getMaHangHoa() == hangHoa.getMaHangHoa()){
            holder.tvSoluong.setText("x"+hoaDonChiTiet.getSoLuong());
            holder.tvGiaTien.setText(hangHoa.getGiaTien() * hoaDonChiTiet.getSoLuong()+"VND");
        }


    }

    @Override
    public int getItemCount() {
        if (list == null){
            return 0;
        }
        return list.size();
    }

    public static class ChiTietHoaDonViewHolder extends RecyclerView.ViewHolder {
        ImageView ivHinhAnh;
        TextView tvTenHangHoa, tvSoluong, tvGiaTien;
        public ChiTietHoaDonViewHolder(@NonNull View itemView) {
            super(itemView);
            ivHinhAnh = itemView.findViewById(R.id.ivHinhAnh);
            tvTenHangHoa = itemView.findViewById(R.id.tvTenHangHoa);
            tvSoluong = itemView.findViewById(R.id.tvSoluong);
            tvGiaTien = itemView.findViewById(R.id.tvGiaTien);
        }
    }
}
