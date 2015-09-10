package com.BlueMatrix.sound;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import com.BlueMatrix.Activity.R;

import java.util.HashMap;

/**
 * Created by chzu131 on 2015/9/9.
 */
public class Sound {
    private static SoundPool pool = null;
    private static int sourceid;

    public void initSoundPool(Context context) {
        //指定声音池的最大音频流数目为10，声音品质为5
        pool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
        //载入音频流，返回在池中的id
       sourceid = pool.load(context, R.raw.shot, 0);
    }

    public void playSound(){
        pool.play(sourceid, 1, 1, 0, 0, 1);
    }
}
