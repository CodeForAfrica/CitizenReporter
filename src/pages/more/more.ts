import { Component } from '@angular/core';
import { Storage } from '@ionic/storage';
import {NavController, NavParams, App } from 'ionic-angular';
import {AboutPage} from "../about-page/about-page";
import {AuthService} from "../../providers/auth-service";
import {LoginPage} from "../login-page/login-page";
import { EditUserDetails } from "../edit-user-details/edit-user-details";

@Component({
  selector: 'page-contact',
  templateUrl: 'more.html',
  providers: [AuthService]
})
export class MorePage {

  first_name: string;
  last_name: string;
  username: string;
  avatar: string;
  email: string;
  phone_number: string;
  location: string;
  user_id: string;

  constructor(public navCtrl: NavController,
              public navParams: NavParams,
              private _authService: AuthService,
              private _storage: Storage,
              private _app: App) {

                this._storage.get("user_dets").then((data) => {
                  console.log("user data", data);
                  this.first_name = data.first_name;
                  this.last_name = data.last_name;
                  this.username = data.username;
                  this.avatar = data.avatar;
                  this.email = data.email;
                  this.phone_number = data.phone_number;
                  this.location = data.location;
                  this.user_id = data.user_id;
                });
                console.log(this._storage.keys());

  }

  goToAboutPage(){
    this.navCtrl.push(AboutPage);
  }

  gotToEditUserDetails(){
    this.navCtrl.push(EditUserDetails, {
      first_name: this.first_name,
      last_name: this.last_name,
      email: this.email,
      phone_number: this.phone_number,
      location: this.location,
      id: this.user_id
    });
  }

  logout(){
    this._authService.logout();
    this._app.getRootNav().setRoot(LoginPage);
  }

}
