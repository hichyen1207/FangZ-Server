package com.server.project.api;

public class Task {
	private String id;
	private String title;
	private String Address;
	private String start_geometry;
	private String end_geometry;
	private String time;
	private String distance;
	private String duration;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAddress() {
		return Address;
	}

	public void setAddress(String address) {
		Address = address;
	}

	public String getStart_geometry() {
		return start_geometry;
	}

	public void setStart_geometry(String start_geometry) {
		this.start_geometry = start_geometry;
	}

	public String getEnd_geometry() {
		return end_geometry;
	}

	public void setEnd_geometry(String end_geometry) {
		this.end_geometry = end_geometry;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getDistance() {
		return distance;
	}

	public void setDistance(String distance) {
		this.distance = distance;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

}
