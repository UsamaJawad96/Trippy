package com.example.darkage.trippy;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

///**
// * A simple {@link Fragment} subclass.
// * Activities that contain this fragment must implement the
// * {@link TripBuddyListFragment.OnFragmentInteractionListener} interface
// * to handle interaction events.
// * Use the {@link TripBuddyListFragment#newInstance} factory method to
// * create an instance of this fragment.
// */
public class TripBuddyListFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    ListView ls;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    PopupMenu popup;
    int clickpos;
    FirebaseUser curruser;
    public TripBuddyListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TripBuddyListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TripBuddyListFragment newInstance(String param1, String param2) {
        TripBuddyListFragment fragment = new TripBuddyListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_trip_buddy_list, container, false);
        TripViewActivity ac= (TripViewActivity) getActivity();
        TripClass t=ac.gettp();
        if (t!=null)
            setListView(t,view);
        return view;
    }

//    private OnFragmentInteractionListener mListener;
//    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }
//
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }
//
//    /**
//     * This interface must be implemented by activities that contain this
//     * fragment to allow an interaction in this fragment to be communicated
//     * to the activity and potentially other fragments contained in that
//     * activity.
//     * <p>
//     * See the Android Training lesson <a href=
//     * "http://developer.android.com/training/basics/fragments/communicating.html"
//     * >Communicating with Other Fragments</a> for more information.
//     */
//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        void onFragmentInteraction(Uri uri);
//    }

    public void setListView(final TripClass trip, final View view){
        curruser=FirebaseAuth.getInstance().getCurrentUser();

        ls=view.findViewById(R.id.lsbuddy);
        ArrayAdapter adap = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, trip.getTripMembers());
        ls.setAdapter(adap);

        if (curruser.getUid().equals(trip.getTrip_admin_id())) {

            ls.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                               int pos, long id) {
                    // TODO Auto-generated method stub


                    clickpos = pos;
                    popup = new PopupMenu(view.getContext(), ls.getChildAt(pos), Gravity.CENTER_HORIZONTAL);

                    PopupMenu.OnMenuItemClickListener listener = new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            //your corresponding code comes here
                            switch (item.getItemId()) {
                                case R.id.remove:
                                    TripViewActivity ac = (TripViewActivity) getActivity();
                                    ac.removeBuddy(trip.getTripMembers().get(clickpos));
                                    ArrayAdapter adap = new ArrayAdapter<String>(getActivity(),
                                            android.R.layout.simple_list_item_1, ac.gettp().getTripMembers());
                                    ls.setAdapter(adap);

                                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                                    DatabaseReference myRef = database.getReference();
                                    myRef.child("trips").child(ac.gettp().getTripId()).child("tripMembers").setValue(ac.gettp().getTripMembers());
                                    return true;
                                default:
                                    return false;
                            }
                        }
                    };

                    popup.setOnMenuItemClickListener(listener);
                    MenuInflater inflater = popup.getMenuInflater();
                    inflater.inflate(R.menu.actions, popup.getMenu());

                    if (!trip.getTripMembers().get(pos).equals(curruser.getDisplayName())) {
                        popup.show();
                    }
                    //Toast.makeText(getActivity(), trip.getTripMembers().get(pos), Toast.LENGTH_LONG).show();

                    return true;
                }
            });

        }

    }


}
