package com.example.myshoppinglist;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.myshoppinglist.databinding.FragmentListGroupBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListGroupFragment extends Fragment{

    private FragmentListGroupBinding binding;

    private ListGroupBroadcastReceiver _broadcastReceiver;

    private CollectionReference _listsCollectionReference;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState  ) {
        binding = FragmentListGroupBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        List<String> Lists = new ArrayList<>();
        //Create and set grid for lists
        Grid_ListAdapter gridListAdapter = new Grid_ListAdapter(getActivity(), Lists);
        binding.ListsGridView.setAdapter(gridListAdapter);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        _listsCollectionReference = db.collection("users")
                .document(User._loggedInUser._nameID).collection("Lists");

        _listsCollectionReference.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    //Creates Grid entries for each ListID of logged in User
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot listDocument : task.getResult()) {
                                gridListAdapter._lists.add(listDocument.getId());
                            }
                            // Firebase sorts the entries in reverse, so we reverse grid here
                            Collections.reverse(gridListAdapter._lists);
                            gridListAdapter.notifyDataSetChanged();
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

        binding.buttonNewList.setOnClickListener(v ->
                {
                    gridListAdapter.addList();
                }
        );

            //Listens to open List Event from Grid_ListAdapter
        _broadcastReceiver = new ListGroupBroadcastReceiver() {
            @Override
            //Bundle sends information from the intent
            public void onReceive(Context context, Intent intent) {
                Bundle bundle = new Bundle();
                bundle.putString("ListID", intent.getStringExtra("ListID"));
                //Navigate into ListFragment with corresponding ListID
                NavHostFragment.findNavController(ListGroupFragment.this)
                        .navigate(R.id.action_listGroupFragment_to_listFragment, bundle);
            }
        };
        // Register the BroadcastReceiver
        IntentFilter OpenListIntentFilter = new IntentFilter();
        OpenListIntentFilter.addAction("android.intent.action.OpenList");
        getActivity().registerReceiver(_broadcastReceiver, OpenListIntentFilter, Context.RECEIVER_EXPORTED);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;

        if (getActivity() != null && _broadcastReceiver != null)
        {
            getActivity().unregisterReceiver(_broadcastReceiver);
            _broadcastReceiver = null;
        }
    }
}