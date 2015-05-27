package com.example.basicshare;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.linkedin.platform.APIHelper;
import com.linkedin.platform.AccessToken;
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
import com.example.basicshare.utils.LogHelper;

public class HelperLinkedIn  extends HelperAuth {
	
     public static final String personByIdBaseUrl = "https://api.linkedin.com/v1/people/id=";
     public static final String personBasedUrl = "https://api.linkedin.com/v1/people/~";
     public static final String shareBaseUrl = "https://api.linkedin.com/v1/people/~/shares";
     public static final String personProjection1 = ":(id,first-name,last-name,location,headline,industry,picture-url)";
     public static final String personProjection2 = ":(id,first-name,last-name,location,headline,industry,picture-url)?format=json";
     public static final String personProjection3 = ":(id,first-name,last-name,location,headline,industry,picture-url,email-address)?format=json";
    
     public static final Scope scope = Scope.build(Scope.R_BASICPROFILE, Scope.W_SHARE, Scope.R_EMAILADDRESS);
     public static final String requestUrl = personBasedUrl + personProjection3;

  	 public LogHelper log;
	
     private Context mContext; 
	 private Fragment mFragment;
	 private UserProfile  mUserProfile;
	 
	 public interface OnLinkedInSignInListener{
		 void onLinkedInSignIn(UserProfile profile);
		 void onLinkedInError(int code); 
		 void onLinkedInCancel(); 
		// void onClickFacebook();
	 };
	 
	 
	  
	
	 public interface OnLinkedInLogInListener{
		 void onLinkedInLogIn(UserProfile profile); 
	 };
	
	 private OnLinkedInLogInListener mLogInListener;
	 private OnLinkedInSignInListener mSignInListener;
	 
	 public HelperLinkedIn(Context context, Fragment fragment){
		 log = new LogHelper(this.getClass().getSimpleName(),"MainActivity"); 
		 mContext = context;
		 mFragment = fragment; 
	 }
	 
	 public void logIn(OnLinkedInLogInListener listener){
		    
			mLogInListener = listener;
			logIn();
		}

	public void shareUsingLinkedIn() {

		//Toast.makeText(getActivity(), "Success!", Toast.LENGTH_SHORT).show();
		if (isAccessTokenValid()) {
			postLink();
			Toast.makeText(mFragment.getActivity(), "Success!", Toast.LENGTH_SHORT).show();
		} else {
			LISessionManager.getInstance(mContext).init(mFragment.getActivity(), scope, new AuthListener() {
				@Override
				public void onAuthSuccess() {

					Toast.makeText(mFragment.getActivity(), "Success log in!", Toast.LENGTH_SHORT).show();
					postLink();
				}

				@Override
				public void onAuthError(LIAuthError error) {
					Toast.makeText(mFragment.getActivity(), "Error log in!", Toast.LENGTH_SHORT).show();

				}
			}, true);
		}
	}

