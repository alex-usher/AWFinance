package com.example.alex_.project;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.skydoves.colorpickerview.ColorEnvelope;
import com.skydoves.colorpickerview.ColorPickerDialog;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;

import java.sql.Timestamp;

public class AddBudget extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_add_budget, container, false);

        //handling spinner
        Spinner typeSpinner = v.findViewById(R.id.type);
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(getContext(), R.array.budgetTypes, android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(arrayAdapter);
        int position = arrayAdapter.getPosition("daily");
        typeSpinner.setSelection(position);

        //handling color picker
        final EditText colorBox = v.findViewById(R.id.colour);
        colorBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ColorPickerDialog.Builder(getContext(), AlertDialog.BUTTON_NEUTRAL)
                        .setTitle("Colour Picker")
                        .setPreferenceName("ColorPickerDialog")
                        .setPositiveButton(getString(R.string.confirm),
                                new ColorEnvelopeListener() {
                                    @Override
                                    public void onColorSelected(ColorEnvelope envelope, boolean fromUser) {
                                        String hex = "#" + envelope.getHexCode();
                                        colorBox.setText(hex);
                                        colorBox.clearFocus();
                                    }
                                })
                        .setNegativeButton(getString(R.string.cancel),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                        colorBox.clearFocus();
                                    }
                                })
                        .attachAlphaSlideBar(true) // default is true. If false, do not show the AlphaSlideBar.
                        .attachBrightnessSlideBar(true)  // default is true. If false, do not show the BrightnessSlideBar.
                        .show();
            }
        });

        Button submit = v.findViewById(R.id.submit);
        submit.setOnClickListener(
                new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submit(view);
            }
        });

        return v;
    }

    public void submit(View view) {
        try {
            final EditText name = getActivity().findViewById(R.id.name);
            final EditText amount = getActivity().findViewById(R.id.amount);
            final Spinner type = getActivity().findViewById(R.id.type);
            final EditText colour = getActivity().findViewById(R.id.colour);

            String nameStr = name.getText().toString();
            float amountDec = Float.parseFloat(amount.getText().toString());
            String typeStr = type.getSelectedItem().toString();
            int typeInt;
            String colourStr = colour.getText().toString();

            if ((nameStr.isEmpty()) || (amountDec == 0.0f) || (typeStr.isEmpty()) || (colourStr.isEmpty())) {
                Toast toast = Toast.makeText(getActivity(), "Please Complete the Form", Toast.LENGTH_SHORT);
                toast.show();
            } else {
                typeInt = Budget.typeToInt(typeStr);

                Budget budget = new Budget(nameStr, amountDec, BudgetType.intToType(typeInt),
                                new Timestamp(System.currentTimeMillis()), colourStr);

                DBHelper dbHelper = new DBHelper(getActivity());
                dbHelper.addBudget(budget);

                Toast toast = Toast.makeText(getActivity(), "Budget Created", Toast.LENGTH_SHORT);
                toast.show();

                changeFragment();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void changeFragment() {
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new BudgetsFragment()).commit();
    }
}
