package com.example.basicshare;

import java.util.Arrays;
import java.util.List;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.example.basicshare.utils.LogHelper;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestAsyncTask;
import com.facebook.GraphResponse;
import com.facebook.internal.ImageRequest;
import com.facebook.login.DefaultAudience;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.share.ShareApi;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareContent;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.MessageDialog;
import com.facebook.share.widget.ShareDialog;


public class HelperFacebook extends HelperAuth implements FacebookCallback<LoginResult> {
	
	private static final List<String> READ_PERMISSIONS = Arrays.asList("email","public_profile", "user_friends", "publish_actions");
	private static final String ADDITIONAL_PERMISSIONS = "publish_actions";

	private static final String SHARE_APP_LINK = "https://developers.facebook.com/docs/android";
	private static final String SHARE_APP_NAME = "Qcards";

	private ShareDialog shareDialog;
	private MessageDialog messageDialog;

	private boolean shouldImplicitlyPublish = true;
	private boolean pendingPublish;

	public LogHelper log;
	
	 private Context mContext; 
	 private CallbackManager callbackManager;
	 private Fragment mFragment;
	 private UserProfile  mUserProfile;  
	   
	 public interface OnFacebokLogInListener{
		 void onFacebookLogIn(UserProfile profile); 
	 }; 
	 
	 public interface OnFacebookSignInListener{
		 void onFacebookSignIn(UserProfile profile);
		 void onFacebookError(int code); 
		 void onFacebookCancel(); 
		// void onClickFacebook();
	 };
	 
	 
	 private OnFacebookSignInListener mSignInListener; 
	 private OnFacebokLogInListener mLogInListener; 
	 
	 
	 public HelperFacebook(Context context, Fragment fragment){
		 log = new LogHelper(this.getClass().getSimpleName(),"MainActivity"); 
		 mContext = context;
		 mFragment = fragment;
		 FacebookSdk.sdkInitialize(mContext);
	     callbackManager = CallbackManager.Factory.create();
	 }

	 public void setUpButton(LoginButton loginButton, OnFacebookSignInListener listener){
		    mSignInListener = listener; 
		    loginButton.setReadPermissions(READ_PERMISSIONS);		  
		    loginButton.setFragment(mFragment);    
		    // Other app specific specialization
		    loginButton.registerCallback(callbackManager,this); 	    
	 }


	@Override
	public void onSuccess(LoginResult result) {
		// TODO Auto-generated method stub
		log.debug("SigIn Exitoso: "); 
		log.debug("Access token	: " + result.getAccessToken());
		log.debug("Permission	: " + result.getRecentlyGrantedPermissions().toString());
		
		 AccessToken mAccessToken = result.getAccessToken(); 
		 
		 //Profile.fetchProfileForCurrentAccessToken();
		 //Profile profile = Profile.getCurrentProfile(); 
		 	 
		 GraphRequest request = GraphRequest.newMeRequest(
                  mAccessToken, new GraphRequest.GraphJSONObjectCallback() {
                      @Override
                      public void onCompleted(JSONObject userMe, GraphResponse response) {                  	  
                    	  if(userMe!=null){
                    		  mUserProfile = parse(userMe); 
	                    	  mSignInListener.onFacebookSignIn(mUserProfile);
                    	  }
                      }
            });

		GraphRequestAsyncTask graphRequestAsyncTask = GraphRequest.executeBatchAsync(request);
	}


	@Override
	public void onCancel() {
		log.debug("SignIn Cancelado: ");
		mSignInListener.onFacebookCancel();
	}


	@Override
	public void onError(FacebookException error) {
		log.debug("SignIn Error: " +  error.getMessage());
		mSignInListener.onFacebookError(0);  // Resolver errores
	}

