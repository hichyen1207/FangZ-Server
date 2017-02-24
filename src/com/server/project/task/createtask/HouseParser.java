package com.server.project.task.createtask;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import com.google.gson.Gson;
import com.server.project.api.House;

public class HouseParser {
	WebDriver driver;

	public static void main(String[] args) throws InterruptedException, ClassNotFoundException, SQLException {
		HouseParser ap = new HouseParser();
		ap.parseAddress();
	}

	public void parseAddress() throws InterruptedException, ClassNotFoundException, SQLException {
		System.setProperty("webdriver.chrome.driver", "/Users/Hao/Documents/Java/ProjectServer/chromedriver");
		driver = new ChromeDriver();
		// parse 信義房屋
		// navigate to house list

		// driver.get("http://www.sinyi.com.tw/");
		// WebElement searchButton =
		// driver.findElement(By.className("button_keyword"));
		// searchButton.click();
		// driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);

		int endNum = 0;
		try {
			Class.forName("org.postgresql.Driver").newInstance();
			String url = "jdbc:postgresql://140.119.19.33:5432/project";
			Connection con = DriverManager.getConnection(url, "postgres", "093622"); // 帳號密碼
			Statement selectST = con.createStatement();

			String getHouseNumSQL = "select count('id') from house;";
			ResultSet selectRS = selectST.executeQuery(getHouseNumSQL);
			while (selectRS.next()) {
				endNum = Integer.valueOf(selectRS.getString(1));
			}
		} catch (Exception e) {
			e.getMessage();
		}

		int startPage = (endNum / 30) + 1;
		int startNum = (endNum % 30) + 1;
		driver.get("http://buy.sinyi.com.tw/list/" + (startPage - 1) + ".html");

		System.out.println("start from page" + startPage);
		List<WebElement> button = driver.findElement(By.id("search_pagination")).findElements(By.tagName("li"));
		boolean breakIt = true;
		while (true) {
			breakIt = true;
			try {
				for (WebElement li : button) {
					String text = li.getText();
					if (text.equals(String.valueOf(startPage))) {
						System.out.println("click");
						li.click();
						Thread.sleep(4000);
						break;
					}
				}
			} catch (Exception e) {
				if (e.getMessage().contains("element is not attached")) {
					breakIt = false;
					System.out.println("try again");
				}
			}
			if (breakIt) {
				break;
			}

		}
		// navigate to each page
		for (int i = startNum - 1; i < 30; i++) {
			System.out.println("start parse No." + (i + 1));
			House house = seleniumParse(i);
			System.out.println("start insert to database");
			Gson gson = new Gson();
			System.out.println(gson.toJson(house));
			locationInsertIntoDB(house);
		}

		for (int j = startPage + 1; j < 100; j++) {
			driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
			System.out.println("start from page" + j);
			button = driver.findElement(By.id("search_pagination")).findElements(By.tagName("li"));
			breakIt = true;
			while (true) {
				breakIt = true;
				try {
					for (WebElement li : button) {
						String text = li.getText();
						if (text.equals(String.valueOf(j))) {
							System.out.println("click");
							li.click();
							Thread.sleep(4000);
							break;
						}
					}
				} catch (Exception e) {
					if (e.getMessage().contains("element is not attached")) {
						breakIt = false;
						System.out.println("try again");
					}
				}
				if (breakIt) {
					break;
				}

			}
			// navigate to each page
			for (int i = 0; i < 30; i++) {
				System.out.println("start parse No." + (i + 1));
				House house = seleniumParse(i);
				System.out.println("start insert to database");
				Gson gson = new Gson();
				System.out.println(gson.toJson(house));
				locationInsertIntoDB(house);
			}
		}
		driver.quit();
		System.out.println("complete parse the web and start to inert into DB.");
	}

