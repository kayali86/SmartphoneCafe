package com.kayali_developer.smartphonecafe;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import com.github.florent37.singledateandtimepicker.SingleDateAndTimePicker;
import com.github.florent37.singledateandtimepicker.dialog.SingleDateAndTimePickerDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.kayali_developer.smartphonecafe.data.model.Event;
import com.kayali_developer.smartphonecafe.data.model.FirebasePostMessageByTopic;
import com.kayali_developer.smartphonecafe.data.model.FirebasePostNotificationResponse;
import com.kayali_developer.smartphonecafe.data.model.User;
import com.kayali_developer.smartphonecafe.data.remote.APIService;
import com.kayali_developer.smartphonecafe.data.remote.ApiUtils;
import com.kayali_developer.smartphonecafe.utilities.AppDateUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddNewEventFragment extends Fragment {
    public static final String TAG = "AddNewEventFragmentTag";
    public static final String EVENT_KEY = "newEventKey";

    private static final int START_TIME_TYPE = 0;
    private static final int END_TIME_TYPE = 1;

    @BindView(R.id.etv_event_topic)
    EditText etvEventTopic;
    @BindView(R.id.etv_event_location)
    EditText etvEventLocation;
    @BindView(R.id.etv_event_description)
    EditText etvEventDescription;
    @BindView(R.id.sp_organizers)
    AppCompatSpinner spOrganizers;

    private Unbinder unbinder;
    private Event event;

    private MainActivity mActivity;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mActivity = ((MainActivity) context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_new_event, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState == null) {
            event = new Event();
            event.setNextEvent(true);
        }

        etvEventTopic.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null) {
                    event.setTopic(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etvEventLocation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null) {
                    event.setLocation(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etvEventDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null) {
                    event.setDescription(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            try {
                event = new Gson().fromJson(savedInstanceState.getString(EVENT_KEY), Event.class);
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
            }
        }

        List<String> allMembersString = new ArrayList<>();
        if (mActivity != null && getContext() != null && mActivity.mViewModel.mAllUsers != null) {
            for (User user : mActivity.mViewModel.mAllUsers) {
                allMembersString.add(user.getFirstName() + " " + user.getLastName());
            }
            spOrganizers.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, allMembersString));
        }

        spOrganizers.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mActivity != null && getContext() != null) {
                    String organizer = mActivity.mViewModel.getMemberByFullName(allMembersString.get(position)).getFirstName() + " " + mActivity.mViewModel.getMemberByFullName(allMembersString.get(position)).getLastName();
                    event.setOrganizerFullName(organizer);
                    event.setOrganizerUID(mActivity.mViewModel.getMemberByFullName(organizer).getUid());
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if (event != null) {
            outState.putString(EVENT_KEY, new Gson().toJson(event));
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    @OnClick({R.id.btn_start_time, R.id.btn_end_time, R.id.btn_publish_new_event})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_start_time:
                showTimePicker(START_TIME_TYPE, "Title");
                break;
            case R.id.btn_end_time:
                showTimePicker(END_TIME_TYPE, "Title2");
                break;

            case R.id.btn_publish_new_event:
                publishNewEvent();
                break;
        }
    }

    private void publishNewEvent() {
        if (event.getTopic() != null && event.getLocation() != null && event.getDescription() != null && event.getOrganizerFullName() != null & event.getStartTime() > 0 && event.getEndTime() > 0) {
            FirebaseDatabase mFbDatabase = FirebaseDatabase.getInstance();
            DatabaseReference eventsReference = mFbDatabase.getReference().child(AppConstants.EVENTS_CHILD);
            String eventId = eventsReference.push().getKey();
            event.setEventId(eventId);

            event.setFeedback(0);
            event.setFeedbackCount(0);

            // pushing user to 'users' node using the userId
            eventsReference.child(eventId).setValue(event);
            if (mActivity.mViewModel.mNextEvent != null) {
                Event isNotNextEvent = null;
                try {
                    isNotNextEvent = (Event) mActivity.mViewModel.mNextEvent.clone();
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }
                if (isNotNextEvent != null) {
                    isNotNextEvent.setNextEvent(false);
                    eventsReference.child(mActivity.mViewModel.mNextEvent.getEventId()).setValue(isNotNextEvent).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(getContext(), "DB updated", Toast.LENGTH_SHORT).show();
                            postEventNotificationByTopic();
                        }
                    });
                    mActivity.updateEventsHistory();
                }
            }


            Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show();
            mActivity.mainActivityFragmentContainer.setVisibility(View.GONE);
            mActivity.mFragmentManager.beginTransaction().remove(this).commitAllowingStateLoss();
        }
    }

    public void postEventNotificationByTopic() {
        APIService mAPIService = ApiUtils.getAPIService();
        if (getContext() != null) {
            FirebasePostMessageByTopic firebasePostMessageByTopic = new FirebasePostMessageByTopic(getContext().getString(R.string.news_event_topic), mActivity.mViewModel.mNextEvent);
            mAPIService.postEventByTopic(firebasePostMessageByTopic).enqueue(new Callback<FirebasePostNotificationResponse>() {
                @Override
                public void onResponse(Call<FirebasePostNotificationResponse> call, Response<FirebasePostNotificationResponse> response) {
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            Toast.makeText(getContext(), "Notification sent", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<FirebasePostNotificationResponse> call, Throwable t) {
                    Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
/*
    public void postEventNotificationByTopic() {
        APIService mAPIService = ApiUtils.getAPIService();
        List<String> tokens = new ArrayList<>();
        String token = "cEYOx2DHi2k:APA91bER_GPCxiNJmnzvw4Vy_S-m5ZbRcIaG4vZWR_jTexO-ZNPuTaw_Y9lzrBIM8EB6RetAYAYwLkQAAPu7hRoozLJOJs5J3r70H2k010PnYGYbXgGQ2RIwLEhtJ8txgmfX7CVla6jr";
        tokens.add(token);
        if (getContext() != null) {
            FirebasePostMessageByTopic firebasePostMessageByTopic = new FirebasePostMessageByTopic(getContext().getString(R.string.news_event_topic), event);
            mAPIService.postEventNotificationByTopic(firebasePostMessageByTopic).enqueue(new Callback<FirebasePostNotificationResponse>() {
                @Override
                public void onResponse(Call<FirebasePostNotificationResponse> call, Response<FirebasePostNotificationResponse> response) {
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<FirebasePostNotificationResponse> call, Throwable t) {
                    Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
*/
    private void showTimePicker(int timeType, String title) {
        new SingleDateAndTimePickerDialog.Builder(getContext())
                //.bottomSheet()
                //.curved()
                .minutesStep(15)
                //.displayHours(false)
                //.displayMinutes(false)
                //.todayText("aujourd'hui")

                .displayListener(new SingleDateAndTimePickerDialog.DisplayListener() {
                    @Override
                    public void onDisplayed(SingleDateAndTimePicker picker) {
                        //retrieve the SingleDateAndTimePicker
                    }
                })
                .title(title)
                .backgroundColor(Color.BLACK)
                .mainColor(Color.GREEN)
                .titleTextColor(Color.WHITE)
                .listener(new SingleDateAndTimePickerDialog.Listener() {
                    @Override
                    public void onDateSelected(Date date) {
                        if (timeType == START_TIME_TYPE) {
                            Toast.makeText(getContext(), AppDateUtils.dateObjectToDeFormatComplete(date), Toast.LENGTH_LONG).show();
                            event.setStartTime(date.getTime());
                        } else {
                            Toast.makeText(getContext(), AppDateUtils.dateObjectToDeFormatComplete(date), Toast.LENGTH_LONG).show();
                            event.setEndTime(date.getTime());
                        }

                    }
                }).display();
    }

}
