import { Component } from '@angular/core';
import { NavController, NavParams } from 'ionic-angular';
import { MediaPlugin, MediaObject } from '@ionic-native/media';

/**
 * Generated class for the CreateStoryPage page.
 *
 * See http://ionicframework.com/docs/components/#navigation for more info
 * on Ionic pages and navigation.
 */
@Component({
  selector: 'page-create-story-page',
  templateUrl: 'create-story-page.html',
})
export class CreateStoryPage {

  slides: any[];
  data: any;
  format: string;
  playing: boolean = false;
  paused: boolean = true;
  audio_file: MediaObject;
  constructor(
    public navCtrl: NavController, 
    public navParams: NavParams,
    private media: MediaPlugin) {

    this.data = this.navParams.get("audio");
    this.audio_file = this.media.create(this.data);
    this.format = this.navParams.get("format");
    if (this.format == "audio/mpeg"){
      this.slides = ['../../assets/img/audio.png']
    }
    console.log(this.slides);

  }

  ionViewDidLoad() {
    console.log('ionViewDidLoad CreateStoryPage');
  }

  onPlay(){
    this.playing = true;
    this.paused = false;

    this.audio_file.play()
  }
  onPause(){
    this.playing = false;
    this.paused = true;

    this.audio_file.pause()
  }

}
