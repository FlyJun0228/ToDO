package edu.gatech.seclass.todo.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;

import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import edu.gatech.seclass.todo.R;
import edu.gatech.seclass.todo.TickApplication;
import edu.gatech.seclass.todo.TickService;
import edu.gatech.seclass.todo.util.SeekBarPreference;


/**
 * 使用 PreferenceFragment 然后 addPreferencesFromResource(xml) 会更简洁
 */
public class SettingActivity extends AppCompatActivity {

    private LinearLayout layout;
    private Switch setting;

    public static Intent newIntent(Context context) {
        return new Intent(context, SettingActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        layout = findViewById(R.id.super_setting);
        setting = findViewById(R.id.setting);
        layout.setVisibility(View.INVISIBLE);
        setting.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    layout.setVisibility(View.VISIBLE);
                }else {
                    layout.setVisibility(View.INVISIBLE);
                }
            }
        });
        setToolBar();

        Resources res = getResources();

        // 工作时长
        (new SeekBarPreference(this))
                .setSeekBar((SeekBar) findViewById(R.id.pref_key_work_length))
                .setSeekBarValue((TextView) findViewById(R.id.pref_key_work_length_value))
                .setMax(res.getInteger(R.integer.pref_work_length_max))
                .setMin(res.getInteger(R.integer.pref_work_length_min))
                .setUnit(R.string.pref_title_time_value)
                .setProgress(PreferenceManager.getDefaultSharedPreferences(this)
                        .getInt("pref_key_work_length", TickApplication.DEFAULT_WORK_LENGTH))
                .build();
        // 短时休息
        (new SeekBarPreference(this))
                .setSeekBar((SeekBar) findViewById(R.id.pref_key_short_break))
                .setSeekBarValue((TextView) findViewById(R.id.pref_key_short_break_value))
                .setMax(res.getInteger(R.integer.pref_short_break_max))
                .setMin(res.getInteger(R.integer.pref_short_break_min))
                .setUnit(R.string.pref_title_time_value)
                .setProgress(PreferenceManager.getDefaultSharedPreferences(this)
                        .getInt("pref_key_short_break", TickApplication.DEFAULT_SHORT_BREAK))
                .build();
        // 长时休息
        (new SeekBarPreference(this))
                .setSeekBar((SeekBar) findViewById(R.id.pref_key_long_break))
                .setSeekBarValue((TextView) findViewById(R.id.pref_key_long_break_value))
                .setMax(res.getInteger(R.integer.pref_long_break_max))
                .setMin(res.getInteger(R.integer.pref_long_break_min))
                .setUnit(R.string.pref_title_time_value)
                .setProgress(PreferenceManager.getDefaultSharedPreferences(this)
                        .getInt("pref_key_long_break", TickApplication.DEFAULT_LONG_BREAK))
                .build();
        // 长时休息间隔
        (new SeekBarPreference(this))
                .setSeekBar((SeekBar) findViewById(R.id.pref_key_long_break_frequency))
                .setSeekBarValue((TextView) findViewById(R.id.pref_key_long_break_frequency_value))
                .setMax(res.getInteger(R.integer.pref_long_break_frequency_max))
                .setMin(res.getInteger(R.integer.pref_long_break_frequency_min))
                .setUnit(R.string.pref_title_frequency_value)
                .setProgress(PreferenceManager.getDefaultSharedPreferences(this)
                        .getInt("pref_key_long_break_frequency",
                                TickApplication.DEFAULT_LONG_BREAK_FREQUENCY))
                .build();

        SwitchCompat pomodoroMode = (SwitchCompat) findViewById(R.id.pref_key_pomodoro_mode);
        pomodoroMode.setChecked(PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("pref_key_pomodoro_mode", true));
        pomodoroMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                SharedPreferences.Editor editor = PreferenceManager
                        .getDefaultSharedPreferences(getApplicationContext()).edit();
                editor.putBoolean("pref_key_pomodoro_mode", isChecked);

                if (isChecked) {
                    Intent i = TickService.newIntent(getApplicationContext());
                    i.setAction(TickService.ACTION_POMODORO_MODE_ON);
                    startService(i);
                }

                try {
                    editor.apply();
                } catch (AbstractMethodError unused) {
                    // The app injected its own pre-Gingerbread
                    // SharedPreferences.Editor implementation without
                    // an apply method.
                    editor.commit();
                }
            }
        });

        SwitchCompat infinityMode = (SwitchCompat) findViewById(R.id.pref_key_infinity_mode);
        infinityMode.setChecked(PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("pref_key_infinity_mode", false));
        infinityMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                SharedPreferences.Editor editor = PreferenceManager
                        .getDefaultSharedPreferences(getApplicationContext()).edit();
                editor.putBoolean("pref_key_infinity_mode", isChecked);

                try {
                    editor.apply();
                } catch (AbstractMethodError unused) {
                    editor.commit();
                }
            }
        });

        SwitchCompat tickSound = (SwitchCompat) findViewById(R.id.pref_key_tick_sound);
        tickSound.setChecked(PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("pref_key_tick_sound", true));
        tickSound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                SharedPreferences.Editor editor = PreferenceManager
                        .getDefaultSharedPreferences(getApplicationContext()).edit();
                editor.putBoolean("pref_key_tick_sound", isChecked);

                try {
                    editor.apply();
                } catch (AbstractMethodError unused) {
                    editor.commit();
                }

                Intent i = TickService.newIntent(getApplicationContext());

                if (isChecked) {
                    i.setAction(TickService.ACTION_TICK_SOUND_ON);
                } else {
                    i.setAction(TickService.ACTION_TICK_SOUND_OFF);
                }

                startService(i);
            }
        });

    }

    private void setToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    
}
