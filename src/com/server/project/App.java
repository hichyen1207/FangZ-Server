package com.server.project;

import static spark.Spark.*;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;
import com.server.project.api.House;
import com.server.project.api.Task;
import com.server.project.api.Video;
import com.server.project.response.HouseResponcer;
import com.server.project.response.LocationResponcer;
import com.server.project.response.TaskResponcer;
import com.server.project.response.VideoResponcer;

/**
 * Hello world!
 *
 */
public class App {
	public static void main(String[] args) {
		Gson gson = new Gson();
		LocationResponcer locationListResponcer = new LocationResponcer();
		VideoResponcer videoResponcer = new VideoResponcer();
		TaskResponcer getTask = new TaskResponcer();
		HouseResponcer houseResponcer = new HouseResponcer();

		// set port
		exception(Exception.class, (e, req, res) -> e.printStackTrace());
		staticFiles.location("/");
		port(9999);

		// task location list
		get("/taskLocationList", (req, res) -> {
			List<com.server.project.api.Road> taskLocationList = null;
			try {
				taskLocationList = locationListResponcer.getTaskLocationList();
			} catch (Exception e) {
				e.getMessage();
			}
			return taskLocationList;
		}, gson::toJson);

		// task list
		get("/taskList/:address/:time", (req, res) -> {
			String address;
			String time;
			List<Task> taskList = null;
			try {
				address = req.params("address");
				time = req.params("time");
				taskList = getTask.getTaskList(address, time);
			} catch (Exception e2) {
				e2.getMessage();
			}
			return taskList;
		}, gson::toJson);

		// task
		get("/task/:id", (req, res) -> {
			String id;
			Task task = null;
			try {
				id = req.params("id");
				task = getTask.getTask(Integer.valueOf(id));
			} catch (Exception e) {
				e.getMessage();
			}
			return task;
		}, gson::toJson);

		// video address list
		get("/videoRoadList", (req, res) -> {
			List<com.server.project.api.Road> videoLocationList = null;
			try {
				videoLocationList = locationListResponcer.getRoadList();
			} catch (Exception e) {
				e.getMessage();
			}
			return videoLocationList;
		}, gson::toJson);

		// house list
		get("/houseList/:address", (req, res) -> {
			String address;
			List<com.server.project.api.House> houseList = null;
			try {
				address = req.params("address");
				houseList = houseResponcer.getHouseList(address);
			} catch (Exception e) {
				e.getMessage();
			}
			return houseList;
		}, gson::toJson);

		// video list
		get("/videoList/:address", (req, res) -> {
			String address;
			List<Video> videoList = new ArrayList<>();
			try {
				address = req.params("address");
				videoList = videoResponcer.getVideoList(address);
			} catch (Exception e) {
				e.getMessage();
			}
			return videoList;
		}, gson::toJson);

		// video
		get("/video/:id", (req, res) -> {
			String id;
			Video video = null;
			try {
				id = req.params("id");
				video = videoResponcer.getVideo(Integer.parseInt(id));
			} catch (Exception e) {
				e.getMessage();
			}
			return video;
		}, gson::toJson);

		// house
		get("/house/:id", (req, res) -> {
			String id;
			House house = null;
			try {
				id = req.params("id");
				house = houseResponcer.getHouse(Integer.valueOf(id));
			} catch (Exception e) {
				e.getMessage();
			}
			return house;
		}, gson::toJson);
	}
}
