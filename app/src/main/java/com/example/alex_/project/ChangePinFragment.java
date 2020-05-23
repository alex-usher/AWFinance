package com.example.alex_.project;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ChangePinFragment extends Fragment {

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_change_pin, container, false);
		Button b = v.findViewById(R.id.submit);
		b.setClickable(true);
		b.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				changePin();
			}
		});
		return v;
	}

	private void changePin() {
		EditText oldPin = getActivity().findViewById(R.id.currentPin);
		EditText newPin = getActivity().findViewById(R.id.newPin);
		EditText confirmPin = getActivity().findViewById(R.id.confirmPin);

		String old = oldPin.getText().toString();
		String pnew = newPin.getText().toString();
		String confirm = confirmPin.getText().toString();

		if(old.isEmpty() || pnew.isEmpty() || confirm.isEmpty()) {
			Toast.makeText(getContext(), "Please complete the form", Toast.LENGTH_SHORT).show();
		} else {
			if(FileHandler.checkContents(getContext(), Crypt.hash512(old), FileHandler.DEFAULT_FILENAME)) {
				if(Crypt.hash512(pnew).equals(Crypt.hash512(confirm))) {
					FileHandler.writeToFile(getContext(), FileHandler.DEFAULT_FILENAME, Crypt.hash512(pnew));
					Toast.makeText(getContext(), "PIN successfully updated", Toast.LENGTH_SHORT).show();
					changeFragment();
				} else {
					Toast.makeText(getContext(), "PINs do not match", Toast.LENGTH_SHORT).show();
				}
			} else {
				Toast.makeText(getContext(), "Current PIN is incorrect", Toast.LENGTH_SHORT).show();
			}
		}
	}

	private void changeFragment() {
		getFragmentManager()
			.beginTransaction()
			.replace(R.id.fragment_container, new SettingsFragment())
			.addToBackStack(null)
			.commit();
	}
}
