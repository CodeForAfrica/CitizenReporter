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
    captureType: any;

  constructor(public navCtrl: NavController, public navParams: NavParams, public viewCtrl: ViewController) {
      this.captureType = this.navParams.get('capture');
  }

  ionViewDidLoad() {
    console.log('ionViewDidLoad ScenePopover');
  }
  dismiss() {
    this.viewCtrl.dismiss();
  }

  openCameraOverlay(imagePath){
      this.navCtrl.push(CameraOverlay,
          {
              path: imagePath,
              capture: this.captureType

          });
  }

}
