package com.integral_applications.products.quoitscounterfree;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.integral_applications.products.quoitscounter.models.QCGame;
import com.integral_applications.products.quoitscounter.models.QCSettings;

import java.lang.reflect.Method;


public class MainActivity extends AppCompatActivity {

    final int START_EDIT_TEAMS_REQUEST_CODE = 1;
    final int START_ADJUST_SCRORE_TEAM_0 = 2;
    final int START_ADJUST_SCRORE_TEAM_1 = 3;

    QCSettings _settings;
    QCGame _game;
    int _workingScore = 0;
    int _currentRound = 1;
    boolean _workingTeam = false;
    boolean _editTeamsShown = false;

    Button _apply0, _apply1, _cancel0, _cancel1;
    ImageView _pointTeam0, _ringerTeam0, _pointTeam1, _ringerTeam1;
    TextView _labelTeamName0, _labelTeamName1, _labelScore0, _labelScore1, _labelChange0, _labelChange1;
    LinearLayout _pendingOverlayTeam0, _pendingOverlayTeam1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (null != savedInstanceState) {
            _settings = (QCSettings) savedInstanceState.getSerializable("_settings");
            _game = (QCGame) savedInstanceState.getSerializable("_game");
            _workingScore = savedInstanceState.getInt("_workingScore");
            _currentRound = savedInstanceState.getInt("_currentRound");
            _workingTeam = savedInstanceState.getBoolean("_workingTeam");
            _editTeamsShown = savedInstanceState.getBoolean("_editTeamsShown");
        } else {
            if (null == _settings)
                _settings = QCSettings.GetSettings(this);

            if (null == _game)
                _game = QCGame.newGame(_settings);
        }

        findViews();
        setListeners();

