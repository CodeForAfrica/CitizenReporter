import { Component, OnInit } from '@angular/core';
import { NavController } from 'ionic-angular';
import { Storage } from '@ionic/storage';
import { CitizenReporterService } from '../../providers/citizen-reporter-service';
import { ViewStoryPage } from '../view-story-page/view-story-page';

@Component({
  selector: 'page-stories',
  templateUrl: 'stories.html',
  providers: [CitizenReporterService]
})
export class StoriesPage implements OnInit {

  posts: any[];
  constructor(public navCtrl: NavController, private _crService: CitizenReporterService, private _storage: Storage) {

      this._storage.get("posts").then((data) => {
        console.log("posts ", data);
        this.posts = data;
      })

  }

  ngOnInit() {
      this._storage.get("posts").then((data) => {
        // console.log("posts ", data);
        this.posts = data;
      })
    }
  
  goToDetails(story){
    this.navCtrl.push(ViewStoryPage, {story: story});
  }

}
