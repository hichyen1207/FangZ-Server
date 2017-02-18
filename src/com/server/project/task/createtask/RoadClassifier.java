package com.server.project.task.createtask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.google.gson.Gson;
import com.server.project.api.PathPoint;
import com.server.project.tool.GoogleMapApiKey;

public class RoadClassifier {
	public static void main(String[] args) throws IOException {
		RoadClassifier rc = new RoadClassifier();

		double lat = 24.9873313;
		double lng = 121.5761281;
		List<PathPoint> pathList = rc.classifyRoad(lat, lng);
		Gson gson = new Gson();

		for (PathPoint path : pathList) {
			String task = gson.toJson(path);
			System.out.println(task);

			// System.out.println(path.getStartLat() + "," + path.getStartLng()
			// + "/" + path.getEndLat() + "," + path.getEndLng() + "@");
		}
	}

	public List<PathPoint> classifyRoad(double lat, double lng) throws IOException {
		List<PathPoint> pathList = new ArrayList<>();

		for (int i = 0; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				// connect to google map api
				// 0.001 lat/lng = 110 m
				lat = lat + 0.001 * j;
				lng = lng + 0.001 * i;
				double distanceLat = lat + 0.001 * j;
				double distanceLng = lng + 0.001 * (i + 1);
				String apiURL = "https://maps.googleapis.com/maps/api/directions/json?origin=" + lat + "," + lng
						+ "&destination=" + distanceLat + "," + distanceLng + "&mode=walking&key="
						+ GoogleMapApiKey.getKey();				
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
				int geoIndex = text.indexOf("start_location");
				int setStartIndex = text.indexOf("{", geoIndex);
				int setEndIndex = text.indexOf("}", geoIndex) + 1;
				String geometry = text.substring(setStartIndex, setEndIndex);
				geometry = geometry.replace("lat", "startLat").replace("lng", "startLng");

				PathPoint coordinate = new PathPoint();
				Gson gson = new Gson();
				PathPoint startCoordinate = gson.fromJson(geometry, PathPoint.class);
				coordinate.setStartLat(startCoordinate.getStartLat());
				coordinate.setStartLng(startCoordinate.getStartLng());

				geoIndex = text.indexOf("end_location");
				setStartIndex = text.indexOf("{", geoIndex);
				setEndIndex = text.indexOf("}", geoIndex) + 1;
				geometry = text.substring(setStartIndex, setEndIndex);
				geometry = geometry.replace("lat", "endLat").replace("lng", "endLng");

				PathPoint endCoordinate = gson.fromJson(geometry, PathPoint.class);
				coordinate.setEndLat(endCoordinate.getEndLat());
				coordinate.setEndLng(endCoordinate.getEndLng());

				pathList.add(coordinate);
			}
		}