        if (_settings.getShowEditTeamsOnStart() && !_editTeamsShown) {
            Intent editActivity = new Intent("android.intent.action.EDITTEAMS");
            startActivityForResult(editActivity, START_EDIT_TEAMS_REQUEST_CODE);
        } else {

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // icon hack for menu: http://stackoverflow.com/questions/18374183/how-to-show-icons-in-overflow-menu-in-actionbar
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (menu != null) {
            if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
                try {
                    Method m = menu.getClass().getDeclaredMethod(
                            "setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (Exception e) {
                    Log.e(getClass().getSimpleName(), "onMenuOpened...unable to set icons for overflow menu", e);
                }
            }
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();

        switch (id) {
            case R.id.action_reset_game: {
                resetScore();
                break;
            }
            case R.id.action_clear_preferences: {
                resetPreferences();
                break;
            }
            case R.id.action_end_game: {
                endGame();
                break;
            }
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case START_EDIT_TEAMS_REQUEST_CODE: {
                _editTeamsShown = resultCode == Activity.RESULT_OK;

                if (_editTeamsShown) {
                    // anything to do?
                    _settings = QCSettings.GetSettings(this);
                    _settings = (QCSettings)data.getExtras().getSerializable("QCSettings");
                    _game = QCGame.newGame(_settings);
                }
            }
            case START_ADJUST_SCRORE_TEAM_0:
            case START_ADJUST_SCRORE_TEAM_1: {
                if (resultCode != Activity.RESULT_OK)
                    return;

                updateUIPostApplyScore();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        updateUIInit();

        if (0 != _workingScore)
            updateUIPostWorkingScoreChange();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable("_settings", _settings);
        outState.putSerializable("_game", _game);
        outState.putInt("_workingScore", _workingScore);
        outState.putInt("_currentRound", _currentRound);
        outState.putBoolean("_workingTeam", _workingTeam);
        outState.putBoolean("_editTeamsShown", _editTeamsShown);
    }


    private void addToWorkingScore(boolean team, int score) {
        _workingTeam = team;
        _workingScore += score;

        disablePointControls(team);
        updateUIPostWorkingScoreChange();
    }

    private void applyScore() {
        if (!_workingTeam)
            _game.getTeam0().adjustScore(_workingScore);
        else
            _game.getTeam1().adjustScore(_workingScore);

        _workingScore = 0;
        _currentRound++;

        enablePointControls();
        updateUIPostApplyScore();
    }

    private void cancelWorkingScore() {
        _workingScore = 0;
        hidePendingElements();
        enablePointControls();
    }

    private void instantAddScore(boolean team, int score) {
        _workingTeam = team;
        _workingScore += score;
        applyScore();
    }

    void resetScore() {
        _game.getTeam0().setScore(0);
        _game.getTeam1().setScore(0);
        _currentRound = 0;

        hidePendingElements();
        updateUIInit();

        Toast t = Toast.makeText
                (getApplicationContext(), R.string.action_reset_confirmation, Toast.LENGTH_SHORT);

        t.setGravity(Gravity.TOP, 0, 40);
        t.show();
    }

    void resetPreferences() {
        QCSettings.clearSettings(this);
        propagateNewSettings();

        updateUIInit();
        updateUIPostWorkingScoreChange();
        // updateUIPostApplyScore();

        Toast t = Toast.makeText
                (getApplicationContext(), R.string.action_reset_prefs_confirmation, Toast.LENGTH_SHORT);

        t.setGravity(Gravity.TOP, 0, 40);
        t.show();
    }

    void endGame() {
        finish();
        this.onStop();
        // finishAffinity();
        // System.exit(0);
    }

    void propagateNewSettings(){
        _settings = QCSettings.GetSettings(this);
        QCGame newGame = QCGame.newGame(_settings);

        newGame.getTeam0().setScore(_game.getTeam0().getScore());
        newGame.getTeam1().setScore(_game.getTeam1().getScore());

        _game = newGame;
    }

    private void teamLabelEditGesture(boolean team) {
        int teamParam = team ? START_ADJUST_SCRORE_TEAM_1 : START_ADJUST_SCRORE_TEAM_0;
        Intent adjustActivity = new Intent("android.intent.action.ADJUSTSCORE");
        adjustActivity.putExtra("team", team);
        startActivityForResult(adjustActivity, teamParam);
    }

    private void setListeners() {
        _pointTeam0.setOnTouchListener((view, ev) -> {
            int action = ev.getActionMasked();

            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    addToWorkingScore(false, 1);
                    break;
            }

            return false;
        });

        _pointTeam0.setLongClickable(true);
        _pointTeam0.setOnLongClickListener(view -> {
            applyScore();
            return true;
        });

        _ringerTeam0.setOnTouchListener((view, ev) -> {
            int action = ev.getActionMasked();

            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    addToWorkingScore(false, 2);
                    break;
            }

            return false;
        });

        _ringerTeam0.setLongClickable(true);
        _ringerTeam0.setOnLongClickListener(view -> {
            applyScore();
            return true;
        });

        _pointTeam1.setOnTouchListener((view, ev) -> {
            int action = ev.getActionMasked();

            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    addToWorkingScore(true, 1);
                    break;
            }

            return false;
        });

        _pointTeam1.setLongClickable(true);
        _pointTeam1.setOnLongClickListener(view -> {
            applyScore();
            return true;
        });

        _ringerTeam1.setOnTouchListener((view, ev) -> {
            int action = ev.getActionMasked();

            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    addToWorkingScore(true, 2);
                    break;
            }

            return false;
        });

        _ringerTeam1.setLongClickable(true);
        _ringerTeam1.setOnLongClickListener(view -> {
            applyScore();
            return true;
        });

