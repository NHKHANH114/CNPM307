package fpt.edu.aptcoffee.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import fpt.edu.aptcoffee.fragment.HomeFragment;
import fpt.edu.aptcoffee.fragment.MeseegerFragment;
import fpt.edu.aptcoffee.fragment.SearchFragment;
import fpt.edu.aptcoffee.fragment.SettingFragment;

public class ViewPagerMainAdapter extends FragmentStateAdapter {
    public ViewPagerMainAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1:
                return new SearchFragment();
            case 2:
                return new MeseegerFragment();
            case 3:
                return new SettingFragment();
            case 0:
            default:
                return new HomeFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
