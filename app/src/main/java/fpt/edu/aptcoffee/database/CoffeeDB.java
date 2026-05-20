package fpt.edu.aptcoffee.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import fpt.edu.aptcoffee.R;
import fpt.edu.aptcoffee.model.ThongBao;
import fpt.edu.aptcoffee.utils.ImageToByte;
import fpt.edu.aptcoffee.utils.XDate;

public class CoffeeDB extends SQLiteOpenHelper {
    public static final String DB_NAME = "APTCoffee";
    public static final int DB_VERSION = 1;
    public Context context;
    public static final String TABLE_BAN = "CREATE TABLE BAN(" +
            "maBan INTEGER PRIMARY KEY AUTOINCREMENT," +
            "trangThai INTEGER NOT NULL)";

    public static final String TABLE_NGUOIDUNG = "CREATE TABLE NGUOIDUNG(" +
            "maNguoiDung TEXT PRIMARY KEY," +
            "hoVaTen TEXT NOT NULL," +
            "hinhAnh BLOB," +
            "ngaySinh DATE NOT NULL," +
            "email TEXT NOT NULL," +
            "chucVu TEXT NOT NULL," +
            "gioiTinh TEXT NOT NULL," +
            "matKhau TEXT NOT NULL)";

    public static final String TABLE_LOAIHANG = "CREATE TABLE LOAIHANG(" +
            "maLoai INTEGER PRIMARY KEY AUTOINCREMENT," +
            "hinhAnh BLOB," +
            "tenLoai TEXT NOT NULL)";

    public static final String TABLE_HOADON = "CREATE TABLE HOADON(" +
            "maHoaDon INTEGER PRIMARY KEY AUTOINCREMENT," +
            "maBan INTEGER REFERENCES BAN(maBan)," +
            "gioVao DATE NOT NULL," +
            "gioRa DATE NOT NULL," +
            "trangThai INTEGER NOT NULL)";

    public static final String TABLE_HANGHOA = "CREATE TABLE HANGHOA(" +
            "maHangHoa INTEGER PRIMARY KEY AUTOINCREMENT," +
            "hinhAnh BLOB," +
            "tenHangHoa TEXT NOT NULL," +
            "giaTien INTEGER NOT NULL, " +
            "maLoai INTEGER REFERENCES LOAIHANG(maLoai)," +
            "trangThai INTEGER NOT NULL)";

    public static final String TABLE_HOADONCHITIET = "CREATE TABLE HOADONCHITIET(" +
            "maHDCT INTEGER PRIMARY KEY AUTOINCREMENT," +
            "maHoaDon INTEGER REFERENCES HOADON(maHoaDon)," +
            "maHangHoa INTEGER REFERENCES HANGHOA(maHangHoa)," +
            "soLuong INTEGER NOT NULL," +
            "giaTien INTEGER NOT NULL, ghiChu TEXT," +
            "ngayXuatHoaDon DATE NOT NULL)";

    public static final String TABLE_THONGBAO = "CREATE TABLE THONGBAO(" +
            "maThongBao INTEGER PRIMARY KEY AUTOINCREMENT," +
            "trangThai INTEGER NOT NULL," +
            "noiDung TEXT NOT NULL," +
            "ngayThongBao DATE NOT NULL)";

