import {Component} from '@angular/core';
import { NavController, NavParams, Platform} from 'ionic-angular';
import {CameraPreviewOptions, CameraPreview} from "@ionic-native/camera-preview";
import {CreateStoryPage} from "../create-story-page/create-story-page";
import {MediaCapture, MediaFile} from '@ionic-native/media-capture';
import {Camera} from '@ionic-native/camera';
import {ScenePicker} from "../scene-picker/scene-picker";
import { AndroidPermissions } from '@ionic-native/android-permissions';
import { ImagePicker } from '@ionic-native/image-picker';

/**
 * Generated class for the CameraOverlay page.
 *
 * See http://ionicframework.com/docs/components/#navigation for more info
 * on Ionic pages and navigation.
 */
@Component({
    selector: 'page-camera-overlay',
    templateUrl: 'camera-overlay.html',
    providers: [MediaCapture]
})
export class CameraOverlay {
    tabBarElement: any;
    htmlElement: any;
    imageOverlayElement: any;
    fabButtons: any;
    fabButtons1: any;
    fabButtons2: any;
    fabButtons3: any;
    cameraPreviewOpts: CameraPreviewOptions;
    imagePath: String;
    captureType: any;
    buttons: any;


    constructor(public navCtrl: NavController,
                public navParams: NavParams,
                private cameraPreview: CameraPreview,
                private camera: Camera,
                private imagePicker: ImagePicker,
                private androidPermissions: AndroidPermissions,
                private mediaCapture: MediaCapture,
                private platform: Platform) {

        this.imagePath = this.navParams.get('path');
        this.captureType = this.navParams.get('capture');

        this.cameraPreviewOpts = {
            x: 0,
            y: 0,
            width: window.screen.width,
            height: window.screen.height,
            camera: 'rear',
            tapPhoto: true,
            previewDrag: true,
            toBack: true,
            alpha: 1
        };

        this.startCamera();
    }

    ionViewDidLoad() {
        console.log('ionViewDidLoad CameraOverlay');

        this.platform.ready().then(() => {
            this.tabBarElement = document.querySelector('.tabbar.show-tabbar');
            this.htmlElement = document.getElementsByTagName('html')[0];
            this.imageOverlayElement = document.getElementById('image-overlay');
            this.fabButtons = document.getElementById('floating-buttons');
            this.fabButtons1 = document.getElementById('floating-buttons1');
            this.fabButtons2 = document.getElementById('floating-buttons2');
            this.fabButtons3 = document.getElementById('floating-buttons3');
        });
    }

    ionViewWillEnter() {
        // this.screenOrientation.lock(this.screenOrientation.ORIENTATIONS.LANDSCAPE);
        this.tabBarElement.style.display = 'none';
        this.htmlElement.style.visibility = 'hidden';
        this.fabButtons.style.visibility = 'hidden';
        this.fabButtons1.style.visibility = 'hidden';
        this.fabButtons2.style.visibility = 'hidden';
        this.fabButtons3.style.visibility = 'hidden';
        this.imageOverlayElement.style.visibility = 'visible';

    }

    ionViewWillLeave() {
        console.log("camera overlay left");
        this.htmlElement.style.visibility = 'visible';
        this.tabBarElement.style.display = 'flex';
        this.cameraPreview.stopCamera();
        this.imageOverlayElement.style.visibility = 'hidden';
        this.fabButtons.style.visibility = 'visible';
        this.fabButtons1.style.visibility = 'visible';
        this.fabButtons2.style.visibility = 'visible';
        this.fabButtons3.style.visibility = 'visible';

    }

    startCamera(){
        this.cameraPreview.startCamera(this.cameraPreviewOpts).then(
            (res) => {
                console.log(res)
            },
            (err) => {
                console.log(err)
            });
    }

    onTapOverlay(){
        if(this.captureType == "camera"){
            console.log("start the camera for image capture");
            this.captureImage();
        }else if(this.captureType == "video"){
            console.log("start the camera for video capture");
            this.captureVideo();
        }else{
            console.log("do nothing with the camera");
        }
    }

    captureImage() {
        this.androidPermissions.checkPermission(this.androidPermissions.PERMISSION.CAMERA).then(
            (success) => {
                this.camera.getPicture({
                    sourceType: this.camera.PictureSourceType.CAMERA,
                    destinationType: this.camera.DestinationType.FILE_URI,
                    saveToPhotoAlbum: true
                }).then((imagePath) => {
                    console.log(imagePath);
                    this.navCtrl.push(CreateStoryPage, {path: imagePath, format: "image/jpeg"})
                })
            },
            (err) => {
                this.androidPermissions.requestPermissions(this.androidPermissions.PERMISSION.CAMERA)
            })

    }

    captureVideo() {
        this.androidPermissions.checkPermission(this.androidPermissions.PERMISSION.CAMERA).then(
            (success) => {
                this.mediaCapture.captureVideo().then(
                    (data: MediaFile[]) => {
                        this.navCtrl.push(CreateStoryPage, {path: data[0].fullPath, format: data[0].type});
                    })
            },
            (err) => {
                this.androidPermissions.requestPermissions(this.androidPermissions.PERMISSION.CAMERA);
            })

    }

}
