package fpt.edu.aptcoffee.model;

import androidx.annotation.NonNull;

import java.util.Date;

public class ThongBao {
    private int maThongBao;
    private int trangThai;
    private String noiDung;
    private Date ngayThongBao;
    public static final int STATUS_DA_XEM = 1;  // trạng thái đã xem
    public static final int STATUS_CHUA_XEM = 0; // trạng thái chưa xem
    public ThongBao() {
    }

    public ThongBao(int maThongBao, int trangThai, String noiDung, Date ngayThongBao) {
        this.maThongBao = maThongBao;
        this.trangThai = trangThai;
        this.noiDung = noiDung;
        this.ngayThongBao = ngayThongBao;
    }

    public int getMaThongBao() {
        return maThongBao;
    }

    public void setMaThongBao(int maThongBao) {
        this.maThongBao = maThongBao;
    }

    public int getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(int trangThai) {
        this.trangThai = trangThai;
    }

    public String getNoiDung() {
        return noiDung;
    }

    public void setNoiDung(String noiDung) {
        this.noiDung = noiDung;
    }

    public Date getNgayThongBao() {
        return ngayThongBao;
    }

    public void setNgayThongBao(Date ngayThongBao) {
        this.ngayThongBao = ngayThongBao;
    }

    @NonNull
    @Override
    public String toString() {
        return "ThongBao{" +
                "maThongBao=" + maThongBao +
                ", trangThai=" + trangThai +
                ", noiDung='" + noiDung + '\'' +
                ", ngayThongBao=" + ngayThongBao +
                '}';
    }
}