	public void postLink() {
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
			APIHelper apiHelper = APIHelper.getInstance(mFragment.getActivity().getApplicationContext());

			apiHelper.postRequest(mFragment.getActivity(), url, body, new ApiListener() {
				@Override
				public void onApiSuccess(ApiResponse apiResponse) {
					Toast.makeText(mFragment.getActivity(), "Success making POST request!", Toast.LENGTH_SHORT).show();
					DeepLinkHelper.getInstance().openCurrentProfile(mFragment.getActivity(), new DeepLinkListener() {
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
					Toast.makeText(mFragment.getActivity(), liApiError.toString(), Toast.LENGTH_SHORT).show();
					// Error making POST request!
				}
			});


		}
		else {
			Toast.makeText(mFragment.getActivity(), "Error Accesstoken", Toast.LENGTH_SHORT).show();
		}
	}

	public void logIn(){
		 if (isAccessTokenValid())
			{
				log.debug("User is already loged");
				
			} else
			{
				LISessionManager.getInstance(mContext).init(mFragment.getActivity(), scope, new AuthListener() {
         		@Override
                 public void onAuthSuccess() {
                     log.debug("Login successful");
                     //Toast.makeText(mFragment.getActivity(), "Login successful", Toast.LENGTH_SHORT).show();
                     // Check ******
                     fetchUserInfo();
                     //Toast.makeText(mFragment.getActivity(), mUserProfile.getEmail(), Toast.LENGTH_SHORT).show();
                     //mSignInListener.onLinkedInSignIn(mUserProfile);
                 }
                 @Override
                 public void onAuthError(LIAuthError error) {
                 	log.debug("Login error");
                 	}
     			}, true);
			}
	 }
	 
	 public void setUpButton(Button button,  final OnLinkedInSignInListener listener) {
		 		button.setOnClickListener(new View.OnClickListener() {
	            @Override
	            public void onClick(View view) {
					signIn(listener);
				}
		 		});
       	}
	 
	 public void signIn(OnLinkedInSignInListener listener){	
		 mSignInListener = listener; 
		 //signIn();
		 logIn();
	 }
	 
	 
			
	 private boolean isAccessTokenValid() {
	    	
		 	boolean accessTokenValid;
	        LISession session = getSession();
	        accessTokenValid = session.isValid();
	        return accessTokenValid;
        
	    }
	
	 public AccessToken getAccessToken() {
	        LISession session = getSession();
			return session.getAccessToken();
		}
	 
	 public LISession getSession(){
		 LISessionManager sessionManager = LISessionManager.getInstance(mFragment.getActivity().getApplicationContext());
		 return sessionManager.getSession();
	 }


	 
	 public void onActivityResult(int requestCode, int resultCode, Intent data) {
		 LISessionManager.getInstance(mContext).onActivityResult(mFragment.getActivity(), requestCode, resultCode, data);
	    }
	 
	 
	 private UserProfile parse(JSONObject userMe){
		 String userId = "";
		 String pictureUrl = "";
		 String name = "";
		 String surname = "";
		 String email = "";
		 
		 try{ 
			   userId = userMe.getString("id");
	           pictureUrl = userMe.has("pictureUrl") ? userMe.getString("pictureUrl") : null;
	         //JSONObject location = s.getJSONObject("location");
	         //String locationName = location != null && location.has("name") ? location.getString("name") : "";
	           name = userMe.getString("firstName");
	           surname = userMe.getString("lastName");
	           email = userMe.getString("emailAddress");
	         /*
	
	         if (pictureUrl != null) {
	             new FetchImageTask(userImageView).execute(pictureUrl);
	         } else {
	         	userImageView.setImageResource(R.drawable.ghost_person);
	         }*/
		 	 } catch (JSONException e) {
		 		log.debug("JSON Exception");
	         }
		// Toast.makeText(mFragment.getActivity(), email, Toast.LENGTH_SHORT).show();
   	  return new UserProfile(email,name,surname,pictureUrl); 
    }
    
    /*private String  getImageURI(final String userId) {
    	return ImageRequest.getProfilePictureUri(userId,250,250).toString(); 
    }*/
	
    public void fetchUserInfo()
	{
    	final boolean response;
	    if (isAccessTokenValid())
        {	
    
	    	APIHelper apiHelper = APIHelper.getInstance(mContext);
		    apiHelper.getRequest( mFragment.getActivity(), requestUrl, new ApiListener() {
			        @Override
			        public void onApiSuccess(ApiResponse apiResponse) {
			        			log.debug("Parsing...");
			        			JSONObject userMe;
			        			userMe = apiResponse.getResponseDataAsJson();
			        			mUserProfile = parse(userMe);
			        			mSignInListener.onLinkedInSignIn(mUserProfile);
			        }   
			        @Override
			        public void onApiError(LIApiError error) {
			        	log.debug(error.toString());
			        }
			    });

	    	}
	    
    }

	

	

}

