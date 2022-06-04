package com.example.mapbox_kotlin

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mapbox.geojson.*
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.layers.CircleLayer
import com.mapbox.mapboxsdk.style.layers.FillLayer
import com.mapbox.mapboxsdk.style.layers.LineLayer
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import java.util.ArrayList
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.example.mapbox_kotlin.model.Area
import com.example.mapbox_kotlin.model.AreaPoint
import com.example.mapbox_kotlin.model.AreaWithPoint
import com.example.mapbox_kotlin.view_model.AreaViewModel



class MainActivity : AppCompatActivity() , OnMapReadyCallback {

    private val CIRCLE_SOURCE_ID = "circle-source-id"
    private val FILL_SOURCE_ID = "fill-source-id"
    private val LINE_SOURCE_ID = "line-source-id"
    private val CIRCLE_LAYER_ID = "circle-layer-id"
    private val FILL_LAYER_ID = "fill-layer-polygon-id"
    private val LINE_LAYER_ID = "line-layer-id"
    private  var fillAreaList: MutableList<Point> = ArrayList()
    private  var linesList: MutableList<Point> = ArrayList()
    private  var circlesList: MutableList<Feature> = ArrayList()

    private  var allFillAreas: MutableList<List<Point>> = ArrayList()

    private  var myPoints: MutableList<AreaPoint> = ArrayList()

    private var mapView: MapView? = null
    private var mapboxMap: MapboxMap? = null
    private var circlesPainter: GeoJsonSource? = null
    private var areaPainter: GeoJsonSource? = null
    private var linesPainter: GeoJsonSource? = null
    private var firstPoint: Point? = null

    private var fab: FloatingActionButton? = null


    var adapter : ArrayAdapter<AreaWithPoint>? = null
    var spinner : Spinner? = null
    var saveBtn : Button? = null

    var spinnerAreas = listOf<AreaWithPoint>()

    var areaViewModel : AreaViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Mapbox.getInstance(this, getString(R.string.mapbox_access_token))

        setContentView(R.layout.activity_main)

        areaViewModel = ViewModelProvider(this).get(AreaViewModel::class.java)
        
        spinner =  findViewById<Spinner>(R.id.spinner)

        areaViewModel?.getAllAreas()?.observe(this,
            { areas ->
                spinnerAreas = areas
                Log.i("tag",areas.size.toString())
                adapter = ArrayAdapter(
                    this,
                    R.layout.spinner_item,
                    spinnerAreas
                )
                spinnerBuilder()
            })
        mapView = findViewById(R.id.map_view)
        mapView?.onCreate(savedInstanceState)
        mapView?.getMapAsync(this)



        fab = findViewById<FloatingActionButton>(R.id.fab)
        saveBtn = findViewById(R.id.saveBtn)

        fab?.setOnClickListener(View.OnClickListener {
            clearEntireMap()
        })


