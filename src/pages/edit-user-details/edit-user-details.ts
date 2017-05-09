import { Component, OnInit } from '@angular/core';
import { Storage } from '@ionic/storage';
import {NavController, NavParams } from 'ionic-angular';

@Component({
  selector: 'edit-user-details',
  templateUrl: 'edit-user-details.html',
})
export class EditUserDetails implements OnInit {
        first_name: string;
        last_name: string;
        email: string;
        phone_number: string;
        location: string;


      constructor(public navCtrl: NavController,
              public navParams: NavParams,
              private _storage: Storage) {

            }
            
     ngOnInit() {
        this._storage.get("user").then(data => {
                  console.log("user data", data);
                  this.first_name = data.first_name;
                  this.last_name = data.last_name;
                  this.email = data.email;
                  this.phone_number = data.phone_number;
                  this.location = data.location;
        })
     }

}