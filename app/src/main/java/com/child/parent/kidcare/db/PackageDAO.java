package com.child.parent.kidcare.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;
@Dao
public interface PackageDAO {
    String TABLE_NAME = "package";
    @Query("SELECT * FROM "+TABLE_NAME)
    List<Packages> getPackageList();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPackage(Packages order);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllPackage(List<Packages> order);
    @Query("UPDATE package SET lock_status=:lockStatus WHERE pkg_name=:pkg")
    void updateRecAlarmStatus(String pkg, boolean lockStatus);

    @Query("UPDATE package SET daily_lock_status=:dailyLockStatus WHERE pkg_name=:pkg")
    void updateDailyLockStatus(String pkg, boolean dailyLockStatus);

    @Query("SELECT * FROM package WHERE pkg_name=:pkg")
    List<Packages> getPackage(String pkg);



}
