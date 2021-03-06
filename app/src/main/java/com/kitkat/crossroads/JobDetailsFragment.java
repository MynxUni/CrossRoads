package com.kitkat.crossroads;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kitkat.crossroads.Jobs.BidInformation;
import com.kitkat.crossroads.Jobs.JobDetailsActivity;
import com.kitkat.crossroads.Jobs.JobInformation;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link JobDetailsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link JobDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class JobDetailsFragment extends Fragment
{

    private TextView jobName, jobDescription, jobSize, jobType, jobColDate, jobColTime, jobFrom, jobTo;
    private Button buttonBid;
    private EditText editTextBid;

    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private DataSnapshot bidReference;


    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public JobDetailsFragment()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment JobDetailsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static JobDetailsFragment newInstance(String param1, String param2)
    {
        JobDetailsFragment fragment = new JobDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_job_details, container, false);

        Bundle bundle = getArguments();

        databaseReference = FirebaseDatabase.getInstance().getReference();

        final JobInformation jobInformation = (JobInformation) bundle.getSerializable("Job");

        jobName = (TextView) view.findViewById(R.id.textViewJobName1);
        jobDescription = (TextView) view.findViewById(R.id.textViewJobDescription1);
        jobSize = (TextView) view.findViewById(R.id.textViewJobSize1);
        jobType = (TextView) view.findViewById(R.id.textViewJobType1);
        jobColDate = (TextView) view.findViewById(R.id.textViewJobColDate1);
        jobColTime = (TextView) view.findViewById(R.id.textViewJobColTime1);
        jobFrom = (TextView) view.findViewById(R.id.textViewJobFrom1);
        jobTo = (TextView) view.findViewById(R.id.textViewJobTo1);
        editTextBid = (EditText) view.findViewById(R.id.editTextBid);
        buttonBid = (Button) view.findViewById(R.id.buttonBid);

        jobName.setText(jobInformation.getAdvertName().toString());
        jobDescription.setText(jobInformation.getAdvertDescription().toString());
        jobSize.setText(jobInformation.getJobSize().toString());
        jobType.setText(jobInformation.getJobType().toString());
        jobColDate.setText(jobInformation.getCollectionDate());
        jobColTime.setText(jobInformation.getCollectionTime());
        jobFrom.setText(jobInformation.getColTown().toString());
        jobTo.setText(jobInformation.getDelTown().toString());


        buttonBid.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(TextUtils.isEmpty(editTextBid.getText()))
                {
                    editTextBid.setHint("Please enter a bid!");
                    editTextBid.setHintTextColor(Color.RED);
                }
                else {

                    saveBidInformation();
                }
            }
        });





        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();

        databaseReference.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                bidReference = dataSnapshot.child("Bids");

                Iterable<DataSnapshot> jobListSnapShot = bidReference.getChildren();



                for (DataSnapshot ds : jobListSnapShot)
                {
                    if(ds.getKey().toString().equals(jobInformation.getJobID())) {

                        Iterable<DataSnapshot> bidListSnapShot = ds.getChildren();

                        for (DataSnapshot ds1 : bidListSnapShot) {
                            BidInformation b = ds1.getValue(BidInformation.class);

                            String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

                            if (b.getUserID().equals(currentUser)) {
                                buttonBid.setClickable(false);
                                buttonBid.setHighlightColor(Color.GRAY);
                                editTextBid.setText("Bid already placed!");
                                editTextBid.setClickable(false);
                                editTextBid.setKeyListener(null);
                            }

                        }
                    }
                }






            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });






        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri)
    {
        if (mListener != null)
        {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener)
        {
            mListener = (OnFragmentInteractionListener) context;
        } else
        {
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener
    {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void saveBidInformation()
    {
        Bundle bundle = getArguments();

        JobInformation jobInformation = (JobInformation) bundle.getSerializable("Job");

        String userBid = editTextBid.getText().toString().trim();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        user.getUid();

        String userID = user.getUid();


        String jobID = jobInformation.getJobID().toString().trim();

        BidInformation bidInformation = new BidInformation(userID, userBid);

        databaseReference.child("Bids").child(jobID).push().setValue(bidInformation);

        startActivity(new Intent(getActivity(), CrossRoads.class));
    }

    private void customToastMessage(String message)
    {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
}
