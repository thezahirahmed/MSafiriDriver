package com.eleganzit.msafiridriver.subfragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.eleganzit.msafiridriver.R;
import com.eleganzit.msafiridriver.activity.PayDetail;
import com.eleganzit.msafiridriver.activity.TripDetail;
import com.eleganzit.msafiridriver.adapter.PickUpAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class PayStatementFragment extends Fragment {
    ListView listView;

    public PayStatementFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_pay_statement, container, false);
        listView = v.findViewById(R.id.listview);

        listView.setAdapter(new PickUpAdapter(getContext()));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(getActivity(), PayDetail.class);
                startActivity(intent);
            }
        });

        return v;
    }

}
