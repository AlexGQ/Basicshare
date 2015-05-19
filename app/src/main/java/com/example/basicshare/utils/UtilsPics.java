package com.example.basicshare.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import com.example.basicshare.R;

public class UtilsPics  {
	
	public static final String ANDROID_RESOURCE = "android.resource://";
	public static final String FORESLASH = "/";
	
	// Folder created to store Qcards resources 
	public final static String APP_FOLDER = "Qcards1";
	
	public final static String IMAGE_NAME = "04261_BG.jpg";
	public static String IMFILENAME = "cardimage";
	//public static String PPHOTODEFAULT = "ppdefault";
	public static String MPPHOTO = "mProfilePhoto";
	public static String JSONFILENAME = "cardinfo1";
	
	public static String filePathJson = android.os.Environment.getExternalStorageDirectory() + FORESLASH + APP_FOLDER + FORESLASH + JSONFILENAME + ".json";

    /*private Context mContext;
    // constructor
    public UtilsPics(Context context) {
        mContext = context;
    }*/

    // Keep all Images in array (cards layouts stored in the folder res).
    
    public Integer[] mThumbIds = {
    		R.drawable.pic_2, R.drawable.pic_4,
    };
    
    // Image_id for each image layout
    public Integer[] image_id = {1, 2, 3, 4, 5, 6, 7, 8}; 


    public Bitmap decodeSampledBitmapFromRes(Context mContext, int res, int reqWidth, int reqHeight) {
	    Bitmap bm = null;
	    
	    // First decode with inJustDecodeBounds=true to check dimensions
	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	    
	    //BitmapFactory.decodeFile(path, options);
	    
	    BitmapFactory.decodeResource(mContext.getResources(), res, options);
	    
	    // Calculate inSampleSize
	    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
	    
	    // Decode bitmap with inSampleSize set
	    options.inJustDecodeBounds = false;
 
	    bm = BitmapFactory.decodeResource(mContext.getResources(), res, options);
	    
	    return bm;  
   }

   
   public int calculateInSampleSize(
     
	    BitmapFactory.Options options, int reqWidth, int reqHeight) {
		    // Raw height and width of image
		    final int height = options.outHeight;
		    final int width = options.outWidth;
		    int inSampleSize = 1;
		       
		    if (height > reqHeight || width > reqWidth) {
		     if (width > height) {
		    	 inSampleSize = Math.round((float)height / (float)reqHeight);   
		     } else {
		    	 inSampleSize = Math.round((float)width / (float)reqWidth);   
		     }   
	    }
    
	    return inSampleSize;   
   }

    public File FindDir(Context context)
	{
		File FileDir;
	    //Find the dir to save images
	    if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
	    	FileDir = new File(android.os.Environment.getExternalStorageDirectory(),APP_FOLDER);
	    else
	    	//FileDir = context.getCacheDir();
	    	FileDir = context.getFilesDir();
	    if(!FileDir.exists())
	    	FileDir.mkdirs();
	    
	    return FileDir;
	}

    public void createFiles(Context mContext, Contact contact)
    {

        UtilsPics im = new UtilsPics();
        int imageID = contact.getImageID();

        Bitmap bm = im.decodeSampledBitmapFromRes(mContext,im.mThumbIds[(int)imageID-1],  70, 70);

        List<Contact> contactListToShare;

        contactListToShare = new ArrayList<Contact>();
        contactListToShare.add(contact);

        String cardData;

        File folder_dir = im.FindDir(mContext);
        String im_filename = UtilsPics.IMFILENAME + "00";
        String json_filename = UtilsPics.JSONFILENAME;

        // Copy image to memory
        im.saveImageToInternalStorage(mContext, bm, folder_dir, im_filename);

        cardData = UtilsJson.ContactsToJSon(contactListToShare);

        // Copy json to memory
        im.saveFileToInternalStorage(mContext, cardData, folder_dir, json_filename);
    }

