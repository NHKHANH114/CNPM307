package fpt.edu.aptcoffee.dao;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

import fpt.edu.aptcoffee.database.CoffeeDB;
import fpt.edu.aptcoffee.model.Ban;
import fpt.edu.aptcoffee.model.LoaiHang;

public class BanDAO {
    CoffeeDB coffeeDB;

    public BanDAO(Context context) {
        this.coffeeDB = new CoffeeDB(context);
    }

    @SuppressLint("Range")
    public ArrayList<Ban> get(String sql, String... choose) {
        ArrayList<Ban> list = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = coffeeDB.getReadableDatabase();
        @SuppressLint("Recycle") Cursor cursor = sqLiteDatabase.rawQuery(sql, choose);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                Ban ban = new Ban();
                ban.setMaBan(cursor.getInt(cursor.getColumnIndex("maBan")));
                ban.setTrangThai(cursor.getInt(cursor.getColumnIndex("trangThai")));
                list.add(ban);
                Log.i("TAG", ban.toString());
            } while (cursor.moveToNext());
        }

        return list;
    }

    public ArrayList<Ban> getAll() {
        String sqlGetAll = "SELECT * FROM BAN";

        return get(sqlGetAll);
    }

    public boolean insertBan(Ban ban) {
        SQLiteDatabase sqLiteDatabase = coffeeDB.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("trangThai", ban.getTrangThai());
        long check = sqLiteDatabase.insert("BAN", null, values);

        return check != -1;
    }

    public boolean deleteBan(String maBan) {
        SQLiteDatabase sqLiteDatabase = coffeeDB.getWritableDatabase();
        int check = sqLiteDatabase.delete("BAN", "maBan=?", new String[]{maBan});

        return check > 0;
    }

    public boolean updateBan(Ban ban) {
        SQLiteDatabase sqLiteDatabase = coffeeDB.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("trangThai", ban.getTrangThai());
        int check = sqLiteDatabase.update("BAN", values, "maBan=?", new String[]{String.valueOf(ban.getMaBan())});
        return check > 0;
    }

    public Ban getByMaBan(String maBan) {
        String sqlGetByMaLoai = "SELECT * FROM BAN WHERE maBan=?";
        ArrayList<Ban> list = get(sqlGetByMaLoai, maBan);

        return list.get(0);
    }
}
