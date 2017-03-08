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
import com.server.project.api.Road;

public class LocationResponcer {
	public static void main(String[] args) throws Exception {
		Gson gson = new Gson();
		LocationResponcer ll = new LocationResponcer();

		// task location list
		List<Road> taskList = ll.getTaskLocationList();
		System.out.println(gson.toJson(taskList));

		// video road list
		// List<Road> videoList = ll.getRoadList();
		// System.out.println(gson.toJson(videoList));
	}

	public List<Road> getTaskLocationList() throws Exception {
		List<Road> taskLocation = new ArrayList<>();
		Class.forName("org.postgresql.Driver").newInstance();

		String url = "jdbc:postgresql://140.119.19.33:5432/project";
		Connection con = DriverManager.getConnection(url, "postgres", "093622"); // 帳號密碼
		Statement selectST = con.createStatement();

		// check time
		DateFormat dateFormat = new SimpleDateFormat("HH");
		Calendar cal = Calendar.getInstance();
		int userCurrentTime = Integer.valueOf(dateFormat.format(cal.getTime()));
		String reqTime = reqTimeToText(userCurrentTime);

		String sql = " select id, start_address, ST_AsText(start_geometry)  from task_" + reqTime + ";";

		ResultSet selectRS = selectST.executeQuery(sql);
		while (selectRS.next()) {
			Road road = new Road();
			int count = 0;
			for (Road loIndex : taskLocation) {
				if (selectRS.getString("start_address").equals(loIndex.getAddress())) {
					count++;
				}
			}
			if (count == 0) {
				int id = selectRS.getInt("id");
				String address = selectRS.getString("start_address");
				String addressPoint = selectRS.getString("st_astext");
				int startIndex = addressPoint.indexOf("(");
				int midIndex = addressPoint.indexOf(" ");
				int endIndex = addressPoint.indexOf(")");
				double lng = Double.valueOf(addressPoint.substring(startIndex + 1, midIndex));
				double lat = Double.valueOf(addressPoint.substring(midIndex + 1, endIndex));

				road.setId(id);
				road.setAddress(address);
				road.setLat(lat);
				road.setLng(lng);
				taskLocation.add(road);
			}
		}
		selectRS.close();
		selectST.close();
		con.close();
		return taskLocation;
	}

	public List<Road> getRoadList() throws Exception {
		List<Road> videoAddress = new ArrayList<>();
		Class.forName("org.postgresql.Driver").newInstance();

		String url = "jdbc:postgresql://140.119.19.33:5432/project";
		Connection con = DriverManager.getConnection(url, "postgres", "093622"); // 帳號密碼
		Statement selectST = con.createStatement();

		String sql = "select distinct address, ST_AsText(address_point)  from house;";

		ResultSet selectRS = selectST.executeQuery(sql);
		int count = 0;
		while (selectRS.next()) {
			count++;
			Road road = new Road();

			int id = selectRS.getInt("id");
			String address = selectRS.getString("address");
			String addressPoint = selectRS.getString("st_astext");
			int startIndex = addressPoint.indexOf("(");
			int midIndex = addressPoint.indexOf(" ");
			int endIndex = addressPoint.indexOf(")");
			double lng = Double.valueOf(addressPoint.substring(startIndex + 1, midIndex));
			double lat = Double.valueOf(addressPoint.substring(midIndex + 1, endIndex));

			road.setId(id);
			road.setAddress(address);
			road.setLat(lat);
			road.setLng(lng);
			videoAddress.add(road);

		}
		System.out.println("total address number = " + count);
		selectRS.close();
		selectST.close();
		con.close();

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
