import { Component, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { TaskListService } from '../../shared/services/task-list.service';
import { TaskList } from '../../models/task-list.model';
import { MatCardModule } from '@angular/material/card';
import { finalize } from 'rxjs';
import { HttpErrorResponse } from '@angular/common/http';
import { MatButton } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { TaskListFormComponent } from '../task-list-page/task-list-form/task-list-form';
import { Location } from '@angular/common';
import { CreateButtonComponent } from '../../shared/components/create-button/create-button';
import { TaskFormComponent } from './task-form/task-form';
import { Task } from '../../models/task.model';
import { TaskService } from '../../shared/services/task.service';

@Component({
  selector: 'app-task-page',
  imports: [MatCardModule, MatButton, CreateButtonComponent],
  templateUrl: './task-page.html',
  styleUrl: './task-page.css',
})
export class TaskPageComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private location = inject(Location);
  private taskListService = inject(TaskListService);
  private taskService = inject(TaskService);
  private dialog = inject(MatDialog);

  sendingLabel = signal('Create a task!');

  taskList = signal<TaskList | null>(null);
  taskListId: string | null = null;

  tasks = signal<Task[]>([]);

  isLoading = signal(true);
  errorMessage = signal<string | null>(null);

  ngOnInit(): void {
    this.taskListId = this.route.snapshot.paramMap.get('id');
    if (!this.taskListId) {
      this.errorMessage.set('Task list id not found');
      this.isLoading.set(false);
      return;
    }

    this.loadTaskList();
    this.loadTasks();
  }

  loadTaskList() {
    this.taskListService
      .getById(this.taskListId!)
      .pipe(finalize(() => this.isLoading.set(false)))
      .subscribe({
        next: (result) => this.taskList.set(result),
        error: (err: HttpErrorResponse) => {
          if (err.status === 404) {
            this.errorMessage.set('Task List not found');
          } else {
            this.errorMessage.set(err.error.message ?? 'Bad request');
          }
        },
      });
  }

  private loadTasks() {
    console.log('loading tasks');
    this.taskService
      .getAll(this.taskListId!)
      .pipe(finalize(() => this.isLoading.set(false)))
      .subscribe({
        next: (result) => this.tasks.set(result),
        error: (err: HttpErrorResponse) => {
          if (err.status === 404) {
            this.errorMessage.set('Task not found');
          } else {
            this.errorMessage.set(err.error.message ?? 'Bad request');
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
      data: this.taskList(),
    });

    dialogRef.afterClosed().subscribe((request) => {
      if (request) {
        this.taskListService.update(this.taskListId!, request).subscribe({
          next: (updated) => this.taskList.set(updated),
          error: (err: HttpErrorResponse) => this.errorMessage.set(err.error.message),
        });
      }
    });
  }

  onDelete() {
    this.taskListService.deleteById(this.taskListId!).subscribe({
      next: () => this.onBack(),
      error: (err: HttpErrorResponse) => this.errorMessage.set(err.error.message),
    });
  }

  onCreatePressed() {
    const dialogRef = this.dialog.open(TaskFormComponent, {
      backdropClass: 'blurred-backdrop',
    });

    dialogRef.afterClosed().subscribe((request) => {
      if (request) {
        this.taskService.create(this.taskListId!, request).subscribe((result) => {
          console.log('result: ', JSON.stringify(result, null, 2));
          this.tasks.update((lists) => [...lists, result]);
          this.loadTaskList();
        });
      }
    });
  }
}
