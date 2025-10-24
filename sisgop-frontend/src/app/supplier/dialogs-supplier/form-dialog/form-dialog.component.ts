import {
  MAT_DIALOG_DATA,
  MatDialogRef,
  MatDialogContent,
  MatDialogClose,
} from '@angular/material/dialog';

import { Component, Inject } from '@angular/core';
import { SupplierService } from '@core/service/supplier.service';
import {
  UntypedFormControl,
  Validators,
  UntypedFormGroup,
  ReactiveFormsModule,
  FormsModule,
  FormGroup,
  FormBuilder,
} from '@angular/forms';
import { Supplier } from '@core/models/supplier.model';
import {
  MAT_DATE_LOCALE,
  MatNativeDateModule,
  MatOptionModule,
} from '@angular/material/core';
import { formatDate } from '@angular/common';
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
  supplier: Supplier;
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
  ],
})
export class FormDialogComponent {

  action: string;
  dialogTitle: string;
  supplierForm: FormGroup;
  supplier: Supplier;
  url: string | null = null;

  constructor(
    public dialogRef: MatDialogRef<FormDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: DialogData,
    public supplierService: SupplierService,
    private fb: FormBuilder
  ) {
    this.action = data.action;
    if (this.action === 'edit') {
      this.dialogTitle =
        data.supplier.supplierName
      this.supplier = data.supplier;
    } else {
      this.dialogTitle = 'New Supplier';
      this.supplier = new Supplier();
    }
    this.supplierForm = this.createSupplierForm();
  }
  formControl = new UntypedFormControl('', [
    Validators.required,
  ]);
  getErrorMessage() {
    return this.formControl.hasError('required')
      ? 'Required field'
      : this.formControl.hasError('supplierName')
        ? 'Not a valid supplierName'
        : '';
  }

  createSupplierForm(): UntypedFormGroup {
    return this.fb.group({
      idSupplier: [this.supplier.idSupplier],
      supplierRuc: [this.supplier.supplierRuc, [Validators.required]],
      supplierName: [this.supplier.supplierName, [Validators.required]],
      supplierEmail: [
        this.supplier.supplierEmail,
        [Validators.required, Validators.email, Validators.minLength(5)],
      ],
      supplierAddress: [this.supplier.supplierAddress],
      supplierPhone: [this.supplier.supplierPhone, [Validators.required]],
      status: [this.supplier.status, [Validators.required]]
    });
  }
  onNoClick(): void {
    this.dialogRef.close();
  }

  submit() {
    if (this.supplierForm.valid) {
      const idData = this.supplierForm.get('idSupplier')?.value;
      const formData = this.supplierForm.getRawValue();
      if (this.action == 'edit') {
        this.supplierService.update(idData, formData).subscribe({
          next: (reponse) => {
            this.dialogRef.close(reponse);
          },
          error: (error) => {
            console.error('Update Error', error);
          },
        });
      } else {
        this.supplierService.add(formData).subscribe({
          next: (response) => {
            this.dialogRef.close(response);
          },
          error: (error) => {
            console.error('Add Error:', error);
          },
        });
      }
    }
  }

  onSelectFile(event: Event) {
    const target = event.target as HTMLInputElement;
    if (target.files && target.files[0]) {
      const reader = new FileReader();

      reader.readAsDataURL(target.files[0]); // read file as data url

      reader.onload = (e) => {
        if (e.target) {
          this.url = e.target.result as string; // Explicitly cast to avoid undefined
        }
      };
    }
  }
}
