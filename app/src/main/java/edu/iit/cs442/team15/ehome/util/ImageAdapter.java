package edu.iit.cs442.team15.ehome.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class ImageAdapter extends PagerAdapter {

    private Context mContext;
    private int[] imageIds;
    private OnImageClickedListener mListener;

    public ImageAdapter(Context context, @NonNull int[] imageIds, @Nullable OnImageClickedListener mListener) {
        this.mContext = context;
        this.imageIds = imageIds;
        this.mListener = mListener;
    }

    @Override
    public int getCount() {
        return imageIds.length;
    }

    @Override
    public boolean isViewFromObject(View v, Object obj) {
        return v == ((ImageView) obj);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int i) {
        ImageView imageView = new ImageView(mContext);

        //imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setImageResource(imageIds[i]);
        if (mListener != null)
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onImageClicked(i);
                }
            });

        container.addView(imageView, 0);

        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int i, Object obj) {
        container.removeView((ImageView) obj);
    }

    public interface OnImageClickedListener {
        void onImageClicked(int position);
    }

}