package com.child.parent.kidcare;

import android.app.Application;
import android.content.Context;
import androidx.room.Room;
import com.child.parent.kidcare.db.AppDatabase;
import com.child.parent.kidcare.db.PackageDAO;
import com.child.parent.kidcare.utils.TimePreference;
import com.child.parent.kidcare.utils.Util;

public class MyApplication extends Application {

    public static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        TimePreference.init(mContext);
        Util.overrideFont(getApplicationContext(), "SERIF", "fonts/Roboto_medium.ttf");
    }

    public static PackageDAO getPkgDAO() {
       final AppDatabase db = Room.databaseBuilder(mContext,
                AppDatabase.class, PackageDAO.TABLE_NAME).build();

       return db.pkgDao();
    }

}
