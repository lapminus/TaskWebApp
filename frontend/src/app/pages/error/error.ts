import { Component, inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { Router, RouterLink } from '@angular/router';

@Component({
  selector: 'app-error',
  imports: [RouterLink, MatButtonModule],
  templateUrl: './error.html',
  styleUrl: './error.css',
})
export class ErrorComponent {
  private router = inject(Router);
  code = this.router.currentNavigation()?.extras.state?.['code'] ?? 500;
  message = this.router.currentNavigation()?.extras.state?.['message'] ?? 'Something went wrong';
}
