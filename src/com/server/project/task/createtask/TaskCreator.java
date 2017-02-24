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
		List<TaskInfomation> list = tc.createTask("台北市忠孝東路一段");
		System.out.println(gson.toJson(list));

		// List<String> laneList = tc.findLane("台北市忠孝東路一段", 48, 71);
		// System.out.println(laneList);

		// List<String> laneAddress = tc.getLaneAddress("台北市忠孝東路一段49巷");
		// System.out.println(laneAddress);
	}

	public List<TaskInfomation> createTask(String address) throws IOException, InterruptedException {
		List<TaskInfomation> taskInfoList = new ArrayList<>();
		int startAddresNum = 0;
		int endAddresNum = 0;

		// find start address
		for (int j = 1; j < 10; j++) {
			URL url = new URL("https://maps.googleapis.com/maps/api/geocode/json?address=" + address + j + "號&key="
					+ GoogleMapApiKey.getKey());
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
				startAddresNum = j;
				break;
			} else if (checkVal.equals("You")) {
				j = j - 1;
			}
			TimeUnit.MILLISECONDS.sleep(100);
		}

		System.out.println("start find end address");
		// find end address
		for (int i = 500; i > 0; i--) {
			if (i % 10 == 0) {
				System.out.println("count end address No." + i);
			}
			// connect to google map api
			URL url = new URL("https://maps.googleapis.com/maps/api/geocode/json?address=" + address + i + "號&key="
					+ GoogleMapApiKey.getKey());
			URLConnection conn = url.openConnection();
			conn.setRequestProperty("user-agent", "Chrome/7.0.517.44");
			conn.setRequestProperty("Content-Language", "zh-tw");
			conn.setRequestProperty("Accept-Charset", "UTF-8");

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
			int startIndex = text.indexOf("formatted_address");
			startIndex = startIndex + 26;
			int endIndex = text.indexOf(",", startIndex);
			String checkVal = text.substring(startIndex, endIndex);
			if (checkVal.equals(String.valueOf(i))) {
				endAddresNum = i;
				break;
			} else if (checkVal.equals("You")) {
				i = i + 1;
			}
			TimeUnit.MILLISECONDS.sleep(100);
		}

		System.out.println("start cut road");
		List<String> roadList = cutRoad(address, startAddresNum, endAddresNum);
		System.out.println(roadList);
		for (int i = 0; i < roadList.size() - 1; i++) {
			TaskInfomation taskInfoByRoad = createTaskInRoad(roadList.get(i), roadList.get(i + 1));
			taskInfoList.add(taskInfoByRoad);
		}

		System.out.println("start find lane");
		List<String> laneList = findLane(address, startAddresNum, endAddresNum);
		System.out.println(laneList);
		System.out.println("start get lane task");
		for (String lane : laneList) {
			List<String> landPath = getLaneAddress(lane);
			System.out.println(landPath);
			TaskInfomation taskInfoByLane = createTaskInRoad(landPath.get(0), landPath.get(1));
			taskInfoList.add(taskInfoByLane);
		}

		Gson gson = new Gson();
		System.out.println(gson.toJson(taskInfoList));

		return taskInfoList;
	}

	private List<String> cutRoad(String address, int startAddressNum, int endAddressNum)
			throws IOException, InterruptedException {
		List<String> addressList = new ArrayList<>();

		if (endAddressNum <= 100) {
			addressList.add(address + startAddressNum + "號");
			addressList.add(address + endAddressNum + "號");
		} else if (100 < endAddressNum && endAddressNum <= 200) {
			int addressNodeNum = 0;
			addressList.add(address + startAddressNum + "號");

			// for No.100
			for (int i = 101; i < 110; i++) {
				URL url = new URL("https://maps.googleapis.com/maps/api/geocode/json?address=" + address + i + "號&key="
						+ GoogleMapApiKey.getKey());
				URLConnection conn = url.openConnection();
				conn.setRequestProperty("user-agent", "Chrome/7.0.517.44");
				InputStream in = conn.getInputStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(in, "utf-8"));

				String retVal = "";
				String line = null;
				while ((line = br.readLine()) != null) {
					retVal = retVal + line + "\n";
				}
				Document doc = Jsoup.parse(retVal);

				String text = doc.text();
				int startIndex = text.indexOf("formatted_address");
				startIndex = startIndex + 26;
				int endIndex = text.indexOf(",", startIndex);
				String checkVal = text.substring(startIndex, endIndex);
				if (checkVal.equals(String.valueOf(i))) {
					addressNodeNum = i;
					addressList.add(address + addressNodeNum + "號");
					break;
				} else if (checkVal.equals("You")) {
					i = i + 1;
				}
				TimeUnit.MILLISECONDS.sleep(100);
			}

			addressList.add(address + endAddressNum + "號");
		} else if (200 < endAddressNum && endAddressNum <= 300) {
			int addressNodeNum = 0;
			addressList.add(address + startAddressNum + "號");

			// for No.100
			for (int i = 101; i < 110; i++) {
				URL url = new URL("https://maps.googleapis.com/maps/api/geocode/json?address=" + address + i + "號&key="
						+ GoogleMapApiKey.getKey());
				URLConnection conn = url.openConnection();
				conn.setRequestProperty("user-agent", "Chrome/7.0.517.44");
				InputStream in = conn.getInputStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(in, "utf-8"));

				String retVal = "";
				String line = null;
				while ((line = br.readLine()) != null) {
					retVal = retVal + line + "\n";
				}
				Document doc = Jsoup.parse(retVal);

				String text = doc.text();
				int startIndex = text.indexOf("formatted_address");
				startIndex = startIndex + 26;
				int endIndex = text.indexOf(",", startIndex);
				String checkVal = text.substring(startIndex, endIndex);
				if (checkVal.equals(String.valueOf(i))) {
					addressNodeNum = i;
					addressList.add(address + addressNodeNum + "號");
					break;
				} else if (checkVal.equals("You")) {
					i = i + 1;
				}
				TimeUnit.MILLISECONDS.sleep(100);
			}

			// for No.200
			for (int i = 201; i < 210; i++) {
				URL url = new URL("https://maps.googleapis.com/maps/api/geocode/json?address=" + address + i + "號&key="
						+ GoogleMapApiKey.getKey());
				URLConnection conn = url.openConnection();
				conn.setRequestProperty("user-agent", "Chrome/7.0.517.44");
				InputStream in = conn.getInputStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(in, "utf-8"));

				String retVal = "";
				String line = null;
				while ((line = br.readLine()) != null) {
					retVal = retVal + line + "\n";
				}
				Document doc = Jsoup.parse(retVal);

				String text = doc.text();
				int startIndex = text.indexOf("formatted_address");
				startIndex = startIndex + 26;
				int endIndex = text.indexOf(",", startIndex);
				String checkVal = text.substring(startIndex, endIndex);
				if (checkVal.equals(String.valueOf(i))) {
					addressNodeNum = i;
					addressList.add(address + addressNodeNum + "號");
					break;
				} else if (checkVal.equals("You")) {
					i = i + 1;
				}
				TimeUnit.MILLISECONDS.sleep(100);
			}

			addressList.add(address + endAddressNum + "號");
		} else if (300 < endAddressNum && endAddressNum <= 400) {
			int addressNodeNum = 0;
			addressList.add(address + startAddressNum + "號");

			// for No.100
			for (int i = 101; i < 110; i++) {
				URL url = new URL("https://maps.googleapis.com/maps/api/geocode/json?address=" + address + i + "號&key="
						+ GoogleMapApiKey.getKey());
				URLConnection conn = url.openConnection();
				conn.setRequestProperty("user-agent", "Chrome/7.0.517.44");
				InputStream in = conn.getInputStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(in, "utf-8"));

				String retVal = "";
				String line = null;
				while ((line = br.readLine()) != null) {
					retVal = retVal + line + "\n";
				}
				Document doc = Jsoup.parse(retVal);

				String text = doc.text();
				int startIndex = text.indexOf("formatted_address");
				startIndex = startIndex + 26;
				int endIndex = text.indexOf(",", startIndex);
				String checkVal = text.substring(startIndex, endIndex);
				if (checkVal.equals(String.valueOf(i))) {
					addressNodeNum = i;
					addressList.add(address + addressNodeNum + "號");
					break;
				} else if (checkVal.equals("You")) {
					i = i + 1;
				}
				TimeUnit.MILLISECONDS.sleep(100);
			}

			// for No.200
			for (int i = 201; i < 210; i++) {
				URL url = new URL("https://maps.googleapis.com/maps/api/geocode/json?address=" + address + i + "號&key="
						+ GoogleMapApiKey.getKey());
				URLConnection conn = url.openConnection();
				conn.setRequestProperty("user-agent", "Chrome/7.0.517.44");
				InputStream in = conn.getInputStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(in, "utf-8"));

				String retVal = "";
				String line = null;
				while ((line = br.readLine()) != null) {
					retVal = retVal + line + "\n";
				}
				Document doc = Jsoup.parse(retVal);

				String text = doc.text();
				int startIndex = text.indexOf("formatted_address");
				startIndex = startIndex + 26;
				int endIndex = text.indexOf(",", startIndex);
				String checkVal = text.substring(startIndex, endIndex);
				if (checkVal.equals(String.valueOf(i))) {
					addressNodeNum = i;
					addressList.add(address + addressNodeNum + "號");
					break;
				} else if (checkVal.equals("You")) {
					i = i + 1;
				}
				TimeUnit.MILLISECONDS.sleep(100);
			}

			// for No.300
			for (int i = 301; i < 310; i++) {
				URL url = new URL(
						"https://maps.googleapis.com/maps/api/geocode/json?address=" + address + i + "號&sensor=false");
				URLConnection conn = url.openConnection();
				conn.setRequestProperty("user-agent", "Chrome/7.0.517.44");
				InputStream in = conn.getInputStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(in, "utf-8"));

				String retVal = "";
				String line = null;
				while ((line = br.readLine()) != null) {
					retVal = retVal + line + "\n";
				}
				Document doc = Jsoup.parse(retVal);

				String text = doc.text();
				int startIndex = text.indexOf("formatted_address");
				startIndex = startIndex + 26;
				int endIndex = text.indexOf(",", startIndex);
				String checkVal = text.substring(startIndex, endIndex);
				if (checkVal.equals(String.valueOf(i))) {
					addressNodeNum = i;
					addressList.add(address + addressNodeNum + "號");
					break;
				} else if (checkVal.equals("You")) {
					i = i + 1;
				}
				TimeUnit.MILLISECONDS.sleep(100);
			}

			addressList.add(address + endAddressNum + "號");
		} else if (400 < endAddressNum && endAddressNum <= 500) {
			int addressNodeNum = 0;
			addressList.add(address + startAddressNum + "號");

			// for No.100
			for (int i = 101; i < 110; i++) {
				URL url = new URL(
						"https://maps.googleapis.com/maps/api/geocode/json?address=" + address + i + "號&sensor=false");
				URLConnection conn = url.openConnection();
				conn.setRequestProperty("user-agent", "Chrome/7.0.517.44");
				InputStream in = conn.getInputStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(in, "utf-8"));

				String retVal = "";
				String line = null;
				while ((line = br.readLine()) != null) {
					retVal = retVal + line + "\n";
				}
				Document doc = Jsoup.parse(retVal);

				String text = doc.text();
				int startIndex = text.indexOf("formatted_address");
				startIndex = startIndex + 26;
				int endIndex = text.indexOf(",", startIndex);
				String checkVal = text.substring(startIndex, endIndex);
				if (checkVal.equals(String.valueOf(i))) {
					addressNodeNum = i;
					addressList.add(address + addressNodeNum + "號");
					break;
				} else if (checkVal.equals("You")) {
					i = i + 1;
				}
				TimeUnit.MILLISECONDS.sleep(100);
			}

			// for No.200
			for (int i = 201; i < 210; i++) {
				URL url = new URL(
						"https://maps.googleapis.com/maps/api/geocode/json?address=" + address + i + "號&sensor=false");
				URLConnection conn = url.openConnection();
				conn.setRequestProperty("user-agent", "Chrome/7.0.517.44");
				InputStream in = conn.getInputStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(in, "utf-8"));

				String retVal = "";
				String line = null;
				while ((line = br.readLine()) != null) {
					retVal = retVal + line + "\n";
				}
				Document doc = Jsoup.parse(retVal);

				String text = doc.text();
				int startIndex = text.indexOf("formatted_address");
				startIndex = startIndex + 26;
				int endIndex = text.indexOf(",", startIndex);
				String checkVal = text.substring(startIndex, endIndex);
				if (checkVal.equals(String.valueOf(i))) {
					addressNodeNum = i;
					addressList.add(address + addressNodeNum + "號");
					break;
				} else if (checkVal.equals("You")) {
					i = i + 1;
				}
				TimeUnit.MILLISECONDS.sleep(100);
			}

			// for No.300
			for (int i = 301; i < 310; i++) {
				URL url = new URL(
						"https://maps.googleapis.com/maps/api/geocode/json?address=" + address + i + "號&sensor=false");
				URLConnection conn = url.openConnection();
				conn.setRequestProperty("user-agent", "Chrome/7.0.517.44");
				InputStream in = conn.getInputStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(in, "utf-8"));

				String retVal = "";
				String line = null;
				while ((line = br.readLine()) != null) {
					retVal = retVal + line + "\n";
				}
				Document doc = Jsoup.parse(retVal);

				String text = doc.text();
				int startIndex = text.indexOf("formatted_address");
				startIndex = startIndex + 26;
				int endIndex = text.indexOf(",", startIndex);
				String checkVal = text.substring(startIndex, endIndex);
				if (checkVal.equals(String.valueOf(i))) {
					addressNodeNum = i;
					addressList.add(address + addressNodeNum + "號");
					break;
				} else if (checkVal.equals("You")) {
					i = i + 1;
				}
				TimeUnit.MILLISECONDS.sleep(100);
			}

			// for No.400
			for (int i = 401; i < 410; i++) {
				URL url = new URL(
						"https://maps.googleapis.com/maps/api/geocode/json?address=" + address + i + "號&sensor=false");
				URLConnection conn = url.openConnection();
				conn.setRequestProperty("user-agent", "Chrome/7.0.517.44");
				InputStream in = conn.getInputStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(in, "utf-8"));

				String retVal = "";
				String line = null;
				while ((line = br.readLine()) != null) {
					retVal = retVal + line + "\n";
				}
				Document doc = Jsoup.parse(retVal);

				String text = doc.text();
				int startIndex = text.indexOf("formatted_address");
				startIndex = startIndex + 26;
				int endIndex = text.indexOf(",", startIndex);
				String checkVal = text.substring(startIndex, endIndex);
				if (checkVal.equals(String.valueOf(i))) {
					addressNodeNum = i;
					addressList.add(address + addressNodeNum + "號");
					break;
				} else if (checkVal.equals("You")) {
					i = i + 1;
				}
				TimeUnit.MILLISECONDS.sleep(100);
			}

			addressList.add(address + endAddressNum + "號");
		} else if (500 < endAddressNum && endAddressNum <= 600) {
			int addressNodeNum = 0;
			addressList.add(address + startAddressNum + "號");

			// for No.100
			for (int i = 101; i < 110; i++) {
				URL url = new URL(
						"https://maps.googleapis.com/maps/api/geocode/json?address=" + address + i + "號&sensor=false");
				URLConnection conn = url.openConnection();
				conn.setRequestProperty("user-agent", "Chrome/7.0.517.44");
				InputStream in = conn.getInputStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(in, "utf-8"));

				String retVal = "";
				String line = null;
				while ((line = br.readLine()) != null) {
					retVal = retVal + line + "\n";
				}
				Document doc = Jsoup.parse(retVal);

				String text = doc.text();
				int startIndex = text.indexOf("formatted_address");
				startIndex = startIndex + 26;
				int endIndex = text.indexOf(",", startIndex);
				String checkVal = text.substring(startIndex, endIndex);
				if (checkVal.equals(String.valueOf(i))) {
					addressNodeNum = i;
					addressList.add(address + addressNodeNum + "號");
					break;
				} else if (checkVal.equals("You")) {
					i = i + 1;
				}
				TimeUnit.MILLISECONDS.sleep(100);
			}

			// for No.200
			for (int i = 201; i < 210; i++) {
				URL url = new URL(
						"https://maps.googleapis.com/maps/api/geocode/json?address=" + address + i + "號&sensor=false");
				URLConnection conn = url.openConnection();
				conn.setRequestProperty("user-agent", "Chrome/7.0.517.44");
				InputStream in = conn.getInputStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(in, "utf-8"));

				String retVal = "";
				String line = null;
				while ((line = br.readLine()) != null) {
					retVal = retVal + line + "\n";
				}
				Document doc = Jsoup.parse(retVal);

				String text = doc.text();
				int startIndex = text.indexOf("formatted_address");
				startIndex = startIndex + 26;
				int endIndex = text.indexOf(",", startIndex);
				String checkVal = text.substring(startIndex, endIndex);
				if (checkVal.equals(String.valueOf(i))) {
					addressNodeNum = i;
					addressList.add(address + addressNodeNum + "號");
					break;
				} else if (checkVal.equals("You")) {
					i = i + 1;
				}
				TimeUnit.MILLISECONDS.sleep(100);
			}

			// for No.300
			for (int i = 301; i < 310; i++) {
				URL url = new URL(
						"https://maps.googleapis.com/maps/api/geocode/json?address=" + address + i + "號&sensor=false");
				URLConnection conn = url.openConnection();
				conn.setRequestProperty("user-agent", "Chrome/7.0.517.44");
				InputStream in = conn.getInputStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(in, "utf-8"));

				String retVal = "";
				String line = null;
				while ((line = br.readLine()) != null) {
					retVal = retVal + line + "\n";
				}
				Document doc = Jsoup.parse(retVal);

				String text = doc.text();
				int startIndex = text.indexOf("formatted_address");
				startIndex = startIndex + 26;
				int endIndex = text.indexOf(",", startIndex);
				String checkVal = text.substring(startIndex, endIndex);
				if (checkVal.equals(String.valueOf(i))) {
					addressNodeNum = i;
					addressList.add(address + addressNodeNum + "號");
					break;
				} else if (checkVal.equals("You")) {
					i = i + 1;
				}
				TimeUnit.MILLISECONDS.sleep(100);
			}

			// for No.400
			for (int i = 401; i < 410; i++) {
				URL url = new URL(
						"https://maps.googleapis.com/maps/api/geocode/json?address=" + address + i + "號&sensor=false");
				URLConnection conn = url.openConnection();
				conn.setRequestProperty("user-agent", "Chrome/7.0.517.44");
				InputStream in = conn.getInputStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(in, "utf-8"));

				String retVal = "";
				String line = null;
				while ((line = br.readLine()) != null) {
					retVal = retVal + line + "\n";
				}
				Document doc = Jsoup.parse(retVal);

				String text = doc.text();
				int startIndex = text.indexOf("formatted_address");
				startIndex = startIndex + 26;
				int endIndex = text.indexOf(",", startIndex);
				String checkVal = text.substring(startIndex, endIndex);
				if (checkVal.equals(String.valueOf(i))) {
					addressNodeNum = i;
					addressList.add(address + addressNodeNum + "號");
					break;
				} else if (checkVal.equals("You")) {
					i = i + 1;
				}
				TimeUnit.MILLISECONDS.sleep(100);
			}

			// for No.500
			for (int i = 501; i < 510; i++) {
				URL url = new URL("https://maps.googleapis.com/maps/api/geocode/json?address=" + address + i + "號&key="
						+ GoogleMapApiKey.getKey());
				URLConnection conn = url.openConnection();
				conn.setRequestProperty("user-agent", "Chrome/7.0.517.44");
				InputStream in = conn.getInputStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(in, "utf-8"));

				String retVal = "";
				String line = null;
				while ((line = br.readLine()) != null) {
					retVal = retVal + line + "\n";
				}
				Document doc = Jsoup.parse(retVal);

				String text = doc.text();
				int startIndex = text.indexOf("formatted_address");
				startIndex = startIndex + 26;
				int endIndex = text.indexOf(",", startIndex);
				String checkVal = text.substring(startIndex, endIndex);
				if (checkVal.equals(String.valueOf(i))) {
					addressNodeNum = i;
					addressList.add(address + addressNodeNum + "號");
					break;
				} else if (checkVal.equals("You")) {
					i = i + 1;
				}
				TimeUnit.MILLISECONDS.sleep(100);
			}

			addressList.add(address + endAddressNum + "號");
		} else {
			int addressNodeNum = 0;
			addressList.add(address + startAddressNum + "號");

			// for No.100
			for (int i = 101; i < 110; i++) {
				URL url = new URL("https://maps.googleapis.com/maps/api/geocode/json?address=" + address + i + "號&key="
						+ GoogleMapApiKey.getKey());
				URLConnection conn = url.openConnection();
				conn.setRequestProperty("user-agent", "Chrome/7.0.517.44");
				InputStream in = conn.getInputStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(in, "utf-8"));

				String retVal = "";
				String line = null;
				while ((line = br.readLine()) != null) {
					retVal = retVal + line + "\n";
				}
				Document doc = Jsoup.parse(retVal);

				String text = doc.text();
				int startIndex = text.indexOf("formatted_address");
				startIndex = startIndex + 26;
				int endIndex = text.indexOf(",", startIndex);
				String checkVal = text.substring(startIndex, endIndex);
				if (checkVal.equals(String.valueOf(i))) {
					addressNodeNum = i;
					addressList.add(address + addressNodeNum + "號");
					break;
				} else if (checkVal.equals("You")) {
					i = i + 1;
				}
				TimeUnit.MILLISECONDS.sleep(100);
			}

			// for No.200
			for (int i = 201; i < 210; i++) {
				URL url = new URL("https://maps.googleapis.com/maps/api/geocode/json?address=" + address + i + "號&key="
						+ GoogleMapApiKey.getKey());
				URLConnection conn = url.openConnection();
				conn.setRequestProperty("user-agent", "Chrome/7.0.517.44");
				InputStream in = conn.getInputStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(in, "utf-8"));

				String retVal = "";
				String line = null;
				while ((line = br.readLine()) != null) {
					retVal = retVal + line + "\n";
				}
				Document doc = Jsoup.parse(retVal);

				String text = doc.text();
				int startIndex = text.indexOf("formatted_address");
				startIndex = startIndex + 26;
				int endIndex = text.indexOf(",", startIndex);
				String checkVal = text.substring(startIndex, endIndex);
				if (checkVal.equals(String.valueOf(i))) {
					addressNodeNum = i;
					addressList.add(address + addressNodeNum + "號");
					break;
				} else if (checkVal.equals("You")) {
					i = i + 1;
				}
				TimeUnit.MILLISECONDS.sleep(100);
			}

			// for No.300
			for (int i = 301; i < 310; i++) {
				URL url = new URL("https://maps.googleapis.com/maps/api/geocode/json?address=" + address + i + "號&key="
						+ GoogleMapApiKey.getKey());
				URLConnection conn = url.openConnection();
				conn.setRequestProperty("user-agent", "Chrome/7.0.517.44");
				InputStream in = conn.getInputStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(in, "utf-8"));

				String retVal = "";
				String line = null;
				while ((line = br.readLine()) != null) {
					retVal = retVal + line + "\n";
				}
				Document doc = Jsoup.parse(retVal);

				String text = doc.text();
				int startIndex = text.indexOf("formatted_address");
				startIndex = startIndex + 26;
				int endIndex = text.indexOf(",", startIndex);
				String checkVal = text.substring(startIndex, endIndex);
				if (checkVal.equals(String.valueOf(i))) {
					addressNodeNum = i;
					addressList.add(address + addressNodeNum + "號");
					break;
				} else if (checkVal.equals("You")) {
					i = i + 1;
				}
				TimeUnit.MILLISECONDS.sleep(100);
			}

			// for No.400
			for (int i = 401; i < 410; i++) {
				URL url = new URL("https://maps.googleapis.com/maps/api/geocode/json?address=" + address + i + "號&key="
						+ GoogleMapApiKey.getKey());
				URLConnection conn = url.openConnection();
				conn.setRequestProperty("user-agent", "Chrome/7.0.517.44");
				InputStream in = conn.getInputStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(in, "utf-8"));

				String retVal = "";
				String line = null;
				while ((line = br.readLine()) != null) {
					retVal = retVal + line + "\n";
				}
				Document doc = Jsoup.parse(retVal);

				String text = doc.text();
				int startIndex = text.indexOf("formatted_address");
				startIndex = startIndex + 26;
				int endIndex = text.indexOf(",", startIndex);
				String checkVal = text.substring(startIndex, endIndex);
				if (checkVal.equals(String.valueOf(i))) {
					addressNodeNum = i;
					addressList.add(address + addressNodeNum + "號");
					break;
				} else if (checkVal.equals("You")) {
					i = i + 1;
				}
				TimeUnit.MILLISECONDS.sleep(100);
			}

			// for No.500
			for (int i = 501; i < 510; i++) {
				URL url = new URL("https://maps.googleapis.com/maps/api/geocode/json?address=" + address + i + "號&key="
						+ GoogleMapApiKey.getKey());
				URLConnection conn = url.openConnection();
				conn.setRequestProperty("user-agent", "Chrome/7.0.517.44");
				InputStream in = conn.getInputStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(in, "utf-8"));

				String retVal = "";
				String line = null;
				while ((line = br.readLine()) != null) {
					retVal = retVal + line + "\n";
				}
				Document doc = Jsoup.parse(retVal);

				String text = doc.text();
				int startIndex = text.indexOf("formatted_address");
				startIndex = startIndex + 26;
				int endIndex = text.indexOf(",", startIndex);
				String checkVal = text.substring(startIndex, endIndex);
				if (checkVal.equals(String.valueOf(i))) {
					addressNodeNum = i;
					addressList.add(address + addressNodeNum + "號");
					break;
				} else if (checkVal.equals("You")) {
					i = i + 1;
				}
				TimeUnit.MILLISECONDS.sleep(100);
			}

			// for No.600
			for (int i = 601; i < 610; i++) {
				URL url = new URL("https://maps.googleapis.com/maps/api/geocode/json?address=" + address + i + "號&key="
						+ GoogleMapApiKey.getKey());
				URLConnection conn = url.openConnection();
				conn.setRequestProperty("user-agent", "Chrome/7.0.517.44");
				InputStream in = conn.getInputStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(in, "utf-8"));

				String retVal = "";
				String line = null;
				while ((line = br.readLine()) != null) {
					retVal = retVal + line + "\n";
				}
				Document doc = Jsoup.parse(retVal);

				String text = doc.text();
				int startIndex = text.indexOf("formatted_address");
				startIndex = startIndex + 26;
				int endIndex = text.indexOf(",", startIndex);
				String checkVal = text.substring(startIndex, endIndex);
				if (checkVal.equals(String.valueOf(i))) {
					addressNodeNum = i;
					addressList.add(address + addressNodeNum + "號");
					break;
				} else if (checkVal.equals("You")) {
					i = i + 1;
				}
				TimeUnit.MILLISECONDS.sleep(100);
			}

			addressList.add(address + endAddressNum + "號");
		}
		return addressList;
	}

	private TaskInfomation createTaskInRoad(String startAddress, String endAddress) throws IOException {
		TaskInfomation taskInfo = new TaskInfomation();
		Gson gson = new Gson();

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

		// set address
		taskInfo.setStartAddress(startAddress);
		taskInfo.setEndAddress(endAddress);

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

		return taskInfo;
	}

	private List<String> findLane(String address, int startNum, int endNum) throws IOException, InterruptedException {
		List<String> laneList = new ArrayList<>();
		for (int i = startNum; i < endNum; i++) {
			if (i % 10 == 0) {
				System.out.println("count number to find lane No." + i);
			}
			URL url = new URL("https://maps.googleapis.com/maps/api/geocode/json?address=" + address + i + "巷&key="
					+ GoogleMapApiKey.getKey());
			URLConnection conn = url.openConnection();
			conn.setRequestProperty("user-agent", "Chrome/7.0.517.44");
			InputStream in = conn.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(in, "utf-8"));

			String retVal = "";
			String line = null;
			while ((line = br.readLine()) != null) {
				retVal = retVal + line + "\n";
			}
			Document doc = Jsoup.parse(retVal);

			String text = doc.text();
			int index = text.indexOf("long_name");
			index = index + 19;
			int endIndex = text.indexOf(",", index);
			String checkLane = text.substring(index, endIndex);
			if (checkLane.equals(String.valueOf(i))) {
				laneList.add(address + i + "巷");
			} else if (checkLane.contains("You")) {
				i = i - 1;
			}
			TimeUnit.MILLISECONDS.sleep(100);
		}
		return laneList;
	}

	private List<String> getLaneAddress(String address) throws IOException, InterruptedException {
		List<String> laneAddress = new ArrayList<>();
		String startAddress = null;
		String endAddress = null;

		// find start address
		for (int i = 1; i < 10; i++) {
			URL url = new URL("https://maps.googleapis.com/maps/api/geocode/json?address=" + address + i + "號&key="
					+ GoogleMapApiKey.getKey());
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
			int startIndex = text.indexOf("formatted_address");
			startIndex = startIndex + 26;
			int endIndex = text.indexOf(",", startIndex);
			String checkVal = text.substring(startIndex, endIndex);
			if (checkVal.equals(String.valueOf(i))) {
				startIndex = text.indexOf("long_name");
				startIndex = text.indexOf("long_name", startIndex + 1);
				startIndex = startIndex + 14;
				endIndex = text.indexOf(" ", startIndex);
				checkVal = text.substring(startIndex, endIndex);
				if (checkVal.equals("Lane")) {
					startAddress = address + i + "號";
					break;
				} else if (checkVal.contains("You")) {
					i = i - 1;
				}
				TimeUnit.MILLISECONDS.sleep(100);
			}
		}

		// find end address
		for (int i = 200; i > 10; i--) {
			if (i % 10 == 0) {
				System.out.println("count end lane No." + i);
			}
			TimeUnit.MILLISECONDS.sleep(100);
			URL url = new URL("https://maps.googleapis.com/maps/api/geocode/json?address=" + address + i + "號&key="
					+ GoogleMapApiKey.getKey());
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
			int startIndex = text.indexOf("formatted_address");
			startIndex = startIndex + 26;
			int endIndex = text.indexOf(",", startIndex);
			String checkVal = text.substring(startIndex, endIndex);
			if (checkVal.equals(String.valueOf(i))) {
				startIndex = text.indexOf("long_name");
				startIndex = text.indexOf("long_name", startIndex + 1);
				startIndex = startIndex + 14;
				endIndex = text.indexOf(" ", startIndex);
				checkVal = text.substring(startIndex, endIndex);
				if (checkVal.equals("Lane")) {
					endAddress = address + i + "號";
					break;
				} else if (checkVal.contains("You")) {
					i = i + 1;
				}
				TimeUnit.MILLISECONDS.sleep(100);
			}
		}
		laneAddress.add(startAddress);
		laneAddress.add(endAddress);
		return laneAddress;
	}
}
