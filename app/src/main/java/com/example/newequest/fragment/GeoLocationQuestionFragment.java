package com.example.newequest.fragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.newequest.R;
import com.example.newequest.model.question.GeoLocationQuestion;
import com.example.newequest.util.Permission;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class GeoLocationQuestionFragment extends QuestionFragment {

    private GeoLocationQuestion question;
    private String respondent = "";
    private ArrayList<String> latitudeLongitude;
    private String[] permissoes = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    private LocationManager locationManager;
    private LocationListener locationListener;
    private GoogleMap mMap;

    private ImageView locationButton;

    private FusedLocationProviderClient fusedLocationClient;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        @SuppressLint("MissingPermission")
        @Override
        public void onMapReady(GoogleMap googleMap) {
//            LatLng sydney = new LatLng(-34, 151);
//            googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//            googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
            mMap = googleMap;
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

            if (question.getAnswers().size() > 0) {
                LatLng answer = new LatLng(Double.parseDouble(question.getAnswers().get(0)), Double.parseDouble(question.getAnswers().get(1)));
                mMap.addMarker(new MarkerOptions().position(answer));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(answer, 18));
                setAnswer(answer);
            } else {
//                LatLng point = new LatLng(-22.158170, -41.527013); //-21.491091, -41.618755
                LatLng point = new LatLng(-21.491091, -41.618755); //,
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point,14));

                locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);

                locationListener = new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        LatLng point = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.addMarker(new MarkerOptions().position(point));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 18));
                        setAnswer(point);
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {
                    }

                    @Override
                    public void onProviderEnabled(String provider) {
                    }

                    @Override
                    public void onProviderDisabled(String provider) {
                    }
                };

                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                    locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener, null);
                }
            }

            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(latLng));
                    setAnswer(latLng);
                }
            });

            locationButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
                    LatLng lastPoint = new LatLng(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLatitude(),locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLongitude());
                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(lastPoint));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastPoint, 18));
                    setAnswer(lastPoint);

                }
            });
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View rootView =  inflater.inflate(R.layout.fragment_geo_location_question, container, false);

        //Validar permissões
        Permission.validarPermissoes(permissoes, getActivity(), 1);

        latitudeLongitude = new ArrayList<>();

//        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
//        supportMapFragment.getMapAsync(this);

        Bundle bundle = getArguments();
        question = bundle.getParcelable("question");
        respondent = bundle.getString("respondent");

        String questionOutput;

        TextView questionView = rootView.findViewById(R.id.question);

        if((question.isReplica()) && (!question.getTitle().contains("[ ]"))){
            questionOutput = question.getTitle().concat(" (" + respondent + ")");
        }else {
            questionOutput = question.getTitle().replace("[ ]", respondent);
        }

        try {
            Spannable wordToSpan = new SpannableString(questionOutput);
            wordToSpan.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.tx_orange)),
                    questionOutput.indexOf(respondent),
                    questionOutput.indexOf(respondent) + respondent.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            questionView.setText(wordToSpan);
        }catch (Exception e){
            e.printStackTrace();
            questionView.setText(questionOutput);
        }

        ImageView nextButton = rootView.findViewById(R.id.next);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(latitudeLongitude.size() > 0) {
                    question.setAnswers(latitudeLongitude);
                }
                mCallback.onNextButtonClick();
            }
        });

        ImageView previousButton = rootView.findViewById(R.id.previous);
        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(latitudeLongitude.size() > 0) {
                    question.setAnswers(latitudeLongitude);
                }
                mCallback.onPreviousButtonClick();
            }
        });

        locationButton = rootView.findViewById(R.id.bt_map_location);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    @SuppressLint("MissingPermission")
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int permissaoResultado : grantResults) {
            //permission denied
            if (permissaoResultado == PackageManager.PERMISSION_DENIED) {
                alertaValidacaoPermissao();
            } else if (permissaoResultado == PackageManager.PERMISSION_GRANTED) {
                //Recuperar localizacao do usuario
                LatLng point = new LatLng(-22.158170, -41.527013);

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point,8));

                locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);

                locationListener = new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        LatLng point = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.addMarker(new MarkerOptions().position(point));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 18));
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {
                    }

                    @Override
                    public void onProviderEnabled(String provider) {
                    }

                    @Override
                    public void onProviderDisabled(String provider) {
                    }
                };

                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                    locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener, null);
                }
            }
        }
    }

    private void alertaValidacaoPermissao(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Permissões Negadas");
        builder.setMessage("Para utilizar o app é necessário aceitar as permissões");
        builder.setCancelable(false);
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getActivity().finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void setAnswer(LatLng latLng) {
        latitudeLongitude.clear();
        StringBuilder sb = new StringBuilder();
        //latitude
        sb.append(latLng.latitude);
        latitudeLongitude.add(sb.toString());
        //longitude
        sb.delete(0,sb.length());
        sb.append(latLng.longitude);
        latitudeLongitude.add(sb.toString());
    }
}