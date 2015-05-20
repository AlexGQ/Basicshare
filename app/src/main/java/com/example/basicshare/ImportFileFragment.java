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

	private static ShareQcardCallback ShareQcardCallback;

	private Contact contact = new Contact();
	private int imageID = 1;

	public interface ShareQcardCallback {
		void onShareQcardPressed(Contact contact);
	}

public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	
        fa = super.getActivity();
     	ll = (RelativeLayout) inflater.inflate(R.layout.activity_main, container, false);

     	mContext = getActivity().getApplicationContext();

		UtilsPics uPics = new UtilsPics(mContext);

		String mParentPath = null;
		Bitmap bm = null;
		String name;

		imageView = (ImageView)ll.findViewById(R.id.imageView);
		textView = (TextView)ll.findViewById(R.id.textView);
		shareButton = (Button)ll.findViewById(R.id.share_button);

		textView.setFocusable(true);
		textView.setEnabled(true);
		textView.setClickable(true);

		// Get the extras
		Intent intent = fa.getIntent();
		Bundle extras = intent.getExtras();
		Boolean FLAG_IMPORT = extras.getBoolean("FLAG_IMPORT");

		// If there is a file to import
		if (FLAG_IMPORT) {

			data = intent.getData();
			mParentPath = extras.getString("mParentPath");

			contact = uPics.ImportCards(data, mParentPath);
			imageID = contact.getImageID();
			name = contact.getName();

			textView.setText(name);

		} else
		{

			//ImageView ie = ((ImageView)holder.findViewById(R.id.im_expand));
			shareButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {

					contact.setImageID(imageID);
					contact.setName(textView.getText().toString());
					contact.setOwnerID(1);
					contact.setID(1);
					contact.setStreet("");
					contact.setPlace("");
					contact.setPhoneNumber("");

					ShareQcardCallback.onShareQcardPressed(contact);

					//InitShare(contact);
					//Toast.makeText(getApplicationContext(), "Ready to share?", Toast.LENGTH_LONG).show();
				}
			});

		}

		bm = uPics.decodeSampledBitmapFromRes(fa.getApplicationContext(), uPics.mThumbIds[(int) imageID-1],  70, 70);
		imageView.setImageBitmap(bm);
		imageView.setAdjustViewBounds(true);

     	return ll;
	}

	public static void setShareQcardCallback(ShareQcardCallback callback) {
		ShareQcardCallback = callback;
	}

}
