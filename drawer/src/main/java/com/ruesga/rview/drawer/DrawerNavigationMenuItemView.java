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

package com.ruesga.rview.drawer;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.support.design.internal.ForegroundLinearLayout;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.view.menu.MenuItemImpl;
import android.support.v7.view.menu.MenuView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.widget.CheckedTextView;
import android.widget.FrameLayout;
import android.widget.ImageView;

@SuppressWarnings({"deprecation", "unused"})
public class DrawerNavigationMenuItemView extends ForegroundLinearLayout implements MenuView.ItemView {

    private static final int[] CHECKED_STATE_SET = {android.R.attr.state_checked};

    private final int mIconSize;

    private final ImageView mIcon;
    private final CheckedTextView mTextView;
    private final CheckedTextView mSubTextView;

    private FrameLayout mActionArea;

    private MenuItemImpl mItemData;

    private ColorStateList mIconTintList;

    public DrawerNavigationMenuItemView(Context context) {
        this(context, null);
    }

    public DrawerNavigationMenuItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DrawerNavigationMenuItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(HORIZONTAL);
        LayoutInflater.from(context).inflate(R.layout.drawer_navigation_menu_item, this, true);
        mIconSize = context.getResources().getDimensionPixelSize(
                R.dimen.drawer_navigation_icon_size);
        mIcon = (ImageView) findViewById(R.id.drawer_menu_item_icon);
        mTextView = (CheckedTextView) findViewById(R.id.drawer_menu_item_text);
        mTextView.setDuplicateParentStateEnabled(true);
        mSubTextView = (CheckedTextView) findViewById(R.id.drawer_menu_item_subtext);
        mSubTextView.setDuplicateParentStateEnabled(true);
    }

    @Override
    public void initialize(MenuItemImpl itemData, int menuType) {
        mItemData = itemData;

        setVisibility(itemData.isVisible() ? VISIBLE : GONE);

        if (getBackground() == null) {
            setBackgroundDrawable(createDefaultBackground());
        }

        setCheckable(itemData.isCheckable());
        setChecked(itemData.isChecked());
        setEnabled(itemData.isEnabled());
        setTitle(itemData.getTitle());
        setIcon(itemData.getIcon());
        setActionView(itemData.getActionView());
    }

    public void recycle() {
        if (mActionArea != null) {
            mActionArea.removeAllViews();
        }
    }

    private void setActionView(View actionView) {
        if (mActionArea == null) {
            mActionArea = (FrameLayout) ((ViewStub) findViewById(
                    R.id.drawer_menu_item_action_area_stub)).inflate();
        }
        mActionArea.removeAllViews();
        if (actionView != null) {
            mActionArea.addView(actionView);
        }
    }

    private StateListDrawable createDefaultBackground() {
        TypedValue value = new TypedValue();
        if (getContext().getTheme().resolveAttribute(
                android.support.v7.appcompat.R.attr.colorControlHighlight, value, true)) {
            StateListDrawable drawable = new StateListDrawable();
            drawable.addState(CHECKED_STATE_SET, new ColorDrawable(value.data));
            drawable.addState(EMPTY_STATE_SET, new ColorDrawable(Color.TRANSPARENT));
            return drawable;
        }
        return null;
    }

    @Override
    public MenuItemImpl getItemData() {
        return mItemData;
    }

    @Override
    public void setTitle(CharSequence title) {
        String s1 = null;
        String s2 = null;
        if (!TextUtils.isEmpty(title)) {
            s1 = title.toString();
            if (s1.contains(DrawerNavigationView.SUBTITLE_SEPARATOR)) {
                String[] s = s1.split(DrawerNavigationView.SUBTITLE_SEPARATOR);
                s1 = s[0];
                s2 = s[1];
            }
        }
        mTextView.setText(s1);
        mSubTextView.setText(s2);
        mSubTextView.setVisibility(TextUtils.isEmpty(s2) ? View.GONE : View.VISIBLE);
    }

    @Override
    public void setCheckable(boolean checkable) {
        refreshDrawableState();
    }

    @Override
    public void setChecked(boolean checked) {
        refreshDrawableState();
        mIcon.setSelected(checked);
        mTextView.setChecked(checked);
        mSubTextView.setChecked(checked);
    }

    @Override
    public void setShortcut(boolean showShortcut, char shortcutKey) {
    }

    @Override
    public void setIcon(Drawable icon) {
        if (icon != null) {
            Drawable.ConstantState state = icon.getConstantState();
            icon = DrawableCompat.wrap(state == null ? icon : state.newDrawable()).mutate();
            icon.setBounds(0, 0, mIconSize, mIconSize);
            DrawableCompat.setTintList(icon, mIconTintList);
            mIcon.setImageDrawable(icon);
        } else {
            mIcon.setImageDrawable(null);
        }
    }

    @Override
    public boolean prefersCondensedTitle() {
        return false;
    }

    @Override
    public boolean showsIcon() {
        return true;
    }

    @Override
    protected int[] onCreateDrawableState(int extraSpace) {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (mItemData != null && mItemData.isCheckable() && mItemData.isChecked()) {
            mergeDrawableStates(drawableState, CHECKED_STATE_SET);
        }
        return drawableState;
    }

    void setIconTintList(ColorStateList tintList) {
        mIconTintList = tintList;
        if (mItemData != null) {
            // Update the icon so that the tint takes effect
            setIcon(mItemData.getIcon());
        }
    }

    public void setTextAppearance(Context context, int textAppearance) {
        mTextView.setTextAppearance(context, textAppearance);
        mSubTextView.setTextAppearance(context, textAppearance);
    }

    public void setTextColor(ColorStateList colors) {
        mTextView.setTextColor(colors);
        mSubTextView.setTextColor(colors);
    }

}