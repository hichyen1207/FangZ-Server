package com.server.project;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class addData {
	public static void main(String[] args) {
		try {
			Class.forName("org.postgresql.Driver").newInstance();
			String url = "jdbc:postgresql://140.119.19.33:5432/project";
			Connection con = DriverManager.getConnection(url, "postgres", "093622"); // 帳號密碼
			Statement st = con.createStatement();

			System.out.println("start");
			String SQL = "";
			for (int i = 0; i <= 10; i++) {
				String insertSQL = "insert into video_processing (id, title, location, time) values ("+i+",'title_" + i
						+ "',ST_GeomFromText('POINT(10" + i + ".5546380 3" + i + ".2339138)', 4326), CURRENT_TIMESTAMP);";
				System.out.println(insertSQL);
				SQL = SQL + insertSQL;
			}

			st.executeQuery(SQL);
		} catch (Exception e) {
			e.getMessage();
		}
	}

}
