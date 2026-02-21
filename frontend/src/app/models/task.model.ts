export interface Task {
  id: number;
  title: string;
  description?: string;
  dueDate: string;
  taskStatus: TaskStatus;
  taskPriority: TaskPriority;
}

export interface CreateTaskRequest {
  title: string;
  description?: string;
  dueDate: string;
  taskPriority: TaskPriority;
}

export enum TaskStatus {
  OPEN = 'OPEN',
  CLOSED = 'CLOSED',
}

export enum TaskPriority {
  HIGH = 'HIGH',
  MEDIUM = 'MEDIUM',
  LOW = 'LOW',
}
