package com.studentguide.utils.imagecrop;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.edmodo.cropper.CropImageView;
import com.studentguide.ParentObj;
import com.studentguide.R;
import com.studentguide.utils.Logger;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;


public class ImageCropActivity extends Activity implements OnClickListener {
    // ===========================================================
    // Widgets
    // ===========================================================

    ImageButton imgbtn_imagecrop_right;
    TextView txt_imagecrop_title;
    Button btn_imagecrop_done;

    CropImageView cropImageView;
    Button btn_crop;
    ToggleButton fixedAspectRatioToggle;
    ImageView img_view_croped_image;

    LinearLayout linear_image_display;
    RelativeLayout rel_crop;

    // ===========================================================
    // Fields/Variables
    // ===========================================================
    public String TAG = "ImageResize";
    private static final int DEFAULT_ASPECT_RATIO_VALUES = 10;
    //	private static final int ROTATE_NINETY_DEGREES = 90;
    private static final String ASPECT_RATIO_X = "ASPECT_RATIO_X";
    private static final String ASPECT_RATIO_Y = "ASPECT_RATIO_Y";

    // Instance variables
    private int mAspectRatioX = DEFAULT_ASPECT_RATIO_VALUES;
    private int mAspectRatioY = DEFAULT_ASPECT_RATIO_VALUES;
    private boolean cropped = false;

    // ===========================================================
    // Methods
    // ===========================================================
    Bitmap croppedImage;
    ExifInterface exif = null;

    // ===========================================================
    // Constructors
    // ===========================================================

