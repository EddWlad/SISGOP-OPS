import {
  MAT_DIALOG_DATA,
  MatDialogRef,
  MatDialogContent,
  MatDialogClose,
} from '@angular/material/dialog';
import { Component, Inject } from '@angular/core';
import { Company } from 'app/core/models/company.model';
import {
  UntypedFormControl,
  Validators,
  UntypedFormGroup,
  ReactiveFormsModule,
  FormsModule,
  FormGroup,
  FormBuilder,
} from '@angular/forms';
import { CompanyService } from 'app/core/service/company.service';
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
  id: number;
  action: string;
  company: Company;
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
  companyForm: FormGroup;
  company: Company;
  url: string | null = null;

  fileName: string[] = [];

  selectedFiles: FileList;

  constructor(
    public dialogRef: MatDialogRef<FormDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: DialogData,
    public companyService: CompanyService,
    private fb: FormBuilder
  ) {
    // Set the defaults
    this.action = data.action;
    if (this.action === 'edit') {
      this.dialogTitle =
        data.company.companyName;
      this.company = data.company;
    } else {
      this.dialogTitle = 'New Company';
      this.company = new Company();
    }
    this.companyForm = this.createCompanyForm();
  }

  formControl = new UntypedFormControl('', [
    Validators.required,
    // Validators.email,
  ]);
  getErrorMessage() {
    return this.formControl.hasError('required')
      ? 'Required field'
      : this.formControl.hasError('email')
        ? 'Not a valid email'
        : '';
  }
  createCompanyForm(): UntypedFormGroup {
    return this.fb.group({
      idCompany: [this.company.idCompany],
      companyRuc: [this.company.companyRuc, [Validators.required]],
      companyName: [this.company.companyName, [Validators.required]],
      companyAddress: [this.company.companyAddress, [Validators.required]],
      companyLogo: [this.company.companyLogo],
      status: [this.company.status]
    });
  }

  onNoClick(): void {
    this.dialogRef.close();
  }

  submit() {
    if (this.companyForm.valid) {
      const idData = this.companyForm.get('idCompany')?.value;

      const enviarDatos = (urlList: string[] = []) => {
        // Actualizamos solo si hay imagen nueva
        if (urlList.length > 0) {
          this.companyForm.get('companyLogo')?.setValue(urlList[0]);

        }
        const formData = this.companyForm.getRawValue();

        if (this.action === 'edit') {
          this.companyService.update(idData, formData).subscribe({
            next: (response) => this.dialogRef.close(response),
            error: (error) => console.error('Update Error:', error),
          });
        } else {
          this.companyService.add(formData).subscribe({
            next: (response) => {
              this.dialogRef.close(response);
            },
            error: (error) => console.error('Add Error:', error),
          });
        }
      };

      if (this.selectedFiles && this.selectedFiles.length > 0) {
        this.companyService.saveFile(this.selectedFiles.item(0)).subscribe({
          next: (urlList: string[]) => enviarDatos(urlList),
          error: (error) => {
            console.error('Upload Error:', error);
            enviarDatos(); // Editar sin nueva imagen si falla
          },
        });
      } else {
        enviarDatos(); // No se subió imagen nueva
      }
    }
  }

  onSelectFile(event: any) {
    this.fileName = event.target.files[0]?.name;
    this.selectedFiles = event.target.files;
  }

  upload() {
    this.companyService.saveFile(this.selectedFiles.item(0)).subscribe();
  }

}


