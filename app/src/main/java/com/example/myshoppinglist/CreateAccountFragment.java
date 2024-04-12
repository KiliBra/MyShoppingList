package com.example.myshoppinglist;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.myshoppinglist.databinding.FragmentCreateAccountBinding;

public class CreateAccountFragment extends Fragment implements UserLoggedInListener {

    private FragmentCreateAccountBinding binding;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentCreateAccountBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //
        binding.buttonCreateAccount.setOnClickListener(v ->
                {
                    User user = new User();
                    user._nameID = binding.textentryFieldUsername.getText().toString();
                    user._password = binding.textentryFieldPassword.getText().toString();

                    user.addLoginEndedListener(this);

                    user.createUserOnFirestore();

                    user.Login();
                }
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onLoginEnded(boolean wasLoginSuccessful) {
        Log.d(TAG, "Login was: " + (wasLoginSuccessful ? "Successful" : "Not successful"));

        if (wasLoginSuccessful)
        {
            NavHostFragment.findNavController(CreateAccountFragment.this)
                    .navigate(R.id.action_SecondFragment_to_listGroupFragment);
        }
    }

}