package fpt.edu.aptcoffee.dao;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import fpt.edu.aptcoffee.database.CoffeeDB;
import fpt.edu.aptcoffee.model.HoaDonChiTiet;
import fpt.edu.aptcoffee.utils.XDate;

public class HoaDonChiTietDAO {
    CoffeeDB coffeeDB;
    public HoaDonChiTietDAO(Context context) {
        this.coffeeDB = new CoffeeDB(context);
    }

    @SuppressLint("Range")
    public ArrayList<HoaDonChiTiet> get(String sql, String... choose) {
        ArrayList<HoaDonChiTiet> list = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = coffeeDB.getReadableDatabase();
        @SuppressLint("Recycle") Cursor cursor = sqLiteDatabase.rawQuery(sql, choose);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                HoaDonChiTiet hoaDonChiTiet = new HoaDonChiTiet();
                hoaDonChiTiet.setMaHDCT(cursor.getInt(cursor.getColumnIndex("maHDCT")));
                hoaDonChiTiet.setMaHoaDon(cursor.getInt(cursor.getColumnIndex("maHoaDon")));
                hoaDonChiTiet.setMaHangHoa(cursor.getInt(cursor.getColumnIndex("maHangHoa")));
                hoaDonChiTiet.setSoLuong(cursor.getInt(cursor.getColumnIndex("soLuong")));
                hoaDonChiTiet.setGiaTien(cursor.getInt(cursor.getColumnIndex("giaTien")));
                hoaDonChiTiet.setGhiChu(cursor.getString(cursor.getColumnIndex("ghiChu")));
                try {
                    hoaDonChiTiet.setNgayXuatHoaDon(XDate.toDate(cursor.getString(cursor.getColumnIndex("ngayXuatHoaDon"))));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                list.add(hoaDonChiTiet);
                Log.i("TAG", hoaDonChiTiet.toString());
            } while (cursor.moveToNext());

        }

        return list;
    }

    public ArrayList<HoaDonChiTiet> getAll() {
        String sql = "SELECT * FROM HOADONCHITIET";

        return get(sql);
    }

    public boolean insertHoaDonChiTiet(HoaDonChiTiet hoaDonChiTiet) {
        SQLiteDatabase sqLiteDatabase = coffeeDB.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("maHoaDon", hoaDonChiTiet.getMaHoaDon());
        values.put("maHangHoa", hoaDonChiTiet.getMaHangHoa());
        values.put("soLuong", hoaDonChiTiet.getSoLuong());
        values.put("giaTien", hoaDonChiTiet.getGiaTien());
        values.put("ghiChu", hoaDonChiTiet.getGhiChu());
        values.put("ngayXuatHoaDon", XDate.toStringDate(hoaDonChiTiet.getNgayXuatHoaDon()));
        long check = sqLiteDatabase.insert("HOADONCHITIET", null, values);

        return check != -1;
    }

    public boolean updateHoaDonChiTiet(HoaDonChiTiet hoaDonChiTiet) {
        SQLiteDatabase sqLiteDatabase = coffeeDB.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("maHoaDon", hoaDonChiTiet.getMaHoaDon());
        values.put("maHangHoa", hoaDonChiTiet.getMaHangHoa());
        values.put("soLuong", hoaDonChiTiet.getSoLuong());
        values.put("giaTien", hoaDonChiTiet.getGiaTien());
        values.put("ghiChu", hoaDonChiTiet.getGhiChu());
        values.put("ngayXuatHoaDon", XDate.toStringDate(hoaDonChiTiet.getNgayXuatHoaDon()));
        long check = sqLiteDatabase.update("HOADONCHITIET", values, "maHDCT=?", new String[]{String.valueOf(hoaDonChiTiet.getMaHDCT())});

        return check > 0;
    }

    public boolean deleteHoaDonChiTiet(String maHDCT) {
        SQLiteDatabase sqLiteDatabase = coffeeDB.getWritableDatabase();
        int check = sqLiteDatabase.delete("HOADONCHITIET", "maHDCT=?", new String[]{maHDCT});

        return check > 0;
    }
    public boolean deleteHoaDonChiTietByMaHoaDon(String maHoaDon) {
        SQLiteDatabase sqLiteDatabase = coffeeDB.getWritableDatabase();
        int check = sqLiteDatabase.delete("HOADONCHITIET", "maHoaDon=?", new String[]{maHoaDon});

        return check > 0;
    }

    public HoaDonChiTiet getByMaHDCT(String maHDCT) {
        String sql = "SELECT * FROM HOADONCHITIET WHERE maHDCT=?";
        ArrayList<HoaDonChiTiet> list = get(sql, maHDCT);

        return list.get(0);
    }
    public ArrayList<HoaDonChiTiet> getByMaHoaDon(String maHoaDon) {
        String sql = "SELECT * FROM HOADONCHITIET WHERE maHoaDon=?";

        return get(sql, maHoaDon);
    }

    @SuppressLint("Range")
    public int getDoanhThuNgay(String date) {
        SQLiteDatabase sqLiteDatabase = coffeeDB.getReadableDatabase();
        String sql = "SELECT SUM(giaTien) as DoanhThu FROM HOADONCHITIET WHERE ngayXuatHoaDon=?";
        ArrayList<Integer> list = new ArrayList<>();
        @SuppressLint("Recycle")
        Cursor cursor = sqLiteDatabase.rawQuery(sql, new String[]{date});
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                try {
                    list.add(cursor.getInt(cursor.getColumnIndex("DoanhThu")));
                } catch (Exception e) {
                    list.add(0);
                }
            } while (cursor.moveToNext());
        }
        return list.get(0);
    }

    @SuppressLint("Range")
    public int getDTThangNam(String tuNgay, String denNgay) {
        SQLiteDatabase sqLiteDatabase = coffeeDB.getReadableDatabase();
        String sql = "SELECT SUM(giaTien) as doanhThu FROM HOADONCHITIET WHERE ngayXuatHoaDon BETWEEN ? AND ?";
        ArrayList<Integer> list = new ArrayList<>();
        @SuppressLint("Recycle")
        Cursor cursor = sqLiteDatabase.rawQuery(sql, new String[]{tuNgay, denNgay});
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                try {
                    list.add(cursor.getInt(cursor.getColumnIndex("doanhThu")));
                } catch (Exception e) {
                    list.add(0);
                }
            } while (cursor.moveToNext());
        }
        return list.get(0);
    }

    @SuppressLint("Range")
    public int getGiaTien(int maHoaDon) {
        SQLiteDatabase sqLiteDatabase = coffeeDB.getReadableDatabase();
        String sql = "SELECT SUM(giaTien) as DoanhThu FROM HOADONCHITIET WHERE maHoaDon=?";
        ArrayList<Integer> list = new ArrayList<>();
        @SuppressLint("Recycle")
        Cursor cursor = sqLiteDatabase.rawQuery(sql, new String[]{String.valueOf(maHoaDon)});
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                try {
                    list.add(cursor.getInt(cursor.getColumnIndex("DoanhThu")));
                } catch (Exception e) {
                    list.add(0);
                }
            } while (cursor.moveToNext());
        }
        return list.get(0);
    }
}
