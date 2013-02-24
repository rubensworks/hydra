import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.json.*;

import org.apache.commons.io.IOUtils;

/**
 * Parses the output of the sed-scripts to a readable json-file.
 * Require 1 argument: the date (yyyymmdd) of the first day of the week.
 * @author Wouter Pinnoo <pinnoo.wouter@gmail.com>
 */
public class Parser {

    // args[0] = date of the first day of the week (yyyymmdd)
    public static void main(String[] args) {
        if (args[0] == null || args[0].length() != 8) {
            return;
        }
        Parser p = new Parser();

        List<List<String>> meat = p.parse(new File("meat.txt"));
        List<List<String>> soup = p.parse(new File("soup.txt"));
        List<List<String>> vegetables = p.parse(new File("vegetables.txt"));

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(format.parse(args[0]));
        } catch (ParseException ex) {
            System.err.println("Date parsing error.");
            return;
        }

        JSONObject main = new JSONObject();

        for (int day = 0; day < 5; day++) {
            JSONObject curday = new JSONObject();
            p.addMeat(curday, meat.get(day));
            p.addSoup(curday, soup.get(day));
            p.addVegetables(curday, vegetables.get(day));
            curday.put("open", true);
            main.put(p.convertDate(cal), curday);
            cal.add(Calendar.DATE, 1);
        }

        System.out.println(main.toString(2));
    }

    private String convertDate(Calendar cal) {
        String str = cal.get(Calendar.YEAR) + "";
        str += cal.get(Calendar.MONTH) < 9 ? "-0" + (cal.get(Calendar.MONTH) + 1) : "-" + (cal.get(Calendar.MONTH) + 1);
        str += cal.get(Calendar.DAY_OF_MONTH) < 10 ? "-0" + cal.get(Calendar.DAY_OF_MONTH) : "-" + cal.get(Calendar.DAY_OF_MONTH);
        return str;
    }

    private void addSoup(JSONObject obj, List<String> soup) {
        String cursoup = soup.get(0);
        JSONObject soupobj = new JSONObject();
        soupobj.put("name", cursoup.substring(7));
        soupobj.put("price", cursoup.substring(0, 4));
        obj.put("soup", soupobj);
    }

    private void addVegetables(JSONObject obj, List<String> vegetables) {
        Iterator<String> it = vegetables.iterator();
        while (it.hasNext()) {
            obj.accumulate("vegetables", it.next());
        }
    }

    private void addMeat(JSONObject obj, List<String> meat) {
        Iterator<String> it = meat.iterator();
        while (it.hasNext()) {
            String curmeat = it.next();
            JSONObject curmeatobj = new JSONObject();
            int offset = 0;
            if (curmeat.startsWith("R ")) {
                curmeatobj.put("recommended", true);
                offset = 2;
            } else {
                curmeatobj.put("recommended", false);
            }
            curmeatobj.put("price", "\u20ac " + curmeat.substring(offset, 4 + offset));
            curmeatobj.put("name", curmeat.substring(7 + offset));
            obj.accumulate("meat", curmeatobj);
        }
    }

    public boolean isWeekday(String str) {
        return str.trim().equals("Maandag")
                || str.trim().equals("Dinsdag")
                || str.trim().equals("Woensdag")
                || str.trim().equals("Donderdag")
                || str.trim().equals("Vrijdag");
    }

    private List<List<String>> parse(File file) {
        List<List<String>> content = new ArrayList<List<String>>();
        try {
            Scanner sc = new Scanner(file);
            int day = -1;
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                if (isWeekday(line)) {
                    content.add(new ArrayList<String>());
                    day++;
                } else {
                    content.get(day).add(line);
                }
            }
            sc.close();
        } catch (FileNotFoundException ex) {
            System.err.println(ex);
        }
        return content;
    }
}
