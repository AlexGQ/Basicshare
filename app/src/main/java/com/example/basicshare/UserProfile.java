package com.example.basicshare;


/**
 * Informacion que debe completarse en el momento del registro, no todos los campos son necesario. 
 * Se marca coon un asterisco lo que es obligatorio. 
 * @author campino
 *
 */
public class UserProfile {
	
	
	// Info basica registro 
	   private String email;	 //*
	   private String name; 	 //*
	   private String surname;   //*
	   private String picture;	 //*
	   
	   
	   private String occupation;    //* Campo obligatorio 	  
	   private String organization;
	   private String description;
	   private String mobile;
	   private String phone; 
	  
	   private String streetname;
	   private String streetnumber;
	   private String homenumber;
	   private String neighborhood;
	   private String city;
	   private String contry;
	   private String zipcod;
	  
	   private String web;
	   private String linkeding; 
	   private String facebook; 
	   private String plus;
	   private String twitter;
	   
	   
	   private String facebookToken; 
	   
	  
	   
	public UserProfile(String email,String name, String surname, String picture){
		this.email = email; 
		this.name = name; 
		this.surname = surname; 
		this.picture = picture; 
	}
	

	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSurname() {
		return surname;
	}
	public void setSurname(String surname) {
		this.surname = surname;
	}
	public String getPicture() {
		return picture;
	}
	public void setPicture(String picture) {
		this.picture = picture;
	}
	public String getOccupation() {
		return occupation;
	}
	public void setOccupation(String occupation) {
		this.occupation = occupation;
	}
	public String getOrganization() {
		return organization;
	}
	public void setOrganization(String organization) {
		this.organization = organization;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getStreetname() {
		return streetname;
	}
	public void setStreetname(String streetname) {
		this.streetname = streetname;
	}
	public String getStreetnumber() {
		return streetnumber;
	}
	public void setStreetnumber(String streetnumber) {
		this.streetnumber = streetnumber;
	}
	public String getHomenumber() {
		return homenumber;
	}
	public void setHomenumber(String homenumber) {
		this.homenumber = homenumber;
	}
	public String getNeighborhood() {
		return neighborhood;
	}
	public void setNeighborhood(String neighborhood) {
		this.neighborhood = neighborhood;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getContry() {
		return contry;
	}
	public void setContry(String contry) {
		this.contry = contry;
	}
	public String getZipcod() {
		return zipcod;
	}
	public void setZipcod(String zipcod) {
		this.zipcod = zipcod;
	}
	public String getWeb() {
		return web;
	}
	public void setWeb(String web) {
		this.web = web;
	}
	public String getLinkeding() {
		return linkeding;
	}
	public void setLinkeding(String linkeding) {
		this.linkeding = linkeding;
	}
	public String getFacebook() {
		return facebook;
	}
	public void setFacebook(String facebook) {
		this.facebook = facebook;
	}
	public String getPlus() {
		return plus;
	}
	public void setPlus(String plus) {
		this.plus = plus;
	}
	public String getTwitter() {
		return twitter;
	}
	public void setTwitter(String twitter) {
		this.twitter = twitter;
	}


	public String getFacebookToken() {
		return facebookToken;
	}


	public void setFacebookToken(String facebookToken) {
		this.facebookToken = facebookToken;
	}
		 
	
}
