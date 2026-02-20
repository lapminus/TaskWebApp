import { Task } from './task.model';

export interface TaskList {
  id: number;
  title: string;
  description?: string;
  tasks?: Task[];
  count?: number;
  progress?: number;
}

export interface CreateTaskListRequest {
  title: string;
  description?: string;
  tasks?: Task[];
  count?: number;
  progress?: number;
}
