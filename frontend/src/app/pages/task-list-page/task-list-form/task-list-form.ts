import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import {
  MatDialogRef,
  MatDialogTitle,
  MatDialogModule,
  MAT_DIALOG_DATA,
} from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { TaskList } from '../../../models/task-list.model';

@Component({
  selector: 'app-task-list-form',
  imports: [
    MatDialogTitle,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    ReactiveFormsModule,
  ],
  templateUrl: './task-list-form.html',
  styleUrl: './task-list-form.css',
})
export class TaskListFormComponent implements OnInit {
  private dialogRef = inject(MatDialogRef<TaskListFormComponent>);
  private formBuilder = inject(FormBuilder);
  data = inject<TaskList | null>(MAT_DIALOG_DATA);

  form = this.formBuilder.group({
    title: [
      this.data?.title ?? '',
      { validators: [Validators.required, Validators.maxLength(45)], updateOn: 'change' },
    ],
    description: [
      this.data?.description ?? '',
      { validators: Validators.maxLength(100), updateOn: 'change' },
    ],
  });

  ngOnInit(): void {
    this.dialogRef.afterOpened().subscribe(() => this.form.markAllAsTouched());
  }

  onSubmit() {
    if (this.form.valid) {
      this.dialogRef.close(this.form.value);
    }
  }

  onCancel() {
    this.dialogRef.close();
  }
}
