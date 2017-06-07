import {Component} from '@angular/core';
import {IonicPage, NavController, NavParams, Platform} from 'ionic-angular';
import {ScreenOrientation} from "@ionic-native/screen-orientation";
import {CameraPreviewOptions, CameraPreview} from "@ionic-native/camera-preview";

/**
 * Generated class for the CameraOverlay page.
 *
 * See http://ionicframework.com/docs/components/#navigation for more info
 * on Ionic pages and navigation.
 */
@Component({
    selector: 'page-camera-overlay',
    templateUrl: 'camera-overlay.html',
})
export class CameraOverlay {
    tabBarElement: any;
    htmlElement: any;
    imageOverlayElement: any;
    fabButtons: any;
    cameraPreviewOpts: CameraPreviewOptions;
    imagePath: String;
    captureType: any;
    buttons: any;


    constructor(public navCtrl: NavController,
                public navParams: NavParams,
                private cameraPreview: CameraPreview,
                private screenOrientation: ScreenOrientation,
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
        });
    }

    ionViewWillEnter() {
        // this.screenOrientation.lock(this.screenOrientation.ORIENTATIONS.LANDSCAPE);
        this.tabBarElement.style.display = 'none';
        this.htmlElement.style.visibility = 'hidden';
        this.fabButtons.style.visibility = 'hidden';
        this.imageOverlayElement.style.visibility = 'visible';

    }

    ionViewWillLeave() {
        console.log("camera overlay left");
        this.htmlElement.style.visibility = 'visible';
        this.tabBarElement.style.display = 'flex';
        this.cameraPreview.stopCamera();
        this.imageOverlayElement.style.visibility = 'hidden';
        this.fabButtons.style.visibility = 'visible';

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
        }else if(this.captureType == "video"){
            console.log("start the camera for video capture");
        }else{
            console.log("do nothing with the camera");
        }
    }

}
