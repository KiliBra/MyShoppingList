package com.example.myshoppinglist;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.myshoppinglist.databinding.FragmentListBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListFragment extends Fragment {

    private FragmentListBinding binding;
    DocumentReference _listDocumentReference;
    CollectionReference _itemsCollectionReference;

    Grid_ItemAdapter gridItemAdapter;



    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        binding = FragmentListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String listID = getArguments().getString("ListID");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
            //References Lists from logged in User
        DocumentReference userDocumentReference = db.collection("users")
                .document(User._loggedInUser._nameID);

        _listDocumentReference = userDocumentReference.collection("Lists").document(listID);
        _itemsCollectionReference = _listDocumentReference.collection("Items");
        List<DocumentReference> itemDocumentReferences = new ArrayList<>();
        //Creates and fills list for each entry in database
        _itemsCollectionReference
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                itemDocumentReferences.add(document.getReference());
                            }
                            //Reverse Listorder for same reasons as before
                            Collections.reverse(itemDocumentReferences);
                            gridItemAdapter = new Grid_ItemAdapter(getActivity(), itemDocumentReferences);
                            binding.ItemsGridView.setAdapter(gridItemAdapter);
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

        binding.buttonNewItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gridItemAdapter.addItem(_listDocumentReference.getId());
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        binding = null;
    }
}