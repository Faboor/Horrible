package info.horriblesubs.sher.old.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import info.horriblesubs.sher.R;
import info.horriblesubs.sher.old.task.FetchShowReleases;

public class ShowReleases extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;

    public static ShowReleases newInstance(int i) {
        ShowReleases fragment = new ShowReleases();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, i);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_shows, container, false);
        recyclerView = rootView.findViewById(R.id.recyclerView);
        swipeRefreshLayout = rootView.findViewById(R.id.textView);
        assert getArguments() != null;
        new FetchShowReleases(recyclerView, getContext(), swipeRefreshLayout)
                .execute(getArguments().getInt(ARG_SECTION_NUMBER));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new FetchShowReleases(recyclerView, getContext(), swipeRefreshLayout)
                        .execute(getArguments().getInt(ARG_SECTION_NUMBER));
            }
        });
        return rootView;
    }
}