    // Saves the state upon rotating the screen/restarting the activity

    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putInt(ASPECT_RATIO_X, mAspectRatioX);
        bundle.putInt(ASPECT_RATIO_Y, mAspectRatioY);
    }

    // Restores the state upon rotating the screen/restarting the activity
    @Override
    protected void onRestoreInstanceState(Bundle bundle) {
        super.onRestoreInstanceState(bundle);
        mAspectRatioX = bundle.getInt(ASPECT_RATIO_X);
        mAspectRatioY = bundle.getInt(ASPECT_RATIO_Y);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_crop);

        InitViews();
        InitViewTextSize();

        cropImageView = (CropImageView) findViewById(R.id.CropImageView);
        cropImageView.setGuidelines(0);
        // Sets initial aspect ratio to 10/10, for demonstration purposes
        cropImageView.setAspectRatio(DEFAULT_ASPECT_RATIO_VALUES, DEFAULT_ASPECT_RATIO_VALUES);

        btn_crop = (Button) findViewById(R.id.btn_crop);
        btn_crop.setOnClickListener(this);

        Logger.e(TAG + "Utils.ORIG_IMAGE_PATH: " + " " + SdcardUtils.ORIGINAL_IMAGE_PATH);

        if (SdcardUtils.ORIGINAL_IMAGE_PATH != null) {
            Bitmap sb = getBitmap(SdcardUtils.ORIGINAL_IMAGE_PATH);
            if (sb != null) {
                cropImageView.setImageBitmap(sb);
            }
        }

        cropImageView.setFixedAspectRatio(true);

        fixedAspectRatioToggle = (ToggleButton) findViewById(R.id.fixedAspectRatioToggle);
        fixedAspectRatioToggle.setVisibility(View.GONE);
        fixedAspectRatioToggle.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                cropImageView.setFixedAspectRatio(isChecked);
            }
        });

        imgbtn_imagecrop_right.setOnClickListener(this);
        btn_imagecrop_done.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        SdcardUtils.isImageUploading = false;
        SdcardUtils.CROPED_IMAGE_PATH = null;
        SdcardUtils.CROPED_IMAGE_THUMB = null;
        SdcardUtils.ORIGINAL_IMAGE_PATH = "";
        SdcardUtils.MEDIA_FILE_ORIGINAL = null;
        //StaticData.mediapath = "";
        ImageCropActivity.this.finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    /**
     * returns bitmap from path
     *
     * @param path image file path
     * @return bitmap
     */
    private Bitmap getBitmap(String path) {

        InputStream in = null;
        try {
            final int IMAGE_MAX_SIZE = 2800000; // 2.8MP
            in = new FileInputStream(path);
            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inPreferredConfig = Bitmap.Config.RGB_565;
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(in, null, o);
            in.close();

            int scale = 1;
            while ((o.outWidth * o.outHeight) * (1 / Math.pow(scale, 2)) > IMAGE_MAX_SIZE) {
                scale++;
            }
            Logger.e(TAG + "scale = " + scale + ", orig-width: " + o.outWidth + ",orig-height: " + o.outHeight);

            Bitmap b = null;
            in = new FileInputStream(path);
            if (scale > 1) {

                scale--;
                // scale to max possible inSampleSize that still yields an image
                // larger than target
                o = new BitmapFactory.Options();
                o.inPreferredConfig = Bitmap.Config.RGB_565;
                o.inSampleSize = scale;
                b = BitmapFactory.decodeStream(in, null, o);

                // resize to desired dimensions
                int height = b.getHeight();
                int width = b.getWidth();

                try {
                    exif = new ExifInterface(path);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Logger.d("EXIF value" + exif.getAttribute(ExifInterface.TAG_ORIENTATION));
                System.out.println("======EXIFvalue========" + exif.getAttribute(ExifInterface.TAG_ORIENTATION));
                if (exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("6")) {
                    b = rotate(b, 90);
                } else if (exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("8")) {
                    b = rotate(b, 270);
                } else if (exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("3")) {
                    b = rotate(b, 180);
                }

                Logger.e(TAG + "1th scale operation dimenions - width: " + width + ",height: " + height);

                double y = Math.sqrt(IMAGE_MAX_SIZE / (((double) width) / height));
                double x = (y / height) * width;

                Bitmap scaledBitmap = ScalingUtilities.createScaledBitmap(b, (int) x, (int) y, ScalingUtilities.ScalingLogic.FIT);
                b.recycle();
                b = scaledBitmap;

                System.gc();
            } else {
                b = BitmapFactory.decodeStream(in);
            }
            in.close();

            Logger.e(TAG + "bitmap size - width: " + b.getWidth() + ", height: " + b.getHeight());
            final int memUsageKb = (b.getRowBytes() * b.getHeight()) / 1024;
            Logger.e(TAG + memUsageKb + "kb");
            long usebyte = (b.getRowBytes() * b.getHeight());
            Logger.e(TAG + "SIZE: " + readableFileSize(usebyte));

            return b;
        } catch (IOException e) {
            Logger.e(TAG + e.getMessage() + e);
            return null;
        }
    }


    /**
     * @param size file size in long
     * @return file size in string
     */
    public static String readableFileSize(long size) {
        if (size <= 0) return "0";
        final String[] units = new String[]{"B", "kB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }


    public static final int RESIZE_BITMAP_SIZE = 1000;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_crop:
                if (cropped) {
                    SdcardUtils.ORIGINAL_IMAGE_PATH = "";
                    SdcardUtils.MEDIA_FILE_ORIGINAL = null;
                    SdcardUtils.isImageUploading = true;
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    ImageCropActivity.this.finish();
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                } else {
                    //	btn_rotate.setVisibility(View.GONE);
                    croppedImage = cropImageView.getCroppedImage();

                    Logger.v("croppedImage getWidth" + "" + croppedImage.getWidth());
                    Logger.v("croppedImage getHeight" + "" + croppedImage.getHeight());

                    if (croppedImage.getWidth() > RESIZE_BITMAP_SIZE && croppedImage.getHeight() > RESIZE_BITMAP_SIZE) {
                        Logger.v("In Resize Function" + "");
                        if (croppedImage.getWidth() > croppedImage.getHeight()) {
                            int new_height = (croppedImage.getHeight() * RESIZE_BITMAP_SIZE) / croppedImage.getWidth();
                            Logger.v("=new_height=" + new_height + "");
                            Bitmap scaledBitmap = ScalingUtilities.createScaledBitmap(croppedImage, (int) RESIZE_BITMAP_SIZE, (int) new_height, ScalingUtilities.ScalingLogic.FIT);
                            croppedImage.recycle();
                            croppedImage = scaledBitmap;
                        } else {
                            int new_width = (croppedImage.getWidth() * RESIZE_BITMAP_SIZE) / croppedImage.getHeight();
                            Logger.v("=new_width=" + "" + new_width);
                            Bitmap scaledBitmap = ScalingUtilities.createScaledBitmap(croppedImage, (int) new_width, (int) RESIZE_BITMAP_SIZE, ScalingUtilities.ScalingLogic.FIT);
                            croppedImage.recycle();
                            croppedImage = scaledBitmap;
                        }
                    }

                    Logger.v("after_croppedgetWidth:" + "" + croppedImage.getWidth());
                    Logger.v("after_croppedgetHeight:" + "" + croppedImage.getHeight());

                    // ==============================================================
                    // Image
                    // =====================================typeface_Montserrat_SemiBold=========================
                    File imageFile = SdcardUtils.returnImageFileName();
                    try {
                        FileOutputStream out = new FileOutputStream(imageFile);
                        croppedImage.compress(Bitmap.CompressFormat.JPEG, 100, out);
                        out.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    SdcardUtils.CROPED_IMAGE_PATH = imageFile;
                    Logger.i(readableFileSize(SdcardUtils.CROPED_IMAGE_PATH.length()));
                    // ==============================================================
                    // Image Thumb
                    // ==============================================================
                    Bitmap bmThumbnail;
                    /*if(croppedImage.getWidth() >  croppedImage.getHeight())
                    {
						int new_width = (croppedImage.getWidth()*150)/croppedImage.getHeight();
						bmThumbnail = ThumbnailUtils.extractThumbnail(croppedImage, 270,270);
					}
					else
					{
						int new_height = (croppedImage.getHeight()*150)/croppedImage.getWidth();

					} */

                    bmThumbnail = ThumbnailUtils.extractThumbnail(croppedImage, 270, 270);
                    croppedImage.recycle();
                    croppedImage = bmThumbnail;
                    File thumbImageFile = SdcardUtils.returnThumbImageFileName();
                    try {
                        FileOutputStream out = new FileOutputStream(thumbImageFile);
                        croppedImage.compress(Bitmap.CompressFormat.JPEG, 100, out);
                        out.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    SdcardUtils.CROPED_IMAGE_THUMB = thumbImageFile;
                    //btn_imagecrop_done.setVisibility(View.GONE);
                    if (SdcardUtils.MEDIA_FILE_ORIGINAL != null) {
                        if (SdcardUtils.MEDIA_FILE_ORIGINAL.exists()) {
                            SdcardUtils.MEDIA_FILE_ORIGINAL.delete();
                        }
                    }
                    cropImageView.setVisibility(View.GONE);
                    //rel_crop.setVisibility(View.GONE);
                    img_view_croped_image.setVisibility(View.VISIBLE);
                    linear_image_display.setVisibility(View.VISIBLE);
                    Bitmap displaybit = getBitmap(SdcardUtils.CROPED_IMAGE_PATH.toString());
                    if (displaybit != null) {
                        img_view_croped_image.setImageBitmap(displaybit);
                    }


					/*SdcardUtils.ORIGINAL_IMAGE_PATH = "";
                    SdcardUtils.MEDIA_FILE_ORIGINAL=null;
					SdcardUtils.isImageUploading=true;
					ImageCropActivity.this.finish();*/

                    cropped = true;
                    btn_crop.setText(getResources().getString(R.string.done));
                }
                break;

            case R.id.imgbtn_imagecrop_right:
                SdcardUtils.isImageUploading = false;
                SdcardUtils.CROPED_IMAGE_PATH = null;
                SdcardUtils.CROPED_IMAGE_THUMB = null;
                SdcardUtils.ORIGINAL_IMAGE_PATH = "";
                SdcardUtils.MEDIA_FILE_ORIGINAL = null;
                //StaticData.mediapath = "";
                ImageCropActivity.this.finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                break;
            case R.id.btn_imagecrop_done:
                SdcardUtils.ORIGINAL_IMAGE_PATH = "";
                SdcardUtils.MEDIA_FILE_ORIGINAL = null;
                if (SdcardUtils.CROPED_IMAGE_PATH != null) {
                    SdcardUtils.ORIGINAL_IMAGE_PATH = "";
                    SdcardUtils.MEDIA_FILE_ORIGINAL = null;
                    Intent i = getIntent();
                    i.putExtra("Cropped_ImagePath", SdcardUtils.CROPED_IMAGE_PATH.getAbsolutePath());
                    setResult(Activity.RESULT_OK, i);
                    ImageCropActivity.this.finish();
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
//			ImageCropActivity.this.finish();
//			overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                break;
        }
    }

    /**
     * rotate a bitmap
     *
     * @param bitmap bitmap to be rotated
     * @param degree degree of rotation
     * @return rotated bitmap
     */
    public static Bitmap rotate(Bitmap bitmap, int degree) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Matrix mtx = new Matrix();
        mtx.postRotate(degree);
        return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
    }

    public void InitViews() {
        imgbtn_imagecrop_right = (ImageButton) findViewById(R.id.imgbtn_imagecrop_right);
        txt_imagecrop_title = (TextView) findViewById(R.id.txt_imagecrop_title);
        btn_imagecrop_done = (Button) findViewById(R.id.btn_imagecrop_done);
        btn_imagecrop_done.setVisibility(View.GONE);
        linear_image_display = (LinearLayout) findViewById(R.id.linear_image_display);
        img_view_croped_image = (ImageView) findViewById(R.id.img_view_croped_image);
        rel_crop = (RelativeLayout) findViewById(R.id.rel_crop);
    }

    public void InitViewTextSize() {
        txt_imagecrop_title.setTextSize(TypedValue.COMPLEX_UNIT_PX, 21f * ParentObj.getInstance().density);
        btn_imagecrop_done.setTextSize(TypedValue.COMPLEX_UNIT_PX, 18f * ParentObj.getInstance().density);
    }
}