        saveBtn?.setOnClickListener {
            if (myPoints.size>2) {
                Log.i("ok",myPoints.size.toString())
                showDialog()
            }
        }


    }

    private fun navigateToArea( points: List<AreaPoint>){
        if(points.isNotEmpty()){
            mapboxMap?.animateCamera(
                CameraUpdateFactory.newCameraPosition(
                    CameraPosition.Builder().target(LatLng(points.get(0).latitude, points.get(0).longitude))
                        .zoom(15.0)
                        .build()
                )
            )

            for(point in points){
                var currentPoint = Point.fromLngLat(
                    point.longitude,
                    point.latitude
                )
                drawPolygon(currentPoint)
            }
        }
    }

    private fun spinnerBuilder(){
        if (spinner != null) {
            spinner?.adapter = adapter
            spinner?.setSelection(spinnerAreas.lastIndex)
            spinner?.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    var selectedArea = parent?.selectedItem as AreaWithPoint
                    clearEntireMap()
                    navigateToArea(selectedArea.points)
                    (view as TextView).setTextColor(Color.WHITE)
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    clearEntireMap()
                }
            }
        }
    }


    private fun drawPolygon(currentPoint:Point) {

        // save first point so we can close the polygon
        if (circlesList.isEmpty()) {
            firstPoint = currentPoint
        }

        // save the current point to the circles list
        circlesList.add(Feature.fromGeometry(currentPoint))
        //circlesList2.add(AreaPoint(0,0,currentPoint.longitude(),currentPoint.latitude()))

        // re-render the circles
        circlesPainter?.setGeoJson(FeatureCollection.fromFeatures(circlesList))


        // add point to the linesList based on the number of circles
        if (circlesList.size < 3) {
            linesList.add(currentPoint)
        }

        else if (circlesList.size == 3) {
            linesList.add(currentPoint)
            linesList.add(firstPoint!!)
        }

        else {
            linesList.removeAt(circlesList.size -1)
            linesList.add(currentPoint)
            linesList.add(firstPoint!!)
        }

        // render the Lines
        linesPainter?.setGeoJson(
            FeatureCollection.fromFeatures(
                arrayOf(
                    Feature.fromGeometry(
                        LineString.fromLngLats(linesList)
                    )
                )
            )
        )

        // add point to the linesList based on the number of circles
        if (circlesList.size < 3) {
            fillAreaList.add(currentPoint)
        } else if (circlesList.size == 3) {
            fillAreaList.add(currentPoint)
            fillAreaList.add(firstPoint!!)
        } else {
            fillAreaList.removeAt(fillAreaList.size - 1)
            fillAreaList.add(currentPoint)
            fillAreaList.add(firstPoint!!)
        }

        allFillAreas = ArrayList()
        allFillAreas.add(fillAreaList)
        val allFeatures: MutableList<Feature> = ArrayList()
        allFeatures.add(Feature.fromGeometry(Polygon.fromLngLats(allFillAreas)))

        // render the area
        areaPainter?.setGeoJson(FeatureCollection.fromFeatures(allFeatures))
    }

    override fun onMapReady(mapboxMap: MapboxMap) {

        this.mapboxMap = mapboxMap

        mapboxMap.addOnMapClickListener {
            var currentPoint = Point.fromLngLat(
                mapboxMap?.cameraPosition?.target?.longitude!!,
                mapboxMap?.cameraPosition?.target?.latitude!!
            )
            var myPoint = AreaPoint(0,0,currentPoint.longitude(),currentPoint.latitude())

            myPoints.add(myPoint)
            drawPolygon(currentPoint)
            false
        }

        mapboxMap.setStyle(
            Style.SATELLITE_STREETS
        ) { style ->
            mapboxMap.animateCamera(
                CameraUpdateFactory.newCameraPosition(
                    CameraPosition.Builder().target(LatLng(46.19206386065661, 6.408275762327703))
                        .zoom(16.0)
                        .build()
                )
            )

            // Add sources to the map
            circlesPainter = initCircleSource(style)
            areaPainter = initFillSource(style)
            linesPainter = initLineSource(style)

            // Add layers to the map
            initCircleLayer(style)
            initLineLayer(style)
            initFillLayer(style)

        }
    }

    private fun initCircleLayer(loadedMapStyle: Style) {
        val circleLayer = CircleLayer(
            CIRCLE_LAYER_ID,
            CIRCLE_SOURCE_ID
        )
        circleLayer.setProperties(
            PropertyFactory.circleRadius(6f),
            PropertyFactory.circleColor(Color.parseColor("#04FFE7"))
        )
        loadedMapStyle.addLayer(circleLayer)
    }

    private fun initFillSource(loadedMapStyle: Style): GeoJsonSource? {
        val fillFeatureCollection = FeatureCollection.fromFeatures(arrayOf())
        val fillGeoJsonSource = GeoJsonSource(FILL_SOURCE_ID, fillFeatureCollection)
        loadedMapStyle.addSource(fillGeoJsonSource)
        return fillGeoJsonSource
    }

    private fun initFillLayer(loadedMapStyle: Style) {
        val fillLayer = FillLayer(
            FILL_LAYER_ID,
            FILL_SOURCE_ID
        )
        fillLayer.setProperties(
            PropertyFactory.fillOpacity(.6f),
            PropertyFactory.fillColor(Color.parseColor("#FFFFFFFF"))
        )
        loadedMapStyle.addLayerBelow(fillLayer, LINE_LAYER_ID)
    }

    private fun initLineSource(loadedMapStyle: Style): GeoJsonSource? {
        val lineFeatureCollection = FeatureCollection.fromFeatures(arrayOf())
        val lineGeoJsonSource = GeoJsonSource(LINE_SOURCE_ID, lineFeatureCollection)
        loadedMapStyle.addSource(lineGeoJsonSource)
        return lineGeoJsonSource
    }

    private fun initLineLayer(loadedMapStyle: Style) {
        val lineLayer = LineLayer(
            LINE_LAYER_ID,
            LINE_SOURCE_ID
        )
        lineLayer.setProperties(
            PropertyFactory.lineColor(Color.WHITE),
            PropertyFactory.lineWidth(5f)
        )
        loadedMapStyle.addLayerBelow(lineLayer, CIRCLE_LAYER_ID)
    }

    private fun clearEntireMap() {
        myPoints.clear()
        fillAreaList = ArrayList()
        circlesList = ArrayList()
        linesList = ArrayList()
        if (circlesPainter != null) {
            circlesPainter?.setGeoJson(FeatureCollection.fromFeatures(arrayOf()))
        }
        if (linesPainter != null) {
            linesPainter?.setGeoJson(FeatureCollection.fromFeatures(arrayOf()))
        }
        if (areaPainter != null) {
            areaPainter?.setGeoJson(FeatureCollection.fromFeatures(arrayOf()))
        }
    }

    private fun initCircleSource(loadedMapStyle: Style): GeoJsonSource? {
        val circleFeatureCollection = FeatureCollection.fromFeatures(arrayOf())
        val circleGeoJsonSource =
            GeoJsonSource(CIRCLE_SOURCE_ID, circleFeatureCollection)
        loadedMapStyle.addSource(circleGeoJsonSource)
        return circleGeoJsonSource
    }

    private fun showDialog(){
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.dialog_layout,null)
        val dialogEdt = dialogLayout.findViewById<EditText>(R.id.dialogEdt)

        with(builder){
            setPositiveButton("SAVE"){dialog,listener ->
               // dialogEdt.text.clear()
                if(dialogEdt.text.isNotEmpty()){
                    val areaWithPoint = AreaWithPoint(
                        Area(dialogEdt.text.toString(),0),
                        myPoints
                    )
                    areaViewModel?.insert(areaWithPoint)
                    Toast.makeText(baseContext, "Area saved successfully", Toast.LENGTH_SHORT).show()
                    //clearEntireMap()
                }
                else Toast.makeText(baseContext, "Area name is empty", Toast.LENGTH_SHORT).show()
            }

            setNegativeButton("Cancel"){dialog,listener ->
            }

            setView(dialogLayout)
            show()
        }
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onStart() {
        super.onStart()
        mapView?.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView?.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView?.onSaveInstanceState(outState)
    }

}