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

@Component({
  selector: 'app-task-page',
  imports: [MatCardModule, MatButton],
  templateUrl: './task-page.html',
  styleUrl: './task-page.css',
})
export class TaskPageComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private taskListService = inject(TaskListService);
  private dialog = inject(MatDialog);
  taskList = signal<TaskList | null>(null);
  taskListId: string | null = null;
  isLoading = signal(true);
  errorMessage = signal<string | null>(null);

  ngOnInit(): void {
    this.taskListId = this.route.snapshot.paramMap.get('id');
    if (!this.taskListId) {
      this.errorMessage.set('Task list id not found');
      this.isLoading.set(false);
      return;
    }

    this.taskListService
      .getById(this.taskListId)
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
      next: () => this.router.navigate(['/']),
      error: (err: HttpErrorResponse) => this.errorMessage.set(err.error.message),
    });
  }
}
