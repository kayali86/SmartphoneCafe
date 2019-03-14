package com.kayali_developer.smartphonecafe;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kayali_developer.smartphonecafe.data.model.Event;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class EventsHistoryFragment extends Fragment {
    public static final String TAG = "EventsHistoryFragmentTag";

    @BindView(R.id.rv_events_history_list)
    RecyclerView rvEventsHistoryList;

    private MainActivity mActivity;
    private Unbinder unbinder;
    private EventsHistoryFragmentAdapter mAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = ((MainActivity) getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_events_history, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvEventsHistoryList.setHasFixedSize(true);
        rvEventsHistoryList.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new EventsHistoryFragmentAdapter();
        rvEventsHistoryList.setAdapter(mAdapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mActivity.mViewModel.mAllArticles != null && mActivity.mViewModel.mAllArticles.size() > 0) {
            updateData(mActivity.mViewModel.mAllEvents);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    public void updateData(List<Event> events) {
        mAdapter.setData(events);
    }
}
