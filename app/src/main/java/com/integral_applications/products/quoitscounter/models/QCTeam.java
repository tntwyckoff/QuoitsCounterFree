package com.integral_applications.products.quoitscounter.models;


import java.io.Serializable;

public class QCTeam implements Serializable {
    String _teamName;
    int _score = 0;

    public  int getScore(){
        return _score;
    }

    public void adjustScore (int change){
        _score += change;
    }

    public void setScore (int newScore){
        _score = newScore;
    }

    public String getTeamName (){
        return  _teamName;
    }


    public QCTeam (String teamName){
        _teamName = teamName;
    }


}
