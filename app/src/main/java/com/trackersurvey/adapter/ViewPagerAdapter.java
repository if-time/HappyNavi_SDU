package com.trackersurvey.adapter;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.trackersurvey.happynavi.R;

import java.io.File;
import java.util.List;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class ViewPagerAdapter extends PagerAdapter {

    private Context      mContext;
    private Activity     mActivity;
    private List<String> mImgList;
    private boolean      showToolbar = false;
    private ActionBar    mActionBar;

    private MediaController mediaController;

    boolean isPlay = false;

    private boolean isShowPlayBtn = true;


    private OnItemClickListener mListener;

    public void setOnItemClickListener(OnItemClickListener l) {
        mListener = l;
    }

    public interface OnItemClickListener {
        //        void onItemClick(int position, Photo image);
        void onItemClick(int position, View view);

    }


    public ViewPagerAdapter(Context context, List<String> imgList) {
        this.mContext = context;
        this.mImgList = imgList;
        this.mActivity = (Activity) context;

    }

    @Override
    public int getCount() {
        return mImgList == null ? 0 : mImgList.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        final View imageLayout = LayoutInflater.from(mContext).inflate(R.layout.item_image_pager, null);
        assert imageLayout != null;

        final ProgressBar progressBar = (ProgressBar) imageLayout.findViewById(R.id.loading);
        final PhotoView photoView = (PhotoView) imageLayout.findViewById(R.id.photoview);
        final ImageView imgPlay = (ImageView) imageLayout.findViewById(R.id.img_play);
        final VideoView videoView = (VideoView) imageLayout.findViewById(R.id.video_view);

        final String path = mImgList.get(position);

        imgPlay.setVisibility(View.GONE);
        videoView.setVisibility(View.GONE);
        photoView.setVisibility(View.VISIBLE);

        Glide.with(mContext)
                .load(path)
                .into(new SimpleTarget<GlideDrawable>() {
                    @Override
                    public void onResourceReady(GlideDrawable glideDrawable, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        photoView.setImageDrawable(glideDrawable);
                    }
                });
                //                .error(R.mipmap.ic_launcher)

        photoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onItemClick(position, v);
                }
            }
        });


        photoView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {

            @Override
            public void onPhotoTap(View arg0, float arg1, float arg2) {
                if (showToolbar) {
                    Log.d("ViewPagerAdapter", "show tool bar");
                } else {
                    Log.d("ViewPagerAdapter", "hide tool bar");
                }
                showToolbar = !showToolbar;
            }

        });


        container.addView(imageLayout);


        return imageLayout;

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }


    private static boolean isVideoFile(String sourcePath) {
        String type = sourcePath.substring(sourcePath.length() - 4, sourcePath.length());
        return type.equals(".mp4") ? true : false;

    }

    private static final String videoThumbUri = "/storage/emulated/0/DCIM/.thumbnails/";

    private String createVideoThumbnail(String thumbFromCP, String videoPath) {

        String thumbPath = videoThumbUri + videoPath.substring(39, videoPath.length() - 4) + ".jpg";

        return thumbFromCP == null ? thumbPath : thumbFromCP;
    }

}
