package com.integral_applications.products.quoitscounterfree;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.integral_applications.products.quoitscounter.models.QCSettings;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;


public class EditTeamsActivity extends AppCompatActivity {

    EditText _edit0, _edit1;
    CheckBox _showEditTeams;
    Button _commit, _clear0, _clear1;
    QCSettings _settings;

    public  QCSettings getSettings(){
        return _settings;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_teams);

        findViews();
        setListeners();

        if (null != savedInstanceState){
            _settings = (QCSettings)savedInstanceState.getSerializable("_settings");
        }

        if (null == _settings)
            _settings = QCSettings.GetSettings(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_edit_teams, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        _settings.setTeamName0(_edit0.getText().toString());
        _settings.setTeamName1(_edit1.getText().toString());
        _settings.setShowEditTeamsOnStart(_showEditTeams.isChecked());

        outState.putSerializable("_settings", _settings);
    }

    @Override
    protected void onResume() {
        super.onResume();

        updateUIFromState();
    }


    private void setListeners() {
        _commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                internalContinue();
            }
        });
        _clear0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearTeam0();
            }
        });
        _clear1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearTeam1();
            }
        });
    }

    void internalContinue(){
        String t0, t1;

        if (null == (t0 = _edit0.getText().toString()) || 1 > t0.length())
            return;

        if (null == (t1 = _edit1.getText().toString()) || 1 > t1.length())
            return;

        _settings.setTeamName0(t0);
        _settings.setTeamName1(t1);
        _settings.setShowEditTeamsOnStart(_showEditTeams.isChecked());

        Boolean saved = QCSettings.saveSettings(this);

        Intent intent = this.getIntent();
        intent.putExtra("QCSettings", _settings);

        setResult(Activity.RESULT_OK, intent);

        finish();
    }

    void clearTeam0(){
        _edit0.setText("");
        _edit0.performClick();
    }

    void clearTeam1(){
        _edit1.setText("");
        _edit1.selectAll();
    }

    private void updateUIFromState() {
        _showEditTeams.setChecked(_settings.getShowEditTeamsOnStart());
        _edit0.setText(_settings.getTeamName0());
        _edit1.setText(_settings.getTeamName1());
    }

    void findViews (){
        _edit0 = (EditText)findViewById(R.id.editTeam0);
        _edit1 = (EditText)findViewById(R.id.editTeam1);
        _showEditTeams = (CheckBox)findViewById(R.id.editShowTeamNames);
        _commit = (Button)findViewById(R.id.btnCommit);
        _clear0 = (Button)findViewById(R.id.clearTeam0);
        _clear1 = (Button)findViewById(R.id.clearTeam1);
    }
}