	// 把爬到的經緯度資料存入資料庫
	public void locationInsertIntoDB(House house) throws ClassNotFoundException, SQLException {
		Connection c = null;
		// connect DB
		Class.forName("org.postgresql.Driver");
		c = DriverManager.getConnection("jdbc:postgresql://140.119.19.33:5432/project", "postgres", "093622");
		// insert each location into table
		Statement stmt = c.createStatement();
		String insertHouseSQL = "INSERT INTO house(title, description, location, price, address, registered_square, status, pattern, type, url) VALUES('"
				+ house.getTitle() + "', '" + house.getDescription() + "', ST_GeomFromText('POINT("
				+ house.getLocation() + ")', 4326), '" + house.getPrice() + "', '" + house.getAddress() + "', '"
				+ house.getRegisteredSquare() + "', '" + house.getStatus() + "', '" + house.getPattern() + "', '"
				+ house.getType() + "', '" + house.getUrl() + "');";
		stmt.executeUpdate(insertHouseSQL);

		stmt.close();
		c.close();
		System.out.println("insert 1");
	}

	private House seleniumParse(int index) {
		List<WebElement> resultItems = driver.findElements(By.className("search_result_item"));
		House house = new House();
		WebElement item = resultItems.get(index);
		String itemURL = item.findElement(By.tagName("a")).getAttribute("href");
		WebDriver itemDriver = new ChromeDriver();
		itemDriver.get(itemURL);

		// get location URL
		WebElement itemMap = itemDriver.findElement(By.id("static_map"));
		String itemlocationURL = itemMap.getAttribute("data-src");
		// get 經緯度
		String[] splitURl = itemlocationURL.replace(".png", "").split("_");
		// 緯度
		String latitude = splitURl[splitURl.length - 2];
		// 經度
		String longitude = splitURl[splitURl.length - 1];
		String itemLocation = longitude + " " + latitude;
		house.setLocation(itemLocation);

		// get title
		String itemTitleElement = itemDriver.findElement(By.id("content-main")).findElement(By.tagName("h2")).getText();
		int titleIndex = itemTitleElement.indexOf("(");
		String itemTitle = itemTitleElement.substring(0, titleIndex);
		house.setTitle(itemTitle);

		// get price
		String itemPrice = itemDriver.findElement(By.id("obj-info")).findElement(By.className("price")).getText() + "萬";
		house.setPrice(itemPrice);

		// get address
		String itemAddress = itemDriver.findElement(By.id("content-main")).findElement(By.tagName("h1")).getText();
		house.setAddress(itemAddress);

		// get registered square
		String itemRegisteredSquare = itemDriver.findElement(By.id("obj-info")).findElements(By.tagName("li")).get(1)
				.getText();
		house.setRegisteredSquare(itemRegisteredSquare);

		// get type
		String itemType = itemDriver.findElement(By.id("obj-info")).findElements(By.tagName("li")).get(3).getText();
		house.setType(itemType);

		// get url
		String itemUrl = itemDriver.getCurrentUrl();
		house.setUrl(itemUrl);

		// get pattern
		String itemPattern = itemDriver.findElement(By.id("obj-info")).findElements(By.tagName("li")).get(4).getText();
		if (itemPattern.contains("社區")) {
			itemPattern = itemDriver.findElement(By.id("obj-info")).findElements(By.tagName("li")).get(5).getText();
			house.setPattern(itemPattern);

			// get status
			String itemStatus = itemDriver.findElement(By.id("obj-info")).findElements(By.tagName("li")).get(6)
					.getText();
			itemStatus = itemStatus.replaceAll("\n", ", ");
			house.setStatus(itemStatus);

			// get description
			String itemDescription = itemDriver.findElement(By.id("obj-info")).findElements(By.tagName("li")).get(7)
					.getText();
			itemDescription = itemDescription.replaceAll("\n", ",");
			house.setDescription(itemDescription);
		} else {
			house.setPattern(itemPattern);

			// get status
			String itemStatus = itemDriver.findElement(By.id("obj-info")).findElements(By.tagName("li")).get(5)
					.getText();
			itemStatus = itemStatus.replaceAll("\n", ", ");
			house.setStatus(itemStatus);

			// get description
			String itemDescription = itemDriver.findElement(By.id("obj-info")).findElements(By.tagName("li")).get(6)
					.getText();
			itemDescription = itemDescription.replaceAll("\n", ", ");
			house.setDescription(itemDescription);
		}
		itemDriver.close();
		return house;
	}
}