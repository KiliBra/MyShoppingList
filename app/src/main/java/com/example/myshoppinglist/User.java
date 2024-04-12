package com.example.myshoppinglist;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//Interface that allows Subscribing to loginEnded Event for other classes
interface UserLoggedInListener {
    void onLoginEnded(boolean wasLoginSuccessful);
}
//Helper-Class to manage Userdata
public class User {
    //Login in User that is accessible from anywhere in our app
    static User _loggedInUser;
    //Also equals UserID in Database
    String _nameID;
    String _password;

    //List of subscribed Listeners
    private List<UserLoggedInListener> _listeners = new ArrayList<>();

    public void addLoginEndedListener(UserLoggedInListener listenerToAdd){
        _listeners.add(listenerToAdd);
    }

    public void removeListener(UserLoggedInListener listenerToRemove){
        _listeners.remove(listenerToRemove);
    }
    //Function that gets called when we create new Account
    public void createUserOnFirestore(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Dictionary for uploading userdata to Database
        Map<String, Object> user = new HashMap<>();
        user.put("Name", _nameID);
        user.put("Password", _password);

        // Add a new document with a generated ID
        db.collection("users").document(_nameID)
                //Add/Set user to database
                .set(user)
                // Logs unsuccessful attempts to create user in database
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }

    public void Login(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        //Get snapshot of Users in the Database
         db.collection("users").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    //Compares textinput from App-User with every User in the snapshot until match
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot userDocument : task.getResult()) {
                                String UserID = userDocument.getString("Name");
                                String UserPassword = userDocument.getString("Password");

                                // When match found, notify subscribers login was successful
                                if (_nameID.equals(UserID) && _password.equals(UserPassword)) {
                                    //Store reference for logged in user
                                    _loggedInUser = User.this;

                                    for (UserLoggedInListener listener : _listeners) {
                                        listener.onLoginEnded(true);
                                        return;
                                    }
                                }
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }

                        for (UserLoggedInListener listener : _listeners){
                            listener.onLoginEnded(false);
                        }
                    }
                });
    }


}
