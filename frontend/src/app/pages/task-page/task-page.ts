import { Component, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { TaskListService } from '../../shared/services/task-list.service';
import { TaskList } from '../../models/task-list.model';
import { MatCardModule } from '@angular/material/card';
import { finalize } from 'rxjs';
import { HttpErrorResponse } from '@angular/common/http';
import { MatButton } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { TaskListFormComponent } from '../task-list-page/task-list-form/task-list-form';
import { DecimalPipe, Location } from '@angular/common';
import { CreateButtonComponent } from '../../shared/components/create-button/create-button';
import { TaskFormComponent } from './task-form/task-form';
import { Task } from '../../models/task.model';
import { TaskService } from '../../shared/services/task.service';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { ConfirmDeletionComponent } from '../../shared/components/confirm-deletion/confirm-deletion';

@Component({
  selector: 'app-task-page',
  imports: [
    MatCardModule,
    MatButton,
    CreateButtonComponent,
    MatProgressBarModule,
    MatIconModule,
    MatProgressSpinnerModule,
    DecimalPipe,
  ],
  templateUrl: './task-page.html',
  styleUrl: './task-page.css',
})
export class TaskPageComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private location = inject(Location);
  private taskListService = inject(TaskListService);
  private taskService = inject(TaskService);
  private dialog = inject(MatDialog);
  private snackBar = inject(MatSnackBar);

  sendingLabel = signal('Create a task!');

  taskList = signal<TaskList | null>(null);
  taskListId: string | null = null;

  tasks = signal<Task[]>([]);

  isLoading = signal(true);

  ngOnInit(): void {
    this.taskListId = this.route.snapshot.paramMap.get('id');
    if (!this.taskListId) {
      this.router.navigate(['/error'], { state: { message: 'Task List not found.' } });
      this.isLoading.set(false);
      return;
    }
    this.loadTaskList();
    this.loadTasks();
  }

  private loadTaskList() {
    this.taskListService
      .getById(this.taskListId!)
      .pipe(finalize(() => this.isLoading.set(false)))
      .subscribe({
        next: (result) => this.taskList.set(result),
        error: (err: HttpErrorResponse) => {
          if (err.status === 404) {
            this.router.navigate(['/error'], {
              state: { code: 404, message: 'Task List not found.' },
            });
          } else {
            this.router.navigate(['/error'], {
              state: { code: 400, message: `Bad request: ${err.error.message}` },
            });
          }
        },
      });
  }

  private loadTasks() {
    this.taskService.getAll(this.taskListId!).subscribe({
      next: (result) => this.tasks.set(result),
      error: (err: HttpErrorResponse) => {
        if (err.status === 404) {
          this.router.navigate(['/error'], { state: { code: 404, message: 'Task not found.' } });
        } else {
          this.router.navigate(['/error'], {
            state: { code: 400, message: `Bad request: ${err.error.message}` },
          });
        }
      },
    });
  }

  onBack() {
    this.location.back();
  }

  onEdit() {
    const dialogRef = this.dialog.open(TaskListFormComponent, {
      backdropClass: 'blurred-backdrop',
      width: '90vw',
      maxWidth: '500px',
      data: this.taskList(),
    });

    dialogRef.afterClosed().subscribe((request) => {
      if (!request) return;

      this.taskListService.update(this.taskListId!, request).subscribe({
        next: (updated) => {
          (this.taskList.set(updated),
            this.snackBar.open('Succesfully updated task list!', 'Close', {
              duration: 5000,
              panelClass: 'snack-success',
            }));
        },
        error: (err: HttpErrorResponse) =>
          this.snackBar.open(err.error.message ?? 'Could not update task list.', 'Close', {
            duration: 5000,
            panelClass: 'snack-error',
          }),
      });
    });
  }

  onDelete() {
    const dialogRef = this.dialog.open(ConfirmDeletionComponent, {
      data: {
        title: 'Delete Task List',
        message:
          'Are you sure you want to delete this task list? All tasks inside the task list will also be deleted. This cannot be undone.',
      },
    });

    dialogRef.afterClosed().subscribe((confirmed) => {
      if (!confirmed) return;

      this.taskListService.deleteById(this.taskListId!).subscribe({
        next: () => {
          (this.onBack(),
            this.snackBar.open('Succesfully deleted task list!', 'Close', {
              duration: 5000,
              panelClass: 'snack-success',
            }));
        },
        error: (err: HttpErrorResponse) =>
          this.snackBar.open(err.error.message ?? 'Could not delete task list.', 'Close', {
            duration: 5000,
            panelClass: 'snack-error',
          }),
      });
    });
  }

  onCreatePressed() {
    const dialogRef = this.dialog.open(TaskFormComponent, {
      backdropClass: 'blurred-backdrop',
    });

    dialogRef.afterClosed().subscribe((request) => {
      if (!request) return;

      this.taskService.create(this.taskListId!, request).subscribe({
        next: (result) => {
          this.tasks.update((lists) => [...lists, result]);
          this.loadTaskList();
          this.snackBar.open('Succesfully created task!', 'Close', {
            duration: 5000,
            panelClass: 'snack-success',
          });
        },
        error: (err: HttpErrorResponse) => {
          this.snackBar.open(err.error.message ?? 'Could not create task', 'Close', {
            duration: 5000,
            panelClass: 'snack-error',
          });
        },
      });
    });
  }

  onEditPressed(taskId: string) {
    const dialogRef = this.dialog.open(TaskFormComponent, {
      backdropClass: 'blurred-backdrop',
      data: this.tasks().find((task) => task.id === taskId),
    });

    dialogRef.afterClosed().subscribe((request) => {
      if (!request) return;

      this.taskService.update(this.taskListId!, taskId, request).subscribe({
        next: (updated) => {
          this.tasks.update((tasks) => tasks.map((t) => (t.id === taskId ? updated : t)));
          this.loadTaskList();
          this.snackBar.open('Succesfully updated task!', 'Close', {
            duration: 5000,
            panelClass: 'snack-success',
          });
        },
        error: (err: HttpErrorResponse) =>
          this.snackBar.open(err.error.message ?? 'Could not update task.', 'Close', {
            duration: 5000,
            panelClass: 'snack-error',
          }),
      });
    });
  }

  onDeletePressed(taskId: string) {
    const dialogRef = this.dialog.open(ConfirmDeletionComponent, {
      data: {
        title: 'Delete Task',
        message: 'Are you sure you want to delete this task? This cannot be undone.',
      },
    });

    dialogRef.afterClosed().subscribe((confirmed) => {
      if (!confirmed) return;

      this.taskService.delete(this.taskListId!, taskId).subscribe({
        next: () => {
          this.tasks.update((tasks) => tasks.filter((task) => task.id !== taskId));
          this.loadTaskList();
          this.snackBar.open('Succesfully deleted task!', 'Close', {
            duration: 5000,
            panelClass: 'snack-success',
          });
        },
        error: (err: HttpErrorResponse) =>
          this.snackBar.open(err.error.message ?? 'Could not delete task.', 'Close', {
            duration: 5000,
            panelClass: 'snack-error',
          }),
      });
    });
  }
}
