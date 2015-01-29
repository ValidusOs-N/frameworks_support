/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.support.design.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.design.R;
import android.support.design.internal.NavigationMenuPresenter;
import android.support.design.internal.ScrimInsetsFrameLayout;
import android.support.v4.view.ViewCompat;
import android.support.v7.internal.view.menu.MenuBuilder;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

/**
 * Represents a standard navigation drawer for application. The drawer contents can be populated by
 * a menu resource
 * file.
 * <p>NavigationDrawer needs to be placed inside a {@link android.support.v4.widget.DrawerLayout}.
 * </p>
 * <pre>
 * &lt;android.support.v4.widget.DrawerLayout xmlns:android="http://schemas.android.com/apk/res/android"
 *     xmlns:app="http://schemas.android.com/apk/res-auto"
 *     android:id="@+id/drawer_layout"
 *     android:layout_width="match_parent"
 *     android:layout_height="match_parent"
 *     android:fitsSystemWindows="true"&gt;
 *
 *     &lt;!-- Your contents --&gt;
 *
 *     &lt;android.support.design.widget.NavigationDrawerView
 *         android:id="@+id/navigation_drawer"
 *         android:layout_width="wrap_content"
 *         android:layout_height="match_parent"
 *         android:layout_gravity="start" /&gt;
 * &lt;/android.support.v4.widget.DrawerLayout&gt;
 * </pre>
 */
public class NavigationDrawerView extends ScrimInsetsFrameLayout {

    private OnNavigationItemSelectedListener mListener;

    private MenuBuilder mMenu;

    private int mMaxWidth;

    public NavigationDrawerView(Context context) {
        this(context, null);
    }

    public NavigationDrawerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NavigationDrawerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        // Custom attributes
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.NavigationDrawerView, defStyleAttr,
                R.style.Widget_Design_NavigationDrawerView);

        //noinspection deprecation
        setBackgroundDrawable(a.getDrawable(R.styleable.NavigationDrawerView_android_background));
        ViewCompat.setElevation(this,
                a.getDimensionPixelSize(R.styleable.NavigationDrawerView_android_elevation, 0));
        ViewCompat.setFitsSystemWindows(this,
                a.getBoolean(R.styleable.NavigationDrawerView_android_fitsSystemWindows, false));
        mMaxWidth = a.getDimensionPixelSize(R.styleable.NavigationDrawerView_android_maxWidth, 0);
        a.recycle();

        // Set up the menu
        mMenu = new MenuBuilder(context);
        MenuBuilder.Callback menuCallback = new MenuBuilder.Callback() {
            @Override
            public boolean onMenuItemSelected(MenuBuilder menu, MenuItem item) {
                return mListener != null && mListener.onNavigationItemSelected(item);
            }

            @Override
            public void onMenuModeChange(MenuBuilder menu) {

            }
        };
        mMenu.setCallback(menuCallback);
        NavigationMenuPresenter presenter = new NavigationMenuPresenter();
        presenter.initForMenu(context, mMenu);
        addView((View) presenter.getMenuView(this));
    }

    /**
     * Set a listener that will be notified when a menu item is clicked.
     *
     * @param listener The listener to notify
     */
    public void setNavigationItemSelectedListener(OnNavigationItemSelectedListener listener) {
        mListener = listener;
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        switch (MeasureSpec.getMode(widthSpec)) {
            case MeasureSpec.EXACTLY:
                // Nothing to do
                break;
            case MeasureSpec.AT_MOST:
                widthSpec = MeasureSpec.makeMeasureSpec(
                        Math.min(MeasureSpec.getSize(widthSpec), mMaxWidth), MeasureSpec.EXACTLY);
                break;
            case MeasureSpec.UNSPECIFIED:
                widthSpec = MeasureSpec.makeMeasureSpec(mMaxWidth, MeasureSpec.EXACTLY);
                break;
        }
        // Let super sort out the height
        super.onMeasure(widthSpec, heightSpec);
    }

    /**
     * @return The {@link Menu} associated with this NavigationDrawerView.
     */
    public Menu getMenu() {
        return mMenu;
    }

    /**
     * Listener for handling events on navigation drawer items.
     */
    public interface OnNavigationItemSelectedListener {

        /**
         * Called when an item in the navigation drawer is selected.
         *
         * @param item The selected item
         */
        public boolean onNavigationItemSelected(MenuItem item);
    }

}
