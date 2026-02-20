import { Component, input, output, signal } from '@angular/core';
import { MatAnchor } from '@angular/material/button';

@Component({
  selector: 'app-create-button',
  imports: [MatAnchor],
  templateUrl: './create-button.html',
  styleUrl: './create-button.css',
})
export class CreateButtonComponent {
  receivedLabel = input.required<string>();
  createPressed = output<void>();

  onClicked() {
    this.createPressed.emit();
  }
}
