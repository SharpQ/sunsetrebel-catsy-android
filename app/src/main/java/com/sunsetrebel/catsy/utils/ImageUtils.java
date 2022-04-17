package com.sunsetrebel.catsy.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.sunsetrebel.catsy.R;

public class ImageUtils {
    public interface IconLoadCallback {
        void onLoad(Bitmap scaledBitmap);
    }

    public static void loadImageView(Context context, String imageURL, ImageView imageView, int errorDrawableResourceId) {
        RequestOptions defaultOptionsEventAvatar = new RequestOptions()
                .error(errorDrawableResourceId);
        Glide.with(context)
                .setDefaultRequestOptions(defaultOptionsEventAvatar)
                .load(imageURL)
                .into(imageView);
    }

    public static void loadRoundedImageView(Context context, String imageURL, ImageView imageView, int errorDrawableResourceId) {
        RequestOptions defaultOptionsEventAvatar = new RequestOptions()
                .error(errorDrawableResourceId);
        Glide.with(context)
                .setDefaultRequestOptions(defaultOptionsEventAvatar)
                .load(imageURL)
                .apply(RequestOptions.circleCropTransform())
                .into(imageView);
    }

    public static void loadBitmapMapIcons(IconLoadCallback iconLoadCallback, Context context, String eventAvatarURL) {
        final float scale = context.getResources().getDisplayMetrics().density;
        int pixels = (int) (35 * scale + 0.5f);
        Glide.with(context)
                .asBitmap()
                .load(eventAvatarURL)
                .apply(RequestOptions.circleCropTransform())
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        Bitmap errowDrawable = ((BitmapDrawable) ContextCompat.getDrawable(context, R.drawable.im_event_avatar_placeholder_64)).getBitmap();
                        errowDrawable = ImageUtils.getCroppedBitmap(errowDrawable);
                        Bitmap scaledBitmap = Bitmap.createScaledBitmap(errowDrawable, pixels, pixels, true);
                        iconLoadCallback.onLoad(scaledBitmap);
                    }

                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        Bitmap bitmap = Bitmap.createScaledBitmap(resource, pixels, pixels, true);
                        iconLoadCallback.onLoad(bitmap);
                    }});
    }

    private static Bitmap getCroppedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }
}
