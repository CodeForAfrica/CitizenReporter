import { Component } from '@angular/core';
import { Platform, NavParams, ViewController } from 'ionic-angular';
import { FormBuilder, FormGroup } from '@angular/forms';


@Component({
  template: `
      <ion-header>
        <ion-toolbar>
          <ion-title>
            Who is involved?
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
      <form [formGroup]="qWho" (ngSubmit)="getWhoIsInvolved()">
          <ion-item>
          <ion-textarea formControlName="involved" 
            class="text-area" 
            rows="6" 
            placeholder="Who is involved?"></ion-textarea>
          </ion-item>
          <div padding>
            <button ion-button block type="submit">Save</button>
          </div>
      </form>
      </ion-content>
      `
})
export class ModalWhoInvolvedPage {
  private qWho : FormGroup;

  constructor(
    public platform: Platform,
    public params: NavParams,
    public viewCtrl: ViewController,
    private formBuilder: FormBuilder
  ) {
      let qwho_text = params.get("qwho_text");
        this.qWho = this.formBuilder.group({
        involved: [qwho_text],
        });

  }

  getWhoIsInvolved(){
    this.viewCtrl.dismiss(this.qWho.value);
  }

  dismiss() {
    this.viewCtrl.dismiss();
  }
}