import {Component} from '@angular/core';
import {Platform, NavParams, ViewController} from 'ionic-angular';


@Component({
    templateUrl: 'scene-picker.modal.html',
})
export class ModalScenePickerPage {

    constructor(public platform: Platform,
                public params: NavParams,
                public viewCtrl: ViewController) {

    }

    dismiss() {
        this.viewCtrl.dismiss();
    }
}