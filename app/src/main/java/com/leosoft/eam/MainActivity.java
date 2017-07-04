package com.leosoft.eam;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationViewPager;
import com.leosoft.eam.utils.FileUtils;
import com.leosoft.eam.utils.InfoText;
import com.leosoft.eam.utils.SystemApplication;

import java.io.File;
import java.io.IOException;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity extends AppCompatActivity {

    private AHBottomNavigation bottomNavigation;
    private AHBottomNavigationViewPager mainViewPager;
    private MainFragment currentFragment;
    private MainViewPagerAdapter mainViewPagerAdapter;

    private float beginY = 0;
    private float endY = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SystemApplication.getInstance().addActivity(this);

        boolean enabledTranslucentNavigation = getSharedPreferences("shared", Context.MODE_PRIVATE)
                .getBoolean("translucentNavigation", false);
        setTheme(enabledTranslucentNavigation ? R.style.AppTheme_TranslucentNavigation : R.style.AppTheme);

        setContentView(R.layout.activity_main);

        File file = new File(getApplicationContext().getFilesDir() + "/images");
        if (!file.exists()) {
            try {
                FileUtils.copyAssetDirToFiles(getApplicationContext(), "images");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //初始化底部工具栏的UI
        initUI();
    }

    //初始化底部工具栏的UI
    private void initUI() {

        bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);
        mainViewPager = (AHBottomNavigationViewPager) findViewById(R.id.main_view_pager);

        AHBottomNavigationItem item1 = new AHBottomNavigationItem(R.string.tab_1, R.drawable.ic_bill_add, R.color.color_tab_1);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem(R.string.tab_2, R.drawable.ic_bill_delete, R.color.color_tab_2);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem(R.string.tab_3, R.drawable.ic_setup, R.color.color_tab_3);

        bottomNavigation.addItem(item1);
        bottomNavigation.addItem(item2);
        bottomNavigation.addItem(item3);

        bottomNavigation.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);
        bottomNavigation.setTranslucentNavigationEnabled(true);

        mainViewPager.setOffscreenPageLimit(3);
        mainViewPagerAdapter = new MainViewPagerAdapter(getSupportFragmentManager());
        mainViewPager.setAdapter(mainViewPagerAdapter);

        currentFragment = mainViewPagerAdapter.getCurrentFragment();

        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {

                if (currentFragment == null) {
                    currentFragment = mainViewPagerAdapter.getCurrentFragment();
                }

                if (wasSelected) {
                    currentFragment.refresh(position);
                    return true;
                }

                if (currentFragment != null) {
                    currentFragment.willBeHidden();
                }

                mainViewPager.setCurrentItem(position, false);
                currentFragment = mainViewPagerAdapter.getCurrentFragment();
                currentFragment.willBeDisplayed(position);

                return true;
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            String contextString = MainActivity.this.toString();
            if ("MainActivity".equals(contextString.substring(contextString.lastIndexOf(".") + 1, contextString.indexOf("@")))) {
                new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText(InfoText.TEXT_ASK_TITLE)
                        .setContentText(InfoText.TEXT_ASK_EXIT)
                        .setCancelText(InfoText.TEXT_CANCEL)
                        .setConfirmText(InfoText.TEXT_CONFIRM)
                        .showCancelButton(true)
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.cancel();
                            }
                        })
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {

                                SystemApplication.getInstance().exit();

                                sDialog.dismissWithAnimation();
                            }
                        })
                        .show();
            }

        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            beginY = event.getY();
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            endY = event.getY();

            if ((beginY - endY) != 0) {
                bottomNavigation.restoreBottomNavigation();
            }
        }

        return super.onTouchEvent(event);
    }


}
