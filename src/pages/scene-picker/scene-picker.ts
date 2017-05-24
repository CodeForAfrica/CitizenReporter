///<reference path="../../../node_modules/ionic-angular/platform/platform.d.ts"/>
import {Component} from '@angular/core';
import {NavController, NavParams, Platform} from 'ionic-angular';
import {ScreenOrientation} from "@ionic-native/screen-orientation";

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

    constructor(public navCtrl: NavController,
                public navParams: NavParams,
                private platform: Platform,
                private screenOrientation: ScreenOrientation) {
        this.platform.ready().then(()=>{
            this.screenOrientation.lock(this.screenOrientation.ORIENTATIONS.LANDSCAPE);
            this.tabBarElement = document.querySelector('.tabbar.show-tabbar');
        });

    }

    ionViewDidLoad() {
        console.log('ionViewDidLoad ScenePicker');
    }

    scenes = [
        {
            title: "Portrait",
            description: "Introduce the character. Get Close",
            image: "assets/img/portrait.svg"
        },
        {
            title: "Candid",
            description: "Show the character engaged in his or her environment",
            image: "assets/img/candid.svg"
        },
        {
            title: "Environment",
            description: "Show where your story takes place",
            image: "assets/img/environment.svg"
        },
        {
            title: "Reaction/Interaction",
            description: "Depict how people are responding to the event or its aftermath",
            image: "assets/img/reaction.svg"
        },
        {
            title: "Signature",
            description: "Summarise the entire issue by illustrating an essential element of the story",
            image: "assets/img/signature.svg"
        }
    ]


    ionViewWillEnter() {
        this.tabBarElement.style.display = 'none';
    }

    ionViewWillLeave() {
        this.tabBarElement.style.display = 'flex';
    }


}
