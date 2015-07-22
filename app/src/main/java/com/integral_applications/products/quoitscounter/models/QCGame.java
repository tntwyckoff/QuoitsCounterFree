package com.integral_applications.products.quoitscounter.models;

import android.content.ContextWrapper;

import java.io.Serializable;


public class QCGame implements Serializable {
    transient static QCGame _currentGame;
    // transient static ContextWrapper _context;
    transient QCSettings _settings;
    QCTeam _team0, _team1;

    public QCTeam getTeam0 (){
        return _team0;
    }

    public QCTeam getTeam1 (){
        return _team1;
    }

    public static QCGame getCurrentGame() {
        return  _currentGame;
    }


    // public static QCGame newGame(ContextWrapper context){
    public static QCGame newGame(QCSettings settings){
        // _context = context;
        // QCSettings settings = QCSettings.GetSettings(_context);

        _currentGame = new QCGame
        (
                settings,
                new QCTeam (settings._teamName0),
                new QCTeam (settings._teamName1)
        );

        return  _currentGame;
    }

    public QCGame (QCSettings settings, QCTeam team0, QCTeam team1){
        _settings = settings;
        _team0 = team0;
        _team1 = team1;
    }

}
