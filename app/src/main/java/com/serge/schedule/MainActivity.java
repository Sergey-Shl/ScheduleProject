package com.serge.schedule;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.serge.schedule.list.ListActivity;
import com.serge.schedule.helpfullTools.ATTRIBUTE_CONSTS;

import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class MainActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    ScheduleManager scheduleManager;
    Toolbar toolbar;

    private ViewPager mViewPager;
    private SharedPreferences preferences;
    SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener;
    private static SimpleAdapter[] simpleAdapter;

    final ATTRIBUTE_CONSTS consts = new ATTRIBUTE_CONSTS();

    private File rootFile;
    private InputStream inputStreamXpp = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rootFile = getFilesDir();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        simpleAdapter = new SimpleAdapter[6];
        scheduleManager = new ScheduleManager();

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        preferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                if(key.equals("list_pref")) {
                    Log.i("INFO", "Preference has been changed");
                    updateSchedule();
                }
            }
        };
        preferences.registerOnSharedPreferenceChangeListener(preferenceChangeListener);

        if (!preferences.getBoolean("filesExists", false)) {
            Log.i("INFO", "shedule not exists");
            firstRunApp();
        } else {
            Log.i("INFO", "shedule exists");
            try {
                if (preferences.getString("list_pref", "first").equals("first"))
                    scheduleManager.Parse(getInputStream(1));
                else
                    scheduleManager.Parse(getInputStream(2));
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        toolbar.setSubtitle(scheduleManager.getWeekParaty());

        for (int i = 0; i < simpleAdapter.length; i++) {
            simpleAdapter[i] = new SimpleAdapter(MainActivity.this, scheduleManager.getDays(i), R.layout.schedule_item, consts.getFrom(), consts.getTo());
        }

        mViewPager.setCurrentItem(scheduleManager.getNumOfDay() - 1);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fffa);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (scheduleManager.getCurrentWeekNum() != scheduleManager.getWeekNum()) {
                    scheduleManager.setWeekNum(scheduleManager.getCurrentWeekNum());

                    toolbar.setSubtitle(scheduleManager.getWeekParaty());

                    for (int i = 0; i < simpleAdapter.length; i++) {
                        simpleAdapter[i] = new SimpleAdapter(MainActivity.this, scheduleManager.getDays(i), R.layout.schedule_item, consts.getFrom(), consts.getTo());
                    }
                    //mViewPager.setAdapter(mSectionsPagerAdapter);
                    mSectionsPagerAdapter.notifyDataSetChanged();
                    mViewPager.setCurrentItem(scheduleManager.getNumOfDay() - 1);
                    Toast.makeText(getApplicationContext(), "Вернулись на текущую неделю", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(getApplicationContext(), "Вы смотрите текущую неделю", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        final ATTRIBUTE_CONSTS consts = new ATTRIBUTE_CONSTS();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.next_week || id == R.id.prev_week) {
            int currentPage = mViewPager.getCurrentItem();

            switch (id) {
                case R.id.next_week:
                    scheduleManager.setWeekNum(scheduleManager.getWeekNum() + 1);
                    break;
                case R.id.prev_week:
                    scheduleManager.setWeekNum(scheduleManager.getWeekNum() - 1);
                    break;
            }

            toolbar.setSubtitle(scheduleManager.getWeekParaty());

            for (int i = 0; i < simpleAdapter.length; i++) {
                simpleAdapter[i] = new SimpleAdapter(MainActivity.this, scheduleManager.getDays(i), R.layout.schedule_item, consts.getFrom(), consts.getTo());
            }

            mViewPager.setAdapter(mSectionsPagerAdapter);
            mViewPager.setCurrentItem(currentPage);

            return true;
        }

        if (id == R.id.update) {
            DownloadFiles downloadFiles = new DownloadFiles();
            downloadFiles.execute(rootFile, this, preferences);
        }

        if(id == R.id.subject_list)
        {
            startActivity(new Intent(this, ListActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    public void firstRunApp()
    {
        Log.i("Info", "First run func");
        preferences.edit().putBoolean("filesExists", false).commit();
        DownloadFiles downloadFiles = new DownloadFiles();
        downloadFiles.execute(rootFile, this, preferences);
        try {
            if (preferences.getString("list_pref", "first").equals("first"))
                scheduleManager.Parse(getInputStream(1));
            else
                scheduleManager.Parse(getInputStream(2));
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        preferences.edit().putBoolean("firstrun", false).commit();
    }

    public static class PlaceholderFragment extends Fragment {

        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            ListView listView;
            listView = (ListView) rootView.findViewById(R.id.schedule_list_view);
            listView.setAdapter(simpleAdapter[getArguments().getInt(ARG_SECTION_NUMBER) - 1]);
            return rootView;
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            return 6;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            final String[] days = {"Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота"};
            return days[position];
        }

        @Override
        public void finishUpdate(ViewGroup container) {
            try{
                super.finishUpdate(container);
            } catch (NullPointerException nullPointerException){
                Log.e("ERROR", "Catch the NullPointerException in FragmentPagerAdapter.finishUpdate");
            }
        }
    }

    public void updateSchedule()
    {
        Log.i("INFO", "Schedule was updated");
        int currentPage = mViewPager.getCurrentItem();
        int currentViewedWeek = scheduleManager.getWeekNum();

        try {
            if (preferences.getString("list_pref", "first").equals("first"))
                scheduleManager.Parse(getInputStream(1));
            else
                scheduleManager.Parse(getInputStream(2));
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        scheduleManager.setWeekNum(currentViewedWeek);

        for (int i = 0; i < simpleAdapter.length; i++) {
            simpleAdapter[i] = new SimpleAdapter(MainActivity.this, scheduleManager.getDays(i), R.layout.schedule_item, consts.getFrom(), consts.getTo());
        }

        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setCurrentItem(currentPage);
    }

    private InputStream getInputStream(int groupNum) throws IOException {
        InputStream object = null;
        if (!preferences.getBoolean("filesExists", false)) {
            if (groupNum == 1)
                object = this.getResources().openRawResource(R.raw.schedule_first);
            else
                object = this.getResources().openRawResource(R.raw.schedule_second);
            Log.i("INFO","Расписание с устройства");
            return object;
        } else {
            File scheduleFile;
            if (groupNum == 1)
                scheduleFile = new File(rootFile, "FirstSchedule.xml");
            else
                scheduleFile = new File(rootFile, "SecondSchedule.xml");;
            try {
                object = new BufferedInputStream(new FileInputStream(scheduleFile));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            return object;
        }
    }

    public class DownloadFiles extends AsyncTask {

        private boolean downloadFile(String fileURL, String fileName, File rootFile) {
            try {
                URL url = new URL(fileURL);
                HttpURLConnection c = (HttpURLConnection) url.openConnection();
                c.setRequestMethod("GET");
                //c.setDoOutput(true);
                c.connect();

                FileOutputStream f = new FileOutputStream(new File(rootFile,
                        fileName));
                InputStream in = c.getInputStream();
                byte[] buffer = new byte[1024];
                int len1 = 0;
                while ((len1 = in.read(buffer)) > 0) {
                    f.write(buffer, 0, len1);
                }
                f.close();
                return true;
            } catch (IOException e) {
                Log.d("Error....", e.toString());
                return false;
            }
        }

        @Override
        protected Object doInBackground(Object[] params) {
            boolean download1, download2, download3;
            //parent = (Activity) params[1];

            download1 = downloadFile("https://github.com/Sergey-Shl/Schedule/raw/master/Shedule1.xml", "FirstSchedule.xml", (File) params[0]);
            download2 = downloadFile("https://github.com/Sergey-Shl/Schedule/raw/master/Shedule2.xml", "SecondSchedule.xml", (File) params[0]);
            download3 = downloadFile("https://github.com/Sergey-Shl/Schedule/raw/master/Subjects_list.xml", "SubjectsList.xml", (File) params[0]);
            //https://cdn.rawgit.com/Sergey-Shl/Schedule/cac6d9c6/Shedule2.xml
            //https://rawgit.com/Sergey-Shl/Schedule/master/Shedule1.xml
            if(download1 && download2 && params[2] != null)
            {
                Log.i("Download", "Files was downloaded");
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getBaseContext(), "Загрузка завершена", Toast.LENGTH_LONG).show();
                    }
                });
                ((SharedPreferences) params[2]).edit().putBoolean("filesExists", true).commit();
            }
            else
            {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getBaseContext(), "Ошибка загрузки расписания, проверьте интернет-подключение", Toast.LENGTH_LONG).show();
                    }
                });
            }

            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            updateSchedule();
        }
    }

}
