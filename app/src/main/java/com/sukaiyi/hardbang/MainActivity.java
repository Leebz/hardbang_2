package com.sukaiyi.hardbang;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.sukaiyi.hardbang.bean.Organization;
import com.sukaiyi.hardbang.bean.Post;
import com.sukaiyi.hardbang.fragments.MainColumnFragment;
import com.sukaiyi.hardbang.fragments.MainListFragment;
import com.sukaiyi.hardbang.fragments.MainMenuFragment;
import com.sukaiyi.hardbang.fragments.MainMessageFragment;
import com.sukaiyi.hardbang.fragments.OnFragmentInteractionListener;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.listener.ConnectListener;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

public class MainActivity extends AppCompatActivity implements OnFragmentInteractionListener {

    private ViewPager mViewPager;
    private BottomNavigationView navigation;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_list:
                    mViewPager.setCurrentItem(0);
                    return true;
                case R.id.navigation_column:
                    mViewPager.setCurrentItem(1);
                    return true;
                case R.id.navigation_message:
                    mViewPager.setCurrentItem(2);
                    return true;
                case R.id.navigation_menu:
                    mViewPager.setCurrentItem(3);
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final BmobUser bmobUser = BmobUser.getCurrentUser(this);
        if(bmobUser == null){
            Intent intent = new Intent(MainActivity.this,LoginActivity.class);
            startActivity(intent);
            this.finish();
            return;
        }else{
            Log.d("MainActivity", bmobUser.getEmail()+"");
            Log.d("MainActivity", bmobUser.getUsername()+"");
        }

        setContentView(R.layout.activity_main);

        mViewPager = (ViewPager) findViewById(R.id.viewpager);

        MainListFragment f1 = MainListFragment.newInstance(this);
        MainColumnFragment f2 = MainColumnFragment.newInstance(this);
        MainMessageFragment f3 = MainMessageFragment.newInstance(this);
        MainMenuFragment f4 = MainMenuFragment.newInstance(this);

        final ArrayList<Fragment> fragments = new ArrayList<Fragment>();
        fragments.add(f1);
        fragments.add(f2);
        fragments.add(f3);
        fragments.add(f4);

        mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public int getCount()
            {
                return fragments.size();
            }

            @Override
            public android.support.v4.app.Fragment getItem(int arg)
            {
                return fragments.get(arg);
            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                int[] arr = {
                        R.id.navigation_list,
                        R.id.navigation_column,
                        R.id.navigation_message,
                        R.id.navigation_menu
                };
                MainActivity.this.navigation.setSelectedItemId(arr[position]);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        BmobIM.connect(bmobUser.getObjectId(), new ConnectListener() {
            @Override
            public void done(String uid, BmobException e) {
                if (e == null) {
                    Log.d("MainActivity", "BmobIM connect success");
                } else {
                    Log.e("MainActivity", e.getErrorCode()+e.getMessage());
                }
            }
        });

        BmobIM.getInstance().loadAllConversation();

    }

    @Override
    public void onFragmentInteraction(Bundle bundle) {
        int action = bundle.getInt("action",-1);
        if(action==OnFragmentInteractionListener.ACTION_LOGOUT){
            Intent intent = new Intent(MainActivity.this,LoginActivity.class);
            startActivity(intent);
            MainActivity.this.finish();
        }
    }
}
