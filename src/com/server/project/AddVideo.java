package com.server.project;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import com.server.project.task.database.TaskToVideo;

public class AddVideo {
	public static void main(String[] args) throws Exception {
		AddVideo addVideo = new AddVideo();
		addVideo.fix();
	}

	public void fix() throws Exception {
		TaskToVideo taskToVideo = new TaskToVideo();
		System.setProperty("webdriver.chrome.driver",
				"/Users/Hao/Documents/Java/SoslabProjectHouseParser/chromedriver");
		WebDriver driver = new ChromeDriver();
		driver.get("https://www.youtube.com/feed/trending");
		List<WebElement> list = driver.findElement(By.id("browse-items-primary"))
				.findElement(By.className("feed-item-dismissable")).findElement(By.tagName("ul"))
				.findElements(By.className("expanded-shelf-content-item-wrapper"));
		for (int i = 0; i <= 40; i++) {
			String youtubeId = list.get(i).findElement(By.tagName("a")).getAttribute("href");
			youtubeId = youtubeId.substring(32);
			taskToVideo.toVideo(String.valueOf(i + 110), youtubeId, "aaa", "aAa", "aaa");
		}
		driver.close();

	}
}
