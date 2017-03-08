package com.server.project.api;

public class Task {
	private String id;
	private String title;
	private String Address;
	private String time;
	private String distance;
	private String duration;
	private Point start_point;
	private Point end_point;

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

	public Point getStart_point() {
		return start_point;
	}

	public void setStart_point(Point start_point) {
		this.start_point = start_point;
	}

	public Point getEnd_point() {
		return end_point;
	}

	public void setEnd_point(Point end_point) {
		this.end_point = end_point;
	}

}
