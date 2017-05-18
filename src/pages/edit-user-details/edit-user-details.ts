import { Component, OnInit } from '@angular/core';
import { Storage } from '@ionic/storage';
import {NavController, NavParams } from 'ionic-angular';
import { FormBuilder, FormGroup } from '@angular/forms';
import { CitizenReporterService } from '../../providers/citizen-reporter-service';

@Component({
  selector: 'edit-user-details',
  templateUrl: 'edit-user-details.html',
  providers: [CitizenReporterService]
})
export class EditUserDetails implements OnInit {
        first_name: string;
        last_name: string;
        email: string;
        phone_number: string;
        location: string;
        id: string;
        editUserForm: FormGroup;


      constructor(public navCtrl: NavController,
              public navParams: NavParams,
              private formBuilder: FormBuilder,
              private _crService: CitizenReporterService,
              private _storage: Storage) {

                console.log(navParams);

                this.first_name = this.navParams.get("first_name");
                this.last_name = this.navParams.get("last_name");
                this.email = this.navParams.get("email");
                this.location = this.navParams.get("location");
                this.phone_number = this.navParams.get("phone_number");
                this.id = this.navParams.get("id")

                this.editUserForm = this.formBuilder.group({
                  first_name: [this.first_name],
                  last_name: [this.last_name],
                  email: [this.email],
                  phone_number: [this.phone_number],
                  location: [this.location]
                })

            }
            
     ngOnInit() {
        // this._storage.get("user").then(data => {
        //           console.log("user data", data);
        //           this.first_name = data.first_name;
        //           this.last_name = data.last_name;
        //           this.email = data.email;
        //           this.phone_number = data.phone_number;
        //           this.location = data.location;
        // });
     }

     onSaveUserForm(){
       console.log(this.editUserForm.value);
       let body = this.editUserForm.value;
       this._crService.editUserDetails(this.id,body);
     }

}