package com.server.project.fix;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class AddressDatabaseCreator {
	public static void main(String[] args) throws Exception {
		AddressDatabaseCreator addressDatabaseCreator = new AddressDatabaseCreator();
		addressDatabaseCreator.insertAddress();
	}

	public void insertAddress() throws Exception {
		Class.forName("org.postgresql.Driver").newInstance();

		String url = "jdbc:postgresql://140.119.19.33:5432/project";
		Connection con = DriverManager.getConnection(url, "postgres", "093622"); // 帳號密碼
		Statement selectST = con.createStatement();
		Statement getNumST = con.createStatement();

		String sql = "select distinct address, address_point from house;";
		ResultSet selectRS = selectST.executeQuery(sql);
		while (selectRS.next()) {
			String address = selectRS.getString("address");
			String addressPoint = selectRS.getString("address_point");
			int houseNum = 0;

			String getHouseNumSQL = "select address,count(id) from house group by address;";
			ResultSet getNumRS = getNumST.executeQuery(getHouseNumSQL);
			while (getNumRS.next()) {
				if (getNumRS.getString("address").equals(address)) {
					houseNum = getNumRS.getInt("count");
					break;
				}
			}

			Statement insertST = con.createStatement();
			String insertSQL = "insert into address (address, geometry, house_number) values ('" + address + "', '"
					+ addressPoint + "', " + houseNum + ");";
			System.out.println(insertSQL);
			insertST.executeUpdate(insertSQL);
			insertST.close();
		}
		selectRS.close();
		selectST.close();
		con.close();
	}
}
