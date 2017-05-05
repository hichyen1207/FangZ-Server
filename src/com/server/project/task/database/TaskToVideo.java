package com.server.project.task.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class TaskToVideo {
	public static void main(String[] args) throws Exception {
		TaskToVideo ttv = new TaskToVideo();
		ttv.toVideo("2", "iIPH8LFYFRk", "餐廳, 醫院", "陰天", "公園, 警察局", "10:00", "安靜");
	}

	public void toVideo(String id, String youtubeID, String shop, String weather, String facility, String time, String environment)
			throws Exception {
		// connect DB
		Class.forName("org.postgresql.Driver").newInstance();
		String url = "jdbc:postgresql://140.119.19.33:5432/project";
		Connection con = DriverManager.getConnection(url, "postgres", "093622"); // 帳號密碼
		Statement selectST = con.createStatement();
		Statement updateST = con.createStatement();

		String selectSQL = "select * from task where id=" + id;
		ResultSet selectRS = selectST.executeQuery(selectSQL);
		while (selectRS.next()) {
			// task to video
			String insertSQL = "insert into video (title, youtube_id, start_geometry, end_geometry, time, address, shop, weather, facility, environment) values ('"
					+ selectRS.getString("title") + "','" + youtubeID + "', '" + selectRS.getString("start_geometry")
					+ "', '" + selectRS.getString("end_geometry") + "', '" + time + "', '"
					+ selectRS.getString("address") + "', '" + shop + "', '" + weather + "', '" + facility + "', '" + environment + "');";
			// String deleteSQL = "delete from task where id=" + id;
			System.out.println(insertSQL);
			updateST.executeUpdate(insertSQL);
			System.out.println("task id:" + id + " insert to video");
			// updateST.executeUpdate(deleteSQL);
		}

		selectRS.close();
		selectST.close();
		updateST.close();
		con.close();
	}
}