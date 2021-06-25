package com.example.homework3.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.homework3.FM.SettingsFragment;
import com.example.homework3.FM.crosslinkFragment;
import com.example.homework3.FM.dcardFragment;
import com.example.homework3.FM.pttFragment;

public class FragmentAdapter extends FragmentStateAdapter {

    String search;

    public FragmentAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, String search) {
        super(fragmentManager, lifecycle);
        this.search = search;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        switch (position)
        {
            case 1 :
                return new dcardFragment(search);
            case 2 :
                return new crosslinkFragment(search);
        }
        return new pttFragment(search);
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
