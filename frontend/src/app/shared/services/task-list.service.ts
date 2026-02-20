import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { CreateTaskListRequest, TaskList } from '../../models/task-list.model';

@Injectable({
  providedIn: 'root',
})
export class TaskListService {
  baseUrl = 'http://localhost/8080/api/v1/tasklist';

  http = inject(HttpClient);

  getAll(): Observable<TaskList[]> {
    return this.http.get<TaskList[]>(this.baseUrl);
  }

  create(request: CreateTaskListRequest): Observable<TaskList> {
    return this.http.post<TaskList>(this.baseUrl, request);
  }
}
