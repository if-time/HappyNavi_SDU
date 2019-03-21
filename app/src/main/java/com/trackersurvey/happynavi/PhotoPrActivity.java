package com.trackersurvey.happynavi;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.trackersurvey.adapter.ViewPagerAdapter;
import com.trackersurvey.view.ShowImagesViewPager;

import java.util.ArrayList;
import java.util.List;


public class PhotoPrActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = PhotoPrActivity.class.getName();

    ViewPager bigImgVp;

    private       int position;
    public static int deletePosition;

    ViewPagerAdapter mImagePagerAdapter;

    public static final String       PIC_PATH = "path";
    private             List<String> imagePath;

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_pr);
        Intent intent = getIntent();
        position = intent.getIntExtra("position", 0);
        initView();
        initViewPager();
        bigImgVp.setCurrentItem(position);
    }

    private void initView() {
        bigImgVp = (ShowImagesViewPager) findViewById(R.id.big_img_vp);
        bigImgVp.setOnClickListener(this);

        bigImgVp = (ShowImagesViewPager) findViewById(R.id.big_img_vp);

        intent = getIntent();

        imagePath = intent.getStringArrayListExtra(PIC_PATH);
        Log.i("bitmap", "imagePath=" + imagePath + "  imagePath.size() " + imagePath.size());
    }

    /**
     * 初始化ViewPager
     */
    private void initViewPager() {
        mImagePagerAdapter = new ViewPagerAdapter(this, imagePath);

        bigImgVp.setAdapter(mImagePagerAdapter);

        bigImgVp.setOnTouchListener(new View.OnTouchListener() {
            int touchFlag = 0;
            float x = 0, y = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        touchFlag = 0;
                        x = event.getX();
                        y = event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float xDiff = Math.abs(event.getX() - x);
                        float yDiff = Math.abs(event.getY() - y);
                        if (xDiff == 0 && yDiff == 0) {
                            touchFlag = 0;
                        } else {
                            touchFlag = -1;
                        }
                        /*if (xDiff < mTouchSlop && xDiff >= yDiff)
                            touchFlag = 0;
                        else
                            touchFlag = -1;*/
                        break;
                    case MotionEvent.ACTION_UP:
                        if (touchFlag == 0) {
//                            Toast.makeText(PhotoPrActivity.this, "click_setOnTouchListener", Toast.LENGTH_LONG).show();

                        } else if (touchFlag == -1) {

                        }
                        break;
                }
                return false;
            }
        });

        mImagePagerAdapter.setOnItemClickListener(new ViewPagerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View view) {

            }
        });

        bigImgVp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    public void onClick(View v) {

    }
}
