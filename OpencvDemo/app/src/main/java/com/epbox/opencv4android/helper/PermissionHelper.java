package com.epbox.opencv4android.helper;

import android.app.Activity;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import com.epbox.opencv4android.utils.Utils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by dawn on 2018/4/28.
 */

public class PermissionHelper {
    private int mRequestPermissionCode;
    //处理权限
    private List<String> mRequestPermissionList;
    private List<String> mRequestPermissionListBack;
    private OnCallBack onCallBack;
    private boolean isFirst = false;

    public PermissionHelper(OnCallBack onCallBack) {
        this.onCallBack = onCallBack;
    }

    /**
     * 在activity中请求权限
     *
     * @param activity
     * @param requestCode
     * @param permissions
     * @return true：已经获取了所有请求的权限 false：还没有完全获取权限
     */
    public boolean requestPermissions(Activity activity, int requestCode, String... permissions) {
        if (activity == null) return false;
        if (permissions != null && permissions.length > 0) {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mRequestPermissionCode = requestCode;

                if (mRequestPermissionList == null) {
                    mRequestPermissionList = new ArrayList<>();
                } else {
                    mRequestPermissionList.clear();
                }

                for (String permission : permissions) {
                    if (hashPermission(activity, permission)) {
                        continue;
                    }
                    mRequestPermissionList.add(permission);
                }

                if (mRequestPermissionList.size() == 0) {
                    return true;
                } else {
                    mRequestPermissionListBack = mRequestPermissionList;
                    ActivityCompat.requestPermissions(activity, mRequestPermissionList.toArray(new String[mRequestPermissionList.size()]), mRequestPermissionCode);
                    return false;
                }
            } else {
                return true;
            }
        }
        return true;
    }

    public void setIsFirst(boolean isFirst){
        this.isFirst = isFirst;
    }

    /**
     * 在Fragment请求权限
     *
     * @param fragment
     * @param requestCode
     * @param permissions
     * @return true：已经获取了所有请求的权限 false：还没有完全获取权限
     */
    public boolean requestPermissionFragment(Fragment fragment, int requestCode, String... permissions) {
        if (null == fragment) return false;
        if (permissions != null && permissions.length > 0) {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mRequestPermissionCode = requestCode;

                if (mRequestPermissionList == null)
                    mRequestPermissionList = new ArrayList<>();
                else
                    mRequestPermissionList.clear();

                for (String permission : permissions) {
                    if (hashPermission(fragment.getContext(), permission)) continue;
                    mRequestPermissionList.add(permission);
                }

                if (mRequestPermissionList.size() == 0) {
                    return true;
                } else {
                    fragment.requestPermissions(
                            mRequestPermissionList.toArray(new String[mRequestPermissionList.size()]),
                            mRequestPermissionCode);
                    return false;
                }
            } else {
                return true;
            }
        }
        return true;
    }

    public void onRequestPermissionsResult(Activity activity, int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == mRequestPermissionCode) {

            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED
                        && !Utils.listEmpty(mRequestPermissionList)
                        && !TextUtils.isEmpty(permissions[i])
                        && mRequestPermissionList.contains(permissions[i])) {
                    mRequestPermissionList.remove(permissions[i]);
                }
            }

            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED
                        && !Utils.listEmpty(mRequestPermissionList)
                        && !TextUtils.isEmpty(permissions[i])
                        && mRequestPermissionList.contains(permissions[i])) {
                    mRequestPermissionList.remove(permissions[i]);
                }
            }

            String[] permissionArray = mRequestPermissionList.toArray(new String[mRequestPermissionList.size()]);

            if (mRequestPermissionList.size() == 0) {
                if (onCallBack != null) {
                    if (checkPermissionForXiaomi(activity) && !isFirst) { //判断小米权限
                        onCallBack.requestPermissionFail(requestCode, permissionArray);
                    }else {
                        onCallBack.requestPermissionSuccess(requestCode, permissionArray);
                    }
                }
            } else {
                if (onCallBack != null)
                    onCallBack.requestPermissionFail(requestCode, permissionArray);
            }
        }
    }

    public void requestSetPermission(Activity activity, int REQUEST_PERMISSION_SETTING) {
//        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
//        intent.setData(Uri.parse("package:" +activity.getPackageName()));
//        activity.startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
//        ACTION_APPLICATION_DETAILS_SETTINGS

        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + activity.getPackageName()));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivityForResult(intent, REQUEST_PERMISSION_SETTING);

//        try {
//            Intent localIntent = new Intent("miui.intent.action.APP_PERM_EDITOR");
//            localIntent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
//            localIntent.putExtra("extra_pkgname", getPackageName());
//            activity.startActivityForResult(localIntent, REQUEST_PERMISSION_SETTING);
//        } catch (ActivityNotFoundException localActivityNotFoundException) {
//            Intent intent1 = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//            Uri uri = Uri.fromParts("package", getPackageName(), null);
//            intent1.setData(uri);
//            activity.startActivityForResult(intent1, REQUEST_PERMISSION_SETTING);
//        }
    }

    private boolean hashPermission(Context context, String permission) {
        return context != null && ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    public void removeCallback() {
        onCallBack = null;
    }

    public interface OnCallBack {
        void requestPermissionSuccess(int requestPermissionCode, String... permissions);

        void requestPermissionFail(int requestPermissionCode, String... permissions);
    }

    private boolean checkPermissionForXiaomi(Activity activity) {
//        PermissionChecker.checkPermission(activity.getApplicationContext(), permissions[0], android.os.Process.myPid(), android.os.Process.myUid(), getPackageName());
        AppOpsManager manager = (AppOpsManager) activity.getSystemService(Context.APP_OPS_SERVICE);
        try {
            Method method = manager.getClass().getDeclaredMethod("checkOp", int.class, int.class, String.class);
            int property = (Integer) method.invoke(manager, 26, Binder.getCallingUid(),activity.getPackageName());
            if (AppOpsManager.MODE_ALLOWED == property) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
