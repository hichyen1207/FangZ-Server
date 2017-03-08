package com.server.project.response;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import com.google.gson.Gson;
import com.server.project.api.Point;
import com.server.project.api.Task;

public class TaskResponcer {
	public static void main(String[] args) throws Exception {
		Gson gson = new Gson();
		TaskResponcer tr = new TaskResponcer();

		// task list
		// List<Task> list = tr.getTaskList("新北市三重區重新路三段", "8");
		// System.out.println(gson.toJson(list));

		// task
		Task retask = tr.getTask(1);
		System.out.println(gson.toJson(retask));
	}

	// public List<Task> getTaskList(String address, String requestTime) throws
	// SQLException, ClassNotFoundException {
	// Task task = new Task();
	// List<Task> taskList = new ArrayList<>();
	// Connection c = null;
	// // connect to DB
	// Class.forName("org.postgresql.Driver");
	// c =
	// DriverManager.getConnection("jdbc:postgresql://140.119.19.33:5432/project",
	// "postgres", "093622");
	// // determine morning, afternoon,...
	// int reqTimeValue = Integer.valueOf(requestTime);
	// String reqTime = reqTimeToText(reqTimeValue);
	// // execute sql query from specific view
	// String getTasks = "SELECT * FROM task_" + reqTime + " where address='" +
	// address + "';";
	// Statement stmt = c.createStatement();
	// ResultSet rs = stmt.executeQuery(getTasks);
	// // form a task with id, title, ... & form a task list
	// while (rs.next()) {
	// String id = rs.getString("id");
	// String title = rs.getString("title");
	// String start_geometry = rs.getString(4);
	// String end_geometry = rs.getString(5);
	//
	// task.setId(id);
	// task.setTitle(title);
	// task.setAddress(address);
	// task.setStart_geometry(start_geometry);
	// task.setEnd_geometry(end_geometry);
	// taskList.add(task);
	// }
	// stmt.close();
	// rs.close();
	// c.close();
	//
	// return taskList;
	// }

	public Task getTask(int id) throws Exception {
		Task task = new Task();
		// connect DB
		Class.forName("org.postgresql.Driver").newInstance();
		String url = "jdbc:postgresql://140.119.19.33:5432/project";
		Connection con = DriverManager.getConnection(url, "postgres", "093622"); // 帳號密碼
		Statement selectST = con.createStatement();
		Statement getPointST = con.createStatement();

		String selectSQL = "select * from task where id=" + id + ";";
		ResultSet selectRS = selectST.executeQuery(selectSQL);
		while (selectRS.next()) {
			task.setId(String.valueOf(id));
			task.setTitle(selectRS.getString("title"));
			task.setAddress(selectRS.getString("address"));
			task.setTime(selectRS.getString("time"));
			task.setDistance(selectRS.getString("distance"));
			task.setDuration(selectRS.getString("duration"));

			String getStartGeoSQL = "select ST_AsText(start_geometry) from task where id=" + id + ";";
			ResultSet getStartRS = getPointST.executeQuery(getStartGeoSQL);
			while (getStartRS.next()) {
				String startPoint = getStartRS.getString("st_astext");
				int startIndex = startPoint.indexOf("(");
				int midIndex = startPoint.indexOf(" ");
				int endIndex = startPoint.indexOf(")");
				double lng = Double.valueOf(startPoint.substring(startIndex + 1, midIndex));
				double lat = Double.valueOf(startPoint.substring(midIndex + 1, endIndex));
				Point point = new Point();
				point.setLat(lat);
				point.setLng(lng);
				task.setStart_point(point);
			}

			String getEndGeoSQL = "select ST_AsText(end_geometry) from task where id=" + id + ";";
			ResultSet getEndRS = getPointST.executeQuery(getEndGeoSQL);
			while (getEndRS.next()) {
				String endPoint = getEndRS.getString("st_astext");
				int startIndex = endPoint.indexOf("(");
				int midIndex = endPoint.indexOf(" ");
				int endIndex = endPoint.indexOf(")");
				double lng = Double.valueOf(endPoint.substring(startIndex + 1, midIndex));
				double lat = Double.valueOf(endPoint.substring(midIndex + 1, endIndex));
				Point point = new Point();
				point.setLat(lat);
				point.setLng(lng);
				task.setEnd_point(point);
			}
			getEndRS.close();
			getStartRS.close();
		}
		getPointST.close();
		selectRS.close();
		selectST.close();
		con.close();

		return task;
	}

	// public String reqTimeToText(int reqTimeValue) {
	// if (reqTimeValue >= 6 && reqTimeValue < 12) {
	// return "morning";
	// } else if (reqTimeValue >= 12 && reqTimeValue < 18) {
	// return "afternoon";
	// } else if (reqTimeValue >= 18 && reqTimeValue < 24) {
	// return "night";
	// } else if (reqTimeValue >= 0 && reqTimeValue < 6) {
	// return "midnight";
	// } else {
	// return null;
	// }
	// }
}