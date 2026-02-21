import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TaskListDetail } from './task-list-detail';

describe('TaskListDetail', () => {
  let component: TaskListDetail;
  let fixture: ComponentFixture<TaskListDetail>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TaskListDetail]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TaskListDetail);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
