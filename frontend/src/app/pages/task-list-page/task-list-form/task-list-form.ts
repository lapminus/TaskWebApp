import { Component, inject } from '@angular/core';
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
export class TaskListFormComponent {
  private dialogRef = inject(MatDialogRef<TaskListFormComponent>);
  private formBuilder = inject(FormBuilder);
  data = inject<TaskList | null>(MAT_DIALOG_DATA);

  form = this.formBuilder.group({
    title: [this.data?.title ?? '', Validators.required],
    description: [this.data?.description ?? ''],
  });

  onSubmit() {
    if (this.form.valid) {
      this.dialogRef.close(this.form.value);
    }
  }

  onCancel() {
    this.dialogRef.close();
  }
}
