package com.example.basicshare;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

//public class GetFileCardsActivity  extends FragmentActivity implements RepeateGroupDialogListener{
public class GetCardsActivity  extends FragmentActivity{
	
	private SharedPreferences settings;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_file);
        
       /* String PREFS_NAME = "MyPrefsFile";
        settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        */
        ImportFileFragment firstFragment = new ImportFileFragment();
        
        // pass the Intent's extras to the fragment as arguments
        firstFragment.setArguments(getIntent().getExtras());

        // Add the fragment to the 'fragment_container' FrameLayout
        getSupportFragmentManager().beginTransaction()
                .add(R.id.frag_container_import, firstFragment, "IMPORT_FILE_FRAGMENT").commit();

        
    }

	@Override
	  protected void onResume() {
	      super.onResume();

	      // If the app is run for first time

	  	}
}