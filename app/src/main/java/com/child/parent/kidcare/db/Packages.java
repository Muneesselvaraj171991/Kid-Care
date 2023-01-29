package com.child.parent.kidcare.db;

import android.content.Intent;
import android.graphics.drawable.Drawable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
@Entity(tableName ="package" )
public class Packages {
    @PrimaryKey(autoGenerate = true)
    public int uid;
    @ColumnInfo(name = "pkg_name")
    private String mPkgName;
    @ColumnInfo(name = "lock_status")
    boolean mLockStatus;

    @ColumnInfo(name="daily_lock_status")
    boolean mDailyLockStatus;

    public String getPkgName() {
        return mPkgName;
    }

    public void setPkgName(String mPkgName) {
        this.mPkgName = mPkgName;
    }

    public boolean isLocked() {
        return mLockStatus;
    }

    public boolean isLockedNow() {
        return  mDailyLockStatus;
    }

    public void setLockStatus(boolean mLockStatus) {
        this.mLockStatus = mLockStatus;
    }
    @Ignore
    private String appName;
    @Ignore
    private Drawable icon;
    @Ignore
    private Intent mintent;
    public boolean isFlipped() {
        return isFlipped;
    }

    public void setFlipped(boolean flipped) {
        isFlipped = flipped;
    }
    @Ignore
    private boolean isFlipped;




    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }



    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public Intent getMintent() {
        return mintent;
    }

    public void setMintent(Intent mintent) {
        this.mintent = mintent;
    }


}
