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


    constructor(public navCtrl: NavController,
                public navParams: NavParams,
                private cameraPreview: CameraPreview,
                private screenOrientation: ScreenOrientation,
                private platform: Platform) {

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
            this.fabButtons = document.getElementsByTagName('ion-fab')[0];
        });
    }

    ionViewWillEnter() {
        // this.screenOrientation.lock(this.screenOrientation.ORIENTATIONS.LANDSCAPE);
        this.tabBarElement.style.display = 'none';
        this.htmlElement.style.visibility = 'hidden';
        // this.fabButtons.style.visibility = 'hidden';
        this.imageOverlayElement.style.visibility = 'visible';

    }

    ionViewWillLeave() {
        console.log("camera overlay left");
        this.htmlElement.style.visibility = 'visible';
        this.tabBarElement.style.display = 'flex';
        this.cameraPreview.stopCamera();
        // this.screenOrientation.unlock();
        // this.htmlElement.style.backgroundColor = 'visible';
        // this.fabButtons.style.backgroundColor = 'visible';
        this.imageOverlayElement.style.visibility = 'hidden';

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

}
