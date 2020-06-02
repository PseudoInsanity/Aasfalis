package com.softwareengineering.aasfalis.fragments;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Column;
import com.anychart.enums.Anchor;
import com.anychart.enums.HoverMode;
import com.anychart.enums.Position;
import com.anychart.enums.TooltipPositionMode;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.maps.GeoApiContext;
import com.google.maps.model.LatLng;
import com.softwareengineering.aasfalis.R;
import com.softwareengineering.aasfalis.models.Distance;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class StatFragment extends Fragment {

    private boolean isFABOpen;
    private FloatingActionButton floatingActionButton1;
    private FloatingActionButton floatingActionButton2;
    private FloatingActionButton floatingActionButton3;
    private AnyChartView anyChartView;
    private GeoApiContext geoApiContext = null;
    private static int count = 0;
    private Cartesian cartesian;
    private Column column;
    private static boolean chartCreated;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_stat, container, false);


        if (geoApiContext == null) {
            geoApiContext = new GeoApiContext.Builder()
                    .apiKey("AIzaSyBG66LIjDtocQGXDlg58_ItA86RU4TZe-o")
                    .build();
        }

        anyChartView = view.findViewById(R.id.any_chart_view);
        isFABOpen = false;

        FloatingActionButton floatingActionButton = view.findViewById(R.id.floatingActionButton3);
        floatingActionButton.setImageBitmap(textAsBitmap("i", 37, Color.WHITE));
        floatingActionButton.setBackgroundColor(Color.BLACK);
        floatingActionButton1 = view.findViewById(R.id.fab1);
        floatingActionButton1.setImageBitmap(textAsBitmap("30 Min", 37, Color.WHITE));
        floatingActionButton1.setBackgroundColor(Color.BLACK);
        floatingActionButton2 = view.findViewById(R.id.fab2);
        floatingActionButton2.setImageBitmap(textAsBitmap("1H", 37, Color.WHITE));
        floatingActionButton2.setBackgroundColor(Color.BLACK);
        floatingActionButton3 = view.findViewById(R.id.fab3);
        floatingActionButton3.setImageBitmap(textAsBitmap("24H", 37, Color.WHITE));
        floatingActionButton3.setBackgroundColor(Color.BLACK);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isFABOpen){
                    showFABMenu();
                }else{
                    closeFABMenu();
                }
            }
        });

        floatingActionButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setThirtyMinutes();
                closeFABMenu();
            }
        });

        floatingActionButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setOneHour();
                closeFABMenu();
            }
        });

        floatingActionButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTwentyFourHours();
                closeFABMenu();
            }
        });

        return view;
    }

    private void showFABMenu(){
        isFABOpen=true;
        floatingActionButton1.animate().translationY(-getResources().getDimension(R.dimen.standard_155));
        floatingActionButton2.animate().translationY(-getResources().getDimension(R.dimen.standard_105));
        floatingActionButton3.animate().translationY(-getResources().getDimension(R.dimen.standard_55));
    }

    private void closeFABMenu(){
        isFABOpen=false;
        floatingActionButton1.animate().translationY(0);
        floatingActionButton2.animate().translationY(0);
        floatingActionButton3.animate().translationY(0);
    }

    private void setThirtyMinutes () {

        count = 0;
        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        CollectionReference collectionReference = documentReference.collection("position");
        collectionReference.orderBy("time", Query.Direction.DESCENDING).limit(6).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                ArrayList<LatLng> latLngs = new ArrayList<>();
                ArrayList<Distance> distanceArrayList = new ArrayList<>();
                for (QueryDocumentSnapshot q: task.getResult()) {
                    latLngs.add(new LatLng(q.getDouble("lat"), q.getDouble("lon")));
                }
                DocumentReference docRef = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            try {
                                JSONObject jsonObject = new JSONObject(new JSONTokener(calculateDirections(latLngs)));
                                int dist = Integer.parseInt(jsonObject.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("distance").getString("value"));
                                distanceArrayList.add(new Distance(document.getString("firstName"), dist));

                                CollectionReference collectionReference1 = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getEmail()).collection("friends");
                                collectionReference1.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        ArrayList<String> friends = new ArrayList<>();
                                        for (QueryDocumentSnapshot documentSnapshot: task.getResult()) {
                                            friends.add(documentSnapshot.getString("eMail"));
                                        }

                                        if (count < friends.size()) {
                                            getFriendData(distanceArrayList, friends, 6, "30 Minutes");

                                        } else {
                                            if (chartCreated) {
                                                updateChartData(distanceArrayList);
                                            }else {
                                                createChart(distanceArrayList);
                                            }
                                        }

                                    }
                                });
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        });
    }

    private void setOneHour () {

        count = 0;
        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        CollectionReference collectionReference = documentReference.collection("position");
        collectionReference.orderBy("time", Query.Direction.DESCENDING).limit(12).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                ArrayList<LatLng> latLngs = new ArrayList<>();
                ArrayList<Distance> distanceArrayList = new ArrayList<>();
                for (QueryDocumentSnapshot q: task.getResult()) {
                    latLngs.add(new LatLng(q.getDouble("lat"), q.getDouble("lon")));
                }
                DocumentReference docRef = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            try {
                                JSONObject jsonObject = new JSONObject(new JSONTokener(calculateDirections(latLngs)));
                                int dist = Integer.parseInt(jsonObject.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("distance").getString("value"));
                                distanceArrayList.add(new Distance(document.getString("firstName"), dist));

                                CollectionReference collectionReference1 = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getEmail()).collection("friends");
                                collectionReference1.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        ArrayList<String> friends = new ArrayList<>();
                                        for (QueryDocumentSnapshot documentSnapshot: task.getResult()) {
                                            friends.add(documentSnapshot.getString("eMail"));
                                        }

                                        if (count < friends.size()) {
                                            getFriendData(distanceArrayList, friends, 12, "Hour");

                                        } else {
                                            if (chartCreated) {
                                                updateChartData(distanceArrayList);
                                            }else {
                                                createChart(distanceArrayList);
                                            }
                                        }

                                    }
                                });
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        });
    }

    private void setTwentyFourHours () {

        count = 0;
        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        CollectionReference collectionReference = documentReference.collection("position");
        collectionReference.orderBy("time", Query.Direction.DESCENDING).limit(228).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                ArrayList<LatLng> latLngs = new ArrayList<>();
                ArrayList<Distance> distanceArrayList = new ArrayList<>();
                for (QueryDocumentSnapshot q: task.getResult()) {
                    latLngs.add(new LatLng(q.getDouble("lat"), q.getDouble("lon")));
                }
                DocumentReference docRef = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            try {
                                JSONObject jsonObject = new JSONObject(new JSONTokener(calculateDirections(latLngs)));
                                int dist = Integer.parseInt(jsonObject.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("distance").getString("value"));
                                distanceArrayList.add(new Distance(document.getString("firstName"), dist));

                                CollectionReference collectionReference1 = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getEmail()).collection("friends");
                                collectionReference1.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        ArrayList<String> friends = new ArrayList<>();
                                        for (QueryDocumentSnapshot documentSnapshot: task.getResult()) {
                                            friends.add(documentSnapshot.getString("eMail"));
                                        }

                                        if (count < friends.size()) {
                                            getFriendData(distanceArrayList, friends, 228, "24 Hours");

                                        } else {
                                            if (chartCreated) {
                                                updateChartData(distanceArrayList);
                                            }else {
                                                createChart(distanceArrayList);
                                            }

                                        }

                                    }
                                });
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        });
    }

    private void getFriendData (ArrayList<Distance> distanceList, ArrayList<String> friends, int limit, String time) {

        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("users").document(friends.get(count));
        CollectionReference collectionReference = documentReference.collection("position");
        collectionReference.orderBy("time", Query.Direction.DESCENDING).limit(limit).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                ArrayList<LatLng> latLngs = new ArrayList<>();
                ArrayList<Distance> distanceArrayList = new ArrayList<>();
                for (QueryDocumentSnapshot q: task.getResult()) {
                    latLngs.add(new LatLng(q.getDouble("lat"), q.getDouble("lon")));
                }
                DocumentReference docRef = FirebaseFirestore.getInstance().collection("users").document(friends.get(count));
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            try {
                                JSONObject jsonObject = new JSONObject(new JSONTokener(calculateDirections(latLngs)));
                                int dist = Integer.parseInt(jsonObject.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("distance").getString("value"));
                                distanceList.add(new Distance(document.getString("firstName"), dist));

                                count++;

                                if (count < friends.size()) {
                                    getFriendData(distanceList, friends, limit, time);

                                } else {
                                    if (chartCreated) {
                                        updateChartData(distanceList);
                                    } else {
                                        createChart(distanceList);
                                    }
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        });

    }

    private String calculateDirections(ArrayList<LatLng> markers){

        String origin = markers.get(0).toUrlValue();
        String destination = markers.get(markers.size()-1).toUrlValue();
        String waypoint = "";
        String result = "";

        for (int i = 1; i < markers.size()-1; i++) {

            waypoint += "via:" + markers.get(i);

            if (i != markers.size()-2) {
                waypoint += "|";
            }

        }
        String url = "https://maps.googleapis.com/maps/api/directions/json?origin=" + origin + "&destination=" + destination + "&waypoints=" + waypoint + "&units=metric&mode=walking&key=AIzaSyAL9J7OdOJ6qcq0oQLD8HkdHdOudL4eMDY";
        try {
            URL url1 = new URL(url);
            HttpURLConnection urlConnection = (HttpURLConnection) url1.openConnection();
            try {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                result = CharStreams.toString(new InputStreamReader(
                        in, Charsets.UTF_8));
            } finally {
                urlConnection.disconnect();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    private void createChart (ArrayList<Distance> distanceArrayList) {

        cartesian = AnyChart.column();
        List<DataEntry> data = new ArrayList<>();
        for (Distance distance: distanceArrayList) {
            data.add(new ValueDataEntry(distance.getName(), distance.getDistance()));
        }

        column = cartesian.column(data);

        column.tooltip()
                .titleFormat("{%X}")
                .position(Position.CENTER_BOTTOM)
                .anchor(Anchor.CENTER_BOTTOM)
                .offsetX(0d)
                .offsetY(5d)
                .format("{%Value}m");

        cartesian.animation(true);
        cartesian.title("Most Meters");

        cartesian.yScale().minimum(0d);

        cartesian.yAxis(0).labels().format("{%Value}m");

        cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
        cartesian.interactivity().hoverMode(HoverMode.BY_X);

        cartesian.xAxis(0).title("Users");
        cartesian.yAxis(0).title("Meters");

        anyChartView.setChart(cartesian);
        chartCreated = true;


    }

    private void updateChartData (ArrayList<Distance> distanceArrayList) {

        List<DataEntry> data = new ArrayList<>();
        for (Distance distance: distanceArrayList) {
            data.add(new ValueDataEntry(distance.getName(), distance.getDistance()));
        }

        cartesian.removeAllSeries();
        column = cartesian.column(data);

        cartesian.title("Most Meters");

    }

    private static Bitmap textAsBitmap(String text, float textSize, int textColor) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(textSize);
        paint.setColor(textColor);
        paint.setTextAlign(Paint.Align.LEFT);
        float baseline = -paint.ascent(); // ascent() is negative
        int width = (int) (paint.measureText(text) + 0.0f); // round
        int height = (int) (baseline + paint.descent() + 0.0f);
        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(image);
        canvas.drawText(text, 0, baseline, paint);
        return image;
    }

}
