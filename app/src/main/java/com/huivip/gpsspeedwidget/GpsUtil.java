package com.huivip.gpsspeedwidget;

import android.content.Context;
import android.location.*;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.enums.AimLessMode;
import com.amap.api.navi.enums.BroadcastMode;
import com.amap.api.navi.model.*;
import com.autonavi.tbt.TrafficFacilityInfo;
import com.huivip.gpsspeedwidget.utils.PrefUtils;
import com.huivip.gpsspeedwidget.utils.TTSUtil;

import java.text.NumberFormat;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author sunlaihui
 */
public class GpsUtil {
    Context context;
    private String latitude;
    private String longitude;
    private float bearing;
    private String velocitaString=null;
    private Integer velocitaNumber;
    Double speed=0D;
    Integer mphSpeed=Integer.valueOf(0);
    Integer kmhSpeed=Integer.valueOf(0);
    Integer limitSpeed=Integer.valueOf(0);
    String mphSpeedStr="0";
    String kmhSpeedStr="0";
    String velocita_prec = "ciao";
    Integer c = Integer.valueOf(0);
    String providerId = "gps";
    boolean gpsEnabled=false;
    boolean gpsLocationStarted=false;
    boolean gpsLocationChanged=false;
    boolean serviceStarted=false;
    TimerTask locationScanTask;
    Timer locationTimer;
    AMapNavi aMapNavi;
    TTSUtil ttsUtil;
    final Handler locationHandler = new Handler();
    LocationListener locationListener=new LocationListener() {
        @Override
        public void onLocationChanged(Location paramAnonymousLocation) {
            GpsUtil.this.updateLocationData(paramAnonymousLocation);
        }

        @Override
        public void onStatusChanged(String s, int status, Bundle bundle) {
            if (status == LocationProvider.TEMPORARILY_UNAVAILABLE || status == LocationProvider.OUT_OF_SERVICE) {
                GpsUtil.this.updateLocationData(null);
            }
        }

        @Override
        public void onProviderDisabled(String paramAnonymousString) {
            GpsUtil.this.updateLocationData(null);
        }

        @Override
        public void onProviderEnabled(String paramAnonymousString) {
        }

    };
    private static GpsUtil instance=new GpsUtil();

