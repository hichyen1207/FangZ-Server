package com.server.project.response;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.server.project.api.Task;

public class TaskResponcer {
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		Gson gson = new Gson();
		TaskResponcer tr = new TaskResponcer();

		// task list
		List<Task> list = tr.getTaskList("0101000020E6100000A60A4625755E5E408BFD65F7E4013940", "8");
		System.out.println(gson.toJson(list));

		// task
		Task retask = tr.getTask(3741);
		System.out.println(gson.toJson(retask));
	}

	public List<Task> getTaskList(String location, String requestTime) throws SQLException, ClassNotFoundException {
		Task task = new Task();
		List<Task> taskList = new ArrayList<>();
		Connection c = null;
		// connect to DB
		Class.forName("org.postgresql.Driver");
		c = DriverManager.getConnection("jdbc:postgresql://140.119.19.33:5432/project", "postgres", "093622");
		// determine morning, afternoon,...
		int reqTimeValue = Integer.valueOf(requestTime);
		String reqTime = reqTimeToText(reqTimeValue);
		// execute sql query from specific view
		String getTasks = "SELECT id, title, ST_AsText(location), ST_AsText(start_geometry), ST_AsText(end_geometry), time FROM task_"
				+ reqTime + " where location='" + location + "';";
		Statement stmt = c.createStatement();
		ResultSet rs = stmt.executeQuery(getTasks);
		// form a task with id, title, ... & form a task list
		while (rs.next()) {
			String id = rs.getString("id");
			String title = rs.getString("title");
			String houselocation = rs.getString(3);
			String start_geometry = rs.getString(4);
			String end_geometry = rs.getString(5);

			task.setId(id);
			task.setTitle(title);
			task.setLocation(houselocation);
			task.setStart_geometry(start_geometry);
			task.setEnd_geometry(end_geometry);
			taskList.add(task);
		}
		return taskList;
	}

	public Task getTask(int id) {
		Task task = new Task();
		try {
			// connect DB
			Class.forName("org.postgresql.Driver").newInstance();
			String url = "jdbc:postgresql://140.119.19.33:5432/project";
			Connection con = DriverManager.getConnection(url, "postgres", "093622"); // 帳號密碼
			Statement selectST = con.createStatement();

			String selectSQL = "select * from task where id=" + id;
			ResultSet selectRS = selectST.executeQuery(selectSQL);
			while (selectRS.next()) {
				task.setId(String.valueOf(id));
				task.setTitle(selectRS.getString("title"));
				task.setLocation(selectRS.getString("location"));
				task.setStart_geometry(selectRS.getString("start_geometry"));
				task.setEnd_geometry(selectRS.getString("end_geometry"));
				task.setTime(selectRS.getString("time"));
			}
		} catch (Exception e) {
			e.getMessage();
		}
		return task;
	}

	public String reqTimeToText(int reqTimeValue) {
		if (reqTimeValue >= 6 && reqTimeValue < 12) {
			return "morning";
		} else if (reqTimeValue >= 12 && reqTimeValue < 18) {
			return "afternoon";
		} else if (reqTimeValue >= 18 && reqTimeValue < 24) {
			return "night";
		} else if (reqTimeValue >= 0 && reqTimeValue < 6) {
			return "midnight";
		} else {
			return null;
		}
	}
}