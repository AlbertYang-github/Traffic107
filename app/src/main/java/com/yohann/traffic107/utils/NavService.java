package com.yohann.traffic107.utils;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.DriveStep;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;

import java.util.ArrayList;
import java.util.List;

/**
 * NavService
 *
 * @author: lenovo
 * @time: 2016/10/4 21:26
 */
public class NavService {

    /**
     * 避免收费模式
     */
    public static final int MODE_SAVEMONEY = RouteSearch.DrivingSaveMoney;

    /**
     * 不走高速路模式
     */
    public static final int MODE_NOHIGHWAY = RouteSearch.DrivingNoHighWay;

    /**
     * 速度优先模式
     */
    public static final int MODE_DEFAULT = RouteSearch.DrivingDefault;

    private Context mContext;
    private AMap mAMap;
    private RouteSearch mRouteSearch;
    private OverLay mOverLay;

    /**
     * 构造器
     *
     * @param context
     * @param aMap
     * @param overLay 覆盖物
     */
    public NavService(@NonNull Context context, @NonNull AMap aMap, @NonNull OverLay overLay) {
        this.mContext = context;
        this.mAMap = aMap;
        mOverLay = overLay;
        mRouteSearch = new RouteSearch(mContext);
        mRouteSearch.setRouteSearchListener(new RouteSearchListener());
    }

    /**
     * 显示路径规划的线路（可以避开堵点）
     *
     * @param startLatLon 路径规划的起始点
     * @param endLatLon   路径规划的终点
     * @param mode        路径规划的模式（包括：避免收费模式，不走高速路模式，速度优先模式）
     * @param avoidAreas  堵点的集合(null表示不进行避让堵点）
     */
    public void driveRoutePlan(@NonNull LatLonPoint startLatLon, @NonNull LatLonPoint endLatLon,
                               int mode,
                               List<List<LatLonPoint>> avoidAreas) {
        if (avoidAreas.size() <= 0) {
            avoidAreas = null;
        }

        RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(startLatLon, endLatLon);
        // 第一个参数表示路径规划的起点和终点，第二个参数表示驾车模式，第三个参数表示途经点，第四个参数表示避让区域，第五个参数表示避让道路
        RouteSearch.DriveRouteQuery query = new RouteSearch.DriveRouteQuery(fromAndTo, mode, null, avoidAreas, "");
        mRouteSearch.calculateDriveRouteAsyn(query);
    }

    /**
     * 路径查询结束的监听器
     */
    private class RouteSearchListener implements RouteSearch.OnRouteSearchListener {

        @Override
        public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {

        }

        @Override
        public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int i) {
            //当路径规划好之后将回调用该方法
            if (i == 1000) {
                if (driveRouteResult != null && driveRouteResult.getPaths() != null) {
                    if (driveRouteResult.getPaths().size() > 0) {
                        //获取驾车路线规划的路径
                        DrivePath drivePath = driveRouteResult.getPaths().get(0);
                        mOverLay.initRequire(mAMap, drivePath,
                                driveRouteResult.getStartPos(),
                                driveRouteResult.getTargetPos());

                        mOverLay.addToMap();
                        mOverLay.zoomToSpan();
                    } else {
                        Toast.makeText(mContext, "没有查询到路径", Toast.LENGTH_LONG).show();
                        return;
                    }
                } else {
                    Toast.makeText(mContext, "没有查询到路径", Toast.LENGTH_LONG).show();
                    return;
                }
            } else {
                Toast.makeText(mContext, "出现错误（错误码）：" + i, Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {

        }
    }

    /**
     * 导航覆盖物类
     */
    public static class OverLay {
        private AMap mAMap;
        private LatLng startPoint, endPoint;
        private DrivePath drivePath;
        private PolylineOptions mPolylineOptions;
        private float mWidth;
        private int mColor;
        private List<Polyline> allPolyLines = new ArrayList<>();

        /**
         * 覆盖物构造器
         */
        public OverLay() {
            this(17, Color.BLUE);
        }

        /**
         * 覆盖物构造器
         *
         * @param width 覆盖物的宽度
         * @param color 覆盖物的颜色
         */
        public OverLay(float width, int color) {
            this.mWidth = width;
            this.mColor = color;
        }

        /**
         * 设置覆盖物的宽度
         *
         * @param width
         */
        public void setOverLayWidth(float width) {
            mWidth = width;
        }

        /**
         * 设置覆盖物的颜色
         *
         * @param color
         */
        public void setOverLayColor(int color) {
            mColor = color;
        }

        /**
         * 覆盖物条件设置
         *
         * @param path
         * @param start
         * @param end
         */
        private void initRequire(AMap aMap, DrivePath path,
                                 LatLonPoint start, LatLonPoint end) {
            mAMap = aMap;
            drivePath = path;
            startPoint = new LatLng(start.getLatitude(), start.getLongitude());
            endPoint = new LatLng(end.getLatitude(), end.getLongitude());
        }

        /**
         * 添加驾车路线添加到地图上显示。
         */
        private void addToMap() {
            initPolylineOptions();
            try {
                if (mAMap == null || drivePath == null) {
                    return;
                }

                List<DriveStep> drivePaths = drivePath.getSteps();
                mPolylineOptions.add(startPoint);
                for (DriveStep step : drivePaths) {
                    List<LatLonPoint> latlonPoints = step.getPolyline();
                    for (LatLonPoint latlonpoint : latlonPoints) {
                        mPolylineOptions.add(new LatLng(latlonpoint.getLatitude(), latlonpoint.getLongitude()));
                    }
                }
                mPolylineOptions.add(endPoint);
                showPolyline();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }

        /**
         * 初始化线段属性
         */
        private void initPolylineOptions() {

            mPolylineOptions = null;
            mPolylineOptions = new PolylineOptions();
            mPolylineOptions.color(mColor).width(mWidth);
        }

        /**
         * 显示路径
         */
        private void showPolyline() {
            addPolyLine(mPolylineOptions);
        }

        /**
         * 绘制路径
         *
         * @param options
         */
        private void addPolyLine(PolylineOptions options) {
            if (options == null) {
                return;
            }
            Polyline polyline = mAMap.addPolyline(options);
            if (polyline != null) {
                allPolyLines.add(polyline);
            }
        }

        /**
         * 获取照相机的数据
         */
        private LatLngBounds getLatLngBounds() {
            LatLngBounds.Builder b = LatLngBounds.builder();
            b.include(new LatLng(startPoint.latitude, startPoint.longitude));
            b.include(new LatLng(endPoint.latitude, endPoint.longitude));
            return b.build();
        }

        /**
         * 移动镜头到当前的视角。
         */
        private void zoomToSpan() {
            if (startPoint != null) {
                if (mAMap == null)
                    return;
                try {
                    LatLngBounds bounds = getLatLngBounds();
                    mAMap.animateCamera(CameraUpdateFactory
                            .newLatLngBounds(bounds, 10));
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
