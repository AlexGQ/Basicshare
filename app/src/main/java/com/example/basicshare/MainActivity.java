package com.example.basicshare;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.ShareActionProvider;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.basicshare.utils.Contact;
import com.example.basicshare.utils.UtilsPics;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.FacebookSdk;
import com.linkedin.platform.LISessionManager;


import java.util.LinkedHashMap;


public class MainActivity extends FragmentActivity {

    private ShareActionProvider mShareActionProvider;

    private Context mContext;
    private Uri data;

    private MenuItem share;
    private MenuItem message;
    private MenuItem invite;

    private boolean isResumed = false;

    private AccessTokenTracker accessTokenTracker;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_file);

        mContext = getApplicationContext();
        UtilsPics uPics = new UtilsPics(mContext);

        String mParentPath = null;

        ImportFileFragment firstFragment = new ImportFileFragment();

        Intent intent = getIntent();

        // pass the Intent's extras to the fragment as arguments
        if (intent.getData() == null) {
            intent.putExtra("FLAG_IMPORT", false);
            intent.putExtra("mParentPath", mParentPath);
            firstFragment.setArguments(intent.getExtras());

        }
        else
        {
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
                    mParentPath = uPics.handleContentUri(data);
                }
            }

            intent.putExtra("FLAG_IMPORT", true);
            intent.putExtra("mParentPath", mParentPath);
            firstFragment.setArguments(intent.getExtras());
        }

        FacebookSdk.sdkInitialize(this.getApplicationContext());

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken,
                                                       AccessToken currentAccessToken) {
                if (isResumed) {
                    if (currentAccessToken == null) {

                        ImportFileFragment firstFragment = new ImportFileFragment();
                        // Add the fragment to the 'fragment_container' FrameLayout
                        getSupportFragmentManager().beginTransaction()
                                .add(R.id.frag_container_import, firstFragment, "IMPORT_FILE_FRAGMENT").commit();
                    }
                }
            }
        };


        // Add the fragment to the 'fragment_container' FrameLayout
        getSupportFragmentManager().beginTransaction()
                .add(R.id.frag_container_import, firstFragment, "IMPORT_FILE_FRAGMENT").commit();

        ImportFileFragment.setShareQcardCallback(new ImportFileFragment.ShareQcardCallback() {
            @Override
            public void onShareQcardPressed(Contact contact) {
                InitShare(contact);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //Call to update the share intent
    private void setShareIntent(Intent shareIntent)
    {
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

            LISessionManager.getInstance(getApplicationContext()).onActivityResult(this, requestCode, resultCode, data);

    }


    /**
     * Launching activity to share card
     * */
    private void InitShare(Contact contact) {

        UtilsPics uPics = new UtilsPics(mContext);
        ShareCards shCard = new ShareCards(mContext);

        uPics.createFiles(contact);
        LinkedHashMap<Integer, Integer> cardsByGroups = new LinkedHashMap<Integer, Integer>();
        cardsByGroups.put(0,0);

        startActivity(shCard.SetupShare(cardsByGroups));
    }

}
