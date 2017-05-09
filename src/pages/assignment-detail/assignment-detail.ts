import { Component } from '@angular/core';
import { NavController, NavParams } from 'ionic-angular';

/**
 * Generated class for the AssignmentDetail page.
 *
 * See http://ionicframework.com/docs/components/#navigation for more info
 * on Ionic pages and navigation.
 */

@Component({
  selector: 'page-assignment-detail',
  templateUrl: 'assignment-detail.html',
})
export class AssignmentDetailPage {

  assignment: any;

  constructor(public navCtrl: NavController, public navParams: NavParams) {
    this.assignment = navParams.get('assignment');
  }

  ionViewDidLoad() {
    console.log('ionViewDidLoad AssignmentDetail');
  }



}
