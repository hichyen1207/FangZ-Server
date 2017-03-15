package com.server.project.response;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.server.project.api.Point;
import com.server.project.api.Video;
import com.server.project.tool.GeometryToPoint;

public class VideoResponcer {
	public static void main(String[] args) throws Exception {
		Gson gson = new Gson();
		VideoResponcer vg = new VideoResponcer();

		// video list
		List<Video> videoList = vg.getVideoList("台北市文山區秀明路二段");
		System.out.println(gson.toJson(videoList));

		// video
		// Video result = vg.getVideo(3738);
		// System.out.println(gson.toJson(result));
	}

	public Video getVideo(int id) throws Exception {
		Video video = new Video();
		// connect DB
		Class.forName("org.postgresql.Driver").newInstance();
		String url = "jdbc:postgresql://140.119.19.33:5432/project";
		Connection con = DriverManager.getConnection(url, "postgres", "093622"); // 帳號密碼
		Statement selectST = con.createStatement();

		String selectSQL = "select * from video where id=" + id;
		ResultSet selectRS = selectST.executeQuery(selectSQL);
		while (selectRS.next()) {
			GeometryToPoint toPoint = new GeometryToPoint();
			video.setId(id);
			video.setYoutube_id(selectRS.getString("youtube_id"));
			video.setTitle(selectRS.getString("title"));
			Point start_geometryInPoint = toPoint.toPoint(selectRS.getString("start_geometry"));
			double[] start_geometry = { start_geometryInPoint.getLat(), start_geometryInPoint.getLng() };
			video.setStart_geometry(start_geometry);
			Point end_geometryInPoint = toPoint.toPoint(selectRS.getString("end_geometry"));
			double[] end_geometry = { end_geometryInPoint.getLat(), end_geometryInPoint.getLng() };
			video.setEnd_geometry(end_geometry);
			video.setAddress(selectRS.getString("address"));
			video.setTime(selectRS.getString("time"));
			video.setShop(selectRS.getString("shop"));
			video.setWeather(selectRS.getString("weather"));
			video.setFacility(selectRS.getString("facility"));
		}
		selectRS.close();
		selectST.close();
		con.close();

		return video;
	}

	public List<Video> getVideoList(String address) throws Exception {
		List<Video> vidoeList = new ArrayList<>();
		// connect DB
		Class.forName("org.postgresql.Driver").newInstance();
		String url = "jdbc:postgresql://140.119.19.33:5432/project";
		Connection con = DriverManager.getConnection(url, "postgres", "093622"); // 帳號密碼
		Statement selectST = con.createStatement();

		String selectSQL = "select * from video where address='" + address + "';";
		ResultSet selectRS = selectST.executeQuery(selectSQL);
		while (selectRS.next()) {
			Video video = new Video();
			video.setId(selectRS.getInt("id"));
			video.setTitle(selectRS.getString("title"));
			video.setTime(selectRS.getString("time"));
			vidoeList.add(video);
		}
		selectRS.close();
		selectST.close();
		con.close();

		return vidoeList;
	}
}
