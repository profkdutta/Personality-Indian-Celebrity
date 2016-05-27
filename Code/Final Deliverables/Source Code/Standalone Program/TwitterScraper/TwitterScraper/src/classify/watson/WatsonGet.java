package classify.watson;

import java.util.HashMap;
import java.util.LinkedHashMap;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class WatsonGet extends Thread{
	WebDriver driver = new FirefoxDriver();
	HashMap <String,String> insights=new LinkedHashMap<String,String>();
	String text;
	public HashMap<String, String> getParam(){
		return insights;	
	}
	public void run(){
		try{
			int i=1;
			driver.get("https://watson-pi-demo.mybluemix.net/");
			driver.findElement(By.xpath("/html/body/div[2]/div/div[1]/div/div[2]/div[1]/button")).click();
			WebElement element = driver.findElement(By.tagName("textarea"));
			((JavascriptExecutor)driver).executeScript("arguments[0].value = arguments[1];", element, text);
			driver.findElement(By.xpath("/html/body/div[2]/div/div[1]/div/div[2]/div[2]/button")).click();
			WebDriverWait wait = new WebDriverWait(driver,5);
			wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class='col555px well traits']/div[@id='trait-template'][1]/div/div[1]/span")));
			while(i<=52){
				insights.put(driver.findElement(By.xpath("//div[@class='col555px well traits']/div[@id='trait-template']["+i+"]/div/div[1]/span")).getText(), driver.findElement(By.xpath("//div[@class='col555px well traits']/div[@id='trait-template']["+i+"]/div/div[2]/span")).getText());
				i++;
			}	
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			driver.close();
            driver.quit();
		}
	}
	public WatsonGet(String text){
	    this.text=text;
	}

}
