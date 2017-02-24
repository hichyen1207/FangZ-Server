package com.server.project.task.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import com.server.project.api.TaskInfomation;
import com.server.project.task.createtask.TaskCreator;

public class HouseToTask {
	public static void main(String[] args) {
		HouseToTask htu = new HouseToTask();
		htu.toTask();

		// boolean reVal = htu.checkTask(2);
		// System.out.println(reVal);
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
				System.out.println("start from id=" + checkRS.getString("id") + 1);

				int insertId = Integer.valueOf(checkRS.getString("id")) + 1;
				boolean ifCreateNewTask = checkTask(insertId);
				if (ifCreateNewTask) {
					String selectSQL = "select * from house where id=" + insertId;
					ResultSet selectRS = selectST.executeQuery(selectSQL);
					while (selectRS.next()) {
						// task time classify :
						// 0-> morning; 1-> afternoon; 2-> night; 3->
						// midnight;
						List<TaskInfomation> taskInfoList = taskCreator.createTask(selectRS.getString("address"));
						for (int i = 0; i <= 3; i++) {
							int countRoad = 0;
							for (TaskInfomation taskInfo : taskInfoList) {
								String startGeometry = "ST_GeomFromText('POINT(" + taskInfo.getStartLng() + " "
										+ taskInfo.getStartLat() + ")', 4326)";
								String endGeometry = "ST_GeomFromText('POINT(" + taskInfo.getEndLng() + " "
										+ taskInfo.getEndLat() + ")', 4326)";
								String taskTime;
								if (i == 0) {
									System.out.println("start insert morning task");
									taskTime = "morning";
								} else if (i == 1) {
									System.out.println("start insert afternoon task");
									taskTime = "afternoon";
								} else if (i == 2) {
									System.out.println("start insert night task");
									taskTime = "night";
								} else {
									System.out.println("start insert midnight task");
									taskTime = "midnight";
								}

								String title = selectRS.getNString("address");
								if (taskInfo.getStartAddress().contains("巷")) {
									int toLaneIndex = taskInfo.getStartAddress().indexOf("巷");
									String lane = taskInfo.getStartAddress().substring(0, toLaneIndex + 1);
									title = lane + "_" + taskTime;
								} else {
									countRoad += 1;
									title = selectRS.getString("address") + "(" + countRoad + ")_" + taskTime;
								}

								String insertSQL = "insert into task (title, address, start_geometry, end_geometry, time, distance, duration, start_address, end_address) values ('"
										+ title + "', '" + selectRS.getString("address") + "', " + startGeometry + ", "
										+ endGeometry + ", '" + taskTime + "', '" + taskInfo.getDistance() + "', '"
										+ taskInfo.getDuration() + "', '" + taskInfo.getStartAddress() + "', '"
										+ taskInfo.getEndAddress() + "');";
								System.out.println(insertSQL);

								updateST.executeUpdate(insertSQL);
							}
						}
					}
					String updateStartSQL = "update house set endNote='' where id=" + checkRS.getString("id") + ";";
					String updateEndSQL = "update house set endNote='here' where id=" + insertId + ";";
					System.out.println("finish create task");
					selectST.executeQuery(updateStartSQL + updateEndSQL);
				} else {
					String updateStartSQL = "update house set endNote='' where id=" + checkRS.getString("id") + ";";
					String updateEndSQL = "update house set endNote='here' where id=" + insertId + ";";
					System.out.println("no need to crete task");
					selectST.executeQuery(updateStartSQL + updateEndSQL);
				}
			} else {
				System.out.println("start from beginning");
				String selectSQL = "select * from house where id=1;";
				ResultSet selectRS = selectST.executeQuery(selectSQL);
				while (selectRS.next()) {
					// task time classify :
					// 0-> morning; 1-> afternoon; 2-> night; 3->
					// midnight;
					List<TaskInfomation> taskInfoList = taskCreator.createTask(selectRS.getString("address"));
					for (int i = 0; i <= 3; i++) {
						System.out.println("start insert task to database");
						int countRoad = 0;
						for (TaskInfomation taskInfo : taskInfoList) {
							String startGeometry = "ST_GeomFromText('POINT(" + taskInfo.getStartLng() + " "
									+ taskInfo.getStartLat() + ")', 4326)";
							String endGeometry = "ST_GeomFromText('POINT(" + taskInfo.getEndLng() + " "
									+ taskInfo.getEndLat() + ")', 4326)";
							String taskTime;
							if (i == 0) {
								System.out.println("start insert morning task");
								taskTime = "morning";
							} else if (i == 1) {
								System.out.println("start insert afternoon task");
								taskTime = "afternoon";
							} else if (i == 2) {
								System.out.println("start insert night task");
								taskTime = "night";
							} else {
								System.out.println("start insert midnight task");
								taskTime = "midnight";
							}

							String title = selectRS.getString("address");
							if (taskInfo.getStartAddress().contains("巷")) {
								int toLaneIndex = taskInfo.getStartAddress().indexOf("巷");
								String lane = taskInfo.getStartAddress().substring(0, toLaneIndex + 1);
								title = lane + "_" + taskTime;
							} else {
								countRoad += 1;
								title = selectRS.getString("address") + "(" + countRoad + ")_" + taskTime;
							}

							String insertSQL = "insert into task (title, address, start_geometry, end_geometry, time, distance, duration, start_address, end_address) values ('"
									+ title + "', '" + selectRS.getString("address") + "', " + startGeometry + ", "
									+ endGeometry + ", '" + taskTime + "', '" + taskInfo.getDistance() + "', '"
									+ taskInfo.getDuration() + "', '" + taskInfo.getStartAddress() + "', '"
									+ taskInfo.getEndAddress() + "');";

							updateST.executeUpdate(insertSQL);
							System.out.println("finish insert 1");
						}
					}
				}

				String updateEndSQL = "update house set endNote='here' where id=1;";
				System.out.println("finish create task");
				selectST.executeQuery(updateEndSQL);
			}

			selectST.close();
			updateST.close();
			con.close();
		} catch (Exception e) {
			e.getMessage();
		}
	}

	public boolean checkTask(int id) {
		Boolean reVal = null;
		List<String> addressList = new ArrayList<>();
		String currentAddress = null;
		try {
			Class.forName("org.postgresql.Driver").newInstance();
			String url = "jdbc:postgresql://140.119.19.33:5432/project";
			Connection con = DriverManager.getConnection(url, "postgres", "093622"); // 帳號密碼
			Statement selectST = con.createStatement();

			// get address list
			for (int i = 1; i < id; i++) {
				String getAddressSQL = "select address from house where id=" + i + ";";
				ResultSet selectRS = selectST.executeQuery(getAddressSQL);
				while (selectRS.next()) {
					addressList.add(selectRS.getString("address"));
				}
			}

			// get current address
			String getAddressSQL = "select address from house where id=" + id + ";";
			ResultSet selectRS = selectST.executeQuery(getAddressSQL);
			while (selectRS.next()) {
				currentAddress = selectRS.getString("address");
			}

			// compare addres
			for (String address : addressList) {
				if (currentAddress.equals(address)) {
					reVal = false;
				} else {
					reVal = true;
				}
			}
		} catch (Exception e) {
			e.getMessage();
		}
		return reVal;
	}
}
