import { Component } from '@angular/core';
import { NavController, NavParams } from 'ionic-angular';
import { Constant } from "../../utils/constants";


/**
 * Generated class for the AboutPage page.
 *
 * See http://ionicframework.com/docs/components/#navigation for more info
 * on Ionic pages and navigation.
 */
@Component({
  selector: 'page-about-page',
  templateUrl: 'about-page.html',
  providers: [ Constant ]
})
export class AboutPage {

  version: string;

  constructor(public navCtrl: NavController, public navParams: NavParams, private constants: Constant) {
    this.version = this.constants.versionCode;
  }

  ionViewDidLoad() {
    console.log('ionViewDidLoad AboutPage');
  }

}
