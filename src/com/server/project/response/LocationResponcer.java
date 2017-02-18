package com.server.project.response;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;
import com.server.project.api.House;
import com.server.project.api.Location;
import com.server.project.api.Point;
import com.server.project.api.Road;
import com.server.project.tool.GeometryToPoint;
import com.server.project.tool.PointCreator;

public class LocationResponcer {
	public static void main(String[] args) {
		Gson gson = new Gson();
		LocationResponcer ll = new LocationResponcer();

		// task location list
		List<Location> taskList = ll.getTaskLocationList();
		System.out.println(gson.toJson(taskList));

		// video road list
		List<Road> videoList = ll.getVideoRoadList();
		System.out.println(gson.toJson(videoList));

		// house list
		List<House> houseList = ll.getHouseList("台灣新北市三重區重新路三段");
		System.out.println(gson.toJson(houseList));
	}

	public List<Location> getTaskLocationList() {
		List<Location> taskLocation = new ArrayList<>();
		try {
			Class.forName("org.postgresql.Driver").newInstance();

			String url = "jdbc:postgresql://140.119.19.33:5432/project";
			Connection con = DriverManager.getConnection(url, "postgres", "093622"); // 帳號密碼
			Statement selectST = con.createStatement();

			String sql = " select * from task;";

			ResultSet selectRS = selectST.executeQuery(sql);
			while (selectRS.next()) {
				int count = 0;
				for (Location loIndex : taskLocation) {
					if (selectRS.getString("location").equals(loIndex.getGeometry())) {
						count++;
					}
				}
				if (count == 0) {
					String geometry = selectRS.getString("location");
					GeometryToPoint toPoint = new GeometryToPoint();
					Point point = toPoint.toPoint(geometry);

					Location locationapi = new Location();
					locationapi.setLat(point.getLat());
					locationapi.setLng(point.getLng());
					locationapi.setGeometry(geometry);
					taskLocation.add(locationapi);
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

	public List<Road> getVideoRoadList() {
		PointCreator createPoint = new PointCreator();
		List<Road> videoAddress = new ArrayList<>();
		try {
			Class.forName("org.postgresql.Driver").newInstance();

			String url = "jdbc:postgresql://140.119.19.33:5432/project";
			Connection con = DriverManager.getConnection(url, "postgres", "093622"); // 帳號密碼
			Statement selectST = con.createStatement();

			String sql = " select * from video;";

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

	public List<House> getHouseList(String address) {
		List<House> houseList = new ArrayList<>();
		try {
			Class.forName("org.postgresql.Driver").newInstance();

			String url = "jdbc:postgresql://140.119.19.33:5432/project";
			Connection con = DriverManager.getConnection(url, "postgres", "093622"); // 帳號密碼
			Statement selectST = con.createStatement();

			String videoSQL = " select * from video where address = '" + address + "';";
			ResultSet selectRS = selectST.executeQuery(videoSQL);
			while (selectRS.next()) {
				House house = new House();
				String location = selectRS.getString("location");
				house.setAddress(address);
				house.setLocation(location);

				String houseSQL = " select * from house where location = '" + location + "';";
				ResultSet selectVideoRS = selectST.executeQuery(houseSQL);
				while (selectVideoRS.next()) {
					house.setTitle(selectVideoRS.getString("title"));
					house.setDescription(selectVideoRS.getString("description"));
					house.setPrice(selectVideoRS.getString("price"));

					houseList.add(house);
				}
			}
		} catch (Exception e) {
			e.getMessage();
		}
		return houseList;
	}
}