    public static GpsUtil getInstance(Context context){
        instance.setContext(context);
        instance.initInstance();
        return instance;
    }
    public void startLocationService(){
        if(serviceStarted) return;
        this.locationTimer = new Timer();
        this.locationScanTask = new TimerTask()
        {
            @Override
            public void run()
            {
                GpsUtil.this.locationHandler.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        checkLocationData();
                    }
                });
            }
        };
        this.locationTimer.schedule(this.locationScanTask, 0L, 100L);
        aMapNavi.startAimlessMode(AimLessMode.CAMERA_AND_SPECIALROAD_DETECTED);
        serviceStarted=true;
    }
    public void stopLocationService(){
        if(serviceStarted) {
            this.locationTimer.cancel();
            this.locationTimer.purge();
            serviceStarted=false;
            if(aMapNavi!=null){
                aMapNavi.stopAimlessMode();
            }
        }
    }
    public float getBearing() {
        return bearing;
    }

    public Double getSpeed() {
        return speed;
    }

    private void updateLocationData(Location paramLocation)
    {
        if(paramLocation!=null ) {
            this.latitude = Double.toString(paramLocation.getLatitude());
            this.longitude=Double.toString(paramLocation.getLongitude());
            this.velocitaString = null;
            if (paramLocation.hasSpeed()) {
                this.velocitaNumber = Integer.valueOf((int) paramLocation.getSpeed());
                NumberFormat localNumberFormat = NumberFormat.getNumberInstance();
                localNumberFormat.setMaximumFractionDigits(1);
                this.speed = Double.valueOf(paramLocation.getSpeed());
                this.velocitaString = localNumberFormat.format(this.speed);
                this.bearing=paramLocation.getBearing();
            }
        }
        else {

            this.velocitaString=null;
            this.velocita_prec="ciao";
            this.latitude =null;
        }
    }
    void checkLocationData() {
        try {
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            if (locationManager.getProvider(this.providerId) == null) {
                return;
            }
            gpsEnabled = locationManager.isProviderEnabled(this.providerId);
            Location localLocation = locationManager.getLastKnownLocation("gps");
            if (localLocation != null) {
                updateLocationData(localLocation);
            }
            locationManager.requestLocationUpdates(this.providerId, 1L, 1.0F, this.locationListener);
        }catch (SecurityException e){
            Toast.makeText(context, "GPS widget Need Location Permissions!", Toast.LENGTH_SHORT).show();
        }

        if ((this.velocitaString != null) && (gpsEnabled)) {
            if (!this.velocitaString.equals(this.velocita_prec)) {
                computeAndShowData();
                gpsLocationChanged=true;
                this.velocita_prec = this.velocitaString;
            }
            else{
                gpsLocationChanged=false;
            }
        }
    }
    void computeAndShowData() {
        NumberFormat localNumberFormat = NumberFormat.getNumberInstance();
        localNumberFormat.setMaximumFractionDigits(1);
        mphSpeed = (int)(this.velocitaNumber.intValue() * 3.6D / 1.609344D);
        kmhSpeed=(int)(this.speed.doubleValue() * 3.6D);
        mphSpeedStr=localNumberFormat.format(this.speed.doubleValue() * 3.6D / 1.609344D);
        kmhSpeedStr=localNumberFormat.format(this.speed.doubleValue() * 3.6D);
        Log.d("GPS",kmhSpeedStr);
    }

    public Integer getMphSpeed() {
        return mphSpeed;
    }

    public String getMphSpeedStr() {
        return mphSpeedStr;
    }

    public String getKmhSpeedStr() {
        return kmhSpeedStr;
    }

    public Integer getKmhSpeed() {
        return kmhSpeed;
    }
    public Integer getLimitSpeed(){
        return limitSpeed;
    }
    private void setContext(Context context) {
        this.context = context;
    }
    private void initInstance(){
        aMapNavi=AMapNavi.getInstance(context);
        aMapNavi.setBroadcastMode(BroadcastMode.CONCISE);
        aMapNavi.addAMapNaviListener(new NaviListenerImpl());
        ttsUtil=TTSUtil.getInstance(context);
    }
    public boolean isGpsEnabled() {
        return gpsEnabled;
    }

    public boolean isGpsLocationStarted() {
        gpsLocationStarted=this.velocitaString!=null ;
        return gpsLocationStarted;
    }

    public boolean isGpsLocationChanged() {
        return gpsLocationChanged;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }
    private class NaviListenerImpl implements AMapNaviListener {
        @Override
        public void onInitNaviFailure() {
            Toast.makeText(context,"巡航初始化失败！",Toast.LENGTH_SHORT);
        }

        @Override
        public void onInitNaviSuccess() {

        }

        @Override
        public void onStartNavi(int i) {
            Toast.makeText(context,"智能巡航开始",Toast.LENGTH_SHORT);
        }

        @Override
        public void onTrafficStatusUpdate() {

        }

        @Override
        public void onLocationChange(AMapNaviLocation aMapNaviLocation) {
            // mSpeedometerText.setText(Float.toString(aMapNaviLocation.getSpeed()));
        }

        @Override
        public void onGetNavigationText(int i, String s) {

        }

        @Override
        public void onGetNavigationText(String s) {
            if(PrefUtils.isEnableAudioService(context)) {
                ttsUtil.speak(s);
            }
        }

        @Override
        public void onEndEmulatorNavi() {

        }

        @Override
        public void onArriveDestination() {

        }

        @Override
        public void onCalculateRouteFailure(int i) {

        }

        @Override
        public void onReCalculateRouteForYaw() {

        }

        @Override
        public void onReCalculateRouteForTrafficJam() {

        }

        @Override
        public void onArrivedWayPoint(int i) {

        }

        @Override
        public void onGpsOpenStatus(boolean b) {

        }

        @Override
        public void onNaviInfoUpdate(NaviInfo naviInfo) {

        }

        @Override
        public void onNaviInfoUpdated(AMapNaviInfo aMapNaviInfo) {

        }

        @Override
        public void updateCameraInfo(AMapNaviCameraInfo[] aMapNaviCameraInfos) {

        }

        @Override
        public void updateIntervalCameraInfo(AMapNaviCameraInfo aMapNaviCameraInfo, AMapNaviCameraInfo aMapNaviCameraInfo1, int i) {

        }

        @Override
        public void onServiceAreaUpdate(AMapServiceAreaInfo[] aMapServiceAreaInfos) {

        }

        @Override
        public void showCross(AMapNaviCross aMapNaviCross) {

        }

        @Override
        public void hideCross() {

        }

        @Override
        public void showModeCross(AMapModelCross aMapModelCross) {

        }

        @Override
        public void hideModeCross() {

        }

        @Override
        public void showLaneInfo(AMapLaneInfo[] aMapLaneInfos, byte[] bytes, byte[] bytes1) {

        }

        @Override
        public void showLaneInfo(AMapLaneInfo aMapLaneInfo) {

        }

        @Override
        public void hideLaneInfo() {

        }

        @Override
        public void onCalculateRouteSuccess(int[] ints) {

        }

        @Override
        public void notifyParallelRoad(int i) {

        }

        @Override
        public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo aMapNaviTrafficFacilityInfo) {

        }

        @Override
        public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo[] aMapNaviTrafficFacilityInfos) {
            for (AMapNaviTrafficFacilityInfo info :
                    aMapNaviTrafficFacilityInfos) {
                if(info.getBroadcastType() == 102 || info.getBroadcastType() == 4){
                    limitSpeed=info.getLimitSpeed();
                }

            }
        }

        @Override
        public void OnUpdateTrafficFacility(TrafficFacilityInfo trafficFacilityInfo) {

        }

        @Override
        public void updateAimlessModeStatistics(AimLessModeStat aimLessModeStat) {

        }

        @Override
        public void updateAimlessModeCongestionInfo(AimLessModeCongestionInfo aimLessModeCongestionInfo) {

        }

        @Override
        public void onPlayRing(int i) {

        }
    }
}
