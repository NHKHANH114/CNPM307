package fpt.edu.aptcoffee.dao;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

import fpt.edu.aptcoffee.database.CoffeeDB;
import fpt.edu.aptcoffee.model.HangHoa;
import fpt.edu.aptcoffee.model.LoaiHang;

public class LoaiHangDAO {
    CoffeeDB coffeeDB;

    public LoaiHangDAO(Context context) {
        this.coffeeDB = new CoffeeDB(context);
    }

    @SuppressLint("Range")
    public ArrayList<LoaiHang> get(String sql, String... choose) {
        ArrayList<LoaiHang> list = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = coffeeDB.getReadableDatabase();
        @SuppressLint("Recycle") Cursor cursor = sqLiteDatabase.rawQuery(sql, choose);
        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            do {
                LoaiHang loaiHang = new LoaiHang();
                loaiHang.setMaLoai(cursor.getInt(cursor.getColumnIndex("maLoai")));
                loaiHang.setTenLoai(cursor.getString(cursor.getColumnIndex("tenLoai")));
                loaiHang.setHinhAnh(cursor.getBlob(cursor.getColumnIndex("hinhAnh")));
                list.add(loaiHang);
                Log.i("TAG", loaiHang.toString());
            } while (cursor.moveToNext());
        }

        return list;
    }

    public ArrayList<LoaiHang> getAll() {
        String sqlGetAll = "SELECT * FROM LOAIHANG";

        return get(sqlGetAll);
    }

    public boolean insertLoaiHang(LoaiHang loaiHang) {
        SQLiteDatabase sqLiteDatabase = coffeeDB.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("tenLoai", loaiHang.getTenLoai());
        values.put("hinhAnh", loaiHang.getHinhAnh());
        long check = sqLiteDatabase.insert("LOAIHANG", null, values);

        return check != 1;
    }

    public boolean updateLoaiHang(LoaiHang loaiHang) {
        SQLiteDatabase sqLiteDatabase = coffeeDB.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("tenLoai", loaiHang.getTenLoai());
        values.put("hinhAnh", loaiHang.getHinhAnh());
        int check = sqLiteDatabase.update("LOAIHANG", values, "maLoai=?", new String[]{String.valueOf(loaiHang.getMaLoai())});

        return check > 0;
    }

    public boolean deleteLoaiHang(String maLoai) {
        SQLiteDatabase sqLiteDatabase = coffeeDB.getWritableDatabase();

        @SuppressLint("Recycle") Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM HANGHOA WHERE maLoai=?", new String[]{maLoai});
        if (cursor.getCount() != 0){
            return false;
        }
        int check = sqLiteDatabase.delete("LOAIHANG", "maLoai=?", new String[]{maLoai});
        return check > 0;
    }

    public LoaiHang getByMaLoai(String maLoai){
        String sqlGetByMaLoai = "SELECT * FROM LOAIHANG WHERE maLoai=?";
        ArrayList<LoaiHang> list = get(sqlGetByMaLoai, maLoai);

        return list.get(0);
    }
    public LoaiHang getbyTenLoai(String tenLoai){
        String sqlGetByMaLoai = "SELECT * FROM LOAIHANG WHERE tenLoai=?";
        ArrayList<LoaiHang> list = get(sqlGetByMaLoai, tenLoai);

        return list.get(0);
    }

}
