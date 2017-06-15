import {Injectable} from '@angular/core';
import {Http} from '@angular/http';
import 'rxjs/add/operator/map';
import {SQLite, SQLiteObject} from '@ionic-native/sqlite';

/*
 Generated class for the StoryProvider provider.

 See https://angular.io/docs/ts/latest/guide/dependency-injection.html
 for more info on providers and Angular 2 DI.
 */
@Injectable()
export class StoryProvider {

    CREATE_POSTS_TABLE = "create table if not exists posts (id integer primary key autoincrement, "
        + "postid text, title text default '', dateCreated date, date_created_gmt date, categories text default '', custom_fields text default '', "
        + "description text default '', link text default '', mt_allow_comments boolean, mt_allow_pings boolean, "
        + "mt_excerpt text default '', mt_keywords text default '', mt_text_more text default '', permaLink text default '', post_status text default '', userid integer default 0, "
        + "wp_author_display_name text default '', wp_author_id text default '', wp_password text default '', wp_post_format text default '', wp_slug text default '', mediaPaths text default '', "
        + "latitude real, longitude real, localDraft boolean default 0, uploaded boolean default 0, isPage boolean default 0, wp_page_parent_id text, wp_page_parent_title text);";

    constructor(public http: Http, private sqlite: SQLite) {
        this.sqlite.create({
            name: 'data.db',
            location: 'default'
        })
            .then((db: SQLiteObject) => {


                db.executeSql(this.CREATE_POSTS_TABLE, {})
                    .then(() => console.log('Executed SQL'))
                    .catch(e => console.log(e));


            })
            .catch(e => console.log(e));
    }

    createPost(){

    }

}
