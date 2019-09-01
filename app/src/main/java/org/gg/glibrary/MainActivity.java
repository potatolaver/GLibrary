package org.gg.glibrary;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import org.gg.glibrary.adapter.SubAdapter1;

import androidx.viewpager2.widget.ViewPager2;

public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected int setDecorViewBackgroundResId() {
        return R.drawable.background_white;
    }

    @Override
    protected boolean needDarkStatusBar() {
        return true;
    }

    @Override
    protected boolean hasToolbar() {
        return false;
    }

    @Override
    protected boolean needSwipeBack() {
        return false;
    }

    @Override
    protected int setLayoutResId() {
        return R.layout.activity_main;
    }

    @Override
    protected void init() {
        try {
            ApplicationInfo info = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            String channel = info.metaData.getString("ATMAN_CHANNEL");
            Log.e(TAG, "channel = " + channel);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

//        ViewPager viewPager = findViewById(R.id.viewpager);
//        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
//            @Override
//            public Fragment getItem(int position) {
//                CommonFragment fragment = new CommonFragment();
//                Bundle bundle = new Bundle();
//                bundle.putInt("position", position);
//                fragment.setArguments(bundle);
//                return fragment;
//            }
//
//            @Override
//            public int getCount() {
//                return 4;
//            }
//        });

//        findViewById(R.id.button).setOnClickListener(v -> startActivity(new Intent(this, CompressActivity.class)));


        ViewPager2 recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setOrientation(ViewPager2.ORIENTATION_VERTICAL);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new SubAdapter1());
//        LinearSnapHelper helper = new LinearSnapHelper();
//        helper.attachToRecyclerView(recyclerView);
    }
}
