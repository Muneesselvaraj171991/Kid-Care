package com.child.parent.kidcare.views.applist;

import android.content.Intent;
import android.graphics.drawable.Drawable;

public class AppInformation {
    private String appName;
    private String packageName;
    private Drawable icon;
    private Intent mintent;
    boolean isFlipped;

    public AppInformation(String appName, String packageName, Drawable icon, Intent mintent,boolean isFlipped) {
        this.appName = appName;
        this.packageName = packageName;
        this.icon = icon;
        this.mintent = mintent;
        this.isFlipped = isFlipped;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
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