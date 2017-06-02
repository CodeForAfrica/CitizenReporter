import {Component} from '@angular/core';
import {Platform, NavParams, ViewController, NavController} from 'ionic-angular';
import {FormBuilder, FormGroup} from '@angular/forms';


@Component({
    template: `
        <ion-header>
            <ion-toolbar>
                <ion-title>
                    Description
                </ion-title>
                <ion-buttons start>
                    <button ion-button (click)="dismiss()">
                        <span ion-text color="primary" showWhen="ios">Cancel</span>
                        <ion-icon name="md-close" showWhen="android, windows"></ion-icon>
                    </button>
                </ion-buttons>
            </ion-toolbar>
        </ion-header>
        <ion-content>
            <form [formGroup]="desc" (ngSubmit)="getDescription()">
                <ion-item>
                    <ion-textarea formControlName="description"
                                  class="text-area"
                                  rows="6"
                                  placeholder="Please a brief, precise summary of your story"></ion-textarea>
                </ion-item>
                <div padding>
                    <button ion-button block type="submit">Save</button>
                </div>
            </form>
        </ion-content>
    `
})
export class ModalDescriptionPage {
    private desc: FormGroup;

    constructor(public platform: Platform,
                public params: NavParams,
                public viewCtrl: ViewController,
                private navCtrl: NavController,
                private formBuilder: FormBuilder) {
        let desc_text = params.get("description");
        this.desc = this.formBuilder.group({
            description: [desc_text],
        });

    }

    getDescription() {
        this.viewCtrl.dismiss(this.desc.value);
    }

    dismiss() {
        this.viewCtrl.dismiss();
    }

    ionViewWillLeave() {
        const index = this.viewCtrl.index;
        this.navCtrl.remove(index);
    }
}