package com.server.project.task.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import com.server.project.api.PathPoint;
import com.server.project.api.Point;
import com.server.project.task.createtask.RoadClassifier;
import com.server.project.tool.GeometryToPoint;

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

			String checkSQL = "select * from house where endNote='here'";
			ResultSet checkRS = selectST.executeQuery(checkSQL);
			GeometryToPoint pointToDouble = new GeometryToPoint();
			RoadClassifier roadClassifier = new RoadClassifier();
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
								Point coordinate = pointToDouble.toPoint(selectRS.getString("location"));
								List<PathPoint> pathList = roadClassifier.classifyRoad(coordinate.getLat(),
										coordinate.getLng());

								for (PathPoint path : pathList) {
									String startGeometry = "ST_GeomFromText('POINT(" + path.getStartLng() + " "
											+ path.getStartLat() + ")', 4326)";
									String endGeometry = "ST_GeomFromText('POINT(" + path.getEndLng() + " "
											+ path.getEndLat() + ")', 4326)";
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
									String insertSQL = "insert into task (title, location, start_geometry, end_geometry, time) values ('"
											+ selectRS.getString("title") + "_" + taskTime + "', '"
											+ selectRS.getString("location") + "', " + startGeometry + ", "
											+ endGeometry + ", '" + taskTime + "');";

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
						String selectSQL = "select * from house where id=" + k;
						ResultSet selectRS = selectST.executeQuery(selectSQL);
						while (selectRS.next()) {
							// task time classify :
							// 0-> morning; 1-> afternoon; 2-> night; 3->
							// midnight;
							for (int i = 0; i <= 3; i++) {
								Point coordinate = pointToDouble.toPoint(selectRS.getString("location"));
								List<PathPoint> pathList = roadClassifier.classifyRoad(coordinate.getLat(),
										coordinate.getLng());

								for (PathPoint path : pathList) {
									String startGeometry = "ST_GeomFromText('POINT(" + path.getStartLng() + " "
											+ path.getStartLat() + ")', 4326)";
									String endGeometry = "ST_GeomFromText('POINT(" + path.getEndLng() + " "
											+ path.getEndLat() + ")', 4326)";
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
									String insertSQL = "insert into task (title, location, start_geometry, end_geometry, time) values ('"
											+ selectRS.getString("title") + "_" + taskTime + "', '"
											+ selectRS.getString("location") + "', " + startGeometry + ", "
											+ endGeometry + ", '" + taskTime + "');";

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
