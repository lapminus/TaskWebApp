import { Component, inject } from '@angular/core';
import {
  MAT_DIALOG_DATA,
  MatDialogRef,
  MatDialogTitle,
  MatDialogContent,
  MatDialogActions,
} from '@angular/material/dialog';
import { MatAnchor } from '@angular/material/button';

@Component({
  selector: 'app-confirm-deletion',
  imports: [MatDialogTitle, MatDialogContent, MatDialogActions, MatAnchor],
  template: `
    <h2 mat-dialog-title>{{ data.title }}</h2>
    <mat-dialog-content>{{ data.message }}</mat-dialog-content>
    <mat-dialog-actions align="end">
      <button
        matButton="outlined"
        (click)="dialogRef.close(false)"
      >
        Cancel
      </button>
      <button
        matButton="filled"
        (click)="dialogRef.close(true)"
      >
        Confirm
      </button>
    </mat-dialog-actions>
  `,
})
export class ConfirmDeletionComponent {
  dialogRef = inject(MatDialogRef<ConfirmDeletionComponent>);
  data = inject<{ title: string; message: string }>(MAT_DIALOG_DATA);
}
