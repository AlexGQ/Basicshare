package com.example.basicshare;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.Toast;


import com.facebook.login.DefaultAudience;
import com.facebook.share.model.ShareContent;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.AppInviteDialog;
import com.example.basicshare.utils.Contact;
import com.example.basicshare.utils.UtilsPics;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.ShareApi;
import com.facebook.share.Sharer;
import com.facebook.share.widget.AppInviteDialog;

import com.facebook.share.widget.MessageDialog;
import com.facebook.share.widget.ShareDialog;
import com.linkedin.platform.APIHelper;
import com.linkedin.platform.LISession;
import com.linkedin.platform.LISessionManager;
import com.linkedin.platform.errors.LIApiError;
import com.linkedin.platform.errors.LIAuthError;
import com.linkedin.platform.listeners.ApiListener;
import com.linkedin.platform.listeners.ApiResponse;
import com.linkedin.platform.listeners.AuthListener;
import com.linkedin.platform.utils.Scope;
import com.linkedin.platform.listeners.ApiListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

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

	private static final String ADDITIONAL_PERMISSIONS = "publish_actions";
	private boolean shouldImplicitlyPublish = true;
	private boolean pendingPublish;
	private static final String TAG = ImportFileFragment.class.getName();
	private ShareDialog shareDialog;
	private MessageDialog messageDialog;
	private AppInviteDialog appInviteDialog;

	private static final String SHARE_APP_LINK = "https://developers.facebook.com/docs/android";
	private static final String SHARE_APP_NAME = "Qcards";

	private CallbackManager callbackManager;

	private static ShareQcardCallback ShareQcardCallback;

	private Contact contact = new Contact();
	private int imageID = 1;

	public interface ShareQcardCallback {
		void onShareQcardPressed(Contact contact);
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		callbackManager = CallbackManager.Factory.create();
		LoginManager.getInstance().registerCallback(
			callbackManager,
			new FacebookCallback<LoginResult>() {
		@Override
		public void onSuccess(LoginResult loginResult) {
			AccessToken accessToken = AccessToken.getCurrentAccessToken();
			if (accessToken.getPermissions().contains(ADDITIONAL_PERMISSIONS)) {
				publishResult();
			} else {
				handleError();
			}
		}

		@Override
		public void onCancel() {
			handleError();
		}

		@Override
		public void onError(FacebookException exception) {
			handleError();
		}

		private void handleError() {
			// this means the user did not grant us write permissions, so
			// we don't do implicit publishes
			shouldImplicitlyPublish = false;
			pendingPublish = false;
		}
		}
		);

		FacebookCallback<Sharer.Result> callback =
		new FacebookCallback<Sharer.Result>() {
	@Override
	public void onCancel() {
			Log.d(TAG, "Canceled");
			}

	@Override
	public void onError(FacebookException error) {
			Log.d(TAG, String.format("Error: %s", error.toString()));
			}

	@Override
	public void onSuccess(Sharer.Result result) {
			Log.d(TAG, "Success!");
			}
			};
			shareDialog = new ShareDialog(this);
			shareDialog.registerCallback(callbackManager, callback);
			messageDialog = new MessageDialog(this);
			messageDialog.registerCallback(callbackManager, callback);

			FacebookCallback<AppInviteDialog.Result> appInviteCallback =
			new FacebookCallback<AppInviteDialog.Result>() {
	@Override
	public void onSuccess(AppInviteDialog.Result result) {
			Log.d(TAG, "Success!");
			}

	@Override
	public void onCancel() {
			Log.d(TAG, "Canceled");
			}

	@Override
	public void onError(FacebookException error) {
			Log.d(TAG, String.format("Error: %s", error.toString()));
			}
			};
			appInviteDialog = new AppInviteDialog(this);
			appInviteDialog.registerCallback(callbackManager, appInviteCallback);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
			callbackManager.onActivityResult(requestCode, resultCode, data);
			//LISessionManager.getInstance(mContext).onActivityResult(fa, requestCode, resultCode, data);

			}

	private static Scope buildScope() {
		return Scope.build(Scope.R_BASICPROFILE, Scope.W_SHARE, Scope.R_EMAILADDRESS);
	}



	private void publishResult() {
		if (shouldImplicitlyPublish && canPublish()) {

			ShareContent content = getLinkContent();
			ShareApi.share(content, new FacebookCallback<Sharer.Result>() {
				@Override
				public void onSuccess(Sharer.Result result) {
					Log.i(TAG, "Posted OG Action with id: " +
							result.getPostId());
				}

				@Override
				public void onCancel() {
					// This should not happen
				}

				@Override
				public void onError(FacebookException error) {
					Log.e(TAG, "Play action creation failed: " + error.getMessage());
				}
			});
		}
	}

	private boolean canPublish() {
		final AccessToken accessToken = AccessToken.getCurrentAccessToken();
		if (accessToken != null) {
			if (accessToken.getPermissions().contains(ADDITIONAL_PERMISSIONS)) {
				// if we already have publish permissions, then go ahead and publish
				return true;
			} else {
				// otherwise we ask the user if they'd like to publish to facebook
				new AlertDialog.Builder(getActivity())
						.setTitle(R.string.share_with_friends_title)
						.setMessage(R.string.share_with_friends_message)
						.setPositiveButton(R.string.share_with_friends_yes, canPublishClickListener)
						.setNegativeButton(R.string.share_with_friends_no, dontPublishClickListener)
						.show();
				return false;
			}
		}
		return false;
	}

	private DialogInterface.OnClickListener canPublishClickListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialogInterface, int i) {
			if (AccessToken.getCurrentAccessToken() != null) {
				// if they choose to publish, then we request for publish permissions
				shouldImplicitlyPublish = true;
				pendingPublish = true;

				LoginManager.getInstance()
						.setDefaultAudience(DefaultAudience.FRIENDS)
						.logInWithPublishPermissions(
								ImportFileFragment.this,
								Arrays.asList(ADDITIONAL_PERMISSIONS));
			}
		}
	};

	private DialogInterface.OnClickListener dontPublishClickListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialogInterface, int i) {
			// if they choose not to publish, then we save that choice, and don't prompt them
			// until they restart the app
			pendingPublish = false;
			shouldImplicitlyPublish = false;
		}
	};

	public void shareUsingNativeDialog() {

		ShareContent content = getLinkContent();
		// share the app
		if (shareDialog.canShow(content, ShareDialog.Mode.NATIVE)) {
			shareDialog.show(content, ShareDialog.Mode.NATIVE);
		} else {
			showError(R.string.native_share_error);
		}
	}

	public void shareUsingMessengerDialog() {

			ShareContent content = getLinkContent();

			// share the app
			if (messageDialog.canShow(content)) {
				messageDialog.show(content);
			} else {
				showError(R.string.native_share_error);
			}

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
				"http://linkd.in/1FC2PyG\"," +
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

	private ShareLinkContent getLinkContent() {
		return new ShareLinkContent.Builder()
				.setContentTitle(SHARE_APP_NAME)
				.setContentUrl(Uri.parse(SHARE_APP_LINK))
				.build();
	}


	/*@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		shareUsingLinkedIn();

	}
*/
	/*@Override
	public void onResume() {
		super.onResume();
		shareUsingLinkedIn();

	}*/

	private void showError(int messageId) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.error_dialog_title).
				setMessage(messageId).
				setPositiveButton(R.string.error_ok_button, null);
		builder.show();
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

					shareUsingNativeDialog();
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
