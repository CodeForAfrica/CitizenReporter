import { Component } from '@angular/core';
import { NavController, NavParams, ViewController } from 'ionic-angular';
import {CameraOverlay} from "../camera-overlay/camera-overlay";

/**
 * Generated class for the ScenePopover page.
 *
 * See http://ionicframework.com/docs/components/#navigation for more info
 * on Ionic pages and navigation.
 */
@Component({
  selector: 'page-scene-popover',
  templateUrl: 'scene-popover.html',
})
export class ScenePopover {

  constructor(public navCtrl: NavController, public navParams: NavParams, public viewCtrl: ViewController) {
  }

  ionViewDidLoad() {
    console.log('ionViewDidLoad ScenePopover');
  }
  dismiss() {
    this.viewCtrl.dismiss();
  }

  openCameraOverlay(){
      this.navCtrl.push(CameraOverlay);
      // this.viewCtrl.dismiss();
  }

}
