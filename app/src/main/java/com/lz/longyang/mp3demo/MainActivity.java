package com.lz.longyang.mp3demo;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.lz.longyang.mp3demo.R;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private Button btnPlay, btnPause, btnStop;
    private TextView text;
    private MediaPlayer player;

    // 声明一个变量判断是否为暂停,默认为false
    private boolean isPaused = false;
    private SeekBar seekBar;
    private boolean isSeekBarChanging;//记录是否暂停
    private Timer mTimer;
    private TimerTask mTimerTask;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnPlay = (Button) findViewById(R.id.btnPlay);
        btnPause = (Button) findViewById(R.id.btnPause);
        btnStop = (Button) findViewById(R.id.btnStop);
        text = (TextView) findViewById(R.id.text);
        seekBar = (SeekBar)findViewById(R.id.seekBar);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            /*滚动时,应当暂停后台定时器*/
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isSeekBarChanging = true;
            }

            /*滑动结束后，重新设置值*/
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                isSeekBarChanging = false;
                player.seekTo(seekBar.getProgress());//seekTo调节歌曲的进度
            }
        });

        // 创建MediaPlayer对象，使用raw文件夹下的sound1.mp3
        player = MediaPlayer.create(this, R.raw.sound1);

        // 增加播放音乐按钮的事件
        btnPlay.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (player != null) {
                        player.stop();
                    }
                    player.prepare();

                    text.setText("音乐播放中...");
                } catch (Exception e) {
                    text.setText("播放发生异常...");
                    e.printStackTrace();
                }
                seekBar.setMax(player.getDuration());//设置进度条 歌曲的时长
                //----------定时器记录播放进度---------//
                mTimer = new Timer();

                mTimerTask = new TimerTask() {
                    @Override
                    public void run() {
                        if(isSeekBarChanging==true) {
                            return;
                        }
                        seekBar.setProgress(player.getCurrentPosition());//当前位置
                    }
                };
                mTimer.schedule(mTimerTask, 0, 10);//延时重复执行mTimerTask
                player.start();
            }
        });

        // 暂停按钮
        btnPause.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (player != null) {
                        player.stop();
                        text.setText("音乐停止播放...");
                    }
                } catch (Exception e) {
                    text.setText("音乐停止发生异常...");
                    e.printStackTrace();
                }
            }
        });

        // 停止按钮
        btnStop.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (player != null) {
                        if (isPaused == false) {
                            player.pause();
                            isPaused = true;
                            text.setText("停止播放!");
                        } else if (isPaused == true) {
                            player.start();
                            isPaused = false;
                            text.setText("开始播放!");
                        }
                    }
                } catch (Exception e) {
                    text.setText("发生异常...");
                    e.printStackTrace();
                }
            }
        });

        // 播放完毕的处理
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer arg0) {
                try {
                    // 释放sound1.mp3这个资源，让它可被其他播放器使用
                    player.release();
                    text.setText("音乐播发结束!");
                } catch (Exception e) {
                    text.setText(e.toString());
                    e.printStackTrace();
                }
            }
        });

        // 播放出错的处理
        player.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer arg0, int arg1, int arg2) {
                try {
                    // 发生错误时也释放资源
                    player.release();
                    text.setText("播放发生异常!");
                } catch (Exception e) {
                    text.setText(e.toString());
                    e.printStackTrace();
                }
                return false;
            }
        });
    }
}
