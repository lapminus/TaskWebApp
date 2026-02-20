import { Component, signal } from '@angular/core';
import { TaskListCardComponent } from './task-list-card/task-list-card';

@Component({
  selector: 'app-task-list-page',
  imports: [TaskListCardComponent],
  templateUrl: './task-list-page.html',
  styleUrl: './task-list-page.css',
})
export class TaskListPageComponent {
  tempTaskList = { id: 0, title: 'First task list', description: 'Something test 123' };
  sendingTaskList = signal(this.tempTaskList);
}