		for (int i = 0; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				// connect to google map api
				// 0.001 lat/lng = 110 m
				lat = lat - 0.001 * j;
				lng = lng - 0.001 * i;
				double distanceLat = lat - 0.001 * j;
				double distanceLng = lng - 0.001 * (i + 1);
				String apiURL = "https://maps.googleapis.com/maps/api/directions/json?origin=" + lat + "," + lng
						+ "&destination=" + distanceLat + "," + distanceLng + "&mode=walking&key="
						+ GoogleMapApiKey.getKey();
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
				int geoIndex = text.indexOf("start_location");
				int setStartIndex = text.indexOf("{", geoIndex);
				int setEndIndex = text.indexOf("}", geoIndex) + 1;
				String geometry = text.substring(setStartIndex, setEndIndex);
				geometry = geometry.replace("lat", "startLat").replace("lng", "startLng");

				PathPoint coordinate = new PathPoint();
				Gson gson = new Gson();
				PathPoint startCoordinate = gson.fromJson(geometry, PathPoint.class);
				coordinate.setStartLat(startCoordinate.getStartLat());
				coordinate.setStartLng(startCoordinate.getStartLng());

				geoIndex = text.indexOf("end_location");
				setStartIndex = text.indexOf("{", geoIndex);
				setEndIndex = text.indexOf("}", geoIndex) + 1;
				geometry = text.substring(setStartIndex, setEndIndex);
				geometry = geometry.replace("lat", "endLat").replace("lng", "endLng");

				PathPoint endCoordinate = gson.fromJson(geometry, PathPoint.class);
				coordinate.setEndLat(endCoordinate.getEndLat());
				coordinate.setEndLng(endCoordinate.getEndLng());

				pathList.add(coordinate);
			}
		}

		for (int i = -1; i <= 1; i++) {
			for (int j = 0; j <= 1; j++) {
				// connect to google map api
				// 0.001 lat/lng = 110 m
				lat = lat + 0.001 * j;
				lng = lng + 0.001 * i;
				double distanceLat = lat + 0.001 * (j + 1);
				double distanceLng = lng + 0.001 * i;
				String apiURL = "https://maps.googleapis.com/maps/api/directions/json?origin=" + lat + "," + lng
						+ "&destination=" + distanceLat + "," + distanceLng + "&mode=walking&key="
						+ GoogleMapApiKey.getKey();
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
				int geoIndex = text.indexOf("start_location");
				int setStartIndex = text.indexOf("{", geoIndex);
				int setEndIndex = text.indexOf("}", geoIndex) + 1;
				String geometry = text.substring(setStartIndex, setEndIndex);
				geometry = geometry.replace("lat", "startLat").replace("lng", "startLng");

				PathPoint coordinate = new PathPoint();
				Gson gson = new Gson();
				PathPoint startCoordinate = gson.fromJson(geometry, PathPoint.class);
				coordinate.setStartLat(startCoordinate.getStartLat());
				coordinate.setStartLng(startCoordinate.getStartLng());

				geoIndex = text.indexOf("end_location");
				setStartIndex = text.indexOf("{", geoIndex);
				setEndIndex = text.indexOf("}", geoIndex) + 1;
				geometry = text.substring(setStartIndex, setEndIndex);
				geometry = geometry.replace("lat", "endLat").replace("lng", "endLng");

				PathPoint endCoordinate = gson.fromJson(geometry, PathPoint.class);
				coordinate.setEndLat(endCoordinate.getEndLat());
				coordinate.setEndLng(endCoordinate.getEndLng());

				pathList.add(coordinate);
			}
		}

		for (int i = -1; i <= 1; i++) {
			for (int j = 0; j <= 1; j++) {
				// connect to google map api
				// 0.001 lat/lng = 110 m
				lat = lat - 0.001 * j;
				lng = lng - 0.001 * i;
				double distanceLat = lat - 0.001 * (j + 1);
				double distanceLng = lng - 0.001 * i;
				String apiURL = "https://maps.googleapis.com/maps/api/directions/json?origin=" + lat + "," + lng
						+ "&destination=" + distanceLat + "," + distanceLng + "&mode=walking&key="
						+ GoogleMapApiKey.getKey();
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
				int geoIndex = text.indexOf("start_location");
				int setStartIndex = text.indexOf("{", geoIndex);
				int setEndIndex = text.indexOf("}", geoIndex) + 1;
				String geometry = text.substring(setStartIndex, setEndIndex);
				geometry = geometry.replace("lat", "startLat").replace("lng", "startLng");

				PathPoint coordinate = new PathPoint();
				Gson gson = new Gson();
				PathPoint startCoordinate = gson.fromJson(geometry, PathPoint.class);
				coordinate.setStartLat(startCoordinate.getStartLat());
				coordinate.setStartLng(startCoordinate.getStartLng());

				geoIndex = text.indexOf("end_location");
				setStartIndex = text.indexOf("{", geoIndex);
				setEndIndex = text.indexOf("}", geoIndex) + 1;
				geometry = text.substring(setStartIndex, setEndIndex);
				geometry = geometry.replace("lat", "endLat").replace("lng", "endLng");

				PathPoint endCoordinate = gson.fromJson(geometry, PathPoint.class);
				coordinate.setEndLat(endCoordinate.getEndLat());
				coordinate.setEndLng(endCoordinate.getEndLng());

				pathList.add(coordinate);
			}
		}

		return pathList;
	}
}
