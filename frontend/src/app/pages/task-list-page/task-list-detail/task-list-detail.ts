import { Component, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { TaskListService } from '../../../shared/services/task-list.service';
import { TaskList } from '../../../models/task-list.model';
import { MatCardModule } from '@angular/material/card';
import { finalize } from 'rxjs';
import { HttpErrorResponse } from '@angular/common/http';
import { MatButton, MatIconButton } from '@angular/material/button';

@Component({
  selector: 'app-task-list-detail',
  imports: [MatCardModule, MatButton, MatIconButton],
  templateUrl: './task-list-detail.html',
  styleUrl: './task-list-detail.css',
})
export class TaskListDetailComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private taskListService = inject(TaskListService);
  taskList = signal<TaskList | null>(null);
  isLoading = signal(true);
  errorMessage = signal<string | null>(null);

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (!id) {
      this.errorMessage.set('Task list id not found');
      this.isLoading.set(false);
      return;
    }

    this.taskListService
      .getById(id)
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
}
