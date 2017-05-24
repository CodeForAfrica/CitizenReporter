import {Component, OnInit} from '@angular/core';
import { NavController } from 'ionic-angular';
import { Storage } from '@ionic/storage';
import { Observable } from 'rxjs/Observable';
import { File } from '@ionic-native/file';
import 'rxjs/add/observable/forkJoin';

import { CitizenReporterService } from '../../providers/citizen-reporter-service';
import {AssignmentDetailPage} from "../assignment-detail/assignment-detail";

@Component({
  selector: 'page-assignments',
  templateUrl: 'assignments.html',
  providers: [CitizenReporterService]
})
export class AssignmentsPage implements OnInit{

  assignments: any[];
  isLoading = true;

  constructor(public navCtrl: NavController,
              private _crService: CitizenReporterService,
              private file: File,
              private _storage: Storage) {

    this._storage.get("assignments").then((value) => {
      this.assignments = value;
    
    });

    console.log("Auth", this._storage.get('auth_token'));
    console.log("login", this._storage.get('login'));

    // this.file.checkDir(this.file.dataDirectory, 'recording').then(
    //   (value) => {
    //     if(!value){
    //       this.file.createDir(this.file.dataDirectory, 'recording', false).then(
    //         (value) => {
    //                 console.log(value);
    //             }
    //         );

    //     }
    //   }
    // );

    console.log(Math.ceil(new Date().getTime() / 1000));



  }

  ngOnInit() {
    Observable.forkJoin(
      this._crService.getCurrentAssignments(),
    ).subscribe(
      res => {
        this._storage.set("assignments", res[0].assignments);
      }
    );


    this._crService.getCurrentAssignments()
      .subscribe(assignments_json => {
        this._storage.set("assignments", assignments_json.assignments);
      });

      this._crService.getCurrentUser();

  }

  goToAssignmentDetails(assignment) {
    this.navCtrl.push(AssignmentDetailPage, { assignment: assignment });
  }

  doRefresh(refresher) {
    this._crService.getCurrentAssignments()
      .subscribe(assignments_json => {
        this._storage.set("assignments", assignments_json.assignments);
      });

    this._storage.get("assignments").then((value) => {
      this.assignments = value;
    });

    setTimeout(() => {
      console.log('Async operation has ended');
      refresher.complete();
    }, 3000);
  }


}
