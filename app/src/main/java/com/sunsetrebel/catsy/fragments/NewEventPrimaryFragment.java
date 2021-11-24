package com.sunsetrebel.catsy.fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.sunsetrebel.catsy.R;
import com.sunsetrebel.catsy.utils.AccessType;
import com.sunsetrebel.catsy.utils.EventThemes;
import com.sunsetrebel.catsy.utils.EventThemesService;
import com.sunsetrebel.catsy.viewmodel.NewEventViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class NewEventPrimaryFragment extends Fragment {
    private TextInputLayout eventTitleLayout, eventStartTimeLayout, eventEndTimeLayout;
    private TextInputEditText eventTitle, eventStartTime, eventEndTime;
    private AppCompatButton submitButton;
    private AppCompatTextView eventAccessDescr;
    private MaterialButton buttonPublic, buttonPrivate, buttonSelective;
    private Date startTimeDate;
    private Date endTimeDate;
    private AccessType eventAccessValue;
    private boolean[] selectedTheme;
    private List<EventThemes> eventThemes;
    private TextInputEditText eventTheme;
    private NewEventViewModel newEventViewModel;

    public NewEventPrimaryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_new_event_primary, container, false);
        newEventViewModel = new ViewModelProvider(requireActivity()).get(NewEventViewModel.class);
        newEventViewModel.init();

        eventTitle = v.findViewById(R.id.tiet_event_title);
        eventTitleLayout = v.findViewById(R.id.til_event_title);
        submitButton = v.findViewById(R.id.button_next);
        eventStartTimeLayout = v.findViewById(R.id.til_start_time);
        eventStartTime = v.findViewById(R.id.tiet_start_time);
        eventEndTimeLayout = v.findViewById(R.id.til_end_time);
        eventEndTime = v.findViewById(R.id.tiet_end_time);
        buttonPublic = v.findViewById(R.id.button_public);
        buttonPrivate = v.findViewById(R.id.button_private);
        buttonSelective = v.findViewById(R.id.button_selective);
        eventAccessDescr = v.findViewById(R.id.tv_access_description);
        eventTheme = v.findViewById(R.id.tiet_event_themes);

        //Initial layout
        buttonPublic.setChecked(true);
        eventAccessValue = AccessType.PUBLIC;
        buttonPublic.setBackgroundColor(getResources().getColor(R.color.primaryColorTransparent));
        eventAccessDescr.setText(getResources().getText(R.string.event_access_public_description));
        eventAccessDescr.setCompoundDrawablesWithIntrinsicBounds(R.drawable.im_caticorn_public_64p, 0, 0, 0);

        buttonPublic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventAccessValue = AccessType.PUBLIC;
                buttonPrivate.setBackgroundColor(getResources().getColor(R.color.blackQuoterTransparent));
                buttonSelective.setBackgroundColor(getResources().getColor(R.color.blackQuoterTransparent));
                buttonPublic.setBackgroundColor(getResources().getColor(R.color.primaryColorTransparent));
                eventAccessDescr.setCompoundDrawablesWithIntrinsicBounds(R.drawable.im_caticorn_public_64p, 0, 0, 0);
                eventAccessDescr.setText(getResources().getText(R.string.event_access_public_description));
            }
        });

        buttonPrivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventAccessValue = AccessType.PRIVATE;
                buttonPublic.setBackgroundColor(getResources().getColor(R.color.blackQuoterTransparent));
                buttonSelective.setBackgroundColor(getResources().getColor(R.color.blackQuoterTransparent));
                buttonPrivate.setBackgroundColor(getResources().getColor(R.color.primaryColorTransparent));
                eventAccessDescr.setCompoundDrawablesWithIntrinsicBounds(R.drawable.im_caticorn_private_64p, 0, 0, 0);
                eventAccessDescr.setText(getResources().getText(R.string.event_access_private_description));
            }
        });

        buttonSelective.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventAccessValue = AccessType.SELECTIVE;
                buttonPublic.setBackgroundColor(getResources().getColor(R.color.blackQuoterTransparent));
                buttonPrivate.setBackgroundColor(getResources().getColor(R.color.blackQuoterTransparent));
                buttonSelective.setBackgroundColor(getResources().getColor(R.color.primaryColorTransparent));
                eventAccessDescr.setCompoundDrawablesWithIntrinsicBounds(R.drawable.im_caticorn_selective_64p, 0, 0, 0);
                eventAccessDescr.setText(getResources().getText(R.string.event_access_selective_description));
            }
        });

        eventStartTime.setOnClickListener(v15 -> showDateTimeDialog(eventStartTime, true));

        eventEndTime.setOnClickListener(v16 -> showDateTimeDialog(eventEndTime, false));

        //Initialize themes array
        EventThemesService eventThemesServiceList = new EventThemesService(getActivity().getResources());
        Map<Enum<?>, String> themesArray = eventThemesServiceList.getEventThemesList();
        String[] themesArrayValues = themesArray.values().toArray(new String[0]);
        ArrayList<Integer> chosenThemesArray = new ArrayList<>();
        selectedTheme = new boolean[themesArray.size()];

        eventTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Initialize alert dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.DatePickerDialog);
                //Set builder properties
                builder.setTitle(getResources().getString(R.string.event_theme_dialog_title));
                builder.setCancelable(false);
                builder.setMultiChoiceItems(themesArrayValues, selectedTheme, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if (isChecked) {
                            //When checkbox selected add position in theme list
                            if (!chosenThemesArray.contains(which)) {
                                chosenThemesArray.add(which);
                            }
                            //Sort theme list
                            Collections.sort(chosenThemesArray);
                        } else {
                            if (chosenThemesArray.size() > which && chosenThemesArray.contains(which)) {
                                //When checkbox unselected remove position from theme list
                                chosenThemesArray.remove(which);
                            }
                        }
                    }
                });

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        StringBuilder stringBuilder = new StringBuilder();
                        eventThemes = new ArrayList<>();
                        int maxQuantity;
                        if (chosenThemesArray.size() > 3) {
                            Toast.makeText(getContext(), getResources().getString(R.string.event_theme_dialog_notification), Toast.LENGTH_SHORT).show();
                            maxQuantity = 3;
                        } else {
                            maxQuantity = chosenThemesArray.size();
                        }
                        for (int j = 0; j < maxQuantity; j++) {
                            eventThemes.add((EventThemes) EventThemesService.getKeyByValue(themesArray, themesArrayValues[chosenThemesArray.get(j)]));
                            //Concat array value
                            stringBuilder.append(themesArrayValues[chosenThemesArray.get(j)]);
                            if (j != maxQuantity - 1) {
                                stringBuilder.append(", ");
                            }
                        }
                        //Clear all checkboxes and chosen list
                        chosenThemesArray.clear();
                        selectedTheme = new boolean[themesArray.size()];
                        //Set text on textview
                        eventTheme.setText(stringBuilder.toString());
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builder.setNeutralButton("Clear All", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for (int j=0; j<selectedTheme.length; j++) {
                            //Remove all selection
                            selectedTheme[j] = false;
                            chosenThemesArray.clear();
                            eventTheme.setText("");
                        }
                    }
                });

                builder.show();
            }
        });

        submitButton.setOnClickListener(v1 -> {
            eventTitleLayout.setError(null);
            eventStartTimeLayout.setError(null);
            eventEndTimeLayout.setError(null);
            String eventTitleValue = eventTitle.getText().toString().trim();

            if (TextUtils.isEmpty(eventTitleValue) || startTimeDate == null || endTimeDate == null
                    || eventAccessValue == null) {
                //TO DO: change color of all mandatory fields
                eventTitleLayout.setError(getResources().getString(R.string.new_event_fill_mandatory_field_error));
                eventStartTimeLayout.setError(getResources().getString(R.string.new_event_fill_mandatory_field_error));
                eventEndTimeLayout.setError(getResources().getString(R.string.new_event_fill_mandatory_field_error));
                return;
            }

            //Save info to NewEventViewModel and move to next fragment
            newEventViewModel.setNewEventPrimaryInfo(eventTitleValue, eventAccessValue, startTimeDate, endTimeDate, eventThemes);
            getParentFragmentManager().beginTransaction().addToBackStack("NewEventPrimaryFragment")
                    .replace(R.id.frameLayoutMain, new NewEventMapFragment()).commit();
        });
        return v;
    }

    private void showDateTimeDialog(final EditText timeEditText, boolean isStartTime) {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        calendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
                        calendar.set(Calendar.MINUTE,minute);

                        if (isStartTime) {
                            startTimeDate = calendar.getTime();
                        } else {
                            endTimeDate = calendar.getTime();
                        }

                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm d MMM ''yy", Locale.getDefault());
                        timeEditText.setText(simpleDateFormat.format(calendar.getTime()));
                    }
                };
                new TimePickerDialog(getActivity(), R.style.DatePickerDialog, timeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE),true).show();
            }
        };
        new DatePickerDialog(getActivity(), R.style.DatePickerDialog, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }
}
