package com.server.project.response;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.server.project.api.Road;

public class SearchResponcer {
	public static void main(String[] args) throws Exception {
		Gson gson = new Gson();
		SearchResponcer searchResponcer = new SearchResponcer();

		List<Road> roadList = searchResponcer.searchByTag("餐廳", "警察局", "nothing");
		System.out.println(gson.toJson(roadList));

		// List<String> enList = searchResponcer.searchByEnvironment("車流量少");
		// System.out.println(enList);

		// List<String> faList = searchResponcer.searchByShop("醫院");
		// System.out.println(faList);
	}

	public List<Road> searchByTag(String shops, String facilities, String environments) throws Exception {
		List<String> searchAddressList = new ArrayList<>();

		List<String> shopAddressList = new ArrayList<>();
		List<String> environmentAddressList = new ArrayList<>();
		List<String> facilityAddressList = new ArrayList<>();

		Boolean shopCheck = false;
		Boolean facilityCheck = false;
		Boolean environmentCheck = false;

		if (!shops.equals("nothing")) {
			shopCheck = true;
			shopAddressList = searchByShop(shops);
		}

		if (!facilities.equals("nothing")) {
			facilityCheck = true;
			facilityAddressList = searchByFacility(facilities);
		}

		if (!environments.equals("nothing")) {
			environmentCheck = true;
			environmentAddressList = searchByEnvironment(environments);
		}

		if (shopCheck && facilityCheck && environmentCheck) {
			System.out.println("search shop, facility, and environment.");
			List<String> tempList = new ArrayList<>();
			for (String shopAddress : shopAddressList) {
				for (String facilityAddress : facilityAddressList) {
					if (shopAddress.equals(facilityAddress)) {
						if (!tempList.contains(shopAddress)) {
							tempList.add(shopAddress);
						}
					}
				}
			}

			for (String tempAddress : tempList) {
				for (String environmentAddress : environmentAddressList) {
					if (tempAddress.equals(environmentAddress)) {
						if (!searchAddressList.contains(tempAddress)) {
							searchAddressList.add(tempAddress);
						}
					}
				}
			}
		} else if (shopCheck && facilityCheck) {
			System.out.println("search shop and facility.");
			for (String shopAddress : shopAddressList) {
				for (String facilityAddress : facilityAddressList) {
					if (shopAddress.equals(facilityAddress)) {
						if (!searchAddressList.contains(shopAddress)) {
							searchAddressList.add(shopAddress);
						}
					}
				}
			}
		} else if (shopCheck && environmentCheck) {
			System.out.println("search shop and environment.");
			for (String shopAddress : shopAddressList) {
				for (String environmentAddress : environmentAddressList) {
					if (shopAddress.equals(environmentAddress)) {
						if (!searchAddressList.contains(shopAddress)) {
							searchAddressList.add(shopAddress);
						}
					}
				}
			}
		} else if (facilityCheck && environmentCheck) {
			System.out.println("search facility and environment.");
			for (String facilityAddress : facilityAddressList) {
				for (String environmentAddress : environmentAddressList) {
					if (facilityAddress.equals(environmentAddress)) {
						if (!searchAddressList.contains(facilityAddress)) {
							searchAddressList.add(facilityAddress);
						}
					}
				}
			}
		} else if (shopCheck) {
			System.out.println("search shop.");
			for (String shopAddress : shopAddressList) {
				if (!searchAddressList.contains(shopAddress)) {
					searchAddressList.add(shopAddress);
				}
			}
		} else if (facilityCheck) {
			System.out.println("search facility.");
			for (String facilityAddress : facilityAddressList) {
				if (!searchAddressList.contains(facilityAddress)) {
					searchAddressList.add(facilityAddress);
				}
			}
		} else if (environmentCheck) {
			System.out.println("search environment.");
			for (String environmentAddress : environmentAddressList) {
				if (!searchAddressList.contains(environmentAddress)) {
					searchAddressList.add(environmentAddress);
				}
			}
		}

		System.out.println(searchAddressList);
		List<Road> resultRoadList = getRoadInfoByAddress(searchAddressList);

		return resultRoadList;
	}

