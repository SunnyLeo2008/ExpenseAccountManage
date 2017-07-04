package com.leosoft.eam.about;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.leosoft.eam.R;

public class AboutActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private CardPagerAdapter mCardAdapter;
    private ShadowTransformer mCardShadowTransformer;
    private ImageButton imgBtnAboutBack;
    private TextView tvAppVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        mViewPager = (ViewPager) findViewById(R.id.view_pager_about);
        imgBtnAboutBack = (ImageButton) findViewById(R.id.imageButtonAboutBack);
        tvAppVersion = (TextView) findViewById(R.id.app_version);

        String tempStr = "Ver " + tvAppVersion.getText().toString();
        tvAppVersion.setText(tempStr);

        mCardAdapter = new CardPagerAdapter();
        mCardAdapter.addCardItem(new CardItem(R.string.about_title_1, R.string.about_text_1));
        mCardAdapter.addCardItem(new CardItem(R.string.about_title_2, R.string.about_text_2));
        mCardAdapter.addCardItem(new CardItem(R.string.about_title_3, R.string.about_text_3));
        mCardAdapter.addCardItem(new CardItem(R.string.about_title_4, R.string.about_text_4));
        mCardAdapter.addCardItem(new CardItem(R.string.about_title_5, R.string.about_text_5));

        mCardShadowTransformer = new ShadowTransformer(mViewPager, mCardAdapter);
        mCardShadowTransformer.enableScaling(true);

        mViewPager.setAdapter(mCardAdapter);
        mViewPager.setPageTransformer(false, mCardShadowTransformer);
        mViewPager.setOffscreenPageLimit(4);

        imgBtnAboutBack.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
