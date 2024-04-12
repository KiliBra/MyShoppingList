package com.example.myshoppinglist;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Class for storing buttons for individual lists
// https://youtu.be/aRgSrJO40z8?si=xrNIIKTXC3BOvtab
public class Grid_ListAdapter extends BaseAdapter {

    Context _context;
    public List<String> _lists = new ArrayList<>();

    LayoutInflater _inflater;

    Grid_ListAdapter(Context context, List<String> lists)
    {
        this._context = context;
        this._lists = lists;
    }
    //Helper Function for Listupload to Database and populate GridView
    public void addList(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> list = new HashMap<>();
        list.put("ListName", "List");

        // Add a new document with a generated ID
        DocumentReference userDocumentReference = db.collection("users")
                .document(User._loggedInUser._nameID);
        // Adds new List to Database
        userDocumentReference.collection("Lists")
                .add(list)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    // Adds created List to grid on success
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                        _lists.add(0, documentReference.getId());
                        notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    @Override
    public int getCount() {
        return _lists.size();
    }

    @Override
    public Object getItem(int position) {
        return _lists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    //
    //Necessary implementation to create GridListElements
    //
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (_inflater == null){
            _inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        if (convertView == null) {
            convertView = _inflater.inflate(R.layout.grid_list, null);
        }

        //Sends Event/intent to ListGroupFragment to open clicked list
        //
        Button button = convertView.findViewById(R.id.button_list);
        button.setOnClickListener(v -> {
                    Intent intent = new Intent("android.intent.action.OpenList");
                    //Adds info to event which list to navigate to
                    intent.putExtra("ListID", _lists.get(position));
                    _context.sendBroadcast(intent);
                }
        );

        return convertView;
    }

}
