package com.sukaiyi.hardbang.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.beardedhen.androidbootstrap.AwesomeTextView;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.sukaiyi.hardbang.R;
import com.sukaiyi.hardbang.activity_personal_info;
import com.sukaiyi.hardbang.bean.Organization;
import com.sukaiyi.hardbang.bean.Post;

import cn.bmob.newim.BmobIM;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

import java.util.Date;
import java.util.List;

import com.sukaiyi.hardbang.view.*;
import static com.sukaiyi.hardbang.R.layout.fragment_main_menu;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainMenuFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainMenuFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private BootstrapButton exitBtn;
    private AwesomeTextView aTextView;
    private LinearLayout linear;

    public MainMenuFragment() {
        // Required empty public constructor
    }

    public static MainMenuFragment newInstance(OnFragmentInteractionListener listener) {
        MainMenuFragment fragment = new MainMenuFragment();
        fragment.mListener = listener;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(fragment_main_menu, container, false);
//        View rootView = inflater.inflate(R.layout.activity_personal_info,container,false);

        linear = (LinearLayout)rootView.findViewById(R.id.linearLayout2);
        linear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), activity_personal_info.class);
                startActivity(intent);
            }
        });

        aTextView = (AwesomeTextView)rootView.findViewById(R.id.person_info);
        aTextView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), activity_personal_info.class);
                startActivity(intent);

            }
        });

        exitBtn = (BootstrapButton) rootView.findViewById(R.id.exit);
        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BmobUser user = BmobUser.getCurrentUser(getContext());
                if(user!=null){
                    BmobIM.getInstance().disConnect();
                    user.logOut(getContext());
                    if (mListener != null) {
                        Bundle bundle = new Bundle();
                        bundle.putInt("action",OnFragmentInteractionListener.ACTION_LOGOUT);
                        mListener.onFragmentInteraction(bundle);
                    }
                }
            }
        });
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


}
