package classify.main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import com.watson.MainApp;

import classify.uclassify.Uclassify;
import classify.watson.WatsonGet;

public class Controller {
	
	
	public static List<String> twitterHandleList;
	
	public String getText(String thand){
		BufferedReader br;
		String text;
		try{
			br=new BufferedReader(new FileReader("Twitter Clean Text/"+thand+" RelevantTweet.txt"));
			text=br.readLine();
			br.close();
			return text;
			}catch(Exception e){
				e.printStackTrace();
			}
		return "";
	}
	public void startClassify(){		
		System.out.println("Classification Started...");
		String text;
		String str;
		int i=0;
		for(String thand:twitterHandleList){
			text=getText(thand);
			try{
				Uclassify uc=new Uclassify(text);
				WatsonGet wg=new WatsonGet(text);
				uc.start();
				wg.start();
				uc.join();
				wg.join();
				HashMap<String,String> ucres=new LinkedHashMap<String,String>();
				HashMap<String,String> wcres=new LinkedHashMap<String,String>();
				ucres=uc.getParam();
				wcres=wg.getParam();
				String path="Reports/Report "+thand+".txt";
				BufferedWriter bw=new BufferedWriter(new FileWriter(path));
				bw.write("Text\n======\n"+text+"\n\nUclassify Parameters\n====================\n");
				StringBuilder sb=new StringBuilder();
				sb.append("insert into PERSONALITY_UCLASS(ID,T_HANDLE,T_NAME,CATEGORY,"
						+ "MB_JUD_THINKING,MB_JUD_FEELING,MB_ATT_EXTRAVERSION,MB_ATT_INTROVERSION,"
						+ "MB_LIFE_JUDGING,MB_LIFE_PERCEIVING,MB_PERC_SENSING,MB_PERC_INTUITION) "
						+ " values(NULL,'"+thand+"','"+MainApp.thandleNames.get(i)+"','"+MainApp.cat+"',");
				Set<String> ucresset = ucres.keySet();
				double num;
			    Iterator<String> itr = ucresset.iterator();
				while (itr.hasNext()) { 
					str = itr.next();
			    	num=Math.round(Double.parseDouble(ucres.get(str))*100);
			    	sb.append(num+",");
			    	bw.write(str+" - "+num+"%\n");
			    }
				sb.deleteCharAt(sb.length()-1);
				sb.append(")");
				StoreData(sb.toString());
				sb.setLength(0);
				sb.append("insert into PERSONALITY_WATSON(ID,T_HANDLE,T_NAME,CATEGORY,OPENNESS,ADVENTUROUSNESS,"
			    		+ "ARTISTIC_INTERESTS,EMOTIONALITY,IMAGINATION,INTELLECT,AUTHORITY_CHALLENGING,"
			    		+ "CONSCIENTIOUSNESS,ACHIEVEMENT_STRIVING,CAUTIOUSNESS,DUTIFULNESS,ORDERLINESS,"
			    		+ "SELF_DISCIPLINE,SELF_EFFICACY,EXTRAVERSION,ACTIVITY_LEVEL,ASSERTIVENESS,"
			    		+ "CHEERFULNESS,EXCITEMENT_SEEKING,OUTGOING,GREGARIOUSNESS,AGREEABLENESS,ALTRUISM,"
			    		+ "COOPERATION,MODESTY,UNCOMPROMISING,SYMPATHY,TRUST,EMOTIONAL_RANGE,FIERY,PRONE_TO_WORRY,"
			    		+ "MELANCHOLY,IMMODERATION,SELF_CONSCIOUSNESS,SUSCEPTIBLE_TO_STRESS,CHALLENGE,CLOSENESS,CURIOSITY,"
			    		+ "EXCITEMENT,HARMONY,IDEAL,LIBERTY,LOVE,PRACTICALITY,SELF_EXPRESSION,STABILITY,STRUCTURE,CONSERVATION,"
			    		+ "OPENNESS_TO_CHANGE,HEDONISM,SELF_ENHANCEMENT,SELF_TRANSCENDENCE) values(NULL,'"+thand+"','"+MainApp.thandleNames.get(i)+"','"+MainApp.cat+"',");
			    bw.write("\nWatson Parameters\n==================\n");
			    Set<String> wcresset = wcres.keySet();
			    itr = wcresset.iterator();
			    while (itr.hasNext()) { 
			    	str = itr.next();
			    	sb.append(Integer.parseInt(wcres.get(str).replace("%",""))+",");
			    	bw.write(str+" - "+wcres.get(str)+"\n");
			    }
			    sb.deleteCharAt(sb.length()-1);
				sb.append(")");
				StoreData(sb.toString());
				bw.flush();
				bw.close();
			}catch(Exception e){
				e.printStackTrace();
		}
		System.out.println("Classification Completed for "+thand+"- Please Check the Report in Reports Folder");
		i++;
		}
		System.out.println("Whole Process Completed");
	}
	public static boolean StoreData(String query){
		Connection conn;
		try {
			File file = new File("DB File/TWITTER_PERSONALITY.accdb");
		      conn = DriverManager.getConnection("jdbc:ucanaccess://"+file.getAbsolutePath());
		      Statement sta = conn.createStatement(); 
		      sta.execute(query);
		      
		      
		      return true;
		    } catch (Exception e) {
		      e.printStackTrace();
		      return false;
		    }
	}
}
