import { Component, inject, OnInit, signal } from '@angular/core';
import { TaskListCardComponent } from './task-list-card/task-list-card';
import { TaskListService } from '../../shared/services/task-list.service';
import { TaskList } from '../../models/task-list.model';
import { CreateButtonComponent } from '../../shared/components/create-button/create-button';
import { MatDialog } from '@angular/material/dialog';
import { TaskListFormComponent } from './task-list-form/task-list-form';
import { MatSnackBar } from '@angular/material/snack-bar';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-task-list-page',
  imports: [TaskListCardComponent, CreateButtonComponent],
  templateUrl: './task-list-page.html',
  styleUrl: './task-list-page.css',
})
export class TaskListPageComponent implements OnInit {
  private taskListService = inject(TaskListService);
  private dialog = inject(MatDialog);
  private snackBar = inject(MatSnackBar);

  sendingTaskLists = signal<TaskList[]>([]);
  sendingLabel = signal('Create Task List');

  ngOnInit(): void {
    this.taskListService.getAll().subscribe((results) => {
      return this.sendingTaskLists.set(results);
    });
  }

  onCreatePressed() {
    const dialogRef = this.dialog.open(TaskListFormComponent, {
      backdropClass: 'blurred-backdrop',
      width: '90vw',
      maxWidth: '500px'
    });

    dialogRef.afterClosed().subscribe((request) => {
      if (!request) return;

      this.taskListService.create(request).subscribe({
        next: (result) => {
          this.sendingTaskLists.update((lists) => [...lists, result]);
          this.snackBar.open('Successfully created task list!', 'Close', {
            duration: 5000,
            panelClass: 'snack-success',
          });
        },
        error: (err: HttpErrorResponse) => {
          this.snackBar.open(err.error?.message ?? 'Could not create task list.', 'Close', {
            duration: 5000,
            panelClass: 'snack-error',
          });
        },
      });
    });
  }
}
