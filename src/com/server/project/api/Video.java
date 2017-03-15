package com.server.project.api;

public class Video {
	private int id;
	private String youtube_id;
	private String title;
	private double[] start_geometry;
	private double[] end_geometry;
	private String time;
	private String address;
	private String shop;
	private String weather;
	private String facility;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getYoutube_id() {
		return youtube_id;
	}

	public void setYoutube_id(String youtube_id) {
		this.youtube_id = youtube_id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public double[] getStart_geometry() {
		return start_geometry;
	}

	public void setStart_geometry(double[] start_geometry) {
		this.start_geometry = start_geometry;
	}

	public double[] getEnd_geometry() {
		return end_geometry;
	}

	public void setEnd_geometry(double[] end_geometry) {
		this.end_geometry = end_geometry;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getShop() {
		return shop;
	}

	public void setShop(String shop) {
		this.shop = shop;
	}

	public String getWeather() {
		return weather;
	}

	public void setWeather(String weather) {
		this.weather = weather;
	}

	public String getFacility() {
		return facility;
	}

	public void setFacility(String facility) {
		this.facility = facility;
	}

}
