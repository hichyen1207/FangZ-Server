package com.server.project;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import com.server.project.api.Point;
import com.server.project.tool.PointCreator;

public class Debugger {
	public static void main(String[] args) {
		Debugger debugger = new Debugger();
		debugger.addAddressPoint();
	}

	public void addAddressPoint() {
		PointCreator pointCreator = new PointCreator();
		try {
			Class.forName("org.postgresql.Driver").newInstance();
			String url = "jdbc:postgresql://140.119.19.33:5432/project";
			Connection con = DriverManager.getConnection(url, "postgres", "093622"); // 帳號密碼
			Statement selectST = con.createStatement();

			String getAddressSQL = "select * from house";
			ResultSet selectRS = selectST.executeQuery(getAddressSQL);
			while (selectRS.next()) {
				String address = selectRS.getString("address");
				Point addressPoint = pointCreator.createPointByRoad(address);
				// System.out.println(gson.toJson(addressPoint));

				String id = selectRS.getString("id");
				String addPointSQL = "update house set address_point=ST_GeomFromText('POINT(" + addressPoint.getLng()
						+ " " + addressPoint.getLat() + ")', 4326) where id=" + id + ";";
				System.out.println(addPointSQL);
				Statement updateST = con.createStatement();
				updateST.executeUpdate(addPointSQL);

				Thread.sleep(500);
			}
		} catch (Exception e) {
			e.getMessage();
		}
	}
}