    public CoffeeDB(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // TẠO TABLE BÀN
        sqLiteDatabase.execSQL(TABLE_BAN);
        // TẠO TABLE NGƯỜI DUNG
        sqLiteDatabase.execSQL(TABLE_NGUOIDUNG);
        // TẠO TABLE LOẠI HÀNG
        sqLiteDatabase.execSQL(TABLE_LOAIHANG);
        // TẠO TABLE HÓA ĐƠN
        sqLiteDatabase.execSQL(TABLE_HOADON);
        // TẠO TABLE HÀNG HÓA
        sqLiteDatabase.execSQL(TABLE_HANGHOA);
        // TẠO TABLE HÓA ĐƠN CHI TIẾT
        sqLiteDatabase.execSQL(TABLE_HOADONCHITIET);
        // TẠO TABLE HÓA ĐƠN THÔNG BÁO
        sqLiteDatabase.execSQL(TABLE_THONGBAO);

        String insertBan = "INSERT INTO BAN(trangThai) VALUES(?)";
        for (int i = 0; i < 12; i++) {
            sqLiteDatabase.execSQL(insertBan, new Object[]{0});
        }
        String insertNguoiDung = "INSERT INTO NGUOIDUNG(maNguoiDung, hoVaTen, hinhAnh, ngaySinh, email, chucVu, gioiTinh, matKhau) VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
        sqLiteDatabase.execSQL(insertNguoiDung, new Object[]{"admin", "ADMIN", ImageToByte.drawableToByte(context, R.drawable.avatar_user_md), "2003-01-01", "admin@gmail.com", "Admin", "Nam", 1212});
        sqLiteDatabase.execSQL(insertNguoiDung, new Object[]{"nhanvien", "Nguyễn Viết Tín", ImageToByte.drawableToByte(context, R.drawable.avatar_user_md), "2003-01-01", "tinthq@gmail.com", "NhanVien", "Nam", 1212});
        sqLiteDatabase.execSQL(insertNguoiDung, new Object[]{"ND2", "Trần Hồ Quốc An", ImageToByte.drawableToByte(context, R.drawable.avatar_user_md), "2003-01-01", "anthq@gmail.com", "NhanVien", "Nam", 1212});
        sqLiteDatabase.execSQL(insertNguoiDung, new Object[]{"ND3", "Hồ Minh Phú", ImageToByte.drawableToByte(context, R.drawable.avatar_user_md), "2003-01-01", "phuhm@gmail.com", "NhanVien", "Nam", 1212});

        String insertLoaiHang = "INSERT INTO LOAIHANG(hinhAnh, tenLoai) VALUES(?, ?)";
        sqLiteDatabase.execSQL(insertLoaiHang, new Object[]{ImageToByte.drawableToByte(context, R.drawable.sample_data_loai_hang_caphe), "Cà phê"});
        sqLiteDatabase.execSQL(insertLoaiHang, new Object[]{ImageToByte.drawableToByte(context, R.drawable.sample_data_loai_hang_nuocep), "Nước ép"});
        sqLiteDatabase.execSQL(insertLoaiHang, new Object[]{ImageToByte.drawableToByte(context, R.drawable.sample_data_loai_hang_soda), "Soda"});
        sqLiteDatabase.execSQL(insertLoaiHang, new Object[]{ImageToByte.drawableToByte(context, R.drawable.sample_data_loai_hang_trasua), "Trà sữa"});

        String insertHangHoa = "INSERT INTO HANGHOA(tenHangHoa, hinhAnh, giaTien, maLoai, trangThai) VALUES(?, ?, ?, ?, ?)";
        sqLiteDatabase.execSQL(insertHangHoa, new Object[]{"Cà phê máy", ImageToByte.drawableToByte(context, R.drawable.sample_data_hanghoa_cfmay), 15000, 1, 1});
        sqLiteDatabase.execSQL(insertHangHoa, new Object[]{"Cà phê phin", ImageToByte.drawableToByte(context, R.drawable.sample_data_hanghoa_cfphin), 12000, 1, 1});
        sqLiteDatabase.execSQL(insertHangHoa, new Object[]{"Cà phê sài gòn", ImageToByte.drawableToByte(context, R.drawable.sample_data_hanghoa_cfsaigon), 20000, 1, 1});
        sqLiteDatabase.execSQL(insertHangHoa, new Object[]{"Cà phê bọt biển", ImageToByte.drawableToByte(context, R.drawable.sample_data_hanghoa_cfbotbien), 25000, 1, 0});

        sqLiteDatabase.execSQL(insertHangHoa, new Object[]{"Nước ép cam", ImageToByte.drawableToByte(context, R.drawable.sample_data_hanghoa_epcam), 27000, 2, 1});
        sqLiteDatabase.execSQL(insertHangHoa, new Object[]{"Nước ép dứa", ImageToByte.drawableToByte(context, R.drawable.sample_data_hanghoa_epdua), 25000, 2, 0});
        sqLiteDatabase.execSQL(insertHangHoa, new Object[]{"Nước ép ổi", ImageToByte.drawableToByte(context, R.drawable.sample_data_hanghoa_epoi), 23000, 2, 0});
        sqLiteDatabase.execSQL(insertHangHoa, new Object[]{"Chanh đá", ImageToByte.drawableToByte(context, R.drawable.sample_data_hanghoa_chanhda), 20000, 2, 1});

        sqLiteDatabase.execSQL(insertHangHoa, new Object[]{"Soda bạc hà", ImageToByte.drawableToByte(context, R.drawable.sample_data_loai_hang_soda_bacha), 33000, 3, 1});
        sqLiteDatabase.execSQL(insertHangHoa, new Object[]{"Soda việt quất", ImageToByte.drawableToByte(context, R.drawable.sample_data_loai_hang_soda_vietquat), 35000, 3, 0});
        sqLiteDatabase.execSQL(insertHangHoa, new Object[]{"Soda trái cây", ImageToByte.drawableToByte(context, R.drawable.sample_data_loai_hang_soda_traicay), 35000, 3, 1});

        sqLiteDatabase.execSQL(insertHangHoa, new Object[]{"Trà sữa khoai môn", ImageToByte.drawableToByte(context, R.drawable.sample_data_hanghoa_trasuamon), 23000, 4, 1});
        sqLiteDatabase.execSQL(insertHangHoa, new Object[]{"Trà sữa thái xanh", ImageToByte.drawableToByte(context, R.drawable.sample_data_hanghoa_trasuathaixanh), 24000, 4, 1});
        sqLiteDatabase.execSQL(insertHangHoa, new Object[]{"Trà sữa truyền thống", ImageToByte.drawableToByte(context, R.drawable.sample_data_hanghoa_trasuatruyenthong), 25000, 4, 1});

        String insertHoaDon = "INSERT INTO HOADON(maBan, gioVao, gioRa , trangThai) VALUES(?, ?, ?, ?)";
        sqLiteDatabase.execSQL(insertHoaDon, new Object[]{1, "25-11-2023 07:25:44", "25-11-2023 08:45:44", 1});
        sqLiteDatabase.execSQL(insertHoaDon, new Object[]{2, "27-11-2023 09:25:44", "27-11-2023 12:31:44", 1});
        sqLiteDatabase.execSQL(insertHoaDon, new Object[]{3, "27-11-2023 09:25:44", "27-11-2023 12:31:44", 1});

        String insertHoaDonChiTiet = "INSERT INTO HOADONCHITIET(maHoaDon, maHangHoa, soLuong , giaTien, ghiChu, ngayXuatHoaDon) VALUES(?, ?, ?, ?, ?, ?)";
        sqLiteDatabase.execSQL(insertHoaDonChiTiet, new Object[]{1, 1, 2, 30000, "", "2023-11-28"});
        sqLiteDatabase.execSQL(insertHoaDonChiTiet, new Object[]{1, 2, 1, 12000, "", "2023-11-29"});
        sqLiteDatabase.execSQL(insertHoaDonChiTiet, new Object[]{1, 5, 2, 54000, "", "2023-12-15"});
        sqLiteDatabase.execSQL(insertHoaDonChiTiet, new Object[]{1, 6, 2, 50000, "", "2023-01-01"});

        sqLiteDatabase.execSQL(insertHoaDonChiTiet, new Object[]{2, 1, 1, 15000, "", "2023-03-28"});
        sqLiteDatabase.execSQL(insertHoaDonChiTiet, new Object[]{2, 1, 2, 30000, "", "2023-03-29"});

        sqLiteDatabase.execSQL(insertHoaDonChiTiet, new Object[]{3, 1, 2, 30000, "", "2023-02-28"});
        sqLiteDatabase.execSQL(insertHoaDonChiTiet, new Object[]{3, 1, 2, 30000, "", "2023-02-29"});
        sqLiteDatabase.execSQL(insertHoaDonChiTiet, new Object[]{3, 1, 2, 30000, "", "2023-02-29"});

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS BAN");
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS NGUOIDUNG");
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS LOAIHANG");
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS HOADON");
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS HANGHOA");
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS HOADONCHITIET");
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS THONGBAO");

            onCreate(sqLiteDatabase);
        }

    }
}
