import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    loadComponent: () =>
      import('./pages/task-list-page/task-list-page').then((m) => m.TaskListPageComponent),
  },
  {
    path: 'tasklist/:id',
    loadComponent: () =>
      import('./pages/task-list-page/task-list-detail/task-list-detail').then(
        (m) => m.TaskListDetailComponent,
      ),
  },
];
