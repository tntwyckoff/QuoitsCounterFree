package com.integral_applications.products.quoitscounterfree;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.integral_applications.products.quoitscounter.models.QCGame;
import com.integral_applications.products.quoitscounter.models.QCTeam;


public class AdjustScoreActivity extends Activity {

    QCTeam _workingTeam;
    TextView _label;
    NumberPicker _adjustControl;
    Button _commit, _cancel;
    boolean _team;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adjust_score);

        _team = getIntent().getBooleanExtra("team", false);

        _workingTeam = _team ? QCGame.getCurrentGame().getTeam1() : QCGame.getCurrentGame().getTeam0();

        findViews();
        attachListeners();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_adjust_score, menu);
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
    protected void onResume() {
        super.onResume();

        updateUIInit();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }


    void internalCommit(){
        _workingTeam.setScore(_adjustControl.getValue());
        finish();
    }

    void internalCancel(){
        setResult(Activity.RESULT_CANCELED);
        finish();
    }

    private void updateUIInit (){
        _label.setText(String.format
                (
                        getString(R.string.modify_score_label_format),
                        _workingTeam.getTeamName()
                ));

        _adjustControl.setMaxValue(20);
        _adjustControl.setMinValue(0);
        _adjustControl.setValue(_workingTeam.getScore());
    }

    private void attachListeners(){
        _commit.setOnClickListener (new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   internalCommit();
               }
           }
        );

        _cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                internalCancel();
            }
        });
    }

    private void findViews () {
        _label = (TextView)findViewById(R.id.adjustScoreLabel);
        _adjustControl = (NumberPicker)findViewById(R.id.adjustScoreControl);
        _commit = (Button)findViewById(R.id.commitChange);
        _cancel = (Button)findViewById(R.id.cancelChange);
    }

}
