package com.server.project.notuse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.google.gson.Gson;
import com.server.project.api.Point;

public class NearPlaceFinder {
	public static void main(String[] args) throws IOException {
		NearPlaceFinder pf = new NearPlaceFinder();
		String place = "711";
		List<Point> nearPlaceList = pf.findNearPlace(24.9873313, 121.5761281, place);

		System.out.println("near " + place);
		for (Point coordinate : nearPlaceList) {
			System.out.println("lat : " + coordinate.getLat() + ", lng : " + coordinate.getLng());
		}
	}

	public List<Point> findNearPlace(Double lat, Double lng, String place) throws IOException {
		// connect to google map api
		String placeURL = "https://maps.googleapis.com/maps/api/place/radarsearch/json?location=" + lat + "," + lng
				+ "&radius=250&keyword=" + URLEncoder.encode(place, "UTF-8") + "&key=" + GoogleMapApiKey.getKey();
		URL url = new URL(placeURL);
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
		List<Point> nearPlaceList = new ArrayList<>();
		int count = 0;

		// get place coordinate add into nearPlace list
		while (count >= 0) {
			int geoIndex = text.indexOf("location", count);
			int setStartIndex = text.indexOf("{", geoIndex);
			int setEndIndex = text.indexOf("}", geoIndex) + 1;
			String geometry = text.substring(setStartIndex, setEndIndex);
			Gson gson = new Gson();
			Point coordinate = gson.fromJson(geometry, Point.class);
			nearPlaceList.add(coordinate);
			count = geoIndex + 1;
			geoIndex = text.indexOf("location", count);
			count = geoIndex;
		}

		return nearPlaceList;
	}
}
