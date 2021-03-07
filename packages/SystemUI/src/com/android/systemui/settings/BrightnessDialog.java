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

package com.android.systemui.settings;

import static com.android.settingslib.display.BrightnessUtils.GAMMA_SPACE_MAX;
import static com.android.settingslib.display.BrightnessUtils.convertGammaToLinearFloat;
import static com.android.settingslib.display.BrightnessUtils.convertLinearToGammaFloat;

import android.app.Activity;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.util.MathUtils;
import android.util.Log;

import com.android.internal.logging.MetricsLogger;
import com.android.internal.logging.nano.MetricsProto.MetricsEvent;
import com.android.systemui.R;
import com.android.systemui.broadcast.BroadcastDispatcher;

import javax.inject.Inject;

/** A dialog that provides controls for adjusting the screen brightness. */
public class BrightnessDialog extends Activity implements ToggleSlider.Listener {

    private BrightnessController mBrightnessController;
    private final BroadcastDispatcher mBroadcastDispatcher;

    @Inject
    public BrightnessDialog(BroadcastDispatcher broadcastDispatcher) {
        mBroadcastDispatcher = broadcastDispatcher;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Window window = getWindow();

        window.setGravity(Gravity.TOP);
        window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        window.requestFeature(Window.FEATURE_NO_TITLE);

        View v = LayoutInflater.from(this).inflate(
                R.layout.quick_settings_brightness_dialog, null);
        setContentView(v);

        final ImageButton icon = findViewById(R.id.brightness_icon);
        final ToggleSliderView slider = findViewById(R.id.brightness_slider);
        mBrightnessController = new BrightnessController(this, icon, slider, mBroadcastDispatcher);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mBrightnessController.registerCallbacks();
        MetricsLogger.visible(this, MetricsEvent.BRIGHTNESS_DIALOG);
    }

    @Override
    protected void onStop() {
        super.onStop();
        MetricsLogger.hidden(this, MetricsEvent.BRIGHTNESS_DIALOG);
        mBrightnessController.unregisterCallbacks();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN
                || keyCode == KeyEvent.KEYCODE_VOLUME_UP
                || keyCode == KeyEvent.KEYCODE_VOLUME_MUTE) {
            finish();
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onInit(ToggleSlider control) {
        // Do nothing
    }

    @Override
    public void onChanged(ToggleSlider toggleSlider, boolean tracking, boolean automatic,
            int value, boolean stopTracking) {
        if (!mBrightnessController.mIsVrModeEnabled) {
           //Log.d("Fluid", "reached point 2");
           PowerManager pm = this.getSystemService(PowerManager.class);
           float maxBacklight = pm.getBrightnessConstraint(PowerManager.BRIGHTNESS_CONSTRAINT_TYPE_MAXIMUM);
           float minBacklight = pm.getBrightnessConstraint(PowerManager.BRIGHTNESS_CONSTRAINT_TYPE_MINIMUM);
           final float valFloat = MathUtils.min(convertGammaToLinearFloat(value,
                   minBacklight, maxBacklight),
                   1.0f);
           final float rotation = 360 * valFloat;
           ImageButton icon = findViewById(R.id.brightness_icon);
           //Log.d("Fluid", "brightness: " + String.valueOf(value) + ", rotation: " + String.valueOf(rotation));
           icon.setRotation(rotation);
        }
    }
}
