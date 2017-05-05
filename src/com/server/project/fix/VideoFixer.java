package com.server.project.fix;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class VideoFixer {
	public static void main(String[] args) throws Exception {
		VideoFixer videoFixer = new VideoFixer();
		videoFixer.fixVideoTitle();
	}

	public void fixVideoTitle() throws Exception {
		Class.forName("org.postgresql.Driver").newInstance();
		String url = "jdbc:postgresql://140.119.19.33:5432/project";
		Connection con = DriverManager.getConnection(url, "postgres", "093622"); // 帳號密碼
		Statement selectST = con.createStatement();
		Statement updateST = con.createStatement();

		String selectSQL = "select id, title from video;";

		ResultSet selectRS = selectST.executeQuery(selectSQL);
		while (selectRS.next()) {
			String title = selectRS.getString("title");
			String id = selectRS.getString("id");

			int index = title.indexOf("_");
			title = (String) title.subSequence(0, index);			
			
			String updateSQL = "update video set title='" + title + "' where id=" + id + ";";
			System.out.println(updateSQL);
			updateST.executeUpdate(updateSQL);
		}
		selectRS.close();
		selectST.close();
		updateST.close();
		con.close();
	}
}
