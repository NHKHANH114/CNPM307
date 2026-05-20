package fpt.edu.aptcoffee.dao;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.ParseException;
import java.util.ArrayList;

import fpt.edu.aptcoffee.database.CoffeeDB;
import fpt.edu.aptcoffee.utils.XDate;
import fpt.edu.aptcoffee.model.NguoiDung;

public class NguoiDungDAO {
    CoffeeDB coffeeDB;

    public NguoiDungDAO(Context context) {
        this.coffeeDB = new CoffeeDB(context);
    }

    @SuppressLint("Range")
    public ArrayList<NguoiDung> get(String sql, String... choose) {
        ArrayList<NguoiDung> list = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = coffeeDB.getReadableDatabase();
        @SuppressLint("Recycle") Cursor cursor = sqLiteDatabase.rawQuery(sql, choose);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                NguoiDung nguoiDung = new NguoiDung();
                nguoiDung.setMaNguoiDung(cursor.getString(cursor.getColumnIndex("maNguoiDung")));
                nguoiDung.setHoVaTen(cursor.getString(cursor.getColumnIndex("hoVaTen")));
                nguoiDung.setHinhAnh(cursor.getBlob(cursor.getColumnIndex("hinhAnh")));
                try {
                    nguoiDung.setNgaySinh(XDate.toDate(cursor.getString(cursor.getColumnIndex("ngaySinh"))));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                nguoiDung.setEmail(cursor.getString(cursor.getColumnIndex("email")));
                nguoiDung.setChucVu(cursor.getString(cursor.getColumnIndex("chucVu")));
                nguoiDung.setGioiTinh(cursor.getString(cursor.getColumnIndex("gioiTinh")));
                nguoiDung.setMatKhau(cursor.getString(cursor.getColumnIndex("matKhau")));

                list.add(nguoiDung);
                Log.i("TAG", nguoiDung.toString());
            } while (cursor.moveToNext());
        }

        return list;
    }

    public ArrayList<NguoiDung> getAll() {
        String sqlGetAll = "SELECT * FROM NGUOIDUNG";

        return get(sqlGetAll);
    }

    public boolean insertNguoiDung(NguoiDung nguoiDung) {
        SQLiteDatabase sqLiteDatabase = coffeeDB.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("maNguoiDung", nguoiDung.getMaNguoiDung());
        values.put("hoVaTen", nguoiDung.getHoVaTen());
        values.put("hinhAnh", nguoiDung.getHinhAnh());
        values.put("ngaySinh", XDate.toStringDate(nguoiDung.getNgaySinh()));
        values.put("email", nguoiDung.getEmail());
        values.put("chucVu", nguoiDung.getChucVu());
        values.put("gioiTinh", nguoiDung.getGioiTinh());
        values.put("matKhau", nguoiDung.getMatKhau());
        long check = sqLiteDatabase.insert("NGUOIDUNG", null, values);

        return check != -1;
    }

    public boolean updateNguoiDung(NguoiDung nguoiDung) {
        SQLiteDatabase sqLiteDatabase = coffeeDB.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("hoVaTen", nguoiDung.getHoVaTen());
        values.put("hinhAnh", nguoiDung.getHinhAnh());
        values.put("ngaySinh", XDate.toStringDate(nguoiDung.getNgaySinh()));
        values.put("email", nguoiDung.getEmail());
        values.put("chucVu", nguoiDung.getChucVu());
        values.put("gioiTinh", nguoiDung.getGioiTinh());
        values.put("matKhau", nguoiDung.getMatKhau());
        int check = sqLiteDatabase.update("NGUOIDUNG", values, "maNguoiDung=?", new String[]{String.valueOf(nguoiDung.getMaNguoiDung())});

        return check > 0;
    }

    public boolean deleteNguoiDung(String maNguoiDung) {
        SQLiteDatabase sqLiteDatabase = coffeeDB.getWritableDatabase();
        int check = sqLiteDatabase.delete("NGUOIDUNG", "maNguoiDung=?", new String[]{maNguoiDung});

        return check > 0;
    }

    public NguoiDung getByMaNguoiDung(String maNguoiDung) {
        String sqlGetMaNguoiDung = "SELECT * FROM NGUOIDUNG WHERE maNguoiDung=?";
        ArrayList<NguoiDung> list = get(sqlGetMaNguoiDung, maNguoiDung);

        return list.get(0);
    }
    public ArrayList<NguoiDung> getAllPositionNhanVien() {
        String sqlGetMaNguoiDung = "SELECT * FROM NGUOIDUNG WHERE chucVu=?";

        return get(sqlGetMaNguoiDung, NguoiDung.POSITION_STAFF);
    }

    public boolean checkLogin(String tenDangNhap, String matKhau) {
        String sqlCheckLogin = "SELECT * FROM NGUOIDUNG WHERE maNguoiDung=? AND matKhau=?";
        ArrayList<NguoiDung> list = get(sqlCheckLogin, tenDangNhap, matKhau);

        return list.size() != 0;
    }

}
