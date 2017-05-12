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
  providers: [MediaPlugin]
})
export class CreateStoryPage {

  slides: any[] = [];
  data: any;
  format: string;
  playing: boolean = false;
  paused: boolean = true;
  file = "file:///storage/emuated/0/DCIM/Camera/20170512_133128.jpg"
  audio_file: MediaObject;
  constructor(
    public navCtrl: NavController, 
    public navParams: NavParams,
    private media: MediaPlugin) {

    this.data = this.navParams.get("path");
    this.audio_file = this.media.create(this.data);
    this.format = this.navParams.get("format");
    if (this.format == "audio/mpeg"){
      this.slides.push({file: "../../assets/img/audio.png", "format": this.format});
    } else {
      this.slides.push({file: this.data, "format": this.format});
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
