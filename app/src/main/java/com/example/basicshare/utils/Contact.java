package com.example.basicshare.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.widget.TextView;

public class Contact {
    
    //private variables
	int _id;
	int _image_id;
	int _owner_id;
    String _date_time;
    String _name;
    String _phone_number;
    String _email;
    String _street;
    String _place;
     
    // Empty constructor
    public Contact(){
         
    }
    // constructor
    public Contact(int id, int image_id, int owner_id,String date_time, String name, String _phone_number,String _email, String _street, String _place){
        this._id = id;
        this._image_id = image_id;
        this._owner_id = owner_id;
        this._date_time = date_time;
        this._name = name;
        this._phone_number = _phone_number;
        this._email = _email;
        this._street = _street;
        this._place = _place;
    }
     
    // constructor
    public Contact(int image_id, int owner_id, String name, String _phone_number, String _email, String _street, String _place){
    	this._image_id = image_id;
    	//this._date_time = date_time;
    	this._owner_id = owner_id;
    	this._name = name;
        this._phone_number = _phone_number;
        this._email = _email;
        this._street = _street;
        this._place = _place;
    }
    // getting ID
    public int getID(){
        return this._id;
    }
    
    // getting image ID
    public int getImageID(){
        return this._image_id;
    }
    
 // getting image ID
    public int getOwnerID(){
        return this._owner_id;
    }
    
    /*public String getDateTime(){
        return this._date_time;
    }*/
    
    // getting Date time
    public String getDateTimeC() {
        return this._date_time;
    }
    
    // setting name
    public void setDateTimeC(String date_time){
        this._date_time = date_time;
    }


    
    // setting id
    public void setID(int id){
        this._id = id;
    }
    
    // setting id
    public void setOwnerID(int owner_id){
        this._owner_id = owner_id;
    }


     
    // getting name
    public String getName(){
        return this._name;
    }
    
    
     
    // getting phone number
    public String getPhoneNumber(){
        return this._phone_number;
    }
    
    // getting email
    public String getEmail(){
        return this._email;
    }
     
    // getting street
    public String getStreet(){
        return this._street;
    }
    
    // getting place
    public String getPlace(){
        return this._place;
    }

    // setting name
    public void setName(String name){
        this._name = name;
    }
    
    // setting image_id
    public void setImageID(int image_id){
        this._image_id = image_id;
    }

     
    // setting phone number
    public void setPhoneNumber(String phone_number){
        this._phone_number = phone_number;
    }
    
    // setting email
    public void setEmail(String email){
        this._email = email;
    }
    
    // setting street
    public void setStreet(String street){
        this._street = street;
    }
    
    // setting place
    public void setPlace(String place){
        this._place = place;
    }
    
    
}
