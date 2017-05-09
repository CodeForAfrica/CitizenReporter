import { NgModule } from '@angular/core';
import { IonicPageModule } from 'ionic-angular';
import { EditStoryPage } from './edit-story-page';

@NgModule({
  declarations: [
    EditStoryPage,
  ],
  imports: [
    IonicPageModule.forChild(EditStoryPage),
  ],
  exports: [
    EditStoryPage
  ]
})
export class EditStoryPageModule {}
