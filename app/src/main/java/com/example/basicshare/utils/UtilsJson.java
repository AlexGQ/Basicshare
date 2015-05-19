package com.example.basicshare.utils;

import android.content.Context;
import android.graphics.Bitmap;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class UtilsJson {

public static String GroupsToJSon(LinkedHashMap<String, List<Contact>> dataList) {

   try {

        // Here we convert Java Object to JSON
        JSONObject jsonObj = new JSONObject();
        
        for (Entry<String, List<Contact>> entry : dataList.entrySet()) 
        {
                String group_name = entry.getKey();
                JSONArray jsonOneGroup = new JSONArray();
                
                List<Contact> contactList = new ArrayList<Contact>();
                contactList = dataList.get(group_name);
                
                for (int i = 0; i < contactList.size(); i++)
                {
                	JSONObject jsonOneContact = new JSONObject(); 
                	
                	Contact contact = new Contact();
                	contact = contactList.get(i);

                	jsonOneContact.put("Street", contact.getStreet());
                	jsonOneContact.put("Place", contact.getPlace());
                	jsonOneContact.put("PhoneNumber", contact.getPhoneNumber());
                	jsonOneContact.put("ImageID", contact.getImageID());
                	jsonOneContact.put("CardID", contact.getID());
                	jsonOneContact.put("Name", contact.getName());
                	jsonOneContact.put("OwnerID", contact.getOwnerID());
                	
                	jsonOneGroup.put(jsonOneContact);
                }
                jsonObj.put(group_name,jsonOneGroup);
        }   
        
        JSONArray jsonGroups = new JSONArray();
        
        for (Entry<String, List<Contact>> entry : dataList.entrySet()) 
        {
                String group_name = entry.getKey();
                JSONObject jsonGroupsNames = new JSONObject();
                jsonGroupsNames.put("GroupName", group_name);
                jsonGroups.put(jsonGroupsNames);
        }        
        jsonObj.put("Groups", jsonGroups);
        jsonObj.put("ShareGroup", true);

        return jsonObj.toString();
    }
    catch(JSONException ex) {

        ex.printStackTrace();

    }
    return null;
   }


public static String ContactsToJSon(List<Contact> contactList) {

	   try {

	        // Here we convert Java Object to JSON
		   	JSONObject jsonObj = new JSONObject();
		   	
		   	JSONArray jsonContacts = new JSONArray();
		   	
            for (int i = 0; i < contactList.size(); i++)
            {
            	JSONObject jsonOneContact = new JSONObject(); 
            	
            	Contact contact = new Contact();
            	contact = contactList.get(i);

            	jsonOneContact.put("Street", contact.getStreet());
            	jsonOneContact.put("Place", contact.getPlace());
            	jsonOneContact.put("PhoneNumber", contact.getPhoneNumber());
            	jsonOneContact.put("ImageID", contact.getImageID());
            	jsonOneContact.put("CardID", contact.getID());
            	jsonOneContact.put("Name", contact.getName());
            	jsonOneContact.put("OwnerID", contact.getOwnerID());
	        	
            	jsonContacts.put(jsonOneContact);
            }
            jsonObj.put("Contacts", jsonContacts);
        	jsonObj.put("ShareGroup", false);
        	
	        return jsonObj.toString();
	    }
	    catch(JSONException ex) {

	        ex.printStackTrace();

	    }
	    return null;
	   }



public static LinkedHashMap<String, List<Contact>> JSonToGroups(String str){
			LinkedHashMap<String, List<Contact>> dataList = new LinkedHashMap<String, List<Contact>>();

	   try {
		   
		   	JSONObject jsonObj = new JSONObject(str);
		   	
		   	JSONArray jsonGroups = jsonObj.getJSONArray("Groups");
		   	
	        for (int ng = 0; ng < jsonGroups.length(); ng++) 
	        {
	        	    JSONObject jsonGroupsNames = jsonGroups.getJSONObject(ng);
	        	    
	        	    String group_name = jsonGroupsNames.getString("GroupName");
	                
	                JSONArray jsonOneGroup = jsonObj.getJSONArray(group_name);
	                
	                List<Contact> contactList = new ArrayList<Contact>();
	                
	                for (int nc = 0; nc < jsonOneGroup.length(); nc++)
	                {
	                	Contact contact = new Contact();
	                	
	                	JSONObject jsonOneContact = jsonOneGroup.getJSONObject(nc);
	                	
	                	contact.setStreet(jsonOneContact.getString("Street")); 
	                	contact.setPlace(jsonOneContact.getString("Place"));
	                	contact.setPhoneNumber(jsonOneContact.getString("PhoneNumber"));
	                	contact.setImageID(jsonOneContact.getInt("ImageID"));
	                	contact.setID(jsonOneContact.getInt("CardID"));
	                	contact.setName(jsonOneContact.getString("Name"));
	                	contact.setOwnerID(jsonOneContact.getInt("OwnerID"));
	                	
	                	contactList.add(contact);
	                	
	                }
	                
	                dataList.put(group_name, contactList);
	                
	        }
	        
	        return dataList;
	    }
	    catch(JSONException ex) {

	        ex.printStackTrace();

	    }
	    return dataList;
	   }





public static List<Contact> JSonToContacts(String str){

	   try {
		   	
		   	JSONObject jsonObj = new JSONObject(str);
		   	
		   	JSONArray jsonContacts = jsonObj.getJSONArray("Contacts");
		   	
            List<Contact> contactList = new ArrayList<Contact>();
            
            for (int nc = 0; nc < jsonContacts.length(); nc++)
            {
            	Contact contact = new Contact();
            	
            	JSONObject jsonOneContact = jsonContacts.getJSONObject(nc);
            	
            	contact.setStreet(jsonOneContact.getString("Street")); 
            	contact.setPlace(jsonOneContact.getString("Place"));
            	contact.setPhoneNumber(jsonOneContact.getString("PhoneNumber"));
            	contact.setImageID(jsonOneContact.getInt("ImageID"));
            	contact.setID(jsonOneContact.getInt("CardID"));
            	contact.setName(jsonOneContact.getString("Name"));
            	contact.setOwnerID(jsonOneContact.getInt("OwnerID"));
            	
            	contactList.add(contact);
            	
            }
	                
	        return contactList;
	    }
	    catch(JSONException ex) {

	        ex.printStackTrace();

	    }
	    return null;
	   }

	public static boolean checkShareGroupsOrCards(String str){
		
			boolean ShareGroup = false;
			
			try {
			
				JSONObject jsonObj = new JSONObject(str);
				ShareGroup = jsonObj.getBoolean("ShareGroup");
			}
		    catch(JSONException ex) {
		        ex.printStackTrace();
		    }
		   	
			return ShareGroup;
	}
}
