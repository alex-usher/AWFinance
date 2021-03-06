package com.example.alex_.project;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class SettingsFragment extends Fragment {

  @Nullable
  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.fragment_settings, container, false);

    GridLayout gl = v.findViewById(R.id.change_pin);
    gl.setClickable(true);
    gl.setFocusable(true);
    gl.setOnClickListener(
        v1 ->
            getFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new ChangePinFragment())
                .addToBackStack(null)
                .commit());

    gl.setOnLongClickListener(
        v12 -> {
          Toast.makeText(getContext(), "Change PIN", Toast.LENGTH_SHORT).show();
          return true;
        });

    return v;
  }
}
