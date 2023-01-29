package com.child.parent.kidcare.views.onboarding;

import android.app.AppOpsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.child.parent.kidcare.R;
import com.child.parent.kidcare.customview.DotIndicator;
import com.child.parent.kidcare.views.UserProfile.UserProfileActivity;

import java.util.List;
import java.util.Locale;

public class OnboardingActivity extends FragmentActivity {
    private static final int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 100;
    private static final int ACTION_MANAGE_USAGE_PERMISSION_REQUEST_CODE = 101;
    private static final int ACTION_AUTO_START_REQUEST_CODE = 102;
    private ViewPager pager;
    private DotIndicator indicator;
    private TextView next;
    private ImageView imgArrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_onboarding);

        pager = findViewById(R.id.pager);
        indicator = findViewById(R.id.indicator);

        next = findViewById(R.id.pager_settings);
        imgArrow = findViewById(R.id.pager_imgArrow);


        FragmentStatePagerAdapter adapter = new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0:
                        return new OnboardingFragment1();
                    case 1:
                        return new OnboardingFragment2();
                    case 2:
                        return new OnboardingFragment3();
                    default:
                        return null;
                }
            }

            @Override
            public int getCount() {
                return isCustomOS() ? 3 : 2;
            }
        };

        pager.setAdapter(adapter);

        indicator.setViewPager(pager);

        if (!isOverlayPermissionGranded()) {
            pager.setCurrentItem(0);
        } else if (!isAccessGranted()) {
            pager.setCurrentItem(1);
        } else if (isCustomOS()) {
            pager.setCurrentItem(2);
        } else {
            startUserProfileActivity();
        }
        //Disable viewpager swipe!
        pager.setOnTouchListener((v, event) -> true);


        next.setOnClickListener(v -> {
            switch (pager.getCurrentItem()) {
                case 0:
                    launchOverlaySettings();
                    break;
                case 1:
                    launchUsagePermissionSettings();
                    break;

                case 2:
                    autoStart();
                    break;
            }
        });
        imgArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                next.callOnClick();
            }
        });
    }


    private void startUserProfileActivity() {

        Intent main = new Intent(this, UserProfileActivity.class);
        startActivity(main);

        finish();
    }

    //Show dialog if usage access permission not given
    public void launchUsagePermissionSettings() {
        //Usage Permission
        Intent intent = new Intent(android.provider.Settings.ACTION_USAGE_ACCESS_SETTINGS);
        startActivityForResult(intent, ACTION_MANAGE_USAGE_PERMISSION_REQUEST_CODE);

    }

    boolean isOverlayPermissionGranded() {

        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || (Settings.canDrawOverlays(this));
    }

    public void launchOverlaySettings() {

        if ("xiaomi".equals(Build.MANUFACTURER.toLowerCase(Locale.ROOT))) {
            Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
            intent.setClassName("com.miui.securitycenter",
                    "com.miui.permcenter.permissions.PermissionsEditorActivity");
            intent.putExtra("extra_pkgname", getPackageName());
            startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);

        } else {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
        }
    }

    private void autoStart() {
        try {
            Intent intent = new Intent();
            String manufacturer = android.os.Build.MANUFACTURER;
            if ("xiaomi".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
            } else if ("oppo".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity"));
            } else if ("vivo".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"));
            } else if ("Letv".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity"));
            } else if ("Honor".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity"));
            }

            List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            if (list.size() > 0) {
                startActivityForResult(intent, ACTION_AUTO_START_REQUEST_CODE);
            }
        } catch (Exception e) {
            Log.e("exc", String.valueOf(e));
        }

    }

    private boolean isCustomOS() {
        String manufacturer = android.os.Build.MANUFACTURER;
        return "xiaomi".equalsIgnoreCase(manufacturer) || "oppo".equalsIgnoreCase(manufacturer) || "vivo".equalsIgnoreCase(manufacturer) || "Letv".equalsIgnoreCase(manufacturer) || "Honor".equalsIgnoreCase(manufacturer);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            case ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE:
                if (isOverlayPermissionGranded()) {
                    pager.setCurrentItem(1);
                }
                break;

            case ACTION_MANAGE_USAGE_PERMISSION_REQUEST_CODE:

                if (isAccessGranted()) {
                    if(isCustomOS()) {
                        pager.setCurrentItem(2);
                    } else {
                        startUserProfileActivity();
                    }

                }
                break;

            case ACTION_AUTO_START_REQUEST_CODE:
                startUserProfileActivity();
                break;

        }


    }

    public boolean isAccessGranted() {
        try {
            PackageManager packageManager = getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
            int mode;
            assert appOpsManager != null;
            mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                    applicationInfo.uid, applicationInfo.packageName);
            return (mode == AppOpsManager.MODE_ALLOWED);

        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}
