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

  constructor(public navCtrl: NavController,
              public navParams: NavParams,
              private _authService: AuthService,
              private _storage: Storage,
              private _app: App) {

                this._storage.get("user").then(data => {
                  console.log("user data", data);
                  this.first_name = data.first_name;
                  this.last_name = data.last_name;
                  this.username = data.username;
                  this.avatar = data.avatar;
                })

  }

  goToAboutPage(){
    this.navCtrl.push(AboutPage);
  }

  gotToEditUserDetails(){
    this.navCtrl.push(EditUserDetails);
  }

  logout(){
    this._authService.logout();
    this._app.getRootNav().setRoot(LoginPage);
  }

}
