import { Component, inject, OnInit, signal } from '@angular/core';
import { TaskListCardComponent } from './task-list-card/task-list-card';
import { TaskListService } from '../../shared/services/task-list.service';
import { TaskList } from '../../models/task-list.model';
import { CreateButtonComponent } from '../../shared/components/create-button/create-button';
import { MatDialog } from '@angular/material/dialog';
import { TaskListFormComponent } from './task-list-form/task-list-form';

@Component({
  selector: 'app-task-list-page',
  imports: [TaskListCardComponent, CreateButtonComponent],
  templateUrl: './task-list-page.html',
  styleUrl: './task-list-page.css',
})
export class TaskListPageComponent implements OnInit {
  private taskListService = inject(TaskListService);
  private dialog = inject(MatDialog);
  sendingTaskLists = signal<TaskList[]>([]);
  sendingLabel = signal('Create a task list!');

  ngOnInit(): void {
    this.taskListService.getAll().subscribe((results) => {
      return this.sendingTaskLists.set(results);
    });
  }

  onCreatePressed() {
    const dialogRef = this.dialog.open(TaskListFormComponent);
    dialogRef.afterClosed().subscribe((request) => {
      if (request) {
        this.taskListService.create(request).subscribe((result) => {
          this.sendingTaskLists.update((lists) => [...lists, result]);
        });
      }
    });
  }
}
