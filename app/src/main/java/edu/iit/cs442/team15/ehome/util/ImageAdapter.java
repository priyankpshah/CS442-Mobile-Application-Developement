package edu.iit.cs442.team15.ehome.util;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Random;

import edu.iit.cs442.team15.ehome.R;

/**
 * Created by Priyank on 4/29/2016.
 */
public class ImageAdapter extends PagerAdapter {
    Context mcontext;
    final Random rand = new Random();


    public ImageAdapter(Context context){
        this.mcontext = context;
    }
    @Override
    public int getCount() {
        return sliderImagesId.length;
    }

    private int[] sliderImagesId = new int[]{
            R.drawable.img1, R.drawable.img2, R.drawable.img3,
            R.drawable.img4, R.drawable.img5, R.drawable.img6,
    };
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((ImageView)object);
    }
    @Override
    public Object instantiateItem(ViewGroup container, int i) {
        ImageView mImageView = new ImageView(mcontext);
        mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        mImageView.setImageResource(sliderImagesId[i]);
        ((ViewPager) container).addView(mImageView, 0);
        return mImageView;
    }
    @Override
    public void destroyItem(ViewGroup container, int i, Object obj) {
        ((ViewPager) container).removeView((ImageView) obj);
    }
}
