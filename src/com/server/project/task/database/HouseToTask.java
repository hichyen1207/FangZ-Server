package com.server.project.task.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import com.server.project.api.TaskInfomation;
import com.server.project.task.createtask.TaskCreator;

public class HouseToTask {
	public static void main(String[] args) {
		HouseToTask htu = new HouseToTask();
		htu.toTask();
	}

	public void toTask() {
		try {
			// connect DB
			Class.forName("org.postgresql.Driver").newInstance();
			String url = "jdbc:postgresql://140.119.19.33:5432/project";
			Connection con = DriverManager.getConnection(url, "postgres", "093622"); // 帳號密碼
			Statement selectST = con.createStatement();
			Statement updateST = con.createStatement();

			String checkSQL = "select * from house where endNote='here';";
			ResultSet checkRS = selectST.executeQuery(checkSQL);
			TaskCreator taskCreator = new TaskCreator();
			if (checkRS.next()) {
				int startId = Integer.valueOf(checkRS.getString("id")) + 1;
				String countSQL = "SELECT COUNT('id') FROM house;";
				ResultSet countRS = selectST.executeQuery(countSQL);
				while (countRS.next()) {
					int endId = Integer.valueOf(countRS.getInt(1));
					for (int j = startId; j <= endId; j++) {
						String selectSQL = "select * from house where id=" + j;
						ResultSet selectRS = selectST.executeQuery(selectSQL);
						while (selectRS.next()) {
							// task time classify :
							// 0-> morning; 1-> afternoon; 2-> night; 3->
							// midnight;
							for (int i = 0; i <= 3; i++) {
								List<TaskInfomation> taskInfoList = taskCreator
										.createTask(selectRS.getString("address"));

								for (TaskInfomation taskInfo : taskInfoList) {
									String startGeometry = "ST_GeomFromText('POINT(" + taskInfo.getStartLng() + " "
											+ taskInfo.getStartLat() + ")', 4326)";
									String endGeometry = "ST_GeomFromText('POINT(" + taskInfo.getEndLng() + " "
											+ taskInfo.getEndLat() + ")', 4326)";
									String taskTime;
									if (i == 0) {
										taskTime = "morning";
									} else if (i == 1) {
										taskTime = "afternoon";
									} else if (i == 2) {
										taskTime = "night";
									} else {
										taskTime = "midnight";
									}
									String insertSQL = "insert into task (title, address, start_geometry, end_geometry, time, distance, duration) values ('"
											+ selectRS.getString("address") + "_" + taskTime + "', '"
											+ selectRS.getString("address") + "', " + startGeometry + ", " + endGeometry
											+ ", '" + taskTime + "', '" + taskInfo.getDistance() + "', '"
											+ taskInfo.getDuration() + "');";
									System.out.println(insertSQL);

									updateST.executeUpdate(insertSQL);
								}
							}
						}
					}
					int updateId = startId - 1;
					String updateStartSQL = "update house set endNote='' where id=" + updateId + ";";
					String updateEndSQL = "update house set endNote='here' where id=" + endId + ";";
					System.out.println("finish create task");
					selectST.executeQuery(updateStartSQL + updateEndSQL);
				}
			} else {
				String countSQL = "SELECT COUNT('id') FROM house;";
				ResultSet countRS = selectST.executeQuery(countSQL);
				while (countRS.next()) {
					int endId = Integer.valueOf(countRS.getInt(1));
					for (int k = 1; k <= endId; k++) {
						String selectSQL = "select * from house where id=" + k + ";";
						ResultSet selectRS = selectST.executeQuery(selectSQL);
						while (selectRS.next()) {
							// task time classify :
							// 0-> morning; 1-> afternoon; 2-> night; 3->
							// midnight;
							for (int i = 0; i <= 3; i++) {
								List<TaskInfomation> taskInfoList = taskCreator
										.createTask(selectRS.getString("address"));

								for (TaskInfomation taskInfo : taskInfoList) {
									String startGeometry = "ST_GeomFromText('POINT(" + taskInfo.getStartLng() + " "
											+ taskInfo.getStartLat() + ")', 4326)";
									String endGeometry = "ST_GeomFromText('POINT(" + taskInfo.getEndLng() + " "
											+ taskInfo.getEndLat() + ")', 4326)";
									String taskTime;
									if (i == 0) {
										taskTime = "morning";
									} else if (i == 1) {
										taskTime = "afternoon";
									} else if (i == 2) {
										taskTime = "night";
									} else {
										taskTime = "midnight";
									}
									String insertSQL = "insert into task (title, address, start_geometry, end_geometry, time, distance, duration) values ('"
											+ selectRS.getString("address") + "_" + taskTime + "', '"
											+ selectRS.getString("address") + "', " + startGeometry + ", " + endGeometry
											+ ", '" + taskTime + "', '" + taskInfo.getDistance() + "', '"
											+ taskInfo.getDuration() + "');";
									System.out.println(insertSQL);

									updateST.executeUpdate(insertSQL);
								}
							}
						}
					}
					String updateEndSQL = "update house set endNote='here' where id=" + endId + ";";
					System.out.println("finish create task");
					selectST.executeQuery(updateEndSQL);
				}
			}

			selectST.close();
			updateST.close();
			con.close();
		} catch (Exception e) {
			e.getMessage();
		}
	}
}
