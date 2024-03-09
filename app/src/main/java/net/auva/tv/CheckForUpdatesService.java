/*
package net.auva.tv;

import android.content.SharedPreferences;

import androidx.fragment.app.FragmentActivity;

import com.mobapphome.androidappupdater.tools.AAUpdaterController;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class CheckForUpdatesService extends FragmentActivity {
    public static void checkForUpdate(FragmentActivity context, boolean askForUpdate){
        SharedPreferences sh = context.getSharedPreferences("laterBtn", MODE_PRIVATE);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        Calendar cal = Calendar.getInstance();
        String currentDate =dateFormat.format(cal.getTime());
        String lastDate = sh.getString("date", "date");
        try {
            Date date1 = dateFormat.parse(currentDate);
            Date date2 = dateFormat.parse(lastDate);
            long diff = date2.getTime() - date1.getTime();
            Long days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
            if(days < 0 || askForUpdate){
                AAUpdaterController.init(context, false);
            }
            System.out.println ("Days: " + TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS));
        } catch (ParseException e) {
            e.printStackTrace();
            AAUpdaterController.init(context, false);
        }
    }
}
*/
