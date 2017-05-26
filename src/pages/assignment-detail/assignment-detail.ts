import {Component} from '@angular/core';
import {NavController, NavParams} from 'ionic-angular';
import {MediaCapture, MediaFile, CaptureError} from '@ionic-native/media-capture';
import {Camera} from '@ionic-native/camera';
import {CreateStoryPage} from "../create-story-page/create-story-page";
import {File} from '@ionic-native/file';
import {ScenePicker} from "../scene-picker/scene-picker";
import { AndroidPermissions } from '@ionic-native/android-permissions';
import { ImagePicker } from '@ionic-native/image-picker';

/**
 * Generated class for the AssignmentDetail page.
 *
 * See http://ionicframework.com/docs/components/#navigation for more info
 * on Ionic pages and navigation.
 */

@Component({
    selector: 'page-assignment-detail',
    templateUrl: 'assignment-detail.html',
    providers: [MediaCapture]
})
export class AssignmentDetailPage {

    assignment: any;
    tabBarElement: any;

    constructor(public navCtrl: NavController,
                public navParams: NavParams,
                private mediaCapture: MediaCapture,
                private camera: Camera,
                private imagePicker: ImagePicker,
                private androidPermissions: AndroidPermissions,
                private file: File) {
        this.tabBarElement = document.querySelector('.tabbar.show-tabbar');
        this.assignment = navParams.get('assignment');
        console.log(this.file.dataDirectory);

    }

    ionViewDidLoad() {
        console.log('ionViewDidLoad AssignmentDetail');
    }

    ionViewWillEnter() {
        this.tabBarElement.style.display = 'none';
    }

    ionViewWillLeave() {
        this.tabBarElement.style.display = 'flex';
    }

    recordAudio() {
        // this.mediaCapture.captureAudio().then(
        //   (data: MediaFile[]) => {
        //     console.log(data[0].fullPath);
        //     console.log(data[0].type);
        //     alert(data[0].type);
        //     this.navCtrl.push(CreateStoryPage, {path: data[0].fullPath, format: 'audio'})
        //   },
        //   (err: CaptureError) => console.error(err)
        // );

    }

    captureImage() {
        this.androidPermissions.checkPermission(this.androidPermissions.PERMISSION.CAMERA).then(
            (success) => {
                this.camera.getPicture({
                    sourceType: this.camera.PictureSourceType.CAMERA,
                    destinationType: this.camera.DestinationType.FILE_URI,
                    saveToPhotoAlbum: true
                }).then((imagePath) => {
                    console.log(imagePath);
                    this.navCtrl.push(CreateStoryPage, {path: imagePath, format: "image/jpeg"})
                })
            },
            (err) => {
                this.androidPermissions.requestPermissions(this.androidPermissions.PERMISSION.CAMERA)
            })

    }

    captureVideo() {
        this.androidPermissions.checkPermission(this.androidPermissions.PERMISSION.CAMERA).then(
            (success) => {
                this.mediaCapture.captureVideo().then(
                    (data: MediaFile[]) => {
                        this.navCtrl.push(CreateStoryPage, {path: data[0].fullPath, format: data[0].type});
                    })
            },
            (err) => {
                this.androidPermissions.requestPermissions(this.androidPermissions.PERMISSION.CAMERA);
            })

    }

    openGallery(){
        console.log("open gallery");
        this.imagePicker.hasReadPermission().then((success) =>{
            let options = {
                maximumImagesCount: 5,
                width: 500,
                height: 500,
                quality: 75
            }

            this.imagePicker.getPictures(options).then(
                file_uris => this.navCtrl.push(CreateStoryPage, {images: file_uris}),
                err => console.log('uh oh')
            );
        },
            (err) => {
            this.imagePicker.requestReadPermission();
            }
        );
    }



    openScenePickerImage(){
        this.navCtrl.push(ScenePicker, {camera: "image"});
    }
    openScenePickerVideo(){
        this.navCtrl.push(ScenePicker, {camera: "video"});
    }

}
