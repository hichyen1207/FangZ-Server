package com.server.project.task.createtask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.google.gson.Gson;
import com.server.project.api.TaskInfomation;
import com.server.project.tool.GoogleMapApiKey;

public class TaskCreator {
	public static void main(String[] args) throws IOException, InterruptedException {
		Gson gson = new Gson();
		TaskCreator tc = new TaskCreator();
		List<TaskInfomation> list = tc.createTask("台北市文山區星光路一段");
		System.out.println(gson.toJson(list));
	}

	public List<TaskInfomation> createTask(String address) throws IOException, InterruptedException {
		List<TaskInfomation> taskInfoList = new ArrayList<>();
		String startAddress = null;
		String endAddress = null;
		for (int j = 1; j < 10; j++) {
			URL url = new URL(
					"http://maps.googleapis.com/maps/api/geocode/json?address=" + address + j + "號&sensor=false");
			URLConnection conn = url.openConnection();
			conn.setRequestProperty("user-agent", "Chrome/7.0.517.44");

			InputStream in = conn.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(in, "utf-8"));

			// parse the HTML
			String retVal = "";
			String line = null;
			while ((line = br.readLine()) != null) {
				retVal = retVal + line + "\n";
			}
			Document doc = Jsoup.parse(retVal);

			// find geometry coordinate
			String text = doc.text();
			int index = text.indexOf("formatted_address");
			String checkVal = text.substring(index + 26, index + 27);
			if (checkVal.equals(String.valueOf(j))) {
				startAddress = address + j + "號";
				break;
			} else if (checkVal.equals("You")) {
				j = j - 1;
			}
			TimeUnit.MILLISECONDS.sleep(100);
		}

		for (int i = 400; i > 0; i--) {
			// connect to google map api
			URL url = new URL(
					"http://maps.googleapis.com/maps/api/geocode/json?address=" + address + i + "號&sensor=false");
			URLConnection conn = url.openConnection();
			conn.setRequestProperty("user-agent", "Chrome/7.0.517.44");

			InputStream in = conn.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(in, "utf-8"));

			// parse the HTML
			String retVal = "";
			String line = null;
			while ((line = br.readLine()) != null) {
				retVal = retVal + line + "\n";
			}
			Document doc = Jsoup.parse(retVal);

			// find geometry coordinate
			String text = doc.text();
			int index = text.indexOf("formatted_address");
			String checkVal = text.substring(index + 22, index + 25);
			if (checkVal.equals("No.")) {
				endAddress = address + i + "號";
				break;
			} else if (checkVal.equals("You")) {
				i = i + 1;
			}
			TimeUnit.MILLISECONDS.sleep(100);
		}

		String apiURL = "https://maps.googleapis.com/maps/api/directions/json?origin=" + startAddress + "&destination="
				+ endAddress + "&mode=walking&key=" + GoogleMapApiKey.getKey();
		URL url = new URL(apiURL);
		URLConnection conn = url.openConnection();
		conn.setRequestProperty("user-agent", "Chrome/7.0.517.44");

		InputStream in = conn.getInputStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(in, "utf-8"));

		// parse the HTML
		String retVal = "";
		String line = null;
		while ((line = br.readLine()) != null) {
			retVal = retVal + line + "\n";
		}
		Document doc = Jsoup.parse(retVal);

		String text = doc.text();
		TaskInfomation taskInfo = new TaskInfomation();
		Gson gson = new Gson();

		// get start geometry
		int geoIndex = text.indexOf("start_location");
		int setStartIndex = text.indexOf("{", geoIndex);
		int setEndIndex = text.indexOf("}", geoIndex) + 1;
		String geometry = text.substring(setStartIndex, setEndIndex);
		geometry = geometry.replace("lat", "startLat").replace("lng", "startLng");

		TaskInfomation startCoordinate = gson.fromJson(geometry, TaskInfomation.class);
		taskInfo.setStartLat(startCoordinate.getStartLat());
		taskInfo.setStartLng(startCoordinate.getStartLng());

		// get end geometry
		geoIndex = text.indexOf("end_location");
		setStartIndex = text.indexOf("{", geoIndex);
		setEndIndex = text.indexOf("}", geoIndex) + 1;
		geometry = text.substring(setStartIndex, setEndIndex);
		geometry = geometry.replace("lat", "endLat").replace("lng", "endLng");

		TaskInfomation endCoordinate = gson.fromJson(geometry, TaskInfomation.class);
		taskInfo.setEndLat(endCoordinate.getEndLat());
		taskInfo.setEndLng(endCoordinate.getEndLng());

		// get distance
		int disStartIndex = text.indexOf("distance");
		disStartIndex = text.indexOf("text", disStartIndex);
		int disEndIndex = text.indexOf(",", disStartIndex);
		String distance = text.substring(disStartIndex + 9, disEndIndex - 1);
		taskInfo.setDistance(distance);

		// get duration
		int durStartIndex = text.indexOf("duration");
		durStartIndex = text.indexOf("text", durStartIndex);
		int durEndIndex = text.indexOf(",", durStartIndex);
		String duration = text.substring(durStartIndex + 9, durEndIndex - 1);
		taskInfo.setDuration(duration);

		taskInfoList.add(taskInfo);

		return taskInfoList;
	}
}