	private List<String> searchByShop(String shops) throws Exception {
		List<String> addressList = new ArrayList<>();

		Class.forName("org.postgresql.Driver").newInstance();
		String url = "jdbc:postgresql://140.119.19.33:5432/project";
		Connection con = DriverManager.getConnection(url, "postgres", "093622"); // 帳號密碼
		Statement selectST = con.createStatement();

		String sql = "select distinct(address) from video where ";

		List<String> shopList = tagStringToList(shops);

		if (shopList.size() != 0) {
			int shopsLength = 0;
			for (String shop : shopList) {
				shopsLength++;
				sql += "shop like '%" + shop + "%'";

				if (shopsLength != shopList.size()) {
					sql += " and ";
				} else {
					sql += ";";
				}
			}
		}

		// System.out.println(sql);
		ResultSet selectRS = selectST.executeQuery(sql);
		while (selectRS.next()) {
			addressList.add(selectRS.getString(1));
		}
		selectRS.close();
		selectST.close();
		con.close();

		System.out.println("shop address: " + addressList);
		return addressList;
	}

	private List<String> searchByFacility(String facilities) throws Exception {
		List<String> addressList = new ArrayList<>();

		Class.forName("org.postgresql.Driver").newInstance();
		String url = "jdbc:postgresql://140.119.19.33:5432/project";
		Connection con = DriverManager.getConnection(url, "postgres", "093622"); // 帳號密碼
		Statement selectST = con.createStatement();

		String sql = "select distinct(address) from video where ";

		List<String> facilityList = tagStringToList(facilities);

		int facilitiesLength = 0;
		for (String facility : facilityList) {
			facilitiesLength++;
			sql += "facility like '%" + facility + "%'";

			if (facilitiesLength != facilityList.size()) {
				sql += " and ";
			} else {
				sql += ";";
			}
		}

		// System.out.println(sql);

		ResultSet selectRS = selectST.executeQuery(sql);
		while (selectRS.next()) {
			addressList.add(selectRS.getString(1));
		}
		selectRS.close();
		selectST.close();
		con.close();

		System.out.println("facility address: " + addressList);
		return addressList;
	}

	private List<String> searchByEnvironment(String environments) throws Exception {
		List<String> addressList = new ArrayList<>();

		Class.forName("org.postgresql.Driver").newInstance();
		String url = "jdbc:postgresql://140.119.19.33:5432/project";
		Connection con = DriverManager.getConnection(url, "postgres", "093622"); // 帳號密碼
		Statement selectST = con.createStatement();

		String sql = "select distinct(address) from video where ";

		List<String> environmentList = tagStringToList(environments);

		int environmentsLength = 0;
		if (environmentList.size() != 0) {
			environmentsLength++;
			for (String environment : environmentList) {
				sql += "environment like '%" + environment + "%'";

				if (environmentsLength != environmentList.size()) {
					sql += " and ";
				} else {
					sql += ";";
				}
			}
		}
		ResultSet selectRS = selectST.executeQuery(sql);
		while (selectRS.next()) {
			addressList.add(selectRS.getString(1));
		}
		selectRS.close();
		selectST.close();
		con.close();

		System.out.println("environment address: " + addressList);
		return addressList;
	}

	private List<Road> getRoadInfoByAddress(List<String> addressList) throws Exception {
		List<Road> roadList = new ArrayList<>();

		Class.forName("org.postgresql.Driver").newInstance();
		String url = "jdbc:postgresql://140.119.19.33:5432/project";
		Connection con = DriverManager.getConnection(url, "postgres", "093622"); // 帳號密碼
		Statement selectST = con.createStatement();

		for (String addressInList : addressList) {
			String sql = "select id, address, ST_AsText(geometry), house_number from address where address='"
					+ addressInList + "';";

			ResultSet selectRS = selectST.executeQuery(sql);
			while (selectRS.next()) {
				Road road = new Road();

				int id = selectRS.getInt("id");
				String address = selectRS.getString("address");
				String addressPoint = selectRS.getString("st_astext");
				int startIndex = addressPoint.indexOf("(");
				int midIndex = addressPoint.indexOf(" ");
				int endIndex = addressPoint.indexOf(")");
				double lng = Double.valueOf(addressPoint.substring(startIndex + 1, midIndex));
				double lat = Double.valueOf(addressPoint.substring(midIndex + 1, endIndex));
				int houseNum = selectRS.getInt("house_number");

				road.setId(id);
				road.setAddress(address);
				road.setLat(lat);
				road.setLng(lng);
				road.setHouseNumber(houseNum);

				roadList.add(road);
			}
			selectRS.close();
		}
		selectST.close();
		con.close();

		return roadList;
	}

	private List<String> tagStringToList(String tagString) {
		List<String> tagList = new ArrayList<>();

		String[] tagArray = tagString.split(",");
		for (String tag : tagArray) {
			tag = tag.replace(" ", "");
			tagList.add(tag);
		}
		return tagList;
	}
}
