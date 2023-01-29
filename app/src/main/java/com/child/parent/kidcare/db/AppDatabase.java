package com.child.parent.kidcare.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Packages.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract PackageDAO pkgDao();


}
