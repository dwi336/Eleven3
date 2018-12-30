package com.cyanogenmod.eleven.utils;

/*
 * Copyright (C) 2013 The Android Open Source Project
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

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.IBinder;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuItemImpl;
import androidx.appcompat.view.menu.ListMenuPresenter;
import androidx.appcompat.view.menu.MenuPresenter;

import com.cyanogenmod.eleven.R;

/**
 * Helper for menus that appear as Dialogs (context and submenus).
 */
class MenuDialogHelper implements DialogInterface.OnKeyListener,
        DialogInterface.OnClickListener,
        DialogInterface.OnDismissListener,
        MenuPresenter.Callback {
    private MenuBuilder mMenu;
    private AlertDialog mDialog;
    ListMenuPresenter mPresenter;
    private MenuPresenter.Callback mPresenterCallback;
    public MenuDialogHelper(MenuBuilder menu) {
        mMenu = menu;
    }
    /**
     * Shows menu as a dialog.
     *
     * @param windowToken Optional token to assign to the window.
     */
    public void show(IBinder windowToken) {
        // Many references to mMenu, create local reference
        final MenuBuilder menu = mMenu;
        // Get the builder for the dialog
        final AlertDialog.Builder builder = new AlertDialog.Builder(MenuBuilderHelper.getContext(menu));
        mPresenter = ListMenuPresenterHelper.createInstance(builder.getContext(),
                R.layout.abc_list_menu_item_layout);
        ListMenuPresenterHelper.setCallback(mPresenter, this);
        MenuBuilderHelper.addMenuPresenter(mMenu,mPresenter);
        builder.setAdapter(ListMenuPresenterHelper.getAdapter(mPresenter), this);
        // Set the title
        final View headerView = MenuBuilderHelper.getHeaderView(menu);
        if (headerView != null) {
            // Menu's client has given a custom header view, use it
            builder.setCustomTitle(headerView);
        } else {
            // Otherwise use the (text) title and icon
            builder.setIcon(MenuBuilderHelper.getHeaderIcon(menu)).setTitle(MenuBuilderHelper.getHeaderTitle(menu));
        }
        // Set the key listener
        builder.setOnKeyListener(this);
        // Show the menu
        mDialog = builder.create();
        mDialog.setOnDismissListener(this);
        WindowManager.LayoutParams lp = mDialog.getWindow().getAttributes();
        lp.type = WindowManager.LayoutParams.TYPE_APPLICATION_ATTACHED_DIALOG;
        if (windowToken != null) {
            lp.token = windowToken;
        }
        lp.flags |= WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM;
        mDialog.show();
    }
    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU || keyCode == KeyEvent.KEYCODE_BACK) {
            if (event.getAction() == KeyEvent.ACTION_DOWN
                    && event.getRepeatCount() == 0) {
                Window win = mDialog.getWindow();
                if (win != null) {
                    View decor = win.getDecorView();
                    if (decor != null) {
                        KeyEvent.DispatcherState ds = decor.getKeyDispatcherState();
                        if (ds != null) {
                            ds.startTracking(event, this);
                            return true;
                        }
                    }
                }
            } else if (event.getAction() == KeyEvent.ACTION_UP && !event.isCanceled()) {
                Window win = mDialog.getWindow();
                if (win != null) {
                    View decor = win.getDecorView();
                    if (decor != null) {
                        KeyEvent.DispatcherState ds = decor.getKeyDispatcherState();
                        if (ds != null && ds.isTracking(event)) {
                            MenuBuilderHelper.close(mMenu,true);
                            dialog.dismiss();
                            return true;
                        }
                    }
                }
            }
        }
        // Menu shortcut matching
        return MenuBuilderHelper.performShortcut(mMenu, keyCode, event, 0);
    }
    public void setPresenterCallback(MenuPresenter.Callback cb) {
        mPresenterCallback = cb;
    }
    /**
     * Dismisses the menu's dialog.
     *
     * @see Dialog#dismiss()
     */
    public void dismiss() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }
    @Override
    public void onDismiss(DialogInterface dialog) {
        ListMenuPresenterHelper.onCloseMenu(mPresenter, mMenu, true);
    }
    @Override
    public void onCloseMenu(MenuBuilder menu, boolean allMenusAreClosing) {
        if (allMenusAreClosing || menu == mMenu) {
            dismiss();
        }
        if (mPresenterCallback != null) {
            mPresenterCallback.onCloseMenu(menu, allMenusAreClosing);
        }
    }
    @Override
    public boolean onOpenSubMenu(MenuBuilder subMenu) {
        if (mPresenterCallback != null) {
            return mPresenterCallback.onOpenSubMenu(subMenu);
        }
        return false;
    }
    public void onClick(DialogInterface dialog, int which) {
        MenuBuilderHelper.performItemAction(mMenu,(MenuItemImpl) ListMenuPresenterHelper.getAdapter(mPresenter).getItem(which), 0);
    }
}