package com.server.project.response;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;
import com.server.project.api.Community;
import com.server.project.api.House;
import com.server.project.tool.GeometryToPoint;

public class HouseResponcer {
	public static void main(String[] args) throws Exception {
		Gson gson = new Gson();
		HouseResponcer hr = new HouseResponcer();

		// house list
		// List<House> houseList = hr.getHouseList("台北市中正區博愛路");
		// System.out.println(gson.toJson(houseList));

		// house
		House house = hr.getHouse(129);
		System.out.println(gson.toJson(house));
	}

	public List<House> getHouseList(String address) throws Exception {
		List<House> houseList = new ArrayList<>();

		Class.forName("org.postgresql.Driver").newInstance();
		String url = "jdbc:postgresql://140.119.19.33:5432/project";
		Connection con = DriverManager.getConnection(url, "postgres", "093622"); // 帳號密碼
		Statement selectST = con.createStatement();

		String houseListSQL = " select * from house where address = '" + address + "';";
		ResultSet selectRS = selectST.executeQuery(houseListSQL);
		while (selectRS.next()) {
			House house = new House();
			house.setId(Integer.valueOf(selectRS.getString("id")));
			house.setTitle(selectRS.getString("title"));
			house.setAddress(address);
			house.setType(selectRS.getString("type"));
			house.setPrice(selectRS.getString("price"));
			house.setSquare(selectRS.getString("square"));
			houseList.add(house);
		}

		selectRS.close();
		selectST.close();
		con.close();
		return houseList;
	}

	public House getHouse(int id) throws Exception {
		House house = null;
		Gson gson = new Gson();
		Class.forName("org.postgresql.Driver").newInstance();

		String url = "jdbc:postgresql://140.119.19.33:5432/project";
		Connection con = DriverManager.getConnection(url, "postgres", "093622"); // 帳號密碼
		Statement selectST = con.createStatement();

		String houseListSQL = " select * from house where id = '" + id + "';";
		ResultSet selectRS = selectST.executeQuery(houseListSQL);
		while (selectRS.next()) {
			house = new House();
			house.setId(id);
			house.setTitle(selectRS.getString("title"));
			house.setAddress(selectRS.getString("address"));
			house.setType(selectRS.getString("type"));
			house.setPrice(selectRS.getString("price"));
			house.setDescription(selectRS.getString("description"));
			house.setUrl(selectRS.getString("url"));
			house.setPicture(selectRS.getString("picture"));
			house.setLife(selectRS.getString("life"));
			house.setInformation(selectRS.getString("information"));
			house.setSquare(selectRS.getString("square"));

			Community community = gson.fromJson(selectRS.getString("community"), Community.class);
			house.setCommunity(community);

			String locationGeo = selectRS.getString("location");
			GeometryToPoint toPoint = new GeometryToPoint();
			com.server.project.api.Point locationPoint = toPoint.toPoint(locationGeo);
			double[] location = { locationPoint.getLat(), locationPoint.getLng() };
			house.setLocationPoint(location);
		}
		selectRS.close();
		selectST.close();
		con.close();

		return house;
	}
}
