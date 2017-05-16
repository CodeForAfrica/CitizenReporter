import { Component } from '@angular/core';
import { NavController, NavParams } from 'ionic-angular';
import { MediaCapture, MediaFile, CaptureError } from '@ionic-native/media-capture';
import { Camera } from '@ionic-native/camera';
import { CreateStoryPage } from "../create-story-page/create-story-page";

/**
 * Generated class for the AssignmentDetail page.
 *
 * See http://ionicframework.com/docs/components/#navigation for more info
 * on Ionic pages and navigation.
 */

@Component({
  selector: 'page-assignment-detail',
  templateUrl: 'assignment-detail.html',
  providers: [MediaCapture]
})
export class AssignmentDetailPage {

  assignment: any;
  base64Image: string;

  constructor(
    public navCtrl: NavController, 
    public navParams: NavParams,
    private mediaCapture: MediaCapture,
    private camera: Camera
    ) {
    this.assignment = navParams.get('assignment');

  }

  ionViewDidLoad() {
    console.log('ionViewDidLoad AssignmentDetail');
  }

  recordAudio(){
    this.mediaCapture.captureAudio().then(
      (data: MediaFile[]) => {
        console.log(data[0].fullPath);
        console.log(data[0].type);
        this.navCtrl.push(CreateStoryPage, {path: data[0].fullPath, format: data[0].type})
      },
      (err: CaptureError) => console.error(err)
    );
  }

  captureImage(){
        this.mediaCapture.captureImage().then(
      (data: MediaFile[]) => {
        this.navCtrl.push(CreateStoryPage, {path: data[0].fullPath, format: data[0].type})
      })

  }

  captureVideo(){
    this.mediaCapture.captureVideo().then(
      (data: MediaFile[]) => {
        this.navCtrl.push(CreateStoryPage, {path: data[0].fullPath, format: data[0].type})
      })
    
  }



}
