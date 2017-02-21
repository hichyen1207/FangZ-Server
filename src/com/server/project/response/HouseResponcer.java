package com.server.project.response;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;
import com.server.project.api.House;

public class HouseResponcer {
	public static void main(String[] args) {
		Gson gson = new Gson();
		HouseResponcer hr = new HouseResponcer();

		// house list
		List<House> houseList = hr.getHouseList("台北市中正區博愛路");
		System.out.println(gson.toJson(houseList));

		// house
		House house = hr.getHouse(13);
		System.out.println(gson.toJson(house));
	}

	public List<House> getHouseList(String address) {
		List<House> houseList = new ArrayList<>();
		try {
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
				house.setRegisteredSquare(selectRS.getString("registered_square"));
				house.setPrice(selectRS.getString("price"));
				houseList.add(house);
			}
		} catch (Exception e) {
			e.getMessage();
		}
		return houseList;
	}

	public House getHouse(int id) {
		House house = null;
		try {
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
				house.setRegisteredSquare(selectRS.getString("registered_square"));
				house.setPrice(selectRS.getString("price"));
				house.setDescription(selectRS.getString("description"));
				house.setPattern(selectRS.getString("pattern"));
				house.setStatus(selectRS.getString("status"));
				house.setUrl(selectRS.getString("url"));
			}
		} catch (Exception e) {
			e.getMessage();
		}
		return house;
	}
}
