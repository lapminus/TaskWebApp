import { Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-task-list-detail',
  imports: [],
  templateUrl: './task-list-detail.html',
  styleUrl: './task-list-detail.css',
})
export class TaskListDetailComponent implements OnInit {
  route = inject(ActivatedRoute);

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    console.log(`Tasklist id of : ${id}`)

  }
}
