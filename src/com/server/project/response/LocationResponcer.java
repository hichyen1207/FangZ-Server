package com.server.project.response;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import com.google.gson.Gson;
import com.server.project.api.Point;
import com.server.project.api.Road;
import com.server.project.tool.PointCreator;

public class LocationResponcer {
	public static void main(String[] args) {
		Gson gson = new Gson();
		LocationResponcer ll = new LocationResponcer();

		// task location list
		List<Road> taskList = ll.getTaskLocationList();
		System.out.println(gson.toJson(taskList));

		// video road list
		List<Road> videoList = ll.getRoadList();
		System.out.println(gson.toJson(videoList));
	}

	public List<Road> getTaskLocationList() {
		PointCreator createPoint = new PointCreator();
		List<Road> taskLocation = new ArrayList<>();
		try {
			Class.forName("org.postgresql.Driver").newInstance();

			String url = "jdbc:postgresql://140.119.19.33:5432/project";
			Connection con = DriverManager.getConnection(url, "postgres", "093622"); // 帳號密碼
			Statement selectST = con.createStatement();

			// check time
			DateFormat dateFormat = new SimpleDateFormat("HH");
			Calendar cal = Calendar.getInstance();
			int userCurrentTime = Integer.valueOf(dateFormat.format(cal.getTime()));
			String reqTime = reqTimeToText(userCurrentTime);

			String sql = " select * from task_" + reqTime + ";";

			ResultSet selectRS = selectST.executeQuery(sql);
			while (selectRS.next()) {
				Road road = new Road();
				int count = 0;
				for (Road loIndex : taskLocation) {
					if (selectRS.getString("address").equals(loIndex.getAddress())) {
						count++;
					}
				}
				if (count == 0) {
					String address = selectRS.getString("address");
					Point addressPoint = createPoint.createPointByRoad(address);
					road.setAddress(address);
					road.setLat(addressPoint.getLat());
					road.setLng(addressPoint.getLng());
					taskLocation.add(road);
				}
			}
			selectRS.close();
			selectST.close();
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return taskLocation;
	}

	public List<Road> getRoadList() {
		PointCreator createPoint = new PointCreator();
		List<Road> videoAddress = new ArrayList<>();
		try {
			Class.forName("org.postgresql.Driver").newInstance();

			String url = "jdbc:postgresql://140.119.19.33:5432/project";
			Connection con = DriverManager.getConnection(url, "postgres", "093622"); // 帳號密碼
			Statement selectST = con.createStatement();

			String sql = " select * from house;";

			ResultSet selectRS = selectST.executeQuery(sql);
			while (selectRS.next()) {
				Road road = new Road();
				int count = 0;
				for (Road loIndex : videoAddress) {
					if (selectRS.getString("address").equals(loIndex.getAddress())) {
						count++;
					}
				}
				if (count == 0) {
					String address = selectRS.getString("address");
					Point addressPoint = createPoint.createPointByRoad(address);
					road.setAddress(address);
					road.setLat(addressPoint.getLat());
					road.setLng(addressPoint.getLng());
					videoAddress.add(road);
				}
			}
			selectRS.close();
			selectST.close();
			con.close();
		} catch (Exception e) {
			e.getMessage();
		}
		return videoAddress;
	}

	public String reqTimeToText(int reqTimeValue) {
		if (reqTimeValue >= 6 && reqTimeValue < 12) {
			return "morning";
		} else if (reqTimeValue >= 12 && reqTimeValue < 18) {
			return "afternoon";
		} else if (reqTimeValue >= 18 && reqTimeValue < 24) {
			return "night";
		} else if (reqTimeValue >= 0 && reqTimeValue < 6) {
			return "midnight";
		} else {
			return null;
		}
	}
}
