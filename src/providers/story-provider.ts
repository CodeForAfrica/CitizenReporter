import { Injectable } from '@angular/core';
import { Http } from '@angular/http';
import 'rxjs/add/operator/map';
import { Storage } from "@ionic/storage";

/*
  Generated class for the StoryProvider provider.

  See https://angular.io/docs/ts/latest/guide/dependency-injection.html
  for more info on providers and Angular 2 DI.
*/
@Injectable()
export class StoryProvider {

  constructor(public http: Http, private _storage: Storage) {
    console.log('Hello StoryProvider Provider');
  }

  createNewStory(){

  }

}
