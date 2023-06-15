package com.studentguide.utils.imagecrop;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * A class that resize a image
 */
public class ImageResize {
    //private Context mContext;
    private int mWidth;
    private int mHeight;
    private Uri mImageUri;
    private BitmapFactory.Options mBitMapOptions;
    private Bitmap mBitMap;
    private Bitmap tempBitMap;

    public ImageResize(Context context, int width, int height, Uri imgUri) {
        //this.mContext = context;
        this.mWidth = width;
        this.mHeight = height;
        this.mImageUri = imgUri;
    }

    public ImageResize(Context context, int width, int height, Bitmap bitmap) {
        //this.mContext = context;
        this.mWidth = width;
        this.mHeight = height;
        this.mBitMap = bitmap;
    }


    public Bitmap getResizeImage() {
        //ContentResolver resolver = mContext.getContentResolver();
        mBitMapOptions = new BitmapFactory.Options();

        if (mImageUri != null) {
            ParcelFileDescriptor fd = null;
            try {

                File f = new File(mImageUri.toString());

                fd = ParcelFileDescriptor.open(f, ParcelFileDescriptor.MODE_READ_WRITE);
                int sampleSize = 1;

                mBitMapOptions.inJustDecodeBounds = true;
                BitmapFactory.decodeFileDescriptor(fd.getFileDescriptor(), null, mBitMapOptions);

                int nextWidth = mBitMapOptions.outWidth >> 1;
                int nextHeight = mBitMapOptions.outHeight >> 1;

                while (nextWidth > mWidth && nextHeight > mHeight) {
                    sampleSize <<= 1;
                    nextWidth >>= 1;
                    nextHeight >>= 1;
                }

                mBitMapOptions.inSampleSize = sampleSize;
                mBitMapOptions.inJustDecodeBounds = false;

                mBitMap = BitmapFactory.decodeFileDescriptor(fd.getFileDescriptor(), null, mBitMapOptions);
                if (mBitMap != null) {
                    if (mBitMapOptions.outWidth != mWidth || mBitMapOptions.outHeight != mHeight) {
                        //??????? :  ???? ????????  =  (???? ???????? * ?????????) / ?????????
                        tempBitMap = Bitmap.createScaledBitmap(mBitMap, mWidth, mHeight, true);
                        mBitMap.recycle();
                        mBitMap = tempBitMap;
                    }
                }

                return mBitMap;

            } catch (FileNotFoundException e) {
            } finally {
                try {
                    if (fd != null) fd.close();
                } catch (IOException e) {
                }
                if (mBitMap != null) mBitMap = null;
                if (tempBitMap != null) tempBitMap = null;
            }
        }
        return null;
    }


    public Bitmap getResizeBitmap() {
        //ContentResolver resolver = mContext.getContentResolver();
        mBitMapOptions = new BitmapFactory.Options();

        if (mBitMap != null) {

            try {


                int sampleSize = 1;

                mBitMapOptions.inJustDecodeBounds = true;

                int nextWidth = mBitMapOptions.outWidth >> 1;
                int nextHeight = mBitMapOptions.outHeight >> 1;

                while (nextWidth > mWidth && nextHeight > mHeight) {
                    sampleSize <<= 1;
                    nextWidth >>= 1;
                    nextHeight >>= 1;
                }

                mBitMapOptions.inSampleSize = sampleSize;
                mBitMapOptions.inJustDecodeBounds = false;


                if (mBitMap != null) {
                    if (mBitMapOptions.outWidth != mWidth || mBitMapOptions.outHeight != mHeight) {

                        tempBitMap = Bitmap.createScaledBitmap(mBitMap, mWidth, mHeight, true);
                        mBitMap.recycle();
                        mBitMap = tempBitMap;
                    }
                }

                return mBitMap;

            } catch (Exception e) {
            } finally {

                if (mBitMap != null) mBitMap = null;
                if (tempBitMap != null) tempBitMap = null;
            }
        }
        return null;
    }
}
