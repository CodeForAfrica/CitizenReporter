import {Component} from '@angular/core';
import {LoadingController, NavController, NavParams, AlertController} from 'ionic-angular';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import { AuthService } from "../../providers/auth-service";
import {LoginPage} from "../login-page/login-page";

/**
 * Generated class for the SignUpPage page.
 *
 * See http://ionicframework.com/docs/components/#navigation for more info
 * on Ionic pages and navigation.
 */

@Component({
    selector: 'page-sign-up-page',
    templateUrl: 'sign-up-page.html',
    providers: [AuthService],
})
export class SignUpPage {

    private signupForm: FormGroup;
    private loader: any;
    private queryingServer = false;
    private hasError = false;

    constructor(public navCtrl: NavController,
                public navParams: NavParams,
                private _authService: AuthService,
                private _loadingCtrl: LoadingController,
                private alertCtrl: AlertController,
                private fb: FormBuilder) {

        this.signupForm = this.fb.group({
            username: ['', Validators.compose([
                Validators.required,
            ])],
            email: ['', Validators.compose([
                Validators.required,
            ]),],
            password: ['', Validators.compose([
                Validators.required,
            ]),],

        });
    }

    ionViewDidLoad() {
        console.log('ionViewDidLoad SignUpPage');
    }

    signup(){
        let password = this.signupForm.value.password;
        let email = this.signupForm.value.email;
        let username = this.signupForm.value.username;

        this._authService.signUpService(username, email, password).subscribe(
            (data) => {
            this.loader.dismiss();
            console.log(data);
            if(data.message == "Registration successful!"){
                this.signupSuccessAlert();
                this.goToLogin();
            } else if (data.message == "User already exists!"){
                this.hasError = true;
                this.signupForm.controls['username'].setErrors({usernameExists: true});
                this.signupFailureAlert('User already exists!');
            }

        },
            (err) => {
                this.loader.dismiss();
                console.log(err);
            }
        )


    }

    presentLoading() {
        this.queryingServer = true;
        this.loader = this._loadingCtrl.create({
            spinner: "crescent"
        });
        this.loader.present();
    }

    goToLogin(){
        this.navCtrl.pop(LoginPage);
    }

    signupSuccessAlert(){
        let alert = this.alertCtrl.create({
            title: 'Sign Up',
            subTitle: 'You have successfully created an account',
            buttons: ['OK']
        });
        alert.present();
    }

    signupFailureAlert(message){
        let alert = this.alertCtrl.create({
            title: 'Sign Up',
            subTitle: message,
            buttons: ['OK']
        });
        alert.present();
    }

}
