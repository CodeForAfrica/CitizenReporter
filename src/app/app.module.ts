import { NgModule, ErrorHandler } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpModule } from '@angular/http';
import { IonicApp, IonicModule, IonicErrorHandler } from 'ionic-angular';
import { IonicStorageModule } from '@ionic/storage';
import { Camera } from '@ionic-native/camera';
import { MyApp } from './app.component';

import { StoriesPage } from '../pages/stories/stories';
import { MorePage } from '../pages/more/more';
import { AssignmentsPage } from '../pages/assignments/assignments';
import { AssignmentDetailPage } from '../pages/assignment-detail/assignment-detail';
import { TabsPage } from '../pages/tabs/tabs';
import { SummaryPipe } from '../utils/summary.pipe';

import { StatusBar } from '@ionic-native/status-bar';
import { SplashScreen } from '@ionic-native/splash-screen';
import { AboutPage } from "../pages/about-page/about-page";
import { Geolocation } from '@ionic-native/geolocation';
import { NativeGeocoder } from '@ionic-native/native-geocoder';
import { LoginPage } from "../pages/login-page/login-page";
import { EditUserDetails } from "../pages/edit-user-details/edit-user-details";
import { ViewStoryPage } from "../pages/view-story-page/view-story-page";
import { CreateStoryPage } from "../pages/create-story-page/create-story-page";
import { ModalDescriptionPage } from "../pages/create-story-page/modal-desc-content";
import {Constant} from "../utils/constants";

@NgModule({
  declarations: [
    MyApp,
    StoriesPage,
    MorePage,
    AssignmentsPage,
    AssignmentDetailPage,
    TabsPage,
    SummaryPipe,
    AboutPage,
    LoginPage,
    EditUserDetails,
    ViewStoryPage,
    CreateStoryPage,
    ModalDescriptionPage,
  ],
  imports: [
    BrowserModule,
    HttpModule,
    IonicModule.forRoot(MyApp),
    IonicStorageModule.forRoot()
  ],
  bootstrap: [IonicApp],
  entryComponents: [
    MyApp,
    StoriesPage,
    MorePage,
    AssignmentsPage,
    AssignmentDetailPage,
    TabsPage,
    AboutPage,
    LoginPage,
    EditUserDetails,
    ViewStoryPage,
    CreateStoryPage,
    ModalDescriptionPage,
  ],
  providers: [
    StatusBar,
    SplashScreen,
    Constant,
    Camera,
    Geolocation,
    NativeGeocoder,
    {provide: ErrorHandler, useClass: IonicErrorHandler}
  ]
})
export class AppModule {}
