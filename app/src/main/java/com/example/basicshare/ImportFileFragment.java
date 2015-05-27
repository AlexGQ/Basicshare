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
import android.widget.Toast;

import com.example.basicshare.utils.Contact;
import com.example.basicshare.utils.UtilsPics;
import com.linkedin.platform.APIHelper;
import com.linkedin.platform.DeepLinkHelper;
import com.linkedin.platform.LISession;
import com.linkedin.platform.LISessionManager;
import com.linkedin.platform.errors.LIApiError;
import com.linkedin.platform.errors.LIAuthError;
import com.linkedin.platform.errors.LIDeepLinkError;
import com.linkedin.platform.listeners.ApiListener;
import com.linkedin.platform.listeners.ApiResponse;
import com.linkedin.platform.listeners.AuthListener;
import com.linkedin.platform.listeners.DeepLinkListener;
import com.linkedin.platform.utils.Scope;

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
		mFacebookAuth.checkPublishActions();

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		mFacebookAuth.onActivityResult(requestCode, resultCode, data);
	}

	private static Scope buildScope() {
		return Scope.build(Scope.R_BASICPROFILE, Scope.W_SHARE, Scope.R_EMAILADDRESS);
	}



	public void shareUsingLinkedIn() {
		String url = "https://api.linkedin.com/v1/people/~/shares";
		//String url = "https://api.linkedin.com/v1/people/~/shares?format=json";
		/*JSONObject body = null;
		try {
			body = new JSONObject("{" +
				"\"comment\": \"Sample share\"," +
				"\"visibility\": { \"code\": \"anyone\" }," +
				"\"content\": { " +
				"\"title\": \"Sample share\"," +
				"\"description\": \"Testing the mobile SDK call wrapper!\"," +
				"\"submitted-url\": \"http://www.example.com/\"," +
				"\"submitted-image-url\": \"http://www.example.com/pic.jpg\"" +
				"}" +
				"}");
		} catch (JSONException e) {
			Toast.makeText(getActivity(), "Exception JSON", Toast.LENGTH_SHORT).show();
		}*/
		String body = "{" +
				"\"comment\":\"Check out developer.linkedin.com! " +
				"http://www.google.com\"," +
				"\"visibility\":{" +
				"    \"code\":\"anyone\"}" +
				"}";


		if(isAccessTokenValid())
			{
				//APIHelper apiHelper = APIHelper.getInstance(mContext);
				APIHelper apiHelper = APIHelper.getInstance(getActivity().getApplicationContext());

				apiHelper.postRequest(getActivity(), url, body, new ApiListener() {
					@Override
					public void onApiSuccess(ApiResponse apiResponse) {
						Toast.makeText(getActivity(), "Success making POST request!", Toast.LENGTH_SHORT).show();
						DeepLinkHelper.getInstance().openCurrentProfile(getActivity(), new DeepLinkListener() {
							@Override
							public void onDeepLinkSuccess() {

							}

							@Override
							public void onDeepLinkError(LIDeepLinkError error) {

							}
						});

					}

					@Override
					public void onApiError(LIApiError liApiError) {
						Toast.makeText(getActivity(), liApiError.toString(), Toast.LENGTH_SHORT).show();
						// Error making POST request!
					}
				});


			}
		else {
			Toast.makeText(getActivity(), "Error Accesstoken", Toast.LENGTH_SHORT).show();
		}
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

					//Toast.makeText(getActivity(), "Success!", Toast.LENGTH_SHORT).show();
					if (isAccessTokenValid()) {
						shareUsingLinkedIn();
						Toast.makeText(getActivity(), "Success!", Toast.LENGTH_SHORT).show();
					} else {
						LISessionManager.getInstance(mContext).init(getActivity(), buildScope(), new AuthListener() {
							@Override
							public void onAuthSuccess() {

								Toast.makeText(getActivity(), "Success log in!", Toast.LENGTH_SHORT).show();
								shareUsingLinkedIn();
							}

							@Override
							public void onAuthError(LIAuthError error) {
								Toast.makeText(getActivity(), "Error log in!", Toast.LENGTH_SHORT).show();

							}
						}, true);
				}

				}
			});



		}

		bm = uPics.decodeSampledBitmapFromRes(fa.getApplicationContext(), uPics.mThumbIds[(int) imageID-1],  70, 70);
		imageView.setImageBitmap(bm);
		imageView.setAdjustViewBounds(true);

     	return ll;
	}

	private boolean isAccessTokenValid() {
		boolean accessTokenValid;
		LISessionManager sessionManager = LISessionManager.getInstance(fa.getApplicationContext());
		LISession session = sessionManager.getSession();
		accessTokenValid = session.isValid();
		return accessTokenValid;
	}

	public static void setShareQcardCallback(ShareQcardCallback callback) {
		ShareQcardCallback = callback;
	}

}
