package com.example.myshoppinglist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.myshoppinglist.databinding.FragmentLoginBinding;

public class LoginFragment extends Fragment implements UserLoggedInListener {


    //Every Fragment has its onw Binding to corresponding XML file
    private FragmentLoginBinding binding;


    //Initializing the binding, for later referencing on buttons, texts etc.
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    //Prepare Button-Click Listener
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Takes Values from textfields to login User
        binding.buttonFirst.setOnClickListener(v -> {
                    User user = new User();
                    user._nameID = binding.textentryFieldUsername.getText().toString();
                    user._password = binding.textentryFieldPassword.getText().toString();
                //Subscribe to login-ended Event
                    user.addLoginEndedListener(this);

                    // Login() in User-Class will broadcast the login-ended Event
                    // that will call our onLoginEnded() function
                    user.Login();
                }
        );
        //Navigate to CreateAccount Fragment
        binding.buttonCreateAccount.setOnClickListener(v ->{
                    NavHostFragment.findNavController(LoginFragment.this)
                            .navigate(R.id.action_FirstFragment_to_SecondFragment);
                }
        );
    }

    //When leaving Fragment, "kill" binding to the corresponding XML
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    //Checks if login was successful
    @Override
    public void onLoginEnded(boolean wasLoginSuccessful) {
        //Navtigate to Lists Fragment when successful
        if (wasLoginSuccessful){
            NavHostFragment.findNavController(LoginFragment.this)
                    .navigate(R.id.action_FirstFragment_to_listGroupFragment);
        }
        // Show Error message when not successful
        else {
            TextView FirstFragmentDescription = (TextView) getActivity().findViewById(R.id.text_login_error);
            FirstFragmentDescription.setVisibility(View.VISIBLE);
        }
    }
}