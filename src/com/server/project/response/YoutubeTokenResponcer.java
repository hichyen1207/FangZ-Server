package com.server.project.response;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class YoutubeTokenResponcer {
	public void saveATtoDB(String access_token, String refresh_token) throws Exception {
		Class.forName("org.postgresql.Driver").newInstance();
		String url = "jdbc:postgresql://140.119.19.33:5432/project";
		Connection con = DriverManager.getConnection(url, "postgres", "093622");
		Statement stmt = con.createStatement();
		String insertATSQL = "INSERT INTO accessToken(access_token, refresh_token, time) VALUES('" + access_token
				+ "', '" + refresh_token + "', " + "NOW()" + ");";
		stmt.executeUpdate(insertATSQL);
		stmt.close();
		con.close();
	}

	public String getAccessToken() throws Exception {
		Connection c = null;
		Class.forName("org.postgresql.Driver");
		c = DriverManager.getConnection("jdbc:postgresql://140.119.19.33:5432/project", "postgres", "093622");
		System.out.println("Opened database successfully");
		Statement stmt = c.createStatement();
		String getATSQL = "SELECT access_token FROM accessToken where id=2;";
		ResultSet rs = stmt.executeQuery(getATSQL);

		if (rs.next()) {
			return rs.getString("access_token");
		} else {
			System.out.println("no access yoken in DB");
			return null;
		}

	}
}
