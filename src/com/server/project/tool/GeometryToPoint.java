package com.server.project.tool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.server.project.api.Point;

public class GeometryToPoint {
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		GeometryToPoint gtp = new GeometryToPoint();
		Point point = gtp.toPoint("0101000020E6100000E04735EC775E5E409F73B7EBA5013940");
		System.out.println("[" + point.getLat() + ", " + point.getLng() + "]");
	}

	// 把 geometry point 轉成經度座標、緯度座標
	public Point toPoint(String locationCode) throws ClassNotFoundException, SQLException {
		Connection c = null;
		Statement stmt = null;
		// connect DB
		Class.forName("org.postgresql.Driver");
		c = DriverManager.getConnection("jdbc:postgresql://140.119.19.33:5432/project", "postgres", "093622");
		// execute sql commands
		stmt = c.createStatement();
		String geoToTextSQL = "SELECT ST_AsText('" + locationCode + "');";
		ResultSet rs = stmt.executeQuery(geoToTextSQL);
		// 字串處理
		rs.next();
		String point = rs.getString("st_astext");
		String location = point.replace("POINT(", "").replace(")", "");
		// 經度
		double longitude = Double.valueOf(location.split(" ")[0]);
		// 緯度
		double latitude = Double.valueOf(location.split(" ")[1]);

		Point coordinate = new Point();
		coordinate.setLat(latitude);
		coordinate.setLng(longitude);

		rs.close();
		stmt.close();
		c.close();
		return coordinate;
	}

}
