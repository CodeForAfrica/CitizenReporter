import {Component} from '@angular/core';
import {NavController, NavParams, ModalController} from 'ionic-angular';
import {MediaCapture} from '@ionic-native/media-capture';
import {File} from '@ionic-native/file';
import {ScenePopover} from "../scene-popover/scene-popover";



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
    tabBarElement: any;

    constructor(public navCtrl: NavController,
                public navParams: NavParams,
                public popoverCtrl: ModalController,
                private file: File) {
        this.tabBarElement = document.querySelector('.tabbar.show-tabbar');
        this.assignment = navParams.get('assignment');
        console.log(this.file.dataDirectory);

    }

    ionViewDidLoad() {
        console.log('ionViewDidLoad AssignmentDetail');
    }

    ionViewWillEnter() {
        this.tabBarElement.style.display = 'none';
    }

    ionViewWillLeave() {
        this.tabBarElement.style.display = 'flex';
    }

    recordAudio() {
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

    presentPopover() {
        let popover = this.popoverCtrl.create(ScenePopover);
        popover.present();
    }


}
