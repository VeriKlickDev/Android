package com.data.extras;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.twillioproject.R;
import com.example.twillioproject.databinding.LayoutVideoConfrencingBinding;
import com.twilio.video.VideoView;

import tvi.webrtc.voiceengine.WebRtcAudioUtils;

public class TwilioRoomConnector extends AppCompatActivity {

    LayoutVideoConfrencingBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.layout_video_confrencing, null, false);
        setContentView(binding.getRoot());

        WebRtcAudioUtils.setWebRtcBasedAcousticEchoCanceler(true);
        WebRtcAudioUtils.setWebRtcBasedNoiseSuppressor(true);
        WebRtcAudioUtils.setWebRtcBasedAutomaticGainControl(true);

        // Enable OpenSL ES
        tvi.webrtc.voiceengine.WebRtcAudioManager.setBlacklistDeviceForOpenSLESUsage(false);

        // Check if OpenSL ES is disabled
        //tvi.webrtc.voiceengine.WebRtcAudioUtils.deviceIsBlacklistedForOpenSLESUsage();

        /*
         * Get videoContainer
         */
      //  FrameLayout videoContainer = findViewById(R.id.video_container);

        /*
         * Create thumbnail video view
         */
        VideoView thumbnailVideoView = new VideoView(this);

        /*
         * Mirror the video. Set to true when rendering video from a local video track using the
         * front facing camera. Set to false otherwise.
         */
        thumbnailVideoView.setMirror(true);

        /*
         * Overlays the thumbnail video view on top of the primary video view
         */
        thumbnailVideoView.applyZOrder(true);

        /*
         * Create primary video view
         */
        VideoView primaryVideoView = new VideoView(this);

        /*
         * Add video views to container
         */
     //   videoContainer.addView(thumbnailVideoView);
      //  videoContainer.addView(primaryVideoView);


    }
}
