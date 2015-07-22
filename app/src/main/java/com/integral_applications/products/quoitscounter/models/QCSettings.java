package com.integral_applications.products.quoitscounter.models;

import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;

import com.integral_applications.products.quoitscounterfree.R;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class QCSettings implements Serializable {

    private static final long serialVersionUID = 1;
    static QCSettings _instance;
    boolean _showEditTeamsOnStart = true, _loadedFromDisk = false;
    String _teamName0, _teamName1;
    int _runCount = 0, _currentRound = 1;
    // transient ContextWrapper _context;

    static QCSettings getInstance(ContextWrapper context){
        if (null == _instance) {
            Boolean loaded = false;

            try {
                FileInputStream fileInputStream = context.openFileInput(context.getString(R.string.settings_file_name));
                ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

                _instance = (QCSettings) objectInputStream.readObject();

                objectInputStream.close();
                fileInputStream.close();

                // _instance.setContext(context);
                _instance.setLoadedFromDisk(loaded = true);

            } catch (IOException e) {
                e.printStackTrace();
            }
            catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            if (!loaded) {
                _instance = new QCSettings();
//
//                _instance.setContext(context);
//
//                _instance.setTeamName0(context.getString(R.string.team_name_0));
//                _instance.setTeamName1(context.getString(R.string.team_name_1));
            }
        }

        _instance.incrementRunCount();

        return _instance;
    }

    public int incrementCurrentRound (){
        return ++_currentRound;
    }

    public void setShowEditTeamsOnStart (Boolean value){
        _showEditTeamsOnStart = value;
    }

    public Boolean getShowEditTeamsOnStart (){
        return _showEditTeamsOnStart;
    }

    public void setTeamName0 (String value){
        _teamName0 = value;
    }

    public String getTeamName0 (){
        return _teamName0;
    }

    public void setTeamName1 (String value){
        _teamName1 = value;
    }

    public String getTeamName1 (){
        return _teamName1;
    }

    public Boolean getIsFirstRun (){
        return 1 == _runCount;
    }

    public static Boolean saveSettings (ContextWrapper context){
        Boolean result = false;

        try
        {
            FileOutputStream fos = context.openFileOutput(context.getString(R.string.settings_file_name), Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(_instance);
            os.flush(); // flush the stream to insure all of the information was written to 'save_object.bin'
            os.close();// close the stream

            result = true;
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }

        return result;
    }

    public static boolean clearSettings(ContextWrapper context){
        boolean result = false;

        try {
            _instance = null;
            context.deleteFile(context.getString(R.string.settings_file_name));
            GetSettings(context);
            result = true;
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return result;
    }

    void incrementRunCount (){
        _runCount++;
    }
//
//    void setContext (ContextWrapper context){
//        _context = context;
//    }

    void setLoadedFromDisk (boolean loaded){
        _loadedFromDisk = loaded;
    }

    boolean getLoadedFromDisk (){
        return  _loadedFromDisk;
    }

    public static QCSettings GetSettings (ContextWrapper context){

        QCSettings result = getInstance(context);

        if (!result.getLoadedFromDisk()){
            result.setTeamName0(context.getString(R.string.team_name_0));
            result.setTeamName1(context.getString(R.string.team_name_1));
        }

        return result;
/*

        if (result.getIsFirstRun()) {
            result.setTeamName0(ctx.getString(R.string.team_name_0));
            result.setTeamName1(ctx.getString(R.string.team_name_1));
        }

*/
    }
}
