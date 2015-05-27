package com.example.basicshare;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.basicshare.utils.Contact;
import com.example.basicshare.utils.UtilsPics;


public class ImportFileFragment extends Fragment{

	private ImageView imageView;
	private TextView textView;
	private Button shareButton;
	private Button shareFaceButton;
	private Button shareLinkedInButton;

	private Context mContext;
	private FragmentActivity fa;
	private RelativeLayout ll;
	private Uri data;

	private HelperFacebook mFacebookAuth;
	private HelperLinkedIn mLinkedInAuth;

	private static ShareQcardCallback ShareQcardCallback;

	private Contact contact = new Contact();
	private int imageID = 1;

	public interface ShareQcardCallback {
		void onShareQcardPressed(Contact contact);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mFacebookAuth = new HelperFacebook(getActivity(), this);
		mLinkedInAuth = new HelperLinkedIn(getActivity().getApplicationContext(),this);

		mFacebookAuth.checkPublishActions();

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		mFacebookAuth.onActivityResult(requestCode, resultCode, data);
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
		shareFaceButton = (Button)ll.findViewById(R.id.share_face_button);
		shareLinkedInButton = (Button)ll.findViewById(R.id.share_linkedin_button);

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



			//ImageView ie = ((ImageView)holder.findViewById(R.id.im_expand));
			shareFaceButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {

					mFacebookAuth.shareUsingNativeDialog();
				}
			});


			shareLinkedInButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {

					mLinkedInAuth.shareUsingLinkedIn();

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
