import { Component } from '@angular/core';

import { StoriesPage } from '../stories/stories';
import { MorePage } from '../more/more';
import { AssignmentsPage } from '../assignments/assignments';

@Component({
  templateUrl: 'tabs.html'
})
export class TabsPage {

  tab1Root = AssignmentsPage;
  tab2Root = StoriesPage;
  tab3Root = MorePage;

  constructor() {

  }
}
