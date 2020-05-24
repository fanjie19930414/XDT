package com.kapok.apps.maple.xdt.utils;

import android.Manifest;
import android.content.Context;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;


/**
 * 定位服务
 */
public class LocationServer {

    /**
     * 我的位置
     */
    private static BDLocation myLocation;

    public interface LocationResultListener {
        void locationFailure(String msg);

        /**
         * 定位成功回调
         *
         * @param location 位置
         */
        void locationSuccess(BDLocation location);
    }

    /**
     * 通用定位函数(一次定位)
     *
     * @param listener 返回监听函数
     */
    public static void getGps(final Context context, final LocationResultListener listener) {
        getGps(context, LocationMode.Battery_Saving, listener);
    }

    /**
     * 通用定位函数(持续定位)
     *
     * @param locationMode 定位精度
     * @param listener     返回监听函数
     * @return
     */
    public static void getGps(final Context context, final LocationMode locationMode, final LocationResultListener listener) {
        PermissionUtils.checkPermission(context, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, new PermissionUtils.CheckResultListener() {
            @Override
            public void checkSuccess() {
                final LocationClient mLocationClient = new LocationClient(context);
                LocationClientOption option = new LocationClientOption();
                option.setOpenGps(true);// 打开gps
                option.setCoorType("bd09ll"); // 设置坐标类型
                option.setIsNeedAddress(true);
                option.setLocationMode(locationMode);
                mLocationClient.setLocOption(option);

                mLocationClient.registerLocationListener(new BDAbstractLocationListener() {
                    @Override
                    public void onReceiveLocation(BDLocation bdLocation) {
                        mLocationClient.stop();
                        if (bdLocation == null) { // 定位失败
                            if (listener != null) {
                                listener.locationFailure("定位失败");
                            }
                            return;
                        }
                        myLocation = bdLocation;
                        //执行Success监听
                        if (listener != null) {
                            listener.locationSuccess(bdLocation);
                        }
                    }
                });
                mLocationClient.start();
            }

            @Override
            public void checkFailure(String[] permissions) {
                if (listener != null)
                    listener.locationFailure("获取定位权限失败");
            }
        });
    }

    public static BDLocation getMyLocation() {
        return myLocation;
    }
}
