import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { CreateTaskRequest, Task } from '../../models/task.model';

@Injectable({
  providedIn: 'root',
})
export class TaskService {
  private baseUrl = 'http://localhost:8080/api/v1/tasklist';
  private getUrl(taskListId: string) {
    return `${this.baseUrl}/${taskListId}/tasks`;
  }

  private http = inject(HttpClient);

  getAll(taskListId: string): Observable<Task[]> {
    return this.http.get<Task[]>(this.getUrl(taskListId));
  }

  create(taskListId: string, request: CreateTaskRequest): Observable<Task> {
    return this.http.post<Task>(this.getUrl(taskListId), request);
  }
}
