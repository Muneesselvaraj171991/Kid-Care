package com.child.parent.kidcare.views.applist;

import android.app.Application;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.child.parent.kidcare.MyApplication;
import com.child.parent.kidcare.db.Packages;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class AppListViewModel extends AndroidViewModel {
    ArrayList<Packages> mAppList;
    private MutableLiveData<List<Packages>> mMutableLiveData = new MutableLiveData<>();
    public AppListViewModel(@NonNull Application application) {
        super(application);
        mAppList = new ArrayList<>();
    }

    public LiveData<List<Packages>> getList() {
        return mMutableLiveData;
    }



    public void getAppList() {

        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                if (mAppList.isEmpty()) {


                    List<Packages> dbPackageList = MyApplication.getPkgDAO().getPackageList();


                    final PackageManager pm = getApplication().getPackageManager();
                    //get a list of installed apps.
                    List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);




                    for (int index = 0; index < packages.size(); index++) {
                        ApplicationInfo packageInfo = packages.get(index);
                        Intent intent = pm.getLaunchIntentForPackage(packageInfo.packageName);
                        if (intent != null) {
                            Packages newInfo = new Packages();
                            newInfo.setAppName(packageInfo.loadLabel(pm).toString());
                            newInfo.setPkgName(packageInfo.packageName);
                            newInfo.setMintent(intent);
                            newInfo.setIcon(packageInfo.loadIcon(pm));
                            mAppList.add(newInfo);

                            boolean isLocked = dbPackageList.stream().filter(o -> o.getPkgName().equals(packageInfo.packageName)).anyMatch(Packages::isLocked);

                            if (isLocked) {
                                newInfo.setLockStatus(isLocked);

                            } else {
                                MyApplication.getPkgDAO().insertPackage(newInfo);
                            }


                        }
                    }
                }
                mMutableLiveData.postValue(mAppList);
            }
        });
    }

    public void updateDB(int position) {
        Executors.newSingleThreadExecutor().execute(() -> {
            Packages pkg = mAppList.get(position);
            pkg.setLockStatus(!pkg.isLocked());
            MyApplication.getPkgDAO().updateRecAlarmStatus(pkg.getPkgName(), pkg.isLocked());

        });
    }

}
