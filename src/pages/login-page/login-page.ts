import { Component } from '@angular/core';
import { Storage } from '@ionic/storage';
import { NavController, LoadingController } from 'ionic-angular';
import { FormBuilder, FormGroup, Validators } from "@angular/forms";
import { AuthService } from "../../providers/auth-service";
import { TabsPage } from "../tabs/tabs";

/**
 * Generated class for the LoginPage page.
 *
 * See http://ionicframework.com/docs/components/#navigation for more info
 * on Ionic pages and navigation.
 */
@Component({
  selector: 'page-login-page',
  templateUrl: 'login-page.html',
  providers: [AuthService]
})
export class LoginPage {

  public auth_error;
  public has_error = false;

  private loginForm: FormGroup;
  private loginResult: any;
  private loader: any;
  private queryingServer = false;

  constructor(public navCtrl: NavController,
              private fb: FormBuilder,
              private _storage: Storage,
              private _loadingCtrl: LoadingController,
              private _authService: AuthService){
    this.loginForm = this.fb.group({
      email: ['', Validators.compose([
        Validators.required,
      ]),],

      password: ['', Validators.compose([
        Validators.required,
      ]),],

    });
  }

  presentLoading() {
    this.queryingServer = true;
    this.loader = this._loadingCtrl.create({
      spinner: "crescent"
    });
    this.loader.present();
  }

  login(){

    let password = this.loginForm.value.password;
    let username = this.loginForm.value.email;

    this._authService.loginService(username, password).subscribe(data =>
      {
        this.loader.dismiss();
        this.loginResult = data;
        console.log(this.loginResult.status);
        if (this.loginResult.status == '403'){
          this.has_error = true;
          this.auth_error = "Incorrect username or password";

        } else {
          if ( this.loginResult.token ){
            this._storage.set("auth_token", this.loginResult.token);
            this._storage.set("user_email", this.loginResult.user_email);
            this._storage.set('login', 'yes');
            this.navCtrl.push(TabsPage);
          }
        }
      },
      err => {
        this.has_error = true;
        console.log(err);
        this.loader.dismiss();
        this.loginForm.controls['email'].setErrors({
          inValidCred: true
        });

        this.loginForm.controls['password'].setErrors({
          inValidCred: true
        });
    }
    );

  }

}
