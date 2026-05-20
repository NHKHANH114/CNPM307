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

public class HangHoaDAO {
    CoffeeDB coffeeDB;

    public HangHoaDAO(Context context) {
        this.coffeeDB = new CoffeeDB(context);
    }

    @SuppressLint("Range")
    public ArrayList<HangHoa> get(String sql, String... choose) {
        ArrayList<HangHoa> list = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = coffeeDB.getWritableDatabase();
        @SuppressLint("Recycle") Cursor cursor = sqLiteDatabase.rawQuery(sql, choose);
        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            do {
                HangHoa hangHoa = new HangHoa();
                hangHoa.setMaHangHoa(cursor.getInt(cursor.getColumnIndex("maHangHoa")));
                hangHoa.setTenHangHoa(cursor.getString(cursor.getColumnIndex("tenHangHoa")));
                hangHoa.setHinhAnh(cursor.getBlob(cursor.getColumnIndex("hinhAnh")));
                hangHoa.setGiaTien(cursor.getInt(cursor.getColumnIndex("giaTien")));
                hangHoa.setMaLoai(cursor.getInt(cursor.getColumnIndex("maLoai")));
                hangHoa.setTrangThai(cursor.getInt(cursor.getColumnIndex("trangThai")));
                list.add(hangHoa);
                Log.i("TAG", hangHoa.toString());
            } while (cursor.moveToNext());
        }

        return list;
    }

    public ArrayList<HangHoa> getAll() {
        String sqlGetAll = "SELECT * FROM HANGHOA";

        return get(sqlGetAll);
    }

    public boolean insertHangHoa(HangHoa hangHoa) {
        SQLiteDatabase sqLiteDatabase = coffeeDB.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("tenHangHoa", hangHoa.getTenHangHoa());
        values.put("hinhAnh", hangHoa.getHinhAnh());
        values.put("giaTien", hangHoa.getGiaTien());
        values.put("maLoai", hangHoa.getMaLoai());
        values.put("trangThai", hangHoa.getTrangThai());
        long check = sqLiteDatabase.insert("HangHoa", null, values);

        return check != 1;
    }

    public boolean updateHangHoa(HangHoa hangHoa) {
        SQLiteDatabase sqLiteDatabase = coffeeDB.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("tenHangHoa", hangHoa.getTenHangHoa());
        values.put("hinhAnh", hangHoa.getHinhAnh());
        values.put("giaTien", hangHoa.getGiaTien());
        values.put("maLoai", hangHoa.getMaLoai());
        values.put("trangThai", hangHoa.getTrangThai());
        int check = sqLiteDatabase.update("HangHoa", values, "maHangHoa=?", new String[]{String.valueOf(hangHoa.getMaHangHoa())});

        return check > 0;
    }

    public boolean deleteHangHoa(String maHangHoa) {
        SQLiteDatabase sqLiteDatabase = coffeeDB.getWritableDatabase();

        @SuppressLint("Recycle")
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM HOADONCHITIET WHERE maHangHoa=?", new String[]{maHangHoa});
        if (cursor.getCount() != 0) {
            return false;
        }
        int check = sqLiteDatabase.delete("HangHoa", "maHangHoa=?", new String[]{maHangHoa});

        return check > 0;
    }

    public HangHoa getByMaHangHoa(String maHangHoa) {
        String sqlGetByMaHangHoa = "SELECT * FROM HANGHOA WHERE maHangHoa=?";
        ArrayList<HangHoa> list = get(sqlGetByMaHangHoa, maHangHoa);

        return list.get(0);
    }

    public ArrayList<HangHoa> getByMaLoai(String maLoaiHang) {
        String sqlGetByMaHangHoa = "SELECT * FROM HANGHOA WHERE maloai=?";

        return get(sqlGetByMaHangHoa, maLoaiHang);
    }

    public ArrayList<HangHoa> getByTrangThai(String trangThai) {
        String sqlGetByMaHangHoa = "SELECT * FROM HANGHOA WHERE trangThai=?";

        return get(sqlGetByMaHangHoa, trangThai);
    }
}
