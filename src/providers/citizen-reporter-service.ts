import { Injectable } from '@angular/core';
import { Storage } from "@ionic/storage";
import { Http, Headers } from '@angular/http';
import 'rxjs/add/operator/map';
import {Constant} from "../utils/constants";

/*
  Generated class for the CitizenReporterService provider.

  See https://angular.io/docs/ts/latest/guide/dependency-injection.html
  for more info on providers and Angular 2 DI.
*/
@Injectable()
export class CitizenReporterService {

  url: string;
  constructor(private _http: Http, private constant: Constant, private _storage: Storage) {
    this.url = constant.baseUrl;

  }

  getCurrentAssignments(){
    let assignment_url = this.url + "wp-json/crrest/v1/assignments";
    return this._http.get(assignment_url)
      .map(res => res.json());
  }

  getCurrentUser(){
    this._storage.get("user_email").then((data)=>{
      console.log("user email: ", data);
      this.getUserCallback(data);
    });

  }

  getUserCallback(email: string){
    let user_url = this.url + "wp-json/crrest/v1/user?username=" + email;
    console.log(user_url);
    return this._http.get(user_url)
      .map(res => res.json())
      .subscribe(
        res => {
          this._storage.set("user", res.user);
          // get user posts immeadiately after getting user details
          console.log("user id",res.user.user_id);
          this.getUserPosts(res.user.user_id);
        }
       
      );
  }

  getUserPosts(user_id:string){
    console.log("get user posts");
    this.getPostsCallback(user_id)


  }

  getPostsCallback(userID: string){
    let posts_url = this.url + "wp-json/crrest/v1/user/posts/" + userID;
    console.log(posts_url);
    this._storage.get("auth_token").then((token) => {
      console.log(token);
      let headers = new Headers();
      headers.append("Authorization", "Bearer " + token);
      return this._http.get(posts_url, {headers: headers})
        .map(res => res.json())
        .subscribe(
          
          res => {
            console.log("p", res);
            this._storage.set("posts", res);}
        );

    })
    // return this._http.get(posts_url)
    //   .map(res => res.json())
    //   .subscribe(
    //     res => this._storage.set("posts", res)
    //   );
  }

}
