import { Component, inject, OnInit } from '@angular/core';
import { Task, TaskPriority, TaskStatus } from '../../../models/task.model';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatInputModule } from '@angular/material/input';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatSelectModule } from '@angular/material/select';
import { formatDate } from '@angular/common';

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
export class TaskFormComponent implements OnInit {
  private dialogRef = inject(MatDialogRef<TaskFormComponent>);
  private formBuilder = inject(FormBuilder);
  today = new Date();
  data = inject<Task | null>(MAT_DIALOG_DATA);

  form = this.formBuilder.group({
    title: [
      this.data?.title ?? '',
      { validators: [Validators.required, Validators.maxLength(45)], updateOn: 'change' },
    ],
    description: [
      this.data?.description ?? '',
      { validators: Validators.maxLength(100), updateOn: 'change' },
    ],
    dueDate: [
      this.data?.dueDate ? this.parseLocalDate(this.data.dueDate) : '',
      Validators.required,
    ],
    taskStatus: [this.data?.taskStatus ?? TaskStatus.OPEN],
    taskPriority: [this.data?.taskPriority ?? TaskPriority.MEDIUM],
  });

  private parseLocalDate(date: string): Date {
    const [year, month, day] = date.split('-').map(Number);
    return new Date(year, month - 1, day);
  }

  ngOnInit(): void {
    this.dialogRef.afterOpened().subscribe(() => this.form.markAllAsTouched());
  }

  onSubmit() {
    console.log('form value:', JSON.stringify(this.form.value));
    if (this.form.valid) {
      const value = {
        ...this.form.value,
        dueDate: formatDate(this.form.value.dueDate!, 'yyyy-MM-dd', 'en-US'),
      };
      
      this.dialogRef.close(value);
      console.log('form value:', JSON.stringify(value));
    } else {
      this.form.markAllAsTouched();
    }
  }

  onCancel() {
    this.dialogRef.close();
  }
}
