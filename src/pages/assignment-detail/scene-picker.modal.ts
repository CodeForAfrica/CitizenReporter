import {Component} from '@angular/core';
import {Platform, NavParams, ViewController, NavController} from 'ionic-angular';
import {Camera} from '@ionic-native/camera';
import { ScreenOrientation } from '@ionic-native/screen-orientation';
import {CreateStoryPage} from "../create-story-page/create-story-page";


@Component({
    templateUrl: 'scene-picker.modal.html',
})
export class ModalScenePicker {

    constructor(public platform: Platform,
                public navCtrl: NavController,
                public params: NavParams,
                private camera: Camera,
                private orientation: ScreenOrientation,
                public viewCtrl: ViewController) {

    }

    dismiss() {
        this.viewCtrl.dismiss();
    }

    onSceneCandid(){
        this.orientation.lock(this.orientation.ORIENTATIONS.LANDSCAPE);
        this.camera.getPicture({
            sourceType: this.camera.PictureSourceType.CAMERA,
            destinationType: this.camera.DestinationType.FILE_URI,
            saveToPhotoAlbum: true
        }).then((imagePath) => {
            console.log(imagePath);
            this.navCtrl.push(CreateStoryPage, {path: imagePath, format: "image/jpeg"})
        })
    }

    onScenePortrait(){}

    onSceneEnvironment(){}

    onSceneReaction(){}

    onSceneSignature(){}

}