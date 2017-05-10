import { Component } from '@angular/core';
import { NavController, NavParams } from 'ionic-angular';

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
  constructor(public navCtrl: NavController, public navParams: NavParams) {
    this.data = this.navParams.get("audio");
    this.format = this.navParams.get("format");
    if (this.format == "audio/mpeg"){
      this.slides = ['../../assets/img/audio.png']
    }
    console.log(this.slides);

  }

  ionViewDidLoad() {
    console.log('ionViewDidLoad CreateStoryPage');
  }

}
