import { Component, OnInit } from '@angular/core';
import { NavController, NavParams } from 'ionic-angular';
import { MediaPlugin, MediaObject } from '@ionic-native/media';
import { Geolocation } from '@ionic-native/geolocation';
import { NativeGeocoder } from '@ionic-native/native-geocoder';

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
export class CreateStoryPage implements OnInit {

  slides: any[] = [];
  data: any;
  format: string;
  playing: boolean = false;
  paused: boolean = true;
  audio_file: MediaObject;

  location: string = "Location";
  qWhen: string = "When did this happen";
  qWho: string = "Who is involved";
  qWhy: string = "Why did this happen";
  description: string = "Provide a brief, precise summary of your story";
  longitude: number;
  latitude: number;

  constructor(
    public navCtrl: NavController, 
    public navParams: NavParams,
    private _geolocation: Geolocation,
    private _geocoder: NativeGeocoder,
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

  getCurrentLocation(){
    this._geolocation.getCurrentPosition().then((resp) => {
      this.latitude = resp.coords.latitude;
      this.longitude = resp.coords.longitude;
      this.getLocationString(this.longitude, this.latitude);
    });
  }

  getLocationString(longitude, latitude){
    this._geocoder.reverseGeocode(latitude, longitude).then((res) => {
      this.location = res.street + ", " + res.district + ", " + res.city + ", " + res.countryName;
    });
  }

  ngOnInit(): void {
    this.getCurrentLocation();
    }

  

}
