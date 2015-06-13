package com.roofnfloors.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.mapslist.R;

public class ImagePagerAdapter extends PagerAdapter {

    private Context context;
    private Drawable drawable[];
    private LayoutInflater inflater;
    private HolderView[] mHolderView;

    @Override
    public int getCount() {
        return drawable.length;
    }

    public ImagePagerAdapter(Context context, Drawable[] image) {
        this.context = context;
        this.drawable = image;
        this.mHolderView = new HolderView[image.length];
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    public HolderView[] getHolderViews() {
        return mHolderView;
    }

    @Override
    public Object instantiateItem(View container, int position) {

        // Declare Variables
        ImageView planImage;

        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mHolderView[position] = new HolderView();
        View itemView = inflater.inflate(R.layout.imagepager_item,
                (ViewGroup) container, false);
        // Locate the ImageView in imagepager_item.xml
        planImage = (ImageView) itemView.findViewById(R.id.plan_image);
        // Capture position and set to the ImageView
        planImage.setBackground(drawable[position]);
        mHolderView[position].setView(planImage);
        // Add viewpager_item.xml to ViewPager
        ((ViewPager) container).addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // Remove viewpager_item.xml from ViewPager
        ((ViewPager) container).removeView((RelativeLayout) object);

    }

    public class HolderView {
        ImageView view;

        public void setView(ImageView v) {
            this.view = v;
        }

        public ImageView getView() {
            return view;
        }
    }

}
