package com.watson;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.LangDetectException;
import com.cybozu.labs.langdetect.Language;

import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.conf.ConfigurationBuilder;

public class Utility {
	
	public static void displayMainMenu(){
		System.out.println("**********************************************");
		System.out.println("**** Welcome to Twitter Extraction module ****");
		System.out.println("**********************************************");
		System.out.println();
		System.out.println("Options");
		System.out.println("\tMode 1: Single Twitter Handle");
		System.out.println("\tMode 2: Twitter Handle List");
		System.out.print("Enter your choice? ");
	}
	
	public static void extractTwitterFeed(String conkey, String consec,String tokkey, String toksec) {
		ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
          .setOAuthConsumerKey(conkey)
          .setOAuthConsumerSecret(consec)
          .setOAuthAccessToken(tokkey)
          .setOAuthAccessTokenSecret(toksec);
        TwitterFactory tf = new TwitterFactory(cb.build());
        Twitter twitter1 = tf.getInstance();
		MainApp.userTweets = new ArrayList<Status>();
		ArrayList<String> temp=new ArrayList<String>();
		MainApp.thandleNames=new ArrayList<String>();
		User user;
		try{
			Iterator<String> twitterHandleIterator = MainApp.twitterHandleList.iterator();
		    while(twitterHandleIterator.hasNext()){
		    	
				 MainApp.twitterHandle= twitterHandleIterator.next();
				 user=twitter1.showUser(MainApp.twitterHandle);
				 MainApp.thandleNames.add(user.getName());
				 temp.add(MainApp.twitterHandle);
				 int page = 1;
		       	 System.out.println("Extracting Data ...");
		    	 while (page<20){
		    		MainApp.userTweets.addAll(twitter1.getUserTimeline(MainApp.twitterHandle,new Paging(page++,200)));
		    	 }
		    	 storeInExcel(MainApp.userTweets); 
		    	 MainApp.userTweets.clear();
			 }
		    }catch(TwitterException t){
		    	temp.remove(temp.size()-1);
		    	if(temp.size()>0){
		    		System.out.println("Twitter Exception Caused - API Limit Exceeded\nTweets only collected for "+temp );
			    	MainApp.twitterHandleList.clear();
			    	MainApp.twitterHandleList.addAll(temp);	
		    	}else{
		    		System.out.println("Twitter Exception Caused - API Limit Exceeded\nNo Tweets Collected");
		    		System.exit(0);
		    	}
		    	
		    }catch(Exception e){
		    	System.out.println(e.getMessage());
		    }
	}	    	
	
	public static void intializeHandles() throws IOException, LangDetectException{
		BufferedReader br;
		MainApp.twitterHandleList = new ArrayList<String>();
		
		br = new BufferedReader(new InputStreamReader(System.in));
		String choice = br.readLine();
		
		if(choice.equals("1")){
			System.out.print("Enter the twitter handle: ");
			MainApp.twitterHandle=br.readLine();
			MainApp.twitterHandleList.add(MainApp.twitterHandle);
			System.out.println("Enter celeb category");
			MainApp.cat=br.readLine();
		}
		else if(choice.equals("2")){
			System.out.println("Enter Twitter Handles Seperated By Space");
			String choices=br.readLine();
			String[] twitterHandles=choices.split(" ");
			Collections.addAll(MainApp.twitterHandleList,twitterHandles);
			System.out.println("Enter celeb category");
			MainApp.cat=br.readLine();
			br.close();
		}
		else{
			System.out.println("Invalid Choice");
			System.exit(0);
		}
		
	}
		
	public static void storeInExcel(List<Status> inputList) throws Exception{
		XSSFWorkbook rawDataFile = new XSSFWorkbook();
		XSSFSheet rawSheet = rawDataFile.createSheet("RawTwitterData");
		int rownum = 0;	
		PrintWriter writer = new PrintWriter("Twitter Clean Text/"+MainApp.twitterHandle+" RelevantTweet.txt", "UTF-8");
        String cleanPost;
		String prevCleanPost="Test";
		for(Status post:inputList){
			if (post.isRetweetedByMe()==false &&  post.isRetweet()==false){				
				Row row = rawSheet.createRow(rownum++);
				Cell cell = row.createCell(0);
				cell.setCellValue(post.getCreatedAt().toString());
				cell = row.createCell(1);
				cell.setCellValue(post.getText().toString());	
				cleanPost = clean(post.getText().toString()).trim();
				if (cleanPost.equals("")){
					cell = row.createCell(2);
					cell.setCellValue("");
					cell = row.createCell(3);
					cell.setCellValue("NA");
					cell = row.createCell(4);
					cell.setCellValue("N");
					
				}
				else{
					cell = row.createCell(2);
					cell.setCellValue(cleanPost);
					cell = row.createCell(3);
					int probability = validateData(cleanPost);
					cell.setCellValue(probability);
					cell = row.createCell(4);
	
					if (probability>=90){
						if(!cleanPost.equals(prevCleanPost)){
							cell.setCellValue("Y");
							cell = row.getCell(2);
							writer.write(cell.getStringCellValue());
							writer.append(" ");
							prevCleanPost=cleanPost;
						}
					}	
					else 
						cell.setCellValue("N");
				}
			}
		} 
		System.out.println(inputList.size() + " tweets extracted.");
		FileOutputStream out = new FileOutputStream(new File("Twitter Raw Data/"+MainApp.twitterHandle+" RawTwitterData.xlsx"));
		rawDataFile.write(out);
		out.close();
		rawDataFile.close();
		writer.close();
		System.out.println("RawTwitterData.xlsx written successfully on disk.");
	}
	
	

	public static String clean(String inputText){
		int wordCount = 0;
		StringTokenizer str = new StringTokenizer(inputText," ");
		StringBuilder sb = new StringBuilder();
		while(str.hasMoreTokens()){
			
			String s = str.nextToken().trim();
				
				if (Pattern.matches(".*[^a-zA-Z].*",s)||s.contains("http") || s.contains("@")||s.contains("#")){
					continue;
				}					
				else{
					if(s.length()>=4){
						s=s.replaceAll("[^a-zA-Z]", " ");
						sb.append(s.trim());
				}
					sb.append(" ");
					wordCount++;		
			}
		}
		if(wordCount>=4)
			{
				return sb.toString();
			}
			else
				return "";
	 }
	
    public static String detect(String text) throws LangDetectException {
    	Detector detector = DetectorFactory.create();
        detector.append(text);
        return detector.detect();
    }
    
    public static ArrayList<Language> detectLangs(String text) throws LangDetectException {
        Detector detector = DetectorFactory.create();
        detector.append(text);
        return detector.getProbabilities();
    }
    
    public static int validateData(String text) throws Exception{
    	if(detect(text).equals("en")){
        	return getProbability(detectLangs(text));
        }
        else 
        	return 0;
        }
       
	public static int getProbability(ArrayList<Language> inputProbability) {
		Integer tempInt = new Integer(0);
    	String tempString ="";
    	ArrayList<Integer> probabilities = new ArrayList<Integer>();
    	
    	Iterator<Language> it = inputProbability.iterator();
    	while(it.hasNext()){
    		 tempString= it.next().toString();
    		if(tempString.contains("en"))
    			tempInt = Integer.parseInt(tempString.substring(6,8));
    			probabilities.add(tempInt);
    	}
    	Collections.sort(probabilities);
    	int maxProbability =probabilities.get(probabilities.size()-1).intValue();
    	return maxProbability;
	}
    
}