        _apply0.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                applyScore();
            }
        });
        _cancel0.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                cancelWorkingScore();
            }
        });

        _apply1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                applyScore();
            }
        });
        _cancel1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                cancelWorkingScore();
            }
        });

        _labelScore0.setLongClickable(true);
        _labelScore0.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                teamLabelEditGesture(false);
                return true;
            }
        });

        _labelScore1.setLongClickable(true);
        _labelScore1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                teamLabelEditGesture(true);
                return true;
            }
        });
    }

    private void findViews() {
        this._apply0 = (Button) findViewById(R.id.apply0);
        this._cancel0 = (Button) findViewById(R.id.cancel0);
        this._apply1 = (Button) findViewById(R.id.apply1);
        this._cancel1 = (Button) findViewById(R.id.cancel1);
        this._labelTeamName0 = (TextView) findViewById(R.id.labelTeamName0);
        this._labelTeamName1 = (TextView) findViewById(R.id.labelTeamName1);
        this._labelChange0 = (TextView) findViewById(R.id.labelChangeTeam0);
        this._labelChange1 = (TextView) findViewById(R.id.labelChangeTeam1);
        this._labelScore0 = (TextView) findViewById(R.id.labelScore0);
        this._labelScore1 = (TextView) findViewById(R.id.labelScore1);
        this._pendingOverlayTeam0 = (LinearLayout) findViewById(R.id.pendingOverlayTeam0);
        this._pendingOverlayTeam1 = (LinearLayout) findViewById(R.id.pendingOverlayTeam1);
        this._pointTeam0 = (ImageView) findViewById(R.id.pointTeam0);
        this._pointTeam1 = (ImageView) findViewById(R.id.pointTeam1);
        this._ringerTeam0 = (ImageView) findViewById(R.id.ringerTeam0);
        this._ringerTeam1 = (ImageView) findViewById(R.id.ringerTeam1);
    }

    private void updateUIPostWorkingScoreChange() {

        if (0 >= _workingScore) {
            hidePendingElements();
            return;
        }

        if (!_workingTeam) {
            // points are being added to T0
            this._pendingOverlayTeam0.setVisibility(View.VISIBLE);
            // _team0Preview.setVisibility(View.VISIBLE);
            this._labelChange0.setText(String.format(getString(R.string.applyScoreFormat), _workingScore, _settings.getTeamName0()));

            return;
        }

        // points are being added to T1
        this._pendingOverlayTeam1.setVisibility(View.VISIBLE);
        this._labelChange1.setText(String.format(getString(R.string.applyScoreFormat), _workingScore, _settings.getTeamName1()));
    }

    private void updateUIPostApplyScore() {
        hidePendingElements();

//        _roundSummary.setText
//                (
//                        String.format
//                                (
//                                        getString(R.string.round_summary_text),
//                                        _currentRound
//                                )
//                );

        if (!_workingTeam) {
//            _team0Summary.setText(String.format
//                    (
//                            getString(R.string.scoreSummaryFormat),
//                            _game.getTeam0().getTeamName(),
//                            _game.getTeam0().getScore()
//                    ));
            this._labelScore0.setText(String.format(getString(R.string.scoreSummaryFormat), _game.getTeam0().getScore()));
            return;
        }

//        _team1Summary.setText(String.format
//                (
//                        getString(R.string.scoreSummaryFormat),
//                        _game.getTeam1().getTeamName(),
//                        _game.getTeam1().getScore()
//                ));
        this._labelScore1.setText(String.format(getString(R.string.scoreSummaryFormat), _game.getTeam1().getScore()));
    }

    private void updateUIInit() {
        this._labelTeamName0.setText(_game.getTeam0().getTeamName());
        this._labelTeamName1.setText(_game.getTeam1().getTeamName());
        this._labelScore0.setText(String.format(getString(R.string.scoreSummaryFormat), _game.getTeam0().getScore()));
        this._labelScore1.setText(String.format(getString(R.string.scoreSummaryFormat), _game.getTeam1().getScore()));
        hidePendingElements();
//        _team0Summary.setText
//                (
//                        String.format
//                                (
//                                        getString(R.string.scoreSummaryFormat),
//                                        _game.getTeam0().getTeamName(),
//                                        _game.getTeam0().getScore()
//                                )
//                );
//        _team1Summary.setText
//                (
//                        String.format
//                                (
//                                        getString(R.string.scoreSummaryFormat),
//                                        _game.getTeam1().getTeamName(),
//                                        _game.getTeam1().getScore()
//                                )
//                );
//        _roundSummary.setText
//                (
//                        String.format
//                                (
//                                        getString(R.string.round_summary_text),
//                                        _currentRound
//                                )
//                );
    }

    private void hidePendingElements() {
        this._pendingOverlayTeam0.setVisibility(View.INVISIBLE);
        this._pendingOverlayTeam1.setVisibility(View.INVISIBLE);

//        _buttonGroup0.setVisibility(View.INVISIBLE);
//        _buttonGroup1.setVisibility(View.INVISIBLE);
//        _team0Preview.setVisibility(View.INVISIBLE);
//        _team1Preview.setVisibility(View.INVISIBLE);
    }

    private void disablePointControls(boolean team) {
//        if (!team) {
//            // team 0 stays enabled; disable T1:
//            _addOne1.setEnabled(false);
//            _addRinger1.setEnabled(false);
//            return;
//        }
//
//        _addOne0.setEnabled(false);
//        _addRinger0.setEnabled(false);
    }

    private void enablePointControls() {
//        _addOne0.setEnabled(true);
//        _addRinger0.setEnabled(true);
//
//        _addOne1.setEnabled(true);
//        _addRinger1.setEnabled(true);
    }


}
