package com.integral_applications.products.quoitscounter.models;

import android.content.ContextWrapper;


public class QCGame {
    static QCGame _currentGame;
    static ContextWrapper _context;
    QCTeam _team0,  _team1;

    public QCTeam getTeam0 (){
        return _team0;
    }

    public QCTeam getTeam1 (){
        return _team1;
    }

    public static QCGame getCurrentGame() {
        return  _currentGame;
    }


    public static QCGame newGame(ContextWrapper context){
        _context = context;

        QCSettings settings = QCSettings.GetSettings(_context);

        _currentGame = new QCGame
        (
                _context,
                new QCTeam (settings._teamName0),
                new QCTeam (settings._teamName1)
        );

        return  _currentGame;
    }

    QCGame (ContextWrapper ctx, QCTeam team0, QCTeam team1){
        _context = ctx;
        _team0 = team0;
        _team1 = team1;
    }

}
