package com.server.project.fix;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class taskFixer {
	public static void main(String[] args) throws Exception {
		taskFixer taskFixer = new taskFixer();
//		taskFixer.fixTask();
		taskFixer.fixEndNote();
	}

	public void fixTask() throws Exception {
		Class.forName("org.postgresql.Driver").newInstance();
		String url = "jdbc:postgresql://140.119.19.33:5432/project";
		Connection con = DriverManager.getConnection(url, "postgres", "093622"); // 帳號密碼
		Statement selectST = con.createStatement();

		String selectSQL = "select * from task;";
		ResultSet selectRS = selectST.executeQuery(selectSQL);
		while (selectRS.next()) {
			String id = selectRS.getString("id");
			String distance = selectRS.getString("distance");
			if (distance.contains("k")) {
				// System.out.println("KM : " + distance);
				distance = distance.replace("km", "");
				if (Double.parseDouble(distance) >= 5.00) {
					System.out.println(id + " : " + Double.parseDouble(distance));
					Statement deleteST = con.createStatement();
					String deleteSQL = "delete from task where id=" + id + ";";
					deleteST.executeUpdate(deleteSQL);
					deleteST.close();
				}
			} else {
				distance = distance.replace("m", "");
				// System.out.println(Double.parseDouble(distance));
				if (Double.parseDouble(distance) <= 10.00) {
					System.out.println(Double.parseDouble(distance));
					System.out.println(id + " : need to delete");
					Statement deleteST = con.createStatement();
					String deleteSQL = "delete from task where id=" + id + ";";
					deleteST.executeUpdate(deleteSQL);
					deleteST.close();
				}
			}
		}
		selectRS.close();
		selectST.close();
		con.close();
	}

	public void fixEndNote() throws Exception {
		Class.forName("org.postgresql.Driver").newInstance();
		String url = "jdbc:postgresql://140.119.19.33:5432/project";
		Connection con = DriverManager.getConnection(url, "postgres", "093622"); // 帳號密碼
		Statement selectST = con.createStatement();

		for (int id = 75; id <= 499; id++) {
			String selectSQL = "update house set endNote='' where id=" + id + ";";
			System.out.println(selectSQL);
			selectST.executeUpdate(selectSQL);
		}
	}
}