    public void saveImageToInternalStorage(Context mContext, Bitmap bitmapImage, File FolderDir, String filename){
	   String fn = filename + ".jpg";
       File mypath = new File(FolderDir,fn);

       //FileOutputStream fos = null;
       try {           

    	   FileOutputStream fos = new FileOutputStream(mypath);

           // Use the compress method on the BitMap object to write image to the internal memory.
           bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
           fos.close();
       } catch (Exception e) {
           e.printStackTrace();
       }
   }
   public void saveFileToInternalStorage(Context context, String data, File FolderDir, String filename){
	   String fn = filename + ".json";
       File mypath = new File(FolderDir,fn);

	   try {
		   FileOutputStream outputStream = new FileOutputStream(mypath);
		   outputStream.write(data.getBytes());
		   outputStream.close();
	   } catch (Exception e) {
		   e.printStackTrace();
	   }
   }
	
    public static Uri resIdToUri(Context context, int resId) {
    	String path = ANDROID_RESOURCE + context.getPackageName()
                + FORESLASH + resId;
        return Uri.parse(path);
        
    }
    
    public Bitmap combineImages(Bitmap background, Bitmap foreground, Context mContext) { 
    //public Bitmap combineImages(Bitmap c, Bitmap s, Context mContext) {

        int width = 0, height = 0;
        Bitmap cs;

        //width = ((Activity)mContext).getWindowManager().getDefaultDisplay().getWidth();
        //height = ((Activity)mContext).getWindowManager().getDefaultDisplay().getHeight();
        
        width = background.getWidth();
        height = background.getHeight();


        cs = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas comboImage = new Canvas(cs);
        background = Bitmap.createScaledBitmap(background, width, height, true);
        comboImage.drawBitmap(background, 0, 0, null);
        comboImage.drawBitmap(foreground, 0, 50, null);

        return cs;
    }
    
    
    //public String readFileFromInternalStorage(String fileName, Context context)
    public String readFileFromInternalStorage(String filePath, Context context, InputStream einputStream)
    {
            String stringToReturn = " ";
            FileInputStream inputStream;
            try 
            {
        				if (einputStream != null){
        					inputStream = (FileInputStream) einputStream;
        				}
        				else{
        					inputStream = new FileInputStream(filePath);
        				}
 
                        if ( inputStream != null ) 
                        {
                                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                                String receiveString = "";
                                StringBuilder stringBuilder = new StringBuilder();
 
                            while ( (receiveString = bufferedReader.readLine()) != null )
                            {
                                 stringBuilder.append(receiveString);
                            }
                            inputStream.close();
                            stringToReturn = stringBuilder.toString();
                        }
            }
            catch (FileNotFoundException e) 
            {
                    Log.e("TAG", "File not found: " + e.toString());
            }
            catch (IOException e) 
            {
                    Log.e("TAG", "Can not read file: " + e.toString());
            }

            return stringToReturn;
    }
    
    public String handleFileUri(Uri beamUri) {
        // Get the path part of the URI
        String fileName = beamUri.getPath();
        return fileName;
    }
    
    public String handleContentUri(Uri beamUri, Context context) {

        int filenameIndex;
        String fileName = null;
        
        // Test the authority of the URI
        if (!TextUtils.equals(beamUri.getAuthority(), MediaStore.AUTHORITY)) {
            
            // Handle content URIs for other content providers
        	String[] proj = { MediaStore.Images.Media.DATA };
    	    CursorLoader loader = new CursorLoader(context, beamUri, proj, null, null, null);
    	    Cursor cursor = loader.loadInBackground();
    	    filenameIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
    	    cursor.moveToFirst();
    	    
    	    fileName = cursor.getString(filenameIndex);
    	    
        } else {
            // Get the column that contains the file name
            String[] projection = { MediaStore.MediaColumns.DATA };
            Cursor pathCursor =
            		context.getContentResolver().query(beamUri, projection,
                    null, null, null);
            // Check for a valid cursor
            if (pathCursor != null &&
                    pathCursor.moveToFirst()) {
                // Get the column index in the Cursor
                filenameIndex = pathCursor.getColumnIndex(
                        MediaStore.MediaColumns.DATA);
                // Get the full file name including path
                fileName = pathCursor.getString(filenameIndex);
             } 
        }
        return fileName;
    }
}