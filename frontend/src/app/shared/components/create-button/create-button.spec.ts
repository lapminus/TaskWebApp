import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateButton } from './create-button';

describe('CreateButton', () => {
  let component: CreateButton;
  let fixture: ComponentFixture<CreateButton>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CreateButton]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CreateButton);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
