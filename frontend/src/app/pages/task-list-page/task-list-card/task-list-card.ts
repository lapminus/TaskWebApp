import { Component, input } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { TaskList } from '../../../models/task-list.model';
import { MatButtonModule } from '@angular/material/button';
import { RouterLink } from "@angular/router";

@Component({
  selector: 'app-task-list-card',
  imports: [MatCardModule, MatButtonModule, RouterLink],
  templateUrl: './task-list-card.html',
  styleUrl: './task-list-card.css',
})
export class TaskListCardComponent {
  receivedTaskLists = input<TaskList[]>();
}