	// Log in if is not and check Facebook publish actions
	public void checkPublishActions (){

		final AccessToken accessToken = AccessToken.getCurrentAccessToken();
		if( accessToken != null){
			if (accessToken.getPermissions().contains(ADDITIONAL_PERMISSIONS)) {
				publishResult();
			} else {
				handleError();
			}
		} else {
			LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

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

					}
			);
		}

		FacebookCallback<Sharer.Result> callback =
				new FacebookCallback<Sharer.Result>() {
					@Override
					public void onCancel() {
						log.debug("Canceled");
					}

					@Override
					public void onError(FacebookException error) {
						log.debug(String.format("Error: %s", error.toString()));
					}

					@Override
					public void onSuccess(Sharer.Result result) {
						log.debug("Success!");
					}
				};


		shareDialog = new ShareDialog(mFragment.getActivity());
		shareDialog.registerCallback(callbackManager, callback);

		messageDialog = new MessageDialog(mFragment.getActivity());
		messageDialog.registerCallback(callbackManager, callback);



	}



	public void shareUsingNativeDialog() {

		ShareContent content = getLinkContent();
		// share the app
		if (shareDialog.canShow(content, ShareDialog.Mode.NATIVE)) {
			shareDialog.show(content, ShareDialog.Mode.NATIVE);
		} else {
			showError(R.string.native_share_error);
		}
	}

	private void showError(int messageId) {
		AlertDialog.Builder builder = new AlertDialog.Builder(mFragment.getActivity());
		builder.setTitle(R.string.error_dialog_title).
				setMessage(messageId).
				setPositiveButton(R.string.error_ok_button, null);
		builder.show();
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


	private void handleError() {
		// this means the user did not grant us write permissions, so
		// we don't do implicit publishes
		shouldImplicitlyPublish = false;
		pendingPublish = false;
	}


	private ShareLinkContent getLinkContent() {
		return new ShareLinkContent.Builder()
				.setContentTitle(SHARE_APP_NAME)
				.setContentUrl(Uri.parse(SHARE_APP_LINK))
				.build();
	}

	private void publishResult() {
		if (shouldImplicitlyPublish && canPublish()) {

			ShareContent content = getLinkContent();
			ShareApi.share(content, new FacebookCallback<Sharer.Result>() {
				@Override
				public void onSuccess(Sharer.Result result) {
					log.debug("Posted OG Action with id: " +
							result.getPostId());
				}

				@Override
				public void onCancel() {
					// This should not happen
				}

				@Override
				public void onError(FacebookException error) {
					log.debug("Play action creation failed: " + error.getMessage());
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
				new AlertDialog.Builder(mFragment.getActivity())
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
								mFragment.getActivity(),
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




	public void logIn(OnFacebokLogInListener listener){
	    
		mLogInListener = listener;
		
		// Ya esta logeado
		if( AccessToken.getCurrentAccessToken()!= null){
			log.debug("Ya estaba conectado: "); 
			 GraphRequest request = GraphRequest.newMeRequest(
					 AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
	                      @Override
	                      public void onCompleted(JSONObject userMe, GraphResponse response) {
	                    	  if(userMe!=null){
	                    		  mUserProfile = parse(userMe); 
		                    	  mLogInListener.onFacebookLogIn(mUserProfile);			                    	  
	                    	  }
	                      }
	            });

	     GraphRequest.executeBatchAsync(request);
	     return; 
		}
		
		
		
		// No esta logeado.
		log.debug("Iniciando facebook logIn: "); 
		LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
			@Override
			public void onSuccess(LoginResult result) {
				log.debug("LogIn Exitoso: "); 
				 GraphRequest request = GraphRequest.newMeRequest(
						 AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
		                      @Override
		                      public void onCompleted(JSONObject userMe, GraphResponse response) {
		                    	  if(userMe!=null){
		                    		  mUserProfile = parse(userMe); 
			                    	  mLogInListener.onFacebookLogIn(mUserProfile);			                    	  
		                    	  }
		                      }
		            });

				   GraphRequest.executeBatchAsync(request);
			}

			@Override
			public void onCancel() {
				log.debug("LogIn Cancelado: "); 
			}

			@Override
			public void onError(FacebookException error) {
				log.debug("LogIn Error: " +  error.getMessage());
				
			}});
		
		
		LoginManager.getInstance().logInWithReadPermissions(mFragment,READ_PERMISSIONS); 
		   
	}
	
	@Override
	public void logOut(){
		if(AccessToken.getCurrentAccessToken()!=null){
			LoginManager.getInstance().logOut();
		}
	}

	 @Override
	 public void signOut(){
		 
	 
	 }
	
	public AccessToken getAccessToken() {
		return AccessToken.getCurrentAccessToken();
	}
	
	public CallbackManager getCallbackManager(){
		return callbackManager; 
	}

    
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
    
    
    private UserProfile parse(JSONObject userMe){
      String userId = userMe.optString("id");
   	  String email =userMe.optString("email");
   	  String name = userMe.optString("first_name"); 
   	  String surname =userMe.optString("last_name"); 
   	  String picture = getImageURI(userId); 
   	  return new UserProfile(email,name,surname,picture); 
    }
    
    private String  getImageURI(final String userId) {
    	return ImageRequest.getProfilePictureUri(userId,250,250).toString(); 
    }
    
     

}
