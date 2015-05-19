package com.example.basicshare;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.basicshare.utils.Contact;
import com.example.basicshare.utils.UtilsPics;

import java.util.LinkedHashMap;


public class MainActivity extends Activity {

    private ShareActionProvider mShareActionProvider;

    private ImageView imageView;
    private TextView textView;
    private Button shareButton;

    private Context mContext;

    private UtilsPics uPics = new UtilsPics();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bitmap bm = null;
        final int imageID = 1;

        mContext = getApplicationContext();

        imageView = (ImageView)findViewById(R.id.imageView);
        textView = (TextView)findViewById(R.id.textView);
        shareButton = (Button)findViewById(R.id.share_button);

        bm = uPics.decodeSampledBitmapFromRes(getApplicationContext(),uPics.mThumbIds[(int)imageID-1],  70, 70);
        imageView.setImageBitmap(bm);
        imageView.setAdjustViewBounds(true);

        //textView.setText("Name");
        textView.setFocusable(true);
        textView.setEnabled(true);
        textView.setClickable(true);


        //ImageView ie = ((ImageView)holder.findViewById(R.id.im_expand));
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Contact contact = new Contact();
                contact.setImageID(imageID);
                contact.setName(textView.getText().toString());
                contact.setOwnerID(1);
                contact.setID(1);
                contact.setStreet("");
                contact.setPlace("");
                contact.setPhoneNumber("");

                InitShare(contact);

                //Toast.makeText(getApplicationContext(), "Ready to share?", Toast.LENGTH_LONG).show();
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

    /**
     * Launching activity to share card
     * */
    private void InitShare(Contact contact) {

        uPics.createFiles(mContext,contact);
        ShareCards shCard = new ShareCards(mContext);
        LinkedHashMap<Integer, Integer> cardsByGroups = new LinkedHashMap<Integer, Integer>();
        cardsByGroups.put(0,0);

        startActivity(shCard.SetupShare(cardsByGroups));
    }

}
