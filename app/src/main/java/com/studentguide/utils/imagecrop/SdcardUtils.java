package com.studentguide.utils.imagecrop;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;

import com.studentguide.utils.Logger;

import java.io.File;
import java.util.Calendar;

public class SdcardUtils {

    public static String BROADCAST_IMAGE_ACTION = "";
    public static boolean isImageUploading = false;

    public static final int ACTION_TAKE_PHOTO = 1001;
    public static final int ACTION_PICK_FROM_GALLERY = 1002;
    public static final int RETURN_INTENT = 1003;

    public static Uri CAMERA_IMAGE_URI;
    public static Uri CAMERA_VIDEO_URI;
    public static File MEDIA_FILE_ORIGINAL = null;
    public static String ORIGINAL_IMAGE_PATH = "";
    public static File CROPED_IMAGE_PATH = null;
    public static File CROPED_IMAGE_THUMB = null;

    public static String TRICE_IMAGE_THUMB = ".Letsgosocial/Thumb";
    public static String TRICE_IMAGE_IMAGES = ".Letsgosocial/Images";
    public static String TRICE_VIDEO = ".Letsgosocial/Video";


    /**
     * if folder doesn't exists it creates a folder
     * or if it exists it return its path
     *
     * @param folderPath folder name
     * @return full folder path
     */
    public static String createFolder(String folderPath) {
        String folderName = "";

        folderName = Environment.getExternalStorageDirectory() + "/" + folderPath;
        Logger.d( "folderName : " + folderName);
        File fileName = new File(folderName);
        if (!fileName.exists()) {
            fileName.mkdirs();
        }
        return folderName;
    }

    /**
     * @return image file name with .jpeg
     */
    public static File returnImageFileName() {
        File imageFileName;
        String ImageName = "Image_" + Calendar.getInstance().getTimeInMillis() + ".jpeg";
        String Imagefolder = createFolder(TRICE_IMAGE_IMAGES);
        if (!Imagefolder.equals("")) {
            imageFileName = new File(Imagefolder, ImageName);
        } else {
            imageFileName = null;
        }
        return imageFileName;
    }

    /**
     * @return video file name with .mp4
     */
    public static File returnVideoFileName() {
        File imageFileName;
        String ImageName = "Video_" + Calendar.getInstance().getTimeInMillis() + ".mp4";
        String Imagefolder = createFolder(TRICE_VIDEO);
        if (!Imagefolder.equals("")) {
            imageFileName = new File(Imagefolder, ImageName);
        } else {
            imageFileName = null;
        }
        return imageFileName;
    }


    /**
     * @return image thumb file name with .jpeg
     */
    public static File returnThumbImageFileName() {
        File thumbfilename;
        String ImagethumbName = "Image_thumb_" + Calendar.getInstance().getTimeInMillis() + ".jpeg";
        String ImageThumbfolder = createFolder(TRICE_IMAGE_THUMB);
        if (!ImageThumbfolder.equals("")) {
            thumbfilename = new File(ImageThumbfolder, ImagethumbName);
        } else {
            thumbfilename = null;
        }

        return thumbfilename;
    }

    public static String saveImageBitmap(Bitmap thumbnail_bitmap) {
        return null;
    }

    public static void deleteRecursive(File fileOrDirectory) {
        try {
            if (fileOrDirectory.isDirectory())
                for (File child : fileOrDirectory.listFiles())
                    deleteRecursive(child);

            fileOrDirectory.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
