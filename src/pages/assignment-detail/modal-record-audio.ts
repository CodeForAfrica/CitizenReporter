import { Component } from '@angular/core';
import { Platform, NavParams, ViewController } from 'ionic-angular';
import { MediaPlugin } from '@ionic-native/media';
import { File } from '@ionic-native/file';

@Component({
      template: `
      <ion-header>
        <ion-toolbar>
          <ion-title>
            Voice Recorder
          </ion-title>
          <ion-buttons start>
            <button ion-button (click)="dismiss()">
              <span ion-text color="primary" showWhen="ios">Cancel</span>
              <ion-icon name="md-close" showWhen="android, windows"></ion-icon>
            </button>
          </ion-buttons>
        </ion-toolbar>
      </ion-header>
      <ion-content>
      <ion-row>
        <ion-img src="../../assets/img/audio.png"></ion-img>
      </ion-row>
      <ion-row>
        <p>00:00:04</p>
      </ion-row>
      <ion-row>
        <ion-icon name="radio-button-on"></ion-icon
        <ion-icon name="square"></ion-icon>
      </ion-row>
      </ion-content>
      `
})
export class RecordAudioModal {
    dataDirectory: string;
    timestamp: number;
    media;
    constructor(
        public platform: Platform,
        public params: NavParams,
        public viewCtrl: ViewController,
        private file: File
    ){
        this.dataDirectory = this.file.dataDirectory + '/recording/';
    }


    startRecording(){
        let time = Math.ceil(new Date().getTime() / 1000);
        let file = this.dataDirectory + time.toString + ".mp3";
        this.media = new MediaPlugin(file);
        this.media.startRecord();
    }

    pauseRecording(){
        this.media.pauseRecord();
    }

    stopRecording(){
        this.media.stopRecord();
    }

}