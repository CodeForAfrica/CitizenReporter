import { Component } from '@angular/core';
import { Platform, NavParams, ViewController } from 'ionic-angular';
import { FormBuilder, FormGroup } from '@angular/forms';


@Component({
  template: `
      <ion-header>
        <ion-toolbar>
          <ion-title>
            Why did this happen?
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
      <form [formGroup]="qWhy" (ngSubmit)="getWhyHappened()">
          <ion-item>
          <ion-textarea formControlName="why" 
            class="text-area" 
            rows="6" 
            placeholder="Why did this happen?"></ion-textarea>
          </ion-item>
          <div padding>
            <button ion-button block type="submit">Save</button>
          </div>
      </form>
      </ion-content>
      `
})
export class ModalWhyHappenedPage {
  private qWhy : FormGroup;

  constructor(
    public platform: Platform,
    public params: NavParams,
    public viewCtrl: ViewController,
    private formBuilder: FormBuilder
  ) {
      let qwhy_text = params.get("qwhy_text");
        this.qWhy = this.formBuilder.group({
        involved: [qwhy_text],
        });

  }

  getWhyHappened(){
    this.viewCtrl.dismiss(this.qWhy.value);
  }

  dismiss() {
    this.viewCtrl.dismiss();
  }
}