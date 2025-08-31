package com.yatra.automation;

import java.time.Duration;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class YatraAutomationScript {
	public static void main(String[] args) throws InterruptedException {

//		creating chromeoption class to manage the actions of chrome
		ChromeOptions chromeOptions = new ChromeOptions();
		chromeOptions.setPageLoadStrategy(PageLoadStrategy.EAGER); 
		chromeOptions.addArguments("--disable-notifications");// This addArguments of String
		
		//Step 1: Launch the browser (Chrome)
		WebDriver wd=new ChromeDriver(chromeOptions);
		// webdriver is interface and chromedriver is class
		// in java object created on child class
		// wd reference variable
		// interface is the second category of parent type
		// loosely coupled having reference type parent entity and object is of child class
		
		//Step 2: load the page for us
		wd.get("https://www.yatra.com/");
		
		// Maximize the Browser window
		wd.manage().window().maximize();
		// This is called the method chaining 
		// always create a method that returns the data to use it in chain type like above technique
		// Main is the first and last method so it wont return anything 
		// test methods wont return anything
		//but if its a utility methods use with return type
		
		//remove the other pop up message by finding the locator(login Pop up)that makes the script flaky
		WebElement element_LoginPopUp=wd.findElement(By.xpath("//span[@class='style_cross__q1ZoV']/img"));
		element_LoginPopUp.click();
		
		// locator to find the required webElement in the site using xpath here
		By departureDateLocator = By.xpath("//div[@aria-label='Departure Date inputbox' and @role='button']");
		
		WebDriverWait wait = new WebDriverWait(wd, Duration.ofSeconds(20));// synchronizing the webdriver that handling the flakiness in script
		
//		WebElement departureDateWebElement = wd.findElement(departureDateLocator); non synchronized
		// till the element clickable it wait and check every 500 ms in wdw here , this is synchronized
		WebElement departureDateWebElement = wait.until(ExpectedConditions.elementToBeClickable(departureDateLocator));
		
		departureDateWebElement.click();
		
		//Here the By is the abstract class
		WebElement currentMonthCalendarWebElement = selectMonthWebElementFromCalendar(wait,0);
		WebElement nextMonthCalendarWebElement = selectMonthWebElementFromCalendar(wait,1);
		
		Thread.sleep(2000);
		
		String currentMonthLowestPrice = getLowestWebElement(currentMonthCalendarWebElement);
		String nextMonthLowestPrice = getLowestWebElement(nextMonthCalendarWebElement); 
		
		System.out.println(currentMonthLowestPrice);
		System.out.println(nextMonthLowestPrice);
		compareTwoMonthsPrices(currentMonthLowestPrice, nextMonthLowestPrice);
		
	}

	private static String getLowestWebElement(WebElement currentMonthCalendarWebElement) {
		By PriceLocator = By.xpath(".//span[contains(@class,'custom-day-content ')]"); //locator for only prices of each date
		
		List<WebElement> monthCalendarDatePriceList = currentMonthCalendarWebElement.findElements(PriceLocator);// getting all prices in a current month
		
		int lowestPrice = Integer.MAX_VALUE;
		int lowestPriceValue=0;
		WebElement priceElement=null;
		for(WebElement price : monthCalendarDatePriceList){
			 
//			By datePrice=By.xpath(".//span[contains(@class,'custom-day-content ')]");
//			WebElement datePriceWebElement=wait.until(ExpectedConditions.visibilityOf(currentDate.findElement(datePrice)));
//			System.out.println(price.getText());// this gets the element visible text
			
			String priceString = price.getText();
			if(priceString.length()>0) {
			priceString = priceString.replace("₹", "").replace(",", "");
//			System.out.println(priceString);
			
			lowestPriceValue = Integer.parseInt(priceString);
			if(lowestPrice>lowestPriceValue) {
				lowestPrice = lowestPriceValue;
				priceElement=price;// recording the lowest price among all prices
			}
			}
		}
		System.out.println(lowestPrice);
		
		WebElement dateElement = priceElement.findElement(By.xpath(".//../.."));
		return dateElement.getAttribute("aria-label")+"-- Price is Rs"+lowestPrice;
	}

	private static WebElement selectMonthWebElementFromCalendar(WebDriverWait wait,int index) {
		
		By calendarMonthsLocator = By.xpath("//div[@class='react-datepicker__month-container']");
		
//		List<WebElement> calendarMonthsWebElement = wd.findElements(calendarMonthsLocator); non synchronized
		
		List<WebElement> calendarMonthsList = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(calendarMonthsLocator));
		
//        System.out.println(calendarMonthsList.size());
		
		WebElement monthCalendarWebElement = calendarMonthsList.get(index);// over the 2 visible months i am getting the required first month
		return monthCalendarWebElement;
	}
	
	public static void compareTwoMonthsPrices(String currentMonthLowestPrice, String nextMonthLowestPrice) {
		
		int currentMonthPriceIndex = currentMonthLowestPrice.indexOf("Rs");
		int nextMonthPriceIndex = nextMonthLowestPrice.indexOf("Rs");
		
		String currentPrice = currentMonthLowestPrice.substring(currentMonthPriceIndex+2);
		String nextPrice = nextMonthLowestPrice.substring(nextMonthPriceIndex+2);
		
		int current=Integer.parseInt(currentPrice);
		int next=Integer.parseInt(nextPrice);
		
		if(current<next) {
			System.out.println("The lowest price for two months:  is currentmonth"+current);
		}else if(current==next) {
			System.out.println("The lowest price of two months are same! choose any month"+ current);
		}else {
			System.out.println("The lowest price for two months: nextmonth"+next);
		}
		
	}
}
