import { Component } from '@angular/core';
import { NavController, NavParams, ModalController } from 'ionic-angular';
import { MediaCapture, MediaFile, CaptureError } from '@ionic-native/media-capture';
import { Camera } from '@ionic-native/camera';
import { CreateStoryPage } from "../create-story-page/create-story-page";
import { File } from '@ionic-native/file';

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
    private camera: Camera,
    private modalCtrl: ModalController,
    private file: File
    ) {
    this.assignment = navParams.get('assignment');
    console.log(this.file.dataDirectory);

  }

  ionViewDidLoad() {
    console.log('ionViewDidLoad AssignmentDetail');
  }

  recordAudio(){
    // this.mediaCapture.captureAudio().then(
    //   (data: MediaFile[]) => {
    //     console.log(data[0].fullPath);
    //     console.log(data[0].type);
    //     alert(data[0].type);
    //     this.navCtrl.push(CreateStoryPage, {path: data[0].fullPath, format: 'audio'})
    //   },
    //   (err: CaptureError) => console.error(err)
    // );


  }

  captureImage(){
    this.camera.getPicture({
      sourceType: this.camera.PictureSourceType.CAMERA,
      destinationType: this.camera.DestinationType.FILE_URI,
      saveToPhotoAlbum: true
    }).then((imagePath) => {
      console.log(imagePath);
      this.navCtrl.push(CreateStoryPage, {path: imagePath, format: "image/jpeg"})
    })
      // this.mediaCapture.captureImage().then(
      // (data: MediaFile[]) => {
      //   alert("name: " + data[0].name + "/nPath: " + data[0].fullPath);
      //   this.navCtrl.push(CreateStoryPage, {path: data[0].fullPath, format: data[0].type})
      // })

  }

  captureVideo(){
    this.mediaCapture.captureVideo().then(
      (data: MediaFile[]) => {
        this.navCtrl.push(CreateStoryPage, {path: data[0].fullPath, format: data[0].type})
      })
    
  }



}
