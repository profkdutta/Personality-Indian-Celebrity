package com.watson;

import classify.main.*;
import java.util.List;

import com.cybozu.labs.langdetect.DetectorFactory;

import twitter4j.Status;

public class MainApp {
	
	public static int choice;
	public static String twitterHandle;
	public static String conkey="4Rekbz8TM6HnrJw9Diy2ZNkof";
	public static String consec="5TFLwVHvCsqwD1bt7oqOMHq3AaiPa9oFxEn4xd7liN1WvNGSkK";
	public static String tokkey="582705387-AmBTn8xxjf65lT9V37NFqcCoy456iIJsUokyNfy4";
	public static String toksec="yzAJzZGBJWMOGAzQKgcTShNvVfklIgMs9JP5HXSirXjlM";
	public static List<String> twitterHandleList;
	public static List<String> thandleNames;
	public static List<Status> userTweets;
	public static String cat;
	
	public static void main(String[] args) throws Exception {
    	DetectorFactory.loadProfile(System.getProperty("user.dir")+"/profiles/");
		Utility.displayMainMenu();
		Utility.intializeHandles();
		Utility.extractTwitterFeed(conkey,consec,tokkey,toksec);
		Controller.twitterHandleList=MainApp.twitterHandleList;
		Controller classify=new Controller();
		classify.startClassify();
	}

}
