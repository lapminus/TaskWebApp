import { Component, inject } from '@angular/core';
import { Task, TaskPriority, TaskStatus } from '../../../models/task.model';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatInputModule } from '@angular/material/input';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatSelectModule } from '@angular/material/select';

@Component({
  selector: 'app-task-form',
  imports: [
    MatDialogModule,
    MatInputModule,
    MatButtonModule,
    MatFormFieldModule,
    MatDatepickerModule,
    MatSelectModule,
    ReactiveFormsModule,
  ],
  templateUrl: './task-form.html',
  styleUrl: './task-form.css',
})
export class TaskFormComponent {
  private dialogRef = inject(MatDialogRef<TaskFormComponent>);
  private formBuilder = inject(FormBuilder);
  data = inject<Task | null>(MAT_DIALOG_DATA);
  
  form = this.formBuilder.group({
    title: [this.data?.title ?? '', Validators.required],
    description: [this.data?.description ?? ''],
    dueDate: [this.data?.dueDate ?? '', Validators.required],
    taskStatus: [this.data?.taskStatus ?? TaskStatus.OPEN],
    taskPriority: [this.data?.taskPriority ?? TaskPriority.MEDIUM],
  });

  onSubmit() {
    if (this.form.valid) {
      console.log('request: ', JSON.stringify(this.form.value, null, 2));
      this.dialogRef.close(this.form.value);
    }
  }

  onCancel() {
    this.dialogRef.close();
  }
}
