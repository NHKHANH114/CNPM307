package fpt.edu.aptcoffee.interfaces;

import android.view.View;

import fpt.edu.aptcoffee.model.HangHoa;
import fpt.edu.aptcoffee.model.HoaDonChiTiet;

public interface ItemTangGiamSoLuongOnClick {
    void itemOclick(View view, int indext, HoaDonChiTiet hoaDonChiTiet, HangHoa hangHoa);
    void itemOclickDeleteHDCT(View view, HoaDonChiTiet hoaDonChiTiet);
}
