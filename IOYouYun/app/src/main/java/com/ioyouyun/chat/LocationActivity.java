package com.ioyouyun.chat;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.animation.Animation;
import com.amap.api.maps.model.animation.TranslateAnimation;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.ioyouyun.R;
import com.ioyouyun.chat.model.POIInfo;
import com.ioyouyun.utils.FunctionUtil;
import com.ioyouyun.utils.Logger;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by 卫彪 on 2016/10/26.
 */

public class LocationActivity extends AppCompatActivity implements LocationSource, AMapLocationListener,
        AMap.OnCameraChangeListener, GeocodeSearch.OnGeocodeSearchListener, AMap.OnMapLoadedListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.map_view)
    MapView mapView;
    @BindView(R.id.tv_address)
    TextView tvAddress;
    @BindView(R.id.btn_right)
    TextView btnRight;

    private AMap aMap;
    private UiSettings settings;
    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = null;
    private GeocodeSearch geocodeSearch;
    private Marker centerMarker;

    private POIInfo poiInfo;
    private int flag = 0; // 1:发送我的位置

    private boolean isFirst = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        ButterKnife.bind(this);
        setToolBar();
        if(getIntentExtra() == 1)
            btnRight.setVisibility(View.VISIBLE);
        initData(savedInstanceState);
    }

    private int getIntentExtra(){
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if(bundle != null) {
            poiInfo = bundle.getParcelable("key_entity");
            flag = bundle.getInt("key_flag", 0);
            if(flag != 1)
                isFirst = true;
        }
        if(poiInfo == null)
            poiInfo = new POIInfo();
        return flag;
    }

    public void initLocation() {
        locationClient = new AMapLocationClient(this);
        locationOption = new AMapLocationClientOption();
        // 设置定位监听
        locationClient.setLocationListener(this);
        // 设置定位模式为低功耗模式
        locationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
        //设置为单次定位
        locationOption.setOnceLocation(true);
        // 设置是否需要显示地址信息
        locationOption.setNeedAddress(true);
        // 设置是否开启缓存
//        locationOption.setLocationCacheEnable(true);
        //设置是否强制刷新WIFI，默认为强制刷新
//        locationOption.setOnceLocationLatest(true);
        //设置是否等待设备wifi刷新，如果设置为true,会自动变为单次定位，持续定位时不要使用
//        locationOption.setWifiActiveScan(true);
        /**
         *  设置发送定位请求的时间间隔,最小值为1000，如果小于1000，按照1000算
         *  只有持续定位设置定位间隔才有效，单次定位无效
         */
//        locationOption.setInterval(2000);
        // 设置定位参数
        locationClient.setLocationOption(locationOption);
    }

    private void initData(Bundle savedInstanceState) {
        mapView.onCreate(savedInstanceState);
        aMap = mapView.getMap();
        initLocation();
        geocodeSearch = new GeocodeSearch(this);
        geocodeSearch.setOnGeocodeSearchListener(this);
        settings = aMap.getUiSettings();
        settings.setCompassEnabled(true); // 指南针
        settings.setZoomControlsEnabled(true); // 缩放按钮
        settings.setScaleControlsEnabled(true); // 比例尺寸
        settings.setMyLocationButtonEnabled(true); // 设置默认定位按钮是否显示
        aMap.setOnMapLoadedListener(this);
        aMap.setLocationSource(this);
        aMap.setOnCameraChangeListener(this);

        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
        aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
    }

    private void setView(double longitude, double latitude) {
        setLocation(longitude, latitude);

        LatLng latLng = new LatLng(latitude, longitude);
        aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18f));
    }

    private void getAddress(double longitude, double latitude) {
        setLocation(longitude, latitude);

        LatLonPoint point = new LatLonPoint(latitude, longitude);
        RegeocodeQuery query = new RegeocodeQuery(point, 200, GeocodeSearch.AMAP);
        geocodeSearch.getFromLocationAsyn(query);
    }

    /**
     * 在屏幕中心添加一个Marker
     */
    private void addMarkerInScreenCenter() {
        LatLng latLng = aMap.getCameraPosition().target;
        Point screenPosition = aMap.getProjection().toScreenLocation(latLng);
        MarkerOptions markerOptions = new MarkerOptions()
                .anchor(0.5f, 0.5f)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.purple_pin));
        centerMarker = aMap.addMarker(markerOptions);
        //设置Marker在屏幕上,不跟随地图移动
        centerMarker.setPositionByPixels(screenPosition.x, screenPosition.y);

        tvAddress.setY(screenPosition.y - FunctionUtil.dip2px(110));
        tvAddress.setVisibility(View.VISIBLE);
    }

    /**
     * 屏幕中心marker 跳动
     */
    public void startJumpAnimation() {
        if (centerMarker != null) {
            //根据屏幕距离计算需要移动的目标点
            final LatLng latLng = centerMarker.getPosition();
            Point point = aMap.getProjection().toScreenLocation(latLng);
            point.y -= FunctionUtil.dip2px(125);
            LatLng target = aMap.getProjection().fromScreenLocation(point);
            //使用TranslateAnimation,填写一个需要移动的目标点
            Animation animation = new TranslateAnimation(target);
            animation.setInterpolator(new Interpolator() {
                @Override
                public float getInterpolation(float input) {
                    // 模拟重加速度的interpolator
                    if (input <= 0.5) {
                        return (float) (0.5f - 2 * (0.5 - input) * (0.5 - input));
                    } else {
                        return (float) (0.5f - Math.sqrt((input - 0.5f) * (1.5f - input)));
                    }
                }
            });
            //整个移动所需要的时间
            animation.setDuration(600);
            //设置动画
            centerMarker.setAnimation(animation);
            //开始动画
            centerMarker.startAnimation();

        } else {
            Logger.d("screenMarker is null");
        }
    }

    private void setToolBar() {
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setText(String text) {
        poiInfo.setAddress(text);

        tvAddress.setText(text);
        tvAddress.setVisibility(View.VISIBLE);
    }

    private void setLocation(double longitude, double latitude) {
        poiInfo.setLongitude(longitude);
        poiInfo.setLatitude(latitude);
    }

    @OnClick(R.id.btn_right)
    public void onClick() {
        if (poiInfo == null || TextUtils.isEmpty(poiInfo.getAddress()) || poiInfo.getLatitude() == 0 || poiInfo.getLongitude() == 0) {
            FunctionUtil.toastMessage("位置信息不正确");
        } else {
            Intent intent = new Intent();
            intent.putExtra("location", poiInfo);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        if (null != locationClient) {
            locationClient.onDestroy();
            locationClient = null;
            locationOption = null;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * LocationSource 接口
     * 点击我的位置时回调这里
     */
    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        if(flag == 1 || !isFirst) {
            locationClient.startLocation();
        } else {
            isFirst = false;
            setView(poiInfo.getLongitude(), poiInfo.getLatitude());
        }
    }
    @Override
    public void deactivate() {
        locationClient.stopLocation();
    }
    //////////////////////////↑↑↑↑↑////////////////////////////

    /**
     * AMapLocationListener 接口
     * 定位完成回调
     */
    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        locationClient.stopLocation();
        setView(aMapLocation.getLongitude(), aMapLocation.getLatitude());
    }
    //////////////////////////↑↑↑↑↑////////////////////////////

    /**
     * OnCameraChangeListener 接口
     * 拖动地图回调
     */
    // 实时回调
    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        if(tvAddress.getVisibility() != View.GONE)
            tvAddress.setVisibility(View.GONE);
    }
    // 拖动停止后回调
    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition) {
        startJumpAnimation();
        LatLng latLng = aMap.getCameraPosition().target;
        getAddress(latLng.longitude, latLng.latitude);
    }
    //////////////////////////↑↑↑↑↑////////////////////////////

    /**
     * OnGeocodeSearchListener 接口
     * 经纬度转地点
     */
    // 逆编码
    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
        if (regeocodeResult != null) {
            RegeocodeAddress regeocodeAddress = regeocodeResult.getRegeocodeAddress();
            setText(regeocodeAddress.getFormatAddress());
        }
    }
    // 编码
    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {
    }
    //////////////////////////↑↑↑↑↑////////////////////////////

    /**
     * OnMapLoadedListener 接口
     * 地图加载完成
     */
    @Override
    public void onMapLoaded() {
        addMarkerInScreenCenter();
    }
    //////////////////////////↑↑↑↑↑////////////////////////////

}
