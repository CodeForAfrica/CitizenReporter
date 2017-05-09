import { Component } from '@angular/core';
import { NavController, NavParams } from 'ionic-angular';

/**
 * Generated class for the ViewStoryPage page.
 *
 * See http://ionicframework.com/docs/components/#navigation for more info
 * on Ionic pages and navigation.
 */
@Component({
  selector: 'page-view-story-page',
  templateUrl: 'view-story-page.html',
})
export class ViewStoryPage {

  slides: any[];
  mySlideOptions = true;
  story: any;
  constructor(public navCtrl: NavController, public navParams: NavParams) {
    this.story = navParams.get('story');
    console.log(this.story);

    this.slides = [
      'https://lorempixel.com/400/200/?1',
      'https://lorempixel.com/400/200/?2',
      'https://lorempixel.com/400/200/?3',
      'https://lorempixel.com/800/1000/?4'
    ]
  }

}
