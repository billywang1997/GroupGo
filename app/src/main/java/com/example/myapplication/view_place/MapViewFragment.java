package com.example.myapplication.view_place;

import static android.app.Activity.RESULT_OK;
import static androidx.constraintlayout.motion.utils.Oscillator.TAG;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.viewmodel.CreationExtras;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.BuildConfig;
import com.example.myapplication.LocalDatabase.LocationEntity;
import com.example.myapplication.LocalDatabase.MyDatabase;
import com.example.myapplication.LocalDatabase.PeopleVote;
import com.example.myapplication.LocalDatabase.VoteEntity;
import com.example.myapplication.MainActivity;
import com.example.myapplication.MyItem;
import com.example.myapplication.R;
import com.example.myapplication.TimeOutEvent;
import com.example.myapplication.VotedEvent;
import com.example.myapplication.account_management.HomePageFragment;
import com.example.myapplication.add_location_management.LocationDataHelper;
import com.example.myapplication.add_location_management.MapsActivityCurrentPlace;
import com.example.myapplication.add_location_management.RemoteLocationHelper;
import com.example.myapplication.place_address_adapter.PlaceAddressAdapter;
import com.example.myapplication.place_address_adapter.PlaceAddressItem;
import com.example.myapplication.vote_management.PeopleVoteHelper;
import com.example.myapplication.vote_management.RemotePeopleVoteHelper;
import com.example.myapplication.vote_management.RemoteVoteDataHelper;
import com.example.myapplication.vote_management.VoteActivity;
import com.example.myapplication.vote_management.VoteDataHelper;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import io.reactivex.CompletableObserver;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


// GoogleMap.OnMarkerClickListener,

public class MapViewFragment extends Fragment implements OnMapReadyCallback, ClusterManager.OnClusterClickListener<MyItem>, ClusterManager.OnClusterItemClickListener<MyItem> {

    private final String[] typeArray = {"All", "Food", "Drink", "Indoor Activity", "Outdoor Activity"};
    private final String[] voteArray = {"No limitation", "> 0 votes", "> 2 votes", "> 5 votes", "> 10 votes", "> 20 votes"};

    Spinner typeSpinner, voteSpinner;
    View view;
    String type, vote;

    View currentDialogView;
    TextView voteButton;

    boolean ifVoted = false;

    private ClusterManager<MyItem> clusterManager;
    private List<MyItem> items = new ArrayList<>();

    private double curLatitude = 0;
    private double curLongitude = 0;

    LatLngBounds curBounds = null;


    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private final LatLng defaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int PADDING = 160;
    private boolean locationPermissionGranted;

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location lastKnownLocation;

    // Keys for storing activity state.
    // [START maps_current_place_state_keys]
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    // [END maps_current_place_state_keys]

    // Used for selecting the current place.
    private static final int M_MAX_ENTRIES = 5;
    private String[] likelyPlaceNames;
    private String[] likelyPlaceAddresses;
    private List[] likelyPlaceAttributions;
    private LatLng[] likelyPlaceLatLngs;

    // The entry point to the Places API.
    private PlacesClient placesClient;

    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient fusedLocationProviderClient;


    private GoogleMap map;

    public static List<LocationEntity> curLocationList = HomePageFragment.curLocationList;
    public static List<LocationEntity> tempLocationList = new ArrayList<>();

    LocationDataHelper localLocationHelper;
    PeopleVoteHelper localPeopleVoteHelper;

    VoteDataHelper localVoteHelper;

