package com.sunsetrebel.catsy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;

public class SliderAdapter extends PagerAdapter {
    Context context;
    LayoutInflater layoutInflater;

    public SliderAdapter(Context context) {
        this.context = context;
    }

    public int[] slideImages = {
            R.drawable.christina_unsplash,
            R.drawable.bez_nazvan,
            R.drawable.the_meetup
    };

    public int[] slideTitles = {
            R.string.text_slide_1,
            R.string.text_slide_2,
            R.string.text_slide_3
        };

    @Override
    public int getCount() {
        return slideTitles.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slide_layout, container, false);

        ImageView slideImageView = view.findViewById(R.id.slideImage);
        TextView slideText = view.findViewById(R.id.slideText);

        slideImageView.setImageResource(slideImages[position]);
        slideText.setText(slideTitles[position]);

        container.addView(view);
        return view;
    }

    public void destroyItem(ViewGroup container, int position, @NonNull Object object) {
        container.removeView((ConstraintLayout) object);
    }
}
