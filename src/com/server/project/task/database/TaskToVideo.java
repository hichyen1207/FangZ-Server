package com.server.project.task.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import com.server.project.tool.AddressCreator;

public class TaskToVideo {
	public static void main(String[] args) {
		TaskToVideo ttv = new TaskToVideo();
		ttv.toVideo("390", "iIPH8LFYFRk");
	}

	public void toVideo(String id, String youtubeID) {
		try {
			// connect DB
			Class.forName("org.postgresql.Driver").newInstance();
			String url = "jdbc:postgresql://140.119.19.33:5432/project";
			Connection con = DriverManager.getConnection(url, "postgres", "093622"); // 帳號密碼
			Statement selectST = con.createStatement();
			Statement updateST = con.createStatement();

			String selectSQL = "select * from task where id=" + id;
			ResultSet selectRS = selectST.executeQuery(selectSQL);
			while (selectRS.next()) {
				AddressCreator addressCreator = new AddressCreator();
				String address = addressCreator.createAddress(selectRS.getString("location"));							

				// task to video
				String insertSQL = "insert into video (id, title, youtube_id, location, start_geometry, end_geometry, time, address) values ("
						+ selectRS.getString("id") + ",'" + selectRS.getString("title") + "','" + youtubeID + "', '"
						+ selectRS.getString("location") + "', '" + selectRS.getString("start_geometry") + "', '"
						+ selectRS.getString("end_geometry") + "', '" + selectRS.getString("time") + "', '" + address
						+ "');";
				String deleteSQL = "delete from task where id=" + id;
				updateST.executeUpdate(insertSQL);
				System.out.println("task id:" + id + " insert to video");
				updateST.executeUpdate(deleteSQL);
			}

			selectRS.close();
			selectST.close();
			updateST.close();
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}