package com.kayali_developer.smartphonecafe;


import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class MainViewPagerAdapter extends FragmentPagerAdapter {
    public static final int ARTICLE_FRAGMENT_INDEX = 1;
    public static final int NEXT_EVENT_FRAGMENT_INDEX = 0;
    public static final int EVENTS_HISTORY_FRAGMENT_INDEX = 2;
    public static final int HELPERS_FRAGMENT_INDEX = 3;

    private Context mContext;

    private ArticlesFragment mArticlesFragment;
    private NextEventFragment mNextEventFragment;
    private EventsHistoryFragment mEventsHistoryFragment;
    private HelpersFragment mHelpersFragment;

    MainViewPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {

            case ARTICLE_FRAGMENT_INDEX:
                return mContext.getString(R.string.title_fragment_articles);

            case NEXT_EVENT_FRAGMENT_INDEX:
                return mContext.getString(R.string.title_fragment_next_event);

            case EVENTS_HISTORY_FRAGMENT_INDEX:
                return mContext.getString(R.string.title_fragment_events_history);

            case HELPERS_FRAGMENT_INDEX:
                return mContext.getString(R.string.title_fragment_helpers_list);

            default:
                return mContext.getString(R.string.title_fragment_articles);
        }
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {

            case ARTICLE_FRAGMENT_INDEX:
                mArticlesFragment = new ArticlesFragment();
                return mArticlesFragment;

            case NEXT_EVENT_FRAGMENT_INDEX:
                mNextEventFragment = new NextEventFragment();
                return mNextEventFragment;

            case EVENTS_HISTORY_FRAGMENT_INDEX:
                mEventsHistoryFragment = new EventsHistoryFragment();
                return mEventsHistoryFragment;

            case HELPERS_FRAGMENT_INDEX:
                mHelpersFragment = new HelpersFragment();
                return mHelpersFragment;

            default:
                mArticlesFragment = new ArticlesFragment();
                return mArticlesFragment;
        }
    }

    public ArticlesFragment getArticlesFragment() {
        return mArticlesFragment;
    }

    public NextEventFragment getNextEventFragment() {
        return mNextEventFragment;
    }

    public EventsHistoryFragment getEventsHistoryFragment() {
        return mEventsHistoryFragment;
    }

    public HelpersFragment getHelpersFragment() {
        return mHelpersFragment;
    }

}
