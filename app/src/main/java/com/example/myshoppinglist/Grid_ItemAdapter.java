package com.example.myshoppinglist;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Grid_ItemAdapter extends BaseAdapter {

    Context _context;
    List<DocumentReference> _itemsDocumentReference = new ArrayList<>();

    LayoutInflater _inflater;


    Grid_ItemAdapter(Context context, List<DocumentReference> itemsDocumentReference) {
       _context = context;
       _itemsDocumentReference = itemsDocumentReference;
    }

    public void addItem(String listID){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> item = new HashMap<>();
        item.put("ItemName", "");

        // Add a new document with a generated ID
        DocumentReference listDocumentReference = db.collection("users")
                .document(User._loggedInUser._nameID).collection("Lists").document(listID);
        //Adds new Item to Database
        Task<DocumentReference> itemDocumentReferenceTask = listDocumentReference.collection("Items")
                .add(item)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                        _itemsDocumentReference.add(0, documentReference);
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
        return _itemsDocumentReference.size();
    }

    @Override
    public Object getItem(int position) {
        return _itemsDocumentReference.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (_inflater == null){
            _inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        if (convertView == null){
            convertView = _inflater.inflate(R.layout.grid_item, null);
        }

        //References which item is being worked on
        DocumentReference itemDocumentReference = _itemsDocumentReference.get(position);
        //Set text for item from database
        EditText editText = convertView.findViewById(R.id.textView_itemName);
        itemDocumentReference.get().addOnCompleteListener(t -> {
                if (t.isSuccessful()) {
                    DocumentSnapshot Snapshot = t.getResult();
                    String ItemNameString = Snapshot.getString("ItemName");
                    editText.setText(ItemNameString);
                }
        });

        //Updates Database after focus on item is lost
        //This was due to ".OnTextChange" calling multiple times
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus){
                    EditText ViewText = (EditText) v;
                    DocumentReference itemDocumentReference = _itemsDocumentReference.get(position);

                    Map<String, Object> item = new HashMap<>();
                    item.put("ItemName", ViewText.getText().toString());

                    itemDocumentReference.set(item)
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error writing document", e);
                                }
                            });
                }
            }
        });

        return convertView;
    }

}
