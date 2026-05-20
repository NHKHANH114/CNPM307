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
import fpt.edu.aptcoffee.model.Ban;
import fpt.edu.aptcoffee.model.ThongBao;
import fpt.edu.aptcoffee.utils.XDate;

public class ThongBaoDAO {
    CoffeeDB coffeeDB;

    public ThongBaoDAO(Context context) {
        this.coffeeDB = new CoffeeDB(context);
    }

    @SuppressLint("Range")
    public ArrayList<ThongBao> get(String sql, String... choose) {
        ArrayList<ThongBao> list = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = coffeeDB.getReadableDatabase();
        @SuppressLint("Recycle") Cursor cursor = sqLiteDatabase.rawQuery(sql, choose);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                ThongBao thongBao = new ThongBao();
                thongBao.setMaThongBao(cursor.getInt(cursor.getColumnIndex("maThongBao")));
                thongBao.setNoiDung(cursor.getString(cursor.getColumnIndex("noiDung")));
                thongBao.setTrangThai(cursor.getInt(cursor.getColumnIndex("trangThai")));
                try {
                    thongBao.setNgayThongBao(XDate.toDate(cursor.getString(cursor.getColumnIndex("ngayThongBao"))));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                list.add(thongBao);
                Log.i("TAG", thongBao.toString());
            } while (cursor.moveToNext());
        }

        return list;
    }

    public ArrayList<ThongBao> getAll() {
        String sqlGetAll = "SELECT * FROM THONGBAO";

        return get(sqlGetAll);
    }

    public ArrayList<ThongBao> getByTrangThaiChuaXem() {
        String sqlGetAll = "SELECT * FROM THONGBAO WHERE trangThai=?";

        return get(sqlGetAll, String.valueOf(ThongBao.STATUS_CHUA_XEM));
    }

    public boolean insertThongBao(ThongBao thongBao) {
        SQLiteDatabase sqLiteDatabase = coffeeDB.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("trangThai", thongBao.getTrangThai());
        values.put("noiDung", thongBao.getNoiDung());
        values.put("ngayThongBao", XDate.toStringDate(thongBao.getNgayThongBao()));
        long check = sqLiteDatabase.insert("THONGBAO", null, values);
        return check != -1;
    }

    public boolean updateThongBao(ThongBao thongBao) {
        SQLiteDatabase sqLiteDatabase = coffeeDB.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("trangThai", thongBao.getTrangThai());
        int check = sqLiteDatabase.update("THONGBAO", values, "maThongBao=?", new String[]{String.valueOf(thongBao.getMaThongBao())});
        return check > 0;
    }


    public boolean deleteThongBao(String maThongBao) {
        SQLiteDatabase sqLiteDatabase = coffeeDB.getWritableDatabase();
        int check = sqLiteDatabase.delete("THONGBAO", "maThongBao=?", new String[]{maThongBao});
        return check > 0;
    }


}
