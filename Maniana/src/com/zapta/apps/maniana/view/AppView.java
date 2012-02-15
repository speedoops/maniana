/*
 * Copyright (C) 2011 The original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.zapta.apps.maniana.view;

import javax.annotation.Nullable;

import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;

import com.zapta.apps.maniana.main.AppContext;
import com.zapta.apps.maniana.model.PageKind;
import com.zapta.apps.maniana.quick_action.QuickActionItem;

/**
 * A wrapper containing the entire view functioanlity.
 * 
 * @author Tal Dayan
 */
public class AppView {

    public static enum ItemAnimationType {
        DELETING_ITEM,
        MOVING_ITEM_TO_OTHER_PAGE,
        SORTING_ITEM,
    }

    private final AppContext mApp;

    /** The view with the two horizontally scrolling pages. */
    private final ViewPager mViewPager;

    /** The view of todays page. Contain header, item list, etc. */
    private final PageView mTodayPageView;

    /** The view of tomorrow page. Contain header, item list, etc. */
    private final PageView mTomorowPageView;

    /** Track the displayed page of the underlying view pager. */
    private int mCurrentPageIndex = 0;

    public AppView(AppContext app) {
        this.mApp = app;

        mTodayPageView = new PageView(mApp, PageKind.TODAY);
        mTomorowPageView = new PageView(mApp, PageKind.TOMOROW);

        mViewPager = new ViewPager(mApp.context());
        mViewPager.setAdapter(new PagerViewAdapter(mTodayPageView, mTomorowPageView));

        // TODO: do we need a margin?
        // mViewPager.setPageMargin(1);
        // mViewPager.setPageMarginDrawable(R.color.page_seperator_color);

        // Make sure we are in sync with the view.
        mViewPager.setCurrentItem(mCurrentPageIndex);

        mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrollStateChanged(int arg0) {
                // TODO: track also scroll state?
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageSelected(int pageIndex) {
                mCurrentPageIndex = pageIndex;
            }
        });
    }

    /** Get the page view of given kind. */
    private PageView getPageView(PageKind pageKind) {
        switch (pageKind) {
            case TODAY:
                return mTodayPageView;
            case TOMOROW:
                return mTomorowPageView;
            default:
                throw new RuntimeException("Unknown page kind");
        }
    }

    /** Get the root View of this app view. Used to display it in an activity. */
    public final View getRootView() {
        return mViewPager;
    }

    public final void startItemAnimation(PageKind pageKind, int itemIndex,
                    ItemAnimationType animationType, int initialDelayMillis, @Nullable final Runnable callback) {
        getPageView(pageKind).startItemAnimation(itemIndex, animationType, initialDelayMillis,
                        callback);
    }

    public void setItemViewHighlight(PageKind pageKind, int itemIndex, boolean isHighlight) {
        getPageView(pageKind).setItemViewHighlight(itemIndex, isHighlight);
    }

    public void showItemMenu(PageKind pageKind, final int itemIndex, QuickActionItem actions[],
                    final int dismissActionId) {
        getPageView(pageKind).showItemMenu(itemIndex, actions, dismissActionId);
    }

    public final void updateSingleItemView(PageKind pageKind, int itemIndex) {
        getPageView(pageKind).updateSingleItemView(itemIndex);
    }

    public final void updatePages() {
        upadatePage(PageKind.TODAY);
        upadatePage(PageKind.TOMOROW);
    }

    public final void upadatePage(PageKind pageKind) {
        final PageView pageView = getPageView(pageKind);
        pageView.upadateAllItemViews();
        pageView.updateUndoButton();
    }

    public final void updateUndoButtons() {
        mTodayPageView.updateUndoButton();
        mTomorowPageView.updateUndoButton();
    }
    
    public final void onItemDividerColorPreferenceChange() {
        mTodayPageView.onItemDividerColorPreferenceChange();
        mTomorowPageView.onItemDividerColorPreferenceChange();
    }

    public final void updateUndoButton(PageKind pageKind) {
        getPageView(pageKind).updateUndoButton();
    }

    public final void onDateChange() {
        mTodayPageView.onDateChange();
    }

    public final void onItemFontVariationPreferenceChange() {
        mTodayPageView.onItemFontVariationPreferenceChange();
        mTomorowPageView.onItemFontVariationPreferenceChange();
    }

    public final void onPageBackgroundPreferenceChange() {
        mTodayPageView.onPageBackgroundPreferenceChange();
        mTomorowPageView.onPageBackgroundPreferenceChange();
    }

    /** Scroll period in millis. Ignored in < 0. */
    public final void setCurrentPage(PageKind pageKind, int scrollPeriodMillis) {
        mViewPager.mForcedScrollDurationMillis = scrollPeriodMillis;
        mViewPager.setCurrentItem(pageKind.isToday() ? 0 : 1);
        mViewPager.mForcedScrollDurationMillis = -1;
    }

    public final PageKind getCurrentPage() {
        return (mCurrentPageIndex == 0) ? PageKind.TODAY : PageKind.TOMOROW;
    }

    public void scrollToTop(PageKind pageKind) {
        getPageView(pageKind).scrollToTop();
    }
}
