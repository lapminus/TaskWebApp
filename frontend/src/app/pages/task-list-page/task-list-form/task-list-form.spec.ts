import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TaskListForm } from './task-list-form';

describe('TaskListForm', () => {
  let component: TaskListForm;
  let fixture: ComponentFixture<TaskListForm>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TaskListForm]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TaskListForm);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
