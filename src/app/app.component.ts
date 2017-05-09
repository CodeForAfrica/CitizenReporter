import { Component } from '@angular/core';
import { Storage } from '@ionic/storage';
import { Platform } from 'ionic-angular';
import { StatusBar } from '@ionic-native/status-bar';
import { SplashScreen } from '@ionic-native/splash-screen';

import { TabsPage } from '../pages/tabs/tabs';
import {LoginPage} from "../pages/login-page/login-page";

@Component({
  templateUrl: 'app.html'
})
export class MyApp {
  rootPage:any;
  user: any;

  constructor(platform: Platform, statusBar: StatusBar, splashScreen: SplashScreen, storage: Storage) {

    this.user = storage.get("login");
    console.log("login", storage.get('login'));

    storage.get("login").then(data => {
      console.log("login", data);
      if (data == "yes"){
        this.rootPage = TabsPage;
      } else 
      {
        this.rootPage = LoginPage
      }
    })
}
}
