import { Injectable } from '@angular/core';
import {Headers, Http} from '@angular/http';
import { Storage } from '@ionic/storage';
import 'rxjs/add/operator/map';
import {Constant} from "../utils/constants";

/*
  Generated class for the AuthService provider.

  See https://angular.io/docs/ts/latest/guide/dependency-injection.html
  for more info on providers and Angular 2 DI.
*/
@Injectable()
export class AuthService {

  login_url: any;
  constructor(private _http: Http, private _storage: Storage, private _constant: Constant) {
    this.login_url = this._constant.baseUrl + "wp-json/jwt-auth/v1/token"
  }

  loginService(username: string, password: string){
    let headers = new Headers();
    headers.append("Content-Type", "application/json");
    let obj = { "username": username,
                      "password": password};

    console.log(obj);

    return this._http.post(this.login_url, JSON.stringify(obj), {headers: headers})
      .map(res => res.json());

  }

  logout(){
    console.log("Auth", this._storage.get('auth_token'));
    console.log("login", this._storage.get('login'));

    this._storage.remove('login');
    this._storage.remove('auth_token');

    // Should delete the user data and user posts but persist the assignments
  }

}
