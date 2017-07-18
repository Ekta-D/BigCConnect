package com.bigc.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bigc.datastorage.Preferences;
import com.bigc.general.classes.DbConstants;
import com.bigc.general.classes.GoogleAnalyticsHelper;
import com.bigc.general.classes.Utils;
import com.bigc.interfaces.LoadImageObservable;
import com.bigc.interfaces.LoadImageObserver;
import com.bigc.interfaces.SignupInterface;
import com.bigc_connect.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ProfilePicFragment extends Fragment implements
        View.OnClickListener, LoadImageObserver {

    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;

    public static ProfilePicFragment newInstance() {
        return new ProfilePicFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_addprofilepic, container,
                false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        GoogleAnalyticsHelper.sendScreenViewGoogleAnalytics(getActivity(),
                "SignUp Select-Profile-Picture Screen");
        //  databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        view.findViewById(R.id.skipOption).setOnClickListener(this);
        view.findViewById(R.id.chooseFromGalleryButton)
                .setOnClickListener(this);
        view.findViewById(R.id.takeAPhotoButton).setOnClickListener(this);

        System.gc();
    }

    @Override
    public void onResume() {
        super.onResume();
        ((LoadImageObservable) getActivity()).setObserver(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.skipOption:
                showNextStep();
                break;
            case R.id.chooseFromGalleryButton:
                GoogleAnalyticsHelper.setClickedAction(getActivity(),
                        "Choose Picture From Gallery");
                Utils.choosePicFromGallery(getActivity(), false);
                break;
            case R.id.takeAPhotoButton:
                GoogleAnalyticsHelper.setClickedAction(getActivity(),
                        "Take Photo Option");
                Utils.launchCamera(getActivity());
                break;
        }
    }

    private void showNextStep() {
        if (Preferences.getInstance(getActivity()).getInt(DbConstants.TYPE) == 1) {
            ((SignupInterface) getActivity()).replaceFragment(SurvivorSearch
                    .newInstance());
        } else {
            ((SignupInterface) getActivity())
                    .replaceFragment(RibbonSelectionFragment.newInstance());
        }
        Utils.saveObjectId(databaseReference);

//        if (ParseUser.getCurrentUser().getInt(DbConstants.TYPE) == Constants.USER_TYPE.SUPPORTER
//                .ordinal()) {
//            ((SignupInterface) getActivity()).replaceFragment(SurvivorSearch
//                    .newInstance());
//        } else {
//            ((SignupInterface) getActivity())
//                    .replaceFragment(RibbonSelectionFragment.newInstance());
//        }
        /**************************************/
//        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
//        String current_uid = currentUser.getUid();
//        databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(current_uid);
//        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//
//                String key = dataSnapshot.getKey();
//                Map<Object, Object> values = (Map<Object, Object>) dataSnapshot.getValue();
//
//
//                Log.i("datab", dataSnapshot.toString());
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });


    }

    @Override
    public void onNotify() {
        showNextStep();
    }

}