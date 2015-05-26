
package com.example.basicshare;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcelable;
import android.widget.Toast;

import com.example.basicshare.utils.Contact;
import com.example.basicshare.utils.UtilsJson;
import com.example.basicshare.utils.UtilsPics;

public class ShareCards {
	
	Context mContext;
	
    // constructor
    public ShareCards(Context context) {
		mContext = context;
    }
        
    public Intent SetupShare(LinkedHashMap<Integer, Integer> cardsByGroups){
   	
    	Uri orgUri;
    	UtilsPics im = new UtilsPics(mContext);
    	ArrayList<Uri> fileUris = new ArrayList<Uri>();
    	
	   	int nc;
    	int ng = 0;
    	String s1;
    	String s2;
    	//list(Integer)
    	for (Entry<Integer, Integer> entry : cardsByGroups.entrySet()) 
        {
            // Get the group index
    		ng = entry.getKey();
			nc = cardsByGroups.get(ng);
            s1 = new Integer(ng).toString(); 
                
	    	for (int i = 0; i < (nc + 1); i++)
	    	{
	    		
	    		s2 = new Integer(i).toString();
	    		String transferFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + UtilsPics.APP_FOLDER + "/" + UtilsPics.IMFILENAME + s1 +s2 + ".jpg";
	    	
	    		File requestFile = new File(transferFile);
	    		requestFile.setReadable(true, false);
	    		orgUri = Uri.fromFile(requestFile);
	    		fileUris.add(orgUri);
	    	}
	    	
        }
    	
		String transferFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + UtilsPics.APP_FOLDER + "/" + UtilsPics.JSONFILENAME + ".json";
	
		File requestFile = new File(transferFile);
		requestFile.setReadable(true, false);
		orgUri = Uri.fromFile(requestFile);
		fileUris.add(orgUri);

		Intent shareIntent = CreateChooserList(fileUris);

    	
    	//shareIntent.putExtra(Intent.EXTRA_SUBJECT,"My business card");
    	//shareIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
    	//shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, fileUris);
    	
    	return shareIntent;
	}

	public Intent CreateChooserList (ArrayList<Uri> fileUris){

		Resources resources = mContext.getResources();

		Intent chooserIntent = null;

		List<Intent> targetShareIntents = new ArrayList<Intent>();
		Intent shareIntent = new Intent();
		shareIntent.setAction(Intent.ACTION_SEND);
		shareIntent.setType("text/plain");

		List<ResolveInfo> resInfos = mContext.getPackageManager().queryIntentActivities(shareIntent, 0);

		if(!resInfos.isEmpty()){
			System.out.println("Have package");
			for(ResolveInfo resInfo : resInfos){
				String packageName=resInfo.activityInfo.packageName;
				//Log.i("Package Name", packageName);
				if (packageName.contains("whatsapp") || packageName.contains("facebook") || packageName.contains("linkedin") || packageName.contains("android.gm")){
					Intent intent = new Intent();
					intent.setComponent(new ComponentName(packageName, resInfo.activityInfo.name));
					intent.setAction(Intent.ACTION_SEND_MULTIPLE);
					//intent.setAction(Intent.ACTION_SEND);
					//intent.setType("*/*");
					intent.setType("text/plain");
					intent.putExtra(Intent.EXTRA_SUBJECT, "My business card");
					intent.putExtra(Intent.EXTRA_TEXT, "This is my text to send. www.google.com");

					//intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, fileUris);
					intent.setPackage(packageName);
					targetShareIntents.add(intent);
				}
			}
			if(!targetShareIntents.isEmpty()){
				//System.out.println("Have Intent");
				chooserIntent = Intent.createChooser(targetShareIntents.remove(0), resources.getString(R.string.text_share_card_to));
				chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetShareIntents.toArray(new Parcelable[]{}));

			}else{
				Toast.makeText(mContext, "There are not applications to share Qcards", Toast.LENGTH_SHORT).show();

			}
		}
		return chooserIntent;
	}
	
    /**
	 * 
	 * */
	public LinkedHashMap<Integer, Integer> ShareGroups(LinkedHashMap<String, List<Contact>> groupListToShare) {
		
    	int indexGroup = 0;
    	UtilsPics im = new UtilsPics(mContext);
    	List<Contact> contactListToShare = new ArrayList<Contact>();
    	
		File FolderDir = im.FindDir();
		LinkedHashMap<Integer, Integer> cardsByGroups = new LinkedHashMap<Integer, Integer>();
		LinkedHashMap<Integer, Integer> cbgTemp = new LinkedHashMap<Integer, Integer>();
		
		for (Entry<String, List<Contact>> entry : groupListToShare.entrySet()) 
        {
                String group_name = entry.getKey();
                // Add the group name
                contactListToShare = groupListToShare.get(group_name);
                cbgTemp = ShareCards(contactListToShare, indexGroup);
                cardsByGroups.put(indexGroup, cbgTemp.get(indexGroup));
                indexGroup++;
        } 
		
		String groupsData = UtilsJson.GroupsToJSon(groupListToShare);
		im.saveFileToInternalStorage(groupsData,FolderDir ,UtilsPics.JSONFILENAME);
		
		return cardsByGroups;
	}
	
    /**
	 * 
	 * */
	public LinkedHashMap<Integer, Integer> ShareCards(List<Contact> contactListToShare, int indexGroup) {
		
		String subix1;
    	String subix2;
    	LinkedHashMap<Integer, Integer> cardsByGroups = new LinkedHashMap<Integer, Integer>();
    	
    	int indexCards;
    	int imageID;
    	Bitmap bm;
    	UtilsPics im = new UtilsPics(mContext);
    	Contact contact = new Contact();
    	
    	
		File FolderDir = im.FindDir();
		subix1 = new Integer(indexGroup).toString();    	
            	
        for (indexCards = 0; indexCards < contactListToShare.size(); indexCards++)
        {
        	contact = contactListToShare.get(indexCards);
        	imageID = contact.getImageID();
        	
        	subix2 = new Integer(indexCards).toString();
        	
        	        			
			bm = im.decodeSampledBitmapFromRes(mContext,im.mThumbIds[imageID-1],  220, 220);
			// Copy file to memory 
			
			String filename = UtilsPics.IMFILENAME + subix1 + subix2;
			im.saveImageToInternalStorage(bm,FolderDir, filename);
        }
        cardsByGroups.put(indexGroup,indexCards-1);

		
		String cardData = UtilsJson.ContactsToJSon(contactListToShare);
		// Create JSON file and Copy it to memory
		im.saveFileToInternalStorage(cardData,FolderDir ,UtilsPics.JSONFILENAME);

		return cardsByGroups;
	}

}
