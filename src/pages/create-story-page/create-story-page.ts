import {Component, OnInit} from '@angular/core';
import {NavController, NavParams, ModalController, ViewController} from 'ionic-angular';
import {MediaPlugin, MediaObject} from '@ionic-native/media';
import {MediaCapture, MediaFile, CaptureError} from '@ionic-native/media-capture';
import {Geolocation} from '@ionic-native/geolocation';
import {NativeGeocoder, NativeGeocoderReverseResult, NativeGeocoderForwardResult} from '@ionic-native/native-geocoder';
import {ModalDescriptionPage} from "./modal-desc-content";
import {ModalWhoInvolvedPage} from "./modal-who-content";
import {ModalWhyHappenedPage} from "./modal-why-content";
import {DatePicker} from '@ionic-native/date-picker';
import {ScreenOrientation} from "@ionic-native/screen-orientation";
import {AndroidPermissions} from "@ionic-native/android-permissions";
import {ImagePicker} from "@ionic-native/image-picker";
import {Camera} from '@ionic-native/camera';


/**
 * Generated class for the CreateStoryPage page.
 *
 * See http://ionicframework.com/docs/components/#navigation for more info
 * on Ionic pages and navigation.
 */

@Component({
    selector: 'page-create-story-page',
    templateUrl: 'create-story-page.html',
    providers: [MediaPlugin]
})
export class CreateStoryPage implements OnInit {

    slides: any[] = [];
    data: any;
    format: string;
    playing: boolean = false;
    paused: boolean = true;
    audio_file: MediaObject;

    location: string = "Location";
    qWhen: string = "When did this happen";
    qWho: string = "Who is involved";
    qWhy: string = "Why did this happen";
    public description: string = "Provide a brief, precise summary of your story";
    longitude: number;
    latitude: number;
    tabBarElement: any;

    constructor(public navCtrl: NavController,
                public navParams: NavParams,
                private _geolocation: Geolocation,
                private orientation: ScreenOrientation,
                private _geocoder: NativeGeocoder,
                private modalCtrl: ModalController,
                private camera: Camera,
                private viewCtrl: ViewController,
                private mediaCapture: MediaCapture,
                private imagePicker: ImagePicker,
                private androidPermissions: AndroidPermissions,
                private datePicker: DatePicker,
                private media: MediaPlugin) {

        this.orientation.lock(this.orientation.ORIENTATIONS.LANDSCAPE);

        this.tabBarElement = document.querySelector('.tabbar.show-tabbar');
        this.data = this.navParams.get("path");
        this.audio_file = this.media.create(this.data);
        this.format = this.navParams.get("format");
        if (this.format == "audio/mpeg") {
            this.slides.push({file: "../../assets/img/audio.png", "format": this.format});
        } else {
            this.slides.push({file: this.data, "format": this.format});
        }

        const prevView: ViewController = this.navCtrl.getPrevious();
        this.navCtrl.removeView(prevView).then(res => console.log("View removed: " + res + "  " + prevView.component.toString()));


        console.log(this.slides);

    }

    ionViewDidLoad() {
        console.log(this.navCtrl.getViews())
        console.log('ionViewDidLoad CreateStoryPage');
    }

    ionViewWillEnter() {
        this.tabBarElement.style.display = 'none';
    }

    ionViewWillLeave() {
        this.tabBarElement.style.display = 'flex';
    }

    onPlay() {
        this.playing = true;
        this.paused = false;

        this.audio_file.play()
    }

    onPause() {
        this.playing = false;
        this.paused = true;

        this.audio_file.pause()
    }

    getCurrentLocation() {
        this._geolocation.getCurrentPosition().then((resp) => {
            this.latitude = resp.coords.latitude;
            this.longitude = resp.coords.longitude;
            this.getLocationString(this.longitude, this.latitude);
        });
    }

    getLocationString(longitude, latitude) {
        this._geocoder.reverseGeocode(latitude, longitude).then((res: NativeGeocoderReverseResult) => {
            this.location = res.street + ", " + res.city + ", " + res.countryName;
        }).catch((err) => console.log(err));
    }

    ngOnInit(): void {
        this.getCurrentLocation();
    }

    getForwardGeoCode(area: string) {
        this._geocoder.forwardGeocode(area).then((coordinates: NativeGeocoderForwardResult) => {

        }).catch((err) => console.log(err));
    }

    openDescriptionModal() {
        console.log("clicked");
        let modal = this.modalCtrl.create(ModalDescriptionPage, {"description": this.description});
        modal.present();
        modal.onDidDismiss((data) => {
            console.log(data);
            this.description = data.description;
        })
    }

    openWhoIsInvolvedModal() {
        let modal = this.modalCtrl.create(ModalWhoInvolvedPage, {"qwho_text": this.qWho});
        modal.present();
        modal.onDidDismiss((data) => {
            console.log(data);
            this.qWho = data.involved;
        })
    }

    openWhyHappenedModal() {
        let modal = this.modalCtrl.create(ModalWhyHappenedPage, {"qwhy_text": this.qWhy});
        modal.present();
        modal.onDidDismiss((data) => {
            console.log(data);
            this.qWhy = data.why;
        })
    }

    openDatePicker() {
        console.log("date picker clicked");
        this.datePicker.show({
            date: new Date(),
            mode: 'date',
            androidTheme: this.datePicker.ANDROID_THEMES.THEME_HOLO_DARK
        }).then(
            date => {
                console.log('Got date: ', date.toDateString());
                this.qWhen = date.toDateString();
            },
            err => console.log('Error occurred while getting date: ', err)
        );

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
                    this.slides.push({file: imagePath, format: "image/jpeg"});
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
                };

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
        // this.navCtrl.push(ScenePicker, {camera: "image"});
    }
    openScenePickerVideo(){
        // this.navCtrl.push(ScenePicker, {camera: "video"});
    }



}
