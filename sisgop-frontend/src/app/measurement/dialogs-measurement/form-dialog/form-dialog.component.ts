import { Component, Inject } from '@angular/core';
import {
  MAT_DIALOG_DATA,
  MatDialogRef,
  MatDialogContent,
  MatDialogClose,
} from '@angular/material/dialog';
import { MeasurementUnitService } from 'app/core/service/measurement-unit.service';
import {
  UntypedFormControl,
  Validators,
  UntypedFormGroup,
  ReactiveFormsModule,
  FormsModule,
  FormGroup,
  FormBuilder,
} from '@angular/forms';
import { MeasurementUnit } from 'app/core/models/measurementUnit.model';
import {
  MAT_DATE_LOCALE,
  MatNativeDateModule,
  MatOptionModule,
} from '@angular/material/core';
import { MatSelectModule } from '@angular/material/select';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatRadioModule } from '@angular/material/radio';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatMomentDateModule } from '@angular/material-moment-adapter';

export interface DialogData {
  id: string;
  action: string;
  measurementUnit: MeasurementUnit;
}

@Component({
  selector: 'app-form-dialog',
  templateUrl: './form-dialog.component.html',
  styleUrl: './form-dialog.component.scss',
  providers: [{ provide: MAT_DATE_LOCALE, useValue: 'en-GB' }],
  imports: [
    MatButtonModule,
    MatIconModule,
    MatDialogContent,
    FormsModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatRadioModule,
    MatDatepickerModule,
    MatSelectModule,
    MatOptionModule,
    MatDialogClose,
    MatNativeDateModule,
    MatMomentDateModule,
  ]
})
export class FormDialogComponent {
  action: string;
  dialogTitle: string;
  measurementForm: FormGroup;
  measurementUnit: MeasurementUnit;
  url: string | null = null;

  fileName: string[] = [];

  selectedFiles: FileList;

  constructor(
    public dialogRef: MatDialogRef<FormDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: DialogData,
    public measurementService: MeasurementUnitService,
    private fb: FormBuilder
  ) {
    this.action = data.action;
    if (this.action === 'edit') {
      this.dialogTitle =
        data.measurementUnit.unitMeasurementName;
      this.measurementUnit = data.measurementUnit;
    } else {
      this.dialogTitle = 'New Measurement Unit';
      this.measurementUnit = new MeasurementUnit();
    }
    this.measurementForm = this.createMeasurementForm();
  }

  formControl = new UntypedFormControl('', [
    Validators.required,
  ]);

  getErrorMessage() {
    return this.formControl.hasError('required')
      ? 'Required field'
      : this.formControl.hasError('name')
        ? 'Not a valid name'
        : '';
  }

  createMeasurementForm(): UntypedFormGroup {
    return this.fb.group({
      idMeasurementUnit: [this.measurementUnit.idMeasurementUnit],
      unitMeasurementName: [this.measurementUnit.unitMeasurementName, [Validators.required]],
      status: [this.measurementUnit.status],
    });
  }

  onNoClick(): void {
    this.dialogRef.close();
  }

  submit() {
    if (this.measurementForm.valid) {
      const idData = this.measurementForm.get('idMeasurementUnit')?.value;
      const formData = this.measurementForm.getRawValue();

      if (this.action === 'edit') {
        this.measurementService.update(idData, formData).subscribe({
          next: (response) => {
            console.log('Edit response:', response); // 👀 debug
            this.dialogRef.close(response); // 👈 DEVUELVE al componente padre
          },
          error: (error) => console.error('Update Error:', error),
        });
      } else {
        this.measurementService.add(formData).subscribe({
          next: (response) => {
            console.log('Add response:', response); // 👀 debug
            this.dialogRef.close(response); // 👈 igual acá
          },
          error: (error) => console.error('Add Error:', error),
        });
      }
    }
  }

  onSelectFile(event: any) {
    this.fileName = event.target.files[0]?.name;
    this.selectedFiles = event.target.files;
  }

}
