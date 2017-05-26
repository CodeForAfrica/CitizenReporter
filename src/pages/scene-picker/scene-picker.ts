///<reference path="../../../node_modules/ionic-angular/platform/platform.d.ts"/>
import {Component} from '@angular/core';
import {NavController, NavParams, Platform, ViewController} from 'ionic-angular';
import {ScreenOrientation} from "@ionic-native/screen-orientation";
import {CreateStoryPage} from "../create-story-page/create-story-page";
import {Camera} from '@ionic-native/camera';

/**
 * Generated class for the ScenePicker page.
 *
 * See http://ionicframework.com/docs/components/#navigation for more info
 * on Ionic pages and navigation.
 */

@Component({
    selector: 'page-scene-picker',
    templateUrl: 'scene-picker.html',
})
export class ScenePicker {

    tabBarElement: any;
    action: string;

    constructor(public navCtrl: NavController,
                public navParams: NavParams,
                private platform: Platform,
                private camera: Camera,
                private screenOrientation: ScreenOrientation) {
        this.action = this.navParams.get("camera");

        this.platform.ready().then(()=>{
            this.tabBarElement = document.querySelector('.tabbar.show-tabbar');
        });

    }

    ionViewDidLoad() {
        console.log('ionViewDidLoad ScenePicker');
    }

    scenes = [
        {
            title: "<b>Portrait</b>",
            description: "<b>Portrait</b> Introduce the character. Get Close",
            image: "assets/img/portrait.svg"
        },
        {
            title: "<b>Candid</b>",
            description: "<b>Candid</b> Show the character engaged in his or her environment",
            image: "assets/img/candid.svg"
        },
        {
            title: "<b>Environment</b>",
            description: "<b>Environment</b> Show where your story takes place",
            image: "assets/img/environment.svg"
        },
        {
            title: "<b>Reaction/Interaction</b>",
            description: "<b>Reaction/Interaction</b> Depict how people are responding to the event or its aftermath",
            image: "assets/img/reaction.svg"
        },
        {
            title: "<b>Signature</b>",
            description: "<b>Signature</b> Summarise the entire issue by illustrating an essential element of the story",
            image: "assets/img/signature.svg"
        }
    ]


    ionViewWillEnter() {
        this.screenOrientation.lock(this.screenOrientation.ORIENTATIONS.LANDSCAPE);
        this.tabBarElement.style.display = 'none';
    }

    ionViewWillLeave() {
        this.tabBarElement.style.display = 'flex';
        this.screenOrientation.unlock();
    }

    captureImage() {
        this.camera.getPicture({
            quality: 75,
            sourceType: this.camera.PictureSourceType.CAMERA,
            destinationType: this.camera.DestinationType.FILE_URI,
            saveToPhotoAlbum: true,
            encodingType: this.camera.EncodingType.JPEG,
            targetWidth: 500,
            targetHeight: 500,
        }).then((imagePath) => {
            // let previousView: ViewController = this.navCtrl.getPrevious();
            console.log(this.navCtrl.getViews());
            // this.navCtrl.removeView(previousView);
            let startIndex = this.navCtrl.getActive().index - 1;
            this.navCtrl.remove(startIndex, 1);
            console.log(this.navCtrl.getViews());
            this.navCtrl.push(CreateStoryPage, {path: imagePath, format: "image/jpeg"});

            })
    }


}
