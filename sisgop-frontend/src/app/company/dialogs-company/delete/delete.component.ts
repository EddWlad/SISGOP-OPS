import { Component, Inject } from '@angular/core';
import {
  MAT_DIALOG_DATA,
  MatDialogRef,
  MatDialogTitle,
  MatDialogContent,
  MatDialogActions,
  MatDialogClose,
} from '@angular/material/dialog';

import { MatButtonModule } from '@angular/material/button';
import { CompanyService } from 'app/core/service/company.service';

export interface DialogData {
  idCompany: string;
  companyRuc: string;
  companyName: string;
  companyAddress: string;
}

@Component({
  selector: 'app-delete',
  templateUrl: './delete.component.html',
  styleUrl: './delete.component.scss',
  imports: [
    MatDialogTitle,
    MatDialogContent,
    MatDialogActions,
    MatButtonModule,
    MatDialogClose,
  ],
})
export class DeleteComponent {
  constructor(
    public dialogRef: MatDialogRef<DeleteComponent>,
    @Inject(MAT_DIALOG_DATA) public data: DialogData,
    public companyService: CompanyService,
  ) { }
  confirmDelete(): void {
    this.companyService.delete(this.data.idCompany).subscribe({
      next: (response) => {
        this.dialogRef.close(response); // Close with the response data
        // Handle successful deletion, e.g., refresh the table or show a notification
      },
      error: (error) => {
        console.error('Delete Error:', error);
        // Handle the error appropriately
      },
    });
  }

}
