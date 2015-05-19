package com.example.basicshare;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.basicshare.utils.Contact;
import com.example.basicshare.utils.UtilsJson;
import com.example.basicshare.utils.UtilsPics;

public class ImportFileFragment extends Fragment{

	private ImageView imageView;
	private TextView textView;
	private Button shareButton;

	private Context mContext;
	private FragmentActivity fa;
	private RelativeLayout ll;
	private Uri data;

	private Contact contact = new Contact();
	
	private UtilsPics uPics = new UtilsPics();

public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	
        fa = super.getActivity();
     	ll = (RelativeLayout) inflater.inflate(R.layout.activity_main, container, false);

     	mContext = getActivity().getApplicationContext();
     	
     	String mParentPath = null;
		Bitmap bm = null;

     // Get the intent that started this activity
	    Intent intent = fa.getIntent();
	    
        data = intent.getData();

        // Get the Intent action
        String action = intent.getAction();

        /*
         * For ACTION_VIEW, the Activity is being asked to display data.
         * Get the URI.
         */
        if (TextUtils.equals(action, Intent.ACTION_VIEW)) {
            // Get the URI from the Intent
            /*
             * Test for the type of URI, by getting its scheme value
             */
            if (TextUtils.equals(data.getScheme(), "file")) {
                mParentPath = uPics.handleFileUri(data);
            } else if (TextUtils.equals(
            		data.getScheme(), "content")) {
                mParentPath = uPics.handleContentUri(data, mContext);
            }
        }

    	ImportCards(mParentPath);

		imageView = (ImageView)ll.findViewById(R.id.imageView);
		textView = (TextView)ll.findViewById(R.id.textView);
		shareButton = (Button)ll.findViewById(R.id.share_button);

		int imageID = contact.getImageID();
		String name = contact.getName();



		bm = uPics.decodeSampledBitmapFromRes(fa.getApplicationContext(),uPics.mThumbIds[(int)imageID-1],  70, 70);
		imageView.setImageBitmap(bm);
		imageView.setAdjustViewBounds(true);

		textView.setText(name);
            
     	return ll;
	}

public void ImportCards(String filePath)
{
	int group_id;	
   	int contactId;
   	int[] contacts_ids;

   	InputStream inputStream = null;

   	if (filePath == null){
   		try 
        {
   			ContentResolver cr = mContext.getContentResolver();
   			inputStream = cr.openInputStream(data);
        }
        catch (FileNotFoundException e) 
        {
                Log.e("TAG", "File not found: " + e.toString());
        }
    }
	String str = uPics.readFileFromInternalStorage(filePath, mContext, inputStream);
	boolean shareGroup = UtilsJson.checkShareGroupsOrCards(str);
	
	LinkedHashMap<String, List<Contact>> listGroupsImport = new LinkedHashMap<String, List<Contact>>();
	List<Contact> listCardsImport = new ArrayList<Contact>();

	
	if (shareGroup){

		Log.e("TAG", "Only implemented on Qcards");

	}
	else
	{
		Log.e("TAG", str);
		listCardsImport = UtilsJson.JSonToContacts(str);
		
    	for (int nc = 0; nc < listCardsImport.size(); nc++)
    	{
    		contact = listCardsImport.get(nc);
    	}
	}
}

public ParcelFileDescriptor openFile(Uri uri, String mode)
        throws FileNotFoundException {
    File f = new File(mContext.getFilesDir(), uri.getPath());
    
    if (f.exists()) {
        return (ParcelFileDescriptor.open(f, ParcelFileDescriptor.MODE_READ_ONLY));
    }

    throw new FileNotFoundException(uri.getPath());
}

}