    LinearLayout voteFilterLayout;


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.map_fragment, container, false);

        localLocationHelper = new RemoteLocationHelper();
        localPeopleVoteHelper = new RemotePeopleVoteHelper();

        localVoteHelper = new RemoteVoteDataHelper();

        typeSpinner = view.findViewById(R.id.filter);
        voteSpinner = view.findViewById(R.id.votes);

        voteFilterLayout = view.findViewById(R.id.votes_filter);

        EventBus.getDefault().register(this);







        return view;
    }


    private void setSpinner() {
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<String>(getContext(), R.layout.item_select, typeArray);
        ArrayAdapter<String> voteAdapter = new ArrayAdapter<String>(getContext(), R.layout.item_select, voteArray);


        typeAdapter.setDropDownViewResource(R.layout.item_dropdown);

        typeSpinner.setPrompt("Select filter type");
        typeSpinner.setAdapter(typeAdapter);
        typeSpinner.setSelection(0);
        typeSpinner.setOnItemSelectedListener(new MySelectedListener());
        typeSpinner.setTag("spinner1");


        voteSpinner.setPrompt("Select vote number limitation");
        voteSpinner.setAdapter(voteAdapter);
        voteSpinner.setSelection(0);
        voteSpinner.setOnItemSelectedListener(new MySelectedListener());
        voteSpinner.setTag("spinner2");

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.map = googleMap;



        items.clear();

        // googleMap.setOnMarkerClickListener(this);


        clusterManager = new ClusterManager<MyItem>(getActivity(),map);


        map.setOnMarkerClickListener(clusterManager);
        map.setOnCameraIdleListener(clusterManager);
        clusterManager.setOnClusterClickListener(this);
        clusterManager.setOnClusterItemClickListener(this);

        clusterManager.setRenderer(new CustomClusterRenderer(getContext(), map, clusterManager));





        // Prompt the user for permission.
        getLocationPermission();
        // [END_EXCLUDE]

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        getDeviceLocation();


        if (HomePageFragment.ifHaveCurrentVote) {

            typeSpinner.setSelection(0);
            voteSpinner.setSelection(0);

            List<Double> latitudes = new ArrayList<>();
            List<Double> longitudes = new ArrayList<>();

            int curId = 0;

            clusterManager.clearItems();
            items.clear();

            for (VoteEntity location : HomePageFragment.curVoteList) {
                System.out.println("@@@@@@@@VoteEntity locationVoteEntity location" + location.locationName);
                System.out.println("@@@@@@@@LatLng curLocationLatLng curLocation" + new LatLng(Double.parseDouble(location.latitude),
                        Double.parseDouble(location.longitude)));

                LatLng curLocation = new LatLng(Double.parseDouble(location.latitude),
                        Double.parseDouble(location.longitude));

                latitudes.add(Double.parseDouble(location.latitude));
                longitudes.add(Double.parseDouble(location.longitude));

                /*
                map.addMarker(new MarkerOptions()
                        .title(location.locationName)
                        .position(curLocation)
                        .snippet(String.valueOf(curId)));

                 */


                items.add(new MyItem(curLocation.latitude, curLocation.longitude, location.locationName, String.valueOf(curId)));
                clusterManager.addItems(items);
                clusterManager.cluster();

                curId++;
            }


            double minLatitude, maxLatitude, minLongitude, maxLongitude;

            minLatitude = Collections.min(latitudes);
            maxLatitude =  Collections.max(latitudes);
            minLongitude = Collections.min(longitudes);
            maxLongitude = Collections.max(longitudes);

            LatLngBounds bounds = new LatLngBounds(new LatLng(minLatitude, minLongitude),
                    new LatLng(maxLatitude, maxLongitude));

            curBounds = bounds;

            // Position the map's camera at the location of the marker.
            map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, PADDING));

        } else {

            localLocationHelper.getAllLocation(HomePageFragment.curGroupNumber)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SingleObserver<List<LocationEntity>>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onSuccess(List<LocationEntity> locationEntities) {
                            curLocationList = locationEntities;

                            typeSpinner.setSelection(0);
                            voteSpinner.setSelection(0);

                            List<Double> latitudes = new ArrayList<>();
                            List<Double> longitudes = new ArrayList<>();

                            int curId = 0;

                            clusterManager.clearItems();
                            items.clear();

                            for (LocationEntity location : curLocationList) {
                                LatLng curLocation = new LatLng(Double.parseDouble(location.latitude),
                                        Double.parseDouble(location.longitude));

                                latitudes.add(Double.parseDouble(location.latitude));
                                longitudes.add(Double.parseDouble(location.longitude));


                                /*
                                map.addMarker(new MarkerOptions()
                                        .title(location.locationName)
                                        .position(curLocation)
                                        .snippet(String.valueOf(curId)));

                                 */



                                items.add(new MyItem(curLocation.latitude, curLocation.longitude, location.locationName, String.valueOf(curId)));
                                clusterManager.addItems(items);
                                clusterManager.cluster();



                                curId++;
                            }


                            double minLatitude, maxLatitude, minLongitude, maxLongitude;

                            minLatitude = Collections.min(latitudes);
                            maxLatitude =  Collections.max(latitudes);
                            minLongitude = Collections.min(longitudes);
                            maxLongitude = Collections.max(longitudes);

                            LatLngBounds bounds = new LatLngBounds(new LatLng(minLatitude, minLongitude),
                                    new LatLng(maxLatitude, maxLongitude));

                            curBounds = bounds;

                            // Position the map's camera at the location of the marker.
                            map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, PADDING));
                        }

                        @Override
                        public void onError(Throwable e) {
                            Toast.makeText(getActivity(), "There's no location added in this group", Toast.LENGTH_SHORT).show();
                        }
                    });
        }



    }

    @NonNull
    @Override
    public CreationExtras getDefaultViewModelCreationExtras() {
        return super.getDefaultViewModelCreationExtras();
    }


    /*
    @Override
    public boolean onMarkerClick(final Marker marker) {

        final BottomSheetDialog dialog = new BottomSheetDialog(getActivity());
        View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.location_list_card, null);

        currentDialogView = dialogView;

        TextView placeNameView;
        TextView placeAddressView;
        TextView placeTypeView;
        TextView oneReasonView;
        ImageView img1View;
        ImageView img2View;
        ImageView img3View;


        placeNameView = (TextView) dialogView.findViewById(R.id.place_name);
        placeAddressView = (TextView) dialogView.findViewById(R.id.address);
        placeTypeView = (TextView) dialogView.findViewById(R.id.place_type);
        oneReasonView = (TextView) dialogView.findViewById(R.id.one_reason);
        img1View = (ImageView) dialogView.findViewById(R.id.img1);
        img2View = (ImageView) dialogView.findViewById(R.id.img2);
        img3View = (ImageView) dialogView.findViewById(R.id.img3);
        voteButton = (TextView) dialogView.findViewById(R.id.tv_vote);

        int curId = Integer.parseInt(marker.getSnippet());


        if (HomePageFragment.ifHaveCurrentVote) {
            VoteEntity curLocation = HomePageFragment.curVoteList.get(curId);

            placeNameView.setText(marker.getTitle());
            placeAddressView.setText(curLocation.locationAddress);

            if (curLocation.locationTag == null || curLocation.locationTag.isEmpty()) {
                placeTypeView.setVisibility(View.INVISIBLE);
            } else {
                placeTypeView.setText("· " + curLocation.locationTag.substring(0, 1).toUpperCase() +
                        curLocation.locationTag.substring(1).toLowerCase());
            }

            if (!HomePageFragment.ifHaveCurrentVote) {
                voteButton.setVisibility(View.GONE);
            } else {
                localPeopleVoteHelper.getVotePeople(HomePageFragment.curGroupNumber, curLocation.locationName,
                                curLocation.locationAddress, curLocation.voteStartTime, HomePageFragment.curUser)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new SingleObserver<PeopleVote>() {
                            @Override
                            public void onSubscribe(Disposable d) {
                                System.out.println("||||||||||||||||||||||||||||||||||||||||||||||||||||||||| " + HomePageFragment.curGroupNumber
                                + " " + curLocation.locationName + " " + curLocation.locationAddress + " " + curLocation.voteStartTime + " "
                                + HomePageFragment.curUser + " ||||||||||||||||||||||||||||||||||||||||||||||||||||||||| " );
                            }

                            @Override
                            public void onSuccess(PeopleVote peopleVote) {

                                localVoteHelper.getNumberOfNotesOneGroupOneTime(HomePageFragment.curGroupNumber, curLocation.voteStartTime,
                                        curLocation.locationName,
                                        curLocation.locationAddress)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new SingleObserver<Integer>() {
                                            @Override
                                            public void onSubscribe(Disposable d) {
                                                System.out.println("|||||||||||||||||||||||||||||||||||||||||||||||||||||||||SUCCESSFUL GETNUMBER |||||||||||||||||||");
                                            }

                                            @Override
                                            public void onSuccess(Integer integer) {
                                                voteButton.setBackgroundResource(R.drawable.grey_background);

                                                voteButton.setText(integer + " voted");
                                                voteButton.setTextColor(Color.parseColor("#333232"));
                                                System.out.println("|||||||||||||||||||||||||||||||||||||||||||||||||||||||||NUMBER |||||||||||||||||||" + integer);

                                                voteButton.setClickable(false);
                                            }

                                            @Override
                                            public void onError(Throwable e) {
                                                voteButton.setBackgroundResource(R.drawable.grey_background);

                                                System.out.println("|||||||||||||||||||||||||||||||||||||||||||||||||||||||||ERROR |||||||||||||||||||");
                                                voteButton.setText(0 + " voted");
                                                voteButton.setTextColor(Color.parseColor("#333232"));

                                                voteButton.setClickable(false);
                                            }
                                        });

                            }

                            @Override
                            public void onError(Throwable e) {

                                System.out.println("|||||||||||||||||||||||||||||||||||||||||||||||||||||||||ERROR LAst |||||||||||||||||||");

                                voteButton.setOnClickListener(v -> {
                                    localPeopleVoteHelper.insertOnePeopleVote(HomePageFragment.curGroupNumber, HomePageFragment.curUser, curLocation.locationName,
                                                    curLocation.locationAddress, "true", curLocation.voteStartTime)
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(new CompletableObserver() {
                                                @Override
                                                public void onSubscribe(Disposable d) {

                                                }

                                                @Override
                                                public void onComplete() {

                                                    localVoteHelper.updateTheVotes(HomePageFragment.curGroupNumber, curLocation.locationName,
                                                            curLocation.locationAddress, curLocation.voteStartTime)
                                                            .subscribeOn(Schedulers.io())
                                                            .observeOn(AndroidSchedulers.mainThread())
                                                            .subscribe(new CompletableObserver() {
                                                                @Override
                                                                public void onSubscribe(Disposable d) {

                                                                }

                                                                @Override
                                                                public void onComplete() {
                                                                    Toast.makeText(getActivity(), "Voted successfully", Toast.LENGTH_SHORT).show();

                                                                    localVoteHelper.getNumberOfNotesOneGroupOneTime(HomePageFragment.curGroupNumber, curLocation.voteStartTime,
                                                                                    curLocation.locationName,
                                                                                    curLocation.locationAddress)
                                                                            .subscribeOn(Schedulers.io())
                                                                            .observeOn(AndroidSchedulers.mainThread())
                                                                            .subscribe(new SingleObserver<Integer>() {
                                                                                @Override
                                                                                public void onSubscribe(Disposable d) {

                                                                                }

                                                                                @Override
                                                                                public void onSuccess(Integer integer) {
                                                                                    voteButton.setBackgroundResource(R.drawable.grey_background);

                                                                                    voteButton.setText(integer + " voted");
                                                                                    voteButton.setTextColor(Color.parseColor("#333232"));

                                                                                    voteButton.setClickable(false);

                                                                                    EventBus.getDefault().post(new VotedEvent());
                                                                                }

                                                                                @Override
                                                                                public void onError(Throwable e) {
                                                                                    voteButton.setBackgroundResource(R.drawable.grey_background);

                                                                                    voteButton.setText(0 + " voted");
                                                                                    voteButton.setTextColor(Color.parseColor("#333232"));

                                                                                    voteButton.setClickable(false);
                                                                                }
                                                                            });
                                                                }

                                                                @Override
                                                                public void onError(Throwable e) {

                                                                }
                                                            });
                                                }

                                                @Override
                                                public void onError(Throwable e) {
                                                    Toast.makeText(getActivity(), "You have voted for this place previously", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                });

                            }
                        });
            }

            oneReasonView.setText(curLocation.comments.split(";newOneComment;")[0]);


            String[] split = curLocation.photos.split(";newOne;");

            if (split ==  null || split.length == 0) {


                img1View.setVisibility(View.INVISIBLE);
                img2View.setVisibility(View.INVISIBLE);
                img3View.setVisibility(View.INVISIBLE);

            } else if (split.length == 1) {

                img1View.setImageBitmap(base64ToBitmap(split[0]));
                img2View.setVisibility(View.INVISIBLE);
                img3View.setVisibility(View.INVISIBLE);
            } else if (split.length == 2) {

                img1View.setImageBitmap(base64ToBitmap(split[0]));
                img2View.setImageBitmap(base64ToBitmap(split[1]));
                img3View.setVisibility(View.INVISIBLE);
            } else {

                img1View.setImageBitmap(base64ToBitmap(split[0]));
                img2View.setImageBitmap(base64ToBitmap(split[1]));
                img3View.setImageBitmap(base64ToBitmap(split[2]));
            }

            dialogView.setOnClickListener(v -> {
                PlaceDetailActivity.clickStart(getContext(), curLocation.locationName,
                        curLocation.locationAddress, curLocation.locationTag == null ? null : curLocation.locationTag, true);
            });

        } else {
            LocationEntity curLocation = curLocationList.get(curId);

            placeNameView.setText(marker.getTitle());
            placeAddressView.setText(curLocation.locationAddress);

            if (curLocation.locationTag == null || curLocation.locationTag.isEmpty()) {
                placeTypeView.setVisibility(View.INVISIBLE);
            } else {
                placeTypeView.setText("· " + curLocation.locationTag.substring(0, 1).toUpperCase() +
                        curLocation.locationTag.substring(1).toLowerCase());
            }

            if (!HomePageFragment.ifHaveCurrentVote) {
                voteButton.setVisibility(View.GONE);
            } else {
                voteButton.setOnClickListener(v -> {

                });
            }

            oneReasonView.setText(curLocation.comments.split(";newOneComment;")[0]);

            String[] split = null;

            if (curLocation.photos != null) {
                split = curLocation.photos.split(";newOne;");
            }


            if (split ==  null || split.length == 0) {


                img1View.setVisibility(View.INVISIBLE);
                img2View.setVisibility(View.INVISIBLE);
                img3View.setVisibility(View.INVISIBLE);

            } else if (split.length == 1) {

                img1View.setImageBitmap(base64ToBitmap(split[0]));
                img2View.setVisibility(View.INVISIBLE);
                img3View.setVisibility(View.INVISIBLE);
            } else if (split.length == 2) {

                img1View.setImageBitmap(base64ToBitmap(split[0]));
                img2View.setImageBitmap(base64ToBitmap(split[1]));
                img3View.setVisibility(View.INVISIBLE);
            } else {

                img1View.setImageBitmap(base64ToBitmap(split[0]));
                img2View.setImageBitmap(base64ToBitmap(split[1]));
                img3View.setImageBitmap(base64ToBitmap(split[2]));
            }

            dialogView.setOnClickListener(v -> {
                PlaceDetailActivity.clickStart(getContext(), curLocation.locationName,
                        curLocation.locationAddress, curLocation.locationTag == null ? null : curLocation.locationTag, true);
            });

        }


        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(dialogView);
        dialog.show();


        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {

                localLocationHelper.getAllLocation(HomePageFragment.curGroupNumber)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new SingleObserver<List<LocationEntity>>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onSuccess(List<LocationEntity> locationEntities) {
                                HomePageFragment.curLocationList = locationEntities;

                            }

                            @Override
                            public void onError(Throwable e) {

                            }
                        });

                localVoteHelper.getHasCurrentVote(HomePageFragment.addCreateTime(), HomePageFragment.curGroupNumber)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new SingleObserver<VoteEntity>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onSuccess(VoteEntity voteEntity) {
                                HomePageFragment.currentVote = voteEntity;



                                HomePageFragment.ifHaveCurrentVote = true;


                                localVoteHelper.getAllVotes(HomePageFragment.curGroupNumber, voteEntity.voteStartTime)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new SingleObserver<List<VoteEntity>>() {
                                            @Override
                                            public void onSubscribe(Disposable d) {

                                            }

                                            @Override
                                            public void onSuccess(List<VoteEntity> voteEntities) {
                                                HomePageFragment.curVoteList = voteEntities;
                                            }

                                            @Override
                                            public void onError(Throwable e) {

                                            }
                                        });

                            }

                            @Override
                            public void onError(Throwable e) {

                                HomePageFragment.ifHaveCurrentVote = false;

                                EventBus.getDefault().post(new TimeOutEvent());

                            }
                        });


            }
        });

        return false;
    }
    */



    public static Bitmap base64ToBitmap(String base64Data) {
        byte[] bytes = Base64.decode(base64Data, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    @Override
    public boolean onClusterClick(Cluster<MyItem> cluster) {

        List<MyItem> tempItems = new ArrayList<>(cluster.getItems());

        List<Double> latitudes = new ArrayList<>();
        List<Double> longitudes = new ArrayList<>();


        for (MyItem curItem : tempItems) {

            latitudes.add(curItem.getPosition().latitude);
            longitudes.add(curItem.getPosition().longitude);

        }


        double minLatitude, maxLatitude, minLongitude, maxLongitude;

        minLatitude = Collections.min(latitudes);
        maxLatitude =  Collections.max(latitudes);
        minLongitude = Collections.min(longitudes);
        maxLongitude = Collections.max(longitudes);

        LatLngBounds bounds = new LatLngBounds(new LatLng(minLatitude, minLongitude),
                new LatLng(maxLatitude, maxLongitude));

        curBounds = bounds;

        // Position the map's camera at the location of the marker.
        map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, PADDING));

        return false;
    }


    @Override
    public boolean onClusterItemClick(MyItem item) {
        final BottomSheetDialog dialog = new BottomSheetDialog(getActivity());
        View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.location_list_card, null);

        currentDialogView = dialogView;

        TextView placeNameView;
        TextView placeAddressView;
        TextView placeTypeView;
        TextView oneReasonView;
        ImageView img1View;
        ImageView img2View;
        ImageView img3View;


        placeNameView = (TextView) dialogView.findViewById(R.id.place_name);
        placeAddressView = (TextView) dialogView.findViewById(R.id.address);
        placeTypeView = (TextView) dialogView.findViewById(R.id.place_type);
        oneReasonView = (TextView) dialogView.findViewById(R.id.one_reason);
        img1View = (ImageView) dialogView.findViewById(R.id.img1);
        img2View = (ImageView) dialogView.findViewById(R.id.img2);
        img3View = (ImageView) dialogView.findViewById(R.id.img3);
        voteButton = (TextView) dialogView.findViewById(R.id.tv_vote);

        int curId = Integer.parseInt(item.getSnippet());


        if (HomePageFragment.ifHaveCurrentVote) {
            VoteEntity curLocation = HomePageFragment.curVoteList.get(curId);

            placeNameView.setText(item.getTitle());
            placeAddressView.setText(curLocation.locationAddress);

            if (curLocation.locationTag == null || curLocation.locationTag.isEmpty()) {
                placeTypeView.setVisibility(View.INVISIBLE);
            } else {
                placeTypeView.setText("· " + curLocation.locationTag.substring(0, 1).toUpperCase() +
                        curLocation.locationTag.substring(1).toLowerCase());
            }

            if (!HomePageFragment.ifHaveCurrentVote) {
                voteButton.setVisibility(View.GONE);
            } else {
                localPeopleVoteHelper.getVotePeople(HomePageFragment.curGroupNumber, curLocation.locationName,
                                curLocation.locationAddress, curLocation.voteStartTime, HomePageFragment.curUser)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new SingleObserver<PeopleVote>() {
                            @Override
                            public void onSubscribe(Disposable d) {
                                System.out.println("||||||||||||||||||||||||||||||||||||||||||||||||||||||||| " + HomePageFragment.curGroupNumber
                                        + " " + curLocation.locationName + " " + curLocation.locationAddress + " " + curLocation.voteStartTime + " "
                                        + HomePageFragment.curUser + " ||||||||||||||||||||||||||||||||||||||||||||||||||||||||| " );
                            }

                            @Override
                            public void onSuccess(PeopleVote peopleVote) {

                                localVoteHelper.getNumberOfNotesOneGroupOneTime(HomePageFragment.curGroupNumber, curLocation.voteStartTime,
                                                curLocation.locationName,
                                                curLocation.locationAddress)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new SingleObserver<Integer>() {
                                            @Override
                                            public void onSubscribe(Disposable d) {
                                                System.out.println("|||||||||||||||||||||||||||||||||||||||||||||||||||||||||SUCCESSFUL GETNUMBER |||||||||||||||||||");
                                            }

                                            @Override
                                            public void onSuccess(Integer integer) {
                                                voteButton.setBackgroundResource(R.drawable.grey_background);

                                                voteButton.setText(integer + " voted");
                                                voteButton.setTextColor(Color.parseColor("#333232"));
                                                System.out.println("|||||||||||||||||||||||||||||||||||||||||||||||||||||||||NUMBER |||||||||||||||||||" + integer);

                                                voteButton.setClickable(false);
                                            }

                                            @Override
                                            public void onError(Throwable e) {
                                                voteButton.setBackgroundResource(R.drawable.grey_background);

                                                System.out.println("|||||||||||||||||||||||||||||||||||||||||||||||||||||||||ERROR |||||||||||||||||||");
                                                voteButton.setText(0 + " voted");
                                                voteButton.setTextColor(Color.parseColor("#333232"));

                                                voteButton.setClickable(false);
                                            }
                                        });

                            }

                            @Override
                            public void onError(Throwable e) {

                                System.out.println("|||||||||||||||||||||||||||||||||||||||||||||||||||||||||ERROR LAst |||||||||||||||||||");

                                voteButton.setOnClickListener(v -> {
                                    localPeopleVoteHelper.insertOnePeopleVote(HomePageFragment.curGroupNumber, HomePageFragment.curUser, curLocation.locationName,
                                                    curLocation.locationAddress, "true", curLocation.voteStartTime)
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(new CompletableObserver() {
                                                @Override
                                                public void onSubscribe(Disposable d) {

                                                }

                                                @Override
                                                public void onComplete() {

                                                    localVoteHelper.updateTheVotes(HomePageFragment.curGroupNumber, curLocation.locationName,
                                                                    curLocation.locationAddress, curLocation.voteStartTime)
                                                            .subscribeOn(Schedulers.io())
                                                            .observeOn(AndroidSchedulers.mainThread())
                                                            .subscribe(new CompletableObserver() {
                                                                @Override
                                                                public void onSubscribe(Disposable d) {

                                                                }

                                                                @Override
                                                                public void onComplete() {
                                                                    Toast.makeText(getActivity(), "Voted successfully", Toast.LENGTH_SHORT).show();

                                                                    localVoteHelper.getNumberOfNotesOneGroupOneTime(HomePageFragment.curGroupNumber, curLocation.voteStartTime,
                                                                                    curLocation.locationName,
                                                                                    curLocation.locationAddress)
                                                                            .subscribeOn(Schedulers.io())
                                                                            .observeOn(AndroidSchedulers.mainThread())
                                                                            .subscribe(new SingleObserver<Integer>() {
                                                                                @Override
                                                                                public void onSubscribe(Disposable d) {

                                                                                }

                                                                                @Override
                                                                                public void onSuccess(Integer integer) {
                                                                                    voteButton.setBackgroundResource(R.drawable.grey_background);

                                                                                    voteButton.setText(integer + " voted");
                                                                                    voteButton.setTextColor(Color.parseColor("#333232"));

                                                                                    voteButton.setClickable(false);

                                                                                    ifVoted = true;

                                                                                    EventBus.getDefault().post(new VotedEvent());
                                                                                }

                                                                                @Override
                                                                                public void onError(Throwable e) {
                                                                                    voteButton.setBackgroundResource(R.drawable.grey_background);

                                                                                    voteButton.setText(0 + " voted");
                                                                                    voteButton.setTextColor(Color.parseColor("#333232"));

                                                                                    voteButton.setClickable(false);
                                                                                }
                                                                            });
                                                                }

                                                                @Override
                                                                public void onError(Throwable e) {

                                                                }
                                                            });
                                                }

                                                @Override
                                                public void onError(Throwable e) {
                                                    Toast.makeText(getActivity(), "You have voted for this place previously", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                });

                            }
                        });
            }

            oneReasonView.setText(curLocation.comments.split(";newOneComment;")[0]);


            String[] split = curLocation.photos.split(";newOne;");

            if (split ==  null || split.length == 0) {


                img1View.setVisibility(View.INVISIBLE);
                img2View.setVisibility(View.INVISIBLE);
                img3View.setVisibility(View.INVISIBLE);

            } else if (split.length == 1) {

                img1View.setImageBitmap(base64ToBitmap(split[0]));
                img2View.setVisibility(View.INVISIBLE);
                img3View.setVisibility(View.INVISIBLE);
            } else if (split.length == 2) {

                img1View.setImageBitmap(base64ToBitmap(split[0]));
                img2View.setImageBitmap(base64ToBitmap(split[1]));
                img3View.setVisibility(View.INVISIBLE);
            } else {

                img1View.setImageBitmap(base64ToBitmap(split[0]));
                img2View.setImageBitmap(base64ToBitmap(split[1]));
                img3View.setImageBitmap(base64ToBitmap(split[2]));
            }

            dialogView.setOnClickListener(v -> {
                PlaceDetailActivity.clickStart(getContext(), curLocation.locationName,
                        curLocation.locationAddress, curLocation.locationTag == null ? null : curLocation.locationTag, true);
            });

        } else {
            LocationEntity curLocation = curLocationList.get(curId);

            placeNameView.setText(item.getTitle());
            placeAddressView.setText(curLocation.locationAddress);

            if (curLocation.locationTag == null || curLocation.locationTag.isEmpty()) {
                placeTypeView.setVisibility(View.INVISIBLE);
            } else {
                placeTypeView.setText("· " + curLocation.locationTag.substring(0, 1).toUpperCase() +
                        curLocation.locationTag.substring(1).toLowerCase());
            }

            if (!HomePageFragment.ifHaveCurrentVote) {
                voteButton.setVisibility(View.GONE);
            } else {
                voteButton.setOnClickListener(v -> {

                });
            }

            oneReasonView.setText(curLocation.comments.split(";newOneComment;")[0]);

            String[] split = null;

            if (curLocation.photos != null) {
                split = curLocation.photos.split(";newOne;");
            }


            if (split ==  null || split.length == 0) {


                img1View.setVisibility(View.INVISIBLE);
                img2View.setVisibility(View.INVISIBLE);
                img3View.setVisibility(View.INVISIBLE);

            } else if (split.length == 1) {

                img1View.setImageBitmap(base64ToBitmap(split[0]));
                img2View.setVisibility(View.INVISIBLE);
                img3View.setVisibility(View.INVISIBLE);
            } else if (split.length == 2) {

                img1View.setImageBitmap(base64ToBitmap(split[0]));
                img2View.setImageBitmap(base64ToBitmap(split[1]));
                img3View.setVisibility(View.INVISIBLE);
            } else {

                img1View.setImageBitmap(base64ToBitmap(split[0]));
                img2View.setImageBitmap(base64ToBitmap(split[1]));
                img3View.setImageBitmap(base64ToBitmap(split[2]));
            }

            dialogView.setOnClickListener(v -> {
                PlaceDetailActivity.clickStart(getContext(), curLocation.locationName,
                        curLocation.locationAddress, curLocation.locationTag == null ? null : curLocation.locationTag, true);
            });

        }


        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(dialogView);
        dialog.show();




        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {

                if (ifVoted) {
                    ifVoted = false;

                    localLocationHelper.getAllLocation(HomePageFragment.curGroupNumber)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new SingleObserver<List<LocationEntity>>() {
                                @Override
                                public void onSubscribe(Disposable d) {

                                    map.moveCamera(CameraUpdateFactory.newLatLngBounds(curBounds, PADDING));
                                }

                                @Override
                                public void onSuccess(List<LocationEntity> locationEntities) {
                                    HomePageFragment.curLocationList = locationEntities;


                                }

                                @Override
                                public void onError(Throwable e) {

                                }
                            });

                    localVoteHelper.getHasCurrentVote(HomePageFragment.addCreateTime(), HomePageFragment.curGroupNumber)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new SingleObserver<VoteEntity>() {
                                @Override
                                public void onSubscribe(Disposable d) {

                                    map.moveCamera(CameraUpdateFactory.newLatLngBounds(curBounds, PADDING));
                                }

                                @Override
                                public void onSuccess(VoteEntity voteEntity) {
                                    HomePageFragment.currentVote = voteEntity;



                                    HomePageFragment.ifHaveCurrentVote = true;


                                    localVoteHelper.getAllVotes(HomePageFragment.curGroupNumber, voteEntity.voteStartTime)
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(new SingleObserver<List<VoteEntity>>() {
                                                @Override
                                                public void onSubscribe(Disposable d) {

                                                }

                                                @Override
                                                public void onSuccess(List<VoteEntity> voteEntities) {
                                                    HomePageFragment.curVoteList = voteEntities;
                                                }

                                                @Override
                                                public void onError(Throwable e) {

                                                }
                                            });

                                }

                                @Override
                                public void onError(Throwable e) {

                                    HomePageFragment.ifHaveCurrentVote = false;


                                }
                            });
                }




            }
        });






        return false;
    }




    // Judge the selected item
    class MySelectedListener implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {

            if (HomePageFragment.ifHaveCurrentVote) {
                List<Double> latitudes = new ArrayList<>();
                List<Double> longitudes = new ArrayList<>();

                if (parent.getTag().equals("spinner2") ) {
                    vote = voteSpinner.getItemAtPosition(i).toString();
                } else {
                    type = typeSpinner.getItemAtPosition(i).toString();
                }

                if ((type == null || type.equals("All")) && (vote == null || vote.equals("No limitation"))) {
                    map.clear();
                    clusterManager.clearItems();

                    int curId = 0;

                    items.clear();

                    for (VoteEntity location : HomePageFragment.curVoteList) {
                        LatLng curLocation = new LatLng(Double.parseDouble(location.latitude),
                                Double.parseDouble(location.longitude));

                        latitudes.add(Double.parseDouble(location.latitude));
                        longitudes.add(Double.parseDouble(location.longitude));

                        /*
                        map.addMarker(new MarkerOptions()
                                .title(location.locationName)
                                .position(curLocation)
                                .snippet(String.valueOf(curId)));

                         */

                        items.add(new MyItem(curLocation.latitude, curLocation.longitude, location.locationName, String.valueOf(curId)));
                        clusterManager.addItems(items);
                        clusterManager.cluster();

                        curId++;

                    }
                } else if (type.equals("Food") && (vote == null || vote.equals("No limitation"))) {
                    map.clear();
                    clusterManager.clearItems();

                    int curId = 0;

                    items.clear();

                    for (VoteEntity location : HomePageFragment.curVoteList) {


                        if (location.locationTag != null && location.locationTag.equals("food")) {
                            LatLng curLocation = new LatLng(Double.parseDouble(location.latitude),
                                    Double.parseDouble(location.longitude));

                            latitudes.add(Double.parseDouble(location.latitude));
                            longitudes.add(Double.parseDouble(location.longitude));

                            /*
                        map.addMarker(new MarkerOptions()
                                .title(location.locationName)
                                .position(curLocation)
                                .snippet(String.valueOf(curId)));

                         */
                            items.add(new MyItem(curLocation.latitude, curLocation.longitude, location.locationName, String.valueOf(curId)));
                            clusterManager.addItems(items);
                            clusterManager.cluster();


                        }

                        curId++;
                    }
                } else if (type.equals("Drink") && (vote == null || vote.equals("No limitation"))) {
                    map.clear();

                    int curId = 0;

                    clusterManager.clearItems();
                    items.clear();

                    for (VoteEntity location : HomePageFragment.curVoteList) {

                        if (location.locationTag != null && location.locationTag.equals("drink")) {
                            LatLng curLocation = new LatLng(Double.parseDouble(location.latitude),
                                    Double.parseDouble(location.longitude));

                            latitudes.add(Double.parseDouble(location.latitude));
                            longitudes.add(Double.parseDouble(location.longitude));

                            /*
                        map.addMarker(new MarkerOptions()
                                .title(location.locationName)
                                .position(curLocation)
                                .snippet(String.valueOf(curId)));

                         */

                            items.add(new MyItem(curLocation.latitude, curLocation.longitude, location.locationName, String.valueOf(curId)));
                            clusterManager.addItems(items);
                            clusterManager.cluster();


                        }
                        curId++;
                    }
                } else if (type.equals("Indoor Activity") && (vote == null || vote.equals("No limitation"))) {
                    map.clear();

                    int curId = 0;

                    clusterManager.clearItems();
                    items.clear();


                    for (VoteEntity location : HomePageFragment.curVoteList) {

                        if (location.locationTag != null && location.locationTag.equals("indoor_activity")) {
                            LatLng curLocation = new LatLng(Double.parseDouble(location.latitude),
                                    Double.parseDouble(location.longitude));

                            latitudes.add(Double.parseDouble(location.latitude));
                            longitudes.add(Double.parseDouble(location.longitude));

                            /*
                        map.addMarker(new MarkerOptions()
                                .title(location.locationName)
                                .position(curLocation)
                                .snippet(String.valueOf(curId)));

                         */

                            items.add(new MyItem(curLocation.latitude, curLocation.longitude, location.locationName, String.valueOf(curId)));
                            clusterManager.addItems(items);
                            clusterManager.cluster();


                        }
                        curId++;
                    }
                } else if (type.equals("Outdoor Activity") && (vote == null || vote.equals("No limitation"))) {
                    map.clear();

                    clusterManager.clearItems();
                    items.clear();

                    int curId = 0;


                    for (VoteEntity location : HomePageFragment.curVoteList) {

                        if (location.locationTag != null && location.locationTag.equals("outdoor_activity")) {
                            LatLng curLocation = new LatLng(Double.parseDouble(location.latitude),
                                    Double.parseDouble(location.longitude));

                            latitudes.add(Double.parseDouble(location.latitude));
                            longitudes.add(Double.parseDouble(location.longitude));

                            /*
                        map.addMarker(new MarkerOptions()
                                .title(location.locationName)
                                .position(curLocation)
                                .snippet(String.valueOf(curId)));

                         */

                            items.add(new MyItem(curLocation.latitude, curLocation.longitude, location.locationName, String.valueOf(curId)));
                            clusterManager.addItems(items);
                            clusterManager.cluster();


                        }
                        curId++;
                    }
                } else if ((type == null || type.equals("All")) && vote.equals("> 2 votes")) {

                    map.clear();

                    clusterManager.clearItems();
                    items.clear();

                    int curId = 0;

                    for (VoteEntity location : HomePageFragment.curVoteList) {

                        if (location.numberOfVotes > 2) {
                            LatLng curLocation = new LatLng(Double.parseDouble(location.latitude),
                                    Double.parseDouble(location.longitude));

                            latitudes.add(Double.parseDouble(location.latitude));
                            longitudes.add(Double.parseDouble(location.longitude));

                            /*
                        map.addMarker(new MarkerOptions()
                                .title(location.locationName)
                                .position(curLocation)
                                .snippet(String.valueOf(curId)));

                         */

                            items.add(new MyItem(curLocation.latitude, curLocation.longitude, location.locationName, String.valueOf(curId)));
                            clusterManager.addItems(items);
                            clusterManager.cluster();

                        }

                        curId++;
                    }

                } else if (type.equals("Food") && vote.equals("> 2 votes")) {

                    map.clear();

                    int curId = 0;
                    items.clear();
                    clusterManager.clearItems();


                    for (VoteEntity location : HomePageFragment.curVoteList) {

                        if (location.locationTag != null && location.locationTag.equals("food") && location.numberOfVotes > 2 ) {
                            LatLng curLocation = new LatLng(Double.parseDouble(location.latitude),
                                    Double.parseDouble(location.longitude));

                            latitudes.add(Double.parseDouble(location.latitude));
                            longitudes.add(Double.parseDouble(location.longitude));

                            /*
                        map.addMarker(new MarkerOptions()
                                .title(location.locationName)
                                .position(curLocation)
                                .snippet(String.valueOf(curId)));

                         */

                            items.add(new MyItem(curLocation.latitude, curLocation.longitude, location.locationName, String.valueOf(curId)));
                            clusterManager.addItems(items);
                            clusterManager.cluster();

                        }

                        curId++;
                    }

                } else if (type.equals("Drink") && vote.equals("> 2 votes")) {

                    map.clear();

                    int curId = 0;

                    items.clear();
                    clusterManager.clearItems();

                    for (VoteEntity location : HomePageFragment.curVoteList) {

                        if (location.locationTag != null && location.locationTag.equals("drink") && location.numberOfVotes > 2 ) {
                            LatLng curLocation = new LatLng(Double.parseDouble(location.latitude),
                                    Double.parseDouble(location.longitude));

                            latitudes.add(Double.parseDouble(location.latitude));
                            longitudes.add(Double.parseDouble(location.longitude));

                            /*
                        map.addMarker(new MarkerOptions()
                                .title(location.locationName)
                                .position(curLocation)
                                .snippet(String.valueOf(curId)));

                         */

                            items.add(new MyItem(curLocation.latitude, curLocation.longitude, location.locationName, String.valueOf(curId)));
                            clusterManager.addItems(items);
                            clusterManager.cluster();


                        }

                        curId++;
                    }

                } else if (type.equals("Indoor Activity") && vote.equals("> 2 votes")) {

                    map.clear();

                    int curId = 0;

                    items.clear();
                    clusterManager.clearItems();

                    for (VoteEntity location : HomePageFragment.curVoteList) {

                        if (location.locationTag != null && location.locationTag.equals("indoor_activity") && location.numberOfVotes > 2 ) {
                            LatLng curLocation = new LatLng(Double.parseDouble(location.latitude),
                                    Double.parseDouble(location.longitude));

                            latitudes.add(Double.parseDouble(location.latitude));
                            longitudes.add(Double.parseDouble(location.longitude));

                            /*
                        map.addMarker(new MarkerOptions()
                                .title(location.locationName)
                                .position(curLocation)
                                .snippet(String.valueOf(curId)));

                         */

                            items.add(new MyItem(curLocation.latitude, curLocation.longitude, location.locationName, String.valueOf(curId)));
                            clusterManager.addItems(items);
                            clusterManager.cluster();


                        }

                        curId++;
                    }

                } else if (type.equals("Outdoor Activity") && vote.equals("> 2 votes")) {

                    map.clear();

                    int curId = 0;

                    items.clear();
                    clusterManager.clearItems();

                    for (VoteEntity location : HomePageFragment.curVoteList) {

                        if (location.locationTag != null && location.locationTag.equals("outdoor_activity") && location.numberOfVotes > 2 ) {
                            LatLng curLocation = new LatLng(Double.parseDouble(location.latitude),
                                    Double.parseDouble(location.longitude));

                            latitudes.add(Double.parseDouble(location.latitude));
                            longitudes.add(Double.parseDouble(location.longitude));

                            /*
                        map.addMarker(new MarkerOptions()
                                .title(location.locationName)
                                .position(curLocation)
                                .snippet(String.valueOf(curId)));

                         */

                            items.add(new MyItem(curLocation.latitude, curLocation.longitude, location.locationName, String.valueOf(curId)));
                            clusterManager.addItems(items);
                            clusterManager.cluster();


                        }

                        curId++;
                    }
                } else if ((type == null || type.equals("All")) && vote.equals("> 5 votes")) {

                    map.clear();

                    int curId = 0;

                    items.clear();
                    clusterManager.clearItems();

                    for (VoteEntity location : HomePageFragment.curVoteList) {

                        if (location.numberOfVotes > 5) {
                            LatLng curLocation = new LatLng(Double.parseDouble(location.latitude),
                                    Double.parseDouble(location.longitude));

                            latitudes.add(Double.parseDouble(location.latitude));
                            longitudes.add(Double.parseDouble(location.longitude));

                            /*
                        map.addMarker(new MarkerOptions()
                                .title(location.locationName)
                                .position(curLocation)
                                .snippet(String.valueOf(curId)));

                         */

                            items.add(new MyItem(curLocation.latitude, curLocation.longitude, location.locationName, String.valueOf(curId)));
                            clusterManager.addItems(items);
                            clusterManager.cluster();

                        }

                        curId++;

                    }

                } else if (type.equals("Food") && vote.equals("> 5 votes")) {

                    map.clear();

                    int curId = 0;

                    clusterManager.clearItems();
                    items.clear();


                    for (VoteEntity location : HomePageFragment.curVoteList) {

                        if (location.locationTag != null && location.locationTag.equals("food") && location.numberOfVotes > 5 ) {
                            LatLng curLocation = new LatLng(Double.parseDouble(location.latitude),
                                    Double.parseDouble(location.longitude));

                            latitudes.add(Double.parseDouble(location.latitude));
                            longitudes.add(Double.parseDouble(location.longitude));

                            /*
                        map.addMarker(new MarkerOptions()
                                .title(location.locationName)
                                .position(curLocation)
                                .snippet(String.valueOf(curId)));

                         */

                            items.add(new MyItem(curLocation.latitude, curLocation.longitude, location.locationName, String.valueOf(curId)));
                            clusterManager.addItems(items);
                            clusterManager.cluster();


                        }

                        curId++;
                    }

                } else if (type.equals("Drink") && vote.equals("> 5 votes")) {

                    map.clear();

                    int curId = 0;

                    clusterManager.clearItems();
                    items.clear();


                    for (VoteEntity location : HomePageFragment.curVoteList) {

                        if (location.locationTag != null && location.locationTag.equals("drink") && location.numberOfVotes > 5 ) {
                            LatLng curLocation = new LatLng(Double.parseDouble(location.latitude),
                                    Double.parseDouble(location.longitude));

                            latitudes.add(Double.parseDouble(location.latitude));
                            longitudes.add(Double.parseDouble(location.longitude));

                            /*
                        map.addMarker(new MarkerOptions()
                                .title(location.locationName)
                                .position(curLocation)
                                .snippet(String.valueOf(curId)));

                         */

                            items.add(new MyItem(curLocation.latitude, curLocation.longitude, location.locationName, String.valueOf(curId)));
                            clusterManager.addItems(items);
                            clusterManager.cluster();


                        }

                        curId++;
                    }

                } else if (type.equals("Indoor Activity") && vote.equals("> 5 votes")) {

                    map.clear();

                    int curId = 0;

                    clusterManager.clearItems();
                    items.clear();


                    for (VoteEntity location : HomePageFragment.curVoteList) {

                        if (location.locationTag != null && location.locationTag.equals("indoor_activity") && location.numberOfVotes > 5 ) {
                            LatLng curLocation = new LatLng(Double.parseDouble(location.latitude),
                                    Double.parseDouble(location.longitude));

                            latitudes.add(Double.parseDouble(location.latitude));
                            longitudes.add(Double.parseDouble(location.longitude));

                            /*
                        map.addMarker(new MarkerOptions()
                                .title(location.locationName)
                                .position(curLocation)
                                .snippet(String.valueOf(curId)));

                         */

                            items.add(new MyItem(curLocation.latitude, curLocation.longitude, location.locationName, String.valueOf(curId)));
                            clusterManager.addItems(items);
                            clusterManager.cluster();


                        }

                        curId++;
                    }

                } else if (type.equals("Outdoor Activity") && vote.equals("> 5 votes")) {

                    map.clear();

                    int curId = 0;

                    clusterManager.clearItems();
                    items.clear();


                    for (VoteEntity location : HomePageFragment.curVoteList) {

                        if (location.locationTag != null && location.locationTag.equals("outdoor_activity") && location.numberOfVotes > 5 ) {
                            LatLng curLocation = new LatLng(Double.parseDouble(location.latitude),
                                    Double.parseDouble(location.longitude));

                            latitudes.add(Double.parseDouble(location.latitude));
                            longitudes.add(Double.parseDouble(location.longitude));

                            /*
                        map.addMarker(new MarkerOptions()
                                .title(location.locationName)
                                .position(curLocation)
                                .snippet(String.valueOf(curId)));

                         */

                            items.add(new MyItem(curLocation.latitude, curLocation.longitude, location.locationName, String.valueOf(curId)));
                            clusterManager.addItems(items);
                            clusterManager.cluster();


                        }

                        curId++;
                    }
                } else if ((type == null || type.equals("All")) && vote.equals("> 10 votes")) {

                    map.clear();

                    int curId = 0;

                    clusterManager.clearItems();
                    items.clear();

                    for (VoteEntity location : HomePageFragment.curVoteList) {

                        if (location.numberOfVotes > 10) {
                            LatLng curLocation = new LatLng(Double.parseDouble(location.latitude),
                                    Double.parseDouble(location.longitude));

                            latitudes.add(Double.parseDouble(location.latitude));
                            longitudes.add(Double.parseDouble(location.longitude));

                            /*
                        map.addMarker(new MarkerOptions()
                                .title(location.locationName)
                                .position(curLocation)
                                .snippet(String.valueOf(curId)));

                         */

                            items.add(new MyItem(curLocation.latitude, curLocation.longitude, location.locationName, String.valueOf(curId)));
                            clusterManager.addItems(items);
                            clusterManager.cluster();


                        }

                        curId++;

                    }

                } else if (type.equals("Food") && vote.equals("> 10 votes")) {

                    map.clear();

                    int curId = 0;

                    clusterManager.clearItems();
                    items.clear();


                    for (VoteEntity location : HomePageFragment.curVoteList) {

                        if (location.locationTag != null && location.locationTag.equals("food") && location.numberOfVotes > 10 ) {
                            LatLng curLocation = new LatLng(Double.parseDouble(location.latitude),
                                    Double.parseDouble(location.longitude));

                            latitudes.add(Double.parseDouble(location.latitude));
                            longitudes.add(Double.parseDouble(location.longitude));

                            /*
                        map.addMarker(new MarkerOptions()
                                .title(location.locationName)
                                .position(curLocation)
                                .snippet(String.valueOf(curId)));

                         */

                            items.add(new MyItem(curLocation.latitude, curLocation.longitude, location.locationName, String.valueOf(curId)));
                            clusterManager.addItems(items);
                            clusterManager.cluster();


                        }

                        curId++;
                    }

                } else if (type.equals("Drink") && vote.equals("> 10 votes")) {

                    map.clear();

                    int curId = 0;

                    clusterManager.clearItems();
                    items.clear();

                    for (VoteEntity location : HomePageFragment.curVoteList) {

                        if (location.locationTag != null && location.locationTag.equals("drink") && location.numberOfVotes > 10 ) {
                            LatLng curLocation = new LatLng(Double.parseDouble(location.latitude),
                                    Double.parseDouble(location.longitude));

                            latitudes.add(Double.parseDouble(location.latitude));
                            longitudes.add(Double.parseDouble(location.longitude));

                            /*
                        map.addMarker(new MarkerOptions()
                                .title(location.locationName)
                                .position(curLocation)
                                .snippet(String.valueOf(curId)));

                         */

                            items.add(new MyItem(curLocation.latitude, curLocation.longitude, location.locationName, String.valueOf(curId)));
                            clusterManager.addItems(items);
                            clusterManager.cluster();


                        }

                        curId++;
                    }

                } else if (type.equals("Indoor Activity") && vote.equals("> 10 votes")) {

                    map.clear();

                    int curId = 0;

                    clusterManager.clearItems();
                    items.clear();


                    for (VoteEntity location : HomePageFragment.curVoteList) {

                        if (location.locationTag != null && location.locationTag.equals("indoor_activity") && location.numberOfVotes > 10 ) {
                            LatLng curLocation = new LatLng(Double.parseDouble(location.latitude),
                                    Double.parseDouble(location.longitude));

                            latitudes.add(Double.parseDouble(location.latitude));
                            longitudes.add(Double.parseDouble(location.longitude));

                            /*
                        map.addMarker(new MarkerOptions()
                                .title(location.locationName)
                                .position(curLocation)
                                .snippet(String.valueOf(curId)));

                         */

                            items.add(new MyItem(curLocation.latitude, curLocation.longitude, location.locationName, String.valueOf(curId)));
                            clusterManager.addItems(items);
                            clusterManager.cluster();

                        }

                        curId++;
                    }

                } else if (type.equals("Outdoor Activity") && vote.equals("> 10 votes")) {

                    map.clear();

                    int curId = 0;

                    clusterManager.clearItems();
                    items.clear();


                    for (VoteEntity location : HomePageFragment.curVoteList) {

                        if (location.locationTag != null && location.locationTag.equals("outdoor_activity") && location.numberOfVotes > 10 ) {
                            LatLng curLocation = new LatLng(Double.parseDouble(location.latitude),
                                    Double.parseDouble(location.longitude));

                            latitudes.add(Double.parseDouble(location.latitude));
                            longitudes.add(Double.parseDouble(location.longitude));

                            /*
                        map.addMarker(new MarkerOptions()
                                .title(location.locationName)
                                .position(curLocation)
                                .snippet(String.valueOf(curId)));

                         */

                            items.add(new MyItem(curLocation.latitude, curLocation.longitude, location.locationName, String.valueOf(curId)));
                            clusterManager.addItems(items);
                            clusterManager.cluster();


                        }

                        curId++;
                    }
                } else if ((type == null || type.equals("All")) && vote.equals("> 20 votes")) {

                    map.clear();

                    int curId = 0;

                    clusterManager.clearItems();
                    items.clear();

                    for (VoteEntity location : HomePageFragment.curVoteList) {

                        if (location.numberOfVotes > 20) {
                            LatLng curLocation = new LatLng(Double.parseDouble(location.latitude),
                                    Double.parseDouble(location.longitude));

                            latitudes.add(Double.parseDouble(location.latitude));
                            longitudes.add(Double.parseDouble(location.longitude));

                            /*
                        map.addMarker(new MarkerOptions()
                                .title(location.locationName)
                                .position(curLocation)
                                .snippet(String.valueOf(curId)));

                         */

                            items.add(new MyItem(curLocation.latitude, curLocation.longitude, location.locationName, String.valueOf(curId)));
                            clusterManager.addItems(items);
                            clusterManager.cluster();


                        }

                        curId++;

                    }

                } else if (type.equals("Food") && vote.equals("> 20 votes")) {

                    map.clear();

                    int curId = 0;

                    clusterManager.clearItems();
                    items.clear();


                    for (VoteEntity location : HomePageFragment.curVoteList) {

                        if (location.locationTag != null && location.locationTag.equals("food") && location.numberOfVotes > 20 ) {
                            LatLng curLocation = new LatLng(Double.parseDouble(location.latitude),
                                    Double.parseDouble(location.longitude));

                            latitudes.add(Double.parseDouble(location.latitude));
                            longitudes.add(Double.parseDouble(location.longitude));

                            /*
                        map.addMarker(new MarkerOptions()
                                .title(location.locationName)
                                .position(curLocation)
                                .snippet(String.valueOf(curId)));

                         */

                            items.add(new MyItem(curLocation.latitude, curLocation.longitude, location.locationName, String.valueOf(curId)));
                            clusterManager.addItems(items);
                            clusterManager.cluster();


                        }

                        curId++;
                    }

                } else if (type.equals("Drink") && vote.equals("> 20 votes")) {

                    map.clear();

                    int curId = 0;

                    clusterManager.clearItems();
                    items.clear();


                    for (VoteEntity location : HomePageFragment.curVoteList) {

                        if (location.locationTag != null && location.locationTag.equals("drink") && location.numberOfVotes > 20 ) {
                            LatLng curLocation = new LatLng(Double.parseDouble(location.latitude),
                                    Double.parseDouble(location.longitude));

                            latitudes.add(Double.parseDouble(location.latitude));
                            longitudes.add(Double.parseDouble(location.longitude));

                            /*
                        map.addMarker(new MarkerOptions()
                                .title(location.locationName)
                                .position(curLocation)
                                .snippet(String.valueOf(curId)));

                         */

                            items.add(new MyItem(curLocation.latitude, curLocation.longitude, location.locationName, String.valueOf(curId)));
                            clusterManager.addItems(items);
                            clusterManager.cluster();


                        }

                        curId++;
                    }

                } else if (type.equals("Indoor Activity") && vote.equals("> 20 votes")) {

                    map.clear();

                    int curId = 0;

                    clusterManager.clearItems();
                    items.clear();


                    for (VoteEntity location : HomePageFragment.curVoteList) {

                        if (location.locationTag != null && location.locationTag.equals("indoor_activity") && location.numberOfVotes > 20 ) {
                            LatLng curLocation = new LatLng(Double.parseDouble(location.latitude),
                                    Double.parseDouble(location.longitude));

                            latitudes.add(Double.parseDouble(location.latitude));
                            longitudes.add(Double.parseDouble(location.longitude));

                            /*
                        map.addMarker(new MarkerOptions()
                                .title(location.locationName)
                                .position(curLocation)
                                .snippet(String.valueOf(curId)));

                         */

                            items.add(new MyItem(curLocation.latitude, curLocation.longitude, location.locationName, String.valueOf(curId)));
                            clusterManager.addItems(items);
                            clusterManager.cluster();


                        }

                        curId++;
                    }

                } else if (type.equals("Outdoor Activity") && vote.equals("> 20 votes")) {

                    map.clear();

                    int curId = 0;

                    clusterManager.clearItems();
                    items.clear();


                    for (VoteEntity location : HomePageFragment.curVoteList) {

                        if (location.locationTag != null && location.locationTag.equals("outdoor_activity") && location.numberOfVotes > 20 ) {
                            LatLng curLocation = new LatLng(Double.parseDouble(location.latitude),
                                    Double.parseDouble(location.longitude));

                            latitudes.add(Double.parseDouble(location.latitude));
                            longitudes.add(Double.parseDouble(location.longitude));

                            /*
                        map.addMarker(new MarkerOptions()
                                .title(location.locationName)
                                .position(curLocation)
                                .snippet(String.valueOf(curId)));

                         */

                            items.add(new MyItem(curLocation.latitude, curLocation.longitude, location.locationName, String.valueOf(curId)));
                            clusterManager.addItems(items);
                            clusterManager.cluster();

                        }

                        curId++;
                    }
                } else if ((type == null || type.equals("All")) && vote.equals("> 0 votes")) {

                    map.clear();

                    int curId = 0;

                    clusterManager.clearItems();
                    items.clear();

                    for (VoteEntity location : HomePageFragment.curVoteList) {

                        if (location.numberOfVotes > 0) {
                            LatLng curLocation = new LatLng(Double.parseDouble(location.latitude),
                                    Double.parseDouble(location.longitude));

                            latitudes.add(Double.parseDouble(location.latitude));
                            longitudes.add(Double.parseDouble(location.longitude));

                            /*
                        map.addMarker(new MarkerOptions()
                                .title(location.locationName)
                                .position(curLocation)
                                .snippet(String.valueOf(curId)));

                         */

                            items.add(new MyItem(curLocation.latitude, curLocation.longitude, location.locationName, String.valueOf(curId)));
                            clusterManager.addItems(items);
                            clusterManager.cluster();


                        }

                        curId++;

                    }

                } else if (type.equals("Food") && vote.equals("> 0 votes")) {

                    map.clear();

                    int curId = 0;

                    clusterManager.clearItems();
                    items.clear();


                    for (VoteEntity location : HomePageFragment.curVoteList) {

                        if (location.locationTag != null && location.locationTag.equals("food") && location.numberOfVotes > 0 ) {
                            LatLng curLocation = new LatLng(Double.parseDouble(location.latitude),
                                    Double.parseDouble(location.longitude));

                            latitudes.add(Double.parseDouble(location.latitude));
                            longitudes.add(Double.parseDouble(location.longitude));

                            /*
                        map.addMarker(new MarkerOptions()
                                .title(location.locationName)
                                .position(curLocation)
                                .snippet(String.valueOf(curId)));

                         */

                            items.add(new MyItem(curLocation.latitude, curLocation.longitude, location.locationName, String.valueOf(curId)));
                            clusterManager.addItems(items);
                            clusterManager.cluster();


                        }

                        curId++;
                    }

                } else if (type.equals("Drink") && vote.equals("> 0 votes")) {

                    map.clear();

                    int curId = 0;

                    clusterManager.clearItems();
                    items.clear();


                    for (VoteEntity location : HomePageFragment.curVoteList) {

                        if (location.locationTag != null && location.locationTag.equals("drink") && location.numberOfVotes > 0 ) {
                            LatLng curLocation = new LatLng(Double.parseDouble(location.latitude),
                                    Double.parseDouble(location.longitude));

                            latitudes.add(Double.parseDouble(location.latitude));
                            longitudes.add(Double.parseDouble(location.longitude));

                            /*
                        map.addMarker(new MarkerOptions()
                                .title(location.locationName)
                                .position(curLocation)
                                .snippet(String.valueOf(curId)));

                         */

                            items.add(new MyItem(curLocation.latitude, curLocation.longitude, location.locationName, String.valueOf(curId)));
                            clusterManager.addItems(items);
                            clusterManager.cluster();


                        }

                        curId++;
                    }

                } else if (type.equals("Indoor Activity") && vote.equals("> 0 votes")) {

                    map.clear();

                    int curId = 0;


                    clusterManager.clearItems();
                    items.clear();


                    for (VoteEntity location : HomePageFragment.curVoteList) {

                        if (location.locationTag != null && location.locationTag.equals("indoor_activity") && location.numberOfVotes > 0 ) {
                            LatLng curLocation = new LatLng(Double.parseDouble(location.latitude),
                                    Double.parseDouble(location.longitude));

                            latitudes.add(Double.parseDouble(location.latitude));
                            longitudes.add(Double.parseDouble(location.longitude));

                            /*
                        map.addMarker(new MarkerOptions()
                                .title(location.locationName)
                                .position(curLocation)
                                .snippet(String.valueOf(curId)));

                         */

                            items.add(new MyItem(curLocation.latitude, curLocation.longitude, location.locationName, String.valueOf(curId)));
                            clusterManager.addItems(items);
                            clusterManager.cluster();


                        }

                        curId++;
                    }

                } else if (type.equals("Outdoor Activity") && vote.equals("> 0 votes")) {

                    map.clear();

                    int curId = 0;

                    clusterManager.clearItems();
                    items.clear();


                    for (VoteEntity location : HomePageFragment.curVoteList) {

                        if (location.locationTag != null && location.locationTag.equals("outdoor_activity") && location.numberOfVotes > 0 ) {
                            LatLng curLocation = new LatLng(Double.parseDouble(location.latitude),
                                    Double.parseDouble(location.longitude));

                            latitudes.add(Double.parseDouble(location.latitude));
                            longitudes.add(Double.parseDouble(location.longitude));

                            /*
                        map.addMarker(new MarkerOptions()
                                .title(location.locationName)
                                .position(curLocation)
                                .snippet(String.valueOf(curId)));

                         */

                            items.add(new MyItem(curLocation.latitude, curLocation.longitude, location.locationName, String.valueOf(curId)));
                            clusterManager.addItems(items);
                            clusterManager.cluster();


                        }

                        curId++;

                    }

                }

                if (latitudes.isEmpty() || longitudes.isEmpty()) {
                    Toast.makeText(getActivity(), "No such location found", Toast.LENGTH_SHORT).show();

                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                            new LatLng(lastKnownLocation.getLatitude(),
                                    lastKnownLocation.getLongitude()), DEFAULT_ZOOM));

                } else {

                    double minLatitude, maxLatitude, minLongitude, maxLongitude;

                    minLatitude = Collections.min(latitudes);
                    maxLatitude =  Collections.max(latitudes);
                    minLongitude = Collections.min(longitudes);
                    maxLongitude = Collections.max(longitudes);

                    LatLngBounds bounds = new LatLngBounds(new LatLng(minLatitude, minLongitude),
                            new LatLng(maxLatitude, maxLongitude));

                    curBounds = bounds;

                    // Position the map's camera at the location of the marker.
                    map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, PADDING));
                }
            }



            if (!HomePageFragment.ifHaveCurrentVote) {
                if (parent.getTag().equals("spinner1")) {

                    List<Double> latitudes = new ArrayList<>();
                    List<Double> longitudes = new ArrayList<>();

                    type = typeSpinner.getItemAtPosition(i).toString();

                    if (type.equals("All")) {

                        if (HomePageFragment.ifHaveCurrentVote) {

                            map.clear();

                            int curId = 0;

                            clusterManager.clearItems();
                            items.clear();


                            for (VoteEntity location : HomePageFragment.curVoteList) {

                                LatLng curLocation = new LatLng(Double.parseDouble(location.latitude),
                                        Double.parseDouble(location.longitude));

                                latitudes.add(Double.parseDouble(location.latitude));
                                longitudes.add(Double.parseDouble(location.longitude));

                                /*
                        map.addMarker(new MarkerOptions()
                                .title(location.locationName)
                                .position(curLocation)
                                .snippet(String.valueOf(curId)));

                         */
                                items.add(new MyItem(curLocation.latitude, curLocation.longitude, location.locationName, String.valueOf(curId)));
                                clusterManager.addItems(items);
                                clusterManager.cluster();

                                curId++;

                            }

                        } else {

                            map.clear();

                            int curId = 0;

                            clusterManager.clearItems();
                            items.clear();


                            for (LocationEntity location : HomePageFragment.curLocationList) {
                                LatLng curLocation = new LatLng(Double.parseDouble(location.latitude),
                                        Double.parseDouble(location.longitude));

                                latitudes.add(Double.parseDouble(location.latitude));
                                longitudes.add(Double.parseDouble(location.longitude));

                                /*
                        map.addMarker(new MarkerOptions()
                                .title(location.locationName)
                                .position(curLocation)
                                .snippet(String.valueOf(curId)));

                         */
                                items.add(new MyItem(curLocation.latitude, curLocation.longitude, location.locationName, String.valueOf(curId)));
                                clusterManager.addItems(items);
                                clusterManager.cluster();

                                curId++;



                            }

                        }


                    } else if (type.equals("Food")) {

                        if (HomePageFragment.ifHaveCurrentVote) {
                            map.clear();

                            int curId = 0;

                            clusterManager.clearItems();
                            items.clear();


                            for (VoteEntity location : HomePageFragment.curVoteList) {

                                if (location.locationTag != null && location.locationTag.equals("food")) {
                                    LatLng curLocation = new LatLng(Double.parseDouble(location.latitude),
                                            Double.parseDouble(location.longitude));

                                    latitudes.add(Double.parseDouble(location.latitude));
                                    longitudes.add(Double.parseDouble(location.longitude));

                                    /*
                        map.addMarker(new MarkerOptions()
                                .title(location.locationName)
                                .position(curLocation)
                                .snippet(String.valueOf(curId)));

                         */

                                    items.add(new MyItem(curLocation.latitude, curLocation.longitude, location.locationName, String.valueOf(curId)));
                                    clusterManager.addItems(items);
                                    clusterManager.cluster();

                                }

                                curId++;
                            }
                        } else {
                            map.clear();

                            int curId = 0;

                            clusterManager.clearItems();
                            items.clear();


                            for (LocationEntity location : HomePageFragment.curLocationList) {

                                if (location.locationTag != null && location.locationTag.equals("food")) {
                                    LatLng curLocation = new LatLng(Double.parseDouble(location.latitude),
                                            Double.parseDouble(location.longitude));

                                    latitudes.add(Double.parseDouble(location.latitude));
                                    longitudes.add(Double.parseDouble(location.longitude));

                                    /*
                        map.addMarker(new MarkerOptions()
                                .title(location.locationName)
                                .position(curLocation)
                                .snippet(String.valueOf(curId)));

                         */

                                    items.add(new MyItem(curLocation.latitude, curLocation.longitude, location.locationName, String.valueOf(curId)));
                                    clusterManager.addItems(items);
                                    clusterManager.cluster();


                                }

                                curId++;

                            }
                        }



                    } else if (type.equals("Drink")) {

                        if (HomePageFragment.ifHaveCurrentVote) {
                            map.clear();

                            int curId = 0;

                            clusterManager.clearItems();
                            items.clear();


                            for (VoteEntity location : HomePageFragment.curVoteList) {

                                if (location.locationTag != null && location.locationTag.equals("drink")) {
                                    LatLng curLocation = new LatLng(Double.parseDouble(location.latitude),
                                            Double.parseDouble(location.longitude));

                                    latitudes.add(Double.parseDouble(location.latitude));
                                    longitudes.add(Double.parseDouble(location.longitude));

                                    /*
                        map.addMarker(new MarkerOptions()
                                .title(location.locationName)
                                .position(curLocation)
                                .snippet(String.valueOf(curId)));

                         */

                                    items.add(new MyItem(curLocation.latitude, curLocation.longitude, location.locationName, String.valueOf(curId)));
                                    clusterManager.addItems(items);
                                    clusterManager.cluster();


                                }

                                curId++;

                            }

                        } else {
                            map.clear();

                            int curId = 0;

                            clusterManager.clearItems();
                            items.clear();


                            for (LocationEntity location : HomePageFragment.curLocationList) {

                                if (location.locationTag != null && location.locationTag.equals("drink")) {
                                    LatLng curLocation = new LatLng(Double.parseDouble(location.latitude),
                                            Double.parseDouble(location.longitude));

                                    latitudes.add(Double.parseDouble(location.latitude));
                                    longitudes.add(Double.parseDouble(location.longitude));

                                    /*
                        map.addMarker(new MarkerOptions()
                                .title(location.locationName)
                                .position(curLocation)
                                .snippet(String.valueOf(curId)));

                         */

                                    items.add(new MyItem(curLocation.latitude, curLocation.longitude, location.locationName, String.valueOf(curId)));
                                    clusterManager.addItems(items);
                                    clusterManager.cluster();



                                }

                                curId++;

                            }
                        }


                    } else if (type.equals("Indoor Activity")) {

                        if (HomePageFragment.ifHaveCurrentVote) {
                            map.clear();

                            int curId = 0;

                            clusterManager.clearItems();
                            items.clear();


                            for (VoteEntity location : HomePageFragment.curVoteList) {

                                if (location.locationTag != null && location.locationTag.equals("indoor_activity")) {
                                    LatLng curLocation = new LatLng(Double.parseDouble(location.latitude),
                                            Double.parseDouble(location.longitude));

                                    latitudes.add(Double.parseDouble(location.latitude));
                                    longitudes.add(Double.parseDouble(location.longitude));

                                    /*
                        map.addMarker(new MarkerOptions()
                                .title(location.locationName)
                                .position(curLocation)
                                .snippet(String.valueOf(curId)));

                         */
                                    items.add(new MyItem(curLocation.latitude, curLocation.longitude, location.locationName, String.valueOf(curId)));
                                    clusterManager.addItems(items);
                                    clusterManager.cluster();

                                }

                                curId++;

                            }
                        } else {
                            map.clear();

                            int curId = 0;

                            clusterManager.clearItems();
                            items.clear();



                            for (LocationEntity location : HomePageFragment.curLocationList) {

                                if (location.locationTag != null && location.locationTag.equals("indoor_activity")) {
                                    LatLng curLocation = new LatLng(Double.parseDouble(location.latitude),
                                            Double.parseDouble(location.longitude));

                                    latitudes.add(Double.parseDouble(location.latitude));
                                    longitudes.add(Double.parseDouble(location.longitude));

                                    /*
                        map.addMarker(new MarkerOptions()
                                .title(location.locationName)
                                .position(curLocation)
                                .snippet(String.valueOf(curId)));

                         */

                                    items.add(new MyItem(curLocation.latitude, curLocation.longitude, location.locationName, String.valueOf(curId)));
                                    clusterManager.addItems(items);
                                    clusterManager.cluster();



                                }

                                curId++;

                            }
                        }



                    } else if (type.equals("Outdoor Activity")) {

                        if (HomePageFragment.ifHaveCurrentVote) {

                            map.clear();

                            int curId = 0;

                            clusterManager.clearItems();
                            items.clear();

                            for (VoteEntity location : HomePageFragment.curVoteList) {

                                if (location.locationTag != null && location.locationTag.equals("outdoor_activity")) {
                                    LatLng curLocation = new LatLng(Double.parseDouble(location.latitude),
                                            Double.parseDouble(location.longitude));

                                    latitudes.add(Double.parseDouble(location.latitude));
                                    longitudes.add(Double.parseDouble(location.longitude));

                                    /*
                        map.addMarker(new MarkerOptions()
                                .title(location.locationName)
                                .position(curLocation)
                                .snippet(String.valueOf(curId)));

                         */

                                    items.add(new MyItem(curLocation.latitude, curLocation.longitude, location.locationName, String.valueOf(curId)));
                                    clusterManager.addItems(items);
                                    clusterManager.cluster();


                                }

                                curId++;

                            }

                        } else {

                            map.clear();

                            int curId = 0;

                            clusterManager.clearItems();
                            items.clear();


                            for (LocationEntity location : HomePageFragment.curLocationList) {

                                if (location.locationTag != null && location.locationTag.equals("outdoor_activity")) {
                                    LatLng curLocation = new LatLng(Double.parseDouble(location.latitude),
                                            Double.parseDouble(location.longitude));

                                    latitudes.add(Double.parseDouble(location.latitude));
                                    longitudes.add(Double.parseDouble(location.longitude));

                                    /*
                        map.addMarker(new MarkerOptions()
                                .title(location.locationName)
                                .position(curLocation)
                                .snippet(String.valueOf(curId)));

                         */

                                    items.add(new MyItem(curLocation.latitude, curLocation.longitude, location.locationName, String.valueOf(curId)));
                                    clusterManager.addItems(items);
                                    clusterManager.cluster();

                                }

                                curId++;

                            }

                        }


                    }

                    if (latitudes.isEmpty() || longitudes.isEmpty()) {
                        Toast.makeText(getActivity(), "No such location found", Toast.LENGTH_SHORT).show();

                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                new LatLng(lastKnownLocation.getLatitude(),
                                        lastKnownLocation.getLongitude()), DEFAULT_ZOOM));

                    } else {

                        double minLatitude, maxLatitude, minLongitude, maxLongitude;

                        minLatitude = Collections.min(latitudes);
                        maxLatitude =  Collections.max(latitudes);
                        minLongitude = Collections.min(longitudes);
                        maxLongitude = Collections.max(longitudes);

                        LatLngBounds bounds = new LatLngBounds(new LatLng(minLatitude, minLongitude),
                                new LatLng(maxLatitude, maxLongitude));

                        curBounds = bounds;

                        // Position the map's camera at the location of the marker.
                        map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, PADDING));
                    }


                }
            }

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            if (parent.getTag().equals("spinner1")) {
                type = "All";
            } else {
                vote = "No limitation";
            }
        }
    }


    /**
     * Prompts the user for permission to use the device location.
     */
    // [START maps_current_place_location_permission]
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }
    // [END maps_current_place_location_permission]

    /**
     * Handles the result of the request for location permissions.
     */
    // [START maps_current_place_on_request_permissions_result]
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        if (requestCode
                == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationPermissionGranted = true;
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
        updateLocationUI();
    }
    // [END maps_current_place_on_request_permissions_result]


    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    // [START maps_current_place_update_location_ui]
    @SuppressLint("MissingPermission")
    private void updateLocationUI() {
        if (map == null) {
            return;
        }
        try {
            if (locationPermissionGranted) {
                map.setMyLocationEnabled(true);
                map.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                map.setMyLocationEnabled(false);
                map.getUiSettings().setMyLocationButtonEnabled(false);
                lastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }
    // [END maps_current_place_update_location_ui]



    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    // [START maps_current_place_get_device_location]
    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (locationPermissionGranted) {
                @SuppressLint("MissingPermission") Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(getActivity(), new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            lastKnownLocation = task.getResult();

                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());

                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }
    // [END maps_current_place_get_device_location]


    @Override
    public void onResume() {
        super.onResume();


        if (!HomePageFragment.ifHaveCurrentVote) {
            voteFilterLayout.setVisibility(View.GONE);
        }

        /*

        localLocationHelper.getAllLocation(HomePageFragment.curGroupNumber)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<LocationEntity>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(List<LocationEntity> locationEntities) {
                        HomePageFragment.curLocationList = locationEntities;

                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });

        localVoteHelper.getHasCurrentVote(HomePageFragment.addCreateTime(), HomePageFragment.curGroupNumber)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<VoteEntity>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(VoteEntity voteEntity) {
                        HomePageFragment.currentVote = voteEntity;



                        HomePageFragment.ifHaveCurrentVote = true;


                        localVoteHelper.getAllVotes(HomePageFragment.curGroupNumber, voteEntity.voteStartTime)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new SingleObserver<List<VoteEntity>>() {
                                    @Override
                                    public void onSubscribe(Disposable d) {

                                    }

                                    @Override
                                    public void onSuccess(List<VoteEntity> voteEntities) {
                                        HomePageFragment.curVoteList = voteEntities;
                                    }

                                    @Override
                                    public void onError(Throwable e) {

                                    }
                                });

                    }

                    @Override
                    public void onError(Throwable e) {

                        HomePageFragment.ifHaveCurrentVote = false;

                        EventBus.getDefault().post(new TimeOutEvent());

                    }
                });
        */


        setSpinner();

        Places.initialize(getActivity().getApplicationContext(), BuildConfig.MAPS_API_KEY);
        placesClient = Places.createClient(getActivity());

        // Construct a FusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        // Build the map.
        // [START maps_current_place_map_fragment]
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map_this);
        mapFragment.getMapAsync( this);


        // here

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        EventBus.getDefault().unregister(this);


    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(VotedEvent event) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEventHome(TimeOutEvent event) {

        clusterManager.clearItems();
        items.clear();

        map.clear();

        ((MainActivity)getActivity()).reloadMap();
        System.out.println("()*)(*)*()*)*()*)(*()*()*())*(*)(*(*)(*)(*)*)(((MainActivity)getActivity()).reloadMap()((MainActivity)getActivity()).reloadMap()");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    int voteNum = data.getIntExtra("data_return", 0);

                    if (voteNum != 0 ) {

                        voteButton.setBackgroundResource(R.drawable.grey_background);

                        voteButton.setText(voteNum + " voted");
                        voteButton.setTextColor(Color.parseColor("#333232"));

                        voteButton.setClickable(false);
                    }

                }
                break;
            default:
        }
    }
}