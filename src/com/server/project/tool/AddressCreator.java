package com.server.project.tool;

import java.io.IOException;
import java.sql.SQLException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import com.server.project.api.Point;

public class AddressCreator {
	public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException {
		AddressCreator ac = new AddressCreator();
		String aString = ac.createAddress("0101000020E61000002261CE22CA2D3840EF94698FF2275E40");
		System.out.println(aString);
	}

	public String createAddress(String geometry) throws IOException, ClassNotFoundException, SQLException {
		GeometryToPoint toPoint = new GeometryToPoint();
		Point point = toPoint.toPoint(geometry);

		// connect to google map api
		String url = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + point.getLat() + "," + point.getLng()
				+ "&key=" + GoogleMapApiKey.getKey();

		System.setProperty("webdriver.chrome.driver", "/Users/Hao/Documents/Java/ProjectServer/chromedriver");
		WebDriver driver = new ChromeDriver();
		driver.get(url);
		String text = driver.findElement(By.tagName("pre")).getText();
		driver.quit();

		int addrIndex = text.indexOf("formatted_address");
		addrIndex = text.indexOf("\"", addrIndex);
		addrIndex = text.indexOf("\"", addrIndex + 1) + 3;
		int addrEndIndex = text.indexOf("\"", addrIndex + 1);
		String address = text.substring(addrIndex + 1, addrEndIndex);

		if (address.contains("段")) {
			addrEndIndex = text.indexOf("段", addrIndex + 1) + 1;
			address = text.substring(addrIndex + 1, addrEndIndex);
		} else {
			if (address.contains("路")) {
				addrEndIndex = text.indexOf("路", addrIndex + 1) + 1;
				address = text.substring(addrIndex + 1, addrEndIndex);
			} else if (address.contains("街")) {
				addrEndIndex = text.indexOf("街", addrIndex + 1) + 1;
				address = text.substring(addrIndex + 1, addrEndIndex);
			} else if (address.contains("道")) {
				addrEndIndex = text.indexOf("道", addrIndex + 1) + 1;
				address = text.substring(addrIndex + 1, addrEndIndex);
			}
		}
		return address;
	}
}
